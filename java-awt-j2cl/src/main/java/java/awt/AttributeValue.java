package java.awt;

abstract class AttributeValue {
    private final int value;
    private final String[] names;

    protected AttributeValue(int value, String[] names) {
        if (value < 0 || value >= names.length) {
            throw new IllegalArgumentException("value out of range");
        }
        this.value = value;
        this.names = names;
    }

    @Override
    public int hashCode() {
        return value;
    }

    @Override
    public String toString() {
        return names[value];
    }
}
