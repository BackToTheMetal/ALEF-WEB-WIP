package com.webawt.portable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import org.junit.Test;

public final class PortableCheckboxTest {
    @Test
    public void preservesSelectionWhenMovingToAnEmptyGroupOrLeavingAGroup() {
        CheckboxGroup first = new CheckboxGroup();
        Checkbox selected = new Checkbox("Selected", true, first);
        CheckboxGroup second = new CheckboxGroup();
        selected.setCheckboxGroup(second);
        assertNull(first.getSelectedCheckbox());
        assertSame(selected, second.getSelectedCheckbox());
        assertTrue(selected.getState());
        selected.setCheckboxGroup(null);
        assertNull(second.getSelectedCheckbox());
        assertTrue(selected.getState());
        selected.setState(false);
        assertFalse(selected.getState());
    }

    @Test
    public void preservesTheDestinationSelectionWhenJoiningAnOccupiedGroup() {
        CheckboxGroup first = new CheckboxGroup();
        Checkbox moving = new Checkbox("Moving", true, first);
        CheckboxGroup second = new CheckboxGroup();
        Checkbox selected = new Checkbox("Selected", true, second);
        moving.setCheckboxGroup(second);
        assertNull(first.getSelectedCheckbox());
        assertSame(selected, second.getSelectedCheckbox());
        assertTrue(selected.getState());
        assertFalse(moving.getState());
        moving.setState(true);
        assertSame(moving, second.getSelectedCheckbox());
        assertFalse(selected.getState());
        moving.setState(false);
        assertTrue(moving.getState());
    }

    @Test
    public void programmaticChangesDoNotDeliverItemEvents() {
        CheckboxGroup group = new CheckboxGroup();
        Checkbox first = new Checkbox("First", true, group);
        Checkbox second = new Checkbox("Second", false, group);
        int[] events = new int[1];
        first.addItemListener(event -> events[0]++);
        second.addItemListener(event -> events[0]++);
        second.setState(true);
        first.setLabel("Updated");
        second.setCheckboxGroup(null);
        second.setState(false);
        assertEquals(0, events[0]);
    }
}
