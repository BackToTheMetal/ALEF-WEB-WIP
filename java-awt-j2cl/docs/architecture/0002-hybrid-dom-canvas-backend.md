# ADR 0002: Hybrid DOM and Canvas backend

## Status

Accepted for Phase 0.

## Context

Semantic HTML controls provide browser-native input, focus and accessibility behavior, while Canvas 2D provides the closest browser mapping for custom AWT painting.

## Decision

Standard components will use semantic DOM peers. AWT layout managers will calculate component bounds in Java and peers will apply them with CSS. Custom painting and `Graphics2D` will use Canvas 2D. Public `java.awt` classes will not expose Elemental2 types and will communicate through narrow internal browser ports.

## Consequences

The component model remains testable with JVM fakes, standard controls remain accessible, and custom drawing has a suitable backend. Exact native AWT appearance and operating-system window behavior are not goals.
