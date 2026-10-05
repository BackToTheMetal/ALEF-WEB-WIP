package java.awt.dnd;

import java.awt.Component;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.io.Serializable;
import org.webawt.internal.peer.PeerDragEvent;
import org.webawt.internal.peer.WebAwtRuntime;

public class DropTargetContext implements Serializable {
    private final DropTarget dropTarget;
    private Transferable transferable;
    private int sourceActions;
    private int dropAction;
    private boolean localTransfer;
    private boolean operationActive;
    private boolean completed;

    DropTargetContext(DropTarget dropTarget) {
        this.dropTarget = dropTarget;
    }

    public DropTarget getDropTarget() {
        return dropTarget;
    }

    public Component getComponent() {
        return dropTarget.getComponent();
    }

    public void dropComplete(boolean success) throws InvalidDnDOperationException {
        if (!operationActive || completed) {
            throw new InvalidDnDOperationException("No incomplete drop is active");
        }
        completed = true;
        WebAwtRuntime.notifyDragSource(PeerDragEvent.DRAG_END, dropAction, sourceActions, 0, 0, 0, success);
    }

    void begin(Transferable transferable, int sourceActions, int dropAction, boolean localTransfer) {
        this.transferable = transferable;
        this.sourceActions = sourceActions;
        this.dropAction = dropAction;
        this.localTransfer = localTransfer;
        operationActive = true;
        completed = false;
    }

    protected DataFlavor[] getCurrentDataFlavors() {
        return transferable == null ? new DataFlavor[0] : transferable.getTransferDataFlavors();
    }

    protected Transferable getTransferable() throws InvalidDnDOperationException {
        if (transferable == null) {
            throw new InvalidDnDOperationException("Transfer data is not available");
        }
        return transferable;
    }

    int getSourceActions() {
        return sourceActions;
    }

    int getDropAction() {
        return dropAction;
    }

    boolean isLocalTransfer() {
        return localTransfer;
    }

    void accept(int action) {
        dropAction = validateAction(action);
    }

    void reject() {
        dropAction = DnDConstants.ACTION_NONE;
    }

    private int validateAction(int action) {
        int supported = action & sourceActions & dropTarget.getDefaultActions();
        if (supported == DnDConstants.ACTION_NONE) {
            throw new IllegalArgumentException("Unsupported drop action");
        }
        if ((supported & DnDConstants.ACTION_COPY) != 0) {
            return DnDConstants.ACTION_COPY;
        }
        if ((supported & DnDConstants.ACTION_MOVE) != 0) {
            return DnDConstants.ACTION_MOVE;
        }
        return DnDConstants.ACTION_LINK;
    }
    protected void setTargetActions(int actions) { dropTarget.setDefaultActions(actions); }
    protected int getTargetActions() { return dropTarget.getDefaultActions(); }
    protected void acceptDrag(int action) { accept(action); }
    protected void rejectDrag() { reject(); }
    protected void acceptDrop(int action) { accept(action); }
    protected void rejectDrop() { reject(); }
    protected java.util.List<DataFlavor> getCurrentDataFlavorsAsList() { return java.util.Arrays.asList(getCurrentDataFlavors()); }
    protected boolean isDataFlavorSupported(DataFlavor flavor) { return transferable != null && transferable.isDataFlavorSupported(flavor); }
    protected Transferable createTransferableProxy(Transferable transferable, boolean isLocal) { return new TransferableProxy(transferable, isLocal); }
    protected class TransferableProxy implements Transferable {
        protected Transferable transferable;
        protected boolean isLocal;
        TransferableProxy(Transferable transferable, boolean isLocal) { this.transferable = transferable; this.isLocal = isLocal; }
        public DataFlavor[] getTransferDataFlavors() { return transferable.getTransferDataFlavors(); }
        public boolean isDataFlavorSupported(DataFlavor flavor) { return transferable.isDataFlavorSupported(flavor); }
        public Object getTransferData(DataFlavor flavor) throws java.awt.datatransfer.UnsupportedFlavorException, java.io.IOException { return transferable.getTransferData(flavor); }
    }
}
