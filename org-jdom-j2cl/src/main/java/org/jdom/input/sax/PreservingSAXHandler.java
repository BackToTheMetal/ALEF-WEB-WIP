package org.jdom.input.sax;

import org.jdom.Content;
import org.jdom.JDOMFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/** Opt-in handler retaining comments and processing instructions dropped by the JetBrains default. */
public final class PreservingSAXHandler extends SAXHandler {
  private int depth;
  public PreservingSAXHandler(JDOMFactory factory) { super(factory); }
  @Override public void startDocument() { depth=0;super.startDocument(); }
  @Override public void startElement(String uri,String local,String name,Attributes attrs)throws SAXException {
    super.startElement(uri,local,name,attrs);depth++;
  }
  @Override public void endElement(String uri,String local,String name)throws SAXException {
    super.endElement(uri,local,name);depth--;
  }
  private void add(Content value)throws SAXException {
    if(depth==0)getFactory().addContent(getDocument(),value);
    else {flushCharacters();getFactory().addContent(getCurrentElement(),value);}
  }
  @Override public void comment(char[] chars,int start,int length)throws SAXException {
    add(new org.jdom.Comment(new String(chars,start,length)));
  }
  @Override public void processingInstruction(String target,String data)throws SAXException {
    add(new org.jdom.ProcessingInstruction(target,data));
  }
}

