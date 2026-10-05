package java.awt.dnd;

import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.util.Arrays;
import java.util.List;

public class DropTargetDragEvent extends DropTargetEvent {
    private final Point location;

    public DropTargetDragEvent(DropTargetContext context, Point location, int dropAction, int sourceActions) {
        super(context);
        if (location == null) {
            throw new NullPointerException("location");
        }
        this.location = new Point(location);
        DragSourceContext sourceContext = DragManager.getActiveContext();
        if (sourceContext != null) {
            context.begin(sourceContext.getTransferable(), sourceActions, dropAction, true);
        }
    }

    public Point getLocation() {
        return new Point(location);
    }

    public DataFlavor[] getCurrentDataFlavors() {
        return getDropTargetContext().getCurrentDataFlavors();
    }

    public List<DataFlavor> getCurrentDataFlavorsAsList() {
        return Arrays.asList(getCurrentDataFlavors());
    }

    public boolean isDataFlavorSupported(DataFlavor flavor) {
        for (DataFlavor current : getCurrentDataFlavors()) {
            if (current.equals(flavor)) {
                return true;
            }
        }
        return false;
    }

    public int getSourceActions() {
        return getDropTargetContext().getSourceActions();
    }

    public int getDropAction() {
        return getDropTargetContext().getDropAction();
    }

    public Transferable getTransferable() {
        return getDropTargetContext().getTransferable();
    }

    public void acceptDrag(int dragOperation) {
        getDropTargetContext().accept(dragOperation);
    }

    public void rejectDrag() {
        getDropTargetContext().reject();
    }
}
