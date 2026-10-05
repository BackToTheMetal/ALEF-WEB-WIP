package java.awt.datatransfer;

import java.io.IOException;

public class StringSelection implements Transferable, ClipboardOwner {
    private final String data;

    public StringSelection(String data) {
        this.data = data;
    }

    @Override
    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[] {DataFlavor.stringFlavor, DataFlavor.plainTextFlavor};
    }

    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return DataFlavor.stringFlavor.equals(flavor) || DataFlavor.plainTextFlavor.equals(flavor);
    }

    @Override
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        if (!isDataFlavorSupported(flavor)) {
            throw new UnsupportedFlavorException(flavor);
        }
        return data;
    }

    @Override
    public void lostOwnership(Clipboard clipboard, Transferable contents) {
    }
}
