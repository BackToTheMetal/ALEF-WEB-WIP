package com.intellij.openapi.util.text;
import java.util.ArrayList;
import java.util.List;
public final class HtmlBuilder {
  private final List<HtmlChunk> chunks = new ArrayList<>();
  public HtmlBuilder append(String text) { return append(HtmlChunk.text(text)); }
  public HtmlBuilder append(HtmlChunk chunk) { chunks.add(chunk); return this; }
  public HtmlBuilder appendRaw(String html) { return append(HtmlChunk.raw(html)); }
  public HtmlChunk toFragment() { return HtmlChunk.fragment(chunks.toArray(new HtmlChunk[0])); }
  @Override public String toString() { return toFragment().toString(); }
}
