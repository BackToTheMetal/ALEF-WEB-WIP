package com.webawt.portable;

import org.junit.Test;

public final class PortableScrollJ2clTest {
    @Test
    public void normalizesScrollbarRanges() {
        PortableScrollScenarios.normalizesScrollbarRanges();
    }

    @Test
    public void laysOutAndClampsScrollPaneContent() {
        PortableScrollScenarios.laysOutAndClampsScrollPaneContent();
    }

    @Test
    public void reportsAdjustmentChanges() {
        PortableScrollScenarios.reportsAdjustmentChanges();
    }

    @Test
    public void dispatchesScrollbarAdjustmentEvents() {
        PortableScrollScenarios.dispatchesScrollbarAdjustmentEvents();
    }
}
