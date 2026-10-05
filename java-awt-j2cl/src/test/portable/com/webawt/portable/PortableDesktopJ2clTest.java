package com.webawt.portable;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public final class PortableDesktopJ2clTest {
    @Test
    public void matchesDesktopEnumSurface() {
        assertEquals("24:OPEN:MOVE_TO_TRASH:4:UNSPECIFIED:LOCK:2", PortableDesktopScenarios.enumSurface());
    }
}
