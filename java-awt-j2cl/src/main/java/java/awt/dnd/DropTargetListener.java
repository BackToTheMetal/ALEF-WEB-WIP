package java.awt.dnd;

import java.util.EventListener;

public interface DropTargetListener extends EventListener {
    void dragEnter(DropTargetDragEvent event);
    void dragOver(DropTargetDragEvent event);
    void dropActionChanged(DropTargetDragEvent event);
    void dragExit(DropTargetEvent event);
    void drop(DropTargetDropEvent event);
}
