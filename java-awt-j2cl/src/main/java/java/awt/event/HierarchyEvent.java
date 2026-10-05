package java.awt.event;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Container;

public class HierarchyEvent extends AWTEvent {
    public static final int HIERARCHY_FIRST = 1400;
    public static final int HIERARCHY_CHANGED = HIERARCHY_FIRST;
    public static final int ANCESTOR_MOVED = HIERARCHY_FIRST + 1;
    public static final int ANCESTOR_RESIZED = HIERARCHY_FIRST + 2;
    public static final int HIERARCHY_LAST = ANCESTOR_RESIZED;
    public static final int PARENT_CHANGED = 1;
    public static final int DISPLAYABILITY_CHANGED = 2;
    public static final int SHOWING_CHANGED = 4;
    private static final long serialVersionUID = 1L;
    private final Component changed;
    private final Container changedParent;
    private final long changeFlags;

    public HierarchyEvent(Component source, int id, Component changed, Container changedParent) {
        this(source, id, changed, changedParent, 0);
    }

    public HierarchyEvent(Component source, int id, Component changed, Container changedParent, long changeFlags) {
        super(source, id);
        this.changed = changed;
        this.changedParent = changedParent;
        this.changeFlags = changeFlags;
    }

    public Component getComponent() {
        return source instanceof Component ? (Component) source : null;
    }

    public Component getChanged() {
        return changed;
    }

    public Container getChangedParent() {
        return changedParent;
    }

    public long getChangeFlags() {
        return changeFlags;
    }

    @Override
    public String paramString() {
        if (id == ANCESTOR_MOVED) {
            return "ANCESTOR_MOVED (" + changed + "," + changedParent + ")";
        }
        if (id == ANCESTOR_RESIZED) {
            return "ANCESTOR_RESIZED (" + changed + "," + changedParent + ")";
        }
        if (id == HIERARCHY_CHANGED) {
            String flags = "";
            if ((changeFlags & PARENT_CHANGED) != 0) {
                flags += "PARENT_CHANGED,";
            }
            if ((changeFlags & DISPLAYABILITY_CHANGED) != 0) {
                flags += "DISPLAYABILITY_CHANGED,";
            }
            if ((changeFlags & SHOWING_CHANGED) != 0) {
                flags += "SHOWING_CHANGED,";
            }
            return "HIERARCHY_CHANGED (" + flags + changed + "," + changedParent + ")";
        }
        return "unknown type";
    }
}
