package mpsj2cl.resources;

import jetbrains.mps.smodel.runtime.IconResource;

/** Checks resource identity without annotation or package reflection. */
public final class IconResourceTest {
  private IconResourceTest() {
  }

  @SuppressWarnings("removal")
  public static String run() {
    IconResource.registerProvider(First.class, "module:first", "mpsj2cl.resources");
    IconResource.registerProvider(SameOrigin.class, "module:first", "mpsj2cl.resources");
    IconResource.registerProvider(Second.class, "module:second", "mpsj2cl.resources");
    IconResource legacy = new IconResource("icons/model.svg", First.class);
    require("/mpsj2cl/resources/icons/model.svg".equals(legacy.getResourceId()), "relative resource path");
    require(legacy.isLegacy() && legacy.getProvider() == First.class, "legacy class identity");
    require(legacy.getOriginModule(null) == null, "legacy origin remains absent");
    require("IconResource{ id='/mpsj2cl/resources/icons/model.svg' }".equals(legacy.toString()), "resource description");

    IconResource.registerModuleOrigin(First.class, "module:first");
    IconResource.registerModuleOrigin(First.class, "module:first");
    IconResource.registerModuleOrigin(SameOrigin.class, "module:first");
    IconResource.registerModuleOrigin(Second.class, "module:second");
    IconResource first = new IconResource("/icons/model.svg", "icons/new.svg", First.class);
    IconResource equivalent = new IconResource("/icons/model.svg", "icons/new.svg", SameOrigin.class);
    IconResource different = new IconResource("/icons/model.svg", "icons/new.svg", Second.class);
    require(!first.isLegacy() && first.getProvider() == null, "modern resource does not expose legacy provider");
    require("/icons/model.svg".equals(first.getResourceId(false)), "absolute path unchanged");
    require("/mpsj2cl/resources/icons/new.svg".equals(first.getResourceId(true)), "new UI relative path");
    require(first.equals(equivalent) && first.hashCode() == equivalent.hashCode(), "same module/resource identity");
    require(!first.equals(different), "module origin distinguishes matching resource paths");
    IconResource unregistered = new IconResource("/icons/model.svg", null, NeverRegistered.class);
    require(unregistered.getResourceId(false).equals(unregistered.getResourceId(true)), "new UI fallback");
    require(unregistered.getOriginModule(null) == null, "unregistered origin remains explicitly absent");
    try {
      IconResource.registerModuleOrigin(First.class, "module:conflict");
      throw new AssertionError("Conflicting origin was accepted");
    } catch (IllegalStateException expected) {
      require(first.equals(new IconResource("/icons/model.svg", "icons/new.svg", First.class)),
          "conflicting registration does not change the origin");
    }
    IconResource.unregisterProvider(First.class);
    IconResource.unregisterProvider(SameOrigin.class);
    IconResource.unregisterProvider(Second.class);
    try {
      new IconResource("icons/model.svg", First.class);
      throw new AssertionError("Unregistered relative icon guessed a package from Class metadata");
    } catch (IllegalStateException expected) {}
    IconResource.registerProvider(First.class, "module:reloaded", "new.resources");
    require("/new/resources/reloaded.svg".equals(new IconResource("reloaded.svg", First.class).getResourceId()),
        "reloading replaces released provider metadata");
    IconResource.unregisterProvider(First.class);
    return "icon-resources: qualified paths, legacy provider, explicit module identity, registration conflicts";
  }

  private static void require(boolean condition, String message) {
    if (!condition) {
      throw new AssertionError(message);
    }
  }

  private static final class First {
  }
  private static final class SameOrigin {
  }
  private static final class Second {
  }
  private static final class NeverRegistered {
  }
}
