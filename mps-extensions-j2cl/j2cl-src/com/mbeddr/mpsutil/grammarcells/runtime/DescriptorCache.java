package com.mbeddr.mpsutil.grammarcells.runtime;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import jetbrains.mps.components.ComponentHost;
import org.jetbrains.mps.openapi.module.SModule;
import org.jetbrains.mps.openapi.module.SModuleReference;

/** Compiled language descriptors are registered by the browser application. */
public class DescriptorCache {
  private static final DescriptorCache INSTANCE = new DescriptorCache();
  private final Map<SModuleReference, IGrammarActionsDescriptor> descriptors = new HashMap<>();

  public static DescriptorCache getInstance() { return INSTANCE; }

  public void register(SModuleReference module, IGrammarActionsDescriptor descriptor) {
    descriptors.put(Objects.requireNonNull(module), Objects.requireNonNull(descriptor));
    invalidate();
  }

  public void unregister(SModuleReference module) {
    descriptors.remove(module);
    invalidate();
  }

  public void invalidate() {
    // Registrations are the AOT source of truth, not a disposable classloader cache.
    RulesCache.invalidateAll();
    VisibleLanguagesCache.getInstance().invalidate();
    EditorHierachyCache.getInstance().invalidate();
  }

  public void init(ComponentHost platform) {}
  public void dispose() { descriptors.clear(); invalidate(); }
  public IGrammarActionsDescriptor getDescriptor(SModule module) { return doGetDescriptor(module); }
  public IGrammarActionsDescriptor getDescriptorByReference(SModuleReference module) {
    return module == null ? null : descriptors.get(module);
  }
  protected IGrammarActionsDescriptor doGetDescriptor(SModule module) {
    return module == null ? null : getDescriptorByReference(module.getModuleReference());
  }
}
