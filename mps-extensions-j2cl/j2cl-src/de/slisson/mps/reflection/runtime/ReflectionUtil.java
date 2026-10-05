package de.slisson.mps.reflection.runtime;

import jetbrains.mps.lang.editor.menus.transformation.SubstituteMenuItemAsActionItem;
import jetbrains.mps.nodeEditor.EditorComponent;
import jetbrains.mps.nodeEditor.EditorComponentActions;
import jetbrains.mps.nodeEditor.NodeEditorActions;
import jetbrains.mps.nodeEditor.cellActions.CellAction_InsertIntoCollection;
import jetbrains.mps.nodeEditor.cells.EditorCell_Label;
import jetbrains.mps.nodeEditor.sidetransform.EditorCell_STHint;
import jetbrains.mps.nodeEditor.updater.UpdaterImpl;

/** Ahead-of-time dispatch for the reviewed extensions hacks. No runtime reflection. */
public final class ReflectionUtil {
  private ReflectionUtil() {}

  public static Object readField(Class<?> cls, Object obj, String fieldName) {
    if (cls == UpdaterImpl.class && "myBigCellsMap".equals(fieldName))
      return ((UpdaterImpl) obj).myBigCellsMap;
    if (cls == SubstituteMenuItemAsActionItem.class && "mySubstituteItem".equals(fieldName))
      return ((SubstituteMenuItemAsActionItem) obj).mySubstituteItem;
    if (cls == EditorComponent.class && "myEditorComponentActions".equals(fieldName))
      return ((EditorComponent) obj).myEditorComponentActions;
    if (cls == EditorComponentActions.class && "myActionMap".equals(fieldName))
      return ((EditorComponentActions) obj).myActionMap;
    if (cls == NodeEditorActions.MoveDown.class && "myPositionTracker".equals(fieldName))
      return ((NodeEditorActions.MoveDown) obj).myPositionTracker;
    if (cls == EditorCell_STHint.class && "myAnchorCell".equals(fieldName))
      return ((EditorCell_STHint) obj).myAnchorCell;
    if (cls == CellAction_InsertIntoCollection.class && "myListHandler".equals(fieldName))
      return ((CellAction_InsertIntoCollection) obj).myListHandler;
    throw unsupported("readField", fieldName);
  }

  public static Object callMethod(Class<?> cls, Object obj, String name, Class<?>[] types, Object[] args) {
    if (cls == EditorCell_Label.class && "getUpdatedText".equals(name)
        && types != null && types.length == 1 && types[0] == String.class
        && args != null && args.length == 1) {
      return ((EditorCell_Label) obj).getUpdatedText((String) args[0]);
    }
    throw unsupported("callMethod", name);
  }

  public static Class<?> getClass(String name) {
    if ("jetbrains.mps.nodeEditor.EditorComponentActions".equals(name)) return EditorComponentActions.class;
    if ("jetbrains.mps.lang.editor.menus.transformation.SubstituteMenuItemAsActionItem".equals(name)) return SubstituteMenuItemAsActionItem.class;
    throw unsupported("getClass", name);
  }

  public static void writeField(Class<?> cls, Object obj, String name, Object value) {
    throw unsupported("writeField", name);
  }
  public static void callVoidMethod(Class<?> cls, Object obj, String name, Class<?>[] types, Object[] args) {
    throw unsupported("callVoidMethod", name);
  }
  public static Object callStaticMethod(Class<?> cls, String name, Class<?>[] types, Object[] args) {
    throw unsupported("callStaticMethod", name);
  }
  public static void callStaticVoidMethod(Class<?> cls, String name, Class<?>[] types, Object[] args) {
    throw unsupported("callStaticVoidMethod", name);
  }
  private static UnsupportedOperationException unsupported(String operation, String member) {
    return new UnsupportedOperationException("Unregistered J2CL reflection hack: " + operation + " " + member);
  }
}
