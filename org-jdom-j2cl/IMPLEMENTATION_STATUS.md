# Implementation status

The browser library is implemented. Full MPS application integration remains a separate gate; this repository has not modified the sibling MPS checkout or supplied its browser storage layer.

## Implemented

- Pinned source provenance, retained licenses, API declaration comparison and classified exclusions.
- Mutable JetBrains-compatible tree, collections, namespaces and cloning without Object.clone.
- XML output and strict portable byte encoders; fixed nonvalidating SAX parser, JDOM adapter and MPS helper facade.
- Explicit XPath/validation failures, absent desktop signatures, separate SAX type ownership.
- JVM differential harness, browser contracts, actual MPS memento/SAX consumer probes, XML9 reference/browser/reference fixture harness.
- Packaged source artifact, stable Bazel targets and independent consumer example.

## Verification

Run `scripts/Validate.ps1` to regenerate all reports in `out/`. The harness compares observable contracts against the version-matched JetBrains source, builds development and optimized JavaScript, runs Chromium/Firefox/WebKit, reloads 18 browser-produced XML9 documents through installed MPS, and builds/runs an independent consumer from the source artifact.

`compatibility/jdom-api.json` records 53 exported upstream types (48 present in the browser profile) and 1,194 effective public/protected member entries. This inventory includes ancestry, inherited methods, generic signatures and checked exception declarations. Inherited Java methods are identified as JRE-owned; JVM Object.wait/notify/notifyAll are explicitly excluded based on the pinned J2CL source. It classifies declaration differences; it does not claim every provided member has exhaustive behavioral coverage.

The MPS XML9 fixture oracle is installed MPS 2025.3, distinct from the MPS-Web checkout's newer source baseline. The model has one property-bearing root and metadata; its exported single-file/header/root documents exercise XML interoperability. Five real MPS source consumers execute in JavaScript. The full XML9 model handler executes in the reference JVM harness, not in JavaScript.

## Remaining MPS application gates

- Resolve the full member/dependency closure of enabled MPS/IntelliJ consumers. The 155-file scan is textual; unknown consumers remain unreviewed.
- Compile full XML9 persistence handlers/writers and browser data-source implementations together in MPS-Web; apply the utility substitutions in `integration/mps/README.md`.
- Validate file-per-root model assembly, multiple roots, containment/references/imports/loading states, complete module descriptors, generator/debug caches, and editor selection/folding state through actual MPS APIs.
- Integrate browser fetching/saving and application lifecycle. Publish the source artifact to a chosen registry/host only when that distribution target is selected.

These gates limit the claim of complete MPS migration. They do not require desktop stubs in the working XML runtime, and no unknown API has been classified unused to bypass them.
