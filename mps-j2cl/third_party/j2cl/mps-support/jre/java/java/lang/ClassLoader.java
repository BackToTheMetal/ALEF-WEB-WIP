package java.lang;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Closed-world browser loader for classes already compiled by J2CL.
 * Binary names are explicit registration keys and never derived from optional
 * Class name metadata. This class does not translate or execute JVM bytecode.
 */
public abstract class ClassLoader {
  private static int nextResourceId;
  private static final Map<URL, byte[]> RESOURCE_BYTES = new HashMap<>();
  private static final ClassLoader SYSTEM = new ClassLoader("browser-system", null) {};

  static {
    SYSTEM.registerClass("java.lang.Object", Object.class);
    SYSTEM.registerClass("java.lang.Class", Class.class);
    SYSTEM.registerClass("java.lang.String", String.class);
    SYSTEM.registerClass("java.lang.Number", Number.class);
    SYSTEM.registerClass("java.lang.Boolean", Boolean.class);
    SYSTEM.registerClass("java.lang.Byte", Byte.class);
    SYSTEM.registerClass("java.lang.Short", Short.class);
    SYSTEM.registerClass("java.lang.Integer", Integer.class);
    SYSTEM.registerClass("java.lang.Long", Long.class);
    SYSTEM.registerClass("java.lang.Float", Float.class);
    SYSTEM.registerClass("java.lang.Double", Double.class);
    SYSTEM.registerClass("java.lang.Character", Character.class);
    SYSTEM.registerClass("java.lang.Void", Void.class);
    SYSTEM.registerClass("java.lang.Enum", Enum.class);
    SYSTEM.registerClass("java.lang.Runnable", Runnable.class);
    SYSTEM.registerClass("java.lang.Throwable", Throwable.class);
    SYSTEM.registerClass("java.lang.Exception", Exception.class);
    SYSTEM.registerClass("java.lang.RuntimeException", RuntimeException.class);
    SYSTEM.registerClass("java.lang.Error", Error.class);
    SYSTEM.registerClass("java.lang.Cloneable", Cloneable.class);
    SYSTEM.registerClass("java.io.Serializable", java.io.Serializable.class);
  }

  private final String name;
  private final ClassLoader parent;
  private final Map<String, Class<?>> registeredClasses = new LinkedHashMap<>();
  private final Map<String, Class<?>> initiatedClasses = new HashMap<>();
  private final Map<String, List<URL>> registeredResources = new LinkedHashMap<>();
  private final Set<String> loading = new HashSet<>();

  protected ClassLoader() {
    this(null, SYSTEM);
  }

  protected ClassLoader(ClassLoader parent) {
    this(null, parent);
  }

  protected ClassLoader(String name, ClassLoader parent) {
    if (name != null && name.isEmpty()) {
      throw new IllegalArgumentException("ClassLoader name must not be empty");
    }
    this.name = name;
    this.parent = parent;
  }

  public String getName() {
    return name;
  }

  public final ClassLoader getParent() {
    return parent;
  }

  public static ClassLoader getSystemClassLoader() {
    return SYSTEM;
  }

  /** A registration binds one binary name to one actual, precompiled class. */
  public void registerClass(String binaryName, Class<?> type) {
    checkOpen();
    requireBinaryName(binaryName);
    Objects.requireNonNull(type, "type");
    if (type.isPrimitive() || type.isArray()) {
      throw new IllegalArgumentException("Register a named reference class: " + binaryName);
    }
    Class<?> previous = registeredClasses.get(binaryName);
    Class<?> initiated = initiatedClasses.get(binaryName);
    if ((previous != null && previous != type) || (initiated != null && initiated != type)) {
      throw new IllegalStateException("Class name already resolves to another class: " + binaryName);
    }
    registeredClasses.put(binaryName, type);
  }

  public Class<?> loadClass(String binaryName) throws ClassNotFoundException {
    return loadClass(binaryName, false);
  }

  protected Class<?> loadClass(String binaryName, boolean resolve) throws ClassNotFoundException {
    checkOpen();
    Objects.requireNonNull(binaryName, "binaryName");
    Class<?> type = findLoadedClass(binaryName);
    if (type != null) {
      return type;
    }
    if (!loading.add(binaryName)) {
      throw new ClassNotFoundException("Cyclic class delegation: " + binaryName);
    }
    try {
      if (parent != null) {
        try {
          type = parent.loadClass(binaryName);
        } catch (ClassNotFoundException ignored) {
          // An ordinary miss permits lookup in this loader's own registry.
        }
      }
      if (type == null) {
        type = findClass(binaryName);
      }
      initiatedClasses.put(binaryName, type);
      if (resolve) {
        resolveClass(type);
      }
      return type;
    } finally {
      loading.remove(binaryName);
    }
  }

  protected Class<?> findClass(String binaryName) throws ClassNotFoundException {
    Class<?> type = findRegisteredClass(binaryName);
    if (type == null) {
      throw new ClassNotFoundException(binaryName);
    }
    return type;
  }

  /** Own registration only; unlike findLoadedClass, this excludes delegation. */
  protected final Class<?> findRegisteredClass(String binaryName) {
    checkOpen();
    return registeredClasses.get(Objects.requireNonNull(binaryName, "binaryName"));
  }

  /** Tests explicit ownership without consulting the parent or a loaded-class cache. */
  public final boolean isOwnRegisteredClass(String binaryName, Class<?> type) {
    Objects.requireNonNull(type, "type");
    return findRegisteredClass(binaryName) == type;
  }

  protected final Class<?> findLoadedClass(String binaryName) {
    checkOpen();
    return initiatedClasses.get(Objects.requireNonNull(binaryName, "binaryName"));
  }

  protected final void resolveClass(Class<?> type) {
    checkOpen();
    Objects.requireNonNull(type, "type");
    // Registered Class objects already belong to the linked J2CL application.
  }

  protected Object getClassLoadingLock(String binaryName) {
    Objects.requireNonNull(binaryName, "binaryName");
    return this;
  }

  protected static boolean registerAsParallelCapable() {
    // The browser profile executes class lookup on one thread; no JVM locks exist.
    return true;
  }

  protected final Class<?> defineClass(String binaryName, byte[] bytes, int offset, int length) {
    throw new UnsupportedOperationException("ClassLoader.defineClass: JVM bytecode is unavailable; register a class compiled by J2CL");
  }

  protected final Class<?> defineClass(byte[] bytes, int offset, int length) {
    throw new UnsupportedOperationException("ClassLoader.defineClass: JVM bytecode is unavailable; register a class compiled by J2CL");
  }

  /**
   * Adds an ordered resource occurrence and returns an opaque memory identifier.
   * Its bytes are copied; use getResourceAsStream/openRegisteredResource to read.
   * URL.openStream, network requests and filesystem lookup are not implemented.
   */
  public URL registerResource(String resourceName, byte[] bytes) {
    checkOpen();
    Objects.requireNonNull(resourceName, "resourceName");
    Objects.requireNonNull(bytes, "bytes");
    URL url;
    try {
      url = new URL("mps-resource:" + (++nextResourceId));
    } catch (MalformedURLException impossible) {
      throw new IllegalStateException(impossible);
    }
    RESOURCE_BYTES.put(url, Arrays.copyOf(bytes, bytes.length));
    registeredResources.computeIfAbsent(resourceName, unused -> new ArrayList<>()).add(url);
    return url;
  }

  public URL getResource(String resourceName) {
    checkOpen();
    Objects.requireNonNull(resourceName, "resourceName");
    URL resource = parent == null ? null : parent.getResource(resourceName);
    return resource == null ? findResource(resourceName) : resource;
  }

  protected URL findResource(String resourceName) {
    checkOpen();
    List<URL> resources = registeredResources.get(Objects.requireNonNull(resourceName, "resourceName"));
    return resources == null || resources.isEmpty() ? null : resources.get(0);
  }

  public Enumeration<URL> getResources(String resourceName) throws IOException {
    checkOpen();
    Objects.requireNonNull(resourceName, "resourceName");
    List<URL> resources = new ArrayList<>();
    if (parent != null) {
      append(resources, parent.getResources(resourceName));
    }
    append(resources, findResources(resourceName));
    return Collections.enumeration(resources);
  }

  protected Enumeration<URL> findResources(String resourceName) throws IOException {
    checkOpen();
    List<URL> resources = registeredResources.get(Objects.requireNonNull(resourceName, "resourceName"));
    return Collections.enumeration(resources == null ? Collections.emptyList() : new ArrayList<>(resources));
  }

  public InputStream getResourceAsStream(String resourceName) {
    URL resource = getResource(resourceName);
    if (resource == null) {
      return null;
    }
    try {
      return openRegisteredResource(resource);
    } catch (IOException unavailable) {
      return null;
    }
  }

  public static URL getSystemResource(String resourceName) {
    return SYSTEM.getResource(resourceName);
  }

  public static Enumeration<URL> getSystemResources(String resourceName) throws IOException {
    return SYSTEM.getResources(resourceName);
  }

  public static InputStream getSystemResourceAsStream(String resourceName) {
    return SYSTEM.getResourceAsStream(resourceName);
  }

  public static InputStream openRegisteredResource(URL resource) throws IOException {
    byte[] bytes = RESOURCE_BYTES.get(Objects.requireNonNull(resource, "resource"));
    if (bytes == null) {
      throw new IOException("Resource is not registered or has been disposed: " + resource);
    }
    return new ByteArrayInputStream(bytes);
  }

  /** Lifecycle hook for module loaders; metadata such as name/parent stays usable. */
  protected void checkOpen() {
  }

  protected final void clearRegistrations() {
    for (List<URL> resources : registeredResources.values()) {
      for (URL resource : resources) {
        RESOURCE_BYTES.remove(resource);
      }
    }
    registeredResources.clear();
    registeredClasses.clear();
    initiatedClasses.clear();
    loading.clear();
  }

  private static void append(List<URL> destination, Enumeration<URL> values) {
    while (values.hasMoreElements()) {
      destination.add(values.nextElement());
    }
  }

  private static void requireBinaryName(String binaryName) {
    Objects.requireNonNull(binaryName, "binaryName");
    if (binaryName.isEmpty() || binaryName.indexOf('/') >= 0 || binaryName.indexOf('[') >= 0 || binaryName.indexOf(';') >= 0) {
      throw new IllegalArgumentException("Expected an explicit binary class name: " + binaryName);
    }
  }
}
