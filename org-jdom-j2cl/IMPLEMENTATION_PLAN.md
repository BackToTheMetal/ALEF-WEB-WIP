# org.jdom for J2CL: implementation plan

Date: 2026-09-08. Status: the browser XML runtime and validation harness are implemented. See [implementation status](IMPLEMENTATION_STATUS.md) for completed work, evidence, and remaining MPS application integration gates. The analysis and proposed phases below are retained as the design baseline.

Build a source-compatible implementation of the **JetBrains `org.jdom` API**, with a working XML tree, parser, and serializer written against J2CL's emulated Java library. Prioritize the APIs used by MPS persistence, project descriptors, caches, and editor state. Keep unavailable desktop integrations and unused XML engines outside the working runtime; provide explicit failing stubs only where compilation requires them.

The initial acceptance profile proposed here is MPS XML9 model persistence, module/project descriptor XML, generator/debug caches, and editor mementos. This is an implementation starting point, not a claim that all MPS plugins, persistence versions, or the full workbench already fit that profile.

## 1. Evidence and compatibility baseline

The target workspace was empty. Analysis located the existing migration checkout at `C:\Work\MPS-Web`, revision `1ae803e4f8c52e470c94563059eca4b4b8397c8c`. Its tracked working tree was clean when checked. Its build pins J2CL `20260402`, Bazel `8.3.1`, and rules_closure `0.16.0`. Use this checkout as the provisional consumer baseline.

The scan found **234 import lines in 155 tracked Java/Kotlin files**, covering 21 distinct imported JDOM names:

| Imported name | Import lines | Consequence |
| --- | ---: | --- |
| `Element` | 137 | Highest priority: real mutable XML tree behavior. |
| `Document` | 38 | Root ownership, document construction, parsing and output. |
| `JDOMException` | 24 | Preserve error contracts and checked exception signatures. |
| `Attribute` | 8 | Ordered attributes and value access are required. |
| `output.XMLOutputter`, `output.Format` | 4 each | Serialization and formatting are working features. |
| `Text`, `input.SAXBuilder` | 3 each | Text content and in-memory XML parsing are required. |
| `Namespace`, `Content`, `CDATA`, `Comment`, `ProcessingInstruction`, `EntityRef` | 1 each | Direct imports occur in the XML-persistence sample; common XML node semantics also support parser fidelity. |
| `JDOMFactory`, `DefaultJDOMFactory` | 1 each | Direct factory imports occur in the build test reporter. |
| `filter2.AbstractFilter`, `filter2.Filter`, `filter2.Filters`, `xpath.XPathExpression`, `xpath.XPathFactory` | 1 each | Direct imports are in the IDEA project-maintenance tool. |

The 155 files comprise 58 in core, 39 in plugins, 36 in workbench, 7 in tools, 5 in editor, 5 in samples, 2 in jps, 2 in testbench, and 1 in IdeaPlugin. Counts include generated sources and tests; they are **not runtime reachability or a complete member inventory**. Qualified references are saved separately. IntelliJ dependency code, reflection, untracked files, and MPS model declarations need additional coverage analysis before final stub decisions.

Reproduce the textual inventory with:

```powershell
.\scripts\Inventory-Jdom.ps1 -MpsRoot C:\Work\MPS-Web
```

Results are in `analysis/mps-jdom-summary.json`, `analysis/mps-jdom-imports.txt`, and `analysis/mps-jdom-references.txt`.

The checkout's `.idea/libraries/org_jdom.xml` points to `lib/util-8.jar`, but that jar was not present there. As a secondary reference, public declarations were inspected from the installed MPS 2025.3, build `MPS-253.28294.432`, at `C:\Program Files\JetBrains\MPS 2025.3\lib\util-8.jar`. SHA-256: `5190cd0ffeb41bf0cbf9c4863f4e59ea17cfad1d1f47c250038f4e383d59262e`. The sampled declarations are in `analysis/installed-jdom-api-sample.txt`. This older jar is **not yet a version-matched oracle** for the source checkout.

JetBrains describes its fork as based on JDOM 2.0.x with compatibility restored for JDOM 1.1. Preserve `org.jdom`, including JetBrains additions and the distinction between `org.jdom.filter` and `org.jdom.filter2`; do not substitute `org.jdom2`. [JetBrains JDOM fork](https://github.com/JetBrains/intellij-deps-jdom)

## 2. What MPS actually needs

Paths below are relative to the inspected MPS checkout. These are representative verified call sites, not an exhaustive method list.

| MPS consumer | Observed dependency | Implementation requirement |
| --- | --- | --- |
| `core/persistence/source/jetbrains/mps/smodel/persistence/def/v9/ModelWriter9.java` | Builds `Element` trees, sets attributes, adds children, returns `Document`. | Real XML9 tree construction and deterministic serialization. |
| `core/persistence/source/jetbrains/mps/smodel/persistence/def/ModelPersistence.java` | Loads through SAX handlers; `parseAndHandleExceptions` invokes `JDOMUtil.createSAXParser().parse(...)`; header parsing deliberately throws `BreakParseSAXException`. | A working SAX event path in addition to JDOM. Preserve early termination and exception identity. |
| `core/persistence/source/jetbrains/mps/smodel/persistence/def/FilePerRootFormatUtil.java` | Reads a header and separate root streams using SAX; sorts root streams before assembling the model. | Cover both single-file and file-per-root XML9 using in-memory multi-stream data sources. |
| `core/kernel/source/jetbrains/mps/util/JDOMUtil.java` | `SAXBuilder.build(Reader/InputSource)`, entity resolver, `XMLOutputter.output(Document, Writer)`, pretty format, outputter subclass. | Compatible parser facade, configured formatting, and virtual escaping hooks. |
| `core/kernel/source/jetbrains/mps/persistence/PersistenceUtil.java` | Parses a byte stream, detaches the root, wraps an element in a document, serializes back to bytes. | In-memory byte input/output and correct parent ownership; a text-only facade alone is insufficient. |
| `core/kernel/source/jetbrains/mps/persistence/MementoUtil.java` | Iterates attributes and children; recursively reads/writes configuration. | Attribute names/values, ordered children, recursive structure. |
| `core/project/source_gen/jetbrains/mps/project/persistence/*DescriptorPersistence.java` | Reads attributes, child text, and child lists; reads/writes descriptor documents. | Real descriptor round trips, not just XML9 model files. |
| `core/persistence/source/jetbrains/mps/smodel/persistence/def/DefaultMetadataPersistence.java` | `getChild(...).getText()` for keys and values. | Preserve text exactly; do not globally trim XML content. |
| `core/generator/.../GenerationDependencies*.java`, `core/debuginfo/.../TraceInfoCache.java` | JDOM-based cache persistence. | Real XML read/write if generators and trace information are included. |
| `editor/editor-runtime/source/jetbrains/mps/nodeEditor/Memento.java`, `.../selection/SelectionInfoImpl.java` | Stores and restores editor state using attributes and child lists, including Java streams over children. | Real element collections for browser editor state. |
| `samples/xmlPersistence/.../XmlConverter.java` | Namespace and mixed-content conversion, including CDATA, comments, PI, entity references. | XML language/sample support needs more than element-only trees. |
| `tools/tools/source/jetbrains/mps/excluded/IDEAProject.java` | XPath over IDEA project/module metadata. | Exclude this maintenance tool initially; XPath may be stubbed only while this functionality is excluded. |

**Critical integration finding:** implementing `SAXBuilder` alone will not make MPS load models. Model reads bypass JDOM and use SAX directly, while model writes use JDOM. Both must share a working XML implementation or equivalent compatible behavior.

## 3. Runtime architecture and J2CL constraints

Use a pure-Java implementation that transpiles with J2CL. The core must not require native code, JVM parser providers, Xerces, JAXP provider discovery, reflection-based construction, class loaders, filesystem access, network access, or Java object serialization. A desktop JAR can serve as a test oracle, but importing it is not a browser implementation.

J2CL supplies a subset of Java, including useful in-memory I/O types such as `Reader`, `Writer`, `StringReader`, `StringWriter`, and byte-array streams. Reuse those rather than assuming all `java.io` is unavailable. The sibling WebAWT inventory for `20260402` confirms these classes and identifies gaps such as `File`; it is a local generated snapshot that must be regenerated and checked at member level in Phase 0. [J2CL JRE build](https://github.com/google/j2cl/blob/master/jre/java/BUILD), [J2CL I/O sources](https://github.com/google/j2cl/tree/master/jre/java/java/io)

Proposed dependency direction:

```text
J2CL JRE
  |-- org.jdom tree + serializer
  |-- portable XML tokenizer/parser + event interface
        |-- JDOM tree builder -> SAXBuilder facade
        |-- SAX compatibility adapter -> existing MPS XML handlers

MPS integration -> these libraries + separately owned storage adapters
```

- Keep the XML parser's event interface independent of both JDOM and SAX. This avoids circular dependencies and allows model loading without constructing a duplicate JDOM tree.
- Implement bounded, namespace-aware XML 1.0 parsing in Java. Prefer adapting an existing compatible parser after source and license review; otherwise implement this defined subset. Parsing must be a real tokenizer/state machine, not regular-expression extraction.
- Support in-memory `Reader` and `InputStream` entry points. Implement required byte decoding using verified J2CL operations or a small portable codec; do not assume `InputStreamReader` is supplied. Start with UTF-8; add UTF-16/BOM and other encodings only as required by the selected corpus/API contract. Reject unsupported encodings explicitly.
- Keep asynchronous fetching and saving outside JDOM. Load bytes into memory before entering synchronous MPS parsing. The library must never disguise a network operation as synchronous `build(...)`.
- A DOMParser/JsInterop backend is a possible later alternative, not a required dependency. The proposed implementation uses only transpiled Java and works without a browser document. It also supports SAX event positions and early termination without first constructing a browser DOM.
- Coordinate with `C:\Codex\java-io-j2cl` and `C:\Codex\java-nio-j2cl` for missing type ownership. These directories are not evidence of working I/O implementations; the NIO plan inspected here proposes stubs. Do not depend on stubbed streams/charsets in the working XML path or duplicate J2CL-owned classes.
- Keep missing `File`, `URL`, SAX, and JAXP signature dependencies explicit. A throwing method still needs its parameter types at compile time. Do not weaken signatures to `Object`, rely on dead-code removal to fix missing types, or define two versions of the same class in one build. If alternate source profiles are required, make them mutually exclusive and document their API differences.

## 4. Implement, defer, or stub

| API area | Decision for the initial profile | Required behavior |
| --- | --- | --- |
| `Element`, `Document`, `Content`, `Parent`, clone support | Implement | Constructors; ordered tree contents; child lookup; mutation; detach; deep clone; correct parent/document links. Match the pinned inheritance and covariant returns. |
| `Attribute`, attribute lists | Implement | Name/value access, ordered iteration, replacement by expanded name, removal, parent ownership; conversions and defaults for required members. |
| `Namespace`, name/character validation, JDOM exceptions | Implement | Prefix/URI lookup, namespace declarations, legal names/content, compatible failures. These enforce the correctness of implemented APIs even if rarely imported directly. |
| `Text`, `CDATA`, `Comment`, `ProcessingInstruction` | Implement | Preserve common mixed XML content through parse/edit/write; validate node syntax. Basic support is small relative to silent data loss. |
| `Element` collections and traversal | Implement used methods and their collection contracts | Live child/content/attribute views, filtering, iterator mutation, ordering, and parent updates. Add descendant/filter operations as the member inventory requires. |
| `Format`, `XMLOutputter` | Implement required API plus coherent raw/pretty modes | Document/element/string and writer/stream output; escaping; declaration/encoding/line-separator settings; whitespace preservation; cloning/configuration behavior. |
| `SAXBuilder` | Implement in-memory subset | `build(Reader)`, `build(InputStream)`, buffered `InputSource`, required constructors/settings. No parser-provider discovery. |
| SAX support used by MPS | Implement as an explicitly owned companion target | Input source, attributes, handlers, parse exceptions, locator, and parser calls used by the selected consumers; event engine is shared with JDOM building. |
| `JDOMFactory`, `DefaultJDOMFactory`, parser factory hooks | Audit then implement required subset | Tree construction can use a simple default factory; honor custom factories if selected consumers install them. No reflective factory loading. |
| `filter` / `filter2` | Preserve required declarations; implement reachable operations | They are different APIs in the JetBrains fork. Do not conflate them. Full optional filtering waits for evidence. |
| XPath/Jaxen | Exclude or fail explicitly | Currently observed direct XPath imports belong to an excluded maintenance tool. Reassess for IntelliJ dependencies and later plugins. |
| DTD validation, external entity loading, XSD validation | Unsupported initially | Reject unsupported validation or entity requests. Do not silently produce an incomplete document. |
| `DocType`, `EntityRef` | Signature/metadata support if needed; expansion deferred | Their presence must not imply a DTD engine. Arbitrary entity preservation is required only if the XML sample/profile is promoted; otherwise reject unsupported input. |
| W3C DOM, StAX, XSLT, SAX output bridges | Exclude or stub if signatures require them | No direct imports in this scan establish runtime need; dependency audit is still required. |
| `build(File)`, `build(URL)`, system-ID-only sources | Stub or route through a separate explicit storage boundary | Fail before attempting access. Preserve `build(String)` as a system-ID API; do not reinterpret it as literal XML text. Use `StringReader` or a clearly named new helper for XML strings. |
| Java object serialization | Unsupported | An emulated `Serializable` marker is not permission to depend on `ObjectInputStream`/`ObjectOutputStream`. |

Stub policy: retain exact targeted signatures, checked exceptions, generic bounds, constants, and inheritance. A callable unsupported operation throws `UnsupportedOperationException` with its fully qualified member and reason. Use the API's established checked exception for unsupported parse/input conditions where appropriate. Never return empty documents, `null`, successful writes, or empty XPath matches to imitate success. Value objects and exception constructors must remain usable; do not indiscriminately throw from every constructor or static initializer.

Unreferenced APIs can be omitted when they are outside the declared compatibility surface. APIs needed to compile excluded consumers may be stubbed in a separate compatibility target. **A low import count does not establish that an API is safe to stub.** An operation is eligible only after its callers and relevant behavior are excluded from the selected profile.

## 5. Behavioral contracts that cannot be simplified away

1. **Tree ownership:** a node/attribute cannot acquire two parents, content cannot form cycles, and documents obey root/content constraints. Detach and removal update both sides. Clones have independent mutable descendants and no original parent. Namespace objects may be shared according to their immutable contract.
2. **Lists:** `getChildren()`, filtered child lists, `getContent()`, and `getAttributes()` must follow the fork's live-view semantics. Mutations through lists, sublists, iterators, indexed operations, and bulk operations must validate types/ownership before leaving partial corruption. Verify actual supported operations against the pinned oracle.
3. **Text and namespaces:** distinguish direct text from descendant value; preserve whitespace unless the selected formatting operation says otherwise. Default namespaces do not automatically apply to unprefixed attributes. Namespace shadowing, expanded names, and escaping must survive round trips.
4. **Serialization hooks:** MPS `JDOMUtil.MyXMLOutputter` overrides `escapeAttributeEntities` and `escapeElementEntities`. The writer must dispatch through those overrides. In particular, attribute newlines, carriage returns, and tabs must survive save/reload. Do not bypass the subclass with an unrelated serializer.
5. **Formatting:** use an explicit browser line-separator policy instead of depending on `System.getProperty("line.separator")`. Test LF and CRLF as configured. Preserve attribute/child order, avoid destructive indentation in mixed content, and honor `xml:space`. Match byte formatting where MPS persistence or cache tests require it; otherwise compare semantic XML.
6. **Parsing:** handle declarations, escaped predefined entities, numeric references including supplementary Unicode, comments, CDATA, PI, namespace scopes, empty elements, line-ending and attribute normalization, and malformed XML. Reject duplicate attributes and invalid XML characters. Bound depth, token/input size, and entity behavior; never fetch external identifiers.
7. **SAX:** preserve callback order, qualified/local names under supported feature settings, attributes, character content, locator semantics, and early abort. Header parsing intentionally stops before the remainder of a document; do not turn this into a corruption error or require a complete DOM parse first. Verify whether declaration, namespace, and lexical callbacks are used by selected handlers.
8. **I/O contracts:** check read errors, close/flush behavior, charset errors, and ownership of supplied readers/writers against the oracle. MPS `JDOMUtil.writeDocument` closes its writer; do not accidentally move this behavior into every outputter method. MPS indexing may read an input twice: supply genuinely replayable memory streams, not fake `mark/reset` support.

## 6. Ordered implementation work

| Phase | Concrete work and deliverables | Exit criterion |
| --- | --- | --- |
| 0 — Freeze the target | Obtain the exact JDOM source/artifact for the MPS checkout; record version, checksums, license/NOTICE obligations. Resolve constructors, overloads, fields, protected hooks, inherited members, fully qualified uses, and dependency consumers. Regenerate J2CL member availability and assign one owner per missing type. Produce `compatibility/jdom-api.json`, `mps-jdom-usage.json`, and `type-ownership.json`. | Every selected member is classified as provided, implement, adapter, stub, or an explicit blocker; no unknown entry is labeled unused. |
| 1 — Prove the build boundary | Add pinned Bazel configuration and public consumer target, proposed `@org_jdom_j2cl//:jdom`. Compile a separate consumer using element/document construction, Reader/Writer signatures, and an outputter subclass. Test the missing-signature-type strategy before generating broad stubs. | JVM reference harness and actual optimized J2CL JavaScript consumer run; no duplicate classes or hidden JVM dependencies. |
| 2 — Build the tree | Port the matched fork's portable tree code where practical; replace incompatible internals. Implement validation, namespaces, attributes, common content types, document/parent rules, live collections, and deep clone. | Differential tree-operation tests and compiled MPS memento/descriptor consumer probes pass. |
| 3 — Build serialization | Implement `Format` and `XMLOutputter` with required writer/stream/string overloads and escaping hooks. Supply tested in-memory encoding. | XML9 writer fixtures and descriptor/cache/memento fixtures match required semantics and formatting; special characters and whitespace round-trip. |
| 4 — Build XML input and SAX integration | Implement/reuse the portable parser, JDOM tree sink, `SAXBuilder`, and a narrow SAX companion. Adapt `JDOMUtil.createSAXParser()` to a fixed portable implementation, or supply precisely the required JAXP facade without service discovery. Keep an explicit choice in the MPS integration patch. | The same XML corpus builds JDOM trees and drives real MPS header/model handlers, including early abort, locations, and errors. |
| 5 — Integrate the initial MPS profile | Replace utility dependencies on unavailable readers/writers with portable equivalents; connect buffered storage adapters. Compile selected persistence, descriptor, cache, and editor-state paths. Register explicit exclusions and stubs for remaining desktop/optional paths. Changes to generated code must be made through its MPS source of truth during implementation. | Read → edit → save → reload works for single-file XML9, per-root XML9, descriptors, and editor mementos; no supported scenario reaches a stub. |
| 6 — Release a consumable library | Publish/document stable targets and source artifact layout; add member/API checks, dependency rules, fixture provenance, consumer examples, and the supported-profile table. Test from a fresh separate consumer with reproducible dependencies. | Development and optimized browser builds pass in Chromium, Firefox, and WebKit, with no local absolute-path dependency in the released artifact. |

Phases 2–4 are the substantive library work. Phase 0 and Phase 1 are early feasibility gates. Phase 5 is necessary before claiming MPS persistence compatibility; completing the JDOM library in isolation is not the same milestone.

Proposed source boundaries are `src/main/java/org/jdom/` for compatibility classes, an internal portable XML engine package, a separately owned SAX compatibility source set, and `integration/mps/` for integration documentation/patches. Build/test scripts may use JVM tools; shipped runtime code must transpile to JavaScript. Keep reference JDOM and replacement JDOM in separate test processes/classpaths because they share package names.

## 7. Verification and completion criteria

- **Member compatibility:** compile real extracted consumer classes/probes, including generated Java, overload resolution, generic assignments, protected overrides, and catch clauses. Compare against the pinned API, not only class names. Inspect dependency signatures even when their bodies are excluded.
- **Differential behavior:** replay tree mutation sequences on the reference fork and J2CL implementation; compare results, exception types, list effects, parent links, clone isolation, namespaces, and text. Include failure cases, mixed content, and Unicode.
- **Persistence corpus:** generate representative models with the reference MPS runtime and record expected semantics. Cover model IDs/references, properties, imports, language/concept registries, ordering, metadata, per-root headers/root streams, and supported loading states. Obtain fixtures through supported MPS export/test workflows. Hand-crafted XML parser fixtures supplement real MPS fixtures.
- **Round-trip checks:** compare reference save → browser read → browser save → reference reload, then repeated save/reload stability. Include descriptor mementos, generator dependencies, and editor selection/folding state. XML fidelity is broader than parsing one model successfully.
- **Parser boundaries:** reject malformed input and unsupported DTD/encoding/validation requests; prove no external lookup. Test early SAX termination, accurate supported source locations, truncated streams, callback exceptions, and replayable input. Include realistic large models and report parse/write time, memory, and worst-case depth behavior.
- **Runtime verification:** test both development and Closure-optimized output in real browsers; include exports that exercise the tested API so dead-code elimination cannot hide the whole implementation. Run independent consumer builds against the public target. JVM-only tests are insufficient.
- **Stub coverage:** record each stub's member, reason, excluded feature/caller, and enabling condition. Fail supported integration scenarios if a stub is invoked. Adding an MPS module/plugin requires reviewing its new member references before enabling it.

Initial completion means the selected API compiles, the supported scenarios execute with zero stub hits, reference/browser round trips pass, and the released artifact is reproducibly usable by a separate J2CL consumer. Report any remaining blocked type or behavior separately from deliberate exclusions.

## 8. Explicit boundaries and remaining decisions

- Resolved during implementation: the MPS checkout's IntelliJ tag identifies the exact JetBrains JDOM source baseline. `compatibility/upstream.json` records the commit, tree, source hashes and retained license. Installed MPS 2025.3 remains a separate integration fixture oracle.
- The current scan is textual. A resolved method/dependency inventory and runtime profile are planned Phase 0 work, not completed analysis results.
- General IntelliJ `com.intellij.openapi.util.JDOMUtil`, component serialization/reflection, and platform bootstrapping are separate integrations. Audit their reachable JDOM members before enabling full workbench components; this plan does not promise all IntelliJ XML utilities work unchanged.
- Older XML persistence versions used by VCS, binary model persistence, arbitrary serialized user objects, XML-language entity expansion, and XPath-based maintenance tools remain outside the initial acceptance profile. Promote any required feature explicitly rather than silently dropping its data.
- Browser storage, threading, Swing/AWT, reflection, and class loading remain separate MPS migration concerns. The XML layer accepts buffered data and does not implement those subsystems.
- Prior project notes describe a bounded XML9 backend codec; that earlier implementation was not revalidated in this analysis. If reused for fixture comparison, it is not a replacement for JDOM semantics or the actual MPS reader/writer compatibility tests.

Recommended first implementation milestone: pin the fork and member inventory, prove a J2CL consumer, and implement tree construction plus `XMLOutputter` sufficiently to serialize real `ModelWriter9` output. Follow immediately with the shared parser/SAX path so both saving and loading become functional.
