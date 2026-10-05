# ADR 0006: Project-owned prerequisites in unclaimed J2CL packages

## Status

Accepted for the stock-J2CL API-completeness program.

## Context

The audited AWT target contains seven feasible members whose signatures reference standard Java types absent from J2CL 20260402: `java.net.URI`, `java.net.URL`, and `java.text.BreakIterator`. J2CL does not own the relevant `java.net` package in the pinned source inventory, and WebAWT already owns the required subset of the otherwise unclaimed `java.text` package.

Omitting all seven members would remove useful browser mappings such as URI navigation, mail links, URL image loading, and explicit text line-breaking. Adding an external compatibility library would violate the production dependency policy and could introduce a second owner for Java runtime packages.

## Decision

WebAWT may implement the required subsets of `java.net.URI`, `java.net.URL`, and `java.text.BreakIterator` as project-owned compatibility code. The implementations must be original, dependency-free, included in the emitted-API and differential-test gates, and limited to behavior that can be implemented correctly under J2CL.

Only packages absent from the pinned J2CL JRE may be project-owned. WebAWT will not add or replace a class in a package owned by J2CL. A J2CL version update must rerun the package-ownership audit and fail the build if ownership has changed.

The prerequisite types are implemented in the phase that first consumes them: `BreakIterator` with text layout, and `URI`/`URL` with desktop navigation and URL-backed images. Their public contracts require a separate compatibility inventory and must not be represented as complete Java SE implementations unless their own tested surface supports that claim.

## Consequences

The seven AWT members remain in the feasible target denominator and are reported as requiring project-owned prerequisites until those types are implemented. No new runtime dependency is introduced. Swing prerequisites remain out of scope, while signatures involving J2CL-owned missing types such as `java.io.File` remain unavailable in the stock profile.
