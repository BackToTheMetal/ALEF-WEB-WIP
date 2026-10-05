package jetbrains.mps.extapi.persistence;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import jetbrains.mps.messages.Message;
import jetbrains.mps.messages.MessageKind;
import jetbrains.mps.util.StringsIO;
import jetbrains.mps.vfs.IFile;

/** Shared upstream JVM/browser tests for stream, clock, and diagnostic adaptations. */
public final class KernelUtilitiesTest {
  public static String run() {
    StringBuilder output = new StringBuilder();
    for (String input : new String[]{"", "a", "\n", "\r", "\r\n", "\n\n", "a\n", "a\r\nb",
        "a\rb", "\r\r\n", "a\u0085b", "a\u2028b", "a\u2029b", "a\u000Bb", "a\fb",
        "\r\n\u0085\u2028\u2029", "€😀é\n漢字\r\nend", "a\u0000b", "trailing\r"}) {
      byte[] bytes = input.getBytes(StandardCharsets.UTF_8);
      for (int chunk : new int[]{1, 2, 7, 8192}) {
        stream(output, "text:" + escape(input) + ":chunk=" + chunk, bytes, bytes.length + 1, chunk, false);
      }
    }
    byte[][] partialInputs = {
        "α\n😀last".getBytes(StandardCharsets.UTF_8),
        "first\r\nlast".getBytes(StandardCharsets.UTF_8),
        {(byte) 0xE2, (byte) 0x82}, {(byte) 0xFF, 'a', '\n', (byte) 0xC3}
    };
    for (int index = 0; index < partialInputs.length; index++) {
      byte[] bytes = partialInputs[index];
      for (int failAt = 0; failAt <= bytes.length + 1; failAt++) {
        stream(output, "partial:" + index + ":" + failAt, bytes, failAt, 1, false);
      }
    }
    stream(output, "close-failure", "a\nb".getBytes(StandardCharsets.UTF_8), 100, 2, true);
    TrackedOutput written = new TrackedOutput();
    StringsIO.writeLines(written, Arrays.asList("first", "", "€😀", null));
    output.append("write\t").append(escape(new String(written.toByteArray(), StandardCharsets.UTF_8)))
        .append("|closed=").append(written.closes).append('\n');

    long before = System.currentTimeMillis();
    Message message = new Message(MessageKind.WARNING, "sender", "text 'quoted' {0}");
    long after = System.currentTimeMillis();
    require(message.getCreationTime() >= before && message.getCreationTime() <= after, "message epoch-millisecond timestamp");
    RuntimeException cause = new RuntimeException("cause");
    require(message.setException(cause) == message && message.getException() == cause, "message cause");
    message.setHelpUrl("https://example.invalid/help");
    output.append("message\t").append(message).append('|').append(message.getKind()).append('|')
        .append(message.getHelpUrl()).append('\n');

    StringBuilder calls = new StringBuilder();
    SourceRoot root = root("root '{0}' %s", "R", calls);
    SourceRoot existing = root("existing", "E", calls);
    SourceRootKind kind = kind("kind", "K", calls);
    SourceRootKind existingKind = kind("old-kind", "O", calls);
    output.append("kind-name\t").append(new FileKindIsNotAllowedException("kind '{0}'").getMessage()).append('\n');
    output.append("kind-rootpath\t").append(new FileKindIsNotAllowedException("kind", "/ignored-by-upstream").getMessage()).append('\n');
    FileKindIsNotAllowedException kindException = new FileKindIsNotAllowedException(kind, root);
    // J2CL initializes the native Error by rendering this exception once.
    // Measure the explicit diagnostic call independently of that runtime work.
    calls.setLength(0);
    output.append("kind-object\t").append(kindException.getMessage())
        .append("|calls=").append(calls).append('\n');
    calls.setLength(0);
    output.append("duplicate\t").append(SourcePaths.SourceRootAlreadyExistsException.getMsg(root, kind, existing, existingKind))
        .append("|calls=").append(calls).append('\n');
    output.append("null-arguments\t").append(SourcePaths.SourceRootAlreadyExistsException.getMsg(null, null, null, null)).append('\n');
    SourcePaths paths = new SourcePaths();
    paths.addSourceRoot(kind, root);
    require(paths.getByKind(kind).size() == 1 && paths.getByKind(kind).get(0) == root, "source-root registration");
    require(paths.removeSourceRoot(root) == kind && paths.getByKind(kind).isEmpty(), "source-root removal");
    boolean rejected = false;
    try { new SourcePaths(ignored -> false).addSourceRoot(kind, root); }
    catch (FileKindIsNotAllowedException expected) { rejected = true; }
    require(rejected, "prohibited root kind");
    return output.toString();
  }

  private static void stream(StringBuilder output, String label, byte[] bytes, int failAt, int chunk, boolean failClose) {
    TrackedInput input = new TrackedInput(bytes, failAt, chunk, failClose);
    List<String> lines = StringsIO.readLines(input);
    output.append(label).append('\t').append(lines.size());
    for (String line : lines) output.append('|').append(escape(line));
    output.append("|closed=").append(input.closes).append('\n');
    require(input.closes == 1, "readLines must close its input exactly once");
  }

  private static SourceRoot root(String text, String marker, StringBuilder calls) {
    return new SourceRoot() {
      public String getPath() { return text; }
      public IFile getAbsolutePath() { throw new UnsupportedOperationException("Lexical test root"); }
      public String toString() { calls.append(marker); return text; }
    };
  }

  private static SourceRootKind kind(String text, String marker, StringBuilder calls) {
    return new SourceRootKind() {
      public String getName() { return text; }
      public boolean isExcluded() { return false; }
      public String toString() { calls.append(marker); return text; }
    };
  }

  private static final class TrackedInput extends InputStream {
    final byte[] bytes;
    final int failAt;
    final int chunk;
    final boolean failClose;
    int position;
    int closes;
    TrackedInput(byte[] bytes, int failAt, int chunk, boolean failClose) {
      this.bytes = bytes; this.failAt = failAt; this.chunk = chunk; this.failClose = failClose;
    }
    public int read() throws IOException {
      if (position >= failAt) throw new IOException("injected read failure");
      return position >= bytes.length ? -1 : bytes[position++] & 255;
    }
    public int read(byte[] buffer, int offset, int length) throws IOException {
      if (length == 0) return 0;
      if (position >= failAt) throw new IOException("injected read failure");
      if (position >= bytes.length) return -1;
      int count = Math.min(Math.min(length, chunk), Math.min(bytes.length - position, failAt - position));
      System.arraycopy(bytes, position, buffer, offset, count);
      position += count;
      return count;
    }
    public void close() throws IOException {
      closes++;
      if (failClose) throw new IOException("injected close failure");
    }
  }

  private static final class TrackedOutput extends ByteArrayOutputStream {
    int closes;
    public void close() throws IOException { closes++; super.close(); }
  }

  private static String escape(String value) {
    StringBuilder result = new StringBuilder();
    for (int i = 0; i < value.length(); i++) {
      char c = value.charAt(i);
      if (c < 32 || c >= 127 || c == '\\') {
        result.append("\\u");
        for (int shift = 12; shift >= 0; shift -= 4) result.append("0123456789ABCDEF".charAt((c >>> shift) & 15));
      } else result.append(c);
    }
    return result.toString();
  }

  private static void require(boolean value, String message) {
    if (!value) throw new AssertionError(message);
  }

  public static void main(String[] args) { System.out.print(run()); }
}
