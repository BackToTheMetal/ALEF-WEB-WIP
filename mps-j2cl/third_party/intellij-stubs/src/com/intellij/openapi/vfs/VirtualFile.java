package com.intellij.openapi.vfs;
import com.intellij.openapi.util.UserDataHolderBase;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
/** Metadata may be supplied by a browser provider; content operations need an explicit backend. */
public abstract class VirtualFile extends UserDataHolderBase {
  public static final VirtualFile[] EMPTY_ARRAY = new VirtualFile[0];
  public abstract String getName();
  public abstract String getPath();
  public abstract VirtualFileSystem getFileSystem();
  public abstract boolean isWritable();
  public abstract boolean isDirectory();
  public abstract boolean isValid();
  public abstract VirtualFile getParent();
  public abstract VirtualFile[] getChildren();
  public String getUrl() { return getFileSystem().getProtocol() + "://" + getPath(); }
  public String getPresentableUrl() { return getPath(); }
  public String getExtension() { int dot = getName().lastIndexOf('.'); return dot < 0 ? null : getName().substring(dot + 1); }
  public String getNameWithoutExtension() { int dot = getName().lastIndexOf('.'); return dot < 0 ? getName() : getName().substring(0, dot); }
  public CharSequence getNameSequence() { return getName(); }
  public long getTimeStamp() { return 0; }
  public long getModificationStamp() { return getTimeStamp(); }
  public long getLength() { throw unavailable(); }
  public boolean isInLocalFileSystem() { return false; }
  public Charset getCharset() { return StandardCharsets.UTF_8; }
  public void setCharset(Charset charset) { if (!StandardCharsets.UTF_8.equals(charset)) throw unavailable(); }
  public byte[] contentsToByteArray() throws IOException { throw unavailable(); }
  public InputStream getInputStream() throws IOException { return new ByteArrayInputStream(contentsToByteArray()); }
  public OutputStream getOutputStream(Object requestor, long modificationStamp, long timeStamp) throws IOException { throw unavailable(); }
  public void setBinaryContent(byte[] content) throws IOException { throw unavailable(); }
  public void setBinaryContent(byte[] content, long modificationStamp, long timeStamp) throws IOException { throw unavailable(); }
  public VirtualFile findChild(String name) {
    VirtualFile[] children = getChildren();
    if (children != null) for (VirtualFile child : children) if (child.getName().equals(name)) return child;
    return null;
  }
  public VirtualFile findFileByRelativePath(String path) {
    VirtualFile result = this;
    for (String name : path.split("/")) {
      if (name.isEmpty() || name.equals(".")) continue;
      if (result == null) return null;
      result = name.equals("..") ? result.getParent() : result.findChild(name);
    }
    return result;
  }
  public void refresh(boolean asynchronous, boolean recursive) { refresh(asynchronous, recursive, null); }
  public void refresh(boolean asynchronous, boolean recursive, Runnable postRunnable) {
    if (postRunnable != null) postRunnable.run();
  }
  public void delete(Object requestor) throws IOException { throw unavailable(); }
  public void rename(Object requestor, String name) throws IOException { throw unavailable(); }
  public void move(Object requestor, VirtualFile parent) throws IOException { throw unavailable(); }
  public VirtualFile copy(Object requestor, VirtualFile parent, String name) throws IOException { throw unavailable(); }
  public VirtualFile createChildData(Object requestor, String name) throws IOException { throw unavailable(); }
  public VirtualFile createChildDirectory(Object requestor, String name) throws IOException { throw unavailable(); }
  private UnsupportedOperationException unavailable() { return new UnsupportedOperationException("Virtual file backend required: " + getPath()); }
  @Override public String toString() { return getUrl(); }
}
