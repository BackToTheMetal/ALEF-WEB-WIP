package com.intellij.openapi.editor;
import com.intellij.openapi.util.UserDataHolder;
public interface Document extends UserDataHolder {
  CharSequence getImmutableCharSequence();
  default String getText() { return getImmutableCharSequence().toString(); }
  default int getTextLength() { return getImmutableCharSequence().length(); }
  void setText(CharSequence text);
  boolean isWritable();
  long getModificationStamp();
}
