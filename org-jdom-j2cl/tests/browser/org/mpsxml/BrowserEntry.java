package org.mpsxml;
import jsinterop.annotations.JsMethod;
public final class BrowserEntry {
  @JsMethod public static String run() throws Exception {return ContractScenarios.run()+"\n"+PortableScenarios.run();}
  @JsMethod public static String roundTrip(String xml) throws Exception {
    org.jdom.Document document=org.jdom.mps.MpsXml.parse(xml);
    org.jdom.Element node=document.getRootElement().getChild("node");
    if(node!=null) node.getChild("property").setAttribute("value","browser edit <>&\"\n\r\t🙂");
    String saved=org.jdom.mps.MpsXml.createOutputter().outputString(document);
    ContractScenarios.equal(saved,org.jdom.mps.MpsXml.createOutputter().outputString(org.jdom.mps.MpsXml.parse(saved)));
    return saved;
  }
}
