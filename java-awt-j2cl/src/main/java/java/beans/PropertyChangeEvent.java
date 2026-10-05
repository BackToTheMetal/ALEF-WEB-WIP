package java.beans;

import java.util.EventObject;

public class PropertyChangeEvent extends EventObject {
    private static final long serialVersionUID = 1L;
    private final String propertyName;
    private final Object oldValue;
    private final Object newValue;
    private Object propagationId;

    public PropertyChangeEvent(Object source, String propertyName, Object oldValue, Object newValue) {
        super(source);
        this.propertyName = propertyName;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public Object getNewValue() {
        return newValue;
    }

    public Object getOldValue() {
        return oldValue;
    }

    public void setPropagationId(Object propagationId) {
        this.propagationId = propagationId;
    }

    public Object getPropagationId() {
        return propagationId;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(getClass().getName());
        builder.append("[propertyName=").append(getPropertyName());
        appendTo(builder);
        builder.append("; oldValue=").append(getOldValue());
        builder.append("; newValue=").append(getNewValue());
        builder.append("; propagationId=").append(getPropagationId());
        builder.append("; source=").append(getSource());
        return builder.append(']').toString();
    }

    void appendTo(StringBuilder builder) {
    }
}
