package org.mpsxml;

import java.io.StringReader;
import java.io.StringWriter;
import org.jdom.Comment;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.IllegalDataException;
import org.jdom.JDOMException;
import org.jdom.ProcessingInstruction;
import org.jdom.input.SAXBuilder;
import org.jdom.input.sax.PreservingSAXHandler;
import org.jdom.input.sax.XMLReaderJDOMFactory;
import org.jdom.internal.xml.PortableXmlReader;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.xml.sax.XMLReader;

/** Regression coverage for PI serialization and lexical-token limit boundaries. */
public final class ReviewRegressionScenarios {
  public static void run() throws Exception {
    processingInstructions();
    tokenLimits();
  }

  private static void processingInstructions() throws Exception {
    XMLOutputter output = new XMLOutputter(Format.getRawFormat().setPreserveMiscContent(true));
    for (String target : new String[] {null, "", "xml", "XML", "xMl", "bad name", "1bad", "bad?>", "bad\ud800", "\u0001"}) {
      assertInvalidPi(output, new ProcessingInstruction(target, "data"));
    }
    for (String data : new String[] {null, "?>", "before?><injected/><?pi after", "\u0000", "\ud800", "\udc00"}) {
      // Mutated nodes must be checked too; the JetBrains constructors/setters are unchecked.
      assertInvalidPi(output, new ProcessingInstruction("pi", "valid").setData(data));
    }
    SAXBuilder builder = preservingBuilder(1000);
    for (String target : new String[] {"pi", "xml-stylesheet", "target:with:colons", "\ud800\udc00", "pi\ud800\udc00"}) {
      for (String data : new String[] {"", "a<&>b", "question?", "Unicode \ud83d\ude42"}) {
        Element root = new Element("r").addContent(new ProcessingInstruction(target, data));
        Element parsed = builder.build(new StringReader(output.outputString(root))).getRootElement();
        ContractScenarios.equal(1, parsed.getContentSize());
        ProcessingInstruction pi = (ProcessingInstruction) parsed.getContent(0);
        ContractScenarios.equal(target, pi.getTarget());
        ContractScenarios.equal(data, pi.getData());
      }
    }
    // The default profile still ignores processing instructions, as the pinned fork does.
    StringWriter ignored = new StringWriter();
    new XMLOutputter().output(new ProcessingInstruction("xml", "?>"), ignored);
    ContractScenarios.equal("", ignored.toString());
  }

  private static void assertInvalidPi(XMLOutputter output, ProcessingInstruction pi) throws Exception {
    StringWriter writer = new StringWriter();
    ContractScenarios.expect(IllegalDataException.class, () -> output.output(pi, writer));
    ContractScenarios.equal("", writer.toString());
    Element root = new Element("r").addContent(pi);
    ContractScenarios.expect(IllegalDataException.class, () -> output.outputString(root));
    pi.detach();
    Document document = new Document(new Element("r")).addContent(pi);
    ContractScenarios.expect(IllegalDataException.class, () -> output.outputString(document));
  }

  private static void tokenLimits() throws Exception {
    String[] openings = {"", "<!--", "<![CDATA[", "<?p "};
    String[] closings = {"", "-->", "]]>", "?>"};
    for (int limit = 1; limit <= 6; limit++) {
      SAXBuilder builder = preservingBuilder(limit);
      for (String payload : new String[] {"", "a", "ab", "abc", "abcd", "abcde", "abcdef", "abcdefg", "ab?", "a??", "ab]", "a]]", "\ud83d\ude42a"}) {
        for (int kind = 0; kind < openings.length; kind++) {
          String xml = "<r>" + openings[kind] + payload + closings[kind] + "</r>";
          if (payload.length() > limit) {
            ContractScenarios.expect(JDOMException.class, () -> builder.build(new StringReader(xml)));
          } else {
            Element root = builder.build(new StringReader(xml)).getRootElement();
            String actual = kind == 1 ? ((Comment) root.getContent(0)).getText()
                : kind == 3 ? ((ProcessingInstruction) root.getContent(0)).getData() : root.getText();
            ContractScenarios.equal(payload, actual);
          }
        }
      }
    }
    for (String xml : new String[] {"<r><!--abc--", "<r><![CDATA[abc]]", "<r><?p abc?"}) {
      ContractScenarios.expect(JDOMException.class, () -> preservingBuilder(3).build(new StringReader(xml)));
    }
  }

  private static SAXBuilder preservingBuilder(final int tokenLimit) {
    SAXBuilder builder = new SAXBuilder(new XMLReaderJDOMFactory() {
      @Override public XMLReader createXMLReader() {
        PortableXmlReader reader = new PortableXmlReader();
        reader.setLimits(10000, tokenLimit, 10);
        return reader;
      }
      @Override public boolean isValidating() { return false; }
    });
    builder.setSAXHandlerFactory(PreservingSAXHandler::new);
    return builder;
  }
}
