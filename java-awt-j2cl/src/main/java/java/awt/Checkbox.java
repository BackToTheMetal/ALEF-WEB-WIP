package java.awt;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;
import java.util.Locale;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleState;
import javax.accessibility.AccessibleStateSet;

public class Checkbox extends Component implements ItemSelectable, Accessible {
    private final List<ItemListener> itemListeners = new ArrayList<>();
    private CheckboxGroup group;
    private String label;
    private boolean state;

    public Checkbox() throws HeadlessException {
        this("", false, null);
    }

    public Checkbox(String label) throws HeadlessException {
        this(label, false, null);
    }

    public Checkbox(String label, boolean state) throws HeadlessException {
        this(label, state, null);
    }

    public Checkbox(String label, boolean state, CheckboxGroup group) throws HeadlessException {
        super("checkbox");
        this.label = label;
        this.group = group;
        setState(state);
    }

    public Checkbox(String label, CheckboxGroup group, boolean state) throws HeadlessException {
        this(label, state, group);
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
        updatePeerText(label);
        invalidate();
    }

    public boolean getState() {
        return state;
    }

    public void setState(boolean state) {
        if (group != null && state) {
            group.setSelectedCheckbox(this);
            return;
        }
        if (group != null && !state && group.getSelectedCheckbox() == this) {
            return;
        }
        setStateFromGroup(state);
    }

    @Override
    public Object[] getSelectedObjects() {
        return state ? new Object[] {label} : null;
    }

    public CheckboxGroup getCheckboxGroup() {
        return group;
    }

    public void setCheckboxGroup(CheckboxGroup group) {
        if (this.group == group) {
            return;
        }
        CheckboxGroup previous = this.group;
        boolean selected = state;
        this.group = group;
        if (previous != null && previous.getSelectedCheckbox() == this) {
            previous.setSelectedCheckbox(null);
        }
        if (group != null && selected) {
            if (group.getSelectedCheckbox() == null) {
                group.setSelectedCheckbox(this);
            } else {
                setStateFromGroup(false);
            }
        } else {
            setStateFromGroup(selected);
        }
        synchronizeCheckboxPeer();
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
        return listenerType == ItemListener.class ? (T[]) getItemListeners() : super.getListeners(listenerType);
    }

    @Override
    protected boolean eventEnabled(AWTEvent event) {
        if (event.getID() == ItemEvent.ITEM_STATE_CHANGED && !itemListeners.isEmpty()) {
            return true;
        }
        return super.eventEnabled(event);
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
    public void addNotify() {
        super.addNotify();
        synchronizeCheckboxPeer();
        updatePeerItemChangeHandler(this::readStateFromPeer);
    }

    private void synchronizeCheckboxPeer() {
        updatePeerInputType(group == null ? "checkbox" : "radio");
        if (getPeer() != null) {
            getPeer().setInputGroup(group == null ? null : group.getPeerName());
        }
        updatePeerText(label);
        updatePeerSelected(state);
    }

    @Override
    public Dimension getMinimumSize() {
        if (isMinimumSizeSet()) {
            return super.getMinimumSize();
        }
        Font font = effectiveFont(getFont());
        FontMetrics metrics = getFontMetrics(font);
        int indicator = Math.max(13, (int) Math.ceil(font.getSize2D()));
        return new Dimension(indicator + 8 + metrics.stringWidth(label == null ? "" : label), Math.max(indicator, metrics.getHeight()) + 4);
    }

    @Override
    public Dimension getPreferredSize() {
        return isPreferredSizeSet() ? super.getPreferredSize() : getMinimumSize();
    }

    @Override
    public AccessibleContext getAccessibleContext() {
        if (accessibleContext == null) {
            accessibleContext = new AccessibleAWTCheckbox();
        }
        return accessibleContext;
    }

    void setStateFromGroup(boolean state) {
        this.state = state;
        updatePeerSelected(state);
    }

    protected class AccessibleAWTCheckbox extends AccessibleAWTComponent implements javax.accessibility.AccessibleAction, javax.accessibility.AccessibleValue, java.awt.event.ItemListener {

        public int getAccessibleActionCount() { return 0; }
        public String getAccessibleActionDescription(int index) { return null; }
        public boolean doAccessibleAction(int index) { return false; }
        public javax.accessibility.AccessibleAction getAccessibleAction() { return this; }
        public void itemStateChanged(java.awt.event.ItemEvent event) { firePropertyChange(ACCESSIBLE_STATE_PROPERTY, null, javax.accessibility.AccessibleState.CHECKED); }
        public Number getCurrentAccessibleValue() { return null; }
        public boolean setCurrentAccessibleValue(Number value) { return false; }
        public Number getMinimumAccessibleValue() { return null; }
        public Number getMaximumAccessibleValue() { return null; }
        public javax.accessibility.AccessibleValue getAccessibleValue() { return this; }

        public AccessibleAWTCheckbox() {}
        @Override
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.CHECK_BOX;
        }

        @Override
        public AccessibleStateSet getAccessibleStateSet() {
            AccessibleStateSet states = new AccessibleStateSet();
            if (isEnabled()) {
                states.add(AccessibleState.ENABLED);
            }
            if (state) {
                states.add(AccessibleState.CHECKED);
            }
            if (isVisible()) {
                states.add(AccessibleState.VISIBLE);
            }
            return states;
        }

        @Override
        public int getAccessibleIndexInParent() {
            return getParent() == null ? -1 : getParent().getComponentZOrder(Checkbox.this);
        }

        @Override
        public int getAccessibleChildrenCount() {
            return 0;
        }

        @Override
        public Accessible getAccessibleChild(int index) {
            return null;
        }

        @Override
        public Locale getLocale() {
            return Checkbox.this.getLocale();
        }
    }

    private void readStateFromPeer() {
        boolean previous = state;
        setState(isPeerSelected());
        updatePeerSelected(state);
        if (previous == state) {
            return;
        }
        ItemEvent event = new ItemEvent(this, ItemEvent.ITEM_STATE_CHANGED, label, state ? ItemEvent.SELECTED : ItemEvent.DESELECTED);
        dispatchEvent(event);
    }
}
