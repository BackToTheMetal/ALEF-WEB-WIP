/*
 * Copyright 2003-2026 JetBrains s.r.o.
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
package jetbrains.mps.util;

import jetbrains.mps.logging.Logger;
import jetbrains.mps.vfs.IFile;
import jetbrains.mps.vfs.path.Path;
import jetbrains.mps.vfs.util.PathFormatChecker;
import jetbrains.mps.vfs.util.PathUtil;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.Objects;

public class FileUtil {
  private static final Logger LOG = Logger.getLogger(FileUtil.class);

  private static final String[] IGNORED_DIRS = new String[]{".svn", ".git", "_svn"};
  public static final String DEFAULT_CHARSET_NAME = "UTF-8";
  public static final Charset DEFAULT_CHARSET = Charset.forName(DEFAULT_CHARSET_NAME);
  private static final String MPSTEMP = "mpstemp";
  private static final char DOT = '.';

  @NotNull
  public static File createTmpDir() {
    return createTmpDir(MPSTEMP);
  }

  public static File createTmpDir(@NotNull String prefix) {
    File tmpDir = null;
    try {
      tmpDir = Files.createTempDirectory(prefix).toRealPath().toFile();
    } catch (IOException e) {
      throw new IllegalStateException("Could not create a temporary directory", e);
    }
    return tmpDir;
  }

  @NotNull
  public static File createTmpFile() {
    return createTmpFile(MPSTEMP);
  }

  public static File createTmpFile(@NotNull String prefix) {
    File result = null;
    try {
      result = File.createTempFile(prefix, "").getCanonicalFile();
    } catch (IOException e) {
      LOG.error(e);
    }
    return result;
  }

  /**
   * Beware, returned fils in not necessarily canonical (e.g. MacOS could give /tmp/whatever with canonical variant /private/tmp/whatever), which
   * may affect code that relies on path prefix matching
   */
  public static File getTempDir() {
    return new File(System.getProperty("java.io.tmpdir"));
  }

  public static void zip(File dir, File to) {
    throw new UnsupportedOperationException("ZIP creation requires an archive provider in the browser profile");
  }

  public static void zip(Map<String, File> entries, File to) {
    throw new UnsupportedOperationException("ZIP creation requires an archive provider in the browser profile");
  }

  public static void copyDir(File what, File to) {
    assert what.isDirectory();
    if (!to.exists()) {
      to.mkdirs();
    }

    for (File f : what.listFiles()) {
      if (f.isDirectory()) {
        if (isIgnoredDir(f.getName())) {
          continue;
        }

        File fCopy = new File(to, f.getName());
        if (!fCopy.exists()) {
          fCopy.mkdir();
        }
        copyDir(f, fCopy);
      }

      if (f.isFile()) {
        copyFile(f, to);
      }
    }

  }

  public static boolean copyFile(File f, File to) {
    if (Objects.equals(f, to)) return true;
    try {
      copyFileChecked(f, to);
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }

  public static void copyFileChecked(File from, File to) throws IOException {
    java.nio.file.Path source = from.toPath();
    java.nio.file.Path toPath = to.toPath();

    java.nio.file.Path target = Files.isDirectory(toPath)
                  ? toPath.resolve(from.getName())
                  : toPath;

    java.nio.file.Path parent = target.getParent();
    if (parent != null) {
      Files.createDirectories(parent);
    }

    Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
  }

  //[MM] this does not return canonical path either
  //todo [MM] review occurences and remove the method
  public static String getCanonicalPath(String path) {
    if (path == null) {
      path = "";
    }
    path = normalize0(path, File.separator);
    File file = new File(path);
    try {
      return file.getCanonicalPath();
    } catch (IOException e) {
      return file.getAbsolutePath();
    }
  }

  /**
   * @return unix-style path without last slashes with some version of normalization
   */
  @NotNull
  //this should be replaced with FS-dependent normalisation (e.g. JarFS.normalise(path))
  public static String normalize(@NotNull String path) {
    return stripLastSlashes(normalize0(getUnixPath(path), Path.UNIX_SEPARATOR));
  }

  @Deprecated
  public static String normalizeAndResolveParentDirs(@NotNull String path) {
    path = normalize(path);
    return resolveParentDirs(path);
  }

  @NotNull
  //replaces /xx/.. with /. Use with already-normalized paths
  // FIXME a/b//c/../../x gives a/b/x, not a/x, as // is treated as fs level, which is wrong, IMO.
  public static String resolveParentDirs(@NotNull String path) {
    new PathFormatChecker(path).absolute().osIndependentPath();

    String currentPath = path;
    if (currentPath.endsWith("/..")) {
      currentPath += "/";
    }
    if (!currentPath.contains("/../")) {
      return currentPath;
    }

    int index;
    next_occ:
    while ((index = currentPath.indexOf("/../")) >= 0) {
      for (int i = index - 1; i >= 0; i--) {
        if (currentPath.charAt(i) == '/') {
          currentPath = currentPath.substring(0, i) + currentPath.substring(index + 3); //we leave the last slash in "/../"
          continue next_occ;
        }
      }
      currentPath = currentPath.replaceFirst("/\\.\\./", "/");
      LOG.warning("Unexpected path: can't get parent: " + path);
    }

    if (currentPath.endsWith("/") && !PathUtil.isRoot(currentPath)) {
      currentPath = currentPath.substring(0, currentPath.length() - 1);
    }
    return currentPath;
  }

  // poor version of normalization
  // does not consider '..'; will be provided in the future release within new vfs API
  private static String normalize0(@NotNull String path, @NotNull String separator) {
    boolean isUNCPath = isUncPath(path);
    path = path.replaceAll("/+", "/").replaceAll("\\\\+", "\\\\");
    if (path.endsWith(separator + DOT)) {
      path = path.substring(0, path.length() - 1);
    }
    // fixme [apyshkin] awful consideration of UNC paths, normalization must be done via vfs.Path utilities
    if (isUNCPath) {
      assert (path.startsWith("\\") || path.startsWith("/"));
      path = path.charAt(0) + path; // we just have cut it, lets return it
    }
    if (path.equals("" + DOT)) {
      return "";
    }
    String oldPath;
    //the following cycle is because "/././".replace(<used template>) -> "/./"
    do {
      oldPath = path;
      path = oldPath.replace("\\.\\", "\\").replace("/./", "/");
    } while (oldPath.length() != path.length());
    return path;
  }

  /*
   * Copied from {@code com.intellij.openapi.util.io.OSAgnosticPathUtil#isUncPath(String)} along with {@code isSlash(char)}
   */
  private static boolean isUncPath(@NotNull String path) {
    return path.length() > 1 && isSlash(path.charAt(0)) && path.charAt(1) == path.charAt(0);
  }

  private static boolean isSlash(char c) {
    return c == '/' || c == '\\';
  }

  public static boolean delete(File root) {
    boolean result = true;

    if (root.isDirectory()) {
      for (File child : root.listFiles()) {
        result = delete(child) && result;
      }
    }
    // !result means one of children was not deleted, hence you may not delete this directory
    return result && root.delete();
  }

  /**
   * FIXME how come this is the only method that brings IFile dependency here? I had an impression
   *       this class is more about File, whereas IFileUtil is about IFile?
   * deletes the file and all its parents above which happen to be empty after this file's removal
   *
   * @return true iff the file has been removed
   */
  public static boolean deleteWithAllEmptyDirs(@NotNull IFile file) {
    if (file.exists()) { // exists is vital, see VirtualFile assert for file existence [AP]
      do {
        file.delete();
        file = file.getParent();
      } while (file != null && file.getChildren().isEmpty());
      return true;
    }
    return false;
  }

  public static boolean clear(File dir) {
    File[] files = dir.listFiles();
    if (files == null) {
      return true;
    }

    boolean result = true;

    for (File f : files) {
      boolean r = delete(f);
      result = result && r;
    }

    return result;
  }

  public static long getNewestFileTime(File dir) {
    return getNewestFileTime(dir, true);
  }

  public static long getNewestFileTime(File dir, boolean recursive) {
    File[] files = dir.listFiles();
    if (files == null) {
      return dir.lastModified();
    }
    long result = dir.lastModified();
    for (File file : files) {
      if (isIgnoredDir(file.getName())) {
        continue;
      }

      if (recursive) {
        result = Math.max(result, getNewestFileTime(file));
      } else {
        result = Math.max(result, file.lastModified());
      }
    }
    return result;
  }

  public static void writeFile(final File file, final String content) throws IOException {
    if (!file.getParentFile().exists()) {
      file.getParentFile().mkdirs();
    }

    if (file.exists()) {
      try {
        String existingContents = FileUtil.read(file);
        if (existingContents.equals(content)) {
          return;
        }
      } catch (RuntimeException ex) {
        /* ignore */
      }
      file.delete();
    }

    boolean fileCreated = false;
    IOException lastExc = null;
    for (int i = 1; i <= 20; i++) {
      try {
        file.createNewFile();
        fileCreated = true;
        break;
      } catch (IOException ex) {
        lastExc = ex;
        //sometimes:
        //java.io.IOException: Access is denied
        //at java.io.WinNTFileSystem.createFileExclusively(Native Method)
        //at java.io.File.createNewFile(File.java:850)
        // so we'll try 5(20) times
      }
      try {
        Thread.sleep(100);
      } catch (InterruptedException ie) {
        //ok
      }
    }

    if (fileCreated) {
      try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file), FileUtil.DEFAULT_CHARSET))) {
        writer.print(content);
        writer.flush();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    } else {
      throw lastExc == null ? new IOException("Can't create " + file.getPath()) : lastExc;
    }
  }

  public static void write(File file, byte[] content) {
    OutputStream writer = null;
    try {
      writer = new FileOutputStream(file);
      writer.write(content);
    } catch (IOException e) {
      throw new RuntimeException(e);
    } finally {
      FileUtil.closeFileSafe(writer);
    }
  }

  public static String read(File file) {
    try {
      return read(new FileReader(file));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static String read(File file, String charset) {
    try {
      return read(new InputStreamReader(new FileInputStream(file), charset));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static String read(Reader reader) {
    BufferedReader r = null;
    try {
      r = new BufferedReader(reader);

      StringBuilder result = new StringBuilder();

      String line;
      while ((line = r.readLine()) != null) {
        result.append(line).append('\n');
      }

      return result.toString();
    } catch (IOException e) {
      throw new RuntimeException(e);
    } finally {
      FileUtil.closeFileSafe(r);
    }
  }

  public static String readLine(Reader reader, int lineNo) {
    BufferedReader r = null;
    try {
      r = new BufferedReader(reader);

      String line;
      int currentLine = 0;
      while ((line = r.readLine()) != null) {
        if (currentLine == lineNo) {
          return line;
        }
        currentLine++;
      }

      return null;
    } catch (IOException e) {
      throw new RuntimeException(e);
    } finally {
      try {
        if (r != null) {
          r.close();
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  public static boolean isIgnoredDir(String name) {
    for (String ignoredDir : IGNORED_DIRS) {
      if (ignoredDir.equals(name)) {
        return true;
      }
    }
    return false;
  }

@Deprecated(since = "2019.1", forRemoval = true)
  @NotNull
  public static String getUnixPath(@NotNull String path) {
    return path.replace(Path.WIN_SEPARATOR, Path.UNIX_SEPARATOR);
  }

  public static boolean isAncestor(@NotNull String ancestorPath, @NotNull String path) {
    ancestorPath = getUnixPath(ancestorPath);
    path = getUnixPath(path);
    return path.startsWith(ancestorPath);
  }

  /**
   * @throws PathResolutionException if the paths do not intersect
   * @deprecated Use of this method is discouraged, as it gives inconsistent results depending on File(basePath) existence, e.g. see MPS-29999
   */
  @Deprecated
  public static String getRelativePath(@NotNull String targetPath, @NotNull String basePath, @NotNull String pathSeparator) {
    // FTR, targetPath["/a/b/c"].startsWith(basePath["/"]) but getRelativePath() fails with exception
    //      (split gives empty base[] in this case, while target has "" as first element)
    String[] base = splitLiteral(basePath, pathSeparator);
    String[] target = splitLiteral(targetPath, pathSeparator);

    StringBuilder common = new StringBuilder();
    int commonIndex = 0;
    while (commonIndex < target.length && commonIndex < base.length
           && target[commonIndex].equals(base[commonIndex])) {
      common.append(target[commonIndex]).append(pathSeparator);
      commonIndex++;
    }

    if (commonIndex == 0) {
      throw new PathResolutionException("No common path element found for '" + targetPath + "' and '" + basePath + "'");
    }

    if (target.length == commonIndex && base.length == target.length) {
      return "";
    }

    boolean baseIsFile = true;

    File baseResource = new File(basePath);

    if (baseResource.exists()) {
      baseIsFile = baseResource.isFile();

    } else if (basePath.endsWith(pathSeparator)) {
      baseIsFile = false;
    }

    StringBuilder relative = new StringBuilder();

    if (base.length != commonIndex) {
      int numDirsUp = baseIsFile ? base.length - commonIndex - 1 : base.length - commonIndex;

      for (int i = 0; i < numDirsUp; i++) {
        relative.append("..").append(pathSeparator);
      }
    }
    if (targetPath.length() > common.length()) {
      relative.append(targetPath.substring(common.length()));
    }
    return relative.toString();
  }

  public static boolean isAbsolute(@NotNull String path) {
    return new File(path).isAbsolute();
  }

  public static String getAbsolutePath(String path) {
    return new File(path).getAbsolutePath();
  }

  public static void closeFileSafe(Closeable c) {
    if (c != null) {
      try {
        c.close();
      } catch (IOException ignored) {
      }
    }
  }

  public static boolean canWrite(File f) {
    while (!f.exists()) {
      f = f.getParentFile();
      if (f == null) {
        return false;
      }
    }
    return f.canWrite();
  }

  public final static class PathResolutionException extends RuntimeException {
    PathResolutionException(String msg) {
      super(msg);
    }
  }

  @NotNull
  public static String getNameWithoutExtension(@NotNull String name) {
    int i = name.lastIndexOf(DOT);
    if (i != -1) {
      name = name.substring(0, i);
    }
    return name;
  }

  /**
   * without dot
   */
  @Nullable
  public static String getExtension(@NotNull String name) {
    int i = name.lastIndexOf(DOT);
    if (i != -1) {
      return name.substring(i + 1);
    }
    return null;
  }

  @Contract(value = "null -> null;!null->!null")
  @Nullable
  public static String stripLastSlashes(@Nullable String path) {
    if (path == null) {
      return null;
    }

    while (path.endsWith(Path.UNIX_SEPARATOR) || path.endsWith(Path.WIN_SEPARATOR)) {
      if (path.endsWith(Path.ARCHIVE_SEPARATOR)) {
        break;
      }
      if (PathUtil.isRoot(path)) {
        break;
      }
      path = path.substring(0, path.length() - 1);
    }
    return path;
  }

  // not taking non-canonical paths into account
  public static boolean isSubPath(@NotNull String base, @NotNull String sub) {
    boolean startsWith = sub.startsWith(base);
    if (!startsWith) {
      return false;
    }
    int baseLen = base.length();
    if (sub.length() == baseLen) {
      return true; // non-strict comparison: equal strings -> true
    }

    char lastBaseChar = base.charAt(baseLen - 1);
    char nextChar = sub.charAt(baseLen);
    return lastBaseChar == '/' || lastBaseChar == '\\' || nextChar == '/' || nextChar == '\\';
  }

  /**
   * Lexical file URI used because the sibling File/URI profiles do not implement
   * toURI/toURL. Does not access storage or assume a browser can fetch this URL.
   * The ASCII form encodes the path's exact Unicode sequence; unlike JDK URI it
   * does not first normalize decomposed Unicode to NFC.
   */
  public static String toFileUri(File file, boolean ascii) {
    File absolute = file.getAbsoluteFile();
    String path = absolute.getPath().replace(File.separatorChar, '/');
    if (!path.startsWith("/")) {
      path = "/" + path;
    }
    if (absolute.isDirectory() && !path.endsWith("/")) {
      path += "/";
    }
    if (path.startsWith("//")) {
      path = "//" + path;
    }
    StringBuilder result = new StringBuilder("file:");
    for (int i = 0; i < path.length();) {
      char c = path.charAt(i);
      if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9')
          || "/-_.!~*'();:@&=+$,".indexOf(c) >= 0
          || (!ascii && c >= 128 && !(c <= 159) && !Character.isSpaceChar(c))) {
        result.append(c);
        i++;
      } else {
        int end = i + 1;
        if (Character.isHighSurrogate(c) && end < path.length() && Character.isLowSurrogate(path.charAt(end))) {
          end++;
        }
        byte[] bytes = path.substring(i, end).getBytes(DEFAULT_CHARSET);
        for (byte value : bytes) {
          int unsigned = value & 255;
          result.append('%').append("0123456789ABCDEF".charAt(unsigned >>> 4))
              .append("0123456789ABCDEF".charAt(unsigned & 15));
        }
        i = end;
      }
    }
    return result.toString();
  }

  // String.split(Pattern.quote(separator)) semantics, including empty input,
  // zero-width splitting for an empty separator, and discarded trailing empties.
  private static String[] splitLiteral(String value, String separator) {
    if (value.isEmpty()) {
      return new String[]{""};
    }
    if (separator.isEmpty()) {
      String[] result = new String[value.length()];
      for (int i = 0; i < result.length; i++) {
        result[i] = value.substring(i, i + 1);
      }
      return result;
    }
    java.util.ArrayList<String> parts = new java.util.ArrayList<>();
    int from = 0;
    int next;
    while ((next = value.indexOf(separator, from)) >= 0) {
      parts.add(value.substring(from, next));
      from = next + separator.length();
    }
    parts.add(value.substring(from));
    while (!parts.isEmpty() && parts.get(parts.size() - 1).isEmpty()) {
      parts.remove(parts.size() - 1);
    }
    return parts.toArray(new String[0]);
  }
}
