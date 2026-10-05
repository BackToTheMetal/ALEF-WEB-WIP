package org.webawt.samples.dualshowcase;

import java.awt.AWTKeyStroke;
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Canvas;
import java.awt.Checkbox;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.KeyEventDispatcher;
import java.awt.KeyEventPostProcessor;
import java.awt.KeyboardFocusManager;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.Toolkit;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.InputEvent;
import java.awt.event.InputMethodEvent;
import java.awt.event.InputMethodListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.text.AttributedCharacterIterator;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

final class KeyboardInputPage extends Panel {
    private final KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
    private final KeyEventDispatcher dispatcher = this::interceptKey;
    private final KeyEventPostProcessor processor = this::observeKey;
    private final List<String> rows = new ArrayList<>();
    private final TextArea trace = new TextArea("", 12, 100, TextArea.SCROLLBARS_BOTH);
    private final TextField input = new TextField("Cafe \u00e9 / \u6f22\u5b57 / \ud83d\ude42", 26);
    private final Label selection = new Label("Caret and selection appear here");
    private final Label focus = new Label("Focus owner: none");
    private final Checkbox consume = new Checkbox("Consume X and Enter");
    private final Checkbox customTraversal = new Checkbox("F6 / Shift+F6 traversal");
    private final Panel targets = new Panel(new FlowLayout(FlowLayout.LEFT));
    private boolean disposed;
    private int sequence;
    private int actions;
    private int bindings;
    private final InputCanvas canvas = new InputCanvas();

    KeyboardInputPage() {
        super(new BorderLayout(6, 6));
        setName("keyboardPage");
        Panel header = new Panel(new GridLayout(0, 1, 3, 3));
        header.add(new Label("Compare keys, characters, focus and editing. Try Shift, punctuation, F-keys, numpad, dead keys and your IME."));
        header.add(new Label("Application binding: " + shortcutName() + "+Shift+K. Menu action: " + shortcutName() + "+Shift+M (Actions menu)."));
        header.add(createTargets());
        header.add(createTools());
        header.add(selection);
        header.add(focus);
        add(header, BorderLayout.NORTH);
        trace.setName("keyboardTrace");
        trace.setEditable(false);
        trace.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        add(trace, BorderLayout.CENTER);
        canvas.setName("inputCanvas");
        canvas.setPreferredSize(new Dimension(800, 150));
        watchFocus(canvas);
        add(canvas, BorderLayout.SOUTH);
        manager.addKeyEventDispatcher(dispatcher);
        manager.addKeyEventPostProcessor(processor);
        refreshSelection();
    }

    void dispose() {
        disposed = true;
        manager.removeKeyEventDispatcher(dispatcher);
        manager.removeKeyEventPostProcessor(processor);
    }

    String traceText() {
        return trace.getText();
    }

    void paintNow() {
        if (disposed || !canvas.isShowing()) {
            return;
        }
        Graphics graphics = canvas.getGraphics();
        if (graphics != null) {
            try {
                canvas.update(graphics);
            } finally {
                graphics.dispose();
            }
        }
    }

    String selectionState() {
        return input.getSelectionStart() + ":" + input.getSelectionEnd() + ":" + input.getCaretPosition();
    }

    void menuAction() {
        append("ACTION menu shortcut");
    }

    private Panel createTargets() {
        input.setName("keyInput");
        watchFocus(input);
        input.addActionListener(event -> append("ACTION Enter count=" + ++actions));
        input.addTextListener(event -> {
            append("TEXT length=" + input.getText().length());
            refreshSelection();
        });
        input.addInputMethodListener(new InputMethodListener() {
            @Override
            public void inputMethodTextChanged(InputMethodEvent event) {
                String text = compositionText(event.getText());
                append("IME text=" + text + " committed=" + event.getCommittedCharacterCount());
            }
            @Override
            public void caretPositionChanged(InputMethodEvent event) {
                append("IME caret=" + event.getCaret());
            }
        });
        targets.add(input);
        TextField password = new TextField("sample", 10);
        password.setEchoChar('*');
        password.setName("passwordInput");
        watchFocus(password);
        targets.add(password);
        Button disabled = new Button("Disabled target");
        disabled.setName("disabledTarget");
        disabled.setEnabled(false);
        targets.add(disabled);
        Button optional = new Button("Optional target");
        optional.setName("optionalTarget");
        watchFocus(optional);
        optional.addActionListener(event -> append("ACTION optional target"));
        targets.add(optional);
        Checkbox hide = new Checkbox("Hide optional");
        hide.addItemListener(event -> {
            optional.setVisible(!hide.getState());
            validate();
        });
        targets.add(hide);
        return targets;
    }

    private Panel createTools() {
        Panel tools = new Panel(new FlowLayout(FlowLayout.LEFT));
        Button select = new Button("Select sample");
        select.addActionListener(event -> {
            input.select(0, Math.min(4, input.getText().length()));
            input.requestFocus();
            refreshSelection();
        });
        Button caret = new Button("Caret to end");
        caret.addActionListener(event -> {
            input.setCaretPosition(input.getText().length());
            input.requestFocus();
            refreshSelection();
        });
        Button inspect = new Button("Inspect selection");
        inspect.addActionListener(event -> refreshSelection());
        Button clear = new Button("Clear input log");
        clear.addActionListener(event -> {
            rows.clear();
            trace.setText("");
            sequence = 0;
        });
        customTraversal.addItemListener(event -> configureTraversal());
        tools.add(select);
        tools.add(caret);
        tools.add(inspect);
        tools.add(consume);
        tools.add(customTraversal);
        tools.add(clear);
        return tools;
    }

    private void configureTraversal() {
        for (Component target : targets.getComponents()) {
            configureTraversal(target, KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, 0);
            configureTraversal(target, KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, InputEvent.SHIFT_DOWN_MASK);
        }
        append("TRAVERSAL F6=" + customTraversal.getState());
    }

    private void configureTraversal(Component target, int identifier, int modifiers) {
        Set<AWTKeyStroke> keys = new LinkedHashSet<>(manager.getDefaultFocusTraversalKeys(identifier));
        if (customTraversal.getState()) {
            keys.add(AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_F6, modifiers));
        }
        target.setFocusTraversalKeys(identifier, keys);
    }

    private boolean interceptKey(KeyEvent event) {
        if (!belongsHere(event.getComponent()) || event.getID() != KeyEvent.KEY_PRESSED) {
            return false;
        }
        int bindingMask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx() | InputEvent.SHIFT_DOWN_MASK;
        if (event.getKeyCode() == KeyEvent.VK_K && event.getModifiersEx() == bindingMask) {
            event.consume();
            append("ACTION application binding count=" + ++bindings);
        } else if (consume.getState() && (event.getKeyCode() == KeyEvent.VK_X || event.getKeyCode() == KeyEvent.VK_ENTER)) {
            event.consume();
        }
        if (event.isConsumed()) {
            logKey(event);
        }
        return event.isConsumed();
    }

    private boolean observeKey(KeyEvent event) {
        if (belongsHere(event.getComponent())) {
            EventQueue.invokeLater(() -> {
                if (!disposed) {
                    logKey(event);
                    refreshSelection();
                }
            });
        }
        return false;
    }

    private boolean belongsHere(Component component) {
        if (disposed || component == trace || !isShowing()) {
            return false;
        }
        for (Component current = component; current != null; current = current.getParent()) {
            if (current == this) {
                return true;
            }
        }
        return false;
    }

    private void logKey(KeyEvent event) {
        String type = event.getID() == KeyEvent.KEY_PRESSED ? "PRESSED" : event.getID() == KeyEvent.KEY_RELEASED ? "RELEASED" : "TYPED";
        String key = event.getKeyCode() == 0 ? "Undefined" : KeyEvent.getKeyText(event.getKeyCode());
        String character = event.getKeyChar() == KeyEvent.CHAR_UNDEFINED ? "undefined" : "U+" + Integer.toHexString(event.getKeyChar());
        append(type + " target=" + event.getComponent().getName() + " code=" + event.getKeyCode() + " key=" + key + " char=" + character + " modifiers=" + InputEvent.getModifiersExText(event.getModifiersEx()) + " location=" + event.getKeyLocation() + " consumed=" + event.isConsumed());
    }

    private void watchFocus(Component component) {
        component.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent event) {
                focus.setText("Focus owner: " + component.getName());
                append("FOCUS gained target=" + component.getName());
                refreshSelection();
            }
            @Override
            public void focusLost(FocusEvent event) {
                focus.setText("Focus left: " + component.getName());
                append("FOCUS lost target=" + component.getName());
            }
        });
    }

    private void refreshSelection() {
        selection.setText("Selection " + input.getSelectionStart() + ".." + input.getSelectionEnd() + " | caret " + input.getCaretPosition() + " | UTF-16 length " + input.getText().length());
    }

    private void append(String row) {
        if (disposed) {
            return;
        }
        rows.add(++sequence + " " + row);
        if (rows.size() > 200) {
            rows.remove(0);
        }
        StringBuilder text = new StringBuilder();
        for (String value : rows) {
            text.append(value).append('\n');
        }
        trace.setText(text.toString());
        trace.setCaretPosition(trace.getText().length());
    }

    private static String shortcutName() {
        return InputEvent.getModifiersExText(Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx());
    }

    private static String compositionText(AttributedCharacterIterator iterator) {
        StringBuilder text = new StringBuilder();
        if (iterator != null) {
            for (char character = iterator.first(); character != AttributedCharacterIterator.DONE; character = iterator.next()) {
                text.append(character);
            }
        }
        return text.toString();
    }

    private final class InputCanvas extends Canvas {
        private int pointerX = 40;
        private int pointerY = 80;

        private InputCanvas() {
            setFocusable(true);
            setBackground(new Color(225, 239, 250));
            MouseAdapter pointer = new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent event) {
                    requestFocus();
                    recordPointer("pressed", event);
                }
                @Override
                public void mouseReleased(MouseEvent event) {
                    recordPointer("released", event);
                }
                @Override
                public void mouseClicked(MouseEvent event) {
                    recordPointer("clicked", event);
                }
                @Override
                public void mouseMoved(MouseEvent event) {
                    recordPointer("moved", event);
                }
                @Override
                public void mouseDragged(MouseEvent event) {
                    recordPointer("dragged", event);
                }
                @Override
                public void mouseWheelMoved(MouseWheelEvent event) {
                    event.consume();
                    append("WHEEL rotation=" + event.getPreciseWheelRotation() + " consumed=true");
                }
            };
            addMouseListener(pointer);
            addMouseMotionListener(pointer);
            addMouseWheelListener(pointer);
        }

        private void recordPointer(String type, MouseEvent event) {
            pointerX = event.getX();
            pointerY = event.getY();
            append("MOUSE " + type + " x=" + pointerX + " y=" + pointerY + " button=" + event.getButton() + " clicks=" + event.getClickCount() + " modifiers=" + InputEvent.getModifiersExText(event.getModifiersEx()));
            repaint();
        }

        @Override
        public void paint(Graphics graphics) {
            graphics.setColor(getBackground());
            graphics.fillRect(0, 0, getWidth(), getHeight());
            graphics.setColor(new Color(20, 70, 110));
            graphics.drawString("Focusable canvas: click, drag, double-click or scroll; then type keys. Wheel events are consumed.", 15, 25);
            graphics.fillOval(pointerX - 5, pointerY - 5, 10, 10);
        }
    }
}
