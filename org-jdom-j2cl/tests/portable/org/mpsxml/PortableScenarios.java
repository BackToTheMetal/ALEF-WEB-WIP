package org.mpsxml;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.internal.xml.PortableXmlReader;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

public final class PortableScenarios {
  public static String run()throws Exception {
    ContractScenarios.run();
    MpsConsumerScenarios.run();
    ExtendedScenarios.run();
    ReviewRegressionScenarios.run();
    SAXBuilder builder=new SAXBuilder();
    ContractScenarios.expect(JDOMException.class,()->builder.build(new StringReader("<!DOCTYPE r SYSTEM 'https://must-not-fetch.invalid/a'><r/>")));
    ContractScenarios.expect(JDOMException.class,()->new SAXBuilder(true).build(new StringReader("<r/>")));
    ContractScenarios.expect(IOException.class,()->builder.build("https://must-not-fetch.invalid/a"));
    ContractScenarios.expect(IOException.class,()->builder.build(new ByteArrayInputStream(new byte[]{'<','r','>',(byte)0xc0,(byte)0xaf,'<','/','r','>'})));
    PortableXmlReader reader=new PortableXmlReader();
    final SAXException stop=new SAXException("header complete");
    final StringBuilder events=new StringBuilder();
    reader.setContentHandler(new DefaultHandler(){
      Locator locator;
      @Override public void setDocumentLocator(Locator value){locator=value;}
      @Override public void startElement(String uri,String local,String qName,Attributes attributes)throws SAXException {
        events.append(qName).append('@').append(locator.getLineNumber()).append(':').append(locator.getColumnNumber()).append(';');
        if(qName.equals("persistence")){ContractScenarios.equal("9",attributes.getValue("version"));throw stop;}
      }
    });
    try {reader.parse(new InputSource(new StringReader("<model>\n<persistence version='9'/><deliberately-invalid")));throw new IllegalStateException("Expected early abort");}
    catch(SAXException e){ContractScenarios.check(e==stop);}
    ContractScenarios.check(events.toString().contains("persistence@2:"));
    reader.setContentHandler(new DefaultHandler());reader.parse(new InputSource(new StringReader("<reusable/>")));
    reader.setLimits(1000,100,2);
    try {reader.parse(new InputSource(new StringReader("<a><b><c/></b></a>")));throw new IllegalStateException("Expected depth limit");}catch(SAXParseException expected){}
    reader.setLimits(10,100,100);
    try {reader.parse(new InputSource(new StringReader("<root>too long</root>")));throw new IllegalStateException("Expected input limit");}catch(SAXParseException expected){}
    for(String encoding:new String[]{"UTF-8","UTF-16LE","UTF-16BE","ISO-8859-1","US-ASCII"}) {
      // ASCII fixture deliberately exercises the declared byte decoder on all runtimes.
      String xml="<?xml version='1.0' encoding='"+encoding+"'?><r a='ok'/>";
      byte[] bytes;
      if(encoding.startsWith("UTF-16")) {
        bytes=new byte[xml.length()*2];boolean little=encoding.endsWith("LE");
        for(int i=0;i<xml.length();i++){bytes[i*2+(little?0:1)]=(byte)xml.charAt(i);}
      } else bytes=xml.getBytes("UTF-8");
      ContractScenarios.equal("ok",builder.build(new ByteArrayInputStream(bytes)).getRootElement().getAttributeValue("a"));
    }
    return "portable XML boundaries passed; "+events;
  }
  public static void main(String[] args)throws Exception{System.out.println(run());}
}
