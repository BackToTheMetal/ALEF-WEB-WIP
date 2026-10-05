import fs from 'node:fs';
import path from 'node:path';
const root = path.resolve(import.meta.dirname, '../src/sax/java');
function write(name, body) {
  const slash = name.lastIndexOf('/');
  const file = path.join(root, name + '.java');
  fs.mkdirSync(path.dirname(file), {recursive:true});
  fs.writeFileSync(file, '// Portable SAX API compatibility declarations.\npackage ' + name.slice(0, slash).replaceAll('/', '.') + ';\n\n' + body + '\n');
}
const api = {
  Locator: 'String getPublicId(); String getSystemId(); int getLineNumber(); int getColumnNumber();',
  Attributes: 'int getLength(); String getURI(int index); String getLocalName(int index); String getQName(int index); String getType(int index); String getValue(int index); int getIndex(String uri, String localName); int getIndex(String qName); String getType(String uri, String localName); String getType(String qName); String getValue(String uri, String localName); String getValue(String qName);',
  ContentHandler: 'void setDocumentLocator(Locator locator); void startDocument() throws SAXException; void endDocument() throws SAXException; void startPrefixMapping(String prefix, String uri) throws SAXException; void endPrefixMapping(String prefix) throws SAXException; void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException; void endElement(String uri, String localName, String qName) throws SAXException; void characters(char[] ch, int start, int length) throws SAXException; void ignorableWhitespace(char[] ch, int start, int length) throws SAXException; void processingInstruction(String target, String data) throws SAXException; void skippedEntity(String name) throws SAXException;',
  DTDHandler: 'void notationDecl(String name, String publicId, String systemId) throws SAXException; void unparsedEntityDecl(String name, String publicId, String systemId, String notationName) throws SAXException;',
  EntityResolver: 'InputSource resolveEntity(String publicId, String systemId) throws SAXException, java.io.IOException;',
  ErrorHandler: 'void warning(SAXParseException e) throws SAXException; void error(SAXParseException e) throws SAXException; void fatalError(SAXParseException e) throws SAXException;',
  XMLReader: 'boolean getFeature(String name) throws SAXNotRecognizedException, SAXNotSupportedException; void setFeature(String name, boolean value) throws SAXNotRecognizedException, SAXNotSupportedException; Object getProperty(String name) throws SAXNotRecognizedException, SAXNotSupportedException; void setProperty(String name, Object value) throws SAXNotRecognizedException, SAXNotSupportedException; void setEntityResolver(EntityResolver resolver); EntityResolver getEntityResolver(); void setDTDHandler(DTDHandler handler); DTDHandler getDTDHandler(); void setContentHandler(ContentHandler handler); ContentHandler getContentHandler(); void setErrorHandler(ErrorHandler handler); ErrorHandler getErrorHandler(); void parse(InputSource source) throws java.io.IOException, SAXException; void parse(String systemId) throws java.io.IOException, SAXException;'
};
for (const [name, body] of Object.entries(api)) write('org/xml/sax/' + name, `public interface ${name} {\n  ${body.split('; ').join(';\n  ')}\n}`);
write('org/xml/sax/SAXException', `public class SAXException extends Exception {
  public SAXException() {} public SAXException(String message) { super(message); }
  public SAXException(Exception cause) { super(cause); } public SAXException(String message, Exception cause) { super(message, cause); }
  public Exception getException() { return getCause() instanceof Exception ? (Exception)getCause() : null; }
  @Override public String getMessage() { String message=super.getMessage(); return message==null && getCause()!=null ? getCause().getMessage() : message; }
  @Override public String toString() { return getCause()==null ? super.toString() : super.toString()+"\\n"+getCause().toString(); }
}`);
for (const name of ['SAXNotRecognizedException','SAXNotSupportedException']) write('org/xml/sax/'+name, `public class ${name} extends SAXException { public ${name}() {} public ${name}(String message) { super(message); } }`);
write('org/xml/sax/SAXParseException', `public class SAXParseException extends SAXException {
  private final String publicId, systemId; private final int line, column;
  public SAXParseException(String m, Locator l) { this(m,l,null); }
  public SAXParseException(String m, Locator l, Exception e) { this(m,l==null?null:l.getPublicId(),l==null?null:l.getSystemId(),l==null?-1:l.getLineNumber(),l==null?-1:l.getColumnNumber(),e); }
  public SAXParseException(String m,String p,String s,int l,int c) { this(m,p,s,l,c,null); }
  public SAXParseException(String m,String p,String s,int l,int c,Exception e) { super(m,e); publicId=p;systemId=s;line=l;column=c; }
  public String getPublicId(){return publicId;} public String getSystemId(){return systemId;}
  public int getLineNumber(){return line;} public int getColumnNumber(){return column;}
}`);
write('org/xml/sax/InputSource', `public class InputSource {
  private String publicId,systemId,encoding; private java.io.Reader reader; private java.io.InputStream stream;
  public InputSource() {} public InputSource(String id){systemId=id;} public InputSource(java.io.Reader r){reader=r;} public InputSource(java.io.InputStream s){stream=s;}
  public void setPublicId(String v){publicId=v;} public String getPublicId(){return publicId;}
  public void setSystemId(String v){systemId=v;} public String getSystemId(){return systemId;}
  public void setEncoding(String v){encoding=v;} public String getEncoding(){return encoding;}
  public void setCharacterStream(java.io.Reader v){reader=v;} public java.io.Reader getCharacterStream(){return reader;}
  public void setByteStream(java.io.InputStream v){stream=v;} public java.io.InputStream getByteStream(){return stream;}
}`);
write('org/xml/sax/helpers/DefaultHandler', `import org.xml.sax.*;
public class DefaultHandler implements ContentHandler, DTDHandler, EntityResolver, ErrorHandler {
${[api.ContentHandler, api.DTDHandler, api.ErrorHandler].join(' ').split(';').filter(s=>s.trim()).map(s=>'  public '+s.trim()+(s.includes('fatalError')?' { throw e; }':' {}')).join('\n')}
  public InputSource resolveEntity(String publicId,String systemId) throws SAXException,java.io.IOException { return null; }
}`);
write('org/xml/sax/ext/Attributes2', `public interface Attributes2 extends org.xml.sax.Attributes {
  boolean isDeclared(int index); boolean isDeclared(String qName); boolean isDeclared(String uri,String localName);
  boolean isSpecified(int index); boolean isSpecified(String qName); boolean isSpecified(String uri,String localName);
}`);
write('org/xml/sax/ext/LexicalHandler', `public interface LexicalHandler {
  void startDTD(String name,String publicId,String systemId) throws org.xml.sax.SAXException;
  void endDTD() throws org.xml.sax.SAXException; void startEntity(String name) throws org.xml.sax.SAXException;
  void endEntity(String name) throws org.xml.sax.SAXException; void startCDATA() throws org.xml.sax.SAXException;
  void endCDATA() throws org.xml.sax.SAXException; void comment(char[] ch,int start,int length) throws org.xml.sax.SAXException;
}`);
write('org/xml/sax/ext/DeclHandler', `public interface DeclHandler {
  void elementDecl(String name,String model) throws org.xml.sax.SAXException;
  void attributeDecl(String eName,String aName,String type,String mode,String value) throws org.xml.sax.SAXException;
  void internalEntityDecl(String name,String value) throws org.xml.sax.SAXException;
  void externalEntityDecl(String name,String publicId,String systemId) throws org.xml.sax.SAXException;
}`);
write('javax/xml/XMLConstants', `public final class XMLConstants {
  private XMLConstants() {}
  public static final String NULL_NS_URI="", DEFAULT_NS_PREFIX="", XML_NS_PREFIX="xml", XML_NS_URI="http://www.w3.org/XML/1998/namespace", XMLNS_ATTRIBUTE="xmlns", XMLNS_ATTRIBUTE_NS_URI="http://www.w3.org/2000/xmlns/", FEATURE_SECURE_PROCESSING="http://javax.xml.XMLConstants/feature/secure-processing";
}`);
