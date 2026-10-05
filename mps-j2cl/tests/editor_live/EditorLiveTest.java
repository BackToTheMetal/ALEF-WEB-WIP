package mpsj2cl.editorlive;

import com.intellij.openapi.command.undo.UndoManager;
import java.awt.datatransfer.DataFlavor;
import java.awt.Toolkit;
import java.awt.BorderLayout;
import java.awt.event.KeyEvent;
import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import jetbrains.mps.intentions.IntentionsManager;
import jetbrains.mps.nodeEditor.NodeEditorComponent;
import jetbrains.mps.classloading.ModuleClassLoader;
import jetbrains.mps.nodeEditor.EditorMessage;
import jetbrains.mps.nodeEditor.checking.UpdateResult;
import jetbrains.mps.nodeEditor.cells.EditorCell_Property;
import jetbrains.mps.nodeEditor.cells.EditorCell_Label;
import jetbrains.mps.openapi.editor.cells.SubstituteAction;
import jetbrains.mps.openapi.editor.cells.CellActionType;
import jetbrains.mps.openapi.editor.selection.SelectionInfo;
import jetbrains.mps.openapi.editor.message.SimpleEditorMessage;
import jetbrains.mps.openapi.intentions.IntentionExecutable;
import jetbrains.mps.project.ModuleId;
import jetbrains.mps.project.Solution;
import jetbrains.mps.project.structure.modules.SolutionDescriptor;
import jetbrains.mps.smodel.EditableModelDescriptor;
import jetbrains.mps.smodel.ModelLoadResult;
import jetbrains.mps.smodel.SModelId;
import jetbrains.mps.smodel.SModelReference;
import jetbrains.mps.smodel.loading.ModelLoadingState;
import jetbrains.mps.typesystem.checking.TypesEditorChecker;
import jetbrains.mps.typesystem.checking.AbstractTypesystemEditorChecker;
import jetbrains.mps.typesystem.checking.NonTypesystemEditorChecker;
import jetbrains.mps.typechecking.TypecheckingFacade;
import jetbrains.mps.typechecking.TypecheckingSession;
import jetbrains.mps.util.Cancellable;
import jetbrains.mps.util.Pair;
import mpsj2cl.editor.BrowserEditorHost;
import org.jetbrains.mps.openapi.model.SNode;
import org.jetbrains.mps.openapi.language.SConcept;
import org.jetbrains.mps.openapi.persistence.NullDataSource;
import org.webawt.WebAwt;

/** Real editor and model-event pipeline exercised by an explicitly handwritten language. */
public final class EditorLiveTest {
  private static BrowserEditorHost host;
  private static NodeEditorComponent editor;
  private static JFrame window;
  private static Solution solution;
  private static MemoryModel model;
  private static SNode literal;
  private static String result;
  private static String keyboardState;
  private static String progress = "not started";
  private static TypesEditorChecker checker;
  private static NonTypesystemEditorChecker nonTypesystemChecker;
  private static Instant lastChecked = Instant.MIN;
  private static Instant lastNonTypesystemCheck = Instant.MIN;

  public static void start() {
    phase("install WebAWT");
    WebAwt.install("mps-editor-root");
    SwingUtilities.invokeLater(() -> {
      try {
        createEditor();
        runContracts();
        closeEditor();
        createEditor();
        runContracts();
        result = "PASS";
      } catch (Throwable failure) { failure.printStackTrace(); result = "FAIL: " + failure; }
    });
  }
  public static String getResult() { return result; }
  public static String getProgress() { return progress; }
  public static String getKeyboardState() { return keyboardState; }
  public static String getPropertyScreenBounds() {
    return host.getProject().getModelAccess().computeReadAction(() -> {
      EditorCell_Property cell = propertyCell();
      java.awt.Point origin = editor.getLocationOnScreen();
      return (origin.x + cell.getX()) + "," + (origin.y + cell.getY()) + "," + cell.getWidth() + "," + cell.getHeight();
    });
  }
  public static int getIntentionQueryCount() { return ExpressionLanguage.intentionQueries; }
  public static int getIntentionBulbCount() {
    if (editor == null || editor.isDisposed()) return 0;
    int count = 0;
    for (java.awt.Component component : editor.getComponents()) {
      if (component instanceof jetbrains.mps.intentions.LightBulbMenu && component.isShowing()) count++;
    }
    return count;
  }
  public static int getAutomaticErrorCount() {
    return host.getProject().getModelAccess().computeReadAction(() -> {
      int count = 0;
      for (SimpleEditorMessage message : editor.getHighlightManager().getMessagesFor(literal)) {
        if (message.getOwner() instanceof TypesEditorChecker && message.getOwner() != checker) count++;
      }
      return count;
    });
  }
  public static String getValue() {
    return host.getProject().getModelAccess().computeReadAction(() -> literal.getProperty(ExpressionLanguage.VALUE));
  }
  public static void prepareKeyboard() {
    SwingUtilities.invokeLater(() -> {
      try {
        host.getProject().getModelAccess().runReadAction(() -> {
          editor.getUpdater().flushModelEvents();
          editor.selectNode(literal, "value");
          propertyCell().selectAll();
          require(editor.requestFocusInWindow() && editor.isFocusOwner(), "real editor receives keyboard focus");
        });
        keyboardState = "READY";
      } catch (Throwable failure) { failure.printStackTrace(); keyboardState = "FAIL: " + failure; }
    });
  }
  public static void prepareIme() {
    SwingUtilities.invokeLater(() -> {
      try {
        host.getProject().getModelAccess().runReadAction(() -> {
          editor.getUpdater().flushModelEvents();
          editor.selectNode(literal, "value");
          propertyCell().deselectAll();
          propertyCell().setCaretPosition(propertyCell().getText().length());
          require(editor.requestFocusInWindow() && editor.isFocusOwner(), "real editor retains focus for IME input");
        });
        keyboardState = "IME_READY";
      } catch (Throwable failure) { failure.printStackTrace(); keyboardState = "FAIL: " + failure; }
    });
  }
  public static void showDiagnostic() {
    SwingUtilities.invokeLater(() -> {
      try {
        phase("automatic host diagnostic");
        // Only the host Highlighter may publish/clear the next diagnostic.
        editor.getHighlightManager().clearForOwner(checker, false);
        editor.getHighlightManager().clearForOwner(nonTypesystemChecker, false);
        host.getProject().getModelAccess().executeCommand(() -> literal.setProperty(ExpressionLanguage.VALUE, "invalid"));
        host.getProject().getModelAccess().runReadAction(() -> {
          editor.getUpdater().flushModelEvents();
        });
        keyboardState = "DIAGNOSTIC_PENDING";
      } catch (Throwable failure) { failure.printStackTrace(); keyboardState = "FAIL: " + failure; }
    });
  }
  public static void dispose() {
    SwingUtilities.invokeLater(() -> {
      try {
        closeEditor();
        WebAwt.uninstall();
        keyboardState = "DISPOSED";
      } catch (Throwable failure) { failure.printStackTrace(); keyboardState = "FAIL: " + failure; }
    });
  }

  private static void closeEditor() {
    phase("dispose editor and host");
    // Editor/session listeners must release model/language deployment tokens first.
    editor.dispose();
    require(host.getEditorCount() == 0, "individually disposed editor leaves host tracking");
    window.dispose();
    host.getProject().getModelAccess().executeCommand(() -> {
      solution.unregisterModel(model);
      host.getProject().removeModule(solution);
    });
    jetbrains.mps.project.MPSProject disposedProject = host.getProject();
    UndoManager disposedHistory = UndoManager.getInstance(host.getIdeaProject());
    host.dispose();
    host.dispose();
    require(editor.isDisposed() && disposedProject.isDisposed(), "editor and project dispose before restart");
    require(!disposedHistory.isUndoAvailable(null), "disposed project releases model undo history");
    try {
      ModuleClassLoader.getRegisteredClassName(jetbrains.mps.nodeEditor.selection.EditorCellLabelSelection.class);
      throw new AssertionError("disposed selection factory retained its binary name owner");
    } catch (IllegalStateException expected) {}
    try { host.getPlatform(); throw new AssertionError("disposed browser host remained usable"); } catch (IllegalStateException expected) {}
  }

  private static void createEditor() {
    phase("create browser editor host");
    host = new BrowserEditorHost("Live editor fixture");
    phase("register handwritten language aspects");
    host.getPlatform().registerLanguage(ExpressionLanguage.coreLanguage());
    host.getPlatform().registerLanguage(ExpressionLanguage.typeVariableLanguage());
    ExpressionLanguage language = new ExpressionLanguage();
    host.getPlatform().registerLanguage(language, ExpressionLanguage.LiteralRule.class, ExpressionLanguage.AvoidThirteenRule.class,
        ExpressionLanguage.GroupRule.class, ExpressionLanguage.ReferenceRule.class);
    phase("attach solution and editable model");
    host.getProject().getModelAccess().executeCommand(() -> {
      SolutionDescriptor descriptor = new SolutionDescriptor();
      descriptor.setId(ModuleId.regular(new UUID(94, 113)));
      descriptor.setNamespace("mpsj2cl.editorlive.solution");
      solution = new MemorySolution(descriptor);
      require(!solution.isPackaged() && !solution.isReadOnly(), "in-memory editor solution is writable before repository listeners attach");
      host.getProject().addModule(solution);
      model = new MemoryModel(new SModelReference(solution.getModuleReference(), SModelId.regular(new UUID(94, 114)), "mpsj2cl.editorlive.model"));
      solution.registerModel(model);
      model.addLanguage(language.getIdentity());
      literal = model.createNode(ExpressionLanguage.LITERAL);
      literal.setProperty(ExpressionLanguage.VALUE, "1");
      model.addRootNode(literal);
    });
    phase("create real NodeEditorComponent");
    host.getProject().getModelAccess().runReadAction(() -> editor = host.createEditor(literal));
    checker = new TypesEditorChecker(host.getProject().getRepository(), Collections.emptyList());
    nonTypesystemChecker = new NonTypesystemEditorChecker(host.getProject().getRepository(), Collections.emptyList());
    lastChecked = Instant.MIN;
    lastNonTypesystemCheck = Instant.MIN;
    require(literal.getModel() == model && model.getRepository() != null, "editable node attached to a real model repository");
    window = new JFrame("MPS typed expression — handwritten fixture");
    window.getContentPane().add(editor.getExternalComponent(), BorderLayout.CENTER);
    window.setSize(720, 260);
    window.setVisible(true);
    editor.relayout();
    phase("editor window visible");
  }

  private static void verifyHostOwnership() {
    phase("reject overlapping host without changing live services");
    com.intellij.compat.BrowserApplication application = host.getApplication();
    jetbrains.mps.smodel.BrowserModelAccess modelAccess = jetbrains.mps.smodel.BrowserModelAccess.getInstalled();
    jetbrains.mps.smodel.language.LanguageRegistry registry = host.getPlatform().findComponent(jetbrains.mps.smodel.language.LanguageRegistry.class);
    jetbrains.mps.datatransfer.DataTransferManager transfers = jetbrains.mps.datatransfer.DataTransferManager.getInstance();
    host.getProject().getModelAccess().runReadAction(transfers::buildCache);
    try {
      BrowserEditorHost unexpected = new BrowserEditorHost("Rejected overlapping host");
      unexpected.dispose();
      throw new AssertionError("second live browser host was accepted");
    } catch (IllegalStateException expected) {
      require(expected.getMessage().contains("already installed"), "second host rejects the occupied model-access owner");
    }
    require(com.intellij.openapi.application.ApplicationManager.getApplication() == application && !application.isDisposed(),
        "failed host preserves the original application");
    require(jetbrains.mps.smodel.BrowserModelAccess.getInstalled() == modelAccess,
        "failed host preserves the original model access");
    require(jetbrains.mps.smodel.language.LanguageRegistry.getInstance() == registry,
        "failed host preserves the original language registry");
    require(jetbrains.mps.datatransfer.DataTransferManager.getInstance() == transfers,
        "failed host preserves the exact transfer manager and its registry listener");
    require(!host.getProject().isDisposed() && !editor.isDisposed() && host.getEditorCount() == 1,
        "failed host preserves the live project and editor");
    host.getProject().getModelAccess().runReadAction(() -> {
      require(literal.getReference().resolve(host.getProject().getRepository()) == literal && "1".equals(propertyCell().getText()),
          "failed host preserves the attached model and cell tree");
    });

    phase("dispose an editor independently from its host");
    NodeEditorComponent additional = host.getProject().getModelAccess().computeReadAction(() -> host.createEditor(literal));
    require(host.getEditorCount() == 2, "host tracks each newly created editor");
    additional.dispose();
    require(additional.isDisposed() && !editor.isDisposed() && host.getEditorCount() == 1,
        "individual disposal releases only the disposed editor from host tracking");
    // The existing keyboard, inference, clipboard and undo contracts now run on the surviving host.
  }

  private static void runContracts() {
    verifyHostOwnership();
    phase("icon resources and search");
    EditorResourceContracts.run(host);
    FindUsagesContracts.run(host, solution);
    phase("initial cells and typechecking");
    host.getProject().getModelAccess().runReadAction(() -> {
      require(ExpressionLanguage.editorBuilds > 0 && editor.getRootCell() != null, "language editor builds a real cell tree");
      require("1".equals(propertyCell().getText()), "property cell reads the attached model");
      require(checkErrors() == 0, "initial integer has no typesystem diagnostic");
      require(checkWarnings() == 0, "initial integer has no non-typesystem warning");
      require(ExpressionLanguage.inferenceRuns > 0, "real typesystem executes the language rule");
    });

    // Real keyboard pipeline: selection -> EditorComponent handlers -> property accessor -> command/model event.
    phase("keyboard model edit");
    host.getProject().getModelAccess().runReadAction(() -> {
      editor.selectNode(literal, "value");
      propertyCell().selectAll();
    });
    editor.processKeyTyped(new KeyEvent(editor, KeyEvent.KEY_TYPED, System.currentTimeMillis(), 0, KeyEvent.VK_UNDEFINED, '2'));
    require("2".equals(getValue()), "keyboard event changes the model property");
    require(ExpressionLanguage.constraintChecks > 0, "keyboard edit invokes the language property constraint");

    // No direct cell synchronization or explicit type invalidation here: both consume real model events.
    phase("model events and diagnostic invalidation");
    int inferenceBefore = ExpressionLanguage.inferenceRuns;
    host.getProject().getModelAccess().executeCommand(() -> literal.setProperty(ExpressionLanguage.VALUE, "invalid"));
    host.getProject().getModelAccess().runReadAction(() -> {
      editor.getUpdater().flushModelEvents();
      require("invalid".equals(propertyCell().getText()), "model event updates the existing property cell");
      require(checkErrors() == 1, "model event invalidates typechecking and creates the diagnostic");
      require(ExpressionLanguage.inferenceRuns > inferenceBefore, "type inference reruns after property event");
    });

    phase("completion and undo");
    host.getProject().getModelAccess().executeCommand(() -> {
      List<SubstituteAction> completions = propertyCell().getSubstituteInfo().getMatchingActions("1", false);
      require(completions.size() == 1 && "10".equals(completions.get(0).getMatchingText("1")), "real completion matching selects one candidate");
      completions.get(0).substitute(editor.getEditorContext(), "1");
    });
    host.getProject().getModelAccess().runReadAction(() -> {
      require("10".equals(propertyCell().getText()) && checkErrors() == 0, "completion updates cells and clears diagnostic through model events");
    });
    verifyUndoRedo("invalid", "10", 1, 0, 0, 0);

    host.getProject().getModelAccess().executeCommand(() -> literal.setProperty(ExpressionLanguage.VALUE, "13"));
    assertState("13", 0, 1);

    phase("intention and undo");
    host.getProject().getModelAccess().executeCommand(() -> {
      IntentionsManager.QueryDescriptor query = new IntentionsManager.QueryDescriptor();
      query.setCurrentNodeOnly(true);
      Collection<Pair<IntentionExecutable, SNode>> intentions = IntentionsManager.getInstance().getAvailableIntentions(query, literal, editor.getEditorContext());
      boolean executed = false;
      for (Pair<IntentionExecutable, SNode> intention : intentions) {
        if (intention.o1.getDescriptor().getPersistentStateKey().equals(ExpressionLanguage.NAME + ".replaceWithZero")) {
          intention.o1.execute(intention.o2, editor.getEditorContext());
          executed = true;
          break;
        }
      }
      require(executed, "real intention manager discovers and runs the language intention");
    });
    host.getProject().getModelAccess().runReadAction(() -> {
      editor.getUpdater().flushModelEvents();
      require("0".equals(propertyCell().getText()) && checkErrors() == 0, "intention updates cells and preserves valid type");
      require(checkWarnings() == 0, "intention invalidates and clears the non-typesystem warning");
    });
    verifyUndoRedo("13", "0", 0, 0, 1, 0);
    verifyClipboardAndSelection();
    verifyChildrenAndReferences();
  }

  private static void verifyClipboardAndSelection() {
    phase("clipboard and selection persistence");
    host.getProject().getModelAccess().executeCommand(() -> literal.setProperty(ExpressionLanguage.VALUE, "123"));
    host.getProject().getModelAccess().runReadAction(() -> {
      editor.getUpdater().flushModelEvents();
      editor.selectNode(literal, "value");
      propertyCell().setSelectionStart(1);
      propertyCell().setSelectionEnd(2);
      List<SelectionInfo> saved = editor.getSelectionManager().getSelectionInfoStack();
      require(!saved.isEmpty(), "actual cell selection serializes");
      propertyCell().deselectAll();
      editor.getSelectionManager().setSelectionInfoStack(saved);
      require("2".equals(propertyCell().getSelectedText()), "selection restores text range with registered class aliases");
    });
    // The actual cell action establishes a command; an enclosing read action
    // would incorrectly require elevating the repository lock to write access.
    require(editor.getActionHandler().executeAction(propertyCell(), CellActionType.COPY), "real label copy action is available");
    try {
      Object copied = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null).getTransferData(DataFlavor.stringFlavor);
      require("2".equals(copied), "MPS copy preserves selected text in WebAWT clipboard");
    } catch (Exception failure) { throw new AssertionError("clipboard contents", failure); }
    host.getProject().getModelAccess().executeCommand(() -> literal.setProperty(ExpressionLanguage.VALUE, "5"));
    host.getProject().getModelAccess().runReadAction(() -> {
      editor.getUpdater().flushModelEvents();
      editor.selectNode(literal, "value");
      propertyCell().selectAll();
    });
    host.getProject().getModelAccess().executeCommand(() -> {
      require(editor.getActionHandler().executeAction(propertyCell(), CellActionType.PASTE), "real label paste action is available");
    });
    assertState("2", 0, 0);
    verifyUndoRedo("5", "2", 0, 0, 0, 0);
  }

  private static void verifyChildrenAndReferences() {
    phase("attach group and reference fixture nodes");
    SNode variableA = new jetbrains.mps.smodel.SNode(jetbrains.mps.smodel.SNodeUtil.concept_RuntimeTypeVariable);
    SNode variableB = new jetbrains.mps.smodel.SNode(jetbrains.mps.smodel.SNodeUtil.concept_RuntimeTypeVariable);
    variableA.setProperty(jetbrains.mps.smodel.SNodeUtil.property_INamedConcept_name, "a");
    variableB.setProperty(jetbrains.mps.smodel.SNodeUtil.property_INamedConcept_name, "b");
    require("a".equals(variableA.getName()) && "b".equals(variableB.getName()),
        "runtime type variables retain distinct names through real INamedConcept metadata");
    SNode[] nodes = new SNode[5];
    host.getProject().getModelAccess().executeCommand(() -> {
      nodes[0] = model.createNode(ExpressionLanguage.GROUP);
      nodes[1] = model.createNode(ExpressionLanguage.LITERAL);
      nodes[1].setProperty(ExpressionLanguage.VALUE, "3");
      nodes[2] = model.createNode(ExpressionLanguage.LITERAL);
      nodes[2].setProperty(ExpressionLanguage.VALUE, "bad-child");
      nodes[3] = model.createNode(ExpressionLanguage.REFERENCE);
      nodes[3].setReferenceTarget(ExpressionLanguage.TARGET, literal);
      nodes[4] = model.createNode(ExpressionLanguage.LITERAL);
      nodes[4].setProperty(ExpressionLanguage.VALUE, "bad-target");
      nodes[0].addChild(ExpressionLanguage.MEMBERS, nodes[1]);
      model.addRootNode(nodes[0]);
      model.addRootNode(nodes[4]);
    });
    SNode group = nodes[0], first = nodes[1], inserted = nodes[2], reference = nodes[3], otherTarget = nodes[4];
    editor.editNode(group);
    lastChecked = Instant.MIN;
    lastNonTypesystemCheck = Instant.MIN;
    TypecheckingSession session = editor.getTypecheckingSession();
    UndoManager history = UndoManager.getInstance(host.getIdeaProject());
    boolean completed = false;
    try {
      assertGroupState(group, new SNode[] {first}, ExpressionLanguage.INTEGER, first, 0);
      int groupRuns = ExpressionLanguage.groupInferenceRuns;
      phase("child insertion and incremental type update");
      host.getProject().getModelAccess().executeCommand(() -> group.addChild(ExpressionLanguage.MEMBERS, inserted));
      assertGroupState(group, new SNode[] {first, inserted}, ExpressionLanguage.TEXT, inserted, 1);
      require(ExpressionLanguage.groupInferenceRuns > groupRuns, "child insertion invalidates the existing group rule");
      history.undo(null);
      assertGroupState(group, new SNode[] {first}, ExpressionLanguage.INTEGER, first, 0);
      assertChildAbsent(inserted);
      history.redo(null);
      assertGroupState(group, new SNode[] {first, inserted}, ExpressionLanguage.TEXT, inserted, 1);

      phase("child deletion and undo");
      host.getProject().getModelAccess().executeCommand(() -> group.removeChild(inserted));
      assertGroupState(group, new SNode[] {first}, ExpressionLanguage.INTEGER, first, 0);
      assertChildAbsent(inserted);
      history.undo(null);
      assertGroupState(group, new SNode[] {first, inserted}, ExpressionLanguage.TEXT, inserted, 1);
      history.redo(null);
      assertGroupState(group, new SNode[] {first}, ExpressionLanguage.INTEGER, first, 0);
      assertChildAbsent(inserted);

      phase("reference replacement invalidates cells and dependent equations");
      host.getProject().getModelAccess().executeCommand(() -> group.addChild(ExpressionLanguage.MEMBERS, reference));
      assertReferenceState(group, first, reference, "2", ExpressionLanguage.INTEGER, 0);
      int referenceRuns = ExpressionLanguage.referenceInferenceRuns;
      host.getProject().getModelAccess().executeCommand(() -> reference.setReferenceTarget(ExpressionLanguage.TARGET, otherTarget));
      assertReferenceState(group, first, reference, "bad-target", ExpressionLanguage.TEXT, 1);
      require(ExpressionLanguage.referenceInferenceRuns > referenceRuns, "reference replacement reruns inference");
      history.undo(null);
      assertReferenceState(group, first, reference, "2", ExpressionLanguage.INTEGER, 0);
      history.redo(null);
      assertReferenceState(group, first, reference, "bad-target", ExpressionLanguage.TEXT, 1);

      phase("referenced root property invalidates its dependent editor and type");
      referenceRuns = ExpressionLanguage.referenceInferenceRuns;
      host.getProject().getModelAccess().executeCommand(() -> otherTarget.setProperty(ExpressionLanguage.VALUE, "9"));
      assertReferenceState(group, first, reference, "9", ExpressionLanguage.INTEGER, 0);
      require(ExpressionLanguage.referenceInferenceRuns > referenceRuns, "external target property invalidates the reference rule");
      history.undo(null);
      assertReferenceState(group, first, reference, "bad-target", ExpressionLanguage.TEXT, 1);
      history.redo(null);
      assertReferenceState(group, first, reference, "9", ExpressionLanguage.INTEGER, 0);
      require(editor.getTypecheckingSession() == session, "child/reference edits retain the same incremental editor session");
      completed = true;
    } finally {
      if (completed) phase("restore literal editor after structural contracts");
      editor.editNode(literal);
      lastChecked = Instant.MIN;
      lastNonTypesystemCheck = Instant.MIN;
      host.getProject().getModelAccess().executeCommand(() -> {
        model.removeRootNode(group);
        model.removeRootNode(otherTarget);
      });
    }
    assertState("2", 0, 0);
  }

  private static void assertGroupState(SNode group, SNode[] expectedChildren, SConcept expectedType, SNode diagnosticNode, int diagnostics) {
    host.getProject().getModelAccess().runReadAction(() -> {
      editor.getUpdater().flushModelEvents();
      List<SNode> children = new ArrayList<>();
      for (SNode child : group.getChildren(ExpressionLanguage.MEMBERS)) children.add(child);
      require(children.equals(Arrays.asList(expectedChildren)), "actual child order matches the command/undo state");
      require(editor.findCellWithId(group, "group") != null, "group editor remains mounted");
      for (SNode child : expectedChildren) {
        String cellId = child.getConcept().equals(ExpressionLanguage.REFERENCE) ? "target" : "value";
        require(child.getModel() == model && editor.findCellWithId(child, cellId) != null, "attached child has a real updated editor cell");
      }
      checkErrors();
      List<SimpleEditorMessage> messages = editor.getHighlightManager().getMessagesFor(diagnosticNode, checker);
      StringBuilder details = new StringBuilder();
      for (SimpleEditorMessage message : messages) details.append(message.getMessage()).append("; ");
      require(messages.size() == diagnostics,
          "child/reference diagnostics expected=" + diagnostics + " actual=" + messages.size() + ": " + details);
      assertInferredType(group, expectedType);
    });
  }

  private static void assertChildAbsent(SNode child) {
    host.getProject().getModelAccess().runReadAction(() -> {
      require(child.getParent() == null && child.getModel() == null && model.getNode(child.getNodeId()) == null,
          "deleted child leaves containment and model index");
      require(editor.findCellWithId(child, "value") == null, "deleted child leaves the editor cell tree");
      require(editor.getHighlightManager().getMessagesFor(child, checker).isEmpty(), "deleted child diagnostic is removed");
    });
  }

  private static void assertReferenceState(SNode group, SNode first, SNode reference, String text, SConcept expectedType, int diagnostics) {
    assertGroupState(group, new SNode[] {first, reference}, expectedType, reference, diagnostics);
    host.getProject().getModelAccess().runReadAction(() -> {
      EditorCell_Label targetCell = (EditorCell_Label) editor.findCellWithId(reference, "target");
      require(text.equals(targetCell.getText()), "reference cell follows its target and target property");
      assertInferredType(reference, expectedType);
    });
  }

  /** Query the same session already checked by the real TypesEditorChecker. */
  private static void assertInferredType(SNode node, SConcept expectedType) {
    TypecheckingFacade facade = TypecheckingFacade.getFromContext();
    SNode inferred = facade.computeWithSession(editor.getTypecheckingSession(), () -> facade.getTypeOf(node));
    require(inferred != null && inferred.getConcept().equals(expectedType), "real equations infer the expected dependent type");
  }
  private static void verifyUndoRedo(String before, String after, int errorsBefore, int errorsAfter, int warningsBefore, int warningsAfter) {
    UndoManager history = UndoManager.getInstance(host.getIdeaProject());
    require(history.isUndoAvailable(null), "MPS model command produces an undo action");
    history.undo(null);
    assertState(before, errorsBefore, warningsBefore);
    require(history.isRedoAvailable(null), "undo exposes the redo action");
    history.redo(null);
    assertState(after, errorsAfter, warningsAfter);
  }
  private static void assertState(String value, int errors, int warnings) {
    host.getProject().getModelAccess().runReadAction(() -> {
      editor.getUpdater().flushModelEvents();
      require(value.equals(literal.getProperty(ExpressionLanguage.VALUE)) && value.equals(propertyCell().getText()), "undo/redo model and property cell agree");
      require(checkErrors() == errors && checkWarnings() == warnings, "undo/redo diagnostic state follows actual model events");
    });
  }
  private static EditorCell_Property propertyCell() {
    return (EditorCell_Property) editor.findCellWithId(literal, "value");
  }
  private static int checkErrors() {
    int count = updateChecker(checker, lastChecked);
    lastChecked = Instant.now();
    return count;
  }
  private static int checkWarnings() {
    int count = updateChecker(nonTypesystemChecker, lastNonTypesystemCheck);
    lastNonTypesystemCheck = Instant.now();
    return count;
  }
  private static int updateChecker(AbstractTypesystemEditorChecker checker, Instant previouslyChecked) {
    require(editor.getTypecheckingSession() != null, "editor owns an incremental typechecking session");
    UpdateResult result = checker.update(editor, previouslyChecked != Instant.MIN, previouslyChecked, false, Cancellable.NEVER);
    require(result instanceof UpdateResult.Completed, "real typesystem editor checker completes");
    UpdateResult.Completed completed = (UpdateResult.Completed) result;
    // The browser test schedules this update explicitly. Message conversion,
    // targeting, painting and removal all remain the actual MPS implementations.
    if (completed.myMessagesChanged) {
      editor.getHighlightManager().clearForOwner(checker, false);
      for (EditorMessage message : completed.myMessages) editor.getHighlightManager().mark(message);
      editor.getHighlightManager().repaintAndRebuildEditorMessages();
    }
    checker.doneUpdating();
    return editor.getHighlightManager().getMessagesFor(literal, checker).size();
  }
  private static void require(boolean condition, String detail) { if (!condition) throw new AssertionError(progress + ": " + detail); }
  private static void phase(String detail) { progress = detail; System.out.println("EDITOR_PHASE: " + detail); }

  /** A writable session-owned module; the default fileless Solution represents packaged code. */
  private static final class MemorySolution extends Solution {
    MemorySolution(SolutionDescriptor descriptor) { super(descriptor, null); }
    @Override public boolean isPackaged() { return false; }
  }

  /** Storage policy only; real MPS loading, root edits, and listener dispatch remain inherited. */
  private static final class MemoryModel extends EditableModelDescriptor {
    MemoryModel(org.jetbrains.mps.openapi.model.SModelReference reference) { super(reference, new NullDataSource()); }
    @Override protected ModelLoadResult<jetbrains.mps.smodel.SModel> createModel() {
      return new ModelLoadResult<>(new jetbrains.mps.smodel.SModel(getReference()), ModelLoadingState.FULLY_LOADED);
    }
    @Override public boolean isReadOnly() { return false; }
    @Override public void save() { throw new UnsupportedOperationException("This test model has no persistent storage"); }
    @Override public void reloadFromSource() { throw new UnsupportedOperationException("This test model has no persistent storage"); }
  }
}
