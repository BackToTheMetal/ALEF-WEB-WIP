package mpsj2cl.vfs;

import java.io.File;
import java.util.Collections;
import jetbrains.mps.util.FileUtil;
import jetbrains.mps.vfs.IFile;
import jetbrains.mps.vfs.VFSManager;

/** Browser-specific boundary tests: no content query may pretend an archive is empty. */
public final class ArchiveBoundaryTest {
  public static String run() {
    VFSManager manager = new VFSManager();
    manager.init();
    try {
      IFile entry = manager.getFileSystem(VFSManager.JAR_FS).getFile("/libraries/example.jar!/a/b.txt");
      equal("/libraries/example.jar!/a/b.txt", entry.getPath());
      equal("b.txt", entry.getName());
      equal("/libraries/example.jar!/a", entry.getParent().getPath());
      equal("/libraries/example.jar!/a/c.txt", entry.getParent().findChild("c.txt").getPath());
      equal("/libraries/example.jar!/", entry.getParent().getParent().getPath());
      if (entry.getParent().getParent().getParent() != null || !entry.isInZipArchive() || !entry.isReadOnly()) {
        throw new AssertionError("Archive lexical root/read-only contract");
      }
      IFile another = manager.getFileSystem(VFSManager.JAR_FS).getFile(entry.getPath());
      if (!entry.equals(another) || entry.hashCode() != another.hashCode()) {
        throw new AssertionError("Archive identity must not depend on a reference cache");
      }
      unsupported(() -> entry.exists());
      unsupported(() -> entry.isDirectory());
      unsupported(() -> entry.getChildren());
      unsupported(() -> entry.length());
      unsupported(() -> entry.lastModified());
      unsupported(() -> entry.openInputStream());
      unsupported(() -> { FileUtil.zip(new File("/input"), new File("/output")); return null; });
      unsupported(() -> { FileUtil.zip(Collections.emptyMap(), new File("/output")); return null; });
      equal("file:/x/e%CC%81", FileUtil.toFileUri(new File("/x/e\u0301"), true));
      return "PASS";
    } finally {
      manager.dispose();
    }
  }

  private interface Operation {
    Object run() throws Exception;
  }

  private static void unsupported(Operation operation) {
    try {
      operation.run();
      throw new AssertionError("Expected explicit archive provider boundary");
    } catch (UnsupportedOperationException expected) {
      if (!expected.getMessage().contains("provider")) {
        throw new AssertionError("Unsupported archive operation must explain the missing provider");
      }
    } catch (Exception unexpected) {
      throw new AssertionError(unexpected);
    }
  }

  private static void equal(Object expected, Object actual) {
    if (!expected.equals(actual)) {
      throw new AssertionError("Expected " + expected + ", received " + actual);
    }
  }

  public static void main(String[] args) {
    System.out.print(run());
  }
}
