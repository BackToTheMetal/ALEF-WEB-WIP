package mpsj2cl.financial;

import java.awt.BorderLayout;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import jetbrains.mps.nodeEditor.NodeEditorComponent;
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
import mpsj2cl.editor.BrowserEditorHost;
import org.jetbrains.mps.openapi.language.*;
import org.jetbrains.mps.openapi.model.SNode;
import org.jetbrains.mps.openapi.persistence.NullDataSource;
import org.webawt.WebAwt;

/** An in-memory calculator instance edited by the upstream generated financial language. */
public final class FinancialDemo {
  private static final Map<String, SConcept> concepts = new HashMap<>();
  private static BrowserEditorHost host;
  private static FinancialLanguages languages;
  private static NodeEditorComponent editor;
  private static JFrame window;
  private static MemoryModel model;
  private static SNode calculator;
  private static SNode interest;
  private static String status = "Starting";

  public static void start() {
    WebAwt.install("financial-editor");
    SwingUtilities.invokeLater(() -> {
      try {
        status = "Starting MPS platform";
        host = new BrowserEditorHost("Financial calculations");
        host.getPlatform().findComponent(jetbrains.mps.persistence.PersistenceRegistry.class)
            .setModelIdFactory("java", new jetbrains.mps.java.stub.JavaPackageModelId.Factory());
        status = "Registering generated languages";
        languages = new FinancialLanguages(host.getPlatform());
        host.getProject().getModelAccess().executeCommand(FinancialDemo::loadPrimitiveTypes);
        host.getProject().getModelAccess().runReadAction(() -> languages.verifyTypesystems(host.getPlatform()));
        index(new jetbrains.mps.samples.fincalculator.structure.StructureAspectDescriptor());
        index(new jetbrains.mps.baseLanguage.structure.StructureAspectDescriptor());
        status = "Creating calculator model";
        host.getProject().getModelAccess().executeCommand(FinancialDemo::createModel);
        status = "Building generated editor cells";
        host.getProject().getModelAccess().runReadAction(() -> editor = host.createEditor(calculator));
        window = new JFrame("Financial calculations");
        window.getContentPane().add(editor.getExternalComponent(), BorderLayout.CENTER);
        window.setSize(1080, 600);
        window.setVisible(true);
        editor.relayout();
        if (editor.getRootCell() == null || editor.findCellWithId(calculator, "property_name") == null) {
          throw new IllegalStateException("The generated Calculator editor did not produce its name cell");
        }
        status = "READY";
      } catch (Throwable failure) {
        failure.printStackTrace();
        status = "FAIL: " + failure;
      }
    });
  }

  public static String getStatus() { return status; }
  public static String getName() {
    if (calculator == null) return "";
    return host.getProject().getModelAccess().computeReadAction(() -> calculator.getProperty(property(calculator, "name")));
  }
  public static String getInterestType() {
    return host.getProject().getModelAccess().computeReadAction(() -> {
      jetbrains.mps.typechecking.TypecheckingFacade facade = jetbrains.mps.typechecking.TypecheckingFacade.getFromContext();
      SNode type = facade.computeWithSession(editor.getTypecheckingSession(), () -> facade.getTypeOf(interest));
      return type == null ? "" : type.getConcept().getName();
    });
  }
  /** Read-only observations used by the native keyboard regression smoke test. */
  public static String getSelectionState() {
    return host.getProject().getModelAccess().computeReadAction(() -> {
      jetbrains.mps.openapi.editor.cells.EditorCell cell = editor.getSelectedCell();
      if (cell == null) return "";
      if (cell instanceof jetbrains.mps.nodeEditor.cells.EditorCell_Label) {
        jetbrains.mps.nodeEditor.cells.EditorCell_Label label = (jetbrains.mps.nodeEditor.cells.EditorCell_Label) cell;
        return label.getText() + "|" + label.getCaretPosition() + "|" + label.getY();
      }
      return cell.getCellId() + "|-1|" + cell.getY();
    });
  }
  public static void focusName() {
    SwingUtilities.invokeLater(() -> host.getProject().getModelAccess().runReadAction(() -> {
      jetbrains.mps.nodeEditor.cells.EditorCell_Label cell =
          (jetbrains.mps.nodeEditor.cells.EditorCell_Label) editor.findCellWithId(calculator, "property_name");
      editor.changeSelection(cell);
      cell.selectAll();
      editor.requestFocusInWindow();
    }));
  }
  public static void close() {
    SwingUtilities.invokeLater(() -> {
      if (window != null) window.dispose();
      if (host != null) host.dispose();
      if (languages != null) languages.close();
      status = "CLOSED";
    });
  }

  private static void index(StructureAspectDescriptor structure) {
    for (ConceptDescriptor descriptor : structure.getDescriptors()) {
      String name = descriptor.getConceptFqName();
      concepts.put(name.substring(name.lastIndexOf('.') + 1), MetaAdapterFactory.getConcept(descriptor.getId(), name));
    }
  }
  private static SNode node(String concept) {
    SConcept type = concepts.get(concept);
    if (type == null) throw new IllegalArgumentException("Missing generated concept " + concept);
    return model.createNode(type);
  }
  private static SProperty property(SNode node, String name) {
    for (SProperty property : node.getConcept().getProperties()) if (name.equals(property.getName())) return property;
    throw new IllegalArgumentException(node.getConcept() + " has no property " + name);
  }
  private static void text(SNode node, String name, String value) { node.setProperty(property(node, name), value); }
  private static void child(SNode parent, String role, SNode child) {
    for (SContainmentLink link : parent.getConcept().getContainmentLinks()) {
      if (role.equals(link.getName())) { parent.addChild(link, child); return; }
    }
    throw new IllegalArgumentException(parent.getConcept() + " has no child role " + role);
  }
  private static SNode reference(String concept, String role, SNode target) {
    SNode node = node(concept);
    for (SReferenceLink link : node.getConcept().getReferenceLinks()) {
      if (role.equals(link.getName())) { node.setReferenceTarget(link, target); return node; }
    }
    throw new IllegalArgumentException(concept + " has no reference " + role);
  }
  private static void kind(SNode node, String name) {
    SProperty property = property(node, "value_kind");
    SEnumeration enumeration = (SEnumeration) property.getType();
    node.setProperty(property, enumeration.toString(enumeration.getLiteral(name)));
  }
  private static SNode input(String name, String kind) {
    SNode input = node("InputField");
    text(input, "name", name); kind(input, kind); child(calculator, "commands", input);
    return input;
  }
  private static SNode binary(String type, SNode left, SNode right) {
    SNode expression = node(type);
    child(expression, "leftExpression", left); child(expression, "rightExpression", right);
    return expression;
  }
  private static void createModel() {
    SolutionDescriptor descriptor = new SolutionDescriptor();
    descriptor.setId(ModuleId.regular(new UUID(20260910, 1)));
    descriptor.setNamespace("mpsj2cl.financial.session");
    Solution solution = new MemorySolution(descriptor);
    host.getProject().addModule(solution);
    model = new MemoryModel(new SModelReference(solution.getModuleReference(), SModelId.regular(new UUID(20260910, 2)), "financial.Savings"));
    solution.registerModel(model);
    model.addLanguage(concepts.get("Calculator").getLanguage());
    model.addLanguage(concepts.get("IntegerConstant").getLanguage());
    calculator = node("Calculator");
    text(calculator, "name", "Savings");
    model.addRootNode(calculator);
    SNode principal = input("Principal", "amount");
    SNode rate = input("AnnualRate", "percent");
    SNode hundred = node("IntegerConstant"); text(hundred, "value", "100");
    interest = node("CalculationValue"); text(interest, "name", "Interest");
    child(interest, "initializer", binary("DivExpression",
        binary("MulExpression", reference("FieldReference", "field", principal), reference("FieldReference", "field", rate)), hundred));
    child(calculator, "commands", interest);
    SNode result = node("OutputField"); text(result, "name", "Total"); kind(result, "amount");
    child(result, "expression", binary("PlusExpression", reference("FieldReference", "field", principal),
        reference("CalculationValueReference", "target", interest)));
    child(calculator, "commands", result);
  }
  private static void loadPrimitiveTypes() {
    SolutionDescriptor descriptor = new SolutionDescriptor();
    descriptor.setId(ModuleId.regular(new UUID(20260910, 3)));
    descriptor.setNamespace("mpsj2cl.financial.accessories");
    Solution solution = new MemorySolution(descriptor);
    host.getProject().addModule(solution);
    org.jetbrains.mps.openapi.model.SModelReference reference =
        jetbrains.mps.persistence.PersistenceRegistry.getInstance().createModelReference(
            "r:00000000-0000-4000-0000-011c895902de(jetbrains.mps.baseLanguage.blTypes.primitiveDescriptors)");
    MemoryModel accessory = new MemoryModel(reference, PrimitiveModelResource.bytes());
    solution.registerModel(accessory);
    if (accessory.getNode(jetbrains.mps.smodel.SNodeId.fromString("1196683729865")) == null) {
      throw new IllegalStateException("The original numeric primitive descriptor was not loaded");
    }
  }
  private static final class MemorySolution extends Solution {
    MemorySolution(SolutionDescriptor descriptor) { super(descriptor, null); }
    @Override public boolean isPackaged() { return false; }
  }
  private static final class MemoryModel extends EditableModelDescriptor {
    private final byte[] content;
    MemoryModel(org.jetbrains.mps.openapi.model.SModelReference reference) { this(reference, null); }
    MemoryModel(org.jetbrains.mps.openapi.model.SModelReference reference, byte[] content) { super(reference, new NullDataSource()); this.content = content; }
    @Override protected ModelLoadResult<jetbrains.mps.smodel.SModel> createModel() {
      jetbrains.mps.smodel.SModel data = new jetbrains.mps.smodel.SModel(getReference());
      if (content == null) return new ModelLoadResult<>(data, ModelLoadingState.FULLY_LOADED);
      return jetbrains.mps.smodel.persistence.def.v9.BrowserModelReader.read(data, content);
    }
    @Override public boolean isReadOnly() { return content != null; }
    @Override public void save() { throw new UnsupportedOperationException("Browser session has no persistence backend"); }
    @Override public void reloadFromSource() { throw new UnsupportedOperationException("Reload the page to reset the session"); }
  }
}
