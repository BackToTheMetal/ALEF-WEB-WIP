package com.webawt.portable;

import java.beans.PropertyVetoException;
import org.junit.Test;

public final class PortableBeansJ2clTest {
    @Test
    public void dispatchesPropertyChanges() {
        PortableBeansScenarios.dispatchesPropertyChanges();
    }

    @Test
    public void describesIndexedChanges() {
        PortableBeansScenarios.describesIndexedChanges();
    }

    @Test
    public void rollsBackVetoNotifications() throws PropertyVetoException {
        PortableBeansScenarios.rollsBackVetoNotifications();
    }
}
