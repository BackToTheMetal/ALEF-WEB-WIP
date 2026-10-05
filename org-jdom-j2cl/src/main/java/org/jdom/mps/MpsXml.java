package org.jdom.mps;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.internal.xml.PortableXmlReader;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/** In-memory integration points for replacing the JVM portions of MPS JDOMUtil. */
public final class MpsXml {
  private MpsXml() {}
  public static Document parse(String xml)throws JDOMException,IOException{return parse(new StringReader(xml));}
  public static Document parse(Reader reader)throws JDOMException,IOException{return new SAXBuilder().build(reader);}
  public static PortableXmlReader createSAXParser(){return new PortableXmlReader();}
  public static void parse(InputSource source,DefaultHandler handler)throws SAXException,IOException {
    PortableXmlReader reader=createSAXParser();reader.setContentHandler(handler);reader.setDTDHandler(handler);reader.setEntityResolver(handler);reader.setErrorHandler(handler);reader.parse(source);
  }
  public static XMLOutputter createOutputter(){return createOutputter("\n");}
  public static XMLOutputter createOutputter(String lineSeparator){return new MpsOutputter(Format.getPrettyFormat().setLineSeparator(lineSeparator));}
  private static final class MpsOutputter extends XMLOutputter {
    MpsOutputter(Format format){super(format);}
    @Override public String escapeAttributeEntities(String value){return escape(value,true);}
    @Override public String escapeElementEntities(String value){return escape(value,false);}
    @Override public Object clone(){return new MpsOutputter(getFormat().clone());}
  }
  private static String escape(String value,boolean attribute) {
    StringBuilder result=new StringBuilder();
    for(int i=0;i<value.length();i++) {
      char c=value.charAt(i);
      switch(c) {
        case '<':result.append("&lt;");break;case '>':result.append("&gt;");break;
        case '&':result.append("&amp;");break;case '"':result.append("&quot;");break;
        case '\n':result.append(attribute?"&#10;":"\n");break;
        case '\r':result.append(attribute?"&#13;":"\r");break;
        case '\t':result.append(attribute?"&#9;":"\t");break;
        default:result.append(c);
      }
    }
    return result.toString();
  }
}
