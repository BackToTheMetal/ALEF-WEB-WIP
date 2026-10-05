# ADR 0003: Source provenance

## Status

Accepted for Phase 0.

## Context

OpenJDK AWT implementation sources and tests are generally GPLv2 with the Classpath Exception. Apache Harmony is permissively licensed but retired and built around desktop-native assumptions.

## Decision

The initial WebAWT implementation will be original Apache-2.0-compatible code. OpenJDK public signatures, documentation and black-box behavior may define the contract, but OpenJDK implementation bodies and tests will not be copied. Any future reuse from Apache Harmony or another source requires a per-file provenance record and preserved notices before the file enters the implementation branch.

## Consequences

Implementation work may take longer than mechanically adapting OpenJDK, but licensing remains clear and browser architecture is not constrained by desktop internals. Legal review remains required before publication.
