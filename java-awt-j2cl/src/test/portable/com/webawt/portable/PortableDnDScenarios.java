package com.webawt.portable;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.io.IOException;

public final class PortableDnDScenarios {
    private PortableDnDScenarios() {
    }

    public static String actionConstants() {
        return DnDConstants.ACTION_NONE + ":" + DnDConstants.ACTION_COPY + ":" + DnDConstants.ACTION_MOVE + ":" + DnDConstants.ACTION_COPY_OR_MOVE + ":" + DnDConstants.ACTION_LINK + ":" + DnDConstants.ACTION_REFERENCE;
    }

    public static String stringSelection() throws UnsupportedFlavorException, IOException {
        StringSelection selection = new StringSelection("portable");
        return selection.getTransferDataFlavors().length + ":" + selection.isDataFlavorSupported(DataFlavor.stringFlavor) + ":" + selection.getTransferData(DataFlavor.stringFlavor);
    }

    public static String stringFlavor() throws CloneNotSupportedException {
        DataFlavor flavor = DataFlavor.stringFlavor;
        return flavor.getPrimaryType() + ":" + flavor.getSubType() + ":" + flavor.isFlavorTextType() + ":" + flavor.equals(flavor.clone());
    }
}
