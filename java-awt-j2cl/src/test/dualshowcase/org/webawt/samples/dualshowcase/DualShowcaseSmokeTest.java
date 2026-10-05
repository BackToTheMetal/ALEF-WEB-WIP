package org.webawt.samples.dualshowcase;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public final class DualShowcaseSmokeTest {
    @Test
    public void verifiesGraphicsStatePixelsAndQueries() {
        // The portable runner uses PhantomJS, which has no native text ink bounds.
        // Actual glyph pixels run on the JDK and all three modern-browser suites.
        GraphicsStateAssertions.verify(false);
    }

    @Test
    public void exposesTheSharedGalleryModel() {
        DualShowcase showcase = new DualShowcase("test runtime");
        assertEquals(12, showcase.getPageCount());
        assertEquals("Controls & events", showcase.getSelectedPage());
    }
}
