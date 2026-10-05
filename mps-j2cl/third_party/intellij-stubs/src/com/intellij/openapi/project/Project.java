package com.intellij.openapi.project;
import com.intellij.openapi.components.ComponentManager;
import com.intellij.openapi.vfs.VirtualFile;
public interface Project extends ComponentManager {
  String DIRECTORY_STORE_FOLDER = ".idea";
  String getName();
  String getBasePath();
  default VirtualFile getBaseDir() { return null; }
  default VirtualFile getProjectFile() { return null; }
  default String getProjectFilePath() { return null; }
  default VirtualFile getWorkspaceFile() { return null; }
  default String getPresentableUrl() { return getBasePath(); }
  default String getLocationHash() { return Integer.toHexString(getBasePath().hashCode()); }
  default boolean isOpen() { return !isDisposed(); }
  default boolean isInitialized() { return !isDisposed(); }
  default boolean isDefault() { return false; }
  default void save() { throw new UnsupportedOperationException("Browser project persistence must be supplied by the host"); }
  default void scheduleSave() { save(); }
}
