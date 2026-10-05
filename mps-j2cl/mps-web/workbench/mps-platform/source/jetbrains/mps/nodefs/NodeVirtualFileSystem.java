/* Copyright 2003-2026 JetBrains s.r.o. Licensed under the Apache License, Version 2.0. */
package jetbrains.mps.nodefs;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileSystem;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.jetbrains.mps.openapi.model.SNode;
import org.jetbrains.mps.openapi.model.SNodeReference;
import org.jetbrains.mps.openapi.module.SRepository;

/** Repository-scoped node identities for browser editor commands; no desktop filesystem. */
public final class NodeVirtualFileSystem extends VirtualFileSystem implements Disposable {
  private final Map<SRepository, Map<SNodeReference, MPSNodeVirtualFile>> repositories = new IdentityHashMap<>();
  private final Map<String, MPSNodeVirtualFile> paths = new HashMap<>();
  private long sequence;
  private boolean disposed;

  public static NodeVirtualFileSystem getInstance() {
    return Objects.requireNonNull(ApplicationManager.getApplication().getService(NodeVirtualFileSystem.class),
        "Register the browser node filesystem before opening editors");
  }

  public MPSNodeVirtualFile getFileFor(SRepository repository, SNode node) {
    return getFileFor(repository, Objects.requireNonNull(node).getReference());
  }

  public MPSNodeVirtualFile getFileFor(SRepository repository, SNodeReference reference) {
    checkAlive();
    Objects.requireNonNull(repository);
    Objects.requireNonNull(reference);
    Map<SNodeReference, MPSNodeVirtualFile> files = repositories.computeIfAbsent(repository, key -> new HashMap<>());
    MPSNodeVirtualFile file = files.get(reference);
    if (file == null) {
      // Paths identify a deployed browser handle. They are deliberately not persisted as IDE history.
      file = new MPSNodeVirtualFile(this, repository, reference, "node://" + (++sequence) + "/" + reference);
      files.put(reference, file);
      paths.put(file.getPath(), file);
    }
    return file;
  }

  public Optional<MPSNodeVirtualFile> lookupVirtualFile(SRepository repository, SNodeReference reference) {
    checkAlive();
    Map<SNodeReference, MPSNodeVirtualFile> files = repositories.get(repository);
    return Optional.ofNullable(files == null ? null : files.get(reference));
  }

  public void releaseRepository(SRepository repository) {
    Map<SNodeReference, MPSNodeVirtualFile> files = repositories.remove(repository);
    if (files == null) return;
    for (MPSNodeVirtualFile file : files.values()) {
      paths.remove(file.getPath());
      file.invalidate();
    }
  }

  @Override public String getProtocol() { return "mps"; }
  @Override public VirtualFile findFileByPath(String path) { checkAlive(); return paths.get(path); }
  @Override public void refresh(boolean asynchronous) {
    checkAlive();
    for (MPSNodeVirtualFile file : paths.values().toArray(new MPSNodeVirtualFile[0])) file.refresh(asynchronous, false, null);
  }
  @Override public boolean isReadOnly() { return true; }
  @Override public void dispose() {
    if (disposed) return;
    for (SRepository repository : repositories.keySet().toArray(new SRepository[0])) releaseRepository(repository);
    disposed = true;
  }
  private void checkAlive() { if (disposed) throw new IllegalStateException("Node filesystem is disposed"); }
}
