package java.awt;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.Locale;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleState;
import javax.accessibility.AccessibleStateSet;

public class Choice extends Component implements ItemSelectable, Accessible {
    private final java.util.List<ItemListener> itemListeners = new ArrayList<>();
    private final java.util.List<String> items = new ArrayList<>();
    private int selectedIndex = -1;

    public Choice() throws HeadlessException {
        super("select");
    }

    public int getItemCount() {
        return items.size();
    }

    public int countItems() {
        return getItemCount();
    }

    public String getItem(int index) {
        return items.get(index);
    }

    public void add(String item) {
        addItem(item);
    }

    public void addItem(String item) {
        if (item == null) {
            throw new NullPointerException("item");
        }
        items.add(item);
        if (selectedIndex < 0) {
            selectedIndex = 0;
        }
        synchronizeItems();
        invalidate();
    }

    public void insert(String item, int index) {
        if (item == null) {
            throw new NullPointerException("item");
        }
        if (index < 0 || index > items.size()) {
            throw new IllegalArgumentException("index out of range: " + index);
        }
        items.add(index, item);
        selectedIndex = selectedIndex < 0 ? 0 : selectedIndex >= index ? selectedIndex + 1 : selectedIndex;
        synchronizeItems();
        invalidate();
    }

    public void remove(String item) {
        int index = items.indexOf(item);
        if (index < 0) {
            throw new IllegalArgumentException("item not found: " + item);
        }
        remove(index);
    }

    public void remove(int index) {
        items.remove(index);
        if (items.isEmpty()) {
            selectedIndex = -1;
        } else if (selectedIndex == index) {
            selectedIndex = Math.min(index, items.size() - 1);
        } else if (selectedIndex > index) {
            selectedIndex--;
        }
        synchronizeItems();
        invalidate();
    }

    public void removeAll() {
        items.clear();
        selectedIndex = -1;
        synchronizeItems();
        invalidate();
    }

    public String getSelectedItem() {
        return selectedIndex < 0 ? null : items.get(selectedIndex);
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    @Override
    public Object[] getSelectedObjects() {
        String item = getSelectedItem();
        return item == null ? null : new Object[] {item};
    }

    public void select(int index) {
        if (index < 0 || index >= items.size()) {
            throw new IllegalArgumentException("index out of range: " + index);
        }
        selectedIndex = index;
        synchronizeItems();
    }

    public void select(String item) {
        int index = items.indexOf(item);
        if (index >= 0) {
            select(index);
        }
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
        synchronizeItems();
        updatePeerItemChangeHandler(this::readSelectionFromPeer);
    }

    @Override
    public Dimension getMinimumSize() {
        if (isMinimumSizeSet()) {
            return super.getMinimumSize();
        }
        int characters = 1;
        for (String item : items) {
            characters = Math.max(characters, item.length());
        }
        return new Dimension(28 + characters * 8, 24);
    }

    @Override
    public Dimension getPreferredSize() {
        return isPreferredSizeSet() ? super.getPreferredSize() : getMinimumSize();
    }

    @Override
    public AccessibleContext getAccessibleContext() {
        if (accessibleContext == null) {
            accessibleContext = new AccessibleAWTChoice();
        }
        return accessibleContext;
    }

    protected class AccessibleAWTChoice extends AccessibleAWTComponent implements javax.accessibility.AccessibleAction {

        public int getAccessibleActionCount() { return 0; }
        public String getAccessibleActionDescription(int index) { return null; }
        public boolean doAccessibleAction(int index) { return false; }
        public javax.accessibility.AccessibleAction getAccessibleAction() { return this; }

        public AccessibleAWTChoice() {}
        @Override
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.COMBO_BOX;
        }

        @Override
        public AccessibleStateSet getAccessibleStateSet() {
            AccessibleStateSet states = new AccessibleStateSet();
            if (isEnabled()) {
                states.add(AccessibleState.ENABLED);
            }
            if (isVisible()) {
                states.add(AccessibleState.VISIBLE);
            }
            return states;
        }

        @Override
        public int getAccessibleIndexInParent() {
            return getParent() == null ? -1 : getParent().getComponentZOrder(Choice.this);
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
            return Choice.this.getLocale();
        }
    }

    private void synchronizeItems() {
        String[] values = items.toArray(new String[0]);
        int[] indexes = selectedIndex < 0 ? new int[0] : new int[] {selectedIndex};
        updatePeerSelectionItems(values, indexes, false, 0);
    }

    private void readSelectionFromPeer() {
        int[] indexes = getPeerSelectedIndexes();
        int nextIndex = indexes.length == 0 ? -1 : indexes[0];
        if (nextIndex == selectedIndex || nextIndex < 0 || nextIndex >= items.size()) {
            return;
        }
        selectedIndex = nextIndex;
        ItemEvent event = new ItemEvent(this, ItemEvent.ITEM_STATE_CHANGED, getSelectedItem(), ItemEvent.SELECTED);
        dispatchEvent(event);
    }
}
