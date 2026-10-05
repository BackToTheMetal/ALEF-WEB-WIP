package java.awt.desktop;

public final class UserSessionEvent extends AppEvent {
    public enum Reason {
        UNSPECIFIED,
        CONSOLE,
        REMOTE,
        LOCK
    }

    private final Reason reason;

    public UserSessionEvent(Reason reason) {
        if (reason == null) {
            throw new NullPointerException("reason");
        }
        this.reason = reason;
    }

    public Reason getReason() {
        return reason;
    }
}
