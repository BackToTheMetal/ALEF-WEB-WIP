package mpsj2cl.financial;

// Generated from the selected, pinned language classes by tools/inventory_financial.py.
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.HashSet;
import java.util.Set;
import jetbrains.mps.classloading.ModuleClassLoader;
import jetbrains.mps.classloading.FinancialClassLoader;
import jetbrains.mps.core.platform.BrowserPlatform;
import jetbrains.mps.smodel.language.LanguageRuntime;
public final class FinancialLanguages {
  private final List<ModuleClassLoader> loaders = new ArrayList<>();
  private final Set<Class<?>> aspectRuleClasses = new HashSet<>();
  public FinancialLanguages(BrowserPlatform platform) {
    register0(platform);
    register1(platform);
    register2(platform);
    register3(platform);
    register4(platform);
    platform.registerLanguage(new RuntimeTypesLanguage());
  }
  public void verifyTypesystems(BrowserPlatform platform) {
    jetbrains.mps.lang.typesystem.runtime.IHelginsDescriptor aspect0 = platform.findComponent(jetbrains.mps.smodel.language.LanguageRegistry.class).getLanguage("jetbrains.mps.lang.core").getAspect(jetbrains.mps.lang.typesystem.runtime.IHelginsDescriptor.class);
    if (aspect0 == null)
      throw new IllegalStateException("Typesystem aspect failed to initialize: jetbrains.mps.lang.core");
    bindRules(aspect0.getInferenceRules(), "jetbrains.mps.lang.core");
    bindRules(aspect0.getNonTypesystemRules(), "jetbrains.mps.lang.core");
    bindRules(aspect0.getSubtypingRules(), "jetbrains.mps.lang.core");
    bindRules(aspect0.getSubstituteTypeRules(), "jetbrains.mps.lang.core");
    bindRules(aspect0.getComparisonRules(), "jetbrains.mps.lang.core");
    bindRules(aspect0.getEliminationRules(), "jetbrains.mps.lang.core");
    bindRules(aspect0.getVariableConverters(), "jetbrains.mps.lang.core");
    bindRules(aspect0.getOverloadedOperationsTypesProviders(), "jetbrains.mps.lang.core");
    jetbrains.mps.lang.typesystem.runtime.IHelginsDescriptor aspect3 = platform.findComponent(jetbrains.mps.smodel.language.LanguageRegistry.class).getLanguage("jetbrains.mps.baseLanguage").getAspect(jetbrains.mps.lang.typesystem.runtime.IHelginsDescriptor.class);
    if (aspect3 == null)
      throw new IllegalStateException("Typesystem aspect failed to initialize: jetbrains.mps.baseLanguage");
    bindRules(aspect3.getInferenceRules(), "jetbrains.mps.baseLanguage");
    bindRules(aspect3.getNonTypesystemRules(), "jetbrains.mps.baseLanguage");
    bindRules(aspect3.getSubtypingRules(), "jetbrains.mps.baseLanguage");
    bindRules(aspect3.getSubstituteTypeRules(), "jetbrains.mps.baseLanguage");
    bindRules(aspect3.getComparisonRules(), "jetbrains.mps.baseLanguage");
    bindRules(aspect3.getEliminationRules(), "jetbrains.mps.baseLanguage");
    bindRules(aspect3.getVariableConverters(), "jetbrains.mps.baseLanguage");
    bindRules(aspect3.getOverloadedOperationsTypesProviders(), "jetbrains.mps.baseLanguage");

  }
  private void register0(BrowserPlatform platform) {
    LanguageRuntime language = new jetbrains.mps.lang.core.Language();
    ModuleClassLoader loader = FinancialClassLoader.create(language.getIdentity().getSourceModuleReference());
    loaders.add(loader);
    loader.registerFactory("jetbrains.mps.lang.core.typesystem.DoNotSuppressError_QuickFix", jetbrains.mps.lang.core.typesystem.DoNotSuppressError_QuickFix.class, new Class<?>[0], args -> new jetbrains.mps.lang.core.typesystem.DoNotSuppressError_QuickFix());
    loader.registerFactory("jetbrains.mps.lang.core.typesystem.RemoveUndeclaredLinkAttribute_QuickFix", jetbrains.mps.lang.core.typesystem.RemoveUndeclaredLinkAttribute_QuickFix.class, new Class<?>[0], args -> new jetbrains.mps.lang.core.typesystem.RemoveUndeclaredLinkAttribute_QuickFix());
    loader.registerFactory("jetbrains.mps.lang.core.typesystem.RemoveUndeclaredPropertyAttribute_QuickFix", jetbrains.mps.lang.core.typesystem.RemoveUndeclaredPropertyAttribute_QuickFix.class, new Class<?>[0], args -> new jetbrains.mps.lang.core.typesystem.RemoveUndeclaredPropertyAttribute_QuickFix());
    loader.registerFactory("jetbrains.mps.lang.core.typesystem.RemoveUndeclaredProperty_QuickFix", jetbrains.mps.lang.core.typesystem.RemoveUndeclaredProperty_QuickFix.class, new Class<?>[0], args -> new jetbrains.mps.lang.core.typesystem.RemoveUndeclaredProperty_QuickFix());
    loader.registerFactory("jetbrains.mps.lang.core.typesystem.RemoveUnknownChildren_QuickFix", jetbrains.mps.lang.core.typesystem.RemoveUnknownChildren_QuickFix.class, new Class<?>[0], args -> new jetbrains.mps.lang.core.typesystem.RemoveUnknownChildren_QuickFix());
    loader.registerFactory("jetbrains.mps.lang.core.typesystem.RemoveUnknownReference_QuickFix", jetbrains.mps.lang.core.typesystem.RemoveUnknownReference_QuickFix.class, new Class<?>[0], args -> new jetbrains.mps.lang.core.typesystem.RemoveUnknownReference_QuickFix());
    loader.registerFactory("jetbrains.mps.lang.core.typesystem.add_ChildAttribute_id_QuickFix", jetbrains.mps.lang.core.typesystem.add_ChildAttribute_id_QuickFix.class, new Class<?>[0], args -> new jetbrains.mps.lang.core.typesystem.add_ChildAttribute_id_QuickFix());
    loader.registerFactory("jetbrains.mps.lang.core.typesystem.add_LinkAttribute_id_QuickFix", jetbrains.mps.lang.core.typesystem.add_LinkAttribute_id_QuickFix.class, new Class<?>[0], args -> new jetbrains.mps.lang.core.typesystem.add_LinkAttribute_id_QuickFix());
    loader.registerFactory("jetbrains.mps.lang.core.typesystem.add_PropertyAttribute_id_QuickFix", jetbrains.mps.lang.core.typesystem.add_PropertyAttribute_id_QuickFix.class, new Class<?>[0], args -> new jetbrains.mps.lang.core.typesystem.add_PropertyAttribute_id_QuickFix());
    loader.registerFactory("jetbrains.mps.lang.core.typesystem.fix_ChildAttribute_name_QuickFix", jetbrains.mps.lang.core.typesystem.fix_ChildAttribute_name_QuickFix.class, new Class<?>[0], args -> new jetbrains.mps.lang.core.typesystem.fix_ChildAttribute_name_QuickFix());
    loader.registerFactory("jetbrains.mps.lang.core.typesystem.fix_LinkAttribute_name_QuickFix", jetbrains.mps.lang.core.typesystem.fix_LinkAttribute_name_QuickFix.class, new Class<?>[0], args -> new jetbrains.mps.lang.core.typesystem.fix_LinkAttribute_name_QuickFix());
    loader.registerFactory("jetbrains.mps.lang.core.typesystem.fix_PropertyAttribute_name_QuickFix", jetbrains.mps.lang.core.typesystem.fix_PropertyAttribute_name_QuickFix.class, new Class<?>[0], args -> new jetbrains.mps.lang.core.typesystem.fix_PropertyAttribute_name_QuickFix());
    platform.registerLanguage(language, loader, Collections.emptyList(), Collections.emptyList(), new Class<?>[] {
      jetbrains.mps.lang.core.typesystem.DoNotSuppressError_QuickFix.class,
      jetbrains.mps.lang.core.typesystem.RemoveUndeclaredLinkAttribute_QuickFix.class,
      jetbrains.mps.lang.core.typesystem.RemoveUndeclaredPropertyAttribute_QuickFix.class,
      jetbrains.mps.lang.core.typesystem.RemoveUndeclaredProperty_QuickFix.class,
      jetbrains.mps.lang.core.typesystem.RemoveUnknownChildren_QuickFix.class,
      jetbrains.mps.lang.core.typesystem.RemoveUnknownReference_QuickFix.class,
      jetbrains.mps.lang.core.typesystem.TypesystemDescriptor.class,
      jetbrains.mps.lang.core.typesystem.add_ChildAttribute_id_QuickFix.class,
      jetbrains.mps.lang.core.typesystem.add_LinkAttribute_id_QuickFix.class,
      jetbrains.mps.lang.core.typesystem.add_PropertyAttribute_id_QuickFix.class,
      jetbrains.mps.lang.core.typesystem.check_ChildAttribute_NonTypesystemRule.class,
      jetbrains.mps.lang.core.typesystem.check_DeprecatedReference_NonTypesystemRule.class,
      jetbrains.mps.lang.core.typesystem.check_InstanceOfAbstract_NonTypesystemRule.class,
      jetbrains.mps.lang.core.typesystem.check_InstanceOfDeprecated_NonTypesystemRule.class,
      jetbrains.mps.lang.core.typesystem.check_InstanceOfExperimental_NonTypesystemRule.class,
      jetbrains.mps.lang.core.typesystem.check_LinkAttribute_NonTypesystemRule.class,
      jetbrains.mps.lang.core.typesystem.check_PropertyAttribute_NonTypesystemRule.class,
      jetbrains.mps.lang.core.typesystem.check_SuppressErrorsAnnotation_NonTypesystemRule.class,
      jetbrains.mps.lang.core.typesystem.fix_ChildAttribute_name_QuickFix.class,
      jetbrains.mps.lang.core.typesystem.fix_LinkAttribute_name_QuickFix.class,
      jetbrains.mps.lang.core.typesystem.fix_PropertyAttribute_name_QuickFix.class,
      jetbrains.mps.lang.core.typesystem.typeof_TypeAnnotated_InferenceRule.class
    });
  }
  private void register1(BrowserPlatform platform) {
    LanguageRuntime language = new jetbrains.mps.lang.traceable.Language();
    ModuleClassLoader loader = FinancialClassLoader.create(language.getIdentity().getSourceModuleReference());
    loaders.add(loader);
    platform.registerLanguage(language, loader, Collections.emptyList(), Collections.emptyList(), new Class<?>[] {

    });
  }
  private void register2(BrowserPlatform platform) {
    LanguageRuntime language = new jetbrains.mps.baseLanguage.blTypes.Language();
    ModuleClassLoader loader = FinancialClassLoader.create(language.getIdentity().getSourceModuleReference());
    loaders.add(loader);
    platform.registerLanguage(language, loader, Collections.emptyList(), Collections.emptyList(), new Class<?>[] {

    });
  }
  private void register3(BrowserPlatform platform) {
    LanguageRuntime language = new jetbrains.mps.baseLanguage.Language();
    ModuleClassLoader loader = FinancialClassLoader.create(language.getIdentity().getSourceModuleReference());
    loaders.add(loader);
    loader.registerFactory("jetbrains.mps.baseLanguage.typesystem.AddCast_QuickFix", jetbrains.mps.baseLanguage.typesystem.AddCast_QuickFix.class, new Class<?>[0], args -> new jetbrains.mps.baseLanguage.typesystem.AddCast_QuickFix());
    loader.registerFactory("jetbrains.mps.baseLanguage.typesystem.AddExceptionToMethodSignature_QuickFix", jetbrains.mps.baseLanguage.typesystem.AddExceptionToMethodSignature_QuickFix.class, new Class<?>[0], args -> new jetbrains.mps.baseLanguage.typesystem.AddExceptionToMethodSignature_QuickFix());
    loader.registerFactory("jetbrains.mps.baseLanguage.typesystem.AddImplicitConstructorToAnonymousClass_QuickFix", jetbrains.mps.baseLanguage.typesystem.AddImplicitConstructorToAnonymousClass_QuickFix.class, new Class<?>[0], args -> new jetbrains.mps.baseLanguage.typesystem.AddImplicitConstructorToAnonymousClass_QuickFix());
    loader.registerFactory("jetbrains.mps.baseLanguage.typesystem.AddMissingParensToNotExpression_QuickFix", jetbrains.mps.baseLanguage.typesystem.AddMissingParensToNotExpression_QuickFix.class, new Class<?>[0], args -> new jetbrains.mps.baseLanguage.typesystem.AddMissingParensToNotExpression_QuickFix());
    loader.registerFactory("jetbrains.mps.baseLanguage.typesystem.Add_NotNullAnnotation_QuickFix", jetbrains.mps.baseLanguage.typesystem.Add_NotNullAnnotation_QuickFix.class, new Class<?>[0], args -> new jetbrains.mps.baseLanguage.typesystem.Add_NotNullAnnotation_QuickFix());
    loader.registerFactory("jetbrains.mps.baseLanguage.typesystem.Add_NullableAnnotationToParameter_QuickFix", jetbrains.mps.baseLanguage.typesystem.Add_NullableAnnotationToParameter_QuickFix.class, new Class<?>[0], args -> new jetbrains.mps.baseLanguage.typesystem.Add_NullableAnnotationToParameter_QuickFix());
    loader.registerFactory("jetbrains.mps.baseLanguage.typesystem.BinaryExpressionPriority_QuickFix", jetbrains.mps.baseLanguage.typesystem.BinaryExpressionPriority_QuickFix.class, new Class<?>[0], args -> new jetbrains.mps.baseLanguage.typesystem.BinaryExpressionPriority_QuickFix());
    loader.registerFactory("jetbrains.mps.baseLanguage.typesystem.ChangeExpectedType_QuickFix", jetbrains.mps.baseLanguage.typesystem.ChangeExpectedType_QuickFix.class, new Class<?>[0], args -> new jetbrains.mps.baseLanguage.typesystem.ChangeExpectedType_QuickFix());
    loader.registerFactory("jetbrains.mps.baseLanguage.typesystem.ChangeMethodReturnType_QuickFix", jetbrains.mps.baseLanguage.typesystem.ChangeMethodReturnType_QuickFix.class, new Class<?>[0], args -> new jetbrains.mps.baseLanguage.typesystem.ChangeMethodReturnType_QuickFix());
    loader.registerFactory("jetbrains.mps.baseLanguage.typesystem.ChangeMethodSignatureFromCall_QuickFix", jetbrains.mps.baseLanguage.typesystem.ChangeMethodSignatureFromCall_QuickFix.class, new Class<?>[0], args -> new jetbrains.mps.baseLanguage.typesystem.ChangeMethodSignatureFromCall_QuickFix());
    loader.registerFactory("jetbrains.mps.baseLanguage.typesystem.ClearMethodBody_QuickFix", jetbrains.mps.baseLanguage.typesystem.ClearMethodBody_QuickFix.class, new Class<?>[0], args -> new jetbrains.mps.baseLanguage.typesystem.ClearMethodBody_QuickFix());
    loader.registerFactory("jetbrains.mps.baseLanguage.typesystem.ConvertFieldToLocalVariable_QuickFix", jetbrains.mps.baseLanguage.typesystem.ConvertFieldToLocalVariable_QuickFix.class, new Class<?>[0], args -> new jetbrains.mps.baseLanguage.typesystem.ConvertFieldToLocalVariable_QuickFix());
    loader.registerFactory("jetbrains.mps.baseLanguage.typesystem.ConvertString_QuickFix", jetbrains.mps.baseLanguage.typesystem.ConvertString_QuickFix.class, new Class<?>[0], args -> new jetbrains.mps.baseLanguage.typesystem.ConvertString_QuickFix());
    loader.registerFactory("jetbrains.mps.baseLanguage.typesystem.CreateDefaultConstructorInSuperClass_QuickFix", jetbrains.mps.baseLanguage.typesystem.CreateDefaultConstructorInSuperClass_QuickFix.class, new Class<?>[0], args -> new jetbrains.mps.baseLanguage.typesystem.CreateDefaultConstructorInSuperClass_QuickFix());
    loader.registerFactory("jetbrains.mps.baseLanguage.typesystem.CreateMethodFromCall_QuickFix", jetbrains.mps.baseLanguage.typesystem.CreateMethodFromCall_QuickFix.class, new Class<?>[0], args -> new jetbrains.mps.baseLanguage.typesystem.CreateMethodFromCall_QuickFix());
    loader.registerFactory("jetbrains.mps.baseLanguage.typesystem.FixArrayClassExpressionNotOnArrayType_QuickFix", jetbrains.mps.baseLanguage.typesystem.FixArrayClassExpressionNotOnArrayType_QuickFix.class, new Class<?>[0], args -> new jetbrains.mps.baseLanguage.typesystem.FixArrayClassExpressionNotOnArrayType_QuickFix());
    loader.registerFactory("jetbrains.mps.baseLanguage.typesystem.FixInstanceOfExpressionPrecedences_QuickFix", jetbrains.mps.baseLanguage.typesystem.FixInstanceOfExpressionPrecedences_QuickFix.class, new Class<?>[0], args -> new jetbrains.mps.baseLanguage.typesystem.FixInstanceOfExpressionPrecedences_QuickFix());
    loader.registerFactory("jetbrains.mps.baseLanguage.typesystem.FixParametersInAnonymousClass_QuickFix", jetbrains.mps.baseLanguage.typesystem.FixParametersInAnonymousClass_QuickFix.class, new Class<?>[0], args -> new jetbrains.mps.baseLanguage.typesystem.FixParametersInAnonymousClass_QuickFix());
    loader.registerFactory("jetbrains.mps.baseLanguage.typesystem.FixPrimitiveClassExpressionNotOnPrimitiveType_QuickFix", jetbrains.mps.baseLanguage.typesystem.FixPrimitiveClassExpressionNotOnPrimitiveType_QuickFix.class, new Class<?>[0], args -> new jetbrains.mps.baseLanguage.typesystem.FixPrimitiveClassExpressionNotOnPrimitiveType_QuickFix());
    loader.registerFactory("jetbrains.mps.baseLanguage.typesystem.InitializeVariable_QuickFix", jetbrains.mps.baseLanguage.typesystem.InitializeVariable_QuickFix.class, new Class<?>[0], args -> new jetbrains.mps.baseLanguage.typesystem.InitializeVariable_QuickFix());
    loader.registerFactory("jetbrains.mps.baseLanguage.typesystem.MakeClassAbstract_QuickFix", jetbrains.mps.baseLanguage.typesystem.MakeClassAbstract_QuickFix.class, new Class<?>[0], args -> new jetbrains.mps.baseLanguage.typesystem.MakeClassAbstract_QuickFix());
    loader.registerFactory("jetbrains.mps.baseLanguage.typesystem.MakeClassNotFinal_QuickFix", jetbrains.mps.baseLanguage.typesystem.MakeClassNotFinal_QuickFix.class, new Class<?>[0], args -> new jetbrains.mps.baseLanguage.typesystem.MakeClassNotFinal_QuickFix());
    loader.registerFactory("jetbrains.mps.baseLanguage.typesystem.MakeInnerClassStatic_QuickFix", jetbrains.mps.baseLanguage.typesystem.MakeInnerClassStatic_QuickFix.class, new Class<?>[0], args -> new jetbrains.mps.baseLanguage.typesystem.MakeInnerClassStatic_QuickFix());
    loader.registerFactory("jetbrains.mps.baseLanguage.typesystem.MakeMethodNotFinal_QuickFix", jetbrains.mps.baseLanguage.typesystem.MakeMethodNotFinal_QuickFix.class, new Class<?>[0], args -> new jetbrains.mps.baseLanguage.typesystem.MakeMethodNotFinal_QuickFix());
    loader.registerFactory("jetbrains.mps.baseLanguage.typesystem.MakeMethodPublic_QuickFix", jetbrains.mps.baseLanguage.typesystem.MakeMethodPublic_QuickFix.class, new Class<?>[0], args -> new jetbrains.mps.baseLanguage.typesystem.MakeMethodPublic_QuickFix());
    loader.registerFactory("jetbrains.mps.baseLanguage.typesystem.MakeStaticCall_QuickFix", jetbrains.mps.baseLanguage.typesystem.MakeStaticCall_QuickFix.class, new Class<?>[0], args -> new jetbrains.mps.baseLanguage.typesystem.MakeStaticCall_QuickFix());
    loader.registerFactory("jetbrains.mps.baseLanguage.typesystem.MakeStaticFieldNotStatic_QuickFix", jetbrains.mps.baseLanguage.typesystem.MakeStaticFieldNotStatic_QuickFix.class, new Class<?>[0], args -> new jetbrains.mps.baseLanguage.typesystem.MakeStaticFieldNotStatic_QuickFix());
    loader.registerFactory("jetbrains.mps.baseLanguage.typesystem.MakeStaticInitializerNotStatic_QuickFix", jetbrains.mps.baseLanguage.typesystem.MakeStaticInitializerNotStatic_QuickFix.class, new Class<?>[0], args -> new jetbrains.mps.baseLanguage.typesystem.MakeStaticInitializerNotStatic_QuickFix());
    loader.registerFactory("jetbrains.mps.baseLanguage.typesystem.MakeStaticMethodNotStatic_QuickFix", jetbrains.mps.baseLanguage.typesystem.MakeStaticMethodNotStatic_QuickFix.class, new Class<?>[0], args -> new jetbrains.mps.baseLanguage.typesystem.MakeStaticMethodNotStatic_QuickFix());
    loader.registerFactory("jetbrains.mps.baseLanguage.typesystem.MakeVariableFinal_QuickFix", jetbrains.mps.baseLanguage.typesystem.MakeVariableFinal_QuickFix.class, new Class<?>[0], args -> new jetbrains.mps.baseLanguage.typesystem.MakeVariableFinal_QuickFix());
    loader.registerFactory("jetbrains.mps.baseLanguage.typesystem.MatchVisibility_QuickFix", jetbrains.mps.baseLanguage.typesystem.MatchVisibility_QuickFix.class, new Class<?>[0], args -> new jetbrains.mps.baseLanguage.typesystem.MatchVisibility_QuickFix());
    loader.registerFactory("jetbrains.mps.baseLanguage.typesystem.MethodCallsFixer_Quickfix_QuickFix", jetbrains.mps.baseLanguage.typesystem.MethodCallsFixer_Quickfix_QuickFix.class, new Class<?>[0], args -> new jetbrains.mps.baseLanguage.typesystem.MethodCallsFixer_Quickfix_QuickFix());
    loader.registerFactory("jetbrains.mps.baseLanguage.typesystem.QuickFixForClassCreator_QuickFix", jetbrains.mps.baseLanguage.typesystem.QuickFixForClassCreator_QuickFix.class, new Class<?>[0], args -> new jetbrains.mps.baseLanguage.typesystem.QuickFixForClassCreator_QuickFix());
    loader.registerFactory("jetbrains.mps.baseLanguage.typesystem.QuickFixForDefaultClassCreator_QuickFix", jetbrains.mps.baseLanguage.typesystem.QuickFixForDefaultClassCreator_QuickFix.class, new Class<?>[0], args -> new jetbrains.mps.baseLanguage.typesystem.QuickFixForDefaultClassCreator_QuickFix());
    loader.registerFactory("jetbrains.mps.baseLanguage.typesystem.RebalanceBaseAssignmentExpression_QuickFix", jetbrains.mps.baseLanguage.typesystem.RebalanceBaseAssignmentExpression_QuickFix.class, new Class<?>[0], args -> new jetbrains.mps.baseLanguage.typesystem.RebalanceBaseAssignmentExpression_QuickFix());
    loader.registerFactory("jetbrains.mps.baseLanguage.typesystem.RemoveAbstractClassModifier_QuickFix", jetbrains.mps.baseLanguage.typesystem.RemoveAbstractClassModifier_QuickFix.class, new Class<?>[0], args -> new jetbrains.mps.baseLanguage.typesystem.RemoveAbstractClassModifier_QuickFix());
    loader.registerFactory("jetbrains.mps.baseLanguage.typesystem.RemoveAbstractMethodModifier_QuickFix", jetbrains.mps.baseLanguage.typesystem.RemoveAbstractMethodModifier_QuickFix.class, new Class<?>[0], args -> new jetbrains.mps.baseLanguage.typesystem.RemoveAbstractMethodModifier_QuickFix());
    loader.registerFactory("jetbrains.mps.baseLanguage.typesystem.RemoveAllJavaImports_QuickFix", jetbrains.mps.baseLanguage.typesystem.RemoveAllJavaImports_QuickFix.class, new Class<?>[0], args -> new jetbrains.mps.baseLanguage.typesystem.RemoveAllJavaImports_QuickFix());
    loader.registerFactory("jetbrains.mps.baseLanguage.typesystem.RemoveDefaultEnumConstructor_QuickFix", jetbrains.mps.baseLanguage.typesystem.RemoveDefaultEnumConstructor_QuickFix.class, new Class<?>[0], args -> new jetbrains.mps.baseLanguage.typesystem.RemoveDefaultEnumConstructor_QuickFix());
    loader.registerFactory("jetbrains.mps.baseLanguage.typesystem.RemoveDefaultModifier_QuickFix", jetbrains.mps.baseLanguage.typesystem.RemoveDefaultModifier_QuickFix.class, new Class<?>[0], args -> new jetbrains.mps.baseLanguage.typesystem.RemoveDefaultModifier_QuickFix());
    loader.registerFactory("jetbrains.mps.baseLanguage.typesystem.RemoveExcessTypeCast_QuickFix", jetbrains.mps.baseLanguage.typesystem.RemoveExcessTypeCast_QuickFix.class, new Class<?>[0], args -> new jetbrains.mps.baseLanguage.typesystem.RemoveExcessTypeCast_QuickFix());
    loader.registerFactory("jetbrains.mps.baseLanguage.typesystem.RemoveFinalClassModifier_QuickFix", jetbrains.mps.baseLanguage.typesystem.RemoveFinalClassModifier_QuickFix.class, new Class<?>[0], args -> new jetbrains.mps.baseLanguage.typesystem.RemoveFinalClassModifier_QuickFix());
    loader.registerFactory("jetbrains.mps.baseLanguage.typesystem.RemoveGivenJavaImports_QuickFix", jetbrains.mps.baseLanguage.typesystem.RemoveGivenJavaImports_QuickFix.class, new Class<?>[0], args -> new jetbrains.mps.baseLanguage.typesystem.RemoveGivenJavaImports_QuickFix());
    loader.registerFactory("jetbrains.mps.baseLanguage.typesystem.RemoveImplicitConstructorFromAnonymousClass_QuickFix", jetbrains.mps.baseLanguage.typesystem.RemoveImplicitConstructorFromAnonymousClass_QuickFix.class, new Class<?>[0], args -> new jetbrains.mps.baseLanguage.typesystem.RemoveImplicitConstructorFromAnonymousClass_QuickFix());
    loader.registerFactory("jetbrains.mps.baseLanguage.typesystem.RemoveInvisibleDeprecation_QuickFix", jetbrains.mps.baseLanguage.typesystem.RemoveInvisibleDeprecation_QuickFix.class, new Class<?>[0], args -> new jetbrains.mps.baseLanguage.typesystem.RemoveInvisibleDeprecation_QuickFix());
    loader.registerFactory("jetbrains.mps.baseLanguage.typesystem.RemoveProtected_QuickFix", jetbrains.mps.baseLanguage.typesystem.RemoveProtected_QuickFix.class, new Class<?>[0], args -> new jetbrains.mps.baseLanguage.typesystem.RemoveProtected_QuickFix());
    loader.registerFactory("jetbrains.mps.baseLanguage.typesystem.RemoveRedundantExtends_QuickFix", jetbrains.mps.baseLanguage.typesystem.RemoveRedundantExtends_QuickFix.class, new Class<?>[0], args -> new jetbrains.mps.baseLanguage.typesystem.RemoveRedundantExtends_QuickFix());
    loader.registerFactory("jetbrains.mps.baseLanguage.typesystem.RemoveUnnecessaryParentheses_QuickFix", jetbrains.mps.baseLanguage.typesystem.RemoveUnnecessaryParentheses_QuickFix.class, new Class<?>[0], args -> new jetbrains.mps.baseLanguage.typesystem.RemoveUnnecessaryParentheses_QuickFix());
    loader.registerFactory("jetbrains.mps.baseLanguage.typesystem.RemoveUnusedAssignment_QuickFix", jetbrains.mps.baseLanguage.typesystem.RemoveUnusedAssignment_QuickFix.class, new Class<?>[0], args -> new jetbrains.mps.baseLanguage.typesystem.RemoveUnusedAssignment_QuickFix());
    loader.registerFactory("jetbrains.mps.baseLanguage.typesystem.RemoveUnusedField_QuickFix", jetbrains.mps.baseLanguage.typesystem.RemoveUnusedField_QuickFix.class, new Class<?>[0], args -> new jetbrains.mps.baseLanguage.typesystem.RemoveUnusedField_QuickFix());
    loader.registerFactory("jetbrains.mps.baseLanguage.typesystem.RemoveUnusedLocalVariable_QuickFix", jetbrains.mps.baseLanguage.typesystem.RemoveUnusedLocalVariable_QuickFix.class, new Class<?>[0], args -> new jetbrains.mps.baseLanguage.typesystem.RemoveUnusedLocalVariable_QuickFix());
    loader.registerFactory("jetbrains.mps.baseLanguage.typesystem.RemoveUnusedMethod_QuickFix", jetbrains.mps.baseLanguage.typesystem.RemoveUnusedMethod_QuickFix.class, new Class<?>[0], args -> new jetbrains.mps.baseLanguage.typesystem.RemoveUnusedMethod_QuickFix());
    loader.registerFactory("jetbrains.mps.baseLanguage.typesystem.RemoveUnusedPrivateClassifier_QuickFix", jetbrains.mps.baseLanguage.typesystem.RemoveUnusedPrivateClassifier_QuickFix.class, new Class<?>[0], args -> new jetbrains.mps.baseLanguage.typesystem.RemoveUnusedPrivateClassifier_QuickFix());
    loader.registerFactory("jetbrains.mps.baseLanguage.typesystem.RemoveUnusedStaticField_QuickFix", jetbrains.mps.baseLanguage.typesystem.RemoveUnusedStaticField_QuickFix.class, new Class<?>[0], args -> new jetbrains.mps.baseLanguage.typesystem.RemoveUnusedStaticField_QuickFix());
    loader.registerFactory("jetbrains.mps.baseLanguage.typesystem.ReplaceAlwaysTrueFalseWithLiteral_QuickFix", jetbrains.mps.baseLanguage.typesystem.ReplaceAlwaysTrueFalseWithLiteral_QuickFix.class, new Class<?>[0], args -> new jetbrains.mps.baseLanguage.typesystem.ReplaceAlwaysTrueFalseWithLiteral_QuickFix());
    loader.registerFactory("jetbrains.mps.baseLanguage.typesystem.ResolvedUnknownNode_QuickFix", jetbrains.mps.baseLanguage.typesystem.ResolvedUnknownNode_QuickFix.class, new Class<?>[0], args -> new jetbrains.mps.baseLanguage.typesystem.ResolvedUnknownNode_QuickFix());
    loader.registerFactory("jetbrains.mps.baseLanguage.typesystem.SetConstructorDeclaration_QuickFix", jetbrains.mps.baseLanguage.typesystem.SetConstructorDeclaration_QuickFix.class, new Class<?>[0], args -> new jetbrains.mps.baseLanguage.typesystem.SetConstructorDeclaration_QuickFix());
    loader.registerFactory("jetbrains.mps.baseLanguage.typesystem.SetConstructorToObject_QuickFix", jetbrains.mps.baseLanguage.typesystem.SetConstructorToObject_QuickFix.class, new Class<?>[0], args -> new jetbrains.mps.baseLanguage.typesystem.SetConstructorToObject_QuickFix());
    loader.registerFactory("jetbrains.mps.baseLanguage.typesystem.SetDefaultEnumConstructurToEnumConstant_QuickFix", jetbrains.mps.baseLanguage.typesystem.SetDefaultEnumConstructurToEnumConstant_QuickFix.class, new Class<?>[0], args -> new jetbrains.mps.baseLanguage.typesystem.SetDefaultEnumConstructurToEnumConstant_QuickFix());
    loader.registerFactory("jetbrains.mps.baseLanguage.typesystem.SetExplicitEnumConstructurToEnumConstant_QuickFix", jetbrains.mps.baseLanguage.typesystem.SetExplicitEnumConstructurToEnumConstant_QuickFix.class, new Class<?>[0], args -> new jetbrains.mps.baseLanguage.typesystem.SetExplicitEnumConstructurToEnumConstant_QuickFix());
    loader.registerFactory("jetbrains.mps.baseLanguage.typesystem.SimplifyBinaryLogicalExpressionWithBooleanConstant_QuickFix", jetbrains.mps.baseLanguage.typesystem.SimplifyBinaryLogicalExpressionWithBooleanConstant_QuickFix.class, new Class<?>[0], args -> new jetbrains.mps.baseLanguage.typesystem.SimplifyBinaryLogicalExpressionWithBooleanConstant_QuickFix());
    loader.registerFactory("jetbrains.mps.baseLanguage.typesystem.SimplifyCondition_QuickFix", jetbrains.mps.baseLanguage.typesystem.SimplifyCondition_QuickFix.class, new Class<?>[0], args -> new jetbrains.mps.baseLanguage.typesystem.SimplifyCondition_QuickFix());
    loader.registerFactory("jetbrains.mps.baseLanguage.typesystem.SimplifyNotExpression_QuickFix", jetbrains.mps.baseLanguage.typesystem.SimplifyNotExpression_QuickFix.class, new Class<?>[0], args -> new jetbrains.mps.baseLanguage.typesystem.SimplifyNotExpression_QuickFix());
    loader.registerFactory("jetbrains.mps.baseLanguage.typesystem.SimplifyWholeBinaryLogicalExpressionWithBooleanConstant_QuickFix", jetbrains.mps.baseLanguage.typesystem.SimplifyWholeBinaryLogicalExpressionWithBooleanConstant_QuickFix.class, new Class<?>[0], args -> new jetbrains.mps.baseLanguage.typesystem.SimplifyWholeBinaryLogicalExpressionWithBooleanConstant_QuickFix());
    loader.registerFactory("jetbrains.mps.baseLanguage.typesystem.SurroundExceptionWithTryCatch_QuickFix", jetbrains.mps.baseLanguage.typesystem.SurroundExceptionWithTryCatch_QuickFix.class, new Class<?>[0], args -> new jetbrains.mps.baseLanguage.typesystem.SurroundExceptionWithTryCatch_QuickFix());
    loader.registerFactory("jetbrains.mps.baseLanguage.typesystem.TurnAbstractClassifierOperandOfDotExprToStaticFieldReference_QuickFix", jetbrains.mps.baseLanguage.typesystem.TurnAbstractClassifierOperandOfDotExprToStaticFieldReference_QuickFix.class, new Class<?>[0], args -> new jetbrains.mps.baseLanguage.typesystem.TurnAbstractClassifierOperandOfDotExprToStaticFieldReference_QuickFix());
    loader.registerFactory("jetbrains.mps.baseLanguage.typesystem.TurnAbstractClassifierOperandOfDotExprToStaticMethodCall_QuickFix", jetbrains.mps.baseLanguage.typesystem.TurnAbstractClassifierOperandOfDotExprToStaticMethodCall_QuickFix.class, new Class<?>[0], args -> new jetbrains.mps.baseLanguage.typesystem.TurnAbstractClassifierOperandOfDotExprToStaticMethodCall_QuickFix());
    loader.registerFactory("jetbrains.mps.baseLanguage.typesystem.TurnInstanceFieldReferenceOperationToStaticFieldReferenceOperation_QuickFix", jetbrains.mps.baseLanguage.typesystem.TurnInstanceFieldReferenceOperationToStaticFieldReferenceOperation_QuickFix.class, new Class<?>[0], args -> new jetbrains.mps.baseLanguage.typesystem.TurnInstanceFieldReferenceOperationToStaticFieldReferenceOperation_QuickFix());
    loader.registerFactory("jetbrains.mps.baseLanguage.typesystem.TurnInstanceMethodCallOperationToStaticMethodCall_QuickFix", jetbrains.mps.baseLanguage.typesystem.TurnInstanceMethodCallOperationToStaticMethodCall_QuickFix.class, new Class<?>[0], args -> new jetbrains.mps.baseLanguage.typesystem.TurnInstanceMethodCallOperationToStaticMethodCall_QuickFix());
    loader.registerFactory("jetbrains.mps.baseLanguage.typesystem.TurnStaticFieldReferenceOperationFieldReferenceOperation_QuickFix", jetbrains.mps.baseLanguage.typesystem.TurnStaticFieldReferenceOperationFieldReferenceOperation_QuickFix.class, new Class<?>[0], args -> new jetbrains.mps.baseLanguage.typesystem.TurnStaticFieldReferenceOperationFieldReferenceOperation_QuickFix());
    loader.registerFactory("jetbrains.mps.baseLanguage.typesystem.TurnStaticMethodCallOperationToInstanceMethodCall_QuickFix", jetbrains.mps.baseLanguage.typesystem.TurnStaticMethodCallOperationToInstanceMethodCall_QuickFix.class, new Class<?>[0], args -> new jetbrains.mps.baseLanguage.typesystem.TurnStaticMethodCallOperationToInstanceMethodCall_QuickFix());
    loader.registerFactory("jetbrains.mps.baseLanguage.typesystem.makeReferenceStatic_QuickFix", jetbrains.mps.baseLanguage.typesystem.makeReferenceStatic_QuickFix.class, new Class<?>[0], args -> new jetbrains.mps.baseLanguage.typesystem.makeReferenceStatic_QuickFix());
    loader.registerFactory("jetbrains.mps.baseLanguage.typesystem.rebalanceCastExpression_QuickFix", jetbrains.mps.baseLanguage.typesystem.rebalanceCastExpression_QuickFix.class, new Class<?>[0], args -> new jetbrains.mps.baseLanguage.typesystem.rebalanceCastExpression_QuickFix());
    loader.registerFactory("jetbrains.mps.baseLanguage.typesystem.replaceNode_QuickFix", jetbrains.mps.baseLanguage.typesystem.replaceNode_QuickFix.class, new Class<?>[0], args -> new jetbrains.mps.baseLanguage.typesystem.replaceNode_QuickFix());
    loader.registerFactory("jetbrains.mps.baseLanguage.typesystem.wrapCastExpressionInParentheses_QuickFix", jetbrains.mps.baseLanguage.typesystem.wrapCastExpressionInParentheses_QuickFix.class, new Class<?>[0], args -> new jetbrains.mps.baseLanguage.typesystem.wrapCastExpressionInParentheses_QuickFix());
    loader.registerFactory("jetbrains.mps.baseLanguage.typesystem.wrapMisplacedTernaryOperatorInParens_QuickFix", jetbrains.mps.baseLanguage.typesystem.wrapMisplacedTernaryOperatorInParens_QuickFix.class, new Class<?>[0], args -> new jetbrains.mps.baseLanguage.typesystem.wrapMisplacedTernaryOperatorInParens_QuickFix());
    platform.registerLanguage(language, loader, Collections.emptyList(), Collections.emptyList(), new Class<?>[] {
      jetbrains.mps.baseLanguage.typesystem.AddCast_QuickFix.class,
      jetbrains.mps.baseLanguage.typesystem.AddExceptionToMethodSignature_QuickFix.class,
      jetbrains.mps.baseLanguage.typesystem.AddImplicitConstructorToAnonymousClass_QuickFix.class,
      jetbrains.mps.baseLanguage.typesystem.AddMissingParensToNotExpression_QuickFix.class,
      jetbrains.mps.baseLanguage.typesystem.Add_NotNullAnnotation_QuickFix.class,
      jetbrains.mps.baseLanguage.typesystem.Add_NullableAnnotationToParameter_QuickFix.class,
      jetbrains.mps.baseLanguage.typesystem.Arrays_InequationReplacementRule.class,
      jetbrains.mps.baseLanguage.typesystem.BinaryExpressionPriority_QuickFix.class,
      jetbrains.mps.baseLanguage.typesystem.Boolean_extends_boolean_2_SubtypingRule.class,
      jetbrains.mps.baseLanguage.typesystem.Boolean_extends_primitive_SubtypingRule.class,
      jetbrains.mps.baseLanguage.typesystem.ChangeExpectedType_QuickFix.class,
      jetbrains.mps.baseLanguage.typesystem.ChangeMethodReturnType_QuickFix.class,
      jetbrains.mps.baseLanguage.typesystem.ChangeMethodSignatureFromCall_QuickFix.class,
      jetbrains.mps.baseLanguage.typesystem.CheckBinaryOperationPriority_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.CheckExcessTypeCasts_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.CheckVariableDoubling_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.CheckingUtil.class,
      jetbrains.mps.baseLanguage.typesystem.Class_Void_weak_supertypeof_void_SubtypingRule.class,
      jetbrains.mps.baseLanguage.typesystem.ClassifierTypeDescriptor.class,
      jetbrains.mps.baseLanguage.typesystem.ClassifierType_NOT_subtypeOf_ClassifierType_InequationReplacementRule.class,
      jetbrains.mps.baseLanguage.typesystem.ClassifierType_comparableTo_raw_ClassifierType_ComparisonRule.class,
      jetbrains.mps.baseLanguage.typesystem.ClassifierTypingUtil.class,
      jetbrains.mps.baseLanguage.typesystem.ClearMethodBody_QuickFix.class,
      jetbrains.mps.baseLanguage.typesystem.ConvertFieldToLocalVariable_QuickFix.class,
      jetbrains.mps.baseLanguage.typesystem.ConvertString_QuickFix.class,
      jetbrains.mps.baseLanguage.typesystem.CreateDefaultConstructorInSuperClass_QuickFix.class,
      jetbrains.mps.baseLanguage.typesystem.CreateMethodFromCall_QuickFix.class,
      jetbrains.mps.baseLanguage.typesystem.DataFlowUtil.class,
      jetbrains.mps.baseLanguage.typesystem.EnumsAreEnum_SubtypingRule.class,
      jetbrains.mps.baseLanguage.typesystem.ExpressionPresentationUtil.class,
      jetbrains.mps.baseLanguage.typesystem.FixArrayClassExpressionNotOnArrayType_QuickFix.class,
      jetbrains.mps.baseLanguage.typesystem.FixInstanceOfExpressionPrecedences_QuickFix.class,
      jetbrains.mps.baseLanguage.typesystem.FixParametersInAnonymousClass_QuickFix.class,
      jetbrains.mps.baseLanguage.typesystem.FixPrimitiveClassExpressionNotOnPrimitiveType_QuickFix.class,
      jetbrains.mps.baseLanguage.typesystem.InferenceHelper.class,
      jetbrains.mps.baseLanguage.typesystem.InferredClassifierType_subtypeOf_ClassifierType_InequationReplacementRule.class,
      jetbrains.mps.baseLanguage.typesystem.InheritanceCheckingCallback.class,
      jetbrains.mps.baseLanguage.typesystem.InitializeVariable_QuickFix.class,
      jetbrains.mps.baseLanguage.typesystem.InstanceMethod.class,
      jetbrains.mps.baseLanguage.typesystem.Interface_is_comparable_to_ClassifierType_ComparisonRule.class,
      jetbrains.mps.baseLanguage.typesystem.LowerBoundType_subtypeOf_bound_InequationReplacementRule.class,
      jetbrains.mps.baseLanguage.typesystem.MakeClassAbstract_QuickFix.class,
      jetbrains.mps.baseLanguage.typesystem.MakeClassNotFinal_QuickFix.class,
      jetbrains.mps.baseLanguage.typesystem.MakeInnerClassStatic_QuickFix.class,
      jetbrains.mps.baseLanguage.typesystem.MakeMethodNotFinal_QuickFix.class,
      jetbrains.mps.baseLanguage.typesystem.MakeMethodPublic_QuickFix.class,
      jetbrains.mps.baseLanguage.typesystem.MakeStaticCall_QuickFix.class,
      jetbrains.mps.baseLanguage.typesystem.MakeStaticFieldNotStatic_QuickFix.class,
      jetbrains.mps.baseLanguage.typesystem.MakeStaticInitializerNotStatic_QuickFix.class,
      jetbrains.mps.baseLanguage.typesystem.MakeStaticMethodNotStatic_QuickFix.class,
      jetbrains.mps.baseLanguage.typesystem.MakeVariableFinal_QuickFix.class,
      jetbrains.mps.baseLanguage.typesystem.MatchVisibility_QuickFix.class,
      jetbrains.mps.baseLanguage.typesystem.MethodCallsFixer_Quickfix_QuickFix.class,
      jetbrains.mps.baseLanguage.typesystem.MethodCallsFixer_Rule_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.MethodParameterMatcher.class,
      jetbrains.mps.baseLanguage.typesystem.NonFinalParametersInAnonymousClass_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.NonFinalVariablesInAnonymousClass_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.ObjectArraySupertypes_SubtypingRule.class,
      jetbrains.mps.baseLanguage.typesystem.ParameterNameUtil.class,
      jetbrains.mps.baseLanguage.typesystem.PrimitiveArraySupertypes_SubtypingRule.class,
      jetbrains.mps.baseLanguage.typesystem.PrimitiveSubtyping_SubtypingRule.class,
      jetbrains.mps.baseLanguage.typesystem.PrimitiveTypesInInstanceOfNotAllowed_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.QuickFixForClassCreator_QuickFix.class,
      jetbrains.mps.baseLanguage.typesystem.QuickFixForDefaultClassCreator_QuickFix.class,
      jetbrains.mps.baseLanguage.typesystem.RebalanceBaseAssignmentExpression_QuickFix.class,
      jetbrains.mps.baseLanguage.typesystem.RemoveAbstractClassModifier_QuickFix.class,
      jetbrains.mps.baseLanguage.typesystem.RemoveAbstractMethodModifier_QuickFix.class,
      jetbrains.mps.baseLanguage.typesystem.RemoveAllJavaImports_QuickFix.class,
      jetbrains.mps.baseLanguage.typesystem.RemoveDefaultEnumConstructor_QuickFix.class,
      jetbrains.mps.baseLanguage.typesystem.RemoveDefaultModifier_QuickFix.class,
      jetbrains.mps.baseLanguage.typesystem.RemoveExcessTypeCast_QuickFix.class,
      jetbrains.mps.baseLanguage.typesystem.RemoveFinalClassModifier_QuickFix.class,
      jetbrains.mps.baseLanguage.typesystem.RemoveGivenJavaImports_QuickFix.class,
      jetbrains.mps.baseLanguage.typesystem.RemoveImplicitConstructorFromAnonymousClass_QuickFix.class,
      jetbrains.mps.baseLanguage.typesystem.RemoveInvisibleDeprecation_QuickFix.class,
      jetbrains.mps.baseLanguage.typesystem.RemoveProtected_QuickFix.class,
      jetbrains.mps.baseLanguage.typesystem.RemoveRedundantExtends_QuickFix.class,
      jetbrains.mps.baseLanguage.typesystem.RemoveUnnecessaryParentheses_QuickFix.class,
      jetbrains.mps.baseLanguage.typesystem.RemoveUnusedAssignment_QuickFix.class,
      jetbrains.mps.baseLanguage.typesystem.RemoveUnusedField_QuickFix.class,
      jetbrains.mps.baseLanguage.typesystem.RemoveUnusedLocalVariable_QuickFix.class,
      jetbrains.mps.baseLanguage.typesystem.RemoveUnusedMethod_QuickFix.class,
      jetbrains.mps.baseLanguage.typesystem.RemoveUnusedPrivateClassifier_QuickFix.class,
      jetbrains.mps.baseLanguage.typesystem.RemoveUnusedStaticField_QuickFix.class,
      jetbrains.mps.baseLanguage.typesystem.ReplaceAlwaysTrueFalseWithLiteral_QuickFix.class,
      jetbrains.mps.baseLanguage.typesystem.ResolvedUnknownNode_QuickFix.class,
      jetbrains.mps.baseLanguage.typesystem.RulesFunctions_BaseLanguage.class,
      jetbrains.mps.baseLanguage.typesystem.SetConstructorDeclaration_QuickFix.class,
      jetbrains.mps.baseLanguage.typesystem.SetConstructorToObject_QuickFix.class,
      jetbrains.mps.baseLanguage.typesystem.SetDefaultEnumConstructurToEnumConstant_QuickFix.class,
      jetbrains.mps.baseLanguage.typesystem.SetExplicitEnumConstructurToEnumConstant_QuickFix.class,
      jetbrains.mps.baseLanguage.typesystem.SimplifyBinaryLogicalExpressionWithBooleanConstant_QuickFix.class,
      jetbrains.mps.baseLanguage.typesystem.SimplifyCondition_QuickFix.class,
      jetbrains.mps.baseLanguage.typesystem.SimplifyNotExpression_QuickFix.class,
      jetbrains.mps.baseLanguage.typesystem.SimplifyWholeBinaryLogicalExpressionWithBooleanConstant_QuickFix.class,
      jetbrains.mps.baseLanguage.typesystem.SurroundExceptionWithTryCatch_QuickFix.class,
      jetbrains.mps.baseLanguage.typesystem.ThrownTypeVariableReferencesHelper.class,
      jetbrains.mps.baseLanguage.typesystem.TurnAbstractClassifierOperandOfDotExprToStaticFieldReference_QuickFix.class,
      jetbrains.mps.baseLanguage.typesystem.TurnAbstractClassifierOperandOfDotExprToStaticMethodCall_QuickFix.class,
      jetbrains.mps.baseLanguage.typesystem.TurnInstanceFieldReferenceOperationToStaticFieldReferenceOperation_QuickFix.class,
      jetbrains.mps.baseLanguage.typesystem.TurnInstanceMethodCallOperationToStaticMethodCall_QuickFix.class,
      jetbrains.mps.baseLanguage.typesystem.TurnStaticFieldReferenceOperationFieldReferenceOperation_QuickFix.class,
      jetbrains.mps.baseLanguage.typesystem.TurnStaticMethodCallOperationToInstanceMethodCall_QuickFix.class,
      jetbrains.mps.baseLanguage.typesystem.TypeVariableInInstanceOfNotAllowed_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.TypeVariableMatchUtil.class,
      jetbrains.mps.baseLanguage.typesystem.TypeVariableReference_compatibleWith_otherType_ComparisonRule.class,
      jetbrains.mps.baseLanguage.typesystem.TypesystemDescriptor.class,
      jetbrains.mps.baseLanguage.typesystem.TypesystemDescriptor.CustomOverloadedOperationsTypesProvider_a.class,
      jetbrains.mps.baseLanguage.typesystem.TypesystemDescriptor.CustomOverloadedOperationsTypesProvider_g.class,
      jetbrains.mps.baseLanguage.typesystem.TypesystemDescriptor.CustomOverloadedOperationsTypesProvider_h.class,
      jetbrains.mps.baseLanguage.typesystem.TypesystemDescriptor.CustomOverloadedOperationsTypesProvider_b.class,
      jetbrains.mps.baseLanguage.typesystem.TypesystemDescriptor.CustomOverloadedOperationsTypesProvider_c.class,
      jetbrains.mps.baseLanguage.typesystem.TypesystemDescriptor.CustomOverloadedOperationsTypesProvider_d.class,
      jetbrains.mps.baseLanguage.typesystem.TypesystemDescriptor.CustomOverloadedOperationsTypesProvider_e.class,
      jetbrains.mps.baseLanguage.typesystem.VaraibleTypeUtil.class,
      jetbrains.mps.baseLanguage.typesystem.VariableInitializationUtil.class,
      jetbrains.mps.baseLanguage.typesystem.VariableReferenceUtil.class,
      jetbrains.mps.baseLanguage.typesystem.VariableTypeDescriptor.class,
      jetbrains.mps.baseLanguage.typesystem.anonymousClassExtendsItsClassifier_SubtypingRule.class,
      jetbrains.mps.baseLanguage.typesystem.any_type_comparable_to_wildcard_type_ComparisonRule.class,
      jetbrains.mps.baseLanguage.typesystem.any_type_comparable_with_nulltype_ComparisonRule.class,
      jetbrains.mps.baseLanguage.typesystem.any_type_subtypeOf_LowerBoundType_InequationReplacementRule.class,
      jetbrains.mps.baseLanguage.typesystem.any_type_supertypeof_bottom_InequationReplacementRule.class,
      jetbrains.mps.baseLanguage.typesystem.any_type_supertypeof_nulltype_InequationReplacementRule.class,
      jetbrains.mps.baseLanguage.typesystem.any_type_supertypeof_wildcard_type_InequationReplacementRule.class,
      jetbrains.mps.baseLanguage.typesystem.boolean_extends_Boolean_1_SubtypingRule.class,
      jetbrains.mps.baseLanguage.typesystem.bound_comparableWith_lbt_ComparisonRule.class,
      jetbrains.mps.baseLanguage.typesystem.bound_comparableWith_ubt_ComparisonRule.class,
      jetbrains.mps.baseLanguage.typesystem.byte_boxing_SubtypingRule.class,
      jetbrains.mps.baseLanguage.typesystem.byte_extends_short_SubtypingRule.class,
      jetbrains.mps.baseLanguage.typesystem.byte_unboxing_SubtypingRule.class,
      jetbrains.mps.baseLanguage.typesystem.char_boxing_SubtypingRule.class,
      jetbrains.mps.baseLanguage.typesystem.char_compatibleWith_byte_ComparisonRule.class,
      jetbrains.mps.baseLanguage.typesystem.char_extends_int_SubtypingRule.class,
      jetbrains.mps.baseLanguage.typesystem.char_unboxing_SubtypingRule.class,
      jetbrains.mps.baseLanguage.typesystem.checkAbstractClassCreators_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.checkAbstractClassDefaultCreators_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.checkImplementsInterfaceRefs_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.checkThrowedByThrowIsCaught_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.checkThrowsOfMethodIsCaught_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.checkThrowsOfResourceIsCaught_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.checkTypesInForStatement_InferenceRule.class,
      jetbrains.mps.baseLanguage.typesystem.checkUnnecessaryInstanceof_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_AbstractClassifierReference_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_AbstractMethodsInClass_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_AnnotationDuplication_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_AnnotationForVoidMethod_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_AnnotationInstanceHasAllValues_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_AnnotationMethodDeclarationReturnType_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_AnnotationTarget_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_AnonymousClassCreator_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_AnonymousClassHasArgumentsInGoodRole_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_AnonymousClassHasConstructorDeclaration_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_AnonymousClassWithNoConstructorInheritsDefaultConstructor_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_ArrayClassExpression_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_ArrayClone_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_ArrayCreator_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_ArrayLength_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_BaseAssignmentExpression_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_BaseAssignmentExpression_TreeBalance_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_BaseMethodDeclarationParameterUniqueNames_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_BaseMethodDeclaration_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_BaseMethodDeclaration_UnreachableStatements_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_BinaryLogicalOperationCanBeSimplified_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_BoundTypeinBoundType_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_BreakStatement_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_CastExpressionLegalExpression_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_CaughtWasThrown_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_ClassConceptAbstractAndFinal_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_ClassCreatorConstructorVisibility_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_ClassCreator_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_ClassDoesNotExtendFinalClass_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_ClassRefs_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_ClassShouldHaveConstructor_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_ClassifierMethodMemberVisibilityInInterfaces_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_ClassifierMethodsHierarchy_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_ClassifierOverrideAnnotations_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_ClassifierType_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_ClassifierType_UseOfRawType_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_ConceptFunctionParameter_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_ConceptFunctionReturns_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_ConceptFunction_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_ConstructorInvocationStatementHasConstructor_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_ConstructorInvocationStatementIsFirstStatement_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_ConstructorRecursiveInvocation_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_ContinueStatement_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_CorrectScope_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_DefaultClassCreator_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_DefaultMethodDeclarationV8_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_DefaultMethodIsNotAbstract_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_DefaultMethodIsNotStatic_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_DefaultSuperConstructorThrowables_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_DuplicateCase_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_DuplicateClassifierNames_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_DuplicateEnumConstantMethods_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_DuplicateEnumConstants_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_DuplicateMethods_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_DuplicatedConstructors_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_DynamicReference_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_ElsifClause_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_EnumConstantMustUseExplicitConstructor_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_EnumConstantOverrideAnnotation_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_EnumConstantOverridingMethods_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_EqualsMethodTypes_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_ExceptionWasNotCaught_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_ExpressionStatement_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_Expression_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_FieldDeclarationCanBeLocalVariable_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_FieldDuplication_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_FieldIsNeverUsedOrAssigned_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_FieldReferenceOperationMustBeStatic_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_FinalAndAbstractMethod_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_FinalFieldReferenceNotAssigned_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_FinalFieldWasAssigned_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_FinalLocalFieldReferenceNotAssigned_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_FinalNotPrivateMethod_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_FinalVariableReferenceNotAssigned_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_ForeachWithErasure_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_GenericLValueExpression_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_GenericNewExpressionTypeParameters_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_GenericNewExpression_PointingToNonStaticFromStatic_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_GetAccessor_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_HasSuperConstructorCall_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_HexLongLiteral_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_IMethodCallTypeArgs_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_IThrowCheckedExceptions_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_IfStatementBooleanConstantInCondition_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_IncompleteRightParen_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_InnerClassesCantHaveStaticDeclarations_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_InnerClassesCantHaveStaticFields_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_InnerClassesCantHaveStaticInitializer_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_InnerClassesCantHaveStaticInnerClasses_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_InnerClassesCantHaveStaticMethods_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_InstanceInitializer_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_InstanceMethodCallOperationMustBeStatic_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_InstanceMethodDeclarationForAbstractPrivateModifiers_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_InstanceMethodDeclarationNeedsBody_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_InstanceMethodDeclarationNeedsVisibility_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_InstanceMethodDeclarationThrownExceptionsSignature_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_InstanceOf_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_Interface_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_LeftParen_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_LocalMethodCall_TypeArguments_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_LocalPropertyReferenceNotUsedInUnaryNumericOperations_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_LocalStaticMethodCall_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_LongLiteral_within_range_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_MethodBody_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_NativeMethod_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_NestedNewExpression_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_NonStaticAnonymousClassCreation_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_NonStaticClassInInterface_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_NonStaticInnerClassCreation_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_NonStaticInnerDefaultClassCreation_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_NotExpressionCanBeSimplified_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_NotExpression_NeedsParens_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_NullableStates_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_OverrideNullAnnotations_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_ParenthesisedExpressionSurroundedWithParentheses_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_PostfixDecrementExpression_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_PostfixIncrementExpression_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_PrefixDecrementExpression_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_PrefixIncrementExpression_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_PrimitiveClassExpression_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_PropertyAssignedToItself_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_PropertyNameUniqueness_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_PropertyReferenceSetterVisibility_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_ResourceVariable_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_SetAccessor_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_SingleLineCommentForMultilineCode_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_StaticFieldDeclarationMustHaveInitializerInInterface_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_StaticFieldDuplication_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_StaticFieldReferenceAccessOperation_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_StaticFieldReference_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_StaticFinalFieldNotAssigned_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_StaticFinalFieldWasAssigned_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_StaticInitializer_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_StaticInterfaceMethodCall_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_StaticInterfaceMethodDeclarationV8_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_StaticInterfaceMethodsHaveAllowedModifiers_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_StaticMethodCallOperationMustBeInstance_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_StringLiteral_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_StringTypeClassifierType_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_SuperInterfaceMethodCall_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_SynchronizedStatement_NonFinalFields_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_TernaryOperationCanBeSimplified_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_TernaryOperatorExpression_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_ThisExpression_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_ThrowStatementInFinally_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_TryUniversalStatement_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_TypeInMethodCall_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_TypeInVarDecl_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_TypeVariableDeclarationDuplicateName_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_TypeVariableDeclarationForwardReferences_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_TypeVariableDeclarationWithInvalidWildTypes_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_Type_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_UninitializedLocalFieldInOperation_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_UninitializedLocalField_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_UnknownConsCall_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_UnknownDotCall_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_UnknownLocalCall_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_UnknownNameRef_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_UnknownNew_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_UnneededJavaImports_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_UnneededMethodTypeParameters_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_UnqualifiedEnumConstUnderSwitch_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_UnqualifiedEnumConstant_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_UnqualifiedStaticCall_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_UnqualifiedStaticFieldReference_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_UnusedPackageClassifier_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_UnusedPackageProtectedMethod_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_UnusedPrivateClassifier_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_UnusedPrivateMethods_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_UnusedStaticFields_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_UpperBoundType_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_UsingStatement_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_VariableArityType_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_VariableAssignedToItself_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_VariableReferenceToStaticField_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_VolatileFieldNonFinal_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_VolatileStaticFieldNonFinal_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_parametersCount_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.check_switchArgument_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.classifierTypeWithSameClassifier_InequationReplacementRule.class,
      jetbrains.mps.baseLanguage.typesystem.double_boxing_SubtypingRule.class,
      jetbrains.mps.baseLanguage.typesystem.double_extends_floatingPoint_SubtypingRule.class,
      jetbrains.mps.baseLanguage.typesystem.double_unboxing_SubtypingRule.class,
      jetbrains.mps.baseLanguage.typesystem.float_boxing_SubtypingRule.class,
      jetbrains.mps.baseLanguage.typesystem.float_extends_double_SubtypingRule.class,
      jetbrains.mps.baseLanguage.typesystem.float_unboxing_SubtypingRule.class,
      jetbrains.mps.baseLanguage.typesystem.genericArrayCreatorsAreIllegal_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.genericArrayCreatorsWithInitializerAreIllegal_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.generic_ClassfierType_subtypeOf_generic_ClassifierType_InequationReplacementRule.class,
      jetbrains.mps.baseLanguage.typesystem.int_boxing_SubtypingRule.class,
      jetbrains.mps.baseLanguage.typesystem.int_extends_long_SubtypingRule.class,
      jetbrains.mps.baseLanguage.typesystem.int_unboxing_SubtypingRule.class,
      jetbrains.mps.baseLanguage.typesystem.java_lang_String_extends_string_SubtypingRule.class,
      jetbrains.mps.baseLanguage.typesystem.lbt_comparableWith_bound_ComparisonRule.class,
      jetbrains.mps.baseLanguage.typesystem.long_boxing_SubtypingRule.class,
      jetbrains.mps.baseLanguage.typesystem.long_extends_float_SubtypingRule.class,
      jetbrains.mps.baseLanguage.typesystem.long_extends_integral_SubtypingRule.class,
      jetbrains.mps.baseLanguage.typesystem.long_unboxing_SubtypingRule.class,
      jetbrains.mps.baseLanguage.typesystem.makeReferenceStatic_QuickFix.class,
      jetbrains.mps.baseLanguage.typesystem.raw_ClassifierType_subtypeOf_generic_ClassifierType_InequationReplacementRule.class,
      jetbrains.mps.baseLanguage.typesystem.rebalanceCastExpression_QuickFix.class,
      jetbrains.mps.baseLanguage.typesystem.replaceNode_QuickFix.class,
      jetbrains.mps.baseLanguage.typesystem.short_boxing_SubtypingRule.class,
      jetbrains.mps.baseLanguage.typesystem.short_extends_int_SubtypingRule.class,
      jetbrains.mps.baseLanguage.typesystem.short_unboxing_SubtypingRule.class,
      jetbrains.mps.baseLanguage.typesystem.string_extends_java_lang_String_SubtypingRule.class,
      jetbrains.mps.baseLanguage.typesystem.subtyping_arrayType_SubtypingRule.class,
      jetbrains.mps.baseLanguage.typesystem.subtyping_classifier_SubtypingRule.class,
      jetbrains.mps.baseLanguage.typesystem.supertypesOfLowerBoundType_InequationReplacementRule.class,
      jetbrains.mps.baseLanguage.typesystem.supertypesOfUpperBoundType_SubtypingRule.class,
      jetbrains.mps.baseLanguage.typesystem.supertypes_of_typeVariableReference_SubtypingRule.class,
      jetbrains.mps.baseLanguage.typesystem.typeOf_ArrayAccess_InferenceRule.class,
      jetbrains.mps.baseLanguage.typesystem.typeOf_ArrayCreatorWithInitializer_InferenceRule.class,
      jetbrains.mps.baseLanguage.typesystem.typeOf_AssertStatement_InferenceRule.class,
      jetbrains.mps.baseLanguage.typesystem.typeOf_BaseMethodDeclaration_InferenceRule.class,
      jetbrains.mps.baseLanguage.typesystem.typeOf_BooleanConst_InferenceRule.class,
      jetbrains.mps.baseLanguage.typesystem.typeOf_CastExpression_InferenceRule.class,
      jetbrains.mps.baseLanguage.typesystem.typeOf_ClassifierClassExpression_InferenceRule.class,
      jetbrains.mps.baseLanguage.typesystem.typeOf_ClosureParameterReference_InferenceRule.class,
      jetbrains.mps.baseLanguage.typesystem.typeOf_ConceptFunctionParameter_InferenceRule.class,
      jetbrains.mps.baseLanguage.typesystem.typeOf_ConceptFunction_InferenceRule.class,
      jetbrains.mps.baseLanguage.typesystem.typeOf_DoWhileStatement_InferenceRule.class,
      jetbrains.mps.baseLanguage.typesystem.typeOf_EnumConstantDeclaration_InferenceRule.class,
      jetbrains.mps.baseLanguage.typesystem.typeOf_EnumConstantReference_InferenceRule.class,
      jetbrains.mps.baseLanguage.typesystem.typeOf_FloatingPointConstant_InferenceRule.class,
      jetbrains.mps.baseLanguage.typesystem.typeOf_ForeachStatement_InferenceRule.class,
      jetbrains.mps.baseLanguage.typesystem.typeOf_GenericNewExpression_InferenceRule.class,
      jetbrains.mps.baseLanguage.typesystem.typeOf_IfStatement_InferenceRule.class,
      jetbrains.mps.baseLanguage.typesystem.typeOf_InstanceOfExpression_InferenceRule.class,
      jetbrains.mps.baseLanguage.typesystem.typeOf_IntegerLiteral_InferenceRule.class,
      jetbrains.mps.baseLanguage.typesystem.typeOf_NotExpression_InferenceRule.class,
      jetbrains.mps.baseLanguage.typesystem.typeOf_NullLiteral_InferenceRule.class,
      jetbrains.mps.baseLanguage.typesystem.typeOf_OrExpression_InferenceRule.class,
      jetbrains.mps.baseLanguage.typesystem.typeOf_ParenthesizedExpression_InferenceRule.class,
      jetbrains.mps.baseLanguage.typesystem.typeOf_ReturnStatement_InferenceRule.class,
      jetbrains.mps.baseLanguage.typesystem.typeOf_StringLiteral_InferenceRule.class,
      jetbrains.mps.baseLanguage.typesystem.typeOf_Switch_InferenceRule.class,
      jetbrains.mps.baseLanguage.typesystem.typeOf_TernaryOperator_InferenceRule.class,
      jetbrains.mps.baseLanguage.typesystem.typeOf_ThrowStatement_InferenceRule.class,
      jetbrains.mps.baseLanguage.typesystem.typeOf_VarRef_InferenceRule.class,
      jetbrains.mps.baseLanguage.typesystem.typeOf_WhileStatement_InferenceRule.class,
      jetbrains.mps.baseLanguage.typesystem.typeOf_andExpression_InferenceRule.class,
      jetbrains.mps.baseLanguage.typesystem.typeOf_equalsExpression_InferenceRule.class,
      jetbrains.mps.baseLanguage.typesystem.typeOf_notEqualsExpression_InferenceRule.class,
      jetbrains.mps.baseLanguage.typesystem.typeOf_thisExpr_InferenceRule.class,
      jetbrains.mps.baseLanguage.typesystem.typeof_AbstractCatchClause_InferenceRule.class,
      jetbrains.mps.baseLanguage.typesystem.typeof_AbstractCreator_InferenceRule.class,
      jetbrains.mps.baseLanguage.typesystem.typeof_AbstractLongLiteral_InferenceRule.class,
      jetbrains.mps.baseLanguage.typesystem.typeof_AbstractUnaryNumberOperation_InferenceRule.class,
      jetbrains.mps.baseLanguage.typesystem.typeof_AdditionalForLoopVariable_InferenceRule.class,
      jetbrains.mps.baseLanguage.typesystem.typeof_AnnotationInstanceExpression_InferenceRule.class,
      jetbrains.mps.baseLanguage.typesystem.typeof_AnnotationInstanceValue_InferenceRule.class,
      jetbrains.mps.baseLanguage.typesystem.typeof_AnnotationInstance_InferenceRule.class,
      jetbrains.mps.baseLanguage.typesystem.typeof_AnonymousClassCreator_InferenceRule.class,
      jetbrains.mps.baseLanguage.typesystem.typeof_AnonymousClass_InferenceRule.class,
      jetbrains.mps.baseLanguage.typesystem.typeof_ArrayClassExpression_InferenceRule.class,
      jetbrains.mps.baseLanguage.typesystem.typeof_ArrayCloneOperation_InferenceRule.class,
      jetbrains.mps.baseLanguage.typesystem.typeof_ArrayCreator_InferenceRule.class,
      jetbrains.mps.baseLanguage.typesystem.typeof_ArrayLengthOperation_InferenceRule.class,
      jetbrains.mps.baseLanguage.typesystem.typeof_ArrayLiteral_InferenceRule.class,
      jetbrains.mps.baseLanguage.typesystem.typeof_AsExpression_InferenceRule.class,
      jetbrains.mps.baseLanguage.typesystem.typeof_BaseAssignmentExpression_InferenceRule.class,
      jetbrains.mps.baseLanguage.typesystem.typeof_BaseStringOperation_InferenceRule.class,
      jetbrains.mps.baseLanguage.typesystem.typeof_BaseVariableDeclaration_InferenceRule.class,
      jetbrains.mps.baseLanguage.typesystem.typeof_BaseVariableReference_InferenceRule.class,
      jetbrains.mps.baseLanguage.typesystem.typeof_BinaryCompareOperation_InferenceRule.class,
      jetbrains.mps.baseLanguage.typesystem.typeof_BinaryOperation_InferenceRule.class,
      jetbrains.mps.baseLanguage.typesystem.typeof_BitwiseNotExpression_InferenceRule.class,
      jetbrains.mps.baseLanguage.typesystem.typeof_CatchVariable_InferenceRule.class,
      jetbrains.mps.baseLanguage.typesystem.typeof_CharConstant_InferenceRule.class,
      jetbrains.mps.baseLanguage.typesystem.typeof_ClassCreator_InferenceRule.class,
      jetbrains.mps.baseLanguage.typesystem.typeof_DefaultClassCreator_InferenceRule.class,
      jetbrains.mps.baseLanguage.typesystem.typeof_DimensionExpression_InferenceRule.class,
      jetbrains.mps.baseLanguage.typesystem.typeof_DotExpression_InferenceRule.class,
      jetbrains.mps.baseLanguage.typesystem.typeof_ElsifClause_InferenceRule.class,
      jetbrains.mps.baseLanguage.typesystem.typeof_EnumValueOfExpression_InferenceRule.class,
      jetbrains.mps.baseLanguage.typesystem.typeof_EnumValuesExpression_InferenceRule.class,
      jetbrains.mps.baseLanguage.typesystem.typeof_EscapeOperation_InferenceRule.class,
      jetbrains.mps.baseLanguage.typesystem.typeof_FieldReferenceOperation_InferenceRule.class,
      jetbrains.mps.baseLanguage.typesystem.typeof_FloatingPointFloatConstant_InferenceRule.class,
      jetbrains.mps.baseLanguage.typesystem.typeof_FormatOperation_InferenceRule.class,
      jetbrains.mps.baseLanguage.typesystem.typeof_GenereicVarialeExpression_InferenceRule.class,
      jetbrains.mps.baseLanguage.typesystem.typeof_GetAccessor_InferenceRule.class,
      jetbrains.mps.baseLanguage.typesystem.typeof_IInferredExpression_InferenceRule.class,
      jetbrains.mps.baseLanguage.typesystem.typeof_IMethodCall_InferenceRule.class,
      jetbrains.mps.baseLanguage.typesystem.typeof_IYetUnresolved_InferenceRule.class,
      jetbrains.mps.baseLanguage.typesystem.typeof_InstanceMethodCallOperation_InferenceRule.class,
      jetbrains.mps.baseLanguage.typesystem.typeof_LocalMethodCall_InferenceRule.class,
      jetbrains.mps.baseLanguage.typesystem.typeof_LocalPropertyReference_InferenceRule.class,
      jetbrains.mps.baseLanguage.typesystem.typeof_NPEEqualsExpression_InferenceRule.class,
      jetbrains.mps.baseLanguage.typesystem.typeof_NPENotEqualsExpression_InferenceRule.class,
      jetbrains.mps.baseLanguage.typesystem.typeof_NestedNewExpression_InferenceRule.class,
      jetbrains.mps.baseLanguage.typesystem.typeof_OperationAssignmentExpression_InferenceRule.class,
      jetbrains.mps.baseLanguage.typesystem.typeof_PassByRefExpression_InferenceRule.class,
      jetbrains.mps.baseLanguage.typesystem.typeof_PrimitiveClassExpression_InferenceRule.class,
      jetbrains.mps.baseLanguage.typesystem.typeof_PropertyReference_InferenceRule.class,
      jetbrains.mps.baseLanguage.typesystem.typeof_PropertyValueReference_InferenceRule.class,
      jetbrains.mps.baseLanguage.typesystem.typeof_Property_InferenceRule.class,
      jetbrains.mps.baseLanguage.typesystem.typeof_ResourceVariable_InferenceRule.class,
      jetbrains.mps.baseLanguage.typesystem.typeof_SetAccessor_InferenceRule.class,
      jetbrains.mps.baseLanguage.typesystem.typeof_StaticMethodCallOperation_InferenceRule.class,
      jetbrains.mps.baseLanguage.typesystem.typeof_StaticMethodCall_InferenceRule.class,
      jetbrains.mps.baseLanguage.typesystem.typeof_StringBooleanOperation_InferenceRule.class,
      jetbrains.mps.baseLanguage.typesystem.typeof_SubstringExpression_InferenceRule.class,
      jetbrains.mps.baseLanguage.typesystem.typeof_SuperConstructorInvocation_InferenceRule.class,
      jetbrains.mps.baseLanguage.typesystem.typeof_SuperMethodCall_InferenceRule.class,
      jetbrains.mps.baseLanguage.typesystem.typeof_SwitchCase_InferenceRule.class,
      jetbrains.mps.baseLanguage.typesystem.typeof_SynchronizedStatement_InferenceRule.class,
      jetbrains.mps.baseLanguage.typesystem.typeof_ThisConstructorInvocation_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.typeof_TrimOperation_InferenceRule.class,
      jetbrains.mps.baseLanguage.typesystem.typeof_UnaryMinus_InferenceRule.class,
      jetbrains.mps.baseLanguage.typesystem.typeof_ValueParameter_InferenceRule.class,
      jetbrains.mps.baseLanguage.typesystem.typeof_ValueRef_InferenceRule.class,
      jetbrains.mps.baseLanguage.typesystem.typeof_VariableDeclaration_InferenceRule.class,
      jetbrains.mps.baseLanguage.typesystem.typevar_comparable_with_classifier_type_ComparisonRule.class,
      jetbrains.mps.baseLanguage.typesystem.ubt_comparableWith_bound_ComparisonRule.class,
      jetbrains.mps.baseLanguage.typesystem.voidArraysAreIllegal_NonTypesystemRule.class,
      jetbrains.mps.baseLanguage.typesystem.wildCard_is_Object_SubtypingRule.class,
      jetbrains.mps.baseLanguage.typesystem.wrapCastExpressionInParentheses_QuickFix.class,
      jetbrains.mps.baseLanguage.typesystem.wrapMisplacedTernaryOperatorInParens_QuickFix.class
    });
  }
  private void register4(BrowserPlatform platform) {
    LanguageRuntime language = new jetbrains.mps.execution.util.Language();
    ModuleClassLoader loader = FinancialClassLoader.create(language.getIdentity().getSourceModuleReference());
    loaders.add(loader);
    platform.registerLanguage(language, loader, Collections.emptyList(), Collections.emptyList(), new Class<?>[] {
      jetbrains.mps.execution.util.typesystem.TypesystemDescriptor.class
    });
  }
  
  private void bindRules(Iterable<?> rules, String namespace) {
    for (Object rule : rules) {
      jetbrains.mps.lang.typesystem.runtime.RuleSet.registerRuleNamespace(rule.getClass(), namespace);
      aspectRuleClasses.add(rule.getClass());
    }
  }
  public void close() {
    aspectRuleClasses.forEach(jetbrains.mps.lang.typesystem.runtime.RuleSet::unregisterRuleNamespace);
    aspectRuleClasses.clear();
    for (ModuleClassLoader loader : loaders) loader.dispose(); loaders.clear();
  }
}
