# Third-party materials

Original WebAWT code uses [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0.txt), selected in PLAN.md and reproduced in LICENSE. NOTICE identifies the project; this does not replace dependency notices.

| Material | Scope | Notice / provenance |
| --- | --- | --- |
| J2CL 20260402 | Transpiler and Java emulation used by consumers | Apache-2.0; https://github.com/google/j2cl/blob/master/LICENSE |
| Elemental2 1.3.2 | Browser bindings used by consumers | Apache-2.0; https://github.com/google/elemental2/blob/master/LICENSE |
| Elemental2 build patch | Included source patch | Apache-2.0; ADR 0004 and release/provenance.json record the two build-only modifications |
| rules_closure 0.16.0 | Build tool | Apache-2.0; https://github.com/bazelbuild/rules_closure/blob/master/LICENSE |
| rules_jvm_external 6.7 | Development/test dependency resolver | Apache-2.0; https://github.com/bazel-contrib/rules_jvm_external/blob/master/LICENSE |
| DejaVu Sans | Included demonstration font | Full bundled license: samples/dual_showcase/fonts/LICENSE.txt; includes Bitstream copyright and public-domain DejaVu changes |
| Playwright, JUnit and transitive Maven artifacts | Tests only; not runtime dependencies | Exact coordinates and archive hashes are retained in rules_jvm_external++maven+webawt_test_maven_install.json and the release SBOM |

The source archive includes project code, the small build patch and demonstration font, not dependency binaries or browser installations. The generated SBOM records the resolved Bzlmod graph and locked Maven test artifacts. License metadata not established by the records above is left unasserted rather than guessed. Toolchain and browser downloads remain governed by their upstream notices.

`release/provenance.json` records the current evidence and the outstanding prepublication review from ADR 0003. Producing a candidate archive does not mark that review complete. Distributing compiled applications also requires reviewing the notices of the dependencies actually included in the output.
