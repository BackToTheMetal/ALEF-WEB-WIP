package java.awt;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

public class List extends Component implements ItemSelectable, Accessible {
    private final java.util.List<ActionListener> actionListeners = new ArrayList<>();
    private final java.util.List<ItemListener> itemListeners = new ArrayList<>();
    private final java.util.List<String> items = new ArrayList<>();
    private int[] selectedIndexes = new int[0];
    private int visibleIndex = -1;
    private int rows;
    private boolean multipleMode;

    public List() throws HeadlessException {
        this(0, false);
    }

    public List(int rows) throws HeadlessException {
        this(rows, false);
    }

    public List(int rows, boolean multipleMode) throws HeadlessException {
        super("select");
        if (rows < 0) {
            throw new IllegalArgumentException("rows must not be negative");
        }
        this.rows = rows == 0 ? 4 : rows;
        this.multipleMode = multipleMode;
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

    public String[] getItems() {
        return items.toArray(new String[0]);
    }

    public void add(String item) {
        addItem(item);
    }

    public void addItem(String item) {
        add(item, -1);
    }

    public void add(String item, int index) {
        if (item == null) {
            throw new NullPointerException("item");
        }
        int insertionIndex = index < 0 || index >= items.size() ? items.size() : index;
        items.add(insertionIndex, item);
        shiftSelection(insertionIndex, 1);
        synchronizeItems();
        invalidate();
    }

    public void addItem(String item, int index) {
        add(item, index);
    }

    public void replaceItem(String item, int index) {
        if (item == null) {
            throw new NullPointerException("item");
        }
        items.set(index, item);
        synchronizeItems();
    }

    public void removeAll() {
        items.clear();
        selectedIndexes = new int[0];
        synchronizeItems();
        invalidate();
    }

    public void clear() {
        removeAll();
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
        selectedIndexes = withoutIndex(selectedIndexes, index);
        shiftSelection(index, -1);
        synchronizeItems();
        invalidate();
    }

    public void delItem(int index) {
        remove(index);
    }

    public synchronized void delItems(int start, int end) {
        for (int index = end; index >= start; index--) {
            remove(index);
        }
    }

    public int getSelectedIndex() {
        return selectedIndexes.length == 1 ? selectedIndexes[0] : -1;
    }

    public int[] getSelectedIndexes() {
        return copy(selectedIndexes);
    }

    public String getSelectedItem() {
        int index = getSelectedIndex();
        return index < 0 ? null : items.get(index);
    }

    public String[] getSelectedItems() {
        String[] selectedItems = new String[selectedIndexes.length];
        for (int index = 0; index < selectedIndexes.length; index++) {
            selectedItems[index] = items.get(selectedIndexes[index]);
        }
        return selectedItems;
    }

    @Override
    public Object[] getSelectedObjects() {
        return getSelectedItems();
    }

    public void select(int index) {
        checkIndex(index);
        if (contains(selectedIndexes, index)) {
            return;
        }
        if (!multipleMode) {
            selectedIndexes = new int[] {index};
        } else {
            int[] next = new int[selectedIndexes.length + 1];
            System.arraycopy(selectedIndexes, 0, next, 0, selectedIndexes.length);
            next[selectedIndexes.length] = index;
            selectedIndexes = next;
        }
        synchronizeItems();
    }

    public void deselect(int index) {
        selectedIndexes = withoutIndex(selectedIndexes, index);
        synchronizeItems();
    }

    public boolean isIndexSelected(int index) {
        return contains(selectedIndexes, index);
    }

    public boolean isSelected(int index) {
        return isIndexSelected(index);
    }

    public int getRows() {
        return rows;
    }

    public boolean isMultipleMode() {
        return multipleMode;
    }

    public boolean allowsMultipleSelections() {
        return isMultipleMode();
    }

    public void setMultipleMode(boolean multipleMode) {
        this.multipleMode = multipleMode;
        if (!multipleMode && selectedIndexes.length > 1) {
            selectedIndexes = new int[] {selectedIndexes[0]};
        }
        synchronizeItems();
    }

    public void setMultipleSelections(boolean multipleMode) {
        setMultipleMode(multipleMode);
    }

    public int getVisibleIndex() {
        return visibleIndex;
    }

    public void makeVisible(int index) {
        checkIndex(index);
        visibleIndex = index;
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
        if (listenerType == ActionListener.class) {
            return (T[]) getActionListeners();
        }
        return listenerType == ItemListener.class ? (T[]) getItemListeners() : super.getListeners(listenerType);
    }

    @Override
    protected boolean eventEnabled(AWTEvent event) {
        if (event.getID() == ItemEvent.ITEM_STATE_CHANGED && !itemListeners.isEmpty()) {
            return true;
        }
        if (event.getID() == ActionEvent.ACTION_PERFORMED && !actionListeners.isEmpty()) {
            return true;
        }
        return super.eventEnabled(event);
    }

    @Override
    protected void processEvent(AWTEvent event) {
        if (event instanceof ItemEvent) {
            processItemEvent((ItemEvent) event);
        } else if (event instanceof ActionEvent) {
            processActionEvent((ActionEvent) event);
        } else {
            super.processEvent(event);
        }
    }

    protected void processItemEvent(ItemEvent event) {
        for (ItemListener listener : getItemListeners()) {
            listener.itemStateChanged(event);
        }
    }

    protected void processActionEvent(ActionEvent event) {
        for (ActionListener listener : getActionListeners()) {
            listener.actionPerformed(event);
        }
    }

    @Override
    public void addNotify() {
        super.addNotify();
        synchronizeItems();
        updatePeerItemChangeHandler(this::readSelectionFromPeer);
        updatePeerActionHandler(this::dispatchAction);
    }

    @Override
    public Dimension getMinimumSize() {
        return getMinimumSize(rows);
    }

    public Dimension getMinimumSize(int rows) {
        if (isMinimumSizeSet()) {
            return super.getMinimumSize();
        }
        return new Dimension(80, Math.max(1, rows) * 20);
    }

    public Dimension minimumSize(int rows) {
        return getMinimumSize(rows);
    }

    @Override
    public Dimension getPreferredSize() {
        return getPreferredSize(rows);
    }

    public Dimension getPreferredSize(int rows) {
        return isPreferredSizeSet() ? super.getPreferredSize() : getMinimumSize(rows);
    }

    public Dimension preferredSize(int rows) {
        return getPreferredSize(rows);
    }

    @Override
    public AccessibleContext getAccessibleContext() {
        if (accessibleContext == null) {
            accessibleContext = new AccessibleAWTList();
        }
        return accessibleContext;
    }

    protected class AccessibleAWTList extends AccessibleAWTComponent implements javax.accessibility.AccessibleSelection, java.awt.event.ItemListener, java.awt.event.ActionListener {
        public javax.accessibility.AccessibleSelection getAccessibleSelection() { return this; }
        public void actionPerformed(ActionEvent event) { firePropertyChange(ACCESSIBLE_STATE_PROPERTY, null, AccessibleState.SELECTED); }
        public void itemStateChanged(ItemEvent event) { firePropertyChange(ACCESSIBLE_SELECTION_PROPERTY, null, this); }
        public Accessible getAccessibleAt(Point point) { return null; }
        public int getAccessibleSelectionCount() { return getSelectedIndexes().length; }
        public Accessible getAccessibleSelection(int index) { int[] indexes = getSelectedIndexes(); return index < 0 || index >= indexes.length ? null : getAccessibleChild(indexes[index]); }
        public boolean isAccessibleChildSelected(int index) { return isIndexSelected(index); }
        public void addAccessibleSelection(int index) { if (index >= 0 && index < getItemCount()) { select(index); } }
        public void removeAccessibleSelection(int index) { if (index >= 0 && index < getItemCount()) { deselect(index); } }
        public void clearAccessibleSelection() { for (int index : getSelectedIndexes()) { deselect(index); } }
        public void selectAllAccessibleSelection() { for (int index = 0; index < getItemCount(); index++) { select(index); } }

        protected class AccessibleAWTListChild extends AccessibleAWTComponent implements Accessible {
            private final List list;
            private final int index;
            public AccessibleAWTListChild(List list, int index) { this.list = list; this.index = index; setAccessibleParent(list); }
            public AccessibleContext getAccessibleContext() { return this; }
            public AccessibleRole getAccessibleRole() { return AccessibleRole.LIST_ITEM; }
            public AccessibleStateSet getAccessibleStateSet() { AccessibleStateSet states = super.getAccessibleStateSet(); if (list.isIndexSelected(index)) { states.add(AccessibleState.SELECTED); } return states; }
            public Locale getLocale() { return list.getLocale(); }
            public int getAccessibleIndexInParent() { return index; }
            public boolean isVisible() { return false; }
            public boolean isShowing() { return false; }
            public boolean contains(Point point) { return false; }
            public Point getLocationOnScreen() { return null; }
            public Point getLocation() { return null; }
            public Rectangle getBounds() { return null; }
            public Dimension getSize() { return null; }
            public void setLocation(Point point) {}
            public void setBounds(Rectangle bounds) {}
            public void setSize(Dimension size) {}
            public boolean isFocusTraversable() { return false; }
            public void requestFocus() {}
            public void addFocusListener(java.awt.event.FocusListener listener) {}
            public void removeFocusListener(java.awt.event.FocusListener listener) {}
        }

        public AccessibleAWTList() {}
        @Override
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.LIST;
        }

        @Override
        public AccessibleStateSet getAccessibleStateSet() {
            AccessibleStateSet states = new AccessibleStateSet();
            if (isEnabled()) {
                states.add(AccessibleState.ENABLED);
            }
            if (multipleMode) {
                states.add(AccessibleState.MULTISELECTABLE);
            }
            return states;
        }

        @Override
        public int getAccessibleIndexInParent() {
            return getParent() == null ? -1 : getParent().getComponentZOrder(List.this);
        }

        @Override
        public int getAccessibleChildrenCount() {
            return getItemCount();
        }

        @Override
        public Accessible getAccessibleChild(int index) {
            return index < 0 || index >= getItemCount() ? null : new AccessibleAWTListChild(List.this, index);
        }

        @Override
        public Locale getLocale() {
            return List.this.getLocale();
        }
    }

    private void synchronizeItems() {
        updatePeerSelectionItems(getItems(), selectedIndexes, multipleMode, rows);
    }

    private void readSelectionFromPeer() {
        int[] previous = selectedIndexes;
        selectedIndexes = getPeerSelectedIndexes();
        for (int index : previous) {
            if (!contains(selectedIndexes, index)) {
                dispatchItem(index, ItemEvent.DESELECTED);
            }
        }
        for (int index : selectedIndexes) {
            if (!contains(previous, index)) {
                dispatchItem(index, ItemEvent.SELECTED);
            }
        }
    }

    private void dispatchItem(int index, int stateChange) {
        if (index < 0 || index >= items.size()) {
            return;
        }
        ItemEvent event = new ItemEvent(this, ItemEvent.ITEM_STATE_CHANGED, items.get(index), stateChange);
        dispatchEvent(event);
    }

    private void dispatchAction() {
        String command = getSelectedItem();
        ActionEvent event = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, command);
        dispatchEvent(event);
    }

    private void checkIndex(int index) {
        if (index < 0 || index >= items.size()) {
            throw new IllegalArgumentException("index out of range: " + index);
        }
    }

    private void shiftSelection(int index, int delta) {
        for (int selection = 0; selection < selectedIndexes.length; selection++) {
            if (selectedIndexes[selection] >= index) {
                selectedIndexes[selection] += delta;
            }
        }
    }

    private static int[] withoutIndex(int[] values, int removed) {
        int count = contains(values, removed) ? values.length - 1 : values.length;
        int[] result = new int[count];
        int outputIndex = 0;
        for (int value : values) {
            if (value != removed) {
                result[outputIndex++] = value;
            }
        }
        return result;
    }

    private static boolean contains(int[] values, int expected) {
        for (int value : values) {
            if (value == expected) {
                return true;
            }
        }
        return false;
    }

    private static int[] copy(int[] values) {
        int[] result = new int[values.length];
        System.arraycopy(values, 0, result, 0, values.length);
        return result;
    }
}
