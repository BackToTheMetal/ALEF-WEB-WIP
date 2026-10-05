package org.javanioj2cl.test;

import java.io.*;
import java.net.URI;
import java.nio.*;
import java.nio.channels.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.nio.file.attribute.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Stream;

/** Compile-only probes: independent consumer expressions for fragile API relationships. */
public final class SignatureProbe {
  static ByteBuffer buffers(ByteBuffer b, CharBuffer c) throws IOException {
    Readable readable = c;
    readable.read(c);
    b.get(0, new byte[2], 0, 2);
    b.put(0, new byte[2], 0, 2);
    return b.position(0).limit(2).mark().reset().flip().rewind().clear();
  }
  static void files(Path p, URI uri, File file) throws IOException {
    Path other = file.toPath();
    File converted = p.toFile();
    Path fromUri = Path.of(uri);
    try (DirectoryStream<Path> directory = Files.newDirectoryStream(p, item -> item.endsWith(".mps"))) {
      for (Path child : directory) Files.copy(child, other, StandardCopyOption.REPLACE_EXISTING);
    } catch (DirectoryIteratorException exception) { throw exception.getCause(); }
    try (Stream<Path> stream = Files.find(p, 3, (path, attributes) -> attributes.isRegularFile())) {
      stream.forEach(path -> {});
    }
    Files.walkFileTree(p, new SimpleFileVisitor<Path>() {
      @Override public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) { return FileVisitResult.CONTINUE; }
    });
    FileAttribute<Set<PosixFilePermission>> attributes = PosixFilePermissions.asFileAttribute(Collections.emptySet());
    Files.createDirectory(p, attributes);
    BasicFileAttributes basic = Files.readAttributes(p, BasicFileAttributes.class);
    WatchEvent.Kind<Path> kind = StandardWatchEventKinds.ENTRY_CREATE;
    p.register(p.getFileSystem().newWatchService(), kind);
  }
  static void channels(Path path, ByteBuffer buffer, ExecutorService executor) throws IOException {
    try (FileChannel channel = FileChannel.open(path, StandardOpenOption.READ)) {
      MappedByteBuffer mapped = channel.map(FileChannel.MapMode.READ_ONLY, 0, 16);
      FileLock lock = channel.lock();
      channel.read(new ByteBuffer[]{buffer}, 0, 1);
      lock.release();
    }
    AsynchronousFileChannel async = AsynchronousFileChannel.open(path, Collections.singleton(StandardOpenOption.READ), executor);
    Future<Integer> result = async.read(buffer, 0);
    async.read(buffer, 0, "attachment", new CompletionHandler<Integer,String>() {
      public void completed(Integer count, String attachment) {}
      public void failed(Throwable error, String attachment) {}
    });
  }
  private static final class UserCharset extends Charset {
    UserCharset() { super("X-MPS", new String[]{"mps"}); }
    public boolean contains(Charset other) { return other == this; }
    public CharsetEncoder newEncoder() { throw new UnsupportedOperationException(); }
    public CharsetDecoder newDecoder() { throw new UnsupportedOperationException(); }
  }
  static String metadata() { return new UserCharset().aliases().iterator().next(); }
}
