package java.awt;

import java.util.Locale;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleState;
import javax.accessibility.AccessibleStateSet;

public class Panel extends Container implements Accessible {

    public Panel() {
        this(new FlowLayout());
    }

    public Panel(LayoutManager layout) {
        setLayout(layout);
    }

    @Override
    public AccessibleContext getAccessibleContext() {
        if (accessibleContext == null) {
            accessibleContext = new AccessibleAWTPanel();
        }
        return accessibleContext;
    }

    protected class AccessibleAWTPanel extends AccessibleAWTContainer {
        @Override
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.PANEL;
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
            return getParent() == null ? -1 : getParent().getComponentZOrder(Panel.this);
        }

        @Override
        public int getAccessibleChildrenCount() {
            return getComponentCount();
        }

        @Override
        public Accessible getAccessibleChild(int index) {
            Component child = getComponent(index);
            return child instanceof Accessible ? (Accessible) child : null;
        }

        @Override
        public Locale getLocale() {
            return Panel.this.getLocale();
        }
    }
}
