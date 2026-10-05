/*
 * Copyright 2003-2022 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jetbrains.mps.nodeEditor.keymaps;

import com.intellij.util.ui.UIUtil;
import gnu.trove.TIntObjectHashMap;
import jetbrains.mps.logging.Logger;
import jetbrains.mps.nodeEditor.EditorComponent;
import jetbrains.mps.openapi.editor.EditorContext;
import jetbrains.mps.openapi.editor.cells.EditorCell;
import jetbrains.mps.openapi.editor.cells.KeyMap;
import jetbrains.mps.openapi.editor.cells.KeyMap.ActionKey;
import jetbrains.mps.openapi.editor.cells.KeyMapAction;
import jetbrains.mps.util.Pair;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.plaf.basic.BasicMenuItemUI;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * User: shatalin
 * Date: 2/5/13
 */
public class AWTKeymapHandler extends KeymapHandler<KeyEvent> {
  private static final Logger LOG = Logger.getLogger(AWTKeymapHandler.class);
  private static final TIntObjectHashMap<List<String>> ourJavaKeyCodesMap = new TIntObjectHashMap<>();

  static {
    // Explicit sibling AWT constants; retain aliases sharing the same numeric code.
    registerKey(KeyEvent.VK_ENTER, "VK_ENTER");
    registerKey(KeyEvent.VK_BACK_SPACE, "VK_BACK_SPACE");
    registerKey(KeyEvent.VK_TAB, "VK_TAB");
    registerKey(KeyEvent.VK_CANCEL, "VK_CANCEL");
    registerKey(KeyEvent.VK_CLEAR, "VK_CLEAR");
    registerKey(KeyEvent.VK_PAUSE, "VK_PAUSE");
    registerKey(KeyEvent.VK_CAPS_LOCK, "VK_CAPS_LOCK");
    registerKey(KeyEvent.VK_ESCAPE, "VK_ESCAPE");
    registerKey(KeyEvent.VK_SPACE, "VK_SPACE");
    registerKey(KeyEvent.VK_PAGE_UP, "VK_PAGE_UP");
    registerKey(KeyEvent.VK_PAGE_DOWN, "VK_PAGE_DOWN");
    registerKey(KeyEvent.VK_END, "VK_END");
    registerKey(KeyEvent.VK_HOME, "VK_HOME");
    registerKey(KeyEvent.VK_LEFT, "VK_LEFT");
    registerKey(KeyEvent.VK_UP, "VK_UP");
    registerKey(KeyEvent.VK_RIGHT, "VK_RIGHT");
    registerKey(KeyEvent.VK_DOWN, "VK_DOWN");
    registerKey(KeyEvent.VK_COMMA, "VK_COMMA");
    registerKey(KeyEvent.VK_MINUS, "VK_MINUS");
    registerKey(KeyEvent.VK_PERIOD, "VK_PERIOD");
    registerKey(KeyEvent.VK_SLASH, "VK_SLASH");
    registerKey(KeyEvent.VK_0, "VK_0");
    registerKey(KeyEvent.VK_1, "VK_1");
    registerKey(KeyEvent.VK_2, "VK_2");
    registerKey(KeyEvent.VK_3, "VK_3");
    registerKey(KeyEvent.VK_4, "VK_4");
    registerKey(KeyEvent.VK_5, "VK_5");
    registerKey(KeyEvent.VK_6, "VK_6");
    registerKey(KeyEvent.VK_7, "VK_7");
    registerKey(KeyEvent.VK_8, "VK_8");
    registerKey(KeyEvent.VK_9, "VK_9");
    registerKey(KeyEvent.VK_SEMICOLON, "VK_SEMICOLON");
    registerKey(KeyEvent.VK_EQUALS, "VK_EQUALS");
    registerKey(KeyEvent.VK_A, "VK_A");
    registerKey(KeyEvent.VK_B, "VK_B");
    registerKey(KeyEvent.VK_C, "VK_C");
    registerKey(KeyEvent.VK_D, "VK_D");
    registerKey(KeyEvent.VK_E, "VK_E");
    registerKey(KeyEvent.VK_F, "VK_F");
    registerKey(KeyEvent.VK_G, "VK_G");
    registerKey(KeyEvent.VK_H, "VK_H");
    registerKey(KeyEvent.VK_I, "VK_I");
    registerKey(KeyEvent.VK_J, "VK_J");
    registerKey(KeyEvent.VK_K, "VK_K");
    registerKey(KeyEvent.VK_L, "VK_L");
    registerKey(KeyEvent.VK_M, "VK_M");
    registerKey(KeyEvent.VK_N, "VK_N");
    registerKey(KeyEvent.VK_O, "VK_O");
    registerKey(KeyEvent.VK_P, "VK_P");
    registerKey(KeyEvent.VK_Q, "VK_Q");
    registerKey(KeyEvent.VK_R, "VK_R");
    registerKey(KeyEvent.VK_S, "VK_S");
    registerKey(KeyEvent.VK_T, "VK_T");
    registerKey(KeyEvent.VK_U, "VK_U");
    registerKey(KeyEvent.VK_V, "VK_V");
    registerKey(KeyEvent.VK_W, "VK_W");
    registerKey(KeyEvent.VK_X, "VK_X");
    registerKey(KeyEvent.VK_Y, "VK_Y");
    registerKey(KeyEvent.VK_Z, "VK_Z");
    registerKey(KeyEvent.VK_OPEN_BRACKET, "VK_OPEN_BRACKET");
    registerKey(KeyEvent.VK_BACK_SLASH, "VK_BACK_SLASH");
    registerKey(KeyEvent.VK_CLOSE_BRACKET, "VK_CLOSE_BRACKET");
    registerKey(KeyEvent.VK_NUMPAD0, "VK_NUMPAD0");
    registerKey(KeyEvent.VK_NUMPAD1, "VK_NUMPAD1");
    registerKey(KeyEvent.VK_NUMPAD2, "VK_NUMPAD2");
    registerKey(KeyEvent.VK_NUMPAD3, "VK_NUMPAD3");
    registerKey(KeyEvent.VK_NUMPAD4, "VK_NUMPAD4");
    registerKey(KeyEvent.VK_NUMPAD5, "VK_NUMPAD5");
    registerKey(KeyEvent.VK_NUMPAD6, "VK_NUMPAD6");
    registerKey(KeyEvent.VK_NUMPAD7, "VK_NUMPAD7");
    registerKey(KeyEvent.VK_NUMPAD8, "VK_NUMPAD8");
    registerKey(KeyEvent.VK_NUMPAD9, "VK_NUMPAD9");
    registerKey(KeyEvent.VK_MULTIPLY, "VK_MULTIPLY");
    registerKey(KeyEvent.VK_ADD, "VK_ADD");
    registerKey(KeyEvent.VK_SEPARATER, "VK_SEPARATER");
    registerKey(KeyEvent.VK_SEPARATOR, "VK_SEPARATOR");
    registerKey(KeyEvent.VK_SUBTRACT, "VK_SUBTRACT");
    registerKey(KeyEvent.VK_DECIMAL, "VK_DECIMAL");
    registerKey(KeyEvent.VK_DIVIDE, "VK_DIVIDE");
    registerKey(KeyEvent.VK_DELETE, "VK_DELETE");
    registerKey(KeyEvent.VK_NUM_LOCK, "VK_NUM_LOCK");
    registerKey(KeyEvent.VK_SCROLL_LOCK, "VK_SCROLL_LOCK");
    registerKey(KeyEvent.VK_F1, "VK_F1");
    registerKey(KeyEvent.VK_F2, "VK_F2");
    registerKey(KeyEvent.VK_F3, "VK_F3");
    registerKey(KeyEvent.VK_F4, "VK_F4");
    registerKey(KeyEvent.VK_F5, "VK_F5");
    registerKey(KeyEvent.VK_F6, "VK_F6");
    registerKey(KeyEvent.VK_F7, "VK_F7");
    registerKey(KeyEvent.VK_F8, "VK_F8");
    registerKey(KeyEvent.VK_F9, "VK_F9");
    registerKey(KeyEvent.VK_F10, "VK_F10");
    registerKey(KeyEvent.VK_F11, "VK_F11");
    registerKey(KeyEvent.VK_F12, "VK_F12");
    registerKey(KeyEvent.VK_F13, "VK_F13");
    registerKey(KeyEvent.VK_F14, "VK_F14");
    registerKey(KeyEvent.VK_F15, "VK_F15");
    registerKey(KeyEvent.VK_F16, "VK_F16");
    registerKey(KeyEvent.VK_F17, "VK_F17");
    registerKey(KeyEvent.VK_F18, "VK_F18");
    registerKey(KeyEvent.VK_F19, "VK_F19");
    registerKey(KeyEvent.VK_F20, "VK_F20");
    registerKey(KeyEvent.VK_F21, "VK_F21");
    registerKey(KeyEvent.VK_F22, "VK_F22");
    registerKey(KeyEvent.VK_F23, "VK_F23");
    registerKey(KeyEvent.VK_F24, "VK_F24");
    registerKey(KeyEvent.VK_PRINTSCREEN, "VK_PRINTSCREEN");
    registerKey(KeyEvent.VK_INSERT, "VK_INSERT");
    registerKey(KeyEvent.VK_HELP, "VK_HELP");
    registerKey(KeyEvent.VK_META, "VK_META");
    registerKey(KeyEvent.VK_BACK_QUOTE, "VK_BACK_QUOTE");
    registerKey(KeyEvent.VK_QUOTE, "VK_QUOTE");
    registerKey(KeyEvent.VK_KP_UP, "VK_KP_UP");
    registerKey(KeyEvent.VK_KP_DOWN, "VK_KP_DOWN");
    registerKey(KeyEvent.VK_KP_LEFT, "VK_KP_LEFT");
    registerKey(KeyEvent.VK_KP_RIGHT, "VK_KP_RIGHT");
    registerKey(KeyEvent.VK_DEAD_GRAVE, "VK_DEAD_GRAVE");
    registerKey(KeyEvent.VK_DEAD_ACUTE, "VK_DEAD_ACUTE");
    registerKey(KeyEvent.VK_DEAD_CIRCUMFLEX, "VK_DEAD_CIRCUMFLEX");
    registerKey(KeyEvent.VK_DEAD_TILDE, "VK_DEAD_TILDE");
    registerKey(KeyEvent.VK_DEAD_MACRON, "VK_DEAD_MACRON");
    registerKey(KeyEvent.VK_DEAD_BREVE, "VK_DEAD_BREVE");
    registerKey(KeyEvent.VK_DEAD_ABOVEDOT, "VK_DEAD_ABOVEDOT");
    registerKey(KeyEvent.VK_DEAD_DIAERESIS, "VK_DEAD_DIAERESIS");
    registerKey(KeyEvent.VK_DEAD_ABOVERING, "VK_DEAD_ABOVERING");
    registerKey(KeyEvent.VK_DEAD_DOUBLEACUTE, "VK_DEAD_DOUBLEACUTE");
    registerKey(KeyEvent.VK_DEAD_CARON, "VK_DEAD_CARON");
    registerKey(KeyEvent.VK_DEAD_CEDILLA, "VK_DEAD_CEDILLA");
    registerKey(KeyEvent.VK_DEAD_OGONEK, "VK_DEAD_OGONEK");
    registerKey(KeyEvent.VK_DEAD_IOTA, "VK_DEAD_IOTA");
    registerKey(KeyEvent.VK_DEAD_VOICED_SOUND, "VK_DEAD_VOICED_SOUND");
    registerKey(KeyEvent.VK_DEAD_SEMIVOICED_SOUND, "VK_DEAD_SEMIVOICED_SOUND");
    registerKey(KeyEvent.VK_AMPERSAND, "VK_AMPERSAND");
    registerKey(KeyEvent.VK_ASTERISK, "VK_ASTERISK");
    registerKey(KeyEvent.VK_QUOTEDBL, "VK_QUOTEDBL");
    registerKey(KeyEvent.VK_LESS, "VK_LESS");
    registerKey(KeyEvent.VK_GREATER, "VK_GREATER");
    registerKey(KeyEvent.VK_BRACELEFT, "VK_BRACELEFT");
    registerKey(KeyEvent.VK_BRACERIGHT, "VK_BRACERIGHT");
    registerKey(KeyEvent.VK_AT, "VK_AT");
    registerKey(KeyEvent.VK_COLON, "VK_COLON");
    registerKey(KeyEvent.VK_CIRCUMFLEX, "VK_CIRCUMFLEX");
    registerKey(KeyEvent.VK_DOLLAR, "VK_DOLLAR");
    registerKey(KeyEvent.VK_EURO_SIGN, "VK_EURO_SIGN");
    registerKey(KeyEvent.VK_EXCLAMATION_MARK, "VK_EXCLAMATION_MARK");
    registerKey(KeyEvent.VK_INVERTED_EXCLAMATION_MARK, "VK_INVERTED_EXCLAMATION_MARK");
    registerKey(KeyEvent.VK_LEFT_PARENTHESIS, "VK_LEFT_PARENTHESIS");
    registerKey(KeyEvent.VK_NUMBER_SIGN, "VK_NUMBER_SIGN");
    registerKey(KeyEvent.VK_PLUS, "VK_PLUS");
    registerKey(KeyEvent.VK_RIGHT_PARENTHESIS, "VK_RIGHT_PARENTHESIS");
    registerKey(KeyEvent.VK_UNDERSCORE, "VK_UNDERSCORE");
    registerKey(KeyEvent.VK_WINDOWS, "VK_WINDOWS");
    registerKey(KeyEvent.VK_CONTEXT_MENU, "VK_CONTEXT_MENU");
    registerKey(KeyEvent.VK_FINAL, "VK_FINAL");
    registerKey(KeyEvent.VK_CONVERT, "VK_CONVERT");
    registerKey(KeyEvent.VK_NONCONVERT, "VK_NONCONVERT");
    registerKey(KeyEvent.VK_ACCEPT, "VK_ACCEPT");
    registerKey(KeyEvent.VK_MODECHANGE, "VK_MODECHANGE");
    registerKey(KeyEvent.VK_KANA, "VK_KANA");
    registerKey(KeyEvent.VK_KANJI, "VK_KANJI");
    registerKey(KeyEvent.VK_ALPHANUMERIC, "VK_ALPHANUMERIC");
    registerKey(KeyEvent.VK_KATAKANA, "VK_KATAKANA");
    registerKey(KeyEvent.VK_HIRAGANA, "VK_HIRAGANA");
    registerKey(KeyEvent.VK_FULL_WIDTH, "VK_FULL_WIDTH");
    registerKey(KeyEvent.VK_HALF_WIDTH, "VK_HALF_WIDTH");
    registerKey(KeyEvent.VK_ROMAN_CHARACTERS, "VK_ROMAN_CHARACTERS");
    registerKey(KeyEvent.VK_ALL_CANDIDATES, "VK_ALL_CANDIDATES");
    registerKey(KeyEvent.VK_PREVIOUS_CANDIDATE, "VK_PREVIOUS_CANDIDATE");
    registerKey(KeyEvent.VK_CODE_INPUT, "VK_CODE_INPUT");
    registerKey(KeyEvent.VK_JAPANESE_KATAKANA, "VK_JAPANESE_KATAKANA");
    registerKey(KeyEvent.VK_JAPANESE_HIRAGANA, "VK_JAPANESE_HIRAGANA");
    registerKey(KeyEvent.VK_JAPANESE_ROMAN, "VK_JAPANESE_ROMAN");
    registerKey(KeyEvent.VK_KANA_LOCK, "VK_KANA_LOCK");
    registerKey(KeyEvent.VK_INPUT_METHOD_ON_OFF, "VK_INPUT_METHOD_ON_OFF");
    registerKey(KeyEvent.VK_CUT, "VK_CUT");
    registerKey(KeyEvent.VK_COPY, "VK_COPY");
    registerKey(KeyEvent.VK_PASTE, "VK_PASTE");
    registerKey(KeyEvent.VK_UNDO, "VK_UNDO");
    registerKey(KeyEvent.VK_AGAIN, "VK_AGAIN");
    registerKey(KeyEvent.VK_FIND, "VK_FIND");
    registerKey(KeyEvent.VK_PROPS, "VK_PROPS");
    registerKey(KeyEvent.VK_STOP, "VK_STOP");
    registerKey(KeyEvent.VK_COMPOSE, "VK_COMPOSE");
    registerKey(KeyEvent.VK_ALT_GRAPH, "VK_ALT_GRAPH");
    registerKey(KeyEvent.VK_BEGIN, "VK_BEGIN");
    registerKey(KeyEvent.VK_UNDEFINED, "VK_UNDEFINED");
  }

  private static void registerKey(int code, String name) {
    List<String> names = ourJavaKeyCodesMap.get(code);
    if (names == null) {
      names = new ArrayList<>();
      ourJavaKeyCodesMap.put(code, names);
    }
    names.add(name);
  }

  public static List<String> getValidKeyCodes() {
    final List<String> result = new ArrayList<>(ourJavaKeyCodesMap.size() + 5);
    result.add(KeyMap.KEY_CODE_DIGIT);
    result.add(KeyMap.KEY_CODE_LETTER);
    result.add(KeyMap.KEY_CODE_LETTER_OR_DIGIT);
    result.add(KeyMap.KEY_CODE_SPACE);
    result.add(KeyMap.KEY_CODE_CHAR);
    ourJavaKeyCodesMap.forEachValue(value -> {
      result.addAll(value);
      return true;
    });
    Collections.sort(result);
    return result;
  }

  public static List<String> getValidModifiers() {
    final List<String> result = new ArrayList<>(8);
    result.add(KeyMap.KEY_MODIFIERS_NONE);
    result.add(KeyMap.KEY_MODIFIERS_ANY);
    result.add(KeyMap.KEY_MODIFIERS_CTRL);
    result.add(KeyMap.KEY_MODIFIERS_ALT);
    result.add(KeyMap.KEY_MODIFIERS_SHIFT);
    result.add(KeyMap.KEY_MODIFIERS_CTRL_ALT);
    result.add(KeyMap.KEY_MODIFIERS_CTRL_SHIFT);
    result.add(KeyMap.KEY_MODIFIERS_CTRL_ALT_SHIFT);
    result.add(KeyMap.KEY_MODIFIERS_ALT_SHIFT);
    Collections.sort(result);
    return result;
  }

  private static List<String> modifiersForEvent(KeyEvent event) {
    List<String> modifiers = new LinkedList<>();
    if (event.getModifiersEx() == 0) {
      modifiers.add(KeyMap.KEY_MODIFIERS_NONE);
    } else if (event.isControlDown() && !event.isAltDown() && !event.isShiftDown()) {
      modifiers.add(KeyMap.KEY_MODIFIERS_CTRL);
    } else if (!event.isControlDown() && event.isAltDown() && !event.isShiftDown()) {
      modifiers.add(KeyMap.KEY_MODIFIERS_ALT);
    } else if (!event.isControlDown() && !event.isAltDown() && event.isShiftDown()) {
      modifiers.add(KeyMap.KEY_MODIFIERS_SHIFT);
    } else if (event.isControlDown() && event.isAltDown() && !event.isShiftDown()) {
      modifiers.add(KeyMap.KEY_MODIFIERS_CTRL_ALT);
    } else if (event.isControlDown() && !event.isAltDown() && event.isShiftDown()) {
      modifiers.add(KeyMap.KEY_MODIFIERS_CTRL_SHIFT);
    } else if (event.isControlDown() && event.isAltDown() && event.isShiftDown()) {
      modifiers.add(KeyMap.KEY_MODIFIERS_CTRL_ALT_SHIFT);
    } else if (!event.isControlDown() && event.isAltDown() && event.isShiftDown()) {
      modifiers.add(KeyMap.KEY_MODIFIERS_ALT_SHIFT);
    }

    modifiers.add(KeyMap.KEY_MODIFIERS_ANY);
    return modifiers;
  }

  private static List<String> keyCodesForEvent(KeyEvent event) {
    List<String> keyCodes = new LinkedList<>();
    keyCodes.add("");

    int keyCode = event.getKeyCode();
    if (keyCode != KeyEvent.VK_CONTROL &&
      keyCode != KeyEvent.VK_ALT &&
      keyCode != KeyEvent.VK_SHIFT &&
      keyCode != KeyEvent.VK_UNDEFINED) {
      List<String> keyCodeNames = ourJavaKeyCodesMap.get(keyCode);
      // it may happen that the keyCode is not repesented by any of VK_ constants (most notably umlauts on DE keyboards on Macs)
      if (keyCodeNames != null) {
        keyCodes.addAll(keyCodeNames);
      }
    }

    // todo: the "keychar" testing in the "key pressed" event is not very reliable
    // todo: the "key typed" event should be handled instead
    if (event.isControlDown() || event.isAltDown()) {
      // ignore keychar
      return keyCodes;   //TODO why?!
    }

    char keyChar = event.getKeyChar();
    if (keyChar == KeyEvent.CHAR_UNDEFINED) {
      return keyCodes;
    }

    keyCodes.add("" + keyChar);

    if (!Character.isSpaceChar(keyChar) && !Character.isWhitespace(keyChar) &&
      keyChar != KeyEvent.VK_DELETE &&
      keyChar != KeyEvent.VK_ESCAPE &&
      keyChar != KeyEvent.VK_BACK_SPACE) {
      keyCodes.add(KeyMap.KEY_CODE_CHAR);
    }

    if (Character.isDigit(keyChar)) {
      keyCodes.add(KeyMap.KEY_CODE_DIGIT);
      keyCodes.add(KeyMap.KEY_CODE_LETTER_OR_DIGIT);
    } else if (Character.isLetter(keyChar)) {
      keyCodes.add(KeyMap.KEY_CODE_LETTER);
      keyCodes.add(KeyMap.KEY_CODE_LETTER_OR_DIGIT);
    } else if (Character.isLetterOrDigit(keyChar)) {
      keyCodes.add(KeyMap.KEY_CODE_LETTER_OR_DIGIT);
    } else if (Character.isSpaceChar(keyChar) || Character.isWhitespace(keyChar)) {
      keyCodes.add(KeyMap.KEY_CODE_SPACE);
    }

    return keyCodes;
  }

  @Override
  public Collection<ActionKey> getActionKeys(KeyEvent event) {
    List<ActionKey> keys = new LinkedList<>();
    List<String> modifiers = modifiersForEvent(event);
    List<String> keyCodes = keyCodesForEvent(event);
    if (modifiers.size() > 0 && keyCodes.size() > 0) {
      for (String modifier : modifiers) {
        for (String keyCode : keyCodes) {
          ActionKey actionKey = new ActionKey(modifier, keyCode, event.getID() == KeyEvent.KEY_TYPED);
          keys.add(actionKey);
        }
      }
    }
    return keys;
  }

  @Override
  public void showActionsMenu(Collection<Pair<KeyMapAction, EditorCell>> actionsInfo, final EditorContext editorContext, EditorCell selectedCell) {
    JPopupMenu menu = new JPopupMenu();
    final Color selectionColor = UIUtil.getTreeSelectionBackground(true);
    int index = 1;
    for (Pair<KeyMapAction, EditorCell> actionAndContextCell : actionsInfo) {
      final KeyMapAction action = actionAndContextCell.o1;
      final EditorCell contextCell = actionAndContextCell.o2;
      char acc = 0;
      if (1 <= index && index <= 9) {
        acc = (char) ('0' + index);
      } else if (index == 10) {
        acc = '0';
      } else if (10 < index && index - 11 < ('Z' - 'A')) {
        acc = (char) ('A' + index - 11);
      }
      JMenuItem menuItem = new JMenuItem(action.getDescriptionText());
      if (acc != 0) {
        menuItem.setAccelerator(KeyStroke.getKeyStroke(acc));
      }
      ActionListener actionListener = e -> executeAction(action, contextCell, editorContext);
      menuItem.addActionListener(actionListener);
      menuItem.setUI(new BasicMenuItemUI(){
        @Override
        protected void paintBackground(Graphics g, JMenuItem mi, Color bgColor) {
          final Color originalColor = g.getColor();
          if (mi.isArmed() || (mi instanceof JMenu && mi.getModel().isSelected())) {
            g.setColor(selectionColor);
            g.fillRect(0, 0, mi.getWidth(), mi.getHeight());
          } else {
            g.setColor(mi.getBackground());
            g.fillRect(0, 0, mi.getWidth(), mi.getHeight());
          }
          g.setColor(originalColor);
        }
      });
      menu.add(menuItem);
      index++;
    }
    EditorComponent component = ((jetbrains.mps.nodeEditor.EditorContext) editorContext).getNodeEditorComponent();
    int x = selectedCell.getX();
    int y = selectedCell.getY() + selectedCell.getHeight();
    if (component.getParent() instanceof JViewport) {
      JViewport viewport = (JViewport) component.getParent();
      Rectangle vr = viewport.getViewRect();
      x = Math.max(vr.x, x);
      y = Math.max(vr.y, y);
    }
    menu.show(component, x, y);
  }
}
