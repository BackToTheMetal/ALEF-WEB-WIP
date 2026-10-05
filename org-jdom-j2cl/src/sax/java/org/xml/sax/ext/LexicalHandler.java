// Portable SAX API compatibility declarations.
package org.xml.sax.ext;

public interface LexicalHandler {
  void startDTD(String name,String publicId,String systemId) throws org.xml.sax.SAXException;
  void endDTD() throws org.xml.sax.SAXException; void startEntity(String name) throws org.xml.sax.SAXException;
  void endEntity(String name) throws org.xml.sax.SAXException; void startCDATA() throws org.xml.sax.SAXException;
  void endCDATA() throws org.xml.sax.SAXException; void comment(char[] ch,int start,int length) throws org.xml.sax.SAXException;
}
