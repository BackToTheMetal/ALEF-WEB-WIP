package org.webawt.internal.peer;

public final class FontCss {
    private FontCss() {
    }

    public static String font(String family, boolean bold, boolean italic, double size) {
        String cssFamily;
        if (family.equalsIgnoreCase("Dialog") || family.equalsIgnoreCase("Default") || family.equalsIgnoreCase("SansSerif")) {
            cssFamily = "sans-serif";
        } else if (family.equalsIgnoreCase("Monospaced") || family.equalsIgnoreCase("DialogInput")) {
            cssFamily = "monospace";
        } else if (family.equalsIgnoreCase("Serif")) {
            cssFamily = "serif";
        } else {
            cssFamily = "\"" + family.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", " ").replace("\r", " ") + "\"";
        }
        return (italic ? "italic " : "") + (bold ? "bold " : "") + size + "px " + cssFamily;
    }
}
