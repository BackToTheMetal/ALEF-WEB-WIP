package java.text;

public class Annotation {
    private final Object value;

    public Annotation(Object value) {
        this.value = value;
    }

    public Object getValue() {
        return value;
    }

    @Override
    public String toString() {
        return getClass().getName() + "[value=" + value + "]";
    }
}
