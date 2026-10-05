package alefj2cl.example;

import elemental2.dom.DomGlobal;
import elemental2.dom.EventListener;
import elemental2.dom.HTMLElement;
import elemental2.dom.KeyboardEvent;
import java.awt.Cursor;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import jetbrains.mps.nodeEditor.EditorComponent;
import jetbrains.mps.editor.runtime.style.StyleAttributes;
import jetbrains.mps.nodeEditor.cells.APICellAdapter;
import jetbrains.mps.openapi.editor.cells.EditorCell;
import org.jetbrains.mps.openapi.language.SReferenceLink;
import org.jetbrains.mps.openapi.model.SNode;
import org.jetbrains.mps.openapi.model.SNodeReference;

/** Reference gestures for the browser host, using generated MPS cell metadata. */
final class BrowserReferenceNavigation {
  static void install(EditorComponent editor, Consumer<SNodeReference> navigate) {
    class ReferenceMouse extends MouseAdapter {
      private int pressX, pressY;
      private int mouseX, mouseY;
      private boolean inside;
      private boolean dragged;
      private EditorCell hovered;
      private HTMLElement cursorElement;
      private String previousCursor;
      private SNodeReference pressedReference;
      public void mouseEntered(MouseEvent event) { mouseMoved(event); }
      public void mouseMoved(MouseEvent event) {
        inside = true; mouseX = event.getX(); mouseY = event.getY();
        updateHover(event.isControlDown() && !event.isAltDown() && !event.isShiftDown());
      }
      public void mouseExited(MouseEvent event) { leave(); }
      void leave() { inside = false; clearHover(); }
      void clearHover() {
        if (cursorElement != null) {
          cursorElement.style.cursor = previousCursor;
          cursorElement = null;
        }
        if (hovered != null) {
          if (hovered.getStyle().get(StyleAttributes.CONTROL_OVERED_REFERENCE)) {
            hovered.getStyle().set(StyleAttributes.CONTROL_OVERED_REFERENCE, false);
            editor.repaintExternalComponent();
          }
          editor.setCursor(Cursor.getDefaultCursor());
        }
      }
      void updateHover(boolean control) {
        if (!inside || !editor.isShowing()) { clearHover(); return; }
        editor.getEditorContext().getRepository().getModelAccess().runReadAction(() -> {
          EditorCell cell = editor.getRootCell() == null ? null : editor.getRootCell().findLeaf(mouseX, mouseY);
          if (!control) {
            clearHover();
            // The native underliner may retain Ctrl state across a browser blur.
            if (cell != null && cell.getStyle().get(StyleAttributes.CONTROL_OVERED_REFERENCE)) {
              cell.getStyle().set(StyleAttributes.CONTROL_OVERED_REFERENCE, false);
              editor.setCursor(Cursor.getDefaultCursor());
              editor.repaintExternalComponent();
            }
            return;
          }
          SNode target = target(cell);
          if (target == null || target.getModel() == null) { clearHover(); return; }
          if (hovered != cell) clearHover();
          hovered = cell;
          // Use MPS's native link color and underline without changing selection.
          if (!cell.getStyle().get(StyleAttributes.CONTROL_OVERED_REFERENCE)) {
            cell.getStyle().set(StyleAttributes.CONTROL_OVERED_REFERENCE, true);
            editor.repaintExternalComponent();
          }
          editor.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
          // WebAWT currently stores Cursor on the component without updating CSS.
          java.awt.Point origin = editor.getLocationOnScreen();
          HTMLElement element = jsinterop.base.Js.cast(DomGlobal.document.elementFromPoint(origin.x + mouseX, origin.y + mouseY));
          if (element != cursorElement) {
            if (cursorElement != null) cursorElement.style.cursor = previousCursor;
            cursorElement = element;
            if (element != null) previousCursor = element.style.cursor;
          }
          if (cursorElement != null) cursorElement.style.cursor = "pointer";
        });
      }
      public void mousePressed(MouseEvent event) {
        pressX = event.getX(); pressY = event.getY(); dragged = false;
        pressedReference = null;
        if (!event.isControlDown() || event.isAltDown() || event.isShiftDown() || event.getButton() != MouseEvent.BUTTON1) return;
        editor.getEditorContext().getRepository().getModelAccess().runReadAction(() -> {
          if (editor.getRootCell() == null) return;
          SNode target = target(editor.getRootCell().findLeaf(event.getX(), event.getY()));
          if (target != null && target.getModel() != null) pressedReference = target.getReference();
        });
      }
      public void mouseDragged(MouseEvent event) {
        if (Math.abs(event.getX() - pressX) > 4 || Math.abs(event.getY() - pressY) > 4) dragged = true;
        clearHover();
      }
      public void mouseClicked(MouseEvent event) {
        if (event.getClickCount() == 1 && event.getButton() == MouseEvent.BUTTON1) followMouse(event);
      }
      private void followMouse(MouseEvent event) {
        if (dragged || !event.isControlDown() || event.isAltDown() || event.isShiftDown() || pressedReference == null) return;
        // Selection can scroll the cell before release; retain the pressed target.
        SNodeReference reference = pressedReference;
        leave();
        // Complete the source mouse event before replacing its editor surface.
        SwingUtilities.invokeLater(() -> navigate.accept(reference));
      }
    }
    ReferenceMouse mouse = new ReferenceMouse();
    editor.addMouseListener(mouse);
    editor.addMouseMotionListener(mouse);
    // Ctrl may be pressed while the project tree, rather than this editor, has focus.
    EventListener modifiers = event -> {
      KeyboardEvent key = jsinterop.base.Js.cast(event);
      boolean control = key.ctrlKey && !key.altKey && !key.shiftKey;
      // Apply after native key listeners so their cached hover cannot restore it.
      SwingUtilities.invokeLater(() -> { if (!editor.isDisposed()) mouse.updateHover(control); });
    };
    EventListener blur = event -> mouse.leave();
    DomGlobal.document.addEventListener("keydown", modifiers, true);
    DomGlobal.document.addEventListener("keyup", modifiers, true);
    DomGlobal.window.addEventListener("blur", blur);
    FocusAdapter focus = new FocusAdapter() {
      public void focusLost(FocusEvent event) { mouse.leave(); }
    };
    editor.addFocusListener(focus);
    String action = "alef-open-reference";
    for (int modifier : new int[] {InputEvent.CTRL_DOWN_MASK, InputEvent.META_DOWN_MASK}) {
      editor.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_B, modifier), action);
    }
    editor.getActionMap().put(action, new AbstractAction() {
      public void actionPerformed(java.awt.event.ActionEvent event) {
        if (!editor.isFocusOwner()) return;
        editor.getEditorContext().getRepository().getModelAccess().runReadAction(() -> {
          SNode target = target(editor.getSelectedCell());
          if (target != null && target.getModel() != null) {
            SNodeReference reference = target.getReference();
            SwingUtilities.invokeLater(() -> navigate.accept(reference));
          }
        });
      }
    });
    editor.addDisposeListener(component -> {
      mouse.leave();
      editor.removeMouseListener(mouse);
      editor.removeMouseMotionListener(mouse);
      editor.removeFocusListener(focus);
      DomGlobal.document.removeEventListener("keydown", modifiers, true);
      DomGlobal.document.removeEventListener("keyup", modifiers, true);
      DomGlobal.window.removeEventListener("blur", blur);
      editor.getActionMap().remove(action);
      for (int modifier : new int[] {InputEvent.CTRL_DOWN_MASK, InputEvent.META_DOWN_MASK}) {
        editor.getInputMap(JComponent.WHEN_FOCUSED).remove(KeyStroke.getKeyStroke(KeyEvent.VK_B, modifier));
      }
    });
  }

  private static SNode target(EditorCell cell) {
    if (cell == null) return null;
    SNode owner = cell.getSNode();
    for (EditorCell candidate = cell; candidate != null && candidate.getSNode() == owner; candidate = candidate.getParent()) {
      if (candidate.getSRole() instanceof SReferenceLink || APICellAdapter.hasExplicitNavigatableNode(candidate)) {
        SNode target = APICellAdapter.getSNodeWRTReference(candidate);
        // An unresolved reference falls back to its owning node in the MPS adapter.
        if (target != owner || APICellAdapter.hasExplicitNavigatableNode(candidate)
            || (candidate.getSRole() instanceof SReferenceLink && owner != null
                && owner.getReferenceTarget((SReferenceLink) candidate.getSRole()) != null)) return target;
        return null;
      }
    }
    return null;
  }
}
