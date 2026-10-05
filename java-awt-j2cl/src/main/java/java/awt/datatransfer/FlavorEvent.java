package java.awt.datatransfer;

import java.util.EventObject;

public class FlavorEvent extends EventObject {
    private static final long serialVersionUID = 9083382461728398578L;

    public FlavorEvent(Clipboard source) {
        super(source);
    }
}
