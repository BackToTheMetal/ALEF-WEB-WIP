package java.awt.dnd;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Image;
import java.awt.Point;
import java.awt.datatransfer.FlavorMap;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;
import java.util.Map;
import org.webawt.internal.peer.WebAwtRuntime;

public class DragSource implements Serializable {
    public static final Cursor DefaultCopyDrop = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
    public static final Cursor DefaultMoveDrop = Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR);
    public static final Cursor DefaultLinkDrop = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
    public static final Cursor DefaultCopyNoDrop = Cursor.getDefaultCursor();
    public static final Cursor DefaultMoveNoDrop = Cursor.getDefaultCursor();
    public static final Cursor DefaultLinkNoDrop = Cursor.getDefaultCursor();
    private static final DragSource DEFAULT_DRAG_SOURCE = new DragSource();
    static { WebAwtRuntime.addShutdownHook(() -> { DEFAULT_DRAG_SOURCE.dragSourceListeners.clear(); DEFAULT_DRAG_SOURCE.motionListeners.clear(); }); }
    private final List<DragSourceListener> dragSourceListeners = new ArrayList<>();
    private final List<DragSourceMotionListener> motionListeners = new ArrayList<>();
    private final FlavorMap flavorMap = new DefaultFlavorMap();

    public static DragSource getDefaultDragSource() {
        return DEFAULT_DRAG_SOURCE;
    }

    public static boolean isDragImageSupported() {
        return false;
    }

    public DragSource() throws java.awt.HeadlessException {
    }

    public void startDrag(DragGestureEvent trigger, Cursor cursor, Image image, Point offset, Transferable transferable, DragSourceListener listener, FlavorMap flavorMap) throws InvalidDnDOperationException {
        validateStart(trigger, image, offset, transferable);
        DragSourceContext context = createDragSourceContext(trigger, cursor, image, offset, transferable, listener);
        DragManager.begin(context);
        List<String> mimeTypes = new ArrayList<>();
        List<String> data = new ArrayList<>();
        FlavorMap effectiveFlavorMap = flavorMap == null ? this.flavorMap : flavorMap;
        Map<DataFlavor, String> natives = effectiveFlavorMap.getNativesForFlavors(transferable.getTransferDataFlavors());
        for (DataFlavor flavor : transferable.getTransferDataFlavors()) {
            appendStringPayload(transferable, flavor, natives.get(flavor), mimeTypes, data);
        }
        WebAwtRuntime.beginDrag(mimeTypes.toArray(new String[0]), data.toArray(new String[0]), context.getSourceActions(), DragManager::handlePeerEvent);
    }

    public void startDrag(DragGestureEvent trigger, Cursor cursor, Transferable transferable, DragSourceListener listener, FlavorMap flavorMap) throws InvalidDnDOperationException {
        startDrag(trigger, cursor, null, null, transferable, listener, flavorMap);
    }

    public void startDrag(DragGestureEvent trigger, Cursor cursor, Image image, Point offset, Transferable transferable, DragSourceListener listener) throws InvalidDnDOperationException {
        startDrag(trigger, cursor, image, offset, transferable, listener, null);
    }

    public void startDrag(DragGestureEvent trigger, Cursor cursor, Transferable transferable, DragSourceListener listener) throws InvalidDnDOperationException {
        startDrag(trigger, cursor, null, null, transferable, listener, null);
    }

    public FlavorMap getFlavorMap() {
        return flavorMap;
    }

    @SuppressWarnings("unchecked")
    public <T extends DragGestureRecognizer> T createDragGestureRecognizer(Class<T> recognizerClass, Component component, int actions, DragGestureListener listener) {
        if (recognizerClass == null || recognizerClass == MouseDragGestureRecognizer.class) {
            return (T) createDefaultDragGestureRecognizer(component, actions, listener);
        }
        return null;
    }

    public DragGestureRecognizer createDefaultDragGestureRecognizer(Component component, int actions, DragGestureListener listener) {
        return new WebMouseDragGestureRecognizer(this, component, actions, listener);
    }

    public void addDragSourceListener(DragSourceListener listener) {
        if (listener != null) {
            dragSourceListeners.add(listener);
        }
    }

    public void removeDragSourceListener(DragSourceListener listener) {
        dragSourceListeners.remove(listener);
    }

    public DragSourceListener[] getDragSourceListeners() {
        return dragSourceListeners.toArray(new DragSourceListener[0]);
    }

    public void addDragSourceMotionListener(DragSourceMotionListener listener) {
        if (listener != null) {
            motionListeners.add(listener);
        }
    }

    public void removeDragSourceMotionListener(DragSourceMotionListener listener) {
        motionListeners.remove(listener);
    }

    public DragSourceMotionListener[] getDragSourceMotionListeners() {
        return motionListeners.toArray(new DragSourceMotionListener[0]);
    }

    @SuppressWarnings("unchecked")
    public <T extends EventListener> T[] getListeners(Class<T> listenerType) {
        if (listenerType == DragSourceListener.class) {
            return (T[]) getDragSourceListeners();
        }
        if (listenerType == DragSourceMotionListener.class) {
            return (T[]) getDragSourceMotionListeners();
        }
        throw new ClassCastException("Unsupported listener type");
    }

    public static int getDragThreshold() {
        return 5;
    }

    void fireDragEnter(DragSourceDragEvent event) {
        for (DragSourceListener listener : getDragSourceListeners()) {
            listener.dragEnter(event);
        }
    }

    void fireDragOver(DragSourceDragEvent event) {
        for (DragSourceListener listener : getDragSourceListeners()) {
            listener.dragOver(event);
        }
    }

    void fireDropActionChanged(DragSourceDragEvent event) {
        for (DragSourceListener listener : getDragSourceListeners()) {
            listener.dropActionChanged(event);
        }
    }

    void fireDragExit(DragSourceEvent event) {
        for (DragSourceListener listener : getDragSourceListeners()) {
            listener.dragExit(event);
        }
    }

    void fireDragDropEnd(DragSourceDropEvent event) {
        for (DragSourceListener listener : getDragSourceListeners()) {
            listener.dragDropEnd(event);
        }
    }

    void fireDragMouseMoved(DragSourceDragEvent event) {
        for (DragSourceMotionListener listener : getDragSourceMotionListeners()) {
            listener.dragMouseMoved(event);
        }
    }

    private void validateStart(DragGestureEvent trigger, Image image, Point offset, Transferable transferable) {
        if (trigger == null || transferable == null) {
            throw new NullPointerException("trigger and transferable are required");
        }
        if (trigger.getDragSource() != this) {
            throw new InvalidDnDOperationException("DragGestureEvent belongs to another DragSource");
        }
        if ((image == null) != (offset == null)) {
            throw new NullPointerException("drag image and offset must be supplied together");
        }
    }

    private static void appendStringPayload(Transferable transferable, DataFlavor flavor, String nativeType, List<String> mimeTypes, List<String> data) {
        if (nativeType == null) {
            return;
        }
        try {
            Object value = transferable.getTransferData(flavor);
            if (value instanceof String && !mimeTypes.contains(nativeType)) {
                mimeTypes.add(nativeType);
                data.add((String) value);
            }
        } catch (IOException | UnsupportedFlavorException exception) {
            return;
        }
    }
    protected DragSourceContext createDragSourceContext(DragGestureEvent trigger, Cursor cursor, Image image, Point offset, Transferable transferable, DragSourceListener listener) {
        return new DragSourceContext(trigger, cursor, image, offset, transferable, listener);
    }
}
