/*
 * Copyright 2000-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */
package jetbrains.mps.nodeEditor;

import com.intellij.codeInsight.hint.TooltipGroup;
import com.intellij.codeInsight.hint.TooltipRenderer;
import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.ScrollingModel;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.UserDataHolderBase;
import com.intellij.ui.awt.RelativePoint;
import jetbrains.mps.editor.runtime.DocumentationProvider;
import jetbrains.mps.ide.project.ProjectHelper;
import jetbrains.mps.nodeEditor.documentation.MPSDocumentationManager;
import jetbrains.mps.openapi.editor.cells.EditorCell;
import jetbrains.mps.smodel.CancellableReadAction;
import jetbrains.mps.util.Reference;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Point;
import java.awt.event.MouseEvent;

/** Browser tooltip/view adapter; projectional editing stays in EditorComponent. */
public final class PlatformEditorEmulation extends UserDataHolderBase implements Editor {
  private final EditorComponent myEditorComponent;
  private final PlatformScrollingModelEmulation myScrollingModel;
  private final HintPopupController myHintPopupController = new HintPopupController(this::showInfoToolTip);

  PlatformEditorEmulation(EditorComponent editorComponent) {
    myEditorComponent = editorComponent;
    myScrollingModel = new PlatformScrollingModelEmulation(this);
  }
  public void installListeners(Component owner) { myHintPopupController.installListeners(owner); }
  public void uninstallListeners(Component owner) { myHintPopupController.uninstallListeners(owner); }
  JScrollPane getScrollPane() { return myEditorComponent.getScrollPane(); }
  @Override public JComponent getComponent() { return myEditorComponent.getExternalComponent(); }
  @Override public JComponent getContentComponent() { return myEditorComponent; }
  @Override public ScrollingModel getScrollingModel() { return myScrollingModel; }
  @Override public EditorColorsScheme getColorsScheme() { return EditorColorsManager.getInstance().getGlobalScheme(); }
  @Override public Project getProject() { return CommonDataKeys.PROJECT.getData(DataManager.getInstance().getDataContext(myEditorComponent)); }
  @Override public boolean isDisposed() { return myEditorComponent.isDisposed(); }
  @Override public int getLineHeight() { return myEditorComponent.getFontMetrics(EditorSettings.getInstance().getDefaultEditorFont()).getHeight(); }
  public void cancelShowInfoToolTipRequest() {
    myHintPopupController.cancelAllRequests();
  }

  private void showInfoToolTip(@NotNull MouseEvent event) {
    if (getComponent().getRootPane() == null || MPSDocumentationManager.getInstance().isQuickDocPopupShown()) {
      return;
    }
    // Browser editor frames use ordinary Swing root panes; only actual dialogs suppress documentation.
    DocumentationProvider provider = (SwingUtilities.getWindowAncestor(getComponent()) instanceof Dialog) ? null : getDocumentationProvider(event);

    boolean isGutter = event.getSource() == myEditorComponent.getLeftEditorHighlighter();
    final EditorTooltipProvider tooltipProvider =
        isGutter ? myEditorComponent.getLeftEditorHighlighter().getTooltipProvider() : myEditorComponent.getTooltipProvider();
    if (tooltipProvider == null) {
      // TODO how is this possible?
      return;
    }

    final TooltipRenderer tooltipRenderer = tooltipProvider.getTooltipRenderer(event);
    final TooltipGroup tooltipGroup = tooltipProvider.getTooltipGroup();
    if ((tooltipRenderer == null && (provider == null || !provider.hasDocumentation())) || this.isDisposed()) {
      return;
    }

    final RelativePoint showPoint = getShowPoint(event, isGutter);
    Project project = ProjectHelper.toIdeaProject(ProjectHelper.getProject(myEditorComponent.getRepository()));
    if (project==null) return;
    myHintPopupController.showInfoToolTip(project, this, provider, tooltipRenderer, tooltipGroup, showPoint);
  }

  @NotNull
  private RelativePoint getShowPoint(@NotNull MouseEvent event, boolean isGutter) {
    EditorCell hoverCell = getEditorCellAtXY(event.getX(), event.getY());
    int yCoordinate = hoverCell == null ? event.getY() : hoverCell.getBottom() - 1;
    Point pointToShow = new Point(event.getX(), yCoordinate);
    return new RelativePoint(isGutter ? myEditorComponent.getLeftEditorHighlighter() : myEditorComponent, pointToShow);
  }

  private EditorCell getEditorCellAtXY(int x, int y) {
    final Reference<EditorCell> rv = new Reference<>(null);
    myEditorComponent.getRepository().getModelAccess().runReadAction(new CancellableReadAction() {
      @Override
      protected void execute() {
        if (isDisposed()) {
          return;
        }
        jetbrains.mps.openapi.editor.cells.EditorCell root = myEditorComponent.getRootCell();
        if (root == null) return;
        jetbrains.mps.openapi.editor.cells.EditorCell cell = root.findLeaf(x, y);
        rv.set(cell);
      }
    });
    return rv.get();
  }
  
  @Nullable
  private DocumentationProvider getDocumentationProvider(MouseEvent event) {
    final Reference<DocumentationProvider> providerRef = new Reference<>(null);
    myEditorComponent.getRepository().getModelAccess().runReadAction(new CancellableReadAction() {
      @Override
      protected void execute() {
        if (isDisposed()) {
          return;
        }
        final jetbrains.mps.nodeEditor.cells.EditorCell rootCell = myEditorComponent.getRootCell();
        if (rootCell == null) {
          return;
        }
        jetbrains.mps.openapi.editor.cells.EditorCell cell = rootCell.findLeaf(event.getX(), event.getY());
        if (cell == null) {
          return;
        }
        DocumentationProvider provider = new DocumentationProvider(myEditorComponent.getRepository(), cell);
        providerRef.set(provider);
      }
    });
    return providerRef.get();
  }

  public void release() {
    myHintPopupController.cancelAllRequests();
    myScrollingModel.dispose();
  }
}
