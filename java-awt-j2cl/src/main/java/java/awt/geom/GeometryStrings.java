package java.awt.geom;

final class GeometryStrings {
    private GeometryStrings() {
    }

    static String format(double value) {
        if (value == 0.0) {
            return 1.0 / value < 0.0 ? "-0.0" : "0.0";
        }
        String text = java.lang.Double.toString(value);
        if (!java.lang.Double.isInfinite(value) && !java.lang.Double.isNaN(value) && value == Math.rint(value) && text.indexOf('.') < 0 && text.indexOf('E') < 0) {
            return text + ".0";
        }
        return text;
    }

    static String format(float value) {
        if (value == 0.0f) {
            return 1.0f / value < 0.0f ? "-0.0" : "0.0";
        }
        String text = java.lang.Float.toString(value);
        if (!java.lang.Float.isInfinite(value) && !java.lang.Float.isNaN(value) && value == Math.rint(value) && text.indexOf('.') < 0 && text.indexOf('E') < 0) {
            return text + ".0";
        }
        return text;
    }
}
