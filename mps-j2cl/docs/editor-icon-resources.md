The browser icon adapter keeps MPS's icon selection, inheritance, constraints,
behavior features, and layer composition. `BrowserMpsAssets` installs the original
bundled image bytes with their measured dimensions. `MPSIcons` holds references
that follow asset registration and disposal. Missing dynamically requested image
resources return `null`, allowing MPS's existing default-icon behavior.

Browser language deployment must declare resource origins explicitly. Java Class
names, packages, annotations, and classloaders are not used to discover resource
paths, including when the build uses stripped Class metadata:

```java
IconLoadingUtil.registerModuleOrigin(moduleReference, "/my-language-bundle");
IconLoadingUtil.registerProvider(EditorAspect.class, moduleReference, "my.language.editor");
// Register actual image assets under /my-language-bundle/my/language/editor/...
// before constructing relative IconResource values from that provider.
```

The declaration supplies `IconResource`'s module and package metadata as well as
the icon loader's module root. Both legacy provider-based resources and modern
module-based resources retain their public MPS contracts. Modern resources are
resolved only while their real MPS module runtime is deployed.

On module unloading, call `IconLoadingUtil.unregisterModuleOrigin(moduleReference)`
and unregister that bundle's actual `BrowserIcons` assets. This releases provider
Class identities and invalidates cached resource lookups. Retained legacy
`IconResource` objects use real weak references. Relative construction without a
declared provider fails explicitly instead of guessing from stripped Class names.

`tests/editor_live/EditorResourceContracts.java` checks actual resource dimensions,
relative legacy and modern resolution, missing resources, unloading, cache
invalidation, and reloading in the live editor's three metadata/build modes.
`tests/smodel/IconResourceTest.java` separately checks metadata identity and cleanup.
These sources are contracts to execute; acceptance is recorded in the browser test
reports after compilation.

IntelliJ symbolic UI icons remain host-replaceable symbols in the compatibility
layer. They are distinct from the original MPS assets and dynamically requested
language image resources described here.
