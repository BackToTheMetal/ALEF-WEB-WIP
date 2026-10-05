package java.awt;

public interface LayoutManager {
    void addLayoutComponent(String name, Component component);

    void removeLayoutComponent(Component component);

    Dimension preferredLayoutSize(Container parent);

    Dimension minimumLayoutSize(Container parent);

    void layoutContainer(Container parent);
}
