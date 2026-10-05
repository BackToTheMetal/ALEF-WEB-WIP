package java.beans;

import java.io.Serializable;
import java.util.List;

public class PropertyChangeSupport implements Serializable {
    private static final long serialVersionUID = 1L;
    private final Object source;
    private final ListenerRegistry<PropertyChangeListener> listeners = new ListenerRegistry<>();

    public PropertyChangeSupport(Object sourceBean) {
        if (sourceBean == null) {
            throw new NullPointerException();
        }
        source = sourceBean;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        if (listener != null) {
            listeners.add(listener);
        }
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        if (listener != null) {
            listeners.remove(listener);
        }
    }

    public PropertyChangeListener[] getPropertyChangeListeners() {
        return toArray(listeners.getAllListeners());
    }

    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        if (propertyName != null && listener != null) {
            listeners.add(propertyName, listener);
        }
    }

    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        if (propertyName != null && listener != null) {
            listeners.remove(propertyName, listener);
        }
    }

    public PropertyChangeListener[] getPropertyChangeListeners(String propertyName) {
        return propertyName == null ? new PropertyChangeListener[0] : toArray(listeners.getNamedListeners(propertyName));
    }

    public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        if (oldValue == null || newValue == null || !oldValue.equals(newValue)) {
            firePropertyChange(new PropertyChangeEvent(source, propertyName, oldValue, newValue));
        }
    }

    public void firePropertyChange(String propertyName, int oldValue, int newValue) {
        if (oldValue != newValue) {
            firePropertyChange(propertyName, Integer.valueOf(oldValue), Integer.valueOf(newValue));
        }
    }

    public void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {
        if (oldValue != newValue) {
            firePropertyChange(propertyName, Boolean.valueOf(oldValue), Boolean.valueOf(newValue));
        }
    }

    public void firePropertyChange(PropertyChangeEvent event) {
        Object oldValue = event.getOldValue();
        Object newValue = event.getNewValue();
        if (oldValue != null && newValue != null && oldValue.equals(newValue)) {
            return;
        }
        List<PropertyChangeListener> globalListeners = listeners.getGlobalListeners();
        String propertyName = event.getPropertyName();
        List<PropertyChangeListener> namedListeners = propertyName == null ? null : listeners.getNamedListeners(propertyName);
        notifyListeners(globalListeners, event);
        if (namedListeners != null) {
            notifyListeners(namedListeners, event);
        }
    }

    public void fireIndexedPropertyChange(String propertyName, int index, Object oldValue, Object newValue) {
        if (oldValue == null || newValue == null || !oldValue.equals(newValue)) {
            firePropertyChange(new IndexedPropertyChangeEvent(source, propertyName, oldValue, newValue, index));
        }
    }

    public void fireIndexedPropertyChange(String propertyName, int index, int oldValue, int newValue) {
        if (oldValue != newValue) {
            fireIndexedPropertyChange(propertyName, index, Integer.valueOf(oldValue), Integer.valueOf(newValue));
        }
    }

    public void fireIndexedPropertyChange(String propertyName, int index, boolean oldValue, boolean newValue) {
        if (oldValue != newValue) {
            fireIndexedPropertyChange(propertyName, index, Boolean.valueOf(oldValue), Boolean.valueOf(newValue));
        }
    }

    public boolean hasListeners(String propertyName) {
        return listeners.hasListeners(propertyName);
    }

    private static PropertyChangeListener[] toArray(List<PropertyChangeListener> listeners) {
        return listeners.toArray(new PropertyChangeListener[listeners.size()]);
    }

    private static void notifyListeners(List<PropertyChangeListener> listeners, PropertyChangeEvent event) {
        for (PropertyChangeListener listener : listeners) {
            listener.propertyChange(event);
        }
    }
}
