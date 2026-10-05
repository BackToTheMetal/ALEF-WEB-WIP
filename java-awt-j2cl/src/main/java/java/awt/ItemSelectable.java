package java.awt;

import java.awt.event.ItemListener;

public interface ItemSelectable {
    Object[] getSelectedObjects();
    void addItemListener(ItemListener listener);
    void removeItemListener(ItemListener listener);
}
