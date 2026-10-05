package java.awt;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.NoSuchElementException;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import org.webawt.internal.peer.MenuPeer;
import org.webawt.internal.peer.MenuPeerEntry;
import org.webawt.internal.peer.WebAwtRuntime;

public class MenuBar extends MenuComponent implements MenuContainer, Accessible {
    private static final long serialVersionUID = -4930327919388951260L;
    private final List<Menu> menus = new ArrayList<>();
    private AccessibleContext accessibleContext;
    private Menu helpMenu;
    private MenuPeer peer;
    private boolean displayable;

    public MenuBar() throws HeadlessException {
    }

    public void addNotify() {
        displayable = true;
        if (peer == null) {
            peer = WebAwtRuntime.createMenuPeer(true);
        }
        MenuContainer parent = getParent();
        if (parent instanceof Component && ((Component) parent).getPeer() != null && !peer.isAttached()) {
            peer.attachTo(((Component) parent).getPeer());
        }
        for (Menu menu : snapshotMenus()) {
            menu.addNotify();
        }
        synchronizePeer();
        peer.show(0, 0);
    }

    @Override
    public void removeNotify() {
        for (Menu menu : snapshotMenus()) {
            menu.removeNotify();
        }
        if (peer != null) {
            peer.detach();
            peer = null;
        }
        displayable = false;
        super.removeNotify();
    }

    public Menu getHelpMenu() {
        return helpMenu;
    }

    public void setHelpMenu(Menu menu) {
        synchronized (getTreeLock()) {
            if (helpMenu == menu) {
                return;
            }
            if (helpMenu != null) {
                helpMenu.setHelpMenu(false);
                remove(helpMenu);
            }
            helpMenu = menu;
            if (menu != null) {
                if (menu.getParent() != this) {
                    add(menu);
                }
                menu.setHelpMenu(true);
            }
            synchronizePeer();
        }
    }

    public Menu add(Menu menu) {
        if (menu == null) {
            throw new NullPointerException("menu");
        }
        synchronized (getTreeLock()) {
            MenuContainer previousParent = menu.getParent();
            if (previousParent != null) {
                previousParent.remove(menu);
            }
            menus.add(menu);
            menu.setParent(this);
            if (displayable) {
                menu.addNotify();
            }
            synchronizePeer();
        }
        return menu;
    }

    public void remove(int index) {
        synchronized (getTreeLock()) {
            Menu menu = menus.remove(index);
            menu.removeNotify();
            menu.setParent(null);
            if (menu == helpMenu) {
                menu.setHelpMenu(false);
                helpMenu = null;
            }
            synchronizePeer();
        }
    }

    @Override
    public void remove(MenuComponent component) {
        synchronized (getTreeLock()) {
            int index = menus.indexOf(component);
            if (index >= 0) {
                remove(index);
            }
        }
    }

    public int getMenuCount() {
        return countMenus();
    }

    @Deprecated
    public int countMenus() {
        return menus.size();
    }

    public Menu getMenu(int index) {
        return menus.get(index);
    }

    public synchronized Enumeration<MenuShortcut> shortcuts() {
        List<MenuShortcut> shortcuts = new ArrayList<>();
        for (Menu menu : snapshotMenus()) {
            Enumeration<MenuShortcut> menuShortcuts = menu.shortcuts();
            while (menuShortcuts.hasMoreElements()) {
                shortcuts.add(menuShortcuts.nextElement());
            }
        }
        return new MenuShortcutEnumeration(shortcuts);
    }

    public MenuItem getShortcutMenuItem(MenuShortcut shortcut) {
        if (shortcut == null) {
            return null;
        }
        for (Menu menu : snapshotMenus()) {
            MenuItem match = menu.getShortcutMenuItem(shortcut);
            if (match != null) {
                return match;
            }
        }
        return null;
    }

    public void deleteShortcut(MenuShortcut shortcut) {
        if (shortcut == null) {
            return;
        }
        for (Menu menu : snapshotMenus()) {
            menu.deleteShortcut(shortcut);
        }
    }

    @Override
    public AccessibleContext getAccessibleContext() {
        if (accessibleContext == null) {
            accessibleContext = new AccessibleAWTMenuBar();
        }
        return accessibleContext;
    }

    @Override
    int getAccessibleChildIndex(MenuComponent child) {
        return menus.indexOf(child);
    }

    void synchronizePeer() {
        if (peer == null) {
            notifyFrameLayout();
            return;
        }
        MenuPeerEntry[] entries = new MenuPeerEntry[menus.size()];
        for (int index = 0; index < entries.length; index++) {
            entries[index] = menus.get(index).createPeerEntry();
        }
        peer.setEntries(entries);
        peer.setFont(getMenuCssFont());
        peer.setHelpMenu(menus.indexOf(helpMenu));
        notifyFrameLayout();
    }

    boolean handleShortcut(java.awt.event.KeyEvent event) {
        if (event.getID() != java.awt.event.KeyEvent.KEY_PRESSED && event.getID() != java.awt.event.KeyEvent.KEY_RELEASED) {
            return false;
        }
        int mask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx();
        int modifiers = event.getModifiersEx() & ~java.awt.event.InputEvent.SHIFT_DOWN_MASK;
        if (modifiers != mask) {
            return false;
        }
        MenuItem item = getShortcutMenuItem(new MenuShortcut(event.getKeyCode(), event.isShiftDown()));
        if (item == null || !item.isItemEnabled()) {
            return false;
        }
        if (event.getID() == java.awt.event.KeyEvent.KEY_PRESSED) {
            item.doMenuEvent(event.getWhen(), event.getModifiers());
        }
        return true;
    }

    int getBarHeight() {
        int measured = peer == null ? 0 : peer.getPreferredHeight();
        Font font = Component.effectiveFont(getFont());
        return measured > 0 ? measured : new FontMetrics(font) { }.getHeight() + 10;
    }

    private void notifyFrameLayout() {
        if (getParent() instanceof Frame) {
            ((Frame) getParent()).menuBarChanged();
        }
    }

    private List<Menu> snapshotMenus() {
        return new ArrayList<>(menus);
    }

    protected class AccessibleAWTMenuBar extends AccessibleAWTMenuComponent {
        @Override
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.MENU_BAR;
        }

    }

    private static final class MenuShortcutEnumeration implements Enumeration<MenuShortcut> {
        private final List<MenuShortcut> shortcuts;
        private int index;

        private MenuShortcutEnumeration(List<MenuShortcut> shortcuts) {
            this.shortcuts = shortcuts;
        }

        @Override
        public boolean hasMoreElements() {
            return index < shortcuts.size();
        }

        @Override
        public MenuShortcut nextElement() {
            if (!hasMoreElements()) {
                throw new NoSuchElementException();
            }
            return shortcuts.get(index++);
        }
    }
}
