package alefj2cl;
import java.util.*;
import jetbrains.mps.classloading.*;
import jetbrains.mps.core.platform.BrowserPlatform;
import jetbrains.mps.smodel.language.LanguageRuntime;
import jetbrains.mps.lang.typesystem.runtime.IHelginsDescriptor;
import jetbrains.mps.lang.typesystem.runtime.RuleSet;
import jetbrains.mps.smodel.runtime.IconResource;
/** Explicit registration of the compiled ALEF language closure. */
public final class AlefLanguages implements AutoCloseable {
  private final mpsj2cl.financial.FinancialLanguages base;
  private final List<ModuleClassLoader> loaders = new ArrayList<>();
  private final Set<Class<?>> rules = new HashSet<>();
  private final Set<Class<?>> iconProviders = new HashSet<>();
  public AlefLanguages(BrowserPlatform platform) {
    base = new mpsj2cl.financial.FinancialLanguages(platform);
    platform.registerLanguage(new com.mbeddr.mpsutil.grammarcells.runtimelang.Language());
    IconResource.registerProvider(bronspraak.structure.IconContainer.class, "08d6f877-03cc-45d3-b03c-d6f786266853(bronspraak)", "bronspraak.structure");
    iconProviders.add(bronspraak.structure.IconContainer.class);
    IconResource.registerProvider(contexts.structure.IconContainer.class, "09c04f52-88c5-4bd6-a481-cabab9f61ff5(contexts)", "contexts.structure");
    iconProviders.add(contexts.structure.IconContainer.class);
    IconResource.registerProvider(gegevensspraak.structure.IconContainer.class, "471364db-8078-4933-b2ef-88232bfa34fc(gegevensspraak)", "gegevensspraak.structure");
    iconProviders.add(gegevensspraak.structure.IconContainer.class);
    IconResource.registerProvider(jetbrains.mps.lang.structure.structure.IconContainer.class, "c72da2b9-7cce-4447-8389-f407dc1158b7(jetbrains.mps.lang.structure)", "jetbrains.mps.lang.structure.structure");
    iconProviders.add(jetbrains.mps.lang.structure.structure.IconContainer.class);
    IconResource.registerProvider(rapporten.structure.IconContainer.class, "b2fc4154-1657-4d74-8828-c55b57a96ecd(rapporten)", "rapporten.structure");
    iconProviders.add(rapporten.structure.IconContainer.class);
    IconResource.registerProvider(regelspraak.structure.IconContainer.class, "7bbaf860-5f96-44b4-9731-6e00ae137ece(regelspraak)", "regelspraak.structure");
    iconProviders.add(regelspraak.structure.IconContainer.class);
    IconResource.registerProvider(servicespraak.structure.IconContainer.class, "d8af31be-1847-4d5b-8686-78e232d4e0f8(servicespraak)", "servicespraak.structure");
    iconProviders.add(servicespraak.structure.IconContainer.class);
    IconResource.registerProvider(testspraak.structure.IconContainer.class, "8bc962c0-cb3c-49f0-aa03-23c3bc0304b0(testspraak)", "testspraak.structure");
    iconProviders.add(testspraak.structure.IconContainer.class);
    register0(platform);
    register1(platform);
    register2(platform);
    register3(platform);
    register4(platform);
    register5(platform);
    register6(platform);
    register7(platform);
    register8(platform);
    register9(platform);
    register10(platform);
    register11(platform);
    register12(platform);
    register13(platform);
    register14(platform);
    register15(platform);
    register16(platform);
    register17(platform);
    register18(platform);
    register19(platform);
    register20(platform);
    base.verifyTypesystems(platform);
  }
  private void register0(BrowserPlatform platform) {
    LanguageRuntime language = new jetbrains.mps.baseLanguage.unitTest.Language();
    ModuleClassLoader loader = FinancialClassLoader.create(language.getIdentity().getSourceModuleReference());
    loaders.add(loader);
    platform.registerLanguage(language, loader, Collections.emptyList(), Collections.emptyList(), new Class<?>[]{
      jetbrains.mps.baseLanguage.unitTest.typesystem.check_TestIsCompiledAndLoadedByMPS_NonTypesystemRule.class
    });
    IHelginsDescriptor aspect = language.getAspect(IHelginsDescriptor.class);
    if (aspect != null) {
      bind(aspect.getInferenceRules(), language.getNamespace());
      bind(aspect.getNonTypesystemRules(), language.getNamespace());
      bind(aspect.getSubtypingRules(), language.getNamespace());
      bind(aspect.getSubstituteTypeRules(), language.getNamespace());
      bind(aspect.getComparisonRules(), language.getNamespace());
      bind(aspect.getEliminationRules(), language.getNamespace());
      bind(aspect.getVariableConverters(), language.getNamespace());
      bind(aspect.getOverloadedOperationsTypesProviders(), language.getNamespace());
    }
  }
  private void register1(BrowserPlatform platform) {
    LanguageRuntime language = new jetbrains.mps.core.xml.Language();
    ModuleClassLoader loader = FinancialClassLoader.create(language.getIdentity().getSourceModuleReference());
    loaders.add(loader);
    platform.registerLanguage(language, loader, Collections.emptyList(), Collections.emptyList(), new Class<?>[]{

    });
    IHelginsDescriptor aspect = language.getAspect(IHelginsDescriptor.class);
    if (aspect != null) {
      bind(aspect.getInferenceRules(), language.getNamespace());
      bind(aspect.getNonTypesystemRules(), language.getNamespace());
      bind(aspect.getSubtypingRules(), language.getNamespace());
      bind(aspect.getSubstituteTypeRules(), language.getNamespace());
      bind(aspect.getComparisonRules(), language.getNamespace());
      bind(aspect.getEliminationRules(), language.getNamespace());
      bind(aspect.getVariableConverters(), language.getNamespace());
      bind(aspect.getOverloadedOperationsTypesProviders(), language.getNamespace());
    }
  }
  private void register2(BrowserPlatform platform) {
    LanguageRuntime language = new jetbrains.mps.baseLanguage.closures.Language();
    ModuleClassLoader loader = FinancialClassLoader.create(language.getIdentity().getSourceModuleReference());
    loaders.add(loader);
    platform.registerLanguage(language, loader, Collections.emptyList(), Collections.emptyList(), new Class<?>[]{

    });
    IHelginsDescriptor aspect = language.getAspect(IHelginsDescriptor.class);
    if (aspect != null) {
      bind(aspect.getInferenceRules(), language.getNamespace());
      bind(aspect.getNonTypesystemRules(), language.getNamespace());
      bind(aspect.getSubtypingRules(), language.getNamespace());
      bind(aspect.getSubstituteTypeRules(), language.getNamespace());
      bind(aspect.getComparisonRules(), language.getNamespace());
      bind(aspect.getEliminationRules(), language.getNamespace());
      bind(aspect.getVariableConverters(), language.getNamespace());
      bind(aspect.getOverloadedOperationsTypesProviders(), language.getNamespace());
    }
  }
  private void register3(BrowserPlatform platform) {
    LanguageRuntime language = new jetbrains.mps.baseLanguage.collections.Language();
    ModuleClassLoader loader = FinancialClassLoader.create(language.getIdentity().getSourceModuleReference());
    loaders.add(loader);
    platform.registerLanguage(language, loader, Collections.emptyList(), Collections.emptyList(), new Class<?>[]{

    });
    IHelginsDescriptor aspect = language.getAspect(IHelginsDescriptor.class);
    if (aspect != null) {
      bind(aspect.getInferenceRules(), language.getNamespace());
      bind(aspect.getNonTypesystemRules(), language.getNamespace());
      bind(aspect.getSubtypingRules(), language.getNamespace());
      bind(aspect.getSubstituteTypeRules(), language.getNamespace());
      bind(aspect.getComparisonRules(), language.getNamespace());
      bind(aspect.getEliminationRules(), language.getNamespace());
      bind(aspect.getVariableConverters(), language.getNamespace());
      bind(aspect.getOverloadedOperationsTypesProviders(), language.getNamespace());
    }
  }
  private void register4(BrowserPlatform platform) {
    LanguageRuntime language = new jetbrains.mps.lang.modelapi.Language();
    ModuleClassLoader loader = FinancialClassLoader.create(language.getIdentity().getSourceModuleReference());
    loaders.add(loader);
    platform.registerLanguage(language, loader, Collections.emptyList(), Collections.emptyList(), new Class<?>[]{

    });
    IHelginsDescriptor aspect = language.getAspect(IHelginsDescriptor.class);
    if (aspect != null) {
      bind(aspect.getInferenceRules(), language.getNamespace());
      bind(aspect.getNonTypesystemRules(), language.getNamespace());
      bind(aspect.getSubtypingRules(), language.getNamespace());
      bind(aspect.getSubstituteTypeRules(), language.getNamespace());
      bind(aspect.getComparisonRules(), language.getNamespace());
      bind(aspect.getEliminationRules(), language.getNamespace());
      bind(aspect.getVariableConverters(), language.getNamespace());
      bind(aspect.getOverloadedOperationsTypesProviders(), language.getNamespace());
    }
  }
  private void register5(BrowserPlatform platform) {
    LanguageRuntime language = new jetbrains.mps.baseLanguage.classifiers.Language();
    ModuleClassLoader loader = FinancialClassLoader.create(language.getIdentity().getSourceModuleReference());
    loaders.add(loader);
    platform.registerLanguage(language, loader, Collections.emptyList(), Collections.emptyList(), new Class<?>[]{

    });
    IHelginsDescriptor aspect = language.getAspect(IHelginsDescriptor.class);
    if (aspect != null) {
      bind(aspect.getInferenceRules(), language.getNamespace());
      bind(aspect.getNonTypesystemRules(), language.getNamespace());
      bind(aspect.getSubtypingRules(), language.getNamespace());
      bind(aspect.getSubstituteTypeRules(), language.getNamespace());
      bind(aspect.getComparisonRules(), language.getNamespace());
      bind(aspect.getEliminationRules(), language.getNamespace());
      bind(aspect.getVariableConverters(), language.getNamespace());
      bind(aspect.getOverloadedOperationsTypesProviders(), language.getNamespace());
    }
  }
  private void register6(BrowserPlatform platform) {
    LanguageRuntime language = new jetbrains.mps.lang.smodel.Language();
    ModuleClassLoader loader = FinancialClassLoader.create(language.getIdentity().getSourceModuleReference());
    loaders.add(loader);
    platform.registerLanguage(language, loader, Collections.emptyList(), Collections.emptyList(), new Class<?>[]{
      jetbrains.mps.lang.smodel.typesystem.RulesUtil.class
    });
    IHelginsDescriptor aspect = language.getAspect(IHelginsDescriptor.class);
    if (aspect != null) {
      bind(aspect.getInferenceRules(), language.getNamespace());
      bind(aspect.getNonTypesystemRules(), language.getNamespace());
      bind(aspect.getSubtypingRules(), language.getNamespace());
      bind(aspect.getSubstituteTypeRules(), language.getNamespace());
      bind(aspect.getComparisonRules(), language.getNamespace());
      bind(aspect.getEliminationRules(), language.getNamespace());
      bind(aspect.getVariableConverters(), language.getNamespace());
      bind(aspect.getOverloadedOperationsTypesProviders(), language.getNamespace());
    }
  }
  private void register7(BrowserPlatform platform) {
    LanguageRuntime language = new jetbrains.mps.lang.descriptor.Language();
    ModuleClassLoader loader = FinancialClassLoader.create(language.getIdentity().getSourceModuleReference());
    loaders.add(loader);
    platform.registerLanguage(language, loader, Collections.emptyList(), Collections.emptyList(), new Class<?>[]{

    });
    IHelginsDescriptor aspect = language.getAspect(IHelginsDescriptor.class);
    if (aspect != null) {
      bind(aspect.getInferenceRules(), language.getNamespace());
      bind(aspect.getNonTypesystemRules(), language.getNamespace());
      bind(aspect.getSubtypingRules(), language.getNamespace());
      bind(aspect.getSubstituteTypeRules(), language.getNamespace());
      bind(aspect.getComparisonRules(), language.getNamespace());
      bind(aspect.getEliminationRules(), language.getNamespace());
      bind(aspect.getVariableConverters(), language.getNamespace());
      bind(aspect.getOverloadedOperationsTypesProviders(), language.getNamespace());
    }
  }
  private void register8(BrowserPlatform platform) {
    LanguageRuntime language = new jetbrains.mps.lang.sharedConcepts.Language();
    ModuleClassLoader loader = FinancialClassLoader.create(language.getIdentity().getSourceModuleReference());
    loaders.add(loader);
    platform.registerLanguage(language, loader, Collections.emptyList(), Collections.emptyList(), new Class<?>[]{

    });
    IHelginsDescriptor aspect = language.getAspect(IHelginsDescriptor.class);
    if (aspect != null) {
      bind(aspect.getInferenceRules(), language.getNamespace());
      bind(aspect.getNonTypesystemRules(), language.getNamespace());
      bind(aspect.getSubtypingRules(), language.getNamespace());
      bind(aspect.getSubstituteTypeRules(), language.getNamespace());
      bind(aspect.getComparisonRules(), language.getNamespace());
      bind(aspect.getEliminationRules(), language.getNamespace());
      bind(aspect.getVariableConverters(), language.getNamespace());
      bind(aspect.getOverloadedOperationsTypesProviders(), language.getNamespace());
    }
  }
  private void register9(BrowserPlatform platform) {
    LanguageRuntime language = new jetbrains.mps.lang.structure.Language();
    ModuleClassLoader loader = FinancialClassLoader.create(language.getIdentity().getSourceModuleReference());
    loaders.add(loader);
    platform.registerLanguage(language, loader, Collections.emptyList(), Collections.emptyList(), new Class<?>[]{

    });
    IHelginsDescriptor aspect = language.getAspect(IHelginsDescriptor.class);
    if (aspect != null) {
      bind(aspect.getInferenceRules(), language.getNamespace());
      bind(aspect.getNonTypesystemRules(), language.getNamespace());
      bind(aspect.getSubtypingRules(), language.getNamespace());
      bind(aspect.getSubstituteTypeRules(), language.getNamespace());
      bind(aspect.getComparisonRules(), language.getNamespace());
      bind(aspect.getEliminationRules(), language.getNamespace());
      bind(aspect.getVariableConverters(), language.getNamespace());
      bind(aspect.getOverloadedOperationsTypesProviders(), language.getNamespace());
    }
  }
  private void register10(BrowserPlatform platform) {
    LanguageRuntime language = new jetbrains.mps.lang.text.Language();
    ModuleClassLoader loader = FinancialClassLoader.create(language.getIdentity().getSourceModuleReference());
    loaders.add(loader);
    platform.registerLanguage(language, loader, Collections.emptyList(), Collections.emptyList(), new Class<?>[]{
      jetbrains.mps.lang.text.typesystem.TypesystemDescriptor.class,
      jetbrains.mps.lang.text.typesystem.check_Empty_Paragraph_NonTypesystemRule.class,
      jetbrains.mps.lang.text.typesystem.check_Line_Can_Be_Bullet_NonTypesystemRule.class,
      jetbrains.mps.lang.text.typesystem.check_Line_Can_Be_Numbered_NonTypesystemRule.class,
      jetbrains.mps.lang.text.typesystem.check_Line_NonTypesystemRule.class,
      jetbrains.mps.lang.text.typesystem.check_Paragraph_Can_Be_Bullet_NonTypesystemRule.class,
      jetbrains.mps.lang.text.typesystem.check_Paragraph_Can_Be_NumberedPoint_NonTypesystemRule.class,
      jetbrains.mps.lang.text.typesystem.check_Word_NonTypesystemRule.class
    });
    IHelginsDescriptor aspect = language.getAspect(IHelginsDescriptor.class);
    if (aspect != null) {
      bind(aspect.getInferenceRules(), language.getNamespace());
      bind(aspect.getNonTypesystemRules(), language.getNamespace());
      bind(aspect.getSubtypingRules(), language.getNamespace());
      bind(aspect.getSubstituteTypeRules(), language.getNamespace());
      bind(aspect.getComparisonRules(), language.getNamespace());
      bind(aspect.getEliminationRules(), language.getNamespace());
      bind(aspect.getVariableConverters(), language.getNamespace());
      bind(aspect.getOverloadedOperationsTypesProviders(), language.getNamespace());
    }
  }
  private void register11(BrowserPlatform platform) {
    LanguageRuntime language = new bronspraak.Language();
    ModuleClassLoader loader = FinancialClassLoader.create(language.getIdentity().getSourceModuleReference());
    loaders.add(loader);
    platform.registerLanguage(language, loader, Collections.emptyList(), Collections.emptyList(), new Class<?>[]{
      bronspraak.typesystem.OnlyOneMetatagConfiguratie_NonTypesystemRule.class,
      bronspraak.typesystem.TypesystemDescriptor.class,
      bronspraak.typesystem.check_BronVerwijzing_NonTypesystemRule.class,
      bronspraak.typesystem.check_Metatag_NonTypesystemRule.class
    });
    IHelginsDescriptor aspect = language.getAspect(IHelginsDescriptor.class);
    if (aspect != null) {
      bind(aspect.getInferenceRules(), language.getNamespace());
      bind(aspect.getNonTypesystemRules(), language.getNamespace());
      bind(aspect.getSubtypingRules(), language.getNamespace());
      bind(aspect.getSubstituteTypeRules(), language.getNamespace());
      bind(aspect.getComparisonRules(), language.getNamespace());
      bind(aspect.getEliminationRules(), language.getNamespace());
      bind(aspect.getVariableConverters(), language.getNamespace());
      bind(aspect.getOverloadedOperationsTypesProviders(), language.getNamespace());
    }
  }
  private void register12(BrowserPlatform platform) {
    LanguageRuntime language = new contexts.Language();
    ModuleClassLoader loader = FinancialClassLoader.create(language.getIdentity().getSourceModuleReference());
    loaders.add(loader);
    loader.registerFactory("contexts.typesystem.DeleteLeegCommentaar_QuickFix", contexts.typesystem.DeleteLeegCommentaar_QuickFix.class, new Class<?>[0], args -> new contexts.typesystem.DeleteLeegCommentaar_QuickFix());
    loader.registerFactory("contexts.typesystem.MaakZichtbaar_QuickFix", contexts.typesystem.MaakZichtbaar_QuickFix.class, new Class<?>[0], args -> new contexts.typesystem.MaakZichtbaar_QuickFix());
    loader.registerFactory("contexts.typesystem.WitruimteInContext_QuickFix", contexts.typesystem.WitruimteInContext_QuickFix.class, new Class<?>[0], args -> new contexts.typesystem.WitruimteInContext_QuickFix());
    platform.registerLanguage(language, loader, Collections.emptyList(), Collections.emptyList(), new Class<?>[]{
      contexts.typesystem.DeleteLeegCommentaar_QuickFix.class,
      contexts.typesystem.MaakZichtbaar_QuickFix.class,
      contexts.typesystem.TypesystemDescriptor.class,
      contexts.typesystem.WitruimteInContext_QuickFix.class,
      contexts.typesystem.check_CommentAttribute_NonTypesystemRule.class,
      contexts.typesystem.check_Commentaar_NonTypesystemRule.class,
      contexts.typesystem.check_ContextInhoud_NonTypesystemRule.class,
      contexts.typesystem.check_Context_NonTypesystemRule.class,
      contexts.typesystem.check_Imports_NonTypesystemRule.class
    });
    IHelginsDescriptor aspect = language.getAspect(IHelginsDescriptor.class);
    if (aspect != null) {
      bind(aspect.getInferenceRules(), language.getNamespace());
      bind(aspect.getNonTypesystemRules(), language.getNamespace());
      bind(aspect.getSubtypingRules(), language.getNamespace());
      bind(aspect.getSubstituteTypeRules(), language.getNamespace());
      bind(aspect.getComparisonRules(), language.getNamespace());
      bind(aspect.getEliminationRules(), language.getNamespace());
      bind(aspect.getVariableConverters(), language.getNamespace());
      bind(aspect.getOverloadedOperationsTypesProviders(), language.getNamespace());
    }
  }
  private void register13(BrowserPlatform platform) {
    LanguageRuntime language = new linguistics.Language();
    ModuleClassLoader loader = FinancialClassLoader.create(language.getIdentity().getSourceModuleReference());
    loaders.add(loader);
    loader.registerFactory("linguistics.typesystem.FlattenSequenceWithinSequence_QuickFix", linguistics.typesystem.FlattenSequenceWithinSequence_QuickFix.class, new Class<?>[0], args -> new linguistics.typesystem.FlattenSequenceWithinSequence_QuickFix());
    platform.registerLanguage(language, loader, Collections.emptyList(), Collections.emptyList(), new Class<?>[]{
      linguistics.typesystem.FlattenSequenceWithinSequence_QuickFix.class,
      linguistics.typesystem.TypesystemDescriptor.class,
      linguistics.typesystem.check_AbstractAuxPattern_NonTypesystemRule.class,
      linguistics.typesystem.check_ModelAccessGuard_NonTypesystemRule.class,
      linguistics.typesystem.check_Noun_NonTypesystemRule.class,
      linguistics.typesystem.check_OnePattern_NonTypesystemRule.class,
      linguistics.typesystem.check_PatternCompleteness_NonTypesystemRule.class,
      linguistics.typesystem.check_Sequence_NonTypesystemRule.class,
      linguistics.typesystem.check_abstractOverrides_NonTypesystemRule.class,
      linguistics.typesystem.typeof_GuardGetterConceptFunction_InferenceRule.class
    });
    IHelginsDescriptor aspect = language.getAspect(IHelginsDescriptor.class);
    if (aspect != null) {
      bind(aspect.getInferenceRules(), language.getNamespace());
      bind(aspect.getNonTypesystemRules(), language.getNamespace());
      bind(aspect.getSubtypingRules(), language.getNamespace());
      bind(aspect.getSubstituteTypeRules(), language.getNamespace());
      bind(aspect.getComparisonRules(), language.getNamespace());
      bind(aspect.getEliminationRules(), language.getNamespace());
      bind(aspect.getVariableConverters(), language.getNamespace());
      bind(aspect.getOverloadedOperationsTypesProviders(), language.getNamespace());
    }
  }
  private void register14(BrowserPlatform platform) {
    LanguageRuntime language = new rapporten.Language();
    ModuleClassLoader loader = FinancialClassLoader.create(language.getIdentity().getSourceModuleReference());
    loaders.add(loader);
    platform.registerLanguage(language, loader, Collections.emptyList(), Collections.emptyList(), new Class<?>[]{

    });
    IHelginsDescriptor aspect = language.getAspect(IHelginsDescriptor.class);
    if (aspect != null) {
      bind(aspect.getInferenceRules(), language.getNamespace());
      bind(aspect.getNonTypesystemRules(), language.getNamespace());
      bind(aspect.getSubtypingRules(), language.getNamespace());
      bind(aspect.getSubstituteTypeRules(), language.getNamespace());
      bind(aspect.getComparisonRules(), language.getNamespace());
      bind(aspect.getEliminationRules(), language.getNamespace());
      bind(aspect.getVariableConverters(), language.getNamespace());
      bind(aspect.getOverloadedOperationsTypesProviders(), language.getNamespace());
    }
  }
  private void register15(BrowserPlatform platform) {
    LanguageRuntime language = new interpreter.debug.Language();
    ModuleClassLoader loader = FinancialClassLoader.create(language.getIdentity().getSourceModuleReference());
    loaders.add(loader);
    platform.registerLanguage(language, loader, Collections.emptyList(), Collections.emptyList(), new Class<?>[]{

    });
    IHelginsDescriptor aspect = language.getAspect(IHelginsDescriptor.class);
    if (aspect != null) {
      bind(aspect.getInferenceRules(), language.getNamespace());
      bind(aspect.getNonTypesystemRules(), language.getNamespace());
      bind(aspect.getSubtypingRules(), language.getNamespace());
      bind(aspect.getSubstituteTypeRules(), language.getNamespace());
      bind(aspect.getComparisonRules(), language.getNamespace());
      bind(aspect.getEliminationRules(), language.getNamespace());
      bind(aspect.getVariableConverters(), language.getNamespace());
      bind(aspect.getOverloadedOperationsTypesProviders(), language.getNamespace());
    }
  }
  private void register16(BrowserPlatform platform) {
    LanguageRuntime language = new gegevensspraak.Language();
    ModuleClassLoader loader = FinancialClassLoader.create(language.getIdentity().getSourceModuleReference());
    loaders.add(loader);
    loader.registerFactory("gegevensspraak.typesystem.MergeNodes_QuickFix", gegevensspraak.typesystem.MergeNodes_QuickFix.class, new Class<?>[0], args -> new gegevensspraak.typesystem.MergeNodes_QuickFix());
    loader.registerFactory("gegevensspraak.typesystem.PasRollenZonodigAanOpWederkerigheid_QuickFix", gegevensspraak.typesystem.PasRollenZonodigAanOpWederkerigheid_QuickFix.class, new Class<?>[0], args -> new gegevensspraak.typesystem.PasRollenZonodigAanOpWederkerigheid_QuickFix());
    loader.registerFactory("gegevensspraak.typesystem.Remove_deleted_child_QuickFix", gegevensspraak.typesystem.Remove_deleted_child_QuickFix.class, new Class<?>[0], args -> new gegevensspraak.typesystem.Remove_deleted_child_QuickFix());
    loader.registerFactory("gegevensspraak.typesystem.Remove_deleted_property_QuickFix", gegevensspraak.typesystem.Remove_deleted_property_QuickFix.class, new Class<?>[0], args -> new gegevensspraak.typesystem.Remove_deleted_property_QuickFix());
    loader.registerFactory("gegevensspraak.typesystem.Remove_deleted_reference_QuickFix", gegevensspraak.typesystem.Remove_deleted_reference_QuickFix.class, new Class<?>[0], args -> new gegevensspraak.typesystem.Remove_deleted_reference_QuickFix());
    loader.registerFactory("gegevensspraak.typesystem.SetEenheid_QuickFix", gegevensspraak.typesystem.SetEenheid_QuickFix.class, new Class<?>[0], args -> new gegevensspraak.typesystem.SetEenheid_QuickFix());
    loader.registerFactory("gegevensspraak.typesystem.VerwijderLegeEenheid_QuickFix", gegevensspraak.typesystem.VerwijderLegeEenheid_QuickFix.class, new Class<?>[0], args -> new gegevensspraak.typesystem.VerwijderLegeEenheid_QuickFix());
    loader.registerFactory("gegevensspraak.typesystem.ZetDatumGranulariteitVoorGeldigheidsperiode_QuickFix", gegevensspraak.typesystem.ZetDatumGranulariteitVoorGeldigheidsperiode_QuickFix.class, new Class<?>[0], args -> new gegevensspraak.typesystem.ZetDatumGranulariteitVoorGeldigheidsperiode_QuickFix());
    platform.registerLanguage(language, loader, Collections.emptyList(), Collections.emptyList(), new Class<?>[]{
      gegevensspraak.typesystem.CompareDates_ComparisonRule.class,
      gegevensspraak.typesystem.DatumTijdType.class,
      gegevensspraak.typesystem.MergeNodes_QuickFix.class,
      gegevensspraak.typesystem.PasRollenZonodigAanOpWederkerigheid_QuickFix.class,
      gegevensspraak.typesystem.Remove_deleted_child_QuickFix.class,
      gegevensspraak.typesystem.Remove_deleted_property_QuickFix.class,
      gegevensspraak.typesystem.Remove_deleted_reference_QuickFix.class,
      gegevensspraak.typesystem.SetEenheid_QuickFix.class,
      gegevensspraak.typesystem.TypesystemDescriptor.class,
      gegevensspraak.typesystem.VerwijderLegeEenheid_QuickFix.class,
      gegevensspraak.typesystem.ZetDatumGranulariteitVoorGeldigheidsperiode_QuickFix.class,
      gegevensspraak.typesystem.check_AbstractNumeriekType_NonTypesystemRule.class,
      gegevensspraak.typesystem.check_AbstractNumeriekeLiteral_NonTypesystemRule.class,
      gegevensspraak.typesystem.check_Concatenatie_NonTypesystemRule.class,
      gegevensspraak.typesystem.check_DatumTijdLiteral_NonTypesystemRule.class,
      gegevensspraak.typesystem.check_DatumTijdType_NonTypesystemRule.class,
      gegevensspraak.typesystem.check_DimensieFilter_NonTypesystemRule.class,
      gegevensspraak.typesystem.check_Domein_NonTypesystemRule.class,
      gegevensspraak.typesystem.check_EenheidConversie_NonTypesystemRule.class,
      gegevensspraak.typesystem.check_EenheidMacht_NonTypesystemRule.class,
      gegevensspraak.typesystem.check_EenheidSysteem_NonTypesystemRule.class,
      gegevensspraak.typesystem.check_EnumWaardeRef_NonTypesystemRule.class,
      gegevensspraak.typesystem.check_EnumeratieType_NonTypesystemRule.class,
      gegevensspraak.typesystem.check_FeitType_Naam_Ingevuld_NonTypesystemRule.class,
      gegevensspraak.typesystem.check_FeitType_NonTypesystemRule.class,
      gegevensspraak.typesystem.check_Geldigheidsperiode_NonTypesystemRule.class,
      gegevensspraak.typesystem.check_IDimensieSelectie_NonTypesystemRule.class,
      gegevensspraak.typesystem.check_LabelFilterInterval_NonTypesystemRule.class,
      gegevensspraak.typesystem.check_LabelFilterLijst_NonTypesystemRule.class,
      gegevensspraak.typesystem.check_Naamwoord_NonTypesystemRule.class,
      gegevensspraak.typesystem.check_NumeriekType_NonTypesystemRule.class,
      gegevensspraak.typesystem.check_NumeriekeLiteral_NonTypesystemRule.class,
      gegevensspraak.typesystem.check_ObjectExtensie_NonTypesystemRule.class,
      gegevensspraak.typesystem.check_ObjectModel_NonTypesystemRule.class,
      gegevensspraak.typesystem.check_ObjectReference_NonTypesystemRule.class,
      gegevensspraak.typesystem.check_ObjectType_NonTypesystemRule.class,
      gegevensspraak.typesystem.check_Parameterset_NonTypesystemRule.class,
      gegevensspraak.typesystem.check_Parametertoekenning_NonTypesystemRule.class,
      gegevensspraak.typesystem.check_PercentageLiteral_NonTypesystemRule.class,
      gegevensspraak.typesystem.check_RolRefs_Gevuld_NonTypesystemRule.class,
      gegevensspraak.typesystem.check_Typed_NonTypesystemRule.class,
      gegevensspraak.typesystem.check_deleted_features_NonTypesystemRule.class,
      gegevensspraak.typesystem.check_dubbeleDagsoortNamen_NonTypesystemRule.class,
      gegevensspraak.typesystem.replace_NumeriekType_InequationReplacementRule.class,
      gegevensspraak.typesystem.replace_PercentageType_InequationReplacementRule.class,
      gegevensspraak.typesystem.substituteType_DomeinType_SubstituteTypeRule.class,
      gegevensspraak.typesystem.supertypeof_Domein_SubtypingRule.class,
      gegevensspraak.typesystem.typeof_BooleanType_InferenceRule.class,
      gegevensspraak.typesystem.typeof_DatumTijdType_InferenceRule.class,
      gegevensspraak.typesystem.typeof_Domein_InferenceRule.class,
      gegevensspraak.typesystem.typeof_ObjectTypeRef_InferenceRule.class,
      gegevensspraak.typesystem.typeof_ObjectType_InferenceRule.class,
      gegevensspraak.typesystem.typeof_Parameter_InferenceRule.class,
      gegevensspraak.typesystem.typeof_Parametertoekenning_InferenceRule.class,
      gegevensspraak.typesystem.typeof_TekstType_InferenceRule.class,
      gegevensspraak.typesystem.typeof_Typed_InferenceRule.class
    });
    IHelginsDescriptor aspect = language.getAspect(IHelginsDescriptor.class);
    if (aspect != null) {
      bind(aspect.getInferenceRules(), language.getNamespace());
      bind(aspect.getNonTypesystemRules(), language.getNamespace());
      bind(aspect.getSubtypingRules(), language.getNamespace());
      bind(aspect.getSubstituteTypeRules(), language.getNamespace());
      bind(aspect.getComparisonRules(), language.getNamespace());
      bind(aspect.getEliminationRules(), language.getNamespace());
      bind(aspect.getVariableConverters(), language.getNamespace());
      bind(aspect.getOverloadedOperationsTypesProviders(), language.getNamespace());
    }
    com.mbeddr.mpsutil.grammarcells.runtime.DescriptorCache.getInstance().register(language.getIdentity().getSourceModuleReference(), new gegevensspraak.editor.GrammarActionsDescriptor());
  }
  private void register17(BrowserPlatform platform) {
    LanguageRuntime language = new regelspraak.Language();
    ModuleClassLoader loader = FinancialClassLoader.create(language.getIdentity().getSourceModuleReference());
    loaders.add(loader);
    loader.registerFactory("regelspraak.typesystem.ConvertTekstLiteralNaarTekstExpressie_QuickFix", regelspraak.typesystem.ConvertTekstLiteralNaarTekstExpressie_QuickFix.class, new Class<?>[0], args -> new regelspraak.typesystem.ConvertTekstLiteralNaarTekstExpressie_QuickFix());
    loader.registerFactory("regelspraak.typesystem.FixGenesteBegrensdeExpressie_QuickFix", regelspraak.typesystem.FixGenesteBegrensdeExpressie_QuickFix.class, new Class<?>[0], args -> new regelspraak.typesystem.FixGenesteBegrensdeExpressie_QuickFix());
    loader.registerFactory("regelspraak.typesystem.MaakAfTeRondenEenheidExpliciet_QuickFix", regelspraak.typesystem.MaakAfTeRondenEenheidExpliciet_QuickFix.class, new Class<?>[0], args -> new regelspraak.typesystem.MaakAfTeRondenEenheidExpliciet_QuickFix());
    loader.registerFactory("regelspraak.typesystem.MaakRegelgroepVoorwaardeOnafhankelijk_QuickFix", regelspraak.typesystem.MaakRegelgroepVoorwaardeOnafhankelijk_QuickFix.class, new Class<?>[0], args -> new regelspraak.typesystem.MaakRegelgroepVoorwaardeOnafhankelijk_QuickFix());
    loader.registerFactory("regelspraak.typesystem.NeemAdditieveExpressieOpInSommatie_QuickFix", regelspraak.typesystem.NeemAdditieveExpressieOpInSommatie_QuickFix.class, new Class<?>[0], args -> new regelspraak.typesystem.NeemAdditieveExpressieOpInSommatie_QuickFix());
    loader.registerFactory("regelspraak.typesystem.RondTijdsduurAf_QuickFix", regelspraak.typesystem.RondTijdsduurAf_QuickFix.class, new Class<?>[0], args -> new regelspraak.typesystem.RondTijdsduurAf_QuickFix());
    loader.registerFactory("regelspraak.typesystem.StripAttribuutVoorAantalAggregatie_QuickFix", regelspraak.typesystem.StripAttribuutVoorAantalAggregatie_QuickFix.class, new Class<?>[0], args -> new regelspraak.typesystem.StripAttribuutVoorAantalAggregatie_QuickFix());
    loader.registerFactory("regelspraak.typesystem.VergelijkingMetLeeg_QuickFix", regelspraak.typesystem.VergelijkingMetLeeg_QuickFix.class, new Class<?>[0], args -> new regelspraak.typesystem.VergelijkingMetLeeg_QuickFix());
    loader.registerFactory("regelspraak.typesystem.VervangRolDoorObjectType_QuickFix", regelspraak.typesystem.VervangRolDoorObjectType_QuickFix.class, new Class<?>[0], args -> new regelspraak.typesystem.VervangRolDoorObjectType_QuickFix());
    loader.registerFactory("regelspraak.typesystem.VervangSelectieDoorOnderwerpRef_QuickFix", regelspraak.typesystem.VervangSelectieDoorOnderwerpRef_QuickFix.class, new Class<?>[0], args -> new regelspraak.typesystem.VervangSelectieDoorOnderwerpRef_QuickFix());
    loader.registerFactory("regelspraak.typesystem.VerwijderOverbodigeConditie_QuickFix", regelspraak.typesystem.VerwijderOverbodigeConditie_QuickFix.class, new Class<?>[0], args -> new regelspraak.typesystem.VerwijderOverbodigeConditie_QuickFix());
    loader.registerFactory("regelspraak.typesystem.ZetHaakjesBinnenAfronding_QuickFix", regelspraak.typesystem.ZetHaakjesBinnenAfronding_QuickFix.class, new Class<?>[0], args -> new regelspraak.typesystem.ZetHaakjesBinnenAfronding_QuickFix());
    loader.registerFactory("regelspraak.typesystem.ZetHaakjesOmAfronding_QuickFix", regelspraak.typesystem.ZetHaakjesOmAfronding_QuickFix.class, new Class<?>[0], args -> new regelspraak.typesystem.ZetHaakjesOmAfronding_QuickFix());
    loader.registerFactory("regelspraak.typesystem.ZetOmInEnkeleVoorwaarde_QuickFix", regelspraak.typesystem.ZetOmInEnkeleVoorwaarde_QuickFix.class, new Class<?>[0], args -> new regelspraak.typesystem.ZetOmInEnkeleVoorwaarde_QuickFix());
    loader.registerFactory("regelspraak.typesystem.decimalenAfronden_QuickFix", regelspraak.typesystem.decimalenAfronden_QuickFix.class, new Class<?>[0], args -> new regelspraak.typesystem.decimalenAfronden_QuickFix());
    loader.registerFactory("regelspraak.typesystem.fix_EersteLaatste_QuickFix", regelspraak.typesystem.fix_EersteLaatste_QuickFix.class, new Class<?>[0], args -> new regelspraak.typesystem.fix_EersteLaatste_QuickFix());
    loader.registerFactory("regelspraak.typesystem.fix_Subselectie_QuickFix", regelspraak.typesystem.fix_Subselectie_QuickFix.class, new Class<?>[0], args -> new regelspraak.typesystem.fix_Subselectie_QuickFix());
    loader.registerFactory("regelspraak.typesystem.fix_VoegHaakjesToe_QuickFix", regelspraak.typesystem.fix_VoegHaakjesToe_QuickFix.class, new Class<?>[0], args -> new regelspraak.typesystem.fix_VoegHaakjesToe_QuickFix());
    platform.registerLanguage(language, loader, Collections.emptyList(), Collections.emptyList(), new Class<?>[]{
      regelspraak.typesystem.ConvertTekstLiteralNaarTekstExpressie_QuickFix.class,
      regelspraak.typesystem.FixGenesteBegrensdeExpressie_QuickFix.class,
      regelspraak.typesystem.GeenTekstLiteralsInEenRegelVersie_NonTypesystemRule.class,
      regelspraak.typesystem.MaakAfTeRondenEenheidExpliciet_QuickFix.class,
      regelspraak.typesystem.MaakRegelgroepVoorwaardeOnafhankelijk_QuickFix.class,
      regelspraak.typesystem.NeemAdditieveExpressieOpInSommatie_QuickFix.class,
      regelspraak.typesystem.RondTijdsduurAf_QuickFix.class,
      regelspraak.typesystem.StripAttribuutVoorAantalAggregatie_QuickFix.class,
      regelspraak.typesystem.ToekenningChecker.class,
      regelspraak.typesystem.TypesystemDescriptor.class,
      regelspraak.typesystem.VergelijkingChecker.class,
      regelspraak.typesystem.VergelijkingMetLeeg_QuickFix.class,
      regelspraak.typesystem.VervangRolDoorObjectType_QuickFix.class,
      regelspraak.typesystem.VervangSelectieDoorOnderwerpRef_QuickFix.class,
      regelspraak.typesystem.VerwijderOverbodigeConditie_QuickFix.class,
      regelspraak.typesystem.ZetHaakjesBinnenAfronding_QuickFix.class,
      regelspraak.typesystem.ZetHaakjesOmAfronding_QuickFix.class,
      regelspraak.typesystem.ZetOmInEnkeleVoorwaarde_QuickFix.class,
      regelspraak.typesystem.check_AantalQuantificatie_NonTypesystemRule.class,
      regelspraak.typesystem.check_AbsoluteWaarde_NonTypesystemRule.class,
      regelspraak.typesystem.check_AbstractRegel_NonTypesystemRule.class,
      regelspraak.typesystem.check_AbstracteRegelVersie_NonTypesystemRule.class,
      regelspraak.typesystem.check_ActieIndienVoorwaarde_NonTypesystemRule.class,
      regelspraak.typesystem.check_AdditieveExpressie_NonTypesystemRule.class,
      regelspraak.typesystem.check_Afronden_NonTypesystemRule.class,
      regelspraak.typesystem.check_Aggregatie_NonTypesystemRule.class,
      regelspraak.typesystem.check_AlleOnderwerp_NonTypesystemRule.class,
      regelspraak.typesystem.check_ArithmetischeExpressie_NonTypesystemRule.class,
      regelspraak.typesystem.check_AttribuutSelector_NonTypesystemRule.class,
      regelspraak.typesystem.check_BegrensdeExpressie_NonTypesystemRule.class,
      regelspraak.typesystem.check_Combinatie_NonTypesystemRule.class,
      regelspraak.typesystem.check_ConsistentieRegel_NonTypesystemRule.class,
      regelspraak.typesystem.check_Constructie_NonTypesystemRule.class,
      regelspraak.typesystem.check_DagsoortDefinitie_NonTypesystemRule.class,
      regelspraak.typesystem.check_DatumAanvulling_NonTypesystemRule.class,
      regelspraak.typesystem.check_DatumElementUit_NonTypesystemRule.class,
      regelspraak.typesystem.check_DatumMetJaarMaandDagEnTijd_NonTypesystemRule.class,
      regelspraak.typesystem.check_DatumMetJaarMaandEnDag_NonTypesystemRule.class,
      regelspraak.typesystem.check_DatumVerschil_NonTypesystemRule.class,
      regelspraak.typesystem.check_DimAttribuutSelector_NonTypesystemRule.class,
      regelspraak.typesystem.check_DimensieAggregatie_NonTypesystemRule.class,
      regelspraak.typesystem.check_DubbeleRegelNamen_NonTypesystemRule.class,
      regelspraak.typesystem.check_EerstePaasdag_NonTypesystemRule.class,
      regelspraak.typesystem.check_ElfProef_NonTypesystemRule.class,
      regelspraak.typesystem.check_EnkeleExpressie_NonTypesystemRule.class,
      regelspraak.typesystem.check_EnkeleVoorwaarde_NonTypesystemRule.class,
      regelspraak.typesystem.check_FeitCreatie_NonTypesystemRule.class,
      regelspraak.typesystem.check_Gelijkstelling_NonTypesystemRule.class,
      regelspraak.typesystem.check_Haakjes_NonTypesystemRule.class,
      regelspraak.typesystem.check_IAfrondenNodig_NonTypesystemRule.class,
      regelspraak.typesystem.check_IsDagsoort_NonTypesystemRule.class,
      regelspraak.typesystem.check_IsNumeriek_NonTypesystemRule.class,
      regelspraak.typesystem.check_MeervoudigeSelectie_NonTypesystemRule.class,
      regelspraak.typesystem.check_OnderwerpRef_NonTypesystemRule.class,
      regelspraak.typesystem.check_OverbodigeSelectie_NonTypesystemRule.class,
      regelspraak.typesystem.check_PercentageVanExpressie_NonTypesystemRule.class,
      regelspraak.typesystem.check_RegelConditie_NonTypesystemRule.class,
      regelspraak.typesystem.check_RegelCycles_NonTypesystemRule.class,
      regelspraak.typesystem.check_Regel_NonTypesystemRule.class,
      regelspraak.typesystem.check_RegelgroepBundel_NonTypesystemRule.class,
      regelspraak.typesystem.check_RegelgroepConditie_NonTypesystemRule.class,
      regelspraak.typesystem.check_RolOfKenmerkCheck_NonTypesystemRule.class,
      regelspraak.typesystem.check_SamengesteldPredicaat_NonTypesystemRule.class,
      regelspraak.typesystem.check_Selectie_NonTypesystemRule.class,
      regelspraak.typesystem.check_SorteerCriterium_NonTypesystemRule.class,
      regelspraak.typesystem.check_Statement_NonTypesystemRule.class,
      regelspraak.typesystem.check_SubSelectie_NonTypesystemRule.class,
      regelspraak.typesystem.check_Subconditie_NonTypesystemRule.class,
      regelspraak.typesystem.check_Uniciteit_NonTypesystemRule.class,
      regelspraak.typesystem.check_UnivOnderwerp_NonTypesystemRule.class,
      regelspraak.typesystem.check_Verdeling_NonTypesystemRule.class,
      regelspraak.typesystem.check_Vergelijking_NonTypesystemRule.class,
      regelspraak.typesystem.check_VerminderdMet_NonTypesystemRule.class,
      regelspraak.typesystem.check_Worteltrekken_NonTypesystemRule.class,
      regelspraak.typesystem.decimalenAfronden_QuickFix.class,
      regelspraak.typesystem.fix_EersteLaatste_QuickFix.class,
      regelspraak.typesystem.fix_Subselectie_QuickFix.class,
      regelspraak.typesystem.fix_VoegHaakjesToe_QuickFix.class,
      regelspraak.typesystem.typeof_EigenschapInitialisatie_InferenceRule.class,
      regelspraak.typesystem.typeof_Gelijkstelling_InferenceRule.class,
      regelspraak.typesystem.typeof_KenmerkToekenning_InferenceRule.class,
      regelspraak.typesystem.typeof_Term_InferenceRule.class,
      regelspraak.typesystem.typeof_Vergelijking_InferenceRule.class
    });
    IHelginsDescriptor aspect = language.getAspect(IHelginsDescriptor.class);
    if (aspect != null) {
      bind(aspect.getInferenceRules(), language.getNamespace());
      bind(aspect.getNonTypesystemRules(), language.getNamespace());
      bind(aspect.getSubtypingRules(), language.getNamespace());
      bind(aspect.getSubstituteTypeRules(), language.getNamespace());
      bind(aspect.getComparisonRules(), language.getNamespace());
      bind(aspect.getEliminationRules(), language.getNamespace());
      bind(aspect.getVariableConverters(), language.getNamespace());
      bind(aspect.getOverloadedOperationsTypesProviders(), language.getNamespace());
    }
    com.mbeddr.mpsutil.grammarcells.runtime.DescriptorCache.getInstance().register(language.getIdentity().getSourceModuleReference(), new regelspraak.editor.GrammarActionsDescriptor());
  }
  private void register18(BrowserPlatform platform) {
    LanguageRuntime language = new servicespraak.Language();
    ModuleClassLoader loader = FinancialClassLoader.create(language.getIdentity().getSourceModuleReference());
    loaders.add(loader);
    loader.registerFactory("servicespraak.typesystem.DatatypeMappingNoodzakelijk_QuickFix", servicespraak.typesystem.DatatypeMappingNoodzakelijk_QuickFix.class, new Class<?>[0], args -> new servicespraak.typesystem.DatatypeMappingNoodzakelijk_QuickFix());
    loader.registerFactory("servicespraak.typesystem.VerplaatsConversie_QuickFix", servicespraak.typesystem.VerplaatsConversie_QuickFix.class, new Class<?>[0], args -> new servicespraak.typesystem.VerplaatsConversie_QuickFix());
    platform.registerLanguage(language, loader, Collections.emptyList(), Collections.emptyList(), new Class<?>[]{
      servicespraak.typesystem.DatatypeMappingNoodzakelijk_QuickFix.class,
      servicespraak.typesystem.TypesystemDescriptor.class,
      servicespraak.typesystem.VerplaatsConversie_QuickFix.class,
      servicespraak.typesystem.check_AfrondingConversie_NonTypesystemRule.class,
      servicespraak.typesystem.check_BerichtType_NonTypesystemRule.class,
      servicespraak.typesystem.check_BerichtVeld_NonTypesystemRule.class,
      servicespraak.typesystem.check_Choice_NonTypesystemRule.class,
      servicespraak.typesystem.check_ComplexBerichtVeld_NonTypesystemRule.class,
      servicespraak.typesystem.check_DataTypeMapping_NonTypesystemRule.class,
      servicespraak.typesystem.check_DecimalenRestrictie_NonTypesystemRule.class,
      servicespraak.typesystem.check_DirectAttribuut_NonTypesystemRule.class,
      servicespraak.typesystem.check_DirectKenmerk_NonTypesystemRule.class,
      servicespraak.typesystem.check_Entrypoint_NonTypesystemRule.class,
      servicespraak.typesystem.check_EnumWaardeMapping_NonTypesystemRule.class,
      servicespraak.typesystem.check_IDataTypeMapping_NonTypesystemRule.class,
      servicespraak.typesystem.check_InvoerBerichtVeld_NonTypesystemRule.class,
      servicespraak.typesystem.check_LengteRestrictie_NonTypesystemRule.class,
      servicespraak.typesystem.check_MappedDataType_NonTypesystemRule.class,
      servicespraak.typesystem.check_NumeriekeWaardeRestrictie_NonTypesystemRule.class,
      servicespraak.typesystem.check_RestrictedDatatype_NonTypesystemRule.class,
      servicespraak.typesystem.check_ServiceTekstDeelAttribuut_NonTypesystemRule.class,
      servicespraak.typesystem.check_ServiceTekstDeelTekst_NonTypesystemRule.class,
      servicespraak.typesystem.check_Service_NonTypesystemRule.class,
      servicespraak.typesystem.check_TekstSpecifiekInvoerVeld_NonTypesystemRule.class,
      servicespraak.typesystem.check_TekstspecifiekVelddeel_NonTypesystemRule.class,
      servicespraak.typesystem.check_Xsd_NonTypesystemRule.class,
      servicespraak.typesystem.check_generatedXsd_NonTypesystemRule.class,
      servicespraak.typesystem.typeof_DirectInvoerAttribuut_InferenceRule.class
    });
    IHelginsDescriptor aspect = language.getAspect(IHelginsDescriptor.class);
    if (aspect != null) {
      bind(aspect.getInferenceRules(), language.getNamespace());
      bind(aspect.getNonTypesystemRules(), language.getNamespace());
      bind(aspect.getSubtypingRules(), language.getNamespace());
      bind(aspect.getSubstituteTypeRules(), language.getNamespace());
      bind(aspect.getComparisonRules(), language.getNamespace());
      bind(aspect.getEliminationRules(), language.getNamespace());
      bind(aspect.getVariableConverters(), language.getNamespace());
      bind(aspect.getOverloadedOperationsTypesProviders(), language.getNamespace());
    }
  }
  private void register19(BrowserPlatform platform) {
    LanguageRuntime language = new testspraak.Language();
    ModuleClassLoader loader = FinancialClassLoader.create(language.getIdentity().getSourceModuleReference());
    loaders.add(loader);
    loader.registerFactory("testspraak.typesystem.VoegInvoerToe_QuickFix", testspraak.typesystem.VoegInvoerToe_QuickFix.class, new Class<?>[0], args -> new testspraak.typesystem.VoegInvoerToe_QuickFix());
    loader.registerFactory("testspraak.typesystem.VoegParameterToe_QuickFix", testspraak.typesystem.VoegParameterToe_QuickFix.class, new Class<?>[0], args -> new testspraak.typesystem.VoegParameterToe_QuickFix());
    loader.registerFactory("testspraak.typesystem.VoegUitvoervoorspellingToe_QuickFix", testspraak.typesystem.VoegUitvoervoorspellingToe_QuickFix.class, new Class<?>[0], args -> new testspraak.typesystem.VoegUitvoervoorspellingToe_QuickFix());
    loader.registerFactory("testspraak.typesystem.VulOnbrekendeRekendatumAan_QuickFix", testspraak.typesystem.VulOnbrekendeRekendatumAan_QuickFix.class, new Class<?>[0], args -> new testspraak.typesystem.VulOnbrekendeRekendatumAan_QuickFix());
    loader.registerFactory("testspraak.typesystem.fix_DatumTijdGranulariteit_QuickFix", testspraak.typesystem.fix_DatumTijdGranulariteit_QuickFix.class, new Class<?>[0], args -> new testspraak.typesystem.fix_DatumTijdGranulariteit_QuickFix());
    loader.registerFactory("testspraak.typesystem.fix_NoRegex_QuickFix", testspraak.typesystem.fix_NoRegex_QuickFix.class, new Class<?>[0], args -> new testspraak.typesystem.fix_NoRegex_QuickFix());
    platform.registerLanguage(language, loader, Collections.emptyList(), Collections.emptyList(), new Class<?>[]{
      testspraak.typesystem.IH_SPECIFIEK_check_partner_consistency_IH_SPECIFIEK_NonTypesystemRule.class,
      testspraak.typesystem.TypesystemDescriptor.class,
      testspraak.typesystem.UitvoerVoorspellingChecker.class,
      testspraak.typesystem.VoegInvoerToe_QuickFix.class,
      testspraak.typesystem.VoegParameterToe_QuickFix.class,
      testspraak.typesystem.VoegUitvoervoorspellingToe_QuickFix.class,
      testspraak.typesystem.VulOnbrekendeRekendatumAan_QuickFix.class,
      testspraak.typesystem.check_ComplexTestBerichtVeld_NonTypesystemRule.class,
      testspraak.typesystem.check_EigenschapToekenning_NonTypesystemRule.class,
      testspraak.typesystem.check_ElementaireVeldVerwachting_NonTypesystemRule.class,
      testspraak.typesystem.check_IComplexTestBericht_NonTypesystemRule.class,
      testspraak.typesystem.check_IElementairVeld_NonTypesystemRule.class,
      testspraak.typesystem.check_ITestSet_NonTypesystemRule.class,
      testspraak.typesystem.check_InstantieResultaat_NonTypesystemRule.class,
      testspraak.typesystem.check_Instantie_NonTypesystemRule.class,
      testspraak.typesystem.check_ServiceUitvoerTest_NonTypesystemRule.class,
      testspraak.typesystem.check_TeTestenEigenschapRegels_NonTypesystemRule.class,
      testspraak.typesystem.check_TeTestenRegelGroepEigenschap_NonTypesystemRule.class,
      testspraak.typesystem.check_TestGeval_NonTypesystemRule.class,
      testspraak.typesystem.check_TestInvoerBericht_NonTypesystemRule.class,
      testspraak.typesystem.check_TestSet_NonTypesystemRule.class,
      testspraak.typesystem.check_UitvoerVoorspelling_NonTypesystemRule.class,
      testspraak.typesystem.check_VerwachtFoutAttribute_NonTypesystemRule.class,
      testspraak.typesystem.check_WaardeUitvoervoorspelling_NonTypesystemRule.class,
      testspraak.typesystem.fix_DatumTijdGranulariteit_QuickFix.class,
      testspraak.typesystem.fix_NoRegex_QuickFix.class,
      testspraak.typesystem.parametersZijnVerplicht_NonTypesystemRule.class,
      testspraak.typesystem.typeof_EigenschapToekenning_InferenceRule.class,
      testspraak.typesystem.typeof_UitvoerVoorspelling_InferenceRule.class
    });
    IHelginsDescriptor aspect = language.getAspect(IHelginsDescriptor.class);
    if (aspect != null) {
      bind(aspect.getInferenceRules(), language.getNamespace());
      bind(aspect.getNonTypesystemRules(), language.getNamespace());
      bind(aspect.getSubtypingRules(), language.getNamespace());
      bind(aspect.getSubstituteTypeRules(), language.getNamespace());
      bind(aspect.getComparisonRules(), language.getNamespace());
      bind(aspect.getEliminationRules(), language.getNamespace());
      bind(aspect.getVariableConverters(), language.getNamespace());
      bind(aspect.getOverloadedOperationsTypesProviders(), language.getNamespace());
    }
  }
  private void register20(BrowserPlatform platform) {
    LanguageRuntime language = new xml.schema.Language();
    ModuleClassLoader loader = FinancialClassLoader.create(language.getIdentity().getSourceModuleReference());
    loaders.add(loader);
    platform.registerLanguage(language, loader, Collections.emptyList(), Collections.emptyList(), new Class<?>[]{

    });
    IHelginsDescriptor aspect = language.getAspect(IHelginsDescriptor.class);
    if (aspect != null) {
      bind(aspect.getInferenceRules(), language.getNamespace());
      bind(aspect.getNonTypesystemRules(), language.getNamespace());
      bind(aspect.getSubtypingRules(), language.getNamespace());
      bind(aspect.getSubstituteTypeRules(), language.getNamespace());
      bind(aspect.getComparisonRules(), language.getNamespace());
      bind(aspect.getEliminationRules(), language.getNamespace());
      bind(aspect.getVariableConverters(), language.getNamespace());
      bind(aspect.getOverloadedOperationsTypesProviders(), language.getNamespace());
    }
  }
  private void bind(Iterable<?> values, String namespace) {
    for (Object rule : values) { RuleSet.registerRuleNamespace(rule.getClass(), namespace); rules.add(rule.getClass()); }
  }
  public void close() {
    iconProviders.forEach(IconResource::unregisterProvider); iconProviders.clear();
    rules.forEach(RuleSet::unregisterRuleNamespace); rules.clear();
    for (ModuleClassLoader loader : loaders) loader.dispose(); loaders.clear();
    base.close();
  }
}
