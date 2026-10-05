package java.awt;

public class DefaultFocusTraversalPolicy extends ContainerOrderFocusTraversalPolicy {
    public DefaultFocusTraversalPolicy() {
    }

    @Override
    protected boolean accept(Component component) {
        return !(component instanceof Label) && !(component instanceof Panel) && !(component instanceof Window) && super.accept(component);
    }
}
