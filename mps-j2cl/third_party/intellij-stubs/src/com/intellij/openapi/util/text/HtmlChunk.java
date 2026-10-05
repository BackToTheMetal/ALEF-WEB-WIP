package com.intellij.openapi.util.text;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
/** HTML construction escapes text and attributes; raw input must be marked explicitly. */
public abstract class HtmlChunk {
  public static HtmlChunk text(String text) { return raw(Strings.escapeXmlEntities(text)); }
  public static HtmlChunk raw(String html) { return new HtmlChunk() { @Override public String toString() { return html; } }; }
  public static HtmlChunk nbsp() { return raw("&nbsp;"); }
  public static HtmlChunk br() { return raw("<br>"); }
  public static Element tag(String name) { return new Element(name, new LinkedHashMap<>(), new ArrayList<>()); }
  public static Element div() { return tag("div"); }
  public static Element span() { return tag("span"); }
  public static Element font(String color) { return tag("font").attr("color", color); }
  public static Element font(int size) { return tag("font").attr("size", Integer.toString(size)); }
  public static Element link(String target, String text) { return link(target, text(text)); }
  public static Element link(String target, HtmlChunk text) { return tag("a").attr("href", target).child(text); }
  public static HtmlChunk fragment(HtmlChunk... children) { StringBuilder result = new StringBuilder(); for (HtmlChunk child : children) result.append(child); return raw(result.toString()); }
  public HtmlChunk bold() { return wrapWith("b"); }
  public HtmlChunk italic() { return wrapWith("i"); }
  public HtmlChunk wrapWith(String tag) { return tag(tag).child(this); }
  public static final class Element extends HtmlChunk {
    private final String name; private final Map<String, String> attributes; private final List<HtmlChunk> children;
    Element(String name, Map<String, String> attributes, List<HtmlChunk> children) {
      validateName(name); this.name = name; this.attributes = attributes; this.children = children;
    }
    private static void validateName(String name) {
      if (name.isEmpty()) throw new IllegalArgumentException("Empty HTML name");
      for (int i = 0; i < name.length(); i++) { char c = name.charAt(i); if (!(c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z' || c >= '0' && c <= '9' || c == '-' || c == ':')) throw new IllegalArgumentException("Invalid HTML name: " + name); }
    }
    public Element attr(String key, String value) { validateName(key); Map<String, String> next = new LinkedHashMap<>(attributes); next.put(key, value); return new Element(name, next, children); }
    public Element setClass(String value) { return attr("class", value); }
    public Element style(String value) { return attr("style", value); }
    public Element child(HtmlChunk value) { List<HtmlChunk> next = new ArrayList<>(children); next.add(value); return new Element(name, attributes, next); }
    public Element children(HtmlChunk... values) { Element result = this; for (HtmlChunk value : values) result = result.child(value); return result; }
    public Element addText(String value) { return child(text(value)); }
    @Override public String toString() {
      StringBuilder result = new StringBuilder("<").append(name);
      for (Map.Entry<String, String> attribute : attributes.entrySet()) result.append(' ').append(attribute.getKey()).append("=\"").append(Strings.escapeXmlEntities(attribute.getValue())).append('"');
      result.append('>'); for (HtmlChunk child : children) result.append(child);
      if (!name.equals("br") && !name.equals("hr") && !name.equals("img") && !name.equals("input") && !name.equals("meta") && !name.equals("link")) result.append("</").append(name).append('>');
      return result.toString();
    }
  }
}
