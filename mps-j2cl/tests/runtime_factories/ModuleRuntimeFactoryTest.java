package jetbrains.mps.smodel.runtime;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Collections;
import jetbrains.mps.classloading.ModuleClassLoader;
import jetbrains.mps.classloading.RuntimeTestLoaders;
import jetbrains.mps.components.ComponentHost;
import jetbrains.mps.components.CoreComponent;

/** Exercises the real standard activator selection, lifecycle and own-resource APIs. */
public final class ModuleRuntimeFactoryTest {
  private static int activations;
  private static int contributions;
  private static int deactivations;

  public static final class GoodActivator implements ModuleRuntime.Activator {
    private final ComponentHost host;
    private GoodActivator(ComponentHost host) { this.host = host; }
    public void activate() { activations++; }
    public void contribute(ModuleRuntime.ActivatorContext context) {
      contributions++;
      context.extension(String.class, ModuleRuntime.Extension.of("registered extension"));
    }
    public void deactivate() { deactivations++; }
  }

  public static final class UnregisteredActivator implements ModuleRuntime.Activator {}
  public static final class FailingActivator implements ModuleRuntime.Activator {}

  private ModuleRuntimeFactoryTest() {}

  public static String run() throws Exception {
    activations = contributions = deactivations = 0;
    ComponentHost host = new ComponentHost() {
      public <T extends CoreComponent> T findComponent(Class<T> componentClass) { return null; }
    };
    ModuleRuntime.ModuleRuntimeContext context = () -> host;
    ModuleClassLoader dependency = RuntimeTestLoaders.create("factory.dependency", Collections.emptyList());
    ModuleClassLoader loader = RuntimeTestLoaders.create("factory.test", Collections.singletonList(dependency));
    try {
      ModuleRuntime runtime = new ModuleRuntime(loader.getModule(), loader, ModuleRuntime.Flags.WithExtensions);
      ModuleRuntime.StandardActivatorFactory factory = new ModuleRuntime.StandardActivatorFactory();
      require(factory.newInstance(runtime, context) == null, "absent activator class remains optional");
      loader.registerFactory("factory.test.ModuleActivator", GoodActivator.class,
          new Class<?>[]{ComponentHost.class}, args -> new GoodActivator((ComponentHost) args[0]));
      GoodActivator direct = (GoodActivator) factory.newInstance(runtime, context);
      require(direct.host == host, "ComponentHost argument is passed to the registered factory");
      runtime.activate(context);
      require(activations == 1 && contributions == 1, "activation precedes one extension contribution");
      require(runtime.extensionsFor(String.class).count() == 1, "contributed extension is installed");
      final int[] visited = {0};
      runtime.forActivatorIfInstance(GoodActivator.class, instance -> {
        require(instance.host == host, "checked activator callback receives the created instance");
        visited[0]++;
      });
      runtime.forActivatorIfInstance(FailingActivator.class, instance -> { throw new AssertionError("wrong activator type"); });
      require(visited[0] == 1, "activator callback type filtering");
      runtime.deactivate(context);
      require(deactivations == 1 && runtime.extensionsFor(String.class).count() == 0, "deactivation clears installed extensions");
      runtime.forActivatorIfInstance(GoodActivator.class, instance -> { throw new AssertionError("disposed activator retained"); });

      ModuleRuntime noExtensions = new ModuleRuntime(loader.getModule(), loader, ModuleRuntime.Flags.NoExtensions);
      noExtensions.activate(context);
      require(activations == 2 && contributions == 1, "NoExtensions retains activation but skips contribution");
      noExtensions.deactivate(context);

      loader.registerResource("own.bin", new byte[]{6, 7});
      dependency.registerResource("dependency.bin", new byte[]{8});
      try (InputStream resource = runtime.getOwnResource("own.bin")) {
        require(resource.read() == 6 && resource.read() == 7 && resource.read() == -1, "own resource returns registered bytes");
      }
      boolean ownBoundary = false;
      try {
        runtime.getOwnResource("dependency.bin");
      } catch (FileNotFoundException expected) {
        ownBoundary = true;
      }
      require(ownBoundary, "own resource API does not search module dependencies");
    } finally {
      loader.dispose();
      dependency.dispose();
    }

    ModuleClassLoader missingFactory = RuntimeTestLoaders.create("factory.missing", Collections.emptyList());
    ModuleClassLoader failingFactory = RuntimeTestLoaders.create("factory.failure", Collections.emptyList());
    ModuleClassLoader wrongType = RuntimeTestLoaders.create("factory.wrong", Collections.emptyList());
    try {
      ModuleRuntime.StandardActivatorFactory factory = new ModuleRuntime.StandardActivatorFactory();
      missingFactory.registerClass("factory.missing.ModuleActivator", UnregisteredActivator.class);
      boolean unavailable = false;
      try {
        factory.newInstance(new ModuleRuntime(missingFactory.getModule(), missingFactory), context);
      } catch (IllegalStateException expected) {
        unavailable = expected.getMessage().contains("ComponentHost factory");
      }
      require(unavailable, "present activator without factory is an error, not an optional miss");

      final ClassNotFoundException constructorFailure = new ClassNotFoundException("failure inside constructor");
      failingFactory.registerFactory("factory.failure.ModuleActivator", FailingActivator.class,
          new Class<?>[]{ComponentHost.class}, args -> { throw constructorFailure; });
      boolean constructionFailure = false;
      try {
        factory.newInstance(new ModuleRuntime(failingFactory.getModule(), failingFactory), context);
      } catch (IllegalStateException expected) {
        constructionFailure = expected.getCause() == constructorFailure;
      }
      require(constructionFailure, "constructor CNFE remains a construction failure with its cause");

      wrongType.registerClass("factory.wrong.ModuleActivator", String.class);
      boolean incompatible = false;
      try {
        factory.newInstance(new ModuleRuntime(wrongType.getModule(), wrongType), context);
      } catch (IllegalStateException expected) {
        incompatible = expected.getMessage().contains("not instance of MR.Activator");
      }
      require(incompatible, "incompatible activator class is rejected");
    } finally {
      missingFactory.dispose();
      failingFactory.dispose();
      wrongType.dispose();
    }
    return "ModuleRuntime: factory selection/failures, activation, extension lifecycle, checked callbacks and own resources passed";
  }

  private static void require(boolean condition, String message) {
    if (!condition) throw new AssertionError(message);
  }
}
