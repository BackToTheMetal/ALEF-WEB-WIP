package jetbrains.mps.ide.icons;

/* Browser adaptation of MPS-generated Java; no model generation is implied. */

import com.intellij.compat.BrowserIcons;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.swing.Icon;
import org.jetbrains.mps.openapi.module.SModuleReference;
import jetbrains.mps.smodel.runtime.IconResource;

/** Explicit resource origins replace JVM Class package/classloader resource discovery. */
public class IconLoadingUtil {
  private static final Map<SModuleReference, String> MODULE_ROOTS = new HashMap<>();
  private static final Map<Class<?>, ProviderOrigin> PROVIDERS = new HashMap<>();
  private static long generation;

  /** iconRoot is the prefix used by BrowserIcons registrations, or empty for root resources. */
  public static void registerModuleOrigin(SModuleReference module, String iconRoot) {
    Objects.requireNonNull(module);
    String root = normalizeRoot(iconRoot);
    String previous = MODULE_ROOTS.putIfAbsent(module, root);
    if (previous != null && !previous.equals(root)) throw new IllegalStateException("Module icon root is already registered");
    if (previous == null) generation++;
  }

  /** packageName is declared by the build manifest; it is never inferred from Class metadata. */
  public static void registerProvider(Class<?> provider, SModuleReference module, String packageName) {
    Objects.requireNonNull(provider); Objects.requireNonNull(packageName);
    if (!MODULE_ROOTS.containsKey(module)) throw new IllegalStateException("Register the module icon root first");
    if (packageName.startsWith(".") || packageName.endsWith(".") || packageName.contains("..")
        || packageName.indexOf('/') >= 0 || packageName.indexOf('\\') >= 0) {
      throw new IllegalArgumentException("Expected a declared Java package name");
    }
    ProviderOrigin origin = new ProviderOrigin(module, packageName.replace('.', '/'));
    ProviderOrigin previous = PROVIDERS.get(provider);
    if (previous != null && (!previous.module.equals(module) || !previous.packagePath.equals(origin.packagePath))) {
      throw new IllegalStateException("Icon provider already belongs to another resource origin");
    }
    IconResource.registerProvider(provider, module.toString(), packageName);
    PROVIDERS.put(provider, origin);
    if (previous == null) generation++;
  }

  /** Release every Class identity belonging to an unloaded module. */
  public static void unregisterModuleOrigin(SModuleReference module) {
    boolean removed = MODULE_ROOTS.remove(module) != null;
    removed |= PROVIDERS.entrySet().removeIf(entry -> {
      if (!entry.getValue().module.equals(module)) return false;
      IconResource.unregisterProvider(entry.getKey());
      return true;
    });
    if (removed) generation++;
  }

  public static Icon loadIcon(String resource, Class loader) {
    Objects.requireNonNull(resource);
    ProviderOrigin origin = PROVIDERS.get(loader);
    if (resource.startsWith("/")) {
      return origin != null ? loadModuleIcon(resource, origin.module)
                            : loader == null ? BrowserIcons.findRegistered(resource) : null;
    }
    if (origin == null) return null;
    String path = origin.packagePath.isEmpty() ? "/" + resource : "/" + origin.packagePath + "/" + resource;
    return loadModuleIcon(path, origin.module);
  }

  public static Icon loadModuleIcon(String resource, SModuleReference module) {
    Objects.requireNonNull(resource);
    String root = MODULE_ROOTS.get(module);
    if (root == null) return null;
    return BrowserIcons.findRegistered(root + (resource.startsWith("/") ? resource : "/" + resource));
  }

  public static long getGeneration() { return generation + BrowserIcons.getGeneration(); }

  /** This browser profile uses the bundled classic MPS icon resources. */
  public static boolean isNewUIActive() { return false; }

  private static String normalizeRoot(String root) {
    Objects.requireNonNull(root);
    while (root.endsWith("/")) root = root.substring(0, root.length() - 1);
    if ((!root.isEmpty() && !root.startsWith("/")) || root.contains("..") || root.indexOf('\\') >= 0) {
      throw new IllegalArgumentException("Expected an absolute icon registration prefix");
    }
    return root;
  }

  private static final class ProviderOrigin {
    final SModuleReference module;
    final String packagePath;
    ProviderOrigin(SModuleReference module, String packagePath) { this.module = module; this.packagePath = packagePath; }
  }
}
