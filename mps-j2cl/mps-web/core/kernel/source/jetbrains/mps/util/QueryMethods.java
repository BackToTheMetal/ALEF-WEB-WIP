/*
 * Copyright 2003-2017 JetBrains s.r.o.
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
package jetbrains.mps.util;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Browser support for generated, public static query methods with one context argument.
 * A linked application registers typed callbacks before constructing this collection.
 * Registrations use actual Class identities and explicit, unique method names.
 * No method discovery or JVM reflection is performed.
 */
public class QueryMethods {
  private static final Map<Class<?>, Map<String, RegisteredMethod<?, ?>>> REGISTRY = new HashMap<>();

  private final Class<?> myQueriesClass;
  private final Map<String, RegisteredMethod<?, ?>> myMethods;

  public QueryMethods(@NotNull Class<?> queriesClass) {
    myQueriesClass = Objects.requireNonNull(queriesClass, "queriesClass");
    Map<String, RegisteredMethod<?, ?>> methods = REGISTRY.get(queriesClass);
    // Keep the upstream construction-time snapshot boundary.
    myMethods = methods == null ? new HashMap<>() : new HashMap<>(methods);
  }

  /**
   * Registers a directly callable, public static query. The callback should be a
   * method reference or a lambda that calls the generated method directly.
   * Close the returned registration when its owning module is removed.
   */
  public static <C, T> RegisteredMethod<C, T> register(Class<?> queriesClass, String methodName,
                                                    Class<C> contextType, Class<T> returnType,
                                                    QueryBody<? super C, ? extends T> body) {
    Objects.requireNonNull(queriesClass, "queriesClass");
    Objects.requireNonNull(methodName, "methodName");
    Objects.requireNonNull(contextType, "contextType");
    Objects.requireNonNull(returnType, "returnType");
    Objects.requireNonNull(body, "body");
    if (methodName.isEmpty() || contextType == void.class) {
      throw new IllegalArgumentException("A query needs a method name and one non-void context parameter");
    }
    Map<String, RegisteredMethod<?, ?>> methods = REGISTRY.computeIfAbsent(queriesClass, unused -> new HashMap<>());
    if (methods.containsKey(methodName)) {
      throw new IllegalStateException("A query is already registered with method name '" + methodName + "'");
    }
    RegisteredMethod<C, T> method = new RegisteredMethod<>(queriesClass, methodName, contextType, returnType, body);
    methods.put(methodName, method);
    return method;
  }

  /** Browser API boundary: returns a registered callback descriptor, not java.lang.reflect.Method. */
  public RegisteredMethod<?, ?> getMethodPrim(String methodName) throws NoSuchMethodException {
    RegisteredMethod<?, ?> method = myMethods.get(methodName);
    if (method == null || !method.isActive()) {
      throw new NoSuchMethodException("couldn't find method '" + methodName + "' in '" + myQueriesClass.getName() + "'");
    }
    return method;
  }

  public boolean hasMethod(String methodName) {
    RegisteredMethod<?, ?> method = myMethods.get(methodName);
    return method != null && method.isActive();
  }

  @SuppressWarnings("unchecked")
  public <T> QueryMethod<T> getMethod(final String methodName) {
    return contextObject -> {
      try {
        return (T) getMethodPrim(methodName).invoke(null, contextObject);
      } catch (IllegalArgumentException | NoSuchMethodException e) {
        String message = "method invocation error: '" + methodName + "' in '" + myQueriesClass.getName() + "'";
        throw new IllegalQueryMethodException(message, e);
      }
    };
  }

  public interface QueryBody<C, T> {
    T invoke(C contextObject) throws Throwable;
  }

  /** Typed wrapper preserving the distinction between lookup/input errors and user-code failures. */
  public interface QueryMethod<T> {
    T invoke(Object contextObject) throws IllegalQueryMethodException, InvocationTargetException;
  }

  /**
   * A single registered static query and its registration lifetime. Closing a
   * registration also invalidates descriptors held by earlier QueryMethods snapshots.
   * It releases the callback and never removes a newer registration with the same name.
   */
  public static final class RegisteredMethod<C, T> implements AutoCloseable {
    private final Class<?> declaringClass;
    private final String name;
    private final Class<C> contextType;
    private final Class<T> returnType;
    private QueryBody<? super C, ? extends T> body;

    private RegisteredMethod(Class<?> declaringClass, String name, Class<C> contextType, Class<T> returnType,
                             QueryBody<? super C, ? extends T> body) {
      this.declaringClass = declaringClass;
      this.name = name;
      this.contextType = contextType;
      this.returnType = returnType;
      this.body = body;
    }

    public String getName() { return name; }
    public Class<?> getDeclaringClass() { return declaringClass; }
    public Class<C> getContextType() { return contextType; }
    public Class<T> getReturnType() { return returnType; }
    public boolean isActive() { return body != null; }

    /** The receiver is ignored for a static query; exactly one context argument is required. */
    @SuppressWarnings("unchecked")
    public T invoke(Object receiver, Object... arguments) throws InvocationTargetException {
      QueryBody<? super C, ? extends T> callback = body;
      if (callback == null) {
        throw new IllegalArgumentException("Query registration is closed: " + name);
      }
      if (arguments == null || arguments.length != 1) {
        throw new IllegalArgumentException("Query '" + name + "' requires exactly one context argument");
      }
      C context = (C) convertContext(contextType, arguments[0]);
      T result;
      try {
        result = callback.invoke(context);
      } catch (Throwable failure) {
        throw new InvocationTargetException(failure);
      }
      if (returnType == void.class) {
        if (result != null) {
          throw new IllegalArgumentException("A void query callback must return null: " + name);
        }
      } else if ((result == null && returnType.isPrimitive())
          || (result != null && !boxedType(returnType).isInstance(result))) {
        throw new IllegalArgumentException("Query callback returned a value incompatible with its registered result type: " + name);
      }
      return result;
    }

    @Override
    public void close() {
      if (body == null) {
        return;
      }
      body = null;
      Map<String, RegisteredMethod<?, ?>> methods = REGISTRY.get(declaringClass);
      if (methods != null && methods.get(name) == this) {
        methods.remove(name);
        if (methods.isEmpty()) {
          REGISTRY.remove(declaringClass);
        }
      }
    }
  }

  private static Object convertContext(Class<?> type, Object value) {
    if (!type.isPrimitive()) {
      if (value == null || type.isInstance(value)) {
        return value;
      }
    } else if (value != null) {
      // Method invocation permits unboxing followed by primitive widening, never narrowing.
      if (type == boolean.class && value instanceof Boolean
          || type == char.class && value instanceof Character
          || type == byte.class && value instanceof Byte) {
        return value;
      }
      if (type == short.class && (value instanceof Byte || value instanceof Short)) {
        return ((Number) value).shortValue();
      }
      if (value instanceof Character) {
        value = Integer.valueOf(((Character) value).charValue());
      }
      if (type == int.class && (value instanceof Byte || value instanceof Short || value instanceof Integer)) {
        return ((Number) value).intValue();
      }
      if (type == long.class && (value instanceof Byte || value instanceof Short || value instanceof Integer || value instanceof Long)) {
        return ((Number) value).longValue();
      }
      if (type == float.class && (value instanceof Byte || value instanceof Short || value instanceof Integer
          || value instanceof Long || value instanceof Float)) {
        return ((Number) value).floatValue();
      }
      if (type == double.class && (value instanceof Byte || value instanceof Short || value instanceof Integer
          || value instanceof Long || value instanceof Float || value instanceof Double)) {
        return ((Number) value).doubleValue();
      }
    }
    throw new IllegalArgumentException("Query context is incompatible with the registered parameter type");
  }

  private static Class<?> boxedType(Class<?> type) {
    if (type == boolean.class) return Boolean.class;
    if (type == byte.class) return Byte.class;
    if (type == short.class) return Short.class;
    if (type == char.class) return Character.class;
    if (type == int.class) return Integer.class;
    if (type == long.class) return Long.class;
    if (type == float.class) return Float.class;
    if (type == double.class) return Double.class;
    return type;
  }

  public static class IllegalQueryMethodException extends Exception {
    public IllegalQueryMethodException(String message, Throwable throwable) {
      super(message, throwable);
    }
  }
}
