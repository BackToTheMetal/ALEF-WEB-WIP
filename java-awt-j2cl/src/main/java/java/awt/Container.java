package java.awt;

import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.HierarchyEvent;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class Container extends Component {
    private static final FocusTraversalPolicy DEFAULT_FOCUS_TRAVERSAL_POLICY = new DefaultFocusTraversalPolicy();
    private final List<Component> components = new ArrayList<>();
    private final List<ContainerListener> containerListeners = new ArrayList<>();
    private Set<AWTKeyStroke> downCycleFocusTraversalKeys;
    private FocusTraversalPolicy focusTraversalPolicy;
    private LayoutManager layout;
    private boolean focusCycleRoot;
    private boolean focusTraversalPolicyProvider;

    public Container() {
        this("div");
    }

    Container(String elementName) {
        super(elementName);
    }

    @Override
    public void setFocusTraversalKeys(int identifier, Set<? extends AWTKeyStroke> keystrokes) {
        if (identifier != KeyboardFocusManager.DOWN_CYCLE_TRAVERSAL_KEYS) {
            super.setFocusTraversalKeys(identifier, keystrokes);
            return;
        }
        if (keystrokes == null) {
            downCycleFocusTraversalKeys = null;
            return;
        }
        LinkedHashSet<AWTKeyStroke> copy = new LinkedHashSet<>();
        for (AWTKeyStroke keystroke : keystrokes) {
            if (keystroke == null || keystroke.getKeyEventType() == java.awt.event.KeyEvent.KEY_TYPED) {
                throw new IllegalArgumentException("focus traversal keys must identify pressed or released keys");
            }
            copy.add(keystroke);
        }
        downCycleFocusTraversalKeys = Collections.unmodifiableSet(copy);
    }

    @Override
    public Set<AWTKeyStroke> getFocusTraversalKeys(int identifier) {
        if (identifier != KeyboardFocusManager.DOWN_CYCLE_TRAVERSAL_KEYS) {
            return super.getFocusTraversalKeys(identifier);
        }
        return downCycleFocusTraversalKeys == null
                ? KeyboardFocusManager.getCurrentKeyboardFocusManager().getDefaultFocusTraversalKeys(identifier)
                : downCycleFocusTraversalKeys;
    }

    @Override
    public boolean areFocusTraversalKeysSet(int identifier) {
        return identifier == KeyboardFocusManager.DOWN_CYCLE_TRAVERSAL_KEYS
                ? downCycleFocusTraversalKeys != null
                : super.areFocusTraversalKeysSet(identifier);
    }

    public int getComponentCount() {
        return components.size();
    }

    public int countComponents() {
        return getComponentCount();
    }

    public Component getComponent(int index) {
        return components.get(index);
    }

    public Component[] getComponents() {
        return components.toArray(new Component[0]);
    }

    public Insets getInsets() {
        return new Insets(0, 0, 0, 0);
    }

    public Insets insets() {
        return getInsets();
    }

    public Component add(Component component) {
        addImpl(component, null, -1);
        return component;
    }

    public Component add(String name, Component component) {
        addImpl(component, name, -1);
        return component;
    }

    public Component add(Component component, int index) {
        addImpl(component, null, index);
        return component;
    }

    public void add(Component component, Object constraints) {
        addImpl(component, constraints, -1);
    }

    public void add(Component component, Object constraints, int index) {
        addImpl(component, constraints, index);
    }

    protected void addImpl(Component component, Object constraints, int index) {
        if (component == null) {
            throw new NullPointerException("component");
        }
        if (component == this || isParentOf(component)) {
            throw new IllegalArgumentException("adding container's parent to itself");
        }
        Container oldParent = component.getParent();
        if (oldParent != null) {
            oldParent.remove(component);
        }
        int insertionIndex = index < 0 ? components.size() : index;
        if (insertionIndex > components.size()) {
            throw new IllegalArgumentException("illegal component position");
        }
        components.add(insertionIndex, component);
        component.setParent(this);
        component.dispatchHierarchyEventToSubtree(HierarchyEvent.HIERARCHY_CHANGED, component, this, HierarchyEvent.PARENT_CHANGED);
        if (layout instanceof LayoutManager2) {
            ((LayoutManager2) layout).addLayoutComponent(component, constraints);
        } else if (layout != null && constraints instanceof String) {
            layout.addLayoutComponent((String) constraints, component);
        }
        if (isDisplayable()) {
            component.addNotify();
        }
        invalidate();
        dispatchEvent(new ContainerEvent(this, ContainerEvent.COMPONENT_ADDED, component));
    }

    public void setComponentZOrder(Component component, int index) {
        if (component.getParent() != this) {
            add(component, index);
            return;
        }
        components.remove(component);
        components.add(index, component);
        invalidate();
    }

    public int getComponentZOrder(Component component) {
        return components.indexOf(component);
    }

    public void remove(int index) {
        Component component = components.remove(index);
        if (layout != null) {
            layout.removeLayoutComponent(component);
        }
        component.removeNotify();
        component.setParent(null);
        component.dispatchHierarchyEventToSubtree(HierarchyEvent.HIERARCHY_CHANGED, component, this, HierarchyEvent.PARENT_CHANGED);
        invalidate();
        dispatchEvent(new ContainerEvent(this, ContainerEvent.COMPONENT_REMOVED, component));
    }

    public void remove(Component component) {
        int index = components.indexOf(component);
        if (index >= 0) {
            remove(index);
        }
    }

    public void removeAll() {
        while (!components.isEmpty()) {
            remove(components.size() - 1);
        }
    }

    public synchronized void addContainerListener(ContainerListener listener) {
        if (listener != null) {
            containerListeners.add(listener);
        }
    }

    public synchronized void removeContainerListener(ContainerListener listener) {
        containerListeners.remove(listener);
    }

    public synchronized ContainerListener[] getContainerListeners() {
        return containerListeners.toArray(new ContainerListener[0]);
    }

    @Override
    protected boolean eventEnabled(AWTEvent event) {
        if (event.getID() == ContainerEvent.COMPONENT_ADDED || event.getID() == ContainerEvent.COMPONENT_REMOVED) {
            return (eventMask & AWTEvent.CONTAINER_EVENT_MASK) != 0 || !containerListeners.isEmpty();
        }
        return super.eventEnabled(event);
    }

    @Override
    protected void processEvent(AWTEvent event) {
        if (event instanceof ContainerEvent) {
            processContainerEvent((ContainerEvent) event);
        } else {
            super.processEvent(event);
        }
    }

    protected void processContainerEvent(ContainerEvent event) {
        for (ContainerListener listener : getContainerListeners()) {
            if (event.getID() == ContainerEvent.COMPONENT_ADDED) {
                listener.componentAdded(event);
            } else if (event.getID() == ContainerEvent.COMPONENT_REMOVED) {
                listener.componentRemoved(event);
            }
        }
    }

    public LayoutManager getLayout() {
        return layout;
    }

    public void setLayout(LayoutManager layout) {
        this.layout = layout;
        invalidate();
    }

    @Override
    public void doLayout() {
        if (layout != null) {
            layout.layoutContainer(this);
        }
    }

    @Override
    public void layout() {
        doLayout();
    }

    public boolean isValidateRoot() {
        return false;
    }

    public void setFocusTraversalPolicy(FocusTraversalPolicy focusTraversalPolicy) {
        this.focusTraversalPolicy = focusTraversalPolicy;
    }

    public FocusTraversalPolicy getFocusTraversalPolicy() {
        if (focusTraversalPolicy != null) {
            return focusTraversalPolicy;
        }
        Container ancestor = getFocusCycleRootAncestor();
        return ancestor == null ? DEFAULT_FOCUS_TRAVERSAL_POLICY : ancestor.getFocusTraversalPolicy();
    }

    public boolean isFocusTraversalPolicySet() {
        return focusTraversalPolicy != null;
    }

    public void setFocusCycleRoot(boolean focusCycleRoot) {
        this.focusCycleRoot = focusCycleRoot;
    }

    public boolean isFocusCycleRoot() {
        return focusCycleRoot;
    }

    public final void setFocusTraversalPolicyProvider(boolean focusTraversalPolicyProvider) {
        this.focusTraversalPolicyProvider = focusTraversalPolicyProvider;
    }

    public final boolean isFocusTraversalPolicyProvider() {
        return focusTraversalPolicyProvider;
    }

    public void transferFocusDownCycle() {
        if (!focusCycleRoot) {
            return;
        }
        FocusTraversalPolicy policy = getFocusTraversalPolicy();
        Component target = policy == null ? null : policy.getDefaultComponent(this);
        if (target != null) {
            target.requestFocus();
        }
    }

    @Override
    public void validate() {
        validateTree();
        for (Component component : components) {
            component.validate();
        }
        super.validate();
    }

    @Override
    public Dimension getPreferredSize() {
        if (isPreferredSizeSet() || layout == null) {
            return super.getPreferredSize();
        }
        return layout.preferredLayoutSize(this);
    }

    @Override
    public Dimension getMinimumSize() {
        if (isMinimumSizeSet() || layout == null) {
            return super.getMinimumSize();
        }
        return layout.minimumLayoutSize(this);
    }

    @Override
    public Dimension getMaximumSize() {
        if (isMaximumSizeSet() || !(layout instanceof LayoutManager2)) {
            return super.getMaximumSize();
        }
        return ((LayoutManager2) layout).maximumLayoutSize(this);
    }

    @Override
    public float getAlignmentX() {
        return layout instanceof LayoutManager2 ? ((LayoutManager2) layout).getLayoutAlignmentX(this) : super.getAlignmentX();
    }

    @Override
    public float getAlignmentY() {
        return layout instanceof LayoutManager2 ? ((LayoutManager2) layout).getLayoutAlignmentY(this) : super.getAlignmentY();
    }

    @Override
    public Component getComponentAt(int x, int y) {
        if (!contains(x, y)) {
            return null;
        }
        for (int index = components.size() - 1; index >= 0; index--) {
            Component component = components.get(index);
            if (component.isVisible() && component.contains(x - component.getX(), y - component.getY())) {
                return component;
            }
        }
        return this;
    }

    public Component findComponentAt(int x, int y) {
        Component component = getComponentAt(x, y);
        if (!(component instanceof Container) || component == this) {
            return component;
        }
        Container child = (Container) component;
        return child.findComponentAt(x - child.getX(), y - child.getY());
    }

    public Component findComponentAt(Point point) {
        return findComponentAt(point.x, point.y);
    }

    public Point getMousePosition(boolean allowChildren) throws HeadlessException {
        return getRecordedMousePosition(allowChildren);
    }

    @Override
    public void print(Graphics graphics) {
        if (isShowing()) {
            super.print(graphics);
            printComponents(graphics);
        }
    }

    public void paintComponents(Graphics graphics) {
        paintChildren(graphics, false);
    }

    public void printComponents(Graphics graphics) {
        paintChildren(graphics, true);
    }

    @Override
    public void deliverEvent(Event event) {
        Component target = getComponentAt(event.x, event.y);
        if (target == null || target == this) {
            postEvent(event);
            return;
        }
        event.translate(-target.getX(), -target.getY());
        target.deliverEvent(event);
    }

    @Override
    public void list(PrintStream output, int indent) {
        super.list(output, indent);
        for (Component component : components) {
            component.list(output, indent + 1);
        }
    }

    @Override
    public void list(PrintWriter output, int indent) {
        super.list(output, indent);
        for (Component component : components) {
            component.list(output, indent + 1);
        }
    }

    @Override
    public void addNotify() {
        super.addNotify();
        for (Component component : components) {
            component.addNotify();
        }
    }

    @Override
    public void removeNotify() {
        for (Component component : components) {
            component.removeNotify();
        }
        super.removeNotify();
    }

    public boolean isAncestorOf(Component component) {
        Component current = component == null ? null : component.getParent();
        while (current != null) {
            if (current == this) {
                return true;
            }
            current = current.getParent();
        }
        return false;
    }

    @Override
    public void applyComponentOrientation(ComponentOrientation orientation) {
        super.applyComponentOrientation(orientation);
        for (Component component : components) {
            component.applyComponentOrientation(orientation);
        }
    }

    private boolean isParentOf(Component component) {
        for (Container ancestor = this; ancestor != null; ancestor = ancestor.getParent()) {
            if (ancestor == component) {
                return true;
            }
        }
        return false;
    }

    private void paintChildren(Graphics graphics, boolean printing) {
        if (!isShowing()) {
            return;
        }
        for (int index = components.size() - 1; index >= 0; index--) {
            Component component = components.get(index);
            if (!component.isVisible()) {
                continue;
            }
            Graphics childGraphics = graphics.create(component.getX(), component.getY(), component.getWidth(), component.getHeight());
            try {
                if (printing) {
                    component.printAll(childGraphics);
                } else {
                    component.paintAll(childGraphics);
                }
            } finally {
                childGraphics.dispose();
            }
        }
    }

    protected class AccessibleAWTContainer extends AccessibleAWTComponent {
        protected java.awt.event.ContainerListener accessibleContainerHandler;
        private final java.util.List<java.beans.PropertyChangeListener> listeners = new java.util.ArrayList<>();
        protected AccessibleAWTContainer() {}
        public int getAccessibleChildrenCount() {
            int count = 0;
            for (Component child : getComponents()) { if (child instanceof javax.accessibility.Accessible) { count++; } }
            return count;
        }
        public javax.accessibility.Accessible getAccessibleChild(int index) {
            if (index < 0) { return null; }
            for (Component child : getComponents()) {
                if (child instanceof javax.accessibility.Accessible && index-- == 0) { return (javax.accessibility.Accessible) child; }
            }
            return null;
        }
        public javax.accessibility.Accessible getAccessibleAt(Point point) {
            Component child = Container.this.getComponentAt(point);
            return child instanceof javax.accessibility.Accessible ? (javax.accessibility.Accessible) child : null;
        }
        public void addPropertyChangeListener(java.beans.PropertyChangeListener listener) {
            if (listener == null) { return; }
            if (listeners.isEmpty()) {
                accessibleContainerHandler = new AccessibleContainerHandler();
                Container.this.addContainerListener(accessibleContainerHandler);
            }
            listeners.add(listener);
            super.addPropertyChangeListener(listener);
        }
        public void removePropertyChangeListener(java.beans.PropertyChangeListener listener) {
            listeners.remove(listener);
            super.removePropertyChangeListener(listener);
            if (listeners.isEmpty()) {
                Container.this.removeContainerListener(accessibleContainerHandler);
                accessibleContainerHandler = null;
            }
        }
        protected class AccessibleContainerHandler implements java.awt.event.ContainerListener, java.io.Serializable {
            protected AccessibleContainerHandler() {}
            public void componentAdded(java.awt.event.ContainerEvent event) { firePropertyChange(ACCESSIBLE_CHILD_PROPERTY, null, event.getChild()); }
            public void componentRemoved(java.awt.event.ContainerEvent event) { firePropertyChange(ACCESSIBLE_CHILD_PROPERTY, event.getChild(), null); }
        }
    }

    protected void validateTree() { doLayout(); }
}
