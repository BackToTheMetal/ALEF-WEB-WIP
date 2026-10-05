// Portable SAX API compatibility declarations.
package org.xml.sax;

public class SAXException extends Exception {
  public SAXException() {} public SAXException(String message) { super(message); }
  public SAXException(Exception cause) { super(cause); } public SAXException(String message, Exception cause) { super(message, cause); }
  public Exception getException() { return getCause() instanceof Exception ? (Exception)getCause() : null; }
  @Override public String getMessage() { String message=super.getMessage(); return message==null && getCause()!=null ? getCause().getMessage() : message; }
  @Override public String toString() { return getCause()==null ? super.toString() : super.toString()+"\n"+getCause().toString(); }
}
