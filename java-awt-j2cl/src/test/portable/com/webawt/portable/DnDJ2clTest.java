package com.webawt.portable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.awt.Button;
import java.awt.Label;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceAdapter;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import org.junit.Test;
import org.webawt.internal.peer.PeerDragEvent;

public final class DnDJ2clTest {
    @Test
    public void recognizesAndCompletesALocalStringDrop() {
        Button source = new Button("Drag source");
        Label targetComponent = new Label("Drop target");
        int[] sourceCompletions = new int[1];
        int[] targetDrops = new int[1];
        String[] received = new String[1];
        DragSource dragSource = new DragSource();
        DragSourceAdapter sourceListener = new DragSourceAdapter() {
            @Override
            public void dragDropEnd(DragSourceDropEvent event) {
                sourceCompletions[0] += event.getDropSuccess() ? 1 : 100;
            }
        };
        dragSource.createDefaultDragGestureRecognizer(source, DnDConstants.ACTION_COPY, event -> event.startDrag(DragSource.DefaultCopyDrop, new StringSelection("local payload"), sourceListener));
        DropTarget target = new DropTarget(targetComponent, DnDConstants.ACTION_COPY, new DropTargetAdapter() {
            @Override
            public void drop(DropTargetDropEvent event) {
                try {
                    event.acceptDrop(DnDConstants.ACTION_COPY);
                    received[0] = (String) event.getTransferable().getTransferData(DataFlavor.stringFlavor);
                    targetDrops[0]++;
                    event.dropComplete(true);
                } catch (Exception exception) {
                    throw new AssertionError(exception);
                }
            }
        });
        source.dispatchEvent(new MouseEvent(source, MouseEvent.MOUSE_PRESSED, 1L, InputEvent.BUTTON1_DOWN_MASK, 2, 2, 1, false, MouseEvent.BUTTON1));
        source.dispatchEvent(new MouseEvent(source, MouseEvent.MOUSE_DRAGGED, 2L, InputEvent.BUTTON1_DOWN_MASK, 12, 2, 0, false, MouseEvent.NOBUTTON));
        target.handlePeerDragEvent(new PeerDragEvent(PeerDragEvent.DROP, 4, 5, 0, DnDConstants.ACTION_COPY, DnDConstants.ACTION_COPY, new String[] {"text/plain"}, new String[] {"snapshot"}));
        assertEquals("local payload", received[0]);
        assertEquals(1, targetDrops[0]);
        assertEquals(1, sourceCompletions[0]);
        assertTrue(target.getDropTargetContext().getComponent() == targetComponent);
    }

    @Test
    public void snapshotsExternalStringDataDuringTheDropEvent() {
        String[] received = new String[1];
        DropTarget target = new DropTarget(new Label("Drop target"), DnDConstants.ACTION_COPY, new DropTargetAdapter() {
            @Override
            public void drop(DropTargetDropEvent event) {
                try {
                    event.acceptDrop(DnDConstants.ACTION_COPY);
                    received[0] = (String) event.getTransferable().getTransferData(DataFlavor.stringFlavor);
                    event.dropComplete(true);
                } catch (Exception exception) {
                    throw new AssertionError(exception);
                }
            }
        });
        target.handlePeerDragEvent(new PeerDragEvent(PeerDragEvent.DROP, 1, 2, 0, DnDConstants.ACTION_COPY, DnDConstants.ACTION_COPY, new String[] {"text/plain"}, new String[] {"external payload"}));
        assertEquals("external payload", received[0]);
    }
}
