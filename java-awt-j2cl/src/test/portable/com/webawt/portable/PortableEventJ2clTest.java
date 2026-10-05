package com.webawt.portable;

import org.junit.Test;

public final class PortableEventJ2clTest {
    @Test
    public void describesActionEvents() {
        PortableEventScenarios.describesActionEvents();
    }

    @Test
    public void describesAdjustmentAndItemEvents() {
        PortableEventScenarios.describesAdjustmentAndItemEvents();
    }

    @Test
    public void dispatchesInvocationEvents() {
        PortableEventScenarios.dispatchesInvocationEvents();
    }

    @Test
    public void describesHierarchyAndPaintEvents() {
        PortableEventScenarios.describesHierarchyAndPaintEvents();
    }

    @Test
    public void describesTextHitsAndInputMethodEvents() {
        PortableEventScenarios.describesTextHitsAndInputMethodEvents();
    }

    @Test
    public void multicastsListeners() {
        PortableEventScenarios.multicastsListeners();
    }

    @Test
    public void describesKeyStrokes() {
        PortableEventScenarios.describesKeyStrokes();
    }

    @Test
    public void updatesEventSources() {
        PortableEventScenarios.updatesEventSources();
    }
}
