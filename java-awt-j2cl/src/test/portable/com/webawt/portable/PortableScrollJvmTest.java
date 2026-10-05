package com.webawt.portable;

import org.junit.Test;

public final class PortableScrollJvmTest {
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
}
