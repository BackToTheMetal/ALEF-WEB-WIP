/*
 * Copyright 2003-2026 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jetbrains.mps.classloading;

import jetbrains.mps.reloading.IClassPathItem;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.mps.openapi.module.SModuleReference;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

/**
 * MPS module-first delegation over explicitly registered J2CL classes/resources.
 * JVM classpath entries are diagnostic only: bytecode is never defined in JS.
 * A new module loader and registrations represent redeployment of compiled code.
 */
public final class ModuleClassLoader extends MPSModuleClassLoader {
  private static final Map<Class<?>, ModuleClassLoader> FACTORY_OWNERS = new HashMap<>();

  private final IClassPathItem myClassPathItem;
  private final SModuleReference myModule;
  private Supplier<List<ClassLoader>> myDependencySupplier;
  private List<ClassLoader> myDependenciesClassLoaders;
  private final Map<String, Class<?>> myClasses = new HashMap<>();
  private final Set<String> myLoading = new HashSet<>();
  private final Set<String> myResourceLookups = new HashSet<>();
  private final Map<Class<?>, Map<List<Class<?>>, RegisteredFactory<?>>> myFactories = new HashMap<>();

  ModuleClassLoader(@NotNull String clName, @NotNull ClassLoader parent, @NotNull SModuleReference module,
                    @NotNull IClassPathItem cp, @NotNull Supplier<List<ClassLoader>> dependencies) {
    super(clName, parent);
    myModule = Objects.requireNonNull(module, "module");
    myClassPathItem = Objects.requireNonNull(cp, "classPath");
    myDependencySupplier = Objects.requireNonNull(dependencies, "dependencies");
  }

  @Override
  @NotNull
  public Class<?> loadOwnClass(String name) throws ClassNotFoundException {
    checkNotDisposed();
    Class<?> type = loadFromSelf(name);
    if (type == null) {
      throw createCLNFException(name, null);
    }
    return type;
  }

  @NotNull
  @Override
  public SModuleReference getModule() {
    return myModule;
  }

  @Override
  public void registerClass(String binaryName, Class<?> type) {
    checkNotDisposed();
    if (Objects.requireNonNull(binaryName, "binaryName").startsWith("java.")) {
      throw new IllegalArgumentException("JRE classes belong to the system registry: " + binaryName);
    }
    Class<?> cached = myClasses.get(binaryName);
    if (cached != null && cached != type) {
      throw new IllegalStateException("Class name already resolved through a dependency: " + binaryName);
    }
    super.registerClass(binaryName, type);
  }

  @Override
  protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
    try {
      checkNotDisposed();
      Objects.requireNonNull(name, "name");
      if (name.startsWith("java.")) {
        return getSystemClassLoader().loadClass(name);
      }
      Class<?> type = myClasses.get(name);
      if (type == null) {
        type = loadFromSelf(name);
      }
      if (type == null) {
        if (!myLoading.add(name)) {
          throw createCLNFException(name, null);
        }
        try {
          type = loadFromDependencies(name);
        } finally {
          myLoading.remove(name);
        }
      }
      if (type == null) {
        throw createCLNFException(name, null);
      }
      myClasses.put(name, type);
      if (resolve) {
        resolveClass(type);
      }
      return type;
    } catch (ModuleClassLoaderIsDisposedException disposed) {
      throw createCLNFException(name, disposed);
    }
  }

  private Class<?> loadFromSelf(String name) {
    checkNotDisposed();
    Class<?> type = findRegisteredClass(name);
    if (type == null && myClassPathItem.hasClass(name)) {
      throw new UnsupportedOperationException("ModuleClassLoader cannot execute JVM bytecode for " + name
          + "; register its J2CL-compiled Class before loading");
    }
    return type;
  }

  private Class<?> loadFromDependencies(String name) throws ClassNotFoundException {
    List<ClassLoader> dependencies = getDependencyClassLoaders();
    // MPS module dependencies supply only their own classes, preventing cyclic
    // module graphs from recursively expanding the same dependency closure.
    for (ClassLoader dependency : dependencies) {
      if (dependency instanceof ModuleClassLoader) {
        Class<?> type = ((ModuleClassLoader) dependency).loadFromSelf(name);
        if (type != null) {
          return type;
        }
      }
    }
    for (ClassLoader dependency : dependencies) {
      if (!(dependency instanceof ModuleClassLoader)) {
        try {
          return dependency.loadClass(name);
        } catch (ClassNotFoundException ignored) {
        }
      }
    }
    ClassLoader parent = getParent();
    if (parent != null && !dependencies.contains(parent)) {
      try {
        return parent.loadClass(name);
      } catch (ClassNotFoundException ignored) {
      }
    }
    return null;
  }

  private ModuleClassNotFoundException createCLNFException(String name, @Nullable Throwable cause) {
    return new ModuleClassNotFoundException(myModule,
        "Unable to load class: '" + name + "' using ModuleClassLoader of the module '" + myModule.getModuleName() + "'", cause);
  }

  @Nullable
  @Override
  public URL getResource(@NonNls String name) {
    checkNotDisposed();
    Objects.requireNonNull(name, "name");
    if (!myResourceLookups.add(name)) {
      return null;
    }
    try {
      URL resource = findResource(name);
      return resource != null || getParent() == null ? resource : getParent().getResource(name);
    } finally {
      myResourceLookups.remove(name);
    }
  }

  @NotNull
  @Override
  public Enumeration<URL> getResources(@NonNls String name) throws IOException {
    checkNotDisposed();
    Objects.requireNonNull(name, "name");
    if (!myResourceLookups.add(name)) {
      return Collections.emptyEnumeration();
    }
    try {
      List<URL> resources = new ArrayList<>();
      append(resources, findResources(name));
      if (getParent() != null) {
        append(resources, getParent().getResources(name));
      }
      return Collections.enumeration(resources);
    } finally {
      myResourceLookups.remove(name);
    }
  }

  @Nullable
  public URL getOwnResource(String name) {
    checkNotDisposed();
    return super.findResource(name);
  }

  @Override
  protected URL findResource(String name) {
    URL resource = getOwnResource(name);
    if (resource != null) {
      return resource;
    }
    for (ClassLoader dependency : getDependencyClassLoaders()) {
      resource = dependency instanceof ModuleClassLoader
          ? ((ModuleClassLoader) dependency).getOwnResource(name) : dependency.getResource(name);
      if (resource != null) {
        return resource;
      }
    }
    return null;
  }

  @Override
  protected Enumeration<URL> findResources(String name) throws IOException {
    checkNotDisposed();
    List<URL> resources = new ArrayList<>();
    append(resources, super.findResources(name));
    for (ClassLoader dependency : getDependencyClassLoaders()) {
      if (dependency instanceof ModuleClassLoader) {
        append(resources, ((ModuleClassLoader) dependency).ownResources(name));
      } else {
        append(resources, dependency.getResources(name));
      }
    }
    return Collections.enumeration(resources);
  }

  private Enumeration<URL> ownResources(String name) throws IOException {
    checkNotDisposed();
    return super.findResources(name);
  }

  private static void append(List<URL> result, Enumeration<URL> values) {
    while (values.hasMoreElements()) {
      result.add(values.nextElement());
    }
  }

  private List<ClassLoader> getDependencyClassLoaders() {
    checkNotDisposed();
    if (myDependenciesClassLoaders == null) {
      List<ClassLoader> supplied = Objects.requireNonNull(myDependencySupplier.get(), "dependency list");
      List<ClassLoader> dependencies = new ArrayList<>();
      for (ClassLoader dependency : supplied) {
        Objects.requireNonNull(dependency, "dependency loader");
        if (dependency != this && !dependencies.contains(dependency)) {
          dependencies.add(dependency);
        }
      }
      myDependenciesClassLoaders = dependencies;
      myDependencySupplier = null;
    }
    return myDependenciesClassLoaders;
  }

  /** A typed construction callback, supplied when generated runtime code is registered. */
  @FunctionalInterface
  public interface Factory<T> {
    T create(Object[] arguments) throws Exception;
  }

  public <T> void registerFactory(String binaryName, Class<T> implementation, Class<?>[] parameterTypes,
                                  Factory<? extends T> factory) {
    checkNotDisposed();
    Objects.requireNonNull(implementation, "implementation");
    Objects.requireNonNull(factory, "factory");
    List<Class<?>> signature = signature(parameterTypes);
    ModuleClassLoader owner = FACTORY_OWNERS.get(implementation);
    if (owner != null && owner != this) {
      throw new IllegalStateException("The compiled class already has a factory owner: " + binaryName);
    }
    Map<List<Class<?>>, RegisteredFactory<?>> factories = myFactories.get(implementation);
    if (factories != null && factories.containsKey(signature)) {
      throw new IllegalStateException("Factory signature already registered: " + binaryName);
    }
    registerClass(binaryName, implementation);
    if (factories == null) {
      factories = new HashMap<>();
      myFactories.put(implementation, factories);
    }
    factories.put(signature, new RegisteredFactory<>(binaryName, implementation, signature, factory));
    FACTORY_OWNERS.put(implementation, this);
  }

  public static boolean hasFactory(Class<?> implementation, Class<?>... parameterTypes) {
    Objects.requireNonNull(implementation, "implementation");
    List<Class<?>> signature = signature(parameterTypes);
    ModuleClassLoader owner = FACTORY_OWNERS.get(implementation);
    return owner != null && !owner.isDisposed() && owner.myFactories.get(implementation).containsKey(signature);
  }

  /** A deployed binary alias remains valid even when J2CL strips Class names. */
  public static String getRegisteredClassName(Class<?> implementation) {
    Objects.requireNonNull(implementation, "implementation");
    ModuleClassLoader owner = FACTORY_OWNERS.get(implementation);
    if (owner == null || owner.isDisposed()) {
      throw new IllegalStateException("No live constructor factory owns this compiled class");
    }
    for (RegisteredFactory<?> factory : owner.myFactories.get(implementation).values()) {
      return factory.myBinaryName;
    }
    throw new IllegalStateException("Compiled class has no registered binary alias");
  }

  public static <T> T newInstance(Class<?> implementation, Class<T> expectedType) throws Exception {
    return newInstance(implementation, expectedType, new Class<?>[0]);
  }

  public static <T> T newInstance(Class<?> implementation, Class<T> expectedType,
                                  Class<?>[] parameterTypes, Object... arguments) throws Exception {
    Objects.requireNonNull(implementation, "implementation");
    Objects.requireNonNull(expectedType, "expectedType");
    List<Class<?>> signature = signature(parameterTypes);
    Objects.requireNonNull(arguments, "arguments");
    ModuleClassLoader owner = FACTORY_OWNERS.get(implementation);
    if (owner == null) {
      throw new FactoryNotFoundException("No factory registered for the supplied compiled class");
    }
    owner.checkNotDisposed();
    RegisteredFactory<?> factory = owner.myFactories.get(implementation).get(signature);
    if (factory == null) {
      throw new FactoryNotFoundException("No factory registered for the supplied constructor parameter types");
    }
    if (!expectedType.isAssignableFrom(implementation)) {
      throw new ClassCastException("Registered class is incompatible with the requested result type: " + factory.myBinaryName);
    }
    Object value = factory.create(Arrays.copyOf(arguments, arguments.length));
    if (!expectedType.isInstance(value)) {
      throw new ClassCastException("Factory result is incompatible with the requested result type: " + factory.myBinaryName);
    }
    @SuppressWarnings("unchecked") T typed = (T) value;
    return typed;
  }

  public <T> T newInstance(String binaryName, Class<T> expectedType,
                           Class<?>[] parameterTypes, Object... arguments) throws Exception {
    return newInstance(loadClass(binaryName), expectedType, parameterTypes, arguments);
  }

  public <T> T newInstance(String binaryName, Class<T> expectedType) throws Exception {
    return newInstance(loadClass(binaryName), expectedType);
  }

  private static List<Class<?>> signature(Class<?>[] parameterTypes) {
    Objects.requireNonNull(parameterTypes, "parameterTypes");
    Class<?>[] copy = Arrays.copyOf(parameterTypes, parameterTypes.length);
    for (Class<?> parameter : copy) {
      Objects.requireNonNull(parameter, "parameter type");
      if (parameter == void.class) {
        throw new IllegalArgumentException("void is not a constructor parameter type");
      }
    }
    return Collections.unmodifiableList(Arrays.asList(copy));
  }

  private static boolean accepts(Class<?> parameter, Object value) {
    if (!parameter.isPrimitive()) {
      return value == null || parameter.isInstance(value);
    }
    if (parameter == boolean.class) return Boolean.class.isInstance(value);
    if (parameter == byte.class) return Byte.class.isInstance(value);
    if (parameter == short.class) return Short.class.isInstance(value);
    if (parameter == int.class) return Integer.class.isInstance(value);
    if (parameter == long.class) return Long.class.isInstance(value);
    if (parameter == float.class) return Float.class.isInstance(value);
    if (parameter == double.class) return Double.class.isInstance(value);
    if (parameter == char.class) return Character.class.isInstance(value);
    return false;
  }

  private static final class RegisteredFactory<T> {
    private final String myBinaryName;
    private final Class<T> myImplementation;
    private final List<Class<?>> myParameterTypes;
    private final Factory<? extends T> myFactory;

    private RegisteredFactory(String binaryName, Class<T> implementation, List<Class<?>> parameterTypes,
                              Factory<? extends T> factory) {
      myBinaryName = binaryName;
      myImplementation = implementation;
      myParameterTypes = parameterTypes;
      myFactory = factory;
    }

    private T create(Object[] arguments) throws Exception {
      if (arguments.length != myParameterTypes.size()) {
        throw new IllegalArgumentException("Wrong argument count for " + myBinaryName);
      }
      for (int i = 0; i < arguments.length; i++) {
        if (!accepts(myParameterTypes.get(i), arguments[i])) {
          throw new IllegalArgumentException("Incompatible argument " + i + " for " + myBinaryName);
        }
      }
      T value = myFactory.create(arguments);
      if (!myImplementation.isInstance(value)) {
        throw new ClassCastException("Factory returned an incompatible value for " + myBinaryName);
      }
      return value;
    }
  }

  public static final class FactoryNotFoundException extends ReflectiveOperationException {
    public FactoryNotFoundException(String message) {
      super(message);
    }
  }

  @Override
  public boolean isDisposed() {
    return super.isDisposed();
  }

  @Override
  public void dispose() {
    if (isDisposed()) {
      return;
    }
    for (Class<?> implementation : myFactories.keySet()) {
      FACTORY_OWNERS.remove(implementation);
    }
    myFactories.clear();
    myClasses.clear();
    myLoading.clear();
    myResourceLookups.clear();
    if (myDependenciesClassLoaders != null) {
      myDependenciesClassLoaders.clear();
    }
    myDependencySupplier = null;
    super.dispose();
  }

  public static final class ModuleClassLoaderIsDisposedException extends IllegalStateException {
    ModuleClassLoaderIsDisposedException(@NotNull SModuleReference module, @Nullable Throwable disposeTrace) {
      super("ClassLoader of the module '" + module.getModuleName() + "' is disposed and not operable!", disposeTrace);
    }

    @Nullable
    public Throwable getDisposeTrace() {
      return getCause();
    }
  }
}
