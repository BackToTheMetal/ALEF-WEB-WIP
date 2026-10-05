# WalkingKooka java.time for J2CL

This workspace checks out and pins the 16 WalkingKooka repositories required by
[mP1/j2cl-java-time](https://github.com/mP1/j2cl-java-time). It builds their locale
annotation processors, shades the ThreeTen-derived implementation to `java.time`,
and integrates it into the **J2CL 20260402** JRE. Application code uses ordinary
`java.time` imports.

## Consume from another Bazel component

Extract `release/walkingkooka-java-time-0.1.0.tar.gz` into your component's
`third_party` directory, and copy its `patches/j2cl-java-time.patch` to your root
`third_party/patches/j2cl-java-time.patch`. Export that patch from a BUILD file.
Add this to the **consuming root's** `MODULE.bazel`:

```starlark
bazel_dep(name = "walkingkooka_java_time", version = "0.1.0")
local_path_override(
    module_name = "walkingkooka_java_time",
    path = "third_party/walkingkooka-java-time-0.1.0",
)
bazel_dep(name = "j2cl", version = "20260402")
single_version_override(
    module_name = "j2cl",
    version = "20260402",
    patches = ["//third_party/patches:j2cl-java-time.patch"],
    patch_strip = 1,
)
```

Add `@walkingkooka_java_time//:java-time-j2cl` to the consuming `j2cl_library`'s
dependencies. For a local checkout, use `C:/Codex/walkingkooka-j2cl` (Windows) or
`/mnt/c/Codex/walkingkooka-j2cl` (WSL) as the override path instead.

```java
import java.time.LocalDate;
import java.time.ZoneId;

LocalDate leapDay = LocalDate.of(2024, 2, 29);
String nextYear = leapDay.plusYears(1).toString(); // 2025-02-28
var midnight = leapDay.atStartOfDay(ZoneId.of("Europe/Amsterdam"));
```

Java platform types must have one provider inside the J2CL JRE. Bazel ignores
dependency-module overrides, so the consuming root must apply this patch.
If a component already patches J2CL (for example java-io-j2cl or java-nio-j2cl),
compose the overlays first: they can overlap in `java.io`, `java.util.Locale`,
and JRE BUILD changes. Do not apply conflicting definitions twice. This package
has not yet been validated together with those other JRE overlays.
Supporting WalkingKooka classes are also owned by this patched JRE; avoid adding
overlapping WalkingKooka runtime source sets to the same application.

## Build and validate

Use Ubuntu/WSL, JDK 21, Python 3, Git, Node/npm, and Bazelisk. Bazel 8.3.1 and
J2CL are pinned. All commands below run from this directory:

```sh
python3 tools/checkout.py       # fetch the exact upstream-lock.json revisions
python3 tools/prepare.py        # compile upstream processors; regenerate data
python3 tools/overlay.py        # regenerate the standalone JRE patch
npm ci
npx playwright install chromium firefox webkit
bash tools/validate.sh
```

On Windows, invoke `./tools/validate.ps1` after setup. Validation takes the shared
`/tmp/j2cl-build.lock` and builds with one job. It runs a JDK reference program,
compiles development and optimized JavaScript, compares browser results with
the reference, packages the library, and builds/runs a separate archive consumer.
The consumer test uses the packaged source target and a root-local copy of the
packaged patch. No upstream Maven snapshot server or Maven plugin is required.

The differential suite covers 307 cases: leap years, month ends, negative years,
date bounds, nanosecond precision, duration and period parsing/arithmetic,
offsets, clocks, ISO weeks, adjusters, numeric and English/Dutch text formatting,
strict parsing failures, two alternate chronologies, named zones, Amsterdam DST
gaps/overlaps, and 240 deterministic date arithmetic cases.
Machine-readable results go to `out/browser-results.json` and
`out/consumer-results.json`; the reference is `out/jdk-reference.txt`.
`release/validation.json` records the tested archive checksum and browser matrix.

## Metadata profile and scope

`profile.json` selects English (`en`, `en-US`, `en-GB`) and Dutch (`nl`, `nl-NL`),
default locale `en-US`, and seven zones: UTC, GMT, Europe/Amsterdam, Europe/London,
America/New_York, Australia/Sydney, and Asia/Tokyo. The default zone is UTC.
Fixed numeric offsets also work. Other region IDs are unavailable until included
in the profile and regenerated. `XXX` currency metadata supports formatting internals.
Regenerate and rerun validation after profile changes.

The upstream time-zone processor reads the build JDK's `lib/tzdb.dat`; locale
text also comes from that JDK. Generated data is included in the package, so
consumers do not regenerate it. `release/metadata.json` records the profile,
JDK, TZDB version/hash, and generated provider hashes. Changes to JDK/locale data
can change generated sources even at the same WalkingKooka commits.

This is the upstream ThreeTen-derived API subset, not full modern-JDK API parity.
Upstream limitations include Java serialization, ServiceLoader-discovered
chronologies, Hijrah deviation files, and some display-name methods. Newer JDK
methods are not implied by the package name. `ZoneRulesProvider.getVersions()`
is also unsupported by upstream's generated-data provider. Locale/text/calendar support is the
WalkingKooka subset needed by java.time. Browser tests establish the listed
contracts, not exhaustive equivalence for all dates, zones, locales, or calendars.

`upstream-lock.json` records Git URLs and exact revisions;
`release/source-manifest.json` maps shaded source files back to upstream sources.
The archive includes upstream license files (Apache-2.0 and ThreeTen BSD notices),
and source license headers are preserved. This is a local integration package;
it is not published to Maven or a public Bazel registry.

The integration preserves stock J2CL's `Locale.ENGLISH` and `Locale.US` constants,
applies the official GwtIncompatible stripper to the JRE bootclasspath sources,
and marks WalkingKooka's unused `Lists.copyOnWrite()` factory as JVM-only. The
java.time concurrency adapter already uses ordinary lists in the browser.
`overlay/java/text/ParseException.java` reuses the existing implementation from
the local `javax-swing-j2cl` component; its message and error-offset behavior are
covered by the `DateTimeFormatter.toFormat()` tests. If a consumer also provides
that class, remove the duplicate definition from its source set.
