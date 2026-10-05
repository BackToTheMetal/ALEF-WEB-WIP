package java.awt;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;
import java.util.Locale;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleAction;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleState;
import javax.accessibility.AccessibleStateSet;

public class Button extends Component implements Accessible {
    private final List<ActionListener> actionListeners = new ArrayList<>();
    private String actionCommand;
    private String label;

    public Button() throws HeadlessException {
        this("");
    }

    public Button(String label) throws HeadlessException {
        super("button");
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
        updatePeerText(label);
        invalidate();
    }

    public void setActionCommand(String actionCommand) {
        this.actionCommand = actionCommand;
    }

    public String getActionCommand() {
        return actionCommand == null ? label : actionCommand;
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
        updatePeerText(label);
        updatePeerActionHandler(this::dispatchAction);
    }

    @Override
    public Dimension getMinimumSize() {
        if (isMinimumSizeSet()) {
            return super.getMinimumSize();
        }
        int characters = label == null ? 0 : label.length();
        return new Dimension(16 + characters * 8, 24);
    }

    @Override
    public Dimension getPreferredSize() {
        return isPreferredSizeSet() ? super.getPreferredSize() : getMinimumSize();
    }

    @Override
    public AccessibleContext getAccessibleContext() {
        if (accessibleContext == null) {
            accessibleContext = new AccessibleAWTButton();
        }
        return accessibleContext;
    }

    protected class AccessibleAWTButton extends AccessibleAWTComponent implements AccessibleAction, javax.accessibility.AccessibleValue {
        public Number getCurrentAccessibleValue() { return 0; }
        public boolean setCurrentAccessibleValue(Number value) { return false; }
        public Number getMinimumAccessibleValue() { return 0; }
        public Number getMaximumAccessibleValue() { return 0; }
        public javax.accessibility.AccessibleValue getAccessibleValue() { return this; }
        @Override
        public String getAccessibleName() {
            return accessibleName == null ? label : accessibleName;
        }

        @Override
        public AccessibleAction getAccessibleAction() {
            return this;
        }

        @Override
        public int getAccessibleActionCount() {
            return 1;
        }

        @Override
        public String getAccessibleActionDescription(int index) {
            return index == 0 ? AccessibleAction.CLICK : null;
        }

        @Override
        public boolean doAccessibleAction(int index) {
            if (index != 0 || !isEnabled()) {
                return false;
            }
            dispatchAction();
            return true;
        }

        @Override
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.PUSH_BUTTON;
        }

        @Override
        public AccessibleStateSet getAccessibleStateSet() {
            AccessibleStateSet states = new AccessibleStateSet();
            if (isEnabled()) {
                states.add(AccessibleState.ENABLED);
            }
            if (isVisible()) {
                states.add(AccessibleState.VISIBLE);
            }
            return states;
        }

        @Override
        public int getAccessibleIndexInParent() {
            return getParent() == null ? -1 : getParent().getComponentZOrder(Button.this);
        }

        @Override
        public int getAccessibleChildrenCount() {
            return 0;
        }

        @Override
        public Accessible getAccessibleChild(int index) {
            return null;
        }

        @Override
        public Locale getLocale() {
            return Button.this.getLocale();
        }
    }

    private void dispatchAction() {
        ActionEvent event = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, getActionCommand());
        dispatchEvent(event);
    }
}
