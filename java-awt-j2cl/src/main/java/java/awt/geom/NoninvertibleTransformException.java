package java.awt.geom;

public class NoninvertibleTransformException extends Exception {
    private static final long serialVersionUID = 6137225240503990466L;

    public NoninvertibleTransformException(String message) {
        super(message);
    }
}
