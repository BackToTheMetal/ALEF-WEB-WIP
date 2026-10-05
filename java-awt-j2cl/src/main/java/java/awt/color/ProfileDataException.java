package java.awt.color;

/**
 * Signals invalid color profile data.
 */
public class ProfileDataException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public ProfileDataException(String message) {
        super(message);
    }
}
