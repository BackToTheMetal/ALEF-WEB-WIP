# Browser runtime registration

The browser build compiles classes with J2CL before deployment. A
`ModuleClassLoader` can resolve only the explicit binary names registered with
that loader (or its dependency/parent loaders). A Java bytecode classpath is not
an implementation of browser loading.

Register the actual generated class and each supported construction callback
before MPS deployment listeners attempt to create its runtime. Registration
belongs to the module loader that owns the implementation. It must be repeated
for a replacement loader after disposal. The factory registry releases its
references when that loader is disposed.

```java
loader.registerFactory(
    "example.language.Language",
    example.language.Language.class,
    new Class<?>[0],
    arguments -> new example.language.Language());
```

Use literal binary names, including `$` for nested classes where applicable.
Do not derive registration keys from `Class.getName()`: class names can be
stripped from an optimized application. Registration adds the corresponding
class entry as well; `registerClass(name, type)` alone does not register a
constructor.

The MPS replacements request these exact signatures:

| Consumer | Name selected by MPS | Registered parameter types |
| --- | --- | --- |
| LanguageRegistry | `<language module name>.Language` | none |
| LanguageRegistry generator runtime | `<generator module name before #>.Generator` | `LanguageRegistry`, `LanguageRuntime`, `Generator`; otherwise legacy `LanguageRegistry`, `LanguageRuntime` |
| ExtensionRegistry | `<module namespace>.plugin.ExtensionDescriptor` | none |
| ModuleRuntime standard activator | `<module name>.ModuleActivator` | `ComponentHost` |
| BaseQuickFixProvider | explicitly supplied quick-fix binary name | none |

ExtensionRegistry retains its upstream special namespace mapping from
`jetbrains.mps.ide.refactoring.platform` to
`jetbrains.mps.refactoring.participant`. Generator selection prefers the
three-parameter signature even when both signatures are registered. A
three-parameter factory that fails does not fall back to the legacy factory.

For example, a generated module activator receives its real component host:

```java
loader.registerFactory(
    "example.runtime.ModuleActivator",
    example.runtime.ModuleActivator.class,
    new Class<?>[]{ComponentHost.class},
    arguments -> new example.runtime.ModuleActivator((ComponentHost) arguments[0]));
```

`ModuleClassLoader` checks the exact signature, argument count and argument
types before invoking a callback. It checks the returned instance against the
registered implementation class and requested result type. Reference arguments
may be null; primitive arguments require the matching boxed type. Callbacks may
throw checked exceptions. These are construction failures; a callback's own
`ClassNotFoundException` is not treated as an absent optional runtime.

An absent ModuleActivator class remains optional. A present activator with no
ComponentHost factory is an explicit error. An absent noarg language-runtime
factory follows MPS's missing-runtime warning path. Extension construction
failures retain the trace-and-skip behavior. Quick-fix class misses retain the
BadQuickFix fallback, while construction failures retain error logging and the
existing retry behavior.

Quick fixes resolve the language's already-deployed `ModuleRuntime` through
`LanguageRegistry.withModuleRuntime`; they do not infer a JVM defining loader
from a Class object. Therefore the language's module runtime must already be
registered before a quick fix is requested.

Resource registration uses `loader.registerResource(name, bytes)`, which copies
the bytes. `getResourceAsStream` and
`ClassLoader.openRegisteredResource(resourceUrl)` return fresh buffered streams.
The URLs identify registered memory only. `ModuleRuntime.getOwnResource`
preserves its own-module boundary and reports a missing resource with
FileNotFoundException; it does not call URL.openStream.

These APIs and construction paths are implemented. This change does not yet
generate or install a registration catalog for arbitrary MPS languages,
generators, plugins or quick fixes. A browser application must provide its
catalog for the concrete classes included in that application's build.

## Query callbacks and plugin ownership

`RootClassloaderLookup` returns the registered system loader unless an MPS
library owner supplies its plugin loader. `IDEADelegatingModuleClassLoader`
checks own class identity with `delegate.isOwnRegisteredClass(binaryName, type)`.
The method reads only the delegate's own registration map. A delegated class
cache entry or the same name bound to another actual class is not ownership.

Query methods have a separate explicit catalog. Register the actual declaring
class, unique method name, context type, result type and a direct callback
before constructing `QueryMethods`. For example:

```java
QueryMethods.RegisteredMethod<TemplateContext, Boolean> registration =
    QueryMethods.register(QueriesGenerated.class, "condition_123",
        TemplateContext.class, boolean.class, QueriesGenerated::condition_123);
```

`QueryMethods` snapshots the registrations at construction time, as upstream
snapshots declared methods. `getMethod(name)` and `hasMethod(name)` retain their
typed invocation and presence APIs. `getMethodPrim(name)` returns the browser
`RegisteredMethod<?, ?>` descriptor instead of `java.lang.reflect.Method`.
The initial checked-out MPS Java sources contain no callers of this raw method
API. Future code that names `Method` explicitly must use a browser adaptation;
method discovery, modifier introspection and arbitrary reflection are absent.

Each callback represents a public static method with one context argument.
Registration can refer only to code already linked into the browser build.
Invocation checks reference assignability, nulls and primitive unboxing with
widening; it rejects primitive narrowing and incompatible callback results.
The static receiver argument is ignored. Missing methods and wrong arguments
retain `IllegalQueryMethodException` with their real causes. An exception or
error from the callback retains its exact identity as the cause and target of
`InvocationTargetException`. These exception classes do not provide reflection.

Keep the registration handle with its module's lifecycle and close it when the
module is removed. Closing releases its callback, removes the registry entry,
and invalidates old descriptors and snapshots. Re-register before constructing
a new query collection after reload; old snapshots do not acquire the new
callback. Registration lifetime is explicit and is not automatically connected
to `ModuleClassLoader.dispose()`.
