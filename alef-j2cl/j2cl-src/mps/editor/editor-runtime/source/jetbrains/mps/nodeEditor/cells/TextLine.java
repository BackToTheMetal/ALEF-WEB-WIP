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

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.ui.ColorUtil;
import com.intellij.ui.DarculaColors;
import com.intellij.ui.JBColor;
import jetbrains.mps.editor.runtime.style.Measure;
import jetbrains.mps.editor.runtime.style.Padding;
import jetbrains.mps.editor.runtime.style.StyleAttributes;
import jetbrains.mps.editor.runtime.style.StyleImpl;
import jetbrains.mps.nodeEditor.EditorComponentSettingsImpl;
import jetbrains.mps.nodeEditor.EditorSettings;
import jetbrains.mps.openapi.editor.EditorComponentSettings;
import jetbrains.mps.openapi.editor.cells.EditorFontMetrics;
import jetbrains.mps.openapi.editor.style.Style;
import jetbrains.mps.openapi.editor.style.StyleAttribute;
import jetbrains.mps.openapi.editor.style.StyleRegistry;
import org.jetbrains.annotations.NotNull;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.font.TextAttribute;
import java.text.AttributedCharacterIterator.Attribute;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class TextLine {
  // TODO COLORS: Remove hardcoded color
  private static final Color SELECTED_OR_BACKGROUND_ERROR_COLOR =
      new JBColor(new Color(255, 220, 220, 90), new Color(0xd6, 0x4d, 0x5b, 50));
  private static final Color ERROR_FOREGROUND_COLOR = new JBColor(new Color(168, 30, 30, 190), DarculaColors.RED);

  private String myText;
  private int myDescent = 0;

  private Font myFont = EditorSettings.getInstance().getDefaultEditorFont();
  private final EditorComponentSettings myEditorComponentSettings;
  private EditorFontMetrics myFontMetrics;

  private int myCaretPosition = 0;
  private int myCaretX = -1;
  private int mySelectionStartX = -1;
  private int mySelectionEndX = -1;
  private int myTextEndX = -1;
  private int myMinWidth = -1;
  private int myStartTextSelectionPosition = 0;
  private int myEndTextSelectionPosition = 0;

  private int myWidth = 0;
  private int myHeight = 0;
  private int myTextHeight = 0;
  private boolean myCaretEnabled = true;
  private int myMinimalLength = 0;

  private final double myLineSpacing = EditorSettings.getInstance().getLineSpacing();
  // FIXME use "COMPLETION_POPUP" (or dedicated) style instead of EditorSettings service
  private Color mySelectedTextColor = EditorSettings.getInstance().getSelectionForegroundColor();
  private Color myTextSelectedTextColor = EditorSettings.getInstance().getSelectionForegroundColor();
  private Color myTextSelectedBackgroundColor = EditorSettings.getInstance().getSelectionBackgroundColor();
  private int[] myTextHighlights;
  private Color myHighlightForeground;
  private Color myHighlightBackground;

  /** Optional presentation-only ranges; model contents and cell geometry stay native. */
  public void setTextHighlights(int[] ranges, Color foreground, Color background) {
    myTextHighlights = ranges;
    myHighlightForeground = foreground;
    myHighlightBackground = background;
  }

  public void setTextSelectionColors(Color foreground, Color background) {
    myTextSelectedTextColor = foreground;
    myTextSelectedBackgroundColor = background;
  }

  private void paintTextHighlights(Graphics graphics, int shiftX, int shiftY) {
    if (myTextHighlights == null) return;
    for (int i = 0; i < myTextHighlights.length; i += 2) {
      int start = getTextWidth(myTextHighlights[i]);
      int end = getTextWidth(myTextHighlights[i + 1]);
      int x = shiftX + getPaddingLeft() + start;
      int y = shiftY + getPaddingTop();
      Graphics g = graphics.create();
      try {
        g.clipRect(x, y, Math.max(2, end - start), myTextHeight);
        g.setColor(myHighlightBackground);
        g.fillRect(x, y, Math.max(2, end - start), myTextHeight);
        g.setFont(getFont());
        g.setColor(myHighlightForeground);
        int baseline = shiftY + myHeight - myDescent - getPaddingBottom();
        // Draw the whole shaped run through the clip, preserving kerning/ligatures.
        g.drawString(myText, shiftX + getPaddingLeft() + myFontCorrectionTextShift, baseline);
        if (isUnderlined()) g.drawLine(x, baseline + 1, x + end - start, baseline + 1);
      } finally { g.dispose(); }
    }
  }


  private boolean myShowsErrorColor = false;

  private boolean myNull;
  private Style myStyle;

  private int myPaddingLeft;
  private int myPaddingRight;
  private int myPaddingTop;
  private int myPaddingBottom;

  private boolean myControlOvered;
  private boolean myStrikeOut;
  private boolean myUnderlined;

  private Color myTextColor;
  private Color myNullTextColor;
  private Color myTextBackground;
  private Color myNullTextBackground;
  private Color mySelectedTextBackground;
  private Color myNulLSelectedTextBackground;

  private boolean myShowCaret;
  private boolean mySelected;
  private boolean myInitialized;
  private int myFontCorrectionRightGap;
  private int myFontCorrectionTextShift;

  public TextLine(String text, EditorComponentSettings editorComponentSettings) {
    this(text, new StyleImpl(), false, editorComponentSettings);
  }

  /**
   * Used by mps extensions.
   * @deprecated use {@link #TextLine(String, Style, boolean, EditorComponentSettings)} instead
   */
  @Deprecated
  public TextLine(String text, @NotNull Style style, boolean isNull) {
    this(text, style, isNull, EditorComponentSettingsImpl.DEFAULT_SETTINGS);
  }

  public TextLine(String text, @NotNull Style style, boolean isNull, EditorComponentSettings editorComponentSettings) {
    setText(text);
    myNull = isNull;
    myStyle = style;
    showTextColor();
    myEditorComponentSettings = editorComponentSettings;
  }

  public String getText() {
    return myText;
  }

  public void setText(String text) {
    if (text == null) {
      text = "";
    }

    if (text.equals(myText)) {
      return;
    }
    doSetText(text);
    doSetCaretPosition(Math.min(myText.length(), getCaretPosition()));

    setStartTextSelectionPosition(getCaretPosition());
    setEndTextSelectionPosition(getCaretPosition());
  }

  private void doSetText(String text) {
    myText = text;
    myCaretX = -1;
    mySelectionStartX = -1;
    mySelectionEndX = -1;
    myTextEndX = -1;
  }

  public String getTextBeforeCaret() {
    return myText.substring(0, getCaretPosition());
  }

  public String getTextAfterCaret() {
    return myText.substring(getCaretPosition(), myText.length());
  }

  public int getWidth() {
    return myWidth;
  }

  public int getHeight() {
    return myHeight;
  }

  public int getStartTextSelectionPosition() {
    return myStartTextSelectionPosition;
  }

  public int getEndTextSelectionPosition() {
    return myEndTextSelectionPosition;
  }

  /**
   * @param length Minimal size of the text edit field in chars.
   */
  public void setMinimalLength(int length) {
    myMinimalLength = length;
    myMinWidth = -1;
  }

  public void updateStyle(Set<StyleAttribute> attributes) {
    myStrikeOut = myStyle.get(StyleAttributes.STRIKE_OUT);
    myUnderlined = myStyle.get(StyleAttributes.UNDERLINED);

    if (attributes == null
        || attributes.contains(StyleAttributes.FONT_SIZE)
        || attributes.contains(StyleAttributes.FONT_STYLE)
        || attributes.contains(StyleAttributes.FONT_FAMILY)) {
      //this is the most expensive calculation
      EditorSettings settings = EditorSettings.getInstance();
      Integer styleFontSize = myStyle.get(StyleAttributes.FONT_SIZE);
      String styleFontFamily = myStyle.get(StyleAttributes.FONT_FAMILY);
      if (styleFontFamily != null && !FontRegistry.getInstance().getAvailableFontFamilyNames().contains(styleFontFamily)) {
        FontRegistry.getInstance().reportUnknownFontFamily(styleFontFamily);
        styleFontFamily = null;
      }

      Integer style = myStyle.get(StyleAttributes.FONT_STYLE);
      String family = styleFontFamily != null ? styleFontFamily : settings.getFontFamily();
      int fontSize = styleFontSize != null ? styleFontSize : settings.getFontSize();
      fontSize = myEditorComponentSettings.getFontSizeScaled(fontSize);

      final Font font = FontRegistry.getInstance().getFont(family, style, fontSize);

      Map<Attribute, Object> fontAttributes = new HashMap<>();
      if (ApplicationManager.getApplication() != null && EditorColorsManager.getInstance().getGlobalScheme().getFontPreferences().useLigatures()) {
        fontAttributes.put(TextAttribute.LIGATURES, TextAttribute.LIGATURES_ON);
      }
      if (myStrikeOut) {
        fontAttributes.put(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON);
      }
      myFont = fontAttributes.isEmpty() ? font : font.deriveFont(fontAttributes);
      myFontMetrics = myEditorComponentSettings.getFontMetrics(family, style, fontSize);
      myFontCorrectionRightGap = FontRegistry.getInstance().isFakeItalic(family, style) ? 1 : 0;
      myFontCorrectionTextShift = (style & Font.ITALIC) > 0 ? -1 : 0;
    }

    myPaddingLeft = getHorizontalInternalInset(myStyle.get(StyleAttributes.PADDING_LEFT));
    myPaddingRight = getHorizontalInternalInset(myStyle.get(StyleAttributes.PADDING_RIGHT));
    myPaddingTop = getVerticalInternalInset(myStyle.get(StyleAttributes.PADDING_TOP));
    myPaddingBottom = getVerticalInternalInset(myStyle.get(StyleAttributes.PADDING_BOTTOM));

    myControlOvered = myStyle.get(StyleAttributes.CONTROL_OVERED_REFERENCE);

    myTextColor = myStyle.get(StyleAttributes.TEXT_COLOR);
    myNullTextColor = myStyle.get(StyleAttributes.NULL_TEXT_COLOR);
    myTextBackground = myStyle.get(StyleAttributes.TEXT_BACKGROUND_COLOR);
    myNullTextBackground = myStyle.get(StyleAttributes.NULL_TEXT_BACKGROUND_COLOR);
    mySelectedTextBackground = myStyle.get(StyleAttributes.SELECTED_TEXT_BACKGROUND_COLOR);
    myNulLSelectedTextBackground = myStyle.get(StyleAttributes.NULL_SELECTED_TEXT_BACKGROUND_COLOR);
  }

  private void init() {
    if (myInitialized) {
      return;
    }
    myInitialized = true;
    updateStyle(null);
    myStyle.addListener(e -> {
      Set<StyleAttribute> changedAttributes = e.getChangedAttributes();
      updateStyle(changedAttributes);
    });

  }

  public void relayout() {
    EditorFontMetrics metrics = getEditorFontMetrics();
    myHeight = (int) (metrics.getHeight() * myLineSpacing + getPaddingTop() + getPaddingBottom());
    myTextHeight = (int) (metrics.getHeight() * myLineSpacing);
    int minWidth = calculateMinWidth();
    int width = metrics.getWidth(myText) + myFontCorrectionRightGap + getPaddingLeft() + getPaddingRight();
    myWidth = Math.max(minWidth, width);
    myDescent = metrics.getDescent();
  }

  int getEffectiveWidth() {
    int minWidth = calculateMinWidth();
    int effectiveWidth = myWidth - getPaddingLeft() - getPaddingRight();
    return Math.max(minWidth, effectiveWidth);
  }

  private int calculateMinWidth() {
    if (myMinWidth == -1) {
      myMinWidth = Math.max(myMinimalLength * charWidth(), myStyle.get(StyleAttributes.SELECTABLE) ? 2 : 0);
    }
    return myMinWidth;
  }

  private int getHorizontalInternalInset(Padding p) {
    double value = p.getValue();
    Measure type = p.getType();

    if (type == null) {
      type = Measure.SPACES;
    }

    if (type == Measure.SPACES) {
      return (int) (charWidth() * value);
    }
    if (type == Measure.PIXELS) {
      return (int) value;
    }
    return 0;
  }

  private int getVerticalInternalInset(Padding p) {
    double value = p.getValue();
    Measure type = p.getType();

    if (type == null) {
      type = Measure.SPACES;
    }

    if (type == Measure.SPACES) {
      return (int) (charHeight() * value);
    }
    if (type == Measure.PIXELS) {
      return (int) value;
    }
    return 0;
  }

  public int getPaddingLeft() {
    init();
    return myPaddingLeft;
  }

  public int getPaddingRight() {
    init();
    return myPaddingRight;
  }

  public int getPaddingTop() {
    init();
    return myPaddingTop;
  }

  public int getPaddingBottom() {
    init();
    return myPaddingBottom;
  }

  public int charWidth() {
    return getEditorFontMetrics().getWidth("w");
  }

  public int charHeight() {
    return getEditorFontMetrics().getHeight();
  }

  public boolean isCaretEnabled() {
    return myCaretEnabled;
  }

  public void setCaretEnabled(boolean caretEnabled) {
    myCaretEnabled = caretEnabled;
  }

  public void home() {
    setCaretPosition(0);
  }

  public void end() {
    setCaretPosition(getText().length());
  }

  public void showErrorColor() {
    myShowsErrorColor = true;
  }

  public void showTextColor() {
    myShowsErrorColor = false;
  }

  public Color getTextColor() {
    init();
    if (myControlOvered) {
      return EditorSettings.getInstance().getHyperlinkColor();
    }

    if (!myNull && myTextColor != null) {
      return myTextColor;
    } else {
      return myNullTextColor;
    }
  }

  public void setSelectedTextColor(Color selectedTextColor) {
    mySelectedTextColor = selectedTextColor;
  }

  public Font getFont() {
    init();
    return myFont;
  }

  public boolean isSelected() {
    return mySelected;
  }

  public void setSelected(boolean isSelected) {
    mySelected = isSelected;
  }

  public boolean isShowCaret() {
    return myShowCaret;
  }

  public void setShowCaret(boolean showCaret) {
    myShowCaret = showCaret;
  }

  public void paint(Graphics g, int shiftX, int shiftY) {
    paint(g, shiftX, shiftY, null);
  }

  public void paint(Graphics g, int shiftX, int shiftY, Color forcedTextColor) {
    Color textColor;
    Color textBackgroundColor;

    if (forcedTextColor != null) {
      textColor = forcedTextColor;
      textBackgroundColor = null;
    } else {
      if (mySelected) {
        if (myShowsErrorColor) {
          textColor =  ERROR_FOREGROUND_COLOR;
        } else {
          textColor =  mySelectedTextColor != null ? mySelectedTextColor : getTextColor();
        }
        if (myNull) {
          textBackgroundColor = myNulLSelectedTextBackground;
        } else {
          textBackgroundColor = mySelectedTextBackground;
        }
      } else {
        if (myShowsErrorColor) {
          textColor = ERROR_FOREGROUND_COLOR;
          textBackgroundColor = SELECTED_OR_BACKGROUND_ERROR_COLOR;
        } else {
          textColor = getTextColor();
          if (myNull) {
            textBackgroundColor =  myNullTextBackground;
          } else {
            textBackgroundColor =  myTextBackground;
          }
        }
      }
    }

    if (textBackgroundColor != null) {
      g.setColor(textBackgroundColor);
      g.fillRect(shiftX + getPaddingLeft(),
                 shiftY + getPaddingTop(),
                 getEffectiveWidth(),
                 myTextHeight);
    }

    g.setFont(getFont());
    if (!g.getColor().equals(textColor)) {
      g.setColor(textColor);
    }

    int selectionStartX = shiftX + getPaddingLeft() + getSelectionStartX();
    int selectionEndX = shiftX + getPaddingLeft() + getSelectionEndX();
    int endLineX = shiftX + getPaddingLeft() + getTextEndX();
    int baselineY = shiftY + myHeight - myDescent - getPaddingBottom();
    int centerLineY = shiftY + (myHeight - getPaddingBottom() + getPaddingTop()) / 2;

    if (getStartTextSelectionPosition() > 0) {
      g.drawString(myText.substring(0, getStartTextSelectionPosition()), shiftX + getPaddingLeft() + myFontCorrectionTextShift, baselineY);
      if (isUnderlined()) {
        g.drawLine(shiftX + getPaddingLeft(), baselineY + 1, selectionStartX, baselineY + 1);
      }
    }

    if (getEndTextSelectionPosition() <= myText.length()) {
      g.drawString(myText.substring(getEndTextSelectionPosition()), selectionEndX + myFontCorrectionTextShift, baselineY);
      if (isUnderlined()) {
        g.drawLine(selectionEndX, baselineY + 1, endLineX, baselineY + 1);
      }
    }

    if (getStartTextSelectionPosition() < getEndTextSelectionPosition()) {
      //drawing textual selection
      String selectedText = getTextuallySelectedText();
      g.setColor(myTextSelectedBackgroundColor);
      // Filling smaller rectangle to not cover frames created by other messages
      if (selectionEndX - selectionStartX - 2 + myFontCorrectionRightGap > 0) {
        g.fillRect(selectionStartX + 1, shiftY + getPaddingTop() + 1,
                   selectionEndX - selectionStartX - 2 + myFontCorrectionRightGap, myTextHeight - 2);
      }

      g.setColor(myTextSelectedTextColor != null ? myTextSelectedTextColor : getTextColor());
      g.drawString(selectedText, selectionStartX + myFontCorrectionTextShift, baselineY);
      if (isUnderlined()) {
        g.drawLine(selectionStartX, baselineY + 1, selectionEndX, baselineY + 1);
      }

      g.setColor(textColor);
    }

    paintTextHighlights(g, shiftX, shiftY);
    if (myShowCaret) {
      drawCaret(g, shiftX, shiftY);
    }
  }

  private void drawCaret(Graphics g, int shiftX, int shiftY) {
    if (!myCaretEnabled) {
      return;
    }
    int x = getCaretX(shiftX);
    if (getCaretPosition() != 0) {
      x--;
    }
    g.setColor(EditorSettings.getInstance().getCaretColor());
    g.fillRect(x, shiftY + getPaddingTop(), 2, myTextHeight);
  }

  public void repaintCaret(Component component, int shiftX, int shiftY) {
    int x = getCaretX(shiftX);
    component.repaint(x - 1, shiftY + getPaddingTop() - 1, x + 2, myTextHeight + 2);
  }

  public int getCaretX(int shiftX) {
    if (myCaretX == -1) {
      myCaretX = getTextWidth(getCaretPosition());
    }
    return shiftX + getPaddingLeft() + myCaretX;
  }

  private int getSelectionStartX() {
    if (mySelectionStartX == -1) {
      mySelectionStartX = getTextWidth(getStartTextSelectionPosition());
    }
    return mySelectionStartX;
  }

  private int getSelectionEndX() {
    if (mySelectionEndX == -1) {
      mySelectionEndX = getTextWidth(getEndTextSelectionPosition());
    }
    return mySelectionEndX;
  }

  private int getTextEndX() {
    if (myTextEndX == -1) {
      myTextEndX = getTextWidth(getText().length());
    }
    return myTextEndX;
  }

  private int getTextWidth(int caretPosition) {
    return getEditorFontMetrics().getWidth(myText, 0, caretPosition);
  }

  public EditorFontMetrics getEditorFontMetrics() {
    init();
    return myFontMetrics;
  }

  /**
   * @deprecated use {@link #getEditorFontMetrics()} instead
   */
  @Deprecated
  public FontMetrics getFontMetrics() {
    return ((EditorFontMetricsImpl) getEditorFontMetrics()).getFontMetrics();
  }

  public String getTextuallySelectedText() {
    if (getStartTextSelectionPosition() > getEndTextSelectionPosition()) {
      return "";
    }
    return myText.substring(getStartTextSelectionPosition(), getEndTextSelectionPosition());
  }


  public void resetSelection() {
    setStartTextSelectionPosition(getCaretPosition());
    setEndTextSelectionPosition(getCaretPosition());
  }

  public boolean hasNonTrivialSelection() {
    return (getStartTextSelectionPosition() != getCaretPosition() || getEndTextSelectionPosition() != getCaretPosition());
  }

  public void setStartTextSelectionPosition(int i) {
    assert i >= 0;
    this.myStartTextSelectionPosition = Math.min(i, myText.length());
    mySelectionStartX = -1;
  }

  public void setEndTextSelectionPosition(int i) {
    assert i >= 0;
    this.myEndTextSelectionPosition = Math.min(i, myText.length());
    mySelectionEndX = -1;
  }

  public void selectAll() {
    setStartTextSelectionPosition(0);
    setEndTextSelectionPosition(getText().length());
  }

  public void deselectAll() {
    setStartTextSelectionPosition(getCaretPosition());
    setEndTextSelectionPosition(getCaretPosition());
  }

  public boolean isEverythingSelected() {
    return getStartTextSelectionPosition() == 0 && getEndTextSelectionPosition() == getText().length();
  }

  public void setCaretByXCoord(int x) {
    setCaretPosition(getCaretPositionByXCoord(x));
  }

  public int getCaretPositionByXCoord(int _x) {
    int x = _x - getPaddingLeft();
    EditorFontMetrics metrics = getEditorFontMetrics();
    int caretPosition = myText.length();
    int len = 0;
    for (int i = 0; i < myText.length(); i++) {
      int newLen = metrics.getWidth(myText, 0, i + 1);
      if (x <= (len + newLen + 1) / 2) {
        caretPosition = i;
        break;
      }
      len = newLen;
    }
    return caretPosition;
  }

  public int getCaretPosition() {
    return myCaretPosition;
  }

  public void setCaretPosition(int i) {
    setCaretPosition(i, false);
  }

  private void doSetCaretPosition(int position) {
    myCaretPosition = position;
    myCaretX = -1;
  }

  public void setCaretPosition(int position, boolean duringSelection) {
    assert position >= 0;
    if (!duringSelection) {
      doSetCaretPosition(Math.min(myText.length(), position));
      setStartTextSelectionPosition(getCaretPosition());
      setEndTextSelectionPosition(getCaretPosition());
      return;
    }

    int old = getCaretPosition();
    doSetCaretPosition(Math.min(myText.length(), position));

    if (getEndTextSelectionPosition() == old) {
      setEndTextSelectionPosition(getCaretPosition());
    } else {
      setStartTextSelectionPosition(getCaretPosition());
    }

    if (getEndTextSelectionPosition() < getStartTextSelectionPosition()) {
      int temp = getEndTextSelectionPosition();
      setEndTextSelectionPosition(getStartTextSelectionPosition());
      setStartTextSelectionPosition(temp);
    }
  }

  public boolean isUnderlined() {
    init();
    if (myControlOvered) {
      return true;
    }

    return myUnderlined;
  }

  public boolean isStrikeOut() {
    init();
    return myStrikeOut;
  }

  public int getAscent() {
    return myTextHeight - myDescent;
  }

  public int getDescent() {
    return myDescent;
  }
}
