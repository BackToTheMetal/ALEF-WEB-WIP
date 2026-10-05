# MPS XML compatibility for J2CL

A pure Java browser implementation of the JetBrains `org.jdom` API, plus the SAX event path used by MPS model loading. Runtime code uses J2CL's emulated Java library. It does not depend on a browser DOM, JsInterop, JAXP discovery, native code, or desktop I/O. JsInterop appears only in test/example entry points.

The tree and output code derives from IntelliJ `idea/262.8665.258`, commit `1822802cd32c2268237bc2734326fc8d51cf1f67`. This matches the IntelliJ dependency tag in the inspected MPS-Web checkout. See [source provenance](compatibility/upstream.json), [API inventory](compatibility/jdom-api.json), and [implementation status](IMPLEMENTATION_STATUS.md).

## Supported profile

| Area | Behavior |
| --- | --- |
| Tree | Elements, ordered attributes, namespaces, documents, text/CDATA, comments, PI, parent ownership, live filtered collections, detach and deep clone. |
| Output | `XMLOutputter`, `Format`, string/Writer/OutputStream overloads, raw/pretty/compact modes, escaping override hooks. |
| Input | `SAXBuilder`, Reader/InputStream/InputSource, namespace-aware XML 1.0, predefined/numeric character references, Unicode and line normalization. |
| SAX | Content, namespace, lexical, error and locator callbacks; callback exceptions propagate unchanged, including MPS header aborts. |
| Encoding | UTF-8, UTF-16 with BOM, UTF-16BE/LE, ISO-8859-1/Latin1, US-ASCII. Other encodings fail explicitly. |
| MPS helpers | `org.jdom.mps.MpsXml` supplies parser wiring and MPS-compatible output escaping. |

The default JetBrains `SAXHandler` discards comments and processing instructions, and its outputter omits these nodes within documents/elements, matching the pinned fork. To round-trip them, use `builder.setSAXHandlerFactory(PreservingSAXHandler::new)` and `new XMLOutputter(Format.getRawFormat().setPreserveMiscContent(true))`. These are explicit extensions; default behavior still matches JetBrains. CDATA is retained. A descriptor's significant text should use raw formatting or `xml:space="preserve"`; pretty formatting can trim text by design.

Preserving output validates processing-instruction targets and data before writing each instruction, throwing `IllegalDataException` for invalid names, the reserved target `xml`, invalid XML characters, or an embedded `?>`. Token limits count comment, CDATA, and PI payloads without their closing delimiters; delimiters still count toward the total input limit.

DTD parsing, external entities, schema validation, XML 1.1, and XPath evaluation are outside this profile. Unsupported requests throw rather than returning empty data. The legacy XPath/Jaxen engines and W3C `DOMBuilder` are excluded. `SAXBuilder.build(File/URL)` and corresponding engine/interface overloads are absent because those parameter types are not supplied by the pinned J2CL JRE. Java object serialization is not supported. [Full boundaries and stubs](compatibility/stubs.json).

`Element` subclasses must override the additional protected `newCloneInstance()` hook or `clone()`. `Attribute`, `Text`, `CDATA`, and `XMLOutputter` subclasses must implement their own clone when cloning is needed. This avoids silently slicing subtype state: J2CL does not implement JVM `Object.clone()`.

## Use from Java

```java
import java.io.StringReader;
import org.jdom.Document;
import org.jdom.input.SAXBuilder;
import org.jdom.mps.MpsXml;

Document document = new SAXBuilder().build(new StringReader(xmlText));
document.getRootElement().setAttribute("version", "9");
String saved = MpsXml.createOutputter().outputString(document);
```

For MPS SAX handlers:

```java
// handler is the existing MPS XMLSAXHandler, which extends DefaultHandler.
MpsXml.parse(new org.xml.sax.InputSource(new StringReader(xmlText)), handler);
```

Fetch/load data before calling the synchronous XML library, and save the returned bytes/text through the host's storage layer. A String passed to `SAXBuilder.build(String)` remains a system identifier, not inline XML, and fails because the library does not load resources. Use `StringReader` for XML text. Supplied readers/writers/streams remain caller-owned and are not closed.

The parser defaults to 16 Mi UTF-16 code units of input, 1 Mi code units per token, and 256 element levels. Configure another bound on `PortableXmlReader.setLimits(chars, token, depth)`, supplying it through `XMLReaderJDOMFactory` when using `SAXBuilder`. Reader parsing is incremental with a 4 KiB buffer; byte input is decoded in memory before events begin, with a separate byte bound of `4 * maxChars + 4`. Thus early SAX abort stops parsing but does not avoid reading an already supplied byte stream. Namespace processing is always enabled; namespace-prefix attribute reporting is optional. Parsers/builders and mutable trees are not thread-safe.

## Bazel dependency

Pinned versions: Bazel 8.3.1, J2CL 20260402, rules_closure 0.16.0, Java 21 build toolchain.

```starlark
# MODULE.bazel
bazel_dep(name = "org_jdom_j2cl", version = "0.1.0")
local_path_override(module_name = "org_jdom_j2cl", path = "../org-jdom-j2cl")
```

Depend on `@org_jdom_j2cl//:jdom`. Code that directly imports SAX should also declare `@org_jdom_j2cl//:sax-api`. The latter exclusively owns the supplied `org.xml.sax.*` and `javax.xml.XMLConstants` sources; do not combine it with another SAX emulation. JVM consumers compile `src/main/java` against the JDK SAX API and must exclude `src/sax/java`. [Type ownership](compatibility/type-ownership.json).

`node scripts/prepare-release.mjs` creates `out/dist/mps-xml-compat-0.1.0.tar.gz` and a SHA-256 file, then extracts it for the independent example. No registry publication is implied. An artifact host can replace the local override with an `archive_override` using that archive's URL and checksum. The runtime artifact contains no machine-specific dependency paths. It omits the repository's tests and scripts; the validation commands below apply to a full repository checkout.

## Validate

The PowerShell scripts assume a Java development kit, Node.js 22+, and the reference MPS 2025.3 installation. Override `-MpsHome` / `-JaxenJar` for another installation path. MPS 2025.3 is only the integration-fixture oracle; the JDOM differential oracle is the exact pinned source above.

```powershell
# Once, if out/upstream is absent; network required only for source reference recovery.
./scripts/Fetch-Upstream.ps1
./scripts/Test-Jvm.ps1
./scripts/Inventory-Api.ps1

# In Ubuntu/WSL, from the project directory:
npm ci
npx playwright install --with-deps chromium firefox webkit
flock /tmp/j2cl-build.lock bash -lc 'bazelisk build //tests:browser //tests:browser_dev --jobs=1 --local_test_jobs=1 --worker_max_instances=1 --worker_max_multiplex_instances=1; result=$?; bazelisk shutdown; exit "$result"'
npm run test:browser

# Back in PowerShell: reload all browser-produced fixtures through real MPS.
./scripts/Test-MpsReference.ps1
```

`scripts/Validate.ps1` orchestrates those checks and the separate packaged consumer, using WSL Ubuntu for Bazel/browser execution. Cold J2CL builds can take several minutes. Each WSL stage acquires `/tmp/j2cl-build.lock` with `flock` and holds it through server shutdown to coordinate with other projects. Builds run serially with a 1 GiB Bazel server heap and nonpersistent compiler processes; the script shuts each server down after its checks to release WSL memory. Browser results, reference observations, and build logs go under `out/`.

To regenerate vendored runtime sources after a deliberate upstream change, use `scripts/port-upstream.mjs`. It rewrites only transformed upstream files, preserving hand-written parser/adapters. `scripts/create-sax-api.mjs` regenerates the narrow SAX declarations. Neither generator is required to consume the checked-in runtime.

This library has not ported the entire MPS workbench. The [MPS integration guide](integration/mps/README.md) identifies remaining utility, storage, descriptor, cache, and editor integration work. Adding a consumer requires reviewing its member usage; the textual import scan alone never proves a feature unused.

This product includes software developed by the JDOM Project (http://www.jdom.org/). Upstream notices are retained in source and [third_party](third_party). MPS test sources retain their Apache-2.0 notices; these test sources are not part of the runtime artifact.
