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
package jetbrains.mps.nodeEditor.cellMenu;

import com.intellij.util.ui.UIUtil;
import jetbrains.mps.editor.runtime.style.StyleAttributes;
import jetbrains.mps.editor.runtime.style.StyleImpl;
import jetbrains.mps.nodeEditor.EditorComponent;
import jetbrains.mps.nodeEditor.MPSColors;
import jetbrains.mps.nodeEditor.cells.EditorCell_Label;
import jetbrains.mps.nodeEditor.cells.FontRegistry;
import jetbrains.mps.nodeEditor.cells.TextLine;
import jetbrains.mps.nodeEditor.keyboard.TextChangeEvent;
import jetbrains.mps.openapi.editor.EditorComponentSettings;
import jetbrains.mps.openapi.editor.EditorContext;
import jetbrains.mps.openapi.editor.cells.CellActionType;
import jetbrains.mps.openapi.editor.style.Style;
import jetbrains.mps.openapi.editor.style.StyleRegistry;
import org.jetbrains.annotations.NotNull;

import javax.swing.JPanel;
import javax.swing.JWindow;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.util.Objects;

/**
 * Author: Sergey Dmitriev.
 * Time: Oct 20, 2003 1:45:39 PM
 */
public class NodeSubstitutePatternEditor {
  private final EditorContext myContext;
  private final EditorCell_Label myCell;
  private TextLineOperations myTextLineOperations;
  private boolean myEditorActivated;

  private String myCachedText = "";
  private int myCachedCaretPosition;
  private int mySavedCaretPosition = 0;

  /**
   * Use {@link NodeSubstitutePatternEditor#NodeSubstitutePatternEditor(EditorContext)}
   */
  @Deprecated
  public NodeSubstitutePatternEditor() {
    myContext = null;
    myCell = null;
  }

  public NodeSubstitutePatternEditor(@NotNull EditorContext context) {
    // XXX in fact, in all but one scenario we've got EditorCell instance when constructing NSPE,
    //     and can derive EC from EditorCell.getContext()
    myContext = context;
    myCell = null;
  }

  public NodeSubstitutePatternEditor(@NotNull EditorCell_Label cell) {
    myContext = null;
    myCell = cell;
    TextLine textLine = myCell.getRenderedTextLine();
    myCachedText = textLine.getText();
    myCachedCaretPosition = textLine.getCaretPosition();
  }

  public void setText(String text) {
    if (myEditorActivated) {
      myTextLineOperations.setText(text);
      myTextLineOperations.relayout();
      myTextLineOperations.repaint();
    } else {
      myCachedText = text;
    }
  }

  public boolean isActivated() {
    return myEditorActivated;
  }

  public String getText() {
    if (myEditorActivated) {
      return myTextLineOperations.getText();
    }
    return myCachedText;
  }

  public void setCaretPosition(int caretPosition) {
    if (myEditorActivated) {
      myTextLineOperations.setCaretPosition(caretPosition);
      myTextLineOperations.repaint();
    } else {
      myCachedCaretPosition = caretPosition;
    }
  }

  public int getCaretPosition() {
    return myTextLineOperations.getCaretPosition();
  }

  public boolean processKeyPressed(KeyEvent keyEvent) {
    if (myEditorActivated) {
      if (keyEvent.getKeyCode() == KeyEvent.VK_SPACE && keyEvent.isControlDown()) {
        toggleReplaceMode();
        return true;
      }
      return myTextLineOperations.processKeyPressed(keyEvent);
    }
    return false;
  }

  public void toggleReplaceMode() {
    if (myEditorActivated) {
      if (mySavedCaretPosition != 0) {
        myTextLineOperations.setCaretPosition(mySavedCaretPosition);
        mySavedCaretPosition = 0;
      } else {
        mySavedCaretPosition = myTextLineOperations.getCaretPosition();
        myTextLineOperations.setCaretPosition(0);
      }
      myTextLineOperations.repaint();
    } else {
      if (mySavedCaretPosition != 0) {
        myCachedCaretPosition = mySavedCaretPosition;
        mySavedCaretPosition = 0;
      } else {
        mySavedCaretPosition = myCachedCaretPosition;
        myCachedCaretPosition = 0;
      }
    }
  }

  public boolean processKeyTyped(KeyEvent keyEvent) {
    if (myEditorActivated && myTextLineOperations.processKeyTyped(keyEvent)) {
      mySavedCaretPosition = 0;
      return true;
    }
    return false;
  }

  public boolean processTextChanged(TextChangeEvent textChangeEvent) {
    if (myEditorActivated) {
      myTextLineOperations.processTextChanged(textChangeEvent);
      mySavedCaretPosition = 0;
      return true;
    }
    return false;
  }

  @NotNull
  public String getPattern() {
    if (myEditorActivated) {
      int caretPosition = myTextLineOperations.getCaretPosition();
      String text = myTextLineOperations.getText();
      return text.substring(0, caretPosition);
    }

    if (myCachedText == null) {
      return "";
    }
    int caretPos = Math.min(myCachedText.length(), Math.max(myCachedCaretPosition, 0));
    return myCachedText.substring(0, caretPos);
  }

  // ------------------

  public void activate(Window owner, Point location, Dimension size, boolean show) {
    if (!myEditorActivated) {
      myEditorActivated = true;
      if (myCell == null) {
        EditorWindow editorWindow = new EditorWindow(owner, myContext);
        editorWindow.setFocusableWindowState(false);
        editorWindow.setLocation(location);
        editorWindow.setMinimalSize(size);
        editorWindow.setText(myCachedText);
        editorWindow.setCaretPosition(myCachedCaretPosition);
        if (show) {
          editorWindow.relayout();
          editorWindow.setVisible(true);
        }
        myTextLineOperations = editorWindow;
      } else {
        myTextLineOperations = new TextLineDelegate();
      }
    }
  }

  public void setLocation(Point point) {
    myTextLineOperations.setLocation(point);
  }

  public Point getLeftBottomPosition() {
    Point location = myTextLineOperations.getLocation();
    location.translate(0, myTextLineOperations.getHeight());
    return location;
  }

  public int getHeight() {
    return myTextLineOperations.getHeight();
  }

  public void commit() {
    myTextLineOperations.commit();
  }

  public void done() {
    if (myEditorActivated) {
      myTextLineOperations.dispose();
      myEditorActivated = false;
      mySavedCaretPosition = 0;
    }
  }

  public void selectionChanged() {
    myTextLineOperations.update();
  }

  Font getFont() {
    return myTextLineOperations.getFont();
  }

  private interface TextLineOperations {
    void setText(String text);
    void setCaretPosition(int caretPosition);
    void relayout();
    void repaint();
    int getCaretPosition();
    String getText();
    boolean processKeyPressed(KeyEvent keyEvent);
    boolean processKeyTyped(KeyEvent keyEvent);
    void processTextChanged(TextChangeEvent event);
    void dispose();
    void commit();
    void setLocation(Point point);
    int getHeight();
    Point getLocation();
    Font getFont();
    void execute(CellActionType type);
    void update();
  }

  private class TextLineDelegate implements TextLineOperations {
    private final EditorComponent editorComponent;
    private final String myOriginalText;
    private final int myOriginalCaretPosition;
    private String myText;
    private int myCaretPosition;
    private boolean myCommitted;

    TextLineDelegate() {
      myOriginalText = myText = myCell.getText();
      myOriginalCaretPosition = myCaretPosition = myCell.getCaretPosition();
      editorComponent = (EditorComponent) myCell.getEditorComponent();
    }

    @Override
    public void update() {
      myText = myCell.getText();
      myCaretPosition = myCell.getCaretPosition();
    }

    @Override
    public void execute(CellActionType type) {
      String originalText = myText;
      int originalCaret = myCaretPosition;
      myCell.executeTextAction(type, true);
      if (originalCaret != myCaretPosition || !Objects.equals(myText, originalText)) {
        return;
      }
      if (type == CellActionType.BACKSPACE) {
        myText = myText.substring(0, myCaretPosition - 1) + myText.substring(myCaretPosition);
        myCaretPosition--;
      } else {
        myText = myText.substring(0, myCaretPosition) + myText.substring(myCaretPosition + 1);
      }
    }

    @Override
    public Font getFont() {
      Font cellFont = myCell.getFont();
      return FontRegistry.getInstance().getFont(cellFont.getFamily(), 0, cellFont.getSize());
    }

    @Override
    public void setText(String text) {
      myText = text;
      myCell.setText(text);
      editorComponent.relayout();
    }

    @Override
    public void setCaretPosition(int caretPosition) {
      myCaretPosition = Math.min(caretPosition, myText.length());
      myCell.setCaretPositionIfPossible(myCaretPosition);
    }

    @Override
    public void relayout() {
      editorComponent.relayout();
    }

    @Override
    public void repaint() {
    }

    @Override
    public int getCaretPosition() {
      return myCaretPosition;
    }

    @Override
    public String getText() {
      return myText;
    }

    @Override
    public void commit() {
      myCommitted = true;
    }

    @Override
    public void dispose() {
      if (!myCommitted) {
        myCell.setText(myOriginalText);
        myCell.setCaretPosition(myOriginalCaretPosition);
      }
    }

    @Override
    public final void setLocation(Point point) {
    }

    @Override
    public int getHeight() {
      return myCell.getHeight();
    }

    @Override
    public Point getLocation() {
      Point anchor = editorComponent.getLocationOnScreen();
      return new Point(anchor.x + myCell.getX() + myCell.getLeftInset(), anchor.y + myCell.getY() + myCell.getTopInset());
    }

    @Override
    public boolean processKeyPressed(KeyEvent keyEvent) {
      if (keyEvent.isControlDown()) {
        return false;
      }

      if (keyEvent.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
        if (myCaretPosition > 0) {
          execute(CellActionType.BACKSPACE);
          return true;
        } else {
          return false;
        }
      }

      if (keyEvent.getKeyCode() == KeyEvent.VK_DELETE) {
        if (myCaretPosition < myText.length()) {
          execute(CellActionType.DELETE);
          return true;
        } else {
          return false;
        }
      }

      if (keyEvent.getKeyCode() == KeyEvent.VK_LEFT) {
        if (myCaretPosition > 0) {
          setCaretPosition(myCaretPosition - 1);
          return true;
        } else {
          return false;
        }
      }

      if (keyEvent.getKeyCode() == KeyEvent.VK_RIGHT) {
        if (myCaretPosition < myText.length()) {
          setCaretPosition(myCaretPosition + 1);
          return true;
        } else {
          return false;
        }
      }
      return false;
    }

    @Override
    public boolean processKeyTyped(KeyEvent keyEvent) {
      if (UIUtil.isReallyTypedEvent(keyEvent)) {
        myText = myText.substring(0, myCaretPosition) + keyEvent.getKeyChar() + myText.substring(myCaretPosition);
        myCaretPosition++;
      }
      return false;
    }

    @Override
    public void processTextChanged(TextChangeEvent textChangeEvent) {
      int keptTextEndIndex = Math.max(myCaretPosition - textChangeEvent.getOffset(), 0);
      setText(myText.substring(0, keptTextEndIndex) + textChangeEvent.getText());
      setCaretPosition(keptTextEndIndex + textChangeEvent.getText().length());
    }
  }

  private static class EditorWindow extends JWindow implements TextLineOperations {
    private final TextLine myTextLine;
    private Dimension myMinimalSize;

    EditorWindow(Window owner, EditorContext context) {
      super(owner);
      final EditorPanel p = new EditorPanel();
      if (context != null) {
        myTextLine = new TextLine("", context.getEditorComponent().getEditorComponentSettings());
        // XXX another TextLineOperations uses font of a cell, removing styles like bold/italic.
        //     Which approach is right? Can't we use editor's default font everywhere?
        setFont(context.getEditorComponent().getEditorComponentSettings().getDefaultFont());
        final Style cpStyle = context.getEditorComponent().getStyleRegistry().getStyle("COMPLETION_POPUP");
        // XXX I wonder if we can use Style to pass Font information, not to use EditorComponentSettings
        p.setBackground(cpStyle.get(StyleAttributes.TEXT_BACKGROUND_COLOR));
        p.setForeground(cpStyle.get(StyleAttributes.TEXT_COLOR));
      } else {
        // just a transition line for mbeddr using NodeSubstitutePatternEditor no-arg cons
        myTextLine = new TextLine("", new StyleImpl(), false);
        p.setBackground(MPSColors.YELLOW);
        p.setForeground(MPSColors.GRAY);
      }
      add(p);
    }

    @Override
    public void commit() {
    }

    @Override
    public void update() {
    }

    @Override
    public void execute(CellActionType type) {
      String oldText = myTextLine.getText();
      int caretPosition = myTextLine.getCaretPosition();
      if (type == CellActionType.BACKSPACE) {
        setText(oldText.substring(0, caretPosition - 1) + oldText.substring(caretPosition));
        setCaretPosition(caretPosition - 1);
      } else {
        setText(oldText.substring(0, caretPosition) + oldText.substring(caretPosition + 1));
      }
    }

    @Override
    public void setText(String text) {
      myTextLine.setText(text);
    }

    @Override
    public void setCaretPosition(int caretPosition) {
      myTextLine.setCaretPosition(caretPosition);
    }

    @Override
    public int getCaretPosition() {
      return myTextLine.getCaretPosition();
    }

    @Override
    public String getText() {
      return myTextLine.getText();
    }

    @Override
    public Font getFont() {
      // no true need to override, just to highlight the fact TextLineOperations.getFont() relies on JWindow.getFont()
      return super.getFont();
    }

    public void setMinimalSize(Dimension size) {
      myMinimalSize = size;
    }

    public void relayout() {
      myTextLine.relayout();
      int h = Math.max(myMinimalSize.height, myTextLine.getHeight());
      int w = Math.max(myMinimalSize.width, getWidth());
      w = Math.max(w, myTextLine.getWidth());
      setSize(w, h);
    }

    public boolean processKeyTyped(KeyEvent keyEvent) {
      if (processKeyTypedInternal(keyEvent)) {
        relayout();
        repaint();
        return true;
      }
      return false;
    }

    public void processTextChanged(TextChangeEvent textChangeEvent) {
      String oldText = myTextLine.getText();
      int keptTextEndIndex = Math.max(myTextLine.getCaretPosition() - textChangeEvent.getOffset(), 0);
      changeText(oldText.substring(0, keptTextEndIndex) + textChangeEvent.getText());
      myTextLine.setCaretPosition(keptTextEndIndex + textChangeEvent.getText().length());
      relayout();
      repaint();
    }

    private boolean processKeyTypedInternal(KeyEvent keyEvent) {
      String oldText = myTextLine.getText();
      int caretPosition = myTextLine.getCaretPosition();

      char keyChar = keyEvent.getKeyChar();
      if (UIUtil.isReallyTypedEvent(keyEvent)) {
        changeText(oldText.substring(0, caretPosition) + keyChar/* + myText.substring(caretPosition)*/);
        myTextLine.setCaretPosition(caretPosition + 1);
        return true;
      }

      return false;
    }

    public boolean processKeyPressed(KeyEvent keyEvent) {
      if (processKeyPressedInternal(keyEvent)) {
        relayout();
        repaint();
        return true;
      }
      return false;
    }

    private boolean processKeyPressedInternal(KeyEvent keyEvent) {
      if (keyEvent.isControlDown()) {
        return false;
      }

      String oldText = myTextLine.getText();
      int caretPosition = myTextLine.getCaretPosition();
      if (keyEvent.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
        if (caretPosition > 0) {
          changeText(oldText.substring(0, caretPosition - 1) + oldText.substring(caretPosition));
          myTextLine.setCaretPosition(caretPosition - 1);
          return true;
        } else {
          return false;
        }
      }

      if (keyEvent.getKeyCode() == KeyEvent.VK_DELETE) {
        if (caretPosition < oldText.length()) {
          changeText(oldText.substring(0, caretPosition) + oldText.substring(caretPosition + 1));
          return true;
        } else {
          return false;
        }
      }

      if (keyEvent.getKeyCode() == KeyEvent.VK_LEFT) {
        if (caretPosition > 0) {
          myTextLine.setCaretPosition(caretPosition - 1);
          return true;
        } else {
          return false;
        }
      }

      if (keyEvent.getKeyCode() == KeyEvent.VK_RIGHT) {
        if (caretPosition < oldText.length()) {
          myTextLine.setCaretPosition(caretPosition + 1);
          return true;
        } else {
          return false;
        }
      }

      return false;
    }

    protected void changeText(String text) {
      myTextLine.setText(text);
    }

    private class EditorPanel extends JPanel {
      private final javax.swing.BrowserPaintSurface myBrowserPaintSurface =
          new javax.swing.BrowserPaintSurface(this, this::paint);

      @Override
      public void addNotify() {
        super.addNotify();
        myBrowserPaintSurface.mount();
      }

      @Override
      public void removeNotify() {
        myBrowserPaintSurface.unmount();
        super.removeNotify();
      }

      @Override
      public void setBounds(int x, int y, int width, int height) {
        super.setBounds(x, y, width, height);
        if (myBrowserPaintSurface != null) myBrowserPaintSurface.resize();
      }

      @Override
      public Graphics getGraphics() {
        return myBrowserPaintSurface == null ? null : myBrowserPaintSurface.getGraphics();
      }

      @Override
      protected void paintComponent(Graphics g) {
        // COLORS: move colors to properties
        Rectangle bounds = g.getClipBounds();
        g.setColor(getBackground());
        g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
        g.setColor(getForeground());
        g.drawRect(bounds.x, bounds.y, bounds.width - 1, bounds.height - 1);
        EditorComponent.turnOnAliasingIfPossible((Graphics2D) g);

        TextLine textLine = myTextLine; // oh, no! EditorPanel is just a "painting component" for TextLine?
        textLine.setSelected(false);
        textLine.setShowCaret(true);
        textLine.paint(g, 0, 0);
      }
    }
  } // private class EditorWindow
}
