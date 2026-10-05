package java.awt.event;

import java.awt.Component;
import java.awt.Container;

public class ContainerEvent extends ComponentEvent {
    public static final int CONTAINER_FIRST = 300;
    public static final int CONTAINER_LAST = 301;
    public static final int COMPONENT_ADDED = 300;
    public static final int COMPONENT_REMOVED = 301;
    private static final long serialVersionUID = 1L;
    private final Component child;

    public ContainerEvent(Component source, int id, Component child) {
        super(source, id);
        this.child = child;
    }

    public Container getContainer() {
        return source instanceof Container ? (Container) source : null;
    }

    public Component getChild() {
        return child;
    }

    @Override
    public String paramString() {
        String type = "unknown type";
        if (id == COMPONENT_ADDED) {
            type = "COMPONENT_ADDED";
        } else if (id == COMPONENT_REMOVED) {
            type = "COMPONENT_REMOVED";
        }
        return type + ",child=" + child.getName();
    }
}
