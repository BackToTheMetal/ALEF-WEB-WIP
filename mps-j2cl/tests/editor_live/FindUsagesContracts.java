package mpsj2cl.editorlive;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import jetbrains.mps.findUsages.FindUsagesManager;
import jetbrains.mps.ide.findusages.model.scopes.ModelsScope;
import jetbrains.mps.project.Solution;
import jetbrains.mps.smodel.EditableModelDescriptor;
import jetbrains.mps.smodel.ModelImports;
import jetbrains.mps.smodel.ModelLoadResult;
import jetbrains.mps.smodel.SModelId;
import jetbrains.mps.smodel.SModelReference;
import jetbrains.mps.smodel.loading.ModelLoadingState;
import mpsj2cl.editor.BrowserEditorHost;
import org.jetbrains.mps.openapi.model.SModel;
import org.jetbrains.mps.openapi.model.SNode;
import org.jetbrains.mps.openapi.model.SReference;
import org.jetbrains.mps.openapi.module.FindUsagesFacade;
import org.jetbrains.mps.openapi.module.ModelAccess;
import org.jetbrains.mps.openapi.persistence.NullDataSource;

/** Real MPS searches over two attached models, including edits after the first query. */
final class FindUsagesContracts {
  static void run(BrowserEditorHost host, Solution solution) {
    FindUsagesFacade searches = FindUsagesFacade.getInstance();
    require(searches instanceof FindUsagesManager, "host installs the real MPS usage search service");
    require(host.getApplication().getService(FindUsagesFacade.class) == searches,
        "application service and legacy facade resolve the same search manager");
    ModelAccess access = host.getProject().getModelAccess();
    MemoryModel source = new MemoryModel(new SModelReference(solution.getModuleReference(),
        SModelId.regular(new UUID(94, 501)), "mpsj2cl.editorlive.search.source"));
    MemoryModel target = new MemoryModel(new SModelReference(solution.getModuleReference(),
        SModelId.regular(new UUID(94, 502)), "mpsj2cl.editorlive.search.target"));
    SNode[] nodes = new SNode[3];
    try {
      access.executeCommand(() -> {
        solution.registerModel(source);
        solution.registerModel(target);
        source.addLanguage(ExpressionLanguage.LITERAL.getLanguage());
        target.addLanguage(ExpressionLanguage.LITERAL.getLanguage());
        SNode group = source.createNode(ExpressionLanguage.GROUP);
        SNode reference = source.createNode(ExpressionLanguage.REFERENCE);
        SNode first = target.createNode(ExpressionLanguage.LITERAL);
        SNode second = target.createNode(ExpressionLanguage.LITERAL);
        source.addRootNode(group);
        group.addChild(ExpressionLanguage.MEMBERS, reference);
        target.addRootNode(first);
        target.addRootNode(second);
        reference.setReferenceTarget(ExpressionLanguage.TARGET, first);
        nodes[0] = reference;
        nodes[1] = first;
        nodes[2] = second;
      });
      ModelsScope scope = new ModelsScope(source, target);
      access.runReadAction(() -> {
        Set<SReference> found = searches.findUsages(scope, Collections.singleton(nodes[1]), null);
        require(found.size() == 1, "usage search finds a nested cross-model reference");
        SReference actual = found.iterator().next();
        require(actual.getSourceNode() == nodes[0] && actual.getTargetNode() == nodes[1],
            "usage search returns the actual source and target nodes");
        require(searches.findUsages(scope, Collections.singleton(nodes[2]), null).isEmpty(),
            "unreferenced nodes have no fabricated usages");
        require(searches.findUsages(new ModelsScope(target), Collections.singleton(nodes[1]), null).isEmpty(),
            "usage search respects the supplied model scope");
        List<SReference> streamed = new ArrayList<>();
        searches.findUsages(scope, Collections.singleton(nodes[1]), streamed::add, null);
        require(streamed.size() == 1 && streamed.get(0).getSourceNode() == nodes[0],
            "consumer search reports real references");
        Set<SNode> expected = new HashSet<>(Arrays.asList(nodes[1], nodes[2]));
        require(searches.findInstances(scope, Collections.singleton(ExpressionLanguage.LITERAL), true, null).equals(expected),
            "exact concept search finds both attached instances");
        require(searches.findInstances(scope, Collections.singleton(ExpressionLanguage.LITERAL), false, null).equals(expected),
            "concept descendants search retains the requested concept's own instances");
        require(searches.findModelUsages(scope, Collections.singleton(target.getReference()), null).equals(Collections.singleton(source)),
            "model usage search follows a cross-model reference without an explicit import");
      });
      access.executeCommand(() -> nodes[0].setReferenceTarget(ExpressionLanguage.TARGET, nodes[2]));
      access.runReadAction(() -> {
        require(searches.findUsages(scope, Collections.singleton(nodes[1]), null).isEmpty(),
            "retargeted reference leaves the previous target's usage set");
        require(searches.findUsages(scope, Collections.singleton(nodes[2]), null).size() == 1,
            "retargeted reference appears in the new target's usage set");
      });
      access.executeCommand(() -> {
        nodes[0].setReferenceTarget(ExpressionLanguage.TARGET, null);
        target.removeRootNode(nodes[1]);
      });
      access.runReadAction(() -> {
        require(searches.findInstances(scope, Collections.singleton(ExpressionLanguage.LITERAL), true, null).equals(Collections.singleton(nodes[2])),
            "instance index updates after a root is removed");
        require(searches.findModelUsages(scope, Collections.singleton(target.getReference()), null).isEmpty(),
            "removed cross-model reference leaves no implicit model usage");
      });
      access.executeCommand(() -> new ModelImports(source).addModelImport(target.getReference()));
      access.runReadAction(() -> {
        List<SModel> streamed = new ArrayList<>();
        searches.findModelUsages(scope, Collections.singleton(target.getReference()), streamed::add, null);
        require(streamed.equals(Collections.singletonList(source)),
            "explicit model imports are found without a node reference");
      });
      access.executeCommand(() -> new ModelImports(source).removeModelImport(target.getReference()));
      access.runReadAction(() -> require(searches.findModelUsages(scope, Collections.singleton(target.getReference()), null).isEmpty(),
          "removed model import no longer appears in search results"));
    } finally {
      access.executeCommand(() -> {
        if (source.getModule() == solution) solution.unregisterModel(source);
        if (target.getModule() == solution) solution.unregisterModel(target);
      });
    }
  }

  private static void require(boolean condition, String detail) {
    if (!condition) throw new AssertionError("Find usages: " + detail);
  }

  /** Storage policy only; model edits, references, indexing and events use real MPS classes. */
  private static final class MemoryModel extends EditableModelDescriptor {
    MemoryModel(org.jetbrains.mps.openapi.model.SModelReference reference) { super(reference, new NullDataSource()); }
    @Override protected ModelLoadResult<jetbrains.mps.smodel.SModel> createModel() {
      return new ModelLoadResult<>(new jetbrains.mps.smodel.SModel(getReference()), ModelLoadingState.FULLY_LOADED);
    }
    @Override public boolean isReadOnly() { return false; }
    @Override public void save() { throw new UnsupportedOperationException("Search fixture has no persistent storage"); }
    @Override public void reloadFromSource() { throw new UnsupportedOperationException("Search fixture has no persistent storage"); }
  }
}
