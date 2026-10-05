package java.awt;

import java.io.Serializable;

public class CheckboxGroup implements Serializable {
    private static final long serialVersionUID = 1L;
    private static int nextPeerId;
    private transient String peerName;
    private Checkbox selectedCheckbox;

    public CheckboxGroup() {
    }

    String getPeerName() {
        if (peerName == null) {
            peerName = "webawt-checkbox-group-" + nextPeerId++;
        }
        return peerName;
    }

    public Checkbox getSelectedCheckbox() {
        return selectedCheckbox;
    }

    public Checkbox getCurrent() {
        return getSelectedCheckbox();
    }

    public void setSelectedCheckbox(Checkbox checkbox) {
        if (checkbox != null && checkbox.getCheckboxGroup() != this) {
            return;
        }
        Checkbox previous = selectedCheckbox;
        selectedCheckbox = checkbox;
        if (previous != null && previous != checkbox) {
            previous.setStateFromGroup(false);
        }
        if (checkbox != null && !checkbox.getState()) {
            checkbox.setStateFromGroup(true);
        }
    }

    public synchronized void setCurrent(Checkbox checkbox) {
        setSelectedCheckbox(checkbox);
    }

    @Override
    public String toString() {
        return getClass().getName() + "[selectedCheckbox=" + selectedCheckbox + "]";
    }
}
