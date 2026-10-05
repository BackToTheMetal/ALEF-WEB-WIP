package com.webawt.portable;

import org.junit.Test;

public final class PortableComponentJ2clTest {
    @Test
    public void maintainsComponentHierarchyAndBounds() {
        PortableComponentScenarios.maintainsComponentHierarchyAndBounds();
    }

    @Test
    public void laysOutFlowRows() {
        PortableComponentScenarios.laysOutFlowRows();
    }

    @Test
    public void laysOutBorderRegions() {
        PortableComponentScenarios.laysOutBorderRegions();
    }

    @Test
    public void laysOutUniformGridCells() {
        PortableComponentScenarios.laysOutUniformGridCells();
    }

    @Test
    public void switchesNamedCards() {
        PortableComponentScenarios.switchesNamedCards();
    }

    @Test
    public void traversesFocusInContainerOrder() {
        PortableComponentScenarios.traversesFocusInContainerOrder();
    }

    @Test
    public void dispatchesHierarchyAndInputMethodEvents() {
        PortableComponentScenarios.dispatchesHierarchyAndInputMethodEvents();
    }

    @Test
    public void exposesLegacyComponentContracts() {
        PortableComponentScenarios.exposesLegacyComponentContracts();
    }

    @Test
    public void dispatchesLegacyEventsAndPrimitiveProperties() {
        PortableComponentScenarios.dispatchesLegacyEventsAndPrimitiveProperties();
    }
}
