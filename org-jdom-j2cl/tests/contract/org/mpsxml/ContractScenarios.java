package org.mpsxml;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.jdom.*;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

/** Runs unchanged against the pinned JetBrains sources and the browser port. */
public final class ContractScenarios {
  private static int checks;
  public static String run() throws Exception {
    checks=0;
    // The JetBrains unchecked constructors deliberately skip validation; cloning preserves that state.
    equal("\u0001",new Text(true,"\u0001").clone().getText());
    equal("]]>",new CDATA(true,"]]>").clone().getText());
    equal("bad name",new Attribute(true,"bad name","v",Namespace.NO_NAMESPACE).clone().getName());
    Element root=new Element("model").setAttribute("ref","r:123(example)");
    Document document=new Document(root);
    Element a=new Element("node").setAttribute("id","1"), b=new Element("node").setAttribute("id","2");
    root.addContent(a).addContent(new Comment("between")).addContent(b);
    equal(root,a.getParentElement());equal(document,a.getDocument());equal(2,root.getChildren().size());
    List<Element> children=root.getChildren("node");
    children.add(new Element("node").setAttribute("id","3"));equal(3,root.getChildren().size());
    Iterator<Element> iterator=children.iterator();equal(a,iterator.next());iterator.remove();equal(null,a.getParent());
    equal(2,children.size());root.addContent(0,a);equal(a,children.get(0));
    expect(IllegalAddException.class,()->root.addContent(a));
    expect(IllegalAddException.class,()->a.addContent(root));
    expect(IllegalAddException.class,()->document.addContent(new Element("second")));
    Element replacement=new Element("node").setAttribute("id","4");
    children.set(0,replacement);equal(null,a.getParent());equal(root,replacement.getParent());
    children.subList(1,2).clear();equal(null,b.getParent());equal(2,children.size());
    Element copy=root.clone();equal(null,copy.getParent());equal(null,copy.getDocument());
    copy.getChildren().get(0).setAttribute("id","changed");equal("4",root.getChildren().get(0).getAttributeValue("id"));
    copy.getAttributes().get(0).setValue("copy");equal("r:123(example)",root.getAttributeValue("ref"));
    Document docCopy=document.clone();equal(docCopy,docCopy.getRootElement().getDocument());
    Attribute attr=new Attribute("state","ready");root.setAttribute(attr);root.setAttribute("state","updated");
    equal("updated",root.getAttributeValue("state"));equal(root,attr.getParent());
    root.getAttributes().remove(root.getAttribute("state"));equal(null,root.getAttribute("state"));
    equal("fallback",root.getAttributeValue("absent","fallback"));
    Element text=new Element("value").addContent(" leading ").addContent(new Element("inner").setText("nested")).addContent(new CDATA(" trailing "));
    equal(" leading  trailing ",text.getText());equal(" leading nested trailing ",text.getValue());
    equal("leading trailing",text.getTextNormalize());text.setText("reset");equal(1,text.getContentSize());
    Namespace ns=Namespace.getNamespace("p","urn:one");
    equal(ns,Namespace.getNamespace("q","urn:one"));check(ns==Namespace.getNamespace("p","urn:one"));
    Element namespaced=new Element("root",ns).setAttribute("plain","v").setAttribute("named","n",ns);
    equal(null,namespaced.getAttribute("named"));equal("n",namespaced.getAttributeValue("named",ns));
    expect(IllegalNameException.class,()->new Element("bad name"));
    expect(IllegalDataException.class,()->new Text("\u0001"));
    expect(IllegalDataException.class,()->new Comment("bad--comment"));
    expect(IllegalDataException.class,()->new CDATA("bad]]>value"));
    Element detached=root.detach();equal(root,detached);equal(false,document.hasRootElement());equal(null,root.getParent());
    document.setRootElement(root);
    Element cloneBatch=new Element("batch");cloneBatch.addContent(root.cloneContent());equal(root.getContentSize(),cloneBatch.getContentSize());
    Element atomic=new Element("atomic");Element attached=new Element("owned");new Element("owner").addContent(attached);
    expect(IllegalAddException.class,()->atomic.addContent(Arrays.asList(new Element("free"),attached)));equal(0,atomic.getContentSize());

    String xml="<?xml version=\"1.0\" encoding=\"UTF-8\"?><p:root xmlns:p=\"urn:p\" xmlns=\"urn:def\" plain=\"a&#10;b&#9;c&#13;d\" p:n=\"yes\"><child/>before<![CDATA[<raw>]]><!--keep--><?test value?>after&#x1F642;</p:root>";
    SAXBuilder builder=new SAXBuilder();Document parsed=builder.build(new StringReader(xml));Element parsedRoot=parsed.getRootElement();
    equal("urn:p",parsedRoot.getNamespaceURI());equal("a\nb\tc\rd",parsedRoot.getAttributeValue("plain"));
    equal("urn:def",parsedRoot.getChildren().get(0).getNamespaceURI());equal("before<raw>after🙂",parsedRoot.getText());
    // The pinned JetBrains SAXHandler deliberately drops comments and PIs.
    equal(4,parsedRoot.getContentSize());check(parsedRoot.getContent(2) instanceof CDATA);
    XMLOutputter output=new XMLOutputter(Format.getRawFormat().setLineSeparator("\n"));
    String encoded=output.outputString(parsed);Document second=builder.build(new StringReader(encoded));
    equal(encoded,output.outputString(second));
    ByteArrayOutputStream bytes=new ByteArrayOutputStream();output.output(parsed,bytes);
    equal(encoded,output.outputString(builder.build(new ByteArrayInputStream(bytes.toByteArray()))));
    StringWriter writer=new StringWriter();output.output(parsed,writer);writer.write("open");check(writer.toString().endsWith("open"));
    Element lineValue=new Element("node").setAttribute("value","<>&\"\n\r\t🙂");
    XMLOutputter mps=new XMLOutputter(Format.getPrettyFormat().setLineSeparator("\n")) {
      @Override public String escapeAttributeEntities(String value) {
        return super.escapeAttributeEntities(value).replace("\n","&#10;").replace("\r","&#13;").replace("\t","&#9;");
      }
    };
    equal(lineValue.getAttributeValue("value"),builder.build(new StringReader(mps.outputString(lineValue))).getRootElement().getAttributeValue("value"));
    XMLOutputter hooks=new XMLOutputter(Format.getRawFormat()) {
      @Override public String escapeAttributeEntities(String value) {return "attribute-hook";}
      @Override public String escapeElementEntities(String value) {return "text-hook";}
    };
    equal("<node a=\"attribute-hook\">text-hook</node>",hooks.outputString(new Element("node").setAttribute("a","input").setText("input")));
    Element preserve=builder.build(new StringReader("<r xml:space='preserve'> a <c/> b </r>")).getRootElement();
    equal(" a  b ",builder.build(new StringReader(mps.outputString(preserve))).getRootElement().getText());
    for(String bad:Arrays.asList("<a><b></a>","<a x='1' x='2'/>","<p:a/>","<a>&unknown;</a>","<a>&#0;</a>","<a>]]></a>","<a/ ><b/>","<a/><b/>","<!--bad--x--><a/>","<a xmlns:p='u' xmlns:q='u' p:x='1' q:x='2'/>","<a x='<'/>")) {
      expect(JDOMException.class,()->builder.build(new StringReader(bad)));
    }
    equal("a\nb\nc",builder.build(new StringReader("<r>a\r\nb\rc</r>")).getRootElement().getText());
    equal("a b c",builder.build(new StringReader("<r a='a\tb\nc'/>")).getRootElement().getAttributeValue("a"));
    equal("]]>",builder.build(new StringReader("<r>]]&gt;</r>")).getRootElement().getText());
    SAXBuilder factoryBuilder=new SAXBuilder();
    factoryBuilder.setFactory(new DefaultJDOMFactory(){@Override public Element element(int line,int col,String name,Namespace namespace){return super.element(line,col,name,namespace).setAttribute("factory","yes");}});
    Element fromFactory=factoryBuilder.build(new StringReader("<r/>")).getRootElement();
    equal("yes",fromFactory.getAttributeValue("factory"));
    int corpusDigest=CorpusScenarios.run();
    return "checks="+checks+"; corpus="+corpusDigest+"\n"+encoded+"\n"+output.outputString(root);
  }
  public interface Action {void run() throws Exception;}
  public static void check(boolean value){checks++;if(!value)throw new IllegalStateException("Check "+checks+" failed");}
  public static void equal(Object expected,Object actual){checks++;if(!(expected==null?actual==null:expected.equals(actual)))throw new IllegalStateException("Check "+checks+": expected ["+expected+"] got ["+actual+"]");}
  public static void expect(Class<?> type,Action action)throws Exception {
    checks++;try{action.run();}catch(Exception e){if(type==e.getClass() || type==JDOMException.class && e instanceof JDOMException)return;throw new IllegalStateException("Expected "+type.getName()+" got "+e,e);}throw new IllegalStateException("Expected "+type.getName());
  }
  public static void main(String[] args)throws Exception {System.out.println(run());}
}


