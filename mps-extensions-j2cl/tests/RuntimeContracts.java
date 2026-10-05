package extensionsj2cl;

import com.mbeddr.mpsutil.editor.querylist.runtime.*;
import com.mbeddr.mpsutil.grammarcells.runtime.*;
import de.itemis.mps.editor.bool.runtime.*;
import de.itemis.mps.editor.math.runtime.*;
import de.slisson.mps.hacks.editor.EditorComponentHacks;
import de.slisson.mps.reflection.runtime.ReflectionUtil;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.*;
import javax.swing.SwingUtilities;
import jetbrains.mps.lang.editor.menus.transformation.DefaultSubstituteMenuItemAsActionItem;
import jetbrains.mps.lang.editor.menus.transformation.SubstituteMenuItemAsActionItem;
import jetbrains.mps.nodeEditor.*;
import jetbrains.mps.nodeEditor.cells.*;
import jetbrains.mps.nodeEditor.cellActions.CellAction_InsertIntoCollection;
import jetbrains.mps.nodeEditor.cellActions.SideTransformSubstituteInfo;
import jetbrains.mps.nodeEditor.cellLayout.CellLayout_Horizontal;
import jetbrains.mps.nodeEditor.sidetransform.EditorCell_STHint;
import jetbrains.mps.nodeEditor.updater.UpdaterImpl;
import jetbrains.mps.openapi.editor.EditorContext;
import jetbrains.mps.openapi.editor.cells.SubstituteInfo;
import jetbrains.mps.project.ModuleId;
import jetbrains.mps.project.Solution;
import jetbrains.mps.project.structure.modules.SolutionDescriptor;
import jetbrains.mps.smodel.EditableModelDescriptor;
import jetbrains.mps.smodel.ModelLoadResult;
import jetbrains.mps.smodel.SModelId;
import jetbrains.mps.smodel.SModelReference;
import jetbrains.mps.smodel.loading.ModelLoadingState;
import mpsj2cl.editor.BrowserEditorHost;
import mpsj2cl.editorlive.ExpressionLanguage;
import org.jetbrains.mps.openapi.model.SNode;
import org.jetbrains.mps.openapi.persistence.NullDataSource;
import org.webawt.WebAwt;

/** Runs real MPS cells and extension runtime contracts in the browser. */
public final class RuntimeContracts {
  private static String result;
  private static int checks;
  private static BrowserEditorHost host;
  private static NodeEditorComponent editor;
  private static Solution solution;
  private static MemoryModel model;
  private static SNode node;
  public static String result() { return result; }
  public static void start() {
    WebAwt.install("extensions-root");
    SwingUtilities.invokeLater(() -> {
      try {
        grammar();
        queryContexts();
        setup();
        host.getProject().getModelAccess().runReadAction(RuntimeContracts::cells);
        arbitraryText();
        // A write command must start outside the read action used to construct cells.
        checkbox();
      } catch (Throwable t) {
        t.printStackTrace();
        finish("FAIL: " + t);
      }
    });
  }

  private static void grammar() {
    InputCursor cursor = new InputCursor(Arrays.asList(new ConstantToken("a"), new ConstantToken("+"), new ConstantToken("b")));
    cursor.next();
    InputCursor saved = cursor.save();
    cursor.next();
    require("+".equals(saved.peek().getTextValue()), "parser checkpoint preserves position independently");
    cursor.restore(saved);
    require("+".equals(cursor.getNext().getTextValue()), "parser checkpoint restore");
    require("b".equals(cursor.peek().getTextValue()), "parser token advancement");
    require(new NumberLiteralTokenizer().tokenize("12.5", "+7").equals(Arrays.asList("12.5", "+", "7")), "number tokenization across caret");
    require(new ConstantToken("x").equals(new ConstantToken("x")), "constant token equality");
    require(!new ConstantToken("x").equals(new ConstantToken("y")), "constant token inequality");
  }

  private static void queryContexts() {
    QueryListContext outer = new QueryListContext(null), inner = new QueryListContext(null);
    QueryListContext.runWithContext(outer, () -> {
      require(QueryListContext.getCurrentContext() == outer, "outer query context");
      try {
        QueryListContext.runWithContext(inner, () -> {
          require(inner.getParentContext() == outer, "nested query parent");
          inner.increaseIndex();
          require(inner.getIndex() == 1, "query index advances");
          throw new IllegalStateException("test unwind");
        });
      } catch (IllegalStateException expected) {}
      require(QueryListContext.getCurrentContext() == outer, "exception restores enclosing query context");
    });
  }

  private static void setup() {
    host = new BrowserEditorHost("Extensions runtime contracts");
    host.getPlatform().registerLanguage(new AttributeCoreLanguage());
    host.getPlatform().registerLanguage(ExpressionLanguage.typeVariableLanguage());
    host.getPlatform().registerLanguage(new ExpressionLanguage());
    host.getPlatform().registerLanguage(new MathRuntimeLanguage());
    host.getPlatform().registerLanguage(new SpecializedLanguage());
    host.getPlatform().registerLanguage(new com.mbeddr.mpsutil.grammarcells.runtimelang.Language());
    GrammarCellsRuntime.init();
    host.getProject().getModelAccess().executeCommand(() -> {
      SolutionDescriptor descriptor = new SolutionDescriptor();
      descriptor.setId(ModuleId.regular(new UUID(188, 1)));
      descriptor.setNamespace("extensionsj2cl.test");
      solution = new Solution(descriptor, null) { @Override public boolean isPackaged() { return false; } };
      host.getProject().addModule(solution);
      model = new MemoryModel(new SModelReference(solution.getModuleReference(), SModelId.regular(new UUID(188, 2)), "extensionsj2cl.test.model"));
      solution.registerModel(model);
      model.addLanguage(ExpressionLanguage.LITERAL.getLanguage());
      node = model.createNode(ExpressionLanguage.LITERAL);
      node.setProperty(ExpressionLanguage.VALUE, "false");
      SNode root = model.createNode(ExpressionLanguage.GROUP);
      root.addChild(ExpressionLanguage.MEMBERS, node);
      model.addRootNode(root);
    });
    host.getProject().getModelAccess().runReadAction(() -> editor = host.createEditor(node));
  }

  private static void cells() {
    EditorContext context = editor.getEditorContext();
    require(EditorComponentHacks.getPositionTracker(editor) != null, "nested cursor tracker hack");
    require(ReflectionUtil.readField(EditorComponent.class, editor, "myEditorComponentActions") == editor.myEditorComponentActions, "component actions identity");
    require(ReflectionUtil.readField(EditorComponentActions.class, editor.myEditorComponentActions, "myActionMap") == editor.myEditorComponentActions.myActionMap, "action map identity");
    NodeEditorActions.MoveDown down = new NodeEditorActions.MoveDown(new jetbrains.mps.nodeEditor.actions.CursorPositionTracker(context));
    require(ReflectionUtil.readField(NodeEditorActions.MoveDown.class, down, "myPositionTracker") == down.myPositionTracker, "cursor tracker identity");
    UpdaterImpl updater = (UpdaterImpl) editor.getUpdater();
    require(ReflectionUtil.readField(UpdaterImpl.class, updater, "myBigCellsMap") == updater.myBigCellsMap, "query list uses actual updater map");

    EditorCell_Constant label = new EditorCell_Constant(context, node, "abcd");
    label.setSelectionStart(1); label.setSelectionEnd(3);
    require("aXd".equals(ReflectionUtil.callMethod(EditorCell_Label.class, label, "getUpdatedText", new Class<?>[]{String.class}, new Object[]{"X"})), "label selection replacement");
    require("abcd".equals(label.getText()), "text preview does not mutate label");
    EditorCell_STHint hint = new EditorCell_STHint(editor.getRootCell(), label, SideTransformSubstituteInfo.Side.RIGHT, null);
    require(ReflectionUtil.readField(EditorCell_STHint.class, hint, "myAnchorCell") == label, "side transform anchor identity");
    jetbrains.mps.openapi.editor.menus.substitute.SubstituteMenuItem substitute = new jetbrains.mps.openapi.editor.menus.substitute.SubstituteMenuItem() {
      public org.jetbrains.mps.openapi.language.SAbstractConcept getOutputConcept() { return ExpressionLanguage.GROUP; }
      public SNode getType(String pattern) { return null; }
      public String getMatchingText(String pattern) { return "group"; }
      public String getDescriptionText(String pattern) { return "test"; }
      public boolean canExecute(String pattern) { return true; }
      public boolean canExecuteStrictly(String pattern) { return false; }
      public SNode createNode(String pattern) { return null; }
      public jetbrains.mps.smodel.runtime.IconResource getIcon(String pattern) { return null; }
      public void select(SNode createdNode, String pattern) {}
    };
    DefaultSubstituteMenuItemAsActionItem item = new DefaultSubstituteMenuItemAsActionItem(substitute, null);
    require(ReflectionUtil.readField(SubstituteMenuItemAsActionItem.class, item, "mySubstituteItem") == substitute, "substitution item identity");
    require(de.slisson.mps.hacks.editor.SubstituteUtil.getOutputConcept(
        new jetbrains.mps.nodeEditor.cellMenu.CompletionActionItemAsSubstituteAction(item, node, context.getRepository())) == ExpressionLanguage.GROUP,
        "substitution hack preserves wrapper output concept");

    QueryListHandler handler = new QueryListHandler(context, node) {
      public Iterable<SNode> getNodesForList(SNode owner) { return Collections.emptyList(); }
      public SubstituteInfo getSubstituteInfo(SNode owner, SNode child, SubstituteInfoFactory factory, EditorContext c) { return null; }
      public void insertNewNode(SNode owner, SNode anchor, boolean before, EditorContext c) {}
      public void deleteNode(SNode owner, SNode child, EditorContext c) {}
    };
    CellAction_InsertIntoCollection insert = new CellAction_InsertIntoCollection(handler, false);
    require(ReflectionUtil.readField(CellAction_InsertIntoCollection.class, insert, "myListHandler") == handler, "insert action handler identity");
    EditorCell_QueryList query = handler.createCells(new CellLayout_Horizontal());
    require(((org.jetbrains.mps.openapi.language.SContainmentLink) query.getSRole()).isMultiple(), "query list insertion role");
    query.toggleDisableModelChecking(true);
    require(query.disableModelChecking(), "query list model checking flag");
    require(EditorHierachyCache.getInstance().getActiveEditor(ExpressionLanguage.LITERAL) != null, "editor hierarchy default-hints overload");
    EditorHierachyCache hierarchy = EditorHierachyCache.getInstance();
    require(hierarchy.getActiveEditor(SpecializedLanguage.CONCEPT) instanceof SpecializedLanguage.WrapperEditor, "specialized editor selected");
    hierarchy.registerConditionalEditor(SpecializedLanguage.WrapperEditor.class);
    hierarchy.invalidate();
    require(hierarchy.getActiveEditor(SpecializedLanguage.CONCEPT).getClass() == hierarchy.getActiveEditor(ExpressionLanguage.GROUP).getClass(),
        "conditional wrapper ignored without class-name metadata");
    hierarchy.unregisterConditionalEditor(SpecializedLanguage.WrapperEditor.class);
    require(hierarchy.getActiveEditor(SpecializedLanguage.CONCEPT) instanceof SpecializedLanguage.WrapperEditor, "conditional registration invalidates cached hierarchy");
    require(GrammarCellsUtil.getSpecializedTargetConcept(ExpressionLanguage.MEMBERS, SpecializedLanguage.CONCEPT).equals(ExpressionLanguage.LITERAL),
        "compiled containment specialization without language-design models");
    require(GrammarCellsUtil.getSpecializedTargetConcept(ExpressionLanguage.MEMBERS, ExpressionLanguage.GROUP).equals(ExpressionLanguage.MEMBERS.getTargetConcept()),
        "unspecialized containment target");

    EditorCell_MathEnd end = new EditorCell_MathEnd(context, node, true);
    require(end.isValidText("") && end.isValidText(" ") && !end.isValidText("x"), "math end input contract");
    MathCellLayout layout = new MathCellLayout() {
      protected void layout(MathLayoutableCell cell, List<MathLayoutableCell> children, List<IMathSymbol> symbols) {
        int x = 0;
        for (MathLayoutableCell child : children) { child.setX(x); child.setY(0); x += child.getWidthInt(); }
      }
    };
    EditorCell_MathBase math = new EditorCell_MathBase(context, node, layout) {
      protected void paint(Graphics2D g, List<MathLayoutableCell> children, MathLayoutableCell cell, List<IMathSymbol> symbols) {}
    };
    math.addEditorCell(new EditorCell_Constant(context, node, "x"));
    math.init(); math.relayout();
    require(math.showEndCells() && math.getLeftEndCell() != math.getRightEndCell(), "math style and helper cell initialization");
    require(math.getWidth() > 0 && math.getHeight() > 0, "math cell layout");
    Path2D.Double path = new Path2D.Double();
    path.moveTo(0, 0); path.lineTo(2, 4);
    Rectangle2D bounds = MathDrawUtil.changeBounds(path, new Rectangle2D.Double(10, 20, 30, 40)).getBounds2D();
    require(bounds.getX() == 10 && bounds.getY() == 20 && bounds.getWidth() == 30 && bounds.getHeight() == 40, "math symbol geometry");
    require(path.getBounds2D().getWidth() == 2, "math geometry leaves original unchanged");
    BufferedImage image = new BufferedImage(80, 80, BufferedImage.TYPE_INT_ARGB);
    Graphics2D graphics = image.createGraphics();
    new MathDrawUtil(graphics).drawLeftParenthesis(new Rectangle2D.Double(5, 5, 20, 60));
    CheckboxImages images = new CheckboxImages(context.getRepository());
    images.setChecked(true); images.paint(graphics, 40, 20, editor); graphics.dispose();
    require(images.getWidth() == 12 && images.getHeight() == 12, "default checkbox image dimensions");
    int painted = 0;
    for (int y = 0; y < 80; y++) for (int x = 0; x < 80; x++) if ((image.getRGB(x, y) >>> 24) != 0) painted++;
    require(painted > 0, "math and checkbox drawing produces pixels");
    for (IMathSymbol symbol : Arrays.asList(new de.itemis.mps.editor.math.symbols.SumSymbol(),
        new de.itemis.mps.editor.math.symbols.FontIntegralSymbol(), new de.itemis.mps.editor.math.symbols.LeftParenthesis())) {
      symbol.setBounds(new Rectangle2D.Double(10, 10, 25, 50));
      symbol.updateDimension();
      BufferedImage symbolImage = new BufferedImage(80, 80, BufferedImage.TYPE_INT_ARGB);
      Graphics2D symbolGraphics = symbolImage.createGraphics();
      symbol.paint(symbolGraphics);
      symbolGraphics.dispose();
      int symbolPixels = 0;
      for (int y = 0; y < 80; y++) for (int x = 0; x < 80; x++) if ((symbolImage.getRGB(x, y) >>> 24) != 0) symbolPixels++;
      require(symbol.getWidth() > 0 && symbolPixels > 0, "built-in math symbol layout and painting");
    }
    AbstractGrammarActionDescriptor descriptor = new AbstractGrammarActionDescriptor() {};
    DescriptorCache cache = DescriptorCache.getInstance();
    cache.register(solution.getModuleReference(), descriptor);
    require(cache.getDescriptor(solution) == descriptor, "static grammar descriptor registration");
    cache.invalidate();
    require(cache.getDescriptor(solution) == descriptor, "cache invalidation preserves compiled registration");
    cache.unregister(solution.getModuleReference());
    require(cache.getDescriptor(solution) == null, "grammar descriptor unregister");
    IRule rule = new AbstractRule() {
      public List<ISymbol> getSymbols() { return Arrays.asList(new ConstantSymbol("hello"), new ConstantSymbol("world")); }
      public org.jetbrains.mps.openapi.language.SAbstractConcept getOutputConcept() { return ExpressionLanguage.LITERAL; }
      public org.jetbrains.mps.openapi.language.SAbstractConcept getDefinedForConcept() { return ExpressionLanguage.LITERAL; }
    };
    AbstractGrammarActionDescriptor grammar = new AbstractGrammarActionDescriptor() {
      @Override public List<IRule> getRules(org.jetbrains.mps.openapi.model.SModel m) { return Collections.singletonList(rule); }
    };
    cache.register(ExpressionLanguage.LITERAL.getLanguage().getSourceModuleReference(), grammar);
    require(GrammarCellsUtil.getVisibleDescriptors(model).contains(grammar), "compiled grammar discovery without a source language module");
    Parser parser = new Parser(model);
    PNode parsed = parser.parse(Arrays.asList(new ConstantToken("hello"), new ConstantToken("world")), null);
    require(parsed != null && parsed.getRule() == rule && parsed.getChildren().size() == 2, "grammar parser uses registered descriptor and partitions tokens");
    require(parser.parse(Arrays.asList(new ConstantToken("hello"), new ConstantToken("wrong")), null) == null, "grammar parser rejects unmatched tokens");
    cache.unregister(ExpressionLanguage.LITERAL.getLanguage().getSourceModuleReference());
    expectUnsupported(() -> ReflectionUtil.readField(EditorComponent.class, editor, "unknown"));
    expectUnsupported(() -> ReflectionUtil.callMethod(EditorCell_Label.class, label, "getUpdatedText", new Class<?>[0], new Object[0]));
    expectUnsupported(() -> ReflectionUtil.getClass("unknown"));
    expectUnsupported(() -> ReflectionUtil.writeField(EditorComponent.class, editor, "myEditorComponentActions", null));
  }

  private static void arbitraryText() {
    org.jetbrains.mps.openapi.language.SConcept concept = jetbrains.mps.smodel.adapter.structure.MetaAdapterFactory.getConcept(
        0xb4f35ed845af4efaL, 0xabe400ac26956e69L, 0x468dcccb641e8fb9L,
        "com.mbeddr.mpsutil.grammarcells.runtimelang.structure.ArbitraryTextAnnotation");
    SNode[] annotation = new SNode[1];
    host.getProject().getModelAccess().executeCommand(() -> {
      annotation[0] = model.createNode(concept);
      annotation[0].setProperty(jetbrains.mps.smodel.adapter.structure.MetaAdapterFactory.getProperty(
          0xb4f35ed845af4efaL, 0xabe400ac26956e69L, 0x468dcccb641e8fb9L, 0x468dcccb641e99b6L, "text"), "?");
      new jetbrains.mps.lang.smodel.generator.smodelAdapter.IAttributeDescriptor.NodeAttribute(concept).set(node, annotation[0]);
      require(annotation[0].getParent() == node, "arbitrary text attached to model");
    });
    host.getProject().getModelAccess().runReadAction(() -> {
      editor.rebuildEditorContent();
      require(editor.findNodeCell(annotation[0]) != null, "generated arbitrary-text annotation editor renders");
    });
    host.getProject().getModelAccess().executeCommand(() -> {
      new com.mbeddr.mpsutil.grammarcells.runtimelang.editor.ArbitraryTextDeleteAction(annotation[0]).execute(editor.getEditorContext());
      require(annotation[0].getParent() == null, "browser handler deletes arbitrary text and reparses");
      require(node.getModel() == model, "annotation deletion preserves edited node");
    });
    GrammarCellsRuntime.dispose();
    host.getProject().getModelAccess().executeCommand(() -> {
      new jetbrains.mps.lang.smodel.generator.smodelAdapter.IAttributeDescriptor.NodeAttribute(concept).set(node, annotation[0]);
      new com.mbeddr.mpsutil.grammarcells.runtimelang.editor.ArbitraryTextDeleteAction(annotation[0]).execute(editor.getEditorContext());
      require(annotation[0].getParent() == node, "browser disposal removes deletion handler");
      GrammarCellsRuntime.init();
      new com.mbeddr.mpsutil.grammarcells.runtimelang.editor.ArbitraryTextDeleteAction(annotation[0]).execute(editor.getEditorContext());
      require(annotation[0].getParent() == null, "browser handler can be initialized again");
      SNode root = node.getContainingRoot();
      new jetbrains.mps.lang.smodel.generator.smodelAdapter.IAttributeDescriptor.NodeAttribute(concept).set(root, annotation[0]);
      new com.mbeddr.mpsutil.grammarcells.runtimelang.editor.ArbitraryTextDeleteAction(annotation[0]).execute(editor.getEditorContext());
      require(annotation[0].getParent() == null, "last annotation deletion on a model root");
      require(root.getModel() == model && node.getParent() == root, "root deletion preserves the expression tree");
    });
  }

  private static void checkbox() {
    EditorCell_Checkbox[] cell = new EditorCell_Checkbox[1];
    host.getProject().getModelAccess().runReadAction(() -> cell[0] = EditorCell_Checkbox.create(editor.getEditorContext(), new ModelAccessor() {
      public String getText() { return node.getProperty(ExpressionLanguage.VALUE); }
      public void setText(String text) { node.setProperty(ExpressionLanguage.VALUE, text); }
      public boolean isValidText(String text) { return "true".equals(text) || "false".equals(text); }
    }, node));
    cell[0].toggle();
    later(() -> {
      host.getProject().getModelAccess().runReadAction(() -> require("true".equals(node.getProperty(ExpressionLanguage.VALUE)), "checkbox commits to attached MPS model"));
      cell[0].toggle();
      later(() -> {
        host.getProject().getModelAccess().runReadAction(() -> require("false".equals(node.getProperty(ExpressionLanguage.VALUE)), "checkbox toggles back"));
        finish("PASS: " + checks + " checks");
      });
    });
  }

  private static void later(Runnable action) {
    host.getApplication().invokeLater(() -> {
      try { action.run(); }
      catch (Throwable t) { t.printStackTrace(); finish("FAIL: " + t); }
    });
  }

  private static void finish(String output) {
    try {
      if (host != null) {
        GrammarCellsRuntime.dispose();
        host.dispose();
        host = null;
      }
      result = output;
    } catch (Throwable t) { t.printStackTrace(); result = "FAIL during disposal: " + t; }
  }

  private static void expectUnsupported(Runnable action) {
    try { action.run(); throw new AssertionError("unsupported hack accepted"); }
    catch (UnsupportedOperationException expected) { checks++; }
  }
  private static void require(boolean value, String message) {
    if (!value) throw new AssertionError(message);
    checks++;
  }
  private static final class MemoryModel extends EditableModelDescriptor {
    MemoryModel(org.jetbrains.mps.openapi.model.SModelReference reference) { super(reference, new NullDataSource()); }
    @Override protected ModelLoadResult<jetbrains.mps.smodel.SModel> createModel() {
      return new ModelLoadResult<>(new jetbrains.mps.smodel.SModel(getReference()), ModelLoadingState.FULLY_LOADED);
    }
    @Override public boolean isReadOnly() { return false; }
    @Override public void save() { throw new UnsupportedOperationException(); }
    @Override public void reloadFromSource() { throw new UnsupportedOperationException(); }
  }
}
