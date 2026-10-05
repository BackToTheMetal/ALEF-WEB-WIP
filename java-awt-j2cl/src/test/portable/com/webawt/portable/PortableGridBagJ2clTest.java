package com.webawt.portable;

import org.junit.Test;

public final class PortableGridBagJ2clTest {
    @Test
    public void clonesConstraintsAndRejectsInvalidValues() {
        PortableGridBagScenarios.clonesConstraintsAndRejectsInvalidValues();
    }

    @Test
    public void distributesExtraSpaceByWeight() {
        PortableGridBagScenarios.distributesExtraSpaceByWeight();
    }

    @Test
    public void appliesInsetsFillAndAnchors() {
        PortableGridBagScenarios.appliesInsetsFillAndAnchors();
    }

    @Test
    public void resolvesRemainderRowsAndReportsGridState() {
        PortableGridBagScenarios.resolvesRemainderRowsAndReportsGridState();
    }

    @Test
    public void mirrorsColumnsForRightToLeftContainers() {
        PortableGridBagScenarios.mirrorsColumnsForRightToLeftContainers();
    }
}
