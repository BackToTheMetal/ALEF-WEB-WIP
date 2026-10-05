# Class type support

`third_party/patches/j2cl-type-support.patch` extends J2CL 20260402 with the ordinary Java
`Class.isInstance(Object)`, `Class.isAssignableFrom(Class<?>)`, `Class.cast(Object)`,
and `Class.asSubclass(Class<?>)` APIs. Apply it
with the root's J2CL override; composed consumers apply their IO/NIO patch first. It also implements both
`java.lang.reflect.Array.newInstance` overloads. It changes seven upstream
files: the JRE `Class`, `Constructor`, and reflective `Array` classes, native
bootstrap `Util` and VM `Arrays`, and two compiler classes.

The compiler records each Java type's direct superclass and interfaces as
constructor identities. The runtime walks these relations. The compiler emits each relation list in one
assignment, so Closure can remove it when the Class operations are unused;
incremental array mutations would unnecessarily retain the constructor graph. It does not infer
assignability by manufacturing an object, invoking constructors, comparing
class names, or calling an instance predicate on a prototype. The relation
metadata is separate from optional class-name metadata and remains needed when
Closure strips class names.

`Class.isInstance` obtains the object's Java `Class` and checks the same relation.
This uses J2CL's existing representation of Java arrays and native-backed Java
values such as String, Boolean, and Double. Array covariance is recursive and
includes the Object, Cloneable, and Serializable array supertypes. Primitive
classes, including void, are only assignable to themselves; boxing and numeric
widening are not assignability conversions here.

`Class.cast` returns an unchanged compatible reference or null and throws
ClassCastException for an incompatible non-null reference. MPS's collection and
component registry implementations use this API.
`Class.asSubclass` preserves the Class identity when the requested supertype is
assignable from it, otherwise throwing ClassCastException; a null supertype fails
with NullPointerException. The matrix checks all 4096 type pairs for this API too.

Reflective array creation passes the Class's retained leaf constructor and
dimension count to J2CL's existing array allocator. This preserves primitive
defaults, interface component types, Java array store checks, and unallocated
trailing dimensions when the component Class is itself an array. It validates
null/void/negative sizes and the JVM's 255 dimension limit without initializing
the component class. The constructor retention required by dynamic creation may
increase optimized bundle size; this is an explicit cost of supporting the API.
No target constructor or prototype is used as an example instance.

The compiler also passes each Java type's actual `$isInstance` function as a
direct reference into the retained metadata. Reflective arrays obtain their
element predicate from that metadata. This is necessary because Closure may
move or remove static constructor properties during optimization; a later
dynamic `constructor.$isInstance` lookup would then be undefined. The retained
function is J2CL's existing array-check predicate, including its interface and
native-backed value handling. Primitive arrays use their existing permissive
unboxed element predicate; primitive conversions and array type checks remain
in the normal J2CL paths. Optimized and STRIPPED tests must exercise stores into
reflectively created arrays, not just allocation and Class identity.

Native JsType and JsFunction literals are already erased by J2CL to shared
bootstrap Class proxies. This patch does not reconstruct distinct Java type
identities after that erasure. Queries that need their unavailable nominal
relations throw UnsupportedOperationException. Object queries and null instance
queries still have their ordinary result. Native array literals can already be
erased to Object[], so their original native identity cannot be recovered.
Ordinary Java functional interfaces and their lambdas retain their Java type
relations and are covered by the shared test.
`InteropBoundaryTest` is a separate browser-only test for these native identity
limits; it checks explicit rejection of nominal queries and reflective allocation,
while null casts and the Object supertype continue to work.

The exact release archive has SHA256
`d1419aecefec2727d3449f10a772d973ff5d3a7a7470367d1cfc2c782c7d6e66`.
The release's `BazelJ2clBuilder` target is a source-built `compiler_java_binary`
whose macro invokes `java_binary`; the patch therefore rebuilds the host
compiler used by the J2CL rule's `_j2cl_transpiler` executable attribute. Verify
the selected executable with Bazel aquery and verify `$setClassTypeRelations`
in development JavaScript output before accepting the result.

Regenerate and check without modifying the extracted release:

```sh
python3 tools/compatibility/make_type_support_patch.py --upstream .work/j2cl-20260402
python3 tools/compatibility/make_type_support_patch.py --upstream .work/j2cl-20260402 --check
git -C .work/j2cl-20260402 apply --check ../../third_party/patches/j2cl-type-support.patch
```

`src/test/runtime/class_support` contains a shared JVM/JavaScript matrix: 64 Class values,
31 instances, all pairwise Class assignments, all instance and cast tests, null argument
exceptions, ordinary lambdas, local/anonymous types, and constructor/class
initialization checks. Fixtures and the matrix belong in separate J2CL targets
so the test exercises metadata across compilation boundaries. Compare
`classSupportResult` with `jvm-expected.txt` after normalizing line endings; do
this for development and ADVANCED JavaScript, including STRIPPED metadata.
The matrix calls the actual Class APIs directly across Bazel library boundaries;
it does not test a substitute helper API. Its unchanged output also checks
`asSubclass` for all pairs and null arguments. The matrix also checks reflection-created arrays against real array Class literals.

Upstream references:

- [Class.java](https://github.com/google/j2cl/blob/v20260402/jre/java/java/lang/Class.java)
- [Metadata compiler pass](https://github.com/google/j2cl/blob/v20260402/transpiler/java/com/google/j2cl/transpiler/passes/ImplementClassMetadataViaConstructors.java)
- [Native bootstrap Util](https://github.com/google/j2cl/blob/v20260402/jre/java/javaemul/internal/nativebootstrap/Util.impl.java.js)
- [Java Class API](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/Class.html)
