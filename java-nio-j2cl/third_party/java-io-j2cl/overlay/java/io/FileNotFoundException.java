package java.io;

/** Browser-profile file access failure. */
public class FileNotFoundException extends IOException {
  public FileNotFoundException() {}

  public FileNotFoundException(String message) {
    super(message);
  }
}
