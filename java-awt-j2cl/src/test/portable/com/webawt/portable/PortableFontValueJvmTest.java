package com.webawt.portable;

import org.junit.Test;

public final class PortableFontValueJvmTest {
    @Test
    public void storesAndValidatesGlyphJustification() {
        PortableFontValueScenarios.storesAndValidatesGlyphJustification();
    }

    @Test
    public void reportsHorizontalAndVerticalGlyphMetrics() {
        PortableFontValueScenarios.reportsHorizontalAndVerticalGlyphMetrics();
    }

    @Test
    public void copiesAndComparesTransformAttributes() {
        PortableFontValueScenarios.copiesAndComparesTransformAttributes();
    }

    @Test
    public void exposesCanonicalTextAttributesAndOpenTypeTags() {
        PortableFontValueScenarios.exposesCanonicalTextAttributesAndOpenTypeTags();
    }

    @Test
    public void measuresAndDrawsGraphicAttributes() {
        PortableFontValueScenarios.measuresAndDrawsGraphicAttributes();
    }

    @Test
    public void laysOutMeasuresAndDrawsGlyphVectors() {
        PortableFontValueScenarios.laysOutMeasuresAndDrawsGlyphVectors();
    }

    @Test
    public void shapesEuropeanDigitsAcrossUnicodeRanges() {
        PortableFontValueScenarios.shapesEuropeanDigitsAcrossUnicodeRanges();
    }

    @Test
    public void laysOutCaretsHighlightsAndWrappedLines() {
        PortableFontValueScenarios.laysOutCaretsHighlightsAndWrappedLines();
    }
}
