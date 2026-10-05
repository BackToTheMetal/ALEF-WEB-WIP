package java.awt.dnd;

public class InvalidDnDOperationException extends IllegalStateException {
    public InvalidDnDOperationException() {
    }

    public InvalidDnDOperationException(String message) {
        super(message);
    }
}
