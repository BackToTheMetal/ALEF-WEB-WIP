package javax.accessibility;

public interface AccessibleTable {
    Accessible getAccessibleCaption();

    void setAccessibleCaption(Accessible caption);

    Accessible getAccessibleSummary();

    void setAccessibleSummary(Accessible summary);

    int getAccessibleRowCount();

    int getAccessibleColumnCount();

    Accessible getAccessibleAt(int row, int column);

    int getAccessibleRowExtentAt(int row, int column);

    int getAccessibleColumnExtentAt(int row, int column);

    AccessibleTable getAccessibleRowHeader();

    void setAccessibleRowHeader(AccessibleTable table);

    AccessibleTable getAccessibleColumnHeader();

    void setAccessibleColumnHeader(AccessibleTable table);

    Accessible getAccessibleRowDescription(int row);

    void setAccessibleRowDescription(int row, Accessible description);

    Accessible getAccessibleColumnDescription(int column);

    void setAccessibleColumnDescription(int column, Accessible description);

    boolean isAccessibleSelected(int row, int column);

    boolean isAccessibleRowSelected(int row);

    boolean isAccessibleColumnSelected(int column);

    int[] getSelectedAccessibleRows();

    int[] getSelectedAccessibleColumns();
}
