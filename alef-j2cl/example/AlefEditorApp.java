package alefj2cl.example;

import alefj2cl.AlefLanguages;
import com.intellij.openapi.command.undo.UndoManager;
import com.mbeddr.mpsutil.grammarcells.runtime.GrammarCellsRuntime;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import jetbrains.mps.nodeEditor.NodeEditorComponent;
import jetbrains.mps.nodeEditor.cells.EditorCell_Label;
import jetbrains.mps.nodeEditor.cells.CellFinderUtil;
import jetbrains.mps.nodeEditor.cells.CellFinderUtil.Finder;
import jetbrains.mps.openapi.intentions.IntentionExecutable;
import jetbrains.mps.openapi.editor.cells.EditorCell;
import jetbrains.mps.nodeEditor.cells.EditorCell_Collection;
import jetbrains.mps.project.ModuleId;
import jetbrains.mps.project.Solution;
import jetbrains.mps.project.structure.modules.SolutionDescriptor;
import jetbrains.mps.smodel.EditableModelDescriptor;
import jetbrains.mps.smodel.ModelLoadResult;
import jetbrains.mps.smodel.SModelId;
import jetbrains.mps.smodel.SModelReference;
import jetbrains.mps.smodel.adapter.structure.MetaAdapterFactory;
import jetbrains.mps.smodel.loading.ModelLoadingState;
import jetbrains.mps.smodel.runtime.ConceptDescriptor;
import jetbrains.mps.smodel.runtime.StructureAspectDescriptor;
import jetbrains.mps.typechecking.TypecheckingFacade;
import mpsj2cl.editor.BrowserEditorHost;
import org.jetbrains.mps.openapi.language.*;
import org.jetbrains.mps.openapi.model.SNode;
import org.jetbrains.mps.openapi.persistence.NullDataSource;
import org.webawt.WebAwt;

/** A small writable model displayed by the actual generated ALEF editors. */
public final class AlefEditorApp {
  private static final Map<String, SConcept> concepts = new HashMap<>();
  private static final Map<String, SConcept> rootConcepts = new HashMap<>();
  private static BrowserEditorHost host;
  private static AlefLanguages languages;
  private static MemoryModel model;
  private static Solution projectSolution;
  private static final Map<String, NodeEditorComponent> rootEditors = new HashMap<>();
  private static JFrame window;
  private static NodeEditorComponent dataEditor, expressionEditor;
  private static NodeEditorComponent serviceEditor, testEditor;
  private static JPanel dataPane, expressionPane, servicePane, testPane;
  private static SNode service, entrypoint, testSet, serviceTest, testAge, expectedAllowance, ageField;
  private static SNode objectModel, person, birthDate, ruleGroup, rule, assignment, expression, amount;
  private static ExampleTheme theme;
  private static BrowserInspector inspector;
  private static String status = "Starting the language runtime…";
  private static int width = 1000;
  private static int workspaceEditorHeight = 620, workspaceInspectorHeight = 260;
  private static String testResult = "Not run";
  private static String testFailure = "";
  private static String pendingNavigation = "null";
  private static BackendModelBridge backend;
  private static SNode activeRoot;
  private static boolean debugConfigured;
  private static final Map<String, BrowserComparisonSession> comparisons = new HashMap<>();
  public static void prepareComparison(String id, String before, String after) {
    if (!"READY".equals(status)) throw new IllegalStateException("Workspace is not ready");
    closeComparison(id);
    var session = new BrowserComparisonSession(host); comparisons.put(id, session); session.load(before, after);
  }
  public static String comparisonStatus(String id) { var session = comparisons.get(id); return session == null ? "CLOSED" : session.status(); }
  public static void openComparison(String id, String side, String reference, String changes, String target) { comparisons.get(id).open(side, reference, changes, target); }
  public static String comparisonState(String id, String side) { var session = comparisons.get(id); return session == null ? "null" : session.state(side); }
  public static void focusComparison(String id, String side, String node) { var session = comparisons.get(id); if (session != null) session.focus(side, node); }
  public static void layoutComparison(String id, String side, int width, int height) { var session = comparisons.get(id); if (session != null) session.layout(side, width, height); }
  public static void closeComparisonEditor(String id, String side) { var session = comparisons.get(id); if (session != null) session.closeEditor(side); }
  public static void closeComparison(String id) { var session = comparisons.remove(id); if (session != null) session.close(); }
  private static void closeComparisons() { for (var session : comparisons.values()) session.close(); comparisons.clear(); }

  public static void start() {
    start(null);
  }
  public static void startRemote(String snapshot) { start(snapshot); }
  static void rollbackRejectedCommand(String snapshot) {
    refreshRemote(snapshot);
    elemental2.dom.DomGlobal.document.dispatchEvent(new elemental2.dom.Event("alef-command-rollback"));
  }
  public static void refreshRemote(String snapshot) {
    if (backend == null || !"READY".equals(status)) throw new IllegalStateException("Workspace is not ready to refresh");
    closeComparisons();
    status = "Refreshing authorized workspace…";
    SwingUtilities.invokeLater(() -> {
      try {
        inspector.close();
        for (NodeEditorComponent editor : new java.util.HashSet<>(rootEditors.values())) editor.dispose();
        rootEditors.clear(); window.getContentPane().removeAll(); history().clearHistory();
        host.getProject().getModelAccess().executeCommand(() -> {
          backend.closeRepository(); backend = new BackendModelBridge(host);
        });
        backend.loadAsync(snapshot,()->{host.getProject().getModelAccess().runReadAction(() -> {
          inspector = new BrowserInspector(host, width);
          inspector.layout(width, workspaceEditorHeight + 32, workspaceInspectorHeight);
          BrowserReferenceNavigation.install(inspector.editor(), AlefEditorApp::navigateTo);
        });
        history().clearHistory(); status = "READY";},AlefEditorApp::loadingFailed);
      } catch (Throwable failure) { status = "FAIL: " + failure; failure.printStackTrace(); }
    });
  }
  private static void start(String snapshot) {
    status = "Starting the language runtime…";
    WebAwt.install("alef-editor");
    SwingUtilities.invokeLater(() -> {
      try {
        host = new BrowserEditorHost("ALEF editor example");
        host.getIdeaProject().registerService(jetbrains.mps.openapi.navigation.NavigationSupport.class,
            new BrowserNavigationSupport((node, focus, select) -> navigateTo(node.getReference(), focus, select)));
        theme = new ExampleTheme();
        host.getPlatform().findComponent(jetbrains.mps.persistence.PersistenceRegistry.class)
            .setModelIdFactory("java", new jetbrains.mps.java.stub.JavaPackageModelId.Factory());
        languages = new AlefLanguages(host.getPlatform());
        GrammarCellsRuntime.init();
        index(new gegevensspraak.structure.StructureAspectDescriptor());
        index(new regelspraak.structure.StructureAspectDescriptor());
        index(new contexts.structure.StructureAspectDescriptor());
        index(new servicespraak.structure.StructureAspectDescriptor());
        index(new testspraak.structure.StructureAspectDescriptor());
        if (snapshot != null) {
          backend = new BackendModelBridge(host);
          backend.loadAsync(snapshot,()->{
          // The authorized snapshot already contains the standard models.
          // Register debugging only after those models have been loaded.
          configureDebugger();
          host.getProject().getModelAccess().runReadAction(() -> {
            inspector = new BrowserInspector(host, width);
            inspector.layout(width, workspaceEditorHeight + 32, workspaceInspectorHeight);
            BrowserReferenceNavigation.install(inspector.editor(), AlefEditorApp::navigateTo);
          });
          window = new JFrame("ALEF workspace"); window.setUndecorated(true);
          window.setLocation(0, 0); window.getContentPane().setLayout(new GridLayout(1, 1));
          window.setSize(width, workspaceEditorHeight); window.setVisible(true);
          history().clearHistory(); status = "READY";},AlefEditorApp::loadingFailed);
          return;
        }
        // Only the standalone example needs locally compiled standard models.
        host.getProject().getModelAccess().executeCommand(() ->
            alefj2cl.AlefStandardModels.install(host.getProject()));
        configureDebugger();
        host.getProject().getModelAccess().executeCommand(AlefEditorApp::createModel);
        host.getProject().getModelAccess().runReadAction(() -> {
          inspector = new BrowserInspector(host, width);
          dataEditor = host.createEditor(objectModel);
          expressionEditor = host.createEditor(ruleGroup);
          serviceEditor = host.createEditor(service);
          testEditor = host.createEditor(testSet);
          for (int i = 0; i < 4; i++) BrowserReferenceNavigation.install(component(i), AlefEditorApp::navigateTo);
          BrowserReferenceNavigation.install(inspector.editor(), AlefEditorApp::navigateTo);
        });
        window = new JFrame("ALEF language editors");
        window.setUndecorated(true);
        window.setLocation(0, 0);
        window.getContentPane().setLayout(new GridLayout(1, 2, 1, 0));
        window.getContentPane().setBackground(new Color(222, 229, 230));
        dataPane = pane(dataEditor);
        expressionPane = pane(expressionEditor);
        servicePane = pane(serviceEditor);
        testPane = pane(testEditor);
        window.getContentPane().add(dataPane);
        window.getContentPane().add(expressionPane);
        window.setSize(width, 620);
        window.setVisible(true);
        host.getProject().getModelAccess().runReadAction(() -> {
          dataEditor.relayout();
          expressionEditor.relayout();
          if (dataEditor.findCellWithId(objectModel, "property_name") == null
              || dataEditor.findCellWithId(person, "property_name") == null
              || dataEditor.findCellWithId(birthDate, "property_name") == null
              || expressionEditor.findCellWithId(ruleGroup, "property_name") == null
              || expressionEditor.findCellWithId(rule, "property_name") == null
              || serviceEditor.findCellWithId(entrypoint, "property_name") == null
              || testEditor.findCellWithId(testAge, "EBWEC_property_waarde") == null
              || expressionEditor.findCellWithId(amount, "property_waarde") == null
              || expressionEditor.findCellWithId(expression, "Constant_gp57vv_a0a") == null) {
            throw new IllegalStateException("A generated editor did not initialize");
          }
        });
        history().clearHistory();
        status = "READY";
      } catch (Throwable failure) {
        failure.printStackTrace();
        status = "FAIL: " + failure;
      }
    });
  }

  private static void configureDebugger() {
    // Register the runtime part of ALEF's desktop application plugin.
    // Without it the default debugger configuration hides every rule.
    host.getProject().getModelAccess().executeCommand(() -> {
      if (!debugConfigured) {
        interpreter.debug.behavior.DebugConfiguration.instance().addConfiguration(
            new testspraak.translator.AlefDebugConfiguration());
        debugConfigured = true;
      }
    });
  }

  private static JPanel pane(NodeEditorComponent editor) {
    ExampleTheme.style(editor);
    JPanel pane = new JPanel(new BorderLayout());
    pane.setBackground(Color.WHITE);
    pane.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    pane.add(editor.getExternalComponent(), BorderLayout.CENTER);
    return pane;
  }

  public static String status() { return status; }
  public static String inspectorState() { return inspector == null || !"READY".equals(status) ? "null" : inspector.state(); }
  public static void showInspector(boolean visible) { if (inspector != null) inspector.setVisible(visible); }
  public static String inspectorText() {
    return host.getProject().getModelAccess().computeReadAction(() -> {
      StringBuilder result = new StringBuilder();
      cellText(inspector.editor().getRootCell(), result);
      return result.toString();
    });
  }
  private static org.jetbrains.mps.openapi.model.SModel writableProjectModel(String reference) {
    if (backend != null) return backend.writableModel(reference);
    for (org.jetbrains.mps.openapi.model.SModel candidate : projectSolution.getModels()) {
      if (candidate.getReference().toString().equals(reference) && !candidate.isReadOnly()) return candidate;
    }
    throw new IllegalArgumentException("Choose a writable model in this project");
  }
  private static boolean canCreateRoot(SConcept concept, org.jetbrains.mps.openapi.model.SModel target) {
    return concept != null && !concept.isAbstract() && jetbrains.mps.smodel.constraints.ConstraintsCanBeFacade
        .checkCanBeRoot(new jetbrains.mps.core.aspects.constraints.rules.kinds.CanBeRootContext(concept, target)).isEmpty();
  }
  private static SProperty rootNameProperty(SConcept concept) {
    SProperty name = null;
    for (SProperty property : concept.getProperties()) {
      // Test sets derive their presentation/name from simpleName.
      if ("simpleName".equals(property.getName())) return property;
      if ("name".equals(property.getName())) name = property;
    }
    return name;
  }
  public static String rootTypes(String modelReference) {
    return host.getProject().getModelAccess().computeReadAction(() -> {
      org.jetbrains.mps.openapi.model.SModel target = writableProjectModel(modelReference);
      elemental2.core.JsArray<Object> result = new elemental2.core.JsArray<>();
      for (SConcept concept : rootConcepts.values()) {
        if (!canCreateRoot(concept, target)) continue;
        jsinterop.base.JsPropertyMap<Object> item = jsinterop.base.JsPropertyMap.of();
        item.set("id", concept.getQualifiedName());
        item.set("name", concept.getName());
        item.set("language", concept.getLanguage().getQualifiedName());
        item.set("named", rootNameProperty(concept) != null);
        result.push(item);
      }
      return elemental2.core.Global.JSON.stringify(result);
    });
  }
  /** One undoable MPS command, including generated factories and mandatory children. */
  public static String createRoot(String modelReference, String conceptName, String name, String virtualPackage) {
    if (!"READY".equals(status)) throw new IllegalStateException("The model is not ready");
    String[] result = new String[1];
    host.getProject().getModelAccess().executeCommand(() -> {
      org.jetbrains.mps.openapi.model.SModel target = writableProjectModel(modelReference);
      SConcept concept = rootConcepts.get(conceptName);
      if (!canCreateRoot(concept, target)) throw new IllegalArgumentException("This concept cannot be created as a root");
      SProperty nameProperty = rootNameProperty(concept);
      if (nameProperty != null && (name == null || name.trim().isEmpty())) throw new IllegalArgumentException("Enter a root name");
      String path = virtualPackage == null ? "" : virtualPackage.trim();
      if (!path.isEmpty()) for (String segment : path.split("\\.", -1)) {
        if (segment.trim().isEmpty() || !segment.equals(segment.trim())) throw new IllegalArgumentException("Use dot-separated package names without empty segments");
      }
      SNode root = jetbrains.mps.smodel.action.NodeFactoryManager.createNode(concept, null, null, target);
      if (root == null) throw new IllegalStateException("The node factory did not create a root");
      if (nameProperty != null) org.jetbrains.mps.openapi.model.SNodeAccessUtil.setProperty(root, nameProperty, name.trim());
      root.setProperty(jetbrains.mps.smodel.SNodeUtil.property_BaseConcept_virtualPackage, path.isEmpty() ? null : path);
      if (backend != null) backend.assignReservation(target, root);
      ((jetbrains.mps.smodel.SModelInternal) target).addLanguage(concept.getLanguage());
      target.addRootNode(root);
      result[0] = root.getReference().toString();
    });
    return result[0];
  }
  /** Snapshot the project's own models under MPS read access; no synthetic tree entries. */
  public static String projectTree() {
    if (!"READY".equals(status)) return "null";
    if (backend != null) return host.getProject().getModelAccess().computeReadAction(backend::tree);
    return host.getProject().getModelAccess().computeReadAction(() -> {
      jsinterop.base.JsPropertyMap<Object> project = jsinterop.base.JsPropertyMap.of();
      project.set("name", projectSolution.getModuleName());
      elemental2.core.JsArray<Object> models = new elemental2.core.JsArray<>();
      project.set("models", models);
      for (org.jetbrains.mps.openapi.model.SModel source : projectSolution.getModels()) {
        jsinterop.base.JsPropertyMap<Object> item = jsinterop.base.JsPropertyMap.of();
        item.set("id", source.getReference().toString());
        item.set("reference", source.getReference().toString());
        item.set("moduleId", source.getModule().getModuleId().toString());
        item.set("moduleName", source.getModule().getModuleName());
        item.set("modelId", source.getModelId().toString());
        item.set("name", source.getName().getValue());
        elemental2.core.JsArray<Object> roots = new elemental2.core.JsArray<>();
        item.set("roots", roots); models.push(item);
        for (SNode root : source.getRootNodes()) {
          jsinterop.base.JsPropertyMap<Object> node = jsinterop.base.JsPropertyMap.of();
          node.set("id", root.getReference().toString());
          node.set("name", root.getPresentation());
          node.set("concept", root.getConcept().getName());
          String virtualPackage = root.getProperty(jetbrains.mps.smodel.SNodeUtil.property_BaseConcept_virtualPackage);
          node.set("virtualPackage", virtualPackage == null ? "" : virtualPackage);
          roots.push(node);
        }
      }
      return elemental2.core.Global.JSON.stringify(project);
    });
  }

  /** Resolve the stable MPS reference again, rather than trusting a frontend label or index. */
  public static void openRoot(String reference) {
    if (!"READY".equals(status)) return;
    host.getProject().getModelAccess().runReadAction(() -> {
      SNode root = org.jetbrains.mps.openapi.persistence.PersistenceFacade.getInstance()
          .createNodeReference(reference).resolve(host.getProject().getRepository());
      if (root == null || root.getParent() != null || root.getModel() == null) {
        throw new IllegalArgumentException("This root no longer exists in the repository");
      }
      activeRoot = root;
      NodeEditorComponent editor = rootEditors.get(reference);
      boolean firstOpen = editor == null;
      if (editor == null) {
        for (int i = 0; i < 4; i++) {
          if (component(i) != null && component(i).getEditedNode() == root) { editor = component(i); break; }
        }
        if (editor == null) {
          editor = host.createEditor(root);
          BrowserReferenceNavigation.install(editor, AlefEditorApp::navigateTo);
          if (backend != null) installLeaseHistory(editor);
        }
        rootEditors.put(reference, editor);
      }
      window.getContentPane().removeAll();
      // Reuse the existing panes to keep comparison mode intact.
      JPanel container = editor == dataEditor ? dataPane : editor == expressionEditor ? expressionPane
          : editor == serviceEditor ? servicePane : editor == testEditor ? testPane : pane(editor);
      window.getContentPane().add(container);
      window.getContentPane().revalidate();
      window.getContentPane().repaint();
      editor.relayout();
      EditorCell selection = editor.getSelectedCell();
      if (firstOpen || selection == null) {
        selection = editor.findCellWithId(root, "property_name");
        if (selection == null) selection = editor.findCellWithId(root, "property_simpleName");
        if (selection == null) selection = CellFinderUtil.findChildByManyFinders(
            editor.getRootCell(), Finder.FIRST_EDITABLE, Finder.FIRST_SELECTABLE_LEAF);
        if (selection != null) {
          editor.changeSelection(selection);
          if (selection instanceof EditorCell_Label) {
            EditorCell_Label label = (EditorCell_Label) selection;
            label.deselectAll();
            label.setCaretPositionIfPossible(0);
          }
        }
      }
      inspector.inspect(selection == null ? root : selection.getSNode());
    });
  }
  private static void navigateTo(org.jetbrains.mps.openapi.model.SNodeReference reference) {
    navigateTo(reference, true, true);
  }
  private static NodeEditorComponent navigateTo(org.jetbrains.mps.openapi.model.SNodeReference reference, boolean focus, boolean select) {
    if (!"READY".equals(status)) return null;
    return host.getProject().getModelAccess().computeReadAction(() -> {
      SNode target = reference.resolve(host.getProject().getRepository());
      if (target == null || target.getModel() == null) return null;
      SNode root = target.getContainingRoot();
      String rootId = root.getReference().toString();
      openRoot(rootId);
      NodeEditorComponent editor = rootEditors.get(rootId);
      EditorCell nodeCell = editor.findNodeCell(target, true);
      if (select && nodeCell != null) {
        for (EditorCell cell = nodeCell; cell != null; cell = cell.getParent()) {
          if (cell instanceof jetbrains.mps.openapi.editor.cells.EditorCell_Collection)
            ((jetbrains.mps.openapi.editor.cells.EditorCell_Collection) cell).unfold();
        }
        editor.relayout();
        EditorCell selection = editor.findCellWithId(target, "property_name");
        if (selection == null) selection = editor.findCellWithId(target, "property_simpleName");
        if (selection == null) selection = CellFinderUtil.findChildByManyFinders(nodeCell, true, Finder.FIRST_EDITABLE, Finder.FIRST_SELECTABLE_LEAF);
        if (selection != null) {
          editor.changeSelection(selection);
          if (selection instanceof EditorCell_Label) {
            ((EditorCell_Label) selection).deselectAll();
            ((EditorCell_Label) selection).setCaretPositionIfPossible(0);
          }
          editor.ensureSelectionVisible();
        }
      }
      if (select) inspector.inspect(target);
      if (focus) editor.requestFocusInWindow();
      jsinterop.base.JsPropertyMap<Object> navigation = jsinterop.base.JsPropertyMap.of();
      navigation.set("id", rootId);
      navigation.set("name", root.getPresentation());
      navigation.set("concept", root.getConcept().getName());
      String virtualPackage = root.getProperty(jetbrains.mps.smodel.SNodeUtil.property_BaseConcept_virtualPackage);
      navigation.set("virtualPackage", virtualPackage == null ? "" : virtualPackage);
      navigation.set("external", backend == null ? root.getModel().getModule() != projectSolution : !backend.workspaceModel(root.getModel()));
      pendingNavigation = elemental2.core.Global.JSON.stringify(navigation);
      elemental2.dom.DomGlobal.document.dispatchEvent(new elemental2.dom.Event("alef-reference-navigation"));
      return editor;
    });
  }
  public static String takeNavigation() {
    String result = pendingNavigation;
    pendingNavigation = "null";
    return result;
  }
  public static String testResult() { return testResult; }
  public static String testFailure() { return testFailure; }
  public static String diagnostics(int editor) {
    return host.getProject().getModelAccess().computeReadAction(() -> {
      StringBuilder result = new StringBuilder();
      for (jetbrains.mps.openapi.editor.message.SimpleEditorMessage message : component(editor).getHighlightManager().getMessages()) {
        result.append(message.getStatus()).append(": ").append(message.getMessage()).append('\n');
      }
      return result.toString();
    });
  }
  public static void runTest() {
    if (!"READY".equals(status)) return;
    testResult = "Running…";
    testFailure = "";
    SwingUtilities.invokeLater(() -> {
      try {
        host.getProject().getModelAccess().executeCommand(() -> {
          boolean passed = interpreter.debug.behavior.LRootAction__BehaviorDescriptor.evalAsBool_id30SQGvmToNE
              .invoke(serviceTest, new testspraak.translator.TestSpraakRunParameters(true));
          testResult = passed ? "PASS" : "FAIL";
          testspraak.behavior.UserState.of(serviceTest).setResult(passed);
          testEditor.getUpdater().update();
          testEditor.relayout();
          if (!passed) {
            StringBuilder details = new StringBuilder();
            for (interpreter.runtime.RtObject object : interpreter.runtime.RuntimeController.INSTANCE
                .getRtRootAction().extent(jetbrains.mps.smodel.SNodeUtil.concept_BaseConcept)) {
              details.append(object).append('\n');
            }
            testFailure = details.toString();
          }
        });
      } catch (Throwable failure) {
        failure.printStackTrace();
        testResult = "ERROR: " + failure;
        testFailure = java.util.Arrays.toString(failure.getStackTrace());
      }
    });
  }
  public static void showPair(int pair) {
    SwingUtilities.invokeLater(() -> {
      if (!"READY".equals(status)) return;
      window.getContentPane().removeAll();
      window.getContentPane().add(pair == 0 ? dataPane : servicePane);
      window.getContentPane().add(pair == 0 ? expressionPane : testPane);
      window.getContentPane().revalidate();
      window.getContentPane().repaint();
    });
  }
  private static NodeEditorComponent component(int index) {
    switch (index) {
      case 0: return dataEditor;
      case 1: return expressionEditor;
      case 2: return serviceEditor;
      case 3: return testEditor;
      default: throw new IllegalArgumentException("Unknown editor " + index);
    }
  }
  public static void resize(int pixels) {
    width = Math.max(640, pixels);
    SwingUtilities.invokeLater(() -> {
      if (window != null) window.setSize(width, 620);
      if (inspector != null) inspector.resize(width);
    });
  }
  /** Size the native editor surfaces to the web IDE's available workspace. */
  public static void layout(int pixels, int editorHeight, int inspectorHeight) {
    width = Math.max(240, pixels);
    workspaceEditorHeight = Math.max(60, editorHeight);
    workspaceInspectorHeight = Math.max(1, inspectorHeight);
    SwingUtilities.invokeLater(() -> {
      if (window != null) window.setSize(width, Math.max(60, editorHeight));
      if (inspector != null) inspector.layout(width, editorHeight + 32, inspectorHeight);
    });
  }

  /** Read-only observations. Type inference is requested explicitly by the page. */
  public static String value(int editor) {
    if (!"READY".equals(status)) return "";
    return host.getProject().getModelAccess().computeReadAction(() -> editor == 2 ? raw(entrypoint, "name")
        : editor == 3 ? raw(testAge, "waarde") : editor == 0
        ? raw(person, "name")
        : currentAmount() == null ? "—" : raw(currentAmount(), "waarde"));
  }

  public static String type(int editor) {
    if (!"READY".equals(status)) return "";
    return host.getProject().getModelAccess().computeReadAction(() -> {
      if (editor == 2 || editor == 3) {
        SNode mapped = editor == 2
            ? gegevensspraak.behavior.Typed__BehaviorDescriptor.type_id2noOFp_6pJO.invoke(ageField)
            : testspraak.behavior.ElementairTestBerichtVeld__BehaviorDescriptor.internType_id6qqZC3QeXHw.invoke(testAge);
        return mapped == null ? "No mapped type" : mapped.getConcept().getName();
      }
      TypecheckingFacade facade = TypecheckingFacade.getFromContext();
      NodeEditorComponent component = component(editor);
      SNode node = editor == 0 ? birthDate : currentExpression();
      if (node == null) return "No expression";
      SNode type = facade.computeWithSession(component.getTypecheckingSession(), () -> facade.getTypeOf(node));
      return type == null ? "No type inferred" : type.getConcept().getName();
    });
  }

  public static void focus(int editor) {
    SwingUtilities.invokeLater(() -> {
      if (!"READY".equals(status)) return;
      host.getProject().getModelAccess().runReadAction(() -> {
        NodeEditorComponent component = component(editor);
        SNode node = editor == 2 ? entrypoint : editor == 3 ? testAge : editor == 0 ? person : currentAmount();
        String id = editor == 3 ? "EBWEC_property_waarde" : editor == 0 || editor == 2 ? "property_name" : "property_waarde";
        EditorCell_Label cell = (EditorCell_Label) component.findCellWithId(node, id);
        if (cell == null) throw new IllegalStateException("The selected example cell no longer exists; reset the examples");
        component.changeSelection(cell);
        cell.selectAll();
        component.requestFocusInWindow();
      });
    });
  }

  /** Executes ALEF's generated sorting intention in a model command. */
  public static void sortObjectModel() {
    SwingUtilities.invokeLater(() -> {
      if (!"READY".equals(status)) return;
      host.getProject().getModelAccess().executeCommand(() -> {
        for (IntentionExecutable intention : new gegevensspraak.intentions.SorteerObjectModel_Intention()
            .instances(objectModel, dataEditor.getEditorContext())) {
          if (intention.isApplicable(objectModel, dataEditor.getEditorContext())) {
            intention.execute(objectModel, dataEditor.getEditorContext());
            return;
          }
        }
        throw new IllegalStateException("Sorting intention is unavailable for this object model");
      });
    });
  }

  private static boolean canEditActiveRoot() {
    return backend == null || host.getProject().getModelAccess().computeReadAction(() -> backend.canChangeNode(activeRoot));
  }
  public static boolean canUndo() { return "READY".equals(status) && canEditActiveRoot() && history().isUndoAvailable(null); }
  public static boolean canRedo() { return "READY".equals(status) && canEditActiveRoot() && history().isRedoAvailable(null); }
  private static void installLeaseHistory(NodeEditorComponent editor) {
    // Host shortcuts bypass native cell actions; consume them while read-only
    // so they cannot replay either model history or the DOM input proxy's history.
    for (int modifier : new int[]{java.awt.event.InputEvent.CTRL_DOWN_MASK, java.awt.event.InputEvent.META_DOWN_MASK}) {
      var input = editor.getInputMap(javax.swing.JComponent.WHEN_FOCUSED);
      input.put(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Z, modifier), "lease-undo");
      // MPS Windows uses Ctrl+Y for Delete Line; only Command+Y aliases Redo.
      if (modifier == java.awt.event.InputEvent.META_DOWN_MASK) {
        input.put(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Y, modifier), "lease-redo");
      }
      input.put(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Z, modifier | java.awt.event.InputEvent.SHIFT_DOWN_MASK), "lease-redo");
    }
    for (boolean redo : new boolean[]{false, true}) editor.getActionMap().put(redo ? "lease-redo" : "lease-undo", new javax.swing.AbstractAction() {
      public void actionPerformed(java.awt.event.ActionEvent event) {
        if (!editor.isFocusOwner() || editor.isReadOnly()) return;
        if (redo) AlefEditorApp.redo(); else AlefEditorApp.undo();
      }
    });
  }
  public static void undo() { SwingUtilities.invokeLater(() -> { if (canUndo()) history().undo(null); }); }
  public static void redo() { SwingUtilities.invokeLater(() -> { if (canRedo()) history().redo(null); }); }
  private static UndoManager history() { return UndoManager.getInstance(host.getIdeaProject()); }
  public static void setRootLease(String rootId, double deadline) { if (backend != null) backend.lease(rootId, deadline); }
  public static void setModelLease(String model, double deadline) { if (backend != null) backend.modelLease(model, deadline); }
  public static void reserveRoot(String model, String rootId, String nodeId, double deadline) { backend.reserve(model, rootId, nodeId, deadline); }
  public static void deleteRoot(String reference) {
    host.getProject().getModelAccess().executeCommand(() -> {
      SNode root = org.jetbrains.mps.openapi.persistence.PersistenceFacade.getInstance().createNodeReference(reference).resolve(host.getProject().getRepository());
      if (root == null || root.getParent() != null || !backend.workspaceModel(root.getModel())) throw new IllegalArgumentException("Choose a workspace root");
      root.getModel().removeRootNode(root);
    });
  }
  public static void freezeEditing() { if (backend != null) backend.freeze(); }
  private static void loadingFailed(Throwable failure){status="FAIL: "+failure;failure.printStackTrace();}
  public static String initializationProgress(){return backend==null?"{\"completed\":0,\"total\":0}":backend.loadProgress();}
  public static String containmentTargets(String source) { return host.getProject().getModelAccess().computeReadAction(() -> backend.containmentTargets(source)); }
  public static void moveContent(String source,String target,String role,String before) { host.getProject().getModelAccess().executeCommand(() -> backend.moveContent(source,target,role,before)); }
  public static String dirtyRoots() { return backend == null ? "[]" : backend.dirtyRoots(); }
  public static void acknowledgeSave(String snapshots) {
    host.getProject().getModelAccess().runReadAction(() -> backend.acknowledgeSave(snapshots));
  }
  public static String rootSnapshot(String rootId) {
    return host.getProject().getModelAccess().computeReadAction(() -> {
      if (backend != null) return backend.rootSnapshot(rootId);
      SNode root = org.jetbrains.mps.openapi.persistence.PersistenceFacade.getInstance()
          .createNodeReference(rootId).resolve(host.getProject().getRepository());
      if (root == null || root.getParent() != null || root.getModel().getModule() != projectSolution)
        throw new IllegalArgumentException("Choose a root in this project");
      return elemental2.core.Global.JSON.stringify(new BackendModelBridge(host).encode(root));
    });
  }

  public static void close() {
    SwingUtilities.invokeLater(() -> {
      if ("CLOSED".equals(status)) return;
      closeComparisons();
      if (backend != null) { backend.close(); backend = null; }
      GrammarCellsRuntime.dispose();
      if (inspector != null) { inspector.close(); inspector = null; }
      if (window != null) window.dispose();
      if (theme != null) theme.close();
      if (host != null) host.dispose();
      rootEditors.clear();
      pendingNavigation = "null";
      if (languages != null) languages.close();
      WebAwt.uninstall();
      status = "CLOSED";
    });
  }

  private static void createModel() {
    SolutionDescriptor descriptor = new SolutionDescriptor();
    descriptor.setId(ModuleId.regular(new UUID(20260916, 101)));
    descriptor.setNamespace("alefj2cl.example");
    Solution solution = new Solution(descriptor, null) { @Override public boolean isPackaged() { return false; } };
    projectSolution = solution;
    host.getProject().addModule(solution);
    model = new MemoryModel(new SModelReference(solution.getModuleReference(), SModelId.regular(new UUID(20260916, 102)), "alefj2cl.example.playground"));
    solution.registerModel(model);
    model.addLanguage(concepts.get("DatumTijdLiteral").getLanguage());
    model.addLanguage(concepts.get("Haakjes").getLanguage());
    model.addLanguage(concepts.get("Witruimte").getLanguage());
    objectModel = named("ObjectModel", "Huishouden");
    model.addRootNode(objectModel);
    person = named("ObjectType", "Persoon");
    set(person, "bezield", "true");
    set(person, "meervoudsvorm", "personen");
    child(objectModel, "elem", person);
    attribute(person, "naam", node("TekstType"));
    SNode age = attribute(person, "leeftijd", numericType(0));
    SNode dateType = node("DatumTijdType");
    enumeration(dateType, "granulariteit", "dag");
    birthDate = attribute(person, "geboortedatum", dateType);
    SNode allowance = attribute(person, "toeslag", numericType(2));
    SNode home = named("ObjectType", "Woning");
    set(home, "meervoudsvorm", "woningen");
    child(objectModel, "elem", home);
    attribute(home, "adres", node("TekstType"));
    attribute(home, "huur", numericType(2));
    SNode occupancy = named("FeitType", "Bewoning");
    child(objectModel, "elem", occupancy);
    SNode resident = named("Rol", "bewoner");
    set(resident, "single", "true");
    set(resident, "frase", "woont in");
    reference(resident, "objectType", person);
    child(occupancy, "rollen", resident);
    SNode dwelling = named("Rol", "woning");
    set(dwelling, "single", "true");
    reference(dwelling, "objectType", home);
    child(occupancy, "rollen", dwelling);
    SNode threshold = named("Parameter", "leeftijdsgrens");
    child(threshold, "type", numericType(0));
    child(objectModel, "elem", threshold);
    child(objectModel, "elem", node("Witruimte"));

    ruleGroup = named("Regelgroep", "Bereken toeslag");
    ruleGroup.setProperty(jetbrains.mps.smodel.SNodeUtil.property_BaseConcept_virtualPackage, "rules.allowance");
    model.addRootNode(ruleGroup);
    rule = named("Regel", "Basistoeslag");
    child(ruleGroup, "inhoud", rule);
    SNode version = node("RegelVersie");
    child(rule, "versie", version);
    child(version, "geldig", node("Geldigheidsperiode"));
    SNode statement = node("ActieIndienVoorwaarde");
    child(version, "statement", statement);
    assignment = node("Gelijkstelling");
    child(statement, "actie", assignment);
    SNode subject = node("UnivOnderwerp");
    reference(subject, "base", person);
    child(assignment, "links", selection(allowance, subject));
    expression = node("Haakjes");
    SNode plus = node("PlusExpressie");
    amount = number("100");
    child(plus, "links", amount);
    child(plus, "rechts", number("25"));
    child(expression, "waarde", plus);
    child(assignment, "rechts", expression);
    SNode condition = node("EnkeleVoorwaarde");
    child(statement, "conditie", condition);
    SNode subjectReference = node("OnderwerpRef");
    reference(subjectReference, "ref", subject);
    child(condition, "expr", selection(age, subjectReference));
    SNode comparison = node("Vergelijking");
    enumeration(comparison, "operator", "GE");
    child(comparison, "rechts", number("18"));
    child(condition, "predicaat", comparison);
    child(ruleGroup, "inhoud", node("Witruimte"));
    createServiceAndTest(age, allowance);
  }

  private static void createServiceAndTest(SNode age, SNode allowance) {
    model.addLanguage(concepts.get("Service").getLanguage());
    model.addLanguage(concepts.get("ServiceTestSet").getLanguage());
    service = named("Service", "Toeslagservice");
    service.setProperty(jetbrains.mps.smodel.SNodeUtil.property_BaseConcept_virtualPackage, "services");
    model.addRootNode(service);
    set(service, "projectnaam", "hhd");
    set(service, "componentnaam", "toeslag");
    set(service, "versienummer", "1.0.0");
    set(service, "serviceAcroniem", "TS");
    set(service, "serviceNamespace", "https://example.org/toeslag");
    set(service, "xsdNamespace", "https://example.org/toeslag");
    set(service, "namespacePrefix", "ts");
    SNode input = named("Invoerberichtmapping", "Aanvraag");
    reference(input, "object", person);
    child(service, "mapping", input);
    ageField = named("DirectInvoerAttribuut", "leeftijd");
    reference(ageField, "attr", age);
    child(input, "veld", ageField);
    SNode output = named("Uitvoerberichtmapping", "Besluit");
    reference(output, "object", person);
    child(service, "mapping", output);
    SNode allowanceField = named("DirectUitvoerAttribuut", "toeslag");
    reference(allowanceField, "attr", allowance);
    child(output, "veld", allowanceField);
    entrypoint = named("Entrypoint", "BerekenToeslag");
    set(entrypoint, "acroniem", "BT");
    set(entrypoint, "soapOperatie", "berekenToeslag");
    set(entrypoint, "soapAction", "https://example.org/toeslag/berekenToeslag");
    set(entrypoint, "xmlRekenmomentVeld", "rekenmoment");
    set(entrypoint, "xmlBerichtType", "Toeslag");
    set(entrypoint, "xmlBerichtInType", "ToeslagRequest");
    set(entrypoint, "xmlBerichtUitType", "ToeslagResponse");
    child(service, "entrypoints", entrypoint);
    SNode rules = node("RegelsetRef");
    reference(rules, "set", ruleGroup);
    child(entrypoint, "regelgroep", rules);
    SNode personInput = named("ComplexInvoerBerichtVeld", "persoon");
    reference(personInput, "sub", input);
    set(personInput, "minOccurs", "1");
    set(personInput, "maxOccurs", "1");
    child(entrypoint, "invoer", personInput);
    SNode personOutput = named("ComplexUitvoerBerichtVeld", "persoon");
    reference(personOutput, "sub", output);
    set(personOutput, "minOccurs", "1");
    set(personOutput, "maxOccurs", "1");
    child(entrypoint, "uitvoer", personOutput);
    testSet = node("ServiceTestSet");
    testSet.setProperty(jetbrains.mps.smodel.SNodeUtil.property_BaseConcept_virtualPackage, "services.tests");
    enumeration(testSet, "testSetType", "service");
    set(testSet, "simpleName", "Toeslag voor volwassenen");
    reference(testSet, "entrypoint", entrypoint);
    model.addRootNode(testSet);
    serviceTest = named("ServiceTest", "Volwassene ontvangt 125");
    child(testSet, "testGevallen", serviceTest);
    SNode request = node("TestInvoerBericht");
    set(request, "jaar", "2026");
    child(serviceTest, "invoer", request);
    SNode testPerson = node("ComplexTestBerichtVeld");
    reference(testPerson, "veld", personInput);
    child(request, "veld", testPerson);
    SNode inputValues = node("InvoerSubBericht");
    child(testPerson, "subs", inputValues);
    testAge = node("ElementairTestBerichtVeld");
    reference(testAge, "veld", ageField);
    set(testAge, "waarde", "21");
    child(inputValues, "veld", testAge);
    SNode response = node("TestUitvoerBericht");
    set(response, "serviceversie", "1.0.0");
    set(response, "resultaatcode", "1");
    set(response, "resultaatmelding", "SERVICE_OK");
    set(response, "consistentieverwachting", "true");
    child(serviceTest, "voorspelling", response);
    SNode expectedPerson = node("ComplexeVeldVerwachting");
    reference(expectedPerson, "veld", personOutput);
    child(response, "veld", expectedPerson);
    SNode outputValues = node("UitvoerSubBericht");
    child(expectedPerson, "subs", outputValues);
    expectedAllowance = node("ElementaireVeldVerwachting");
    reference(expectedAllowance, "veld", allowanceField);
    set(expectedAllowance, "waarde", "125");
    child(outputValues, "veldVerwachting", expectedAllowance);
  }

  private static void index(StructureAspectDescriptor structure) {
    for (ConceptDescriptor descriptor : structure.getDescriptors()) {
      String name = descriptor.getConceptFqName();
      concepts.put(name.substring(name.lastIndexOf('.') + 1), MetaAdapterFactory.getConcept(descriptor.getId(), name));
      if (descriptor.isRootable() && !descriptor.isAbstract() && !descriptor.isInterfaceConcept()) {
        rootConcepts.put(name, MetaAdapterFactory.getConcept(descriptor.getId(), name));
      }
    }
  }
  private static SNode node(String concept) { return model.createNode(concepts.get(concept)); }
  private static SNode named(String concept, String name) {
    SNode node = node(concept);
    set(node, "name", name);
    return node;
  }
  private static void set(SNode node, String name, String value) { node.setProperty(property(node, name), value); }
  private static SNode numericType(int decimals) {
    SNode type = node("NumeriekType");
    set(type, "decimalen", Integer.toString(decimals));
    return type;
  }
  private static SNode attribute(SNode owner, String name, SNode type) {
    SNode attribute = named("Attribuut", name);
    child(attribute, "type", type);
    child(owner, "elem", attribute);
    return attribute;
  }
  private static SNode selection(SNode attribute, SNode object) {
    SNode selection = node("Selectie");
    SNode selector = node("AttribuutSelector");
    reference(selector, "base", attribute);
    child(selection, "selector", selector);
    child(selection, "object", object);
    return selection;
  }
  private static void reference(SNode node, String name, SNode target) {
    for (SReferenceLink link : node.getConcept().getReferenceLinks()) {
      if (name.equals(link.getName())) { node.setReferenceTarget(link, target); return; }
    }
    throw new IllegalArgumentException("Missing reference " + name);
  }
  private static void enumeration(SNode node, String name, String value) {
    SProperty property = property(node, name);
    SEnumeration type = (SEnumeration) property.getType();
    for (SEnumerationLiteral literal : type.getLiterals()) {
      if (value.equals(literal.getName()) || value.equals(literal.getPresentation())) {
        node.setProperty(property, type.toString(literal));
        return;
      }
    }
    throw new IllegalArgumentException("Missing enumeration value " + value);
  }
  private static SNode currentExpression() {
    for (SNode child : assignment.getChildren()) if ("rechts".equals(child.getContainmentLink().getName())) return child;
    return null;
  }
  private static SNode currentAmount() { return findNumber(currentExpression()); }
  private static SNode findNumber(SNode node) {
    if (node == null) return null;
    if (node.getConcept().isSubConceptOf(concepts.get("NumeriekeLiteral"))) return node;
    for (SNode child : node.getChildren()) {
      SNode number = findNumber(child);
      if (number != null) return number;
    }
    return null;
  }
  private static SNode number(String value) {
    SNode node = node("NumeriekeLiteral");
    node.setProperty(property(node, "waarde"), value);
    return node;
  }
  private static SProperty property(SNode node, String name) {
    for (SProperty property : node.getConcept().getProperties()) if (name.equals(property.getName())) return property;
    throw new IllegalArgumentException("Missing property " + name);
  }
  private static String raw(SNode node, String name) {
    String value = node.getProperty(property(node, name));
    return value == null ? "" : value;
  }
  /** Read-only model and editor observations for the browser smoke test. */
  public static String outline(int editor) {
    return host.getProject().getModelAccess().computeReadAction(() -> {
      StringBuilder result = new StringBuilder();
      describe(editor == 2 ? service : editor == 3 ? testSet : editor == 0 ? objectModel : ruleGroup, result, "");
      return result.toString();
    });
  }
  private static void describe(SNode node, StringBuilder result, String indent) {
    result.append(indent).append(node.getConcept().getName());
    for (SProperty property : node.getProperties()) result.append(" ").append(property.getName()).append("=").append(node.getProperty(property));
    for (org.jetbrains.mps.openapi.model.SReference reference : node.getReferences()) {
      SNode target = reference.getTargetNode();
      if (target == null) throw new IllegalStateException("Unresolved example reference");
      result.append(" ").append(reference.getLink().getName()).append("->").append(target.getConcept().getName());
    }
    result.append("\n");
    for (SNode child : node.getChildren()) describe(child, result, indent + "  ");
  }
  public static String editorText(int editor) {
    return host.getProject().getModelAccess().computeReadAction(() -> {
      StringBuilder result = new StringBuilder();
      cellText(component(editor).getRootCell(), result);
      return result.toString();
    });
  }
  private static void cellText(EditorCell cell, StringBuilder result) {
    if (cell instanceof EditorCell_Label) result.append(((EditorCell_Label) cell).getText()).append(" ");
    if (cell instanceof EditorCell_Collection) {
      for (EditorCell child : (EditorCell_Collection) cell) cellText(child, result);
    }
  }
  private static void child(SNode node, String name, SNode child) {
    for (SContainmentLink link : node.getConcept().getContainmentLinks()) {
      if (name.equals(link.getName())) { node.addChild(link, child); return; }
    }
    throw new IllegalArgumentException("Missing child role " + name);
  }
  private static final class MemoryModel extends EditableModelDescriptor {
    MemoryModel(org.jetbrains.mps.openapi.model.SModelReference reference) { super(reference, new NullDataSource()); }
    @Override protected ModelLoadResult<jetbrains.mps.smodel.SModel> createModel() {
      return new ModelLoadResult<>(new jetbrains.mps.smodel.SModel(getReference()), ModelLoadingState.FULLY_LOADED);
    }
    @Override public boolean isReadOnly() { return false; }
    @Override public void save() { throw new UnsupportedOperationException("This example is an in-memory session"); }
    @Override public void reloadFromSource() { throw new UnsupportedOperationException("Reload the page to reset the example"); }
  }
}
