/*
 * Copyright 2003-2019 JetBrains s.r.o.
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

import com.intellij.openapi.editor.impl.FontInfo;
import jetbrains.mps.openapi.editor.cells.EditorFontMetrics;
import jetbrains.mps.openapi.editor.cells.EditorFontMetricsProvider;

import javax.swing.JComponent;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.font.FontRenderContext;
import java.util.stream.IntStream;

public class EditorFontMetricsImpl implements EditorFontMetrics {
  private static final FontRenderContext DEFAULT_FRC = new FontRenderContext(null, true, true);
  public static EditorFontMetricsProvider DEFAULT_FONT_METRICS_PROVIDER = EditorFontMetricsImpl::new;

  private java.awt.FontMetrics myFontMetrics;
  private final String myFamily;
  private final int myStyle;
  private final int mySize;
  private JComponent myComponent;

  private EditorFontMetricsImpl(String fontFamily, int fontStyle, int fontSize) {
    myFamily = fontFamily;
    myStyle = fontStyle;
    mySize = fontSize;
  }

  public EditorFontMetricsImpl(String fontFamily, int fontStyle, int fontSize, JComponent component) {
    this(fontFamily, fontStyle, fontSize);
    myComponent = component;
  }

  public int getHeight() {
    return getFontMetrics().getHeight();
  }

  public int getWidth(String text) {
    return getWidth(text, 0, text.length());
  }

  @Override
  public int getWidth(char ch, int count) {
    StringBuilder sb = new StringBuilder();
    IntStream.range(0, count).forEach((i) -> sb.append(" "));
    return getWidth(sb.toString());
  }

  public int getWidth(String text, int beginIndex, int endIndex) {
    // Canvas paints complete text runs. Summing integer charWidth values loses
    // fractional advances on every character and lets long labels overlap.
    // Measure the same run (including kerning) and round only its final width up.
    return (int) Math.ceil(getFont().getStringBounds(text, beginIndex, endIndex, DEFAULT_FRC).getWidth());
  }

  public int getDescent() {
    return getFontMetrics().getDescent();
  }

  public FontMetrics getFontMetrics() {
    if (myFontMetrics == null) {
      myFontMetrics = createFontMetrics();
    }
    return myFontMetrics;
  }

  protected Font getFont() {
    return FontRegistry.getInstance().getFont(myFamily, myStyle, mySize);
  }

  private FontMetrics createFontMetrics() {
    return myComponent != null ? myComponent.getFontMetrics(getFont()) : FontInfo.getFontMetrics(getFont(), DEFAULT_FRC);
  }
}
