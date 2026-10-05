package java.net;

public class URISyntaxException extends Exception {
    private final String input;
    private final int index;

    public URISyntaxException(String input, String reason, int index) {
        super(reason);
        if (input == null || reason == null || index < -1 || index >= input.length()) {
            throw new IllegalArgumentException();
        }
        this.input = input;
        this.index = index;
    }

    public URISyntaxException(String input, String reason) {
        this(input, reason, -1);
    }

    public String getInput() {
        return input;
    }

    public String getReason() {
        return super.getMessage();
    }

    public int getIndex() {
        return index;
    }

    @Override
    public String getMessage() {
        return getReason() + (index < 0 ? "" : " at index " + index) + ": " + input;
    }
}
