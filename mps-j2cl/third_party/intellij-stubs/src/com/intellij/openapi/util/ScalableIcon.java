package com.intellij.openapi.util;
import javax.swing.Icon;
public interface ScalableIcon extends Icon {
  float getScale();
  Icon scale(float scaleFactor);
}
