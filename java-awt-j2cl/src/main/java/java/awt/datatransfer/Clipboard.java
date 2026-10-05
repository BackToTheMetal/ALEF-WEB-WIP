package java.awt.datatransfer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Clipboard {
    private final List<FlavorListener> flavorListeners = new ArrayList<>();
    private final String name;
    protected Transferable contents;
    protected ClipboardOwner owner;

    public Clipboard(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public synchronized void setContents(Transferable contents, ClipboardOwner owner) {
        ClipboardOwner previousOwner = this.owner;
        Transferable previousContents = this.contents;
        this.contents = contents;
        this.owner = owner;
        if (previousOwner != null && previousOwner != owner) {
            previousOwner.lostOwnership(this, previousContents);
        }
        FlavorEvent event = new FlavorEvent(this);
        for (FlavorListener listener : getFlavorListeners()) {
            listener.flavorsChanged(event);
        }
    }

    public synchronized Transferable getContents(Object requestor) {
        return contents;
    }

    public DataFlavor[] getAvailableDataFlavors() {
        Transferable current = getContents(null);
        return current == null ? new DataFlavor[0] : current.getTransferDataFlavors();
    }

    public boolean isDataFlavorAvailable(DataFlavor flavor) {
        Transferable current = getContents(null);
        return current != null && current.isDataFlavorSupported(flavor);
    }

    public Object getData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        Transferable current = getContents(null);
        if (current == null) {
            throw new UnsupportedFlavorException(flavor);
        }
        return current.getTransferData(flavor);
    }

    public synchronized void addFlavorListener(FlavorListener listener) {
        if (listener != null) {
            flavorListeners.add(listener);
        }
    }

    public synchronized void removeFlavorListener(FlavorListener listener) {
        flavorListeners.remove(listener);
    }

    public synchronized FlavorListener[] getFlavorListeners() {
        return flavorListeners.toArray(new FlavorListener[0]);
    }
}
