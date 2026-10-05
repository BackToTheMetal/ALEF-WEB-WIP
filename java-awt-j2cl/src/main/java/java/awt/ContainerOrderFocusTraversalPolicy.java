package java.awt;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ContainerOrderFocusTraversalPolicy extends FocusTraversalPolicy implements Serializable {
    private static final long serialVersionUID = 1L;
    private boolean implicitDownCycleTraversal = true;

    public ContainerOrderFocusTraversalPolicy() {
    }

    @Override
    public Component getComponentAfter(Container focusCycleRoot, Component component) {
        List<Component> cycle = traversalCycle(focusCycleRoot, component);
        int currentIndex = cycle.indexOf(component);
        for (int offset = 1; offset <= cycle.size(); offset++) {
            Component candidate = cycle.get((currentIndex + offset) % cycle.size());
            Component accepted = downCycle(focusCycleRoot, candidate);
            if (accepted != null) {
                return accepted;
            }
        }
        return null;
    }

    @Override
    public Component getComponentBefore(Container focusCycleRoot, Component component) {
        List<Component> cycle = traversalCycle(focusCycleRoot, component);
        int currentIndex = cycle.indexOf(component);
        for (int offset = 1; offset <= cycle.size(); offset++) {
            int candidateIndex = (currentIndex - offset + cycle.size()) % cycle.size();
            Component candidate = cycle.get(candidateIndex);
            if (accept(candidate)) {
                return candidate;
            }
        }
        return null;
    }

    @Override
    public Component getFirstComponent(Container focusCycleRoot) {
        requireRoot(focusCycleRoot);
        for (Component candidate : buildCycle(focusCycleRoot)) {
            Component accepted = downCycle(focusCycleRoot, candidate);
            if (accepted != null) {
                return accepted;
            }
        }
        return null;
    }

    @Override
    public Component getLastComponent(Container focusCycleRoot) {
        requireRoot(focusCycleRoot);
        List<Component> cycle = buildCycle(focusCycleRoot);
        for (int index = cycle.size() - 1; index >= 0; index--) {
            Component candidate = cycle.get(index);
            if (accept(candidate)) {
                return candidate;
            }
        }
        return null;
    }

    @Override
    public Component getDefaultComponent(Container focusCycleRoot) {
        return getFirstComponent(focusCycleRoot);
    }

    public void setImplicitDownCycleTraversal(boolean implicitDownCycleTraversal) {
        this.implicitDownCycleTraversal = implicitDownCycleTraversal;
    }

    public boolean getImplicitDownCycleTraversal() {
        return implicitDownCycleTraversal;
    }

    protected boolean accept(Component component) {
        if (!component.isVisible() || !component.isDisplayable() || !component.isEnabled() || !component.isFocusable()) {
            return false;
        }
        for (Container parent = component.getParent(); parent != null; parent = parent.getParent()) {
            if (!parent.isEnabled() || !parent.isVisible()) {
                return false;
            }
            if (parent instanceof Window) {
                break;
            }
        }
        return true;
    }

    private List<Component> traversalCycle(Container focusCycleRoot, Component component) {
        requireRoot(focusCycleRoot);
        if (component == null) {
            throw new IllegalArgumentException("component cannot be null");
        }
        List<Component> cycle = buildCycle(focusCycleRoot);
        if (!cycle.contains(component)) {
            throw new IllegalArgumentException("component is not in focus cycle root");
        }
        return cycle;
    }

    private static void requireRoot(Container focusCycleRoot) {
        if (focusCycleRoot == null) {
            throw new IllegalArgumentException("focusCycleRoot cannot be null");
        }
    }

    private static List<Component> buildCycle(Container root) {
        List<Component> cycle = new ArrayList<>();
        appendCycle(root, root, cycle);
        return cycle;
    }

    private static void appendCycle(Container root, Component component, List<Component> cycle) {
        cycle.add(component);
        if (!(component instanceof Container)) {
            return;
        }
        Container container = (Container) component;
        if (container != root && container.isFocusCycleRoot()) {
            return;
        }
        for (Component child : container.getComponents()) {
            appendCycle(root, child, cycle);
        }
    }

    private Component downCycle(Container focusCycleRoot, Component component) {
        if (component != focusCycleRoot && component instanceof Container && implicitDownCycleTraversal) {
            Container container = (Container) component;
            if (container.isFocusCycleRoot()) {
                FocusTraversalPolicy policy = container.getFocusTraversalPolicy();
                Component defaultComponent = policy == null ? null : policy.getDefaultComponent(container);
                if (defaultComponent != null) {
                    return defaultComponent;
                }
            }
        }
        return accept(component) ? component : null;
    }
}
