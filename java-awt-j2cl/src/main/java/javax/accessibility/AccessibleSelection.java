package javax.accessibility;

public interface AccessibleSelection {
    int getAccessibleSelectionCount();

    Accessible getAccessibleSelection(int index);

    boolean isAccessibleChildSelected(int index);

    void addAccessibleSelection(int index);

    void removeAccessibleSelection(int index);

    void clearAccessibleSelection();

    void selectAllAccessibleSelection();
}
