# Browser formatting subset

The patched `String.format(String, Object...)`,
`String.format(Locale, String, Object...)` and `String.formatted(Object...)`
delegate to `javaemul.internal.MpsFormatter`. Print streams and writers can
use the same helper. This is a bounded implementation of the formatting used
by the initial MPS build, without `java.util.Formatter` or reflection.

A scan of the initial project source roots at MPS commit
`d2589ed55bbc9445b660769fcc17da97e8e395a6` found 238 source lines containing
`format`, `formatted` or `printf` calls. Reviewed conversions include `%s`,
`%d`, `%c`, `%x`, `%04x`, `%.3f`, `%tT` and `%b`. The boolean conversion occurs
in `ProgressTask`, `CLDebugHelper` and `CancellableReadsManager`; fixed-point
conversion is used for elapsed-time logging. `SModelId` uses padded hex.

| Supported feature | Contract |
| --- | --- |
| `%s`, `%S` | String value; uppercase uses ROOT case conversion. Custom `Formattable` dispatch is unavailable. |
| `%b`, `%B` | Boolean value; null is false; other non-null objects are true. |
| `%c`, `%C` | Character or valid Byte/Short/Integer Unicode code point, including supplementary code points. |
| `%d` | Byte, Short, Integer, Long or BigInteger decimal text. |
| `%x`, `%X` | Integral hexadecimal text; negative primitive values keep their Java 8/16/32/64-bit representation. BigInteger keeps its sign. |
| `%f` | Float, Double or BigDecimal; six decimal places by default, explicit precision supported, decimal HALF_UP rounding. Negative rounded zero, NaN and infinities are preserved. |
| `%tT`, `%TT` | `HH:mm:ss` from a valid Date or Long epoch-millisecond timestamp, using the runtime's local timezone. Long timestamps outside the browser Date range fail explicitly. |
| `%%`, `%n` | Literal percent and browser LF newline. |
| Width and flags | Space padding, `-` left alignment, `0` numeric padding with a required width. |
| Precision | String/boolean truncation and fixed-point decimal precision, including JVM-compatible null formatting. |
| Argument selection | Ordinary indexing, one-based `n$` explicit indexing, and `<` reuse of the preceding argument. |

ROOT, ENGLISH, US and null locale arguments are supported. The no-locale
overload uses deterministic ROOT punctuation. The pinned J2CL `Locale`
default is an opaque browser locale; it is not treated as if it were US.
Other locale objects fail with `UnsupportedOperationException`. There is no
localized decimal punctuation or grouping. `%n` is LF regardless of the host
that compiled the application; `%tT` uses the actual runtime local timezone.

Other conversions, date fields, grouping/sign/alternate-form flags and
unsupported option combinations fail with `UnsupportedOperationException`.
Malformed specifications, missing arguments and incompatible argument types
throw `IllegalArgumentException`; the full Formatter-specific exception
hierarchy is not supplied. Unused arguments are allowed and a null argument
array supplies null values, matching Java's Formatter behavior.

The canonical implementation lives in
`third_party/j2cl/api-support/jre/java/javaemul/internal/MpsFormatter.java`.
The canonical String source is copied from the exact J2CL 20260402 release,
with only the forwarding methods/import and the obsolete format TODO changed.
`tools/make_api_patch.py` records and diffs those sources with the other API
adaptations.

`python3 tools/check_formatting.py` verifies the committed JVM-generated
browser goldens and runs the shared helper against them. The 58 cases cover
integer extrema, negative hex widths, float/double extrema and subnormals,
rounding and negative zero, BigDecimal values, nulls, Unicode characters,
indices, date fields and unsupported-input failures. The JVM-only generator
is `tests/formatting/GenerateFormattingGolden.java`; exclude it from browser
sources. The browser entry is `tests/formatting/entry.js`, whose result is
`formattingResult`. Browser execution additionally verifies the patched
String forwarding methods against these independently generated values.
