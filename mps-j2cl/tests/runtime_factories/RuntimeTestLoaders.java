package jetbrains.mps.classloading;

import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import jetbrains.mps.reloading.IClassPathItem;
import jetbrains.mps.reloading.RealClassPathItem;
import org.jetbrains.mps.openapi.module.SModule;
import org.jetbrains.mps.openapi.module.SModuleId;
import org.jetbrains.mps.openapi.module.SModuleReference;
import org.jetbrains.mps.openapi.module.SRepository;

/** Test values at the module/classpath boundary; uses the production loader. */
public final class RuntimeTestLoaders {
  private RuntimeTestLoaders() {}

  public static ModuleClassLoader create(String moduleName, List<ClassLoader> dependencies) {
    SModuleReference reference = new SModuleReference() {
      private final SModuleId id = new SModuleId() {};
      public SModuleId getModuleId() { return id; }
      public String getModuleName() { return moduleName; }
      public SModule resolve(SRepository repository) { throw new UnsupportedOperationException("Not used by runtime factory test"); }
    };
    IClassPathItem classPath = new IClassPathItem() {
      public boolean hasClass(String name) { return false; }
      public ClassBytes getClassBytes(String name) { throw new AssertionError("JVM bytecode must not be read"); }
      public URL getResource(String name) { throw new AssertionError("Resources must be registered in memory"); }
      public Enumeration<URL> getResources(String name) { throw new AssertionError("Resources must be registered in memory"); }
      public List<RealClassPathItem> flatten() { return Collections.emptyList(); }
      public IClassPathItem optimize() { return this; }
    };
    return new ModuleClassLoader(moduleName, ClassLoader.getSystemClassLoader(), reference, classPath, () -> dependencies);
  }
}
