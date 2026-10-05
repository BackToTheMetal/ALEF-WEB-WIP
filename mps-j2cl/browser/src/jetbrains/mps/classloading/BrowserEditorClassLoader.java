package jetbrains.mps.classloading;

import java.util.Collections;
import jetbrains.mps.project.ModuleId;
import jetbrains.mps.project.structure.modules.ModuleReference;
import jetbrains.mps.reloading.CompositeClassPathItem;

/** Creates a compiled-code registry without desktop classpath or filesystem entries. */
public final class BrowserEditorClassLoader {
  private BrowserEditorClassLoader() {}
  public static ModuleClassLoader create() {
    return new ModuleClassLoader("Browser editor selections", ClassLoader.getSystemClassLoader(),
        new ModuleReference("mpsj2cl.editor", ModuleId.fromString("19e2f03c-b96a-4cfb-a561-5093daa42c3c")),
        new CompositeClassPathItem(), Collections::emptyList);
  }
}
