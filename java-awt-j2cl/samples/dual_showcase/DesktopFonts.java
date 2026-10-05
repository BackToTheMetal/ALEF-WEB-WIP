package org.webawt.samples.dualshowcase;

import java.awt.Font;
import java.io.InputStream;
import java.awt.font.TextAttribute;
import java.util.Collections;

public final class DesktopFonts {
    private DesktopFonts() {
    }

    public static void install() throws Exception {
        try (InputStream stream = DesktopFonts.class.getResourceAsStream("/fonts/DejaVuSans.ttf")) {
            FontScenarios.setReference(Font.createFont(Font.TRUETYPE_FONT, stream).deriveFont(Collections.singletonMap(TextAttribute.KERNING, TextAttribute.KERNING_ON)));
        }
    }

    public static void load(String path, Runnable loaded, Runnable failed) {
        try (InputStream stream = DesktopFonts.class.getResourceAsStream("/" + path)) {
            FontScenarios.setReference(Font.createFont(Font.TRUETYPE_FONT, stream).deriveFont(Collections.singletonMap(TextAttribute.KERNING, TextAttribute.KERNING_ON)));
        } catch (Exception exception) {
            failed.run();
            return;
        }
        loaded.run();
    }
}
