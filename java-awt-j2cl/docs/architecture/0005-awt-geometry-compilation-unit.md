# ADR 0005: Compile mutually recursive AWT geometry as one J2CL library

## Status

Accepted.

## Context

The public AWT geometry contracts are mutually recursive. `Shape` refers to `Rectangle`, `Rectangle2D`, `PathIterator`, and `AffineTransform`; `Rectangle` extends `Rectangle2D`; and the geometry implementations return AWT types. Splitting the Java packages into Bazel libraries therefore creates a dependency cycle even though the Java compiler accepts the sources as one compilation unit.

J2CL also requires that a source appear in only one `j2cl_library` within an application dependency graph.

## Decision

Compile public `java.awt` and `java.awt.geom` sources in the single `//src/main/java/java/awt:awt-j2cl` target. Keep Elemental2 implementations and other project-owned browser infrastructure in separate `org.webawt.internal` targets.

Do not introduce API-only placeholder jars or duplicate public sources to break cycles. A future split is allowed only where the resulting dependency graph is acyclic and each source retains one owner.

## Consequences

The public compatibility layer has one clear build owner and can preserve the actual Java type hierarchy. Changes to a geometry source invalidate the shared AWT compilation target, which is acceptable while the compatibility layer remains modest in size.
