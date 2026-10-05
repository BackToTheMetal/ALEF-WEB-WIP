package org.webawt.internal.peer;

import java.util.ArrayList;
import java.util.List;

public final class ModelCanvasPeer extends ModelDomPeer implements CanvasPeer {
    @Override
    public void setLineDash(double[] dash, double phase) {
        record("lineDash", java.util.Arrays.toString(dash), phase);
    }

    private final List<String> commands = new ArrayList<>();

    public ModelCanvasPeer() {
        super("canvas");
    }

    public String[] getCommands() {
        return commands.toArray(new String[0]);
    }

    @Override
    public void setFillStyle(String cssColor) {
        record("fillStyle", cssColor);
    }

    @Override
    public void setStrokeStyle(String cssColor) {
        record("strokeStyle", cssColor);
    }

    @Override
    public void setLinearGradient(boolean filling, double x1, double y1, double x2, double y2, String[] cssColors, double[] fractions) {
        record(filling ? "fillLinearGradient" : "strokeLinearGradient", x1, y1, x2, y2, cssColors.length);
    }

    @Override
    public void setRadialGradient(boolean filling, double centerX, double centerY, double radius, double focusX, double focusY, String[] cssColors, double[] fractions) {
        record(filling ? "fillRadialGradient" : "strokeRadialGradient", centerX, centerY, radius, focusX, focusY, cssColors.length);
    }

    @Override
    public void setLineWidth(double width) {
        record("lineWidth", width);
    }

    @Override
    public void setLineCap(String cap) {
        record("lineCap", cap);
    }

    @Override
    public void setLineJoin(String join) {
        record("lineJoin", join);
    }

    @Override
    public void setMiterLimit(double limit) {
        record("miterLimit", limit);
    }

    @Override
    public void setCompositeOperation(String operation) {
        record("composite", operation);
    }

    @Override
    public void setFont(String cssFont) {
        record("font", cssFont);
    }

    @Override
    public void setGlobalAlpha(double alpha) {
        record("globalAlpha", alpha);
    }

    @Override
    public void clearRect(double x, double y, double width, double height) {
        record("clearRect", x, y, width, height);
    }

    @Override
    public void copyArea(double x, double y, double width, double height, double deltaX, double deltaY) {
        record("copyArea", x, y, width, height, deltaX, deltaY);
    }

    @Override
    public void fillRect(double x, double y, double width, double height) {
        record("fillRect", x, y, width, height);
    }

    @Override
    public void strokeRect(double x, double y, double width, double height) {
        record("strokeRect", x, y, width, height);
    }

    @Override
    public void beginPath() {
        record("beginPath");
    }

    @Override
    public void moveTo(double x, double y) {
        record("moveTo", x, y);
    }

    @Override
    public void lineTo(double x, double y) {
        record("lineTo", x, y);
    }

    @Override
    public void quadraticCurveTo(double controlX, double controlY, double x, double y) {
        record("quadraticCurveTo", controlX, controlY, x, y);
    }

    @Override
    public void bezierCurveTo(double controlX1, double controlY1, double controlX2, double controlY2, double x, double y) {
        record("bezierCurveTo", controlX1, controlY1, controlX2, controlY2, x, y);
    }

    @Override
    public void closePath() {
        record("closePath");
    }

    @Override
    public void fill() {
        record("fill");
    }

    @Override
    public void fill(boolean evenOdd) {
        record("fill", evenOdd ? "evenodd" : "nonzero");
    }

    @Override
    public void stroke() {
        record("stroke");
    }

    @Override
    public void clip() {
        record("clip");
    }

    @Override
    public void clip(boolean evenOdd) {
        record("clip", evenOdd ? "evenodd" : "nonzero");
    }

    @Override
    public void save() {
        record("save");
    }

    @Override
    public void restore() {
        record("restore");
    }

    @Override
    public void translate(double x, double y) {
        record("translate", x, y);
    }

    @Override
    public void scale(double x, double y) {
        record("scale", x, y);
    }

    @Override
    public void rotate(double angle) {
        record("rotate", angle);
    }

    @Override
    public void transform(double scaleX, double shearY, double shearX, double scaleY, double translateX, double translateY) {
        record("transform", scaleX, shearY, shearX, scaleY, translateX, translateY);
    }

    @Override
    public void drawImage(int[] pixels, int imageWidth, int imageHeight, int sourceX1, int sourceY1, int sourceX2, int sourceY2, double destinationX1, double destinationY1, double destinationX2, double destinationY2) {
        record("drawImage", imageWidth, imageHeight, sourceX1, sourceY1, sourceX2, sourceY2, destinationX1, destinationY1, destinationX2, destinationY2);
    }

    @Override
    public void fillText(String text, double x, double y) {
        commands.add("fillText(" + text + "," + x + "," + y + ")");
    }

    private void record(String name, Object... values) {
        StringBuilder command = new StringBuilder(name).append('(');
        for (int index = 0; index < values.length; index++) {
            if (index > 0) {
                command.append(',');
            }
            command.append(values[index]);
        }
        commands.add(command.append(')').toString());
    }
}
