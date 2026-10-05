/*
 * Copyright 2000-2023 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */
package jetbrains.mps.nodeEditor.documentation;

import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.openapi.wm.ex.ToolWindowEx;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import jetbrains.mps.nodeEditor.documentation.ui.MPSDocumentationToolWindowUI;
import jetbrains.mps.nodeEditor.documentation.ui.MPSDocumentationUI;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.JPanel;

public class MPSDocumentationToolWindowManager {
  public static final Key<MPSDocumentationToolWindowUI> TW_UI_KEY = Key.create("mps.documentation.tw.ui");
  private final Project myProject;

  public static MPSDocumentationToolWindowManager getInstance(@NotNull Project project) {
    return project.getService(MPSDocumentationToolWindowManager.class);
  }

  public MPSDocumentationToolWindowManager(Project project) {
    myProject = project;
  }

  public void showInToolWindow(MPSDocumentationUI ui) {
    Content content = getReusableContent();
    if (content == null) {
      content = addNewContent();
    } else {
      MPSDocumentationToolWindowUI toolWindowUI = content.getUserData(TW_UI_KEY);
      Disposer.dispose(toolWindowUI);
      content.putUserData(TW_UI_KEY, null);
    }
    initUI(ui, content);
    installToolWindowActions(ui);
    makeVisible();
  }

  private void initUI(MPSDocumentationUI ui, Content content) {
    MPSDocumentationToolWindowUI toolWindowUI = new MPSDocumentationToolWindowUI(ui, content);
    toolWindowUI.setContentComponent(content);
    content.putUserData(TW_UI_KEY, toolWindowUI);
  }

  @NotNull
  private Content addNewContent() {
    Content content = ContentFactory.getInstance().createContent(new JPanel(), null, false);
    content.setCloseable(true);
    content.putUserData(ToolWindow.SHOW_CONTENT_ICON, true);
    getToolWindow().getContentManager().addContent(content);
    return content;
  }

  private @Nullable ToolWindow getToolWindow() {
    ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(myProject);
    return toolWindowManager.getToolWindow("Documentation");
  }

  private void installToolWindowActions(MPSDocumentationUI ui) {
    ToolWindowEx toolWindowEx = (ToolWindowEx) getToolWindow();
    if (toolWindowEx != null) {
      toolWindowEx.setTitleActions(ui.getNavigateActions());
      toolWindowEx.setAdditionalGearActions(new DefaultActionGroup(ui.getCommonGearActions()));
    }
  }

  private void makeVisible() {
    if (getToolWindow().isVisible()) {
      return;
    }
    ApplicationManager.getApplication().invokeLater(() -> {
      if (!getToolWindow().isAvailable()) {
        getToolWindow().setAvailable(true);
      }
      getToolWindow().show();
      getToolWindow().activate(null);
    });
  }

  public boolean isVisible() {
    final ToolWindow toolWindow = getToolWindow();
    return toolWindow != null && toolWindow.isVisible();
  }

  @Nullable
  private Content getReusableContent() {
    Content[] contents = getToolWindow().getContentManager().getContents();
    assert (contents.length == 1 || contents.length == 0);
    if (contents.length == 1) {
      return contents[0];
    } else {
      return null;
    }
  }

  public void updateContentDisplayName(String newName) {
    Content reusableContent = getReusableContent();
    if (reusableContent != null) {
      reusableContent.setDisplayName(newName);
    }
  }

}
