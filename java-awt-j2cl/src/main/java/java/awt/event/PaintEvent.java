package java.awt.event;

import java.awt.Component;
import java.awt.Rectangle;

public class PaintEvent extends ComponentEvent {
    public static final int PAINT_FIRST = 800;
    public static final int PAINT_LAST = 801;
    public static final int PAINT = PAINT_FIRST;
    public static final int UPDATE = PAINT_FIRST + 1;
    private static final long serialVersionUID = 1L;
    private Rectangle updateRect;

    public PaintEvent(Component source, int id, Rectangle updateRect) {
        super(source, id);
        this.updateRect = updateRect;
    }

    public Rectangle getUpdateRect() {
        return updateRect;
    }

    public void setUpdateRect(Rectangle updateRect) {
        this.updateRect = updateRect;
    }

    @Override
    public String paramString() {
        String type = id == PAINT ? "PAINT" : id == UPDATE ? "UPDATE" : "unknown type";
        return type + ",updateRect=" + updateRect;
    }
}
