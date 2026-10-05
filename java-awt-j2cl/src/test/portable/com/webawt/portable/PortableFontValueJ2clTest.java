package com.webawt.portable;

import org.junit.Test;

public final class PortableFontValueJ2clTest {
    @org.junit.Before
    public void installControlledCoverage() {
        // PhantomJS lacks native ink bounds. These tests exercise value/layout and
        // painting dispatch; real glyph pixels run in the three Playwright engines.
        org.webawt.internal.peer.WebAwtRuntime.installFontRasterizer(new org.webawt.internal.peer.FontRasterizer() {
            public double[] bounds(String font, String text) {
                return new double[] {0, -10, Math.max(1, text.length() * 6), 12};
            }
            public int[] rasterize(String font, String text, double[] matrix, int x, int y, int width, int height) {
                int[] coverage = new int[width * height];
                java.util.Arrays.fill(coverage, 255);
                return coverage;
            }
        });
    }

    @org.junit.After
    public void clearControlledCoverage() {
        org.webawt.internal.peer.WebAwtRuntime.installFontRasterizer(null);
    }

    @Test
    public void snapshotsStyledRangesAndRejectsMixedDirectionCarets() {
        org.webawt.samples.dualshowcase.StyledText iterator = new org.webawt.samples.dualshowcase.StyledText("Big small", 5, new int[] {4, 9}, java.util.Arrays.asList(java.util.Collections.singletonMap(java.awt.font.TextAttribute.FONT, new java.awt.Font("Dialog", 0, 24)), java.util.Collections.singletonMap(java.awt.font.TextAttribute.FONT, new java.awt.Font("Dialog", 0, 12))));
        iterator.setIndex(7);
        java.awt.font.FontRenderContext context = new java.awt.font.FontRenderContext(null, true, true);
        java.awt.font.TextMeasurer measurer = new java.awt.font.TextMeasurer(iterator, context);
        if (iterator.getIndex() != 7) { throw new AssertionError("measurement mutated iterator"); }
        java.awt.font.TextLayout layout = measurer.getLayout(5, 14);
        if (Math.abs(layout.getAdvance() - measurer.getAdvanceBetween(5, 14)) > 0.01) { throw new AssertionError("styled run advances differ"); }
        java.awt.font.TextLayout mixed = new java.awt.font.TextLayout("A\u05d0", new java.awt.Font("Dialog", 0, 12), context);
        try { mixed.getNextRightHit(0); throw new AssertionError("mixed bidi caret must fail explicitly"); } catch (UnsupportedOperationException expected) {}
    }

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
