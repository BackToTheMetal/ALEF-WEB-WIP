package com.webawt.portable;

import java.awt.Button;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridBagLayoutInfo;
import java.awt.Panel;
import java.awt.Rectangle;
import java.awt.TextField;
import java.awt.event.InvocationEvent;
import java.awt.event.TextEvent;
import java.awt.geom.Arc2D;
import java.awt.geom.Rectangle2D;
import javax.accessibility.AccessibleComponent;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleSelection;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public final class ExtendedApiConsumerTest {
    @Test
    public void accessibleComponentsDelegateGeometryAndContainerChildren() {
        Panel panel = new Panel(null);
        Button button = new Button("Press");
        panel.add(button);
        AccessibleContext context = button.getAccessibleContext();
        AccessibleComponent component = context.getAccessibleComponent();
        component.setBounds(new Rectangle(3, 4, 50, 25));
        component.setForeground(Color.BLUE);
        assertEquals(new Rectangle(3, 4, 50, 25), button.getBounds());
        assertEquals(Color.BLUE, button.getForeground());
        assertSame(panel, context.getAccessibleParent());
        assertEquals(0, context.getAccessibleIndexInParent());
        assertEquals(1, panel.getAccessibleContext().getAccessibleChildrenCount());
        assertSame(button, panel.getAccessibleContext().getAccessibleChild(0));
        assertEquals(0, context.getAccessibleValue().getCurrentAccessibleValue().intValue());
    }

    @Test
    public void accessibleListSelectionAndChildrenShareState() {
        java.awt.List list = new java.awt.List(3, true);
        list.add("one"); list.add("two"); list.add("three");
        AccessibleContext context = list.getAccessibleContext();
        AccessibleSelection selection = context.getAccessibleSelection();
        assertEquals(3, context.getAccessibleChildrenCount());
        selection.addAccessibleSelection(1);
        assertEquals(1, selection.getAccessibleSelectionCount());
        assertTrue(selection.isAccessibleChildSelected(1));
        assertEquals(1, selection.getAccessibleSelection(0).getAccessibleContext().getAccessibleIndexInParent());
        selection.selectAllAccessibleSelection();
        assertEquals(3, selection.getAccessibleSelectionCount());
        selection.clearAccessibleSelection();
        assertEquals(0, selection.getAccessibleSelectionCount());
    }

    @Test
    public void protectedContextAndTextListenerRemainSubclassContracts() {
        class Control extends Component {
            Control() { accessibleContext = new Context(); }
            public AccessibleContext getAccessibleContext() { return accessibleContext; }
            class Context extends AccessibleAWTComponent {}
            void change() { firePropertyChange("number", 1, 2); firePropertyChange("flag", false, true); }
        }
        Control control = new Control();
        int[] properties = new int[1];
        control.addPropertyChangeListener(event -> properties[0]++);
        control.change();
        assertEquals(2, properties[0]);
        assertSame(control.getAccessibleContext(), control.getAccessibleContext().getAccessibleComponent());
        class Field extends TextField {
            void install(java.awt.event.TextListener listener) { textListener = listener; enableEvents(java.awt.AWTEvent.TEXT_EVENT_MASK); }
        }
        Field field = new Field();
        int[] calls = new int[1];
        field.install(event -> calls[0]++);
        field.dispatchEvent(new TextEvent(field, TextEvent.TEXT_VALUE_CHANGED));
        assertEquals(1, calls[0]);
    }

    @Test
    public void mutableInvocationFieldsControlDispatch() {
        int[] calls = new int[1];
        class Invocation extends InvocationEvent {
            Invocation() { super(thisSource(), () -> calls[0]++); }
            void replace() { runnable = () -> calls[0] += 7; catchExceptions = true; notifier = null; }
        }
        Invocation invocation = new Invocation();
        invocation.replace(); invocation.dispatch();
        assertEquals(7, calls[0]);
        assertTrue(invocation.isDispatched());
    }

    private static Object thisSource() { return new Object(); }

    @Test
    public void geometryAndLayoutDispatchThroughProtectedHooks() {
        class Arc extends Arc2D.Double {
            int calls;
            protected Rectangle2D makeBounds(double x, double y, double width, double height) { calls++; return super.makeBounds(x, y, width, height); }
        }
        Arc arc = new Arc(); arc.setArc(0, 0, 40, 30, 0, 90, Arc2D.PIE); arc.getBounds2D();
        assertEquals(1, arc.calls);
        class Layout extends GridBagLayout {
            int calls;
            protected void arrangeGrid(Container parent) { calls++; super.arrangeGrid(parent); }
            protected GridBagLayoutInfo getLayoutInfo(Container parent, int mode) { calls++; return super.getLayoutInfo(parent, mode); }
            protected void adjustForGravity(GridBagConstraints constraints, Rectangle bounds) { calls++; super.adjustForGravity(constraints, bounds); }
        }
        Layout layout = new Layout();
        Panel panel = new Panel(layout); panel.setSize(180, 80);
        Button button = new Button("layout"); button.setPreferredSize(new java.awt.Dimension(60, 24)); panel.add(button); panel.doLayout();
        assertTrue(layout.calls >= 3);
        assertTrue(button.getWidth() > 0 && button.getHeight() > 0);
    }
}
