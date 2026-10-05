package javax.accessibility;

import java.util.Vector;

public class AccessibleStateSet {
    protected Vector<AccessibleState> states;

    public AccessibleStateSet() {
    }

    public AccessibleStateSet(AccessibleState[] states) {
        addAll(states);
    }

    public boolean add(AccessibleState state) {
        if (states == null) {
            states = new Vector<>();
        }
        if (states.contains(state)) {
            return false;
        }
        states.addElement(state);
        return true;
    }

    public void addAll(AccessibleState[] states) {
        for (AccessibleState state : states) {
            add(state);
        }
    }

    public boolean remove(AccessibleState state) {
        return states != null && states.removeElement(state);
    }

    public void clear() {
        if (states != null) {
            states.removeAllElements();
        }
    }

    public boolean contains(AccessibleState state) {
        return states != null && states.contains(state);
    }

    public AccessibleState[] toArray() {
        if (states == null) {
            return new AccessibleState[0];
        }
        AccessibleState[] result = new AccessibleState[states.size()];
        for (int index = 0; index < result.length; index++) {
            result[index] = states.elementAt(index);
        }
        return result;
    }

    @Override
    public String toString() {
        if (states == null || states.isEmpty()) {
            return null;
        }
        StringBuilder result = new StringBuilder(states.elementAt(0).toDisplayString());
        for (int index = 1; index < states.size(); index++) {
            result.append(',').append(states.elementAt(index).toDisplayString());
        }
        return result.toString();
    }
}
