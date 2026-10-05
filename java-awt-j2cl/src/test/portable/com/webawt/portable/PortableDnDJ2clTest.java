package com.webawt.portable;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public final class PortableDnDJ2clTest {
    @Test
    public void matchesPortableActionConstants() {
        assertEquals("0:1:2:3:1073741824:1073741824", PortableDnDScenarios.actionConstants());
    }

    @Test
    public void matchesPortableStringTransfer() throws Exception {
        assertEquals("2:true:portable", PortableDnDScenarios.stringSelection());
        assertEquals("application:x-java-serialized-object:true:true", PortableDnDScenarios.stringFlavor());
    }
}
