package java.awt;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CardLayout implements LayoutManager2, Serializable {
    private static final long serialVersionUID = 1L;
    private final List<Card> cards = new ArrayList<>();
    private int horizontalGap;
    private int verticalGap;

    public CardLayout() {
        this(0, 0);
    }

    public CardLayout(int horizontalGap, int verticalGap) {
        this.horizontalGap = horizontalGap;
        this.verticalGap = verticalGap;
    }

    public int getHgap() {
        return horizontalGap;
    }

    public void setHgap(int horizontalGap) {
        this.horizontalGap = horizontalGap;
    }

    public int getVgap() {
        return verticalGap;
    }

    public void setVgap(int verticalGap) {
        this.verticalGap = verticalGap;
    }

    @Override
    public void addLayoutComponent(Component component, Object constraints) {
        synchronized (component.getTreeLock()) {
            Object name = constraints == null ? "" : constraints;
            if (!(name instanceof String)) {
                throw new IllegalArgumentException("cannot add to layout: constraint must be a string");
            }
            addLayoutComponent((String) name, component);
        }
    }

    @Deprecated
    @Override
    public void addLayoutComponent(String name, Component component) {
        synchronized (component.getTreeLock()) {
            if (!cards.isEmpty()) {
                component.setVisible(false);
            }
            for (Card card : cards) {
                if (card.name.equals(name)) {
                    card.component = component;
                    return;
                }
            }
            cards.add(new Card(name, component));
        }
    }

    @Override
    public void removeLayoutComponent(Component component) {
        synchronized (component.getTreeLock()) {
            for (int index = 0; index < cards.size(); index++) {
                Card card = cards.get(index);
                if (card.component != component) {
                    continue;
                }
                Container parent = component.getParent();
                if (component.isVisible() && parent != null) {
                    next(parent);
                }
                cards.remove(index);
                return;
            }
        }
    }

    @Override
    public Dimension preferredLayoutSize(Container parent) {
        synchronized (parent.getTreeLock()) {
            return calculateLayoutSize(parent, true);
        }
    }

    @Override
    public Dimension minimumLayoutSize(Container parent) {
        synchronized (parent.getTreeLock()) {
            return calculateLayoutSize(parent, false);
        }
    }

    @Override
    public Dimension maximumLayoutSize(Container target) {
        return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    @Override
    public float getLayoutAlignmentX(Container target) {
        return 0.5f;
    }

    @Override
    public float getLayoutAlignmentY(Container target) {
        return 0.5f;
    }

    @Override
    public void invalidateLayout(Container target) {
        // Card layout does not cache measurements.
    }

    @Override
    public void layoutContainer(Container parent) {
        synchronized (parent.getTreeLock()) {
            Insets insets = parent.getInsets();
            boolean visibleComponentFound = false;
            int width = parent.getWidth() - horizontalGap * 2 - insets.left - insets.right;
            int height = parent.getHeight() - verticalGap * 2 - insets.top - insets.bottom;
            for (int index = 0; index < parent.getComponentCount(); index++) {
                Component component = parent.getComponent(index);
                component.setBounds(horizontalGap + insets.left, verticalGap + insets.top, width, height);
                if (component.isVisible()) {
                    visibleComponentFound = true;
                }
            }
            if (!visibleComponentFound && parent.getComponentCount() > 0) {
                parent.getComponent(0).setVisible(true);
            }
        }
    }

    public void first(Container parent) {
        synchronized (parent.getTreeLock()) {
            checkLayout(parent);
            hideVisibleComponent(parent);
            if (parent.getComponentCount() > 0) {
                parent.getComponent(0).setVisible(true);
                parent.validate();
            }
        }
    }

    public void next(Container parent) {
        synchronized (parent.getTreeLock()) {
            checkLayout(parent);
            int componentCount = parent.getComponentCount();
            for (int index = 0; index < componentCount; index++) {
                Component component = parent.getComponent(index);
                if (component.isVisible()) {
                    component.setVisible(false);
                    parent.getComponent((index + 1) % componentCount).setVisible(true);
                    parent.validate();
                    return;
                }
            }
            showDefaultComponent(parent);
        }
    }

    public void previous(Container parent) {
        synchronized (parent.getTreeLock()) {
            checkLayout(parent);
            int componentCount = parent.getComponentCount();
            for (int index = 0; index < componentCount; index++) {
                Component component = parent.getComponent(index);
                if (component.isVisible()) {
                    component.setVisible(false);
                    parent.getComponent(index == 0 ? componentCount - 1 : index - 1).setVisible(true);
                    parent.validate();
                    return;
                }
            }
            showDefaultComponent(parent);
        }
    }

    public void last(Container parent) {
        synchronized (parent.getTreeLock()) {
            checkLayout(parent);
            hideVisibleComponent(parent);
            int componentCount = parent.getComponentCount();
            if (componentCount > 0) {
                parent.getComponent(componentCount - 1).setVisible(true);
                parent.validate();
            }
        }
    }

    public void show(Container parent, String name) {
        synchronized (parent.getTreeLock()) {
            checkLayout(parent);
            Component nextComponent = null;
            for (int index = 0; index < cards.size(); index++) {
                Card card = cards.get(index);
                if (card.name.equals(name)) {
                    nextComponent = card.component;
                    break;
                }
            }
            if (nextComponent == null || nextComponent.isVisible()) {
                return;
            }
            hideVisibleComponent(parent);
            nextComponent.setVisible(true);
            parent.validate();
        }
    }

    @Override
    public String toString() {
        return getClass().getName() + "[hgap=" + horizontalGap + ",vgap=" + verticalGap + "]";
    }

    private Dimension calculateLayoutSize(Container parent, boolean preferred) {
        int width = 0;
        int height = 0;
        for (int index = 0; index < parent.getComponentCount(); index++) {
            Component component = parent.getComponent(index);
            Dimension size = preferred ? component.getPreferredSize() : component.getMinimumSize();
            width = Math.max(width, size.width);
            height = Math.max(height, size.height);
        }
        Insets insets = parent.getInsets();
        return new Dimension(insets.left + insets.right + width + horizontalGap * 2, insets.top + insets.bottom + height + verticalGap * 2);
    }

    private void checkLayout(Container parent) {
        if (parent.getLayout() != this) {
            throw new IllegalArgumentException("wrong parent for CardLayout");
        }
    }

    private void hideVisibleComponent(Container parent) {
        for (int index = 0; index < parent.getComponentCount(); index++) {
            Component component = parent.getComponent(index);
            if (component.isVisible()) {
                component.setVisible(false);
                return;
            }
        }
    }

    private void showDefaultComponent(Container parent) {
        if (parent.getComponentCount() > 0) {
            parent.getComponent(0).setVisible(true);
            parent.validate();
        }
    }

    private static final class Card implements Serializable {
        private static final long serialVersionUID = 1L;
        private final String name;
        private Component component;

        private Card(String name, Component component) {
            this.name = name;
            this.component = component;
        }
    }
}
