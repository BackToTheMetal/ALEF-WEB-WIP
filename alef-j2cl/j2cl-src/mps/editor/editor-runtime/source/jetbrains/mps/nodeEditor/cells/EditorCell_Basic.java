/*
 * Copyright 2003-2024 JetBrains s.r.o.
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
package jetbrains.mps.nodeEditor.cells;

import com.intellij.util.ui.UIUtil;
import gnu.trove.TIntObjectHashMap;
import jetbrains.mps.editor.runtime.HtmlTextBuilderImpl;
import jetbrains.mps.editor.runtime.TextBuilderImpl;
import jetbrains.mps.editor.runtime.commands.EditorCommand;
import jetbrains.mps.editor.runtime.impl.LayoutConstraints;
import jetbrains.mps.editor.runtime.style.StyleAttributes;
import jetbrains.mps.editor.runtime.style.StyleImpl;
import jetbrains.mps.lang.smodel.generator.smodelAdapter.AttributeOperations;
import jetbrains.mps.logging.Logger;
import jetbrains.mps.nodeEditor.EditorComponent;
import jetbrains.mps.nodeEditor.EditorManager;
import jetbrains.mps.nodeEditor.EditorMessage;
import jetbrains.mps.nodeEditor.EditorSettings;
import jetbrains.mps.nodeEditor.cellMenu.NodeSubstituteInfoFilterDecorator;
import jetbrains.mps.nodeEditor.cellMenu.NodeSubstitutePatternEditor;
import jetbrains.mps.nodeEditor.cells.collections.Entry;
import jetbrains.mps.nodeEditor.keyboard.TextChangeEvent;
import jetbrains.mps.openapi.editor.EditorContext;
import jetbrains.mps.openapi.editor.HtmlTextBuilder;
import jetbrains.mps.openapi.editor.TextBuilder;
import jetbrains.mps.openapi.editor.cells.CellAction;
import jetbrains.mps.openapi.editor.cells.CellActionType;
import jetbrains.mps.openapi.editor.cells.CellInfo;
import jetbrains.mps.openapi.editor.cells.CellMessagesUtil;
import jetbrains.mps.openapi.editor.cells.EditorCellContext;
import jetbrains.mps.openapi.editor.cells.KeyMap;
import jetbrains.mps.openapi.editor.cells.SubstituteAction;
import jetbrains.mps.openapi.editor.cells.SubstituteInfo;
import jetbrains.mps.openapi.editor.menus.transformation.TransformationMenuLookup;
import jetbrains.mps.openapi.editor.message.SimpleEditorMessage;
import jetbrains.mps.openapi.editor.style.Style;
import jetbrains.mps.smodel.ModelAccessHelper;
import jetbrains.mps.smodel.constraints.ModelConstraints;
import jetbrains.mps.util.ListMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.mps.openapi.language.SAbstractConcept;
import org.jetbrains.mps.openapi.language.SConcept;
import org.jetbrains.mps.openapi.language.SConceptFeature;
import org.jetbrains.mps.openapi.language.SContainmentLink;
import org.jetbrains.mps.openapi.model.SNode;
import org.jetbrains.mps.openapi.model.SNodeId;
import org.jetbrains.mps.openapi.model.SNodeUtil;
import org.jetbrains.mps.util.Condition;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class EditorCell_Basic implements EditorCell, Entry<jetbrains.mps.openapi.editor.cells.EditorCell> {
  public static final Logger LOG = Logger.getLogger(EditorCell_Basic.class);

  public static final int BRACKET_WIDTH = 7;

  private Map<Object, Object> myUserObjects;

  protected int myX = 0;
  protected int myY = 0;
  protected int myWidth = 0;
  protected int myHeight = 0;
  private int myCaretX = 0;

  private boolean myErrorState;
  private boolean mySelected;

  @NotNull
  private final EditorContext myEditorContext;

  private EditorCell_Collection myParent = null;
  private SNode myNode;
  private SNodeId myNodeId;
  private SNode myContextualNode;
  private SubstituteInfo mySubstituteInfo;
  private TransformationMenuLookup myTransformationMenuLookup;
  private final TIntObjectHashMap<CellAction> myActionMap = new TIntObjectHashMap<>();

  private final Style myStyle = new StyleImpl();

  private KeyMap myKeyMap;
  private String myCellId;
  private SConceptFeature mySRole;

  private boolean myInTree;
  private boolean myIsReferenceCell = false;
  protected int myGapLeft;
  protected int myGapRight;

  private boolean myIsNeedRelayout = true;
  private boolean myBig;
  private EditorCellContext myCellContext;
  private String myCommandGroupId;

  /**
   * {@link Entry} fields
   */
  private Entry<jetbrains.mps.openapi.editor.cells.EditorCell> myNext;
  private Entry<jetbrains.mps.openapi.editor.cells.EditorCell> myPrev;

  protected EditorCell_Basic(@NotNull EditorContext editorContext, SNode node) {
    myEditorContext = editorContext;
    myNode = node;
    myContextualNode = node;
    myNodeId = node == null ? null : node.getNodeId();
  }

  @Override
  public EditorComponent getEditor() {
    return (EditorComponent) getContext().getEditorComponent();
  }

  @Override
  public jetbrains.mps.openapi.editor.EditorComponent getEditorComponent() {
    return getContext().getEditorComponent();
  }

  @Override
  public boolean isErrorState() {
    return myErrorState;
  }

  protected boolean isPunctuationLayout() {
    return LayoutConstraints.PUNCTUATION_LAYOUT_CONSTRAINT.getName().equals(getStyle().get(StyleAttributes.LAYOUT_CONSTRAINT));
  }

  @Override
  public void setErrorState(boolean errorState) {
    boolean wasState = myErrorState;
    myErrorState = errorState;

    if (myInTree && wasState != myErrorState) {
      if (myErrorState) {
        getEditor().getCellTracker().addErrorCell(this);
      } else {
        getEditor().getCellTracker().removeErrorCell(this);
      }
    }
  }

  @Override
  public boolean validate(boolean strict, boolean canActivatePopup) {
    SubstituteInfo substituteInfo = getSubstituteInfo();
    if (substituteInfo == null) {
      return false;
    }

    String pattern = "";
    if (this instanceof EditorCell_Label) {
      pattern = ((EditorCell_Label) this).getText();
    }

    if (pattern.isEmpty()) {
      return false;
    }

    SubstituteInfo substituteInfoWithPatternMatchingFilter =
        NodeSubstituteInfoFilterDecorator.createSubstituteInfoWithPatternMatchingFilter(substituteInfo, getContext().getRepository());
    List<SubstituteAction> matchingActions = substituteInfoWithPatternMatchingFilter.getMatchingActions(pattern, strict);
    return APICellAdapter.substituteIfPossible(this, canActivatePopup, pattern, matchingActions);
  }

  public boolean isDrawBrackets() {
    return getStyle().get(StyleAttributes.DRAW_BRACKETS);
  }

  public Color getBracketsColor() {
    return getStyle().get(StyleAttributes.BRACKETS_COLOR);
  }

  @Override
  public CellAction getAction(CellActionType type) {
    return myActionMap.get(type.ordinal());
  }

  @Override
  public Collection<CellActionType> getAvailableActions() {
    final Collection<CellActionType> result = new ArrayList<>(myActionMap.size());
    myActionMap.forEach(value -> result.add(CellActionType.values()[value]));
    return result;
  }

  @Override
  public void setAction(CellActionType type, CellAction action) {
    myActionMap.put(type.ordinal(), action);
  }

  @Override
  public void addKeyMap(KeyMap keyMap) {
    if (myKeyMap != null) {
      myKeyMap.addKeyMap(keyMap);
    } else {
      myKeyMap = keyMap;
    }
  }

  @Override
  public KeyMap getKeyMap() {
    return myKeyMap;
  }

  @Override
  public SNode getSNode() {
    return myNode;
  }

  public final void setSNode(@NotNull SNode node) {
    myContextualNode = node;
    setSemanticNode(node);
  }

  public final void setSemanticNode(@NotNull SNode node) {
    myNode = node;
    myNodeId = node.getNodeId();
    updateCommandGroupId();
  }

  @Nullable
  @Override
  public SNode getContextualNode() {
    return myContextualNode;
  }

  @NotNull
  protected SNodeId getSNodeId() {
    return myNodeId;
  }

  @Override
  public int getHeight() {
    return myHeight;
  }

  @Override
  public void setHeight(int height) {
    myHeight = height;
  }

  @Override
  public int getBottom() {
    return getY() + getHeight();
  }

  @Override
  public int getRight() {
    return getX() + getWidth();
  }

  @Override
  public int getEffectiveWidth() {
    return myWidth;
  }

  @Override
  public int getLeftInset() {
    return 0;
  }

  @Override
  public int getRightInset() {
    return 0;
  }

  @Override
  public int getTopInset() {
    return 0;
  }

  @Override
  public int getBottomInset() {
    return 0;
  }

  @Override
  public int getWidth() {
    return myWidth;
  }

  @Override
  public void setWidth(int width) {
    myWidth = width;
  }

  @Override
  public int getY() {
    return myY;
  }

  @Override
  public void setY(int y) {
    if (myY == y) {
      return;
    }
    myY = y;
  }

  @Override
  public int getX() {
    return myX;
  }

  @Override
  public void setX(int x) {
    if (myX == x) {
      return;
    }
    myX = x;
  }

  @Override
  public boolean isSelected() {
    return mySelected;
  }

  public boolean isWithinSelection() {
    return mySelected && getEditorComponent().getDeepestSelectedCell() == this;
  }


  @Override
  public boolean isSelectable() {
    return getStyle().get(StyleAttributes.SELECTABLE);
  }

  @Override
  public void setSelectable(boolean selectable) {
    getStyle().set(StyleAttributes.SELECTABLE, selectable);
  }

  @Override
  public void setCellId(@NotNull String cellId) {
    assert myCellId == null;
    myCellId = cellId;
    updateCommandGroupId();
  }

  @Override
  public String getCellId() {
    return myCellId;
  }

  private void updateCommandGroupId() {
    myCommandGroupId = myCellId + "_" + myNodeId;
  }

  public String getCommandGroupId() {
    return myCommandGroupId;
  }

@Deprecated(since = "2018.2", forRemoval = true)
  @Override
  public String getRole() {
    SConceptFeature sRole = getSRole();
    return sRole == null ? null : sRole.getName();
  }

  @Override
  public SConceptFeature getSRole() {
    SConceptFeature role = getStyle().get(StyleAttributes.NAVIGATABLE_SREFERENCE);
    if (role != null) {
      return role;
    }
    return mySRole;
  }

  @Override
  public void setSRole(SConceptFeature role) {
    mySRole = role;
  }

  @Override
  public void setSelected(boolean selected) {
    mySelected = selected;
  }

  public boolean isDrawBorder() {
    return getStyle().get(StyleAttributes.DRAW_BORDER);
  }

  @Override
  public Object getUserObject(Object key) {
    if (myUserObjects == null) {
      return null;
    }
    return myUserObjects.get(key);
  }

  @Override
  public void moveTo(int x, int y) {
    myX = x;
    myY = y;
  }

  @Override
  public void putUserObject(Object key, Object value) {
    if (myUserObjects == null) {
      myUserObjects = new ListMap<>();
    }
    myUserObjects.put(key, value);
  }

  @Override
  public EditorContext getContext() {
    return myEditorContext;
  }

  @Override
  public final boolean processKeyPressed(KeyEvent e, boolean allowErrors) {
    if (e.isConsumed()) {
      return false;
    }
    return doProcessKeyPressed(e, allowErrors);
  }

  protected boolean doProcessKeyPressed(KeyEvent e, boolean allowErrors) {
    return false;
  }

  @Override
  public final boolean processKeyTyped(KeyEvent e, boolean allowErrors) {
    if (e.isConsumed()) {
      return false;
    }
    return doProcessKeyTyped(e, allowErrors);
  }

  protected boolean doProcessKeyTyped(final KeyEvent e, final boolean allowErrors) {
    if (getSNode() == null || !isBig() || !isTextTypedEvent(e)) {
      return false;
    }

    if (new ModelAccessHelper(getContext().getRepository()).runReadAction(() -> getSNode().getModel() != null && getSNode().getParent() == null)) {
      return false;
    }

    getContext().getRepository().getModelAccess().executeCommand(new EditorCommand(getContext()) {
      @Override
      public void doExecute() {
        EditorComponent editor = getEditor();
        SNode oldNode = getSNode();
        SNode newNode = replaceWithDefault();
        if (newNode == null) {
          EditorCell_Label editable = CellFinderUtil.findFirstEditable(EditorCell_Basic.this);
          if (editable != null) {
            editor.changeSelection(editable);
            editor.processKeyTyped(e);
          }
          return;
        }

        newNode.putUserObject(EditorManager.OLD_NODE_FOR_SUBSTITUTION, oldNode);
        EditorCell nodeCell = editor.findNodeCell(newNode);
        if (nodeCell == null) {
          return;
        }
        EditorCell_Label editable = CellFinderUtil.findFirstEditable(nodeCell);
        if (editable != null) {
          editor.changeSelection(editable);
          editor.processKeyTyped(e);
        } else {
          editor.changeSelection(nodeCell);
          editor.processKeyTyped(e);
        }
      }
    });

    return true;
  }

  @Override
  public boolean processTextChanged(TextChangeEvent textChangeEvent) {
    return false;
  }

  protected boolean isTextTypedEvent(KeyEvent e) {
    return UIUtil.isReallyTypedEvent(e);
  }

  private SNode replaceWithDefault() {
    SNode node = getSNode();
    while (AttributeOperations.isAttribute(node)) {
      node = node.getParent();
    }
    SContainmentLink link = node.getContainmentLink();
    if (link == null) {
      // quite unexpected, are we trying to replace a root?
      return null;
    }
    SAbstractConcept concept = link.getTargetConcept();
    SConcept concreteConcept = ModelConstraints.getDefaultConcreteConcept(concept);
    if (node.getConcept().equals(concreteConcept)) {
      return null;
    }
    SNode newNode = getContext().getModel().createNode(concreteConcept);
    SNodeUtil.replaceWithAnother(node, newNode);
    getContext().flushEvents();
    return newNode;
  }

  @Override
  public void setCaretX(int x) {
    myCaretX = x;
  }

  @Override
  public int getCaretX() {
    return myCaretX;
  }

  @Override
  public void home() {

  }

  @Override
  public void end() {

  }

  @Override
  public jetbrains.mps.openapi.editor.cells.EditorCell findLeaf(int x, int y) {
    if (getX() <= x && x < getX() + getWidth() && getY() <= y && y < getY() + getHeight()) {
      return this;
    } else {
      return null;
    }
  }

  @Override
  public jetbrains.mps.openapi.editor.cells.EditorCell findNearestLeafOnLine(int x, int y, Condition<jetbrains.mps.openapi.editor.cells.EditorCell> condition) {
    if (getY() <= y && y < getY() + getHeight() && condition.met(this)) {
      return this;
    } else {
      return null;
    }
  }

  private void collectCellsWithY(EditorCell current, int y, Set<EditorCell> cells) {
    collectCellsWithY(current, y, cells, true);
  }

  private void collectCellsWithY(EditorCell current, int y, Set<EditorCell> cells, boolean leafsOnly) {
    if (y >= current.getY() && y <= current.getY() + current.getHeight() && !(leafsOnly && current instanceof EditorCell_Collection)) {
      cells.add(current);
    }

    if (current instanceof EditorCell_Collection) {
      for (EditorCell cell : ((EditorCell_Collection) current).getCells()) {
        collectCellsWithY(cell, y, cells);
      }
    }
  }

  @Override
  public EditorCell_Collection getParent() {
    return myParent;
  }

  @Override
  public boolean isSingleNodeCell() {
    if (myParent == null) {
      return true;
    }
    if (myParent.getSNode() != myNode) {
      return true;
    }
    if (myParent.getCellsCount() == 1) {
      return myParent.isSingleNodeCell();
    }
    return false;
  }

  public void setParent(EditorCell_Collection parent) {
    myParent = parent;
  }

  @Override
  public boolean processMousePressed(MouseEvent e) {
    return false;
  }

  @Override
  public NodeSubstitutePatternEditor createSubstitutePatternEditor() {
    return new NodeSubstitutePatternEditor(myEditorContext);
  }

  @Override
  public void setSubstituteInfo(SubstituteInfo info) {
    mySubstituteInfo = info;
  }

  @Override
  public SubstituteInfo getSubstituteInfo() {
    return mySubstituteInfo;
  }

  @Nullable
  public TransformationMenuLookup getTransformationMenuLookup() {
    return myTransformationMenuLookup;
  }

  public void setTransformationMenuLookup(@Nullable TransformationMenuLookup transformationMenuLookup) {
    myTransformationMenuLookup = transformationMenuLookup;
  }

  @Override
  public void setBig(boolean big) {
    myBig = big;
  }

  @Override
  public boolean isBig() {
    return myBig;
  }

  public void setReferenceCell(boolean isReference) {
    myIsReferenceCell = isReference;
  }

  @Override
  public boolean isReferenceCell() {
    return myIsReferenceCell;
  }

  @Override
  public void paint(Graphics g) {
    paintCell(g, ParentSettings.createDefaultSetting());
    paintDecorations(g);
  }

  @Override
  public void paintCell(Graphics g, ParentSettings parentSettings) {
    fillBackground(g, parentSettings);
    paintContent(g, parentSettings);
  }

  @Override
  public boolean isInClipRegion(Graphics g) {
    // The +1 for width and height takes into account decorations such as selection or border, which may currently be drawn outside the cell.
    return g.hitClip(getX(), getY(), getWidth() + 1, getHeight() + 1);
  }

  protected ParentSettings fillBackground(Graphics g, ParentSettings parentSettings) {
    ParentSettings settings = isSelectionPaintedOnAncestor(parentSettings);
    if (!settings.isSelectionPainted()) {
      if (!parentSettings.isSkipBackground() && getStyle().isSpecified(StyleAttributes.BACKGROUND_COLOR)) {
        // BACKGROUND_COLOR is always present as it's inherited from EC defaults; here we need to paint cell background
        // only in case it was explicitly specified in the cell settings.
        // PS.isSkipBackground seems to still make sense as it reflects EditorMessage aspect rather than BG inheritable defaults.
        Color backgroundColor = getStyle().get(StyleAttributes.BACKGROUND_COLOR);
        if (backgroundColor != null) {
          g.setColor(backgroundColor);
          paintBackground(g);
        }
      }
      boolean hasMessages = false;
      for (EditorMessage message : CellMessagesUtil.getMessages(this, EditorMessage.class)) {
        if (message != null && message.showInEditor() && message.isBackground()) {
          message.paint(g, getEditor(), this);
          hasMessages = true;
        }
      }
      settings = ParentSettings.createBackgroundlessSetting(hasMessages).combineWith(parentSettings);
    }
    paintSelectionIfRequired(g, parentSettings);
    return settings;
  }

  protected ParentSettings isSelectionPaintedOnAncestor(ParentSettings parentSettings) {
    return ParentSettings.createSelectedSetting(isSelectionPainted()).combineWith(parentSettings);
  }

  protected void paintBackground(Graphics g) {
    g.fillRect(getX(), getY(), getWidth(), getHeight());
  }

  protected boolean isSelectionPainted() {
    return isSelected();
  }

  protected void paintSelectionIfRequired(Graphics g, ParentSettings parentSettings) {
    if (isSelectionPainted()) {
      paintSelection(g, getSelectionColor(), false, parentSettings);
    }
  }

  protected abstract void paintContent(Graphics g, ParentSettings parentSettings);

  public void paintDecorations(Graphics g) {
    int effectiveWidth = getEffectiveWidth();

    if (isDrawBorder()) {
      // COLORS: Remove hardcoded color
      g.setColor(Color.lightGray);
      g.drawRect(myX, myY, getWidth(), getHeight());
    }

    int leftInternalInset = getLeftInset();

    if (isDrawBrackets()) {
      g.setColor(getBracketsColor());

      // opening bracket
      g.fillRect(myX + leftInternalInset + 2, myY + 3, 2, myHeight - 5);
      g.fillRect(myX + leftInternalInset + 3, myY + 2, BRACKET_WIDTH - 3, 2);
      g.fillRect(myX + leftInternalInset + 3, myY + myHeight - 3, BRACKET_WIDTH - 3, 2);

      // closing bracket
      g.fillRect(myX + effectiveWidth - 3, myY + 3, 2, myHeight - 5);
      g.fillRect(myX + effectiveWidth - BRACKET_WIDTH + 1, myY + 2, BRACKET_WIDTH - 3, 2);
      g.fillRect(myX + effectiveWidth - BRACKET_WIDTH + 1, myY + myHeight - 3, BRACKET_WIDTH - 3, 2);
    }

    List<EditorMessage> messages = CellMessagesUtil.getMessages(this, EditorMessage.class);
    for (EditorMessage message : messages) {
      if (message != null && message.showInEditor() && !message.isBackground()) {
        message.paint(g, getEditor(), this);
      }
    }
  }

  @Override
  public List<SimpleEditorMessage> getMessages() {
    return getEditor().getHighlightManager().getMessages(this);
  }

  @Override
  public void synchronizeViewWithModel() {
  }

  @Override
  public void setBaseline(int y) {
    int relBaseline = getAscent() + getTopInset();
    moveTo(myX, y - relBaseline);
  }

  @Override
  public int getBaseline() {
    return myY + getAscent() + getTopInset();
  }

  @Override
  public int getAscent() {
    return myHeight;
  }

  @Override
  public int getDescent() {
    return myHeight - getAscent();
  }

  @Override
  public void paintSelection(Graphics g, Color c, boolean drawBorder) {
    paintSelection(g, c, drawBorder, ParentSettings.createDefaultSetting());
  }

  @Override
  public void paintSelection(Graphics g, Color c, boolean drawBorder, ParentSettings parentSettings) {
    g.setColor(c);
    g.fillRect(getX(), getY() + getTopInset(), getWidth(), getHeight() - getTopInset() - getBottomInset());
    if (getEditor().isFocusOwner() && drawBorder) {
      g.setColor(c.darker());
      g.drawRect(getX(), getY() + getTopInset(), getWidth(), getHeight());
    }
  }

  @Override
  public TextBuilder renderText() {
    return new TextBuilderImpl();
  }

  public HtmlTextBuilder renderHtml(){
    return new HtmlTextBuilderImpl();
  }

  @Override
  public final void relayout() {
    if (!myIsNeedRelayout) {
      return;
    }
    boolean drawBrackets = isDrawBrackets();
    if (drawBrackets) {
      myX += BRACKET_WIDTH;
    }
    myX += myGapLeft;

    relayoutImpl();

    if (drawBrackets) {
      myX -= BRACKET_WIDTH;
      myWidth += 2 * BRACKET_WIDTH;
    }
    myX -= myGapLeft;
    myWidth += myGapLeft + myGapRight;
    myIsNeedRelayout = false;
  }

  protected void relayoutImpl() {

  }

  @NotNull
  @Override
  public CellInfo getCellInfo() {
    return new DefaultCellInfo(this);
  }

  public Color getSelectionColor() {
    return EditorSettings.getInstance().getSelectionBackgroundColor();
  }

  public static Color getRangeSelectionColor() {
    return EditorSettings.getInstance().getRangeSelectionForegroundColor();
  }

  @Override
  public EditorCell getRootParent() {
    EditorCell cell = this;
    EditorCell prevCell = null;
    while (cell != null) {
      prevCell = cell;
      cell = (EditorCell) cell.getParent();
    }
    return prevCell;
  }

  @Override
  public EditorCell getNextSibling() {
    return myNext != null ? (EditorCell) myNext.getItem() : null;
  }

  @Override
  public EditorCell getPrevSibling() {
    return myPrev != null && myPrev.getNext() != null ? (EditorCell) myPrev.getItem() : null;
  }

  @Override
  public Style getStyle() {
    return myStyle;
  }

  public boolean isInTree() {
    return myInTree;
  }

  public void onAdd() {
    myInTree = true;

    if (isErrorState()) {
      getEditor().getCellTracker().addErrorCell(this);
    }
  }

  public void onRemove() {
    myInTree = false;

    if (isErrorState()) {
      getEditor().getCellTracker().removeErrorCell(this);
    }
  }

  @Override
  public void setLeftGap(int gap) {
    // TODO: remove this line and modify getEffectiveWidth() method in order to return
    // getWidth() + myGapRight + myGapLeft
    // most of getWidth() usages must be replaced with getEffectiveWidth() then.
    myWidth = myWidth - myGapLeft + gap;
    myGapLeft = gap;
  }

  public int getLeftGap() {
    return myGapLeft;
  }

  @Override
  public void setRightGap(int gap) {
    // TODO: remove this line and modify getEffectiveWidth() method in order to return
    // getWidth() + myGapRight + myGapLeft
    // most of getWidth() usages must be replaced with getEffectiveWidth() then.
    myWidth = myWidth - myGapRight + gap;
    myGapRight = gap;
  }

  public int getRightGap() {
    return myGapRight;
  }

  public void requestRelayout() {
    if (myIsNeedRelayout) {
      return;
    }
    myIsNeedRelayout = true;
    if (getParent() != null) {
      getParent().requestRelayout();
    }
  }

  // Following methods are used from layout algorithms
  @Override
  public final boolean wasRelayoutRequested() {
    return myIsNeedRelayout;
  }

  public void unrequestLayout() {
    myIsNeedRelayout = false;
  }

  @Override
  public void setCellContext(EditorCellContext cellContext) {
    myCellContext = cellContext;
  }

  @Override
  public EditorCellContext getCellContext() {
    return myCellContext;
  }

  /**
   * {@link Entry} methods
   */

  @NotNull
  @Override
  public jetbrains.mps.openapi.editor.cells.EditorCell getItem() {
    return this;
  }

  @Override
  public Entry<jetbrains.mps.openapi.editor.cells.EditorCell> getNext() {
    return myNext;
  }

  @Override
  public void setNext(Entry<jetbrains.mps.openapi.editor.cells.EditorCell> next) {
    myNext = next;
  }

  @Override
  public Entry<jetbrains.mps.openapi.editor.cells.EditorCell> getPrev() {
    return myPrev;
  }

  @Override
  public void setPrev(Entry<jetbrains.mps.openapi.editor.cells.EditorCell> prev) {
    myPrev = prev;
  }
}
