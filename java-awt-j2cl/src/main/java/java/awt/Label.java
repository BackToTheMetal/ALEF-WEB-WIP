package java.awt;

import java.util.Locale;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleState;
import javax.accessibility.AccessibleStateSet;

public class Label extends Component implements Accessible {
    public static final int LEFT = 0;
    public static final int CENTER = 1;
    public static final int RIGHT = 2;
    private int alignment;
    private String text;

    public Label() throws HeadlessException {
        this("", LEFT);
    }

    public Label(String text) throws HeadlessException {
        this(text, LEFT);
    }

    public Label(String text, int alignment) throws HeadlessException {
        super("span");
        this.text = text;
        setAlignment(alignment);
    }

    public int getAlignment() {
        return alignment;
    }

    public synchronized void setAlignment(int alignment) {
        if (alignment != LEFT && alignment != CENTER && alignment != RIGHT) {
            throw new IllegalArgumentException("improper alignment: " + alignment);
        }
        this.alignment = alignment;
        if (getPeer() != null) {
            getPeer().setTextAlignment(alignment);
        }
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
        updatePeerText(text);
        invalidate();
    }

    @Override
    public void addNotify() {
        super.addNotify();
        updatePeerText(text);
        getPeer().setTextAlignment(alignment);
    }

    @Override
    public Dimension getMinimumSize() {
        if (isMinimumSizeSet()) {
            return super.getMinimumSize();
        }
        FontMetrics metrics = getFontMetrics(effectiveFont(getFont()));
        return new Dimension(metrics.stringWidth(text == null ? "" : text) + 4, metrics.getHeight() + 4);
    }

    @Override
    public Dimension getPreferredSize() {
        return isPreferredSizeSet() ? super.getPreferredSize() : getMinimumSize();
    }

    @Override
    public AccessibleContext getAccessibleContext() {
        if (accessibleContext == null) {
            accessibleContext = new AccessibleAWTLabel();
        }
        return accessibleContext;
    }

    protected class AccessibleAWTLabel extends AccessibleAWTComponent {
        public AccessibleAWTLabel() {}
        @Override
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.LABEL;
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
            return getParent() == null ? -1 : getParent().getComponentZOrder(Label.this);
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
            return Label.this.getLocale();
        }
    }
}
