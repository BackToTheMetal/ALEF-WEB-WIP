package java.awt.dnd;

import java.util.EventObject;

public class DropTargetEvent extends EventObject {
    protected DropTargetContext context;

    public DropTargetEvent(DropTargetContext context) {
        super(context);
        this.context = context;
    }

    public DropTargetContext getDropTargetContext() {
        return context;
    }
}
