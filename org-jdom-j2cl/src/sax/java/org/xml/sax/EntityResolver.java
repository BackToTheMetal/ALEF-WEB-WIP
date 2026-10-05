// Portable SAX API compatibility declarations.
package org.xml.sax;

public interface EntityResolver {
  InputSource resolveEntity(String publicId, String systemId) throws SAXException, java.io.IOException;
}
