package java.awt;

import java.io.Serializable;

public class GridBagLayoutInfo implements Serializable {
    private static final long serialVersionUID = -4899416460737170217L;
    int height;
    int startx;
    int starty;
    int width;
    double[] weightX;
    double[] weightY;
    int[] minHeight;
    int[] minWidth;

    GridBagLayoutInfo(int width, int height) {
        this.width = width;
        this.height = height;
    }
}
