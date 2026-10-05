package java.awt;

public interface MenuContainer {
    Font getFont();

    void remove(MenuComponent component);

    boolean postEvent(Event event);
}
