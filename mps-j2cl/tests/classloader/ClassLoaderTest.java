package mpsj2cl.classloading;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;

/** Browser execution tests for the actual registered-class JRE loader. */
public final class ClassLoaderTest {
  public static class ParentValue {}
  public static class ChildValue {}
  public static class LateValue {}

  private ClassLoaderTest() {}

  public static String run() throws Exception {
    TestLoader parent = new TestLoader("parent", null);
    TestLoader child = new TestLoader("child", parent);
    try {
      parent.registerClass("example.Value", ParentValue.class);
      child.registerClass("example.Value", ChildValue.class);
      require(child.own("example.Value") == ChildValue.class, "per-loader registration");
      require(child.loadClass("example.Value") == ParentValue.class, "parent-first lookup");
      require(child.loaded("example.Value") == ParentValue.class, "initiated-class record");
      require(child.isOwnRegisteredClass("example.Value", ChildValue.class), "own registration identity");
      require(!child.isOwnRegisteredClass("example.Value", ParentValue.class), "initiated identity is not ownership");
      require(!child.isOwnRegisteredClass("example.Unknown", ChildValue.class), "unknown registration is not ownership");
      expectMissing(() -> child.loadClass("example.Late"));
      child.registerClass("example.Late", LateValue.class);
      require(child.loadClass("example.Late") == LateValue.class, "miss does not poison a later registration");
      require(ClassLoader.getSystemClassLoader().loadClass("java.lang.String") == String.class, "explicit JRE bootstrap registration");

      boolean conflict = false;
      try {
        child.registerClass("example.Late", ChildValue.class);
      } catch (IllegalStateException expected) {
        conflict = true;
      }
      require(conflict, "class registration cannot replace resolved identity");
      boolean bytecodeRejected = false;
      try {
        child.defineBytes();
      } catch (UnsupportedOperationException expected) {
        bytecodeRejected = expected.getMessage().contains("JVM bytecode");
      }
      require(bytecodeRejected, "defineClass fails explicitly");

      byte[] bytes = {1, 2, 3};
      URL ownResource = child.registerResource("model.bin", bytes);
      bytes[0] = 9;
      try (InputStream first = child.getResourceAsStream("model.bin");
           InputStream second = child.getResourceAsStream("model.bin")) {
        require(first != second && first.read() == 1 && second.read() == 1, "defensive registration and independent streams");
        require(first.read() == 2 && first.read() == 3 && first.read() == -1, "buffered stream contents");
      }
      URL parentResource = parent.registerResource("model.bin", new byte[]{7});
      require(child.getResource("model.bin").equals(parentResource), "parent-first resource lookup");
      Enumeration<URL> resources = child.getResources("model.bin");
      require(resources.nextElement().equals(parentResource) && resources.nextElement().equals(ownResource)
          && !resources.hasMoreElements(), "resource enumeration order");
      require(child.getResourceAsStream("missing") == null, "unknown resource is absent");
      child.clear();
      boolean revoked = false;
      try {
        ClassLoader.openRegisteredResource(ownResource);
      } catch (IOException expected) {
        revoked = true;
      }
      require(revoked, "clearing registrations revokes memory resource handles");
    } finally {
      child.clear();
      parent.clear();
    }
    return "ClassLoader: registration, delegation, misses, bytecode boundary, resource contents/order/lifetime passed";
  }

  private static void expectMissing(Action action) throws Exception {
    try {
      action.run();
      throw new AssertionError("Expected ClassNotFoundException");
    } catch (ClassNotFoundException expected) {
    }
  }

  private static void require(boolean condition, String message) {
    if (!condition) throw new AssertionError(message);
  }

  private interface Action { void run() throws Exception; }

  private static final class TestLoader extends ClassLoader {
    private TestLoader(String name, ClassLoader parent) { super(name, parent); }
    private Class<?> own(String name) { return findRegisteredClass(name); }
    private Class<?> loaded(String name) { return findLoadedClass(name); }
    private void clear() { clearRegistrations(); }
    private Class<?> defineBytes() { return defineClass("example.Bytes", new byte[0], 0, 0); }
  }
}
