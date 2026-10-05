package java.awt.dnd;

import java.util.EventListener;

public interface DragSourceListener extends EventListener {
    void dragEnter(DragSourceDragEvent event);
    void dragOver(DragSourceDragEvent event);
    void dropActionChanged(DragSourceDragEvent event);
    void dragExit(DragSourceEvent event);
    void dragDropEnd(DragSourceDropEvent event);
}
