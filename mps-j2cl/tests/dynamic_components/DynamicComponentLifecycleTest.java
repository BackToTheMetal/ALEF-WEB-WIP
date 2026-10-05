package mpsj2cl.dynamiccomponents;

import java.util.function.Supplier;
import jetbrains.mps.components.CoreComponent;
import jetbrains.mps.core.platform.DynamicComponentPlugin;
import jetbrains.mps.core.platform.DynamicComponentWarden;
import jetbrains.mps.core.platform.DynamicComponentWarden.Token;

/** Runs against unchanged MPS on the JVM and the actual native-weak browser overlay. */
public final class DynamicComponentLifecycleTest {
  interface Service extends CoreComponent {}
  interface Dependency extends CoreComponent {}

  static final class Component implements Service, Dependency {
    int initialized;
    int disposed;
    public void init() { initialized++; }
    public void dispose() { disposed++; }
  }

  public static String run() {
    DynamicComponentPlugin plugin = new DynamicComponentPlugin();
    plugin.init();
    DynamicComponentWarden warden = plugin.findComponent(DynamicComponentWarden.class);
    require(warden != null && plugin.findComponent(Service.class) == null, "empty plugin");
    Component first = new Component();
    Token firstToken = warden.publish(Service.class, first);
    require(plugin.findComponent(Service.class) == first && first.initialized == 1, "published instance initializes once");
    firstToken.discard();
    firstToken.discard();
    require(first.disposed == 1 && plugin.findComponent(Service.class) == null, "idempotent explicit discard");

    int[] factoryCalls = {0};
    Token canceled = warden.publish(Service.class, (Supplier<Service>) () -> { factoryCalls[0]++; return new Component(); });
    canceled.discard();
    require(plugin.findComponent(Service.class) == null && factoryCalls[0] == 0, "discarded factory never runs");
    Component fromFactory = new Component();
    Component secondFactory = new Component();
    Token factoryOne = warden.publish(Service.class, (Supplier<Service>) () -> { factoryCalls[0]++; return fromFactory; });
    Token factoryTwo = warden.publish(Service.class, (Supplier<Service>) () -> { factoryCalls[0]++; return secondFactory; });
    require(plugin.findComponent(Service.class) == fromFactory && factoryCalls[0] == 1, "first published factory wins");
    require(plugin.findComponent(Service.class) == fromFactory && factoryCalls[0] == 1, "factory instance is reused");
    factoryOne.discard();
    require(plugin.findComponent(Service.class) == secondFactory && factoryCalls[0] == 2, "next factory after discard");
    factoryTwo.discard();
    require(fromFactory.disposed == 1 && secondFactory.disposed == 1, "both factory components disposed");

    StringBuilder notifications = new StringBuilder();
    Token[] lateToken = {null};
    Token listenerOne = warden.whenAvailable(Service.class, value -> {
      notifications.append('A');
      lateToken[0] = warden.whenAvailable(Service.class, late -> notifications.append('I'));
    });
    Token listenerTwo = warden.whenAvailable(Service.class, value -> notifications.append('B'));
    Token discardedListener = warden.whenAvailable(Service.class, value -> notifications.append('X'));
    discardedListener.discard();
    Component observed = new Component();
    Token observedToken = warden.publish(Service.class, observed);
    require("AIB".contentEquals(notifications), "ordered once-only listeners and immediate reentrant listener");
    observedToken.discard();
    Component replacement = new Component();
    Token replacementToken = warden.publish(Service.class, replacement);
    require("AIB".contentEquals(notifications), "listeners are consumed after first notification");
    listenerOne.discard();
    listenerTwo.discard();
    lateToken[0].discard();
    replacementToken.discard();

    Component dependency = new Component();
    Token dependencyToken = warden.publish(Dependency.class, (Supplier<Dependency>) () -> dependency);
    Token outer = warden.publish(Service.class, (Supplier<Service>) () -> {
      require(plugin.findComponent(Dependency.class) == dependency, "factory can resolve another factory");
      return new Component();
    });
    require(plugin.findComponent(Service.class) != null && dependency.initialized == 1, "reentrant component initialization");
    Component publishedAtShutdown = (Component) plugin.findComponent(Service.class);
    plugin.dispose();
    require(dependency.disposed == 1 && publishedAtShutdown.disposed == 1, "plugin disposes active components");
    outer.discard();
    dependencyToken.discard();
    require(dependency.disposed == 1 && publishedAtShutdown.disposed == 1, "token discard after shutdown does not double-dispose");
    require(plugin.findComponent(Service.class) == null, "disposed plugin cannot instantiate");
    return "PASS";
  }

  /** Browser cleanup improvements are intentionally separate from the upstream oracle. */
  public static String runBrowserBoundaries() {
    DynamicComponentPlugin plugin = new DynamicComponentPlugin();
    plugin.init();
    DynamicComponentWarden warden = plugin.findComponent(DynamicComponentWarden.class);
    Component replaced = new Component();
    Component current = new Component();
    Token oldToken = warden.publish(Service.class, replaced);
    Token currentToken = warden.publish(Service.class, current);
    plugin.dispose();
    require(replaced.disposed == 1 && current.disposed == 1, "shutdown disposes replaced live registrations");
    oldToken.discard();
    currentToken.discard();
    require(replaced.disposed == 1 && current.disposed == 1, "shutdown releases token ownership once");
    boolean rejected = false;
    try {
      warden.publish(Service.class, (Supplier<Service>) Component::new);
    } catch (IllegalStateException expected) {
      rejected = true;
    }
    require(rejected, "disposed warden rejects new registrations");
    return "PASS";
  }

  static void require(boolean condition, String message) {
    if (!condition) throw new AssertionError(message);
  }

  public static void main(String[] args) {
    System.out.print(run());
  }
}
