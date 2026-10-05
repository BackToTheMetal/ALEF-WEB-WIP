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

import com.intellij.openapi.util.TextRange;
import com.intellij.ui.SimpleColoredComponent;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.ui.speedSearch.SpeedSearchUtil;
import com.intellij.util.ui.UIUtil;
import jetbrains.mps.ide.icons.GlobalIconManager;
import jetbrains.mps.ide.icons.IdeIcons;
import jetbrains.mps.logging.Logger;
import jetbrains.mps.openapi.editor.cells.SubstituteAction;
import jetbrains.mps.openapi.editor.menus.IconResourceProvider;
import jetbrains.mps.smodel.SNodeUtil;
import jetbrains.mps.smodel.adapter.MetaAdapterByDeclaration;
import jetbrains.mps.smodel.presentation.NodePresentationUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.mps.openapi.model.SNode;

import javax.swing.Icon;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

class NodeItemCellRenderer extends JPanel implements ListCellRenderer<SubstituteAction> {
  private static final Logger LOG = Logger.getLogger(NodeItemCellRenderer.class);
  private static final String EXCEPTION_WAS_THROWN_TEXT = "!Exception was thrown!";

  private final SimpleColoredComponent myLeft = new SimpleColoredComponent();
  private final SimpleColoredComponent myRight = new SimpleColoredComponent();
  private static final int HORIZONTAL_GAP = 10;
  private final Color HIGHLIGHT_COLOR = UIUtil.isUnderDarcula() ? new Color(217, 149, 219) : new Color(189, 55, 186);
  private final Color SELECTION_HIGHLIGHT_COLOR = UIUtil.isUnderDarcula() ? HIGHLIGHT_COLOR : new Color(250, 239, 215);
  private final Map<SNode, Icon> myNodeIconMap = new HashMap<>();
  private final Map<SNode, Icon> myConceptIconMap = new HashMap<>();
  private final NodeSubstituteChooser mySubstituteChooser;

  NodeItemCellRenderer(@NotNull NodeSubstituteChooser substituteChooser) {
    mySubstituteChooser = substituteChooser;
    setLayout(new BorderLayout(HORIZONTAL_GAP / 2, 0));
    setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 3, 2, 3));
    add(myLeft, BorderLayout.WEST);
    add(myRight, BorderLayout.EAST);
  }

  @Override
  public Component getListCellRendererComponent(final JList list, final SubstituteAction action, int index, final boolean isSelected, boolean cellHasFocus) {
    mySubstituteChooser.getEditorComponent()
                       .getEditorContext()
                       .getRepository()
                       .getModelAccess()
                       .runReadAction(() -> setupThis(list, action, isSelected, false));
    return this;
  }


  Dimension getDimension(SubstituteAction action, JList<?> list) {
    setupThis(list, action, false, true);
    return getPreferredSize().getSize();
  }

  private void setupThis(JList<?> list, SubstituteAction action, boolean isSelected, boolean isPrecalculating) {
    myLeft.clear();
    myRight.clear();
    String pattern = mySubstituteChooser.getPatternEditor().getPattern();
    try {
      Icon icon = getIcon(action, pattern);
      myLeft.setIcon(icon);
    } catch (Throwable t) {
      LOG.error(t);
    }

    int style = Font.PLAIN;
    try {
      style = getStyle(action, pattern);
    } catch (Throwable t) {
      LOG.error(t);
    }

    Font font = getFont(style).deriveFont(Math.max(13f, mySubstituteChooser.getFont().getSize2D() - 2f));
    myLeft.setFont(font);
    // Reference provenance is supporting information, not another completion
    // name. Keep it compact and regular even when the referent is bold/italic.
    myRight.setFont(font.deriveFont(Font.PLAIN, Math.max(11f, font.getSize2D() - 4f)));


    CompletionCustomizationManager completionCustomizationManager = mySubstituteChooser.getCompletionCustomizationManager();
    Color foreground;
    if (isSelected) {
      foreground = list.getSelectionForeground();
    } else {
      Optional<Color> actionTextColor = completionCustomizationManager.getTextColor(action, pattern);
      foreground = actionTextColor.orElse(list.getForeground());
    }
    try {
      String visibleMatchingText = action.getVisibleMatchingText(pattern);
      if (visibleMatchingText != null) {
        boolean isStrikeout = completionCustomizationManager.isStrikeout(action, pattern);
        appendText(pattern, myLeft, isSelected, visibleMatchingText, foreground, style, isStrikeout);
      }
    } catch (Throwable t) {
      myLeft.append(EXCEPTION_WAS_THROWN_TEXT);
      LOG.error(t);
    }

    try {
      String descriptionText = completionCustomizationManager.getDescriptionText(action, pattern).orElse(action.getDescriptionText(pattern));
      if (descriptionText != null) {
        myRight.append(descriptionText);
      }
    } catch (Throwable t) {
      myRight.append(EXCEPTION_WAS_THROWN_TEXT);
      LOG.error(t);
    }

    if (isSelected) {
      setBackground(list.getSelectionBackground());
      setForeground(list.getSelectionForeground());
      myLeft.setForeground(list.getSelectionForeground());
      myRight.setForeground(new Color(240, 243, 250));
    } else {
      Optional<Color> actionBackgroundColor = completionCustomizationManager.getBackgroundColor(action, pattern);
      Color background = actionBackgroundColor.orElse(list.getBackground());

      setBackground(background);
      setForeground(foreground);
      myRight.setForeground(new Color(112, 117, 128));
      myLeft.setForeground(foreground);
    }

    validate();
    if (!isPrecalculating) {
      Dimension preferredSize = getPreferredSize();
      mySubstituteChooser.getUi().updateListSize(preferredSize.width, preferredSize.height);
    }
  }

  private void appendText(String pattern, SimpleColoredComponent component, boolean isSelected, String text, Color textColor, int style, boolean isStrikeout) {
    if (isStrikeout) {
      style = style | SimpleTextAttributes.STYLE_STRIKEOUT;
    }
    final SimpleTextAttributes base = new SimpleTextAttributes(style, textColor);

    Iterable<TextRange> ranges = mySubstituteChooser.isMenuEmpty() ? null : getMatchingFragments(pattern, text);
    if (ranges != null) {
      SimpleTextAttributes highlighted =
          new SimpleTextAttributes(style, isSelected ? SELECTION_HIGHLIGHT_COLOR : HIGHLIGHT_COLOR);
      SpeedSearchUtil.appendColoredFragments(component, text, ranges, base, highlighted);
    } else {
      component.append(text, base);
    }
  }

  private Iterable<TextRange> getMatchingFragments(String pattern, String text) {
    return NodeSubstituteChooserHandler.CASE_INSENSITIVE_MATCHER_FACTORY.createMatcher(pattern).matchingFragments(text);
  }

  private int getStyle(SubstituteAction action, String pattern) {
    int style = mySubstituteChooser.getCompletionCustomizationManager().isBold(action, pattern) ? Font.BOLD : Font.PLAIN;

    boolean italic = mySubstituteChooser.getCompletionCustomizationManager().isItalic(action, pattern);
    if (italic) {
      style = style | Font.ITALIC;
    }
    if (style == Font.PLAIN) {
      final Object parameterObject = action.getParameterObject();
      if (parameterObject instanceof SNode) {
        style = NodePresentationUtil.getFontStyle(action.getSourceNode(), (SNode) parameterObject);
      }
    }
    return style;
  }

  private Font getFont(int style) {
    Font font = mySubstituteChooser.getFont();
    try {
      font = font.deriveFont(style);
    } catch (Throwable t) {
      LOG.error(t);
    }
    return font;
  }

  private Icon getIcon(SubstituteAction action, String pattern) {
    Icon icon = null;
    if (action instanceof IconResourceProvider) {
      icon = GlobalIconManager.getInstance().getIconForResource(((IconResourceProvider) action).getIcon(pattern));
    }
    if (icon != null) {
      return icon;
    }
    SNode iconNode = action.getIconNode(pattern);
    if (iconNode != null) {
      boolean isConcept = SNodeUtil.isInstanceOfConceptDeclaration(iconNode) && !(action.isReferentPresentation());
      if (isConcept) {
        icon = myConceptIconMap.get(iconNode);
      } else {
        icon = myNodeIconMap.get(iconNode);
      }
      if (icon == null) {
        if (isConcept) {
          icon = GlobalIconManager.getInstance().getIconFor(MetaAdapterByDeclaration.getConcept(iconNode));
          if (icon == null) {
            icon = IdeIcons.DEFAULT_NODE_ICON;
          }
          myConceptIconMap.put(iconNode, icon);
        } else {
          icon = GlobalIconManager.getInstance().getIconFor(iconNode);
          myNodeIconMap.put(iconNode, icon);
        }
      }
    }
    if (icon == null) {
      icon = IdeIcons.DEFAULT_ICON;
    }
    return icon;
  }
}
