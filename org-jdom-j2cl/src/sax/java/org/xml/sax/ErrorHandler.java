// Portable SAX API compatibility declarations.
package org.xml.sax;

public interface ErrorHandler {
  void warning(SAXParseException e) throws SAXException;
  void error(SAXParseException e) throws SAXException;
  void fatalError(SAXParseException e) throws SAXException;
}
