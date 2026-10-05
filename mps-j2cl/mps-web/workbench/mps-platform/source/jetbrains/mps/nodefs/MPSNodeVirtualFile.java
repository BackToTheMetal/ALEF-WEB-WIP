/* Copyright 2003-2026 JetBrains s.r.o. Licensed under the Apache License, Version 2.0. */
package jetbrains.mps.nodefs;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileSystem;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import jetbrains.mps.ide.project.ProjectHelper;
import jetbrains.mps.smodel.ModelAccessHelper;
import org.jetbrains.mps.openapi.model.SNode;
import org.jetbrains.mps.openapi.model.SNodeReference;
import org.jetbrains.mps.openapi.module.SRepository;

/** A node identity used by editor commands. Node data stays in the actual MPS repository. */
public final class MPSNodeVirtualFile extends VirtualFile {
  public static final String NODE_PREFIX = "node://";
  private final NodeVirtualFileSystem fileSystem;
  private final SRepository repository;
  private final SNodeReference reference;
  private final String path;
  private final List<Runnable> ready = new ArrayList<>();
  private boolean active = true;
  private long timeStamp = System.currentTimeMillis();
  private long modificationStamp = timeStamp;

  MPSNodeVirtualFile(NodeVirtualFileSystem fileSystem, SRepository repository, SNodeReference reference, String path) {
    this.fileSystem = fileSystem; this.repository = repository; this.reference = reference; this.path = path;
  }

  public SNode getNode() {
    return active ? new ModelAccessHelper(repository).runReadAction(() -> reference.resolve(repository)) : null;
  }
  public SNodeReference getSNodePointer() { return reference; }
  @Override public String getPath() { return path; }
  @Override public VirtualFileSystem getFileSystem() { return fileSystem; }
  @Override public String getName() {
    return new ModelAccessHelper(repository).runReadAction(() -> {
      SNode node = active ? reference.resolve(repository) : null;
      return node == null ? reference.toString() : node.getPresentation();
    });
  }
  public String getPresentableName() { return getName(); }
  @Override public boolean isDirectory() { return false; }
  @Override public boolean isWritable() { return false; }
  @Override public boolean isValid() { return getNode() != null; }
  public boolean hasValidMPSNode() { return isValid(); }
  @Override public VirtualFile getParent() { return null; }
  @Override public VirtualFile[] getChildren() { return EMPTY_ARRAY; }
  @Override public long getLength() { return 0; }
  @Override public byte[] contentsToByteArray() { return new byte[0]; }
  @Override public InputStream getInputStream() { return new ByteArrayInputStream(new byte[0]); }
  @Override public long getTimeStamp() { return timeStamp; }
  public void setTimeStamp(long value) { timeStamp = value; }
  @Override public long getModificationStamp() { return modificationStamp; }
  public void setModificationStamp(long value) { modificationStamp = value; }
  public boolean isIncludedInEditorHistory(Project project) { return isInProject(project); }
  public boolean isPersistedInEditorHistory() { return false; }
  public boolean isInProject(Project project) { return active && ProjectHelper.getProjectRepository(project) == repository; }
  public void whenReady(Runnable runnable) {
    if (!active) throw new IllegalStateException("Node handle is disposed");
    if (isValid()) runnable.run(); else ready.add(runnable);
  }
  @Override public void refresh(boolean asynchronous, boolean recursive, Runnable done) {
    if (isValid()) {
      List<Runnable> pending = new ArrayList<>(ready);
      ready.clear();
      for (Runnable runnable : pending) runnable.run();
    }
    if (done != null) done.run();
  }
  void invalidate() { active = false; ready.clear(); }
  public void revalidate() {
    if (!active) throw new IllegalStateException("Released browser node handles cannot be reattached");
    refresh(false, false, null);
  }
}
