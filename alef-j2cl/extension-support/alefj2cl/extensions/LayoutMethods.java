package alefj2cl.extensions;
/** Method declarations from the exact source and dependency-header inventory. */
public final class LayoutMethods {
  private static final java.util.Set<String> DECLARATIONS = new java.util.HashSet<>(java.util.Arrays.asList(
    "de.itemis.mps.editor.bool.runtime.EditorCell_Checkbox",
    "jetbrains.mps.editor.runtime.EditorCell_Empty",
    "jetbrains.mps.nodeEditor.cells.EditorCell_Basic",
    "jetbrains.mps.nodeEditor.cells.EditorCell_Collection",
    "jetbrains.mps.nodeEditor.cells.EditorCell_Collection$EditorCell_Brace",
    "jetbrains.mps.nodeEditor.cells.EditorCell_ComponentBase",
    "jetbrains.mps.nodeEditor.cells.EditorCell_ContextAssistantComponent",
    "jetbrains.mps.nodeEditor.cells.EditorCell_Image",
    "jetbrains.mps.nodeEditor.cells.EditorCell_Label"));
  public static boolean declaresRelayout(Class<?> type) { return DECLARATIONS.contains(type.getName()); }
}
