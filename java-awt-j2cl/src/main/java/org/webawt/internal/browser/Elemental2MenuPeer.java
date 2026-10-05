package org.webawt.internal.browser;

import elemental2.dom.DomGlobal;
import elemental2.dom.Element;
import elemental2.dom.Event;
import elemental2.dom.EventListener;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.KeyboardEvent;
import elemental2.dom.Node;
import java.util.ArrayList;
import java.util.List;
import org.webawt.internal.peer.DomPeer;
import org.webawt.internal.peer.MenuPeer;
import org.webawt.internal.peer.MenuPeerEntry;

final class Elemental2MenuPeer implements MenuPeer {
    private static int nextId;
    private final List<ButtonBinding> buttons = new ArrayList<>();
    private final HTMLElement root;
    private final boolean menuBar;
    private final EventListener documentClickListener = this::handleDocumentClick;
    private final EventListener keyboardListener = this::handleKeyboardEvent;
    private boolean attached;

    Elemental2MenuPeer(boolean menuBar) {
        this.menuBar = menuBar;
        root = createElement(menuBar ? "nav" : "div");
        root.id = "webawt-" + (menuBar ? "menubar-" : "popup-menu-") + nextId++;
        root.setAttribute("role", menuBar ? "menubar" : "menu");
        root.style.setProperty("position", menuBar ? "absolute" : "fixed");
        root.style.setProperty("z-index", "1000");
        root.style.setProperty("display", menuBar ? "flex" : "none");
        root.style.setProperty("gap", menuBar ? "2px" : "0");
        root.style.setProperty("padding", "2px");
        root.style.setProperty("background", "Canvas");
        root.style.setProperty("color", "CanvasText");
        root.style.setProperty("border", menuBar ? "0" : "1px solid ButtonBorder");
        if (menuBar) {
            root.style.setProperty("width", "100%");
            root.style.setProperty("box-sizing", "border-box");
            root.style.setProperty("flex-wrap", "wrap");
        }
        root.addEventListener("keydown", keyboardListener);
    }

    @Override
    public void attachTo(DomPeer parent) {
        Element parentElement = DomGlobal.document.getElementById(parent.getElementId());
        if (parentElement == null) {
            throw new IllegalStateException("Parent peer is not attached");
        }
        parentElement.appendChild(root);
        DomGlobal.document.addEventListener("click", documentClickListener, true);
        attached = true;
    }

    @Override
    public void setEntries(MenuPeerEntry[] entries) {
        closeAll();
        buttons.clear();
        while (root.firstChild != null) {
            root.removeChild(root.firstChild);
        }
        for (MenuPeerEntry entry : entries) {
            appendEntry(root, entry, null);
        }
        updateTabIndexes();
    }

    @Override
    public void setFont(String cssFont) {
        root.style.setProperty("font", cssFont == null ? "" : cssFont);
    }

    @Override
    public void setHelpMenu(int index) {
        int rootIndex = 0;
        for (ButtonBinding binding : buttons) {
            if (binding.parent == null) {
                boolean help = rootIndex++ == index;
                ((HTMLElement) binding.button.parentNode).style.setProperty("margin-left", help ? "auto" : "");
                if (binding.submenu != null) {
                    binding.submenu.style.setProperty("left", help ? "auto" : "0");
                    binding.submenu.style.setProperty("right", help ? "0" : "auto");
                }
            }
        }
    }

    @Override
    public int getPreferredHeight() {
        return menuBar ? (int) Math.ceil(root.getBoundingClientRect().height) : 0;
    }

    @Override
    public void show(int x, int y) {
        root.style.setProperty("left", x + "px");
        root.style.setProperty("top", y + "px");
        root.style.setProperty("display", menuBar ? "flex" : "block");
        if (!menuBar) {
            focusFirstRootEntry();
        }
    }

    @Override
    public void showPopup(DomPeer origin, int x, int y) {
        Element originElement = DomGlobal.document.getElementById(origin.getElementId());
        if (originElement == null) {
            throw new IllegalStateException("Popup origin peer is not attached");
        }
        closeAll();
        root.style.setProperty("display", "block");
        double left = originElement.getBoundingClientRect().left + x;
        double top = originElement.getBoundingClientRect().top + y;
        root.style.setProperty("left", fitCoordinate(left, root.getBoundingClientRect().width, DomGlobal.window.innerWidth) + "px");
        root.style.setProperty("top", fitCoordinate(top, root.getBoundingClientRect().height, DomGlobal.window.innerHeight) + "px");
        focusFirstRootEntry();
    }

    @Override
    public void hide() {
        closeAll();
        if (!menuBar) {
            root.style.setProperty("display", "none");
        }
    }

    @Override
    public void detach() {
        root.removeEventListener("keydown", keyboardListener);
        DomGlobal.document.removeEventListener("click", documentClickListener, true);
        if (root.parentNode != null) {
            root.parentNode.removeChild(root);
        }
        buttons.clear();
        attached = false;
    }

    @Override
    public boolean isAttached() {
        return attached;
    }

    private void appendEntry(HTMLElement parentElement, MenuPeerEntry entry, ButtonBinding parentBinding) {
        if (entry.isSeparator()) {
            HTMLElement separator = createElement("div");
            separator.setAttribute("role", "separator");
            separator.style.setProperty("border-top", "1px solid ButtonBorder");
            separator.style.setProperty("margin", "3px 2px");
            parentElement.appendChild(separator);
            return;
        }
        HTMLElement wrapper = createElement("div");
        wrapper.style.setProperty("position", "relative");
        HTMLButtonElement button = (HTMLButtonElement) createElement("button");
        button.setAttribute("type", "button");
        button.setAttribute("role", entry.isCheckbox() ? "menuitemcheckbox" : "menuitem");
        button.setAttribute("aria-disabled", entry.isEnabled() ? "false" : "true");
        if (entry.isCheckbox()) {
            button.setAttribute("aria-checked", entry.isChecked() ? "true" : "false");
        }
        button.disabled = !entry.isEnabled();
        button.textContent = entry.getLabel() == null ? "" : entry.getLabel();
        button.style.setProperty("display", "block");
        button.style.setProperty("min-width", menuBar && parentBinding == null ? "auto" : "160px");
        button.style.setProperty("text-align", "left");
        button.style.setProperty("white-space", "nowrap");
        button.style.setProperty("padding", "4px 10px");
        button.style.setProperty("font", "inherit");
        if (menuBar && parentBinding == null) {
            button.style.setProperty("border", "0");
            button.style.setProperty("background", "transparent");
            button.style.setProperty("color", "inherit");
            button.style.setProperty("padding", "3px 10px");
            button.addEventListener("mouseenter", event -> button.style.setProperty("background", "ButtonFace"));
            button.addEventListener("mouseleave", event -> button.style.setProperty("background", "transparent"));
        }
        String shortcut = entry.getShortcut();
        if (shortcut != null) {
            button.setAttribute("aria-keyshortcuts", shortcut.replace("Ctrl+", "Control+"));
        }
        HTMLElement submenu = createSubmenu(entry, parentBinding);
        ButtonBinding binding = new ButtonBinding(button, entry, submenu, parentBinding);
        buttons.add(binding);
        button.addEventListener("click", event -> activate(binding, event));
        button.addEventListener("mouseenter", event -> handleMouseEnter(binding));
        wrapper.appendChild(button);
        if (submenu != null) {
            wrapper.appendChild(submenu);
            for (MenuPeerEntry child : entry.getChildren()) {
                appendEntry(submenu, child, binding);
            }
        }
        parentElement.appendChild(wrapper);
    }

    private HTMLElement createSubmenu(MenuPeerEntry entry, ButtonBinding parentBinding) {
        if (!entry.isMenu()) {
            return null;
        }
        HTMLElement submenu = createElement("div");
        submenu.setAttribute("role", "menu");
        submenu.style.setProperty("display", "none");
        submenu.style.setProperty("position", "absolute");
        submenu.style.setProperty("left", menuBar && parentBinding == null ? "0" : "100%");
        submenu.style.setProperty("top", menuBar && parentBinding == null ? "100%" : "0");
        submenu.style.setProperty("padding", "2px");
        submenu.style.setProperty("background", "Canvas");
        submenu.style.setProperty("border", "1px solid ButtonBorder");
        submenu.style.setProperty("z-index", "1001");
        return submenu;
    }

    private void activate(ButtonBinding binding, Event event) {
        event.preventDefault();
        event.stopPropagation();
        if (binding.entry.isMenu()) {
            toggleMenu(binding);
            return;
        }
        binding.entry.activate();
        hide();
    }

    private void handleMouseEnter(ButtonBinding binding) {
        if (binding.entry.isMenu() && (binding.parent != null || hasOpenMenu())) {
            openMenu(binding);
        }
    }

    private void handleDocumentClick(Event event) {
        if (!containsTarget(event.target)) {
            hide();
        }
    }

    private boolean containsTarget(Object target) {
        Node node = target instanceof Node ? (Node) target : null;
        while (node != null) {
            if (node == root) {
                return true;
            }
            node = node.parentNode;
        }
        return false;
    }

    private void handleKeyboardEvent(Event event) {
        KeyboardEvent keyboardEvent = (KeyboardEvent) event;
        ButtonBinding binding = findBinding(keyboardEvent.target);
        if (binding == null) {
            return;
        }
        String key = keyboardEvent.key;
        if ("Enter".equals(key) || " ".equals(key)) {
            preventAndActivate(keyboardEvent, binding);
        } else if ("ArrowDown".equals(key)) {
            preventAndMove(keyboardEvent, binding.entry.isMenu() ? openAndFirst(binding) : sibling(binding, 1));
        } else if ("ArrowUp".equals(key)) {
            preventAndMove(keyboardEvent, sibling(binding, -1));
        } else if ("ArrowRight".equals(key)) {
            preventAndMove(keyboardEvent, binding.entry.isMenu() ? openAndFirst(binding) : rootSibling(binding, 1));
        } else if ("ArrowLeft".equals(key)) {
            preventAndMove(keyboardEvent, closeAndParent(binding));
        } else if ("Home".equals(key)) {
            preventAndMove(keyboardEvent, firstSibling(binding));
        } else if ("End".equals(key)) {
            preventAndMove(keyboardEvent, lastSibling(binding));
        } else if ("Escape".equals(key)) {
            keyboardEvent.preventDefault();
            closeFrom(binding);
        } else if ("Tab".equals(key)) {
            hide();
        }
    }

    private void preventAndActivate(KeyboardEvent event, ButtonBinding binding) {
        event.preventDefault();
        activate(binding, event);
    }

    private void preventAndMove(KeyboardEvent event, ButtonBinding destination) {
        event.preventDefault();
        if (destination != null) {
            focus(destination);
        }
    }

    private ButtonBinding openAndFirst(ButtonBinding binding) {
        openMenu(binding);
        return firstChild(binding);
    }

    private ButtonBinding closeAndParent(ButtonBinding binding) {
        if (binding.parent != null) {
            ButtonBinding parent = binding.parent;
            closeMenu(parent);
            return parent;
        }
        return rootSibling(binding, -1);
    }

    private void closeFrom(ButtonBinding binding) {
        if (binding.parent != null) {
            ButtonBinding parent = binding.parent;
            closeMenu(parent);
            focus(parent);
        } else {
            closeAll();
            if (!menuBar) {
                hide();
            }
        }
    }

    private ButtonBinding sibling(ButtonBinding binding, int direction) {
        List<ButtonBinding> siblings = siblings(binding);
        int index = siblings.indexOf(binding);
        return siblings.get((index + direction + siblings.size()) % siblings.size());
    }

    private ButtonBinding rootSibling(ButtonBinding binding, int direction) {
        ButtonBinding rootBinding = binding;
        while (rootBinding.parent != null) {
            rootBinding = rootBinding.parent;
        }
        return sibling(rootBinding, direction);
    }

    private ButtonBinding firstSibling(ButtonBinding binding) {
        return siblings(binding).get(0);
    }

    private ButtonBinding lastSibling(ButtonBinding binding) {
        List<ButtonBinding> siblings = siblings(binding);
        return siblings.get(siblings.size() - 1);
    }

    private ButtonBinding firstChild(ButtonBinding binding) {
        for (ButtonBinding candidate : buttons) {
            if (candidate.parent == binding) {
                return candidate;
            }
        }
        return binding;
    }

    private List<ButtonBinding> siblings(ButtonBinding binding) {
        List<ButtonBinding> siblings = new ArrayList<>();
        for (ButtonBinding candidate : buttons) {
            if (candidate.parent == binding.parent) {
                siblings.add(candidate);
            }
        }
        return siblings;
    }

    private ButtonBinding findBinding(Object target) {
        for (ButtonBinding binding : buttons) {
            if (binding.button == target) {
                return binding;
            }
        }
        return null;
    }

    private void toggleMenu(ButtonBinding binding) {
        if (isOpen(binding)) {
            closeMenu(binding);
        } else {
            openMenu(binding);
        }
    }

    private void openMenu(ButtonBinding binding) {
        closeSiblings(binding);
        if (binding.submenu != null) {
            binding.submenu.style.setProperty("display", "block");
            fitSubmenu(binding);
            binding.button.setAttribute("aria-expanded", "true");
        }
    }

    private void fitSubmenu(ButtonBinding binding) {
        HTMLElement submenu = binding.submenu;
        setMenuTransform(submenu, "none");
        double left = submenu.getBoundingClientRect().left;
        double top = submenu.getBoundingClientRect().top;
        double width = submenu.getBoundingClientRect().width;
        double height = submenu.getBoundingClientRect().height;
        double targetLeft = left;
        if ((!menuBar || binding.parent != null) && left + width > DomGlobal.window.innerWidth) {
            targetLeft = binding.button.getBoundingClientRect().left - width;
        }
        double shiftX = fitCoordinate(targetLeft, width, DomGlobal.window.innerWidth) - left;
        double shiftY = fitCoordinate(top, height, DomGlobal.window.innerHeight) - top;
        setMenuTransform(submenu, "translate(" + shiftX + "px, " + shiftY + "px)");
    }

    private static void setMenuTransform(HTMLElement menu, String transform) {
        menu.style.setProperty("-webkit-transform", transform);
        menu.style.setProperty("transform", transform);
    }

    private static double fitCoordinate(double coordinate, double size, double available) {
        return Math.max(0, Math.min(coordinate, available - size));
    }

    private void closeSiblings(ButtonBinding binding) {
        for (ButtonBinding sibling : siblings(binding)) {
            if (sibling != binding) {
                closeMenu(sibling);
            }
        }
    }

    private void closeMenu(ButtonBinding binding) {
        if (binding.submenu != null) {
            binding.submenu.style.setProperty("display", "none");
            binding.button.setAttribute("aria-expanded", "false");
        }
        for (ButtonBinding child : buttons) {
            if (child.parent == binding) {
                closeMenu(child);
            }
        }
    }

    private void closeAll() {
        for (ButtonBinding binding : buttons) {
            if (binding.parent == null) {
                closeMenu(binding);
            }
        }
    }

    private boolean hasOpenMenu() {
        for (ButtonBinding binding : buttons) {
            if (isOpen(binding)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isOpen(ButtonBinding binding) {
        return binding.submenu != null && !"none".equals(binding.submenu.style.display);
    }

    private void focusFirstRootEntry() {
        for (ButtonBinding binding : buttons) {
            if (binding.parent == null) {
                focus(binding);
                return;
            }
        }
    }

    private void focus(ButtonBinding binding) {
        for (ButtonBinding candidate : buttons) {
            candidate.button.tabIndex = candidate == binding ? 0 : -1;
        }
        binding.button.focus();
    }

    private void updateTabIndexes() {
        boolean first = true;
        for (ButtonBinding binding : buttons) {
            binding.button.tabIndex = first && binding.parent == null ? 0 : -1;
            if (binding.parent == null) {
                first = false;
            }
        }
    }

    private static HTMLElement createElement(String name) {
        return (HTMLElement) DomGlobal.document.createElement(name);
    }

    private static final class ButtonBinding {
        private final HTMLElement button;
        private final MenuPeerEntry entry;
        private final HTMLElement submenu;
        private final ButtonBinding parent;

        private ButtonBinding(HTMLElement button, MenuPeerEntry entry, HTMLElement submenu, ButtonBinding parent) {
            this.button = button;
            this.entry = entry;
            this.submenu = submenu;
            this.parent = parent;
        }
    }
}
