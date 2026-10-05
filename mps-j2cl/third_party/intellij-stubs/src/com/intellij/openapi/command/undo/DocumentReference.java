package com.intellij.openapi.command.undo;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.vfs.VirtualFile;
public interface DocumentReference {
  DocumentReference[] EMPTY_ARRAY = new DocumentReference[0];
  Document getDocument();
  VirtualFile getFile();
}
