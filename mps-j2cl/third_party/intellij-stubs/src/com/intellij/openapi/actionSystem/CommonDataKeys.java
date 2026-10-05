package com.intellij.openapi.actionSystem;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
public class CommonDataKeys {
  public static final DataKey<Project> PROJECT = DataKey.create("project");
  public static final DataKey<VirtualFile> VIRTUAL_FILE = DataKey.create("virtualFile");
  public static final DataKey<VirtualFile[]> VIRTUAL_FILE_ARRAY = DataKey.create("virtualFileArray");
  public static final DataKey<String> SELECTED_TEXT = DataKey.create("selectedText");
}
