/*
 * Copyright 2003-2011 JetBrains s.r.o.
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
package jetbrains.mps.newTypesystem.context.component;

import gnu.trove.THashSet;
import jetbrains.mps.newTypesystem.context.typechecking.BaseTypechecking;
import jetbrains.mps.newTypesystem.context.typechecking.IncrementalTypechecking;
import jetbrains.mps.newTypesystem.state.State;
import jetbrains.mps.smodel.AbstractNodesReadListener;
import jetbrains.mps.typechecking.CacheState;
import org.jetbrains.mps.openapi.model.SNode;
import jetbrains.mps.util.Pair;

import java.time.Instant;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/*package*/ public abstract class IncrementalTypecheckingComponent<STATE extends State> extends SimpleTypecheckingComponent<STATE> {

  private AtomicReference<InvalidationResult> myInvalidation = new AtomicReference<>(InvalidationResult.never());
  // TODO: drop this feature
  private AtomicBoolean myCacheWasRebuilt = new AtomicBoolean(false);
  private Set<SNode> myCurrentNodesToInvalidate = new THashSet<>();
  private AccessTracking myAccessTracking = null;
  private AtomicReference<Instant> myLastChecked = new AtomicReference<>(Instant.MIN); // never

  protected IncrementalTypecheckingComponent(STATE state, BaseTypechecking component) {
    super(state, component);
  }

  @Override
  protected abstract boolean doInvalidate();

  @Override
  public void setChecked() {
    super.setChecked();
    myLastChecked.set(Instant.now());
  }

  /**
   * Returns true if either full check has occurred after {@param since}, or there were rules invalidated and
   * a re-check is required.
   */
  public boolean hasUpdates(Instant since) {
    // Browser wall clocks can return the same instant for edit/check in one tick.
    // Conservatively report equal timestamps so that no completed check is missed.
    boolean isCheckedSince = !myLastChecked.get().equals(Instant.MIN) && !myLastChecked.get().isBefore(since);
    InvalidationResult invalidationResult = myInvalidation.get();
    boolean invalidated = !invalidationResult.getInvalidationTime().isBefore(since) && invalidationResult.hasInvalidated();
    return isCheckedSince || invalidated;
  }

  public CacheState getCacheState() {
    return new MyCacheState();
  }

  @Deprecated(forRemoval = true)
  public void setInvalidationWasPerformed(boolean invalidationWasPerformed) {
    // try to keep backward compatibility
    if (!invalidationWasPerformed) {
      clearInvalidation();
    }
  }

  public InvalidationResult getInvalidationResult() {
    return myInvalidation.get();
  }

  public void addNodeToInvalidate(SNode node) {
    myCurrentNodesToInvalidate.add(node);
    clearInvalidation();
  }

  @Override
  public void clear() {
    super.clear();
    myLastChecked.set(Instant.MIN);
    myCacheWasRebuilt.set(false);
    myInvalidation.set(InvalidationResult.never());
  }

  public void runWithAccessTracking(SNode contextNode, Runnable runnable) {
    AccessTracking accessTracking = myAccessTracking != null ? myAccessTracking : new AccessTracking();
    accessTracking.installReadListeners();
    try {
      runnable.run();
    } finally {
      accessTracking.removeReadListeners();
    }
    accessTracking.postProcess(contextNode);
  }

  @Override
  protected void solveInequalities(AccessTracking accessTracking) {
    this.myAccessTracking = accessTracking;
    try {
      super.solveInequalities(accessTracking);
    } finally {
      this.myAccessTracking = null;
    }
  }

  @Override
  protected IncrementalTypechecking getTypechecking() {
    return (IncrementalTypechecking) super.getTypechecking();
  }

  public void clearNodeTypes() {
    myCurrentNodesToInvalidate.clear();
  }

  protected void clearInvalidation() {
    myInvalidation.set(InvalidationResult.never());
  }

  protected boolean isInvalidationWasPerformed() {
    return myInvalidation.get().hasOccuredSince(Instant.MIN);
  }

  /**
   * @deprecated never true
   * @return
   */
  @Deprecated(forRemoval = true)
  protected boolean isCacheWasRebuilt() {
    return myCacheWasRebuilt.get();
  }

  protected boolean hasInvalidated() {
    return myInvalidation.get().hasInvalidated();
  }

  protected Set<SNode> getCurrentNodesToInvalidate() {
    return new THashSet<>(myCurrentNodesToInvalidate);
  }

  /*
   *  Single-threaded
   */
  protected class MyEventsReadListener extends AbstractNodesReadListener {
    private Set<SNode> myAccessedNodes = new THashSet<>(1);
    private Set<Pair<SNode, String>> myAccessedProperties = new THashSet<>(1);

    public MyEventsReadListener() {
    }

    @Override
    public void nodeChildReadAccess(SNode node, String childRole, SNode child) {
      myAccessedNodes.add(node);
      if (child != null) {
        myAccessedNodes.add(child);
      }
    }

    @Override
    public void nodePropertyReadAccess(SNode node, String propertyName, String value) {
      myAccessedProperties.add(new Pair<>(node, propertyName));
    }

    @Override
    public void nodeReferentReadAccess(SNode node, String referentRole, SNode referent) {
      myAccessedNodes.add(node);
      if (referent != null) {
        myAccessedNodes.add(referent);
      }
    }

    @Override
    public void nodeUnclassifiedReadAccess(SNode node) {
      myAccessedNodes.add(node);
    }

    public Set<SNode> getAccessedNodes() {
      return myAccessedNodes;
    }

    public void clear() {
      myAccessedNodes = new THashSet<>();
      myAccessedProperties = new THashSet<>();
    }

    public Set<Pair<SNode, String>> getAccessedProperties() {
      return myAccessedProperties;
    }
  }

  protected void setInvalidation(boolean result) {
    myCacheWasRebuilt.set(false);
    myInvalidation.set(InvalidationResult.of(result));
  }

  protected class MyCacheState implements CacheState {
    @Override
    public boolean isUpToDate() {
      return isChecked();
    }

    @Override
    public boolean hasChangedSince(Instant since) {
      return hasUpdates(since);
    }
  }

}
