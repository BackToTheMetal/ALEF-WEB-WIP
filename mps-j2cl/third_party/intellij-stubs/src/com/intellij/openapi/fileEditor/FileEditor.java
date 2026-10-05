package com.intellij.openapi.fileEditor;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.util.UserDataHolder;
import javax.swing.JComponent;
public interface FileEditor extends Disposable, UserDataHolder {
  JComponent getComponent();
  JComponent getPreferredFocusedComponent();
  String getName();
  boolean isModified();
  boolean isValid();
}
