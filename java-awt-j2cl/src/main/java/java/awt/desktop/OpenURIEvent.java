package java.awt.desktop;

import java.net.URI;

public final class OpenURIEvent extends AppEvent {
    private final URI uri;

    public OpenURIEvent(URI uri) {
        if (uri == null) {
            throw new NullPointerException("uri");
        }
        this.uri = uri;
    }

    public URI getURI() {
        return uri;
    }
}
