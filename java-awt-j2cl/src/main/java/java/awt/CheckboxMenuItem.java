package java.awt;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import org.webawt.internal.peer.MenuPeerEntry;

public class CheckboxMenuItem extends MenuItem implements ItemSelectable, Accessible {
    private static final long serialVersionUID = 6190621106981774043L;
    private final List<ItemListener> itemListeners = new ArrayList<>();
    private AccessibleContext accessibleContext;
    private boolean state;

    public CheckboxMenuItem() throws HeadlessException {
        this("", false);
    }

    public CheckboxMenuItem(String label) throws HeadlessException {
        this(label, false);
    }

    public CheckboxMenuItem(String label, boolean state) throws HeadlessException {
        super(label);
        this.state = state;
    }

    @Override
    public void addNotify() {
        super.addNotify();
    }

    public boolean getState() {
        return state;
    }

    public synchronized void setState(boolean state) {
        this.state = state;
        refreshMenuPeer();
    }

    @Override
    public synchronized Object[] getSelectedObjects() {
        return state ? new Object[] {getLabel()} : null;
    }

    @Override
    public synchronized void addItemListener(ItemListener listener) {
        if (listener != null) {
            itemListeners.add(listener);
        }
    }

    @Override
    public synchronized void removeItemListener(ItemListener listener) {
        itemListeners.remove(listener);
    }

    public synchronized ItemListener[] getItemListeners() {
        return itemListeners.toArray(new ItemListener[0]);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends EventListener> T[] getListeners(Class<T> listenerType) {
        if (listenerType == ItemListener.class) {
            return (T[]) getItemListeners();
        }
        return super.getListeners(listenerType);
    }

    @Override
    public String paramString() {
        return super.paramString() + ",state=" + state;
    }

    @Override
    public AccessibleContext getAccessibleContext() {
        if (accessibleContext == null) {
            accessibleContext = new AccessibleAWTCheckboxMenuItem();
        }
        return accessibleContext;
    }

    @Override
    protected boolean eventEnabled(AWTEvent event) {
        return event instanceof ItemEvent && (!itemListeners.isEmpty() || isEventMaskEnabled(AWTEvent.ITEM_EVENT_MASK)) || super.eventEnabled(event);
    }

    @Override
    protected void processEvent(AWTEvent event) {
        if (event instanceof ItemEvent) {
            processItemEvent((ItemEvent) event);
        } else {
            super.processEvent(event);
        }
    }

    protected void processItemEvent(ItemEvent event) {
        for (ItemListener listener : getItemListeners()) {
            listener.itemStateChanged(event);
        }
    }

    @Override
    void doMenuEvent(long when, int modifiers) {
        setState(!state);
        dispatchEvent(new ItemEvent(this, ItemEvent.ITEM_STATE_CHANGED, getLabel(), state ? ItemEvent.SELECTED : ItemEvent.DESELECTED));
    }

    @Override
    MenuPeerEntry createPeerEntry() {
        MenuShortcut shortcut = getShortcut();
        String shortcutText = shortcutDescription();
        return new MenuPeerEntry(getLabel(), shortcutText, isItemEnabled(), false, true, state, false, new MenuPeerEntry[0], this::activateFromPeer);
    }

    private void activateFromPeer() {
        if (isItemEnabled()) {
            doMenuEvent(System.currentTimeMillis(), 0);
        }
    }

    protected class AccessibleAWTCheckboxMenuItem extends AccessibleAWTMenuItem {
        @Override
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.CHECK_BOX;
        }

        @Override
        public Number getCurrentAccessibleValue() {
            return null;
        }

        @Override
        public boolean setCurrentAccessibleValue(Number value) {
            return false;
        }

        @Override
        public Number getMinimumAccessibleValue() {
            return null;
        }

        @Override
        public Number getMaximumAccessibleValue() {
            return null;
        }
    }
}
