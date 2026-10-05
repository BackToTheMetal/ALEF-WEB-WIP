package alefj2cl;

import com.mbeddr.mpsutil.grammarcells.runtime.GrammarCellsRuntime;
import java.awt.GridLayout;
import java.util.UUID;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import jetbrains.mps.nodeEditor.NodeEditorComponent;
import jetbrains.mps.nodeEditor.cells.EditorCell_Label;
import jetbrains.mps.openapi.editor.cells.EditorCell;
import jetbrains.mps.openapi.editor.cells.EditorCell_Collection;
import jetbrains.mps.project.ModuleId;
import jetbrains.mps.project.Solution;
import jetbrains.mps.project.structure.modules.SolutionDescriptor;
import jetbrains.mps.smodel.EditableModelDescriptor;
import jetbrains.mps.smodel.ModelLoadResult;
import jetbrains.mps.smodel.SModelId;
import jetbrains.mps.smodel.SModelReference;
import jetbrains.mps.smodel.adapter.structure.MetaAdapterFactory;
import jetbrains.mps.smodel.loading.ModelLoadingState;
import mpsj2cl.editor.BrowserEditorHost;
import org.jetbrains.mps.openapi.language.SConcept;
import org.jetbrains.mps.openapi.language.SProperty;
import org.jetbrains.mps.openapi.model.SNode;
import org.jetbrains.mps.openapi.persistence.NullDataSource;
import org.webawt.WebAwt;

/** Real generated editors; background changes without mouse/resize repaint help. */
public final class EditorUpdateContracts {
  private static BrowserEditorHost host;
  private static MemoryModel model;
  private static SNode root;
  private static SProperty name;
  private static NodeEditorComponent left, right;
  private static String status = "Starting";
  public static String status() { return status; }
  public static void start() {
    WebAwt.install("editors");
    SwingUtilities.invokeLater(() -> {
      try {
        host = new BrowserEditorHost("Editor update contracts");
        host.getPlatform().findComponent(jetbrains.mps.persistence.PersistenceRegistry.class)
            .setModelIdFactory("java", new jetbrains.mps.java.stub.JavaPackageModelId.Factory());
        new AlefLanguages(host.getPlatform());
        GrammarCellsRuntime.init();
        host.getProject().getModelAccess().executeCommand(() -> {
          var descriptor = new SolutionDescriptor();
          descriptor.setId(ModuleId.regular(new UUID(992, 1)));
          descriptor.setNamespace("editor.update.contracts");
          var solution = new Solution(descriptor, null) { @Override public boolean isPackaged() { return false; } };
          host.getProject().addModule(solution);
          model = new MemoryModel(new SModelReference(solution.getModuleReference(), SModelId.regular(new UUID(992, 2)), "editor.update.model"));
          solution.registerModel(model);
          SConcept concept = null;
          for (var c : new gegevensspraak.structure.StructureAspectDescriptor().getDescriptors()) {
            if (c.getConceptFqName().endsWith(".ObjectModel")) concept = MetaAdapterFactory.getConcept(c.getId(), c.getConceptFqName());
          }
          root = model.createNode(concept);
          for (SProperty p : concept.getProperties()) if (p.getName().equals("name")) name = p;
          root.setProperty(name, "Initial model");
          model.addLanguage(concept.getLanguage());
          model.addRootNode(root);
        });
        host.getProject().getModelAccess().runReadAction(() -> {
          left = host.createEditor(root);
          right = host.createEditor(root);
          JFrame frame = new JFrame("Updates");
          frame.setUndecorated(true);
          frame.getContentPane().setLayout(new GridLayout(1, 2));
          frame.getContentPane().add(left.getExternalComponent());
          frame.getContentPane().add(right.getExternalComponent());
          frame.setSize(1200, 600);
          frame.setVisible(true);
          left.relayout(); right.relayout();
        });
        status = "READY";
      } catch (Throwable e) { status = "FAIL: " + e; e.printStackTrace(); }
    });
  }
  public static void change(String mode, String value) {
    Runnable change = () -> {
      if (mode.equals("remove")) model.removeRootNode(root);
      else if (mode.equals("add")) { root.setProperty(name, value); model.addRootNode(root); }
      else root.setProperty(name, value);
    };
    if (mode.equals("command")) host.getProject().getModelAccess().executeCommand(change);
    else host.getProject().getModelAccess().runWriteAction(() -> {
      if (mode.equals("direct")) model.getSModel().enterUpdateMode();
      try { change.run(); }
      finally { if (mode.equals("direct")) model.getSModel().leaveUpdateMode(); }
    });
    // Match ALEF's transient result path: rebuilding without a model event.
    if (mode.equals("direct")) host.getProject().getModelAccess().runReadAction(() -> {
      left.getUpdater().update(); right.getUpdater().update();
    });
  }
  public static String text(int index) {
    return host.getProject().getModelAccess().computeReadAction(() -> {
      StringBuilder text = new StringBuilder();
      append((index == 0 ? left : right).getRootCell(), text);
      return text.toString();
    });
  }
  private static void append(EditorCell cell, StringBuilder text) {
    if (cell instanceof EditorCell_Label) text.append(((EditorCell_Label)cell).getText()).append(' ');
    if (cell instanceof EditorCell_Collection) for (EditorCell child : (EditorCell_Collection)cell) append(child, text);
  }
  private static final class MemoryModel extends EditableModelDescriptor {
    MemoryModel(org.jetbrains.mps.openapi.model.SModelReference ref) { super(ref, new NullDataSource()); }
    @Override protected ModelLoadResult<jetbrains.mps.smodel.SModel> createModel() {
      return new ModelLoadResult<>(new jetbrains.mps.smodel.SModel(getReference()), ModelLoadingState.FULLY_LOADED);
    }
    // Like MPS temporary models: updates are allowed in a write action.
    @Override public boolean isReadOnly() { return true; }
    @Override public void save() { throw new UnsupportedOperationException(); }
    @Override public void reloadFromSource() { throw new UnsupportedOperationException(); }
  }
}
