package com.webawt.portable;

import java.awt.Adjustable;
import java.awt.AWTEventMulticaster;
import java.awt.AWTKeyStroke;
import java.awt.ItemSelectable;
import java.awt.KeyEventDispatcher;
import java.awt.KeyEventPostProcessor;
import java.awt.Panel;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AWTEventListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.HierarchyBoundsAdapter;
import java.awt.event.HierarchyEvent;
import java.awt.event.InputMethodEvent;
import java.awt.event.InputMethodListener;
import java.awt.event.InputEvent;
import java.awt.event.InvocationEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.PaintEvent;
import java.awt.event.TextEvent;
import java.awt.font.TextHitInfo;
import java.util.EventListener;

public final class PortableEventScenarios {
    private PortableEventScenarios() {
    }

    public static void main(String[] arguments) {
        runAll();
    }

    public static void runAll() {
        describesActionEvents();
        describesAdjustmentAndItemEvents();
        dispatchesInvocationEvents();
        describesHierarchyAndPaintEvents();
        describesTextHitsAndInputMethodEvents();
        multicastsListeners();
        describesKeyStrokes();
        updatesEventSources();
    }

    public static void describesActionEvents() {
        ActionEvent event = new ActionEvent("button", ActionEvent.ACTION_PERFORMED, "save", 123L, ActionEvent.CTRL_MASK | ActionEvent.SHIFT_MASK);
        assertEquals("save", event.getActionCommand());
        assertEquals(123L, event.getWhen());
        assertEquals(ActionEvent.CTRL_MASK | ActionEvent.SHIFT_MASK, event.getModifiers());
        assertEquals("ACTION_PERFORMED,cmd=save,when=123,modifiers=Ctrl+Shift", event.paramString());
    }

    public static void describesAdjustmentAndItemEvents() {
        Adjustable adjustable = new TestAdjustable();
        AdjustmentEvent adjustment = new AdjustmentEvent(adjustable, AdjustmentEvent.ADJUSTMENT_VALUE_CHANGED, AdjustmentEvent.TRACK, 42, true);
        assertSame(adjustable, adjustment.getAdjustable());
        assertEquals(42, adjustment.getValue());
        assertTrue(adjustment.getValueIsAdjusting());
        assertEquals("ADJUSTMENT_VALUE_CHANGED,adjType=TRACK,value=42,isAdjusting=true", adjustment.paramString());
        ItemSelectable selectable = new TestItemSelectable();
        ItemEvent item = new ItemEvent(selectable, ItemEvent.ITEM_STATE_CHANGED, "choice", ItemEvent.SELECTED);
        assertSame(selectable, item.getItemSelectable());
        assertEquals("choice", item.getItem());
        assertEquals("ITEM_STATE_CHANGED,item=choice,stateChange=SELECTED", item.paramString());
    }

    public static void dispatchesInvocationEvents() {
        int[] calls = new int[2];
        InvocationEvent event = new InvocationEvent("queue", () -> calls[0]++, (Runnable) () -> calls[1]++, true);
        assertFalse(event.isDispatched());
        event.dispatch();
        assertTrue(event.isDispatched());
        assertEquals(1, calls[0]);
        assertEquals(1, calls[1]);
        InvocationEvent failure = new InvocationEvent("queue", () -> {
            throw new IllegalStateException("failure");
        }, new Object(), true);
        failure.dispatch();
        assertTrue(failure.getException() instanceof IllegalStateException);
        assertSame(failure.getException(), failure.getThrowable());
    }

    public static void describesHierarchyAndPaintEvents() {
        Panel parent = new Panel();
        Panel changed = new Panel();
        long flags = HierarchyEvent.PARENT_CHANGED | HierarchyEvent.SHOWING_CHANGED;
        HierarchyEvent hierarchy = new HierarchyEvent(changed, HierarchyEvent.HIERARCHY_CHANGED, changed, parent, flags);
        assertSame(changed, hierarchy.getComponent());
        assertSame(changed, hierarchy.getChanged());
        assertSame(parent, hierarchy.getChangedParent());
        assertEquals(flags, hierarchy.getChangeFlags());
        assertEquals("HIERARCHY_CHANGED (PARENT_CHANGED,SHOWING_CHANGED," + changed + "," + parent + ")", hierarchy.paramString());
        int[] listenerCalls = new int[1];
        AWTEventListener listener = event -> listenerCalls[0]++;
        listener.eventDispatched(hierarchy);
        assertEquals(1, listenerCalls[0]);
        HierarchyBoundsAdapter adapter = new HierarchyBoundsAdapter() { };
        adapter.ancestorMoved(hierarchy);
        adapter.ancestorResized(hierarchy);
        Rectangle first = new Rectangle(1, 2, 3, 4);
        PaintEvent paint = new PaintEvent(changed, PaintEvent.PAINT, first);
        assertSame(first, paint.getUpdateRect());
        Rectangle second = new Rectangle(5, 6, 7, 8);
        paint.setUpdateRect(second);
        assertSame(second, paint.getUpdateRect());
        assertEquals("PAINT,updateRect=" + second, paint.paramString());
    }

    public static void describesTextHitsAndInputMethodEvents() {
        TextHitInfo leading = TextHitInfo.leading(4);
        TextHitInfo trailing = TextHitInfo.trailing(4);
        assertEquals(4, leading.getCharIndex());
        assertTrue(leading.isLeadingEdge());
        assertEquals(4, leading.getInsertionIndex());
        assertEquals(5, trailing.getInsertionIndex());
        assertEquals(TextHitInfo.trailing(3), TextHitInfo.beforeOffset(4));
        assertEquals(leading, TextHitInfo.afterOffset(4));
        assertEquals(TextHitInfo.trailing(3), leading.getOtherHit());
        assertEquals(TextHitInfo.leading(6), leading.getOffsetHit(2));
        assertEquals("TextHitInfo[4L]", leading.toString());
        Panel source = new Panel();
        InputMethodEvent event = new InputMethodEvent(source, InputMethodEvent.INPUT_METHOD_TEXT_CHANGED, 123L, null, 0, leading, trailing);
        assertEquals(123L, event.getWhen());
        assertEquals(0, event.getCommittedCharacterCount());
        assertSame(leading, event.getCaret());
        assertSame(trailing, event.getVisiblePosition());
        assertFalse(event.isConsumed());
        event.consume();
        assertTrue(event.isConsumed());
        assertEquals("INPUT_METHOD_TEXT_CHANGED, no text, 0 characters committed, caret: TextHitInfo[4L], visible position: TextHitInfo[4T]", event.paramString());
        int[] calls = new int[2];
        InputMethodListener listener = new InputMethodListener() {
            @Override
            public void inputMethodTextChanged(InputMethodEvent inputMethodEvent) {
                calls[0]++;
            }
            @Override
            public void caretPositionChanged(InputMethodEvent inputMethodEvent) {
                calls[1]++;
            }
        };
        listener.inputMethodTextChanged(event);
        listener.caretPositionChanged(event);
        assertEquals(1, calls[0]);
        assertEquals(1, calls[1]);
    }

    public static void multicastsListeners() {
        int[] sequence = new int[4];
        int[] cursor = new int[1];
        ActionListener first = event -> sequence[cursor[0]++] = 1;
        ActionListener second = event -> sequence[cursor[0]++] = 2;
        ActionListener third = event -> sequence[cursor[0]++] = 3;
        assertSame(first, AWTEventMulticaster.add((ActionListener) null, first));
        assertSame(first, AWTEventMulticaster.add(first, (ActionListener) null));
        ActionListener pair = AWTEventMulticaster.add(first, second);
        ActionListener combined = AWTEventMulticaster.add(pair, third);
        combined.actionPerformed(new ActionEvent("source", ActionEvent.ACTION_PERFORMED, "run"));
        assertEquals(3, cursor[0]);
        assertEquals(1, sequence[0]);
        assertEquals(2, sequence[1]);
        assertEquals(3, sequence[2]);
        ActionListener[] listeners = AWTEventMulticaster.getListeners(combined, ActionListener.class);
        assertEquals(3, listeners.length);
        assertSame(first, listeners[0]);
        assertSame(second, listeners[1]);
        assertSame(third, listeners[2]);
        EventListener[] allListeners = AWTEventMulticaster.getListeners(combined, EventListener.class);
        assertEquals(3, allListeners.length);
        assertSame(first, allListeners[0]);
        assertSame(second, allListeners[1]);
        assertSame(third, allListeners[2]);
        assertSame(second, AWTEventMulticaster.remove(pair, first));
        assertSame(pair, AWTEventMulticaster.remove(pair, third));
        ActionListener remaining = AWTEventMulticaster.remove(combined, second);
        cursor[0] = 0;
        remaining.actionPerformed(new ActionEvent("source", ActionEvent.ACTION_PERFORMED, "run"));
        assertEquals(2, cursor[0]);
        assertEquals(1, sequence[0]);
        assertEquals(3, sequence[1]);
        int[] componentCalls = new int[2];
        ComponentListener firstComponent = new ComponentAdapter() {
            @Override
            public void componentMoved(ComponentEvent event) {
                componentCalls[0]++;
            }
        };
        ComponentListener secondComponent = new ComponentAdapter() {
            @Override
            public void componentMoved(ComponentEvent event) {
                componentCalls[1]++;
            }
        };
        AWTEventMulticaster.add(firstComponent, secondComponent).componentMoved(new ComponentEvent(new Panel(), ComponentEvent.COMPONENT_MOVED));
        assertEquals(1, componentCalls[0]);
        assertEquals(1, componentCalls[1]);
        assertEquals(0, AWTEventMulticaster.getListeners(combined, ItemListener.class).length);
    }

    @SuppressWarnings("deprecation")
    public static void describesKeyStrokes() {
        AWTKeyStroke typed = AWTKeyStroke.getAWTKeyStroke('a');
        assertEquals('a', typed.getKeyChar());
        assertEquals(KeyEvent.VK_UNDEFINED, typed.getKeyCode());
        assertEquals(KeyEvent.KEY_TYPED, typed.getKeyEventType());
        assertFalse(typed.isOnKeyRelease());
        assertEquals("typed a", typed.toString());
        AWTKeyStroke controlPressed = AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_DOWN_MASK);
        assertSame(controlPressed, AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_MASK));
        assertEquals(InputEvent.CTRL_MASK | InputEvent.CTRL_DOWN_MASK, controlPressed.getModifiers());
        assertEquals(KeyEvent.KEY_PRESSED, controlPressed.getKeyEventType());
        assertEquals("ctrl pressed A", controlPressed.toString());
        assertEquals(1133248514, controlPressed.hashCode());
        AWTKeyStroke released = AWTKeyStroke.getAWTKeyStroke("control released TAB");
        assertEquals(KeyEvent.VK_TAB, released.getKeyCode());
        assertEquals(KeyEvent.KEY_RELEASED, released.getKeyEventType());
        assertTrue(released.isOnKeyRelease());
        assertEquals("ctrl released TAB", released.toString());
        assertEquals("shift altGraph pressed F12", AWTKeyStroke.getAWTKeyStroke("altGraph shift pressed F12").toString());
        assertEquals("meta alt pressed KP_UP", AWTKeyStroke.getAWTKeyStroke("meta alt pressed KP_UP").toString());
        assertEquals("pressed NUMPAD0", AWTKeyStroke.getAWTKeyStroke("pressed NUMPAD0").toString());
        assertEquals("pressed PAGE_DOWN", AWTKeyStroke.getAWTKeyStroke("pressed PAGE_DOWN").toString());
        assertEquals("pressed PLUS", AWTKeyStroke.getAWTKeyStroke("pressed PLUS").toString());
        assertEquals(0, AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_A, InputEvent.BUTTON1_MASK).getModifiers());
        assertEquals(0, AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_A, 0x40000000).getModifiers());
        assertEquals("pressed UNKNOWN", AWTKeyStroke.getAWTKeyStroke(999, 0).toString());
        KeyEvent event = new KeyEvent(new Panel(), KeyEvent.KEY_PRESSED, 123L, InputEvent.SHIFT_MASK, KeyEvent.VK_TAB, KeyEvent.CHAR_UNDEFINED);
        assertEquals(AWTKeyStroke.getAWTKeyStroke("shift pressed TAB"), AWTKeyStroke.getAWTKeyStrokeForEvent(event));
        KeyEventDispatcher dispatcher = keyEvent -> keyEvent == event;
        KeyEventPostProcessor postProcessor = keyEvent -> keyEvent.getKeyCode() == KeyEvent.VK_TAB;
        assertTrue(dispatcher.dispatchKeyEvent(event));
        assertTrue(postProcessor.postProcessKeyEvent(event));
        assertIllegalArgument(() -> AWTKeyStroke.getAWTKeyStroke((String) null));
        assertIllegalArgument(() -> AWTKeyStroke.getAWTKeyStroke((Character) null, 0));
        assertIllegalArgument(() -> AWTKeyStroke.getAWTKeyStroke("typed SPACE"));
        assertIllegalArgument(() -> AWTKeyStroke.getAWTKeyStroke("pressed NumPad-0"));
        assertIllegalArgument(() -> AWTKeyStroke.getAWTKeyStroke("bogus pressed A"));
    }

    public static void updatesEventSources() {
        TextEvent event = new TextEvent("old", TextEvent.TEXT_VALUE_CHANGED);
        assertEquals("TEXT_VALUE_CHANGED", event.paramString());
        event.setSource("new");
        assertSame("new", event.getSource());
        assertEquals(TextEvent.TEXT_VALUE_CHANGED, event.getID());
    }

    private static void assertEquals(int expected, int actual) {
        if (expected != actual) {
            throw new AssertionError("Expected " + expected + " but was " + actual);
        }
    }

    private static void assertEquals(long expected, long actual) {
        if (expected != actual) {
            throw new AssertionError("Expected " + expected + " but was " + actual);
        }
    }

    private static void assertEquals(Object expected, Object actual) {
        if (!expected.equals(actual)) {
            throw new AssertionError("Expected " + expected + " but was " + actual);
        }
    }

    private static void assertSame(Object expected, Object actual) {
        if (expected != actual) {
            throw new AssertionError("Expected same object");
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

    private static void assertIllegalArgument(Runnable action) {
        try {
            action.run();
        } catch (IllegalArgumentException expected) {
            return;
        }
        throw new AssertionError("Expected IllegalArgumentException");
    }

    private static final class TestAdjustable implements Adjustable {
        @Override public int getOrientation() { return HORIZONTAL; }
        @Override public void setMinimum(int minimum) { }
        @Override public int getMinimum() { return 0; }
        @Override public void setMaximum(int maximum) { }
        @Override public int getMaximum() { return 100; }
        @Override public void setUnitIncrement(int increment) { }
        @Override public int getUnitIncrement() { return 1; }
        @Override public void setBlockIncrement(int increment) { }
        @Override public int getBlockIncrement() { return 10; }
        @Override public void setVisibleAmount(int amount) { }
        @Override public int getVisibleAmount() { return 10; }
        @Override public void setValue(int value) { }
        @Override public int getValue() { return 42; }
        @Override public void addAdjustmentListener(AdjustmentListener listener) { }
        @Override public void removeAdjustmentListener(AdjustmentListener listener) { }
    }

    private static final class TestItemSelectable implements ItemSelectable {
        @Override public Object[] getSelectedObjects() { return new Object[] {"choice"}; }
        @Override public void addItemListener(ItemListener listener) { }
        @Override public void removeItemListener(ItemListener listener) { }
    }
}
