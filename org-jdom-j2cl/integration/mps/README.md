# MPS integration points

Consumer baseline: MPS-Web `1ae803e4f8c52e470c94563059eca4b4b8397c8c`. The sibling checkout is not modified by this library. The executable probes in `tests/mps-source` are copies of five real consumer files with only their GeneratedClass annotation/import removed.

## Replace JVM utility operations

`core/kernel/source/jetbrains/mps/util/JDOMUtil.java` currently creates a JAXP `SAXParserFactory`, wraps byte streams in `InputStreamReader`, and writes through `OutputStreamWriter`. These are integration boundaries; there is no JAXP service-discovery facade in this library.

| Existing call | Browser replacement |
| --- | --- |
| `JDOMUtil.createSAXParser().parse(inputSource, handler)` | `MpsXml.parse(inputSource, handler)` |
| `createSAXParser().parse(inputStream, handler)` | `MpsXml.parse(new InputSource(inputStream), handler)` |
| `loadDocument(InputSource)` | `new SAXBuilder().build(source)` |
| `loadDocument(Reader)` | `new SAXBuilder().build(reader)` |
| `loadDocument(InputStream)` | `new SAXBuilder().build(stream)` |
| `new InputStreamReader(stream, charset)` before XML parsing | Pass bytes directly via `InputSource` and set its encoding when the application requires a fixed charset. |
| `createOutputter()` / pretty LF output | `MpsXml.createOutputter()` |
| `output(Document, new OutputStreamWriter(stream,...))` | `MpsXml.createOutputter().output(document, stream)` with UTF-8 format, or an explicitly supported format encoding. |

Remove the obsolete `ParserConfigurationException` catch when a call no longer constructs a JAXP parser. Keep handling SAXException/IOException and keep the separate catch for `BreakParseSAXException`; it remains the same exception object. For custom SAX feature/limit configuration, construct `PortableXmlReader`, set the handlers, and call `parse(InputSource)` directly.

MPS model persistence, FilePerRootFormatUtil, annotation/indexing handlers, and header reads use SAX. Merely replacing SAXBuilder in JDOMUtil does not redirect those reads. MPS's generated Java must be changed at its MPS source of truth, or via its established J2CL overlay mechanism; do not hand-edit generated sources as the durable fix.

## Storage and application boundaries

Provide a buffered browser implementation of MPS data sources/IFile separately. `File`, `URL`, synchronous network loading, directory traversal and desktop write helpers are not implemented here. Load a complete root/header stream before parsing; retain stream names and ordering at the data-source layer. The parser does not decide MPS multi-stream assembly semantics.

`DefaultMetadataPersistence`, descriptors, generator/debug caches, and editor mementos should call the same in-memory XML helpers. MementoUtil is exercised with its actual code. Complete descriptor/cache/editor consumers still need their enclosing MPS dependencies compiled and tested in MPS-Web. IntelliJ component serialization and its reflection-based JDOM utilities require a separate audit.

The runtime supports general XML tree operations these consumers use; it does not claim their complete dependency closure or browser application lifecycle has been migrated. The current fixture corpus proves single-file and per-root **document** interoperability, not the full file-per-root data-source assembly workflow.

## Validation evidence and next integration gate

The actual MPS 2025.3 ModelWriter9 exports a property-bearing model, a root stream and a header stream. In each browser/build combination the portable implementation parses, edits and serializes them. The actual MPS ModelReader9Handler then reloads all 18 results through PortableXmlReader on the JVM and checks model/node/concept identity, property data and metadata. The language is synthetic and supplied through StuffedMetaModelInfo; no generated language or IDE is required. This does not exercise a full ModelReader9Handler dependency closure in JavaScript.

Before enabling MPS persistence in the web application, compile its actual ModelWriter9/ModelReader9Handler and storage dependencies to J2CL, apply the utility substitutions above, and run read/edit/save/reload through its model/data-source APIs. Expand the corpus to multiple roots, containment, local/external references, imports/language versions, loading states, descriptors, caches, and editor selection/folding state. No unscanned plugin should be classified unused.
