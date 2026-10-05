package org.mpsxml;

import java.io.StringReader;
import org.jdom.*;
import org.jdom.input.SAXBuilder;
import org.jdom.output.*;

/** Deterministic generated corpus, run unchanged with the reference and each browser build. */
public final class CorpusScenarios {
  private static int seed;
  private static int next(int bound){seed=seed*1664525+1013904223;return (seed&0x7fffffff)%bound;}
  public static int run()throws Exception {
    seed=20260908;
    int digest=1;
    SAXBuilder builder=new SAXBuilder();
    XMLOutputter output=new XMLOutputter(Format.getRawFormat().setLineSeparator("\n"));
    String[] values={""," a  b ","<>&\"'","line\nreturn\rtab\t","🙂漢é","x]]>y"};
    for(int sample=0;sample<160;sample++) {
      Namespace ns=Namespace.getNamespace("p","urn:"+next(5));
      Element root=new Element("root",ns);
      root.addNamespaceDeclaration(Namespace.getNamespace("q","urn:q"));
      for(int i=0,count=2+next(6);i<count;i++) {
        Element child=new Element("node"+next(4),next(2)==0?ns:Namespace.NO_NAMESPACE);
        String value=values[next(values.length)];
        child.setAttribute("id",Integer.toString(i));child.setAttribute("value",value,Namespace.getNamespace("q","urn:q"));
        child.addContent(new Text(value));
        child.addContent(new CDATA("CDATA < & ] "+sample));
        child.addContent(new Element("leaf").setText(values[next(values.length)]));
        root.addContent(child);
      }
      Element cloned=root.clone();
      ContractScenarios.equal(output.outputString(root),output.outputString(cloned));
      Element removed=cloned.getChildren().remove(0);
      ContractScenarios.equal(null,removed.getParent());
      ContractScenarios.equal(root.getChildren().size()-1,cloned.getChildren().size());
      cloned.getChildren().add(removed);
      ContractScenarios.equal(cloned,removed.getParent());
      String xml=output.outputString(new Document(cloned));
      Document parsed=builder.build(new StringReader(xml));
      String canonical=output.outputString(parsed);
      ContractScenarios.equal(canonical,output.outputString(builder.build(new StringReader(canonical))));
      ContractScenarios.equal(cloned.getValue(),parsed.getRootElement().getValue());
      digest=31*digest+canonical.hashCode();
    }
    return digest;
  }
}
