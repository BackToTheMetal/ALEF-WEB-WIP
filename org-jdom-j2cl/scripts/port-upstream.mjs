import fs from 'node:fs';
import path from 'node:path';
import {createHash} from 'node:crypto';

const root = path.resolve(import.meta.dirname, '..');
const source = path.join(root, 'out/upstream');
for(const entry of JSON.parse(fs.readFileSync(path.join(root,'compatibility/upstream.json'),'utf8')).files) {
  const actual=createHash('sha256').update(fs.readFileSync(path.join(source,entry.path))).digest('hex');
  if(actual!==entry.sha256)throw new Error(`Pinned source hash mismatch: ${entry.path}`);
}
function removeBlock(text, pattern, replacement = '') {
  const match = pattern.exec(text);
  if (!match) throw new Error(`Missing port anchor ${pattern}`);
  const start = text.indexOf('{', match.index);
  const masked = text.replace(/\/\*[\s\S]*?\*\/|\/\/[^\n]*|"(?:\\.|[^"\\])*"|'(?:\\.|[^'\\])*'/g,
    s => ' '.repeat(s.length));
  let depth = 1, end = start + 1;
  while (depth && end < text.length) {
    if (masked[end] === '{') depth++;
    if (masked[end] === '}') depth--;
    end++;
  }
  return text.slice(0, match.index) + replacement + text.slice(end);
}
let count = 0;
for (const rel of fs.readdirSync(source, {recursive: true})) {
  if (!rel.endsWith('.java')) continue;
  const portable = rel.replaceAll('\\', '/');
  if ((/\/xpath\//.test(portable) && !/\/(XPathFactory|XPathExpression|XPathDiagnostic)\.java$/.test(portable)) || portable.endsWith('/DOMBuilder.java') || portable.endsWith('/XMLReaders.java')) continue;
  let text = fs.readFileSync(path.join(source, rel), 'utf8');
  text = text.replace(/^import org\.jetbrains\.annotations\..*;\r?\n/gm, '')
    .replace(/@(NotNull|Nullable|ApiStatus\.[A-Za-z]+)(?:\([^)]*\))?\s*/g, '');
  text = text.replace('import org.jdom.xpath.XPathFactory;', '');
  text = text.replaceAll('indexes.clone()', 'java.util.Arrays.copyOf(indexes, indexes.length)');
  if (portable.endsWith('/XPathFactory.java')) {
    text = text.replace('import org.jdom.xpath.jaxen.JaxenXPathFactory;', '');
    text = removeBlock(text, /  public static XPathFactory instance\(/, `  public static XPathFactory instance() {
    return UnsupportedFactory.INSTANCE;
  }`);
    text = text.replace(/\}\s*$/, `  private static final class UnsupportedFactory extends XPathFactory {
    static final XPathFactory INSTANCE = new UnsupportedFactory();
    @Override public <T> XPathExpression<T> compile(String expression, Filter<T> filter, Map<String, Object> variables, Namespace... namespaces) {
      throw new UnsupportedOperationException("org.jdom.xpath.XPathFactory.compile: XPath is outside the browser profile");
    }
  }
}\n`);
  }
  for (const name of ['readObject', 'writeObject', 'writeReplace', 'readResolve']) {
    const pattern = new RegExp(`  private [^\\n{;]*\\b${name}\\(`);
    while (pattern.test(text)) text = removeBlock(text, pattern);
  }
  text = text.replace(/^import java\.io\.(ObjectInputStream|ObjectOutputStream|InvalidObjectException|ObjectStreamException);\r?\n/gm, '');
  if (portable.endsWith('/Namespace.java')) {
    text = removeBlock(text, /  private static final class NamespaceSerializationProxy/);
    text = text.replace('<>(512, 0.75f, 64)', '<>(512)');
  }
  if (portable.endsWith('/Verifier.java')) {
    text = text.replaceAll('String.format(', 'formatHex(');
    text = text.replace(/\}\s*$/, `  private static String formatHex(String format, int... values) {
    for (int value : values) {
      int start = format.indexOf('%');
      int end = format.indexOf('x', start);
      int width = Integer.parseInt(format.substring(start + 1, end));
      String hex = Integer.toHexString(value);
      while (hex.length() < width) hex = "0" + hex;
      format = format.substring(0, start) + hex + format.substring(end + 1);
    }
    return format;
  }
}\n`);
  }
  if (portable.endsWith('/Format.java')) {
    text = text.replace('    this.encoding = encoding;\n    escapeStrategy = chooseStrategy(encoding);',
      '    EscapeStrategy strategy = chooseStrategy(encoding);\n    this.encoding = encoding;\n    escapeStrategy = strategy;');
    text = text.replace(/^import java\.nio\.charset\..*;\r?\n/gm, '');
    text = removeBlock(text, /  private static final class DefaultCharsetEscapeStrategy/);
    text = text.replace(/    try \{\s*final CharsetEncoder[\s\S]*?return DEFAULT_ESCAPE_STRATEGY;/,
      '    throw new UnsupportedOperationException("org.jdom.output.Format.setEncoding: unsupported encoding " + encoding);');
    text = text.replace('System.lineSeparator()', '"\\n"');
    text = text.replace('"UTF-16".equalsIgnoreCase(encoding)',
      '"UTF-16".equalsIgnoreCase(encoding) || "UTF-16LE".equalsIgnoreCase(encoding) || "UTF-16BE".equalsIgnoreCase(encoding)');
    text = text.replace('encoding.toUpperCase(Locale.ENGLISH).startsWith("ISO-8859-")', '"ISO-8859-1".equalsIgnoreCase(encoding)');
  }
  if (portable.endsWith('/XMLOutputter.java')) {
    text = text.replace('      if (obj instanceof Element) {', `      if (currentFormat.getPreserveMiscContent() && obj instanceof Comment) {
        printComment(out, (Comment)obj);
      } else if (currentFormat.getPreserveMiscContent() && obj instanceof ProcessingInstruction) {
        printProcessingInstruction(out, (ProcessingInstruction)obj);
      } else if (obj instanceof Element) {`);
    text = text.replace('      if (next instanceof Element) {', `      if (currentFormat.getPreserveMiscContent() && next instanceof Comment) {
        printComment(out, (Comment)next);
      } else if (currentFormat.getPreserveMiscContent() && next instanceof ProcessingInstruction) {
        printProcessingInstruction(out, (ProcessingInstruction)next);
      } else if (next instanceof Element) {`);
    text = text.replace('  public void output(ProcessingInstruction pi, Writer out) throws IOException {\n  }',
      '  public void output(ProcessingInstruction pi, Writer out) throws IOException {\n    if (currentFormat.getPreserveMiscContent()) { printProcessingInstruction(out, pi); out.flush(); }\n  }');
    text = text.replace(/\}\s*$/, `  private static void printProcessingInstruction(Writer out, ProcessingInstruction pi) throws IOException {
    String target = pi.getTarget(), data = pi.getData();
    if (!XmlParser.isXmlName(target) || "xml".equalsIgnoreCase(target)) {
      throw new IllegalDataException("Invalid processing instruction target: " + target);
    }
    String reason = Verifier.checkCharacterData(data);
    if (reason != null) throw new IllegalDataException("Invalid processing instruction data: " + reason);
    if (data.contains("?>")) throw new IllegalDataException("Processing instruction data cannot contain ?>");
    out.write("<?"); out.write(target);
    if (!data.isEmpty()) { out.write(" "); out.write(data); }
    out.write("?>");
  }
}\n`);
    text = text.replace('import java.io.BufferedOutputStream;', '');
    text = text.replace('new BufferedOutputStream(out)', 'out');
    text = text.replace('import java.io.OutputStreamWriter;', 'import org.jdom.internal.xml.XmlStreamWriter;\nimport org.jdom.internal.xml.XmlParser;');
    text = text.replace('new OutputStreamWriter(', 'new XmlStreamWriter(');
    text = text.replace(/    if \("UTF-8"\.equals\(enc\)\) \{\s*enc = "UTF8";\s*\}/, '');
  }
  if (portable.endsWith('/SAXBuilder.java') || portable.endsWith('/SAXBuilderEngine.java')) {
    for (const type of ['File', 'URL']) {
      text = removeBlock(text, new RegExp(`  @Override\\s+public Document build\\(final ${type} `));
    }
    if (portable.endsWith('/SAXBuilderEngine.java')) {
      text = removeBlock(text, /  private static URL fileToURL\(/);
    } else {
      const signature = '  @Override\n  public Document build(final String systemId) throws JDOMException, IOException {';
      text = removeBlock(text, /  @Override\s+public Document build\(final String systemId\)/);
      text = text.replace(/\}\s*$/, signature + '\n    return getEngine().build(systemId);\n  }\n}\n');
    }
    text = text.replace(/^import (java\.io\.File|java\.net\.(URL|MalformedURLException));\r?\n/gm, '');
  }
  if (portable.endsWith('/SAXEngine.java')) {
    text = text.replace(/^\s*Document build\((?:final )?File file\) throws JDOMException, IOException;/m, '')
      .replace(/^\s*Document build\((?:final )?URL url\) throws JDOMException, IOException;/m, '')
      .replace(/^import (java\.io\.File|java\.net\.URL);\r?\n/gm, '');
  }
  const type = path.basename(rel, '.java');
  if (type === 'CloneBase') {
    text = removeBlock(text, /  @Override\s+protected CloneBase clone\(/,
      '  protected CloneBase clone() {\n    throw new UnsupportedOperationException("CloneBase.clone: subclasses must supply a portable clone implementation");\n  }');
  }
  if (type === 'Element') {
    text = text.replace('Element element = (Element)super.clone();',
      'Element element = newCloneInstance();\n    element.name = name;\n    element.namespace = namespace;');
    text = text.replace(/\}\s*$/, '  protected Element newCloneInstance() {\n    if (getClass() != Element.class) throw new UnsupportedOperationException("Element subclass must override newCloneInstance or clone");\n    return new Element();\n  }\n}\n');
  }
  if (type === 'Document') {
    text = text.replace('final Document doc = (Document)super.clone();', 'final Document doc = new Document();\n    doc.baseUri = baseUri;\n    doc.propertyMap = propertyMap;');
  }
  if (type === 'Attribute') {
    text = text.replace('final Attribute clone = (Attribute)super.clone();', 'if (getClass() != Attribute.class) throw new UnsupportedOperationException("Attribute subclass must override clone");\n    final Attribute clone = new Attribute(true, name, value, namespace, type);\n    clone.specified = specified;');
  }
  if (type === 'Text') {
    text = text.replace('Text text = (Text)super.clone();', 'if (getClass() != Text.class) throw new UnsupportedOperationException("Text subclass must override clone");\n    Text text = new Text(true, this.value);');
  }
  const directClones = {
    CDATA: 'if (getClass() != CDATA.class) throw new UnsupportedOperationException("CDATA subclass must override clone");\n    return new CDATA(true, getText());', Comment: 'return new Comment(getText());',
    ProcessingInstruction: 'return new ProcessingInstruction(target, rawData);', EntityRef: 'return new EntityRef(name);',
    DocType: 'DocType copy = new DocType(elementName, publicID, systemID);\n    copy.internalSubset = internalSubset;\n    return copy;'
  };
  if (directClones[type]) text = text.replace(`return (${type})super.clone();`, directClones[type]);
  if (type === 'Format') {
    text = text.replace(/\}\s*$/, `  private boolean preserveMiscContent;
  /** Opt in to comment/PI output omitted by the JetBrains default profile. */
  public Format setPreserveMiscContent(boolean preserve) { preserveMiscContent = preserve; return this; }
  public boolean getPreserveMiscContent() { return preserveMiscContent; }
}\n`);
    text = removeBlock(text, /  @Override\s+public Format clone\(/,
      '  public Format clone() {\n    Format copy = new Format();\n' + ['indent','lineSeparator','encoding','omitDeclaration','omitEncoding','expandEmptyElements','ignoreTrAXEscapingPIs','mode','escapeStrategy','preserveMiscContent'].map(f=>`    copy.${f} = ${f};`).join('\n') + '\n    return copy;\n  }');
  }
  if (type === 'XMLOutputter') {
    text = removeBlock(text, /  @Override\s+public Object clone\(/,
      '  public Object clone() {\n    if (getClass() != XMLOutputter.class) throw new UnsupportedOperationException("XMLOutputter subclass must override clone");\n    XMLOutputter copy = new XMLOutputter();\n    copy.userFormat = userFormat;\n    copy.currentFormat = currentFormat;\n    return copy;\n  }');
  }
  const target = path.join(root, 'src/main/java', rel);
  fs.mkdirSync(path.dirname(target), {recursive: true});
  fs.writeFileSync(target, text);
  count++;
}
const original = fs.readFileSync(path.join(source, 'org/jdom/Element.java'), 'utf8');
fs.mkdirSync(path.join(root, 'third_party'), {recursive:true});
fs.writeFileSync(path.join(root, 'third_party/JDOM-LICENSE.txt'), original.slice(0, original.indexOf('package org.jdom;')));
console.log(`Ported ${count} tree/output sources with retained upstream license notices.`);
