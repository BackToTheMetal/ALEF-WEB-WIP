package java.awt.event;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Rectangle;

public class ComponentEvent extends AWTEvent {
    public static final int COMPONENT_FIRST = 100;
    public static final int COMPONENT_LAST = 103;
    public static final int COMPONENT_MOVED = 100;
    public static final int COMPONENT_RESIZED = 101;
    public static final int COMPONENT_SHOWN = 102;
    public static final int COMPONENT_HIDDEN = 103;
    private static final long serialVersionUID = 1L;

    public ComponentEvent(Component source, int id) {
        super(source, id);
    }

    public Component getComponent() {
        return source instanceof Component ? (Component) source : null;
    }

    @Override
    public String paramString() {
        if (id == COMPONENT_SHOWN) {
            return "COMPONENT_SHOWN";
        }
        if (id == COMPONENT_HIDDEN) {
            return "COMPONENT_HIDDEN";
        }
        if (id == COMPONENT_MOVED || id == COMPONENT_RESIZED) {
            Rectangle bounds = getComponent().getBounds();
            String type = id == COMPONENT_MOVED ? "COMPONENT_MOVED" : "COMPONENT_RESIZED";
            return type + " (" + bounds.x + "," + bounds.y + " " + bounds.width + "x" + bounds.height + ")";
        }
        return "unknown type";
    }
}
