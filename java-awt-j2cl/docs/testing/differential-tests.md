# Differential test design

Portable compatibility behavior is expressed once in dependency-free scenario classes. Each scenario imports only the public AWT contract and stock J2CL JRE APIs.

The JVM target compiles the scenario against OpenJDK AWT and invokes it through JUnit 4. The J2CL target transpiles the identical scenario source against WebAWT and invokes it through a small J2CL JUnit adapter. Any assertion failure fails the corresponding Bazel test, and JVM failures retain normal JUnit method reporting for IntelliJ integration.

This structure prevents expected values from being implemented separately for the JVM and browser. JUnit 4 is a test-only Maven dependency; production targets do not depend on it. J2CL tests use J2CL's emulated JUnit support.

Every portable type should cover constructors, mutation, copying, equality, hashing, string representation, numeric boundaries and exceptional behavior where those behaviors are part of the public contract. Browser-only behavior belongs in a separate Elemental2 integration test and must not be placed in a differential scenario.

Curved-path scenarios additionally cover segment ordering, winding rules, append/connect semantics, float storage precision, transformed clones, flattening output, recursion limits, subdivision, real polynomial roots, implicit curve closure, and region queries. Assertions that are mathematically order-independent, such as polynomial roots, normalize their result order before comparison.

Run all current JVM and J2CL tests with:

```shell
bazelisk test //:all-tests
```

The complete validation matrix, including modern browsers and emitted-API checks, can be run from Windows PowerShell with:

```powershell
./tools/quality/Validate-WebAwt.ps1
```
