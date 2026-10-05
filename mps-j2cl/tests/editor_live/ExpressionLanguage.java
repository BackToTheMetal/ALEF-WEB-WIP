package mpsj2cl.editorlive;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import jetbrains.mps.core.aspects.behaviour.BaseBehaviorAspectDescriptor;
import jetbrains.mps.core.aspects.behaviour.EmptyBHDescriptor;
import jetbrains.mps.core.aspects.behaviour.api.BHDescriptor;
import jetbrains.mps.errors.messageTargets.PropertyMessageTarget;
import jetbrains.mps.errors.messageTargets.ReferenceMessageTarget;
import jetbrains.mps.lang.typesystem.runtime.AbstractInferenceRule_Runtime;
import jetbrains.mps.lang.typesystem.runtime.AbstractNonTypesystemRule_Runtime;
import jetbrains.mps.lang.typesystem.runtime.BaseHelginsDescriptor;
import jetbrains.mps.lang.typesystem.runtime.IHelginsDescriptor;
import jetbrains.mps.lang.typesystem.runtime.IsApplicableStatus;
import jetbrains.mps.nodeEditor.EditorAspectDescriptorBase;
import jetbrains.mps.nodeEditor.cellMenu.AbstractNodeSubstituteInfo;
import jetbrains.mps.nodeEditor.cellMenu.NodeSubstituteInfoFilterDecorator;
import jetbrains.mps.nodeEditor.cells.EditorCell_Collection;
import jetbrains.mps.nodeEditor.cells.EditorCell_Constant;
import jetbrains.mps.nodeEditor.cells.EditorCell_Property;
import jetbrains.mps.nodeEditor.cells.PropertyAccessor;
import jetbrains.mps.openapi.editor.EditorContext;
import jetbrains.mps.openapi.editor.cells.EditorCell;
import jetbrains.mps.openapi.editor.cells.SubstituteAction;
import jetbrains.mps.openapi.editor.descriptor.ConceptEditor;
import jetbrains.mps.openapi.editor.descriptor.EditorAspectDescriptor;
import jetbrains.mps.openapi.intentions.IntentionAspectDescriptor;
import jetbrains.mps.openapi.intentions.IntentionDescriptor;
import jetbrains.mps.openapi.intentions.IntentionExecutable;
import jetbrains.mps.openapi.intentions.IntentionFactory;
import jetbrains.mps.openapi.intentions.Kind;
import jetbrains.mps.smodel.SNodeUtil;
import jetbrains.mps.smodel.action.AbstractSubstituteAction;
import jetbrains.mps.smodel.adapter.ids.MetaIdFactory;
import jetbrains.mps.smodel.adapter.ids.MetaIdHelper;
import jetbrains.mps.smodel.adapter.ids.PrimitiveTypeId;
import jetbrains.mps.smodel.adapter.ids.SConceptId;
import jetbrains.mps.smodel.adapter.ids.SLanguageId;
import jetbrains.mps.smodel.adapter.structure.MetaAdapterFactory;
import jetbrains.mps.smodel.language.LanguageRuntime;
import jetbrains.mps.smodel.runtime.BaseConstraintsAspectDescriptor;
import jetbrains.mps.smodel.runtime.BaseStructureAspectDescriptor;
import jetbrains.mps.smodel.runtime.BehaviorAspectDescriptor;
import jetbrains.mps.smodel.runtime.CheckingNodeContext;
import jetbrains.mps.smodel.runtime.ConceptDescriptor;
import jetbrains.mps.smodel.runtime.ConstraintsAspectDescriptor;
import jetbrains.mps.smodel.runtime.ConstraintsDescriptor;
import jetbrains.mps.smodel.runtime.ConstraintsDescriptorInitContext;
import jetbrains.mps.smodel.runtime.ILanguageAspect;
import jetbrains.mps.smodel.runtime.StructureAspectDescriptor;
import jetbrains.mps.smodel.runtime.base.BaseConstraintsDescriptor;
import jetbrains.mps.smodel.runtime.base.BasePropertyConstraintsDescriptor;
import jetbrains.mps.smodel.runtime.impl.ConceptDescriptorBuilder2;
import jetbrains.mps.typesystem.inference.EquationInfo;
import jetbrains.mps.typesystem.inference.TypeCheckingContext;
import org.jetbrains.mps.openapi.language.SAbstractConcept;
import org.jetbrains.mps.openapi.language.SConcept;
import org.jetbrains.mps.openapi.language.SContainmentLink;
import org.jetbrains.mps.openapi.language.SProperty;
import org.jetbrains.mps.openapi.language.SReferenceLink;
import org.jetbrains.mps.openapi.model.SNode;
import org.jetbrains.mps.openapi.model.SNodeReference;

/** Handwritten test language. This is not generated MPS output or an MPS language distribution. */
public final class ExpressionLanguage extends LanguageRuntime {
  public static final String NAME = "mpsj2cl.editorlive.expression";
  public static final SLanguageId ID = MetaIdFactory.langId(94, 112);
  public static final SConcept LITERAL = concept(1, "IntegerLiteral");
  public static final SConcept INTEGER = concept(2, "IntegerType");
  public static final SConcept TEXT = concept(3, "TextType");
  public static final SProperty VALUE = MetaAdapterFactory.getProperty(94, 112, 1, 4, "value");
  public static final SConcept GROUP = concept(5, "Group");
  public static final SContainmentLink MEMBERS = MetaAdapterFactory.getContainmentLink(94, 112, 5, 6, "members");
  public static final SConcept REFERENCE = concept(7, "Reference");
  public static final SReferenceLink TARGET = MetaAdapterFactory.getReferenceLink(94, 112, 7, 8, "target");
  public static int inferenceRuns;
  public static int groupInferenceRuns;
  public static int referenceInferenceRuns;
  public static int intentionQueries;
  public static int editorBuilds;
  public static int constraintChecks;

  private static SConcept concept(long id, String name) {
    return MetaAdapterFactory.getConcept(94, 112, id, NAME + ".structure." + name);
  }
  @Override public String getNamespace() { return NAME; }
  @Override public SLanguageId getId() { return ID; }
  @Override public int getVersion() { return 0; }
  @Override protected <T extends ILanguageAspect> T createAspect(Class<T> type) {
    if (type == StructureAspectDescriptor.class) {
      SConceptId base = SNodeUtil.conceptId_BaseConcept;
      return type.cast(new Structure(
          new ConceptDescriptorBuilder2(NAME, "IntegerLiteral", 94, 112, 1).class_(false, false, true)
              .property("value", 4).type(PrimitiveTypeId.STRING).done().create(),
          new ConceptDescriptorBuilder2(NAME, "IntegerType", 94, 112, 2).class_(false, false).create(),
          new ConceptDescriptorBuilder2(NAME, "TextType", 94, 112, 3).class_(false, false).create(),
          new ConceptDescriptorBuilder2(NAME, "Group", 94, 112, 5).class_(false, false, true)
              .aggregate("members", 6).target(base.getLanguageId().getHighBits(), base.getLanguageId().getLowBits(), base.getIdValue())
              .multiple(true).optional(true).done().create(),
          new ConceptDescriptorBuilder2(NAME, "Reference", 94, 112, 7).class_(false, false)
              .associate("target", 8).target(94, 112, 1).optional(false).done().create()));
    }
    if (type == EditorAspectDescriptor.class) return type.cast(new Editors());
    if (type == IHelginsDescriptor.class) return type.cast(new Types());
    if (type == BehaviorAspectDescriptor.class) return type.cast(new Behaviors());
    if (type == ConstraintsAspectDescriptor.class) return type.cast(new Constraints());
    if (type == IntentionAspectDescriptor.class) return type.cast(new Intentions());
    return null;
  }

  /** Minimal, explicitly handwritten core metadata for this isolated fixture. */
  public static LanguageRuntime coreLanguage() {
    return new LanguageRuntime() {
      @Override public String getNamespace() { return "jetbrains.mps.lang.core"; }
      @Override public SLanguageId getId() { return SNodeUtil.conceptId_BaseConcept.getLanguageId(); }
      @Override public int getVersion() { return 0; }
      @Override protected <T extends ILanguageAspect> T createAspect(Class<T> type) {
        if (type == StructureAspectDescriptor.class) {
          SConceptId base = SNodeUtil.conceptId_BaseConcept;
          SConceptId named = MetaIdHelper.getConcept(SNodeUtil.concept_INamedConcept);
          return type.cast(new Structure(
              new ConceptDescriptorBuilder2(getNamespace(), "BaseConcept",
                  base.getLanguageId().getHighBits(), base.getLanguageId().getLowBits(), base.getIdValue()).class_(false, false).create(),
              new ConceptDescriptorBuilder2(getNamespace(), "INamedConcept",
                  named.getLanguageId().getHighBits(), named.getLanguageId().getLowBits(), named.getIdValue()).interface_()
                  .property("name", MetaIdHelper.getProperty(SNodeUtil.property_INamedConcept_name).getIdValue())
                  .type(PrimitiveTypeId.STRING).done().create()));
        }
        if (type == BehaviorAspectDescriptor.class) return type.cast(new Behaviors());
        if (type == ConstraintsAspectDescriptor.class) return type.cast(new Constraints());
        return null;
      }
    };
  }

  /**
   * Only the named-variable metadata needed by the real equation engine. This
   * does not deploy the typesystem language or BaseLanguage's Type hierarchy.
   */
  public static LanguageRuntime typeVariableLanguage() {
    return new LanguageRuntime() {
      @Override public String getNamespace() { return "jetbrains.mps.lang.typesystem"; }
      @Override public SLanguageId getId() { return MetaIdHelper.getConcept(SNodeUtil.concept_RuntimeTypeVariable).getLanguageId(); }
      @Override public int getVersion() { return 0; }
      @Override protected void fillExtendedLanguages(Collection<org.jetbrains.mps.openapi.language.SLanguage> languages) {
        languages.add(SNodeUtil.concept_INamedConcept.getLanguage());
      }
      @Override protected <T extends ILanguageAspect> T createAspect(Class<T> type) {
        if (type == StructureAspectDescriptor.class) {
          SConceptId variable = MetaIdHelper.getConcept(SNodeUtil.concept_RuntimeTypeVariable);
          SConceptId named = MetaIdHelper.getConcept(SNodeUtil.concept_INamedConcept);
          return type.cast(new Structure(new ConceptDescriptorBuilder2(getNamespace(), "RuntimeTypeVariable",
              variable.getLanguageId().getHighBits(), variable.getLanguageId().getLowBits(), variable.getIdValue()).class_(false, false)
              .parent(named.getLanguageId().getHighBits(), named.getLanguageId().getLowBits(), named.getIdValue())
              .property("pullUp", MetaIdHelper.getProperty(SNodeUtil.property_RuntimeTypeVariable_pullUp).getIdValue())
              .type(PrimitiveTypeId.BOOLEAN).done().create()));
        }
        if (type == BehaviorAspectDescriptor.class) return type.cast(new Behaviors());
        if (type == ConstraintsAspectDescriptor.class) return type.cast(new Constraints());
        return null;
      }
    };
  }

  private static final class Structure extends BaseStructureAspectDescriptor {
    private final List<ConceptDescriptor> descriptors;
    Structure(ConceptDescriptor... descriptors) { this.descriptors = Arrays.asList(descriptors); }
    @Override public ConceptDescriptor getDescriptor(SConceptId id) {
      for (ConceptDescriptor descriptor : descriptors) if (descriptor.getId().equals(id)) return descriptor;
      return null;
    }
    @Override public Collection<ConceptDescriptor> getDescriptors() { return descriptors; }
  }
  private static final class Behaviors extends BaseBehaviorAspectDescriptor {
    @Override public BHDescriptor getDescriptor(SAbstractConcept concept) { return new EmptyBHDescriptor(concept); }
  }
  private static final class Constraints extends BaseConstraintsAspectDescriptor {
    @Override public ConstraintsDescriptor getConstraints(SAbstractConcept concept, ConstraintsDescriptorInitContext context) {
      return new BaseConstraintsDescriptor(concept, context) {{
        if (concept.equals(LITERAL)) record(new BasePropertyConstraintsDescriptor(VALUE, this, false, false, true) {
          @Override public boolean validateValue(SNode node, Object value, CheckingNodeContext checkingContext) {
            constraintChecks++;
            return value == null || value instanceof String && ((String) value).length() <= 64;
          }
        });
      }};
    }
  }
  private static final class Editors extends EditorAspectDescriptorBase {
    @Override public Collection<ConceptEditor> getDeclaredEditors(SAbstractConcept concept) {
      if (concept.equals(LITERAL)) return Collections.singleton(new LiteralEditor());
      if (concept.equals(GROUP)) return Collections.singleton(new GroupEditor());
      if (concept.equals(REFERENCE)) return Collections.singleton(new ReferenceEditor());
      return Collections.emptyList();
    }
  }
  private static final class GroupEditor implements ConceptEditor {
    @Override public Collection<String> getContextHints() { return Collections.emptyList(); }
    @Override public EditorCell createInspectedCell(EditorContext context, SNode node) { return createEditorCell(context, node); }
    @Override public EditorCell createEditorCell(EditorContext context, SNode node) {
      EditorCell_Collection root = EditorCell_Collection.createVertical(context, node);
      root.setBig(true); root.setCellId("group");
      root.addEditorCell(new EditorCell_Constant(context, node, "group"));
      for (SNode child : node.getChildren(MEMBERS)) {
        root.addEditorCell(context.getEditorComponent().getUpdater().getCurrentUpdateSession().updateChildNodeCell(child));
      }
      return root;
    }
  }
  private static final class ReferenceEditor implements ConceptEditor {
    @Override public Collection<String> getContextHints() { return Collections.emptyList(); }
    @Override public EditorCell createInspectedCell(EditorContext context, SNode node) { return createEditorCell(context, node); }
    @Override public EditorCell createEditorCell(EditorContext context, SNode node) {
      EditorCell_Collection root = EditorCell_Collection.createHorizontal(context, node);
      root.setBig(true); root.setCellId("reference");
      root.addEditorCell(new EditorCell_Constant(context, node, "reference "));
      SNode target = node.getReferenceTarget(TARGET);
      EditorCell_Constant value = target == null ? new EditorCell_Constant(context, node, "<unresolved>")
          : context.getEditorComponent().getUpdater().getCurrentUpdateSession().updateReferencedNodeCell(
              () -> new EditorCell_Constant(context, node, target.getProperty(VALUE)), target, TARGET);
      value.setCellId("target"); value.setSRole(TARGET); value.setReferenceCell(true);
      root.addEditorCell(value);
      return root;
    }
  }
  private static final class LiteralEditor implements ConceptEditor {
    @Override public Collection<String> getContextHints() { return Collections.emptyList(); }
    @Override public EditorCell createInspectedCell(EditorContext context, SNode node) { return createEditorCell(context, node); }
    @Override public EditorCell createEditorCell(EditorContext context, SNode node) {
      editorBuilds++;
      EditorCell_Collection root = EditorCell_Collection.createHorizontal(context, node);
      root.setBig(true); root.setCellId("expression");
      EditorCell_Constant keyword = new EditorCell_Constant(context, node, "number ");
      keyword.setSelectable(false);
      root.addEditorCell(keyword);
      EditorCell_Property property = EditorCell_Property.create(context, new PropertyAccessor(node, VALUE, false, true), node);
      property.setCellId("value"); property.setEditable(true);
      property.setSubstituteInfo(NodeSubstituteInfoFilterDecorator.createSubstituteInfoWithPatternMatchingFilter(
          new AbstractNodeSubstituteInfo(context) {
            @Override protected List<SubstituteAction> createActions() {
              return Arrays.asList(new NumberCompletion(node, "10"), new NumberCompletion(node, "20"));
            }
          }, context.getRepository()));
      root.addEditorCell(property);
      return root;
    }
  }
  private static final class NumberCompletion extends AbstractSubstituteAction {
    private final String value;
    NumberCompletion(SNode node, String value) { super(node); this.value = value; }
    @Override public String getMatchingText(String pattern) { return value; }
    @Override public String getDescriptionText(String pattern) { return "Integer literal " + value; }
    @Override protected SNode doSubstitute(EditorContext context, String pattern) {
      getSourceNode().setProperty(VALUE, value);
      return getSourceNode();
    }
  }
  private static final class Types extends BaseHelginsDescriptor {
    Types() {
      myInferenceRules.add(new LiteralRule()); myInferenceRules.add(new GroupRule()); myInferenceRules.add(new ReferenceRule());
      myNonTypesystemRules.add(new AvoidThirteenRule());
    }
  }
  public static final class GroupRule extends AbstractInferenceRule_Runtime {
    @Override public SAbstractConcept getApplicableConcept() { return GROUP; }
    @Override public IsApplicableStatus isApplicableAndPattern(SNode node) { return new IsApplicableStatus(true, null); }
    @Override public void applyRule(SNode node, TypeCheckingContext context, IsApplicableStatus status) {
      groupInferenceRuns++;
      // A sequence has its last member's type. The real engine records both the
      // containment read and the equation dependency on the chosen member.
      SNode last = null;
      for (SNode child : node.getChildren(MEMBERS)) last = child;
      context.createEquation(context.typeOf(node), last == null ? new jetbrains.mps.smodel.SNode(INTEGER) : context.typeOf(last),
          new EquationInfo(node, "group result", null, null));
    }
  }
  public static final class ReferenceRule extends AbstractInferenceRule_Runtime {
    @Override public SAbstractConcept getApplicableConcept() { return REFERENCE; }
    @Override public IsApplicableStatus isApplicableAndPattern(SNode node) { return new IsApplicableStatus(true, null); }
    @Override public void applyRule(SNode node, TypeCheckingContext context, IsApplicableStatus status) {
      referenceInferenceRuns++;
      SNode target = node.getReferenceTarget(TARGET);
      context.createEquation(context.typeOf(node), target == null ? new jetbrains.mps.smodel.SNode(TEXT) : context.typeOf(target),
          new EquationInfo(node, "reference target type", null, null));
      if (target == null || !isInteger(target.getProperty(VALUE))) {
        context.reportTypeError(node, "Reference must target an integer literal", null, null, null, new ReferenceMessageTarget(TARGET));
      }
    }
  }
  public static final class LiteralRule extends AbstractInferenceRule_Runtime {
    @Override public SAbstractConcept getApplicableConcept() { return LITERAL; }
    @Override public IsApplicableStatus isApplicableAndPattern(SNode node) { return new IsApplicableStatus(true, null); }
    @Override public void applyRule(SNode node, TypeCheckingContext context, IsApplicableStatus status) {
      inferenceRuns++;
      boolean integer = isInteger(node.getProperty(VALUE));
      context.createEquation(context.typeOf(node), new jetbrains.mps.smodel.SNode(integer ? INTEGER : TEXT),
          new EquationInfo(node, "literal type", null, null));
      if (!integer) context.reportTypeError(node, "Expected an integer literal", null, null, null, new PropertyMessageTarget(VALUE));
    }
  }
  public static final class AvoidThirteenRule extends AbstractNonTypesystemRule_Runtime {
    @Override public SAbstractConcept getApplicableConcept() { return LITERAL; }
    @Override public IsApplicableStatus isApplicableAndPattern(SNode node) { return new IsApplicableStatus(true, null); }
    @Override public void applyRule(SNode node, TypeCheckingContext context, IsApplicableStatus status) {
      if ("13".equals(node.getProperty(VALUE))) {
        context.reportWarning(node, "Choose another integer for this fixture", null, null, null, new PropertyMessageTarget(VALUE));
      }
    }
  }
  static boolean isInteger(String text) {
    if (text == null || text.isEmpty()) return false;
    try { Long.parseLong(text); return true; } catch (NumberFormatException invalid) { return false; }
  }
  private static final class Intentions implements IntentionAspectDescriptor {
    private final IntentionFactory zero = new ZeroIntention();
    @Override public Collection<IntentionFactory> getIntentions(SAbstractConcept concept) {
      return concept.equals(LITERAL) ? Collections.singleton(zero) : Collections.emptyList();
    }
    @Override public Collection<IntentionFactory> getAllIntentions() { return Collections.singleton(zero); }
  }
  private static final class ZeroIntention implements IntentionFactory, IntentionExecutable {
    @Override public String getPresentation() { return "Replace with zero"; }
    @Override public String getPersistentStateKey() { return NAME + ".replaceWithZero"; }
    @Override public Kind getKind() { return Kind.NORMAL; }
    @Override public boolean isAvailableInChildNodes() { return false; }
    @Override public SNodeReference getIntentionNodeReference() { return null; }
    @Override public boolean isSurroundWith() { return false; }
    @Override public Collection<? extends IntentionExecutable> instances(SNode node, EditorContext context) {
      intentionQueries++;
      return Collections.singleton(this);
    }
    @Override public String getDescription(SNode node, EditorContext context) { return getPresentation(); }
    @Override public void execute(SNode node, EditorContext context) { node.setProperty(VALUE, "0"); }
    @Override public IntentionDescriptor getDescriptor() { return this; }
  }
}
