package trial;

import java.io.IOException;
import java.nio.CharBuffer;
import java.nio.channels.CompletionHandler;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;

/** Compilation probes: checked exceptions, generic bounds, varargs, nested and inherited types. */
final class SignatureProbe {
  static byte[] checkedException(Path path) {
    try { return Files.readAllBytes(path); }
    catch (IOException expected) { return null; }
  }

  static Path varargs(Path path, FileAttribute<?> attribute) throws IOException {
    return Files.createDirectories(path, attribute);
  }

  static void inheritedTypes(CharBuffer buffer) {
    Appendable appendable = buffer;
    Readable readable = buffer;
    Comparable<CharBuffer> comparable = buffer;
  }

  static CompletionHandler<Integer, String> callback() {
    return new CompletionHandler<Integer, String>() {
      public void completed(Integer result, String attachment) {}
      public void failed(Throwable failure, String attachment) {}
    };
  }
}
