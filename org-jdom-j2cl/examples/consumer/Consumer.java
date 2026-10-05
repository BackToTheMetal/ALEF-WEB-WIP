package example;
import java.io.StringReader;
import java.io.StringWriter;
import jsinterop.annotations.JsMethod;
import org.jdom.*;
import org.jdom.input.SAXBuilder;
import org.jdom.output.*;

/** Compiled from an independent Bazel module against the packaged source artifact. */
public final class Consumer {
  @JsMethod public static String run()throws Exception {
    Document document=new SAXBuilder().build(new StringReader("<model><node id='1'/></model>"));
    document.getRootElement().getChild("node").setAttribute("id","2");
    StringWriter result=new StringWriter();
    XMLOutputter output=new XMLOutputter(Format.getRawFormat().setOmitDeclaration(true)) {
      @Override public String escapeAttributeEntities(String value){return "hook-"+value;}
    };
    output.output(document,result);
    return result.toString();
  }
}
