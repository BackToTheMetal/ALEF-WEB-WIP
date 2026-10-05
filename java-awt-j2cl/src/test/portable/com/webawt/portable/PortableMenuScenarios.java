package com.webawt.portable;

import java.awt.CheckboxMenuItem;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.MenuShortcut;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Enumeration;
import javax.accessibility.AccessibleRole;

public final class PortableMenuScenarios {
    private PortableMenuScenarios() {
    }

    public static void main(String[] arguments) {
        managesOwnershipAndInsertion();
        managesHelpMenusAndShortcuts();
        dispatchesMenuEvents();
        exposesMenuDiagnosticsAndAccessibility();
    }

    public static void managesOwnershipAndInsertion() {
        Menu first = new Menu("First");
        Menu second = new Menu("Second");
        MenuItem shared = new MenuItem("Shared");
        assertSame(shared, first.add(shared));
        assertSame(first, shared.getParent());
        second.add(shared);
        assertEquals(Integer.valueOf(0), Integer.valueOf(first.getItemCount()));
        assertSame(second, shared.getParent());
        second.insert(new MenuItem("Start"), 0);
        second.insert(new MenuItem("End"), 20);
        second.insertSeparator(1);
        assertEquals(Integer.valueOf(4), Integer.valueOf(second.countItems()));
        assertEquals("Start", second.getItem(0).getLabel());
        assertEquals("-", second.getItem(1).getLabel());
        assertEquals("End", second.getItem(3).getLabel());
        second.remove(shared);
        assertSame(null, shared.getParent());
        second.removeAll();
        assertEquals(Integer.valueOf(0), Integer.valueOf(second.getItemCount()));
        assertThrowsIllegalArgument(() -> second.insert(new MenuItem("Invalid"), -1));
    }

    public static void managesHelpMenusAndShortcuts() {
        MenuBar bar = new MenuBar();
        Menu file = bar.add(new Menu("File"));
        Menu recent = new Menu("Recent");
        MenuShortcut openShortcut = new MenuShortcut('O');
        MenuShortcut recentShortcut = new MenuShortcut('R', true);
        MenuItem open = file.add(new MenuItem("Open", openShortcut));
        recent.add(new MenuItem("One", recentShortcut));
        file.add(recent);
        assertEquals(Integer.valueOf(1), Integer.valueOf(bar.countMenus()));
        assertSame(open, bar.getShortcutMenuItem(new MenuShortcut('O')));
        assertSame(recent.getItem(0), bar.getShortcutMenuItem(new MenuShortcut('R', true)));
        Enumeration<MenuShortcut> shortcuts = bar.shortcuts();
        assertEquals(openShortcut, shortcuts.nextElement());
        assertEquals(recentShortcut, shortcuts.nextElement());
        assertFalse(shortcuts.hasMoreElements());
        bar.deleteShortcut(new MenuShortcut('O'));
        assertSame(null, open.getShortcut());
        Menu help = new Menu("Help");
        bar.setHelpMenu(help);
        assertSame(help, bar.getHelpMenu());
        assertSame(bar, help.getParent());
        assertEquals(Integer.valueOf(2), Integer.valueOf(bar.getMenuCount()));
        assertTrue(help.paramString().contains("isHelpMenu=true"));
        bar.setHelpMenu(file);
        assertSame(file, bar.getHelpMenu());
        assertSame(null, help.getParent());
    }

    public static void dispatchesMenuEvents() {
        MenuItem item = new MenuItem("Open");
        item.setActionCommand("open-command");
        int[] actions = new int[1];
        ActionListener actionListener = event -> {
            assertEquals("open-command", event.getActionCommand());
            actions[0]++;
        };
        item.addActionListener(actionListener);
        assertSame(actionListener, item.getActionListeners()[0]);
        assertSame(actionListener, item.getListeners(ActionListener.class)[0]);
        item.dispatchEvent(new ActionEvent(item, ActionEvent.ACTION_PERFORMED, item.getActionCommand()));
        assertEquals(Integer.valueOf(1), Integer.valueOf(actions[0]));
        item.removeActionListener(actionListener);
        assertEquals(Integer.valueOf(0), Integer.valueOf(item.getActionListeners().length));
        CheckboxMenuItem checkbox = new CheckboxMenuItem("Checked");
        int[] itemEvents = new int[1];
        ItemListener itemListener = event -> {
            assertEquals(Integer.valueOf(ItemEvent.SELECTED), Integer.valueOf(event.getStateChange()));
            itemEvents[0]++;
        };
        checkbox.addItemListener(itemListener);
        checkbox.setState(true);
        assertEquals("Checked", checkbox.getSelectedObjects()[0]);
        assertEquals(Integer.valueOf(0), Integer.valueOf(itemEvents[0]));
        checkbox.dispatchEvent(new ItemEvent(checkbox, ItemEvent.ITEM_STATE_CHANGED, checkbox.getLabel(), ItemEvent.SELECTED));
        assertEquals(Integer.valueOf(1), Integer.valueOf(itemEvents[0]));
        assertSame(itemListener, checkbox.getItemListeners()[0]);
        assertSame(itemListener, checkbox.getListeners(ItemListener.class)[0]);
        checkbox.removeItemListener(itemListener);
    }

    public static void exposesMenuDiagnosticsAndAccessibility() {
        MenuItem item = new MenuItem("Open", new MenuShortcut('O'));
        assertEquals(",label=Open,shortcut=Ctrl+O", item.paramString());
        assertTrue(item.toString().endsWith("[,label=Open,shortcut=Ctrl+O]"));
        assertSame(AccessibleRole.MENU_ITEM, item.getAccessibleContext().getAccessibleRole());
        assertSame(AccessibleRole.CHECK_BOX, new CheckboxMenuItem().getAccessibleContext().getAccessibleRole());
        assertSame(AccessibleRole.MENU, new Menu().getAccessibleContext().getAccessibleRole());
        assertSame(AccessibleRole.MENU_BAR, new MenuBar().getAccessibleContext().getAccessibleRole());
    }

    private static void assertThrowsIllegalArgument(Runnable runnable) {
        try {
            runnable.run();
        } catch (IllegalArgumentException expected) {
            return;
        }
        throw new AssertionError("Expected IllegalArgumentException");
    }

    private static void assertEquals(Object expected, Object actual) {
        if (!expected.equals(actual)) {
            throw new AssertionError("Expected " + expected + " but was " + actual);
        }
    }

    private static void assertSame(Object expected, Object actual) {
        if (expected != actual) {
            throw new AssertionError("Expected identical objects: expected=" + expected + ", actual=" + actual);
        }
    }

    private static void assertTrue(boolean value) {
        if (!value) {
            throw new AssertionError("Expected true");
        }
    }

    private static void assertFalse(boolean value) {
        if (value) {
            throw new AssertionError("Expected false");
        }
    }
}
