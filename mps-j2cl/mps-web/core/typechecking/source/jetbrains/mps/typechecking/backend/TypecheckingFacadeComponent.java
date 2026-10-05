/*
 * Copyright 2000-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */
package jetbrains.mps.typechecking.backend;

import jetbrains.mps.components.CoreComponent;
import jetbrains.mps.languageScope.LanguageScopeFactory;
import jetbrains.mps.smodel.language.LanguageRegistry;
import jetbrains.mps.typechecking.TypecheckingFacade;
import jetbrains.mps.typechecking.TypecheckingQueries;
import jetbrains.mps.typechecking.TypecheckingSession;
import jetbrains.mps.typechecking.TypecheckingSession.Flags;
import jetbrains.mps.typechecking.internal.MPSTypechecking;
import jetbrains.mps.util.containers.ConcurrentHashSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.mps.openapi.module.SRepository;

import javax.swing.SwingUtilities;
import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author Fedor Isakov
 */
public class TypecheckingFacadeComponent implements CoreComponent {

  // dependencies
  private final LanguageRegistry myLanguageRegistry;
  private final LanguageScopeFactory myLanguageScopeFactory;
  private final TypecheckingBackend myTypecheckingBackend;

  // managed stuff
  private ConcurrentHashSet<DisposeReference> myLiveDisposeReferences = new ConcurrentHashSet<>();
  private ReferenceQueue<ContextTypecheckingFacade> myReferenceQueue = new ReferenceQueue<>();

  /**
   * Created by {@link MPSTypechecking}.
   */ 
  public TypecheckingFacadeComponent(@NotNull LanguageRegistry languageRegistry,
                                     @NotNull LanguageScopeFactory languageScopeFactory,
                                     @NotNull TypecheckingBackend typecheckingBackend) {
    this.myLanguageRegistry = languageRegistry;
    this.myLanguageScopeFactory = languageScopeFactory;
    this.myTypecheckingBackend = typecheckingBackend;
  }

  @Override
  public void init() {
    ContextTypecheckingFacade.setFactoryInstance(
        () -> {
          if (SwingUtilities.isEventDispatchThread()) {
            // TODO correctly initialize facade for AWT thread
            return createFacade(new TypecheckingControllerFactory() {
              public TypecheckingController createContextController() {
                return new WorkbenchTypecheckingController(myTypecheckingBackend);
              }

              public TypecheckingController createIsolatedController(Flags flags, ParametersDiscoverable discoverable) {
                if (flags.getRoot() != null && flags.isIncremental()) {
                  return new WorkbenchTypecheckingController(myTypecheckingBackend);

                } else {
                  return new IsolatedTypecheckingController(myTypecheckingBackend, flags, discoverable);
                }
              }

              public TypecheckingController createSharedController(@NotNull TypecheckingSessionImpl session, TypecheckingController contextController) {
                return new SharedSessionTypecheckingController(myTypecheckingBackend, session, contextController);
              }
            });

          } else {
            // TODO correctly initialize facade for threads other than AWT
            return createFacade(new TypecheckingControllerFactory() {
              public TypecheckingController createContextController() {
                return new DefaultTypecheckingController(myTypecheckingBackend, TypecheckingSession.Flags.basic());
              }

              public TypecheckingController createIsolatedController(Flags flags, ParametersDiscoverable discoverable) {
                return new IsolatedTypecheckingController(myTypecheckingBackend, flags, discoverable);
              }

              public TypecheckingController createSharedController(@NotNull TypecheckingSessionImpl session, TypecheckingController contextController) {
                return new SharedSessionTypecheckingController(myTypecheckingBackend, session, contextController);
              }
            });
          }
        });
  }

  @Override
  public void dispose() {
    for (DisposeReference ref : myLiveDisposeReferences) {
      ref.dipose();
      ref.clear();
    }
    myLiveDisposeReferences.clear();
    ContextTypecheckingFacade.setFactoryInstance(null);
  }

  private ContextTypecheckingFacade createFacade(TypecheckingControllerFactory sharedControllerFactory)
  {
    DisposeReference ref = null;
    while ((ref = (DisposeReference) myReferenceQueue.poll()) != null) {
      ref.dipose();
      myLiveDisposeReferences.remove(ref);
    }

    ContextTypecheckingFacade facade = new ContextTypecheckingFacade(sharedControllerFactory);
    myLiveDisposeReferences.add(new DisposeReference(facade, myReferenceQueue));
    return facade;
  }

  protected interface TypecheckingControllerFactory {

    TypecheckingController createContextController();

    TypecheckingController createIsolatedController(Flags flags, ParametersDiscoverable parametersDiscoverable);

    TypecheckingController createSharedController(@NotNull TypecheckingSessionImpl session, TypecheckingController contextController);

  }

  protected static class ContextTypecheckingFacade extends TypecheckingFacade {

    /* Provides access to a protected static field in the superclass */
    protected static void setFactoryInstance(Supplier<TypecheckingFacade> factoryInstance) {
      clearContextInstance();
      TypecheckingFacade.DEFAULT_INSTANCE_FACTORY = factoryInstance;
    }

    @NotNull
    private final TypecheckingControllerFactory myControllerFactory;

    private Deque<TypecheckingController> myControllerStack = new ArrayDeque<>();
    private boolean myDisposed;

    public ContextTypecheckingFacade(@NotNull TypecheckingControllerFactory controllerFactory) {
      myControllerFactory = controllerFactory;
    }

    @Override
    public <C> C getData(Class<? extends C> dataClass) {
      return activeController().getData(dataClass);
    }

    @NotNull
    @Override
    protected TypecheckingController activeController() {
      init();
      //noinspection ConstantConditions
      return peek();
    }

    @Override
    protected void overrideSharedController(@NotNull TypecheckingSessionImpl session) {
      init();
      // provide the initial "context" controller as the delegate
      push(myControllerFactory.createSharedController(session, peekLast()));
    }

    @Override
    protected TypecheckingController overrideIsolatedController(Flags flags) {
      init();
      TypecheckingController controller = myControllerFactory.createIsolatedController(flags, peek());
      push(controller);
      return controller;
    }

    @Override
    protected void resetOverride() {
      pop().dispose();
    }

    private void init() {
      if (myDisposed) throw new IllegalStateException("Typechecking facade is disposed");
      if (myControllerStack.isEmpty()) {
        TypecheckingController context = myControllerFactory.createContextController();
        push(context);
      }
    }

    private void push(TypecheckingController context) {
      myControllerStack.addFirst(context);
    }

    private TypecheckingController pop() {
      return myControllerStack.removeFirst();
    }

    @Nullable
    private TypecheckingController peek() {
      return myControllerStack.peekFirst();
    }

    @Nullable
    private TypecheckingController peekLast() {
      return myControllerStack.peekLast();
    }
  }

  private static class DisposeReference extends PhantomReference<ContextTypecheckingFacade> {

    private final Deque<TypecheckingController> myReferentControllerStack;
    private final java.lang.ref.WeakReference<ContextTypecheckingFacade> myFacade;

    private DisposeReference(ContextTypecheckingFacade referent,
                             ReferenceQueue<? super ContextTypecheckingFacade> q) {
      super(referent, q);
      myFacade = new java.lang.ref.WeakReference<>(referent);
      myReferentControllerStack = referent.myControllerStack;
    }

    private void dipose() {
      ContextTypecheckingFacade facade = myFacade.get();
      if (facade != null) facade.myDisposed = true;
      myFacade.clear();
      while (!myReferentControllerStack.isEmpty()) {
        myReferentControllerStack.removeFirst().dispose();
      }
    }
  }

}
