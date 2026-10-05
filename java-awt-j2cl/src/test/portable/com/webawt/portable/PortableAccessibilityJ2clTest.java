package com.webawt.portable;

import org.junit.Test;

public final class PortableAccessibilityJ2clTest {
    @Test
    public void maintainsUniqueAccessibleStates() {
        PortableAccessibilityScenarios.maintainsUniqueAccessibleStates();
    }

    @Test
    public void mergesRelationTargetsDefensively() {
        PortableAccessibilityScenarios.mergesRelationTargetsDefensively();
    }

    @Test
    public void dispatchesAccessiblePropertyChanges() {
        PortableAccessibilityScenarios.dispatchesAccessiblePropertyChanges();
    }
}
