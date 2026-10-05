package org.jdom.input.sax;

import org.jdom.JDOMException;
import org.jdom.internal.xml.PortableXmlReader;
import org.xml.sax.XMLReader;

/** Fixed portable providers; no service loading or validating engine. */
public enum XMLReaders implements XMLReaderJDOMFactory {
  NONVALIDATING, DTDVALIDATING, XSDVALIDATING;

  @Override public XMLReader createXMLReader() throws JDOMException {
    if (this != NONVALIDATING) throw new JDOMException("org.jdom.input.sax.XMLReaders: validation is unsupported");
    return new PortableXmlReader();
  }

  @Override public boolean isValidating() { return this != NONVALIDATING; }
}
