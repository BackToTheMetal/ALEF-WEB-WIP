package com.webawt.portable;

import java.awt.Button;
import java.awt.Checkbox;
import java.awt.Choice;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.List;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.Toolkit;
import java.awt.font.TextAttribute;
import java.awt.im.InputContext;
import java.awt.im.InputMethodHighlight;
import java.awt.im.InputMethodRequests;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ContainerAdapter;
import java.awt.event.ContainerEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.ItemListener;
import java.awt.event.TextListener;
import java.text.AttributedCharacterIterator;
import java.util.Locale;
import java.util.Map;
import org.junit.Test;

public final class FontAndInputJ2clTest {
    @Test
    public void preservesTextSelectionWhenPeersAreRecreatedAndExposesAccessibleButtonAction() {
        TextField field = new TextField("abcdef");
        field.select(1, 4);
        field.addNotify();
        assertEquals(1, field.getSelectionStart());
        assertEquals(4, field.getSelectionEnd());
        field.removeNotify();
        field.addNotify();
        assertEquals(4, field.getCaretPosition());
        field.setText("ab");
        assertEquals(2, field.getSelectionEnd());
        field.removeNotify();
        Button button = new Button("Accessible action");
        int[] actions = new int[1];
        button.addActionListener(event -> actions[0]++);
        javax.accessibility.AccessibleAction action = button.getAccessibleContext().getAccessibleAction();
        assertEquals(1, action.getAccessibleActionCount());
        assertTrue(action.doAccessibleAction(0));
        assertEquals(1, actions[0]);
        button.setEnabled(false);
        assertTrue(!action.doAccessibleAction(0));
        assertTrue(!action.doAccessibleAction(1));
        assertEquals(1, actions[0]);
    }

    @Test
    public void derivesFontsAndProvidesDeterministicMetrics() {
        Font font = new Font(Font.SANS_SERIF, Font.BOLD, 14).deriveFont(18.5f);
        FontMetrics metrics = new FontMetrics(font) {
        };
        assertEquals(Font.BOLD, font.getStyle());
        assertEquals(19, font.getSize());
        assertTrue(metrics.stringWidth("WebAWT") > 0);
        assertTrue(font.getStringBounds("WebAWT", new java.awt.font.FontRenderContext(null, false, false)).getWidth() > 0);
    }

    @Test
    public void dispatchesContainerComponentAndConsumedKeyEvents() {
        Panel panel = new Panel(null);
        Button button = new Button("Input");
        int[] counts = new int[3];
        panel.addContainerListener(new ContainerAdapter() {
            @Override
            public void componentAdded(ContainerEvent event) {
                counts[0]++;
            }
        });
        button.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent event) {
                counts[1]++;
            }
        });
        button.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent event) {
                counts[2]++;
                event.consume();
            }
        });
        panel.add(button);
        button.setSize(20, 10);
        KeyEvent event = new KeyEvent(button, KeyEvent.KEY_PRESSED, 123L, KeyEvent.CTRL_DOWN_MASK, KeyEvent.VK_A, 'a', KeyEvent.KEY_LOCATION_STANDARD);
        button.dispatchEvent(event);
        assertEquals(1, counts[0]);
        assertEquals(1, counts[1]);
        assertEquals(1, counts[2]);
        assertTrue(event.isConsumed());
    }

    @Test
    public void reportsTypedListenersAndLegacyListRemoval() {
        ActionListener actionListener = event -> {
        };
        ItemListener itemListener = event -> {
        };
        TextListener textListener = event -> {
        };
        Button button = new Button();
        button.addActionListener(actionListener);
        assertSame(actionListener, button.getListeners(ActionListener.class)[0]);
        Checkbox checkbox = new Checkbox();
        checkbox.addItemListener(itemListener);
        assertSame(itemListener, checkbox.getListeners(ItemListener.class)[0]);
        Choice choice = new Choice();
        choice.addItemListener(itemListener);
        assertSame(itemListener, choice.getListeners(ItemListener.class)[0]);
        TextField textField = new TextField();
        textField.addActionListener(actionListener);
        textField.addTextListener(textListener);
        assertSame(actionListener, textField.getListeners(ActionListener.class)[0]);
        assertSame(textListener, textField.getListeners(TextListener.class)[0]);
        List list = new List();
        list.add("zero");
        list.add("one");
        list.add("two");
        list.addActionListener(actionListener);
        list.addItemListener(itemListener);
        assertSame(actionListener, list.getListeners(ActionListener.class)[0]);
        assertSame(itemListener, list.getListeners(ItemListener.class)[0]);
        list.delItem(1);
        list.delItems(0, 1);
        assertEquals(0, list.getItemCount());
    }

    @Test
    public void exposesBrowserInputContextAndTextRequests() {
        TextField textField = new TextField("hello");
        textField.select(1, 4);
        InputMethodRequests requests = textField.getInputMethodRequests();
        assertEquals(5, requests.getCommittedTextLength());
        assertEquals(4, requests.getInsertPositionOffset());
        assertEquals("ell", iteratorText(requests.getSelectedText(null)));
        assertEquals("ell", iteratorText(requests.getCommittedText(1, 4, null)));
        assertTrue(requests.getTextLocation(null).height > 0);
        assertTrue(requests.getLocationOffset(0, 0) != null);
        InputContext context = textField.getInputContext();
        assertTrue(context.selectInputMethod(Locale.ENGLISH));
        assertSame(Locale.ENGLISH, context.getLocale());
        context.setCompositionEnabled(false);
        assertTrue(!context.isCompositionEnabled());
        textField.enableInputMethods(false);
        assertSame(null, textField.getInputContext());
        Map<TextAttribute, ?> style = Toolkit.getDefaultToolkit().mapInputMethodHighlight(InputMethodHighlight.SELECTED_RAW_TEXT_HIGHLIGHT);
        assertTrue(style.containsKey(TextAttribute.INPUT_METHOD_UNDERLINE));
        assertTrue(style.containsKey(TextAttribute.SWAP_COLORS));
    }

    private static String iteratorText(AttributedCharacterIterator iterator) {
        StringBuilder text = new StringBuilder();
        for (char character = iterator.first(); character != AttributedCharacterIterator.DONE; character = iterator.next()) {
            text.append(character);
        }
        return text.toString();
    }

    private static void assertEquals(int expected, int actual) {
        if (expected != actual) {
            throw new AssertionError("Expected " + expected + " but got " + actual);
        }
    }

    private static void assertEquals(String expected, String actual) {
        if (!expected.equals(actual)) {
            throw new AssertionError("Expected " + expected + " but got " + actual);
        }
    }

    private static void assertTrue(boolean value) {
        if (!value) {
            throw new AssertionError("Expected true");
        }
    }

    private static void assertSame(Object expected, Object actual) {
        if (expected != actual) {
            throw new AssertionError("Expected identical objects");
        }
    }
}
