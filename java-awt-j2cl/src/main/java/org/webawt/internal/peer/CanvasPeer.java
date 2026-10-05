package org.webawt.internal.peer;

public interface CanvasPeer extends DomPeer {
    default int[] getSurfaceSize() {
        return null;
    }
    void setFillStyle(String cssColor);
    void setStrokeStyle(String cssColor);
    void setLinearGradient(boolean filling, double x1, double y1, double x2, double y2, String[] cssColors, double[] fractions);
    void setRadialGradient(boolean filling, double centerX, double centerY, double radius, double focusX, double focusY, String[] cssColors, double[] fractions);
    void setLineWidth(double width);
    void setLineCap(String cap);
    void setLineJoin(String join);
    void setMiterLimit(double limit);
    void setLineDash(double[] dash, double phase);
    void setCompositeOperation(String operation);
    void setGlobalAlpha(double alpha);
    void clearRect(double x, double y, double width, double height);
    void copyArea(double x, double y, double width, double height, double deltaX, double deltaY);
    void fillRect(double x, double y, double width, double height);
    void strokeRect(double x, double y, double width, double height);
    void beginPath();
    void moveTo(double x, double y);
    void lineTo(double x, double y);
    void quadraticCurveTo(double controlX, double controlY, double x, double y);
    void bezierCurveTo(double controlX1, double controlY1, double controlX2, double controlY2, double x, double y);
    void closePath();
    void fill();
    void fill(boolean evenOdd);
    void stroke();
    void clip();
    void clip(boolean evenOdd);
    void save();
    void restore();
    void translate(double x, double y);
    void scale(double x, double y);
    void rotate(double angle);
    void transform(double scaleX, double shearY, double shearX, double scaleY, double translateX, double translateY);
    void drawImage(int[] pixels, int imageWidth, int imageHeight, int sourceX1, int sourceY1, int sourceX2, int sourceY2, double destinationX1, double destinationY1, double destinationX2, double destinationY2);
    void fillText(String text, double x, double y);
}
