package java.awt;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleAction;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleValue;
import org.webawt.internal.peer.MenuPeerEntry;

public class MenuItem extends MenuComponent implements Accessible {
    private static final long serialVersionUID = -21757335363267194L;
    private final List<ActionListener> actionListeners = new ArrayList<>();
    private AccessibleContext accessibleContext;
    private MenuShortcut shortcut;
    private String actionCommand;
    private String label;
    private boolean enabled = true;
    private boolean displayable;
    private long eventMask;

    public MenuItem() throws HeadlessException {
        this("", null);
    }

    public MenuItem(String label) throws HeadlessException {
        this(label, null);
    }

    public MenuItem(String label, MenuShortcut shortcut) throws HeadlessException {
        this.label = label;
        this.shortcut = shortcut;
    }

    public void addNotify() {
        displayable = true;
    }

    public String getLabel() {
        return label;
    }

    public synchronized void setLabel(String label) {
        this.label = label;
        refreshMenuPeer();
    }

    public boolean isEnabled() {
        return enabled;
    }

    public synchronized void setEnabled(boolean enabled) {
        this.enabled = enabled;
        refreshMenuPeer();
    }

    @Deprecated
    public synchronized void enable() {
        enabled = true;
        refreshMenuPeer();
    }

    @Deprecated
    public void enable(boolean enabled) {
        setEnabled(enabled);
    }

    @Deprecated
    public synchronized void disable() {
        enabled = false;
        refreshMenuPeer();
    }

    public MenuShortcut getShortcut() {
        return shortcut;
    }

    public void setShortcut(MenuShortcut shortcut) {
        this.shortcut = shortcut;
        refreshMenuPeer();
    }

    public void deleteShortcut() {
        shortcut = null;
        refreshMenuPeer();
    }

    public void setActionCommand(String actionCommand) {
        this.actionCommand = actionCommand;
    }

    public String getActionCommand() {
        return actionCommand == null ? label : actionCommand;
    }

    public synchronized void addActionListener(ActionListener listener) {
        if (listener != null) {
            actionListeners.add(listener);
        }
    }

    public synchronized void removeActionListener(ActionListener listener) {
        actionListeners.remove(listener);
    }

    public synchronized ActionListener[] getActionListeners() {
        return actionListeners.toArray(new ActionListener[0]);
    }

    @SuppressWarnings("unchecked")
    public <T extends EventListener> T[] getListeners(Class<T> listenerType) {
        if (listenerType == null) {
            throw new NullPointerException("listenerType");
        }
        return listenerType == ActionListener.class ? (T[]) getActionListeners() : AWTEventMulticaster.getListeners(null, listenerType);
    }

    public String paramString() {
        String result = super.paramString() + ",label=" + label;
        return shortcut == null ? result : result + ",shortcut=" + shortcut;
    }

    @Override
    public AccessibleContext getAccessibleContext() {
        if (accessibleContext == null) {
            accessibleContext = new AccessibleAWTMenuItem();
        }
        return accessibleContext;
    }

    @Override
    public void removeNotify() {
        displayable = false;
        super.removeNotify();
    }

    protected final void enableEvents(long eventsToEnable) {
        eventMask |= eventsToEnable;
    }

    protected final void disableEvents(long eventsToDisable) {
        eventMask &= ~eventsToDisable;
    }

    protected final boolean isEventMaskEnabled(long eventMask) {
        return (this.eventMask & eventMask) != 0;
    }

    @Override
    protected boolean eventEnabled(AWTEvent event) {
        return event instanceof ActionEvent && (!actionListeners.isEmpty() || (eventMask & AWTEvent.ACTION_EVENT_MASK) != 0);
    }

    @Override
    protected void processEvent(AWTEvent event) {
        if (event instanceof ActionEvent) {
            processActionEvent((ActionEvent) event);
        }
    }

    protected void processActionEvent(ActionEvent event) {
        for (ActionListener listener : getActionListeners()) {
            listener.actionPerformed(event);
        }
    }

    void doMenuEvent(long when, int modifiers) {
        dispatchEvent(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, getActionCommand(), when, modifiers));
    }

    void deleteShortcut(MenuShortcut candidate) {
        if (candidate != null && candidate.equals(shortcut)) {
            shortcut = null;
            refreshMenuPeer();
        }
    }

    MenuItem getShortcutMenuItem(MenuShortcut candidate) {
        return candidate != null && candidate.equals(shortcut) ? this : null;
    }

    boolean isDisplayableMenuItem() {
        return displayable;
    }

    final boolean isItemEnabled() {
        if (!enabled) {
            return false;
        }
        MenuContainer ancestor = getParent();
        while (ancestor instanceof Menu) {
            Menu menu = (Menu) ancestor;
            if (!menu.isEnabled()) {
                return false;
            }
            ancestor = menu.getParent();
        }
        return true;
    }

    MenuPeerEntry createPeerEntry() {
        String shortcutText = shortcutDescription();
        return new MenuPeerEntry(label, shortcutText, isItemEnabled(), false, false, false, "-".equals(label), new MenuPeerEntry[0], this::activateFromPeer);
    }

    private void activateFromPeer() {
        if (isItemEnabled()) {
            doMenuEvent(System.currentTimeMillis(), 0);
        }
    }

    final String shortcutDescription() {
        if (shortcut == null) {
            return null;
        }
        String modifier = java.awt.event.InputEvent.getModifiersExText(Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx());
        return modifier + "+" + (shortcut.usesShiftModifier() ? "Shift+" : "") + java.awt.event.KeyEvent.getKeyText(shortcut.getKey());
    }

    protected class AccessibleAWTMenuItem extends AccessibleAWTMenuComponent implements AccessibleAction, AccessibleValue {
        @Override
        public String getAccessibleName() {
            return accessibleName == null ? label : accessibleName;
        }

        @Override
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.MENU_ITEM;
        }

        @Override
        public AccessibleAction getAccessibleAction() {
            return this;
        }

        @Override
        public AccessibleValue getAccessibleValue() {
            return this;
        }

        @Override
        public int getAccessibleActionCount() {
            return 1;
        }

        @Override
        public String getAccessibleActionDescription(int index) {
            return index == 0 ? AccessibleAction.CLICK : null;
        }

        @Override
        public boolean doAccessibleAction(int index) {
            if (index != 0) {
                return false;
            }
            dispatchEvent(new ActionEvent(MenuItem.this, ActionEvent.ACTION_PERFORMED, getActionCommand(), EventQueue.getMostRecentEventTime(), 0));
            return true;
        }

        @Override
        public Number getCurrentAccessibleValue() {
            return Integer.valueOf(0);
        }

        @Override
        public boolean setCurrentAccessibleValue(Number value) {
            return false;
        }

        @Override
        public Number getMinimumAccessibleValue() {
            return Integer.valueOf(0);
        }

        @Override
        public Number getMaximumAccessibleValue() {
            return Integer.valueOf(0);
        }
    }
}
