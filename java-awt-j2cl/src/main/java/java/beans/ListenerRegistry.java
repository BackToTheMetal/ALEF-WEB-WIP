package java.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

final class ListenerRegistry<L> implements Serializable {
    private static final long serialVersionUID = 1L;
    private final List<L> globalListeners = new ArrayList<>();
    private final Map<String, List<L>> namedListeners = new HashMap<>();

    synchronized void add(L listener) {
        globalListeners.add(listener);
    }

    synchronized void add(String propertyName, L listener) {
        List<L> listeners = namedListeners.get(propertyName);
        if (listeners == null) {
            listeners = new ArrayList<>();
            namedListeners.put(propertyName, listeners);
        }
        listeners.add(listener);
    }

    synchronized void remove(L listener) {
        removeFirst(globalListeners, listener);
    }

    synchronized void remove(String propertyName, L listener) {
        List<L> listeners = namedListeners.get(propertyName);
        if (listeners == null) {
            return;
        }
        removeFirst(listeners, listener);
        if (listeners.isEmpty()) {
            namedListeners.remove(propertyName);
        }
    }

    synchronized List<L> getGlobalListeners() {
        return new ArrayList<>(globalListeners);
    }

    synchronized List<L> getNamedListeners(String propertyName) {
        List<L> listeners = namedListeners.get(propertyName);
        return listeners == null ? new ArrayList<>() : new ArrayList<>(listeners);
    }

    synchronized List<L> getAllListeners() {
        List<L> listeners = new ArrayList<>(globalListeners);
        for (List<L> named : namedListeners.values()) {
            listeners.addAll(named);
        }
        return listeners;
    }

    synchronized boolean hasListeners(String propertyName) {
        if (!globalListeners.isEmpty()) {
            return true;
        }
        List<L> listeners = propertyName == null ? null : namedListeners.get(propertyName);
        return listeners != null && !listeners.isEmpty();
    }

    private static <L> void removeFirst(List<L> listeners, L listener) {
        for (int index = 0; index < listeners.size(); index++) {
            if (listener.equals(listeners.get(index))) {
                listeners.remove(index);
                return;
            }
        }
    }
}
