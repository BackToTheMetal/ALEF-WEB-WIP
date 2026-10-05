package java.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class VetoableChangeSupport implements Serializable {
    private static final long serialVersionUID = 1L;
    private final Object source;
    private final ListenerRegistry<VetoableChangeListener> listeners = new ListenerRegistry<>();

    public VetoableChangeSupport(Object sourceBean) {
        if (sourceBean == null) {
            throw new NullPointerException();
        }
        source = sourceBean;
    }

    public void addVetoableChangeListener(VetoableChangeListener listener) {
        if (listener != null) {
            listeners.add(listener);
        }
    }

    public void removeVetoableChangeListener(VetoableChangeListener listener) {
        if (listener != null) {
            listeners.remove(listener);
        }
    }

    public VetoableChangeListener[] getVetoableChangeListeners() {
        return toArray(listeners.getAllListeners());
    }

    public void addVetoableChangeListener(String propertyName, VetoableChangeListener listener) {
        if (propertyName != null && listener != null) {
            listeners.add(propertyName, listener);
        }
    }

    public void removeVetoableChangeListener(String propertyName, VetoableChangeListener listener) {
        if (propertyName != null && listener != null) {
            listeners.remove(propertyName, listener);
        }
    }

    public VetoableChangeListener[] getVetoableChangeListeners(String propertyName) {
        return propertyName == null ? new VetoableChangeListener[0] : toArray(listeners.getNamedListeners(propertyName));
    }

    public void fireVetoableChange(String propertyName, Object oldValue, Object newValue) throws PropertyVetoException {
        if (oldValue == null || newValue == null || !oldValue.equals(newValue)) {
            fireVetoableChange(new PropertyChangeEvent(source, propertyName, oldValue, newValue));
        }
    }

    public void fireVetoableChange(String propertyName, int oldValue, int newValue) throws PropertyVetoException {
        if (oldValue != newValue) {
            fireVetoableChange(propertyName, Integer.valueOf(oldValue), Integer.valueOf(newValue));
        }
    }

    public void fireVetoableChange(String propertyName, boolean oldValue, boolean newValue) throws PropertyVetoException {
        if (oldValue != newValue) {
            fireVetoableChange(propertyName, Boolean.valueOf(oldValue), Boolean.valueOf(newValue));
        }
    }

    public void fireVetoableChange(PropertyChangeEvent event) throws PropertyVetoException {
        Object oldValue = event.getOldValue();
        Object newValue = event.getNewValue();
        if (oldValue != null && newValue != null && oldValue.equals(newValue)) {
            return;
        }
        List<VetoableChangeListener> eventListeners = new ArrayList<>(listeners.getGlobalListeners());
        String propertyName = event.getPropertyName();
        if (propertyName != null) {
            eventListeners.addAll(listeners.getNamedListeners(propertyName));
        }
        notifyListeners(eventListeners, event, source);
    }

    public boolean hasListeners(String propertyName) {
        return listeners.hasListeners(propertyName);
    }

    private static VetoableChangeListener[] toArray(List<VetoableChangeListener> listeners) {
        return listeners.toArray(new VetoableChangeListener[listeners.size()]);
    }

    private static void notifyListeners(List<VetoableChangeListener> listeners, PropertyChangeEvent event, Object source) throws PropertyVetoException {
        int acceptedCount = 0;
        try {
            while (acceptedCount < listeners.size()) {
                listeners.get(acceptedCount).vetoableChange(event);
                acceptedCount++;
            }
        } catch (PropertyVetoException veto) {
            PropertyChangeEvent rollbackEvent = new PropertyChangeEvent(source, event.getPropertyName(), event.getNewValue(), event.getOldValue());
            for (int index = 0; index < acceptedCount; index++) {
                try {
                    listeners.get(index).vetoableChange(rollbackEvent);
                } catch (PropertyVetoException ignored) {
                    // A rollback veto cannot cancel the rollback.
                }
            }
            throw veto;
        }
    }
}
