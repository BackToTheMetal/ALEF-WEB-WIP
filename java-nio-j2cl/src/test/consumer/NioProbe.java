package org.javanioj2cl.test;

import java.nio.ByteOrder;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.nio.file.attribute.*;
import java.io.IOException;
import jsinterop.annotations.JsMethod;

public final class NioProbe {
  private static int checks;
  private interface Operation { void run() throws Exception; }
  private static void check(boolean value) { checks++; if (!value) throw new AssertionError("Check " + checks); }
  private static void unsupported(String member, Operation operation) {
    checks++;
    try { operation.run(); throw new AssertionError("Unexpected success: " + member); }
    catch (UnsupportedOperationException e) {
      if (e.getMessage() == null || !e.getMessage().contains(member)) throw new AssertionError("Unexpected failure: " + e);
    } catch (Exception e) { throw new AssertionError("Wrong exception: " + member, e); }
  }
  @JsMethod public static String run() {
    checks = 0;
    check("BIG_ENDIAN".equals(ByteOrder.BIG_ENDIAN.toString()));
    check(ByteOrder.BIG_ENDIAN != ByteOrder.LITTLE_ENDIAN);
    check(StandardOpenOption.READ != StandardOpenOption.WRITE);
    check(LinkOption.NOFOLLOW_LINKS instanceof OpenOption);
    check("ENTRY_CREATE".equals(StandardWatchEventKinds.ENTRY_CREATE.name()));
    check(StandardWatchEventKinds.ENTRY_CREATE.type() == Path.class);
    check("READ_ONLY".equals(FileChannel.MapMode.READ_ONLY.toString()));
    check(AclEntryPermission.ADD_FILE == AclEntryPermission.WRITE_DATA);
    check("REPORT".equals(CodingErrorAction.REPORT.toString()));
    check(CoderResult.UNDERFLOW.isUnderflow());
    check(CoderResult.OVERFLOW.isOverflow());
    check(CoderResult.malformedForLength(3).length() == 3);
    check(CoderResult.unmappableForLength(2).isUnmappable());
    check(new MalformedInputException(4).getInputLength() == 4);
    check(new UnmappableCharacterException(5).getMessage().contains("5"));
    IOException cause = new IOException("cause");
    check(new DirectoryIteratorException(cause).getCause() == cause);
    check(new CoderMalfunctionError(cause).getCause() == cause);
    FileSystemException error = new NoSuchFileException("model.mps", "other.mps", "missing");
    check("model.mps".equals(error.getFile()));
    check("other.mps".equals(error.getOtherFile()));
    check("missing".equals(error.getReason()));
    check(error.getMessage().contains("model.mps -> other.mps: missing"));
    InvalidPathException pathError = new InvalidPathException("abc", "invalid", 2);
    check(pathError.getIndex() == 2 && "abc".equals(pathError.getInput()));
    check("name".equals(new UserPrincipalNotFoundException("name").getName()));
    check("UTF-16".equals(StandardCharsets.UTF_16.name()));
    check("US-ASCII".equals(StandardCharsets.US_ASCII.name()));
    check("UTF-8".equals(Charset.forName("UTF-8").name()));
    check(Charset.forName("unknown", StandardCharsets.UTF_8) == StandardCharsets.UTF_8);
    check("MPS é".equals(new String("MPS é".getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8)));
    check("mps".equals(SignatureProbe.metadata()));
    unsupported("java.io.File.toPath", () -> new java.io.File("model.mps").toPath());
    unsupported("ByteOrder.nativeOrder", () -> ByteOrder.nativeOrder());
    unsupported("ByteBuffer.allocate", () -> ByteBuffer.allocate(32));
    unsupported("ByteBuffer.wrap", () -> ByteBuffer.wrap(new byte[0]));
    unsupported("CharBuffer.wrap", () -> CharBuffer.wrap("MPS"));
    unsupported("Files.exists", () -> Files.exists(null));
    unsupported("Files.notExists", () -> Files.notExists(null));
    unsupported("Files.isDirectory", () -> Files.isDirectory(null));
    unsupported("Files.readAllBytes", () -> Files.readAllBytes(null));
    unsupported("Files.write", () -> Files.write(null, new byte[0]));
    unsupported("Files.list", () -> Files.list(null));
    unsupported("Files.walk", () -> Files.walk(null));
    unsupported("Files.newDirectoryStream", () -> Files.newDirectoryStream(null));
    unsupported("Paths.get", () -> Paths.get("model.mps"));
    unsupported("Path.of", () -> Path.of("model.mps"));
    unsupported("FileSystems.getDefault", () -> FileSystems.getDefault());
    unsupported("FileChannel.open", () -> FileChannel.open((Path) null));
    unsupported("SocketChannel.open", () -> SocketChannel.open());
    unsupported("Selector.open", () -> Selector.open());
    unsupported("AsynchronousFileChannel.open", () -> AsynchronousFileChannel.open((Path) null));
    unsupported("FileSystemProvider.installedProviders", () -> java.nio.file.spi.FileSystemProvider.installedProviders());
    unsupported("FileTime.fromMillis", () -> FileTime.fromMillis(1));
    unsupported("Charset.newDecoder", () -> StandardCharsets.UTF_8.newDecoder());
    unsupported("Charset.newEncoder", () -> StandardCharsets.UTF_16.newEncoder());
    unsupported("StandardCharsets encoding UTF-16", () -> "text".getBytes(StandardCharsets.UTF_16));
    unsupported("StandardCharsets decoding UTF-16", () -> new String(new byte[0], StandardCharsets.UTF_16));
    unsupported("CharBuffer.wrap", () -> new jetbrains.mps.baseLanguage.sandbox.internal.InternalAnonymousClassUsage().check3());
    return "PASS: " + checks + " NIO runtime checks; no storage backend";
  }
}
