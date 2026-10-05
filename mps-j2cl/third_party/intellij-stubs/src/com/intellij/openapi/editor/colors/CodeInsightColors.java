package com.intellij.openapi.editor.colors;
import com.intellij.openapi.editor.markup.EffectType;
import com.intellij.openapi.editor.markup.TextAttributes;
import java.awt.Color;
public interface CodeInsightColors {
  static TextAttributes diagnostic(Color color) {
    TextAttributes attributes = new TextAttributes(null, null, color, EffectType.WAVE_UNDERSCORE, 0);
    attributes.setErrorStripeColor(color);
    return attributes;
  }
  TextAttributesKey ERRORS_ATTRIBUTES = TextAttributesKey.createTextAttributesKey("ERRORS_ATTRIBUTES", diagnostic(Color.RED));
  TextAttributesKey WARNINGS_ATTRIBUTES = TextAttributesKey.createTextAttributesKey("WARNINGS_ATTRIBUTES", diagnostic(Color.ORANGE));
  TextAttributesKey WEAK_WARNING_ATTRIBUTES = TextAttributesKey.createTextAttributesKey("WEAK_WARNING_ATTRIBUTES", diagnostic(Color.GRAY));
  TextAttributesKey INFORMATION_ATTRIBUTES = TextAttributesKey.createTextAttributesKey("INFORMATION_ATTRIBUTES", new TextAttributes());
  TextAttributesKey GENERIC_SERVER_ERROR_OR_WARNING = ERRORS_ATTRIBUTES;
  TextAttributesKey NOT_USED_ELEMENT_ATTRIBUTES = TextAttributesKey.createTextAttributesKey("NOT_USED_ELEMENT_ATTRIBUTES", new TextAttributes(Color.GRAY, null, null, null, 0));
}
