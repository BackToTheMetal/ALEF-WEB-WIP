package java.awt;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.HierarchyBoundsListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.event.InputMethodEvent;
import java.awt.event.InputMethodListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.TextEvent;
import java.awt.event.TextListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.event.WindowListener;
import java.awt.event.WindowStateListener;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;

public class AWTEventMulticaster implements ComponentListener, ContainerListener, FocusListener, KeyListener, MouseListener, MouseMotionListener, WindowListener, WindowFocusListener, WindowStateListener, ActionListener, ItemListener, AdjustmentListener, TextListener, InputMethodListener, HierarchyListener, HierarchyBoundsListener, MouseWheelListener {
    protected final EventListener a;
    protected final EventListener b;

    protected AWTEventMulticaster(EventListener a, EventListener b) {
        this.a = a;
        this.b = b;
    }

    protected EventListener remove(EventListener oldListener) {
        if (oldListener == a) {
            return b;
        }
        if (oldListener == b) {
            return a;
        }
        EventListener newA = removeInternal(a, oldListener);
        EventListener newB = removeInternal(b, oldListener);
        if (newA == a && newB == b) {
            return this;
        }
        return addInternal(newA, newB);
    }

    protected static EventListener addInternal(EventListener first, EventListener second) {
        if (first == null) {
            return second;
        }
        if (second == null) {
            return first;
        }
        return new AWTEventMulticaster(first, second);
    }

    protected static EventListener removeInternal(EventListener listener, EventListener oldListener) {
        if (listener == oldListener || listener == null) {
            return null;
        }
        if (listener instanceof AWTEventMulticaster) {
            return ((AWTEventMulticaster) listener).remove(oldListener);
        }
        return listener;
    }

    @Override
    public void componentResized(ComponentEvent event) {
        ((ComponentListener) a).componentResized(event);
        ((ComponentListener) b).componentResized(event);
    }

    @Override
    public void componentMoved(ComponentEvent event) {
        ((ComponentListener) a).componentMoved(event);
        ((ComponentListener) b).componentMoved(event);
    }

    @Override
    public void componentShown(ComponentEvent event) {
        ((ComponentListener) a).componentShown(event);
        ((ComponentListener) b).componentShown(event);
    }

    @Override
    public void componentHidden(ComponentEvent event) {
        ((ComponentListener) a).componentHidden(event);
        ((ComponentListener) b).componentHidden(event);
    }

    @Override
    public void componentAdded(ContainerEvent event) {
        ((ContainerListener) a).componentAdded(event);
        ((ContainerListener) b).componentAdded(event);
    }

    @Override
    public void componentRemoved(ContainerEvent event) {
        ((ContainerListener) a).componentRemoved(event);
        ((ContainerListener) b).componentRemoved(event);
    }

    @Override
    public void focusGained(FocusEvent event) {
        ((FocusListener) a).focusGained(event);
        ((FocusListener) b).focusGained(event);
    }

    @Override
    public void focusLost(FocusEvent event) {
        ((FocusListener) a).focusLost(event);
        ((FocusListener) b).focusLost(event);
    }

    @Override
    public void keyTyped(KeyEvent event) {
        ((KeyListener) a).keyTyped(event);
        ((KeyListener) b).keyTyped(event);
    }

    @Override
    public void keyPressed(KeyEvent event) {
        ((KeyListener) a).keyPressed(event);
        ((KeyListener) b).keyPressed(event);
    }

    @Override
    public void keyReleased(KeyEvent event) {
        ((KeyListener) a).keyReleased(event);
        ((KeyListener) b).keyReleased(event);
    }

    @Override
    public void mouseClicked(MouseEvent event) {
        ((MouseListener) a).mouseClicked(event);
        ((MouseListener) b).mouseClicked(event);
    }

    @Override
    public void mousePressed(MouseEvent event) {
        ((MouseListener) a).mousePressed(event);
        ((MouseListener) b).mousePressed(event);
    }

    @Override
    public void mouseReleased(MouseEvent event) {
        ((MouseListener) a).mouseReleased(event);
        ((MouseListener) b).mouseReleased(event);
    }

    @Override
    public void mouseEntered(MouseEvent event) {
        ((MouseListener) a).mouseEntered(event);
        ((MouseListener) b).mouseEntered(event);
    }

    @Override
    public void mouseExited(MouseEvent event) {
        ((MouseListener) a).mouseExited(event);
        ((MouseListener) b).mouseExited(event);
    }

    @Override
    public void mouseDragged(MouseEvent event) {
        ((MouseMotionListener) a).mouseDragged(event);
        ((MouseMotionListener) b).mouseDragged(event);
    }

    @Override
    public void mouseMoved(MouseEvent event) {
        ((MouseMotionListener) a).mouseMoved(event);
        ((MouseMotionListener) b).mouseMoved(event);
    }

    @Override
    public void windowOpened(WindowEvent event) {
        ((WindowListener) a).windowOpened(event);
        ((WindowListener) b).windowOpened(event);
    }

    @Override
    public void windowClosing(WindowEvent event) {
        ((WindowListener) a).windowClosing(event);
        ((WindowListener) b).windowClosing(event);
    }

    @Override
    public void windowClosed(WindowEvent event) {
        ((WindowListener) a).windowClosed(event);
        ((WindowListener) b).windowClosed(event);
    }

    @Override
    public void windowIconified(WindowEvent event) {
        ((WindowListener) a).windowIconified(event);
        ((WindowListener) b).windowIconified(event);
    }

    @Override
    public void windowDeiconified(WindowEvent event) {
        ((WindowListener) a).windowDeiconified(event);
        ((WindowListener) b).windowDeiconified(event);
    }

    @Override
    public void windowActivated(WindowEvent event) {
        ((WindowListener) a).windowActivated(event);
        ((WindowListener) b).windowActivated(event);
    }

    @Override
    public void windowDeactivated(WindowEvent event) {
        ((WindowListener) a).windowDeactivated(event);
        ((WindowListener) b).windowDeactivated(event);
    }

    @Override
    public void windowGainedFocus(WindowEvent event) {
        ((WindowFocusListener) a).windowGainedFocus(event);
        ((WindowFocusListener) b).windowGainedFocus(event);
    }

    @Override
    public void windowLostFocus(WindowEvent event) {
        ((WindowFocusListener) a).windowLostFocus(event);
        ((WindowFocusListener) b).windowLostFocus(event);
    }

    @Override
    public void windowStateChanged(WindowEvent event) {
        ((WindowStateListener) a).windowStateChanged(event);
        ((WindowStateListener) b).windowStateChanged(event);
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        ((ActionListener) a).actionPerformed(event);
        ((ActionListener) b).actionPerformed(event);
    }

    @Override
    public void itemStateChanged(ItemEvent event) {
        ((ItemListener) a).itemStateChanged(event);
        ((ItemListener) b).itemStateChanged(event);
    }

    @Override
    public void adjustmentValueChanged(AdjustmentEvent event) {
        ((AdjustmentListener) a).adjustmentValueChanged(event);
        ((AdjustmentListener) b).adjustmentValueChanged(event);
    }

    @Override
    public void textValueChanged(TextEvent event) {
        ((TextListener) a).textValueChanged(event);
        ((TextListener) b).textValueChanged(event);
    }

    @Override
    public void inputMethodTextChanged(InputMethodEvent event) {
        ((InputMethodListener) a).inputMethodTextChanged(event);
        ((InputMethodListener) b).inputMethodTextChanged(event);
    }

    @Override
    public void caretPositionChanged(InputMethodEvent event) {
        ((InputMethodListener) a).caretPositionChanged(event);
        ((InputMethodListener) b).caretPositionChanged(event);
    }

    @Override
    public void hierarchyChanged(HierarchyEvent event) {
        ((HierarchyListener) a).hierarchyChanged(event);
        ((HierarchyListener) b).hierarchyChanged(event);
    }

    @Override
    public void ancestorMoved(HierarchyEvent event) {
        ((HierarchyBoundsListener) a).ancestorMoved(event);
        ((HierarchyBoundsListener) b).ancestorMoved(event);
    }

    @Override
    public void ancestorResized(HierarchyEvent event) {
        ((HierarchyBoundsListener) a).ancestorResized(event);
        ((HierarchyBoundsListener) b).ancestorResized(event);
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent event) {
        ((MouseWheelListener) a).mouseWheelMoved(event);
        ((MouseWheelListener) b).mouseWheelMoved(event);
    }

    public static ComponentListener add(ComponentListener first, ComponentListener second) {
        return (ComponentListener) addInternal(first, second);
    }

    public static ContainerListener add(ContainerListener first, ContainerListener second) {
        return (ContainerListener) addInternal(first, second);
    }

    public static FocusListener add(FocusListener first, FocusListener second) {
        return (FocusListener) addInternal(first, second);
    }

    public static KeyListener add(KeyListener first, KeyListener second) {
        return (KeyListener) addInternal(first, second);
    }

    public static MouseListener add(MouseListener first, MouseListener second) {
        return (MouseListener) addInternal(first, second);
    }

    public static MouseMotionListener add(MouseMotionListener first, MouseMotionListener second) {
        return (MouseMotionListener) addInternal(first, second);
    }

    public static WindowListener add(WindowListener first, WindowListener second) {
        return (WindowListener) addInternal(first, second);
    }

    public static WindowFocusListener add(WindowFocusListener first, WindowFocusListener second) {
        return (WindowFocusListener) addInternal(first, second);
    }

    public static WindowStateListener add(WindowStateListener first, WindowStateListener second) {
        return (WindowStateListener) addInternal(first, second);
    }

    public static ActionListener add(ActionListener first, ActionListener second) {
        return (ActionListener) addInternal(first, second);
    }

    public static ItemListener add(ItemListener first, ItemListener second) {
        return (ItemListener) addInternal(first, second);
    }

    public static AdjustmentListener add(AdjustmentListener first, AdjustmentListener second) {
        return (AdjustmentListener) addInternal(first, second);
    }

    public static TextListener add(TextListener first, TextListener second) {
        return (TextListener) addInternal(first, second);
    }

    public static InputMethodListener add(InputMethodListener first, InputMethodListener second) {
        return (InputMethodListener) addInternal(first, second);
    }

    public static HierarchyListener add(HierarchyListener first, HierarchyListener second) {
        return (HierarchyListener) addInternal(first, second);
    }

    public static HierarchyBoundsListener add(HierarchyBoundsListener first, HierarchyBoundsListener second) {
        return (HierarchyBoundsListener) addInternal(first, second);
    }

    public static MouseWheelListener add(MouseWheelListener first, MouseWheelListener second) {
        return (MouseWheelListener) addInternal(first, second);
    }

    public static ComponentListener remove(ComponentListener listener, ComponentListener oldListener) {
        return (ComponentListener) removeInternal(listener, oldListener);
    }

    public static ContainerListener remove(ContainerListener listener, ContainerListener oldListener) {
        return (ContainerListener) removeInternal(listener, oldListener);
    }

    public static FocusListener remove(FocusListener listener, FocusListener oldListener) {
        return (FocusListener) removeInternal(listener, oldListener);
    }

    public static KeyListener remove(KeyListener listener, KeyListener oldListener) {
        return (KeyListener) removeInternal(listener, oldListener);
    }

    public static MouseListener remove(MouseListener listener, MouseListener oldListener) {
        return (MouseListener) removeInternal(listener, oldListener);
    }

    public static MouseMotionListener remove(MouseMotionListener listener, MouseMotionListener oldListener) {
        return (MouseMotionListener) removeInternal(listener, oldListener);
    }

    public static WindowListener remove(WindowListener listener, WindowListener oldListener) {
        return (WindowListener) removeInternal(listener, oldListener);
    }

    public static WindowFocusListener remove(WindowFocusListener listener, WindowFocusListener oldListener) {
        return (WindowFocusListener) removeInternal(listener, oldListener);
    }

    public static WindowStateListener remove(WindowStateListener listener, WindowStateListener oldListener) {
        return (WindowStateListener) removeInternal(listener, oldListener);
    }

    public static ActionListener remove(ActionListener listener, ActionListener oldListener) {
        return (ActionListener) removeInternal(listener, oldListener);
    }

    public static ItemListener remove(ItemListener listener, ItemListener oldListener) {
        return (ItemListener) removeInternal(listener, oldListener);
    }

    public static AdjustmentListener remove(AdjustmentListener listener, AdjustmentListener oldListener) {
        return (AdjustmentListener) removeInternal(listener, oldListener);
    }

    public static TextListener remove(TextListener listener, TextListener oldListener) {
        return (TextListener) removeInternal(listener, oldListener);
    }

    public static InputMethodListener remove(InputMethodListener listener, InputMethodListener oldListener) {
        return (InputMethodListener) removeInternal(listener, oldListener);
    }

    public static HierarchyListener remove(HierarchyListener listener, HierarchyListener oldListener) {
        return (HierarchyListener) removeInternal(listener, oldListener);
    }

    public static HierarchyBoundsListener remove(HierarchyBoundsListener listener, HierarchyBoundsListener oldListener) {
        return (HierarchyBoundsListener) removeInternal(listener, oldListener);
    }

    public static MouseWheelListener remove(MouseWheelListener listener, MouseWheelListener oldListener) {
        return (MouseWheelListener) removeInternal(listener, oldListener);
    }

    public static <T extends EventListener> T[] getListeners(EventListener listener, Class<T> listenerType) {
        if (listenerType == null) {
            throw new NullPointerException("listenerType");
        }
        List<T> listeners = new ArrayList<>();
        collectListeners(listener, listenerType, listeners);
        return toListenerArray(listeners, listenerType);
    }

    @SuppressWarnings("unchecked")
    private static <T extends EventListener> void collectListeners(EventListener listener, Class<T> listenerType, List<T> listeners) {
        if (listener instanceof AWTEventMulticaster) {
            AWTEventMulticaster multicaster = (AWTEventMulticaster) listener;
            collectListeners(multicaster.a, listenerType, listeners);
            collectListeners(multicaster.b, listenerType, listeners);
        } else if (listener != null && matches(listener, listenerType)) {
            listeners.add((T) listener);
        }
    }

    private static boolean matches(EventListener listener, Class<?> listenerType) {
        return listenerType == EventListener.class
                || listenerType == ComponentListener.class && listener instanceof ComponentListener
                || listenerType == ContainerListener.class && listener instanceof ContainerListener
                || listenerType == FocusListener.class && listener instanceof FocusListener
                || listenerType == KeyListener.class && listener instanceof KeyListener
                || listenerType == MouseListener.class && listener instanceof MouseListener
                || listenerType == MouseMotionListener.class && listener instanceof MouseMotionListener
                || listenerType == WindowListener.class && listener instanceof WindowListener
                || listenerType == WindowFocusListener.class && listener instanceof WindowFocusListener
                || listenerType == WindowStateListener.class && listener instanceof WindowStateListener
                || listenerType == ActionListener.class && listener instanceof ActionListener
                || listenerType == ItemListener.class && listener instanceof ItemListener
                || listenerType == AdjustmentListener.class && listener instanceof AdjustmentListener
                || listenerType == TextListener.class && listener instanceof TextListener
                || listenerType == InputMethodListener.class && listener instanceof InputMethodListener
                || listenerType == HierarchyListener.class && listener instanceof HierarchyListener
                || listenerType == HierarchyBoundsListener.class && listener instanceof HierarchyBoundsListener
                || listenerType == MouseWheelListener.class && listener instanceof MouseWheelListener;
    }

    @SuppressWarnings("unchecked")
    private static <T extends EventListener> T[] toListenerArray(List<T> listeners, Class<T> listenerType) {
        if (listenerType == ComponentListener.class) {
            return (T[]) listeners.toArray(new ComponentListener[0]);
        }
        if (listenerType == ContainerListener.class) {
            return (T[]) listeners.toArray(new ContainerListener[0]);
        }
        if (listenerType == FocusListener.class) {
            return (T[]) listeners.toArray(new FocusListener[0]);
        }
        if (listenerType == KeyListener.class) {
            return (T[]) listeners.toArray(new KeyListener[0]);
        }
        if (listenerType == MouseListener.class) {
            return (T[]) listeners.toArray(new MouseListener[0]);
        }
        if (listenerType == MouseMotionListener.class) {
            return (T[]) listeners.toArray(new MouseMotionListener[0]);
        }
        if (listenerType == WindowListener.class) {
            return (T[]) listeners.toArray(new WindowListener[0]);
        }
        if (listenerType == WindowFocusListener.class) {
            return (T[]) listeners.toArray(new WindowFocusListener[0]);
        }
        if (listenerType == WindowStateListener.class) {
            return (T[]) listeners.toArray(new WindowStateListener[0]);
        }
        if (listenerType == ActionListener.class) {
            return (T[]) listeners.toArray(new ActionListener[0]);
        }
        if (listenerType == ItemListener.class) {
            return (T[]) listeners.toArray(new ItemListener[0]);
        }
        if (listenerType == AdjustmentListener.class) {
            return (T[]) listeners.toArray(new AdjustmentListener[0]);
        }
        if (listenerType == TextListener.class) {
            return (T[]) listeners.toArray(new TextListener[0]);
        }
        if (listenerType == InputMethodListener.class) {
            return (T[]) listeners.toArray(new InputMethodListener[0]);
        }
        if (listenerType == HierarchyListener.class) {
            return (T[]) listeners.toArray(new HierarchyListener[0]);
        }
        if (listenerType == HierarchyBoundsListener.class) {
            return (T[]) listeners.toArray(new HierarchyBoundsListener[0]);
        }
        if (listenerType == MouseWheelListener.class) {
            return (T[]) listeners.toArray(new MouseWheelListener[0]);
        }
        return (T[]) listeners.toArray(new EventListener[0]);
    }
}
