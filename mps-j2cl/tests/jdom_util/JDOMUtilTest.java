package mpsj2cl.xml;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import jetbrains.mps.util.JDOMUtil;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.output.Format;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/** Exercises the actual JDOMUtil replacement and portable XML implementation. */
public final class JDOMUtilTest {
  private JDOMUtilTest() {
  }

  public static String run() throws Exception {
    Document document = new Document(new Element("model")
        .setAttribute("name", "caf\u00e9 & \"node\"\n\t\r")
        .addContent(new Element("value").setText("\u03bb \ud83d\ude00 <value>")));
    byte[] xml = JDOMUtil.printDocument(document);
    String text = new String(xml, StandardCharsets.UTF_8);
    require(text.contains("&#10;&#9;&#13;"), "attribute control characters escaped");
    require(text.contains("&amp; &quot;node&quot;"), "MPS attribute escaping");
    require(!text.contains("\r"), "deterministic LF output");
    Document restored = JDOMUtil.loadDocument(new ByteArrayInputStream(xml));
    require(document.getRootElement().getAttributeValue("name")
        .equals(restored.getRootElement().getAttributeValue("name")), "UTF-8 attribute round trip");
    require(document.getRootElement().getChildText("value")
        .equals(restored.getRootElement().getChildText("value")), "Unicode element round trip");

    TrackingInput input = new TrackingInput(xml);
    JDOMUtil.loadDocument(input);
    require(!input.closed, "caller-supplied input remains open");
    TrackingOutput output = new TrackingOutput();
    JDOMUtil.writeDocument(document, output);
    require(output.closed, "writeDocument retains MPS output ownership");
    require(text.equals(new String(output.toByteArray(), StandardCharsets.UTF_8)), "stream and byte output agree");
    TrackingWriter writer = new TrackingWriter();
    JDOMUtil.writeDocument(document, writer);
    require(writer.closed && text.equals(writer.toString()), "writer output and close contract");

    // Byte input must reach the XML decoder without an intervening UTF-8 Reader.
    byte[] latin1 = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?><value>caf\u00e9</value>"
        .getBytes(StandardCharsets.ISO_8859_1);
    require("caf\u00e9".equals(JDOMUtil.loadDocument(new ByteArrayInputStream(latin1))
        .getRootElement().getText()), "XML byte encoding declaration");

    final StringBuilder events = new StringBuilder();
    JDOMUtil.createSAXParser().parse(new InputSource(new StringReader("<m:model xmlns:m=\"urn:mps\">ok</m:model>")),
        new DefaultHandler() {
          @Override
          public void startElement(String uri, String localName, String qName, Attributes attributes) {
            events.append(uri).append('|').append(localName).append('|').append(qName).append('|');
          }

          @Override
          public void characters(char[] chars, int start, int length) {
            events.append(chars, start, length);
          }
        });
    require("urn:mps|model|m:model|ok".equals(events.toString()), "namespace-aware SAX events");
    final SAXException abort = new SAXException("MPS header read complete");
    try {
      JDOMUtil.createSAXParser().parse(new ByteArrayInputStream(xml), new DefaultHandler() {
        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
          throw abort;
        }
      });
      throw new AssertionError("SAX handler abort was swallowed");
    } catch (SAXException actual) {
      require(actual == abort, "SAX handler exception identity");
    }

    boolean rejectedDtd = false;
    try {
      JDOMUtil.loadDocument(new StringReader("<!DOCTYPE model [<!ENTITY x 'bad'>]><model>&x;</model>"));
    } catch (JDOMException expected) {
      rejectedDtd = true;
    }
    require(rejectedDtd, "DTD requests fail explicitly");

    JDOMUtil.MyXMLOutputter original = new JDOMUtil.MyXMLOutputter();
    original.setFormat(Format.getPrettyFormat().setLineSeparator("\n"));
    JDOMUtil.MyXMLOutputter copy = (JDOMUtil.MyXMLOutputter) original.clone();
    require(copy != original, "outputter clone is independent");
    require(copy.outputString(document).equals(original.outputString(document)), "outputter clone retains MPS escaping");
    copy.setFormat(Format.getCompactFormat());
    require(!copy.outputString(document).equals(original.outputString(document)), "clone format does not alter original");

    expectUnsupported(() -> JDOMUtil.loadDocument(new File("/model.mps")), "loadDocument(File)");
    expectUnsupported(() -> JDOMUtil.writeDocument(document, new File("/model.mps")), "writeDocument(Document, File)");
    expectUnsupported(() -> JDOMUtil.writeDocument(document, "/model.mps"), "writeDocument(Document, String)");
    return "JDOMUtil: Unicode, byte encodings, stream ownership, SAX events/abort, DTD boundary, cloning and filesystem boundary passed";
  }

  public static void main(String[] args) throws Exception {
    System.out.println(run());
  }

  private static void require(boolean condition, String message) {
    if (!condition) {
      throw new AssertionError(message);
    }
  }

  private static void expectUnsupported(Action action, String member) throws Exception {
    try {
      action.run();
      throw new AssertionError("Expected unsupported operation: " + member);
    } catch (UnsupportedOperationException expected) {
      require(expected.getMessage().contains(member), "unsupported member diagnostic");
    }
  }

  private interface Action {
    void run() throws Exception;
  }

  private static final class TrackingInput extends ByteArrayInputStream {
    private boolean closed;

    private TrackingInput(byte[] bytes) {
      super(bytes);
    }

    @Override
    public void close() {
      closed = true;
    }
  }

  private static final class TrackingOutput extends ByteArrayOutputStream {
    private boolean closed;

    @Override
    public void close() {
      closed = true;
    }
  }

  private static final class TrackingWriter extends StringWriter {
    private boolean closed;

    @Override
    public void close() {
      closed = true;
    }
  }
}
