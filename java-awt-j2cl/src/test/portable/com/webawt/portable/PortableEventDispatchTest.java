package com.webawt.portable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.awt.AWTEvent;
import java.awt.Button;
import java.awt.Checkbox;
import java.awt.Choice;
import java.awt.Component;
import java.awt.Container;
import java.awt.KeyboardFocusManager;
import java.awt.Scrollbar;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ContainerEvent;
import java.awt.event.FocusEvent;
import java.awt.event.HierarchyEvent;
import java.awt.event.InputMethodEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.TextEvent;
import java.awt.event.TextListener;
import java.awt.event.WindowEvent;
import org.junit.Test;

public final class PortableEventDispatchTest {
    @Test
    public void componentMasksSelectIndependentEventFamilies() {
        ProbeComponent component = new ProbeComponent();
        AWTEvent[] events = {
            new ComponentEvent(component, ComponentEvent.COMPONENT_RESIZED),
            new FocusEvent(component, FocusEvent.FOCUS_GAINED),
            new KeyEvent(component, KeyEvent.KEY_PRESSED, 1, 0, KeyEvent.VK_A, 'a'),
            new MouseEvent(component, MouseEvent.MOUSE_PRESSED, 1, 0, 1, 1, 1, false),
            new MouseEvent(component, MouseEvent.MOUSE_MOVED, 1, 0, 1, 1, 0, false),
            new MouseWheelEvent(component, MouseEvent.MOUSE_WHEEL, 1, 0, 1, 1, 0, false, MouseWheelEvent.WHEEL_UNIT_SCROLL, 1, 1),
            new InputMethodEvent(component, InputMethodEvent.CARET_POSITION_CHANGED, null, null),
            new HierarchyEvent(component, HierarchyEvent.HIERARCHY_CHANGED, component, null),
            new HierarchyEvent(component, HierarchyEvent.ANCESTOR_MOVED, component, null)
        };
        long[] masks = {AWTEvent.COMPONENT_EVENT_MASK, AWTEvent.FOCUS_EVENT_MASK, AWTEvent.KEY_EVENT_MASK, AWTEvent.MOUSE_EVENT_MASK, AWTEvent.MOUSE_MOTION_EVENT_MASK, AWTEvent.MOUSE_WHEEL_EVENT_MASK, AWTEvent.INPUT_METHOD_EVENT_MASK, AWTEvent.HIERARCHY_EVENT_MASK, AWTEvent.HIERARCHY_BOUNDS_EVENT_MASK};
        for (int index = 0; index < events.length; index++) {
            component.enable(masks[index]);
            component.record.reset();
            for (AWTEvent event : events) {
                KeyboardFocusManager.getCurrentKeyboardFocusManager().redispatchEvent(component, event);
            }
            assertEquals("mask " + masks[index], 1, component.record.hooks);
            assertSame(events[index], component.record.last);
            component.disable(masks[index]);
            component.record.reset();
            KeyboardFocusManager.getCurrentKeyboardFocusManager().redispatchEvent(component, events[index]);
            assertEquals(0, component.record.hooks);
        }
    }

    @Test
    public void disablingOneMaskPreservesOtherMasksAndApplicationEvents() {
        ProbeComponent component = new ProbeComponent();
        component.enable(AWTEvent.COMPONENT_EVENT_MASK);
        component.enable(AWTEvent.HIERARCHY_EVENT_MASK);
        component.disable(AWTEvent.COMPONENT_EVENT_MASK);
        component.dispatchEvent(new ComponentEvent(component, ComponentEvent.COMPONENT_MOVED));
        assertEquals(0, component.record.hooks);
        component.dispatchEvent(new HierarchyEvent(component, HierarchyEvent.HIERARCHY_CHANGED, component, null));
        assertEquals(1, component.record.hooks);
        AWTEvent custom = new AWTEvent(component, AWTEvent.RESERVED_ID_MAX + 1) { };
        component.dispatchEvent(custom);
        assertEquals(2, component.record.hooks);
        assertSame(custom, component.record.last);
    }

    @Test
    public void buttonActionUsesSubclassHookAndListenerInterest() {
        ProbeButton control = new ProbeButton();
        ActionEvent event = new ActionEvent(control, ActionEvent.ACTION_PERFORMED, "save");
        ActionListener listener = delivered -> control.record.listener(delivered);
        assertDelivery(control, event, AWTEvent.ACTION_EVENT_MASK, () -> control.addActionListener(listener), () -> control.removeActionListener(listener));
    }

    @Test
    public void checkboxItemUsesSubclassHookAndListenerInterest() {
        ProbeCheckbox control = new ProbeCheckbox();
        ItemEvent event = new ItemEvent(control, ItemEvent.ITEM_STATE_CHANGED, "checked", ItemEvent.SELECTED);
        ItemListener listener = delivered -> control.record.listener(delivered);
        assertDelivery(control, event, AWTEvent.ITEM_EVENT_MASK, () -> control.addItemListener(listener), () -> control.removeItemListener(listener));
    }

    @Test
    public void choiceItemUsesSubclassHookAndListenerInterest() {
        ProbeChoice control = new ProbeChoice();
        ItemEvent event = new ItemEvent(control, ItemEvent.ITEM_STATE_CHANGED, "chosen", ItemEvent.SELECTED);
        ItemListener listener = delivered -> control.record.listener(delivered);
        assertDelivery(control, event, AWTEvent.ITEM_EVENT_MASK, () -> control.addItemListener(listener), () -> control.removeItemListener(listener));
    }

    @Test
    public void listItemUsesSubclassHookAndListenerInterest() {
        ProbeList control = new ProbeList();
        ItemEvent event = new ItemEvent(control, ItemEvent.ITEM_STATE_CHANGED, Integer.valueOf(2), ItemEvent.SELECTED);
        ItemListener listener = delivered -> control.record.listener(delivered);
        assertDelivery(control, event, AWTEvent.ITEM_EVENT_MASK, () -> control.addItemListener(listener), () -> control.removeItemListener(listener));
    }

    @Test
    public void listActionUsesSubclassHookAndListenerInterest() {
        ProbeList control = new ProbeList();
        ActionEvent event = new ActionEvent(control, ActionEvent.ACTION_PERFORMED, "row");
        ActionListener listener = delivered -> control.record.listener(delivered);
        assertDelivery(control, event, AWTEvent.ACTION_EVENT_MASK, () -> control.addActionListener(listener), () -> control.removeActionListener(listener));
    }

    @Test
    public void textAreaChangeUsesSubclassHookAndListenerInterest() {
        ProbeTextArea control = new ProbeTextArea();
        TextEvent event = new TextEvent(control, TextEvent.TEXT_VALUE_CHANGED);
        TextListener listener = delivered -> control.record.listener(delivered);
        assertDelivery(control, event, AWTEvent.TEXT_EVENT_MASK, () -> control.addTextListener(listener), () -> control.removeTextListener(listener));
    }

    @Test
    public void textFieldChangeUsesSubclassHookAndListenerInterest() {
        ProbeTextField control = new ProbeTextField();
        TextEvent event = new TextEvent(control, TextEvent.TEXT_VALUE_CHANGED);
        TextListener listener = delivered -> control.record.listener(delivered);
        assertDelivery(control, event, AWTEvent.TEXT_EVENT_MASK, () -> control.addTextListener(listener), () -> control.removeTextListener(listener));
    }

    @Test
    public void textFieldActionUsesSubclassHookAndListenerInterest() {
        ProbeTextField control = new ProbeTextField();
        ActionEvent event = new ActionEvent(control, ActionEvent.ACTION_PERFORMED, "entered");
        ActionListener listener = delivered -> control.record.listener(delivered);
        assertDelivery(control, event, AWTEvent.ACTION_EVENT_MASK, () -> control.addActionListener(listener), () -> control.removeActionListener(listener));
    }

    @Test
    public void scrollbarAdjustmentUsesSubclassHookAndListenerInterest() {
        ProbeScrollbar control = new ProbeScrollbar();
        AdjustmentEvent event = new AdjustmentEvent(control, AdjustmentEvent.ADJUSTMENT_VALUE_CHANGED, AdjustmentEvent.TRACK, 42);
        AdjustmentListener listener = delivered -> control.record.listener(delivered);
        assertDelivery(control, event, AWTEvent.ADJUSTMENT_EVENT_MASK, () -> control.addAdjustmentListener(listener), () -> control.removeAdjustmentListener(listener));
    }

    @Test
    public void containerMasksReachTheContainerHook() {
        ProbeContainer control = new ProbeContainer();
        ContainerEvent event = new ContainerEvent(control, ContainerEvent.COMPONENT_ADDED, new ProbeComponent());
        control.dispatchEvent(event);
        assertEquals(0, control.record.hooks);
        control.enable(AWTEvent.CONTAINER_EVENT_MASK);
        control.dispatchEvent(event);
        assertEquals(1, control.record.hooks);
        assertSame(event, control.record.last);
        control.disable(AWTEvent.CONTAINER_EVENT_MASK);
        control.dispatchEvent(event);
        assertEquals(1, control.record.hooks);
    }

    @Test
    public void windowMasksRemainIndependent() {
        ProbeWindow control = new ProbeWindow();
        try {
            int[] ids = {WindowEvent.WINDOW_OPENED, WindowEvent.WINDOW_GAINED_FOCUS, WindowEvent.WINDOW_STATE_CHANGED};
            long[] masks = {AWTEvent.WINDOW_EVENT_MASK, AWTEvent.WINDOW_FOCUS_EVENT_MASK, AWTEvent.WINDOW_STATE_EVENT_MASK};
            for (int index = 0; index < masks.length; index++) {
                control.enable(masks[index]);
                control.record.reset();
                for (int id : ids) {
                    KeyboardFocusManager.getCurrentKeyboardFocusManager().redispatchEvent(control, new WindowEvent(control, id));
                }
                assertEquals(1, control.record.hooks);
                assertEquals(ids[index], control.record.last.getID());
                control.disable(masks[index]);
            }
        } finally {
            control.dispose();
        }
    }

    @Test
    public void subclassCanConsumeDeliveryAndListenersUseASnapshot() {
        ProbeButton control = new ProbeButton();
        ActionEvent event = new ActionEvent(control, ActionEvent.ACTION_PERFORMED, "save");
        ActionListener[] first = new ActionListener[1];
        int[] calls = new int[2];
        first[0] = delivered -> {
            calls[0]++;
            control.removeActionListener(first[0]);
        };
        control.addActionListener(first[0]);
        control.addActionListener(delivered -> calls[1]++);
        control.forward = false;
        control.dispatchEvent(event);
        assertEquals(1, control.record.hooks);
        assertEquals(0, calls[0]);
        assertEquals(0, calls[1]);
        control.forward = true;
        control.dispatchEvent(event);
        control.dispatchEvent(event);
        assertEquals(1, calls[0]);
        assertEquals(2, calls[1]);
    }

    private static void assertDelivery(Harness control, AWTEvent event, long mask, Runnable attach, Runnable detach) {
        Record record = control.record();
        Component component = control.component();
        component.dispatchEvent(event);
        assertEquals(0, record.hooks);
        control.enable(mask);
        component.dispatchEvent(event);
        assertEquals(1, record.hooks);
        assertSame(event, record.last);
        assertEquals(0, record.listeners);
        control.disable(mask);
        component.dispatchEvent(event);
        assertEquals(1, record.hooks);
        attach.run();
        component.dispatchEvent(event);
        assertEquals(2, record.hooks);
        assertEquals(1, record.listeners);
        control.enable(mask);
        control.disable(mask);
        component.dispatchEvent(event);
        assertEquals(3, record.hooks);
        assertEquals(2, record.listeners);
        detach.run();
        component.dispatchEvent(event);
        assertEquals(3, record.hooks);
        control.enable(mask);
        component.dispatchEvent(event);
        assertEquals(4, record.hooks);
        assertEquals(2, record.listeners);
    }

    private interface Harness {
        Component component();
        Record record();
        void enable(long mask);
        void disable(long mask);
    }

    private static final class Record {
        private int hooks;
        private int listeners;
        private AWTEvent last;

        private void hook(AWTEvent event) {
            hooks++;
            last = event;
        }

        private void listener(AWTEvent event) {
            assertSame(last, event);
            assertEquals(listeners + 2, hooks);
            listeners++;
        }

        private void reset() {
            hooks = 0;
            listeners = 0;
            last = null;
        }
    }

    private static final class ProbeComponent extends Component implements Harness {
        private final Record record = new Record();

        @Override
        public Component component() {
            return this;
        }

        @Override
        public Record record() {
            return record;
        }

        @Override
        public void enable(long mask) {
            enableEvents(mask);
        }

        @Override
        public void disable(long mask) {
            disableEvents(mask);
        }

        @Override
        protected void processEvent(AWTEvent event) {
            record.hook(event);
        }
    }

    private static final class ProbeContainer extends Container implements Harness {
        private final Record record = new Record();

        @Override
        public Component component() {
            return this;
        }

        @Override
        public Record record() {
            return record;
        }

        @Override
        public void enable(long mask) {
            enableEvents(mask);
        }

        @Override
        public void disable(long mask) {
            disableEvents(mask);
        }

        @Override
        protected void processContainerEvent(ContainerEvent event) {
            record.hook(event);
            super.processContainerEvent(event);
        }
    }

    private static final class ProbeWindow extends Window implements Harness {
        private final Record record = new Record();

        private ProbeWindow() {
            super((Window) null);
        }

        @Override
        public Component component() {
            return this;
        }

        @Override
        public Record record() {
            return record;
        }

        @Override
        public void enable(long mask) {
            enableEvents(mask);
        }

        @Override
        public void disable(long mask) {
            disableEvents(mask);
        }

        @Override
        protected void processWindowEvent(WindowEvent event) {
            record.hook(event);
            super.processWindowEvent(event);
        }

        @Override
        protected void processWindowFocusEvent(WindowEvent event) {
            record.hook(event);
            super.processWindowFocusEvent(event);
        }

        @Override
        protected void processWindowStateEvent(WindowEvent event) {
            record.hook(event);
            super.processWindowStateEvent(event);
        }
    }

    private static final class ProbeButton extends Button implements Harness {
        private final Record record = new Record();
        private boolean forward = true;

        @Override
        public Component component() {
            return this;
        }

        @Override
        public Record record() {
            return record;
        }

        @Override
        public void enable(long mask) {
            enableEvents(mask);
        }

        @Override
        public void disable(long mask) {
            disableEvents(mask);
        }

        @Override
        protected void processActionEvent(ActionEvent event) {
            record.hook(event);
            if (forward) {
                super.processActionEvent(event);
            }
        }
    }

    private static final class ProbeCheckbox extends Checkbox implements Harness {
        private final Record record = new Record();

        @Override
        public Component component() {
            return this;
        }

        @Override
        public Record record() {
            return record;
        }

        @Override
        public void enable(long mask) {
            enableEvents(mask);
        }

        @Override
        public void disable(long mask) {
            disableEvents(mask);
        }

        @Override
        protected void processItemEvent(ItemEvent event) {
            record.hook(event);
            super.processItemEvent(event);
        }
    }

    private static final class ProbeChoice extends Choice implements Harness {
        private final Record record = new Record();

        @Override
        public Component component() {
            return this;
        }

        @Override
        public Record record() {
            return record;
        }

        @Override
        public void enable(long mask) {
            enableEvents(mask);
        }

        @Override
        public void disable(long mask) {
            disableEvents(mask);
        }

        @Override
        protected void processItemEvent(ItemEvent event) {
            record.hook(event);
            super.processItemEvent(event);
        }
    }

    private static final class ProbeList extends java.awt.List implements Harness {
        private final Record record = new Record();

        @Override
        public Component component() {
            return this;
        }

        @Override
        public Record record() {
            return record;
        }

        @Override
        public void enable(long mask) {
            enableEvents(mask);
        }

        @Override
        public void disable(long mask) {
            disableEvents(mask);
        }

        @Override
        protected void processItemEvent(ItemEvent event) {
            record.hook(event);
            super.processItemEvent(event);
        }

        @Override
        protected void processActionEvent(ActionEvent event) {
            record.hook(event);
            super.processActionEvent(event);
        }
    }

    private static final class ProbeTextArea extends TextArea implements Harness {
        private final Record record = new Record();

        @Override
        public Component component() {
            return this;
        }

        @Override
        public Record record() {
            return record;
        }

        @Override
        public void enable(long mask) {
            enableEvents(mask);
        }

        @Override
        public void disable(long mask) {
            disableEvents(mask);
        }

        @Override
        protected void processTextEvent(TextEvent event) {
            record.hook(event);
            super.processTextEvent(event);
        }
    }

    private static final class ProbeTextField extends TextField implements Harness {
        private final Record record = new Record();

        @Override
        public Component component() {
            return this;
        }

        @Override
        public Record record() {
            return record;
        }

        @Override
        public void enable(long mask) {
            enableEvents(mask);
        }

        @Override
        public void disable(long mask) {
            disableEvents(mask);
        }

        @Override
        protected void processTextEvent(TextEvent event) {
            record.hook(event);
            super.processTextEvent(event);
        }

        @Override
        protected void processActionEvent(ActionEvent event) {
            record.hook(event);
            super.processActionEvent(event);
        }
    }

    private static final class ProbeScrollbar extends Scrollbar implements Harness {
        private final Record record = new Record();

        @Override
        public Component component() {
            return this;
        }

        @Override
        public Record record() {
            return record;
        }

        @Override
        public void enable(long mask) {
            enableEvents(mask);
        }

        @Override
        public void disable(long mask) {
            disableEvents(mask);
        }

        @Override
        protected void processAdjustmentEvent(AdjustmentEvent event) {
            record.hook(event);
            super.processAdjustmentEvent(event);
        }
    }
}
