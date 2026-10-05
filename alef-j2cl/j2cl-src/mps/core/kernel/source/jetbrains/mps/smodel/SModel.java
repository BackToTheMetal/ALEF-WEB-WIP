/*
 * Copyright 2003-2026 JetBrains s.r.o.
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
package jetbrains.mps.smodel;

import jetbrains.mps.extapi.model.SModelBase;
import jetbrains.mps.extapi.model.SModelData;
import jetbrains.mps.extapi.model.SModelDescriptorStub;
import jetbrains.mps.logging.Logger;
import jetbrains.mps.project.structure.modules.ModuleReference;
import jetbrains.mps.project.structure.modules.RefUpdateUtil;
import jetbrains.mps.smodel.event.ModelEventDispatch;
import jetbrains.mps.smodel.event.SModelChildEvent;
import jetbrains.mps.smodel.event.SModelDevKitEvent;
import jetbrains.mps.smodel.event.SModelImportEvent;
import jetbrains.mps.smodel.event.SModelLanguageEvent;
import jetbrains.mps.smodel.event.SModelListener;
import jetbrains.mps.smodel.event.SModelPropertyEvent;
import jetbrains.mps.smodel.event.SModelReferenceEvent;
import jetbrains.mps.smodel.event.SModelRootEvent;
import jetbrains.mps.smodel.loading.UpdateModeSupport;
import jetbrains.mps.smodel.nodeidmap.INodeIdToNodeMap;
import jetbrains.mps.smodel.nodeidmap.UniversalOptimizedNodeIdMap;
import jetbrains.mps.util.StatefulUpdate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.mps.openapi.language.SContainmentLink;
import org.jetbrains.mps.openapi.language.SLanguage;
import org.jetbrains.mps.openapi.language.SProperty;
import org.jetbrains.mps.openapi.model.EditableSModel;
import org.jetbrains.mps.openapi.model.SModelId;
import org.jetbrains.mps.openapi.model.SModelName;
import org.jetbrains.mps.openapi.model.SModelReference;
import org.jetbrains.mps.openapi.model.SReference;
import org.jetbrains.mps.openapi.module.SModule;
import org.jetbrains.mps.openapi.module.SModuleReference;
import org.jetbrains.mps.openapi.module.SRepository;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A general {@link SModelData model data} implementation which (!) does not implement
 * {@link org.jetbrains.mps.openapi.model.SModel}. For {@link org.jetbrains.mps.openapi.model.SModel openapi.SModel}
 * implementations see subclasses of {@link SModelBase}.
 * <p>
 * This is all-purpose model data implementation designed to back up an API representative aka 'model descriptor'.
 * Can work with any {@link org.jetbrains.mps.openapi.model.SModel openapi.SModel} descriptor, although be careful
 * to use proper {@link #setModelDescriptor(org.jetbrains.mps.openapi.model.SModel, ModelEventDispatch)} method to
 * make sure node events get dispatched with proper model descriptor.
 * </p>
 * <p>
 * Generally, it is pure storage and shall not dispatch events, though still does this for legacy {@link SModelListener} events.
 * Note, it does that only in case associated model descriptor is {@link jetbrains.mps.extapi.model.SModelDescriptorStub}.
 * </p>
 * <p>
 * There's no synchronization nor {@link org.jetbrains.mps.openapi.module.ModelAccess model access} control, it's left up to
 * discretion of a holding model descriptor.
 * </p>
 */
public class SModel implements SModelData, UpdateModeSupport {
  private static final Logger LOG = Logger.getLogger(SModel.class);
  private static AtomicLong ourCounter = new AtomicLong();

  static {
    resetIdCounter();
  }

  private org.jetbrains.mps.openapi.model.SModel myModelDescriptor;
  private Set<SNode> myRoots = new LinkedHashSet<>();
  private SModelReference myReference;
  private boolean myDisposed;
  private List<SLanguage> myLanguagesEngagedOnGeneration = new ArrayList<>();
  private final Map<SLanguage, Integer> myLanguagesIds = new LinkedHashMap<>();
  // FIXME introduce UniqueList or ArraySet to avoid "if (!myList.contains && myList.add())" on change
  //       for both myDevKits and myImports. There's intellij's ArrayListSet, but it's dull and introduces unnecessary dependency
  private List<SModuleReference> myDevKits = new ArrayList<>();
  private List<ImportElement> myImports = new ArrayList<>();
  private INodeIdToNodeMap myIdToNodeMap;
  private StackTraceElement[] myDisposedStacktrace = null;

  /**
   * update mode, aka full load mode, is the state we are attaching newly loaded children to a model loaded partially
   * since it could happen during model read, we can't rely on model read/write action mechanism.
   * It used to be a mere boolean flag, but it doesn't help to deal with multiple getNode(SNodeId) calls, one of which triggered model load.
   * Existing implementation used to hack around with synchronized access to model data (in overridden methods SModelBase#geSModelInternal(),
   * e.g. LazyEditableSModelBase#getSModelInternal synchronize on myModel:UpdatableModel field, and UpdatableModel does synchronize on this when
   * replacing model nodes in update mode). I could have used the same approach for my model, however, don't want to
   * (a) build my model on top of LazyEditableSModelBase
   * (b) keep this synchronized code (future work, though)
   * <p>
   * Lock is not the best primitive to use here. With given API (setUpdateMode(boolean) I need smth like a semaphore I could check for being locked,
   * and wait for release (i.e. without further acquire) if necessary. Neither java's Semaphore, nor Lock are good for this, picked latter just
   * by tossing a coin (well, it respects the thread, its API is more streamlined with the activity, lock() sounds better than acquire())
   */
  private final ReentrantLock myFullLoadMode = new ReentrantLock();
  // nodes from this model communicate with it through this owner instance.
  @NotNull
  private final AttachedNodeOwner myNodeOwner;

  public SModel(@NotNull SModelReference modelReference) {
    this(modelReference, new UniversalOptimizedNodeIdMap());
  }

  public SModel(@NotNull SModelReference modelReference, INodeIdToNodeMap map) {
    myReference = modelReference;
    myIdToNodeMap = map;
    myNodeOwner = new AttachedNodeOwner(this);
  }

  static void resetIdCounter() {
    ourCounter.set(Math.abs(new SecureRandom().nextLong()));
  }

  public static SNodeId generateUniqueId() {
    long id = Math.abs(ourCounter.incrementAndGet());
    return new jetbrains.mps.smodel.SNodeId.Regular(id);
  }


  @Override
  public SModelId getModelId() {
    return myReference.getModelId();
  }

  @Override
  public SModelName getModelName() {
    return myReference.getName();
  }

  @Override
  @NotNull
  public SModelReference getReference() {
    return myReference;
  }

  @Override
  public Iterable<org.jetbrains.mps.openapi.model.SNode> getRootNodes() {
    fireModelNodesReadAccess();
    return () -> new Iterator<org.jetbrains.mps.openapi.model.SNode>() {
      private final Iterator<SNode> myIterator = myRoots.iterator();

      @Override
      public boolean hasNext() {
        return myIterator.hasNext();
      }

      @Override
      public org.jetbrains.mps.openapi.model.SNode next() {
        SNode res = myIterator.next();
        if (res != null) {
          res.assertCanRead();
          res.getNodeOwner().fireNodeRead(res, true);
        }

        return res;
      }

      @Override
      public void remove() {
        throw new UnsupportedOperationException("can't change model roots through roots iterator");
      }
    };
  }

  public boolean isRoot(@Nullable org.jetbrains.mps.openapi.model.SNode node) {
    return myRoots.contains(node);
  }

  //--------------IMPLEMENTATION-------------------

  @Override
  public void addRootNode(@NotNull final org.jetbrains.mps.openapi.model.SNode node) {
    SNode.checkBrowserRootChange(myModelDescriptor, node, true);
    assert node instanceof SNode;
    enforceFullLoad();
    if (myRoots.contains(node)) {
      // why not warn?
      return;
    }
    org.jetbrains.mps.openapi.model.SModel model = node.getModel();
    // FIXME why on earth we remove new root from original location, but don't do the same for insertChild?
    if (model != null && model != myModelDescriptor && node.getParent() == null) {
      model.removeRootNode(node);
    } else {
      org.jetbrains.mps.openapi.model.SNode parent = node.getParent();
      if (parent != null) {
        parent.removeChild(node);
      }
    }

    SNode sn = (SNode) node;
    myRoots.add(sn);
    myNodeOwner.registerNode(sn);
    myNodeOwner.performUndoableAction(new AddRootUndoableAction(node));
    myNodeOwner.fireNodeAdd(null, null, sn, null);
  }

  @Override
  public void removeRootNode(final org.jetbrains.mps.openapi.model.SNode node) {
    SNode.checkBrowserRootChange(myModelDescriptor, node, false);
    assert node instanceof SNode;
    enforceFullLoad();
    if (myRoots.contains(node)) {
      myNodeOwner.fireBeforeNodeRemove(null, null, (SNode) node, null);
      // performing undoable action while node is still in the model because VirtualFiles
      // (and so documents) are available only for those nodes existing in the model.
      myNodeOwner.performUndoableAction(new RemoveRootUndoableAction(node, myModelDescriptor));
      myRoots.remove(node);
      SNode sn = (SNode) node;
      myNodeOwner.unregisterNode(sn);
      myNodeOwner.fireNodeRemove(null, null, sn, null);
    }
  }

  @Override
  @Nullable
  public SNode getNode(@NotNull org.jetbrains.mps.openapi.model.SNodeId nodeId) {
    SNode res = getNode_(nodeId);
    if (res != null) {
      res.assertCanRead();
      myNodeOwner.fireNodeRead(res, true);
    }
    return res;
  }


  protected final void waitUpdateModeIsOver() {
    if (isUpdateMode()) {
      // there's a thread that updates the model, and myIdToNodeMap might get updated
      // thus we shall wait once update is over, and lock()/unlock() pair merely ensures update mode if over.
      // This is not a decent solution, we'd rather get separate SModelData implementation that is update-aware, and use locks/guards
      // when accessing internal fields.
      // Note, if the current thread is the one that performs the update, lock()/unlock() is fine, as the lock is re-enterable
      myFullLoadMode.lock();
      myFullLoadMode.unlock();
    }
  }

  private SNode getNode_(org.jetbrains.mps.openapi.model.SNodeId nodeId) {
    checkNotDisposed();
    if (myDisposed) {
      return null;
    }

    waitUpdateModeIsOver();

    org.jetbrains.mps.openapi.model.SNode node = myIdToNodeMap.get(nodeId);
    if (node != null) {
      return ((SNode) node);
    }
    enforceFullLoad();
    return ((SNode) myIdToNodeMap.get(nodeId));
  }

  @NotNull
  public String toString() {
    return String.format("%s(%s)", getModelName(), getModelId());
  }

  //todo get rid of, try to cast, show an error if not casted
  public boolean isDisposed() {
    return myDisposed;
  }

  //todo cast if can be
  public StackTraceElement[] getDisposedStacktrace() {
    return myDisposedStacktrace;
  }

  public final org.jetbrains.mps.openapi.model.SModel getModelDescriptor() {
    return myModelDescriptor;
  }

  /**
   * Tells this model data implementation is bound to specific model descriptor and uses a supplied mechanism to dispatch events.
   * This method is intended for use by {@link org.jetbrains.mps.openapi.model.SModel model descriptor} implementations only.
   *
   * @param modelDescriptor
   * @param eventDispatch   generally, non-null value makes sense only when {@code modelDescriptor} is not null as well.
   */
  public void setModelDescriptor(@Nullable org.jetbrains.mps.openapi.model.SModel modelDescriptor, @Nullable ModelEventDispatch eventDispatch) {
    myModelDescriptor = modelDescriptor;
    myNodeOwner.setEventDispatch(eventDispatch);
  }

  protected void enforceFullLoad() {
    org.jetbrains.mps.openapi.model.SModel md = myModelDescriptor;
    if (md != null) {
      md.load();
    }
  }

  private void fireModelNodesReadAccess() {
    if (!canFireReadEvent()) {
      return;
    }
    if (myModelDescriptor != null) {
      NodeReadEventsCaster.fireModelNodesReadAccess(myModelDescriptor);
    }
  }

//---------listeners--------

  /**
   * Name clash with {@link SNodeOwner#performUndoableAction(SNodeUndoableAction)} is unfortunate.
   * This one is rather 'registerActionWithUndo'.
   */
  protected void performUndoableAction(@NotNull SNodeUndoableAction action) {
    if (!canFireEvent()) {
      return;
    }
    // indeed, it's not nice to go back and forth from SNodeOwner, but I care to
    // get the overall picture fixed at the moment. There's subclass that needs to
    // control undo, need to fit it into the story
    myNodeOwner.commandContext().registerActionWithUndo(action);
  }

  //todo code in the following methods should be written w/o duplication

  public boolean canFireEvent() {
    return myModelDescriptor != null && myModelDescriptor.getRepository() != null && !isUpdateMode();
  }

  public boolean canFireReadEvent() {
    return canFireEvent();
  }

  /**
   * Make this SModelData unusable, free references to nodes hold to get them available to GC.
   * Doesn't dispatch any event (it's responsibility of openapi.SModel impl).
   * Disconnects from openapi.SModel descriptor, if any.
   * XXX At the moment, doesn't change owner of nodes to DetachedNodeOwner, though it seems it should.
   * The only objection is that doing so would trigger a lot of unregister events we don't really care to get.
   */
  public void dispose() {
    if (myDisposed) {
      return;
    }

    myDisposed = true;
    setModelDescriptor(null, null);
    myDisposedStacktrace = new Throwable().getStackTrace();
    myIdToNodeMap = null;
    myRoots.clear();
  }

  private void checkNotDisposed() {
    if (myDisposed) {
      final IllegalModelAccessError ex = new IllegalModelAccessError("accessing disposed model");
      LOG.error(String.format("Model %s(%s) is disposed", getModelName(), getModelId()), ex);
      if (myDisposedStacktrace != null) {
        final Throwable d = new Throwable();
        d.setStackTrace(myDisposedStacktrace);
        LOG.error(String.format("The model %s(%s) has been disposed from: ", getModelName(), getModelId()), d);
      }
    }
  }

  private List<SModelListener> getModelListeners() {
    if (myModelDescriptor instanceof SModelDescriptorStub) {
      return ((SModelDescriptorStub) myModelDescriptor).getModelListeners();
    }
    return Collections.emptyList();
  }

  private void fireDevKitAddedEvent(@NotNull SModuleReference ref) {
    if (!canFireEvent()) {
      return;
    }
    final SModelDevKitEvent event = new SModelDevKitEvent(getModelDescriptor(), ref, true);
    for (SModelListener sModelListener : getModelListeners()) {
      try {
        sModelListener.devkitAdded(event);
      } catch (Throwable t) {
        LOG.error(t);
      }
    }
  }

  private void fireDevKitRemovedEvent(@NotNull SModuleReference ref) {
    if (!canFireEvent()) {
      return;
    }
    final SModelDevKitEvent event = new SModelDevKitEvent(getModelDescriptor(), ref, false);
    for (SModelListener sModelListener : getModelListeners()) {
      try {
        sModelListener.devkitRemoved(event);
      } catch (Throwable t) {
        LOG.error(t);
      }
    }
  }

  private void fireLanguageAddedEvent(@NotNull SLanguage ref) {
    if (!canFireEvent()) {
      return;
    }
    final SModelLanguageEvent event = new SModelLanguageEvent(getModelDescriptor(), ref, true);
    for (SModelListener sModelListener : getModelListeners()) {
      try {
        sModelListener.languageAdded(event);
      } catch (Throwable t) {
        LOG.error(t);
      }
    }
  }

  private void fireLanguageRemovedEvent(@NotNull SLanguage ref) {
    if (!canFireEvent()) {
      return;
    }
    final SModelLanguageEvent event = new SModelLanguageEvent(getModelDescriptor(), ref, false);
    for (SModelListener sModelListener : getModelListeners()) {
      try {
        sModelListener.languageRemoved(event);
      } catch (Throwable t) {
        LOG.error(t);
      }
    }
  }

  private void fireImportAddedEvent(@NotNull SModelReference modelReference) {
    if (!canFireEvent()) {
      return;
    }
    final SModelImportEvent event = new SModelImportEvent(getModelDescriptor(), modelReference, true);
    for (SModelListener sModelListener : getModelListeners()) {
      try {
        sModelListener.importAdded(event);
      } catch (Throwable t) {
        LOG.error(t);
      }
    }
  }

  private void fireImportRemovedEvent(@NotNull SModelReference modelReference) {
    if (!canFireEvent()) {
      return;
    }
    final SModelImportEvent event = new SModelImportEvent(getModelDescriptor(), modelReference, false);
    for (SModelListener sModelListener : getModelListeners()) {
      try {
        sModelListener.importRemoved(event);
      } catch (Throwable t) {
        LOG.error(t);
      }
    }
  }

  /*package*/ void fireRootAddedEvent(@NotNull SNode root) {
    if (!canFireEvent()) {
      return;
    }
    final SModelRootEvent event = new SModelRootEvent(getModelDescriptor(), root, true);
    for (SModelListener sModelListener : getModelListeners()) {
      try {
        sModelListener.rootAdded(event);
      } catch (Throwable t) {
        LOG.error(t);
      }
    }
  }

  /*package*/ void fireRootRemovedEvent(@NotNull SNode root) {
    if (!canFireEvent()) {
      return;
    }
    final SModelRootEvent event = new SModelRootEvent(getModelDescriptor(), root, false);
    for (SModelListener sModelListener : getModelListeners()) {
      try {
        sModelListener.rootRemoved(event);
      } catch (Throwable t) {
        LOG.error(t);
      }
    }
  }

  /*package*/ void fireBeforeRootRemovedEvent(org.jetbrains.mps.openapi.model.SNode node) {
    if (!canFireEvent()) {
      return;
    }
    final SModelRootEvent event = new SModelRootEvent(getModelDescriptor(), node, false);
    for (SModelListener sModelListener : getModelListeners()) {
      try {
        sModelListener.beforeRootRemoved(event);
      } catch (Throwable t) {
        LOG.error(t);
      }
    }
  }

  void firePropertyChangedEvent(@NotNull SNode node, @NotNull SProperty property, @Nullable String oldValue, @Nullable String newValue) {
    if (!canFireEvent()) {
      return;
    }
    final SModelPropertyEvent event = new SModelPropertyEvent(getModelDescriptor(), property, node, oldValue, newValue);
    for (SModelListener sModelListener : getModelListeners()) {
      try {
        sModelListener.propertyChanged(event);
      } catch (Throwable t) {
        LOG.error(t);
      }
    }
  }

  void fireChildAddedEvent(@NotNull SNode parent, @NotNull SContainmentLink role, @NotNull org.jetbrains.mps.openapi.model.SNode child,
                           org.jetbrains.mps.openapi.model.SNode anchor) {
    if (!canFireEvent()) {
      return;
    }
    int childIndex = anchor == null ? 0 : parent.getChildren().indexOf(anchor) + 1;
    final SModelChildEvent event = new SModelChildEvent(getModelDescriptor(), true, parent, role, childIndex, child);
    for (SModelListener sModelListener : getModelListeners()) {
      try {
        sModelListener.childAdded(event);
      } catch (Throwable t) {
        LOG.error(t);
      }
    }
  }

  void fireChildRemovedEvent(@NotNull SNode parent, @NotNull SContainmentLink role, @NotNull org.jetbrains.mps.openapi.model.SNode child,
                             org.jetbrains.mps.openapi.model.SNode anchor) {
    if (!canFireEvent()) {
      return;
    }
    int childIndex = anchor == null ? 0 : parent.getChildren().indexOf(anchor) + 1;
    final SModelChildEvent event = new SModelChildEvent(getModelDescriptor(), false, parent, role, childIndex, child);
    for (SModelListener sModelListener : getModelListeners()) {
      try {
        sModelListener.childRemoved(event);
      } catch (Throwable t) {
        LOG.error(t);
      }
    }
  }

  void fireBeforeChildRemovedEvent(@NotNull SNode parent, @NotNull SContainmentLink role, @NotNull org.jetbrains.mps.openapi.model.SNode child,
                                   org.jetbrains.mps.openapi.model.SNode anchor) {
    if (!canFireEvent()) {
      return;
    }
    int childIndex = anchor == null ? 0 : parent.getChildren().indexOf(anchor) + 1;
    final SModelChildEvent event = new SModelChildEvent(getModelDescriptor(), false, parent, role, childIndex, child);
    for (SModelListener sModelListener : getModelListeners()) {
      try {
        sModelListener.beforeChildRemoved(event);
      } catch (Throwable t) {
        LOG.error(t);
      }
    }
  }

  void fireReferenceAddedEvent(@NotNull SReference reference) {
    if (!canFireEvent()) {
      return;
    }
    final SModelReferenceEvent event = new SModelReferenceEvent(getModelDescriptor(), reference, true);
    for (SModelListener sModelListener : getModelListeners()) {
      try {
        sModelListener.referenceAdded(event);
      } catch (Throwable t) {
        LOG.error(t);
      }
    }
  }

  void fireReferenceRemovedEvent(@NotNull SReference reference) {
    if (!canFireEvent()) {
      return;
    }
    final SModelReferenceEvent event = new SModelReferenceEvent(getModelDescriptor(), reference, false);
    for (SModelListener sModelListener : getModelListeners()) {
      try {
        sModelListener.referenceRemoved(event);
      } catch (Throwable t) {
        LOG.error(t);
      }
    }
  }

  public FastNodeFinder createFastNodeFinder() {
    return new DefaultFastNodeFinder(getModelDescriptor());
  }

  //---------node registration--------

  void registerNode(@NotNull SNode node) {
    checkNotDisposed();
    if (myDisposed) {
      return;
    }

    org.jetbrains.mps.openapi.model.SNodeId id = node.getNodeId();
    if (id == null) {
      assignNewId(node);
      return;
    }

    org.jetbrains.mps.openapi.model.SNode existingNode = myIdToNodeMap.get(id);
    if (existingNode == null) {
      myIdToNodeMap.put(node.getNodeId(), node);
    }

    if (existingNode != null && existingNode != node) {
      assignNewId(node);
    }
  }

  private void assignNewId(SNode node) {
    org.jetbrains.mps.openapi.model.SNodeId id;
    id = NodeIdentityComponent.getInstance().issue(myModelDescriptor);
    while (myIdToNodeMap.containsKey(id)) {
      resetIdCounter();
      id = NodeIdentityComponent.getInstance().issue(myModelDescriptor);
    }
    node.setId(id);
    myIdToNodeMap.put(id, node);
  }

  void unregisterNode(@NotNull SNode node) {
    org.jetbrains.mps.openapi.model.SNodeId id = node.getNodeId();
    if (myDisposed || id == null) {
      return;
    }
    myIdToNodeMap.remove(id);
  }

  //language

  public Collection<SLanguage> usedLanguages() {
    return Collections.unmodifiableSet(myLanguagesIds.keySet());
  }

  public int getLanguageImportVersion(SLanguage lang) {
    // FIXME if lang comes to the model by means of devkit, this method spits a message for each attempt to find out language version.
    //       Either stop using this method, or fix it to respect devkit
    Integer res = myLanguagesIds.get(lang);
    if (res == null) {
      LOG.error("Model %s: version for language %s not found.".formatted(getModelDescriptor().getName(), lang.getQualifiedName()));
      return -1;
    }
    return res;
  }

  public boolean deleteLanguage(@NotNull SLanguage id) {
    if (myLanguagesIds.remove(id) != null) {
      fireLanguageRemovedEvent(id);
      markChanged();
      return true;
    }
    return false;
  }

  /**
   * @deprecated model can't guess which exact version of a used language it shall record, use {@link #addLanguage(SLanguage, int)} instead
   */
  @Deprecated(since = "2026.1", forRemoval = true)
  public boolean addLanguage(@NotNull SLanguage language) {
    LOG.warnDeprecatedUse("Don't use implementation-level SModel.addLanguage()");
    return addLanguage(language, -1);
  }

  /**
   * Records a language usage with a provided version. If there's already an import of the language, does nothing.
   * Use {@link #setLanguageImportVersion(SLanguage, int)} in case you need to update just the version.
   * This method doesn't make any assumptions about the version (e.g. it's the actual one, or non-existent)
   * @since 2026.1
   */
  public boolean addLanguage(@NotNull SLanguage language, int version) {
    if (myLanguagesIds.containsKey(language)) {
      return false;
    }
    setLanguageVersionInternal(language, version);
    fireLanguageAddedEvent(language);
    return true;
  }

  public void setLanguageImportVersion(SLanguage language, int version) {
    if (!myLanguagesIds.containsKey(language)) {
      throw new IllegalStateException("Can't change version for non-existing language import. Model: " + getModelDescriptor() + "; language: " + language);
    }

    setLanguageVersionInternal(language, version);
  }

  private void setLanguageVersionInternal(SLanguage language, int version) {
    myLanguagesIds.put(language, version);
    markChanged();
  }

  //devkit

  public List<SModuleReference> importedDevkits() {
    return Collections.unmodifiableList(myDevKits);
  }

  public boolean addDevKit(SModuleReference ref) {
    if (!myDevKits.contains(ref) && myDevKits.add(ref)) {
      fireDevKitAddedEvent(ref);
      markChanged();
      return true;
    }
    return false;
  }

  public boolean deleteDevKit(@NotNull SModuleReference ref) {
    if (myDevKits.remove(ref)) {
      fireDevKitRemovedEvent(ref);
      markChanged();
      return true;
    }
    return false;
  }

  //model

  public List<ImportElement> importedModels() {
    return Collections.unmodifiableList(myImports);
  }

  public boolean addModelImport(ImportElement importElement) {
    // myImports is ArrayList, AL.add() doesn't check for presence.
    if (!myImports.contains(importElement) && myImports.add(importElement)) {
      fireImportAddedEvent(importElement.getModelReference());
      markChanged();
      return true;
    }
    return false;
  }

  public boolean deleteModelImport(ImportElement importElement) {
    if (myImports.remove(importElement)) {
      fireImportRemovedEvent(importElement.getModelReference());
      markChanged();
      return true;
    }
    return false;
  }

  private void markChanged() {
    if (myModelDescriptor == null) {
      return;
    }
    org.jetbrains.mps.openapi.model.SModel model = getModelDescriptor();
    if (model instanceof EditableSModel) {
      ((EditableSModel) model).setChanged(true);
    }
  }

  public Collection<SLanguage> getLanguagesEngagedOnGeneration() {
    return myLanguagesEngagedOnGeneration;
  }

  public void addEngagedOnGenerationLanguage(SLanguage ref) {
    if (!myLanguagesEngagedOnGeneration.contains(ref)) {
      myLanguagesEngagedOnGeneration.add(ref);
      // don't send event but mark model as changed
      // FIXME comment above makes no sense, and is just a historical artifact (there's !isLoaded() when it was introduced)
      //       however, I wonder why 'markChanged' is guarded by canFireEvent() -  does it mean models that don't fire event deserve no 'changed' state?
      if (canFireEvent()) {
        markChanged();
      }
    }
    // as long as these languages make sense for m2m process, there seems to be no need to notify about changes
  }

  public void removeEngagedOnGenerationLanguage(SLanguage ref) {
    if (myLanguagesEngagedOnGeneration.contains(ref)) {
      myLanguagesEngagedOnGeneration.remove(ref);
      // don't send event but mark model as changed
      if (canFireEvent()) {
        markChanged();
      }
    }
  }

  //aspects / additional

  /**
   * update mode means we are attaching newly loaded children
   */
  public boolean isUpdateMode() {
    return myFullLoadMode.isLocked();
  }

  @Override
  public void enterUpdateMode() {
    myFullLoadMode.lock();
  }

  @Override
  public void leaveUpdateMode() {
    myFullLoadMode.unlock();
  }

  //to use only from SNode
  protected SRepository getRepository() {
    return myModelDescriptor == null ? null : myModelDescriptor.getRepository();
  }

  //---------refactorings--------

  // To allow update of models not yet attached to a repo (e.g. when we read a model and are going to attach it in with
  // refreshed state, rather than attach first and then refresh), we pass repository from outside rather than using this.getRepository()
  public boolean updateExternalReferences(@NotNull SRepository repository) {
    // XXX I don't like this forced loading, to me, it has to be external code that shall care
    // about model loading state, however, dealing with this use won't help to address 7 others
    enforceFullLoad();

    boolean changed = false;
    // XXX RefUpdateUtil uses MR.differs() which, unlike MR.equals(), compares names and ignores global uniqueness fact. Is that what we
    //     want for references inside nodes? Perhaps, makes sense to use differs for imports only?
    final StatefulUpdate<SModelReference> modelRefUpdate = new RefUpdateUtil(repository).withState();
    for (org.jetbrains.mps.openapi.model.SNode node : myIdToNodeMap.values()) {
      for (SReference reference : node.getReferences()) {
        SModelReference oldReference = reference.getTargetSModelReference();
        if (oldReference == null || !(reference instanceof StaticReference)) {
          continue;
        }
        // FWIW there's similar code in vcs.DiffModelUtil
        if (modelRefUpdate.isChanged(oldReference)) {
          changed = true;
          ((StaticReference) reference).setTargetSModelReference(modelRefUpdate.newValue(oldReference));
        }
      }
    }

    for (ImportElement e : myImports) {
      if (modelRefUpdate.isChanged(e.getModelReference())) {
        changed = true;
        e.myModelReference = modelRefUpdate.newValue(e.getModelReference());
      }
    }

    for (int i = 0; i < myDevKits.size(); i++) {
      SModuleReference ref = myDevKits.get(i);
      SModule module = ref.resolve(repository);
      if (module != null) {
        SModuleReference newRef = module.getModuleReference();
        myDevKits.set(i, newRef);
        changed = changed || ModuleReference.differs(ref, newRef);
      }
    }

    return changed;
  }

  public void changeModelReference(SModelReference newModelReference) {
    myReference = newModelReference;
  }

  public void changeNodeId(@NotNull org.jetbrains.mps.openapi.model.SNodeId existingNodeId, @NotNull org.jetbrains.mps.openapi.model.SNodeId newId) {
    SNode existing = getNode_(existingNodeId);
    if (existing == null) {
      throw new IllegalArgumentException(String.format("No node with id %s in the model %s", existingNodeId, getModelName()));
    }
    if (getNode_(newId) != null) {
      throw new IllegalArgumentException(String.format("Node with id %s already present in the model %s", existingNodeId, getModelName()));
    }
    org.jetbrains.mps.openapi.model.SNode removed = myIdToNodeMap.remove(existingNodeId);
    assert removed == existing;
    existing._setId(newId);
    myIdToNodeMap.put(newId, existing);
  }

  public SModel createEmptyCopy() {
    return new jetbrains.mps.smodel.SModel(getReference());
  }

  public void copyPropertiesTo(SModel to) {

    for (ImportElement ie : importedModels()) {
      to.addModelImport(ie.copy());
    }
    for (SModuleReference mr : importedDevkits()) {
      to.addDevKit(mr);
    }
    for (SLanguage lang : usedLanguages()) {
      to.addLanguage(lang, getLanguageImportVersion(lang));
    }
    for (SLanguage mr : getLanguagesEngagedOnGeneration()) {
      to.addEngagedOnGenerationLanguage(mr);
    }
  }

  public static final class ImportElement {
    private SModelReference myModelReference;
    private final int myReferenceID;  // persistence related index
    private final int myUsedVersion;

    @Deprecated
    public ImportElement(SModelReference modelReference, int referenceID) {
      this(modelReference, referenceID, -1);
    }

    @Deprecated
    public ImportElement(SModelReference modelReference, int referenceID, int usedVersion) {
      myModelReference = modelReference;
      myReferenceID = referenceID;
      myUsedVersion = usedVersion;
    }

    public ImportElement(SModelReference modelReference) {
      myModelReference = modelReference;
      myReferenceID = 0;
      myUsedVersion = -1;
    }

    public SModelReference getModelReference() {
      return myModelReference;
    }

    /**
     * @deprecated not in use, to become no-op and fade away
     */
    @Deprecated(since = "2026.1", forRemoval = true)
    public void setModelReference(SModelReference modelReference) {
      LOG.warnDeprecatedUse("Stop using SModel.ImportElement#setModelReference()!");
      myModelReference = modelReference;
    }

    /**
     * @deprecated not in use except for legacy code. once last use gone, remove
     */
    @Deprecated(since = "2026.1", forRemoval = true)
    public int getReferenceID() {
      return myReferenceID;
    }

    /**
     * @deprecated not in use except for legacy code. once last use gone, remove
     */
    @Deprecated(since = "2026.1", forRemoval = true)
    public int getUsedVersion() {
      LOG.warnDeprecatedUse("Stop using SModel.ImportElement#getUsedVersion()!");
      return myUsedVersion;
    }

    protected ImportElement copy() {
      return new ImportElement(myModelReference, myReferenceID, myUsedVersion);
    }

    public String toString() {
      return "ImportElement(" +
             "uid=" + myModelReference + ", " +
             "referenceId=" + myReferenceID + ", " +
             "usedVersion=" + myUsedVersion + ")";
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }

      ImportElement that = (ImportElement) o;

      if (myReferenceID != that.myReferenceID) {
        return false;
      }
      if (myUsedVersion != that.myUsedVersion) {
        return false;
      }
      return myModelReference != null ? myModelReference.equals(that.myModelReference) : that.myModelReference == null;
    }

    @Override
    public int hashCode() {
      int result = myModelReference != null ? myModelReference.hashCode() : 0;
      result = 31 * result + myReferenceID;
      result = 31 * result + myUsedVersion;
      return result;
    }
  }
}
