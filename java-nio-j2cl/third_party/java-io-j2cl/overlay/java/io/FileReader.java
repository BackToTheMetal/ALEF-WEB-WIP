package java.io;

import java.nio.charset.Charset;

/** File reader declaration; construction fails because no browser filesystem is present. */
public class FileReader extends InputStreamReader {
  public FileReader(String fileName) throws FileNotFoundException {
    super(new FileInputStream(fileName));
  }

  public FileReader(File file) throws FileNotFoundException {
    super(new FileInputStream(file));
  }

  public FileReader(String fileName, Charset charset) throws IOException {
    super(new FileInputStream(fileName), charset);
  }

  public FileReader(File file, Charset charset) throws IOException {
    super(new FileInputStream(file), charset);
  }
}
