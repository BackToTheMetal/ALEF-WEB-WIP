package alefj2cl;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.*;
import javax.swing.SwingUtilities;
import com.mbeddr.mpsutil.grammarcells.runtime.GrammarCellsRuntime;
import gegevensspraak.behavior.DatumTijdLiteral__BehaviorDescriptor;
import jetbrains.mps.nodeEditor.NodeEditorComponent;
import jetbrains.mps.project.ModuleId;
import jetbrains.mps.project.Solution;
import jetbrains.mps.project.structure.modules.SolutionDescriptor;
import jetbrains.mps.smodel.EditableModelDescriptor;
import jetbrains.mps.smodel.ModelLoadResult;
import jetbrains.mps.smodel.SModelId;
import jetbrains.mps.smodel.SModelReference;
import jetbrains.mps.smodel.adapter.structure.MetaAdapterFactory;
import jetbrains.mps.smodel.language.LanguageRegistry;
import jetbrains.mps.smodel.language.LanguageRuntime;
import jetbrains.mps.smodel.loading.ModelLoadingState;
import jetbrains.mps.smodel.runtime.*;
import jetbrains.mps.openapi.intentions.*;
import jetbrains.mps.openapi.editor.descriptor.EditorAspectDescriptor;
import jetbrains.mps.lang.typesystem.runtime.IHelginsDescriptor;
import mpsj2cl.editor.BrowserEditorHost;
import org.jetbrains.mps.openapi.language.*;
import org.jetbrains.mps.openapi.model.SNode;
import org.jetbrains.mps.openapi.persistence.NullDataSource;
import org.webawt.WebAwt;

/** Browser execution of generated language descriptors and representative operations. */
public final class LanguageContracts {
  private static String result;
  private static int checks;
  private static BrowserEditorHost host;
  private static AlefLanguages languages;
  private static MemoryModel model;
  private static SNode date, expression;
  private static NodeEditorComponent dateEditor, expressionEditor;
  private static javax.swing.JFrame window;
  private static final Map<String, SConcept> concepts = new HashMap<>();
  public static String result() { return result; }
  public static void start() {
    WebAwt.install("alef-root");
    SwingUtilities.invokeLater(() -> {
      try {
        helpers();
        tableCopies();
        host = new BrowserEditorHost("ALEF generated language contracts");
        host.getPlatform().findComponent(jetbrains.mps.persistence.PersistenceRegistry.class)
            .setModelIdFactory("java", new jetbrains.mps.java.stub.JavaPackageModelId.Factory());
        languages = new AlefLanguages(host.getPlatform());
        GrammarCellsRuntime.init();
        index(new gegevensspraak.structure.StructureAspectDescriptor());
        index(new regelspraak.structure.StructureAspectDescriptor());
        host.getProject().getModelAccess().runReadAction(LanguageContracts::aspects);
        host.getProject().getModelAccess().executeCommand(LanguageContracts::model);
        host.getProject().getModelAccess().runReadAction(LanguageContracts::read);
        host.getProject().getModelAccess().executeCommand(LanguageContracts::intentions);
        grammarSideTransformation();
        editorShortcuts();
        finish("PASS: " + checks + " language checks");
      } catch (Throwable failure) {
        failure.printStackTrace();
        finish("FAIL after " + checks + " checks: " + failure);
      }
    });
  }
  private static void helpers() {
    require("Cafe A 日本".equals(BrowserUnicode.stripMarks("Café A\u030a 日本")), "Unicode normalization for generated test identifiers");
    require("&lt;&amp;&gt;&quot;&apos;&#127;😀".equals(BrowserHelpers.escapeXml10("<&>\"'\u007f\u0000\ud800😀")), "XML 1.0 escaping and invalid character removal");
    for (int value : new int[]{0, 1, 2, 4, 15, 16, 17, 99999}) {
      BigInteger n = BigInteger.valueOf(value);
      BigInteger[] pair = BrowserHelpers.sqrtAndRemainder(n);
      require(pair[0].multiply(pair[0]).add(pair[1]).equals(n), "square-root identity");
      require(pair[0].add(BigInteger.ONE).pow(2).compareTo(n) > 0, "square-root upper bound");
    }
    require("/1.0:c:BWBR0000001".equals(BrowserHelpers.urlPath("https://wetten.nl/1.0:c:BWBR0000001?x=1#part")), "JuriConnect URL path");
    require("&lt;a &amp; b&gt;".equals(BrowserHelpers.escapeHtml("<a & b>")), "HTML text escaping");
  }
  private static void index(StructureAspectDescriptor structure) {
    for (ConceptDescriptor descriptor : structure.getDescriptors()) {
      String name = descriptor.getConceptFqName();
      concepts.put(name.substring(name.lastIndexOf('.') + 1), MetaAdapterFactory.getConcept(descriptor.getId(), name));
    }
  }
  private static void tableCopies() {
    de.slisson.mps.tables.runtime.simplegrid.SimpleGrid<Object> grid = new de.slisson.mps.tables.runtime.simplegrid.SimpleGrid<>();
    Object element = new Object();
    grid.setElement(1, 2, element);
    int identity = System.identityHashCode(grid);
    de.slisson.mps.tables.runtime.simplegrid.SimpleGrid<Object> copy = grid.clone();
    require(copy != grid && copy.getClass() == grid.getClass(), "grid clone identity and concrete type");
    require(System.identityHashCode(copy) != identity, "clone gets independent J2CL identity hash");
    require(copy.getElement(1, 2) == element, "grid clone preserves element references");
    copy.setElement(1, 2, new Object());
    require(grid.getElement(1, 2) == element, "grid clone has independent storage");
    de.slisson.mps.tables.runtime.gridmodel.StringHeaderReference header = new de.slisson.mps.tables.runtime.gridmodel.StringHeaderReference("column");
    require(header.clone() != header && header.clone().equals(header), "header clone preserves subclass state");
    de.itemis.mps.editor.celllayout.boxmodel.DefaultBoxModel box = new de.itemis.mps.editor.celllayout.boxmodel.DefaultBoxModel();
    box.getContentBox().setSize(20, 10);
    de.itemis.mps.editor.celllayout.boxmodel.DefaultBoxModel boxCopy = box.clone();
    boxCopy.getContentBox().setWidth(30);
    require(box.getContentBox().getWidth() == 20 && boxCopy.getContentBox().getWidth() == 30, "box model deep clone");
    de.itemis.mps.editor.celllayout.boxmodel.CalcOnWriteFrame frame = new de.itemis.mps.editor.celllayout.boxmodel.CalcOnWriteFrame(new de.itemis.mps.editor.celllayout.boxmodel.DefaultLayoutBox());
    frame.setSize(20, 10);
    de.itemis.mps.editor.celllayout.boxmodel.CalcOnWriteFrame frameCopy = frame.clone();
    frameCopy.setWidth(30);
    require(frame.getWidth() == 20 && frameCopy.getWidth() == 30, "frame clone has independent result and listener");
  }
  private static void aspects() {
    require(mpsUtils.MPSUtil.importedLanguages(new testspraak.Language().getIdentity()).stream()
        .anyMatch(language -> "jetbrains.mps.lang.editor".equals(language.getQualifiedName())),
        "compiled Testspraak retains its model imports for completion filtering");
    LanguageRegistry registry = host.getPlatform().findComponent(LanguageRegistry.class);
    for (String name : new String[]{"gegevensspraak", "regelspraak", "servicespraak", "testspraak"}) {
      LanguageRuntime language = registry.getLanguage(name);
      require(language.getAspect(BehaviorAspectDescriptor.class) != null, name + " behavior");
      require(language.getAspect(ConstraintsAspectDescriptor.class) != null, name + " constraints");
      require(language.getAspect(IntentionAspectDescriptor.class) != null, name + " intentions");
      require(language.getAspect(IHelginsDescriptor.class) != null, name + " typesystem");
      require(language.getAspect(EditorAspectDescriptor.class) != null, name + " editors");
    }
  }
  private static void model() {
    AlefStandardModels.install(host.getProject());
    SolutionDescriptor descriptor = new SolutionDescriptor();
    descriptor.setId(ModuleId.regular(new UUID(991, 1)));
    descriptor.setNamespace("alefj2cl.contracts");
    Solution solution = new Solution(descriptor, null) { @Override public boolean isPackaged() { return false; } };
    host.getProject().addModule(solution);
    model = new MemoryModel(new SModelReference(solution.getModuleReference(), SModelId.regular(new UUID(991, 2)), "alefj2cl.contracts.model"));
    solution.registerModel(model);
    model.addLanguage(concepts.get("DatumTijdLiteral").getLanguage());
    model.addLanguage(concepts.get("Haakjes").getLanguage());
    date = DatumTijdLiteral__BehaviorDescriptor.fromDate_id4K62$zpiM$Q.invoke(concepts.get("DatumTijdLiteral"), LocalDate.of(2024, 2, 29));
    model.addRootNode(date);
    expression = model.createNode(concepts.get("Haakjes"));
    SNode literal = model.createNode(concepts.get("BooleanLiteral"));
    SContainmentLink link = null;
    for (SContainmentLink candidate : expression.getConcept().getContainmentLinks()) {
      if (literal.getConcept().isSubConceptOf(candidate.getTargetConcept())) { link = candidate; break; }
    }
    if (link == null) throw new AssertionError("Haakjes expression link missing");
    expression.addChild(link, literal);
    model.addRootNode(expression);
  }
  private static void read() {
    Map<SNode, jetbrains.mps.baseLanguage.closures.runtime._FunctionTypes._return_P2_E0<? extends Boolean, ? super interpreter.runtime.RtContext, ? super LocalDate>> weekdays = new HashMap<>();
    gegevensspraak.behavior.Weekdagen.addDefinities(new jetbrains.mps.smodel.SNodePointer(
        "r:4172b106-22c7-49a2-9043-c1e488e6f56c(standaard.funcs)", "2451177311548673779"), weekdays);
    require(weekdays.size() == 7 && !weekdays.containsKey(null), "original weekday model and node identities resolve");
    int matches = 0;
    for (var predicate : weekdays.values()) if (predicate.invoke(null, LocalDate.of(2026, 9, 17))) matches++;
    require(matches == 1, "exactly one standard weekday matches a date");
    weekdays.clear();
    gegevensspraak.behavior.StandaardDagsoorten.addDefinities(weekdays);
    require(weekdays.size() == 1 && !weekdays.containsKey(null), "original leap-day accessory node resolves");
    for (var predicate : weekdays.values()) {
      require(predicate.invoke(null, LocalDate.of(2024, 2, 29)) && !predicate.invoke(null, LocalDate.of(2024, 2, 28)), "standard leap-day predicate");
    }
    require(LocalDate.of(2024, 2, 29).equals(DatumTijdLiteral__BehaviorDescriptor.getDate_id12Tz9pj4qbS.invoke(date)), "generated leap-date behavior");
    Object granularity = new gegevensspraak.constraints.DatumTijdLiteral_Constraints.Granulariteit_Property(new gegevensspraak.constraints.DatumTijdLiteral_Constraints()).getValue(date);
    require(granularity instanceof SEnumerationLiteral, "generated derived-property constraint");
    linguistics.runtime.Rendering rendering = new linguistics.runtime.Rendering();
    rendering.render(date, new linguistics.runtime.FeatureSet());
    require(rendering.toString().contains("2024") && rendering.toString().contains("29"), "generated linguistics renderer binding");
    dateEditor = host.createEditor(date);
    expressionEditor = host.createEditor(expression);
    window = new javax.swing.JFrame("ALEF editor contracts");
    window.getContentPane().setLayout(new java.awt.GridLayout(2, 1));
    window.getContentPane().add(dateEditor.getExternalComponent());
    window.getContentPane().add(expressionEditor.getExternalComponent());
    window.setSize(800, 400);
    window.setVisible(true);
    dateEditor.relayout();
    expressionEditor.relayout();
    require(dateEditor.getRootCell() != null && dateEditor.getRootCell().getSNode() == date, "Gegevensspraak editor cell tree");
    require(expressionEditor.getRootCell() != null && expressionEditor.getRootCell().getSNode() == expression, "Regelspraak editor cell tree");
    require(dateEditor.findCellWithId(date, "JaarNr") != null, "generated Gegevensspraak year editor: " + cells(dateEditor.getRootCell()));
    require(expressionEditor.findCellWithId(expression, "Constant_gp57vv_a0a") != null, "generated Regelspraak parentheses editor");
    require(dateEditor.getRootCell().getWidth() > 0 && expressionEditor.getRootCell().getWidth() > 0, "generated editor layout");
    SNode dateType = gegevensspraak.translator.Checker.typeOf(date);
    require(dateType != null && "DatumTijdType".equals(dateType.getConcept().getName()), "Gegevensspraak translator type inference");
    SNode expressionType = gegevensspraak.translator.Checker.typeOf(expression);
    require(expressionType != null && "BooleanType".equals(expressionType.getConcept().getName()), "Regelspraak translator extension type inference");
    jetbrains.mps.typechecking.TypecheckingFacade facade = jetbrains.mps.typechecking.TypecheckingFacade.getFromContext();
    SNode inferredDate = facade.computeWithSession(dateEditor.getTypecheckingSession(), () -> facade.getTypeOf(date));
    require(inferredDate != null && "DatumTijdType".equals(inferredDate.getConcept().getName()), "Gegevensspraak MPS inference rule execution");
    SNode inferredExpression = facade.computeWithSession(expressionEditor.getTypecheckingSession(), () -> facade.getTypeOf(expression));
    require(inferredExpression != null && "BooleanType".equals(inferredExpression.getConcept().getName()), "Regelspraak MPS inference rule execution");
  }
  private static void intentions() {
    Collection<IntentionExecutable> options = new gegevensspraak.intentions.VeranderGranulariteit_Intention().instances(date, dateEditor.getEditorContext());
    require(!options.isEmpty(), "generated parameterized intentions");
    for (IntentionExecutable option : options) {
      Object parameter = ((ParameterizedIntentionExecutable) option).getParameter();
      if (parameter instanceof SEnumerationLiteral && "uur".equalsIgnoreCase(((SEnumerationLiteral) parameter).getPresentation())) {
        option.execute(date, dateEditor.getEditorContext());
        require("0".equals(date.getProperty(property(date, "uur"))) && "29".equals(date.getProperty(property(date, "dag"))), "intention executes generated constraint setter");
        for (IntentionExecutable back : new gegevensspraak.intentions.VeranderGranulariteit_Intention().instances(date, dateEditor.getEditorContext())) {
          SEnumerationLiteral value = (SEnumerationLiteral) ((ParameterizedIntentionExecutable) back).getParameter();
          if ("dag".equalsIgnoreCase(value.getPresentation())) {
            back.execute(date, dateEditor.getEditorContext());
            require(date.getProperty(property(date, "uur")) == null, "intention removes finer granularity");
            return;
          }
        }
        throw new AssertionError("Day granularity intention missing");
      }
    }
    throw new AssertionError("Hour granularity intention missing");
  }
  private static SProperty property(SNode node, String name) {
    for (SProperty p : node.getConcept().getProperties()) if (name.equals(p.getName())) return p;
    throw new AssertionError("Missing property " + name);
  }
  private static void grammarSideTransformation() {
    SNode[] literal = new SNode[1];
    host.getProject().getModelAccess().executeCommand(() -> {
      SContainmentLink expressionLink = expression.getChildren().iterator().next().getContainmentLink();
      expression.removeChild(expression.getChildren().iterator().next());
      literal[0] = model.createNode(concepts.get("NumeriekeLiteral"));
      literal[0].setProperty(property(literal[0], "waarde"), "25");
      expression.addChild(expressionLink, literal[0]);
      expressionEditor.getEditorContext().flushEvents();
    });
    host.getProject().getModelAccess().runReadAction(() -> {
      var cell = (jetbrains.mps.nodeEditor.cells.EditorCell_Label) expressionEditor.findCellWithId(literal[0], "property_waarde");
      require(cell != null, "numeric grammar input cell");
      expressionEditor.changeSelection(cell);
      cell.end();
      var language = concepts.get("Haakjes").getLanguage();
      require(language.getSourceModuleReference().resolve(host.getProject().getRepository()) == null,
          "grammar regression uses compiled languages without desktop modules");
      var info = new jetbrains.mps.nodeEditor.cellActions.SideTransformSubstituteInfo(cell,
          jetbrains.mps.nodeEditor.cellActions.SideTransformSubstituteInfo.Side.RIGHT);
      require(info.getMatchingActions("plus", true).size() == 1, "numeric right transformation offers exactly one plus action");
    });
    for (char c : "plus".toCharArray()) {
      expressionEditor.dispatchEvent(new java.awt.event.KeyEvent(expressionEditor,
          java.awt.event.KeyEvent.KEY_TYPED, System.currentTimeMillis(), 0, java.awt.event.KeyEvent.VK_UNDEFINED, c));
    }
    host.getProject().getModelAccess().runReadAction(() -> {
      SNode sum = expression.getChildren().iterator().next();
      require("PlusExpressie".equals(sum.getConcept().getName()), "typing plus creates an addition expression");
      require(literal[0].getParent() == sum && "25".equals(literal[0].getProperty(property(literal[0], "waarde"))),
          "grammar transformation preserves the numeric operand");
      SNode selected = expressionEditor.getSelectedNode();
      require(selected != null && selected.getParent() == sum && "rechts".equals(selected.getContainmentLink().getName()),
          "grammar transformation selects the new right operand");
    });
    expressionEditor.dispatchEvent(new java.awt.event.KeyEvent(expressionEditor,
        java.awt.event.KeyEvent.KEY_TYPED, System.currentTimeMillis(), 0, java.awt.event.KeyEvent.VK_UNDEFINED, '7'));
    host.getProject().getModelAccess().runReadAction(() -> {
      SNode right = null;
      for (SNode child : literal[0].getParent().getChildren()) {
        if ("rechts".equals(child.getContainmentLink().getName())) right = child;
      }
      require(right != null && "NumeriekeLiteral".equals(right.getConcept().getName())
          && "7".equals(right.getProperty(property(right, "waarde"))), "typing continues in the new right operand");
    });
    var history = com.intellij.openapi.command.undo.UndoManager.getInstance(host.getIdeaProject());
    history.undo(null);
    host.getProject().getModelAccess().runReadAction(() ->
        require("PlusExpressie".equals(literal[0].getParent().getConcept().getName()), "undo operand entry retains the addition"));
    history.undo(null);
    host.getProject().getModelAccess().runReadAction(() ->
        require(expression.getChildren().iterator().next() == literal[0], "undo removes the new expression and retains the original number"));
    history.redo(null);
    host.getProject().getModelAccess().runReadAction(() ->
        require("PlusExpressie".equals(literal[0].getParent().getConcept().getName()), "redo restores the grammar transformation"));
    history.redo(null);
    host.getProject().getModelAccess().runReadAction(() -> {
      SNode right = null;
      for (SNode child : literal[0].getParent().getChildren()) {
        if ("rechts".equals(child.getContainmentLink().getName())) right = child;
      }
      require(right != null && "NumeriekeLiteral".equals(right.getConcept().getName())
          && "7".equals(right.getProperty(property(right, "waarde"))), "redo restores the entered operand");
    });
  }
  private static void require(boolean value, String message) {
    if (!value) throw new AssertionError(message);
    checks++;
  }
  private static void editorShortcuts() {
    var access = host.getProject().getModelAccess();
    int ctrl = java.awt.event.InputEvent.CTRL_DOWN_MASK;
    int shift = java.awt.event.InputEvent.SHIFT_DOWN_MASK;
    SNode[] number = new SNode[1];
    access.runReadAction(() -> {
      for (SNode child : expression.getChildren().iterator().next().getChildren()) {
        if ("links".equals(child.getContainmentLink().getName())) number[0] = child;
      }
      var cell = (jetbrains.mps.nodeEditor.cells.EditorCell_Label) expressionEditor.findCellWithId(number[0], "property_waarde");
      expressionEditor.changeSelection(cell);
      cell.end();
    });
    expressionEditor.requestFocusInWindow();
    press(expressionEditor, java.awt.event.KeyEvent.VK_LEFT, ctrl);
    access.runReadAction(() -> require(((jetbrains.mps.nodeEditor.cells.EditorCell_Label) expressionEditor.getSelectedCell()).getCaretPosition() == 0,
        "Ctrl+Left moves to the previous word boundary"));
    press(expressionEditor, java.awt.event.KeyEvent.VK_RIGHT, ctrl | shift);
    access.runReadAction(() -> {
      var cell = (jetbrains.mps.nodeEditor.cells.EditorCell_Label) expressionEditor.getSelectedCell();
      require(cell.getSelectionEnd() - cell.getSelectionStart() == cell.getText().length(), "Ctrl+Shift+Right selects the numeric word: " + cell.getText() + " " + cell.getSelectionStart() + ".." + cell.getSelectionEnd());
      expressionEditor.changeSelection(cell);
      cell.end();
    });
    int depth = expressionEditor.getSelectionManager().getSelectionStackSize();
    press(expressionEditor, java.awt.event.KeyEvent.VK_UP, ctrl);
    require(expressionEditor.getSelectionManager().getSelectionStackSize() > depth, "Ctrl+Up expands structural selection instead of being swallowed");
    press(expressionEditor, java.awt.event.KeyEvent.VK_DOWN, ctrl);
    require(expressionEditor.getSelectionManager().getSelectionStackSize() == depth, "Ctrl+Down shrinks structural selection");
    access.runReadAction(() -> expressionEditor.getSelectedCell().getStyle().set(jetbrains.mps.editor.runtime.style.StyleAttributes.READ_ONLY, true));
    press(expressionEditor, java.awt.event.KeyEvent.VK_DELETE, ctrl);
    access.runReadAction(() -> {
      require(number[0].getModel() == model && "25".equals(number[0].getProperty(property(number[0], "waarde"))), "Ctrl+Delete respects read-only cells");
      expressionEditor.getSelectedCell().getStyle().set(jetbrains.mps.editor.runtime.style.StyleAttributes.READ_ONLY, false);
    });

    SNode[] root = new SNode[1], first = new SNode[1], second = new SNode[1];
    access.executeCommand(() -> {
      root[0] = model.createNode(concepts.get("ObjectModel"));
      root[0].setProperty(property(root[0], "name"), "Shortcut contracts");
      model.addRootNode(root[0]);
      first[0] = model.createNode(concepts.get("ObjectType"));
      second[0] = model.createNode(concepts.get("ObjectType"));
      first[0].setProperty(property(first[0], "name"), "First");
      second[0].setProperty(property(second[0], "name"), "Second");
      SContainmentLink elements = null;
      for (var link : root[0].getConcept().getContainmentLinks()) if ("elem".equals(link.getName())) elements = link;
      root[0].addChild(elements, first[0]);
      root[0].addChild(elements, second[0]);
    });
    access.runReadAction(() -> {
      dateEditor.editNode(root[0]);
      dateEditor.selectNode(first[0]);
    });
    dateEditor.requestFocusInWindow();
    press(dateEditor, java.awt.event.KeyEvent.VK_D, ctrl);
    SNode[] copy = new SNode[1];
    access.runReadAction(() -> {
      copy[0] = first[0].getNextSibling();
      require(copy[0] != second[0] && copy[0].getNextSibling() == second[0], "Ctrl+D duplicates a list node");
      require("First".equals(copy[0].getProperty(property(copy[0], "name"))), "duplicate preserves generated properties");
      dateEditor.selectNode(copy[0]);
    });
    press(dateEditor, java.awt.event.KeyEvent.VK_DOWN, ctrl | shift);
    access.runReadAction(() -> require(second[0].getNextSibling() == copy[0], "Ctrl+Shift+Down moves the selected node"));
    press(dateEditor, java.awt.event.KeyEvent.VK_UP, ctrl | shift);
    access.runReadAction(() -> {
      require(first[0].getNextSibling() == copy[0], "Ctrl+Shift+Up moves the selected node back");
      dateEditor.selectNode(copy[0]);
    });
    press(dateEditor, java.awt.event.KeyEvent.VK_Y, ctrl);
    access.runReadAction(() -> require(first[0].getNextSibling() == second[0] && copy[0].getParent() == null, "Ctrl+Y deletes the line instead of running Redo"));
    press(dateEditor, java.awt.event.KeyEvent.VK_Z, ctrl);
    access.runReadAction(() -> require(first[0].getNextSibling() == copy[0], "Ctrl+Z undoes Delete Line"));
    press(dateEditor, java.awt.event.KeyEvent.VK_Z, ctrl | shift);
    access.runReadAction(() -> {
      require(first[0].getNextSibling() == second[0], "Ctrl+Shift+Z redoes Delete Line");
      dateEditor.selectNode(first[0]);
    });
    press(dateEditor, java.awt.event.KeyEvent.VK_DOWN, shift);
    access.runReadAction(() -> require(dateEditor.getSelectedNodes().size() == 2, "Shift+Down selects the next sibling instead of shrinking the selection"));
    press(dateEditor, java.awt.event.KeyEvent.VK_ESCAPE, 0);
    require(dateEditor.getSelectionManager().getSelectionStackSize() == 1, "Escape clears expanded selection");
    access.runReadAction(() -> dateEditor.selectNode(first[0]));
    var foldable = (jetbrains.mps.nodeEditor.cells.EditorCell_Collection) dateEditor.findNodeCell(first[0]);
    foldable.setFoldable(true);
    press(dateEditor, java.awt.event.KeyEvent.VK_MINUS, ctrl);
    require(foldable.isCollapsed(), "Ctrl+Minus folds the selected node");
    press(dateEditor, java.awt.event.KeyEvent.VK_EQUALS, ctrl);
    require(!foldable.isCollapsed(), "Ctrl+Equals unfolds the selected node");
    press(dateEditor, java.awt.event.KeyEvent.VK_SUBTRACT, ctrl | shift);
    require(foldable.isCollapsed(), "Ctrl+Shift+NumpadSubtract folds all nodes");
    press(dateEditor, java.awt.event.KeyEvent.VK_ADD, ctrl | shift);
    require(!foldable.isCollapsed(), "Ctrl+Shift+NumpadAdd unfolds all nodes");
    access.runReadAction(() -> {
      dateEditor.selectNode(first[0]);
      dateEditor.getSelectedCell().getStyle().set(jetbrains.mps.editor.runtime.style.StyleAttributes.READ_ONLY, true);
    });
    press(dateEditor, java.awt.event.KeyEvent.VK_D, ctrl);
    press(dateEditor, java.awt.event.KeyEvent.VK_Y, ctrl);
    access.runReadAction(() -> {
      require(first[0].getParent() == root[0] && first[0].getNextSibling() == second[0], "Duplicate and Delete Line respect read-only selection");
      dateEditor.getSelectedCell().getStyle().set(jetbrains.mps.editor.runtime.style.StyleAttributes.READ_ONLY, false);
    });
    require(!expressionEditor.getActionMap().get(expressionEditor.getInputMap(javax.swing.JComponent.WHEN_FOCUSED)
        .get(javax.swing.KeyStroke.getKeyStroke("ctrl D"))).isEnabled(), "shortcuts do not execute in an unfocused editor");
    press(dateEditor, java.awt.event.KeyEvent.VK_F, ctrl);
    require(dateEditor.isSearchPanelVisible(), "Ctrl+F opens the MPS editor search panel");
    dateEditor.requestFocusInWindow();
    press(dateEditor, java.awt.event.KeyEvent.VK_ESCAPE, 0);
    require(!dateEditor.isSearchPanelVisible(), "Escape invokes the original cancel-search action");
    editorActionRanges(root[0], first[0], second[0], ctrl, shift);
  }

  private static List<SNode> children(SNode node) {
    List<SNode> result = new ArrayList<>();
    node.getChildren().forEach(result::add);
    return result;
  }

  private static void editorActionRanges(SNode root, SNode first, SNode second, int ctrl, int shift) {
    var access = host.getProject().getModelAccess();
    List<SNode> original = access.computeReadAction(() -> children(root));
    access.runReadAction(() -> dateEditor.getEditorContext().selectRange(first, second));
    press(dateEditor, java.awt.event.KeyEvent.VK_D, ctrl);
    access.runReadAction(() -> {
      List<SNode> after = children(root);
      require(after.size() == original.size() + 2, "duplicate copies every selected sibling");
      SNode firstCopy = second.getNextSibling(), secondCopy = firstCopy.getNextSibling();
      require(firstCopy != first && secondCopy != second, "range duplicate creates independent node identities");
      require("First".equals(firstCopy.getProperty(property(firstCopy, "name")))
          && "Second".equals(secondCopy.getProperty(property(secondCopy, "name"))), "range duplicate preserves sibling order and properties");
    });
    press(dateEditor, java.awt.event.KeyEvent.VK_Z, ctrl);
    access.runReadAction(() -> require(children(root).equals(original), "one undo restores the entire duplicated range"));
    press(dateEditor, java.awt.event.KeyEvent.VK_Z, ctrl | shift);
    access.runReadAction(() -> require(children(root).size() == original.size() + 2, "one redo restores both duplicated nodes"));
    press(dateEditor, java.awt.event.KeyEvent.VK_Z, ctrl);

    // Use a third real generated node to exercise movement of a selected range.
    SNode[] third = new SNode[1];
    access.executeCommand(() -> {
      third[0] = model.createNode(concepts.get("ObjectType"));
      third[0].setProperty(property(third[0], "name"), "Third");
      root.insertChildAfter(first.getContainmentLink(), third[0], second);
    });
    access.runReadAction(() -> dateEditor.getEditorContext().selectRange(first, second));
    press(dateEditor, java.awt.event.KeyEvent.VK_DOWN, ctrl | shift);
    access.runReadAction(() -> require(third[0].getNextSibling() == first && first.getNextSibling() == second,
        "moving a range down retains its internal order"));
    press(dateEditor, java.awt.event.KeyEvent.VK_Z, ctrl);
    access.runReadAction(() -> require(first.getNextSibling() == second && second.getNextSibling() == third[0],
        "range movement is one undoable MPS command"));
    press(dateEditor, java.awt.event.KeyEvent.VK_Z, ctrl | shift);
    access.runReadAction(() -> require(third[0].getNextSibling() == first, "redo restores range movement"));
    press(dateEditor, java.awt.event.KeyEvent.VK_Z, ctrl);

    access.runReadAction(() -> dateEditor.selectNode(first));
    press(dateEditor, java.awt.event.KeyEvent.VK_UP, ctrl | shift);
    access.runReadAction(() -> require(first.getNextSibling() == second, "move up at the list boundary preserves node order"));

    // A read-only sibling must prevent a range action even if the first is writable.
    access.runReadAction(() -> {
      dateEditor.findNodeCell(second).getStyle().set(jetbrains.mps.editor.runtime.style.StyleAttributes.READ_ONLY, true);
      dateEditor.getEditorContext().selectRange(first, second);
    });
    List<SNode> beforeReadOnly = access.computeReadAction(() -> children(root));
    press(dateEditor, java.awt.event.KeyEvent.VK_D, ctrl);
    press(dateEditor, java.awt.event.KeyEvent.VK_DOWN, ctrl | shift);
    press(dateEditor, java.awt.event.KeyEvent.VK_Y, ctrl);
    access.runReadAction(() -> {
      require(children(root).equals(beforeReadOnly), "range edits respect read-only siblings");
      dateEditor.findNodeCell(second).getStyle().set(jetbrains.mps.editor.runtime.style.StyleAttributes.READ_ONLY, false);
      dateEditor.selectNode(second);
    });

    // Insertion uses the original applicability and selection rules.
    for (int[] key : new int[][]{{java.awt.event.KeyEvent.VK_INSERT, 0},
        {java.awt.event.KeyEvent.VK_ENTER, shift}, {java.awt.event.KeyEvent.VK_ENTER, ctrl | shift}}) {
      access.runReadAction(() -> dateEditor.selectNode(second));
      List<SNode> beforeInsert = access.computeReadAction(() -> children(root));
      press(dateEditor, key[0], key[1]);
      access.runReadAction(() -> require(children(root).size() == beforeInsert.size() + 1,
          "insertion creates one sibling: " + key[0] + "/" + key[1]));
      press(dateEditor, java.awt.event.KeyEvent.VK_Z, ctrl);
      access.runReadAction(() -> require(children(root).equals(beforeInsert), "one undo removes the inserted sibling"));
    }

    // The shared installer also works for inspectors, which lack the host's provider.
    var standalone = new NodeEditorComponent(host.getProject().getRepository());
    var oldInput = standalone.getInputMap(javax.swing.JComponent.WHEN_FOCUSED);
    var oldActions = standalone.getActionMap();
    Runnable release = host.installEditorShortcuts(standalone);
    require(standalone.getInputMap(javax.swing.JComponent.WHEN_FOCUSED) != oldInput, "installer scopes a new input map to the editor");
    standalone.setFocusable(true);
    access.runReadAction(() -> {
      standalone.editNode(root);
      standalone.selectNode(first);
    });
    window.getContentPane().add(standalone.getExternalComponent());
    window.validate();
    standalone.requestFocusInWindow();
    List<SNode> beforeInspector = access.computeReadAction(() -> children(root));
    press(standalone, java.awt.event.KeyEvent.VK_D, ctrl);
    access.runReadAction(() -> require(children(root).size() == beforeInspector.size() + 1,
        "inspector-style editor supplies project context to the original MPS action"));
    press(standalone, java.awt.event.KeyEvent.VK_Z, ctrl);
    access.runReadAction(() -> require(children(root).equals(beforeInspector), "inspector edit uses the shared undo history"));
    release.run();
    require(standalone.getInputMap(javax.swing.JComponent.WHEN_FOCUSED) == oldInput && standalone.getActionMap() == oldActions,
        "installer cleanup restores the previous maps");
    window.getContentPane().remove(standalone.getExternalComponent());
    standalone.dispose();
  }
  private static void press(NodeEditorComponent editor, int code, int modifiers) {
    var event = new java.awt.event.KeyEvent(editor, java.awt.event.KeyEvent.KEY_PRESSED,
        System.currentTimeMillis(), modifiers, code, java.awt.event.KeyEvent.CHAR_UNDEFINED);
    editor.dispatchEvent(event);
    require(event.isConsumed(), "editor shortcut consumed: " + code + "/" + modifiers);
  }
  private static String cells(jetbrains.mps.openapi.editor.cells.EditorCell cell) {
    String value = cell.getClass().getSimpleName() + "[" + cell.getCellId() + "]";
    if (cell instanceof jetbrains.mps.openapi.editor.cells.EditorCell_Collection) {
      for (jetbrains.mps.openapi.editor.cells.EditorCell child : (jetbrains.mps.openapi.editor.cells.EditorCell_Collection) cell) value += " " + cells(child);
    }
    if (cell instanceof jetbrains.mps.openapi.editor.cells.EditorCell_Label) value += "=" + ((jetbrains.mps.openapi.editor.cells.EditorCell_Label) cell).getText();
    return value;
  }
  private static void finish(String value) {
    // Let the generated editor's debounced selection highlighting complete.
    javax.swing.Timer timer = new javax.swing.Timer(600, event -> disposeAndFinish(value));
    timer.setRepeats(false);
    timer.start();
  }
  private static void disposeAndFinish(String value) {
    try {
      GrammarCellsRuntime.dispose();
      if (window != null) window.dispose();
      if (host != null) host.dispose();
      if (languages != null) languages.close();
      result = value;
    } catch (Throwable failure) {
      failure.printStackTrace(); result = value + "; disposal failed: " + failure;
    }
  }
  private static final class MemoryModel extends EditableModelDescriptor {
    MemoryModel(org.jetbrains.mps.openapi.model.SModelReference reference) { super(reference, new NullDataSource()); }
    @Override protected ModelLoadResult<jetbrains.mps.smodel.SModel> createModel() {
      return new ModelLoadResult<>(new jetbrains.mps.smodel.SModel(getReference()), ModelLoadingState.FULLY_LOADED);
    }
    @Override public boolean isReadOnly() { return false; }
    @Override public void save() { throw new UnsupportedOperationException("In-memory contract model"); }
    @Override public void reloadFromSource() { throw new UnsupportedOperationException("In-memory contract model"); }
  }
}
