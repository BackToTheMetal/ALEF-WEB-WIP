/*
 * Copyright 2003-2022 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jetbrains.mps.typechecking.backend;

import jetbrains.mps.logging.Logger;
import jetbrains.mps.typechecking.TypecheckingQueries;
import jetbrains.mps.typechecking.TypecheckingSession;
import jetbrains.mps.typechecking.TypecheckingSession.Flags;
import jetbrains.mps.typechecking.TypecheckingSession.Handle;
import jetbrains.mps.typechecking.backend.TypecheckingProvider.AuxDataContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.mps.openapi.language.SConcept;
import org.jetbrains.mps.openapi.model.SNode;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Handles the sessions requested by the editor.
 * Is expected to ever be accessed from the single (AWT) thread.
 *
 * @author Fedor Isakov
 */
public class WorkbenchTypecheckingController extends DefaultTypecheckingController implements ParametersDiscoverable {

  private static final Logger LOG = Logger.getLogger(WorkbenchTypecheckingController.class);

  private final Map<SNodeHandle, TypecheckingSessionImpl> myRootSessions = new HashMap<>();

  public WorkbenchTypecheckingController(TypecheckingBackend typecheckingBackend) {
    super(typecheckingBackend, TypecheckingSession.Flags.basic());
  }

  @Override
  public void dispose() {
    disposeAllSessions();
  }

  @NotNull
  @Override
  public Handle requestSession(@NotNull Flags flags) {
    ensureNotDisposed();
    if (flags.getRoot() != null && flags.isIncremental()) {
      // the editor has requested a session for the opened root
      return new SessionHandle(flags);

    } else {
      return super.requestSession(flags);
    }
  }

  @Override
  public Map<String, ?> discoverParameters(SNode anchor) {
    SNode containingRoot = anchor.getContainingRoot();
    TypecheckingSessionImpl session = myRootSessions.get(new SNodeHandle(containingRoot));
    if (session == null) {
      // sometimes root is not the "containing root"
      session = myRootSessions.get(new SNodeHandle(anchor));
    }
    if (session != null) {
      return session.flags().getParamsMap();
    }
    return myRootSessions.values().stream()
                         .map((s) -> s.flags().getParamsMap())
                         .filter(Objects::nonNull).findFirst()
                         .orElse(null);
  }

  @NotNull
  @Override
  protected TypecheckingQueries getQueries(@NotNull SNode src, SNode trg, SConcept trgConcept, Flags flags) {
    ensureNotDisposed();
    SNode containingRoot = src.getContainingRoot();
    TypecheckingSessionImpl session = myRootSessions.get(new SNodeHandle(containingRoot));
    if (session == null) {
      // sometimes root is not the "containing root"
      session = myRootSessions.get(new SNodeHandle(src));
    }
    if (session != null) {
      return session.getQueries(src, trg, trgConcept);

    } else {
      return super.getQueries(src, trg, trgConcept, flags);
    }
  }

  @Override
  protected AuxDataContainer getDataContainer(TypecheckingProvider<?> provider) {
    return super.getDataContainer(provider);
  }

  private void disposeAllSessions() {
    for (TypecheckingSessionImpl session : myRootSessions.values()) {
      session.dispose();
    }
    myRootSessions.clear();
    super.dispose();
  }

  private synchronized TypecheckingSessionImpl getOrCreateSession(Flags flags) {
    ensureNotDisposed();
    return myRootSessions.computeIfAbsent(new SNodeHandle(flags.getRoot()),
                                          (key) -> new TypecheckingSessionImpl(this, flags) {
                                            @Override
                                            public <C> C getData(Class<? extends C> dataClass) {
                                              return WorkbenchTypecheckingController.this.getData(dataClass);
                                            }
                                          });
  }

  private synchronized void releaseSession(@NotNull TypecheckingSessionImpl session, boolean forceRemoval) {
    SNodeHandle key = new SNodeHandle(session.flags().getRoot());
    if (!session.isDisposed() && !session.isOrphaned()) {
      if (session != myRootSessions.get(key)) {
        LOG.error("Unknown session: " + session, new IllegalArgumentException());

      } else if (session.decUsages() <= 0) {
        myRootSessions.remove(key);
        session.dispose();

      } else if (forceRemoval) {
        // the session may still be in use
        myRootSessions.remove(key);
        session.disown();
      }
    }
  }

  private class SessionHandle implements Handle {

    private WeakReference<TypecheckingSessionImpl> mySession = null;
    private final Flags myFlags;
    private boolean myReleased;

    public SessionHandle(Flags flags) {
      myFlags = flags;
    }

    @Override
    public synchronized TypecheckingSession session() {
      if (myReleased) {
        throw new IllegalStateException("handle already released");
      }
      TypecheckingSessionImpl session = mySession != null ? mySession.get() : null;
      if (session == null || session.isDisposed()) {
        this.mySession = new WeakReference<>(getOrCreateSession(myFlags));
        mySession.get().incUsages();
      }
      return mySession.get();
    }

    @Override
    public synchronized void release() {
      if (!myReleased) {
        if (mySession != null) {
          TypecheckingSessionImpl session = mySession.get();
          if (session != null && !session.isDisposed()) {
            releaseSession(session, false);
          }
        }
        mySession = null;
        myReleased = true;
      }
    }

    @Override
    public synchronized void invalidateAndRelease() {
      if (!myReleased) {
        if (mySession != null) {
          TypecheckingSessionImpl session = mySession.get();
          if (session != null && !session.isDisposed()) {
            releaseSession(session, true);
          }
        }
        mySession = null;
        myReleased = true;
      }
    }
  }

  /**
   * An opaque object to represent a particular SNode instance.
   * Used instead of SNodeReference, since the latter is useless for the purposes
   * of indicating a node w/o a model, which is a very common pattern in MPS. <sigh>
   */
  private static class SNodeHandle {

    private final SNode myNode;

    public SNodeHandle(SNode node) {
      this.myNode = node;
    }

    @Override
    public boolean equals(Object that) {
      return that != null && (that instanceof SNodeHandle && (((SNodeHandle) that).myNode == myNode));
    }

    @Override
    public int hashCode() {
      return System.identityHashCode(myNode)*7 + 43;
    }

    @Override
    public String toString() {
      return "SNode@"+System.identityHashCode(myNode);
    }
    
  }

}
