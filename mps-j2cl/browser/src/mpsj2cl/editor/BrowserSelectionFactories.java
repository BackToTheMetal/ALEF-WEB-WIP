package mpsj2cl.editor;

import java.util.Map;
import jetbrains.mps.classloading.ModuleClassLoader;
import jetbrains.mps.nodeEditor.selection.EditorCellLabelSelection;
import jetbrains.mps.nodeEditor.selection.EditorCellSelection;
import jetbrains.mps.nodeEditor.selection.NodeRangeSelection;
import jetbrains.mps.openapi.editor.EditorComponent;
import jetbrains.mps.openapi.editor.EditorContext;
import jetbrains.mps.openapi.editor.cells.CellInfo;
import jetbrains.mps.openapi.editor.selection.Selection;

/** Explicit, typed deployment of constructors referenced by serialized editor selections. */
public final class BrowserSelectionFactories {
  private BrowserSelectionFactories() {}

  @FunctionalInterface
  public interface SelectionFactory<T extends Selection> {
    T create(EditorComponent editor, Map<String, String> properties, CellInfo cell) throws Exception;
  }

  @FunctionalInterface
  public interface FilterFactory<T extends NodeRangeSelection.RangeSelectionFilter> {
    T create(EditorContext context) throws Exception;
  }

  public static void registerCoreSelections(ModuleClassLoader loader) {
    registerSelection(loader, "jetbrains.mps.nodeEditor.selection.EditorCellSelection", EditorCellSelection.class,
        EditorCellSelection::new, true);
    registerSelection(loader, "jetbrains.mps.nodeEditor.selection.EditorCellLabelSelection", EditorCellLabelSelection.class,
        EditorCellLabelSelection::new, true);
    registerSelection(loader, "jetbrains.mps.nodeEditor.selection.NodeRangeSelection", NodeRangeSelection.class,
        NodeRangeSelection::new, true);
  }

  /** Application-visible names restore without module IDs; module-local names restore through their module. */
  @SuppressWarnings("unchecked")
  public static <T extends Selection> void registerSelection(ModuleClassLoader loader, String binaryName,
      Class<T> type, SelectionFactory<T> factory, boolean applicationVisible) {
    loader.registerFactory(binaryName, type, new Class<?>[] {EditorComponent.class, Map.class, CellInfo.class},
        args -> factory.create((EditorComponent) args[0], (Map<String, String>) args[1], (CellInfo) args[2]));
    if (applicationVisible) ClassLoader.getSystemClassLoader().registerClass(binaryName, type);
  }

  public static <T extends NodeRangeSelection.RangeSelectionFilter> void registerFilter(ModuleClassLoader loader,
      String binaryName, Class<T> type, FilterFactory<T> factory, boolean applicationVisible) {
    loader.registerFactory(binaryName, type, new Class<?>[] {EditorContext.class},
        args -> factory.create((EditorContext) args[0]));
    if (applicationVisible) ClassLoader.getSystemClassLoader().registerClass(binaryName, type);
  }
}
