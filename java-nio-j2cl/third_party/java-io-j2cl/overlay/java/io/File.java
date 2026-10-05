package java.io;

/**
 * Lexical browser profile of {@code File}.
 *
 * <p>The class represents a path but is not connected to persistent storage. Queries describe a
 * permanently absent filesystem; operations that would create resources fail explicitly.
 */
public class File implements Serializable, Comparable<File> {
  private static final long serialVersionUID = 301077366599181567L;

  public static final char separatorChar = '/';
  public static final String separator = "/";
  public static final char pathSeparatorChar = ':';
  public static final String pathSeparator = ":";

  private final String path;

  public File(String pathname) {
    if (pathname == null) {
      throw new NullPointerException("pathname");
    }
    path = pathname;
  }

  public File(String parent, String child) {
    this(join(parent, child));
  }

  public File(File parent, String child) {
    this(parent == null ? child : join(parent.path, child));
  }

  private static String join(String parent, String child) {
    if (child == null) {
      throw new NullPointerException("child");
    }
    if (parent == null || parent.length() == 0) {
      return child;
    }
    char last = parent.charAt(parent.length() - 1);
    return last == '/' || last == '\\' ? parent + child : parent + separatorChar + child;
  }

  private static int lastSeparator(String value) {
    return Math.max(value.lastIndexOf('/'), value.lastIndexOf('\\'));
  }

  public String getName() {
    int separatorIndex = lastSeparator(path);
    return path.substring(separatorIndex + 1);
  }

  public String getParent() {
    int separatorIndex = lastSeparator(path);
    if (separatorIndex < 0) {
      return null;
    }
    if (separatorIndex == 0) {
      return path.length() == 1 ? null : path.substring(0, 1);
    }
    return path.substring(0, separatorIndex);
  }

  public File getParentFile() {
    String parent = getParent();
    return parent == null ? null : new File(parent);
  }

  public String getPath() {
    return path;
  }

  public boolean isAbsolute() {
    if (path.length() == 0) {
      return false;
    }
    char first = path.charAt(0);
    return first == '/' || first == '\\' || (path.length() > 1 && path.charAt(1) == ':');
  }

  public String getAbsolutePath() {
    return isAbsolute() ? path : separator + path;
  }

  public File getAbsoluteFile() {
    return new File(getAbsolutePath());
  }

  public String getCanonicalPath() throws IOException {
    return getAbsolutePath();
  }

  public File getCanonicalFile() throws IOException {
    return new File(getCanonicalPath());
  }

  public boolean canRead() {
    return false;
  }

  public boolean canWrite() {
    return false;
  }

  public boolean exists() {
    return false;
  }

  public boolean isDirectory() {
    return false;
  }

  public boolean isFile() {
    return false;
  }

  public boolean isHidden() {
    return false;
  }

  public long lastModified() {
    return 0L;
  }

  public long length() {
    return 0L;
  }

  public boolean createNewFile() throws IOException {
    throw unsupported("createNewFile");
  }

  public boolean delete() {
    return false;
  }

  public void deleteOnExit() {
    throw new UnsupportedOperationException("java.io.File.deleteOnExit has no browser backend");
  }

  public String[] list() {
    return null;
  }

  public String[] list(FilenameFilter filter) {
    return null;
  }

  public File[] listFiles() {
    return null;
  }

  public File[] listFiles(FilenameFilter filter) {
    return null;
  }

  public File[] listFiles(FileFilter filter) {
    return null;
  }

  public boolean mkdir() {
    return false;
  }

  public boolean mkdirs() {
    return false;
  }

  public boolean renameTo(File destination) {
    if (destination == null) {
      throw new NullPointerException("destination");
    }
    return false;
  }

  public boolean setLastModified(long time) {
    if (time < 0L) {
      throw new IllegalArgumentException("Negative time");
    }
    return false;
  }

  public boolean setReadOnly() {
    return false;
  }

  public boolean setWritable(boolean writable, boolean ownerOnly) {
    return false;
  }

  public boolean setWritable(boolean writable) {
    return false;
  }

  public boolean setReadable(boolean readable, boolean ownerOnly) {
    return false;
  }

  public boolean setReadable(boolean readable) {
    return false;
  }

  public boolean setExecutable(boolean executable, boolean ownerOnly) {
    return false;
  }

  public boolean setExecutable(boolean executable) {
    return false;
  }

  public boolean canExecute() {
    return false;
  }

  public static File[] listRoots() {
    return new File[0];
  }

  public long getTotalSpace() {
    return 0L;
  }

  public long getFreeSpace() {
    return 0L;
  }

  public long getUsableSpace() {
    return 0L;
  }

  public static File createTempFile(String prefix, String suffix, File directory)
      throws IOException {
    throw unsupported("createTempFile");
  }

  public static File createTempFile(String prefix, String suffix) throws IOException {
    throw unsupported("createTempFile");
  }

  private static IOException unsupported(String operation) {
    return new IOException("java.io.File." + operation + " has no browser storage backend");
  }

  @Override
  public int compareTo(File other) {
    return path.compareTo(other.path);
  }

  @Override
  public boolean equals(Object other) {
    return other instanceof File && path.equals(((File) other).path);
  }

  @Override
  public int hashCode() {
    return path.hashCode();
  }

  @Override
  public String toString() {
    return path;
  }
}
