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
package jetbrains.mps.nodeEditor;

import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.awt.RelativePoint;
import com.intellij.util.ui.JBUI;
import jetbrains.mps.smodel.presentation.NodePresentationUtil;
import org.jetbrains.mps.openapi.model.SModelReference;
import org.jetbrains.mps.openapi.model.SNode;
import org.jetbrains.mps.openapi.model.SReference;

import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.border.LineBorder;
import java.awt.Point;

// name ...Dialog is a legacy (used to be a JDialog); didn't rename as this class is part of EditorComponent interface
public final class NodeInformationDialog {
  private final EditorComponent myEditorComponent;
  private final Point myLocation;

  public NodeInformationDialog(EditorComponent editorComponent, Point location) {
    // we could have stick to openapi.EC, if EC.setNodeInformationDialog() or similar would be in editor's openapi.
    myEditorComponent = editorComponent;
    myLocation = location;
  }

  public void show(String text) {
    final var textArea = new JEditorPane("text/html", "") {
      @Override
      public String getSelectedText() {
        // The browser JEditorPane already stores normalized plain text.
        String selected = super.getSelectedText();
        return (selected == null ? getText() : selected).trim();
      }
    };

    textArea.setEditable(false);
    textArea.setText("<html>\n" + text + "</html>");
    textArea.setFont(JBUI.Fonts.label());

    textArea.setOpaque(true);
    textArea.setBackground(JBUI.Panels.simplePanel().getBackground());
    textArea.setBorder(JBUI.Borders.empty(5));

    JScrollPane scrollPane = ScrollPaneFactory.createScrollPane(textArea);
    scrollPane.setBorder(new LineBorder(MPSColors.BLACK));
    var popup = JBPopupFactory.getInstance().createComponentPopupBuilder(scrollPane, textArea)
                              .setRequestFocus(true).setFocusable(true)
                              .setMovable(false).setResizable(false).setShowBorder(true).setShowShadow(false)
                              .setLocateWithinScreenBounds(true)
                              .setCancelOnClickOutside(true).setCancelKeyEnabled(true)
                              .setCancelCallback(this::dispose).createPopup();

    myEditorComponent.setNodeInformationDialog(this);
    popup.show(new RelativePoint(myLocation));
  }

  /*package*/ boolean dispose() {
    myEditorComponent.setNodeInformationDialog(null);
    return true;
  }

  public static String createNodeInfo(final SNode node) {
    StringBuilder result = new StringBuilder();

    result.append("<b>model</b> = ").append(node.getModel().getReference()).append("<br/>\n");
    result.append("<b>concept</b> = ").append(node.getConcept().getName()).append("<br/>\n");
    result.append("<b>language</b> = ").append(node.getConcept().getLanguage().getQualifiedName()).append("<br/>\n");
    result.append("<b>ID</b> = ").append(node.getNodeId().toString()).append("<br/>\n");

    Iterable<? extends SReference> references = node.getReferences();
    if (references.iterator().hasNext()) {
      result.append("\n<b>References:</b><br/>\n");
    }
    for (SReference ref : references) {
      result.append("<i>" + ref.getLink().getName() + "</i>").append("<br/>\n");
      SNode target = ref.getTargetNode();
      final SModelReference refTargetModel = ref.getTargetSModelReference();

      if (target == null) {
        result.append("<i>Bad Reference</i>");
        if (refTargetModel != null) {
          result.append(", <b>target model</b> = ").append(refTargetModel);
        }
        result.append("<br/>\n");
      } else {
        if (target.getModel() != null) {
          result.append("<b>Model</b> = ").append(target.getModel().getReference()).append("<br/>\n");
        } else {
          result.append("<i>Target node in role doesn't belong to a model</i>");
          if (refTargetModel != null) {
            result.append(", <b>target model</b> = ").append(refTargetModel);
          }
          result.append("<br/>\n");
        }
        result.append("<b>Node</b> = ").append(NodePresentationUtil.getPathToRoot(target)).append("<br/>\n");
        result.append("<b>ID</b> = ").append(target.getNodeId().toString()).append("<br/>\n");
      }

      result.append("<br/>\n");
    }

    return result.toString();
  }

}
