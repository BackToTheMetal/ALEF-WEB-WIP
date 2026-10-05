// Portable SAX API compatibility declarations.
package org.xml.sax.ext;

public interface DeclHandler {
  void elementDecl(String name,String model) throws org.xml.sax.SAXException;
  void attributeDecl(String eName,String aName,String type,String mode,String value) throws org.xml.sax.SAXException;
  void internalEntityDecl(String name,String value) throws org.xml.sax.SAXException;
  void externalEntityDecl(String name,String publicId,String systemId) throws org.xml.sax.SAXException;
}
