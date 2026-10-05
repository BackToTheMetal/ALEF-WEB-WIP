package com.intellij.openapi.fileEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import java.util.ArrayList;
import java.util.List;
public class FileEditorManager {
  private static final Key<FileEditorManager> KEY = Key.create("browser.fileEditorManager");
  private final Project project;
  private final List<FileEditor> editors = new ArrayList<>(), selected = new ArrayList<>();
  protected FileEditorManager(Project project) { this.project = project; }
  public static FileEditorManager getInstance(Project project) {
    FileEditorManager manager = project.getUserData(KEY);
    if (manager == null) { manager = new FileEditorManager(project); project.putUserData(KEY, manager); }
    return manager;
  }
  public Project getProject() { return project; }
  public FileEditor[] getAllEditors() { return editors.toArray(new FileEditor[0]); }
  public FileEditor[] getSelectedEditors() { return selected.toArray(new FileEditor[0]); }
  public void registerBrowserEditor(FileEditor editor, boolean select) {
    if (!editors.contains(editor)) editors.add(editor);
    if (select && !selected.contains(editor)) selected.add(editor);
  }
  public void unregisterBrowserEditor(FileEditor editor) { editors.remove(editor); selected.remove(editor); }
}
