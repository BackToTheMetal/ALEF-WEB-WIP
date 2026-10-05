package java.awt;

import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import org.webawt.internal.peer.MenuPeer;
import org.webawt.internal.peer.WebAwtRuntime;

public class PopupMenu extends Menu {
    private static final long serialVersionUID = -4620452533522760060L;
    private AccessibleContext accessibleContext;
    private MenuPeer peer;
    private Component origin;
    private int popupX;
    private int popupY;
    private boolean popupVisible;

    public PopupMenu() throws HeadlessException {
        this("");
    }

    public PopupMenu(String label) throws HeadlessException {
        super(label);
    }

    @Override
    public MenuContainer getParent() {
        return super.getParent();
    }

    @Override
    public void addNotify() {
        super.addNotify();
        if (peer == null) {
            peer = WebAwtRuntime.createMenuPeer(false);
        }
        MenuContainer parent = getParent();
        if (parent instanceof Component && ((Component) parent).getPeer() != null && !peer.isAttached()) {
            peer.attachTo(((Component) parent).getPeer());
        }
        synchronizePeer();
    }

    public void show(Component origin, int x, int y) {
        MenuContainer parent = getParent();
        if (parent == null) {
            throw new NullPointerException("parent is null");
        }
        if (!(parent instanceof Component)) {
            throw new IllegalArgumentException("PopupMenus with non-Component parents cannot be shown");
        }
        Component componentParent = (Component) parent;
        if (componentParent != origin && (!(componentParent instanceof Container) || !((Container) componentParent).isAncestorOf(origin))) {
            throw new IllegalArgumentException("origin not in parent's hierarchy");
        }
        if (!componentParent.isShowing()) {
            throw new RuntimeException("parent not showing on screen");
        }
        if (!origin.isShowing()) {
            throw new IllegalComponentStateException("origin not showing on screen");
        }
        if (!isDisplayableMenuItem()) {
            addNotify();
        }
        this.origin = origin;
        popupX = x;
        popupY = y;
        popupVisible = true;
        if (peer != null) {
            peer.showPopup(origin.getPeer(), x, y);
        }
    }

    @Override
    public AccessibleContext getAccessibleContext() {
        if (accessibleContext == null) {
            accessibleContext = new AccessibleAWTPopupMenu();
        }
        return accessibleContext;
    }

    Component getPopupOrigin() {
        return origin;
    }

    int getPopupX() {
        return popupX;
    }

    int getPopupY() {
        return popupY;
    }

    boolean isPopupVisible() {
        return popupVisible;
    }

    void hidePopup() {
        popupVisible = false;
        if (peer != null) {
            peer.hide();
        }
    }

    @Override
    public void removeNotify() {
        if (peer != null) {
            peer.detach();
            peer = null;
        }
        popupVisible = false;
        super.removeNotify();
    }

    void synchronizePeer() {
        if (peer != null) {
            peer.setEntries(createChildPeerEntries());
            peer.setFont(getMenuCssFont());
        }
    }

    protected class AccessibleAWTPopupMenu extends AccessibleAWTMenu {
        @Override
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.POPUP_MENU;
        }
    }
}
