package java.awt;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.NoSuchElementException;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import org.webawt.internal.peer.MenuPeerEntry;

public class Menu extends MenuItem implements MenuContainer, Accessible {
    private static final long serialVersionUID = -8809584163345499784L;
    private final List<MenuItem> items = new ArrayList<>();
    private AccessibleContext accessibleContext;
    private final boolean tearOff;
    private boolean helpMenu;

    public Menu() throws HeadlessException {
        this("", false);
    }

    public Menu(String label) throws HeadlessException {
        this(label, false);
    }

    public Menu(String label, boolean tearOff) throws HeadlessException {
        super(label);
        this.tearOff = tearOff;
    }

    @Override
    public void addNotify() {
        super.addNotify();
        for (MenuItem item : snapshotItems()) {
            item.addNotify();
        }
    }

    @Override
    public void removeNotify() {
        for (MenuItem item : snapshotItems()) {
            item.removeNotify();
        }
        super.removeNotify();
    }

    public boolean isTearOff() {
        return tearOff;
    }

    public int getItemCount() {
        return countItems();
    }

    @Deprecated
    public int countItems() {
        return items.size();
    }

    public MenuItem getItem(int index) {
        return items.get(index);
    }

    public MenuItem add(MenuItem item) {
        if (item == null) {
            throw new NullPointerException("item");
        }
        synchronized (getTreeLock()) {
            MenuContainer previousParent = item.getParent();
            if (previousParent != null) {
                previousParent.remove(item);
            }
            items.add(item);
            item.setParent(this);
            if (isDisplayableMenuItem()) {
                item.addNotify();
            }
            refreshMenuPeer();
        }
        return item;
    }

    public void add(String label) {
        add(new MenuItem(label));
    }

    public void insert(MenuItem item, int index) {
        if (item == null) {
            throw new NullPointerException("item");
        }
        if (index < 0) {
            throw new IllegalArgumentException("index less than zero");
        }
        synchronized (getTreeLock()) {
            MenuContainer previousParent = item.getParent();
            if (previousParent != null) {
                previousParent.remove(item);
            }
            items.add(Math.min(index, items.size()), item);
            item.setParent(this);
            if (isDisplayableMenuItem()) {
                item.addNotify();
            }
            refreshMenuPeer();
        }
    }

    public void insert(String label, int index) {
        insert(new MenuItem(label), index);
    }

    public void addSeparator() {
        add("-");
    }

    public void insertSeparator(int index) {
        insert("-", index);
    }

    public void remove(int index) {
        synchronized (getTreeLock()) {
            MenuItem item = items.remove(index);
            item.removeNotify();
            item.setParent(null);
            refreshMenuPeer();
        }
    }

    @Override
    public void remove(MenuComponent component) {
        synchronized (getTreeLock()) {
            int index = items.indexOf(component);
            if (index >= 0) {
                remove(index);
            }
        }
    }

    public void removeAll() {
        synchronized (getTreeLock()) {
            for (int index = items.size() - 1; index >= 0; index--) {
                remove(index);
            }
        }
    }

    @Override
    public String paramString() {
        return super.paramString() + ",tearOff=" + tearOff + ",isHelpMenu=" + helpMenu;
    }

    @Override
    public AccessibleContext getAccessibleContext() {
        if (accessibleContext == null) {
            accessibleContext = new AccessibleAWTMenu();
        }
        return accessibleContext;
    }

    synchronized Enumeration<MenuShortcut> shortcuts() {
        List<MenuShortcut> shortcuts = new ArrayList<>();
        collectShortcuts(shortcuts);
        return new SnapshotEnumeration<>(shortcuts);
    }

    @Override
    MenuItem getShortcutMenuItem(MenuShortcut shortcut) {
        for (MenuItem item : snapshotItems()) {
            MenuItem match = item.getShortcutMenuItem(shortcut);
            if (match != null) {
                return match;
            }
        }
        return null;
    }

    @Override
    void deleteShortcut(MenuShortcut shortcut) {
        for (MenuItem item : snapshotItems()) {
            item.deleteShortcut(shortcut);
        }
    }

    @Override
    int getAccessibleChildIndex(MenuComponent child) {
        return items.indexOf(child);
    }

    void setHelpMenu(boolean helpMenu) {
        this.helpMenu = helpMenu;
    }

    @Override
    MenuPeerEntry createPeerEntry() {
        return new MenuPeerEntry(getLabel(), null, isItemEnabled(), true, false, false, false, createChildPeerEntries(), null);
    }

    MenuPeerEntry[] createChildPeerEntries() {
        MenuPeerEntry[] entries = new MenuPeerEntry[items.size()];
        for (int index = 0; index < entries.length; index++) {
            entries[index] = items.get(index).createPeerEntry();
        }
        return entries;
    }

    private void collectShortcuts(List<MenuShortcut> shortcuts) {
        for (MenuItem item : snapshotItems()) {
            MenuShortcut shortcut = item.getShortcut();
            if (shortcut != null) {
                shortcuts.add(shortcut);
            }
            if (item instanceof Menu) {
                ((Menu) item).collectShortcuts(shortcuts);
            }
        }
    }

    private List<MenuItem> snapshotItems() {
        return new ArrayList<>(items);
    }

    protected class AccessibleAWTMenu extends AccessibleAWTMenuItem {
        @Override
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.MENU;
        }

    }

    private static final class SnapshotEnumeration<T> implements Enumeration<T> {
        private final List<T> values;
        private int index;

        private SnapshotEnumeration(List<T> values) {
            this.values = values;
        }

        @Override
        public boolean hasMoreElements() {
            return index < values.size();
        }

        @Override
        public T nextElement() {
            if (!hasMoreElements()) {
                throw new NoSuchElementException();
            }
            return values.get(index++);
        }
    }
}
