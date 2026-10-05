package org.mpsxml;

import java.io.StringReader;
import jetbrains.mps.persistence.MementoImpl;
import jetbrains.mps.persistence.MementoUtil;
import jetbrains.mps.util.xml.BreakParseSAXException;
import jetbrains.mps.util.xml.XMLSAXHandler;
import org.jetbrains.mps.openapi.persistence.Memento;
import org.jdom.Element;
import org.jdom.mps.MpsXml;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/** Integration with executable MPS sources, not replacement interfaces or mocks. */
public final class MpsConsumerScenarios {
  public static String run()throws Exception {
    Memento source=new MementoImpl();source.put("path","models/<example>&🙂");
    source.put("value","line\nreturn\rtab\t\"");
    source.createChild("modelRoot").put("type","default");
    source.createChild("modelRoot").createChild("sourceRoot").put("location","src");
    Element root=new Element("component");MementoUtil.writeMemento(source,root);
    String xml=MpsXml.createOutputter().outputString(root);
    Memento loaded=new MementoImpl();MementoUtil.readMemento(loaded,MpsXml.parse(xml).getRootElement());
    ContractScenarios.equal(source,loaded);
    Element roundtrip=new Element("component");MementoUtil.writeMemento(loaded,roundtrip);
    ContractScenarios.equal(xml,MpsXml.createOutputter().outputString(roundtrip));
    final BreakParseSAXException stop=new BreakParseSAXException();
    XMLSAXHandler<String> header=new XMLSAXHandler<String>() {
      @Override public void startElement(String uri,String local,String name,Attributes attributes)throws SAXException {
        if(name.equals("persistence")){ContractScenarios.equal("9",attributes.getValue("version"));throw stop;}
      }
    };
    try{MpsXml.parse(new InputSource(new StringReader("<model><persistence version='9'/><unfinished")),header);throw new IllegalStateException("Expected header abort");}
    catch(BreakParseSAXException e){ContractScenarios.check(e==stop);}
    return "MPS memento round trip and actual BreakParseSAXException passed";
  }
}
