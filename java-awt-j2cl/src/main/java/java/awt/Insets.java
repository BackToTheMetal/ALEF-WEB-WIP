package java.awt;

import java.io.Serializable;

public class Insets implements Cloneable, Serializable {
    private static final long serialVersionUID = -2272572637695466749L;
    public int top;
    public int left;
    public int bottom;
    public int right;

    public Insets(int top, int left, int bottom, int right) {
        set(top, left, bottom, right);
    }

    public void set(int top, int left, int bottom, int right) {
        this.top = top;
        this.left = left;
        this.bottom = bottom;
        this.right = right;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Insets)) {
            return false;
        }
        Insets insets = (Insets) object;
        return top == insets.top && left == insets.left && bottom == insets.bottom && right == insets.right;
    }

    @Override
    public int hashCode() {
        int firstSum = left + bottom;
        int firstValue = firstSum * (firstSum + 1) / 2 + left;
        int secondSum = right + top;
        int secondValue = secondSum * (secondSum + 1) / 2 + top;
        int finalSum = firstValue + secondValue;
        return finalSum * (finalSum + 1) / 2 + secondValue;
    }

    @Override
    public String toString() {
        return getClass().getName() + "[top=" + top + ",left=" + left + ",bottom=" + bottom + ",right=" + right + "]";
    }

    public Object clone() {
        return new Insets(top, left, bottom, right);
    }
}
