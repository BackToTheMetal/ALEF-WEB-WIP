// Portable SAX API compatibility declarations.
package org.xml.sax.ext;

public interface Attributes2 extends org.xml.sax.Attributes {
  boolean isDeclared(int index); boolean isDeclared(String qName); boolean isDeclared(String uri,String localName);
  boolean isSpecified(int index); boolean isSpecified(String qName); boolean isSpecified(String uri,String localName);
}
