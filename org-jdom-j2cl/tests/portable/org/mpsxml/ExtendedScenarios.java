package org.mpsxml;

import java.io.*;
import org.jdom.*;
import org.jdom.input.SAXBuilder;
import org.jdom.input.sax.PreservingSAXHandler;
import org.jdom.internal.xml.PortableXmlReader;
import org.jdom.internal.xml.XmlStreamWriter;
import org.jdom.output.*;
import org.jdom.filter2.Filters;
import org.jdom.xpath.XPathFactory;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

/** Explicit profile boundaries, stream ownership, encodings and large in-memory models. */
public final class ExtendedScenarios {
  public static void run() throws Exception {
    SAXBuilder builder=new SAXBuilder();
    builder.setSAXHandlerFactory(PreservingSAXHandler::new);
    Document mixed=builder.build(new StringReader("<!--before--><?before data?><r>a<!--inside--><![CDATA[b]]><?inside data?>c</r><!--after-->"));
    ContractScenarios.equal(4,mixed.getContentSize());
    ContractScenarios.equal(5,mixed.getRootElement().getContentSize());
    ContractScenarios.equal("abc",mixed.getRootElement().getText());
    ContractScenarios.check(mixed.getRootElement().getContent(1) instanceof Comment);
    ContractScenarios.check(mixed.getRootElement().getContent(2) instanceof CDATA);
    ContractScenarios.check(mixed.getRootElement().getContent(3) instanceof ProcessingInstruction);
    ContractScenarios.equal(new XMLOutputter().outputString(mixed),new XMLOutputter().outputString(mixed.clone()));
    XMLOutputter preserving=new XMLOutputter(Format.getRawFormat().setPreserveMiscContent(true));
    String preserved=preserving.outputString(mixed);
    ContractScenarios.check(preserved.contains("<!--before--><?before data?>"));
    ContractScenarios.check(preserved.contains("a<!--inside--><![CDATA[b]]><?inside data?>c"));
    ContractScenarios.check(preserved.contains("<!--after-->"));
    ContractScenarios.equal(preserved,preserving.outputString(builder.build(new StringReader(preserved))));
    ContractScenarios.equal(preserved,((XMLOutputter)preserving.clone()).outputString(mixed));
    for(String bad:new String[]{"<?xml version='1.1'?><r/>","<r>&#x110000;</r>","<r>&#xD800;</r>","<r>\ud800</r>","<r>\udc00</r>","<r xmlns:xml='wrong'/>","<r xmlns:p=''/>","<r xmlns='http://www.w3.org/XML/1998/namespace'/>","<r a:b:c='x'/>","<r><!--x---></r>","<?xml version='1.0' encoding='UTF-8' version='1.0'?><r/>"}) {
      ContractScenarios.expect(JDOMException.class,()->builder.build(new StringReader(bad)));
    }
    for(String encoding:new String[]{"UTF-8","UTF-16","UTF-16LE","UTF-16BE","ISO-8859-1","US-ASCII"}) {
      String value="á漢🙂 <>&\"\n\r\t";
      Document document=new Document(new Element("r").setAttribute("v",value).setText(value));
      class OwnedStream extends ByteArrayOutputStream {boolean closed;@Override public void close(){closed=true;}}
      OwnedStream output=new OwnedStream();
      new XMLOutputter(Format.getRawFormat().setEncoding(encoding)).output(document,output);
      ContractScenarios.check(!output.closed);
      Element parsed=builder.build(new ByteArrayInputStream(output.toByteArray())).getRootElement();
      ContractScenarios.equal(value,parsed.getAttributeValue("v"));
      // The outputter escapes carriage returns so XML line normalization cannot lose them.
      ContractScenarios.equal(value,parsed.getText());
    }
    Format format=Format.getRawFormat();
    ContractScenarios.expect(UnsupportedOperationException.class,()->format.setEncoding("windows-1252"));
    ContractScenarios.equal("UTF-8",format.getEncoding());
    StringBuilder declaration=new StringBuilder("<?xml version='1.0' ");
    for(int i=0;i<1400;i++)declaration.append(' ');
    declaration.append("encoding='ISO-8859-1'?><r>é</r>");
    byte[] latinBytes=new byte[declaration.length()];
    for(int i=0;i<latinBytes.length;i++)latinBytes[i]=(byte)declaration.charAt(i);
    ContractScenarios.equal("é",builder.build(new ByteArrayInputStream(latinBytes)).getRootElement().getText());
    ByteArrayOutputStream bytes=new ByteArrayOutputStream();XmlStreamWriter writer=new XmlStreamWriter(bytes,"UTF-8");
    writer.write(new char[]{'\ud83d'},0,1);writer.write(new char[]{'\ude42'},0,1);writer.flush();
    ContractScenarios.equal("🙂",bytes.toString("UTF-8"));
    ContractScenarios.expect(IOException.class,()->{XmlStreamWriter w=new XmlStreamWriter(new ByteArrayOutputStream(),"UTF-8");w.write(new char[]{'\ud800'},0,1);w.close();});
    final boolean[] closed={false};
    builder.build(new StringReader("<r/>"){@Override public void close(){closed[0]=true;}});
    ContractScenarios.check(!closed[0]);
    ContractScenarios.expect(UnsupportedOperationException.class,()->XPathFactory.instance().compile("//node",Filters.element()));
    class SpecialElement extends Element {SpecialElement(){super("special");}@Override protected Element newCloneInstance(){return new SpecialElement();}}
    SpecialElement special=new SpecialElement();special.addContent(new Element("child"));
    ContractScenarios.check(special.clone() instanceof SpecialElement);
    ContractScenarios.check(special.clone().getChild("child")!=special.getChild("child"));
    ContractScenarios.expect(UnsupportedOperationException.class,()->new Element("r"){}.clone());
    PortableXmlReader reader=new PortableXmlReader();
    SAXException nested=new SAXException(null,new IOException("nested cause"));
    ContractScenarios.equal("nested cause",nested.getMessage());
    ContractScenarios.check(nested.getException()==nested.getCause());
    ContractScenarios.expect(SAXNotSupportedException.class,()->reader.setFeature("http://xml.org/sax/features/namespaces",false));
    reader.setFeature("http://xml.org/sax/features/namespace-prefixes",true);
    reader.setContentHandler(new DefaultHandler(){@Override public void startElement(String uri,String local,String name,Attributes attributes){
      ContractScenarios.equal("urn:test",uri);
      ContractScenarios.equal("urn:test",attributes.getValue("xmlns:p"));
      ContractScenarios.equal("",attributes.getURI(attributes.getIndex("xmlns:p")));
      ContractScenarios.equal("",attributes.getLocalName(attributes.getIndex("xmlns:p")));
    }});
    reader.parse(new InputSource(new StringReader("<p:r xmlns:p='urn:test'/>")));
    ContractScenarios.equal("x",builder.build(new StringReader("<?target:with:colons x?><r>x</r>")).getRootElement().getText());
    reader.setEntityResolver((publicId,systemId)->{throw new IllegalStateException("External resolver must never run");});
    ContractScenarios.expect(SAXParseException.class,()->reader.parse(new InputSource(new StringReader("<!DOCTYPE r SYSTEM 'external'><r/>"))));
    reader.setContentHandler(new DefaultHandler(){@Override public void startDocument()throws SAXException {
      try{reader.parse(new InputSource(new StringReader("<r/>")));throw new IllegalStateException("Expected reentrancy rejection");}catch(IOException e){throw new SAXException(e);}catch(SAXException expected){}
    }});
    reader.parse(new InputSource(new StringReader("<r/>")));
    StringBuilder large=new StringBuilder("<model>");
    for(int i=0;i<10000;i++)large.append("<node id='").append(i).append("'><property value='Unicode 🙂 &amp; whitespace'/></node>");
    large.append("</model>");
    Document document=builder.build(new StringReader(large.toString()));
    ContractScenarios.equal(10000,document.getRootElement().getChildren().size());
    String saved=new XMLOutputter(Format.getRawFormat()).outputString(document);
    ContractScenarios.equal(10000,builder.build(new StringReader(saved)).getRootElement().getChildren().size());
  }
}
