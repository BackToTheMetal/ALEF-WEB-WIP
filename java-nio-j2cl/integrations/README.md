# Combining the NIO JRE overlay with WebAWT

Canonical WebAWT targets stock J2CL. This is an optional, separate integration profile.

The NIO JRE overlay includes the four canonical WebAWT `java.net` source files, because NIO signatures expose `URI` from inside `java.base`. Their source owner remains WebAWT; their compiled provider in this profile is the JRE. Applying WebAWT's ordinary `uri-j2cl` target as well would introduce competing definitions.

In an isolated WebAWT integration checkout or source archive, preserve the `uri-j2cl` target label and replace its `srcs` list with `[]`. This delegates the four classes to the patched JRE without changing downstream dependency labels. The optional `webawt-jre-uri.patch` expresses that build change; it has not been applied to the canonical repository.

The integration's ownership check must consider the emitted dependency graph, not count retained dormant source files as providers. Its API inventory must include the JRE header archive as the provider of the four types. Keep canonical WebAWT's source ownership/API gates unchanged. `check_sources.py` verifies unique providers in the NIO composed overlay; it is not a replacement for validating the full WebAWT integration graph.

The existing URI subset lacks general path/authority resolution and encoding behavior. `Path.of(URI)`, `Path.toUri()`, and related NIO operations are fail-fast stubs. No functional URI/path round-trip is promised.

Full WebAWT/MPS integration is not part of the passing NIO-only compatibility claim. Test the separate integration graph before shipping it.
