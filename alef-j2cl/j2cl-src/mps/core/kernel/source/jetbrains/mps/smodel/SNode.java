/*
 * Copyright 2003-2025 JetBrains s.r.o.
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

import jetbrains.mps.extapi.model.ResolveInfoExt;
import jetbrains.mps.logging.Logger;
import jetbrains.mps.smodel.AssociationData.DirectNode;
import jetbrains.mps.smodel.AssociationData.DynamicPtr;
import jetbrains.mps.smodel.AssociationData.IndirectNodePtr;
import jetbrains.mps.smodel.AssociationData.LocalNodePtr;
import jetbrains.mps.smodel.AssociationData.SNodeAssociationUpdate;
import jetbrains.mps.smodel.event.SModelPropertyEvent;
import jetbrains.mps.util.containers.EmptyIterable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.mps.openapi.language.SAbstractConcept;
import org.jetbrains.mps.openapi.language.SConcept;
import org.jetbrains.mps.openapi.language.SContainmentLink;
import org.jetbrains.mps.openapi.language.SProperty;
import org.jetbrains.mps.openapi.language.SReferenceLink;
import org.jetbrains.mps.openapi.model.ResolveInfo;
import org.jetbrains.mps.openapi.model.ResolveInfo.D;
import org.jetbrains.mps.openapi.model.ResolveInfo.N;
import org.jetbrains.mps.openapi.model.SNodeReference;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

import static jetbrains.mps.util.SNodeOperations.getDebugText;

/**
 * As a tribute to legacy code, we do allow access to constant and meta-info objects of a node without read access.
 * It's not encouraged for a new code, though, and might change in future, that's why it's stated here and not in openapi.SNode
 *
 * How come this one is in [kernel], not [smodel]?
 */
public class SNode implements org.jetbrains.mps.openapi.model.SNode, SNodeAssociationUpdate {
  private static final Logger LOG = Logger.getLogger(SNode.class);
  private static final String[] EMPTY_STRING_ARRAY = new String[0];
  private static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];
  private static final Object USER_OBJECT_LOCK = new Object();

  /**
   * For an attached node, owner of the node and all its children is the same, the same instance of AttachedNodeOwner.
   * For a detached node, owner is DetachedNodeOwner. Its children, however, may be FreeFloatNodeOwner.
   * For a newly created, free-floating node, owner if FreeFloatNodeOwner. Children of a free-floating node, however, could have DetachedNodeOwner in case
   * they were previously removed (aka detached) from a model.
   * Once node is attached to a model, {@link AttachedNodeOwner#registerNode(SNode)} is responsible to ensure complete hierarchy of added node, whether it's
   * detached, free-floating or mixed, is initialized with the same AttachedNodeOwner of the model itself.
   */
  @NotNull
  private SNodeOwner myOwner = FreeFloatNodeOwner.INSTANCE;
  private SContainmentLink myRoleInParent;

  // field is never null; but individual elements may be null. References are unordered!
  // Note, when switch to keeping ref data only, may introduce a placeholder that would
  // simplify makeReferencesDirect() implementation (empty methods instead of != null check)
  // Like myProperties, keeps pairs of [SReferenceLink, AssociationData]
  private Object[] myReferences = EMPTY_OBJECT_ARRAY;

  // Keeps pairs [SProperty, String]
  private Object[] myProperties = null;
  private org.jetbrains.mps.openapi.model.SNodeId myId;

  /**
   * seems to be ok, and still extremely fragile since it is important to reset the reference to the field on _any_ element change.
   * I'd rather use some atomic array primitive from java.lang.concurrent
   */
  @SuppressWarnings("VolatileArrayField")
  private volatile Object[] myUserObjects; // key,value,key,value ; copy-on-write (!)
  private final SConcept myConcept;
  private SNode parent;
  /**
   * access only in firstChild()/firstChildInRole(role)
   */
  private SNode first;
  private SNode next;  // == null only for the last child in the list
  private SNode prev;  // notNull, myFirstChild.myLeftSibling = the last child

  public SNode(@NotNull SConcept concept) {
    this(concept, SModel.generateUniqueId());
  }

  public SNode(@NotNull SConcept concept, @NotNull org.jetbrains.mps.openapi.model.SNodeId id) {
    myConcept = concept;
    myId = id;
  }

  @NotNull
  @Override
  public SConcept getConcept() {
    // deliberately no assertCanRead(). It's constant field and meta-info.
    return myConcept;
  }

  @Override
  public boolean isInstanceOfConcept(@NotNull SAbstractConcept c) {
    return getConcept().isSubConceptOf(c);
  }

  @Override
  public void insertChildAfter(@NotNull SContainmentLink role, @NotNull org.jetbrains.mps.openapi.model.SNode child,
      @Nullable org.jetbrains.mps.openapi.model.SNode anchor) {
    if (anchor == null) {
      insertChildBefore(role, child, getFirstChild());
    } else {
      insertChildBefore(role, child, anchor.getNextSibling());
    }
  }

  protected final void assertCanRead() {
    myOwner.assertLegalRead();
  }

  /** Application policy hook at the actual node mutation boundary, including undo. */
  public interface BrowserChangeGuard {
    boolean canChangeNode(org.jetbrains.mps.openapi.model.SNode node);
    void beforeNodeChange(org.jetbrains.mps.openapi.model.SNode node);
    void beforeRootChange(org.jetbrains.mps.openapi.model.SModel model, org.jetbrains.mps.openapi.model.SNode root, boolean adding);
  }
  private static BrowserChangeGuard browserChangeGuard;
  public static void setBrowserChangeGuard(BrowserChangeGuard guard) { browserChangeGuard = guard; }
  public static boolean isBrowserEditable(org.jetbrains.mps.openapi.model.SNode node) {
    return browserChangeGuard == null || browserChangeGuard.canChangeNode(node);
  }
  static void checkBrowserRootChange(org.jetbrains.mps.openapi.model.SModel model, org.jetbrains.mps.openapi.model.SNode root, boolean adding) {
    if (browserChangeGuard != null) browserChangeGuard.beforeRootChange(model, root, adding);
  }
  private void assertCanChange() {
    myOwner.assertLegalChange();
    if (browserChangeGuard != null) browserChangeGuard.beforeNodeChange(this);
  }

  @Override
  public org.jetbrains.mps.openapi.model.SNodeId getNodeId() {
    // deliberately no assertCanRead. It's constant field and sort of meta-info, why to constraint to read access?
    return myId;
  }

  @Override
  @NotNull
  public SNode getContainingRoot() {
    assertCanRead();

    SNode current = this;

    while (true) {
      if (current.treeParent() == null) return current;

      current = current.treeParent();
      myOwner.fireNodeRead(current, false);
    }
  }

  @Override
  public String getName() {
    assertCanRead();

    if (getConcept().isSubConceptOf(SNodeUtil.concept_INamedConcept)) {
      return getProperty(SNodeUtil.property_INamedConcept_name);
    } else {
      myOwner.fireNodeRead(this, false);
      return null;
    }
  }

  @Override
  final public SNode getParent() {
    assertCanRead();

    SNode parent = treeParent();

    if (parent != null) {
      myOwner.fireNodeRead(parent, true);
    }
    return parent;
  }

  /**
   * Removes child from current node. This affects only link between current node and its child, but not links in
   * subtree of child node.
   * <p/>
   * Differs from {@link SNode#delete()}. FIXME please explain how it differs from delete()
   *
   * @param child
   */
  @Override
  public void removeChild(@NotNull org.jetbrains.mps.openapi.model.SNode child) {
    assertCanChange();
    assert
        child.getParent() == this :
        "Can't remove a node not from it's parent node: removing " + child.getReference().toString() + " from " + getReference().toString();

    final SNode wasChild = (SNode) child;
    final SContainmentLink wasRole = wasChild.getContainmentLink();
    final SNode anchorPrev = firstChild() == wasChild ? null : wasChild.treePrevious();
    final SNode anchorNext = wasChild.treeNext();

    assert wasRole != null;
    myOwner.fireBeforeNodeRemove(this, wasRole, wasChild, anchorPrev);

    children_remove(wasChild);
    wasChild.myRoleInParent = null;

    myOwner.unregisterNode(wasChild);

    myOwner.performUndoableAction(new RemoveChildUndoableAction(this, anchorNext, wasRole, wasChild));
    myOwner.fireNodeRemove(this, wasRole, wasChild, anchorPrev);
  }

  /**
   * Deletes all nodes in subtree starting with current. Differs from {@link SNode#removeChild(org.jetbrains.mps.openapi.model.SNode)}.
   */
  @Override
  public void delete() {
    assertCanChange();

    SNode p = getParent();
    if (p != null) {
      p.removeChild(this);
    } else if (myOwner.getModel() != null) {
      myOwner.getModel().removeRootNode(this);
    }
  }

  @Override
  public String getPresentation() {
    if (!getConcept().isValid()) {
      String persistentName = findProperty(SNodeUtil.property_INamedConcept_name);
      return String.format("%s (concept is not found)", persistentName != null ? persistentName : myConcept.getName());
    }

    return String.valueOf(SNodeUtil.getPresentation(this));
  }

  @Override
  public String toString() {
    String s = null;
    try {
      s = findProperty(SNodeUtil.property_INamedConcept_name);
      if (s == null) {
        s = String.format("(instance of %s)", getConcept().getName());
      }
    } catch (RuntimeException t) {
      LOG.error("Failed to get string presentation of a node", t, this);
    }
    if (s == null) {
      return "???";
    }
    return s;
  }

  @NotNull
  @Override
  public SNodeReference getReference() {
    return new jetbrains.mps.smodel.SNodePointer(myOwner.lastKnownModel(), myId);
  }

  @Override
  public Object getUserObject(Object key) {
    final Object[] userObjects = myUserObjects;
    if (userObjects == null) return null;
    for (int i = 0; i < userObjects.length; i += 2) {
      if (userObjects[i].equals(key)) {
        return userObjects[i + 1];
      }
    }
    return null;
  }

  @Override
  public void putUserObject(Object key, @Nullable Object value) {
    synchronized (USER_OBJECT_LOCK) {
      if (value == null) {
        if (myUserObjects == null) return;
        for (int i = 0; i < myUserObjects.length; i += 2) {
          if (myUserObjects[i].equals(key)) {
            Object[] newarr = new Object[myUserObjects.length - 2];
            if (i > 0) {
              System.arraycopy(myUserObjects, 0, newarr, 0, i);
            }
            if (i + 2 < myUserObjects.length) {
              System.arraycopy(myUserObjects, i + 2, newarr, i, newarr.length - i);
            }
            myUserObjects = newarr;
            break;
          }
        }
        if (myUserObjects.length == 0) {
          myUserObjects = null;
        }
        return;
      }

      if (myUserObjects == null) {
        myUserObjects = new Object[]{key, value};
        return;
      }

      final int curLen = myUserObjects.length;
      for (int i = 0; i < curLen; i += 2) {
        if (myUserObjects[i].equals(key)) {
          Object[] newarr = new Object[myUserObjects.length];
          System.arraycopy(myUserObjects, 0, newarr, 0, curLen);
          newarr[i + 1] = value;
          myUserObjects = newarr;
          return;
        }
      }
      Object[] newarr = new Object[curLen + 2];
      System.arraycopy(myUserObjects, 0, newarr, 0, curLen);
      newarr[curLen] = key;
      newarr[curLen+1] = value;
      myUserObjects = newarr;
    }
  }

  @NotNull
  @Override
  public List<SNode> getChildren() {
    return getChildren((SContainmentLink) null);
  }

  @NotNull
  @Override
  public List<jetbrains.mps.smodel.SReference> getReferences() {
    assertCanRead();

    myOwner.fireNodeRead(this, true);

    ArrayList<jetbrains.mps.smodel.SReference> rv = new ArrayList<>(myReferences.length >>> 1);
    for (int i = 1, x = myReferences.length; i < x; i+=2) {
      final Object d = myReferences[i];
      if (d != null) {
        final SReferenceLink link = (SReferenceLink) myReferences[i - 1];
        rv.add(toAPI(link, d));
      }
    }
    // XXX there's override of the method in mps-extensions that doesn't allow me to use openapi.SReference at the moment
    //     (without simultaneous change in MPS-extensions as well)
    return rv;
  }

  @Override
  public org.jetbrains.mps.openapi.model.SNode getFirstChild() {
    assertCanRead();

    SNode child = firstChild();
    if (child != null) {
      myOwner.fireNodeRead(child, false);
    }
    return child;
  }

  @Override
  public org.jetbrains.mps.openapi.model.SNode getLastChild() {
    assertCanRead();

    SNode fc = firstChild();
    if (fc == null) {
      return null;
    }

    SNode lc = fc.treePrevious();
    if (lc != null) {
      myOwner.fireNodeRead(lc, false);
    }
    return lc;
  }

  @Override
  public SNode getPrevSibling() {
    assertCanRead();

    SNode p = treeParent();
    if (p == null) {
      return null;
    }

    myOwner.fireNodeRead(p, true);

    SNode tp = treePrevious();
    SNode ps = tp.next == null ? null : tp;
    if (ps != null) {
      myOwner.fireNodeRead(ps, true);
    }
    return ps;
  }

  @Override
  public SNode getNextSibling() {
    assertCanRead();

    SNode p = treeParent();
    if (p == null) {
      return null;
    }

    myOwner.fireNodeRead(p, true);

    SNode tn = treeNext();
    if (tn != null) {
      myOwner.fireNodeRead(tn, true); // although it used to send 2, not 3 notification, don't see any reason to have it different for parent and sibling
    }
    return tn;
  }

  @Override
  public Iterable<Object> getUserObjectKeys() {
    assertCanRead();

    final Object[] userObjects = myUserObjects;
    if (userObjects == null || userObjects.length == 0) return EmptyIterable.getInstance();
    return () -> new Iterator<Object>() {
      private int myIndex = 0;

      @Override
      public boolean hasNext() {
        return myIndex < userObjects.length;
      }

      @Override
      public Object next() {
        myIndex += 2;
        return userObjects[myIndex - 2];
      }

      @Override
      public void remove() {
        throw new UnsupportedOperationException();
      }
    };
  }

  @Override
  public org.jetbrains.mps.openapi.model.SModel getModel() {
    final SModel persistenceModel = myOwner.getModel();
    return persistenceModel == null ? null : persistenceModel.getModelDescriptor();
  }

  //----------------------------------------------------------
  //----------------USAGES IN REFACTORINGS ONLY---------------
  //----------------------------------------------------------

  public void setId(@Nullable org.jetbrains.mps.openapi.model.SNodeId id) {
    if (Objects.equals(id, myId)) return;

    if (myOwner.getModel() == null) {
      myId = id;
    } else {
      LOG.error("can't set id to registered node " + getDebugText(this), new Throwable());
    }
  }

  /*package*/ final void _setId(@NotNull org.jetbrains.mps.openapi.model.SNodeId id) {
    myId = id;
  }

  /*package*/ SReference toAPI(SReferenceLink link, Object associationData) {
    // both arguments not null
    if (associationData instanceof DynamicPtr) {
      return new DynamicReference(link, this, (DynamicPtr) associationData);
    } else {
      return new StaticReference(link, this, (AssociationData) associationData);
    }
  }


  @Override
  public void updateAssociation(SReferenceLink link, AssociationData oldRef, AssociationData newRef) {
    for (int i = 0, x = myReferences.length; i < x; i+=2) {
      if (link.equals(myReferences[i])) {
        assert myReferences[i+1] == oldRef;
        myReferences[i+1] = newRef;
        return;
      }
    }
    assert false : String.format("attempt to update missing association link %s", link);
  }

  /**
   * for a subtree starting at this node, apply the function to each association link known to the node.
   * This operation usually follows attachment of a node/subtree or precedes detachment of a node/subtree.
   */
  /*package*/ final void forEachAssociationDeep(Function<AssociationData, AssociationData> translate) {
    for (int i = 1, x = myReferences.length; i < x; i+=2) {
      AssociationData d = (AssociationData) myReferences[i];
      if (d == null) {
        continue;
      }
      myReferences[i] = translate.apply(d);
    }

    for (SNode child = firstChild(); child != null; child = child.treeNext()) {
      child.forEachAssociationDeep(translate);
    }
  }

  /**
   * apply the function to each association link known to the node.
   * unlike {@link #forEachAssociationDeep(Function)}, doesn't visit children
   */
  /*package*/ final void forEachAssociationShallow(BiFunction<SReferenceLink, AssociationData, AssociationData> translate) {
    for (int i = 1, x = myReferences.length; i < x; i += 2) {
      AssociationData d = (AssociationData) myReferences[i];
      if (d == null) {
        continue;
      }
      myReferences[i] = translate.apply((SReferenceLink) myReferences[i-1], d);
    }
  }

//  final void forEachInSubtree(Consumer<SNode> c) {
//    c.accept(this);
//    for (SNode child = firstChild(); child != null; child = child.treeNext()) {
//      child.forEachInSubtree(c);
//    }
//  }

  @NotNull
  /*package*/ SNodeOwner getNodeOwner() {
    // FIXME for consistency, shall use same approach to dispatch events from e.g. getParent(), where I use
    // owner of the child node (in assumption owner is identical for the whole tree) myOwner.fireNodeRead(parent, true);
    // and in ChildrenIterator, which I don't want to make non-static, nor don't want to pass SNodeOwner in there right now
    // FIXME revisit uses of this method, re-consider approach. E.g. perhaps SModel shall keep SNodeOwner instance?
    return myOwner;
  }

  /*package*/ void setNodeOwner(/*NotNull*/ SNodeOwner owner) {
    myOwner = owner;
  }

  protected SNode firstChild() {
    return first;
  }

  protected SNode treePrevious() {
    return prev;
  }

  public SNode treeNext() {
    return next;
  }

  protected SNode treeParent() {
    return parent;
  }

  //-------------new methods working by id-----------------

  protected void children_insertBefore(SNode anchor, @NotNull SNode node) {
    //be sure that getFirstChild is called before any access to myFirstChild
    SNode firstChild = firstChild();

    node.parent = this;

    if (firstChild == null) {
      assert anchor == null;
      first = node;
      node.next = null;
      node.prev = node;
      return;
    }

    if (anchor == null) {
      SNode lastChild = firstChild.prev;
      node.next = null;
      node.prev = lastChild;
      firstChild.prev = node;
      lastChild.next = node;
      return;
    }

    node.next = anchor;
    node.prev = anchor.prev;
    if (anchor != firstChild) {
      anchor.prev.next = node;
    } else {
      first = node;
    }
    anchor.prev = node;
  }

  protected void children_remove(@NotNull SNode node) {
    //be sure that getFirstChild is called before any access to myFirstChild
    SNode firstChild = firstChild();
    if (firstChild == node) {
      first = node.next;
      if (first != null) {
        first.prev = node.prev;
      }
    } else {
      node.prev.next = node.next;
      if (node.next != null) {
        node.next.prev = node.prev;
      } else {
        firstChild.prev = node.prev;
      }
    }
    node.prev = node.next = null;
    node.parent = null;
  }

  @Override
  public SContainmentLink getContainmentLink() {
    return myRoleInParent;
  }

  @Override
  public boolean hasProperty(@NotNull SProperty property) {
    assertCanRead();

    String val = findProperty(property);
    myOwner.firePropertyRead(this, property, val, true);
    return !SModelPropertyEvent.isEmptyPropertyValue(val);
  }

  @Override
  public String getProperty(@NotNull SProperty property) {
    assertCanRead();

    String value = findProperty(property);
    myOwner.firePropertyRead(this, property, value, false);
    return value;
  }

  /**
   * Bare access, no notifications nor checks
   */
  private String findProperty(SProperty property) {
    if (myProperties != null) {
      int index = getPropertyIndex(property);
      if (index != -1) {
        return (String) myProperties[index + 1];
      }
    }
    return null;
  }

  @Override
  public void setProperty(@NotNull final SProperty property, String propertyValue) {
    myOwner.assertLegalChange();

    int index = getPropertyIndex(property);
    final String oldValue = index == -1 ? null : (String) myProperties[index + 1];
    if (Objects.equals(oldValue, propertyValue)) return;
    if (browserChangeGuard != null) browserChangeGuard.beforeNodeChange(this);

    if (propertyValue == null) {
      Object[] oldProperties = myProperties;
      int newLength = oldProperties.length - 2;
      if (newLength == 0) {
        myProperties = null;
      } else {
        myProperties = new Object[newLength];
        System.arraycopy(oldProperties, 0, myProperties, 0, index);
        System.arraycopy(oldProperties, index + 2, myProperties, index, newLength - index);
      }
    } else if (oldValue == null) {
      Object[] oldProperties = myProperties == null ? EMPTY_STRING_ARRAY : myProperties;
      myProperties = new Object[oldProperties.length + 2];
      System.arraycopy(oldProperties, 0, myProperties, 0, oldProperties.length);
      myProperties[myProperties.length - 2] = property;
      myProperties[myProperties.length - 1] = propertyValue;
    } else {
      myProperties[index + 1] = propertyValue;
    }

    myOwner.performUndoableAction(new PropertyChangeUndoableAction(this, property, oldValue, propertyValue));

    myOwner.firePropertyChange(this, property, oldValue, propertyValue);
  }

  @NotNull
  @Override
  public Iterable<SProperty> getProperties() {
    assertCanRead();

    myOwner.fireNodeRead(this, true);

    if (myProperties == null) return new EmptyIterable<>();
    List<SProperty> result = new ArrayList<>(myProperties.length / 2);
    for (int i = 0; i < myProperties.length; i += 2) {
      result.add((SProperty) myProperties[i]);
    }
    return result;
  }

  @Override
  public void setReferenceTarget(@NotNull SReferenceLink role, @Nullable org.jetbrains.mps.openapi.model.SNode target) {
    assertCanChange();
    final AssociationData newValue;
    if (target == null) {
      newValue = null; // basically, == dropReference() without extra assertCanChange
    } else {
      if (getModel() != null && target.getModel() != null) {
        // 'mature' reference
        if (getModel() == target.getModel()) {
          newValue = new LocalNodePtr(target.getNodeId(), target.getName());
        } else {
          newValue = new IndirectNodePtr(target.getModel().getReference(), target.getNodeId(), target.getName());
        }
      } else {
        newValue = new DirectNode(target);
      }
    }
    doSetAssociation(role, newValue);
  }

  @Override
  public void setReference(@NotNull SReferenceLink role, ResolveInfo resolveInfo) {
    if (resolveInfo instanceof ResolveInfoExt) {
      setReference(role, ((ResolveInfoExt) resolveInfo).create(this, role));
    } else if (resolveInfo instanceof ResolveInfo.S) {
      String ri = ((ResolveInfo.S) resolveInfo).getValue();
      assertCanChange();
      doSetAssociation(role, new DynamicPtr(ri));
    } else if (resolveInfo instanceof ResolveInfo.PS) {
      ResolveInfo.PS ri = (ResolveInfo.PS) resolveInfo;
      assertCanChange();
      SNodeReference target = ri.getTargetNode();
      doSetAssociation(role, new IndirectNodePtr(target.getModelReference(), target.getNodeId(), ri.getValue()));
    } else if (resolveInfo instanceof ResolveInfo.N) {
      assertCanChange();
      doSetAssociation(role, new DirectNode(((N) resolveInfo).getTargetNode()));
    } else if (resolveInfo instanceof ResolveInfo.D) {
      assertCanChange();
      doSetAssociation(role, new LocalNodePtr(((ResolveInfo.D) resolveInfo).getTargetNode(), ((D) resolveInfo).getValue()));
    } else if (resolveInfo == null) {
      LOG.warning("Unexpected use of ResolveInfo == null. Reference would be removed, although explicit dropReference() has to be used", new Throwable());
      dropReference(role);
    } else {
      LOG.warning(String.format("Unexpected ResolveInfo kind: %s(%s)", resolveInfo, resolveInfo.getClass()), new Throwable());
    }
  }

  @Override
  public void setReference(@NotNull SReferenceLink role, @NotNull SNodeReference target) {
    assertCanChange();
    if (target.getModelReference() != null && target.getModelReference().equals(getReference().getModelReference())) {
      doSetAssociation(role, new LocalNodePtr(target.getNodeId(), null));
    } else {
      doSetAssociation(role, new IndirectNodePtr(target.getModelReference(), target.getNodeId(), null));
    }
  }

  @Override
  public SNode getReferenceTarget(@NotNull SReferenceLink role) {
    assertCanRead();

    SReference reference = findReference(role);
    SNode result = reference == null ? null : (SNode) reference.getTargetNode();
    myOwner.fireReferenceRead(this, role, result);
    return result;
  }

  @Override
  public SReference getReference(@NotNull SReferenceLink role) {
    assertCanRead();

    SReference result = findReference(role);
    myOwner.fireReferenceRead(this, role, null);
    return result;
  }

  /**
   * Bare access, no notifications nor checks
   */
  private SReference findReference(@NotNull SReferenceLink role) {
    for (int i = 0, x = myReferences.length; i < x; i+=2) {
      final Object link = myReferences[i];
      if (role.equals(link)) {
        return toAPI(role, myReferences[i+1]);
      }
    }
    return null;
  }

  // clear or replace an SReference
  // package visibility for undoable actions
  /*package*/ void doSetAssociation(/*not null*/ SReferenceLink role, /*nullable*/ AssociationData newValue) {
    AssociationData oldValue = null;
    int i = 1, x = myReferences.length, empty = x;
    for (; i < x; i+=2) {
      Object r = myReferences[i];
      if (r == null) {
        empty = i; // fine to take the latest empty, but if not, may add (&& empty == x) into condition
        continue;
      }
      if (role.equals(myReferences[i-1])) {
        oldValue = ((AssociationData) r); // don't assign oldValue unless matched
        break;
      }
    }
    if (i >= x && newValue == null) {
      // none found and nothing to add/replace with. no referenceChanged event.
      return;
    }
    if (i < x) {
      // found existing, just replace
      myOwner.performUndoableAction(new RemoveReferenceAtUndoableAction(this, role, oldValue));
      if (newValue != null) {
        myReferences[i] = newValue;
        myOwner.performUndoableAction(new InsertReferenceAtUndoableAction(this, role, newValue));
      } else {
        // drop
        myReferences[i-1] = null;
        myReferences[i] = null;
        // check if we just replaced the last existing reference with null
        boolean allNulls = true;
        for (int j = 1; j < x; j += 2) {
          if (myReferences[j] != null) {
            allNulls = false;
            break;
          }
        }
        if (allNulls) {
          myReferences = EMPTY_OBJECT_ARRAY;
        }
      }
      myOwner.fireReferenceChange(this, role, oldValue, newValue);
      return;
    }
    assert i >= x && newValue != null; // in fact, with +=2, it's i > x
    if (empty < x) {
      // there's available slot
      myReferences[empty-1] = role;
      myReferences[empty] = newValue;
    } else {
      // no space available, storage have to grow. Allocate 2 slots (x2 Objects) right away
      Object[] newArray = new Object[x + 4];
      System.arraycopy(myReferences, 0, newArray, 0, x);
      newArray[x] = role;
      newArray[x+1] = newValue;
      myReferences = newArray;
    }
    myOwner.performUndoableAction(new InsertReferenceAtUndoableAction(this, role, newValue));
    myOwner.fireReferenceChange(this, role, null, newValue);
  }

  // FIXME odd to have role parameter, while SReference.getLink gives exactly what's needed (and doesn't violate consistency)
  // to clear reference, one could use setReferenceTarget(). Alternatively, SReference shall not keep
  // the meta-object, and query its source node for role instead (as a free-floating Reference shall not answer its SReferenceLink).
  //
  // XXX besides, unlike setReference(role, (SNode) null), used to send out referenceChanged event even if no respective link was found.
  @Override
  public void setReference(@NotNull SReferenceLink role, org.jetbrains.mps.openapi.model.SReference toAdd) {
    // FIXME why assert, not RuntimeException?! OTOH, as I retire uses of the method, shall I care?
    assert toAdd == null || toAdd.getSourceNode() == this;
    assert toAdd == null || role.equals(toAdd.getLink());
    assertCanChange();
    doSetAssociation(role, toAdd == null ? null : ((SReference) toAdd).getData());
  }

  @Override
  public void dropReference(@NotNull SReferenceLink role) {
    assertCanChange();
    doSetAssociation(role, null);
  }

  public void insertChildBefore(@NotNull final SContainmentLink role, @NotNull org.jetbrains.mps.openapi.model.SNode child,
                                @Nullable final org.jetbrains.mps.openapi.model.SNode anchor) {
    assertCanChange();

    final SNode schild = (SNode) child;
    SNode parentOfChild = schild.getParent();
    if (parentOfChild != null) {
      final String fmt = "%s already has parent: %s\nCouldn't add it to: %s";
      final String m = String.format(fmt, getDebugText(schild), getDebugText(parentOfChild), getDebugText(this));
      throw new IllegalModelAccessException(m);
    }
    final SModel childModel = schild.getNodeOwner().getModel();
    if (childModel != null) {
      if (childModel.isRoot(schild)) {
        final String fmt = "Attempt to add root %s from model %s to node %s.";
        throw new IllegalModelAccessException(String.format(fmt, getDebugText(schild), childModel, getDebugText(this)));
      } else {
        final String fmt = "Node to add (%s) belongs to a model. Couldn't add it to %s. Shall detach it/remove from the model %s first.";
        throw new IllegalModelAccessException(String.format(fmt, getDebugText(schild), getDebugText(this), childModel));
      }
    }

    if (getContainingRoot() == child) {
      throw new IllegalModelAccessException("Trying to create a cyclic tree");
    }

    if (anchor != null) {
      if (anchor.getParent() != this) {
        throw new IllegalModelAccessException(
            "anchor is not a child of this node" + " | " +
                "this: " + getDebugText(this) + " | " +
                "anchor: " + getDebugText(anchor)
        );
      }
    }

    schild.myRoleInParent = role;
    children_insertBefore(((SNode) anchor), schild);

    myOwner.registerNode(schild);

    myOwner.performUndoableAction(new InsertChildAtUndoableAction(this, anchor, role, child));

    myOwner.fireNodeAdd(this, role, schild, (SNode) anchor);
  }

  @Override
  public void addChild(@NotNull SContainmentLink role, @NotNull org.jetbrains.mps.openapi.model.SNode child) {
    insertChildBefore(role, child, null);
  }

  @Override
  @NotNull
  public List<SNode> getChildren(SContainmentLink role) {
    SNode firstChild = firstChild();

    if (role != null) {
      while (firstChild != null && !role.equals(firstChild.getContainmentLink())) {
        firstChild = firstChild.treeNext();
      }
    }

    if (firstChild == null) {
      return Collections.emptyList();
    }

    return new ImmutableChildrenList(firstChild, role);
  }

  private int getPropertyIndex(SProperty id) {
    if (myProperties == null) return -1;
    for (int i = 0; i < myProperties.length; i += 2) {
      if (id.equals(myProperties[i])) return i;
    }
    return -1;
  }

  @Deprecated
  @Override
  public String getRoleInParent() {
    SContainmentLink cl = getContainmentLink();
    if (cl == null) return null;
    return cl.getRoleName();
  }

  @Deprecated
  @Override
  public final boolean hasProperty(String propertyName) {
    return new SNodeLegacy(this).hasProperty(propertyName);
  }

  @Deprecated
  @Override
  public final String getProperty(String propertyName) {
    return new SNodeLegacy(this).getProperty(propertyName);
  }

  @Deprecated
  @Override
  public void setProperty(String propertyName, String propertyValue) {
    new SNodeLegacy(this).setProperty(propertyName, propertyValue);
  }

  @Deprecated
  @Override
  public Collection<String> getPropertyNames() {
    List<String> res = new ArrayList<>(myProperties == null ? 0 : myProperties.length / 2);
    for (SProperty p : getProperties()) {
      res.add(p.getName());
    }
    return res;
  }

  @Deprecated
  @Override
  public SNode getReferenceTarget(String role) {
    return new SNodeLegacy(this).getReferenceTarget(role);
  }

  @Deprecated
  public void insertChildBefore(@NotNull String role, org.jetbrains.mps.openapi.model.SNode child,
      @Nullable final org.jetbrains.mps.openapi.model.SNode anchor) {
    new SNodeLegacy(this).insertChildBefore(role, child, anchor);
  }

  @Deprecated
  @Override
  public void addChild(String role, org.jetbrains.mps.openapi.model.SNode child) {
    new SNodeLegacy(this).insertChildBefore(role, child, null);
  }

  @Deprecated
  @Override
  @NotNull
  public List<SNode> getChildren(String role) {
    return new SNodeLegacy(this).getChildren(role);
  }
}
