package com.webawt.portable;

import org.junit.Test;

public final class PortableMenuJ2clTest {
    @Test
    public void managesOwnershipAndInsertion() {
        PortableMenuScenarios.managesOwnershipAndInsertion();
    }

    @Test
    public void managesHelpMenusAndShortcuts() {
        PortableMenuScenarios.managesHelpMenusAndShortcuts();
    }

    @Test
    public void dispatchesMenuEvents() {
        PortableMenuScenarios.dispatchesMenuEvents();
    }

    @Test
    public void exposesMenuDiagnosticsAndAccessibility() {
        PortableMenuScenarios.exposesMenuDiagnosticsAndAccessibility();
    }
}
