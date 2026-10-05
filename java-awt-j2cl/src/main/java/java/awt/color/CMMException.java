package java.awt.color;

/**
 * Signals a failure in a color management operation.
 */
public class CMMException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public CMMException(String message) {
        super(message);
    }
}
