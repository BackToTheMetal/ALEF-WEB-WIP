package java.awt;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;

public class TextField extends TextComponent {
    private final List<ActionListener> actionListeners = new ArrayList<>();
    private char echoChar;
    private int columns;

    public TextField() throws HeadlessException {
        this("", 0);
    }

    public TextField(String text) throws HeadlessException {
        this(text, text == null ? 0 : text.length());
    }

    public TextField(int columns) throws HeadlessException {
        this("", columns);
    }

    public TextField(String text, int columns) throws HeadlessException {
        super("input", text);
        if (columns < 0) {
            throw new IllegalArgumentException("columns less than zero");
        }
        this.columns = columns;
    }

    public char getEchoChar() {
        return echoChar;
    }

    public void setEchoChar(char echoChar) {
        this.echoChar = echoChar;
        updatePeerPassword(echoChar != 0);
    }

    public synchronized void setEchoCharacter(char echoChar) {
        setEchoChar(echoChar);
    }

    public boolean echoCharIsSet() {
        return echoChar != 0;
    }

    public int getColumns() {
        return columns;
    }

    public void setColumns(int columns) {
        if (columns < 0) {
            throw new IllegalArgumentException("columns less than zero");
        }
        this.columns = columns;
        invalidate();
    }

    public Dimension getPreferredSize(int columns) {
        return new Dimension(16 + Math.max(0, columns) * 8, 24);
    }

    public Dimension preferredSize(int columns) {
        return getPreferredSize(columns);
    }

    @Override
    public Dimension getPreferredSize() {
        return isPreferredSizeSet() ? super.getPreferredSize() : getPreferredSize(columns);
    }

    public Dimension getMinimumSize(int columns) {
        return getPreferredSize(columns);
    }

    public Dimension minimumSize(int columns) {
        return getMinimumSize(columns);
    }

    @Override
    public Dimension getMinimumSize() {
        return isMinimumSizeSet() ? super.getMinimumSize() : getMinimumSize(columns);
    }

    public synchronized void addActionListener(ActionListener listener) {
        if (listener != null) {
            actionListeners.add(listener);
        }
    }

    public synchronized void removeActionListener(ActionListener listener) {
        actionListeners.remove(listener);
    }

    public synchronized ActionListener[] getActionListeners() {
        return actionListeners.toArray(new ActionListener[0]);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends EventListener> T[] getListeners(Class<T> listenerType) {
        return listenerType == ActionListener.class ? (T[]) getActionListeners() : super.getListeners(listenerType);
    }

    @Override
    protected boolean eventEnabled(AWTEvent event) {
        if (event.getID() == ActionEvent.ACTION_PERFORMED && !actionListeners.isEmpty()) {
            return true;
        }
        return super.eventEnabled(event);
    }

    @Override
    protected void processEvent(AWTEvent event) {
        if (event instanceof ActionEvent) {
            processActionEvent((ActionEvent) event);
        } else {
            super.processEvent(event);
        }
    }

    protected void processActionEvent(ActionEvent event) {
        for (ActionListener listener : getActionListeners()) {
            listener.actionPerformed(event);
        }
    }

    @Override
    public void addNotify() {
        super.addNotify();
        updatePeerPassword(echoChar != 0);
        updatePeerActionHandler(this::dispatchAction);
    }

    private void dispatchAction() {
        ActionEvent event = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, getText());
        dispatchEvent(event);
    }
}
