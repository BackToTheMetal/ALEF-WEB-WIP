package java.beans;

public class PropertyVetoException extends Exception {
    private static final long serialVersionUID = 1L;
    private final PropertyChangeEvent event;

    public PropertyVetoException(String message, PropertyChangeEvent event) {
        super(message);
        this.event = event;
    }

    public PropertyChangeEvent getPropertyChangeEvent() {
        return event;
    }
}
