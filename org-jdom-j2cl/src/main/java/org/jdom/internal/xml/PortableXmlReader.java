package org.jdom.internal.xml;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import org.xml.sax.*;
import org.xml.sax.ext.DeclHandler;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.DefaultHandler;

/** XMLReader backed exclusively by the portable Java event parser. Not thread-safe. */
public final class PortableXmlReader implements XMLReader {
  private static final String FEATURES="http://xml.org/sax/features/";
  private ContentHandler content=new DefaultHandler();
  private EntityResolver resolver;
  private DTDHandler dtd;
  private ErrorHandler errors;
  private LexicalHandler lexical;
  private DeclHandler declarations;
  private boolean namespaces=true, prefixes=false, parsing;
  private int maxChars=XmlParser.DEFAULT_MAX_CHARS, maxToken=XmlParser.DEFAULT_MAX_TOKEN, maxDepth=XmlParser.DEFAULT_MAX_DEPTH;

  public void setLimits(int chars,int token,int depth) {
    if(parsing)throw new IllegalStateException("Parse in progress");
    if(chars<1||token<1||depth<1)throw new IllegalArgumentException("Positive XML limits required");
    maxChars=chars;maxToken=token;maxDepth=depth;
  }
  @Override public boolean getFeature(String name) throws SAXNotRecognizedException {
    if(name.equals(FEATURES+"namespaces"))return namespaces;
    if(name.equals(FEATURES+"namespace-prefixes"))return prefixes;
    if(name.equals("http://javax.xml.XMLConstants/feature/secure-processing") || name.equals("http://apache.org/xml/features/disallow-doctype-decl"))return true;
    if(name.equals(FEATURES+"xmlns-uris") || name.equals(FEATURES+"validation") || name.equals(FEATURES+"external-general-entities") || name.equals(FEATURES+"external-parameter-entities") || name.equals("http://apache.org/xml/features/nonvalidating/load-external-dtd"))return false;
    throw new SAXNotRecognizedException(name);
  }
  @Override public void setFeature(String name,boolean value) throws SAXNotRecognizedException,SAXNotSupportedException {
    if(parsing)throw new SAXNotSupportedException("Cannot change features during parsing");
    if(name.equals(FEATURES+"namespaces")) {
      if(!value)throw new SAXNotSupportedException("PortableXmlReader requires namespace processing");
      return;
    }
    if(name.equals(FEATURES+"namespace-prefixes")) {prefixes=value;return;}
    if(getFeature(name)!=value)throw new SAXNotSupportedException(name+"="+value+" is unsupported");
  }
  @Override public Object getProperty(String name) throws SAXNotRecognizedException {
    if(name.equals("http://xml.org/sax/properties/lexical-handler") || name.equals("http://xml.org/sax/handlers/LexicalHandler"))return lexical;
    if(name.equals("http://xml.org/sax/properties/declaration-handler"))return declarations;
    throw new SAXNotRecognizedException(name);
  }
  @Override public void setProperty(String name,Object value) throws SAXNotRecognizedException,SAXNotSupportedException {
    if(parsing)throw new SAXNotSupportedException("Cannot change properties during parsing");
    getProperty(name);
    if(name.contains("lexical")||name.contains("Lexical")) {
      if(value!=null && !(value instanceof LexicalHandler))throw new SAXNotSupportedException("Expected LexicalHandler");
      lexical=(LexicalHandler)value;
    } else {
      if(value!=null && !(value instanceof DeclHandler))throw new SAXNotSupportedException("Expected DeclHandler");
      declarations=(DeclHandler)value;
    }
  }
  @Override public void setEntityResolver(EntityResolver value){resolver=value;}
  @Override public EntityResolver getEntityResolver(){return resolver;}
  @Override public void setDTDHandler(DTDHandler value){dtd=value;}
  @Override public DTDHandler getDTDHandler(){return dtd;}
  @Override public void setContentHandler(ContentHandler value){content=value;}
  @Override public ContentHandler getContentHandler(){return content;}
  @Override public void setErrorHandler(ErrorHandler value){errors=value;}
  @Override public ErrorHandler getErrorHandler(){return errors;}
  @Override public void parse(String systemId) throws IOException,SAXException {parse(new InputSource(systemId));}

  @Override public void parse(final InputSource source) throws IOException,SAXException {
    if(parsing)throw new SAXException("Reentrant XML parsing is unsupported");
    if(source==null)throw new NullPointerException("source");
    Reader reader=source.getCharacterStream();
    XmlEncoding decoded=null;
    if(reader==null) {
      if(source.getByteStream()==null)throw new IOException("PortableXmlReader requires buffered input; system-ID loading is unsupported: "+source.getSystemId());
      decoded=XmlEncoding.read(source.getByteStream(),source.getEncoding(),maxChars);reader=decoded.reader;
    }
    final String byteEncoding=decoded==null?null:decoded.encoding;
    final ContentHandler handler=content==null?new DefaultHandler():content;
    final XmlParser<SAXException> parser=new XmlParser<>(reader,new XmlParser.Events<SAXException>() {
      @Override public void startElement(String uri,String local,String name,List<XmlParser.Attribute> attributes) throws SAXException {
        List<XmlParser.Attribute> selected=new ArrayList<>();
        for(XmlParser.Attribute a:attributes) {
          if(!a.uri.equals(XmlParser.XMLNS_URI))selected.add(a);
          else if(prefixes)selected.add(new XmlParser.Attribute("","",a.qName,a.value));
        }
        handler.startElement(namespaces?uri:"",namespaces?local:"",name,new AttributeView(selected,namespaces));
      }
      @Override public void endElement(String uri,String local,String name)throws SAXException{handler.endElement(namespaces?uri:"",namespaces?local:"",name);}
      @Override public void text(String text)throws SAXException{char[] chars=text.toCharArray();handler.characters(chars,0,chars.length);}
      @Override public void processingInstruction(String target,String data)throws SAXException{handler.processingInstruction(target,data);}
      @Override public void comment(String text)throws SAXException{if(lexical!=null){char[] chars=text.toCharArray();lexical.comment(chars,0,chars.length);}}
      @Override public void startCdata()throws SAXException{if(lexical!=null)lexical.startCDATA();}
      @Override public void endCdata()throws SAXException{if(lexical!=null)lexical.endCDATA();}
      @Override public void startPrefix(String prefix,String uri)throws SAXException{if(namespaces)handler.startPrefixMapping(prefix,uri);}
      @Override public void endPrefix(String prefix)throws SAXException{if(namespaces)handler.endPrefixMapping(prefix);}
      @Override public void declaration(String encoding)throws SAXException {
        if(encoding!=null && byteEncoding!=null)try {
          if(!XmlEncoding.compatible(XmlEncoding.canonical(encoding),byteEncoding))throw new SAXException("XML declaration conflicts with byte encoding");
        }catch(IOException e){throw new SAXException(e);}
      }
    },maxChars,maxToken,maxDepth);
    Locator locator=new Locator(){
      @Override public String getPublicId(){return source.getPublicId();}
      @Override public String getSystemId(){return source.getSystemId();}
      @Override public int getLineNumber(){return parser.getLine();}
      @Override public int getColumnNumber(){return parser.getColumn();}
    };
    parsing=true;
    try {
      handler.setDocumentLocator(locator);handler.startDocument();parser.parse();handler.endDocument();
    }catch(XmlParser.ParseException e){
      SAXParseException error=new SAXParseException(e.getMessage(),source.getPublicId(),source.getSystemId(),e.line,e.column,e);
      if(errors!=null)errors.fatalError(error);
      throw error;
    }finally{parsing=false;}
  }

  private static final class AttributeView implements Attributes {
    private final List<XmlParser.Attribute> values; private final boolean namespaces;
    AttributeView(List<XmlParser.Attribute> values,boolean namespaces){this.values=values;this.namespaces=namespaces;}
    private boolean valid(int i){return i>=0&&i<values.size();}
    @Override public int getLength(){return values.size();}
    @Override public String getURI(int i){return valid(i)?namespaces?values.get(i).uri:"":null;}
    @Override public String getLocalName(int i){return valid(i)?namespaces?values.get(i).localName:"":null;}
    @Override public String getQName(int i){return valid(i)?values.get(i).qName:null;}
    @Override public String getType(int i){return valid(i)?"CDATA":null;}
    @Override public String getValue(int i){return valid(i)?values.get(i).value:null;}
    @Override public int getIndex(String uri,String local){for(int i=0;i<values.size();i++)if(getURI(i).equals(uri)&&getLocalName(i).equals(local))return i;return -1;}
    @Override public int getIndex(String name){for(int i=0;i<values.size();i++)if(getQName(i).equals(name))return i;return -1;}
    @Override public String getType(String uri,String local){return getType(getIndex(uri,local));}
    @Override public String getType(String name){return getType(getIndex(name));}
    @Override public String getValue(String uri,String local){return getValue(getIndex(uri,local));}
    @Override public String getValue(String name){return getValue(getIndex(name));}
  }
}
