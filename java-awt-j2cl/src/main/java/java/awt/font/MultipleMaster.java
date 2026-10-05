package java.awt.font;

import java.awt.Font;

public interface MultipleMaster {
    int getNumDesignAxes();
    float[] getDesignAxisRanges();
    float[] getDesignAxisDefaults();
    String[] getDesignAxisNames();
    Font deriveMMFont(float[] axes);
    Font deriveMMFont(float[] axes, float weight, float width, float restX, float restY);
}
