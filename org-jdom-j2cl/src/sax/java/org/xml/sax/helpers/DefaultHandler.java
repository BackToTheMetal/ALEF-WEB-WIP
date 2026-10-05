// Portable SAX API compatibility declarations.
package org.xml.sax.helpers;

import org.xml.sax.*;
public class DefaultHandler implements ContentHandler, DTDHandler, EntityResolver, ErrorHandler {
  public void setDocumentLocator(Locator locator) {}
  public void startDocument() throws SAXException {}
  public void endDocument() throws SAXException {}
  public void startPrefixMapping(String prefix, String uri) throws SAXException {}
  public void endPrefixMapping(String prefix) throws SAXException {}
  public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {}
  public void endElement(String uri, String localName, String qName) throws SAXException {}
  public void characters(char[] ch, int start, int length) throws SAXException {}
  public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {}
  public void processingInstruction(String target, String data) throws SAXException {}
  public void skippedEntity(String name) throws SAXException {}
  public void notationDecl(String name, String publicId, String systemId) throws SAXException {}
  public void unparsedEntityDecl(String name, String publicId, String systemId, String notationName) throws SAXException {}
  public void warning(SAXParseException e) throws SAXException {}
  public void error(SAXParseException e) throws SAXException {}
  public void fatalError(SAXParseException e) throws SAXException { throw e; }
  public InputSource resolveEntity(String publicId,String systemId) throws SAXException,java.io.IOException { return null; }
}
