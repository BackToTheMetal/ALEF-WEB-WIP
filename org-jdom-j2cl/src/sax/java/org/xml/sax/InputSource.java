// Portable SAX API compatibility declarations.
package org.xml.sax;

public class InputSource {
  private String publicId,systemId,encoding; private java.io.Reader reader; private java.io.InputStream stream;
  public InputSource() {} public InputSource(String id){systemId=id;} public InputSource(java.io.Reader r){reader=r;} public InputSource(java.io.InputStream s){stream=s;}
  public void setPublicId(String v){publicId=v;} public String getPublicId(){return publicId;}
  public void setSystemId(String v){systemId=v;} public String getSystemId(){return systemId;}
  public void setEncoding(String v){encoding=v;} public String getEncoding(){return encoding;}
  public void setCharacterStream(java.io.Reader v){reader=v;} public java.io.Reader getCharacterStream(){return reader;}
  public void setByteStream(java.io.InputStream v){stream=v;} public java.io.InputStream getByteStream(){return stream;}
}
