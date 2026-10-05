package jetbrains.mps.classloading;

import java.util.Collections;
import jetbrains.mps.reloading.CompositeClassPathItem;
import org.jetbrains.mps.openapi.module.SModuleReference;

/** Per-language registry for the ahead-of-time compiled language classes and factories. */
public final class FinancialClassLoader {
  public static ModuleClassLoader create(SModuleReference module) {
    return new ModuleClassLoader(module.getModuleName(), ClassLoader.getSystemClassLoader(), module,
        new CompositeClassPathItem(), Collections::emptyList);
  }
}
