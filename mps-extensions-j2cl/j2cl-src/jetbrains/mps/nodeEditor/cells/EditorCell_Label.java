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

import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.util.text.HtmlBuilder;
import com.intellij.openapi.util.text.HtmlChunk;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.util.io.URLUtil;
import com.intellij.util.ui.StartupUiUtil;
import jetbrains.mps.editor.runtime.HtmlTextBuilderImpl;
import jetbrains.mps.editor.runtime.TextBuilderImpl;
import jetbrains.mps.editor.runtime.cells.AbstractCellAction;
import jetbrains.mps.editor.runtime.cells.CaretState;
import jetbrains.mps.editor.runtime.commands.EditorComputable;
import jetbrains.mps.editor.runtime.style.Padding;
import jetbrains.mps.editor.runtime.style.StyleAttributes;
import jetbrains.mps.editor.runtime.style.StyleAttributesUtil;
import jetbrains.mps.ide.datatransfer.CopyPasteUtil;
import jetbrains.mps.ide.datatransfer.TextPasteUtil;
import jetbrains.mps.ide.undo.WorkbenchUndoHandler;
import jetbrains.mps.nodeEditor.CellSide;
import jetbrains.mps.nodeEditor.EditorSettings;
import jetbrains.mps.nodeEditor.IntelligentInputUtil;
import jetbrains.mps.nodeEditor.IntelligentInputUtil.IntelligentCellProcessor;
import jetbrains.mps.nodeEditor.cellLayout.PunctuationUtil;
import jetbrains.mps.nodeEditor.cellMenu.CompletionHelper;
import jetbrains.mps.nodeEditor.cellMenu.NodeSubstitutePatternEditor;
import jetbrains.mps.nodeEditor.documentation.MPSDocumentationUtil;
import jetbrains.mps.nodeEditor.keyboard.TextChangeEvent;
import jetbrains.mps.nodeEditor.selection.EditorCellLabelSelection;
import jetbrains.mps.openapi.editor.ActionHandler;
import jetbrains.mps.openapi.editor.EditorComponent;
import jetbrains.mps.openapi.editor.EditorContext;
import jetbrains.mps.openapi.editor.HtmlTextBuilder;
import jetbrains.mps.openapi.editor.TextBuilder;
import jetbrains.mps.openapi.editor.cells.CellActionType;
import jetbrains.mps.openapi.editor.cells.CellTraversalUtil;
import jetbrains.mps.openapi.editor.cells.EditorCell;
import jetbrains.mps.openapi.editor.cells.SubstituteInfo;
import jetbrains.mps.openapi.editor.cells.optional.WithCaret;
import jetbrains.mps.openapi.editor.selection.MultipleSelection;
import jetbrains.mps.openapi.editor.selection.SelectionManager;
import jetbrains.mps.smodel.ModelCommandContext;
import jetbrains.mps.smodel.ModelCommandContext.Provider;
import jetbrains.mps.smodel.SNodeUndoableAction;
import jetbrains.mps.smodel.UndoRunnable;
import jetbrains.mps.typechecking.TypecheckingFacade;
import jetbrains.mps.typechecking.TypecheckingSession;
import jetbrains.mps.util.NameUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.mps.openapi.model.SNode;
import org.jetbrains.mps.openapi.module.ModelAccess;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.Objects;

public abstract class EditorCell_Label extends EditorCell_Basic implements jetbrains.mps.openapi.editor.cells.EditorCell_Label, WithCaret {
  protected boolean myNoTextSet;
  protected TextLine myTextLine;
  protected TextLine myNullTextLine;
  protected CaretState myCaretState = new CaretState();

  protected EditorCell_Label(@NotNull EditorContext editorContext, SNode node, String text) {
    super(editorContext, node);
    myTextLine = new TextLine("", getStyle(), false, editorContext.getEditorComponent().getEditorComponentSettings());
    myNullTextLine = new TextLine("", getStyle(), true, editorContext.getEditorComponent().getEditorComponentSettings());

    myTextLine.setCaretEnabled(true);
    myNullTextLine.setCaretEnabled(true);
    setText(text);

    setAction(CellActionType.LEFT, new MoveLeft(false));
    setAction(CellActionType.RIGHT, new MoveRight(false));

    setAction(CellActionType.LOCAL_HOME, new LocalHome(false));
    setAction(CellActionType.LOCAL_END, new LocalEnd(false));

    setAction(CellActionType.SELECT_RIGHT, new MoveRight(true));
    setAction(CellActionType.SELECT_LEFT, new MoveLeft(true));

    setAction(CellActionType.SELECT_HOME, new SelectHome());
    setAction(CellActionType.SELECT_END, new SelectEnd());

    setAction(CellActionType.SELECT_LOCAL_HOME, new LocalHome(true));
    setAction(CellActionType.SELECT_LOCAL_END, new LocalEnd(true));

    setAction(CellActionType.COPY, new CopyLabelText());
    setAction(CellActionType.PASTE, new PasteIntoLabelText());
    setAction(CellActionType.CUT, new CutLabelText());
    setAction(CellActionType.CLEAR_SELECTION, new ClearSelection());
  }

  public boolean canPasteText() {
    return true;
  }

  @Override
  public void setSelected(boolean selected) {
    super.setSelected(selected);
    if (!selected && !getEditor().selectionStackContains(this)) {
      myTextLine.resetSelection();
    }
    myCaretState.touch(selected);
  }

  @Override
  public String getText() {
    return myTextLine.getText();
  }

  public String getNullText() {
    return myNullTextLine.getText();
  }

  public String getRenderedText() {
    return getRenderedTextLine().getText();
  }

  private String getRenderedHtml() {
    HtmlBuilder htmlBuilder = new HtmlBuilder();
    for (Character c : getRenderedText().toCharArray()) {
      if (c == ' ') {
        htmlBuilder.append(HtmlChunk.nbsp());
      } else {
        htmlBuilder.append(c.toString());
      }
    }
    HtmlChunk htmlChunk = htmlBuilder.toFragment();

    // format
    boolean isBold = this.getRenderedTextLine().getFont().isBold();
    if (isBold) {
      htmlChunk = htmlChunk.bold();
    }
    boolean isUnderlined = this.getRenderedTextLine().isUnderlined();
    if (isUnderlined) {
      htmlChunk = htmlChunk.wrapWith("u");
    }

    String link = null;
    String url = this.getStyle().get(StyleAttributes.URL);
    if (url != null){
      if (!url.startsWith(URLUtil.HTTP_PROTOCOL)) {
        url = VirtualFileManager.constructUrl(URLUtil.HTTP_PROTOCOL, url);
      }
      link = MPSDocumentationUtil.getLinkForUrl(url);
    } else {
      SNode node = APICellAdapter.getSNodeWRTReference(this);
      if (node != null && (node != this.getSNode() || APICellAdapter.hasExplicitNavigatableNode(this))){
        link = MPSDocumentationUtil.getLinkForSNodeReference(node.getReference());
      }
    }
    if (link != null) {
      htmlChunk = HtmlChunk.link(link, htmlChunk);
    }

    // color
    Color color = this.getRenderedTextLine().getTextColor();
    String rgbString = "rgb(" + color.getRed() + ", " + color.getGreen() + ", " + color.getBlue() + ")";
    String rgbBlack1 = "rgb(8, 8, 8)";
    String rgbBlack2 = "rgb(0, 0, 0)";
    if (!(rgbBlack1.equals(rgbString) || rgbBlack2.equals(rgbString))) {
      htmlChunk = HtmlChunk.font("rgb(" + color.getRed() + ", " + color.getGreen() + ", " + color.getBlue() + ")").child(htmlChunk);
    }

    // font-size
    int fontSize = StartupUiUtil.getLabelFont().getSize();
    float cellFontSize = this.getRenderedTextLine().getFont().getSize2D();
    if (fontSize != cellFontSize) {
      htmlChunk = HtmlChunk.font((int) cellFontSize).child(htmlChunk);
    }
    return htmlChunk.toString();
  }

  public Font getFont() {
    return getRenderedTextLine().getFont();
  }

  public void setTextColor(Color color) {
    getStyle().set(StyleAttributes.TEXT_COLOR, color);
  }

  public void setNullTextColor(Color color) {
    getStyle().set(StyleAttributes.NULL_TEXT_COLOR, color);
  }

  public void setTextBackgroundColor(Color color) {
    getStyle().set(StyleAttributes.TEXT_BACKGROUND_COLOR, color);
  }

  public void setNullTextBackgroundColor(Color color) {
    getStyle().set(StyleAttributes.NULL_TEXT_BACKGROUND_COLOR, color);
  }

  public void setSelectedTextBackgroundColor(Color color) {
    getStyle().set(StyleAttributes.SELECTED_TEXT_BACKGROUND_COLOR, color);
  }

  public void setNullSelectedTextBackgroundColor(Color color) {
    getStyle().set(StyleAttributes.NULL_SELECTED_TEXT_BACKGROUND_COLOR, color);
  }

  @Override
  public int getCaretPosition() {
    return myTextLine.getCaretPosition();
  }

  @Override
  public void setCaretPosition(int position) {
    setCaretPosition(position, false);
  }

  public void setCaretPositionIfPossible(int position) {
    if (isCaretPositionAllowed(position)) {
      setCaretPosition(position, false);
    }
  }

  public void setCaretPosition(int position, boolean selection) {
    assert isCaretPositionAllowed(position) : "Position " + position + " is not allowed for EditorCell_Label: \"" + myTextLine.getText() + "\"";
    repaintCaret();
    myTextLine.setCaretPosition(position, selection);
    myCaretState.touch();
    repaintCaret();
  }

  public boolean isCaretPositionAllowed(int position) {
    if (getStyle().get(StyleAttributes.PLACEHOLDER) && position != 0) {
      return false;
    }
    if (!StyleAttributesUtil.isFirstPositionAllowed(getStyle()) && position == 0) {
      return false;
    }
    if (!StyleAttributesUtil.isLastPositionAllowed(getStyle()) && position == myTextLine.getText().length()) {
      return false;
    }
    return position >= 0 && position <= myTextLine.getText().length();
  }

  @Override
  public void home() {
    int textLength = getText().length();
    if (StyleAttributesUtil.isFirstPositionAllowed(getStyle())) {
      if (textLength > 0 || StyleAttributesUtil.isLastPositionAllowed(getStyle())) {
        setCaretPosition(0);
      }
    } else {
      if (textLength > 0 && (textLength > 1 || StyleAttributesUtil.isLastPositionAllowed(getStyle()))) {
        setCaretPosition(1);
      }
    }
  }

  @Override
  public void end() {
    if (getStyle().get(StyleAttributes.PLACEHOLDER)) {
      setCaretPosition(0);
    } else {
      int textLength = getText().length();
      if (StyleAttributesUtil.isLastPositionAllowed(getStyle())) {
        if (textLength > 0 || StyleAttributesUtil.isFirstPositionAllowed(getStyle())) {
          setCaretPosition(getText().length());
        }
      } else {
        if (textLength > 0 && (textLength > 1 || StyleAttributesUtil.isFirstPositionAllowed(getStyle()))) {
          setCaretPosition(getText().length() - 1);
        }
      }
    }
  }

  @Override
  public boolean isSelectable() {
    if (!super.isSelectable()) {
      return false;
    }

    if (getText() == null || getText().length() == 0) {
      return StyleAttributesUtil.isFirstPositionAllowed(getStyle()) && StyleAttributesUtil.isLastPositionAllowed(getStyle());
    }

    if (getText().length() == 1) {
      return StyleAttributesUtil.isFirstPositionAllowed(getStyle()) || StyleAttributesUtil.isLastPositionAllowed(getStyle());
    }

    return true;
  }

  @Override
  public boolean isFirstCaretPosition() {
    if (!StyleAttributesUtil.isFirstPositionAllowed(getStyle())) {
      return getCaretPosition() == 1;
    } else {
      return getCaretPosition() == 0;
    }
  }

  @Override
  public boolean isLastCaretPosition() {
    if (!StyleAttributesUtil.isLastPositionAllowed(getStyle())) {
      return getCaretPosition() == getText().length() - 1;
    } else {
      return getCaretPosition() == getText().length();
    }
  }

  @Override
  public boolean isCaretEnabled() {
    return myTextLine.isCaretEnabled();
  }

  @Override
  public void setCaretEnabled(boolean enabled) {
    myTextLine.setCaretEnabled(enabled);
  }

  public void setText(String text) {
    myNoTextSet = (text == null || text.length() == 0);
    myTextLine.setText(myNoTextSet ? null : text);
    requestRelayout();
  }

  public void setDefaultText(String text) {
    myNullTextLine.setText(text);
  }

  @Override
  public int getEffectiveWidth() {
    return getTextLineWidth();
  }

  @Override
  public int getLeftInset() {
    return getRenderedTextLine().getPaddingLeft() + myGapLeft;
  }

  @Override
  public int getRightInset() {
    return getRenderedTextLine().getPaddingRight() + myGapRight;
  }

  @Override
  public int getTopInset() {
    return getRenderedTextLine().getPaddingTop();
  }

  @Override
  public int getBottomInset() {
    return getRenderedTextLine().getPaddingBottom();
  }

  public int getTextLineWidth() {
    int textLineWidth;
    if (myNoTextSet && myTextLine.getText().length() == 0) {
      textLineWidth = myNullTextLine.getEffectiveWidth();
    } else {
      textLineWidth = myTextLine.getEffectiveWidth();
    }
    if (isDrawBrackets()) {
      textLineWidth += 2 * BRACKET_WIDTH;
    }
    return textLineWidth;
  }

  public boolean isEditable() {
    return getStyle().get(StyleAttributes.EDITABLE);
  }

  public void setEditable(boolean editable) {
    getStyle().set(StyleAttributes.EDITABLE, editable);
  }

  @Override
  public void setErrorState(boolean errorState) {
    super.setErrorState(errorState);
    if (errorState) {
      myTextLine.showErrorColor();
      myNullTextLine.showErrorColor();
    } else {
      myTextLine.showTextColor();
      myNullTextLine.showTextColor();
    }
  }

  @Override
  public void relayoutImpl() {
    if (isPunctuationLayout()) {
      getStyle().set(StyleAttributes.PADDING_LEFT, new Padding(0.0));
      getStyle().set(StyleAttributes.PADDING_RIGHT, new Padding(0.0));
    }

    if (myNoTextSet && myTextLine.getText().length() == 0) {
      myNullTextLine.relayout();
      myHeight = myNullTextLine.getHeight();
      myWidth = myNullTextLine.getWidth();
    } else {
      myTextLine.relayout();
      myHeight = myTextLine.getHeight();
      myWidth = myTextLine.getWidth();
    }
  }

  @Override
  public void switchCaretVisible() {
    myCaretState.tick();
    repaintCaret();
  }

  @Override
  public void setCaretVisible(boolean visible) {
    myCaretState.touch(visible);
    repaintCaret();
  }

  private void repaintCaret() {
    getRenderedTextLine().repaintCaret(getEditor(), getShiftX(), getY());
  }

  @Override
  protected boolean isSelectionPainted() {
    return isSelected() && getEditor().getSelectionManager().getSelection() instanceof MultipleSelection;
  }

  @Override
  protected void paintContent(Graphics g, ParentSettings parentSettings) {
    TextLine textLine = getRenderedTextLine();
    boolean toShowCaret = toShowCaret();
    boolean selected = isSelectionPaintedOnAncestor(parentSettings).isSelectionPainted();
    textLine.setSelected(selected);
    textLine.setShowCaret(toShowCaret);
    Color cellFontColor = getEditor().getAdditionalCellFontColor(this);
    textLine.paint(g, getShiftX(), myY, cellFontColor);
  }

  @Override
  public void paintSelection(Graphics g, Color c, boolean drawBorder, ParentSettings parentSettings) {
    if (!isSelectionPaintedOnAncestor(parentSettings).isSelectionPainted() && getEditor().getAdditionalCellFontColor(this) != null) {
      /*
       * Suppressing selection painting in case this cell is not actually selected and additionalCellFontColor() for it is not null.
       * This will hide messages feedback if there is an AdditionalPainter instance (with specified cellFontColor) covering this cell.
       * Probably it's good idea to use separate property (not cellFontColor) to determine if this AdditionalPainter is "hiding" messages feedback
       * or simply let some additional painters paint background below and above editor messages.
       */
      return;
    }
    super.paintSelection(g, c, drawBorder, parentSettings);
  }

  protected boolean toShowCaret() {
    return myCaretState.isVisible() && isWithinSelection();
  }

  TextLine getTextLine() {
    return myTextLine;
  }

  TextLine getNullTextLine() {
    return myNullTextLine;
  }

  public TextLine getRenderedTextLine() {
    TextLine textLine;
    if (myNoTextSet && myTextLine.getText().length() == 0) {
      textLine = myNullTextLine;
    } else {
      textLine = myTextLine;
    }
    return textLine;
  }

  @Override
  public void setCaretX(int x) {
    myTextLine.setCaretByXCoord(x - getShiftX());
    makePositionValid();
  }

  @Override
  public int getCaretX() {
    return myTextLine.getCaretX(getShiftX());
  }

  private int getShiftX() {
    return isDrawBrackets() ? myX + myGapLeft + BRACKET_WIDTH : myX + myGapLeft;
  }

  private void makePositionValid() {
    if (getStyle().get(StyleAttributes.PLACEHOLDER) && myTextLine.getCaretPosition() > 0) {
      setCaretPosition(0);
    }
    if (myTextLine.getCaretPosition() == 0 && !StyleAttributesUtil.isFirstPositionAllowed(getStyle()) && isCaretPositionAllowed(1)) {
      setCaretPosition(1);
    }
    if (myTextLine.getCaretPosition() == getText().length() && !StyleAttributesUtil.isLastPositionAllowed(getStyle()) &&
        isCaretPositionAllowed(getText().length() - 1)) {
      setCaretPosition(getText().length() - 1);
    }
    getEditor().getBracesHighlighter().updateBracesSelection(this);
  }

  @Override
  public boolean processMousePressed(MouseEvent e) {
    setCaretX(e.getX());
    getEditor().repaint(this);
    return true;
  }

  public void ensureCaretVisible() {
    getEditor().scrollRectToVisible(new Rectangle(getCaretX() - 2 * myTextLine.charWidth(), myY, 4 * myTextLine.charWidth(), myHeight));
  }

  @Override
  protected boolean doProcessKeyTyped(final KeyEvent keyEvent, final boolean allowErrors) {
    if (!isTextTypedEvent(keyEvent)) {
      return false;
    }

    int caretPosition = getCaretPosition();
    CellSide side;
    if (caretPosition == 0) {
      side = CellSide.LEFT;
    } else if (caretPosition == getRenderedText().length()) {
      side = CellSide.RIGHT;
    } else {
      side = null;
    }

    boolean result = false;
    String text = String.valueOf(keyEvent.getKeyChar());

    if (isEditable()) {
      ModelAccess modelAccess = getContext().getRepository().getModelAccess();
      buildActions(modelAccess);
      IntelligentCellProcessor cellProcessor =
          getEditorComponent().isAutomaticSubstitutionEnabled() ? IntelligentInputUtil.getIntelligentCellProcessor(this, getContext(), side) : null;
      ModifyTextCommand command = new ModifyTextCommand(keyEvent, text, allowErrors, side, getContext(), cellProcessor);
      modelAccess.executeCommand(command);
      getEditor().relayout();
      result = command.getResult();
    } else if (getEditorComponent().isAutomaticSubstitutionEnabled()) {
      if (side != null) {
        // TODO: we do this twice ... allowErrors = true/false
        String pattern = getUpdatedText(text);
        if (!pattern.equals(getRenderedText())) {
          result = IntelligentInputUtil.processCell(this, getContext(), pattern, side);
        }
      }
      if (allowErrors && !result && isTypeOverExistingText() && typeOverExistingText(keyEvent)) {
        return true;
      }
    }
    return result;
  }

  private void buildActions(ModelAccess modelAccess) {
    SubstituteInfo substituteInfo = getSubstituteInfo();
    EditorComponent editorComponent = getContext().getEditorComponent();
    if (substituteInfo != null && editorComponent instanceof jetbrains.mps.nodeEditor.EditorComponent) {
      TypecheckingSession typecheckingSession = ((jetbrains.mps.nodeEditor.EditorComponent) editorComponent).getTypecheckingSession();
      if (typecheckingSession != null) {
        modelAccess.runReadAction(() ->
                                      TypecheckingFacade.getFromContext()
                                                        .runWithSession(typecheckingSession,
                                                                        (session) -> substituteInfo.buildActions()));
      }
    }
  }

  private boolean isTypeOverExistingText() {
    return EditorSettings.getInstance().isTypeOverExistingText();
  }

  // https://youtrack.jetbrains.com/issue/MPS-31209
  private boolean typeOverExistingText(@NotNull KeyEvent keyEvent) {
    String cellText = getText();
    if (cellText.isEmpty() || isErrorState()) {
      return false;
    }
    if (!getEditorComponent().isAutomaticSubstitutionEnabled()) {
      return false;
    }

    EditorCell nextCell = CellTraversalUtil.getNextLeaf(this);
    ActionHandler actionHandler = getContext().getEditorComponent().getActionHandler();

    char typedChar = keyEvent.getKeyChar();
    int caretPosition = getCaretPosition();
    int textLength = cellText.length();
    if (caretPosition < textLength) {
      return cellText.charAt(caretPosition) == typedChar && actionHandler.executeAction(this, CellActionType.RIGHT);
    } else {
      if (Character.isWhitespace(typedChar) && !PunctuationUtil.hasPunctuationRight(this) && !PunctuationUtil.hasPunctuationLeft(nextCell)) {
        return actionHandler.executeAction(this, CellActionType.RIGHT);
      }
    }

    while ((nextCell instanceof EditorCell_Collection)) {
      EditorCell_Collection collection = (EditorCell_Collection) nextCell;
      if (collection.getCellsCount() == 0) {
        nextCell = CellTraversalUtil.getNextLeaf(nextCell);
      }
    }

    if (!(nextCell instanceof EditorCell_Label)) {
      return false;
    }
    EditorCell_Label nextLabel = (EditorCell_Label) nextCell;

    String nextText = nextLabel.getText();

    boolean res = false;
    if (nextText.length() > 0 && nextText.charAt(0) == typedChar) {
      if (nextLabel.isEditable() || nextLabel.isCaretPositionAllowed(0)) {
        actionHandler.executeAction(this, CellActionType.RIGHT);
        res = nextLabel.processKeyTyped(keyEvent, true);
      } else {
        res = actionHandler.executeAction(this, CellActionType.RIGHT);
      }
    }
    return res;
  }

  @Override
  public boolean processTextChanged(TextChangeEvent textChangeEvent) {
    if (!isEditable()) {
      return false;
    }

    if (myTextLine.hasNonTrivialSelection()) {
      myTextLine.resetSelection();
    }
    int selectionStart = Math.max(getCaretPosition() - textChangeEvent.getOffset(), 0);
    while (!isCaretPositionAllowed(selectionStart) && selectionStart < getCaretPosition()) {
      selectionStart++;
    }
    if (selectionStart < getCaretPosition()) {
      setCaretPosition(selectionStart, true);
    }

    ModifyTextCommand keyTypedCommand = new ModifyTextCommand(textChangeEvent.getText(), true, getContext());
    getContext().getRepository().getModelAccess().executeCommand(keyTypedCommand);
    if (keyTypedCommand.getResult()) {
      getEditor().relayout();
    }
    return keyTypedCommand.getResult();
  }

  private void addChangeTextUndoableAction() {
    final ModelAccess ma = getContext().getRepository().getModelAccess();
    if (ma instanceof Provider) {
      final ModelCommandContext cc = ((Provider) ma).getCommandContext(getContext().getModel());
      if (cc != null) {
        cc.registerActionWithUndo(new DummyUndoableAction(getSNode()));
      }
    }
  }

  @Override
  public boolean executeTextAction(CellActionType type, boolean allowErrors) {
    assert type == CellActionType.DELETE || type == CellActionType.BACKSPACE;
    if (!isEditable()) {
      return false;
    }
    // TODO: check if we need command here or we can execute command from UI action...
    ProcessTextActionCommand textAction = new ProcessTextActionCommand(getContext(), type, allowErrors);
    getContext().getRepository().getModelAccess().executeCommand(textAction);
    return textAction.getResult();
  }

  /**
   * @param replacingText the text, replacing selected fragment
   * @return the string contained in myTextLine, updated by replacing selected text fragment with the replacingText parameter
   */
  public String getUpdatedText(String replacingText) {
    String currentText = myTextLine.getText();
    int startSelection = myTextLine.getStartTextSelectionPosition();
    int endSelection = myTextLine.getEndTextSelectionPosition();
    return currentText.substring(0, startSelection) + replacingText + currentText.substring(endSelection);
  }

  private boolean canDeleteFrom(EditorCell cell) {
    if (getText().length() == 0) {
      return false;
    }
    if (!(cell instanceof EditorCell_Label)) {
      return false;
    }
    EditorCell_Label label = (EditorCell_Label) cell;
    return label.isEditable() && label.isSelectable();
  }

  private void deleteIfPossible(CellActionType actionType, boolean commit) {
    assert CellActionType.DELETE == actionType || CellActionType.BACKSPACE == actionType;
    if (commit && getText() != null && getText().isEmpty() && getStyle().get(StyleAttributes.AUTO_DELETABLE)) {
      // TODO: just use delete action (do not call getSNode().delete()) in the end if acton was not found or is not applicable
      getEditorComponent().getActionHandler().executeAction(this, actionType);
    }
  }

  @Override
  public String getSelectedText() {
    return myTextLine.getTextuallySelectedText();
  }

  @Override
  public int getSelectionStart() {
    return myTextLine.getStartTextSelectionPosition();
  }

  @Override
  public void setSelectionStart(int position) {
    myTextLine.setStartTextSelectionPosition(position);
  }

  @Override
  public int getSelectionEnd() {
    return myTextLine.getEndTextSelectionPosition();
  }

  @Override
  public void setSelectionEnd(int position) {
    myTextLine.setEndTextSelectionPosition(position);
  }

  public void deleteSelection() {
    String myText = myTextLine.getText();
    int stSel = myTextLine.getStartTextSelectionPosition();
    int endSel = myTextLine.getEndTextSelectionPosition();
    String newText = myText.substring(0, stSel) + myText.substring(endSel);

    if (getEditorComponent().isAutomaticSubstitutionEnabled()) {
      changeText(newText);
      addChangeTextUndoableAction();
    } else {
      changeTextInternal(newText);
    }
    myTextLine.setCaretPosition(stSel);
    fireSelectionChanged();
    ensureCaretVisible();
  }

  public void changeText(final String text) {
    changeTextInternal(text);
  }

  private void changeTextInternal(String text) {
    String oldText = getText();
    setText(text);
    updateVfsTimestamp(text, oldText);
  }

  private void updateVfsTimestamp(String text, String oldText) {
    if (Objects.equals(oldText, text) || isValidText(text)) {
      return;
    }
    if (CommandProcessor.getInstance().getCurrentCommand() == null) {
      return;
    }
    getEditorComponent().touch();
  }

  public void insertText(String text) {
    int startSelectionPosition = myTextLine.getStartTextSelectionPosition();
    int endSelectionPosition = myTextLine.getEndTextSelectionPosition();
    if (startSelectionPosition >= endSelectionPosition) {
      startSelectionPosition = myTextLine.getCaretPosition();
      endSelectionPosition = myTextLine.getCaretPosition();
    }
    String oldText = getText();
    changeText(oldText.substring(0, startSelectionPosition) + text + oldText.substring(endSelectionPosition));
    myTextLine.setCaretPosition(startSelectionPosition + text.length());
    addChangeTextUndoableAction();
  }

  public boolean isValidText(String text) {
    return true;
  }

  public void setUnderlined(boolean b) {
    getStyle().set(StyleAttributes.UNDERLINED, b);
  }

  @Override
  public int getAscent() {
    return getRenderedTextLine().getAscent();
  }

  @Override
  public int getDescent() {
    return getRenderedTextLine().getDescent();
  }

  @Override
  public NodeSubstitutePatternEditor createSubstitutePatternEditor() {
    return new NodeSubstitutePatternEditor(this);
  }

  public void selectWordOrAll() {
    if (selectionNotEmpty()) {
      selectAll();
      return;
    }

    int start = getPrevLocalHome(false);
    int end = getNextLocalEnd(false);

    if (start != end) {
      select(start, end);
    } else {
      selectAll();
    }

  }

  private boolean selectionNotEmpty() {
    TextLine textLine = getTextLine();
    return textLine.getStartTextSelectionPosition() != textLine.getEndTextSelectionPosition();
  }

  private void select(int start, int end) {
    assert start <= end;
    setSelectionStart(start);
    setSelectionEnd(end);
  }

  private int getNextLocalEnd(boolean skipLeadingSpaces) {
    int length = getText().length();
    assert getCaretPosition() <= length;
    for (int i = getCaretPosition(); i != length; ++i) {

      if (Character.isWhitespace(getText().charAt(i))) {
        if (skipLeadingSpaces) {
          if (i == length - 1 || !Character.isWhitespace(getText().charAt(i + 1))) {
            return i + 1;
          }
        } else {
          return i;
        }
      }
    }
    return length;
  }

  private int getPrevLocalHome(boolean skipLeadingSpaces) {
    assert getCaretPosition() >= 0;

    for (int i = getCaretPosition(); i >= 1; --i) {
      char c = getText().charAt(i - 1);
      if (Character.isWhitespace(c) && !skipLeadingSpaces) {
        return i;
      }

      if (!Character.isWhitespace(c)) {
        skipLeadingSpaces = false;
      }
    }
    return 0;
  }

  public void selectAll() {
    getTextLine().selectAll();
  }

  @Override
  public void deselectAll() {
    getTextLine().deselectAll();
  }

  public boolean isEverythingSelected() {
    return getTextLine().isEverythingSelected();
  }

  @Override
  public SubstituteInfo getSubstituteInfo() {
    SubstituteInfo substituteInfo = super.getSubstituteInfo();
    if (substituteInfo != null) {
      substituteInfo.setOriginalText(getText() == null || getText().isEmpty() ? getNullText() : getText());
    }
    return substituteInfo;
  }

  public String toString() {
    return NameUtil.shortNameFromLongName(getClass().getName()) + "[text=" + getText() + "]";
  }

  @Override
  public TextBuilder renderText() {
    return new TextBuilderImpl(getRenderedText());
  }

  @Override
  public HtmlTextBuilder renderHtml() {
    if (getRenderedText().isEmpty() || " ".equals(getRenderedText())) {
      return new HtmlTextBuilderImpl(getRenderedText());
    }
    return new HtmlTextBuilderImpl(getRenderedHtml());
  }

  public int getCharWidth() {
    return getRenderedTextLine().charWidth();
  }

  public String getTextBeforeCaret() {
    return myTextLine.getTextBeforeCaret();
  }

  public String getTextAfterCaret() {
    return myTextLine.getTextAfterCaret();
  }

  private void fireSelectionChanged() {
    getEditorComponent().getSelectionManager().setSelection(getEditorComponent().getSelectionManager().getSelection());
  }

  private boolean isTheOnlyCompletelySelectedLabelInBigCell() {
    EditorCell containingBigCell = CellTraversalUtil.getContainingBigCell(this);
    return CellTraversalUtil.getFirstLeaf(containingBigCell) == this && CellTraversalUtil.getLastLeaf(containingBigCell) == this &&
           getText().equals(getSelectedText());
  }

  private class MoveLeft extends AbstractCellAction {
    private boolean myWithSelection;

    private MoveLeft(boolean withSelection) {
      myWithSelection = withSelection;
    }

    @Override
    public boolean canExecute(EditorContext context) {
      return isCaretPositionAllowed(getCaretPosition() - 1);
    }

    @Override
    public void execute(EditorContext context) {
      int position;
      if (!myWithSelection && selectionNotEmpty() && isCaretPositionAllowed(getSelectionStart())) {
        position = getSelectionStart();
      } else {
        position = getCaretPosition() - 1;
      }
      setCaretPosition(position, myWithSelection);
      fireSelectionChanged();
      ensureCaretVisible();
    }
  }

  private class MoveRight extends AbstractCellAction {
    private boolean myWithSelection;

    private MoveRight(boolean withSelection) {
      myWithSelection = withSelection;
    }

    @Override
    public boolean canExecute(EditorContext context) {
      return isCaretPositionAllowed(getCaretPosition() + 1);
    }

    @Override
    public void execute(EditorContext context) {
      int position;
      if (!myWithSelection && selectionNotEmpty() && isCaretPositionAllowed(getSelectionEnd())) {
        position = getSelectionEnd();
      } else {
        position = getCaretPosition() + 1;
      }
      setCaretPosition(position, myWithSelection);
      fireSelectionChanged();
      ensureCaretVisible();
    }
  }

  private class SelectHome extends AbstractCellAction {
    @Override
    public boolean canExecute(EditorContext context) {
      return isCaretPositionAllowed(0);
    }

    @Override
    public void execute(EditorContext context) {
      setCaretPosition(0, true);
      fireSelectionChanged();
      ensureCaretVisible();
    }
  }

  private class SelectEnd extends AbstractCellAction {
    @Override
    public boolean canExecute(EditorContext context) {
      return isCaretPositionAllowed(getText().length());
    }

    @Override
    public void execute(EditorContext context) {
      setCaretPosition(getText().length(), true);
      fireSelectionChanged();
      ensureCaretVisible();
    }
  }

  private class CopyLabelText extends AbstractCellAction {
    @Override
    public boolean canExecute(EditorContext context) {
      SelectionManager selectionManager = context.getEditorComponent().getSelectionManager();
      if (selectionManager.getSelection() instanceof EditorCellLabelSelection) {
        EditorCellLabelSelection labelSelection = (EditorCellLabelSelection) selectionManager.getSelection();
        return labelSelection.getEditorCellLabel().getSelectedText().length() > 0;
      }
      return false;
    }

    @Override
    public void execute(EditorContext context) {
      // TODO: use EditorCell_Label.this. instead..
      EditorCell_Label label = (EditorCell_Label) context.getSelectedCell();
      if (label.isTheOnlyCompletelySelectedLabelInBigCell()) {
        CopyPasteUtil.copyTextAndNodeToClipboard(label.getSelectedText(), getSNode());
      } else {
        CopyPasteUtil.copyTextToClipboard(label.getSelectedText());
      }
    }
  }

  private class LocalHome extends AbstractCellAction {
    private boolean mySelect;

    private LocalHome(boolean select) {
      mySelect = select;
    }

    @Override
    public boolean canExecute(EditorContext context) {
      return !isFirstCaretPosition() && (StyleAttributesUtil.isFirstPositionAllowed(getStyle()) || getPrevLocalHome(true) != 0);
    }

    @Override
    public void execute(EditorContext context) {
      setCaretPosition(getPrevLocalHome(true), mySelect);
    }

  }

  private class LocalEnd extends AbstractCellAction {
    private boolean mySelect;

    private LocalEnd(boolean select) {
      mySelect = select;
    }

    @Override
    public boolean canExecute(EditorContext context) {
      return !isLastCaretPosition() && (StyleAttributesUtil.isLastPositionAllowed(getStyle()) || getNextLocalEnd(true) != getText().length());
    }

    @Override
    public void execute(EditorContext context) {
      setCaretPosition(getNextLocalEnd(true), mySelect);
    }
  }

  private class ClearSelection extends AbstractCellAction {

    @Override
    public boolean canExecute(EditorContext context) {
      return myTextLine.hasNonTrivialSelection();
    }

    @Override
    public void execute(EditorContext context) {
      myTextLine.resetSelection();
    }
  }

  private class PasteIntoLabelText extends AbstractCellAction {
    @Override
    public boolean canExecute(EditorContext context) {
      if (!(context.getSelectedCell() instanceof EditorCell_Label)) {
        return false;
      }
      // TODO: use EditorCell_Label.this. instead..
      EditorCell_Label label = (EditorCell_Label) context.getSelectedCell();
      SNode node = label.getSNode();

      // If selected cell is:
      // - the only completely selected label in big cell
      // - the cursor is in the beginning of this cell
      // - the cursor is in the end of this cell
      // then we paste text into the cell only if it is on top of clipboard (text was copied from another cell)
      // otherwise in this case this action will not be applicable, so node paste action should perform actual pasting
      //
      // if non of above is true then just pasting text from the clipboard into this cell (e.g. you can copy 1 + 2 and
      // paste it into the name label).
      return node != null && label.canPasteText() && label.isEditable() &&
             (label.isTheOnlyCompletelySelectedLabelInBigCell() || isFirstCaretPosition() && !getTextLine().hasNonTrivialSelection() ||
              isLastCaretPosition() && !getTextLine().hasNonTrivialSelection() ? CopyPasteUtil.isStringOnTopOfClipboard() :
              TextPasteUtil.hasStringInClipboard());
    }

    @Override
    public void execute(EditorContext context) {
      EditorCell_Label cell = (EditorCell_Label) context.getSelectedCell();
      final String s = TextPasteUtil.getStringFromClipboard();
      cell.insertText(NameUtil.escapeInvisibleCharacters(s));
      fireSelectionChanged();
      cell.ensureCaretVisible();
    }
  }

  private class CutLabelText extends AbstractCellAction {
    @Override
    public boolean canExecute(EditorContext context) {
      SelectionManager selectionManager = context.getEditorComponent().getSelectionManager();
      if (selectionManager.getSelection() instanceof EditorCellLabelSelection) {
        EditorCellLabelSelection labelSelection = (EditorCellLabelSelection) selectionManager.getSelection();
        return labelSelection.getEditorCellLabel().getSelectedText().length() > 0;
      }
      return false;
    }

    @Override
    public void execute(EditorContext context) {
      // TODO: use EditorCell_Label.this. instead..
      EditorCell_Label label = (EditorCell_Label) context.getSelectedCell();
      if (label.isTheOnlyCompletelySelectedLabelInBigCell()) {
        CopyPasteUtil.copyTextAndNodeToClipboard(label.getSelectedText(), getSNode());
      } else {
        CopyPasteUtil.copyTextToClipboard(label.getSelectedText());
      }
      if (label.canPasteText()) {
        label.deleteSelection();
      }
    }
  }

  private class ProcessTextActionCommand extends EditorComputable<Boolean> implements UndoRunnable {

    private final CellActionType myActionType;
    private final boolean myAllowErrors;

    ProcessTextActionCommand(EditorContext context, CellActionType type, boolean allowErrors) {
      super(context);
      myActionType = type;
      myAllowErrors = allowErrors;
    }

    @Override
    public Boolean doCompute() {
      String oldText = myTextLine.getText();
      int caretPosition = myTextLine.getCaretPosition();

      boolean commitChanges = getEditorComponent().isAutomaticSubstitutionEnabled();
      if (myTextLine.hasNonTrivialSelection()) {
        deleteSelection();
        deleteIfPossible(myActionType, commitChanges);
        return true;
      }

      if (myActionType == CellActionType.BACKSPACE) {
        return processBackspace(oldText, caretPosition, commitChanges);
      } else if (myActionType == CellActionType.DELETE) {
        return processDelete(oldText, caretPosition, commitChanges);
      }

      return false;
    }

    @NotNull
    private Boolean processDelete(String oldText, int caretPosition, boolean commitChanges) {
      if (caretPosition < oldText.length()) {
        String newText = oldText.substring(0, caretPosition) + oldText.substring(caretPosition + 1);
        if (!myAllowErrors && !isValidText(newText)) {
          return false;
        }
        if (commitChanges) {
          changeText(newText);
          addChangeTextUndoableAction();
        } else {
          changeTextInternal(newText);
        }
        ensureCaretVisible();
        deleteIfPossible(myActionType, commitChanges);
        return true;
      } else if (commitChanges) {
        EditorCell nextLeaf = CellTraversalUtil.getNextLeaf(EditorCell_Label.this);
        if (myAllowErrors && canDeleteFrom(nextLeaf)) {
          EditorCell_Label label = (EditorCell_Label) nextLeaf;
          getEditor().changeSelection(label);
          label.home();
          label.executeTextAction(myActionType, true);
          return true;
        }
      }
      return false;
    }

    @NotNull
    private Boolean processBackspace(String oldText, int caretPosition, boolean commitChanges) {
      if (caretPosition > 0) {
        String newText = oldText.substring(0, caretPosition - 1) + oldText.substring(caretPosition);
        if (!myAllowErrors && !isValidText(newText)) {
          return false;
        }
        if (commitChanges) {
          changeText(newText);
          addChangeTextUndoableAction();
        } else {
          changeTextInternal(newText);
        }
        if (!isCaretPositionAllowed(caretPosition - 1)) {
          return false;
        }
        setCaretPosition(caretPosition - 1);
        ensureCaretVisible();
        deleteIfPossible(myActionType, commitChanges);
        return true;
      } else if (commitChanges) {
        EditorCell prevLeaf = CellTraversalUtil.getPrevLeaf(EditorCell_Label.this);
        if (myAllowErrors && canDeleteFrom(prevLeaf)) {
          EditorCell_Label label = (EditorCell_Label) prevLeaf;
          getEditor().changeSelection(label);
          label.end();
          label.executeTextAction(myActionType, true);
          return true;
        }
      }
      return false;
    }

    @Nullable
    @Override
    public String getName() {
      return null;
    }

    @Nullable
    @Override
    public String getGroupId() {
      return getCommandGroupId();
    }

    @Override
    public boolean shallConfirmUndo() {
      return false;
    }
  }

  public class ModifyTextCommand extends EditorComputable<Boolean> implements UndoRunnable {
    private final String myReplacingText;
    private final boolean myAllowErrors;

    @Nullable
    private final CellSide mySide;
    @Nullable
    private final IntelligentCellProcessor myIntelligentCellProcessor;
    private KeyEvent myKeyEvent;

    private ModifyTextCommand(KeyEvent keyEvent, String replacingText, boolean allowErrors, @Nullable CellSide side, EditorContext context,
                              @Nullable IntelligentCellProcessor cellProcessor) {
      super(context);
      this.myKeyEvent = keyEvent;
      myReplacingText = replacingText;
      myAllowErrors = allowErrors;
      mySide = side;
      myIntelligentCellProcessor = cellProcessor;
    }

    private ModifyTextCommand(String replacingText, boolean allowErrors, EditorContext context) {
      this(null, replacingText, allowErrors, null, context, null);
    }

    @Override
    protected Boolean doCompute() {
      String newText = getUpdatedText(myReplacingText);
      boolean isValid = isValidText(newText);
      if (myAllowErrors || isValid) {
        getContext().flushEvents();
        addChangeTextUndoableAction();

        if (isValid) {
          commit(newText);
        } else {
          if (myIntelligentCellProcessor != null && myIntelligentCellProcessor.processCell(newText)) {
            /*
             * Resetting current command group ID if cell was side-transformed. In such situations
             * side-transforming command as well as char typing command should be separate part of
             * undo-redo process, not connected with eytyping events which are grouped together.
             */
            CommandProcessor.getInstance().setCurrentCommandGroupId(null);
          } else {
            if (isTypeOverExistingText() && myKeyEvent != null && typeOverExistingText(myKeyEvent)) {
              return true;
            }
            commit(newText);
          }
        }
        return true;
      }
      return isErrorState() && mySide == CellSide.LEFT && " ".equals(myReplacingText);
    }

    private void commit(String newText) {
      int startSelection = myTextLine.getStartTextSelectionPosition();
      if (getEditorComponent().isAutomaticSubstitutionEnabled()) {
        changeText(newText);
      } else {
        changeTextInternal(newText);
      }
      setCaretPositionIfPossible(startSelection + myReplacingText.length());
      myTextLine.resetSelection();
      fireSelectionChanged();
      ensureCaretVisible();
      activateCompletion();
    }

    @Nullable
    @Override
    public String getName() {
      return null;
    }

    @Nullable
    @Override
    public String getGroupId() {
      return getCommandGroupId();
    }

    @Override
    public boolean shallConfirmUndo() {
      return false;
    }
  }

  private void activateCompletion() {
    if (myTextLine.getText().isBlank() || !CompletionHelper.isAutoPopup()) {
      return;
    }
    jetbrains.mps.nodeEditor.EditorComponent editorComponent = (jetbrains.mps.nodeEditor.EditorComponent) getContext().getEditorComponent();
    editorComponent.activateNodeSubstituteChooser(this);
  }

  /**
   * This action can be used to introduce empty action into the stack of actions within UndoHelper
   * forcing it to add undoable command into IDEA undo stack: see {@link WorkbenchUndoHandler}
   * flushCommand() method implementation. This method will not add {@link SNodeIdeaUndoableAction}
   * action into IDEA undo stack if it has no own undoable actions.
   * <p/>
   * This is helpful in case of UI-only modifications performed upon the cells. For example, if the textual cell is modified
   * so it represents invalid value. In this case this value cannot be committed into the model and will stay in the editor
   * cell/memento objects only. Empty command in this case will add a "mark" in IDEA undo stack, so corresponding editor
   * memento will be restored on udo/redo of this empty command.
   */
  @SuppressWarnings("JavadocReference")
  protected static class DummyUndoableAction extends SNodeUndoableAction {
    protected DummyUndoableAction(SNode node) {
      super(node);
    }

    @Override
    public void undo() {
    }

    @Override
    public void redo() {
    }
  }
}
