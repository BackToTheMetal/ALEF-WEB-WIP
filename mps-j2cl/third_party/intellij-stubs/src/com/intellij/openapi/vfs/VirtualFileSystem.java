package com.intellij.openapi.vfs;
public abstract class VirtualFileSystem {
  public abstract String getProtocol();
  public abstract VirtualFile findFileByPath(String path);
  public VirtualFile refreshAndFindFileByPath(String path) { return findFileByPath(path); }
  public void refresh(boolean asynchronous) {}
  public boolean isReadOnly() { return true; }
}
