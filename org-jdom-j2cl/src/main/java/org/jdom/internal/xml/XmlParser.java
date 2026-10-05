package org.jdom.internal.xml;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Bounded XML 1.0 event parser. It never resolves external resources. */
public final class XmlParser<E extends Exception> {
  public static final String XML_URI = "http://www.w3.org/XML/1998/namespace";
  public static final String XMLNS_URI = "http://www.w3.org/2000/xmlns/";
  public static final int DEFAULT_MAX_CHARS = 16 * 1024 * 1024;
  public static final int DEFAULT_MAX_TOKEN = 1024 * 1024;
  public static final int DEFAULT_MAX_DEPTH = 256;

  public static final class Attribute {
    public final String uri, localName, qName, value;
    public Attribute(String uri, String localName, String qName, String value) {
      this.uri = uri; this.localName = localName; this.qName = qName; this.value = value;
    }
  }

  public interface Events<E extends Exception> {
    void startElement(String uri, String local, String name, List<Attribute> attributes) throws E;
    void endElement(String uri, String local, String name) throws E;
    void text(String text) throws E;
    void processingInstruction(String target, String data) throws E;
    void comment(String text) throws E;
    void startCdata() throws E;
    void endCdata() throws E;
    void startPrefix(String prefix, String uri) throws E;
    void endPrefix(String prefix) throws E;
    void declaration(String encoding) throws E;
  }

  public static final class ParseException extends Exception {
    public final int line, column;
    ParseException(String message, int line, int column) {
      super(message); this.line = line; this.column = column;
    }
  }

  private final Reader input;
  private final Events<E> events;
  private final int maxChars, maxToken, maxDepth;
  private final char[] buffer = new char[4096];
  private int bufferIndex, bufferLength, pending = -2, lookahead = -2, count;
  private int line = 1, column = 1;

  public XmlParser(Reader input, Events<E> events) {
    this(input, events, DEFAULT_MAX_CHARS, DEFAULT_MAX_TOKEN, DEFAULT_MAX_DEPTH);
  }

  public XmlParser(Reader input, Events<E> events, int maxChars, int maxToken, int maxDepth) {
    if (input == null || events == null) throw new NullPointerException();
    if (maxChars < 1 || maxToken < 1 || maxDepth < 1) throw new IllegalArgumentException("Positive XML limits required");
    this.input = input; this.events = events; this.maxChars = maxChars; this.maxToken = maxToken; this.maxDepth = maxDepth;
  }

  public int getLine() { return line; }
  public int getColumn() { return column; }

  public void parse() throws IOException, ParseException, E {
    boolean first = true, root = false;
    if (peek() == 0xfeff) take();
    Map<String,String> scope = new HashMap<>();
    scope.put("", ""); scope.put("xml", XML_URI);
    while (peek() != -1) {
      if (space(peek())) { whitespace(); first = false; continue; }
      expect('<');
      if (peek() == '?') { take(); instruction(first); }
      else if (peek() == '!') { take(); comment(); }
      else {
        if (root) fail("Multiple root elements");
        element(scope, 1); root = true;
      }
      first = false;
    }
    if (!root) fail("Missing root element");
  }

  private void element(Map<String,String> parent, int depth) throws IOException, ParseException, E {
    if (depth > maxDepth) fail("XML depth limit exceeded");
    String qName = name();
    List<String> names = new ArrayList<>(), values = new ArrayList<>(), declared = new ArrayList<>();
    Map<String,String> scope = new HashMap<>(parent);
    Set<String> unique = new HashSet<>();
    while (true) {
      boolean separated = whitespace();
      if (peek() == '/' || peek() == '>') break;
      if (!separated) fail("Whitespace required before attribute");
      String attr = name();
      if (!unique.add(attr)) fail("Duplicate attribute " + attr);
      whitespace(); expect('='); whitespace();
      String value = attributeValue();
      names.add(attr); values.add(value);
      if (attr.equals("xmlns") || attr.startsWith("xmlns:")) {
        String prefix = attr.equals("xmlns") ? "" : attr.substring(6);
        if (prefix.equals("xmlns") || value.equals(XMLNS_URI) || prefix.equals("xml") != value.equals(XML_URI)
            || (!prefix.isEmpty() && value.isEmpty())) fail("Invalid namespace binding");
        scope.put(prefix, value); declared.add(prefix);
      }
    }
    boolean empty = peek() == '/';
    if (empty) take();
    expect('>');
    String uri = uri(qName, scope, false), local = local(qName);
    List<Attribute> attrs = new ArrayList<>();
    Set<String> expanded = new HashSet<>();
    for (int i = 0; i < names.size(); i++) {
      String n = names.get(i);
      String aUri = n.equals("xmlns") || n.startsWith("xmlns:") ? XMLNS_URI : uri(n, scope, true);
      String aLocal = local(n);
      if (!expanded.add(aUri + '\u0000' + aLocal)) fail("Duplicate expanded attribute " + n);
      attrs.add(new Attribute(aUri, aLocal, n, values.get(i)));
    }
    for (String prefix : declared) events.startPrefix(prefix, scope.get(prefix));
    events.startElement(uri, local, qName, attrs);
    if (!empty) {
      while (true) {
        if (peek() == -1) fail("Unclosed element " + qName);
        if (peek() != '<') {
          StringBuilder text = new StringBuilder();
          int brackets = 0;
          while (peek() != '<' && peek() != -1) {
            if (peek() == '&') { text.append(entity()); brackets = 0; }
            else {
              int c = take();
              if (c == '>' && brackets >= 2) fail("CDATA delimiter in character content");
              brackets = c == ']' ? brackets + 1 : 0;
              append(text, c);
            }
            limit(text);
          }
          // Only a literal closing CDATA delimiter is forbidden, not character references.
          events.text(text.toString());
          continue;
        }
        take();
        if (peek() == '/') {
          take(); String end = name(); whitespace(); expect('>');
          if (!qName.equals(end)) fail("Mismatched closing element " + end);
          break;
        }
        if (peek() == '?') { take(); instruction(false); }
        else if (peek() == '!') {
          take();
          if (peek() == '[') {
            literal("[CDATA["); events.startCdata(); events.text(until("]]>")); events.endCdata();
          } else comment();
        } else element(scope, depth + 1);
      }
    }
    events.endElement(uri, local, qName);
    for (int i = declared.size()-1; i >= 0; i--) events.endPrefix(declared.get(i));
  }

  private String uri(String name, Map<String,String> scope, boolean attribute) throws ParseException {
    int colon = name.indexOf(':');
    if (colon < 0) return attribute ? "" : scope.get("");
    String prefix = name.substring(0, colon);
    if (prefix.equals("xmlns") || !scope.containsKey(prefix)) fail("Unbound namespace prefix " + prefix);
    return scope.get(prefix);
  }
  private static String local(String name) { return name.substring(name.indexOf(':') + 1); }

  private void instruction(boolean declarationAllowed) throws IOException, ParseException, E {
    String target = name(false);
    if (target.equalsIgnoreCase("xml")) {
      if (!declarationAllowed || !target.equals("xml")) fail("Misplaced XML declaration");
      if (!whitespace()) fail("Malformed XML declaration");
      String version = pseudoAttribute("version");
      if (!version.equals("1.0")) fail("Only XML 1.0 is supported");
      String encoding = null;
      boolean separated = whitespace();
      if (peek() == 'e') {
        if (!separated) fail("Malformed XML declaration");
        encoding = pseudoAttribute("encoding");
        if (!encoding.matches("[A-Za-z][A-Za-z0-9._-]*")) fail("Invalid encoding name");
        separated = whitespace();
      }
      if (peek() == 's') {
        if (!separated) fail("Malformed XML declaration");
        String standalone = pseudoAttribute("standalone");
        if (!standalone.equals("yes") && !standalone.equals("no")) fail("Invalid standalone value");
        whitespace();
      }
      literal("?>"); events.declaration(encoding); return;
    }
    String data = "";
    if (whitespace()) data = until("?>");
    else literal("?>");
    events.processingInstruction(target, data);
  }

  private String pseudoAttribute(String expected) throws IOException, ParseException {
    if (!name().equals(expected)) fail("Expected " + expected);
    whitespace(); expect('='); whitespace();
    int quote = take(); if (quote != '\'' && quote != '"') fail("Expected quote");
    StringBuilder value = new StringBuilder();
    while (peek() != quote) { if (peek() == -1 || peek() == '&' || peek() == '<') fail("Invalid declaration"); append(value,take()); limit(value); }
    take(); return value.toString();
  }

  private void comment() throws IOException, ParseException, E {
    if (peek() != '-') fail("DTD and declarations are unsupported");
    literal("--");
    String text = until("-->");
    if (text.contains("--") || text.endsWith("-")) fail("Invalid XML comment");
    events.comment(text);
  }

  private String attributeValue() throws IOException, ParseException {
    int quote = take(); if (quote != '\'' && quote != '"') fail("Expected attribute quote");
    StringBuilder value = new StringBuilder();
    while (peek() != quote) {
      if (peek() == -1 || peek() == '<') fail("Invalid attribute value");
      if (peek() == '&') value.append(entity());
      else { int c = take(); append(value, space(c) ? ' ' : c); }
      limit(value);
    }
    take(); return value.toString();
  }

  private String entity() throws IOException, ParseException {
    expect('&'); StringBuilder entity = new StringBuilder();
    while (peek() != ';') {
      int c = take(); if (c == -1 || c == '<' || c == '&' || space(c)) fail("Malformed entity reference");
      append(entity, c); limit(entity);
    }
    take(); String name = entity.toString();
    switch (name) {
      case "lt": return "<"; case "gt": return ">"; case "amp": return "&"; case "apos": return "'"; case "quot": return "\"";
      default:
        if (!name.startsWith("#")) { fail("Undeclared entity " + name); return null; }
        boolean hex = name.startsWith("#x");
        String digits = name.substring(hex ? 2 : 1);
        if (digits.isEmpty()) fail("Empty character reference");
        int code = 0;
        for (int i=0;i<digits.length();i++) {
          char c = digits.charAt(i); int d = c >= '0' && c <= '9' ? c-'0' : hex && c >= 'a' && c <= 'f' ? c-'a'+10 : hex && c >= 'A' && c <= 'F' ? c-'A'+10 : -1;
          if (d < 0 || code > 0x10ffff/(hex?16:10)) fail("Invalid character reference");
          code = code*(hex?16:10)+d;
        }
        if (!xmlChar(code)) fail("Invalid XML character reference");
        return new String(Character.toChars(code));
    }
  }

  private String until(String delimiter) throws IOException, ParseException {
    StringBuilder text = new StringBuilder();
    while (true) {
      int c = take(); if (c == -1) fail("Unterminated XML token"); append(text, c);
      int len = text.length();
      if (len >= delimiter.length() && text.substring(len-delimiter.length()).equals(delimiter)) {
        return text.substring(0, len-delimiter.length());
      }
      // A suffix may still become the closing delimiter; only count confirmed payload.
      int pending = Math.min(len, delimiter.length() - 1);
      while (pending > 0 && !text.substring(len - pending).equals(delimiter.substring(0, pending))) pending--;
      if (len - pending > maxToken) fail("XML token limit exceeded");
    }
  }

  private String name() throws IOException, ParseException {
    return name(true);
  }

  private String name(boolean qualified) throws IOException, ParseException {
    StringBuilder name = new StringBuilder();
    int c = peek(); if (!nameStart(c)) fail("Invalid XML name");
    append(name, take()); limit(name);
    while (namePart(peek())) { append(name, take()); limit(name); }
    String value = name.toString();
    int colon = value.indexOf(':');
    if (qualified && (colon == 0 || colon == value.length()-1 || colon != value.lastIndexOf(':')
        || colon >= 0 && !nameStart(value.codePointAt(colon+1)))) fail("Invalid qualified name");
    return value;
  }

  /** XML 1.0 Name validation shared with output, including supplementary code points. */
  public static boolean isXmlName(String value) {
    if (value == null || value.isEmpty()) return false;
    int first = value.codePointAt(0);
    if (!nameStart(first)) return false;
    for (int i = Character.charCount(first); i < value.length();) {
      int c = value.codePointAt(i);
      if (!namePart(c)) return false;
      i += Character.charCount(c);
    }
    return true;
  }

  private static boolean nameStart(int c) {
    return c == ':' || c == '_' || c >= 'A' && c <= 'Z' || c >= 'a' && c <= 'z'
      || c >= 0xc0 && c <= 0xd6 || c >= 0xd8 && c <= 0xf6 || c >= 0xf8 && c <= 0x2ff
      || c >= 0x370 && c <= 0x37d || c >= 0x37f && c <= 0x1fff || c >= 0x200c && c <= 0x200d
      || c >= 0x2070 && c <= 0x218f || c >= 0x2c00 && c <= 0x2fef || c >= 0x3001 && c <= 0xd7ff
      || c >= 0xf900 && c <= 0xfdcf || c >= 0xfdf0 && c <= 0xfffd || c >= 0x10000 && c <= 0xeffff;
  }
  private static boolean namePart(int c) { return nameStart(c) || c == '-' || c == '.' || c >= '0' && c <= '9' || c == 0xb7 || c >= 0x300 && c <= 0x36f || c >= 0x203f && c <= 0x2040; }
  private static boolean xmlChar(int c) { return c == 9 || c == 10 || c == 13 || c >= 0x20 && c <= 0xd7ff || c >= 0xe000 && c <= 0xfffd || c >= 0x10000 && c <= 0x10ffff; }
  private static boolean space(int c) { return c == ' ' || c == '\n' || c == '\r' || c == '\t'; }
  private static void append(StringBuilder out, int c) { out.appendCodePoint(c); }
  private void limit(StringBuilder token) throws ParseException { if (token.length() > maxToken) fail("XML token limit exceeded"); }
  private void fail(String message) throws ParseException { throw new ParseException(message, line, column); }
  private void expect(int c) throws IOException, ParseException { if (take() != c) fail("Expected '"+(char)c+"'"); }
  private void literal(String s) throws IOException, ParseException { for (int i=0;i<s.length();i++) expect(s.charAt(i)); }
  private boolean whitespace() throws IOException, ParseException { boolean found=false; while(space(peek())) { take(); found=true; } return found; }
  private int take() throws IOException, ParseException {
    int c=peek(); lookahead=-2;
    if(c=='\n') {line++;column=1;} else if(c!=-1) column += Character.charCount(c);
    return c;
  }
  private int peek() throws IOException, ParseException {
    if (lookahead != -2) return lookahead;
    int c=raw();
    if(c=='\r') {int next=raw(); if(next!='\n') pending=next; c='\n';}
    if(c>=0xd800 && c<=0xdbff) {
      int low=raw(); if(low<0xdc00 || low>0xdfff) fail("Unpaired surrogate");
      c=Character.toCodePoint((char)c,(char)low);
    }
    if(c!=-1 && !xmlChar(c)) fail("Invalid XML character");
    lookahead=c; return c;
  }
  private int raw() throws IOException, ParseException {
    if(pending!=-2) {int c=pending;pending=-2;return c;}
    if(bufferLength<0) return -1;
    if(bufferIndex==bufferLength) {
      bufferLength=input.read(buffer,0,buffer.length); bufferIndex=0;
      if(bufferLength<0) return -1;
      if(bufferLength==0) throw new IOException("Reader made no progress");
    }
    if(++count>maxChars) fail("XML input limit exceeded");
    return buffer[bufferIndex++];
  }
}
