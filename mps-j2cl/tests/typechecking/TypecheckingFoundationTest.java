package mpsj2cl.typechecking;

import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Consumer;
import java.util.stream.Stream;
import jetbrains.mps.lang.typesystem.runtime.AbstractInferenceRule_Runtime;
import jetbrains.mps.lang.typesystem.runtime.BaseHelginsDescriptor;
import jetbrains.mps.lang.typesystem.runtime.IHelginsDescriptor;
import jetbrains.mps.lang.typesystem.runtime.IsApplicableStatus;
import jetbrains.mps.lang.typesystem.runtime.RuleSet;
import jetbrains.mps.lang.typesystem.runtime.SubtypingRule_Runtime;
import jetbrains.mps.languageScope.LanguageScopeFactory;
import jetbrains.mps.languageScope.LanguageScopeExecutor;
import jetbrains.mps.newTypesystem.context.TargetTypecheckingContext;
import jetbrains.mps.newTypesystem.context.IncrementalTypecheckingContext;
import jetbrains.mps.smodel.SNode;
import jetbrains.mps.smodel.adapter.BootstrapAdapterFactory;
import jetbrains.mps.smodel.adapter.ids.MetaIdFactory;
import jetbrains.mps.smodel.adapter.ids.SLanguageId;
import jetbrains.mps.smodel.language.LanguageRegistry;
import jetbrains.mps.smodel.language.LanguageRuntime;
import jetbrains.mps.smodel.runtime.ILanguageAspect;
import jetbrains.mps.typechecking.CacheState;
import jetbrains.mps.typechecking.TypecheckingSession;
import jetbrains.mps.typechecking.backend.TypecheckingBackend;
import jetbrains.mps.typechecking.backend.WorkbenchTypecheckingController;
import jetbrains.mps.typesystem.inference.EquationInfo;
import jetbrains.mps.typesystem.inference.RulesManager;
import jetbrains.mps.typesystem.inference.TypeCheckerHelper;
import jetbrains.mps.typesystem.inference.TypeCheckingContext;
import org.jetbrains.mps.openapi.language.SAbstractConcept;
import org.jetbrains.mps.openapi.language.SConcept;
import org.jetbrains.mps.openapi.language.SLanguage;
import org.jetbrains.mps.openapi.language.SProperty;

/**
 * Handwritten runtime-language fixture, not generated MPS output. Real MPS RulesManager,
 * inference/equation solver, subtyping and incremental state run unchanged.
 * Bootstrap meta-objects deliberately permit this detached-model foundation test.
 */
public final class TypecheckingFoundationTest {
  private static final String LANGUAGE = "mpsj2cl.fixture";
  private static final SConcept LITERAL = concept(1, "Literal");
  private static final SConcept INTEGER = concept(2, "IntegerType");
  private static final SConcept NUMBER = concept(3, "NumberType");
  private static final SConcept TEXT = concept(4, "TextType");
  private static final SProperty KIND = BootstrapAdapterFactory.getProperty(42, 84, 1, 5, "kind");
  private static int inferenceRuns;

  private static SConcept concept(long id, String name) {
    return BootstrapAdapterFactory.getConcept(42, 84, id, LANGUAGE + ".structure." + name);
  }

  public static String run() {
    jetbrains.mps.smodel.NodeIdentityComponent identities = new jetbrains.mps.smodel.NodeIdentityComponent();
    jetbrains.mps.extapi.persistence.ModelFactoryService modelFactories = new jetbrains.mps.extapi.persistence.ModelFactoryService();
    jetbrains.mps.extapi.persistence.datasource.DataSourceFactoryRuleService dataSources = new jetbrains.mps.extapi.persistence.datasource.DataSourceFactoryRuleService();
    jetbrains.mps.persistence.PersistenceRegistry persistence = new jetbrains.mps.persistence.PersistenceRegistry(modelFactories, dataSources);
    identities.init();
    modelFactories.init();
    dataSources.init();
    persistence.init();
    try {
      return runRuntime();
    } finally {
      persistence.dispose();
      dataSources.dispose();
      modelFactories.dispose();
      identities.dispose();
    }
  }

  private static String runRuntime() {
    RuleSet.registerRuleNamespace(LiteralRule.class, LANGUAGE);
    RuleSet.registerRuleNamespace(IntegerSubtype.class, LANGUAGE);
    FixtureLanguage language = new FixtureLanguage();
    LanguageRegistry registry = new LanguageRegistry(null, () -> null);
    registry.registerCompiledLanguage(language);
    LanguageScopeFactory scopeFactory = new LanguageScopeFactory(registry, null);
    RulesManager manager = new RulesManager();
    manager.loadLanguages(Collections.singleton(language));
    TypeCheckerHelper helper = new TypeCheckerHelper(manager, scopeFactory, null);
    SNode literal = new SNode(LITERAL);
    literal.setProperty(KIND, "integer");
    TargetTypecheckingContext inference = new TargetTypecheckingContext(literal, helper);
    try {
      org.jetbrains.mps.openapi.model.SNode inferred = inference.getTypeOf_resolveMode(literal, null);
      require(inferred != null && inferred.getConcept().equals(INTEGER), "real equation solver infers integer");
      require(inferenceRuns > 0, "real inference rule applied");
      require(helper.getSubtypingManager().isSubtype(new SNode(INTEGER), new SNode(NUMBER)), "integer is a number through subtyping rule");
      require(!helper.getSubtypingManager().isSubtype(new SNode(TEXT), new SNode(NUMBER)), "text is not a number");
    } finally {
      inference.dispose();
    }
    verifyLanguageRulePrecedence(language, registry, manager, helper, scopeFactory);

    IncrementalTypecheckingContext incremental = new IncrementalTypecheckingContext(literal, helper, null);
    try {
      org.jetbrains.mps.openapi.model.SNode first = incremental.getTypeOf_normalMode(literal);
      require(first != null && first.getConcept().equals(INTEGER), "incremental engine initial integer");
      int before = inferenceRuns;
      literal.setProperty(KIND, "text");
      // Detached roots have no repository events. Explicit invalidation uses the real
      // incremental engine; automatic repository listener wiring belongs to the live test.
      incremental.getBaseNodeTypesComponent().getTypecheckingComponent().addNodeToInvalidate(literal);
      incremental.getBaseNodeTypesComponent().computeTypes(false);
      org.jetbrains.mps.openapi.model.SNode second = incremental.getBaseNodeTypesComponent().getType(literal);
      require(second != null && second.getConcept().equals(TEXT), "invalidation changes inferred type");
      require(inferenceRuns > before, "invalidation reruns inference");
      incremental.getBaseNodeTypesComponent().getTypecheckingComponent().setChecked();
      CacheState state = incremental.getBaseNodeTypesComponent().getTypecheckingComponent().getCacheState();
      // entry.js freezes Date.now for this test to exercise same-clock-tick checks.
      require(state.hasChangedSince(Instant.now()), "same-clock-tick check is visible");
    } finally {
      incremental.dispose();
    }

    TypecheckingBackend backend = new TypecheckingBackend();
    WorkbenchTypecheckingController controller = new WorkbenchTypecheckingController(backend);
    TypecheckingSession.Handle first = controller.requestSession(TypecheckingSession.Flags.forRoot(literal).incremental());
    TypecheckingSession.Handle second = controller.requestSession(TypecheckingSession.Flags.forRoot(literal).incremental());
    require(first.session() == second.session(), "editor handles share root session");
    first.release();
    require(second.session() != null, "other editor retains session");
    second.release();
    try { second.session(); throw new AssertionError("released handle accepted"); } catch (IllegalStateException expected) {}
    TypecheckingSession.Handle pending = controller.requestSession(TypecheckingSession.Flags.forRoot(literal).incremental());
    pending.session();
    controller.dispose();
    controller.dispose();
    try { pending.session(); throw new AssertionError("disposed controller resurrected session"); } catch (IllegalStateException expected) {}
    pending.release();
    helper.dispose();
    manager.unloadLanguages(Collections.singleton(language));
    registry.unregisterCompiledLanguage(language);
    RuleSet.unregisterRuleNamespace(LiteralRule.class);
    RuleSet.unregisterRuleNamespace(IntegerSubtype.class);
    return "PASS";
  }

  private static void verifyLanguageRulePrecedence(LanguageRuntime base, LanguageRegistry registry,
      RulesManager manager, TypeCheckerHelper helper, LanguageScopeFactory scopes) {
    String derivedNamespace = LANGUAGE + ".derived";
    RuleSet.registerRuleNamespace(DerivedLiteralRule.class, derivedNamespace);
    LanguageRuntime derived = new LanguageRuntime() {
      @Override public String getNamespace() { return derivedNamespace; }
      @Override public SLanguageId getId() { return MetaIdFactory.langId(42, 85); }
      @Override public int getVersion() { return 0; }
      @Override protected void fillExtendedLanguages(Collection<SLanguage> languages) { languages.add(base.getIdentity()); }
      @Override protected <T extends ILanguageAspect> T createAspect(Class<T> type) {
        return type == IHelginsDescriptor.class ? type.cast(new BaseHelginsDescriptor() {{
          myInferenceRules.add(new DerivedLiteralRule());
        }}) : null;
      }
    };
    registry.registerCompiledLanguage(derived);
    manager.loadLanguages(Collections.singleton(derived));
    try {
      require(derived.getExtendedLanguages().contains(base), "derived typesystem language extends base");
      LanguageScopeExecutor.execWithMultiLanguageScope(Collections.singleton(derived.getIdentity()), () -> {
        SNode node = new SNode(LITERAL);
        node.setProperty(KIND, "integer");
        java.util.List<jetbrains.mps.util.Pair<jetbrains.mps.lang.typesystem.runtime.InferenceRule_Runtime, IsApplicableStatus>> rules = manager.getInferenceRules(node);
        require(rules.size() == 1 && rules.get(0).o1 instanceof DerivedLiteralRule,
            "derived language overriding rule precedes and suppresses base rule");
        TargetTypecheckingContext context = new TargetTypecheckingContext(node, helper);
        try {
          org.jetbrains.mps.openapi.model.SNode inferred = context.getTypeOf_resolveMode(node, null);
          require(inferred != null && inferred.getConcept().equals(TEXT), "actual derived inference changes the base literal type");
        } finally { context.dispose(); }
        return null;
      }, scopes);
      LanguageScopeExecutor.execWithMultiLanguageScope(Collections.singleton(base.getIdentity()), () -> {
        java.util.List<jetbrains.mps.util.Pair<jetbrains.mps.lang.typesystem.runtime.InferenceRule_Runtime, IsApplicableStatus>> rules = manager.getInferenceRules(new SNode(LITERAL));
        require(rules.size() == 1 && rules.get(0).o1 instanceof LiteralRule, "base language scope excludes derived rule");
        return null;
      }, scopes);
    } finally {
      manager.unloadLanguages(Collections.singleton(derived));
      registry.unregisterCompiledLanguage(derived);
      RuleSet.unregisterRuleNamespace(DerivedLiteralRule.class);
    }
    require(manager.getInferenceRules(new SNode(LITERAL)).get(0).o1 instanceof LiteralRule,
        "unloading derived language restores base inference rule");
  }

  private static final class DerivedLiteralRule extends AbstractInferenceRule_Runtime {
    @Override public SAbstractConcept getApplicableConcept() { return LITERAL; }
    @Override public IsApplicableStatus isApplicableAndPattern(org.jetbrains.mps.openapi.model.SNode node) { return new IsApplicableStatus(true, null); }
    @Override public boolean overrides() { return true; }
    @Override public void applyRule(org.jetbrains.mps.openapi.model.SNode node, TypeCheckingContext context, IsApplicableStatus status) {
      context.createEquation(context.typeOf(node), new SNode(TEXT), new EquationInfo(node, "derived literal type", null, null));
    }
  }

  private static final class LiteralRule extends AbstractInferenceRule_Runtime {
    @Override public SAbstractConcept getApplicableConcept() { return LITERAL; }
    @Override public IsApplicableStatus isApplicableAndPattern(org.jetbrains.mps.openapi.model.SNode node) { return new IsApplicableStatus(true, null); }
    @Override public void applyRule(org.jetbrains.mps.openapi.model.SNode node, TypeCheckingContext context, IsApplicableStatus status) {
      inferenceRuns++;
      SConcept result = "integer".equals(node.getProperty(KIND)) ? INTEGER : TEXT;
      context.createEquation(context.typeOf(node), new SNode(result), new EquationInfo(node, "literal type", null, null));
    }
  }

  private static final class IntegerSubtype extends SubtypingRule_Runtime {
    @Override public SAbstractConcept getApplicableConcept() { return INTEGER; }
    @Override public IsApplicableStatus isApplicableAndPattern(org.jetbrains.mps.openapi.model.SNode node) { return new IsApplicableStatus(true, null); }
    @Override public org.jetbrains.mps.openapi.model.SNode getSubOrSuperType(org.jetbrains.mps.openapi.model.SNode type, TypeCheckingContext context, IsApplicableStatus status) {
      return new SNode(NUMBER);
    }
  }

  private static final class FixtureTypesystem extends BaseHelginsDescriptor {
    FixtureTypesystem() { myInferenceRules.add(new LiteralRule()); mySubtypingRules.add(new IntegerSubtype()); }
  }

  private static final class FixtureLanguage extends LanguageRuntime {
    @Override public String getNamespace() { return LANGUAGE; }
    @Override public SLanguageId getId() { return MetaIdFactory.langId(42, 84); }
    @Override public int getVersion() { return 0; }
    @Override protected <T extends ILanguageAspect> T createAspect(Class<T> type) {
      return type == IHelginsDescriptor.class ? type.cast(new FixtureTypesystem()) : null;
    }
  }

  private static void require(boolean condition, String detail) { if (!condition) throw new AssertionError(detail); }
}
