package com.webawt.portable;

import org.junit.Test;

public final class PortableMenuJvmTest {
    @Test
    public void managesOwnershipAndInsertion() {
        PortableMenuScenarios.managesOwnershipAndInsertion();
    }

    @Test
    public void managesHelpMenusAndShortcuts() {
        PortableMenuScenarios.managesHelpMenusAndShortcuts();
    }
}
