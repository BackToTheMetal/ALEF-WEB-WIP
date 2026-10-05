# ADR 0004: Backport the Elemental2 CSS extern fix

## Status

Accepted for Elemental2 1.3.2.

## Context

Elemental2 1.3.2 removes `StylePropertyMapReadonly` from the Closure Compiler CSS extern with a build-time patch. The Closure Compiler artifact selected by the current J2CL dependency graph already excludes that API, so the patch is detected as previously applied and the Elemental2 DOM target fails to build.

The current Elemental2 source has removed this obsolete patch, consumes `w3c_css.js` directly, and includes the `web_animations.js` extern now referenced by the Closure Compiler DOM externs.

## Decision

Keep the latest stable Elemental2 release, 1.3.2, and apply a pinned Bzlmod patch containing only the two upstream BUILD-file changes needed by the selected Closure Compiler externs. Do not modify generated Elemental2 Java sources or browser behavior.

Remove the override when a stable Elemental2 release containing the upstream change is adopted. Verify removal by building the optimized sample and running the DOM integration test.

## Consequences

The production dependency remains Elemental2 1.3.2, with one reviewable build-only patch. The patch is part of the dependency lock and must be included in license and provenance reviews.
