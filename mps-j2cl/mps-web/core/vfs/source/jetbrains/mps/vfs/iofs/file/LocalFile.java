/*
 * Copyright 2003-2025 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jetbrains.mps.vfs.iofs.file;

import jetbrains.mps.logging.Logger;
import jetbrains.mps.util.FileUtil;
import jetbrains.mps.util.IFileUtil;
import jetbrains.mps.vfs.FileSystem;
import jetbrains.mps.vfs.Files;
import jetbrains.mps.vfs.IFile;
import jetbrains.mps.vfs.IFileSystem;
import jetbrains.mps.vfs.QualifiedPath;
import jetbrains.mps.vfs.VFSManager;
import jetbrains.mps.vfs.path.Path;
import jetbrains.mps.vfs.path.PathFormats;
import jetbrains.mps.vfs.util.PathFormatChecker;
import jetbrains.mps.vfs.util.PathUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.mps.annotations.Immutable;
import org.jetbrains.mps.annotations.Internal;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * IFile implementation via {@link java.io.File}
 */
@Immutable
class LocalFile implements IFile {
  private static final Logger LOG = Logger.getLogger(LocalFile.class);

  private final LocalIoFileSystem myFileSystem;
  private final String myPath;
  @NotNull private final File myFile;

  //must be used only by filesystems
  @Internal
  LocalFile(@NotNull String path, LocalIoFileSystem fileSystem) {
    myPath = path;
    myFileSystem = fileSystem;
    myFile = new File(PathUtil.toSystemDependent(path));
  }

  @NotNull
  @Override
  public IFileSystem getFS() {
    return myFileSystem;
  }

  @NotNull
  @Override
  public FileSystem getFileSystem() {
    return myFileSystem.getUmbrellaFileSystem();
  }

  @NotNull
  @Override
  public String getName() {
    return myFile.getName();
  }

  @Override
  public IFile getParent() {
    File parentFile = myFile.getParentFile();
    if (parentFile == null) {
      return null;
    }
    return myFileSystem.getFile(parentFile);
  }

  @Override
  public boolean isDirectory() {
    return myFile.isDirectory();
  }

  @NotNull
  @Override
  public String getPath() {
    return myPath;
  }

  @NotNull
  @Override
  public Path toPath() {
    return PathFormats.getCurrentSystemFormat().fromString(myPath).toUnixPathFormat();
  }

  @NotNull
  @Override
  public String toRealPath() {
    try {
      return myFile.getCanonicalPath();
    } catch (IOException e) {
      LOG.warning("Got while accessing the canonical path of " + this, e);
      return getPath();
    }
  }

  @Override
  public QualifiedPath getQualifiedPath() {
    return new QualifiedPath(VFSManager.FILE_FS, getPath());
  }

  @Override
  public URL getUrl() throws MalformedURLException {
    return new URL(FileUtil.toFileUri(myFile, false));
  }

  @Override
  public long lastModified() {
    return myFile.lastModified();
  }

  @Override
  public boolean exists() {
    return myFile.exists();
  }

  @Override
  public boolean mkdirs() {
    return myFile.mkdirs();
  }

  @Override
  public boolean createNewFile() {
    try {
      File parentFile = myFile.getParentFile();
      if (parentFile.exists()) {
        if (!parentFile.isDirectory()) {
          return false;
        }
      } else {
        if (!parentFile.mkdirs()) {
          return false;
        }
      }
      if (myFile.exists()) {
        return myFile.isFile();
      }
      return myFile.createNewFile();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public boolean delete() {
    List<IFile> children = getChildren();
    if (children != null) {
      children.forEach(IFile::delete);
    }
    return myFile.delete();
  }

  private boolean renameOrMove(File newIoFile) {
    return myFile.renameTo(newIoFile);
  }

  @Override
  public boolean rename(@NotNull String newName) {
    return renameOrMove(new File(myFile.getParentFile(), newName));
  }

  @Override
  public boolean move(@NotNull IFile newParent) {
    return renameOrMove(new File(new File(newParent.getPath()), myFile.getName()));
  }

  @Override
  public IFile copy(@NotNull IFile newParent, @NotNull String newName) {
    if (!(newParent instanceof LocalFile)) {
      LOG.warning("Copying the file to the new parent '" + newParent + "' which is not an instance of IoFile");
    }
    if (!newParent.isDirectory()) {
      throw new IllegalArgumentException("Cannot copy: '" + newParent + " is not a directory");
    }
    File to = new File(newParent.getPath(), newName);
    if (FileUtil.copyFile(myFile, to)) {
      return new LocalFile(to.getAbsolutePath(), myFileSystem);
    }
    else return null;
  }

  @Override
  public List<IFile> getChildren() {
    File[] files = myFile.listFiles();
    if (files == null) {
      return Collections.emptyList();
    }

    List<IFile> result = new ArrayList<>(files.length);
    for (File f : files) {
      result.add(myFileSystem.getFile(f));
    }
    return result;
  }

  @Override
  @NotNull
  public IFile getDescendant(@NotNull String suffix) {
    IFile result = IFileUtil.getDescendant(this, PathUtil.toSystemIndependent(suffix));
    if (result == null) {
      throw new IllegalStateException("Can't find descendant " + suffix + " of file " + getPath());
    }
    return result;
  }

  @Override
  @NotNull
  public IFile findChild(@NotNull String name) {
    new PathFormatChecker(name).nonEmpty().noSeparators();
    return myFileSystem.getFile(myPath + IFileSystem.SEPARATOR + name);
  }

  @Override
  public boolean isZipArchive() throws IOException {
    return Files.isJarOrZipFile(myFile);
  }

  @Override
  public @NotNull IFile stepIntoArchive() {
    try {
      if (isZipArchive()) {
        return myFileSystem.getUmbrellaFileSystem().getFile(myPath + Path.ARCHIVE_SEPARATOR);
      } else {
        return this;
      }
    } catch (IOException ex) {
      // ignore, treat as a regular file
      return this;
    }
  }

  @Override
  public boolean isInZipArchive() {
    return false;
  }

  @Override
  @NotNull
  public IFile stepUpToArchive() {
    return this;
  }

  @Override
  public InputStream openInputStream() throws IOException {
    return new FileInputStream(myFile);
  }

  @Override
  public OutputStream openOutputStream() throws IOException {
    if (myFile.exists() && myFile.isHidden()) {
      if (!myFile.delete()) {
        throw new RuntimeException();
      }
    }

    createNewFile();
    return new FileOutputStream(myFile);
  }

  @Override
  public boolean isReadOnly() {
    return false;
  }

  @Override
  public long length() {
    return myFile.length();
  }

  public int hashCode() {
    return myFile.hashCode();
  }

  public boolean equals(Object obj) {
    if (!(obj instanceof LocalFile)) {
      return false;
    }

    LocalFile localFile = (LocalFile) obj;
    return localFile.myFile.equals(myFile);
  }

  public String toString() {
    return "LocalFile(" + myPath + ")";
  }

  @Override
  public IFile getBundleHome() {
    return getParent();
  }

  @Override
  public boolean setTimeStamp(long time) {
    return myFile.setLastModified(time);
  }
}
