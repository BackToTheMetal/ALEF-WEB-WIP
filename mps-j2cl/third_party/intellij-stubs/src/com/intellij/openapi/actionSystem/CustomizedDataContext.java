package com.intellij.openapi.actionSystem;
public final class CustomizedDataContext {
  private CustomizedDataContext() {}
  public static DataContext withSnapshot(DataContext parent, DataSnapshotProvider provider) { return com.intellij.ide.DataManager.createSnapshot(parent, provider); }
}
