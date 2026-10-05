// Portable SAX API compatibility declarations.
package org.xml.sax;

public class SAXParseException extends SAXException {
  private final String publicId, systemId; private final int line, column;
  public SAXParseException(String m, Locator l) { this(m,l,null); }
  public SAXParseException(String m, Locator l, Exception e) { this(m,l==null?null:l.getPublicId(),l==null?null:l.getSystemId(),l==null?-1:l.getLineNumber(),l==null?-1:l.getColumnNumber(),e); }
  public SAXParseException(String m,String p,String s,int l,int c) { this(m,p,s,l,c,null); }
  public SAXParseException(String m,String p,String s,int l,int c,Exception e) { super(m,e); publicId=p;systemId=s;line=l;column=c; }
  public String getPublicId(){return publicId;} public String getSystemId(){return systemId;}
  public int getLineNumber(){return line;} public int getColumnNumber(){return column;}
}
