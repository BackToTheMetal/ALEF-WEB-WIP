package java.io;

/** Filesystem-backed input is deliberately unavailable in the browser profile. */
public class FileInputStream extends InputStream {
  private final String path;

  public FileInputStream(String name) throws FileNotFoundException {
    path = name;
    throw unavailable(name);
  }

  public FileInputStream(File file) throws FileNotFoundException {
    this(file == null ? null : file.getPath());
  }

  private static FileNotFoundException unavailable(String path) {
    return new FileNotFoundException(String.valueOf(path) + " (no browser filesystem backend)");
  }

  @Override
  public int read() throws IOException {
    throw new IOException(path + " (no browser filesystem backend)");
  }
}
