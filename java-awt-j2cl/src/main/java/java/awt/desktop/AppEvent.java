package java.awt.desktop;

import java.awt.Desktop;
import java.util.EventObject;

public class AppEvent extends EventObject {
    AppEvent() {
        super(Desktop.getDesktop());
    }
}
