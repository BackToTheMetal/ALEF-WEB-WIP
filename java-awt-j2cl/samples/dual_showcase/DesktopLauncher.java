package org.webawt.samples.dualshowcase;

import java.awt.EventQueue;

public final class DesktopLauncher {
    private DesktopLauncher() {
    }

    public static void main(String[] arguments) throws Exception {
        DesktopFonts.install();
        EventQueue.invokeLater(() -> {
            DualShowcase showcase = new DualShowcase("JDK java.awt / desktop peers");
            showcase.setFontLoading(DesktopFonts::load);
            showcase.show();
        });
    }
}
