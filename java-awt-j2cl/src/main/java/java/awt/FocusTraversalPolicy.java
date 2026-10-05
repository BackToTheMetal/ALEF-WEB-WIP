package java.awt;

public abstract class FocusTraversalPolicy {
    protected FocusTraversalPolicy() {
    }

    public abstract Component getComponentAfter(Container focusCycleRoot, Component component);

    public abstract Component getComponentBefore(Container focusCycleRoot, Component component);

    public abstract Component getFirstComponent(Container focusCycleRoot);

    public abstract Component getLastComponent(Container focusCycleRoot);

    public abstract Component getDefaultComponent(Container focusCycleRoot);

    public Component getInitialComponent(Window window) {
        Component defaultComponent = getDefaultComponent(window);
        if (defaultComponent != null) {
            return defaultComponent;
        }
        return window != null && window.isFocusableWindow() ? window : null;
    }
}
