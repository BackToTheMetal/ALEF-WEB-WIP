package com.intellij.lang.documentation;
import com.intellij.openapi.util.text.HtmlChunk;
public interface DocumentationMarkup {
  String CLASS_DEFINITION = "definition", CLASS_CONTENT = "content", CLASS_SECTIONS = "sections", CLASS_SECTION = "section", CLASS_GRAYED = "grayed", CLASS_CENTERED = "centered", CLASS_BOTTOM = "bottom", CLASS_TOP = "top";
  String DEFINITION_START = "<div class='definition'><pre>", DEFINITION_END = "</pre></div>", CONTENT_START = "<div class='content'>", CONTENT_END = "</div>";
  String SECTIONS_START = "<table class='sections'>", SECTIONS_END = "</table>", SECTION_HEADER_START = "<tr><td class='section'>", SECTION_SEPARATOR = "</td><td>", SECTION_START = "<p>", SECTION_END = "</td></tr>";
  String GRAYED_START = "<span class='grayed'>", GRAYED_END = "</span>";
  HtmlChunk.Element CONTENT_ELEMENT = HtmlChunk.div().setClass(CLASS_CONTENT), DEFINITION_ELEMENT = HtmlChunk.div().setClass(CLASS_DEFINITION), TOP_ELEMENT = HtmlChunk.div().setClass(CLASS_TOP), BOTTOM_ELEMENT = HtmlChunk.div().setClass(CLASS_BOTTOM), PRE_ELEMENT = HtmlChunk.tag("pre"), GRAYED_ELEMENT = HtmlChunk.span().setClass(CLASS_GRAYED), CENTERED_ELEMENT = HtmlChunk.div().setClass(CLASS_CENTERED), SECTIONS_TABLE = HtmlChunk.tag("table").setClass(CLASS_SECTIONS), SECTION_CONTENT_CELL = HtmlChunk.tag("td"), SECTION_HEADER_CELL = HtmlChunk.tag("td").setClass(CLASS_SECTION);
}
