package java.awt.dnd;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.FlavorMap;
import java.util.LinkedHashMap;
import java.util.Map;

final class DefaultFlavorMap implements FlavorMap {
    @Override
    public Map<DataFlavor, String> getNativesForFlavors(DataFlavor[] flavors) {
        Map<DataFlavor, String> result = new LinkedHashMap<>();
        DataFlavor[] requested = flavors == null ? defaultFlavors() : flavors;
        for (DataFlavor flavor : requested) {
            if (flavor != null) {
                result.put(flavor, toNative(flavor));
            }
        }
        return result;
    }

    @Override
    public Map<String, DataFlavor> getFlavorsForNatives(String[] natives) {
        Map<String, DataFlavor> result = new LinkedHashMap<>();
        String[] requested = natives == null ? defaultNatives() : natives;
        for (String nativeType : requested) {
            if (nativeType != null) {
                result.put(nativeType, toFlavor(nativeType));
            }
        }
        return result;
    }

    private static String toNative(DataFlavor flavor) {
        if (DataFlavor.stringFlavor.equals(flavor) || DataFlavor.plainTextFlavor.equals(flavor)) {
            return "text/plain";
        }
        if (flavor.isMimeTypeEqual("text/html")) {
            return "text/html";
        }
        return flavor.getMimeType();
    }

    private static DataFlavor toFlavor(String nativeType) {
        if ("text/plain".equalsIgnoreCase(nativeType)) {
            return DataFlavor.stringFlavor;
        }
        if ("text/html".equalsIgnoreCase(nativeType)) {
            return DataFlavor.fragmentHtmlFlavor;
        }
        return new DataFlavor(nativeType, nativeType);
    }

    private static DataFlavor[] defaultFlavors() {
        return new DataFlavor[] {DataFlavor.stringFlavor, DataFlavor.fragmentHtmlFlavor};
    }

    private static String[] defaultNatives() {
        return new String[] {"text/plain", "text/html"};
    }
}
