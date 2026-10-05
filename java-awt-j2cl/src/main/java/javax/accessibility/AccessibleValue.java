package javax.accessibility;

public interface AccessibleValue {
    Number getCurrentAccessibleValue();

    boolean setCurrentAccessibleValue(Number value);

    Number getMinimumAccessibleValue();

    Number getMaximumAccessibleValue();
}
