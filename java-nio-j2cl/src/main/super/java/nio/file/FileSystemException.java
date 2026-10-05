package java.nio.file;

public class FileSystemException extends java.io.IOException {
  private final String file;
  private final String other;
  public FileSystemException(String file) { this(file, null, null); }
  public FileSystemException(String file, String other, String reason) {
    super(reason); this.file = file; this.other = other;
  }
  public String getFile() { return file; }
  public String getOtherFile() { return other; }
  public String getReason() { return super.getMessage(); }
  @Override public String getMessage() {
    if (file == null && other == null) return getReason();
    String result = file == null ? "" : file;
    if (other != null) result += " -> " + other;
    if (getReason() != null) result += ": " + getReason();
    return result;
  }
}
