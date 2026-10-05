package com.webawt.portable;

import org.junit.Test;

public final class PortableValueTypesJ2clTest {
    @Test
    public void matchesPortableOpenJdkScenarios() {
        PortableValueTypesScenarios.runAll();
    }
}
