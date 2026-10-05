package com.intellij.openapi.editor.colors;
import com.intellij.openapi.editor.markup.TextAttributes;
import java.awt.Color;
public interface EditorColors {
  ColorKey CARET_COLOR = ColorKey.createColorKey("CARET_COLOR", Color.BLACK);
  ColorKey CARET_ROW_COLOR = ColorKey.createColorKey("CARET_ROW_COLOR", new Color(255, 255, 215));
  ColorKey SELECTION_BACKGROUND_COLOR = ColorKey.createColorKey("SELECTION_BACKGROUND_COLOR", new Color(82, 109, 165));
  ColorKey SELECTION_FOREGROUND_COLOR = ColorKey.createColorKey("SELECTION_FOREGROUND_COLOR", Color.WHITE);
  ColorKey RIGHT_MARGIN_COLOR = ColorKey.createColorKey("RIGHT_MARGIN_COLOR", Color.LIGHT_GRAY);
  ColorKey GUTTER_BACKGROUND = ColorKey.createColorKey("GUTTER_BACKGROUND", new Color(245, 245, 245));
  ColorKey EDITOR_GUTTER_BACKGROUND = GUTTER_BACKGROUND;
  ColorKey TEARLINE_COLOR = ColorKey.createColorKey("TEARLINE_COLOR", Color.LIGHT_GRAY);
  ColorKey SELECTED_TEARLINE_COLOR = ColorKey.createColorKey("SELECTED_TEARLINE_COLOR", Color.GRAY);
  TextAttributesKey REFERENCE_HYPERLINK_COLOR = TextAttributesKey.createTextAttributesKey("REFERENCE_HYPERLINK_COLOR", new TextAttributes(new Color(0, 102, 204), null, null, null, 0));
  TextAttributesKey IDENTIFIER_UNDER_CARET_ATTRIBUTES = TextAttributesKey.createTextAttributesKey("IDENTIFIER_UNDER_CARET_ATTRIBUTES", new TextAttributes(null, new Color(230, 235, 255), null, null, 0));
  TextAttributesKey SEARCH_RESULT_ATTRIBUTES = TextAttributesKey.createTextAttributesKey("SEARCH_RESULT_ATTRIBUTES", new TextAttributes(null, new Color(255, 240, 150), null, null, 0));
  ColorKey DOCUMENTATION_COLOR = ColorKey.createColorKey("DOCUMENTATION_COLOR", Color.WHITE);
}
