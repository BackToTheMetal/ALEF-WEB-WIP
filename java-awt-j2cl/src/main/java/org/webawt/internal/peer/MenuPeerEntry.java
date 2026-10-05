package org.webawt.internal.peer;

public final class MenuPeerEntry {
    private final MenuPeerEntry[] children;
    private final Runnable action;
    private final String label;
    private final String shortcut;
    private final boolean checked;
    private final boolean checkbox;
    private final boolean enabled;
    private final boolean menu;
    private final boolean separator;

    public MenuPeerEntry(String label, String shortcut, boolean enabled, boolean menu, boolean checkbox, boolean checked, boolean separator, MenuPeerEntry[] children, Runnable action) {
        this.label = label;
        this.shortcut = shortcut;
        this.enabled = enabled;
        this.menu = menu;
        this.checkbox = checkbox;
        this.checked = checked;
        this.separator = separator;
        this.children = copy(children == null ? new MenuPeerEntry[0] : children);
        this.action = action;
    }

    public String getLabel() {
        return label;
    }

    public String getShortcut() {
        return shortcut;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isCheckbox() {
        return checkbox;
    }

    public boolean isMenu() {
        return menu;
    }

    public boolean isChecked() {
        return checked;
    }

    public boolean isSeparator() {
        return separator;
    }

    public MenuPeerEntry[] getChildren() {
        return copy(children);
    }

    public boolean hasChildren() {
        return children.length > 0;
    }

    public void activate() {
        if (enabled && action != null) {
            action.run();
        }
    }

    private static MenuPeerEntry[] copy(MenuPeerEntry[] values) {
        MenuPeerEntry[] copy = new MenuPeerEntry[values.length];
        System.arraycopy(values, 0, copy, 0, values.length);
        return copy;
    }
}
