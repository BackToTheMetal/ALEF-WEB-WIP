package org.webawt.internal.peer;

/** Identifies WebAWT glyphs that retain Unicode text for native browser painting. */
public interface GlyphTextSource {
    String getGlyphText(int glyphIndex);
}
