package java.io;

/** Filesystem-backed output is deliberately unavailable in the browser profile. */
public class FileOutputStream extends OutputStream {
  private final String path;

  public FileOutputStream(String name) throws FileNotFoundException {
    this(name, false);
  }

  public FileOutputStream(String name, boolean append) throws FileNotFoundException {
    path = name;
    throw unavailable(name);
  }

  public FileOutputStream(File file) throws FileNotFoundException {
    this(file, false);
  }

  public FileOutputStream(File file, boolean append) throws FileNotFoundException {
    this(file == null ? null : file.getPath(), append);
  }

  private static FileNotFoundException unavailable(String path) {
    return new FileNotFoundException(String.valueOf(path) + " (no browser filesystem backend)");
  }

  @Override
  public void write(int value) throws IOException {
    throw new IOException(path + " (no browser filesystem backend)");
  }
}
