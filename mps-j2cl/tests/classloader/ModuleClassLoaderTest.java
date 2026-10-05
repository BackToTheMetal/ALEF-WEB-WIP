package jetbrains.mps.classloading;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import jetbrains.mps.reloading.IClassPathItem;
import jetbrains.mps.reloading.RealClassPathItem;
import org.jetbrains.mps.openapi.module.SModule;
import org.jetbrains.mps.openapi.module.SModuleId;
import org.jetbrains.mps.openapi.module.SModuleReference;
import org.jetbrains.mps.openapi.module.SRepository;

/** Runs module delegation/factories against the real MPS replacements. */
public final class ModuleClassLoaderTest {
  public interface Value { String value(); }
  public static class Own implements Value { public String value() { return "own"; } }
  public static class Dependency implements Value { public String value() { return "dependency"; } }
  public static class Parent implements Value { public String value() { return "parent"; } }
  public static class Created implements Value {
    private final String text;
    private Created(String text) { this.text = text; }
    public String value() { return text; }
  }

  private ModuleClassLoaderTest() {}

  public static String run() throws Exception {
    TestParent parent = new TestParent();
    parent.registerClass("example.Value", Parent.class);
    parent.registerClass("example.Parent", Parent.class);
    List<ClassLoader> aDependencies = new ArrayList<>();
    List<ClassLoader> bDependencies = new ArrayList<>();
    ModuleClassLoader a = new ModuleClassLoader("a", parent, new ModuleReference("a"), new ClassPath(), () -> aDependencies);
    ModuleClassLoader b = new ModuleClassLoader("b", parent, new ModuleReference("b"), new ClassPath(), () -> bDependencies);
    aDependencies.add(b);
    bDependencies.add(a);
    try {
      a.registerClass("example.Value", Own.class);
      b.registerClass("example.Value", Dependency.class);
      b.registerClass("example.Dependency", Dependency.class);
      require(a.loadClass("example.Value") == Own.class, "module-first class lookup");
      require(a.loadClass("example.Dependency") == Dependency.class, "direct dependency lookup");
      expectMissing(() -> a.loadOwnClass("example.Dependency"));
      require(a.loadClass("example.Parent") == Parent.class, "parent fallback");
      expectMissing(() -> a.loadClass("example.Unknown"));
      require(a.loadOwnClass("example.Value") == Own.class, "own lookup stays local after delegated cache entries");
      TestParent plugin = new TestParent("plugin", parent);
      IDEADelegatingModuleClassLoader pluginWrapper = new IDEADelegatingModuleClassLoader(new ModuleReference("plugin"), plugin);
      try {
        plugin.registerClass("example.Plugin", Dependency.class);
        plugin.registerClass("example.Value", Dependency.class);
        require(pluginWrapper.loadOwnClass("example.Plugin") == Dependency.class, "plugin own registration");
        require(pluginWrapper.loadClass("example.Parent") == Parent.class, "plugin parent delegation");
        expectMissing(() -> pluginWrapper.loadOwnClass("example.Parent"));
        expectMissing(() -> pluginWrapper.loadOwnClass("example.Value"));
      } finally {
        pluginWrapper.dispose();
        plugin.clear();
      }
      boolean bytecodeRejected = false;
      try {
        a.loadClass("example.OnlyBytecode");
      } catch (UnsupportedOperationException expected) {
        bytecodeRejected = expected.getMessage().contains("JVM bytecode");
      }
      require(bytecodeRejected, "classpath bytecode is distinguished from an unknown class");

      URL parentResource = parent.registerResource("data", new byte[]{3});
      URL dependencyResource = b.registerResource("data", new byte[]{2});
      URL ownResource = a.registerResource("data", new byte[]{1});
      require(a.getOwnResource("data").equals(ownResource), "own resource lookup");
      require(a.getResource("data").equals(ownResource), "module-first resource lookup");
      Enumeration<URL> resources = a.getResources("data");
      require(resources.nextElement().equals(ownResource) && resources.nextElement().equals(dependencyResource)
          && resources.nextElement().equals(parentResource) && !resources.hasMoreElements(), "module/dependency/parent resource order");
      try (InputStream stream = a.getResourceAsStream("data")) {
        require(stream.read() == 1, "module-selected buffered resource bytes");
      }

      a.registerFactory("example.Created", Created.class, new Class<?>[0], args -> new Created("zero"));
      a.registerFactory("example.Created", Created.class, new Class<?>[]{String.class, Value.class},
          args -> new Created((String) args[0] + ((Value) args[1]).value()));
      a.registerFactory("example.Created", Created.class, new Class<?>[]{String.class, Value.class, int.class},
          args -> new Created((String) args[0] + ((Value) args[1]).value() + args[2]));
      require(ModuleClassLoader.hasFactory(Created.class), "noarg factory lookup by actual Class");
      require("zero".equals(ModuleClassLoader.newInstance(Created.class, Value.class).value()), "noarg factory by Class");
      require("xown".equals(a.newInstance("example.Created", Value.class,
          new Class<?>[]{String.class, Value.class}, "x", new Own()).value()), "typed two-argument factory");
      require("xown4".equals(ModuleClassLoader.newInstance(Created.class, Value.class,
          new Class<?>[]{String.class, Value.class, int.class}, "x", new Own(), 4).value()), "typed three-argument factory");
      boolean wrongArgument = false;
      try {
        a.newInstance("example.Created", Value.class, new Class<?>[]{String.class, Value.class}, "x", "wrong");
      } catch (IllegalArgumentException expected) {
        wrongArgument = true;
      }
      require(wrongArgument, "factory checks argument types before invoking callback");
      boolean wrongResultType = false;
      try {
        ModuleClassLoader.newInstance(Created.class, String.class);
      } catch (ClassCastException expected) {
        wrongResultType = true;
      }
      require(wrongResultType, "factory checks requested result type");
      boolean missingFactory = false;
      try {
        ModuleClassLoader.newInstance(Created.class, Value.class, new Class<?>[]{String.class}, "x");
      } catch (ModuleClassLoader.FactoryNotFoundException expected) {
        missingFactory = true;
      }
      require(missingFactory, "missing factory signature is explicit");

      a.dispose();
      a.dispose();
      require(a.isDisposed() && !ModuleClassLoader.hasFactory(Created.class), "dispose unregisters factories and is idempotent");
      require(aDependencies.size() == 1, "dispose does not mutate supplied dependency collections");
      boolean disposedCause = false;
      try {
        a.loadClass("example.Value");
      } catch (ModuleClassNotFoundException expected) {
        disposedCause = expected.getCause() instanceof ModuleClassLoader.ModuleClassLoaderIsDisposedException;
      }
      require(disposedCause, "loadClass reports the disposed module as its failure cause");
      boolean ownDisposed = false;
      try {
        a.loadOwnClass("example.Value");
      } catch (ModuleClassLoader.ModuleClassLoaderIsDisposedException expected) {
        ownDisposed = expected.getDisposeTrace() != null;
      }
      require(ownDisposed, "own lookup exposes the original dispose trace");
      boolean registrationDisposed = false;
      try {
        a.registerClass("example.Late", Own.class);
      } catch (ModuleClassLoader.ModuleClassLoaderIsDisposedException expected) {
        registrationDisposed = true;
      }
      require(registrationDisposed, "disposed loaders reject new registrations");
    } finally {
      a.dispose();
      b.dispose();
      parent.clear();
    }
    return "ModuleClassLoader: delegation/cycles, own boundary, bytecode, resources, typed factories and disposal passed";
  }

  private static void require(boolean condition, String message) {
    if (!condition) throw new AssertionError(message);
  }

  private static void expectMissing(Action action) throws Exception {
    try {
      action.run();
      throw new AssertionError("Expected ClassNotFoundException");
    } catch (ClassNotFoundException expected) {
    }
  }

  private interface Action { void run() throws Exception; }

  private static final class TestParent extends ClassLoader {
    private TestParent() { super("test-parent", null); }
    private TestParent(String name, ClassLoader parent) { super(name, parent); }
    private void clear() { clearRegistrations(); }
  }

  private static final class ModuleReference implements SModuleReference {
    private final String name;
    private final SModuleId id = new SModuleId() {};
    private ModuleReference(String name) { this.name = name; }
    public SModuleId getModuleId() { return id; }
    public String getModuleName() { return name; }
    public SModule resolve(SRepository repository) { throw new UnsupportedOperationException("Not used by loader test"); }
  }

  private static final class ClassPath implements IClassPathItem {
    public boolean hasClass(String name) { return "example.OnlyBytecode".equals(name); }
    public ClassBytes getClassBytes(String name) { throw new AssertionError("Browser loader must not read JVM class bytes"); }
    public URL getResource(String name) { throw new AssertionError("Resources must come from the buffered registry"); }
    public Enumeration<URL> getResources(String name) { throw new AssertionError("Resources must come from the buffered registry"); }
    public List<RealClassPathItem> flatten() { return Collections.emptyList(); }
    public IClassPathItem optimize() { return this; }
  }
}
