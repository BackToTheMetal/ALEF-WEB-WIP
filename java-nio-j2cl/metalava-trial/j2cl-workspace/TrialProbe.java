package trial;

import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import jsinterop.annotations.JsMethod;

public final class TrialProbe {
  private interface Action { void run() throws Exception; }
  private static void expectStub(Action action) throws Exception {
    try {
      action.run();
    } catch (RuntimeException expected) {
      if (expected.getClass() != RuntimeException.class || !"Stub!".equals(expected.getMessage())) {
        throw new AssertionError("Unexpected failure: " + expected);
      }
      return;
    }
    throw new AssertionError("Operation returned instead of throwing");
  }

  @JsMethod public static String run() throws Exception {
    expectStub(() -> Files.exists(null));
    expectStub(() -> Files.readAllBytes(null));
    expectStub(() -> Paths.get("trial"));
    expectStub(() -> ByteBuffer.allocate(8));
    expectStub(() -> FileSystems.getDefault());
    expectStub(() -> FileTime.fromMillis(0));
    expectStub(() -> FileChannel.open((Path) null));
    String value = "NIO trial";
    if (!value.equals(new String(value.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8))) {
      throw new AssertionError("Existing J2CL UTF-8 support was lost");
    }
    return "PASS: 7 Metalava operations throw RuntimeException(Stub!); J2CL UTF-8 preserved";
  }
}
