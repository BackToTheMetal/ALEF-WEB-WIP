# Compatibility boundary

The baseline is JetBrains Trove `1.0.20200330`, with 201 upstream source files
ported plus private portability helpers. Byte, int, long, float, double, object,
decorator, strategy, iterator, and procedure families
are included. This is not the separate Trove 2.1.0 bundled by MPS's collections
language, nor the Trove 3 package layout. Those artifacts must not be mixed on
the same classpath. Their migration needs a separate baseline.

The pinned MPS source/model dependency closure retains 203 files including two
helpers. `TIntStack`, `TLinkedList`, `TLinkable`, and `TLinkableAdaptor` are outside
that closure and removed. The API comparison covers retained types only;
see [the requirements report](../docs/mps-requirements.md). The 29 short-related
Trove 2.1.0 requirements absent from this baseline remain unsupported.

## Unavailable JVM capabilities

- Java object serialization is excluded. Private `readObject`/`writeObject`
  hooks and `SerializationProcedure` are removed. A retained `Serializable`
  marker does not provide serialization in JavaScript. Persist collection
  contents through the application's browser-compatible data format instead.
- J2CL does not implement `Object.clone()`. Concrete library collections have
  explicit copies of storage, capacity, strategy, and counters. Abstract hash
  bases declare abstract `clone()` methods. A direct subclass of an abstract
  hash base must implement cloning. A subclass of a concrete collection must
  override cloning when preserving its runtime subtype or additional fields
  matters. Automatic copying of arbitrary subclass state is not supported.
- Reflective typed-array creation is replaced with J2CL's `Arrays.copyOf`.
  No reflection, native JavaScript backend, custom JRE, JVM threading, or
  desktop service is a runtime dependency.

## Deliberate corrections to upstream defects

The differential suite covers the common valid behavior. The following cases
are independently asserted by `RegressionSuite`, because the original binary
does not implement them correctly:

| Case | Original behavior | Port behavior |
| --- | --- | --- |
| Primitive list `min`/`max` | Reads `_data[_pos]` repeatedly, outside live contents | Reads each live element |
| Object-to-primitive map clone | Inherits a clone that shares `_values` | Copies the value array |
| Primitive map/set retain | May compact backing arrays while scanning old indices | Defers compaction with `try/finally` |
| Primitive list bulk `set` | Copies list contents into the caller's source array | Copies the requested source slice into the list; supports overlapping storage |
| Primitive list shuffle | Excludes the current position from random selection | Permits every Fisher-Yates choice, including keeping the current position |
| Object-map key-view removal | Returns false when removing a key mapped to null | Reports whether the key was present, including custom equality strategies |
| Map-view bulk removal with aliased input | Can invalidate iteration through another view of the same map | Snapshots input before mutation; identical view removal clears directly |

The library preserves the pinned API's null-key restrictions, zero absent-value
results, primitive comparison rules, decorator return values, unspecified hash
iteration order, and supported mutation policies. It does not substitute the
contracts of newer Trove releases or `java.util.Map` for primitive APIs.

Float32 normalization occurs at primitive float method boundaries, bulk array
copies, adjustment operations, and callback results. This cannot make arbitrary
floating-point calculations performed by consumer code before calling the
library identical to the JVM: J2CL itself permits extra float precision.
The explicitly unchecked `getQuick`/`setQuick` APIs require valid indices.

## MPS evidence boundary

The selected revision's node-ID maps and identity strategy are compiled and
executed unchanged, with test-only model tokens at their boundary. This verifies
their Trove interactions, including 64-bit IDs and incremental updates. It does
not validate the full MPS model implementation, workbench, generated-language
ecosystem, or every retained IntelliJ utility. The source/model inventory is
lexical, with explicit generator-family expansion and library dependency closure;
it is not whole-program symbol resolution or a distribution-level binary audit.
No identity match to MPS's externally assembled `lib/trove.jar` is asserted.
