package java.awt.dnd;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

final class SnapshotTransferable implements Transferable {
    private final DataFlavor[] flavors;
    private final String[] data;

    SnapshotTransferable(String[] mimeTypes, String[] data) {
        int size = Math.min(mimeTypes.length, data.length);
        flavors = new DataFlavor[size];
        this.data = new String[size];
        for (int index = 0; index < size; index++) {
            flavors[index] = toFlavor(mimeTypes[index]);
            this.data[index] = data[index];
        }
    }

    @Override
    public DataFlavor[] getTransferDataFlavors() {
        DataFlavor[] result = new DataFlavor[flavors.length];
        System.arraycopy(flavors, 0, result, 0, flavors.length);
        return result;
    }

    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return indexOf(flavor) >= 0;
    }

    @Override
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        int index = indexOf(flavor);
        if (index < 0) {
            throw new UnsupportedFlavorException(flavor);
        }
        return data[index];
    }

    private int indexOf(DataFlavor flavor) {
        for (int index = 0; index < flavors.length; index++) {
            if (flavors[index].equals(flavor) || flavors[index].isMimeTypeEqual(flavor)) {
                return index;
            }
        }
        return -1;
    }

    private static DataFlavor toFlavor(String mimeType) {
        if ("text/plain".equalsIgnoreCase(mimeType)) {
            return DataFlavor.stringFlavor;
        }
        if ("text/html".equalsIgnoreCase(mimeType)) {
            return DataFlavor.fragmentHtmlFlavor;
        }
        return new DataFlavor(mimeType, mimeType);
    }
}
