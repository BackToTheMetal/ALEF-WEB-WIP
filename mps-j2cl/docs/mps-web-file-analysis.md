# mps-web file analysis

This document accounts for every file currently under `mps-web` and compares its contents with the same path in MPS commit `d2589ed55bbc9445b660769fcc17da97e8e395a6`. Read the companion [overview and prevention plan](C:/Codex/mps-j2cl/docs/mps-web-overview.md) for the grouped explanation and reduction sequence. Here, “prevent a change” means make a local replacement unnecessary while preserving the required browser behavior.

The current folder contains **116 files: 100 Java replacements and 16 IntelliJ module descriptors**. All 100 Java files replace existing upstream Java files and are explicitly registered in `MPS_OVERRIDES`; none is an added Java-only helper. Nine module descriptors have no exact same-path upstream file; seven customize an existing descriptor. Other browser helpers live outside this folder.

## Baseline and evidence

Snapshot date: **2 October 2026**. The local MPS HEAD matches the pinned commit. All 100 compared original Java files match that commit after line-ending normalization. The wider MPS working tree has existing IntelliJ metadata edits/additions, so it must not be described as an entirely clean checkout. This analysis compares pinned Git blobs, not assumed-clean IDE files, and does not change runtime sources.

The fresh [source audit](C:/Codex/mps-j2cl/out/mps-web-analysis-source-audit.json) reports **36 projects, 2,052 selected Java files, 3,081 named types, 100 replacements and zero errors**. Thus 1,952 of the selected Java files come directly from upstream. The README's 99-replacement count is stale. This is a source audit, not a new build/browser validation; the suggested removals have not been implemented or tested.

The comparison ignores line-ending encoding differences. Added/removed line counts are textual diff statistics, including comments/imports; they are not counts of changed methods or measures of runtime importance. The combined file-path/content digest for this snapshot is `d2f976721191af5981456782e0b7e7c0e3993575a6a2970e2875325dc8229e7e`.

Evidence comes from the actual source diffs, [replacement map and recorded reasons](C:/Codex/mps-j2cl/mps-overrides.bzl), [source selection](C:/Codex/mps-j2cl/mps-rules.bzl), the [browser profile](C:/Codex/mps-j2cl/compatibility/browser-profile.json), and the shared runtime sources. Recorded reasons describe why a replacement was introduced; they are not proof that the original remains unsupported today. Current shared formatting, regex, time and collection support makes some older reasons worth reassessing.

## How a file is replaced

The build selects exactly one provider for each selected upstream path:

```python
"mps-web/" + MPS_OVERRIDES[path] if path in MPS_OVERRIDES else "mps/" + path
```

The whole Java compilation unit is substituted, even where only one line changes. The original is not compiled alongside it. A Java file cannot be retired merely by deleting it: its override mapping, reason and any generator that recreates it must change together. Source selection for omitted desktop classes is a separate mechanism and does not appear as an extra replacement here.

For each entry, **What changes** describes the source delta, **Behavior and limits** explains its effect, and **To make the replacement unnecessary** is an engineering proposal. Small changes include their complete textual diff; larger ones have exact before/after excerpts with source line numbers. The linked full files remain the source of truth. The 16 IDE entries include complete descriptor diffs or, for new descriptors, their complete XML.

## File index

| File | Path under mps-web | Kind | Lines before → after |
| --- | --- | --- | ---: |
| [F001 aspects.iml](C:/Codex/mps-j2cl/docs/mps-web-file-analysis.md#f001-aspectsiml) | `core/aspects/aspects.iml` | IDE addition | 0 → 13 |
| [F002 PredefinedRuleKinds.java](C:/Codex/mps-j2cl/docs/mps-web-file-analysis.md#f002-predefinedrulekindsjava) | `core/aspects/constraints/constraints-runtime/source/jetbrains/mps/core/aspects/constraints/rules/kinds/PredefinedRuleKinds.java` | Java replacement | 70 → 73 |
| [F003 findUsages-runtime2.iml](C:/Codex/mps-j2cl/docs/mps-web-file-analysis.md#f003-findusages-runtime2iml) | `core/findUsages-runtime/findUsages-runtime2.iml` | IDE addition | 0 → 27 |
| [F004 FindUsagesScope.java](C:/Codex/mps-j2cl/docs/mps-web-file-analysis.md#f004-findusagesscopejava) | `core/findUsages-runtime/source_gen/jetbrains/mps/ide/findusages/model/scopes/FindUsagesScope.java` | Java replacement | 89 → 91 |
| [F005 kernel.iml](C:/Codex/mps-j2cl/docs/mps-web-file-analysis.md#f005-kerneliml) | `core/kernel/kernel.iml` | IDE customization | 38 → 35 |
| [F006 kernelSolution.iml](C:/Codex/mps-j2cl/docs/mps-web-file-analysis.md#f006-kernelsolutioniml) | `core/kernel/kernelSolution/kernelSolution.iml` | IDE addition | 0 → 13 |
| [F007 StringsIO.java](C:/Codex/mps-j2cl/docs/mps-web-file-analysis.md#f007-stringsiojava) | `core/kernel/kernelSolution/source_gen/jetbrains/mps/util/StringsIO.java` | Java replacement | 52 → 141 |
| [F008 ClassLoaderManager.java](C:/Codex/mps-j2cl/docs/mps-web-file-analysis.md#f008-classloadermanagerjava) | `core/kernel/source/jetbrains/mps/classloading/ClassLoaderManager.java` | Java replacement | 603 → 603 |
| [F009 IDEADelegatingModuleClassLoader.java](C:/Codex/mps-j2cl/docs/mps-web-file-analysis.md#f009-ideadelegatingmoduleclassloaderjava) | `core/kernel/source/jetbrains/mps/classloading/IDEADelegatingModuleClassLoader.java` | Java replacement | 58 → 57 |
| [F010 ModuleClassLoader.java](C:/Codex/mps-j2cl/docs/mps-web-file-analysis.md#f010-moduleclassloaderjava) | `core/kernel/source/jetbrains/mps/classloading/ModuleClassLoader.java` | Java replacement | 392 → 460 |
| [F011 MPSClassLoadersRegistry.java](C:/Codex/mps-j2cl/docs/mps-web-file-analysis.md#f011-mpsclassloadersregistryjava) | `core/kernel/source/jetbrains/mps/classloading/MPSClassLoadersRegistry.java` | Java replacement | 363 → 361 |
| [F012 MPSModuleClassLoader.java](C:/Codex/mps-j2cl/docs/mps-web-file-analysis.md#f012-mpsmoduleclassloaderjava) | `core/kernel/source/jetbrains/mps/classloading/MPSModuleClassLoader.java` | Java replacement | 104 → 69 |
| [F013 RootClassloaderLookup.java](C:/Codex/mps-j2cl/docs/mps-web-file-analysis.md#f013-rootclassloaderlookupjava) | `core/kernel/source/jetbrains/mps/classloading/RootClassloaderLookup.java` | Java replacement | 63 → 62 |
| [F014 DynamicComponentPlugin.java](C:/Codex/mps-j2cl/docs/mps-web-file-analysis.md#f014-dynamiccomponentpluginjava) | `core/kernel/source/jetbrains/mps/core/platform/DynamicComponentPlugin.java` | Java replacement | 353 → 482 |
| [F015 BaseQuickFixProvider.java](C:/Codex/mps-j2cl/docs/mps-web-file-analysis.md#f015-basequickfixproviderjava) | `core/kernel/source/jetbrains/mps/errors/BaseQuickFixProvider.java` | Java replacement | 119 → 132 |
| [F016 FileKindIsNotAllowedException.java](C:/Codex/mps-j2cl/docs/mps-web-file-analysis.md#f016-filekindisnotallowedexceptionjava) | `core/kernel/source/jetbrains/mps/extapi/persistence/FileKindIsNotAllowedException.java` | Java replacement | 69 → 68 |
| [F017 SourcePaths.java](C:/Codex/mps-j2cl/docs/mps-web-file-analysis.md#f017-sourcepathsjava) | `core/kernel/source/jetbrains/mps/extapi/persistence/SourcePaths.java` | Java replacement | 154 → 152 |
| [F018 ParseFacility.java](C:/Codex/mps-j2cl/docs/mps-web-file-analysis.md#f018-parsefacilityjava) | `core/kernel/source/jetbrains/mps/generator/cache/ParseFacility.java` | Java replacement | 122 → 122 |
| [F019 Message.java](C:/Codex/mps-j2cl/docs/mps-web-file-analysis.md#f019-messagejava) | `core/kernel/source/jetbrains/mps/messages/Message.java` | Java replacement | 151 → 150 |
| [F020 SDKDiscovery.java](C:/Codex/mps-j2cl/docs/mps-web-file-analysis.md#f020-sdkdiscoveryjava) | `core/kernel/source/jetbrains/mps/reloading/SDKDiscovery.java` | Java replacement | 278 → 31 |
| [F021 ExtensionRegistry.java](C:/Codex/mps-j2cl/docs/mps-web-file-analysis.md#f021-extensionregistryjava) | `core/kernel/source/jetbrains/mps/smodel/language/ExtensionRegistry.java` | Java replacement | 141 → 142 |
| [F022 LanguageRegistry.java](C:/Codex/mps-j2cl/docs/mps-web-file-analysis.md#f022-languageregistryjava) | `core/kernel/source/jetbrains/mps/smodel/language/LanguageRegistry.java` | Java replacement | 836 → 928 |
| [F023 ModuleRuntime.java](C:/Codex/mps-j2cl/docs/mps-web-file-analysis.md#f023-moduleruntimejava) | `core/kernel/source/jetbrains/mps/smodel/runtime/ModuleRuntime.java` | Java replacement | 376 → 379 |
| [F024 IconCreationUtil.java](C:/Codex/mps-j2cl/docs/mps-web-file-analysis.md#f024-iconcreationutiljava) | `core/kernel/source/jetbrains/mps/util/IconCreationUtil.java` | Java replacement | 74 → 152 |
| [F025 JDOMUtil.java](C:/Codex/mps-j2cl/docs/mps-web-file-analysis.md#f025-jdomutiljava) | `core/kernel/source/jetbrains/mps/util/JDOMUtil.java` | Java replacement | 332 → 335 |
| [F026 PathManager.java](C:/Codex/mps-j2cl/docs/mps-web-file-analysis.md#f026-pathmanagerjava) | `core/kernel/source/jetbrains/mps/util/PathManager.java` | Java replacement | 176 → 143 |
| [F027 PatternUtil.java](C:/Codex/mps-j2cl/docs/mps-web-file-analysis.md#f027-patternutiljava) | `core/kernel/source/jetbrains/mps/util/PatternUtil.java` | Java replacement | 145 → 136 |
| [F028 QueryMethods.java](C:/Codex/mps-j2cl/docs/mps-web-file-analysis.md#f028-querymethodsjava) | `core/kernel/source/jetbrains/mps/util/QueryMethods.java` | Java replacement | 97 → 228 |
| [F029 UnzipUtil.java](C:/Codex/mps-j2cl/docs/mps-web-file-analysis.md#f029-unziputiljava) | `core/kernel/source/jetbrains/mps/util/UnzipUtil.java` | Java replacement | 47 → 26 |
| [F030 openapi2.iml](C:/Codex/mps-j2cl/docs/mps-web-file-analysis.md#f030-openapi2iml) | `core/openapi/openapi2.iml` | IDE addition | 0 → 12 |
| [F031 SModelName.java](C:/Codex/mps-j2cl/docs/mps-web-file-analysis.md#f031-smodelnamejava) | `core/openapi/source/org/jetbrains/mps/openapi/model/SModelName.java` | Java replacement | 393 → 631 |
| [F032 DataSourceNotSupportedProblem.java](C:/Codex/mps-j2cl/docs/mps-web-file-analysis.md#f032-datasourcenotsupportedproblemjava) | `core/openapi/source/org/jetbrains/mps/openapi/persistence/DataSourceNotSupportedProblem.java` | Java replacement | 37 → 37 |
| [F033 ModelFactory.java](C:/Codex/mps-j2cl/docs/mps-web-file-analysis.md#f033-modelfactoryjava) | `core/openapi/source/org/jetbrains/mps/openapi/persistence/ModelFactory.java` | Java replacement | 179 → 179 |
| [F034 project.iml](C:/Codex/mps-j2cl/docs/mps-web-file-analysis.md#f034-projectiml) | `core/project/project.iml` | IDE customization | 20 → 34 |
| [F035 ProjectModuleLoader.java](C:/Codex/mps-j2cl/docs/mps-web-file-analysis.md#f035-projectmoduleloaderjava) | `core/project/source/jetbrains/mps/project/ProjectModuleLoader.java` | Java replacement | 451 → 450 |
| [F036 ConceptMissingError.java](C:/Codex/mps-j2cl/docs/mps-web-file-analysis.md#f036-conceptmissingerrorjava) | `core/project/source/jetbrains/mps/project/validation/ConceptMissingError.java` | Java replacement | 43 → 37 |
| [F037 FileClassPathItem.java](C:/Codex/mps-j2cl/docs/mps-web-file-analysis.md#f037-fileclasspathitemjava) | `core/project/source/jetbrains/mps/reloading/FileClassPathItem.java` | Java replacement | 135 → 136 |
| [F038 JarFileClassPathItem.java](C:/Codex/mps-j2cl/docs/mps-web-file-analysis.md#f038-jarfileclasspathitemjava) | `core/project/source/jetbrains/mps/reloading/JarFileClassPathItem.java` | Java replacement | 395 → 49 |
| [F039 LocalResourceClassPathItem.java](C:/Codex/mps-j2cl/docs/mps-web-file-analysis.md#f039-localresourceclasspathitemjava) | `core/project/source/jetbrains/mps/reloading/LocalResourceClassPathItem.java` | Java replacement | 65 → 66 |
| [F040 smodel.iml](C:/Codex/mps-j2cl/docs/mps-web-file-analysis.md#f040-smodeliml) | `core/smodel/smodel.iml` | IDE customization | 18 → 22 |
| [F041 ModuleReference.java](C:/Codex/mps-j2cl/docs/mps-web-file-analysis.md#f041-modulereferencejava) | `core/smodel/source/jetbrains/mps/project/structure/modules/ModuleReference.java` | Java replacement | 128 → 135 |
| [F042 RuntimeFlags.java](C:/Codex/mps-j2cl/docs/mps-web-file-analysis.md#f042-runtimeflagsjava) | `core/smodel/source/jetbrains/mps/RuntimeFlags.java` | Java replacement | 130 → 130 |
| [F043 ActionDispatcher.java](C:/Codex/mps-j2cl/docs/mps-web-file-analysis.md#f043-actiondispatcherjava) | `core/smodel/source/jetbrains/mps/smodel/ActionDispatcher.java` | Java replacement | 312 → 328 |
| [F044 DefaultModelAccess.java](C:/Codex/mps-j2cl/docs/mps-web-file-analysis.md#f044-defaultmodelaccessjava) | `core/smodel/source/jetbrains/mps/smodel/DefaultModelAccess.java` | Java replacement | 49 → 74 |
| [F045 IconResource.java](C:/Codex/mps-j2cl/docs/mps-web-file-analysis.md#f045-iconresourcejava) | `core/smodel/source/jetbrains/mps/smodel/runtime/IconResource.java` | Java replacement | 151 → 194 |
| [F046 DefaultTypecheckingController.java](C:/Codex/mps-j2cl/docs/mps-web-file-analysis.md#f046-defaulttypecheckingcontrollerjava) | `core/typechecking/source/jetbrains/mps/typechecking/backend/DefaultTypecheckingController.java` | Java replacement | 203 → 215 |
| [F047 TypecheckingFacadeComponent.java](C:/Codex/mps-j2cl/docs/mps-web-file-analysis.md#f047-typecheckingfacadecomponentjava) | `core/typechecking/source/jetbrains/mps/typechecking/backend/TypecheckingFacadeComponent.java` | Java replacement | 218 → 229 |
| [F048 WorkbenchTypecheckingController.java](C:/Codex/mps-j2cl/docs/mps-web-file-analysis.md#f048-workbenchtypecheckingcontrollerjava) | `core/typechecking/source/jetbrains/mps/typechecking/backend/WorkbenchTypecheckingController.java` | Java replacement | 223 → 226 |
| [F049 TypecheckingFacade.java](C:/Codex/mps-j2cl/docs/mps-web-file-analysis.md#f049-typecheckingfacadejava) | `core/typechecking/source/jetbrains/mps/typechecking/TypecheckingFacade.java` | Java replacement | 167 → 170 |
| [F050 typechecking2.iml](C:/Codex/mps-j2cl/docs/mps-web-file-analysis.md#f050-typechecking2iml) | `core/typechecking/typechecking2.iml` | IDE addition | 0 → 25 |
| [F051 DoubleRuleSet.java](C:/Codex/mps-j2cl/docs/mps-web-file-analysis.md#f051-doublerulesetjava) | `core/typesystemEngine/source/jetbrains/mps/lang/typesystem/runtime/DoubleRuleSet.java` | Java replacement | 117 → 113 |
| [F052 RuleSet.java](C:/Codex/mps-j2cl/docs/mps-web-file-analysis.md#f052-rulesetjava) | `core/typesystemEngine/source/jetbrains/mps/lang/typesystem/runtime/RuleSet.java` | Java replacement | 138 → 154 |
| [F053 IncrementalTypecheckingComponent.java](C:/Codex/mps-j2cl/docs/mps-web-file-analysis.md#f053-incrementaltypecheckingcomponentjava) | `core/typesystemEngine/source/jetbrains/mps/newTypesystem/context/component/IncrementalTypecheckingComponent.java` | Java replacement | 216 → 218 |
| [F054 State.java](C:/Codex/mps-j2cl/docs/mps-web-file-analysis.md#f054-statejava) | `core/typesystemEngine/source/jetbrains/mps/newTypesystem/state/State.java` | Java replacement | 587 → 587 |
| [F055 typesystemEngine2.iml](C:/Codex/mps-j2cl/docs/mps-web-file-analysis.md#f055-typesystemengine2iml) | `core/typesystemEngine/typesystemEngine2.iml` | IDE addition | 0 → 37 |
| [F056 ArrayWrapper.java](C:/Codex/mps-j2cl/docs/mps-web-file-analysis.md#f056-arraywrapperjava) | `core/util/source/jetbrains/mps/util/ArrayWrapper.java` | Java replacement | 104 → 104 |
| [F057 NameUtil.java](C:/Codex/mps-j2cl/docs/mps-web-file-analysis.md#f057-nameutiljava) | `core/util/source/jetbrains/mps/util/NameUtil.java` | Java replacement | 592 → 610 |
| [F058 PerformanceTracer.java](C:/Codex/mps-j2cl/docs/mps-web-file-analysis.md#f058-performancetracerjava) | `core/util/source/jetbrains/mps/util/performance/PerformanceTracer.java` | Java replacement | 293 → 293 |
| [F059 StringUtil.java](C:/Codex/mps-j2cl/docs/mps-web-file-analysis.md#f059-stringutiljava) | `core/util/source/jetbrains/mps/util/StringUtil.java` | Java replacement | 304 → 307 |
| [F060 WeakSet.java](C:/Codex/mps-j2cl/docs/mps-web-file-analysis.md#f060-weaksetjava) | `core/util/source/jetbrains/mps/util/WeakSet.java` | Java replacement | 67 → 199 |
| [F061 util2.iml](C:/Codex/mps-j2cl/docs/mps-web-file-analysis.md#f061-util2iml) | `core/util/util2.iml` | IDE addition | 0 → 13 |
| [F062 Files.java](C:/Codex/mps-j2cl/docs/mps-web-file-analysis.md#f062-filesjava) | `core/vfs/deprecated/jetbrains/mps/vfs/Files.java` | Java replacement | 76 → 76 |
| [F063 FileUtil.java](C:/Codex/mps-j2cl/docs/mps-web-file-analysis.md#f063-fileutiljava) | `core/vfs/source/jetbrains/mps/util/FileUtil.java` | Java replacement | 693 → 681 |
| [F064 IFileUtil.java](C:/Codex/mps-j2cl/docs/mps-web-file-analysis.md#f064-ifileutiljava) | `core/vfs/source/jetbrains/mps/util/IFileUtil.java` | Java replacement | 200 → 208 |
| [F065 MacroHelper.java](C:/Codex/mps-j2cl/docs/mps-web-file-analysis.md#f065-macrohelperjava) | `core/vfs/source/jetbrains/mps/util/MacroHelper.java` | Java replacement | 96 → 170 |
| [F066 PathSpec.java](C:/Codex/mps-j2cl/docs/mps-web-file-analysis.md#f066-pathspecjava) | `core/vfs/source/jetbrains/mps/util/PathSpec.java` | Java replacement | 110 → 109 |
| [F067 LocalFile.java](C:/Codex/mps-j2cl/docs/mps-web-file-analysis.md#f067-localfilejava) | `core/vfs/source/jetbrains/mps/vfs/iofs/file/LocalFile.java` | Java replacement | 320 → 320 |
| [F068 JarEntryFile.java](C:/Codex/mps-j2cl/docs/mps-web-file-analysis.md#f068-jarentryfilejava) | `core/vfs/source/jetbrains/mps/vfs/iofs/jar/JarEntryFile.java` | Java replacement | 306 → 306 |
| [F069 JarFileData.java](C:/Codex/mps-j2cl/docs/mps-web-file-analysis.md#f069-jarfiledatajava) | `core/vfs/source/jetbrains/mps/vfs/iofs/jar/JarFileData.java` | Java replacement | 302 → 70 |
| [F070 JarFileDataCache.java](C:/Codex/mps-j2cl/docs/mps-web-file-analysis.md#f070-jarfiledatacachejava) | `core/vfs/source/jetbrains/mps/vfs/iofs/jar/JarFileDataCache.java` | Java replacement | 142 → 29 |
| [F071 ZipFileContainer.java](C:/Codex/mps-j2cl/docs/mps-web-file-analysis.md#f071-zipfilecontainerjava) | `core/vfs/source/jetbrains/mps/vfs/iofs/jar/ZipFileContainer.java` | Java replacement | 27 → 23 |
| [F072 PathFormats.java](C:/Codex/mps-j2cl/docs/mps-web-file-analysis.md#f072-pathformatsjava) | `core/vfs/source/jetbrains/mps/vfs/path/PathFormats.java` | Java replacement | 319 → 324 |
| [F073 QualifiedPath.java](C:/Codex/mps-j2cl/docs/mps-web-file-analysis.md#f073-qualifiedpathjava) | `core/vfs/source/jetbrains/mps/vfs/QualifiedPath.java` | Java replacement | 80 → 87 |
| [F074 PathFormatChecker.java](C:/Codex/mps-j2cl/docs/mps-web-file-analysis.md#f074-pathformatcheckerjava) | `core/vfs/source/jetbrains/mps/vfs/util/PathFormatChecker.java` | Java replacement | 151 → 149 |
| [F075 vfs2.iml](C:/Codex/mps-j2cl/docs/mps-web-file-analysis.md#f075-vfs2iml) | `core/vfs/vfs2.iml` | IDE addition | 0 → 16 |
| [F076 editing-runtime.iml](C:/Codex/mps-j2cl/docs/mps-web-file-analysis.md#f076-editing-runtimeiml) | `editor/editing-runtime/editing-runtime.iml` | IDE customization | 17 → 31 |
| [F077 DataTransferManager.java](C:/Codex/mps-j2cl/docs/mps-web-file-analysis.md#f077-datatransfermanagerjava) | `editor/editing-runtime/source_gen/jetbrains/mps/datatransfer/DataTransferManager.java` | Java replacement | 172 → 191 |
| [F078 editor-runtime.iml](C:/Codex/mps-j2cl/docs/mps-web-file-analysis.md#f078-editor-runtimeiml) | `editor/editor-runtime/editor-runtime.iml` | IDE customization | 42 → 60 |
| [F079 CompletionHelper.java](C:/Codex/mps-j2cl/docs/mps-web-file-analysis.md#f079-completionhelperjava) | `editor/editor-runtime/source/jetbrains/mps/nodeEditor/cellMenu/CompletionHelper.java` | Java replacement | 221 → 221 |
| [F080 NodeSubstitutePatternEditor.java](C:/Codex/mps-j2cl/docs/mps-web-file-analysis.md#f080-nodesubstitutepatterneditorjava) | `editor/editor-runtime/source/jetbrains/mps/nodeEditor/cellMenu/NodeSubstitutePatternEditor.java` | Java replacement | 615 → 641 |
| [F081 ContextAssistantSettings.java](C:/Codex/mps-j2cl/docs/mps-web-file-analysis.md#f081-contextassistantsettingsjava) | `editor/editor-runtime/source/jetbrains/mps/nodeEditor/cells/contextAssistant/ContextAssistantSettings.java` | Java replacement | 49 → 48 |
| [F082 MPSDocumentationEditorPane.java](C:/Codex/mps-j2cl/docs/mps-web-file-analysis.md#f082-mpsdocumentationeditorpanejava) | `editor/editor-runtime/source/jetbrains/mps/nodeEditor/documentation/MPSDocumentationEditorPane.java` | Java replacement | 155 → 51 |
| [F083 MPSDocumentationHtmlUtil.java](C:/Codex/mps-j2cl/docs/mps-web-file-analysis.md#f083-mpsdocumentationhtmlutiljava) | `editor/editor-runtime/source/jetbrains/mps/nodeEditor/documentation/MPSDocumentationHtmlUtil.java` | Java replacement | 89 → 12 |
| [F084 MPSDocumentationToolWindowManager.java](C:/Codex/mps-j2cl/docs/mps-web-file-analysis.md#f084-mpsdocumentationtoolwindowmanagerjava) | `editor/editor-runtime/source/jetbrains/mps/nodeEditor/documentation/MPSDocumentationToolWindowManager.java` | Java replacement | 113 → 113 |
| [F085 EditorComponent.java](C:/Codex/mps-j2cl/docs/mps-web-file-analysis.md#f085-editorcomponentjava) | `editor/editor-runtime/source/jetbrains/mps/nodeEditor/EditorComponent.java` | Java replacement | 3271 → 3429 |
| [F086 EditorComponentKeyboardHandler.java](C:/Codex/mps-j2cl/docs/mps-web-file-analysis.md#f086-editorcomponentkeyboardhandlerjava) | `editor/editor-runtime/source/jetbrains/mps/nodeEditor/EditorComponentKeyboardHandler.java` | Java replacement | 150 → 147 |
| [F087 HighlighterUpdateSession.java](C:/Codex/mps-j2cl/docs/mps-web-file-analysis.md#f087-highlighterupdatesessionjava) | `editor/editor-runtime/source/jetbrains/mps/nodeEditor/highlighter/HighlighterUpdateSession.java` | Java replacement | 337 → 400 |
| [F088 Highlighter.java](C:/Codex/mps-j2cl/docs/mps-web-file-analysis.md#f088-highlighterjava) | `editor/editor-runtime/source/jetbrains/mps/nodeEditor/Highlighter.java` | Java replacement | 631 → 668 |
| [F089 HintPopupController.java](C:/Codex/mps-j2cl/docs/mps-web-file-analysis.md#f089-hintpopupcontrollerjava) | `editor/editor-runtime/source/jetbrains/mps/nodeEditor/HintPopupController.java` | Java replacement | 147 → 151 |
| [F090 IntentionsSupport.java](C:/Codex/mps-j2cl/docs/mps-web-file-analysis.md#f090-intentionssupportjava) | `editor/editor-runtime/source/jetbrains/mps/nodeEditor/IntentionsSupport.java` | Java replacement | 362 → 351 |
| [F091 AWTKeymapHandler.java](C:/Codex/mps-j2cl/docs/mps-web-file-analysis.md#f091-awtkeymaphandlerjava) | `editor/editor-runtime/source/jetbrains/mps/nodeEditor/keymaps/AWTKeymapHandler.java` | Java replacement | 246 → 425 |
| [F092 LeftEditorHighlighter.java](C:/Codex/mps-j2cl/docs/mps-web-file-analysis.md#f092-lefteditorhighlighterjava) | `editor/editor-runtime/source/jetbrains/mps/nodeEditor/leftHighlighter/LeftEditorHighlighter.java` | Java replacement | 864 → 891 |
| [F093 MemoryAnalyzer.java](C:/Codex/mps-j2cl/docs/mps-web-file-analysis.md#f093-memoryanalyzerjava) | `editor/editor-runtime/source/jetbrains/mps/nodeEditor/memory/MemoryAnalyzer.java` | Java replacement | 148 → 21 |
| [F094 NodeInformationDialog.java](C:/Codex/mps-j2cl/docs/mps-web-file-analysis.md#f094-nodeinformationdialogjava) | `editor/editor-runtime/source/jetbrains/mps/nodeEditor/NodeInformationDialog.java` | Java replacement | 149 → 122 |
| [F095 PlatformEditorEmulation.java](C:/Codex/mps-j2cl/docs/mps-web-file-analysis.md#f095-platformeditoremulationjava) | `editor/editor-runtime/source/jetbrains/mps/nodeEditor/PlatformEditorEmulation.java` | Java replacement | 760 → 137 |
| [F096 EditorCellSelection.java](C:/Codex/mps-j2cl/docs/mps-web-file-analysis.md#f096-editorcellselectionjava) | `editor/editor-runtime/source/jetbrains/mps/nodeEditor/selection/EditorCellSelection.java` | Java replacement | 263 → 265 |
| [F097 NodeRangeSelection.java](C:/Codex/mps-j2cl/docs/mps-web-file-analysis.md#f097-noderangeselectionjava) | `editor/editor-runtime/source/jetbrains/mps/nodeEditor/selection/NodeRangeSelection.java` | Java replacement | 485 → 482 |
| [F098 SelectionInfoImpl.java](C:/Codex/mps-j2cl/docs/mps-web-file-analysis.md#f098-selectioninfoimpljava) | `editor/editor-runtime/source/jetbrains/mps/nodeEditor/selection/SelectionInfoImpl.java` | Java replacement | 236 → 237 |
| [F099 UpdaterRepositoryContentAdapter.java](C:/Codex/mps-j2cl/docs/mps-web-file-analysis.md#f099-updaterrepositorycontentadapterjava) | `editor/editor-runtime/source/jetbrains/mps/nodeEditor/updater/UpdaterRepositoryContentAdapter.java` | Java replacement | 102 → 102 |
| [F100 mps-icons.iml](C:/Codex/mps-j2cl/docs/mps-web-file-analysis.md#f100-mps-iconsiml) | `workbench/mps-icons/mps-icons.iml` | IDE customization | 13 → 13 |
| [F101 MPSIcons.java](C:/Codex/mps-j2cl/docs/mps-web-file-analysis.md#f101-mpsiconsjava) | `workbench/mps-icons/source/jetbrains/mps/icons/MPSIcons.java` | Java replacement | 163 → 163 |
| [F102 jetbrains.mps.ide.platform.iml](C:/Codex/mps-j2cl/docs/mps-web-file-analysis.md#f102-jetbrainsmpsideplatformiml) | `workbench/mps-platform/jetbrains.mps.ide.platform/jetbrains.mps.ide.platform.iml` | IDE addition | 0 → 40 |
| [F103 AddRequiredImportsDialog.java](C:/Codex/mps-j2cl/docs/mps-web-file-analysis.md#f103-addrequiredimportsdialogjava) | `workbench/mps-platform/jetbrains.mps.ide.platform/source_gen/jetbrains/mps/ide/datatransfer/AddRequiredImportsDialog.java` | Java replacement | 270 → 64 |
| [F104 BaseIconManager.java](C:/Codex/mps-j2cl/docs/mps-web-file-analysis.md#f104-baseiconmanagerjava) | `workbench/mps-platform/jetbrains.mps.ide.platform/source_gen/jetbrains/mps/ide/icons/BaseIconManager.java` | Java replacement | 303 → 301 |
| [F105 IconLoadingUtil.java](C:/Codex/mps-j2cl/docs/mps-web-file-analysis.md#f105-iconloadingutiljava) | `workbench/mps-platform/jetbrains.mps.ide.platform/source_gen/jetbrains/mps/ide/icons/IconLoadingUtil.java` | Java replacement | 29 → 95 |
| [F106 mps-platform.iml](C:/Codex/mps-j2cl/docs/mps-web-file-analysis.md#f106-mps-platformiml) | `workbench/mps-platform/mps-platform.iml` | IDE customization | 39 → 34 |
| [F107 MPSCoreComponents.java](C:/Codex/mps-j2cl/docs/mps-web-file-analysis.md#f107-mpscorecomponentsjava) | `workbench/mps-platform/source/jetbrains/mps/ide/MPSCoreComponents.java` | Java replacement | 149 → 50 |
| [F108 ThreadUtils.java](C:/Codex/mps-j2cl/docs/mps-web-file-analysis.md#f108-threadutilsjava) | `workbench/mps-platform/source/jetbrains/mps/ide/ThreadUtils.java` | Java replacement | 138 → 140 |
| [F109 OnReloadingUndoCleaner.java](C:/Codex/mps-j2cl/docs/mps-web-file-analysis.md#f109-onreloadingundocleanerjava) | `workbench/mps-platform/source/jetbrains/mps/ide/undo/OnReloadingUndoCleaner.java` | Java replacement | 154 → 63 |
| [F110 UndoActionsCollector.java](C:/Codex/mps-j2cl/docs/mps-web-file-analysis.md#f110-undoactionscollectorjava) | `workbench/mps-platform/source/jetbrains/mps/ide/undo/UndoActionsCollector.java` | Java replacement | 170 → 50 |
| [F111 WorkbenchUndoHandler.java](C:/Codex/mps-j2cl/docs/mps-web-file-analysis.md#f111-workbenchundohandlerjava) | `workbench/mps-platform/source/jetbrains/mps/ide/undo/WorkbenchUndoHandler.java` | Java replacement | 61 → 69 |
| [F112 MPSNodeVirtualFile.java](C:/Codex/mps-j2cl/docs/mps-web-file-analysis.md#f112-mpsnodevirtualfilejava) | `workbench/mps-platform/source/jetbrains/mps/nodefs/MPSNodeVirtualFile.java` | Java replacement | 341 → 79 |
| [F113 NodeVirtualFileSystem.java](C:/Codex/mps-j2cl/docs/mps-web-file-analysis.md#f113-nodevirtualfilesystemjava) | `workbench/mps-platform/source/jetbrains/mps/nodefs/NodeVirtualFileSystem.java` | Java replacement | 668 → 76 |
| [F114 MPSProject.java](C:/Codex/mps-j2cl/docs/mps-web-file-analysis.md#f114-mpsprojectjava) | `workbench/mps-platform/source/jetbrains/mps/project/MPSProject.java` | Java replacement | 231 → 93 |
| [F115 ProjectModelAccess2.java](C:/Codex/mps-j2cl/docs/mps-web-file-analysis.md#f115-projectmodelaccess2java) | `workbench/mps-platform/source/jetbrains/mps/smodel/ProjectModelAccess2.java` | Java replacement | 196 → 195 |
| [F116 BaseAction.java](C:/Codex/mps-j2cl/docs/mps-web-file-analysis.md#f116-baseactionjava) | `workbench/mps-platform/source/jetbrains/mps/workbench/action/BaseAction.java` | Java replacement | 369 → 367 |

## Per file findings

### F001 aspects.iml

**Path:** `core/aspects/aspects.iml`

[Browser file](C:/Codex/mps-j2cl/mps-web/core/aspects/aspects.iml) · No same-path file at the pinned upstream revision

**Classification:** IntelliJ project metadata. Text delta: +13 / −0 lines; 0 → 13 total lines.

**What changes:** Added IntelliJ module descriptor: no file exists at this exact path in the pinned upstream tree. Indexes the overlay source root(s) `constraints/constraints-runtime/source` with inherited compiler output. Declares 2 module dependencies. The complete XML or descriptor diff below records every dependency name.

**Behavior and limits:** The root .idea/modules.xml references this file, so it affects IDE indexing. It is not an MPS_OVERRIDES entry and is not used to select Bazel Java sources. Local module names such as kernel2 or webswing are IDE identifiers, not proof of Bazel dependency ownership.

**To make the replacement unnecessary:** Generate the IDE project from the authoritative selected-source/build graph, or keep equivalent descriptors outside the overlay. Update the root IDE module references before relocating metadata. Removing this file alone would break that IDE reference and would not reduce the 100 Java replacements.

Current module descriptor

```xml
<?xml version="1.0" encoding="UTF-8"?>
<module type="JAVA_MODULE" version="4">
  <component name="NewModuleRootManager" inherit-compiler-output="true">
    <exclude-output />
    <content url="file://$MODULE_DIR$">
      <sourceFolder url="file://$MODULE_DIR$/constraints/constraints-runtime/source" isTestSource="false" />
    </content>
    <orderEntry type="inheritedJdk" />
    <orderEntry type="sourceFolder" forTests="false" />
    <orderEntry type="module" module-name="context" />
    <orderEntry type="module" module-name="constraints" />
  </component>
</module>
```

### F002 PredefinedRuleKinds.java

**Path:** `core/aspects/constraints/constraints-runtime/source/jetbrains/mps/core/aspects/constraints/rules/kinds/PredefinedRuleKinds.java`

[Browser file](C:/Codex/mps-j2cl/mps-web/core/aspects/constraints/constraints-runtime/source/jetbrains/mps/core/aspects/constraints/rules/kinds/PredefinedRuleKinds.java) · [Compared upstream file](C:/Codex/mps-j2cl/mps/core/aspects/constraints/constraints-runtime/source/jetbrains/mps/core/aspects/constraints/rules/kinds/PredefinedRuleKinds.java)

**Classification:** Java APIs and text processing. Text delta: +10 / −7 lines; 70 → 73 total lines.

**What changes:** Replaces all four MessageFormat calls with concatenation. Temporary child, parent and descendant variables preserve the original evaluation order even when the message displays arguments in a different order.

**Behavior and limits:** The fixed English wording and quoted node names remain; general MessageFormat support and localization are not supplied by this file.

**To make the replacement unnecessary:** Provide MessageFormat in the shared JRE, or adopt these simpler fixed messages upstream. Check null values, apostrophes and argument evaluation order before restoring the original.

Complete textual diff

```diff
--- mps/core/aspects/constraints/constraints-runtime/source/jetbrains/mps/core/aspects/constraints/rules/kinds/PredefinedRuleKinds.java
+++ mps-web/core/aspects/constraints/constraints-runtime/source/jetbrains/mps/core/aspects/constraints/rules/kinds/PredefinedRuleKinds.java
@@ -19,7 +19,6 @@
 import jetbrains.mps.core.context.Context;
 import org.jetbrains.annotations.NotNull;
 
-import java.text.MessageFormat;
 
 public enum PredefinedRuleKinds implements RuleKind, LegacyRuleKind<Context>{
   CAN_BE_CHILD() {
@@ -30,7 +29,9 @@
         throw new IllegalArgumentException("Illegal context provided " + context.getClass());
       }
       ContainmentContext context1 = (ContainmentContext) context;
-      return MessageFormat.format("Node ''{0}'' cannot be child of node ''{1}''", context1.getChildNode(), context1.getParentNode());
+      Object child = context1.getChildNode();
+      Object parent = context1.getParentNode();
+      return "Node '" + child + "' cannot be child of node '" + parent + "'";
     }
   },
   CAN_BE_PARENT() {
@@ -41,7 +42,9 @@
         throw new IllegalArgumentException("Illegal context provided " + context.getClass());
       }
       ContainmentContext context1 = (ContainmentContext) context;
-      return MessageFormat.format("Node ''{1}'' cannot be parent of node ''{0}''", context1.getChildNode(), context1.getParentNode());
+      Object child = context1.getChildNode();
+      Object parent = context1.getParentNode();
+      return "Node '" + parent + "' cannot be parent of node '" + child + "'";
     }
   },
   CAN_BE_ROOT() {
@@ -51,7 +54,7 @@
       if (!(context instanceof CanBeRootContext)) {
         throw new IllegalArgumentException("Illegal context provided " + context.getClass());
       }
-      return MessageFormat.format("The concept ''{0}'' cannot be root", ((CanBeRootContext) context).getConcept());
+      return "The concept '" + ((CanBeRootContext) context).getConcept() + "' cannot be root";
     }
   },
   CAN_BE_ANCESTOR() {
@@ -62,9 +65,9 @@
         throw new IllegalArgumentException("Illegal context provided " + context.getClass());
       }
       CanBeAncestorContext context1 = (CanBeAncestorContext) context;
-      return MessageFormat.format("Node ''{1}'' cannot be ancestor of node ''{0}''",
-                                  context1.getDescendantNode(),
-                                  context1.getParentNode());
+      Object descendant = context1.getDescendantNode();
+      Object parent = context1.getParentNode();
+      return "Node '" + parent + "' cannot be ancestor of node '" + descendant + "'";
     }
   };
 }
```

### F003 findUsages-runtime2.iml

**Path:** `core/findUsages-runtime/findUsages-runtime2.iml`

[Browser file](C:/Codex/mps-j2cl/mps-web/core/findUsages-runtime/findUsages-runtime2.iml) · No same-path file at the pinned upstream revision

**Classification:** IntelliJ project metadata. Text delta: +27 / −0 lines; 0 → 27 total lines.

**What changes:** Added IntelliJ module descriptor: no file exists at this exact path in the pinned upstream tree. Indexes the overlay source root(s) `source_gen` with inherited compiler output. Declares 16 module dependencies. The complete XML or descriptor diff below records every dependency name.

**Behavior and limits:** The root .idea/modules.xml references this file, so it affects IDE indexing. It is not an MPS_OVERRIDES entry and is not used to select Bazel Java sources. Local module names such as kernel2 or webswing are IDE identifiers, not proof of Bazel dependency ownership.

**To make the replacement unnecessary:** Generate the IDE project from the authoritative selected-source/build graph, or keep equivalent descriptors outside the overlay. Update the root IDE module references before relocating metadata. Removing this file alone would break that IDE reference and would not reduce the 100 Java replacements.

Current module descriptor

```xml
<?xml version="1.0" encoding="UTF-8"?>
<module type="JAVA_MODULE" version="4">
  <component name="NewModuleRootManager" inherit-compiler-output="true">
    <exclude-output />
    <content url="file://$MODULE_DIR$">
      <sourceFolder url="file://$MODULE_DIR$/source_gen" isTestSource="false" />
    </content>
    <orderEntry type="inheritedJdk" />
    <orderEntry type="sourceFolder" forTests="false" />
    <orderEntry type="module" module-name="mps-platform2" />
    <orderEntry type="module" module-name="browser" />
    <orderEntry type="module" module-name="mps-annotations" />
    <orderEntry type="module" module-name="mps-platform" />
    <orderEntry type="module" module-name="smodel2" />
    <orderEntry type="module" module-name="mps-workbench" />
    <orderEntry type="module" module-name="openapi2" />
    <orderEntry type="module" module-name="core" />
    <orderEntry type="module" module-name="smodel" />
    <orderEntry type="module" module-name="kernelSolution2" />
    <orderEntry type="module" module-name="kernel2" />
    <orderEntry type="module" module-name="project" />
    <orderEntry type="module" module-name="openapi" />
    <orderEntry type="module" module-name="persistence" />
    <orderEntry type="module" module-name="project3" />
    <orderEntry type="module" module-name="findUsages-runtime" />
  </component>
</module>
```

### F004 FindUsagesScope.java

**Path:** `core/findUsages-runtime/source_gen/jetbrains/mps/ide/findusages/model/scopes/FindUsagesScope.java`

[Browser file](C:/Codex/mps-j2cl/mps-web/core/findUsages-runtime/source_gen/jetbrains/mps/ide/findusages/model/scopes/FindUsagesScope.java) · [Compared upstream file](C:/Codex/mps-j2cl/mps/core/findUsages-runtime/source_gen/jetbrains/mps/ide/findusages/model/scopes/FindUsagesScope.java)

**Classification:** Compiled deployment and reflection. Text delta: +3 / −1 lines; 89 → 91 total lines.

**What changes:** Replaces Class.forName followed by reflective construction of a persisted scope with registered class lookup and a typed ModuleClassLoader factory taking Element and Project.

**Behavior and limits:** The persisted class name is still read, but it must name a class already linked and registered. This is not discovery of arbitrary scope bytecode.

**To make the replacement unnecessary:** Have the MPS generator emit a scope factory catalog and let upstream scope restoration depend on a construction service; alternatively implement the precise constructor-reflection subset in the compiler/JRE.

Complete textual diff

```diff
--- mps/core/findUsages-runtime/source_gen/jetbrains/mps/ide/findusages/model/scopes/FindUsagesScope.java
+++ mps-web/core/findUsages-runtime/source_gen/jetbrains/mps/ide/findusages/model/scopes/FindUsagesScope.java
@@ -75,7 +75,9 @@
     Element scopeXml = element.getChild(SCOPE_TAG);
     String scopeClass = scopeXml.getAttributeValue(SCOPE_CLASS_ATTR);
     try {
-      return (FindUsagesScope) Class.forName(scopeClass).getConstructor(Element.class, Project.class).newInstance(scopeXml, project);
+      return jetbrains.mps.classloading.ModuleClassLoader.newInstance(
+          ClassLoader.getSystemClassLoader().loadClass(scopeClass), FindUsagesScope.class,
+          new Class<?>[] {Element.class, Project.class}, scopeXml, project);
     } catch (Exception e) {
       throw new CantLoadSomethingException(e);
     }
```

### F005 kernel.iml

**Path:** `core/kernel/kernel.iml`

[Browser file](C:/Codex/mps-j2cl/mps-web/core/kernel/kernel.iml) · [Compared upstream file](C:/Codex/mps-j2cl/mps/core/kernel/kernel.iml)

**Classification:** IntelliJ project metadata. Text delta: +26 / −29 lines; 38 → 35 total lines.

**What changes:** Customized IntelliJ descriptor with a same-path upstream counterpart. Indexes the overlay source root(s) `source` with inherited compiler output. Declares 23 module dependencies and 1 project libraries. The complete XML or descriptor diff below records every dependency name. The upstream source/test root(s) `smodelRuntime/source_gen`, `kernelSolution/source_gen`, `tests` are absent here. Added module references: `browser`, `constraints`, `core`, `j2cl`, `kernel2`, `kernelSolution2`, `logging2`, `module`, `mps-platform`, `mps-platform2`, `mps-workbench`, `openapi2`, `persistence`, `project3`, `smodel2`, `vfs2`, `webawt`. Removed module references: `baseLanguage-runtime`, `behavior-api`, `behavior-runtime`, `closures-runtime`, `collections-runtime`, `constraints-runtime`, `context`, `environment`, `messages-for-legacy-constraints`, `mps-messaging`, `tuples-runtime`.

**Behavior and limits:** The root .idea/modules.xml references this file, so it affects IDE indexing. It is not an MPS_OVERRIDES entry and is not used to select Bazel Java sources. Local module names such as kernel2 or webswing are IDE identifiers, not proof of Bazel dependency ownership.

**To make the replacement unnecessary:** Generate the IDE project from the authoritative selected-source/build graph, or keep equivalent descriptors outside the overlay. Update the root IDE module references before relocating metadata. Removing this file alone would break that IDE reference and would not reduce the 100 Java replacements.

Complete descriptor diff

```diff
--- mps/core/kernel/kernel.iml
+++ mps-web/core/kernel/kernel.iml
@@ -1,38 +1,35 @@
 <?xml version="1.0" encoding="UTF-8"?>
-<module relativePaths="true" type="JAVA_MODULE" version="4">
-  <component name="NewModuleRootManager">
-    <output url="file://$MODULE_DIR$/classes" />
-    <output-test url="file://$MODULE_DIR$/testclasses" />
+<module type="JAVA_MODULE" version="4">
+  <component name="NewModuleRootManager" inherit-compiler-output="true">
     <exclude-output />
     <content url="file://$MODULE_DIR$">
-      <sourceFolder url="file://$MODULE_DIR$/smodelRuntime/source_gen" isTestSource="false" />
-      <sourceFolder url="file://$MODULE_DIR$/kernelSolution/source_gen" isTestSource="false" />
       <sourceFolder url="file://$MODULE_DIR$/source" isTestSource="false" />
-      <sourceFolder url="file://$MODULE_DIR$/tests" isTestSource="true" />
     </content>
     <orderEntry type="inheritedJdk" />
     <orderEntry type="sourceFolder" forTests="false" />
-    <orderEntry type="library" scope="TEST" name="hamcrest" level="project" />
-    <orderEntry type="module" module-name="openapi" exported="" />
-    <orderEntry type="module" module-name="smodel" exported="" />
-    <orderEntry type="module" module-name="tuples-runtime" exported="" />
-    <orderEntry type="module" module-name="closures-runtime" exported="" />
-    <orderEntry type="module" module-name="collections-runtime" exported="" />
-    <orderEntry type="module" module-name="project" exported="" />
-    <orderEntry type="module" module-name="components" exported="" />
-    <orderEntry type="module" module-name="behavior-api" exported="" />
-    <orderEntry type="module" module-name="behavior-runtime" exported="" />
-    <orderEntry type="module" module-name="mps-messaging" exported="" />
-    <orderEntry type="library" name="org.jdom" level="project" />
-    <orderEntry type="library" name="gnu.trove" level="project" />
-    <orderEntry type="module" module-name="baseLanguage-runtime" exported="" />
-    <orderEntry type="module" module-name="constraints-runtime" exported="" />
-    <orderEntry type="module" module-name="context" exported="" />
-    <orderEntry type="module" module-name="messages-for-legacy-constraints" />
-    <orderEntry type="module" module-name="vfs" exported="" />
-    <orderEntry type="module" module-name="testbench" scope="TEST" />
-    <orderEntry type="library" scope="TEST" name="JMock" level="project" />
-    <orderEntry type="library" scope="TEST" name="JUnit4" level="project" />
-    <orderEntry type="module" module-name="environment" scope="TEST" />
+    <orderEntry type="library" name="lib2" level="project" />
+    <orderEntry type="module" module-name="components" />
+    <orderEntry type="module" module-name="mps-platform2" />
+    <orderEntry type="module" module-name="browser" />
+    <orderEntry type="module" module-name="webawt" />
+    <orderEntry type="module" module-name="logging2" />
+    <orderEntry type="module" module-name="module" />
+    <orderEntry type="module" module-name="mps-platform" />
+    <orderEntry type="module" module-name="constraints" />
+    <orderEntry type="module" module-name="smodel2" />
+    <orderEntry type="module" module-name="mps-workbench" />
+    <orderEntry type="module" module-name="openapi2" />
+    <orderEntry type="module" module-name="core" />
+    <orderEntry type="module" module-name="smodel" />
+    <orderEntry type="module" module-name="kernelSolution2" />
+    <orderEntry type="module" module-name="kernel2" />
+    <orderEntry type="module" module-name="testbench" />
+    <orderEntry type="module" module-name="project" />
+    <orderEntry type="module" module-name="openapi" />
+    <orderEntry type="module" module-name="vfs" />
+    <orderEntry type="module" module-name="persistence" />
+    <orderEntry type="module" module-name="j2cl" />
+    <orderEntry type="module" module-name="project3" />
+    <orderEntry type="module" module-name="vfs2" />
   </component>
 </module>
```

### F006 kernelSolution.iml

**Path:** `core/kernel/kernelSolution/kernelSolution.iml`

[Browser file](C:/Codex/mps-j2cl/mps-web/core/kernel/kernelSolution/kernelSolution.iml) · No same-path file at the pinned upstream revision

**Classification:** IntelliJ project metadata. Text delta: +13 / −0 lines; 0 → 13 total lines.

**What changes:** Added IntelliJ module descriptor: no file exists at this exact path in the pinned upstream tree. Indexes the overlay source root(s) `source_gen` with inherited compiler output. Declares 2 module dependencies. The complete XML or descriptor diff below records every dependency name.

**Behavior and limits:** The root .idea/modules.xml references this file, so it affects IDE indexing. It is not an MPS_OVERRIDES entry and is not used to select Bazel Java sources. Local module names such as kernel2 or webswing are IDE identifiers, not proof of Bazel dependency ownership.

**To make the replacement unnecessary:** Generate the IDE project from the authoritative selected-source/build graph, or keep equivalent descriptors outside the overlay. Update the root IDE module references before relocating metadata. Removing this file alone would break that IDE reference and would not reduce the 100 Java replacements.

Current module descriptor

```xml
<?xml version="1.0" encoding="UTF-8"?>
<module type="JAVA_MODULE" version="4">
  <component name="NewModuleRootManager" inherit-compiler-output="true">
    <exclude-output />
    <content url="file://$MODULE_DIR$">
      <sourceFolder url="file://$MODULE_DIR$/source_gen" isTestSource="false" />
    </content>
    <orderEntry type="inheritedJdk" />
    <orderEntry type="sourceFolder" forTests="false" />
    <orderEntry type="module" module-name="mps-annotations" />
    <orderEntry type="module" module-name="runtime2" />
  </component>
</module>
```

### F007 StringsIO.java

**Path:** `core/kernel/kernelSolution/source_gen/jetbrains/mps/util/StringsIO.java`

[Browser file](C:/Codex/mps-j2cl/mps-web/core/kernel/kernelSolution/source_gen/jetbrains/mps/util/StringsIO.java) · [Compared upstream file](C:/Codex/mps-j2cl/mps/core/kernel/kernelSolution/source_gen/jetbrains/mps/util/StringsIO.java)

**Classification:** Java APIs and text processing. Text delta: +95 / −6 lines; 52 → 141 total lines.

**What changes:** Replaces Scanner-based line reading with buffered UTF-8 decoding, CR/LF/CRLF/NEL/Unicode line separators, malformed-sequence replacement and explicit stream closing. Writing remains upstream.

**Behavior and limits:** This is a line-reading implementation, not Scanner. It fixes decoding to the reviewed UTF-8 profile and preserves partial decoded text on IO failure, distinguishing IO failure from EOF.

**To make the replacement unnecessary:** Add a shared Scanner nextLine implementation with the same charset, malformed UTF-8, delimiter and closing contracts, or expose a reusable line reader to upstream MPS. Validate EOF, zero-byte reads and partial failures.

Representative excerpts from the compared files; the prose above covers additional changes.

Pinned upstream at line 33

```java
    List<String> lines = ListSequence.fromList(new ArrayList<String>());
    Scanner sc = new Scanner(input);
    while (sc.hasNextLine()) {
      String line = sc.nextLine();
      ListSequence.fromList(lines).addElement(line);
    }
    sc.close();
    return lines;
  }
  public static void writeLines(File file, Iterable<String> lines) throws IOException {
    writeLines(new FileOutputStream(file), lines);
  }
```

Browser replacement at line 33

```java
    if (input == null) throw new NullPointerException("input");
    Utf8Input reader = new Utf8Input(input);
    StringBuilder line = new StringBuilder();
    boolean afterCR = false;
    try {
      int value;
      while ((value = reader.read()) != -1) {
        if (afterCR && value == '\n') { afterCR = false; continue; }
        afterCR = value == '\r';
        if (value == '\r' || value == '\n' || value == 0x85 || value == 0x2028 || value == 0x2029) {
          lines.add(line.toString());
          line.setLength(0);
```

### F008 ClassLoaderManager.java

**Path:** `core/kernel/source/jetbrains/mps/classloading/ClassLoaderManager.java`

[Browser file](C:/Codex/mps-j2cl/mps-web/core/kernel/source/jetbrains/mps/classloading/ClassLoaderManager.java) · [Compared upstream file](C:/Codex/mps-j2cl/mps/core/kernel/source/jetbrains/mps/classloading/ClassLoaderManager.java)

**Classification:** Upstream diagnostic fix. Text delta: +2 / −2 lines; 603 → 603 total lines.

**What changes:** Moves the closing parenthesis so formatted(session.actualNumberOfSessions()) applies to the complete concatenated diagnostic, including the %d placeholder.

**Behavior and limits:** This fixes an upstream expression error caught by the Java FormatString check. It is unrelated to browser architecture.

**To make the replacement unnecessary:** Upstream the parenthesis fix or upgrade to an MPS revision containing it. Suppressing the format check would retain the incorrect expression.

Complete textual diff

```diff
--- mps/core/kernel/source/jetbrains/mps/classloading/ClassLoaderManager.java
+++ mps-web/core/kernel/source/jetbrains/mps/classloading/ClassLoaderManager.java
@@ -410,8 +410,8 @@
       if (ourCheckMemLeaks && session.actualNumberOfSessions() > MAX_SESSIONS_ALIVE) {
         // note that if we do 100 reloads during a single write action we might get a 100 sessions
         LOG.error(
-            "Possible leaking class loaders : currently there are %d sessions alive. "
-            + "Please avoid running too many reloads in the single write action".formatted(session.actualNumberOfSessions()));
+            ("Possible leaking class loaders : currently there are %d sessions alive. "
+            + "Please avoid running too many reloads in the single write action").formatted(session.actualNumberOfSessions()));
       }
 
       myBroadCaster.onUnload(session, monitor.subTask(4, SubProgressKind.AS_COMMENT));
```

### F009 IDEADelegatingModuleClassLoader.java

**Path:** `core/kernel/source/jetbrains/mps/classloading/IDEADelegatingModuleClassLoader.java`

[Browser file](C:/Codex/mps-j2cl/mps-web/core/kernel/source/jetbrains/mps/classloading/IDEADelegatingModuleClassLoader.java) · [Compared upstream file](C:/Codex/mps-j2cl/mps/core/kernel/source/jetbrains/mps/classloading/IDEADelegatingModuleClassLoader.java)

**Classification:** Compiled deployment and reflection. Text delta: +6 / −7 lines; 58 → 57 total lines.

**What changes:** Replaces defining-classloader inspection with isOwnRegisteredClass(name, type) on the delegate; also replaces two formatted diagnostics with concatenation.

**Behavior and limits:** A dependency lookup is not accepted as an own class merely because the lookup succeeded. Ownership is explicit registration metadata.

**To make the replacement unnecessary:** Expose module ownership through an upstream loader interface with JVM and compiled-browser implementations. Generic Class.getClassLoader emulation alone needs the same ownership metadata.

Complete textual diff

```diff
--- mps/core/kernel/source/jetbrains/mps/classloading/IDEADelegatingModuleClassLoader.java
+++ mps-web/core/kernel/source/jetbrains/mps/classloading/IDEADelegatingModuleClassLoader.java
@@ -33,7 +33,7 @@
   private final SModuleReference myModule;
 
   public IDEADelegatingModuleClassLoader(SModuleReference module, ClassLoader delegate) {
-    super(String.format("Delegate %s to IDEA CL %s", NameUtil.compactNamespace(module.getModuleName()), delegate.toString()), delegate);
+    super("Delegate " + NameUtil.compactNamespace(module.getModuleName()) + " to IDEA CL " + delegate.toString(), delegate);
     myModule = module;
   }
 
@@ -41,14 +41,13 @@
   @NotNull
   public Class<?> loadOwnClass(String name) throws ClassNotFoundException {
     final Class<?> rv = loadClass(name);
-    // It's quite convenient to have loadOwnClass() in superclass, yet there's no easy way to implement
-    // it properly in this IDEA delegating CL. I hesitate about loadOwnClass == loadClass, or even just bald `return null`.
-    // for now, explore an assumption classes directly from plugin would come with CL == getParent, while
-    // classes from dependencies would manifest different plugin CL
-    if (rv.getClassLoader() == getParent()) {
+    // The plugin delegate must explicitly own this name and actual class identity.
+    // A successful lookup through one of its dependencies is not an own class.
+    if (getParent().isOwnRegisteredClass(name, rv)) {
       return rv;
     }
-    throw new ModuleClassNotFoundException(myModule, String.format("Class %s is not available directly from module %s but from one of its dependencies", name, myModule.getModuleName()));
+    throw new ModuleClassNotFoundException(myModule, "Class " + name + " is not available directly from module "
+        + myModule.getModuleName() + " but from one of its dependencies");
   }
 
   @Override
```

### F010 ModuleClassLoader.java

**Path:** `core/kernel/source/jetbrains/mps/classloading/ModuleClassLoader.java`

[Browser file](C:/Codex/mps-j2cl/mps-web/core/kernel/source/jetbrains/mps/classloading/ModuleClassLoader.java) · [Compared upstream file](C:/Codex/mps-j2cl/mps/core/kernel/source/jetbrains/mps/classloading/ModuleClassLoader.java)

**Classification:** Compiled deployment and reflection. Text delta: +368 / −300 lines; 392 → 460 total lines.

**What changes:** Replaces bytecode definition, protection domains, parallel class-loading synchronization and concurrent caches with registries of linked Class objects and resources. Adds typed constructor factories, exact signatures, ownership, stable binary aliases, cycle guards and disposal of registrations. Own classes, dependencies, parent fallback and system java.* lookup remain distinct.

**Behavior and limits:** No arbitrary JVM bytecode can be loaded. Factories validate arguments/results; constructor arguments use exact boxed primitive types. getRegisteredClassName requires a live factory owner, not merely a class-name registration.

**To make the replacement unnecessary:** Introduce an upstream compiled-module runtime service and generate class/resource/factory catalogs. Keep module-first resolution and lifecycle tests. Eliminating this replacement while retaining the bytecode algorithm would require an actual JVM execution capability, far beyond a library shim.

Representative excerpts from the compared files; the prose above covers additional changes.

Pinned upstream at line 195

```java
        // originates from 8cf2bde5
        // no reason to explicitly define package, at least here. defineClass(), below, invoked postDefineClass(),
        // which records NamedPackage in CL.packages(), so that later getDefinedPackage() would "define" the package when necessary.
//        String pack = NameUtil.namespaceFromLongName(fqName);
//        synchronized (myPackageLock) {
//          if (getPackage(pack) == null) {
//            definePackage(pack, null, null, null, null, null, null, null);
//          }
//        }
        ProtectionDomain newProtectionDomain = loadedClassDomain(classBytes.getPath());
        byte[] bytes = classBytes.getBytes();
        aClass = defineClass(fqName, bytes, 0, bytes.length, newProtectionDomain);
```

Browser replacement at line 279

```java

  public <T> void registerFactory(String binaryName, Class<T> implementation, Class<?>[] parameterTypes,
                                  Factory<? extends T> factory) {
    checkNotDisposed();
    Objects.requireNonNull(implementation, "implementation");
    Objects.requireNonNull(factory, "factory");
    List<Class<?>> signature = signature(parameterTypes);
    ModuleClassLoader owner = FACTORY_OWNERS.get(implementation);
    if (owner != null && owner != this) {
      throw new IllegalStateException("The compiled class already has a factory owner: " + binaryName);
    }
    Map<List<Class<?>>, RegisteredFactory<?>> factories = myFactories.get(implementation);
```

Additional browser code at line 310

```java
  /** A deployed binary alias remains valid even when J2CL strips Class names. */
  public static String getRegisteredClassName(Class<?> implementation) {
    Objects.requireNonNull(implementation, "implementation");
    ModuleClassLoader owner = FACTORY_OWNERS.get(implementation);
    if (owner == null || owner.isDisposed()) {
      throw new IllegalStateException("No live constructor factory owns this compiled class");
    }
    for (RegisteredFactory<?> factory : owner.myFactories.get(implementation).values()) {
      return factory.myBinaryName;
    }
    throw new IllegalStateException("Compiled class has no registered binary alias");
  }
```

### F011 MPSClassLoadersRegistry.java

**Path:** `core/kernel/source/jetbrains/mps/classloading/MPSClassLoadersRegistry.java`

[Browser file](C:/Codex/mps-j2cl/mps-web/core/kernel/source/jetbrains/mps/classloading/MPSClassLoadersRegistry.java) · [Compared upstream file](C:/Codex/mps-j2cl/mps/core/kernel/source/jetbrains/mps/classloading/MPSClassLoadersRegistry.java)

**Classification:** Java APIs and text processing. Text delta: +8 / −10 lines; 363 → 361 total lines.

**What changes:** Replaces Instant creation/planning/disposal fields with nullable Long epoch milliseconds and Duration.between(...).toMinutes() with arithmetic division by 60000.

**Behavior and limits:** Timeout decisions retain millisecond/minute intent; internal timestamp fields now contain numbers rather than Instant objects. Current shared Instant and Duration implementations already exist, making this a retirement candidate.

**To make the replacement unnecessary:** Restore the original against the composed time APIs in an isolated trial; verify null states and timeout boundaries, including clock changes. No new time library should be assumed necessary before testing current support.

Complete textual diff

```diff
--- mps/core/kernel/source/jetbrains/mps/classloading/MPSClassLoadersRegistry.java
+++ mps-web/core/kernel/source/jetbrains/mps/classloading/MPSClassLoadersRegistry.java
@@ -26,8 +26,6 @@
 import org.jetbrains.mps.openapi.module.SModuleReference;
 import org.jetbrains.mps.openapi.util.Consumer;
 
-import java.time.Duration;
-import java.time.Instant;
 import java.util.Collection;
 import java.util.Collections;
 import java.util.HashMap;
@@ -232,12 +230,12 @@
     private final Set<ReloadableModule> myModulesToUnload;
     private final Set<MPSModuleClassLoader> myModuleClassloaders2Dispose;
     private final ConcurrentMap<Object, Boolean> myBlockingRequestors = new ConcurrentHashMap<>();
-    private final Instant myCreationTime;
+    private final Long myCreationTime;
     private final Consumer<DisposeSession> myOnDisposed;
     private final int myActualNumberOfSessions;
     private volatile boolean myDisposeHappened = false;
-    private volatile Instant myPlanningDisposalTime;
-    private volatile Instant myActualDisposalTime;
+    private volatile Long myPlanningDisposalTime;
+    private volatile Long myActualDisposalTime;
 
     private final ResourceTrackerCallback myTrackerCallback = new ResourceTrackerCallback() {
       @NotNull
@@ -266,7 +264,7 @@
                           int actualNumberOfSessions) {
       myOnDisposed = onDisposed;
       myActualNumberOfSessions = actualNumberOfSessions;
-      myCreationTime = Instant.now();
+      myCreationTime = System.currentTimeMillis();
       myModulesToUnload = modulesToUnload;
       myModuleClassloaders2Dispose = theirClassloaders;
     }
@@ -300,7 +298,7 @@
     public synchronized void readyToDispose() {
       assert myCreationTime != null;
       assert myPlanningDisposalTime == null;
-      myPlanningDisposalTime = Instant.now();
+      myPlanningDisposalTime = System.currentTimeMillis();
       checkAndDisposeIfReady();
     }
 
@@ -310,8 +308,8 @@
     }
 
     public synchronized void destroy() {
-      Instant now = Instant.now();
-      if (!myDisposeHappened && Duration.between(myPlanningDisposalTime, now).toMinutes() > MAX_MINUTES_FOR_STALE_CLASSLOADERS) {
+      Long now = System.currentTimeMillis();
+      if (!myDisposeHappened && ((now - myPlanningDisposalTime) / 60000L) > MAX_MINUTES_FOR_STALE_CLASSLOADERS) {
         Set<Object> blockers = myBlockingRequestors.keySet();
         LOG.error(String.format("The following requestors have not invoked #release and probably are leaking ModuleClassLoaders: %s", blockers));
       }
@@ -328,7 +326,7 @@
       assert myCreationTime != null;
       assert myPlanningDisposalTime != null;
       assert !myDisposeHappened : "Dispose has already been done.";
-      myActualDisposalTime = Instant.now();
+      myActualDisposalTime = System.currentTimeMillis();
       LOG.debug("Disposing " + myModuleClassloaders2Dispose.size() + " class loaders");
       for (MPSModuleClassLoader classLoader : myModuleClassloaders2Dispose) {
         classLoader.dispose();
```

### F012 MPSModuleClassLoader.java

**Path:** `core/kernel/source/jetbrains/mps/classloading/MPSModuleClassLoader.java`

[Browser file](C:/Codex/mps-j2cl/mps-web/core/kernel/source/jetbrains/mps/classloading/MPSModuleClassLoader.java) · [Compared upstream file](C:/Codex/mps-j2cl/mps/core/kernel/source/jetbrains/mps/classloading/MPSModuleClassLoader.java)

**Classification:** Compiled deployment and reflection. Text delta: +9 / −44 lines; 104 → 69 total lines.

**What changes:** Removes parallel-capable JVM loader registration, replaces formatted identity text, records a Throwable disposal trace, clears browser registrations on disposal and routes checkOpen to checkNotDisposed.

**Behavior and limits:** This provides lifecycle for registered classes/resources. It no longer participates in JVM parallel loading or timestamp-formatted disposal traces.

**To make the replacement unnecessary:** Move compiled-loader lifecycle behind a common upstream abstraction, with the JVM implementation retaining parallel loading and the browser implementation clearing its registries.

Representative excerpts from the compared files; the prose above covers additional changes.

Pinned upstream at line 83

```java

  static {
    registerAsParallelCapable0();
    // XXX I wonder if we do it here, in the base abstract class, do we need to have the same
    //     in subclasses?
  }

  /**
   * MPS has a cyclic delegation classloading model (module A.a1 triggers class B.b which in turn triggers the loading of
   * the class A.a2 in the case when A depends on B and vice versa; the implicit class loading is triggered in the #defineClass invocation
   * in the {@code ModuleClassLoader#loadFromSelf(String)} method).
   *
```

Browser replacement at line 58

```java

  protected final void checkNotDisposed() throws ModuleClassLoaderIsDisposedException {
    if (isDisposed()) {
      throw new ModuleClassLoaderIsDisposedException(getModule(), myDisposeTrace);
    }
  }

  @Override
  protected final void checkOpen() {
    checkNotDisposed();
  }
}
```

### F013 RootClassloaderLookup.java

**Path:** `core/kernel/source/jetbrains/mps/classloading/RootClassloaderLookup.java`

[Browser file](C:/Codex/mps-j2cl/mps-web/core/kernel/source/jetbrains/mps/classloading/RootClassloaderLookup.java) · [Compared upstream file](C:/Codex/mps-j2cl/mps/core/kernel/source/jetbrains/mps/classloading/RootClassloaderLookup.java)

**Classification:** Compiled deployment and reflection. Text delta: +2 / −3 lines; 63 → 62 total lines.

**What changes:** Uses ClassLoader.getSystemClassLoader as the default root instead of ReloadableModule.class.getClassLoader; explicit plugin loaders remain selectable.

**Behavior and limits:** The default browser root is a registry. Classes must already be linked and registered there or in the chosen plugin loader.

**To make the replacement unnecessary:** Inject a root-loader provider upstream or supply accurate class-to-loader ownership through the shared runtime. Preserve explicit plugin-loader precedence.

Complete textual diff

```diff
--- mps/core/kernel/source/jetbrains/mps/classloading/RootClassloaderLookup.java
+++ mps-web/core/kernel/source/jetbrains/mps/classloading/RootClassloaderLookup.java
@@ -19,7 +19,6 @@
 import jetbrains.mps.library.SLibrary;
 import jetbrains.mps.module.ReloadableModule;
 import jetbrains.mps.smodel.MPSModuleOwner;
-import jetbrains.mps.smodel.MPSModuleRepository;
 import org.jetbrains.mps.openapi.module.SModule;
 import org.jetbrains.mps.openapi.module.SRepository;
 
@@ -44,7 +43,7 @@
     SRepository repository = myModule.getRepository();
     if (false == repository instanceof SRepositoryExt) {
       // no idea how/why to look up owners of a module attached to unknown repository.
-      return ReloadableModule.class.getClassLoader();
+      return ClassLoader.getSystemClassLoader();
     }
     repository.getModelAccess().checkReadAccess();
     // XXX may want to refactor this to ModuleRepositoryFacade() to hide getOwners internals of an SRepository
@@ -57,7 +56,7 @@
         }
       }
     }
-    return ReloadableModule.class.getClassLoader();
+    return ClassLoader.getSystemClassLoader();
 
   }
 }
```

### F014 DynamicComponentPlugin.java

**Path:** `core/kernel/source/jetbrains/mps/core/platform/DynamicComponentPlugin.java`

[Browser file](C:/Codex/mps-j2cl/mps-web/core/kernel/source/jetbrains/mps/core/platform/DynamicComponentPlugin.java) · [Compared upstream file](C:/Codex/mps-j2cl/mps/core/kernel/source/jetbrains/mps/core/platform/DynamicComponentPlugin.java)

**Classification:** Weak references and lifecycle. Text delta: +188 / −59 lines; 353 → 482 total lines.

**What changes:** Replaces WeakHashMap, WeakReference and ReferenceQueue bookkeeping with native JavaScript WeakMap values and WeakRef key indexes. Adds access-time sweeping, disposed-state guards and deterministic cleanup of still-live component instances.

**Behavior and limits:** WeakMap values preserve token/value cycles without strongly retaining keys through the index. Native WeakRef and WeakMap are required. Garbage collection does not itself dispose a registered component.

**To make the replacement unnecessary:** Put equivalent weak collections/reference support in the shared JRE, preserving ephemeron behavior, and upstream the explicit component lifecycle fixes. Merely using a strong map would introduce retention; collection timing remains nondeterministic.

Representative excerpts from the compared files; the prose above covers additional changes.

Pinned upstream at line 176

```java
    // map's entry get cleared, and references to factory/listeners get released.
    private final WeakHashMap<TokenImpl, Supplier<?>> myFactories = new WeakHashMap<>();
    // each listener receives unique token that facilitates listener removal when necessary
    // I keep strong reference to the listener as this class might be the only one to hold it
    // (e.g. if it's just a lambda inside some plugin class). I do however expect client code
    // to keep token and discard it appropriately (if not, and contributing class is GCed, I'd
    // expect token to get collected, too, and entry in the map to be removed by WeakHasMap itself)
    private final WeakHashMap<TokenImpl, Consumer<Object>> myListeners = new WeakHashMap<>();
    private final WeakHashMap<TokenImpl, CoreComponent> myInstances = new WeakHashMap<>();

    @Nullable
    /*package*/synchronized <T extends CoreComponent> T instantiate(Class<T> componentClass) {
```

Browser replacement at line 159

```java
    private final WeakStore<Class<?>, List<TokenWeakRef>> myIssuedTokens = new WeakStore<>();
    private final WeakStore<TokenImpl, Supplier<?>> myFactories = new WeakStore<>();
    private final WeakStore<TokenImpl, Consumer<Object>> myListeners = new WeakStore<>();
    private final WeakStore<TokenImpl, CoreComponent> myInstances = new WeakStore<>();
    private boolean myDisposed;

    @Override
    public synchronized void dispose() {
      myDisposed = true;
      try {
        // The outer plugin already removed its current components from this
        // store. Dispose any still-live instance replaced under the same class
```

### F015 BaseQuickFixProvider.java

**Path:** `core/kernel/source/jetbrains/mps/errors/BaseQuickFixProvider.java`

[Browser file](C:/Codex/mps-j2cl/mps-web/core/kernel/source/jetbrains/mps/errors/BaseQuickFixProvider.java) · [Compared upstream file](C:/Codex/mps-j2cl/mps/core/kernel/source/jetbrains/mps/errors/BaseQuickFixProvider.java)

**Classification:** Compiled deployment and reflection. Text delta: +17 / −4 lines; 119 → 132 total lines.

**What changes:** Obtains the deployed ModuleRuntime from LanguageRegistry, loads the quick-fix class through that runtime, and creates it through a no-argument factory instead of reflective construction.

**Behavior and limits:** Missing runtime/class and a factory throwing ClassNotFoundException are deliberately different cases; construction failures keep the existing error/retry path.

**To make the replacement unnecessary:** Generate quick-fix constructors in the module catalog and let upstream BaseQuickFixProvider use a construction/ownership service. Test missing class, missing factory and user constructor exceptions separately.

Complete textual diff

```diff
--- mps/core/kernel/source/jetbrains/mps/errors/BaseQuickFixProvider.java
+++ mps-web/core/kernel/source/jetbrains/mps/errors/BaseQuickFixProvider.java
@@ -15,9 +15,11 @@
  */
 package jetbrains.mps.errors;
 
+import jetbrains.mps.classloading.ModuleClassLoader;
 import jetbrains.mps.logging.Logger;
 import jetbrains.mps.smodel.language.LanguageRegistry;
 import jetbrains.mps.smodel.language.LanguageRuntime;
+import jetbrains.mps.smodel.runtime.ModuleRuntime;
 import jetbrains.mps.util.NameUtil;
 import org.jetbrains.annotations.NotNull;
 import org.jetbrains.annotations.Nullable;
@@ -25,6 +27,7 @@
 
 import java.util.HashMap;
 import java.util.Map;
+import java.util.stream.Stream;
 
 public class BaseQuickFixProvider implements QuickFixProvider {
   private QuickFix_Runtime myQuickFix;
@@ -80,10 +83,20 @@
         myQuickFix = new BadQuickFix(msg);
         return myQuickFix;
       }
-      // XXX it's odd way to utilize module classloader, but LanguageRuntime doesn't provide us anything,
-      //     and I feel it's still better than global module repository and classloader manager as it used to be
-      Class<?> aClass = languageRuntime.getClass().getClassLoader().loadClass(myClassFQName);
-      QuickFix_Runtime quickFix = (QuickFix_Runtime) aClass.getConstructor().newInstance();
+      final ModuleRuntime[] owner = new ModuleRuntime[1];
+      languageRegistry.withModuleRuntime(Stream.of(languageRuntime.getIdentity().getSourceModuleReference()), runtime -> owner[0] = runtime);
+      if (owner[0] == null) {
+        throw new ClassNotFoundException("No deployed module runtime for " + languageNamespace);
+      }
+      Class<?> aClass = owner[0].getModuleClassLoader().loadClass(myClassFQName);
+      QuickFix_Runtime quickFix;
+      try {
+        quickFix = ModuleClassLoader.newInstance(aClass, QuickFix_Runtime.class);
+      } catch (Throwable failure) {
+        // A factory's own ClassNotFoundException is a construction failure,
+        // whereas a failed loader lookup follows the missing-class path below.
+        throw new IllegalStateException("Quick-fix factory failed for " + myClassFQName, failure);
+      }
       for (String s : myMap.keySet()) {
         quickFix.putArgument(s, myMap.get(s));
       }
```

### F016 FileKindIsNotAllowedException.java

**Path:** `core/kernel/source/jetbrains/mps/extapi/persistence/FileKindIsNotAllowedException.java`

[Browser file](C:/Codex/mps-j2cl/mps-web/core/kernel/source/jetbrains/mps/extapi/persistence/FileKindIsNotAllowedException.java) · [Compared upstream file](C:/Codex/mps-j2cl/mps/core/kernel/source/jetbrains/mps/extapi/persistence/FileKindIsNotAllowedException.java)

**Classification:** Java APIs and text processing. Text delta: +2 / −3 lines; 69 → 68 total lines.

**What changes:** Replaces both MessageFormat error-message branches with concatenation, preserving quoted root and FileKind values.

**Behavior and limits:** Only diagnostic construction changes; source-root validation and exception data remain.

**To make the replacement unnecessary:** Implement the required MessageFormat behavior in the shared JRE or adopt the fixed concatenation upstream; compare both constructor/message branches.

Complete textual diff

```diff
--- mps/core/kernel/source/jetbrains/mps/extapi/persistence/FileKindIsNotAllowedException.java
+++ mps-web/core/kernel/source/jetbrains/mps/extapi/persistence/FileKindIsNotAllowedException.java
@@ -18,7 +18,6 @@
 import org.jetbrains.annotations.NotNull;
 import org.jetbrains.mps.annotations.Immutable;
 
-import java.text.MessageFormat;
 
 /**
  * Each {@link FileBasedModelRoot} has a notion of the so-called "allowed file kinds".
@@ -61,9 +60,9 @@
   @NotNull
   public String getMessage() {
     if (myRoot != null) {
-      return MessageFormat.format("Trying to register the source root ''{0}'' with the prohibited FileKind: ''{1}''", myRoot, myDisallowedKind);
+      return "Trying to register the source root '" + myRoot + "' with the prohibited FileKind: '" + myDisallowedKind + "'";
     } else {
-      return MessageFormat.format("The FileKind ''{0}'' is not allowed", myKindName);
+      return "The FileKind '" + myKindName + "' is not allowed";
     }
   }
 }
```

### F017 SourcePaths.java

**Path:** `core/kernel/source/jetbrains/mps/extapi/persistence/SourcePaths.java`

[Browser file](C:/Codex/mps-j2cl/mps-web/core/kernel/source/jetbrains/mps/extapi/persistence/SourcePaths.java) · [Compared upstream file](C:/Codex/mps-j2cl/mps/core/kernel/source/jetbrains/mps/extapi/persistence/SourcePaths.java)

**Classification:** Java APIs and text processing. Text delta: +1 / −3 lines; 154 → 152 total lines.

**What changes:** Replaces the duplicate-source-root MessageFormat expression with concatenation of the same four values.

**Behavior and limits:** The source-root registration algorithm is unchanged.

**To make the replacement unnecessary:** Provide MessageFormat in the shared JRE or upstream the fixed diagnostic. Compare the exact error text and argument conversion behavior.

Complete textual diff

```diff
--- mps/core/kernel/source/jetbrains/mps/extapi/persistence/SourcePaths.java
+++ mps-web/core/kernel/source/jetbrains/mps/extapi/persistence/SourcePaths.java
@@ -20,7 +20,6 @@
 import org.jetbrains.annotations.Nullable;
 import org.jetbrains.mps.annotations.Immutable;
 
-import java.text.MessageFormat;
 import java.util.ArrayList;
 import java.util.LinkedHashMap;
 import java.util.List;
@@ -147,8 +146,7 @@
 
     @NotNull
     public static String getMsg(SourceRoot root, SourceRootKind pathKind, SourceRoot existingRoot, SourceRootKind existingRootKind) {
-      return MessageFormat.format("Trying to register the source root: [{0}, kind {1}] while [{2}, kind {3}] " +
-                                  "is already registered", root, pathKind, existingRoot, existingRootKind);
+      return "Trying to register the source root: [" + root + ", kind " + pathKind + "] while [" + existingRoot + ", kind " + existingRootKind + "] is already registered";
     }
   }
 }
```

### F018 ParseFacility.java

**Path:** `core/kernel/source/jetbrains/mps/generator/cache/ParseFacility.java`

[Browser file](C:/Codex/mps-j2cl/mps-web/core/kernel/source/jetbrains/mps/generator/cache/ParseFacility.java) · [Compared upstream file](C:/Codex/mps-j2cl/mps/core/kernel/source/jetbrains/mps/generator/cache/ParseFacility.java)

**Classification:** Compiled deployment and reflection. Text delta: +1 / −1 lines; 122 → 122 total lines.

**What changes:** Replaces URL.openStream with ClassLoader.openRegisteredResource for the parser's resource input.

**Behavior and limits:** Parsing and stream ownership remain, but the URL must refer to registered buffered bytes. This does not perform a network fetch or host-file read.

**To make the replacement unnecessary:** Add a shared URL handler for registered resources, or inject an upstream resource-opening service. Load remote bytes asynchronously before invoking synchronous parsing.

Complete textual diff

```diff
--- mps/core/kernel/source/jetbrains/mps/generator/cache/ParseFacility.java
+++ mps-web/core/kernel/source/jetbrains/mps/generator/cache/ParseFacility.java
@@ -97,7 +97,7 @@
     if (myFile != null) {
       return myFile.openInputStream();
     }
-    return myUrl.openStream();
+    return ClassLoader.openRegisteredResource(myUrl);
   }
 
   private void closeSilently(InputStream is) {
```

### F019 Message.java

**Path:** `core/kernel/source/jetbrains/mps/messages/Message.java`

[Browser file](C:/Codex/mps-j2cl/mps-web/core/kernel/source/jetbrains/mps/messages/Message.java) · [Compared upstream file](C:/Codex/mps-j2cl/mps/core/kernel/source/jetbrains/mps/messages/Message.java)

**Classification:** Java APIs and text processing. Text delta: +2 / −3 lines; 151 → 150 total lines.

**What changes:** Stores System.currentTimeMillis directly instead of Instant.now, and returns that value instead of toEpochMilli.

**Behavior and limits:** The public creation time remains epoch milliseconds. The shared JRE now supplies the original Instant operations.

**To make the replacement unnecessary:** Trial removal against the existing Instant implementation and message contracts. Retire the generator rewrite and manifest entry together if compilation and browser behavior pass.

Complete textual diff

```diff
--- mps/core/kernel/source/jetbrains/mps/messages/Message.java
+++ mps-web/core/kernel/source/jetbrains/mps/messages/Message.java
@@ -22,7 +22,6 @@
 import org.jetbrains.mps.openapi.model.SNode;
 import org.jetbrains.mps.openapi.module.SModule;
 
-import java.time.Instant;
 
 public class Message implements IMessage {
   private static final Logger LOG = Logger.getLogger(Message.class);
@@ -30,7 +29,7 @@
   private final String mySender;
   private final MessageKind myKind;
   private final String myText;
-  private final Instant myCreationTime = Instant.now();
+  private final long myCreationTime = System.currentTimeMillis();
   private Throwable myException;
   private String myHelpUrl;
   private Object myHintObject;
@@ -93,7 +92,7 @@
 
   @Override
   public long getCreationTime() {
-    return myCreationTime.toEpochMilli();
+    return myCreationTime;
   }
 
   /**
```

### F020 SDKDiscovery.java

**Path:** `core/kernel/source/jetbrains/mps/reloading/SDKDiscovery.java`

[Browser file](C:/Codex/mps-j2cl/mps-web/core/kernel/source/jetbrains/mps/reloading/SDKDiscovery.java) · [Compared upstream file](C:/Codex/mps-j2cl/mps/core/kernel/source/jetbrains/mps/reloading/SDKDiscovery.java)

**Classification:** Files archives and XML. Text delta: +8 / −255 lines; 278 → 31 total lines.

**What changes:** Replaces host JDK discovery, jrt paths, runtime archive searching and release-file parsing with explicit failures in discover, isModularRuntime and getJdkClassesRoots.

**Behavior and limits:** These operations are unavailable; they do not return a fake SDK or empty success. The browser JRE is selected at build time.

**To make the replacement unnecessary:** Make SDK discovery a desktop-only MPS service and exclude its implementation from the browser source profile. Browser editing needs a declared compiled-runtime catalog, not a search of the user's installed JDK.

Representative excerpts from the compared files; the prose above covers additional changes.

Pinned upstream at line 51

```java
public class SDKDiscovery {
  public static List<QualifiedPath> discover() {
    return findJavaRuntimeClasses(new File(System.getProperty("java.home")));
  }

  //------------------------------

  @Nullable
  private static QualifiedPath getJDK_ToolsPath() {
    String toolsJarClass = "com.sun.jdi.Field";
    try {
      Class cls = Class.forName(toolsJarClass);
```

Browser replacement at line 20

```java
import jetbrains.mps.vfs.QualifiedPath;
import org.jetbrains.annotations.NotNull;

/** The browser runtime has no installed JVM or host JDK archives to discover. */
public class SDKDiscovery {
  public static List<QualifiedPath> discover() { throw unavailable(); }
  public static boolean isModularRuntime(@NotNull File homePath) { throw unavailable(); }
  public static List<File> getJdkClassesRoots(@NotNull File home, boolean isJre) { throw unavailable(); }
  private static UnsupportedOperationException unavailable() {
    return new UnsupportedOperationException("Host JDK discovery is unavailable in the browser; J2CL links its JRE at build time");
  }
}
```

### F021 ExtensionRegistry.java

**Path:** `core/kernel/source/jetbrains/mps/smodel/language/ExtensionRegistry.java`

[Browser file](C:/Codex/mps-j2cl/mps-web/core/kernel/source/jetbrains/mps/smodel/language/ExtensionRegistry.java) · [Compared upstream file](C:/Codex/mps-j2cl/mps/core/kernel/source/jetbrains/mps/smodel/language/ExtensionRegistry.java)

**Classification:** Compiled deployment and reflection. Text delta: +2 / −1 lines; 141 → 142 total lines.

**What changes:** Replaces reflective no-argument extension-descriptor construction with ModuleClassLoader.newInstance(clazz, Object.class).

**Behavior and limits:** Existing descriptor naming and exception handling remain. Every descriptor needs a registered constructor.

**To make the replacement unnecessary:** Generate descriptor factories and inject a construction service upstream, or implement the exact reflective constructor subset using generated metadata.

Complete textual diff

```diff
--- mps/core/kernel/source/jetbrains/mps/smodel/language/ExtensionRegistry.java
+++ mps-web/core/kernel/source/jetbrains/mps/smodel/language/ExtensionRegistry.java
@@ -18,6 +18,7 @@
 import jetbrains.mps.classloading.ClassLoaderManager;
 import jetbrains.mps.classloading.DeployListener;
 import jetbrains.mps.classloading.MPSModuleClassLoader;
+import jetbrains.mps.classloading.ModuleClassLoader;
 import jetbrains.mps.components.ComponentHost;
 import jetbrains.mps.components.CoreComponent;
 import jetbrains.mps.logging.Logger;
@@ -132,7 +133,7 @@
   private static Object getObjectByClassName(String className, MPSModuleClassLoader moduleCL) {
     try {
       Class<?> clazz = moduleCL.loadOwnClass(className);
-      return clazz.getDeclaredConstructor().newInstance();
+      return ModuleClassLoader.newInstance(clazz, Object.class);
     } catch (Throwable e) {
       LOG.trace("error loading class\"" + className + "\"", e);
     }
```

### F022 LanguageRegistry.java

**Path:** `core/kernel/source/jetbrains/mps/smodel/language/LanguageRegistry.java`

[Browser file](C:/Codex/mps-j2cl/mps-web/core/kernel/source/jetbrains/mps/smodel/language/LanguageRegistry.java) · [Compared upstream file](C:/Codex/mps-j2cl/mps/core/kernel/source/jetbrains/mps/smodel/language/LanguageRegistry.java)

**Classification:** Compiled deployment and reflection. Text delta: +130 / −38 lines; 836 → 928 total lines.

**What changes:** Replaces language and generator reflection with typed factories, preferring the current three-argument generator constructor over the legacy two-argument one. Adds registerCompiledLanguage/unregisterCompiledLanguage, dependency metadata, activation, extension contributions, rollback, unload tracking and notifications. Replaces one Class.cast method reference with a checked-filter plus generic cast.

**Behavior and limits:** Direct deployment accepts already linked code, checks duplicate identities and unload order, and preserves construction-failure distinctions. This is a substantial new deployment path, not just a reflection rewrite.

**To make the replacement unnecessary:** Upstream a runtime deployment API independent of JVM classpaths, generate its catalogs, and keep JVM/browser providers. Restore factory/reflection callers only when their construction, rollback, notification and unload-token contracts are covered.

Representative excerpts from the compared files; the prose above covers additional changes.

Pinned upstream at line 288

```java
        }
        Constructor<?>[] allConstructors = aClass.getConstructors();
        // Provisional constructor for TemplateModule, that takes LanguageRegistry, LanguageRuntime and Generator module. @since 2018.1
        // It's to substitute new TemplateModuleInterpreted from the LanguageRuntime subclass. We need it until all the generators are completely
        // generated. Meanwhile we have a 'partially' interpreted generators, with TemplateModule/GeneratorRuntime being generated always.
        for (Constructor<?> cons : allConstructors) {
          if (cons.getParameterCount() != 3) {
            continue;
          }
          Class<?>[] parameterTypes = cons.getParameterTypes();
          if (parameterTypes[0] == LanguageRegistry.class && parameterTypes[1] == LanguageRuntime.class && parameterTypes[2] == Generator.class) {
            Constructor<? extends GeneratorRuntime> c = aClass.getConstructor(LanguageRegistry.class, LanguageRuntime.class, Generator.class);
```

Browser replacement at line 393

```java
        // generated. Meanwhile we have a 'partially' interpreted generators, with TemplateModule/GeneratorRuntime being generated always.
        Class<?>[] parameterTypes = {LanguageRegistry.class, LanguageRuntime.class, Generator.class};
        Object[] arguments = {this, sourceLanguageRuntime, g};
        if (!ModuleClassLoader.hasFactory(rtClass, parameterTypes)) {
          // Legacy constructor for TemplateModule. @since 2017.1
          parameterTypes = new Class<?>[]{LanguageRegistry.class, LanguageRuntime.class};
          arguments = new Object[]{this, sourceLanguageRuntime};
          if (!ModuleClassLoader.hasFactory(rtClass, parameterTypes)) {
            LOG.error(String.format("No proper constructor found in the class %s of generator %s", rtClassName, g.getModuleName()));
            return null;
          }
        }
```

Additional browser code at line 125

```java
   */
  public void registerCompiledLanguage(@NotNull LanguageRuntime language, @NotNull ClassLoader loader,
                                      @NotNull Collection<SModuleReference> runtimeModules,
                                      @NotNull Collection<SModuleReference> generationTargets) {
    Objects.requireNonNull(language);
    Objects.requireNonNull(loader);
    final SModuleReference reference = language.getIdentity().getSourceModuleReference();
    final ModuleRuntime module = new ModuleRuntime(reference, loader,
        (mr, ctx) -> new LanguageRuntimeActivator(language), Flags.WithExtensions);
    final Set<SLanguageId> extensionTargets = new HashSet<>();
    myRuntimeInstanceAccess.writeLock().lock();
    try {
```

### F023 ModuleRuntime.java

**Path:** `core/kernel/source/jetbrains/mps/smodel/runtime/ModuleRuntime.java`

[Browser file](C:/Codex/mps-j2cl/mps-web/core/kernel/source/jetbrains/mps/smodel/runtime/ModuleRuntime.java) · [Compared upstream file](C:/Codex/mps-j2cl/mps/core/kernel/source/jetbrains/mps/smodel/runtime/ModuleRuntime.java)

**Classification:** Compiled deployment and reflection. Text delta: +18 / −15 lines; 376 → 379 total lines.

**What changes:** Opens resources through the registered-resource API, constructs ModuleActivator through a ComponentHost factory, and replaces an activator Class.cast with a generic cast after the existing type check.

**Behavior and limits:** A missing activator class stays optional; a present class without the required factory is an error. Registered bytes are real streams, not URL metadata alone.

**To make the replacement unnecessary:** Share construction and resource services upstream. A resource URL handler can remove the stream substitution, but activator reflection still needs a factory-aware service or generated reflection metadata.

Complete textual diff

```diff
--- mps/core/kernel/source/jetbrains/mps/smodel/runtime/ModuleRuntime.java
+++ mps-web/core/kernel/source/jetbrains/mps/smodel/runtime/ModuleRuntime.java
@@ -28,8 +28,6 @@
 import java.io.FileNotFoundException;
 import java.io.IOException;
 import java.io.InputStream;
-import java.lang.reflect.Constructor;
-import java.lang.reflect.InvocationTargetException;
 import java.net.URL;
 import java.util.ArrayList;
 import java.util.Arrays;
@@ -109,7 +107,7 @@
       resource = myModuleClassLoader.getResource(fqn);
     }
     if (resource != null) {
-      return resource.openStream();
+      return ClassLoader.openRegisteredResource(resource);
     }
     throw new FileNotFoundException(fqn);
   }
@@ -216,7 +214,8 @@
   @Internal
   public <T extends Activator> void forActivatorIfInstance(@NotNull Class<T> activatorClass, Consumer<T> code) {
     if (activatorClass.isInstance(myModuleActivator)) {
-      code.accept(activatorClass.cast(myModuleActivator));
+      @SuppressWarnings("unchecked") T activator = (T) myModuleActivator;
+      code.accept(activator);
     }
   }
 
@@ -351,21 +350,25 @@
       // provisional code at the moment, DO NOT TREAT AS API, just need to work around a limitation while fixing another issue
       // shall take generated activator class (or even few, perhaps?), instantiate and execute it inside try {} catch (Throwable)
       final String cn = moduleRuntime.getSourceModule().getModuleName() + ".ModuleActivator";
-      try {
-        final Class<?> activatorClass = moduleRuntime.getOwnClass(cn);
-        if (!Activator.class.isAssignableFrom(activatorClass)) {
-          throw new IllegalStateException(String.format("Class %s is not instance of MR.Activator, ignored", cn));
-        }
-        Constructor<? extends Activator> cc = activatorClass.asSubclass(Activator.class).getConstructor(ComponentHost.class);
-        return cc.newInstance(context.getComponentHost());
-      } catch (IllegalAccessException | InstantiationException | InvocationTargetException ex) {
-        throw new IllegalStateException(String.format("Failed to instantiate activator %s", cn), ex);
-      } catch (NoSuchMethodException | SecurityException ex) {
-        throw new IllegalStateException(String.format("Constructor of activator class %s is not available:%s", cn, ex.getMessage()));
+      final Class<?> activatorClass;
+      try {
+        activatorClass = moduleRuntime.getOwnClass(cn);
       } catch (ClassNotFoundException ex) {
         // ignore; expected scenario as long as we try to guess name of activator class;
         return null;
       }
+      if (!Activator.class.isAssignableFrom(activatorClass)) {
+        throw new IllegalStateException("Class " + cn + " is not instance of MR.Activator, ignored");
+      }
+      if (!ModuleClassLoader.hasFactory(activatorClass, ComponentHost.class)) {
+        throw new IllegalStateException("Constructor of activator class " + cn + " is not available: no registered ComponentHost factory");
+      }
+      try {
+        return ModuleClassLoader.newInstance(activatorClass, Activator.class,
+            new Class<?>[]{ComponentHost.class}, context.getComponentHost());
+      } catch (Throwable ex) {
+        throw new IllegalStateException("Failed to instantiate activator " + cn, ex);
+      }
     }
   }
 
```

### F024 IconCreationUtil.java

**Path:** `core/kernel/source/jetbrains/mps/util/IconCreationUtil.java`

[Browser file](C:/Codex/mps-j2cl/mps-web/core/kernel/source/jetbrains/mps/util/IconCreationUtil.java) · [Compared upstream file](C:/Codex/mps-j2cl/mps/core/kernel/source/jetbrains/mps/util/IconCreationUtil.java)

**Classification:** Icon resources and encoding. Text delta: +92 / −14 lines; 74 → 152 total lines.

**What changes:** Retains AWT drawing but replaces ImageIO with a synchronous RGBA PNG writer: scanlines, IHDR/IDAT/IEND, stored DEFLATE blocks, Adler checksum and CRC. Graphics disposal moves into finally.

**Behavior and limits:** Produces real lossless PNG bytes without compression optimization. This is useful reusable image encoding embedded in an MPS class.

**To make the replacement unnecessary:** Move PNG/ImageIO compatibility into the owning graphics/image library and restore the ImageIO call; upstream the finally cleanup separately. Verify pixels, alpha and PNG decoding, not only the signature bytes.

Representative excerpts from the compared files; the prose above covers additional changes.

Pinned upstream at line 53

```java
      try (ImageOutputStream ios = ImageIO.createImageOutputStream(baos)) {
        ImageIO.write(image, "png", ios);
      }
      return baos.toByteArray();
    } catch (IOException ex) {
      LOG.error("Can't produce icon", ex);
      return null;
    }
  }

  public static class DrawContext {
    public Graphics2D g;
```

Browser replacement at line 81

```java
    header.write(0); // No interlacing.
    writeChunk(png, "IHDR", header.toByteArray());
    writeChunk(png, "IDAT", storedZlib(scanlines));
    writeChunk(png, "IEND", new byte[0]);
    return png.toByteArray();
  }

  /** Valid zlib/DEFLATE stream with stored blocks; small icons need no compression engine. */
  private static byte[] storedZlib(byte[] bytes) {
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    output.write(0x78); // DEFLATE, 32 KiB window.
    output.write(0x01); // No dictionary, fastest strategy, valid modulo-31 header check.
```

### F025 JDOMUtil.java

**Path:** `core/kernel/source/jetbrains/mps/util/JDOMUtil.java`

[Browser file](C:/Codex/mps-j2cl/mps-web/core/kernel/source/jetbrains/mps/util/JDOMUtil.java) · [Compared upstream file](C:/Codex/mps-j2cl/mps/core/kernel/source/jetbrains/mps/util/JDOMUtil.java)

**Classification:** Files archives and XML. Text delta: +45 / −42 lines; 332 → 335 total lines.

**What changes:** Replaces JAXP SAXParser discovery with BrowserSAXParser delegating to org.jdom.mps.MpsXml. Stream parsing passes bytes to the portable parser; host File/path read-write overloads fail. Output streams use the portable outputter and close, UTF-8 and LF are explicit, and MyXMLOutputter has an explicit clone implementation.

**Behavior and limits:** The createSAXParser return type changes; callers declaring javax.xml.parsers.SAXParser are outside this API subset. Buffered XML works, host filesystem operations and arbitrary outputter-subclass cloning do not.

**To make the replacement unnecessary:** Add shared JAXP/SAX adapters and stream encoding support to JDOM/JRE, and separate file providers from XML processing upstream. Preserve stream closing and error behavior; do not infer file persistence from successful in-memory XML parsing.

Representative excerpts from the compared files; the prose above covers additional changes.

Pinned upstream at line 54

```java

  public static SAXParser createSAXParser() throws SAXException, ParserConfigurationException {
    if (factory == null) {
      factory = SAXParserFactory.newInstance();
    }
    return factory.newSAXParser();
  }

  public static Document loadDocument(IFile file) throws JDOMException, IOException {
    SAXBuilder saxBuilder = createBuilder();
    try (InputStream in = file.openInputStream()) {
      return saxBuilder.build(new InputStreamReader(in, FileUtil.DEFAULT_CHARSET));
```

Browser replacement at line 49

```java

  public static BrowserSAXParser createSAXParser() throws SAXException {
    return new BrowserSAXParser();
  }

  /** Keeps the MPS parse(source, handler) call shape without JAXP provider discovery. */
  public static final class BrowserSAXParser {
    private BrowserSAXParser() {
    }

    public void parse(InputSource source, DefaultHandler handler) throws SAXException, IOException {
      MpsXml.parse(source, handler);
```

### F026 PathManager.java

**Path:** `core/kernel/source/jetbrains/mps/util/PathManager.java`

[Browser file](C:/Codex/mps-j2cl/mps-web/core/kernel/source/jetbrains/mps/util/PathManager.java) · [Compared upstream file](C:/Codex/mps-j2cl/mps/core/kernel/source/jetbrains/mps/util/PathManager.java)

**Classification:** Files archives and XML. Text delta: +12 / −45 lines; 176 → 143 total lines.

**What changes:** Replaces locating MPS home through Launcher/PathManager class resources and jar URIs with a configured logical home path, adds configureHomePath, and removes launcher-directory discovery.

**Behavior and limits:** Missing configuration fails; changing an already configured home is rejected. A logical path is not a mounted filesystem.

**To make the replacement unnecessary:** Inject installation/resource roots upstream. A browser host can provide them from a deployment manifest; filesystem discovery remains a JVM provider responsibility.

Representative excerpts from the compared files; the prose above covers additional changes.

Pinned upstream at line 44

```java
   */
  public static String getHomePath() {
    // [AT] it's odd to use different PathManager with different idea about 'home path' from various parts of MPS
    if (ourHomePath != null) {
      return ourHomePath;
    }

//    ContainingJar(PAthManager.class): /.../mps/lib/mps-core.jar
//    CL.SystemResource(Launcher):      file:/.../mps/startup/classes/jetbrains/mps/Launcher.class
//    CL.SystemResource(PathManager):   jar:file:/.../mps/lib/mps-core.jar!/jetbrains/mps/util/PathManager.class
    try {
      // we know PathManager.class is part of [kernel], which always goes into lib/mps-core.jar (in sources - as an IDEA project artifact)
```

Browser replacement at line 44

```java
   */
  public static String getHomePath() {
    if (ourHomePath == null) {
      String configured = System.getProperty("mps.home.path", null);
      if (configured == null || configured.isEmpty()) {
        throw new IllegalStateException("Configure the browser application's logical MPS installation root with PathManager.configureHomePath");
      }
      ourHomePath = configured;
    }
    return ourHomePath;
  }

```

### F027 PatternUtil.java

**Path:** `core/kernel/source/jetbrains/mps/util/PatternUtil.java`

[Browser file](C:/Codex/mps-j2cl/mps-web/core/kernel/source/jetbrains/mps/util/PatternUtil.java) · [Compared upstream file](C:/Codex/mps-j2cl/mps/core/kernel/source/jetbrains/mps/util/PatternUtil.java)

**Classification:** Java APIs and text processing. Text delta: +5 / −14 lines; 145 → 136 total lines.

**What changes:** Replaces Java regex quote blocks in generated completion patterns with per-character literal escaping and replaces whitespace shorthand with the explicit Java ASCII whitespace class.

**Behavior and limits:** The pattern-building state machine remains except quote-block handling. The current shared regex translator already documents quote blocks and ASCII whitespace handling.

**To make the replacement unnecessary:** Trial the original completion patterns against the current shared regex implementation, including literal metacharacters and whitespace. If parity holds, remove this overlay and its generator rewrite.

Complete textual diff

```diff
--- mps/core/kernel/source/jetbrains/mps/util/PatternUtil.java
+++ mps-web/core/kernel/source/jetbrains/mps/util/PatternUtil.java
@@ -49,28 +49,15 @@
       char c = text.charAt(i);
       state = appendNextChar(c, state, b, useDots, useStarAndQuestionMark);
     }
-    if (state == State.QUOTING) {
-      b.append("\\E");
-    }
     return b;
   }
 
   private static State appendNextChar(char c, State state, StringBuilder b, boolean useDots, boolean useStarAndQuestionMark) {
     if (state == State.SEQUENCE_LETTERS && Character.isUpperCase(c)) {
       b.append("[a-z0-9_]*");
-      b.append("((").append(c).append(")|((\\s)+[").append(Character.toLowerCase(c)).append(c).append("]))");
+      b.append("((").append(c).append(")|(([ \\t\\n\\x0B\\f\\r])+[").append(Character.toLowerCase(c)).append(c).append("]))");
       return State.SEQUENCE_LETTERS;
     }
-    if (c == '*' || c == '?' || c == '.' || c == '@' || Character.isLetterOrDigit(c) || c == '_') {
-      if (state == State.QUOTING) {
-        b.append("\\E");
-      }
-    } else {
-      if (state != State.QUOTING) {
-        b.append("\\Q");
-      }
-    }
-
     if (c == '*') {
       if (useStarAndQuestionMark) {
         b.append(".*");
@@ -99,6 +86,10 @@
       b.append(c);
       return State.SEQUENCE_LETTERS;
     } else {
+      // Escape literal metacharacters; JavaScript does not support Java's quote blocks.
+      if ("\\^$.*+?()[]{}|".indexOf(c) >= 0) {
+        b.append('\\');
+      }
       b.append(c);
       return State.QUOTING;
     }
```

### F028 QueryMethods.java

**Path:** `core/kernel/source/jetbrains/mps/util/QueryMethods.java`

[Browser file](C:/Codex/mps-j2cl/mps-web/core/kernel/source/jetbrains/mps/util/QueryMethods.java) · [Compared upstream file](C:/Codex/mps-j2cl/mps/core/kernel/source/jetbrains/mps/util/QueryMethods.java)

**Classification:** Compiled deployment and reflection. Text delta: +178 / −47 lines; 97 → 228 total lines.

**What changes:** Replaces getDeclaredMethods/Method invocation with an explicit callback registry keyed by Class identity and method name. Keeps construction-time snapshots, adds closeable descriptors and type conversion checks, and changes getMethodPrim's return type to RegisteredMethod.

**Behavior and limits:** Only registered public static one-context callbacks are represented. Callback exceptions retain InvocationTargetException, primitive widening is supported, and closing a registration invalidates earlier snapshots. Closing is not automatically tied to loader disposal.

**To make the replacement unnecessary:** Have generators emit typed query catalogs and let upstream QueryMethods use a backend-neutral descriptor interface. A Method-compatible runtime would need generated metadata plus invocation support; renaming the wrapper alone does not restore reflection.

Representative excerpts from the compared files; the prose above covers additional changes.

Pinned upstream at line 42

```java
    HashMap<String, Method> methods = new HashMap<>();
    for (Method declaredMethod : myQueriesClass.getDeclaredMethods()) {
      if (!Modifier.isPublic(declaredMethod.getModifiers())) {
        continue;
      }
      String name = declaredMethod.getName();
      declaredMethod.setAccessible(true);
      methods.put(name, declaredMethod);
    }
    myMethods = methods;
  }

```

Browser replacement at line 48

```java
   */
  public static <C, T> RegisteredMethod<C, T> register(Class<?> queriesClass, String methodName,
                                                    Class<C> contextType, Class<T> returnType,
                                                    QueryBody<? super C, ? extends T> body) {
    Objects.requireNonNull(queriesClass, "queriesClass");
    Objects.requireNonNull(methodName, "methodName");
    Objects.requireNonNull(contextType, "contextType");
    Objects.requireNonNull(returnType, "returnType");
    Objects.requireNonNull(body, "body");
    if (methodName.isEmpty() || contextType == void.class) {
      throw new IllegalArgumentException("A query needs a method name and one non-void context parameter");
    }
```

### F029 UnzipUtil.java

**Path:** `core/kernel/source/jetbrains/mps/util/UnzipUtil.java`

[Browser file](C:/Codex/mps-j2cl/mps-web/core/kernel/source/jetbrains/mps/util/UnzipUtil.java) · [Compared upstream file](C:/Codex/mps-j2cl/mps/core/kernel/source/jetbrains/mps/util/UnzipUtil.java)

**Classification:** Files archives and XML. Text delta: +4 / −25 lines; 47 → 26 total lines.

**What changes:** Replaces the ZipInputStream loop and output-directory/file writes with UnsupportedOperationException.

**Behavior and limits:** Extraction is explicitly unavailable, not implemented by the lexical File API.

**To make the replacement unnecessary:** Provide archive decoding and an explicit writable browser storage backend, or separate extraction into a desktop/archive service excluded from this profile. Keep asynchronous file acquisition outside synchronous model operations.

Complete textual diff

```diff
--- mps/core/kernel/source/jetbrains/mps/util/UnzipUtil.java
+++ mps-web/core/kernel/source/jetbrains/mps/util/UnzipUtil.java
@@ -15,33 +15,12 @@
  */
 package jetbrains.mps.util;
 
-import java.io.*;
-import java.util.zip.ZipEntry;
-import java.util.zip.ZipInputStream;
+import java.io.File;
+import java.io.IOException;
 
-/**
- * @author Kostik
- */
+/** Host file extraction requires an archive/filesystem backend. */
 public class UnzipUtil {
-
   public static void unzip(File zipFile, File outputDir) throws IOException {
-    InputStream input = new BufferedInputStream(new FileInputStream(zipFile));
-    ZipInputStream zipInput = new ZipInputStream(input);
-    ZipEntry entry;
-    while ((entry = zipInput.getNextEntry()) != null) {
-      File entryFile = new File(outputDir.getPath() + File.separatorChar + entry.getName());
-      if (entry.isDirectory()) {
-        entryFile.mkdirs();
-      } else {
-        entryFile.getParentFile().mkdirs();
-        FileOutputStream output = new FileOutputStream(entryFile, false);
-        byte[] b = new byte[512];
-        int len;
-        while ((len = zipInput.read(b)) != -1) {
-          output.write(b, 0, len);
-        }
-        output.close();
-      }
-    }
+    throw new UnsupportedOperationException("Extracting a ZIP into host files is unavailable in the browser");
   }
 }
```

### F030 openapi2.iml

**Path:** `core/openapi/openapi2.iml`

[Browser file](C:/Codex/mps-j2cl/mps-web/core/openapi/openapi2.iml) · No same-path file at the pinned upstream revision

**Classification:** IntelliJ project metadata. Text delta: +12 / −0 lines; 0 → 12 total lines.

**What changes:** Added IntelliJ module descriptor: no file exists at this exact path in the pinned upstream tree. Indexes the overlay source root(s) `source` with inherited compiler output. Declares 1 module dependencies. The complete XML or descriptor diff below records every dependency name.

**Behavior and limits:** The root .idea/modules.xml references this file, so it affects IDE indexing. It is not an MPS_OVERRIDES entry and is not used to select Bazel Java sources. Local module names such as kernel2 or webswing are IDE identifiers, not proof of Bazel dependency ownership.

**To make the replacement unnecessary:** Generate the IDE project from the authoritative selected-source/build graph, or keep equivalent descriptors outside the overlay. Update the root IDE module references before relocating metadata. Removing this file alone would break that IDE reference and would not reduce the 100 Java replacements.

Current module descriptor

```xml
<?xml version="1.0" encoding="UTF-8"?>
<module type="JAVA_MODULE" version="4">
  <component name="NewModuleRootManager" inherit-compiler-output="true">
    <exclude-output />
    <content url="file://$MODULE_DIR$">
      <sourceFolder url="file://$MODULE_DIR$/source" isTestSource="false" />
    </content>
    <orderEntry type="inheritedJdk" />
    <orderEntry type="sourceFolder" forTests="false" />
    <orderEntry type="module" module-name="openapi" />
  </component>
</module>
```

### F031 SModelName.java

**Path:** `core/openapi/source/org/jetbrains/mps/openapi/model/SModelName.java`

[Browser file](C:/Codex/mps-j2cl/mps-web/core/openapi/source/org/jetbrains/mps/openapi/model/SModelName.java) · [Compared upstream file](C:/Codex/mps-j2cl/mps/core/openapi/source/org/jetbrains/mps/openapi/model/SModelName.java)

**Classification:** Java APIs and text processing. Text delta: +247 / −9 lines; 393 → 631 total lines.

**What changes:** Replaces ResourceBundle messages with a fixed English switch and SourceVersion.isName with code-point validation using pinned Java identifier-start/part ranges and keywords; also embeds the model-name exception text.

**Behavior and limits:** Unicode tables and keyword policy are tied to the reviewed JVM baseline. This is more than ASCII validation; locale switching is not provided.

**To make the replacement unnecessary:** Move Java-name validation into a shared compiler-model/Unicode library or implement SourceVersion's required subset; package bundles through a shared resource service. Regenerate and compare identifier tables when the target Java version changes.

Representative excerpts from the compared files; the prose above covers additional changes.

Pinned upstream at line 343

```java
  private static SModelNameCheck checkValidJavaPackage(@NotNull CharSequence longName) {
    if (!SourceVersion.isName(longName)) {
      return SModelNameCheck.InvalidJavaPackage;
    }
    return SModelNameCheck.Pass;
  }

  /**
   * Checks if SModelName can be constructed with such input
   */
  @NotNull
  public static SModelNameCheck checkModelName(final String fullName) {
```

Browser replacement at line 340

```java
  private static SModelNameCheck checkValidJavaPackage(@NotNull CharSequence longName) {
    if (!isJavaName(longName.toString())) {
      return SModelNameCheck.InvalidJavaPackage;
    }
    return SModelNameCheck.Pass;
  }

  /**
   * Checks if SModelName can be constructed with such input
   */
  @NotNull
  public static SModelNameCheck checkModelName(final String fullName) {
```

Additional browser code at line 413

```java

  private static boolean isJavaName(String value) {
    if (value.isEmpty()) return false;
    int start = 0;
    for (int end = 0; end <= value.length(); end++) {
      if (end != value.length() && value.charAt(end) != '.') continue;
      if (end == start) return false;
      String identifier = value.substring(start, end);
      for (String keyword : JAVA_KEYWORDS) {
        if (keyword.equals(identifier)) return false;
      }
      int codepoint = value.codePointAt(start);
```

### F032 DataSourceNotSupportedProblem.java

**Path:** `core/openapi/source/org/jetbrains/mps/openapi/persistence/DataSourceNotSupportedProblem.java`

[Browser file](C:/Codex/mps-j2cl/mps-web/core/openapi/source/org/jetbrains/mps/openapi/persistence/DataSourceNotSupportedProblem.java) · [Compared upstream file](C:/Codex/mps-j2cl/mps/core/openapi/source/org/jetbrains/mps/openapi/persistence/DataSourceNotSupportedProblem.java)

**Classification:** Java APIs and text processing. Text delta: +1 / −1 lines; 37 → 37 total lines.

**What changes:** Replaces a single String.format call in the unsupported-data-source diagnostic with concatenation.

**Behavior and limits:** No persistence behavior changes. Current MpsFormatter already supports %s, so the original missing-format rationale may no longer require an overlay.

**To make the replacement unnecessary:** Trial removal using the existing shared formatter and compare exact diagnostic output, including null values.

Complete textual diff

```diff
--- mps/core/openapi/source/org/jetbrains/mps/openapi/persistence/DataSourceNotSupportedProblem.java
+++ mps-web/core/openapi/source/org/jetbrains/mps/openapi/persistence/DataSourceNotSupportedProblem.java
@@ -32,6 +32,6 @@
   @NotNull
   @Override
   public String getDescription() {
-    return String.format("The data source '%s' is not supported", mySource);
+    return "The data source '" + mySource + "' is not supported";
   }
 }
```

### F033 ModelFactory.java

**Path:** `core/openapi/source/org/jetbrains/mps/openapi/persistence/ModelFactory.java`

[Browser file](C:/Codex/mps-j2cl/mps-web/core/openapi/source/org/jetbrains/mps/openapi/persistence/ModelFactory.java) · [Compared upstream file](C:/Codex/mps-j2cl/mps/core/openapi/source/org/jetbrains/mps/openapi/persistence/ModelFactory.java)

**Classification:** Java APIs and text processing. Text delta: +1 / −1 lines; 179 → 179 total lines.

**What changes:** Replaces one mandatory-save-option String.format diagnostic with concatenation.

**Behavior and limits:** Mandatory-option handling remains intact; the current shared formatter supports this %s call.

**To make the replacement unnecessary:** Trial removal against the current JRE formatter, preserving failure type and message for an unhandled mandatory option.

Complete textual diff

```diff
--- mps/core/openapi/source/org/jetbrains/mps/openapi/persistence/ModelFactory.java
+++ mps-web/core/openapi/source/org/jetbrains/mps/openapi/persistence/ModelFactory.java
@@ -128,7 +128,7 @@
   default void save(@NotNull SModel model, @NotNull DataSource dataSource, @Nullable ModelSaveOption ... options) throws ModelSaveException {
     final ModelSaveOption mandatoryOption = options != null ? Arrays.stream(options).filter(ModelSaveOption::mandatory).findFirst().orElse(null) : null;
     if (mandatoryOption != null) {
-      final String m = String.format("Could not handle mandatory save option %s", mandatoryOption);
+      final String m = "Could not handle mandatory save option " + mandatoryOption;
       throw new ModelSaveException(m, Collections.emptySet());
     }
     try {
```

### F034 project.iml

**Path:** `core/project/project.iml`

[Browser file](C:/Codex/mps-j2cl/mps-web/core/project/project.iml) · [Compared upstream file](C:/Codex/mps-j2cl/mps/core/project/project.iml)

**Classification:** IntelliJ project metadata. Text delta: +23 / −9 lines; 20 → 34 total lines.

**What changes:** Customized IntelliJ descriptor with a same-path upstream counterpart. Indexes the overlay source root(s) `source` with inherited compiler output. Declares 22 module dependencies and 1 project libraries. The complete XML or descriptor diff below records every dependency name. The upstream source/test root(s) `source_gen` are absent here. Added module references: `browser`, `core`, `kernel2`, `kernelSolution`, `kernelSolution2`, `logging2`, `mps-platform`, `mps-platform2`, `mps-workbench`, `openapi`, `openapi2`, `persistence`, `project-check`, `project3`, `smodel`, `smodel2`, `testbench`, `util2`, `util4`, `vfs`, `vfs2`. Removed module references: `closures-runtime`, `collections-runtime`, `components`.

**Behavior and limits:** The root .idea/modules.xml references this file, so it affects IDE indexing. It is not an MPS_OVERRIDES entry and is not used to select Bazel Java sources. Local module names such as kernel2 or webswing are IDE identifiers, not proof of Bazel dependency ownership.

**To make the replacement unnecessary:** Generate the IDE project from the authoritative selected-source/build graph, or keep equivalent descriptors outside the overlay. Update the root IDE module references before relocating metadata. Removing this file alone would break that IDE reference and would not reduce the 100 Java replacements.

Complete descriptor diff

```diff
--- mps/core/project/project.iml
+++ mps-web/core/project/project.iml
@@ -1,20 +1,34 @@
 <?xml version="1.0" encoding="UTF-8"?>
 <module type="JAVA_MODULE" version="4">
-  <component name="NewModuleRootManager">
-    <output url="file://$MODULE_DIR$/classes" />
-    <output-test url="file://$MODULE_DIR$/testclasses" />
+  <component name="NewModuleRootManager" inherit-compiler-output="true">
     <exclude-output />
     <content url="file://$MODULE_DIR$">
       <sourceFolder url="file://$MODULE_DIR$/source" isTestSource="false" />
-      <sourceFolder url="file://$MODULE_DIR$/source_gen" isTestSource="false" />
     </content>
     <orderEntry type="inheritedJdk" />
     <orderEntry type="sourceFolder" forTests="false" />
+    <orderEntry type="library" name="lib" level="project" />
+    <orderEntry type="module" module-name="mps-platform2" />
+    <orderEntry type="module" module-name="browser" />
+    <orderEntry type="module" module-name="util4" />
+    <orderEntry type="module" module-name="logging2" />
+    <orderEntry type="module" module-name="mps-platform" />
+    <orderEntry type="module" module-name="smodel2" />
+    <orderEntry type="module" module-name="mps-workbench" />
     <orderEntry type="module" module-name="kernel" />
-    <orderEntry type="module" module-name="closures-runtime" />
-    <orderEntry type="module" module-name="collections-runtime" />
-    <orderEntry type="module" module-name="components" />
-    <orderEntry type="library" name="org.jdom" level="project" />
-    <orderEntry type="library" name="gnu.trove" level="project" />
+    <orderEntry type="module" module-name="openapi2" />
+    <orderEntry type="module" module-name="core" />
+    <orderEntry type="module" module-name="smodel" />
+    <orderEntry type="module" module-name="util2" />
+    <orderEntry type="module" module-name="kernelSolution2" />
+    <orderEntry type="module" module-name="kernel2" />
+    <orderEntry type="module" module-name="testbench" />
+    <orderEntry type="module" module-name="openapi" />
+    <orderEntry type="module" module-name="vfs" />
+    <orderEntry type="module" module-name="persistence" />
+    <orderEntry type="module" module-name="project-check" />
+    <orderEntry type="module" module-name="project3" />
+    <orderEntry type="module" module-name="kernelSolution" />
+    <orderEntry type="module" module-name="vfs2" />
   </component>
 </module>
```

### F035 ProjectModuleLoader.java

**Path:** `core/project/source/jetbrains/mps/project/ProjectModuleLoader.java`

[Browser file](C:/Codex/mps-j2cl/mps-web/core/project/source/jetbrains/mps/project/ProjectModuleLoader.java) · [Compared upstream file](C:/Codex/mps-j2cl/mps/core/project/source/jetbrains/mps/project/ProjectModuleLoader.java)

**Classification:** Files archives and XML. Text delta: +5 / −6 lines; 451 → 450 total lines.

**What changes:** Changes the macro matcher local type to MacroHelper.MacroMatcher and rewrites four module-loading diagnostics with concatenation.

**Behavior and limits:** This depends on the public type change in MacroHelper; it does not add browser module-file persistence.

**To make the replacement unnecessary:** Restore the Pattern/Matcher boundary in MacroHelper or give upstream a stable macro API, then restore this caller. Shared formatting already covers the simple log conversions, but both changes must be resolved before retiring the whole file.

Complete textual diff

```diff
--- mps/core/project/source/jetbrains/mps/project/ProjectModuleLoader.java
+++ mps-web/core/project/source/jetbrains/mps/project/ProjectModuleLoader.java
@@ -45,7 +45,6 @@
 import java.util.Set;
 import java.util.concurrent.CopyOnWriteArrayList;
 import java.util.function.BiConsumer;
-import java.util.regex.Matcher;
 import java.util.stream.Stream;
 
 /**
@@ -103,7 +102,7 @@
       clearErrorsBuffer();
       detachDroppedModules(myDroppedDescriptorFiles, myProject);
       int loadedModules = loadDiscoveredModules(myDiscoveredModuleHandles, myProject);
-      LOG.info(String.format("Modules are loaded: %d new; %d removed", loadedModules, myDroppedDescriptorFiles.size()));
+      LOG.info("Modules are loaded: " + loadedModules + " new; " + myDroppedDescriptorFiles.size() + " removed");
     }
   }
   
@@ -297,12 +296,12 @@
           //    Perhaps, shall use specific discovery method or introduce a mode/flag to avoid unnecessary processing of MD values
           modulesMiner.collectModules(descriptorFile, p.o2);
         } else {
-          error(String.format("Can't load module from %s. File doesn't exist.", descriptorFile.getPath()));
+          error("Can't load module from " + descriptorFile.getPath() + ". File doesn't exist.");
           fireModuleNotFound(descriptorFile);
         }
       } catch (PathFormatException e) {
         // fixme apyshkin
-        Matcher matcher = MacroHelper.MACRO_PATTERN.matcher(e.getProblemPath());
+        MacroHelper.MacroMatcher matcher = MacroHelper.MACRO_PATTERN.matcher(e.getProblemPath());
         if (matcher.find()) {
           LOG.warning("Some paths might contain unknown macros, please define them in 'Path variables' and reopen the project");
         } else {
@@ -326,7 +325,7 @@
       //     alternative to a repository which may provide access to foreign modules.
       final SModule oldModule = e.moduleReference.resolve(projectRepo);
       if (oldModule == null) {
-        LOG.error(String.format("Module %s (%s) not found in the project repository", e.moduleReference.getModuleName(), e.descriptorFile));
+        LOG.error("Module " + e.moduleReference.getModuleName() + " (" + e.descriptorFile + ") not found in the project repository");
       }
       else {
         // fire event with module still attached to a project repo
@@ -364,7 +363,7 @@
         }
         ++loadedModules;
       } else {
-        error(String.format("Can't load module from %s. Unknown file type.", descriptorFile.getPath()));
+        error("Can't load module from " + descriptorFile.getPath() + ". Unknown file type.");
         fireModuleTypeIsUnknown(descriptorFile);
       }
     }
```

### F036 ConceptMissingError.java

**Path:** `core/project/source/jetbrains/mps/project/validation/ConceptMissingError.java`

[Browser file](C:/Codex/mps-j2cl/mps-web/core/project/source/jetbrains/mps/project/validation/ConceptMissingError.java) · [Compared upstream file](C:/Codex/mps-j2cl/mps/core/project/source/jetbrains/mps/project/validation/ConceptMissingError.java)

**Classification:** Java APIs and text processing. Text delta: +1 / −7 lines; 43 → 37 total lines.

**What changes:** Replaces loading CoreBundle and applying MessageFormat with the pinned English missing-concept message.

**Behavior and limits:** The error's concept/language data remain; dynamic bundle selection is lost.

**To make the replacement unnecessary:** Provide ResourceBundle plus MessageFormat with packaged resources, or make diagnostic messages an injected upstream service.

Complete textual diff

```diff
--- mps/core/project/source/jetbrains/mps/project/validation/ConceptMissingError.java
+++ mps-web/core/project/source/jetbrains/mps/project/validation/ConceptMissingError.java
@@ -19,22 +19,16 @@
 import org.jetbrains.mps.openapi.language.SAbstractConcept;
 import org.jetbrains.mps.openapi.model.SNode;
 
-import java.text.MessageFormat;
-import java.util.ResourceBundle;
 
 public class ConceptMissingError extends LanguageFeatureMissingError {
   private final SAbstractConcept myConcept;
 
   public ConceptMissingError(SNode node, SAbstractConcept concept) {
     super(MessageStatus.ERROR, node.getReference(),
-          MessageFormat.format(msgFromBundle(), concept.getName(), concept.getLanguage().getQualifiedName()));
+          "Concept '" + concept.getName() + "' was not found in the language '" + concept.getLanguage().getQualifiedName() + "'");
     myConcept = concept;
   }
 
-  private static String msgFromBundle() {
-    ResourceBundle bundle = ResourceBundle.getBundle("jetbrains.mps.project.validation.CoreBundle");
-    return bundle.getString("concept.not.found");
-  }
 
   public SAbstractConcept getLanguageFeature() {
     return myConcept;
```

### F037 FileClassPathItem.java

**Path:** `core/project/source/jetbrains/mps/reloading/FileClassPathItem.java`

[Browser file](C:/Codex/mps-j2cl/mps-web/core/project/source/jetbrains/mps/reloading/FileClassPathItem.java) · [Compared upstream file](C:/Codex/mps-j2cl/mps/core/project/source/jetbrains/mps/reloading/FileClassPathItem.java)

**Classification:** Files archives and XML. Text delta: +3 / −2 lines; 135 → 136 total lines.

**What changes:** Replaces two File.toURI().toURL conversions with new URL(FileUtil.toFileUri(...)), for class-byte origin and resource lookup.

**Behavior and limits:** The rest of file/classpath access remains upstream and still depends on the filesystem provider. A lexical URL does not make host file IO functional.

**To make the replacement unnecessary:** Implement compatible File.toURI and URI.toURL in the owning IO/network layers, including escaping and directory paths. Recheck class-byte/resource callers without claiming browser bytecode execution.

Complete textual diff

```diff
--- mps/core/project/source/jetbrains/mps/reloading/FileClassPathItem.java
+++ mps-web/core/project/source/jetbrains/mps/reloading/FileClassPathItem.java
@@ -22,6 +22,7 @@
 import jetbrains.mps.util.ReadUtil;
 
 import java.io.File;
+import jetbrains.mps.util.FileUtil;
 import java.io.FileInputStream;
 import java.io.IOException;
 import java.io.InputStream;
@@ -82,7 +83,7 @@
         bytes = ReadUtil.read(inp);
       }
 
-      return bytes == null ? null : new DefaultClassBytes(bytes, path.toURI().toURL());
+      return bytes == null ? null : new DefaultClassBytes(bytes, new URL(FileUtil.toFileUri(path, false)));
     } catch (IOException e) {
       return null;
     }
@@ -92,7 +93,7 @@
   public URL getResource(String name) {
     try {
       File resourceFile = new File(myClassPath, name.replace('/', File.separatorChar));
-      return resourceFile.exists()? resourceFile.toURI().toURL() : null;
+      return resourceFile.exists()? new URL(FileUtil.toFileUri(resourceFile, resourceFile.isDirectory())) : null;
     } catch (MalformedURLException e) {
       return null;
     }
```

### F038 JarFileClassPathItem.java

**Path:** `core/project/source/jetbrains/mps/reloading/JarFileClassPathItem.java`

[Browser file](C:/Codex/mps-j2cl/mps-web/core/project/source/jetbrains/mps/reloading/JarFileClassPathItem.java) · [Compared upstream file](C:/Codex/mps-j2cl/mps/core/project/source/jetbrains/mps/reloading/JarFileClassPathItem.java)

**Classification:** Files archives and XML. Text delta: +16 / −362 lines; 395 → 49 total lines.

**What changes:** Replaces JAR indexes, class-byte/resource reading and nested-JAR extraction with a lexical jar-cp handle. getPath/getFile/flatten/toString remain; content operations and the nested-JAR constructor throw.

**Behavior and limits:** This is an explicit unavailable archive boundary, not a reduced but working classpath reader.

**To make the replacement unnecessary:** Use a shared ZIP/JAR provider plus storage for archive data, or let upstream classpath code select a compiled catalog provider. Reading bytecode still would not make it executable by J2CL.

Representative excerpts from the compared files; the prose above covers additional changes.

Pinned upstream at line 112

```java
  @Override
  public synchronized ClassBytes getClassBytes(String qualifiedClassName) {
    ensureInitialized();
    InputStream inp = null;
    ZipFile zf = null;
    if (!myFile.exists()) {
      LOG.warning("Classbytes file '" + myFile + "' does not exist");
      return null;
    }
    try {
      zf = new ZipFile(myFile);
      String entryName = toClassEntry(qualifiedClassName);
```

Browser replacement at line 38

```java

  @Override public String getPath() { return myPath; }
  public File getFile() { return myFile; }
  @Override public boolean hasClass(String name) { throw unavailable(); }
  @Override public synchronized ClassBytes getClassBytes(String name) { throw unavailable(); }
  @Override public URL getResource(String name) { throw unavailable(); }
  @Override public List<RealClassPathItem> flatten() { return Collections.singletonList(this); }
  @Override public String toString() { return "jar-cp: " + myFile; }
  private static UnsupportedOperationException unavailable() {
    return new UnsupportedOperationException("Reading JAR archives requires a browser archive backend; register compiled module classes and resources instead");
  }
}
```

### F039 LocalResourceClassPathItem.java

**Path:** `core/project/source/jetbrains/mps/reloading/LocalResourceClassPathItem.java`

[Browser file](C:/Codex/mps-j2cl/mps-web/core/project/source/jetbrains/mps/reloading/LocalResourceClassPathItem.java) · [Compared upstream file](C:/Codex/mps-j2cl/mps/core/project/source/jetbrains/mps/reloading/LocalResourceClassPathItem.java)

**Classification:** Files archives and XML. Text delta: +2 / −1 lines; 65 → 66 total lines.

**What changes:** Replaces the resource File-to-URL conversion with FileUtil.toFileUri plus URL construction.

**Behavior and limits:** The exists check and rest of local resource behavior remain; available storage is still a separate requirement.

**To make the replacement unnecessary:** Implement File.toURI and URI.toURL in shared libraries, then verify URI escaping and resource lookup through the original class.

Complete textual diff

```diff
--- mps/core/project/source/jetbrains/mps/reloading/LocalResourceClassPathItem.java
+++ mps-web/core/project/source/jetbrains/mps/reloading/LocalResourceClassPathItem.java
@@ -7,6 +7,7 @@
 import org.jetbrains.annotations.Nullable;
 
 import java.io.File;
+import jetbrains.mps.util.FileUtil;
 import java.net.MalformedURLException;
 import java.net.URL;
 import java.util.Collections;
@@ -47,7 +48,7 @@
     }
     try {
       File resourceFile = new File(myLocation, name.replace('/', File.separatorChar));
-      return resourceFile.exists()? resourceFile.toURI().toURL() : null;
+      return resourceFile.exists()? new URL(FileUtil.toFileUri(resourceFile, resourceFile.isDirectory())) : null;
     } catch (MalformedURLException e) {
       return null;
     }
```

### F040 smodel.iml

**Path:** `core/smodel/smodel.iml`

[Browser file](C:/Codex/mps-j2cl/mps-web/core/smodel/smodel.iml) · [Compared upstream file](C:/Codex/mps-j2cl/mps/core/smodel/smodel.iml)

**Classification:** IntelliJ project metadata. Text delta: +12 / −8 lines; 18 → 22 total lines.

**What changes:** Customized IntelliJ descriptor with a same-path upstream counterpart. Indexes the overlay source root(s) `source` with inherited compiler output. Declares 11 module dependencies. The complete XML or descriptor diff below records every dependency name. Added module references: `kernel2`, `logging2`, `mps-platform`, `mps-platform2`, `mps-workbench`, `openapi2`, `project`, `project3`, `smodel2`. Removed module references: `logging`, `util-core`.

**Behavior and limits:** The root .idea/modules.xml references this file, so it affects IDE indexing. It is not an MPS_OVERRIDES entry and is not used to select Bazel Java sources. Local module names such as kernel2 or webswing are IDE identifiers, not proof of Bazel dependency ownership.

**To make the replacement unnecessary:** Generate the IDE project from the authoritative selected-source/build graph, or keep equivalent descriptors outside the overlay. Update the root IDE module references before relocating metadata. Removing this file alone would break that IDE reference and would not reduce the 100 Java replacements.

Complete descriptor diff

```diff
--- mps/core/smodel/smodel.iml
+++ mps-web/core/smodel/smodel.iml
@@ -1,18 +1,22 @@
 <?xml version="1.0" encoding="UTF-8"?>
 <module type="JAVA_MODULE" version="4">
-  <component name="NewModuleRootManager">
-    <output url="file://$MODULE_DIR$/classes" />
-    <output-test url="file://$MODULE_DIR$/testclasses" />
+  <component name="NewModuleRootManager" inherit-compiler-output="true">
     <exclude-output />
     <content url="file://$MODULE_DIR$">
       <sourceFolder url="file://$MODULE_DIR$/source" isTestSource="false" />
     </content>
     <orderEntry type="inheritedJdk" />
     <orderEntry type="sourceFolder" forTests="false" />
-    <orderEntry type="module" module-name="openapi" exported="" />
-    <orderEntry type="module" module-name="logging" exported="" />
-    <orderEntry type="module" module-name="mps-annotations" exported="" />
-    <orderEntry type="module" module-name="util-core" exported="" />
-    <orderEntry type="library" name="gnu.trove" level="project" />
+    <orderEntry type="module" module-name="mps-platform2" />
+    <orderEntry type="module" module-name="mps-annotations" />
+    <orderEntry type="module" module-name="logging2" />
+    <orderEntry type="module" module-name="kernel2" />
+    <orderEntry type="module" module-name="mps-platform" />
+    <orderEntry type="module" module-name="project" />
+    <orderEntry type="module" module-name="smodel2" />
+    <orderEntry type="module" module-name="openapi" />
+    <orderEntry type="module" module-name="mps-workbench" />
+    <orderEntry type="module" module-name="openapi2" />
+    <orderEntry type="module" module-name="project3" />
   </component>
 </module>
```

### F041 ModuleReference.java

**Path:** `core/smodel/source/jetbrains/mps/project/structure/modules/ModuleReference.java`

[Browser file](C:/Codex/mps-j2cl/mps-web/core/smodel/source/jetbrains/mps/project/structure/modules/ModuleReference.java) · [Compared upstream file](C:/Codex/mps-j2cl/mps/core/smodel/source/jetbrains/mps/project/structure/modules/ModuleReference.java)

**Classification:** Java APIs and text processing. Text delta: +15 / −8 lines; 128 → 135 total lines.

**What changes:** Replaces the fixed reluctant module-reference regex with first-opening-parenthesis and final-closing-parenthesis parsing, rejecting Java dot line terminators explicitly.

**Behavior and limits:** The parser retains id/name groups and PersistenceFacade fallback. It is not a general regex implementation.

**To make the replacement unnecessary:** The current regex subset supports reluctant groups and Java line terminators; trial the original pattern with malformed references, embedded parentheses and all rejected line breaks.

Complete textual diff

```diff
--- mps/core/smodel/source/jetbrains/mps/project/structure/modules/ModuleReference.java
+++ mps-web/core/smodel/source/jetbrains/mps/project/structure/modules/ModuleReference.java
@@ -26,12 +26,9 @@
 import org.jetbrains.mps.openapi.persistence.PersistenceFacade;
 
 import java.util.Objects;
-import java.util.regex.Matcher;
-import java.util.regex.Pattern;
 
 @Immutable
 public final class ModuleReference implements SModuleReference {
-  private static final Pattern MODULE_REFERENCE = Pattern.compile("(.*?)\\((.*?)\\)");
 
   private final String myModuleName;
   private final SModuleId myModuleId;
@@ -95,13 +92,23 @@
 
   public static SModuleReference parseReference(@NotNull String text, @Nullable PersistenceFacade pf) {
     text = text.trim();
-    Matcher m = MODULE_REFERENCE.matcher(text);
-    if (m.matches()) {
+    int opening = text.indexOf('(');
+    boolean noLineBreaks = true;
+    for (int i = 0; i < text.length(); i++) {
+      char c = text.charAt(i);
+      if (c == '\n' || c == '\r' || c == '\u0085' || c == '\u2028' || c == '\u2029') {
+        noLineBreaks = false;
+        break;
+      }
+    }
+    if (noLineBreaks && opening >= 0 && text.endsWith(")")) {
+      String idText = text.substring(0, opening);
+      String nameText = text.substring(opening + 1, text.length() - 1);
       if (pf != null) {
-        return pf.createModuleReference(pf.createModuleId(m.group(1)), m.group(2));
+        return pf.createModuleReference(pf.createModuleId(idText), nameText);
       } else {
-        String name = m.group(2);
-        return new ModuleReference(name == null ? null : name.intern(), ModuleId.fromString(m.group(1)));
+        String name = nameText;
+        return new ModuleReference(name == null ? null : name.intern(), ModuleId.fromString(idText));
       }
     }
     throw new IllegalArgumentException(String.format("Bad module reference %s", text));
```

### F042 RuntimeFlags.java

**Path:** `core/smodel/source/jetbrains/mps/RuntimeFlags.java`

[Browser file](C:/Codex/mps-j2cl/mps-web/core/smodel/source/jetbrains/mps/RuntimeFlags.java) · [Compared upstream file](C:/Codex/mps-j2cl/mps/core/smodel/source/jetbrains/mps/RuntimeFlags.java)

**Classification:** Java APIs and text processing. Text delta: +6 / −6 lines; 130 → 130 total lines.

**What changes:** Replaces Boolean.getBoolean with parseBoolean of literal optional System properties and makes selected String property defaults explicit.

**Behavior and limits:** Flags remain cached and runtime setters remain; build-time J2CL property substitution requires literal keys.

**To make the replacement unnecessary:** Teach the shared compiler/JRE property mechanism to handle these calls, or introduce an upstream configuration service. Preserve absent/default values and setter precedence rather than treating all flags as constants.

Complete textual diff

```diff
--- mps/core/smodel/source/jetbrains/mps/RuntimeFlags.java
+++ mps-web/core/smodel/source/jetbrains/mps/RuntimeFlags.java
@@ -50,7 +50,7 @@
 
   public static boolean isInternalMode() {
     if (ourInternalMode == null) {
-      ourInternalMode = Boolean.getBoolean("mps.internal");
+      ourInternalMode = Boolean.parseBoolean(System.getProperty("mps.internal", "false"));
     }
     return ourInternalMode;
   }
@@ -79,7 +79,7 @@
    */
   public static boolean isExceptionOnBadCast() {
     if (ourCastException == null) {
-      ourCastException = !Boolean.getBoolean("mps.disableNodeCastExceptions");
+      ourCastException = !Boolean.parseBoolean(System.getProperty("mps.disableNodeCastExceptions", "false"));
     }
     return ourCastException;
   }
@@ -94,7 +94,7 @@
    */
   public static boolean useEclipseJavaCompiler() {
     if (ourEclipseJavaCompiler == null) {
-      ourEclipseJavaCompiler = "ecj".equalsIgnoreCase(System.getProperty("mps.compiler.java"));
+      ourEclipseJavaCompiler = "ecj".equalsIgnoreCase(System.getProperty("mps.compiler.java", null));
     }
     return ourEclipseJavaCompiler;
   }
@@ -105,7 +105,7 @@
    */
   public static boolean modelsLoadedOnModuleAttach() {
     if (ourLegacyLoadModels == null) {
-      final String val = System.getProperty("mps.models.force");
+      final String val = System.getProperty("mps.models.force", null);
       // default false for testing purposes
       ourLegacyLoadModels = Boolean.parseBoolean(val);
       // if I don't want to enable it in release, uncomment the next line
@@ -116,14 +116,14 @@
 
   public static boolean customNodeIdentitySupport() {
     if (ourCustomNodeIdentitySupport == null) {
-      ourCustomNodeIdentitySupport = Boolean.getBoolean("mps.nodeuid");
+      ourCustomNodeIdentitySupport = Boolean.parseBoolean(System.getProperty("mps.nodeuid", "false"));
     }
     return ourCustomNodeIdentitySupport;
   }
 
   public static boolean nodeIdByConceptScope() {
     if (ourExperimentalScopedNodeId == null) {
-      ourExperimentalScopedNodeId = Boolean.getBoolean("mps.nodes.fakeid");
+      ourExperimentalScopedNodeId = Boolean.parseBoolean(System.getProperty("mps.nodes.fakeid", "false"));
     }
     return ourExperimentalScopedNodeId;
   }
```

### F043 ActionDispatcher.java

**Path:** `core/smodel/source/jetbrains/mps/smodel/ActionDispatcher.java`

[Browser file](C:/Codex/mps-j2cl/mps-web/core/smodel/source/jetbrains/mps/smodel/ActionDispatcher.java) · [Compared upstream file](C:/Codex/mps-j2cl/mps/core/smodel/source/jetbrains/mps/smodel/ActionDispatcher.java)

**Classification:** Scheduling model access and invalidation. Text delta: +42 / −26 lines; 312 → 328 total lines.

**What changes:** Replaces List.reversed with copy/reverse, rewrites diagnostics, narrows IntelliJ control-flow detection to a superclass/name scan, and aggregates listener cleanup failures while preserving the primary RuntimeException/Error and suppressed failures.

**Behavior and limits:** This includes semantic robustness fixes, not only an API rewrite. Name-based cancellation recognition is metadata-sensitive; interface-based recognition is no longer general.

**To make the replacement unnecessary:** Upstream the cleanup/exception-order fixes. Put cancellation classification behind a service or typed marker and supply any missing reversed-list API centrally. Do not delete this replacement just because newer List APIs compile.

Representative excerpts from the compared files; the prose above covers additional changes.

Pinned upstream at line 109

```java
          // that re-dispatches MA's CommandListener events as its own SRepositoryListener's.
          onExit(toNotify.reversed());
        } catch (InterruptedException ex) {
          // FIXME here we are likely in an invalid state (onEntry succeed, onExit didn't grab myNotifyGuard),
          //       although no idea how to bring it back to 'normal', seems that it's not possible right here - there
          //       could be other threads and we can not reset the state right now.
          //       Perhaps, another State could be introduced, and checked as a first thing in onEntry() under myNotifyGuard,
          //       where we can try to start all over again with a blank page.
          interruptedException = ex;
        }
      }
    }
```

Browser replacement at line 110

```java
          // that re-dispatches MA's CommandListener events as its own SRepositoryListener's.
          final ArrayList<T> finishing = new ArrayList<>(toNotify);
          Collections.reverse(finishing);
          onExit(finishing);
        } catch (InterruptedException ex) {
          // FIXME here we are likely in an invalid state (onEntry succeed, onExit didn't grab myNotifyGuard),
          //       although no idea how to bring it back to 'normal', seems that it's not possible right here - there
          //       could be other threads and we can not reset the state right now.
          //       Perhaps, another State could be introduced, and checked as a first thing in onEntry() under myNotifyGuard,
          //       where we can try to start all over again with a blank page.
          interruptedException = ex;
        } catch (RuntimeException | Error ex) {
```

### F044 DefaultModelAccess.java

**Path:** `core/smodel/source/jetbrains/mps/smodel/DefaultModelAccess.java`

[Browser file](C:/Codex/mps-j2cl/mps-web/core/smodel/source/jetbrains/mps/smodel/DefaultModelAccess.java) · [Compared upstream file](C:/Codex/mps-j2cl/mps/core/smodel/source/jetbrains/mps/smodel/DefaultModelAccess.java)

**Classification:** Scheduling model access and invalidation. Text delta: +28 / −3 lines; 49 → 74 total lines.

**What changes:** Replaces SwingUtilities.invokeLater with a scheduled executor using a zero-delay timer and explicit queued-action error reporting.

**Behavior and limits:** The sibling execute path can run inline, so schedule is intentional. Locked model actions still run, but no JVM thread concurrency is introduced.

**To make the replacement unnecessary:** Expose a shared asynchronous event-queue contract that guarantees a later browser task and inject it upstream. Verify queue ordering, locks, cancellation and failures before reverting to the Swing call.

Complete textual diff

```diff
--- mps/core/smodel/source/jetbrains/mps/smodel/DefaultModelAccess.java
+++ mps-web/core/smodel/source/jetbrains/mps/smodel/DefaultModelAccess.java
@@ -3,7 +3,9 @@
  */
 package jetbrains.mps.smodel;
 
-import javax.swing.SwingUtilities;
+import java.util.concurrent.Executors;
+import java.util.concurrent.ScheduledExecutorService;
+import java.util.concurrent.TimeUnit;
 
 /**
  * This is an instance available from {@code smodel.ModelAccess.instance()} for uses from non-IDE ant tasks and tests.
@@ -15,6 +17,12 @@
  * Then, there's an intersection with {@code  jetbrains.mps.smodel.GlobalModelAccess}, which would need to get sorted out.
  */
 class DefaultModelAccess extends ModelAccess {
+  private static final ScheduledExecutorService EVENT_QUEUE = Executors.newSingleThreadScheduledExecutor(r -> {
+    Thread thread = new Thread(r, "MPS model actions");
+    thread.setDaemon(true);
+    return thread;
+  });
+
   DefaultModelAccess() {
   }
 
@@ -39,11 +47,28 @@
 
   @Override
   public void runReadInEDT(final Runnable r) {
-    SwingUtilities.invokeLater(prepareLocked(r, getReadLock(), myReadActionDispatcher));
+    invokeLater(prepareLocked(r, getReadLock(), myReadActionDispatcher));
   }
 
   @Override
   public void runWriteInEDT(final Runnable r) {
-    SwingUtilities.invokeLater(prepareLocked(r, getWriteLock(), myWriteActionDispatcher));
+    invokeLater(prepareLocked(r, getWriteLock(), myWriteActionDispatcher));
+  }
+
+  private static void invokeLater(Runnable action) {
+    // The browser concurrency overlay schedules timers; Executor.execute would run inline.
+    EVENT_QUEUE.schedule(() -> {
+      try {
+        action.run();
+      } catch (RuntimeException ex) {
+        if (!ActionDispatcher.isControlFlowIDEA(ex)) {
+          LOG.error("Queued model action failed", ex);
+        }
+        throw ex;
+      } catch (Error error) {
+        LOG.error("Queued model action failed", error);
+        throw error;
+      }
+    }, 0, TimeUnit.MILLISECONDS);
   }
 }
```

### F045 IconResource.java

**Path:** `core/smodel/source/jetbrains/mps/smodel/runtime/IconResource.java`

[Browser file](C:/Codex/mps-j2cl/mps-web/core/smodel/source/jetbrains/mps/smodel/runtime/IconResource.java) · [Compared upstream file](C:/Codex/mps-j2cl/mps/core/smodel/source/jetbrains/mps/smodel/runtime/IconResource.java)

**Classification:** Icon resources and encoding. Text delta: +49 / −6 lines; 151 → 194 total lines.

**What changes:** Replaces ResourceModule annotation lookup and Class.getPackage with explicit provider-to-module/package maps; adds registration, conflict validation and unregister APIs, and simplifies toString.

**Behavior and limits:** Relative icons require registered package metadata; module origins must be installed before construction. Existing weak references to legacy providers remain, while new metadata maps require explicit cleanup.

**To make the replacement unnecessary:** Generate resource-provider metadata and expose an upstream resource-origin service, or provide annotation/package metadata in a shared reflection layer. Module unload must unregister strong Class keys.

Representative excerpts from the compared files; the prose above covers additional changes.

Pinned upstream at line 68

```java
    myIconResId_newUI = iconResId_newUI != null ? asQualifiedResourcePath(iconResId_newUI, resourceProvider) : myIconResId;
    ResourceModule ann = resourceProvider.getDeclaredAnnotation(ResourceModule.class);
    myModuleOrigin = ann == null ? null : ann.value();
    myResourceProvider = null;
  }

  /**
   * INTERNAL, PROVISIONAL CODE
   * indicates IconResource generated w/o @ResourceModule annotation, with relative resource id that keeps reference to Class to load actual bytes.
   */
  @Internal
  @Deprecated(since = "2025.2", forRemoval = true)
```

Browser replacement at line 112

```java
    myIconResId_newUI = iconResId_newUI != null ? asQualifiedResourcePath(iconResId_newUI, resourceProvider) : myIconResId;
    myModuleOrigin = MODULE_ORIGINS.get(resourceProvider);
    myResourceProvider = null;
  }

  /**
   * INTERNAL, PROVISIONAL CODE
   * indicates IconResource generated w/o @ResourceModule annotation, with relative resource id that keeps reference to Class to load actual bytes.
   */
  @Internal
  @Deprecated(since = "2025.2", forRemoval = true)
  public boolean isLegacy() {
```

### F046 DefaultTypecheckingController.java

**Path:** `core/typechecking/source/jetbrains/mps/typechecking/backend/DefaultTypecheckingController.java`

[Browser file](C:/Codex/mps-j2cl/mps-web/core/typechecking/source/jetbrains/mps/typechecking/backend/DefaultTypecheckingController.java) · [Compared upstream file](C:/Codex/mps-j2cl/mps/core/typechecking/source/jetbrains/mps/typechecking/backend/DefaultTypecheckingController.java)

**Classification:** Weak references and lifecycle. Text delta: +16 / −4 lines; 203 → 215 total lines.

**What changes:** Makes controller disposal idempotent, drains active sessions, disposes and clears the cached on-demand soft-reference session, clears auxiliary data and rejects later session requests.

**Behavior and limits:** This corrects lifetime assumptions for a long-lived browser event thread. Garbage collection alone is not the cleanup mechanism.

**To make the replacement unnecessary:** Upstream explicit controller ownership/disposal and use it in desktop and browser hosts; restoring reference APIs alone would not replace the lifecycle safeguards.

Complete textual diff

```diff
--- mps/core/typechecking/source/jetbrains/mps/typechecking/backend/DefaultTypecheckingController.java
+++ mps-web/core/typechecking/source/jetbrains/mps/typechecking/backend/DefaultTypecheckingController.java
@@ -52,6 +52,7 @@
 public class DefaultTypecheckingController extends TypecheckingController implements ParametersDiscoverable {
 
   private final Flags myDefaultFlags;
+  private boolean myDisposed;
 
   // explicitly requested session
   private TypecheckingSessionImpl myActiveSession;
@@ -70,9 +71,18 @@
 
   @Override
   public void dispose() {
-    if (myActiveSession != null) {
-      disposeSession(myActiveSession);
-    }
+    if (myDisposed) return;
+    myDisposed = true;
+    while (myActiveSession != null) disposeSession(myActiveSession);
+    TypecheckingSessionImpl onDemand = myOnDemandSession == null ? null : myOnDemandSession.get();
+    if (onDemand != null) onDemand.dispose();
+    if (myOnDemandSession != null) myOnDemandSession.clear();
+    myOnDemandSession = null;
+    clearAuxData();
+  }
+
+  protected final void ensureNotDisposed() {
+    if (myDisposed) throw new IllegalStateException("Typechecking controller is disposed");
   }
 
   @Override
@@ -83,6 +93,7 @@
   @NotNull
   @Override
   protected Handle requestSession(@NotNull Flags flags) {
+    ensureNotDisposed();
     if (myActiveSession == null) {
       this.myActiveSession = createSession(flags);
       return new SessionHandle(myActiveSession);
@@ -110,6 +121,7 @@
   @NotNull
   @Override
   protected TypecheckingQueries getQueries(@NotNull SNode src, SNode trg, SConcept trgConcept, Flags flags) {
+    ensureNotDisposed();
     SNode containingRoot = src.getContainingRoot();
     if (myActiveSession != null && myActiveSession.flags().getRoot() == containingRoot) {
       return myActiveSession.getQueries(src, trg, trgConcept);
@@ -128,6 +140,7 @@
 
   @Override
   protected AuxDataContainer getDataContainer(TypecheckingProvider<?> provider) {
+    ensureNotDisposed();
     TypecheckingSessionImpl runningSession = myActiveSession != null ? myActiveSession : null;
     if (runningSession == null) {
       runningSession = myOnDemandSession != null ? myOnDemandSession.get() : null;
@@ -200,4 +213,3 @@
   }
 
 }
-
```

### F047 TypecheckingFacadeComponent.java

**Path:** `core/typechecking/source/jetbrains/mps/typechecking/backend/TypecheckingFacadeComponent.java`

[Browser file](C:/Codex/mps-j2cl/mps-web/core/typechecking/source/jetbrains/mps/typechecking/backend/TypecheckingFacadeComponent.java) · [Compared upstream file](C:/Codex/mps-j2cl/mps/core/typechecking/source/jetbrains/mps/typechecking/backend/TypecheckingFacadeComponent.java)

**Classification:** Weak references and lifecycle. Text delta: +12 / −1 lines; 218 → 229 total lines.

**What changes:** Clears stored disposal references and the facade factory on shutdown, removes the thread-context facade, tracks disposed facades via a weak reference and rejects new controller pushes after disposal.

**Behavior and limits:** Phantom/reference-queue cleanup remains in the source; explicit shutdown is added alongside it. Restarting the host must not reuse a stale thread-local facade.

**To make the replacement unnecessary:** Upstream an explicit facade/component close contract and deterministic host restart behavior. Preserve reference cleanup as a secondary mechanism and test retained old facade handles.

Complete textual diff

```diff
--- mps/core/typechecking/source/jetbrains/mps/typechecking/backend/TypecheckingFacadeComponent.java
+++ mps-web/core/typechecking/source/jetbrains/mps/typechecking/backend/TypecheckingFacadeComponent.java
@@ -97,7 +97,10 @@
   public void dispose() {
     for (DisposeReference ref : myLiveDisposeReferences) {
       ref.dipose();
-    }
+      ref.clear();
+    }
+    myLiveDisposeReferences.clear();
+    ContextTypecheckingFacade.setFactoryInstance(null);
   }
 
   private ContextTypecheckingFacade createFacade(TypecheckingControllerFactory sharedControllerFactory)
@@ -127,6 +130,7 @@
 
     /* Provides access to a protected static field in the superclass */
     protected static void setFactoryInstance(Supplier<TypecheckingFacade> factoryInstance) {
+      clearContextInstance();
       TypecheckingFacade.DEFAULT_INSTANCE_FACTORY = factoryInstance;
     }
 
@@ -134,6 +138,7 @@
     private final TypecheckingControllerFactory myControllerFactory;
 
     private Deque<TypecheckingController> myControllerStack = new ArrayDeque<>();
+    private boolean myDisposed;
 
     public ContextTypecheckingFacade(@NotNull TypecheckingControllerFactory controllerFactory) {
       myControllerFactory = controllerFactory;
@@ -173,6 +178,7 @@
     }
 
     private void init() {
+      if (myDisposed) throw new IllegalStateException("Typechecking facade is disposed");
       if (myControllerStack.isEmpty()) {
         TypecheckingController context = myControllerFactory.createContextController();
         push(context);
@@ -201,14 +207,19 @@
   private static class DisposeReference extends PhantomReference<ContextTypecheckingFacade> {
 
     private final Deque<TypecheckingController> myReferentControllerStack;
+    private final java.lang.ref.WeakReference<ContextTypecheckingFacade> myFacade;
 
     private DisposeReference(ContextTypecheckingFacade referent,
                              ReferenceQueue<? super ContextTypecheckingFacade> q) {
       super(referent, q);
+      myFacade = new java.lang.ref.WeakReference<>(referent);
       myReferentControllerStack = referent.myControllerStack;
     }
 
     private void dipose() {
+      ContextTypecheckingFacade facade = myFacade.get();
+      if (facade != null) facade.myDisposed = true;
+      myFacade.clear();
       while (!myReferentControllerStack.isEmpty()) {
         myReferentControllerStack.removeFirst().dispose();
       }
```

### F048 WorkbenchTypecheckingController.java

**Path:** `core/typechecking/source/jetbrains/mps/typechecking/backend/WorkbenchTypecheckingController.java`

[Browser file](C:/Codex/mps-j2cl/mps-web/core/typechecking/source/jetbrains/mps/typechecking/backend/WorkbenchTypecheckingController.java) · [Compared upstream file](C:/Codex/mps-j2cl/mps/core/typechecking/source/jetbrains/mps/typechecking/backend/WorkbenchTypecheckingController.java)

**Classification:** Weak references and lifecycle. Text delta: +3 / −0 lines; 223 → 226 total lines.

**What changes:** Adds ensureNotDisposed checks to incremental-session/controller entry points.

**Behavior and limits:** This file relies on the disposal implementation added to DefaultTypecheckingController; it does not itself add the full cleanup algorithm described broadly in the manifest.

**To make the replacement unnecessary:** Upstream the base-controller lifecycle contract and its subclass checks together. Verify both incremental sessions and rejection after disposal.

Complete textual diff

```diff
--- mps/core/typechecking/source/jetbrains/mps/typechecking/backend/WorkbenchTypecheckingController.java
+++ mps-web/core/typechecking/source/jetbrains/mps/typechecking/backend/WorkbenchTypecheckingController.java
@@ -54,6 +54,7 @@
   @NotNull
   @Override
   public Handle requestSession(@NotNull Flags flags) {
+    ensureNotDisposed();
     if (flags.getRoot() != null && flags.isIncremental()) {
       // the editor has requested a session for the opened root
       return new SessionHandle(flags);
@@ -83,6 +84,7 @@
   @NotNull
   @Override
   protected TypecheckingQueries getQueries(@NotNull SNode src, SNode trg, SConcept trgConcept, Flags flags) {
+    ensureNotDisposed();
     SNode containingRoot = src.getContainingRoot();
     TypecheckingSessionImpl session = myRootSessions.get(new SNodeHandle(containingRoot));
     if (session == null) {
@@ -111,6 +113,7 @@
   }
 
   private synchronized TypecheckingSessionImpl getOrCreateSession(Flags flags) {
+    ensureNotDisposed();
     return myRootSessions.computeIfAbsent(new SNodeHandle(flags.getRoot()),
                                           (key) -> new TypecheckingSessionImpl(this, flags) {
                                             @Override
```

### F049 TypecheckingFacade.java

**Path:** `core/typechecking/source/jetbrains/mps/typechecking/TypecheckingFacade.java`

[Browser file](C:/Codex/mps-j2cl/mps-web/core/typechecking/source/jetbrains/mps/typechecking/TypecheckingFacade.java) · [Compared upstream file](C:/Codex/mps-j2cl/mps/core/typechecking/source/jetbrains/mps/typechecking/TypecheckingFacade.java)

**Classification:** Weak references and lifecycle. Text delta: +3 / −0 lines; 167 → 170 total lines.

**What changes:** Adds clearContextInstance, which removes the ThreadLocal facade used by the browser event thread.

**Behavior and limits:** The main facade API remains; explicit shutdown/restart can clear a thread that otherwise lives for the page lifetime.

**To make the replacement unnecessary:** Provide an upstream context-scope close/reset API and call it from the owning platform. A different ThreadLocal implementation cannot infer the correct host lifetime.

Complete textual diff

```diff
--- mps/core/typechecking/source/jetbrains/mps/typechecking/TypecheckingFacade.java
+++ mps-web/core/typechecking/source/jetbrains/mps/typechecking/TypecheckingFacade.java
@@ -149,6 +149,9 @@
     return CONTEXT_INSTANCE.get();
   }
 
+  /** Browser component shutdown/startup owns the event-thread facade explicitly. */
+  protected static void clearContextInstance() { CONTEXT_INSTANCE.remove(); }
+
   // initialized by TypecheckingFacadeComponent
   protected static Supplier<TypecheckingFacade> DEFAULT_INSTANCE_FACTORY;
 
```

### F050 typechecking2.iml

**Path:** `core/typechecking/typechecking2.iml`

[Browser file](C:/Codex/mps-j2cl/mps-web/core/typechecking/typechecking2.iml) · No same-path file at the pinned upstream revision

**Classification:** IntelliJ project metadata. Text delta: +25 / −0 lines; 0 → 25 total lines.

**What changes:** Added IntelliJ module descriptor: no file exists at this exact path in the pinned upstream tree. Indexes the overlay source root(s) `source` with inherited compiler output. Declares 14 module dependencies. The complete XML or descriptor diff below records every dependency name.

**Behavior and limits:** The root .idea/modules.xml references this file, so it affects IDE indexing. It is not an MPS_OVERRIDES entry and is not used to select Bazel Java sources. Local module names such as kernel2 or webswing are IDE identifiers, not proof of Bazel dependency ownership.

**To make the replacement unnecessary:** Generate the IDE project from the authoritative selected-source/build graph, or keep equivalent descriptors outside the overlay. Update the root IDE module references before relocating metadata. Removing this file alone would break that IDE reference and would not reduce the 100 Java replacements.

Current module descriptor

```xml
<?xml version="1.0" encoding="UTF-8"?>
<module type="JAVA_MODULE" version="4">
  <component name="NewModuleRootManager" inherit-compiler-output="true">
    <exclude-output />
    <content url="file://$MODULE_DIR$">
      <sourceFolder url="file://$MODULE_DIR$/source" isTestSource="false" />
    </content>
    <orderEntry type="inheritedJdk" />
    <orderEntry type="sourceFolder" forTests="false" />
    <orderEntry type="module" module-name="components" />
    <orderEntry type="module" module-name="patternRuntime" />
    <orderEntry type="module" module-name="util4" />
    <orderEntry type="module" module-name="logging2" />
    <orderEntry type="module" module-name="webswing" />
    <orderEntry type="module" module-name="constraints" />
    <orderEntry type="module" module-name="generator99" />
    <orderEntry type="module" module-name="smodel2" />
    <orderEntry type="module" module-name="kernel" />
    <orderEntry type="module" module-name="openapi2" />
    <orderEntry type="module" module-name="typechecking" />
    <orderEntry type="module" module-name="kernel2" />
    <orderEntry type="module" module-name="openapi" />
    <orderEntry type="module" module-name="pattern" />
  </component>
</module>
```

### F051 DoubleRuleSet.java

**Path:** `core/typesystemEngine/source/jetbrains/mps/lang/typesystem/runtime/DoubleRuleSet.java`

[Browser file](C:/Codex/mps-j2cl/mps-web/core/typesystemEngine/source/jetbrains/mps/lang/typesystem/runtime/DoubleRuleSet.java) · [Compared upstream file](C:/Codex/mps-j2cl/mps/core/typesystemEngine/source/jetbrains/mps/lang/typesystem/runtime/DoubleRuleSet.java)

**Classification:** Compiled deployment and reflection. Text delta: +1 / −5 lines; 117 → 113 total lines.

**What changes:** Replaces package-name inference for two-concept rule ownership with RuleSet.namespaceOf(rule).

**Behavior and limits:** Rule ordering and scope selection remain, but a missing registered namespace now fails explicitly.

**To make the replacement unnecessary:** Generate rule-to-language metadata and use a common upstream ownership service for both rule-set variants.

Complete textual diff

```diff
--- mps/core/typesystemEngine/source/jetbrains/mps/lang/typesystem/runtime/DoubleRuleSet.java
+++ mps-web/core/typesystemEngine/source/jetbrains/mps/lang/typesystem/runtime/DoubleRuleSet.java
@@ -103,11 +103,7 @@
   }
 
   private String getNamespace(T rule) {
-    String pkg = rule.getClass().getPackage().getName();
-    if (pkg.endsWith(TYPESYSTEM_SUFFIX)) {
-      return pkg.substring(0, pkg.length() - TYPESYSTEM_SUFFIX.length());
-    }
-    return pkg;
+    return RuleSet.namespaceOf(rule);
   }
 
   public void clear() {
```

### F052 RuleSet.java

**Path:** `core/typesystemEngine/source/jetbrains/mps/lang/typesystem/runtime/RuleSet.java`

[Browser file](C:/Codex/mps-j2cl/mps-web/core/typesystemEngine/source/jetbrains/mps/lang/typesystem/runtime/RuleSet.java) · [Compared upstream file](C:/Codex/mps-j2cl/mps/core/typesystemEngine/source/jetbrains/mps/lang/typesystem/runtime/RuleSet.java)

**Classification:** Compiled deployment and reflection. Text delta: +21 / −5 lines; 138 → 154 total lines.

**What changes:** Adds Class-to-language namespace registration, conflict checks and unregister; replaces getClass().getPackage inference with a required registered namespace.

**Behavior and limits:** Stable ownership survives stripped class names. Strong registry entries must be released after sessions/rule caches are disposed.

**To make the replacement unnecessary:** Teach the MPS typesystem generator to emit language ownership or catalogs and let upstream runtime rules expose it directly; package reflection becomes unnecessary.

Complete textual diff

```diff
--- mps/core/typesystemEngine/source/jetbrains/mps/lang/typesystem/runtime/RuleSet.java
+++ mps-web/core/typesystemEngine/source/jetbrains/mps/lang/typesystem/runtime/RuleSet.java
@@ -37,6 +37,26 @@
  *  Synchronized.
  */
 public class RuleSet<T extends IApplicableToConcept> {
+  private static final java.util.Map<Class<?>, String> RULE_NAMESPACES = new java.util.HashMap<>();
+
+  /** Registers stable language ownership without relying on stripped Java class names. */
+  public static void registerRuleNamespace(Class<?> ruleClass, String languageNamespace) {
+    java.util.Objects.requireNonNull(ruleClass);
+    java.util.Objects.requireNonNull(languageNamespace);
+    String prior = RULE_NAMESPACES.putIfAbsent(ruleClass, languageNamespace);
+    if (prior != null && !prior.equals(languageNamespace)) {
+      throw new IllegalStateException("Conflicting rule language ownership");
+    }
+  }
+
+  /** Deployment must unregister its rule classes after disposing sessions and rule caches. */
+  public static void unregisterRuleNamespace(Class<?> ruleClass) { RULE_NAMESPACES.remove(ruleClass); }
+
+  static String namespaceOf(Object rule) {
+    String namespace = RULE_NAMESPACES.get(rule.getClass());
+    if (namespace == null) throw new IllegalStateException("Typesystem rule class has no registered language namespace");
+    return namespace;
+  }
 
   private final Logger LOG = Logger.getLogger(RuleSet.class);
 
@@ -124,11 +144,7 @@
   }
 
   private String getNamespace(T rule) {
-    String pkg = rule.getClass().getPackage().getName();
-    if (pkg.endsWith(TYPESYSTEM_SUFFIX)) {
-      return pkg.substring(0, pkg.length() - TYPESYSTEM_SUFFIX.length());
-    }
-    return pkg;
+    return namespaceOf(rule);
   }
 
   public void clear() {
```

### F053 IncrementalTypecheckingComponent.java

**Path:** `core/typesystemEngine/source/jetbrains/mps/newTypesystem/context/component/IncrementalTypecheckingComponent.java`

[Browser file](C:/Codex/mps-j2cl/mps-web/core/typesystemEngine/source/jetbrains/mps/newTypesystem/context/component/IncrementalTypecheckingComponent.java) · [Compared upstream file](C:/Codex/mps-j2cl/mps/core/typesystemEngine/source/jetbrains/mps/newTypesystem/context/component/IncrementalTypecheckingComponent.java)

**Classification:** Scheduling model access and invalidation. Text delta: +4 / −2 lines; 216 → 218 total lines.

**What changes:** Changes strict timestamp comparisons to inclusive comparisons and excludes Instant.MIN from completed-check detection.

**Behavior and limits:** Same-millisecond checks/invalidations are treated conservatively. The underlying issue is change ordering, not a missing Instant class; current shared time APIs are already used here.

**To make the replacement unnecessary:** Use a monotonic model/check revision counter upstream, or deliberately upstream inclusive boundary semantics. A higher-resolution clock alone is not a correctness guarantee.

Complete textual diff

```diff
--- mps/core/typesystemEngine/source/jetbrains/mps/newTypesystem/context/component/IncrementalTypecheckingComponent.java
+++ mps-web/core/typesystemEngine/source/jetbrains/mps/newTypesystem/context/component/IncrementalTypecheckingComponent.java
@@ -56,9 +56,11 @@
    * a re-check is required.
    */
   public boolean hasUpdates(Instant since) {
-    boolean isCheckedSince = myLastChecked.get().isAfter(since);
+    // Browser wall clocks can return the same instant for edit/check in one tick.
+    // Conservatively report equal timestamps so that no completed check is missed.
+    boolean isCheckedSince = !myLastChecked.get().equals(Instant.MIN) && !myLastChecked.get().isBefore(since);
     InvalidationResult invalidationResult = myInvalidation.get();
-    boolean invalidated = invalidationResult.hasOccuredSince(since) && invalidationResult.hasInvalidated();
+    boolean invalidated = !invalidationResult.getInvalidationTime().isBefore(since) && invalidationResult.hasInvalidated();
     return isCheckedSince || invalidated;
   }
 
```

### F054 State.java

**Path:** `core/typesystemEngine/source/jetbrains/mps/newTypesystem/state/State.java`

[Browser file](C:/Codex/mps-j2cl/mps-web/core/typesystemEngine/source/jetbrains/mps/newTypesystem/state/State.java) · [Compared upstream file](C:/Codex/mps-j2cl/mps/core/typesystemEngine/source/jetbrains/mps/newTypesystem/state/State.java)

**Classification:** Java APIs and text processing. Text delta: +1 / −1 lines; 587 → 587 total lines.

**What changes:** Removes only the Override annotation from the inner MyBlockSet.clone method and adds a comment explaining the absent Object.clone declaration.

**Behavior and limits:** The method still throws CloneNotSupportedException. Contrary to the broad manifest wording, no state-copy algorithm is added or changed.

**To make the replacement unnecessary:** Add the compatible Object.clone declaration/runtime contract to the shared JRE, or make the upstream override optional. Do not describe this as implementing cloning.

Complete textual diff

```diff
--- mps/core/typesystemEngine/source/jetbrains/mps/newTypesystem/state/State.java
+++ mps-web/core/typesystemEngine/source/jetbrains/mps/newTypesystem/state/State.java
@@ -577,7 +577,7 @@
       }
     }
 
-    @Override
+    // Object.clone is absent in the browser JRE; this method still rejects cloning.
     protected Object clone() throws CloneNotSupportedException {
       throw new CloneNotSupportedException();
     }
```

### F055 typesystemEngine2.iml

**Path:** `core/typesystemEngine/typesystemEngine2.iml`

[Browser file](C:/Codex/mps-j2cl/mps-web/core/typesystemEngine/typesystemEngine2.iml) · No same-path file at the pinned upstream revision

**Classification:** IntelliJ project metadata. Text delta: +37 / −0 lines; 0 → 37 total lines.

**What changes:** Added IntelliJ module descriptor: no file exists at this exact path in the pinned upstream tree. Indexes the overlay source root(s) `source` with inherited compiler output. Declares 25 module dependencies and 1 project libraries. The complete XML or descriptor diff below records every dependency name.

**Behavior and limits:** The root .idea/modules.xml references this file, so it affects IDE indexing. It is not an MPS_OVERRIDES entry and is not used to select Bazel Java sources. Local module names such as kernel2 or webswing are IDE identifiers, not proof of Bazel dependency ownership.

**To make the replacement unnecessary:** Generate the IDE project from the authoritative selected-source/build graph, or keep equivalent descriptors outside the overlay. Update the root IDE module references before relocating metadata. Removing this file alone would break that IDE reference and would not reduce the 100 Java replacements.

Current module descriptor

```xml
<?xml version="1.0" encoding="UTF-8"?>
<module type="JAVA_MODULE" version="4">
  <component name="NewModuleRootManager" inherit-compiler-output="true">
    <exclude-output />
    <content url="file://$MODULE_DIR$">
      <sourceFolder url="file://$MODULE_DIR$/source" isTestSource="false" />
    </content>
    <orderEntry type="inheritedJdk" />
    <orderEntry type="sourceFolder" forTests="false" />
    <orderEntry type="library" name="lib" level="project" />
    <orderEntry type="module" module-name="browser" />
    <orderEntry type="module" module-name="util4" />
    <orderEntry type="module" module-name="mps-platform" />
    <orderEntry type="module" module-name="mps-workbench" />
    <orderEntry type="module" module-name="typesystemEngine" />
    <orderEntry type="module" module-name="smodel" />
    <orderEntry type="module" module-name="util2" />
    <orderEntry type="module" module-name="typechecking2" />
    <orderEntry type="module" module-name="kernel2" />
    <orderEntry type="module" module-name="project-check" />
    <orderEntry type="module" module-name="kernelSolution" />
    <orderEntry type="module" module-name="mps-platform2" />
    <orderEntry type="module" module-name="smodelRuntime" />
    <orderEntry type="module" module-name="logging2" />
    <orderEntry type="module" module-name="smodel2" />
    <orderEntry type="module" module-name="kernel" />
    <orderEntry type="module" module-name="openapi2" />
    <orderEntry type="module" module-name="core" />
    <orderEntry type="module" module-name="typechecking" />
    <orderEntry type="module" module-name="kernelSolution2" />
    <orderEntry type="module" module-name="openapi" />
    <orderEntry type="module" module-name="vfs" />
    <orderEntry type="module" module-name="persistence" />
    <orderEntry type="module" module-name="project3" />
    <orderEntry type="module" module-name="vfs2" />
  </component>
</module>
```

### F056 ArrayWrapper.java

**Path:** `core/util/source/jetbrains/mps/util/ArrayWrapper.java`

[Browser file](C:/Codex/mps-j2cl/mps-web/core/util/source/jetbrains/mps/util/ArrayWrapper.java) · [Compared upstream file](C:/Codex/mps-j2cl/mps/core/util/source/jetbrains/mps/util/ArrayWrapper.java)

**Classification:** Java APIs and text processing. Text delta: +1 / −1 lines; 104 → 104 total lines.

**What changes:** Replaces the reflective-type Arrays.copyOf overload with Arrays.copyOf of the caller's destination array; existing arraycopy then fills the enlarged array.

**Behavior and limits:** The requested destination array type is retained and normal array-store checks still matter.

**To make the replacement unnecessary:** Supply the three-argument Arrays.copyOf overload with real runtime array-type behavior in the shared JRE. Test undersized arrays, incompatible elements and null-termination of larger arrays.

Complete textual diff

```diff
--- mps/core/util/source/jetbrains/mps/util/ArrayWrapper.java
+++ mps-web/core/util/source/jetbrains/mps/util/ArrayWrapper.java
@@ -82,7 +82,7 @@
   public <T> T[] toArray(@NotNull T[] a) {
     if (a.length < size()) {
       // Make a new array of a's runtime type, but my contents:
-      return (T[]) Arrays.copyOf(myArray, size(), a.getClass());
+      a = Arrays.copyOf(a, size());
     }
     System.arraycopy(myArray, 0, a, 0, size());
     if (a.length > size()) {
```

### F057 NameUtil.java

**Path:** `core/util/source/jetbrains/mps/util/NameUtil.java`

[Browser file](C:/Codex/mps-j2cl/mps-web/core/util/source/jetbrains/mps/util/NameUtil.java) · [Compared upstream file](C:/Codex/mps-j2cl/mps/core/util/source/jetbrains/mps/util/NameUtil.java)

**Classification:** Java APIs and text processing. Text delta: +32 / −14 lines; 592 → 610 total lines.

**What changes:** Replaces the ASCII identifier regex with a direct scanner and three StringTokenizer loops with a delimiter scanner, retaining optional individual delimiters.

**Behavior and limits:** Name formatting remains tailored to its original ASCII grammar and delimiter sets. Both StringTokenizer and much of the regex subset now exist centrally.

**To make the replacement unnecessary:** Trial restoring both facilities against the existing shared implementations. Check quote delimiters, whitespace and the nested-class form of the original identifier regex before removing the whole overlay.

Representative excerpts from the compared files; the prose above covers additional changes.

Pinned upstream at line 37

```java
public class NameUtil {
  private static final Pattern VALID_IDENTIFIER_PATTERN = Pattern.compile("[a-zA-Z[_]][a-zA-Z0-9[_]]*");

  private static final String DOT_STRUCTURE_DOT = "." + "structure" + ".";

  private static final HashSet<String> PREPOSITIONS;
  private static final HashSet<String> PARTICLES;
  private static final HashSet<String> ARTICLES;

  private static final Map<String, String> ourCompactNamespaceCache;
  static {
    // map, ordered by access (least recently to most recently)
```

Browser replacement at line 35

```java
public class NameUtil {
  private static boolean isAsciiIdentifier(String value) {
    if (value.isEmpty()) return false;
    for (int i = 0; i < value.length(); i++) {
      char c = value.charAt(i);
      if (c == '_' || c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z') continue;
      if (i > 0 && c >= '0' && c <= '9') continue;
      return false;
    }
    return true;
  }

```

### F058 PerformanceTracer.java

**Path:** `core/util/source/jetbrains/mps/util/performance/PerformanceTracer.java`

[Browser file](C:/Codex/mps-j2cl/mps-web/core/util/source/jetbrains/mps/util/performance/PerformanceTracer.java) · [Compared upstream file](C:/Codex/mps-j2cl/mps/core/util/source/jetbrains/mps/util/performance/PerformanceTracer.java)

**Classification:** Java APIs and text processing. Text delta: +1 / −1 lines; 293 → 293 total lines.

**What changes:** Replaces PT-Task(%s) formatting with concatenation in the task description.

**Behavior and limits:** Tracing behavior is unchanged; current shared %s formatting covers this expression.

**To make the replacement unnecessary:** Trial removal with the current formatter and compare task descriptions.

Complete textual diff

```diff
--- mps/core/util/source/jetbrains/mps/util/performance/PerformanceTracer.java
+++ mps-web/core/util/source/jetbrains/mps/util/performance/PerformanceTracer.java
@@ -287,7 +287,7 @@
 
     @Override
     public String toString() {
-      return String.format("PT-Task(%s)", name);
+      return "PT-Task(" + name + ")";
     }
   }
 }
```

### F059 StringUtil.java

**Path:** `core/util/source/jetbrains/mps/util/StringUtil.java`

[Browser file](C:/Codex/mps-j2cl/mps-web/core/util/source/jetbrains/mps/util/StringUtil.java) · [Compared upstream file](C:/Codex/mps-j2cl/mps/core/util/source/jetbrains/mps/util/StringUtil.java)

**Classification:** Java APIs and text processing. Text delta: +6 / −3 lines; 304 → 307 total lines.

**What changes:** Replaces stream collection with an ArrayList loop and List.getFirst with get(0) inside capitalized-word-list handling.

**Behavior and limits:** The internal list becomes mutable but stays local. Current shared Stream.toList and List.getFirst implementations are present, so the original API-gap reason is no longer sufficient on its own.

**To make the replacement unnecessary:** Trial the original against current JRE APIs and word-list edge cases, including empty input, spacing and capitalization.

Complete textual diff

```diff
--- mps/core/util/source/jetbrains/mps/util/StringUtil.java
+++ mps-web/core/util/source/jetbrains/mps/util/StringUtil.java
@@ -19,7 +19,7 @@
 import org.jetbrains.annotations.NotNull;
 import org.jetbrains.annotations.Nullable;
 
-import java.util.Arrays;
+import java.util.ArrayList;
 import java.util.List;
 
 /**
@@ -264,13 +264,16 @@
    * Motivated by NlsCapitalizationUtil::checkSentenceCapitalization
    */
   public static boolean checkShortTitleCapitalization(@NotNull String value) {
-    List<String> words = Arrays.stream(value.split(" ")).map(String::trim).toList();
+    List<String> words = new ArrayList<>();
+    for (String part : value.split(" ")) {
+      words.add(part.trim());
+    }
     final int wordCount = words.size();
     if (wordCount == 0) {
       return true;
     }
 
-    if (Character.isLetter(words.getFirst().charAt(0)) && !isCapitalizedWord(words.getFirst())) {
+    if (Character.isLetter(words.get(0).charAt(0)) && !isCapitalizedWord(words.get(0))) {
       return false;
     }
     if (wordCount == 1) {
```

### F060 WeakSet.java

**Path:** `core/util/source/jetbrains/mps/util/WeakSet.java`

[Browser file](C:/Codex/mps-j2cl/mps-web/core/util/source/jetbrains/mps/util/WeakSet.java) · [Compared upstream file](C:/Codex/mps-j2cl/mps/core/util/source/jetbrains/mps/util/WeakSet.java)

**Classification:** Weak references and lifecycle. Text delta: +164 / −32 lines; 67 → 199 total lines.

**What changes:** Replaces WeakHashMap-backed WeakSet with an equality/hash-aware list of native WeakRef entries, access-time cleanup and fail-fast snapshot iterators that retain references rather than all referents.

**Behavior and limits:** Object-backed values are weak; null and primitive-backed String, Boolean and Double values are intentionally retained strongly. Operations scan the entry list, changing cost from map lookup toward linear scans.

**To make the replacement unnecessary:** Centralize compatible weak collections in the shared runtime, accounting for Java equality, JS primitive representations, iterator behavior and GC nondeterminism. Do not replace this with native WeakSet, whose identity semantics differ.

Representative excerpts from the compared files; the prose above covers additional changes.

Pinned upstream at line 47

```java
  public boolean add(T t) {
    return myWeakHashMap.put(t, VALUE) == null;
  }

  @Override
  public boolean remove(Object o) {
    return myWeakHashMap.remove(o) != null;
  }

  @NotNull
  @Override
  public Iterator<T> iterator() {
```

Browser replacement at line 169

```java
      hash = Objects.hashCode(value);
      if (value == null || value instanceof String || value instanceof Boolean || value instanceof Double) {
        reference = null;
        retained = value;
      } else {
        if (getWeakRefConstructor() == null) {
          throw new UnsupportedOperationException("WeakSet requires native WeakRef support");
        }
        reference = new NativeWeakRef<>(value);
        retained = null;
      }
    }
```

### F061 util2.iml

**Path:** `core/util/util2.iml`

[Browser file](C:/Codex/mps-j2cl/mps-web/core/util/util2.iml) · No same-path file at the pinned upstream revision

**Classification:** IntelliJ project metadata. Text delta: +13 / −0 lines; 0 → 13 total lines.

**What changes:** Added IntelliJ module descriptor: no file exists at this exact path in the pinned upstream tree. Indexes the overlay source root(s) `source` with inherited compiler output. Declares 2 module dependencies. The complete XML or descriptor diff below records every dependency name.

**Behavior and limits:** The root .idea/modules.xml references this file, so it affects IDE indexing. It is not an MPS_OVERRIDES entry and is not used to select Bazel Java sources. Local module names such as kernel2 or webswing are IDE identifiers, not proof of Bazel dependency ownership.

**To make the replacement unnecessary:** Generate the IDE project from the authoritative selected-source/build graph, or keep equivalent descriptors outside the overlay. Update the root IDE module references before relocating metadata. Removing this file alone would break that IDE reference and would not reduce the 100 Java replacements.

Current module descriptor

```xml
<?xml version="1.0" encoding="UTF-8"?>
<module type="JAVA_MODULE" version="4">
  <component name="NewModuleRootManager" inherit-compiler-output="true">
    <exclude-output />
    <content url="file://$MODULE_DIR$">
      <sourceFolder url="file://$MODULE_DIR$/source" isTestSource="false" />
    </content>
    <orderEntry type="inheritedJdk" />
    <orderEntry type="sourceFolder" forTests="false" />
    <orderEntry type="module" module-name="openapi" />
    <orderEntry type="module" module-name="openapi2" />
  </component>
</module>
```

### F062 Files.java

**Path:** `core/vfs/deprecated/jetbrains/mps/vfs/Files.java`

[Browser file](C:/Codex/mps-j2cl/mps-web/core/vfs/deprecated/jetbrains/mps/vfs/Files.java) · [Compared upstream file](C:/Codex/mps-j2cl/mps/core/vfs/deprecated/jetbrains/mps/vfs/Files.java)

**Classification:** Java APIs and text processing. Text delta: +1 / −1 lines; 76 → 76 total lines.

**What changes:** Replaces one archive-recognition warning String.format with concatenation.

**Behavior and limits:** Archive detection itself is unchanged; this file is not one of the archive-content implementations.

**To make the replacement unnecessary:** Trial removal with the current shared %s formatter and the existing warning path.

Complete textual diff

```diff
--- mps/core/vfs/deprecated/jetbrains/mps/vfs/Files.java
+++ mps-web/core/vfs/deprecated/jetbrains/mps/vfs/Files.java
@@ -33,7 +33,7 @@
     boolean result = isJarOrZipFile0(file);
     String absolutePath = file.getAbsolutePath();
     if (!result && (absolutePath.endsWith(".zip") || absolutePath.endsWith(".jar"))) {
-      LOG.warning(String.format("The path '%s' ends with '.jar' or '.zip' but the contents are not recognized as a zip archive", absolutePath));
+      LOG.warning("The path '" + absolutePath + "' ends with '.jar' or '.zip' but the contents are not recognized as a zip archive");
       printDebugOnSuspiciousArchive(file);
     }
     return result;
```

### F063 FileUtil.java

**Path:** `core/vfs/source/jetbrains/mps/util/FileUtil.java`

[Browser file](C:/Codex/mps-j2cl/mps-web/core/vfs/source/jetbrains/mps/util/FileUtil.java) · [Compared upstream file](C:/Codex/mps-j2cl/mps/core/vfs/source/jetbrains/mps/util/FileUtil.java)

**Classification:** Files archives and XML. Text delta: +72 / −84 lines; 693 → 681 total lines.

**What changes:** Removes ZIP packing and throws from both zip overloads; replaces quoted-regex path splitting with a literal splitter; adds shared-to-MPS lexical file-URI encoding for several other overlays.

**Behavior and limits:** ZIP output is unavailable. URI conversion encodes paths but does not authorize/read storage; its ASCII form intentionally avoids the JDK's NFC normalization step. Split behavior handles empty input/separator and trailing empty parts.

**To make the replacement unnecessary:** Separate archive creation into a provider, put File/URI conversion in the owning JRE library, and use the existing regex layer where equivalent. All three responsibilities must be addressed before removing this whole replacement.

Representative excerpts from the compared files; the prose above covers additional changes.

Pinned upstream at line 98

```java
      @Override
      protected ZipOutputStream createDeflaterStream(FileOutputStream fos) {
        return new ZipOutputStream(fos);
      }
    }.pack(dir, to);
  }

  public static void zip(Map<String, File> entries, File to) {
    new Packer() {
      @Override
      protected ZipOutputStream createDeflaterStream(FileOutputStream fos) {
        return new ZipOutputStream(fos);
```

Browser replacement at line 92

```java
  public static void zip(File dir, File to) {
    throw new UnsupportedOperationException("ZIP creation requires an archive provider in the browser profile");
  }

  public static void zip(Map<String, File> entries, File to) {
    throw new UnsupportedOperationException("ZIP creation requires an archive provider in the browser profile");
  }

  public static void copyDir(File what, File to) {
    assert what.isDirectory();
    if (!to.exists()) {
      to.mkdirs();
```

Additional browser code at line 617

```java
   */
  public static String toFileUri(File file, boolean ascii) {
    File absolute = file.getAbsoluteFile();
    String path = absolute.getPath().replace(File.separatorChar, '/');
    if (!path.startsWith("/")) {
      path = "/" + path;
    }
    if (absolute.isDirectory() && !path.endsWith("/")) {
      path += "/";
    }
    if (path.startsWith("//")) {
      path = "//" + path;
```

### F064 IFileUtil.java

**Path:** `core/vfs/source/jetbrains/mps/util/IFileUtil.java`

[Browser file](C:/Codex/mps-j2cl/mps-web/core/vfs/source/jetbrains/mps/util/IFileUtil.java) · [Compared upstream file](C:/Codex/mps-j2cl/mps/core/vfs/source/jetbrains/mps/util/IFileUtil.java)

**Classification:** Java APIs and text processing. Text delta: +12 / −4 lines; 200 → 208 total lines.

**What changes:** Replaces two formatted diagnostics and replaces SimpleDateFormat(yyyy-MM-dd-) for temporary-directory prefixes with local Date fields and explicit numeric padding.

**Behavior and limits:** This is not just a logging change: local timezone fields remain, but numeric Gregorian formatting is fixed rather than locale-driven. It does not supply a browser temporary filesystem.

**To make the replacement unnecessary:** Provide the needed date-formatting contract or upstream a deterministic date-prefix helper; shared String.format already covers the messages. Test timezone/date boundaries and storage separately.

Complete textual diff

```diff
--- mps/core/vfs/source/jetbrains/mps/util/IFileUtil.java
+++ mps-web/core/vfs/source/jetbrains/mps/util/IFileUtil.java
@@ -29,7 +29,6 @@
 import java.io.IOException;
 import java.io.InputStreamReader;
 import java.io.OutputStream;
-import java.text.SimpleDateFormat;
 import java.util.ArrayList;
 import java.util.Date;
 import java.util.List;
@@ -48,7 +47,7 @@
       out.write(bytes);
       return true;
     } catch (IOException e) {
-      LOG.error(String.format("Failed to copy %s to %s", oldFile, newFile), e);
+      LOG.error("Failed to copy " + oldFile + " to " + newFile, e);
       return false;
     } finally {
       FileUtil.closeFileSafe(in);
@@ -80,7 +79,7 @@
     try {
       return file.isZipArchive();
     } catch (IOException e) {
-      String message = String.format("received io error when trying to access the file %s", file);
+      String message = "received io error when trying to access the file " + file;
       LOG.error(message, e);
       return false;
     }
@@ -136,7 +135,16 @@
     // puzzling failures (i.e. U see the file at fs location, but VirtualFile for the same (though, aliased) location doesn't list it).
     tmpHome = vfs.getFile(getCanonicalPath(tmpHome));
     int i = 1;
-    String prefix = "mps-" + new SimpleDateFormat("yyyy-MM-dd-").format(new Date());
+    // Date's local calendar fields preserve the upstream default-time-zone date.
+    // The browser profile uses numeric Gregorian names independent of host locale.
+    Date today = new Date();
+    String year = Integer.toString(today.getYear() + 1900);
+    while (year.length() < 4) {
+      year = "0" + year;
+    }
+    int month = today.getMonth() + 1;
+    int day = today.getDate();
+    String prefix = "mps-" + year + "-" + (month < 10 ? "0" : "") + month + "-" + (day < 10 ? "0" : "") + day + "-";
     while (tmpHome.findChild(prefix + i).exists()) {
       i++;
     }
```

### F065 MacroHelper.java

**Path:** `core/vfs/source/jetbrains/mps/util/MacroHelper.java`

[Browser file](C:/Codex/mps-j2cl/mps-web/core/vfs/source/jetbrains/mps/util/MacroHelper.java) · [Compared upstream file](C:/Codex/mps-j2cl/mps/core/vfs/source/jetbrains/mps/util/MacroHelper.java)

**Classification:** Java APIs and text processing. Text delta: +77 / −3 lines; 96 → 170 total lines.

**What changes:** Changes the public MACRO_PATTERN field type from Pattern to a custom MacroPattern, with MacroMatcher supporting find, start, end and groups 0/1. It scans the original greedy grammar, including empty names and extra closing braces.

**Behavior and limits:** Existing callers naming java.util.regex.Matcher must change; this causes the PathSpec and ProjectModuleLoader overlays. It is a narrow public API substitution.

**To make the replacement unnecessary:** Trial the original grammar in the current shared Pattern/Matcher layer, or upstream a stable macro-specific API. Restore all dependent caller types together and verify greedy grouping and failed-match exceptions.

Representative excerpts from the compared files; the prose above covers additional changes.

Pinned upstream at line 26

```java
  // XXX is it intentional to match "${}" sequence (note *, not +)?
  Pattern MACRO_PATTERN = Pattern.compile("(\\$\\{[^${]*})");

  String expandPath(@Nullable String path);

  /**
   * @deprecated use {@link #shrinkPath(IFile, String)} or {@link #shrinkPath(String, String)} instead
   */
  @Deprecated(since = "2022.3", forRemoval = true)
  default String shrinkPath(@Nullable String absolutePath) {
    return shrinkPath(absolutePath, null);
  }
```

Browser replacement at line 29

```java
   */
  MacroPattern MACRO_PATTERN = new MacroPattern();

  final class MacroPattern {
    public MacroMatcher matcher(CharSequence input) {
      return new MacroMatcher(input.toString());
    }
  }

  final class MacroMatcher {
    private final String input;
    private int next;
```

### F066 PathSpec.java

**Path:** `core/vfs/source/jetbrains/mps/util/PathSpec.java`

[Browser file](C:/Codex/mps-j2cl/mps-web/core/vfs/source/jetbrains/mps/util/PathSpec.java) · [Compared upstream file](C:/Codex/mps-j2cl/mps/core/vfs/source/jetbrains/mps/util/PathSpec.java)

**Classification:** Java APIs and text processing. Text delta: +2 / −3 lines; 110 → 109 total lines.

**What changes:** Changes only two local Matcher declarations to MacroHelper.MacroMatcher and removes the import.

**Behavior and limits:** Macro detection/enumeration still call the same-shaped API; this is a cascading change from MacroHelper.

**To make the replacement unnecessary:** Restore MacroHelper's original Pattern type or upstream a stable macro abstraction. No independent browser implementation is needed in PathSpec once the shared boundary is resolved.

Complete textual diff

```diff
--- mps/core/vfs/source/jetbrains/mps/util/PathSpec.java
+++ mps-web/core/vfs/source/jetbrains/mps/util/PathSpec.java
@@ -8,7 +8,6 @@
 
 import java.util.Optional;
 import java.util.function.Function;
-import java.util.regex.Matcher;
 import java.util.stream.Stream;
 import java.util.stream.Stream.Builder;
 
@@ -50,13 +49,13 @@
    * user can enter a value, {@link #hasMacro()} is more appropriate.
    */
   public boolean startsWithMacro() {
-    final Matcher mm = MacroHelper.MACRO_PATTERN.matcher(myValue);
+    final MacroHelper.MacroMatcher mm = MacroHelper.MACRO_PATTERN.matcher(myValue);
     return mm.find() && mm.start() == 0;
   }
 
   public Stream<String> allMacro() {
     final Builder<String> builder = Stream.builder();
-    final Matcher mm = MacroHelper.MACRO_PATTERN.matcher(myValue);
+    final MacroHelper.MacroMatcher mm = MacroHelper.MACRO_PATTERN.matcher(myValue);
     while (mm.find()) {
       final String macroName = myValue.substring(mm.start(), mm.end());
       if (macroName.isBlank()) {
```

### F067 LocalFile.java

**Path:** `core/vfs/source/jetbrains/mps/vfs/iofs/file/LocalFile.java`

[Browser file](C:/Codex/mps-j2cl/mps-web/core/vfs/source/jetbrains/mps/vfs/iofs/file/LocalFile.java) · [Compared upstream file](C:/Codex/mps-j2cl/mps/core/vfs/source/jetbrains/mps/vfs/iofs/file/LocalFile.java)

**Classification:** Files archives and XML. Text delta: +1 / −1 lines; 320 → 320 total lines.

**What changes:** Replaces LocalFile's File.toURI().toURL call with the FileUtil encoder and URL constructor.

**Behavior and limits:** This is lexical URL compatibility, not a new implementation of the rest of LocalFile's filesystem operations.

**To make the replacement unnecessary:** Implement the File/URI/URL conversion chain centrally and test escaping and directories. Supply a filesystem provider separately for real storage.

Complete textual diff

```diff
--- mps/core/vfs/source/jetbrains/mps/vfs/iofs/file/LocalFile.java
+++ mps-web/core/vfs/source/jetbrains/mps/vfs/iofs/file/LocalFile.java
@@ -125,7 +125,7 @@
 
   @Override
   public URL getUrl() throws MalformedURLException {
-    return myFile.toURI().toURL();
+    return new URL(FileUtil.toFileUri(myFile, false));
   }
 
   @Override
```

### F068 JarEntryFile.java

**Path:** `core/vfs/source/jetbrains/mps/vfs/iofs/jar/JarEntryFile.java`

[Browser file](C:/Codex/mps-j2cl/mps-web/core/vfs/source/jetbrains/mps/vfs/iofs/jar/JarEntryFile.java) · [Compared upstream file](C:/Codex/mps-j2cl/mps/core/vfs/source/jetbrains/mps/vfs/iofs/jar/JarEntryFile.java)

**Classification:** Files archives and XML. Text delta: +4 / −4 lines; 306 → 306 total lines.

**What changes:** Replaces URI-based jar URL construction with FileUtil encoding and new URL, and changes lastModified to an explicit unsupported-operation failure.

**Behavior and limits:** The manifest highlights URL conversion but also loses archive timestamps. Other content methods depend on the deliberately unsupported JarFileData backend.

**To make the replacement unnecessary:** Provide both shared URI conversion and an archive/filesystem timestamp provider. Restoring only URL APIs leaves the timestamp/content boundary unresolved.

Complete textual diff

```diff
--- mps/core/vfs/source/jetbrains/mps/vfs/iofs/jar/JarEntryFile.java
+++ mps-web/core/vfs/source/jetbrains/mps/vfs/iofs/jar/JarEntryFile.java
@@ -17,6 +17,7 @@
 
 import jetbrains.mps.logging.Logger;
 import jetbrains.mps.util.IFileUtil;
+import jetbrains.mps.util.FileUtil;
 import jetbrains.mps.util.annotation.Hack;
 import jetbrains.mps.vfs.FileSystem;
 import jetbrains.mps.vfs.IFile;
@@ -35,7 +36,6 @@
 import java.io.InputStream;
 import java.io.OutputStream;
 import java.net.MalformedURLException;
-import java.net.URI;
 import java.net.URL;
 import java.util.ArrayList;
 import java.util.Collections;
@@ -184,7 +184,7 @@
 
   @Override
   public long lastModified() {
-    return myJarFileData.getFile().lastModified();
+    throw new UnsupportedOperationException("Archive timestamps require a filesystem provider in the browser profile");
   }
 
   @Override
@@ -279,8 +279,8 @@
   @Hack
   @Override
   public URL getUrl() throws MalformedURLException {
-    String encoded = new File(myJarFileData.getFile().getAbsoluteFile() + Path.ARCHIVE_SEPARATOR + myEntryPath).toURI().toASCIIString();
-    return URI.create("jar:" + encoded).toURL();
+    String encoded = FileUtil.toFileUri(new File(myJarFileData.getFile().getAbsoluteFile() + Path.ARCHIVE_SEPARATOR + myEntryPath), true);
+    return new URL("jar:" + encoded);
   }
 
   @Override
```

### F069 JarFileData.java

**Path:** `core/vfs/source/jetbrains/mps/vfs/iofs/jar/JarFileData.java`

[Browser file](C:/Codex/mps-j2cl/mps-web/core/vfs/source/jetbrains/mps/vfs/iofs/jar/JarFileData.java) · [Compared upstream file](C:/Codex/mps-j2cl/mps/core/vfs/source/jetbrains/mps/vfs/iofs/jar/JarFileData.java)

**Classification:** Files archives and XML. Text delta: +11 / −243 lines; 302 → 70 total lines.

**What changes:** Removes ZIP/JAR opening, entry/directory caches, initialization state and archive streams. Keeps lexical file/path helpers while entry listing, existence, directory checks, streams and lengths throw.

**Behavior and limits:** The API does not silently pretend an archive is empty. There is no archive-content implementation in this replacement.

**To make the replacement unnecessary:** Introduce a buffer-backed ZIP/JAR provider with explicit IO ownership, or split lexical handles from archive operations upstream. Verify listing, streams, missing entries and cleanup before reusing the original.

Representative excerpts from the compared files; the prose above covers additional changes.

Pinned upstream at line 119

```java

  InputStream openStream(String path) throws IOException {
    if (!ensureInitialized()) {
      throw new IOException("Unable to read archive '" + getFile() + "'", getInitializationFailure());
    }

    ZipEntry entry = myEntries.get(path);
    if (entry == null) {
      throw new FileNotFoundException("Archive entry does not exist: " + getFile() + "!/" + path);
    }
    // XXX I wonder why not to count InputStream open()/close() to assess when zipFile might be ready to go/close().
    return new MyInputStream(entry);
```

Browser replacement at line 58

```java

  InputStream openStream(String path) throws IOException {
    throw unavailable();
  }

  long getLength(String path) {
    throw unavailable();
  }

  private UnsupportedOperationException unavailable() {
    return new UnsupportedOperationException("Archive content requires a ZIP/JAR provider in the browser profile: " + myFile);
  }
```

### F070 JarFileDataCache.java

**Path:** `core/vfs/source/jetbrains/mps/vfs/iofs/jar/JarFileDataCache.java`

[Browser file](C:/Codex/mps-j2cl/mps-web/core/vfs/source/jetbrains/mps/vfs/iofs/jar/JarFileDataCache.java) · [Compared upstream file](C:/Codex/mps-j2cl/mps/core/vfs/source/jetbrains/mps/vfs/iofs/jar/JarFileDataCache.java)

**Classification:** Files archives and XML. Text delta: +4 / −117 lines; 142 → 29 total lines.

**What changes:** Removes the weak-reference cache, ReferenceQueue cleanup and closing of cached ZipFiles; getData now returns a new JarFileData handle.

**Behavior and limits:** No identity cache remains and handles own no open archive resources in this profile. Path-based equality elsewhere compensates for new instances.

**To make the replacement unnecessary:** Once archive access exists, put resource ownership/cache policy behind an upstream service or supply compatible weak-cache support centrally. Do not restore a cache independently of its archive-resource lifecycle.

Representative excerpts from the compared files; the prose above covers additional changes.

Pinned upstream at line 43

```java

  private final ReferenceQueue<JarFileData> myQueue = new ReferenceQueue<>();
  private final Map<String, JarFileDataWeakReference> myPathToRef = new HashMap<>();

  private final Object myLock = new Object();

  /*package*/ JarFileDataCache() {
  }

  public JarFileData getDataFor(File file) {
    synchronized (myLock) {
      removeGCedReferences();
```

Browser replacement at line 18

```java
import java.io.File;

/**
 * Lexical archive handles own no streams or native resources in this profile.
 * They are not retained in a strong global cache or tied to unavailable Java
 * weak-reference notifications. JarEntryFile equality also compares file paths.
 */
final class JarFileDataCache {
  public JarFileData getDataFor(File file) {
    return new JarFileData(file, this);
  }
}
```

### F071 ZipFileContainer.java

**Path:** `core/vfs/source/jetbrains/mps/vfs/iofs/jar/ZipFileContainer.java`

[Browser file](C:/Codex/mps-j2cl/mps-web/core/vfs/source/jetbrains/mps/vfs/iofs/jar/ZipFileContainer.java) · [Compared upstream file](C:/Codex/mps-j2cl/mps/core/vfs/source/jetbrains/mps/vfs/iofs/jar/ZipFileContainer.java)

**Classification:** Files archives and XML. Text delta: +4 / −8 lines; 27 → 23 total lines.

**What changes:** Removes the ZipFile import and zipFile field, leaving an empty package-private shell with an explanatory comment.

**Behavior and limits:** Despite the manifest's broad archive-failure rationale, this class itself throws nothing and carries no archive identity. Its consumers' content operations fail elsewhere.

**To make the replacement unnecessary:** Exclude this desktop resource-holder implementation through an upstream platform split, or restore it only with a working archive provider and matching cache cleanup.

Complete textual diff

```diff
--- mps/core/vfs/source/jetbrains/mps/vfs/iofs/jar/ZipFileContainer.java
+++ mps-web/core/vfs/source/jetbrains/mps/vfs/iofs/jar/ZipFileContainer.java
@@ -15,13 +15,9 @@
  */
 package jetbrains.mps.vfs.iofs.jar;
 
-import java.util.zip.ZipFile;
-
-
-// todo [MM] want to rewrite it as a weak ref to a ZipFile in JarEntryFile plus cache of already-created Zip-files.
-// todo [MM] This should allow to remove AbstractJarFileData, ZipFileContainer and JarFileData. Also, it will make possible to collect ZipFile
-// todo [MM] while JarFile is still held by someone. Last but not least, to improve readability
-// this class is needed to store a pointer to a "created on demand" ZipFile for closing it when IFile holding it is collected by GC
+/**
+ * Upstream implementation type retained in the source inventory. The browser
+ * archive boundary opens no ZIP files and therefore has no resources to close.
+ */
 class ZipFileContainer {
-  volatile ZipFile zipFile;
 }
```

### F072 PathFormats.java

**Path:** `core/vfs/source/jetbrains/mps/vfs/path/PathFormats.java`

[Browser file](C:/Codex/mps-j2cl/mps-web/core/vfs/source/jetbrains/mps/vfs/path/PathFormats.java) · [Compared upstream file](C:/Codex/mps-j2cl/mps/core/vfs/source/jetbrains/mps/vfs/path/PathFormats.java)

**Classification:** Java APIs and text processing. Text delta: +10 / −5 lines; 319 → 324 total lines.

**What changes:** Replaces path.split(Pattern.quote(separator)) with indexOf-based literal splitting and omission of empty segments.

**Behavior and limits:** This serves the internal path separators, not arbitrary regex splitting. Shared Pattern.quote already exists, but String.split translation must also be checked.

**To make the replacement unnecessary:** Trial the upstream path implementation with the current shared regex/String layer; verify leading/trailing/repeated separators and platform-specific path forms.

Complete textual diff

```diff
--- mps/core/vfs/source/jetbrains/mps/vfs/path/PathFormats.java
+++ mps-web/core/vfs/source/jetbrains/mps/vfs/path/PathFormats.java
@@ -26,7 +26,6 @@
 import java.util.Arrays;
 import java.util.List;
 import java.util.Locale;
-import java.util.regex.Pattern;
 import java.util.stream.Collectors;
 import java.util.stream.Stream;
 
@@ -291,10 +290,16 @@
       assert (path.startsWith(uncPrefix));
       path = path.substring(uncPrefix.length());
     }
-    for (String s : path.split(Pattern.quote(separator))) {
-      if (!s.isEmpty()) {
-        result.add(s);
-      }
+    int from = 0;
+    int next;
+    while ((next = path.indexOf(separator, from)) >= 0) {
+      if (next > from) {
+        result.add(path.substring(from, next));
+      }
+      from = next + separator.length();
+    }
+    if (from < path.length()) {
+      result.add(path.substring(from));
     }
     return result;
   }
```

### F073 QualifiedPath.java

**Path:** `core/vfs/source/jetbrains/mps/vfs/QualifiedPath.java`

[Browser file](C:/Codex/mps-j2cl/mps-web/core/vfs/source/jetbrains/mps/vfs/QualifiedPath.java) · [Compared upstream file](C:/Codex/mps-j2cl/mps/core/vfs/source/jetbrains/mps/vfs/QualifiedPath.java)

**Classification:** Java APIs and text processing. Text delta: +8 / −1 lines; 80 → 87 total lines.

**What changes:** Replaces fs.chars().allMatch(Character::isLetter) with an explicit UTF-16 char loop using Character.isLetter(char).

**Behavior and limits:** This preserves char-unit validation rather than accepting all supplementary letter code points. Current shared Character.isLetter(int) support exists, but the complete stream/method-reference expression still needs compilation verification.

**To make the replacement unnecessary:** Trial the original expression against the current JRE. Verify empty names, surrogate pairs and assertions as well as compilation.

Complete textual diff

```diff
--- mps/core/vfs/source/jetbrains/mps/vfs/QualifiedPath.java
+++ mps-web/core/vfs/source/jetbrains/mps/vfs/QualifiedPath.java
@@ -42,9 +42,16 @@
    * @param path could be any
    */
   public QualifiedPath(@NotNull String fs, @NotNull String path) {
-    assert fs.chars().allMatch(Character::isLetter);
+    assert isFilesystemName(fs);
     myFsId = fs;
     myPath = path;
+  }
+
+  private static boolean isFilesystemName(String fs) {
+    for (int i = 0; i < fs.length(); i++) {
+      if (!Character.isLetter(fs.charAt(i))) return false;
+    }
+    return true;
   }
 
   @NotNull
```

### F074 PathFormatChecker.java

**Path:** `core/vfs/source/jetbrains/mps/vfs/util/PathFormatChecker.java`

[Browser file](C:/Codex/mps-j2cl/mps-web/core/vfs/source/jetbrains/mps/vfs/util/PathFormatChecker.java) · [Compared upstream file](C:/Codex/mps-j2cl/mps/core/vfs/source/jetbrains/mps/vfs/util/PathFormatChecker.java)

**Classification:** Java APIs and text processing. Text delta: +1 / −3 lines; 151 → 149 total lines.

**What changes:** Replaces one String.format diagnostic for relative paths with concatenation while retaining the os.name property lookup.

**Behavior and limits:** Path validation is unchanged; current shared formatting supports its %s placeholders.

**To make the replacement unnecessary:** Trial removal against the current formatter and verify the exact exception message and stored problem path.

Complete textual diff

```diff
--- mps/core/vfs/source/jetbrains/mps/vfs/util/PathFormatChecker.java
+++ mps-web/core/vfs/source/jetbrains/mps/vfs/util/PathFormatChecker.java
@@ -54,9 +54,7 @@
 
   public PathFormatChecker absolute() {
     if (!isAbsolute(myPath)) {
-      throw new PathFormatException(String.format("Path should be absolute: %s, os %s",
-                                                  myPath,
-                                                  System.getProperty("os.name")), myPath);
+      throw new PathFormatException("Path should be absolute: " + myPath + ", os " + System.getProperty("os.name"), myPath);
     }
     return this;
   }
```

### F075 vfs2.iml

**Path:** `core/vfs/vfs2.iml`

[Browser file](C:/Codex/mps-j2cl/mps-web/core/vfs/vfs2.iml) · No same-path file at the pinned upstream revision

**Classification:** IntelliJ project metadata. Text delta: +16 / −0 lines; 0 → 16 total lines.

**What changes:** Added IntelliJ module descriptor: no file exists at this exact path in the pinned upstream tree. Indexes the overlay source root(s) `deprecated`, `source` with inherited compiler output. Declares 4 module dependencies. The complete XML or descriptor diff below records every dependency name.

**Behavior and limits:** The root .idea/modules.xml references this file, so it affects IDE indexing. It is not an MPS_OVERRIDES entry and is not used to select Bazel Java sources. Local module names such as kernel2 or webswing are IDE identifiers, not proof of Bazel dependency ownership.

**To make the replacement unnecessary:** Generate the IDE project from the authoritative selected-source/build graph, or keep equivalent descriptors outside the overlay. Update the root IDE module references before relocating metadata. Removing this file alone would break that IDE reference and would not reduce the 100 Java replacements.

Current module descriptor

```xml
<?xml version="1.0" encoding="UTF-8"?>
<module type="JAVA_MODULE" version="4">
  <component name="NewModuleRootManager" inherit-compiler-output="true">
    <exclude-output />
    <content url="file://$MODULE_DIR$">
      <sourceFolder url="file://$MODULE_DIR$/deprecated" isTestSource="false" />
      <sourceFolder url="file://$MODULE_DIR$/source" isTestSource="false" />
    </content>
    <orderEntry type="inheritedJdk" />
    <orderEntry type="sourceFolder" forTests="false" />
    <orderEntry type="module" module-name="mps-annotations" />
    <orderEntry type="module" module-name="logging2" />
    <orderEntry type="module" module-name="openapi" />
    <orderEntry type="module" module-name="vfs" />
  </component>
</module>
```

### F076 editing-runtime.iml

**Path:** `editor/editing-runtime/editing-runtime.iml`

[Browser file](C:/Codex/mps-j2cl/mps-web/editor/editing-runtime/editing-runtime.iml) · [Compared upstream file](C:/Codex/mps-j2cl/mps/editor/editing-runtime/editing-runtime.iml)

**Classification:** IntelliJ project metadata. Text delta: +19 / −5 lines; 17 → 31 total lines.

**What changes:** Customized IntelliJ descriptor with a same-path upstream counterpart. Indexes the overlay source root(s) `source_gen` with inherited compiler output. Declares 20 module dependencies. The complete XML or descriptor diff below records every dependency name. Added module references: `browser`, `components`, `constraints`, `core`, `kernel2`, `kernelSolution2`, `mps-annotations`, `mps-platform`, `mps-platform2`, `mps-workbench`, `openapi`, `openapi2`, `persistence`, `project3`, `runtime2`, `smodel`, `smodel2`, `smodelRuntime`.

**Behavior and limits:** The root .idea/modules.xml references this file, so it affects IDE indexing. It is not an MPS_OVERRIDES entry and is not used to select Bazel Java sources. Local module names such as kernel2 or webswing are IDE identifiers, not proof of Bazel dependency ownership.

**To make the replacement unnecessary:** Generate the IDE project from the authoritative selected-source/build graph, or keep equivalent descriptors outside the overlay. Update the root IDE module references before relocating metadata. Removing this file alone would break that IDE reference and would not reduce the 100 Java replacements.

Complete descriptor diff

```diff
--- mps/editor/editing-runtime/editing-runtime.iml
+++ mps-web/editor/editing-runtime/editing-runtime.iml
@@ -1,17 +1,31 @@
 <?xml version="1.0" encoding="UTF-8"?>
 <module type="JAVA_MODULE" version="4">
-  <component name="NewModuleRootManager">
-    <output url="file://$MODULE_DIR$/classes" />
-    <output-test url="file://$MODULE_DIR$/testclasses" />
+  <component name="NewModuleRootManager" inherit-compiler-output="true">
     <exclude-output />
     <content url="file://$MODULE_DIR$">
       <sourceFolder url="file://$MODULE_DIR$/source_gen" isTestSource="false" />
-      <excludeFolder url="file://$MODULE_DIR$/classes" />
     </content>
     <orderEntry type="inheritedJdk" />
     <orderEntry type="sourceFolder" forTests="false" />
+    <orderEntry type="module" module-name="components" />
+    <orderEntry type="module" module-name="mps-platform2" />
+    <orderEntry type="module" module-name="browser" />
+    <orderEntry type="module" module-name="smodelRuntime" />
+    <orderEntry type="module" module-name="mps-annotations" />
+    <orderEntry type="module" module-name="mps-platform" />
+    <orderEntry type="module" module-name="constraints" />
+    <orderEntry type="module" module-name="smodel2" />
+    <orderEntry type="module" module-name="mps-workbench" />
     <orderEntry type="module" module-name="kernel" />
+    <orderEntry type="module" module-name="openapi2" />
+    <orderEntry type="module" module-name="core" />
+    <orderEntry type="module" module-name="smodel" />
+    <orderEntry type="module" module-name="kernelSolution2" />
+    <orderEntry type="module" module-name="kernel2" />
+    <orderEntry type="module" module-name="runtime2" />
+    <orderEntry type="module" module-name="openapi" />
+    <orderEntry type="module" module-name="persistence" />
     <orderEntry type="module" module-name="editor-api" />
-    <orderEntry type="library" name="Annotations" level="project" />
+    <orderEntry type="module" module-name="project3" />
   </component>
 </module>
```

### F077 DataTransferManager.java

**Path:** `editor/editing-runtime/source_gen/jetbrains/mps/datatransfer/DataTransferManager.java`

[Browser file](C:/Codex/mps-j2cl/mps-web/editor/editing-runtime/source_gen/jetbrains/mps/datatransfer/DataTransferManager.java) · [Compared upstream file](C:/Codex/mps-j2cl/mps/editor/editing-runtime/source_gen/jetbrains/mps/datatransfer/DataTransferManager.java)

**Classification:** Weak references and lifecycle. Text delta: +20 / −1 lines; 172 → 191 total lines.

**What changes:** Changes eager static DataTransferManager creation to lazy creation tied to the current LanguageRegistry. Adds unconditional and owner-checked release methods that remove the registry listener and clear copy/paste caches.

**Behavior and limits:** The copy/paste algorithms remain. Owner checking prevents teardown of another browser host's singleton; a missing language registry now fails explicitly.

**To make the replacement unnecessary:** Upstream platform-scoped transfer-manager ownership and disposal, and update the originating MPS model/generator for this source_gen class. JVM reference support alone cannot define the correct owner.

Complete textual diff

```diff
--- mps/editor/editing-runtime/source_gen/jetbrains/mps/datatransfer/DataTransferManager.java
+++ mps-web/editor/editing-runtime/source_gen/jetbrains/mps/datatransfer/DataTransferManager.java
@@ -28,7 +28,7 @@
   /**
    * TODO In fact, it is an editor component. When editor components will be introduced, implement initialization in proper way.
    */
-  private static DataTransferManager INSTANCE = new DataTransferManager(LanguageRegistry.getInstance());
+  private static DataTransferManager INSTANCE;
 
   private LanguageRegistry myLanguageRegistry;
 
@@ -47,7 +47,26 @@
   }
 
   public static DataTransferManager getInstance() {
+    LanguageRegistry registry = LanguageRegistry.getInstance();
+    if (registry == null) throw new IllegalStateException("No language registry installed");
+    if (INSTANCE != null && INSTANCE.myLanguageRegistry != registry) releaseInstance();
+    if (INSTANCE == null) INSTANCE = new DataTransferManager(registry);
     return INSTANCE;
+  }
+
+  /** Release only the transfer manager belonging to the caller's platform. */
+  public static void releaseInstance(LanguageRegistry owner) {
+    if (INSTANCE != null && INSTANCE.myLanguageRegistry == owner) releaseInstance();
+  }
+
+  /** Browser host teardown releases the listener and aspect caches before the platform. */
+  public static void releaseInstance() {
+    if (INSTANCE == null) return;
+    INSTANCE.myLanguageRegistry.removeRegistryListener(INSTANCE);
+    INSTANCE.myCopyPreProcessors.clear();
+    INSTANCE.myPastePostProcessors.clear();
+    INSTANCE.myPasteWrappers.clear();
+    INSTANCE = null;
   }
 
   @Override
```

### F078 editor-runtime.iml

**Path:** `editor/editor-runtime/editor-runtime.iml`

[Browser file](C:/Codex/mps-j2cl/mps-web/editor/editor-runtime/editor-runtime.iml) · [Compared upstream file](C:/Codex/mps-j2cl/mps/editor/editor-runtime/editor-runtime.iml)

**Classification:** IntelliJ project metadata. Text delta: +48 / −30 lines; 42 → 60 total lines.

**What changes:** Customized IntelliJ descriptor with a same-path upstream counterpart. Indexes the overlay source root(s) `source` with inherited compiler output. Declares 48 module dependencies and 1 project libraries. The complete XML or descriptor diff below records every dependency name. The upstream source/test root(s) `source_gen`, `tests` are absent here. Added module references: `browser`, `buildA`, `components`, `constraints`, `core`, `editor-runtime2`, `instrumentation`, `intellij-stubs`, `jetbrains.mps.ide`, `jetbrains.mps.testbench`, `kernel2`, `kernelSolution`, `kernelSolution2`, `logging2`, `module`, `mps-annotations`, `mps-editor`, `mps-messaging`, `mps-platform2`, `openapi`, `openapi2`, `persistence`, `project`, `project3`, `smodel`, `smodel2`, `smodelRuntime`, `startup`, `tests`, `typechecking2`, `util2`, `util4`, `vfs`, `vfs2`, `webawt`, `webswing`, `xmlPersistence`. Removed module references: `actions-runtime`, `behavior-runtime`, `closures-runtime`, `collections-runtime`, `constraints-runtime`, `editing-runtime`, `messages-for-legacy-constraints`, `mps-icons`, `mps-ui`, `tuples-runtime`, `typesystemEngine`.

**Behavior and limits:** The root .idea/modules.xml references this file, so it affects IDE indexing. It is not an MPS_OVERRIDES entry and is not used to select Bazel Java sources. Local module names such as kernel2 or webswing are IDE identifiers, not proof of Bazel dependency ownership.

**To make the replacement unnecessary:** Generate the IDE project from the authoritative selected-source/build graph, or keep equivalent descriptors outside the overlay. Update the root IDE module references before relocating metadata. Removing this file alone would break that IDE reference and would not reduce the 100 Java replacements.

Complete descriptor diff

```diff
--- mps/editor/editor-runtime/editor-runtime.iml
+++ mps-web/editor/editor-runtime/editor-runtime.iml
@@ -1,42 +1,60 @@
 <?xml version="1.0" encoding="UTF-8"?>
 <module type="JAVA_MODULE" version="4">
-  <component name="NewModuleRootManager">
-    <output url="file://$MODULE_DIR$/classes" />
-    <output-test url="file://$MODULE_DIR$/testclasses" />
+  <component name="NewModuleRootManager" inherit-compiler-output="true">
     <exclude-output />
     <content url="file://$MODULE_DIR$">
       <sourceFolder url="file://$MODULE_DIR$/source" isTestSource="false" />
-      <sourceFolder url="file://$MODULE_DIR$/source_gen" isTestSource="false" />
-      <sourceFolder url="file://$MODULE_DIR$/tests" isTestSource="true" />
     </content>
     <orderEntry type="inheritedJdk" />
     <orderEntry type="sourceFolder" forTests="false" />
+    <orderEntry type="library" name="lib" level="project" />
+    <orderEntry type="module" module-name="components" />
+    <orderEntry type="module" module-name="browser" />
+    <orderEntry type="module" module-name="mps-annotations" />
+    <orderEntry type="module" module-name="util4" />
+    <orderEntry type="module" module-name="tests" />
+    <orderEntry type="module" module-name="jetbrains.mps.testbench" />
+    <orderEntry type="module" module-name="webswing" />
+    <orderEntry type="module" module-name="mps-platform" />
+    <orderEntry type="module" module-name="constraints" />
+    <orderEntry type="module" module-name="mps-workbench" />
+    <orderEntry type="module" module-name="buildA" />
+    <orderEntry type="module" module-name="smodel" />
+    <orderEntry type="module" module-name="util2" />
+    <orderEntry type="module" module-name="editorlang-runtime" />
+    <orderEntry type="module" module-name="typechecking2" />
+    <orderEntry type="module" module-name="kernel2" />
+    <orderEntry type="module" module-name="instrumentation" />
+    <orderEntry type="module" module-name="make-runtime" />
+    <orderEntry type="module" module-name="mps-messaging" />
+    <orderEntry type="module" module-name="xmlPersistence" />
+    <orderEntry type="module" module-name="jetbrains.mps.ide" />
+    <orderEntry type="module" module-name="findUsages-runtime" />
+    <orderEntry type="module" module-name="kernelSolution" />
+    <orderEntry type="module" module-name="mps-platform2" />
+    <orderEntry type="module" module-name="webawt" />
+    <orderEntry type="module" module-name="smodelRuntime" />
+    <orderEntry type="module" module-name="editor-runtime2" />
+    <orderEntry type="module" module-name="logging2" />
+    <orderEntry type="module" module-name="module" />
+    <orderEntry type="module" module-name="smodel2" />
+    <orderEntry type="module" module-name="mps-editor" />
     <orderEntry type="module" module-name="kernel" />
-    <orderEntry type="module" module-name="editor-api" exported="" />
-    <orderEntry type="module" module-name="editorlang-runtime" exported="" />
-    <orderEntry type="module" module-name="actions-runtime" />
-    <orderEntry type="module" module-name="typesystemEngine" exported="" />
-    <orderEntry type="module" module-name="collections-runtime" />
-    <orderEntry type="module" module-name="tuples-runtime" />
-    <orderEntry type="module" module-name="findUsages-runtime" />
+    <orderEntry type="module" module-name="openapi2" />
+    <orderEntry type="module" module-name="behavior-api" />
+    <orderEntry type="module" module-name="core" />
+    <orderEntry type="module" module-name="typechecking" />
+    <orderEntry type="module" module-name="intellij-stubs" />
+    <orderEntry type="module" module-name="kernelSolution2" />
+    <orderEntry type="module" module-name="startup" />
+    <orderEntry type="module" module-name="testbench" />
+    <orderEntry type="module" module-name="project" />
     <orderEntry type="module" module-name="intentions-runtime" />
-    <orderEntry type="module" module-name="make-runtime" />
-    <orderEntry type="module" module-name="mps-platform" />
-    <orderEntry type="module" module-name="mps-ui" />
-    <orderEntry type="library" name="Annotations" level="project" />
-    <orderEntry type="library" name="Idea-platform" level="project" />
-    <orderEntry type="module" module-name="mps-icons" />
-    <orderEntry type="module" module-name="closures-runtime" />
-    <orderEntry type="module" module-name="behavior-api" />
-    <orderEntry type="module" module-name="behavior-runtime" />
-    <orderEntry type="module" module-name="testbench" scope="TEST" />
-    <orderEntry type="library" scope="TEST" name="JUnit4" level="project" />
-    <orderEntry type="library" scope="TEST" name="JMock" level="project" />
-    <orderEntry type="module" module-name="editing-runtime" exported="" />
-    <orderEntry type="module" module-name="typechecking" />
-    <orderEntry type="module" module-name="constraints-runtime" />
-    <orderEntry type="module" module-name="messages-for-legacy-constraints" />
-    <orderEntry type="library" name="gnu.trove" level="project" />
-    <orderEntry type="module" module-name="mps-workbench" />
+    <orderEntry type="module" module-name="openapi" />
+    <orderEntry type="module" module-name="vfs" />
+    <orderEntry type="module" module-name="persistence" />
+    <orderEntry type="module" module-name="editor-api" />
+    <orderEntry type="module" module-name="project3" />
+    <orderEntry type="module" module-name="vfs2" />
   </component>
 </module>
```

### F079 CompletionHelper.java

**Path:** `editor/editor-runtime/source/jetbrains/mps/nodeEditor/cellMenu/CompletionHelper.java`

[Browser file](C:/Codex/mps-j2cl/mps-web/editor/editor-runtime/source/jetbrains/mps/nodeEditor/cellMenu/CompletionHelper.java) · [Compared upstream file](C:/Codex/mps-j2cl/mps/editor/editor-runtime/source/jetbrains/mps/nodeEditor/cellMenu/CompletionHelper.java)

**Classification:** Java APIs and text processing. Text delta: +3 / −3 lines; 221 → 221 total lines.

**What changes:** Replaces generic parseProperty(name, default) with a dedicated parsePopupDelay using a literal property key.

**Behavior and limits:** Parsing and the 500 ms fallback remain; this is a J2CL property-intrinsic constraint rather than a timer change.

**To make the replacement unnecessary:** Support dynamic property lookup or an injectable configuration service in the shared compiler/runtime, or adopt literal lookup upstream. Check malformed and absent values.

Complete textual diff

```diff
--- mps/editor/editor-runtime/source/jetbrains/mps/nodeEditor/cellMenu/CompletionHelper.java
+++ mps-web/editor/editor-runtime/source/jetbrains/mps/nodeEditor/cellMenu/CompletionHelper.java
@@ -31,7 +31,7 @@
 
   // can be shared among all helpers
   private static final ScheduledExecutorService scheduler = ConcurrencyUtil.newSingleScheduledThreadExecutor("CompletionHelper");
-  private static final int DEFAULT_POPUP_DELAY_MILLIS = parseProperty("mps.completion.delay.millis", 500);
+  private static final int DEFAULT_POPUP_DELAY_MILLIS = parsePopupDelay(500);
 
   private final EditorComponent myEditorComponent;
   private CompletionCalculationTask currentCompletionCalculationTask;
@@ -211,9 +211,9 @@
     return CodeInsightSettings.getInstance().AUTO_POPUP_COMPLETION_LOOKUP && !RuntimeFlags.isTestMode();
   }
 
-  private static int parseProperty(@NotNull String name, int defValue) {
+  private static int parsePopupDelay(int defValue) {
     try {
-      return Integer.parseInt(System.getProperty(name));
+      return Integer.parseInt(System.getProperty("mps.completion.delay.millis"));
     } catch (Exception ignored) {
     }
     return defValue;
```

### F080 NodeSubstitutePatternEditor.java

**Path:** `editor/editor-runtime/source/jetbrains/mps/nodeEditor/cellMenu/NodeSubstitutePatternEditor.java`

[Browser file](C:/Codex/mps-j2cl/mps-web/editor/editor-runtime/source/jetbrains/mps/nodeEditor/cellMenu/NodeSubstitutePatternEditor.java) · [Compared upstream file](C:/Codex/mps-j2cl/mps/editor/editor-runtime/source/jetbrains/mps/nodeEditor/cellMenu/NodeSubstitutePatternEditor.java)

**Classification:** Editor rendering input and dialogs. Text delta: +26 / −0 lines; 615 → 641 total lines.

**What changes:** Adds BrowserPaintSurface to the completion-pattern panel, with addNotify/removeNotify, resize and getGraphics bridges.

**Behavior and limits:** The original panel painting and editing algorithms stay; the surface is mounted independently of ordinary child controls.

**To make the replacement unnecessary:** Make custom Swing painting work through generic component peers in the sibling Swing/AWT libraries so this panel needs no browser-specific lifecycle methods.

Complete textual diff

```diff
--- mps/editor/editor-runtime/source/jetbrains/mps/nodeEditor/cellMenu/NodeSubstitutePatternEditor.java
+++ mps-web/editor/editor-runtime/source/jetbrains/mps/nodeEditor/cellMenu/NodeSubstitutePatternEditor.java
@@ -595,6 +595,32 @@
     }
 
     private class EditorPanel extends JPanel {
+      private final javax.swing.BrowserPaintSurface myBrowserPaintSurface =
+          new javax.swing.BrowserPaintSurface(this, this::paint);
+
+      @Override
+      public void addNotify() {
+        super.addNotify();
+        myBrowserPaintSurface.mount();
+      }
+
+      @Override
+      public void removeNotify() {
+        myBrowserPaintSurface.unmount();
+        super.removeNotify();
+      }
+
+      @Override
+      public void setBounds(int x, int y, int width, int height) {
+        super.setBounds(x, y, width, height);
+        if (myBrowserPaintSurface != null) myBrowserPaintSurface.resize();
+      }
+
+      @Override
+      public Graphics getGraphics() {
+        return myBrowserPaintSurface == null ? null : myBrowserPaintSurface.getGraphics();
+      }
+
       @Override
       protected void paintComponent(Graphics g) {
         // COLORS: move colors to properties
```

### F081 ContextAssistantSettings.java

**Path:** `editor/editor-runtime/source/jetbrains/mps/nodeEditor/cells/contextAssistant/ContextAssistantSettings.java`

[Browser file](C:/Codex/mps-j2cl/mps-web/editor/editor-runtime/source/jetbrains/mps/nodeEditor/cells/contextAssistant/ContextAssistantSettings.java) · [Compared upstream file](C:/Codex/mps-j2cl/mps/editor/editor-runtime/source/jetbrains/mps/nodeEditor/cells/contextAssistant/ContextAssistantSettings.java)

**Classification:** Compiled deployment and reflection. Text delta: +1 / −2 lines; 49 → 48 total lines.

**What changes:** Replaces XmlSerializerUtil.copyBean(state, this) with HELP_SHOWN = state.HELP_SHOWN.

**Behavior and limits:** Only the existing boolean is copied. Future state fields would need corresponding code; generic reflective bean copying is not implemented.

**To make the replacement unnecessary:** Upstream the explicit copy for this small state object, generate state copiers, or implement the required bean-copy metadata centrally. Avoid a general reflection subsystem for one field.

Complete textual diff

```diff
--- mps/editor/editor-runtime/source/jetbrains/mps/nodeEditor/cells/contextAssistant/ContextAssistantSettings.java
+++ mps-web/editor/editor-runtime/source/jetbrains/mps/nodeEditor/cells/contextAssistant/ContextAssistantSettings.java
@@ -19,7 +19,6 @@
 import com.intellij.openapi.components.PersistentStateComponent;
 import com.intellij.openapi.components.State;
 import com.intellij.openapi.components.Storage;
-import com.intellij.util.xmlb.XmlSerializerUtil;
 import org.jetbrains.annotations.NotNull;
 import org.jetbrains.annotations.Nullable;
 
@@ -40,7 +39,7 @@
 
   @Override
   public void loadState(@NotNull ContextAssistantSettings state) {
-    XmlSerializerUtil.copyBean(state, this);
+    HELP_SHOWN = state.HELP_SHOWN;
   }
 
   public static ContextAssistantSettings getInstance() {
```

### F082 MPSDocumentationEditorPane.java

**Path:** `editor/editor-runtime/source/jetbrains/mps/nodeEditor/documentation/MPSDocumentationEditorPane.java`

[Browser file](C:/Codex/mps-j2cl/mps-web/editor/editor-runtime/source/jetbrains/mps/nodeEditor/documentation/MPSDocumentationEditorPane.java) · [Compared upstream file](C:/Codex/mps-j2cl/mps/editor/editor-runtime/source/jetbrains/mps/nodeEditor/documentation/MPSDocumentationEditorPane.java)

**Classification:** Editor rendering input and dialogs. Text delta: +38 / −142 lines; 155 → 51 total lines.

**What changes:** Replaces the IntelliJ HTML editor kit, theme/CSS/section measurement and mouse handling with a small JEditorPane using normalized plain text, font metrics, popup cleanup and font-size changes.

**Behavior and limits:** Documentation remains readable but does not preserve full HTML layout, styling or automatically interactive links. Preferred sizing is an approximation based on text metrics.

**To make the replacement unnecessary:** Provide the required HTML/editor-kit contracts in the shared UI library, or upstream a DocumentationView abstraction with a browser renderer. Restoring rich HTML is a feature change requiring visual and interaction checks.

Representative excerpts from the compared files; the prose above covers additional changes.

Pinned upstream at line 50

```java

  public MPSDocumentationEditorPane() {

    setEditable(false);
    setBackground(BACKGROUND_COLOR);
    HTMLEditorKit editorKit = new HTMLEditorKitBuilder().withFontResolver(EditorCssFontResolver.getGlobalInstance()).build();
    MPSDocumentationHtmlUtil.addDocumentationPaneDefaultCssRules(editorKit);

    setEditorKit(editorKit);
    setBorder(JBUI.Borders.empty());
  }

```

Browser replacement at line 17

```java
  private JBPopup hint;
  public MPSDocumentationEditorPane() {
    super("text/html", "");
    setEditable(false);
    setFont(UIManager.getFont("Label.font"));
    setBackground(UIManager.getColor("Panel.background"));
    setBorder(BorderFactory.createEmptyBorder(5, 8, 5, 8));
  }
  int getPreferredWidth() {
    int length = getDocument().getLength();
    return 300 + Math.min(800, Math.max(0, length - 200)) / 4;
  }
```

### F083 MPSDocumentationHtmlUtil.java

**Path:** `editor/editor-runtime/source/jetbrains/mps/nodeEditor/documentation/MPSDocumentationHtmlUtil.java`

[Browser file](C:/Codex/mps-j2cl/mps-web/editor/editor-runtime/source/jetbrains/mps/nodeEditor/documentation/MPSDocumentationHtmlUtil.java) · [Compared upstream file](C:/Codex/mps-j2cl/mps/editor/editor-runtime/source/jetbrains/mps/nodeEditor/documentation/MPSDocumentationHtmlUtil.java)

**Classification:** Editor rendering input and dialogs. Text delta: +6 / −83 lines; 89 → 12 total lines.

**What changes:** Removes desktop CSS construction and replaces addDocumentationPaneDefaultCssRules with an explicit unsupported-operation exception.

**Behavior and limits:** The paired browser documentation pane avoids this method. This is a restricted rendering profile, not equivalent CSS support.

**To make the replacement unnecessary:** Add compatible rich-document styling in a UI provider, or split the desktop CSS helper out of the common editor source set.

Representative excerpts from the compared files; the prose above covers additional changes.

Pinned upstream at line 30

```java

  static void addDocumentationPaneDefaultCssRules(@NotNull HTMLEditorKit editorKit) {
    StyleSheet styleSheet = editorKit.getStyleSheet();
    for (String rule : getDocumentationPaneDefaultCssRules()) {
      styleSheet.addRule(rule);
    }
  }

  private static @NotNull List<String> getDocumentationPaneDefaultCssRules() {
    int leftPadding = 8;
    int definitionTopPadding = 4;
    String linkColor = ColorUtil.toHtmlColor(JBUI.CurrentTheme.Link.Foreground.ENABLED);
```

Browser replacement at line 1

```java
/* Copyright 2000-2023 JetBrains s.r.o. Licensed under the Apache License, Version 2.0. */
package jetbrains.mps.nodeEditor.documentation;

import javax.swing.text.html.HTMLEditorKit;

/** The selected browser documentation pane uses normalized text, without Swing HTML CSS. */
class MPSDocumentationHtmlUtil {
  private MPSDocumentationHtmlUtil() {}
  static void addDocumentationPaneDefaultCssRules(HTMLEditorKit editorKit) {
    throw new UnsupportedOperationException("Swing HTML stylesheet rendering is outside the browser documentation profile");
  }
}
```

### F084 MPSDocumentationToolWindowManager.java

**Path:** `editor/editor-runtime/source/jetbrains/mps/nodeEditor/documentation/MPSDocumentationToolWindowManager.java`

[Browser file](C:/Codex/mps-j2cl/mps-web/editor/editor-runtime/source/jetbrains/mps/nodeEditor/documentation/MPSDocumentationToolWindowManager.java) · [Compared upstream file](C:/Codex/mps-j2cl/mps/editor/editor-runtime/source/jetbrains/mps/nodeEditor/documentation/MPSDocumentationToolWindowManager.java)

**Classification:** Editor rendering input and dialogs. Text delta: +1 / −1 lines; 113 → 113 total lines.

**What changes:** Changes the documentation tool-window manager's Project constructor from private to public.

**Behavior and limits:** This enables explicit service construction instead of reflective service instantiation; other behavior is unchanged.

**To make the replacement unnecessary:** Upstream a public factory/constructor or generated service factory. No additional rendering implementation is needed to remove this particular overlay.

Complete textual diff

```diff
--- mps/editor/editor-runtime/source/jetbrains/mps/nodeEditor/documentation/MPSDocumentationToolWindowManager.java
+++ mps-web/editor/editor-runtime/source/jetbrains/mps/nodeEditor/documentation/MPSDocumentationToolWindowManager.java
@@ -28,7 +28,7 @@
     return project.getService(MPSDocumentationToolWindowManager.class);
   }
 
-  private MPSDocumentationToolWindowManager(Project project) {
+  public MPSDocumentationToolWindowManager(Project project) {
     myProject = project;
   }
 
```

### F085 EditorComponent.java

**Path:** `editor/editor-runtime/source/jetbrains/mps/nodeEditor/EditorComponent.java`

[Browser file](C:/Codex/mps-j2cl/mps-web/editor/editor-runtime/source/jetbrains/mps/nodeEditor/EditorComponent.java) · [Compared upstream file](C:/Codex/mps-j2cl/mps/editor/editor-runtime/source/jetbrains/mps/nodeEditor/EditorComponent.java)

**Classification:** Editor rendering input and dialogs. Text delta: +160 / −2 lines; 3271 → 3429 total lines.

**What changes:** Adds a canvas paint surface and native-input/IME support, translates composition into MPS InputMethodEvents, mounts/resizes/disposes the helpers, and routes unhandled browser shortcuts to real CellActionType selection actions. Also replaces two assert statements with guarded explicit AssertionErrors.

**Behavior and limits:** Original cell/layout/model algorithms remain. Generated keymaps and completion retain priority; read-only guards and read-lock release precede commands. The explicit assertions now run even where Java assertions would be disabled, and IME/shortcut logic is a real semantic integration layer.

**To make the replacement unnecessary:** Move generic paint/IME bridges into Swing/AWT peers, expose an upstream action-dispatch hook and deploy shortcut metadata. Fix or verify assertion lowering centrally, then assess restoring assertions separately. One library migration does not remove all changes in this file.

Representative excerpts from the compared files; the prose above covers additional changes.

Pinned upstream at line 1706

```java
    // if by any chance you need model access here, use myRepository
    assert myModelDisposedStackTrace == null : getModelDisposedMessage();
    if (myNode == null) {
      return;
    }
    SModel model = myNode.getModel();
    if (model == null) {
      return;
    }
    assert model.getRepository() != null : getNodeDisposedMessage(model);
  }

```

Browser replacement at line 1764

```java
    // evaluates message arguments eagerly. A live model has no disposal trace.
    if (myModelDisposedStackTrace != null) {
      throw new AssertionError(getModelDisposedMessage());
    }
    if (myNode == null) {
      return;
    }
    SModel model = myNode.getModel();
    if (model == null) {
      return;
    }
    if (model.getRepository() == null) {
```

Additional browser code at line 223

```java
  // editor/cell painting pipeline.
  private final javax.swing.BrowserPaintSurface myBrowserPaintSurface =
      new javax.swing.BrowserPaintSurface(this, this::paint);
  private final javax.swing.BrowserInputMethodSupport myBrowserInputMethodSupport =
      new javax.swing.BrowserInputMethodSupport(this);
  private boolean myBrowserCompositionHasText;

  @Override
  public void addNotify() {
    super.addNotify();
    if (!isDisposed() && hasUI()) {
      myBrowserPaintSurface.mount();
```

Additional browser code at line 2683

```java
    // Generated language keymaps and active completion handlers retain priority.
    if (!keyEvent.isConsumed() && !myNodeSubstituteChooser.isVisible()) {
      CellActionType action = browserKeyAction(keyEvent);
      if (action != null) {
        Selection selection = getModelAccess().computeReadAction(() -> {
          Selection current = getSelectionManager().getSelection();
          return current != null && (!browserActionWrites(action) || !ReadOnlyUtil.isSelectionReadOnlyInEditor(this))
              && current.canExecuteAction(action) ? current : null;
        });
        // Release read access before an action starts an undoable command.
        if (selection != null) selection.executeAction(action);
        else if (action == CellActionType.FIND_NEXT) goToNextHighlightedCell(false);
```

### F086 EditorComponentKeyboardHandler.java

**Path:** `editor/editor-runtime/source/jetbrains/mps/nodeEditor/EditorComponentKeyboardHandler.java`

[Browser file](C:/Codex/mps-j2cl/mps-web/editor/editor-runtime/source/jetbrains/mps/nodeEditor/EditorComponentKeyboardHandler.java) · [Compared upstream file](C:/Codex/mps-j2cl/mps/editor/editor-runtime/source/jetbrains/mps/nodeEditor/EditorComponentKeyboardHandler.java)

**Classification:** Editor rendering input and dialogs. Text delta: +1 / −4 lines; 150 → 147 total lines.

**What changes:** Removes the special handler that consumed Ctrl+Up to prevent desktop focus leaving the editor.

**Behavior and limits:** Ctrl+Up can reach structural selection through the browser shortcut path; this deliberately changes input handling.

**To make the replacement unnecessary:** Make the focus workaround platform-dependent upstream or align shared focus/shortcut handling. Verify Ctrl+Up structural selection and focus retention together.

Complete textual diff

```diff
--- mps/editor/editor-runtime/source/jetbrains/mps/nodeEditor/EditorComponentKeyboardHandler.java
+++ mps-web/editor/editor-runtime/source/jetbrains/mps/nodeEditor/EditorComponentKeyboardHandler.java
@@ -49,10 +49,7 @@
       return true;
     }
 
-    // special case - don't allow kbd focus to leave editor area
-    if (keyEvent.getKeyCode() == KeyEvent.VK_UP && keyEvent.isControlDown() && !keyEvent.isAltDown() && !keyEvent.isShiftDown()) {
-      keyEvent.consume();
-    }
+    // The browser fallback receives Ctrl+Up for structural selection.
 
     return false;
   }
```

### F087 HighlighterUpdateSession.java

**Path:** `editor/editor-runtime/source/jetbrains/mps/nodeEditor/highlighter/HighlighterUpdateSession.java`

[Browser file](C:/Codex/mps-j2cl/mps-web/editor/editor-runtime/source/jetbrains/mps/nodeEditor/highlighter/HighlighterUpdateSession.java) · [Compared upstream file](C:/Codex/mps-j2cl/mps/editor/editor-runtime/source/jetbrains/mps/nodeEditor/highlighter/HighlighterUpdateSession.java)

**Classification:** Scheduling model access and invalidation. Text delta: +102 / −39 lines; 337 → 400 total lines.

**What changes:** Removes sleeping/retry loops and the make-service dependency, adds collection/disposal guards and an updateAsync continuation that processes one editor per queued task and always completes cleanup.

**Behavior and limits:** Real checker algorithms remain synchronous within each editor; yielding occurs between editors after read/typechecking scopes close. Cancellation leaves work unchecked for retry.

**To make the replacement unnecessary:** Upstream a cancellable asynchronous highlighter session API with an injected queue/busy gate. Timer support alone cannot transform a blocking loop into continuations.

Representative excerpts from the compared files; the prose above covers additional changes.

Pinned upstream at line 209

```java
          try {
            Thread.sleep(600);
          } catch (InterruptedException ignored) {
          }
        }
        // Important: for CancellableReadAction to work (i.e. to be truly 'cancellable', we have to start it from a non-EDT thread). As commands and most of
        // write actions are executed in EDT, a highlighter read started from EDT has no chance to receive cancel() request.
        final HighlighterReadAction ra = new HighlighterReadAction(checker, editor, wasCheckedOnce, wasLastChecked, applyQuickFixes);
        // it's not clear whether to use SRepository associated with the editor or the project one. Left project as it was the one in runLoPrioRead()
        projectModelAccess.runReadAction(ra);
        checkResult = ra.getUpdateResult();
      } while (checkResult == null);
```

Browser replacement at line 265

```java
   */
  public void updateAsync(Executor executor, Consumer<Throwable> completed) {
    java.util.Objects.requireNonNull(executor);
    java.util.Objects.requireNonNull(completed);
    final List<Pair<EditorComponent, Boolean>> input = collectInput();
    class Work implements Runnable {
      private int next;
      private boolean messagesChanged;
      private boolean finished;

      private void finish(Throwable failure) {
        if (finished) return;
```

### F088 Highlighter.java

**Path:** `editor/editor-runtime/source/jetbrains/mps/nodeEditor/Highlighter.java`

[Browser file](C:/Codex/mps-j2cl/mps-web/editor/editor-runtime/source/jetbrains/mps/nodeEditor/Highlighter.java) · [Compared upstream file](C:/Codex/mps-j2cl/mps/editor/editor-runtime/source/jetbrains/mps/nodeEditor/Highlighter.java)

**Classification:** Scheduling model access and invalidation. Text delta: +65 / −28 lines; 631 → 668 total lines.

**What changes:** Uses scheduled zero-delay tasks instead of potentially inline submit, introduces a busy predicate, nested pause depth, cancellation/disposal state and async-session completion handling. Removes make-service coupling and blocking executor termination.

**Behavior and limits:** The browser highlighter yields, avoids stale queued work after disposal and retains checker/message logic. It does not become parallel typechecking.

**To make the replacement unnecessary:** Separate scheduling and activity policy from MPS highlighter logic upstream, with JVM and browser schedulers. Keep nested pause, restart, cancellation and queued-disposal tests when reducing the overlay.

Representative excerpts from the compared files; the prose above covers additional changes.

Pinned upstream at line 509

```java
        }
        createUpdateSession(activeEditors, inspector, !updateAllCheckers).update();
        // XXX in fact, would be better to check if update() had succeeded and only then extend grace period.
        myCommandWatcher.extendGracePeriod();
      } catch (IndexNotReadyException ex) {
        myCommandWatcher.resetGracePeriod();
        myEditorTracker.markEverythingUnchecked();
      } finally {
        if(!initialEntireFileHighlightingReported) {
          initialEntireFileHighlightingReported = true;
          ApplicationManager.getApplication().getService(StartUpPerformanceService.class).editorRestoringTillHighlighted();
        }
```

Browser replacement at line 530

```java
        }
        createUpdateSession(activeEditors, inspector, !updateAllCheckers).updateAsync(
            action -> myBackgroundExecutor.schedule(action, 0, TimeUnit.MILLISECONDS), this::finished);
      } catch (Throwable failure) {
        finished(failure);
      }
    }

    private void finished(Throwable failure) {
      if (myCancelled || isStopping()) return;
      try {
        if (failure instanceof IndexNotReadyException) {
```

### F089 HintPopupController.java

**Path:** `editor/editor-runtime/source/jetbrains/mps/nodeEditor/HintPopupController.java`

[Browser file](C:/Codex/mps-j2cl/mps-web/editor/editor-runtime/source/jetbrains/mps/nodeEditor/HintPopupController.java) · [Compared upstream file](C:/Codex/mps-j2cl/mps/editor/editor-runtime/source/jetbrains/mps/nodeEditor/HintPopupController.java)

**Classification:** Scheduling model access and invalidation. Text delta: +7 / −3 lines; 147 → 151 total lines.

**What changes:** Cancels queued move/hover requests on listener removal, mouse actions and every key press; documentation cancellation is no longer limited to action keys.

**Behavior and limits:** Prevents a delayed stale diagnostic popup from appearing after typing. This is a lifecycle/input correctness fix.

**To make the replacement unnecessary:** Upstream the cancellation behavior in the generic popup controller. A shared Alarm implementation is necessary but does not by itself know when MPS diagnostics become stale.

Complete textual diff

```diff
--- mps/editor/editor-runtime/source/jetbrains/mps/nodeEditor/HintPopupController.java
+++ mps-web/editor/editor-runtime/source/jetbrains/mps/nodeEditor/HintPopupController.java
@@ -52,6 +52,7 @@
   }
 
   public void uninstallListeners(Component owner) {
+    cancelAllRequests();
     owner.removeMouseListener(myMouseListener);
     owner.removeMouseMotionListener(myMouseMotionAdapter);
     if (owner instanceof EditorComponent) {
@@ -93,6 +94,7 @@
 
     @Override
     public void mouseClicked(MouseEvent e) {
+      cancelAllRequests();
       MPSDocumentationManager.getInstance().cancelAll();
       }
   }
@@ -100,14 +102,16 @@
   private class MyKeyListener extends KeyAdapter {
     @Override
     public void keyTyped(KeyEvent e) {
+      cancelAllRequests();
       MPSDocumentationManager.getInstance().cancelAll();
     }
 
     @Override
     public void keyPressed(KeyEvent e) {
-      if (e.isActionKey()) {
-        MPSDocumentationManager.getInstance().cancelAll();
-      }
+      // Cancel the pending hover too: keyboard editing can invalidate its diagnostic
+      // before the browser's delayed callback creates the tooltip component.
+      cancelAllRequests();
+      MPSDocumentationManager.getInstance().cancelAll();
     }
 
     @Override
```

### F090 IntentionsSupport.java

**Path:** `editor/editor-runtime/source/jetbrains/mps/nodeEditor/IntentionsSupport.java`

[Browser file](C:/Codex/mps-j2cl/mps-web/editor/editor-runtime/source/jetbrains/mps/nodeEditor/IntentionsSupport.java) · [Compared upstream file](C:/Codex/mps-j2cl/mps/editor/editor-runtime/source/jetbrains/mps/nodeEditor/IntentionsSupport.java)

**Classification:** Scheduling model access and invalidation. Text delta: +64 / −75 lines; 362 → 351 total lines.

**What changes:** Replaces the sleeping IntentionsThread with a cancellable Alarm request. Guards missing desktop action registration, focus/disposal, request identity and selected/edited-node identity; scopes real intention lookup within a model read/typechecking session.

**Behavior and limits:** The one-second delay and intention computation remain. Cancellation no longer means stopping a JVM thread, and the Alt-Enter fallback remains usable when the desktop action is absent.

**To make the replacement unnecessary:** Upstream scheduler injection and request lifetime checks, and provide shortcut registration through the host. Do not reintroduce sleep or hold model access across a browser delay.

Representative excerpts from the compared files; the prose above covers additional changes.

Pinned upstream at line 293

```java

  private class IntentionsThread extends Thread {
    private volatile boolean myStopRequested;

    public IntentionsThread() {
      super("Intentions");
    }

    /*package*/ void requestStop() {
      myStopRequested = true;
    }

```

Browser replacement at line 300

```java

  private final class IntentionsRequest implements Runnable {
    private final SNode mySelectedNode;
    private final SNode myEditedNode;
    private boolean myStopRequested;

    IntentionsRequest(SNode selectedNode, SNode editedNode) {
      mySelectedNode = selectedNode;
      myEditedNode = editedNode;
    }

    void requestStop() {
```

### F091 AWTKeymapHandler.java

**Path:** `editor/editor-runtime/source/jetbrains/mps/nodeEditor/keymaps/AWTKeymapHandler.java`

[Browser file](C:/Codex/mps-j2cl/mps-web/editor/editor-runtime/source/jetbrains/mps/nodeEditor/keymaps/AWTKeymapHandler.java) · [Compared upstream file](C:/Codex/mps-j2cl/mps/editor/editor-runtime/source/jetbrains/mps/nodeEditor/keymaps/AWTKeymapHandler.java)

**Classification:** Editor rendering input and dialogs. Text delta: +202 / −23 lines; 246 → 425 total lines.

**What changes:** Replaces KeyEvent field reflection with an explicit table of VK constants and changes numeric-code lookup from one name to a list of names, retaining aliases.

**Behavior and limits:** Shift/Control/Alt are excluded as action names; both VK_SEPARATOR and VK_SEPARATER survive. This intentionally improves alias preservation over the single-value map.

**To make the replacement unnecessary:** Expose a generated key-name table from the shared AWT layer or upstream a table/API independent of field reflection. Verify all constants and alias behavior before removing the local table.

Representative excerpts from the compared files; the prose above covers additional changes.

Pinned upstream at line 55

```java
  static {
    for (Field field : KeyEvent.class.getDeclaredFields()) {
      String name = field.getName();
      if (name.startsWith("VK_")) {
        if ("VK_CONTROL".equals(name) ||
            "VK_ALT".equals(name) ||
            "VK_SHIFT".equals(name)) {
          continue;
        }
        try {
          int value = field.getInt(null);
          ourJavaKeyCodesMap.put(value, name);
```

Browser replacement at line 55

```java
    // Explicit sibling AWT constants; retain aliases sharing the same numeric code.
    registerKey(KeyEvent.VK_ENTER, "VK_ENTER");
    registerKey(KeyEvent.VK_BACK_SPACE, "VK_BACK_SPACE");
    registerKey(KeyEvent.VK_TAB, "VK_TAB");
    registerKey(KeyEvent.VK_CANCEL, "VK_CANCEL");
    registerKey(KeyEvent.VK_CLEAR, "VK_CLEAR");
    registerKey(KeyEvent.VK_PAUSE, "VK_PAUSE");
    registerKey(KeyEvent.VK_CAPS_LOCK, "VK_CAPS_LOCK");
    registerKey(KeyEvent.VK_ESCAPE, "VK_ESCAPE");
    registerKey(KeyEvent.VK_SPACE, "VK_SPACE");
    registerKey(KeyEvent.VK_PAGE_UP, "VK_PAGE_UP");
    registerKey(KeyEvent.VK_PAGE_DOWN, "VK_PAGE_DOWN");
```

### F092 LeftEditorHighlighter.java

**Path:** `editor/editor-runtime/source/jetbrains/mps/nodeEditor/leftHighlighter/LeftEditorHighlighter.java`

[Browser file](C:/Codex/mps-j2cl/mps-web/editor/editor-runtime/source/jetbrains/mps/nodeEditor/leftHighlighter/LeftEditorHighlighter.java) · [Compared upstream file](C:/Codex/mps-j2cl/mps/editor/editor-runtime/source/jetbrains/mps/nodeEditor/leftHighlighter/LeftEditorHighlighter.java)

**Classification:** Editor rendering input and dialogs. Text delta: +27 / −0 lines; 864 → 891 total lines.

**What changes:** Adds a BrowserPaintSurface for the editor's left gutter, including mount/unmount, resize, getGraphics and disposal.

**Behavior and limits:** The original gutter paint and listener algorithms remain.

**To make the replacement unnecessary:** Provide generic custom-component painting and disposal through sibling Swing/AWT peers; verify gutter pixels and mouse geometry in the real editor.

Complete textual diff

```diff
--- mps/editor/editor-runtime/source/jetbrains/mps/nodeEditor/leftHighlighter/LeftEditorHighlighter.java
+++ mps-web/editor/editor-runtime/source/jetbrains/mps/nodeEditor/leftHighlighter/LeftEditorHighlighter.java
@@ -91,6 +91,32 @@
  * This class should be called in UI (EventDispatch) thread only
  */
 public final class LeftEditorHighlighter extends JComponent implements UiDataProvider {
+  private final javax.swing.BrowserPaintSurface myBrowserPaintSurface =
+      new javax.swing.BrowserPaintSurface(this, this::paint);
+
+  @Override
+  public void addNotify() {
+    super.addNotify();
+    myBrowserPaintSurface.mount();
+  }
+
+  @Override
+  public void removeNotify() {
+    myBrowserPaintSurface.unmount();
+    super.removeNotify();
+  }
+
+  @Override
+  public void setBounds(int x, int y, int width, int height) {
+    super.setBounds(x, y, width, height);
+    if (myBrowserPaintSurface != null) myBrowserPaintSurface.resize();
+  }
+
+  @Override
+  public Graphics getGraphics() {
+    return myBrowserPaintSurface == null ? null : myBrowserPaintSurface.getGraphics();
+  }
+
   public static final String ICON_AREA = "LeftEditorHighlighterIconArea";
 
   private static final int MIN_LEFT_TEXT_WIDTH = 0;
@@ -263,6 +289,7 @@
   }
 
   public void dispose() {
+    myBrowserPaintSurface.dispose();
     if (myMessageBusConnection != null) {
       myMessageBusConnection.disconnect();
     }
```

### F093 MemoryAnalyzer.java

**Path:** `editor/editor-runtime/source/jetbrains/mps/nodeEditor/memory/MemoryAnalyzer.java`

[Browser file](C:/Codex/mps-j2cl/mps-web/editor/editor-runtime/source/jetbrains/mps/nodeEditor/memory/MemoryAnalyzer.java) · [Compared upstream file](C:/Codex/mps-j2cl/mps/editor/editor-runtime/source/jetbrains/mps/nodeEditor/memory/MemoryAnalyzer.java)

**Classification:** JVM instrumentation. Text delta: +12 / −139 lines; 148 → 21 total lines.

**What changes:** Removes Java instrumentation, reflected fields and object-size traversal. isValid returns false; all size/append operations throw an explicit unavailable exception.

**Behavior and limits:** No browser object-size numbers are fabricated. JavaScript objects have no equivalent JVM object layout.

**To make the replacement unnecessary:** Make memory instrumentation an optional upstream service excluded from the browser profile. A browser-specific profiler may be a separate feature, but cannot promise JVM object-layout equality.

Representative excerpts from the compared files; the prose above covers additional changes.

Pinned upstream at line 35

```java

  static Instrumentation ourInstrumentation = getInstrumentation();

  private static Instrumentation getInstrumentation() {
    try {
      Class<?> agentClass = Class.forName(AGENT_CLASS_NAME, true, ClassLoader.getSystemClassLoader());
      if (agentClass == null) {
        return null;
      }
      Method getter = getInstrumentationGetter(agentClass);
      return (Instrumentation) getter.invoke(null);
    } catch (ClassNotFoundException | IllegalAccessException | InvocationTargetException e) {
```

Browser replacement at line 9

```java
public class MemoryAnalyzer {
  public boolean isValid() { return false; }
  public long getSize() { throw unavailable(); }
  public void appendObject(Object object) { throw unavailable(); }
  public void appendCollection(Collection collection) { throw unavailable(); }
  public void appendLinkedList(LinkedList list) { throw unavailable(); }
  public void appendArrayList(ArrayList list) { throw unavailable(); }
  public void appendFirstNonPrimitiveField(Object object) { throw unavailable(); }
  public void appendField(Object object, String fieldName) { throw unavailable(); }
  private UnsupportedOperationException unavailable() {
    return new UnsupportedOperationException("JVM object-size instrumentation is not available in the browser");
  }
```

### F094 NodeInformationDialog.java

**Path:** `editor/editor-runtime/source/jetbrains/mps/nodeEditor/NodeInformationDialog.java`

[Browser file](C:/Codex/mps-j2cl/mps-web/editor/editor-runtime/source/jetbrains/mps/nodeEditor/NodeInformationDialog.java) · [Compared upstream file](C:/Codex/mps-j2cl/mps/editor/editor-runtime/source/jetbrains/mps/nodeEditor/NodeInformationDialog.java)

**Classification:** Editor rendering input and dialogs. Text delta: +3 / −30 lines; 149 → 122 total lines.

**What changes:** Replaces HTMLDocument/HTMLEditorKit serialization of selected or complete content with selection from the normalized plain-text document, falling back to all text and trimming.

**Behavior and limits:** Copied text works for this UI profile; rich HTML document behavior is not retained.

**To make the replacement unnecessary:** Implement the needed HTML document serialization in the shared UI layer, or upstream a plain-text extraction interface supplied by each documentation renderer.

Complete textual diff

```diff
--- mps/editor/editor-runtime/source/jetbrains/mps/nodeEditor/NodeInformationDialog.java
+++ mps-web/editor/editor-runtime/source/jetbrains/mps/nodeEditor/NodeInformationDialog.java
@@ -27,12 +27,7 @@
 import javax.swing.JEditorPane;
 import javax.swing.JScrollPane;
 import javax.swing.border.LineBorder;
-import javax.swing.text.BadLocationException;
-import javax.swing.text.html.HTMLDocument;
-import javax.swing.text.html.HTMLEditorKit;
 import java.awt.Point;
-import java.io.IOException;
-import java.io.StringWriter;
 
 // name ...Dialog is a legacy (used to be a JDialog); didn't rename as this class is part of EditorComponent interface
 public final class NodeInformationDialog {
@@ -49,31 +44,9 @@
     final var textArea = new JEditorPane("text/html", "") {
       @Override
       public String getSelectedText() {
-        final HTMLDocument doc = (HTMLDocument) this.getDocument();
-        final HTMLEditorKit kit = new HTMLEditorKit();
-        final StringWriter writer = new StringWriter();
-
-        // Write the plain text version of the selected range or whole doc
-        final int start = this.getSelectionStart();
-        final int end = this.getSelectionEnd();
-        try {
-          if (start != end) {
-            kit.write(writer, doc, start, end - start);
-          } else {
-            kit.write(writer, doc, 0, doc.getLength());
-          }
-        } catch (IOException | BadLocationException e) {
-          throw new RuntimeException(e);
-        }
-
-        final String content = writer.toString();
-        return content.replaceAll("\n", "")
-                      .replaceAll("(?i)<br\\s*/?>", "\n")
-                      .replaceAll("\\<.*?\\>", "")
-                      .replaceAll("&lt;", "<")
-                      .replaceAll("&gt;", ">")
-                      .replaceAll("[ ]+", " ")
-                      .trim();
+        // The browser JEditorPane already stores normalized plain text.
+        String selected = super.getSelectedText();
+        return (selected == null ? getText() : selected).trim();
       }
     };
 
```

### F095 PlatformEditorEmulation.java

**Path:** `editor/editor-runtime/source/jetbrains/mps/nodeEditor/PlatformEditorEmulation.java`

[Browser file](C:/Codex/mps-j2cl/mps-web/editor/editor-runtime/source/jetbrains/mps/nodeEditor/PlatformEditorEmulation.java) · [Compared upstream file](C:/Codex/mps-j2cl/mps/editor/editor-runtime/source/jetbrains/mps/nodeEditor/PlatformEditorEmulation.java)

**Classification:** Editor rendering input and dialogs. Text delta: +24 / −647 lines; 760 → 137 total lines.

**What changes:** Replaces the large IntelliJ Editor facade and dummy text/caret/document APIs with a small UserDataHolderBase adapter exposing components, scrolling, colors, project, disposal, font metrics and real tooltip/documentation behavior. Adds null-root and pending-hint cleanup guards.

**Behavior and limits:** This compiles against the selected smaller IntelliJ compatibility API, not the full IDE Editor interface. Documentation suppression now checks actual Dialog ancestry rather than IdeRootPane.

**To make the replacement unnecessary:** Split projectional-editor tooltip/view contracts from IntelliJ's text-editor API upstream and adapt the desktop side separately. Implementing every unrelated IDE text API would add little browser-editor value.

Representative excerpts from the compared files; the prose above covers additional changes.

Pinned upstream at line 73

```java

public final class PlatformEditorEmulation implements Editor {
  private final EditorComponent myEditorComponent;
  private List<EditorMouseListener> myMouseListeners = ContainerUtil.createLockFreeCopyOnWriteList();
  private final PlatformScrollingModelEmulation myScrollingModel;
  private final MouseMotionListener myMouseMotionAdapter = new MyMouseMotionAdapter();
  private final MouseListener myMouseListener = new MyMouseAdapter();
  private HintPopupController myHintPopupController = new HintPopupController(this::showInfoToolTip);

  PlatformEditorEmulation(@NotNull EditorComponent editorComponent) {
    myEditorComponent = editorComponent;
    myScrollingModel = new PlatformScrollingModelEmulation(this);
```

Browser replacement at line 33

```java
/** Browser tooltip/view adapter; projectional editing stays in EditorComponent. */
public final class PlatformEditorEmulation extends UserDataHolderBase implements Editor {
  private final EditorComponent myEditorComponent;
  private final PlatformScrollingModelEmulation myScrollingModel;
  private final HintPopupController myHintPopupController = new HintPopupController(this::showInfoToolTip);

  PlatformEditorEmulation(EditorComponent editorComponent) {
    myEditorComponent = editorComponent;
    myScrollingModel = new PlatformScrollingModelEmulation(this);
  }
  public void installListeners(Component owner) { myHintPopupController.installListeners(owner); }
  public void uninstallListeners(Component owner) { myHintPopupController.uninstallListeners(owner); }
```

### F096 EditorCellSelection.java

**Path:** `editor/editor-runtime/source/jetbrains/mps/nodeEditor/selection/EditorCellSelection.java`

[Browser file](C:/Codex/mps-j2cl/mps-web/editor/editor-runtime/source/jetbrains/mps/nodeEditor/selection/EditorCellSelection.java) · [Compared upstream file](C:/Codex/mps-j2cl/mps/editor/editor-runtime/source/jetbrains/mps/nodeEditor/selection/EditorCellSelection.java)

**Classification:** Compiled deployment and reflection. Text delta: +3 / −1 lines; 263 → 265 total lines.

**What changes:** Serializes a selection's registered binary alias rather than Class.getName.

**Behavior and limits:** Saved names remain stable under stripped class metadata, but the selection class must have a live registered factory owner.

**To make the replacement unnecessary:** Upstream stable persistence identifiers/factory metadata for selections and generate registrations. Keeping class-name metadata is another option, with bundle-size and ownership consequences.

Complete textual diff

```diff
--- mps/editor/editor-runtime/source/jetbrains/mps/nodeEditor/selection/EditorCellSelection.java
+++ mps-web/editor/editor-runtime/source/jetbrains/mps/nodeEditor/selection/EditorCellSelection.java
@@ -15,6 +15,8 @@
  */
 package jetbrains.mps.nodeEditor.selection;
 
+import jetbrains.mps.classloading.ModuleClassLoader;
+
 import jetbrains.mps.openapi.editor.EditorComponent;
 import jetbrains.mps.openapi.editor.cells.CellAction;
 import jetbrains.mps.openapi.editor.cells.CellActionType;
@@ -125,7 +127,7 @@
 
   @Override
   public SelectionInfoImpl getSelectionInfo() throws SelectionStoreException {
-    SelectionInfoImpl selectionInfo = new SelectionInfoImpl(this.getClass().getName());
+    SelectionInfoImpl selectionInfo = new SelectionInfoImpl(ModuleClassLoader.getRegisteredClassName(getClass()));
     selectionInfo.setCellInfo(myEditorCell.getCellInfo());
     selectionInfo.getPropertiesMap().put(CARET_X_PROPERTY_NAME, Integer.toString(getCaretX()));
     selectionInfo.getPropertiesMap().put(CARET_X_RELATIVE_PROPERTY_NAME, Integer.toString(getCaretXRelative()));
```

### F097 NodeRangeSelection.java

**Path:** `editor/editor-runtime/source/jetbrains/mps/nodeEditor/selection/NodeRangeSelection.java`

[Browser file](C:/Codex/mps-j2cl/mps-web/editor/editor-runtime/source/jetbrains/mps/nodeEditor/selection/NodeRangeSelection.java) · [Compared upstream file](C:/Codex/mps-j2cl/mps/editor/editor-runtime/source/jetbrains/mps/nodeEditor/selection/NodeRangeSelection.java)

**Classification:** Compiled deployment and reflection. Text delta: +15 / −18 lines; 485 → 482 total lines.

**What changes:** Uses registered aliases for selection/filter serialization, system-registry class lookup for global filters and a typed EditorContext constructor factory instead of reflection. Adjusts construction-error translation.

**Behavior and limits:** The selection/filter algorithms remain. Missing factories and constructor failures are explicit; module-local and global lookup boundaries still matter.

**To make the replacement unnecessary:** Generate filter and selection factories with stable identifiers, and let upstream selection persistence use a factory service. Verify custom filters, stale selections and stripped-metadata bundles.

Representative excerpts from the compared files; the prose above covers additional changes.

Pinned upstream at line 261

```java

      Constructor<? extends RangeSelectionFilter> constructor = filterClass.getConstructor(EditorContext.class);
      result = constructor.newInstance(getEditorComponent().getEditorContext());
    } catch (NoSuchMethodException e) {
      throw new SelectionStoreException("No suitable constructor in filter class: " + e.getMessage());
    } catch (ClassNotFoundException e) {
      throw new SelectionStoreException("Filter class not found: " + e.getMessage());
    } catch (InstantiationException e) {
      throw new SelectionStoreException("Can't instantiate filter class: " + e.getMessage());
    } catch (IllegalAccessException e) {
      throw new SelectionStoreException("Illegal access while instantiating filter class: " + e.getMessage());
    } catch (InvocationTargetException e) {
```

Browser replacement at line 261

```java
      try {
        result = ModuleClassLoader.newInstance(filterClass, RangeSelectionFilter.class,
            new Class<?>[] {EditorContext.class}, getEditorComponent().getEditorContext());
      } catch (Error constructorFailure) {
        throw new SelectionStoreException("Exception while constructing filter class: " + constructorFailure.getMessage());
      }
    } catch (ModuleClassLoader.FactoryNotFoundException e) {
      throw new SelectionStoreException("No registered constructor in filter class: " + e.getMessage());
    } catch (ClassNotFoundException e) {
      throw new SelectionStoreException("Filter class not found: " + e.getMessage());
    } catch (Exception e) {
      throw new SelectionStoreException("Exception while constructing filter class: " + e.getMessage());
```

### F098 SelectionInfoImpl.java

**Path:** `editor/editor-runtime/source/jetbrains/mps/nodeEditor/selection/SelectionInfoImpl.java`

[Browser file](C:/Codex/mps-j2cl/mps-web/editor/editor-runtime/source/jetbrains/mps/nodeEditor/selection/SelectionInfoImpl.java) · [Compared upstream file](C:/Codex/mps-j2cl/mps/editor/editor-runtime/source/jetbrains/mps/nodeEditor/selection/SelectionInfoImpl.java)

**Classification:** Compiled deployment and reflection. Text delta: +18 / −17 lines; 236 → 237 total lines.

**What changes:** Restores selections through the system registry/module loader and typed (EditorComponent, Map, CellInfo) factories; treats stale SelectionRestoreException as expected. Adds a direct SelectionDirection.valueOf branch before the generic enum fallback.

**Behavior and limits:** The supported selection enum does not require generic reflection; arbitrary other enum types are not thereby made supported. The raw factory exception boundary differs from InvocationTargetException wrapping.

**To make the replacement unnecessary:** Use an upstream selection factory/serializer service with generated enum parsing. Preserve XML/property formats and expected stale-selection behavior.

Complete textual diff

```diff
--- mps/editor/editor-runtime/source/jetbrains/mps/nodeEditor/selection/SelectionInfoImpl.java
+++ mps-web/editor/editor-runtime/source/jetbrains/mps/nodeEditor/selection/SelectionInfoImpl.java
@@ -32,8 +32,7 @@
 import org.jetbrains.mps.openapi.module.SRepository;
 import org.jetbrains.mps.openapi.persistence.PersistenceFacade;
 
-import java.lang.reflect.Constructor;
-import java.lang.reflect.InvocationTargetException;
+import jetbrains.mps.classloading.ModuleClassLoader;
 import java.util.HashMap;
 import java.util.Map;
 import java.util.Map.Entry;
@@ -126,27 +125,24 @@
         //       EditorAspectDescriptor contribution.
         selectionClass = reloadableModule.getClass(mySelectionClassName);
       } else {
-        selectionClass = getClass().getClassLoader().loadClass(mySelectionClassName);
+        selectionClass = ClassLoader.getSystemClassLoader().loadClass(mySelectionClassName);
       }
       if (!Selection.class.isAssignableFrom(selectionClass)) {
         LOG.error("Serialized selection class: " + mySelectionClassName + " is not a subclass of " + Selection.class.getName());
         return null;
       }
-      Constructor<Selection> constructor = ((Class<Selection>) selectionClass).getConstructor(EditorComponent.class, Map.class, CellInfo.class);
-      return constructor.newInstance(editorComponent, myProperties, myCellInfo);
-    } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InstantiationException e) {
-      LOG.error(e);
+      try {
+        return ModuleClassLoader.newInstance(selectionClass, Selection.class,
+            new Class<?>[] {EditorComponent.class, Map.class, CellInfo.class}, editorComponent, myProperties, myCellInfo);
+      } catch (Error constructorFailure) {
+        // JVM reflection wrapped constructor Errors in InvocationTargetException.
+        LOG.error(constructorFailure);
+        return null;
+      }
+    } catch (SelectionRestoreException e) {
+      // Stale model/cell selections are expected and must not become error messages.
       return null;
-    } catch (InvocationTargetException e) {
-      if (e.getTargetException() instanceof SelectionRestoreException) {
-        /**
-         * Skipping this exception because it is indicating that selection was not restored due to some changes in
-         * associated model(s).
-         *
-         * For example: element that was indicated as selected before is not in a model anymore (was deleter).
-         */
-        return null;
-      }
+    } catch (Exception e) {
       LOG.error(e);
       return null;
     }
@@ -227,6 +223,11 @@
 //        throw new SelectionStoreException("Cannot load enum property - property value was not specified for propertyName = " + propertyName);
       }
       try {
+        // Enum.valueOf(Class, String) needs JVM enum reflection. The only enum
+        // used by MPS selection persistence has a generated static parser.
+        if (enumClass == Selection.SelectionDirection.class) {
+          return Selection.SelectionDirection.valueOf(propertyValue);
+        }
         return Enum.valueOf(enumClass, propertyValue);
       } catch (IllegalArgumentException ex) {
         throw new SelectionStoreException("Invalid enum literal name specified: " + propertyValue + " for enum: " + enumClass.getCanonicalName());
```

### F099 UpdaterRepositoryContentAdapter.java

**Path:** `editor/editor-runtime/source/jetbrains/mps/nodeEditor/updater/UpdaterRepositoryContentAdapter.java`

[Browser file](C:/Codex/mps-j2cl/mps-web/editor/editor-runtime/source/jetbrains/mps/nodeEditor/updater/UpdaterRepositoryContentAdapter.java) · [Compared upstream file](C:/Codex/mps-j2cl/mps/editor/editor-runtime/source/jetbrains/mps/nodeEditor/updater/UpdaterRepositoryContentAdapter.java)

**Classification:** Java APIs and text processing. Text delta: +1 / −1 lines; 102 → 102 total lines.

**What changes:** Replaces Thread.currentThread().getStackTrace with a newly created Throwable's stack trace for model-removal diagnostics.

**Behavior and limits:** This captures the current browser diagnostic stack; it is not JVM thread inspection and frames/text may differ.

**To make the replacement unnecessary:** Supply Thread.getStackTrace in the shared JRE with a documented browser contract, or upstream Throwable-based diagnostic capture.

Complete textual diff

```diff
--- mps/editor/editor-runtime/source/jetbrains/mps/nodeEditor/updater/UpdaterRepositoryContentAdapter.java
+++ mps-web/editor/editor-runtime/source/jetbrains/mps/nodeEditor/updater/UpdaterRepositoryContentAdapter.java
@@ -50,7 +50,7 @@
       return;
     }
     if (myModelReference.equals(model.getReference())) {
-      myEditorComponent.setModelDisposedTrace(Thread.currentThread().getStackTrace());
+      myEditorComponent.setModelDisposedTrace(new Throwable("Model removed").getStackTrace());
     }
     stopListening(model);
   }
```

### F100 mps-icons.iml

**Path:** `workbench/mps-icons/mps-icons.iml`

[Browser file](C:/Codex/mps-j2cl/mps-web/workbench/mps-icons/mps-icons.iml) · [Compared upstream file](C:/Codex/mps-j2cl/mps/workbench/mps-icons/mps-icons.iml)

**Classification:** IntelliJ project metadata. Text delta: +3 / −3 lines; 13 → 13 total lines.

**What changes:** Customized IntelliJ descriptor with a same-path upstream counterpart. Indexes the overlay source root(s) `source` with inherited compiler output. Declares 2 module dependencies. The complete XML or descriptor diff below records every dependency name. Added module references: `intellij-stubs`, `webswing`.

**Behavior and limits:** The root .idea/modules.xml references this file, so it affects IDE indexing. It is not an MPS_OVERRIDES entry and is not used to select Bazel Java sources. Local module names such as kernel2 or webswing are IDE identifiers, not proof of Bazel dependency ownership.

**To make the replacement unnecessary:** Generate the IDE project from the authoritative selected-source/build graph, or keep equivalent descriptors outside the overlay. Update the root IDE module references before relocating metadata. Removing this file alone would break that IDE reference and would not reduce the 100 Java replacements.

Complete descriptor diff

```diff
--- mps/workbench/mps-icons/mps-icons.iml
+++ mps-web/workbench/mps-icons/mps-icons.iml
@@ -1,13 +1,13 @@
 <?xml version="1.0" encoding="UTF-8"?>
 <module type="JAVA_MODULE" version="4">
-  <component name="NewModuleRootManager">
-    <output url="file://$MODULE_DIR$/classes" />
+  <component name="NewModuleRootManager" inherit-compiler-output="true">
     <exclude-output />
     <content url="file://$MODULE_DIR$">
       <sourceFolder url="file://$MODULE_DIR$/source" isTestSource="false" />
     </content>
     <orderEntry type="inheritedJdk" />
     <orderEntry type="sourceFolder" forTests="false" />
-    <orderEntry type="library" name="Idea-platform" level="project" />
+    <orderEntry type="module" module-name="intellij-stubs" />
+    <orderEntry type="module" module-name="webswing" />
   </component>
 </module>
```

### F101 MPSIcons.java

**Path:** `workbench/mps-icons/source/jetbrains/mps/icons/MPSIcons.java`

[Browser file](C:/Codex/mps-j2cl/mps-web/workbench/mps-icons/source/jetbrains/mps/icons/MPSIcons.java) · [Compared upstream file](C:/Codex/mps-j2cl/mps/workbench/mps-icons/source/jetbrains/mps/icons/MPSIcons.java)

**Classification:** Icon resources and encoding. Text delta: +3 / −3 lines; 163 → 163 total lines.

**What changes:** Replaces IconManager plus MPSIcons.class.getClassLoader with BrowserIcons.named using normalized absolute paths.

**Behavior and limits:** The original named MPS icon catalog stays; asset bytes/dimensions are installed elsewhere by BrowserMpsAssets. This is not replacement by blank icons.

**To make the replacement unnecessary:** Provide a shared icon/resource loader with a build-generated MPS asset catalog, or upstream an injected icon resolver. Asset deployment remains necessary even when the class overlay disappears.

Complete textual diff

```diff
--- mps/workbench/mps-icons/source/jetbrains/mps/icons/MPSIcons.java
+++ mps-web/workbench/mps-icons/source/jetbrains/mps/icons/MPSIcons.java
@@ -16,14 +16,14 @@
 package jetbrains.mps.icons;
 
 import com.intellij.icons.AllIcons;
-import com.intellij.ui.IconManager;
+import com.intellij.compat.BrowserIcons;
 
 import javax.swing.Icon;
 
 public class MPSIcons {
   private static Icon load(String path) {
-    // XXX used to be IconLoader.getIcon(), but c.i.icons.AllIcons uses IconManager, why wouldn't MPS then?
-    return IconManager.getInstance().getIcon(path, MPSIcons.class.getClassLoader());
+    // BrowserMpsAssets supplies original bytes and dimensions; this reference follows scoped reloads.
+    return BrowserIcons.named(path.startsWith("/") ? path : "/" + path);
   }
   
   public static class Ant {
```

### F102 jetbrains.mps.ide.platform.iml

**Path:** `workbench/mps-platform/jetbrains.mps.ide.platform/jetbrains.mps.ide.platform.iml`

[Browser file](C:/Codex/mps-j2cl/mps-web/workbench/mps-platform/jetbrains.mps.ide.platform/jetbrains.mps.ide.platform.iml) · No same-path file at the pinned upstream revision

**Classification:** IntelliJ project metadata. Text delta: +40 / −0 lines; 0 → 40 total lines.

**What changes:** Added IntelliJ module descriptor: no file exists at this exact path in the pinned upstream tree. Indexes the overlay source root(s) `source_gen` with inherited compiler output. Declares 29 module dependencies. The complete XML or descriptor diff below records every dependency name.

**Behavior and limits:** The root .idea/modules.xml references this file, so it affects IDE indexing. It is not an MPS_OVERRIDES entry and is not used to select Bazel Java sources. Local module names such as kernel2 or webswing are IDE identifiers, not proof of Bazel dependency ownership.

**To make the replacement unnecessary:** Generate the IDE project from the authoritative selected-source/build graph, or keep equivalent descriptors outside the overlay. Update the root IDE module references before relocating metadata. Removing this file alone would break that IDE reference and would not reduce the 100 Java replacements.

Current module descriptor

```xml
<?xml version="1.0" encoding="UTF-8"?>
<module type="JAVA_MODULE" version="4">
  <component name="NewModuleRootManager" inherit-compiler-output="true">
    <exclude-output />
    <content url="file://$MODULE_DIR$">
      <sourceFolder url="file://$MODULE_DIR$/source_gen" isTestSource="false" />
    </content>
    <orderEntry type="inheritedJdk" />
    <orderEntry type="sourceFolder" forTests="false" />
    <orderEntry type="module" module-name="components" />
    <orderEntry type="module" module-name="browser" />
    <orderEntry type="module" module-name="mps-annotations" />
    <orderEntry type="module" module-name="tests" />
    <orderEntry type="module" module-name="webswing" />
    <orderEntry type="module" module-name="mps-platform" />
    <orderEntry type="module" module-name="constraints" />
    <orderEntry type="module" module-name="mps-workbench" />
    <orderEntry type="module" module-name="smodel" />
    <orderEntry type="module" module-name="behavior-runtime" />
    <orderEntry type="module" module-name="kernel2" />
    <orderEntry type="module" module-name="runtime2" />
    <orderEntry type="module" module-name="make-runtime" />
    <orderEntry type="module" module-name="xmlPersistence" />
    <orderEntry type="module" module-name="findUsages-runtime" />
    <orderEntry type="module" module-name="mps-platform2" />
    <orderEntry type="module" module-name="smodelRuntime" />
    <orderEntry type="module" module-name="logging2" />
    <orderEntry type="module" module-name="smodel2" />
    <orderEntry type="module" module-name="kernel" />
    <orderEntry type="module" module-name="openapi2" />
    <orderEntry type="module" module-name="behavior-api" />
    <orderEntry type="module" module-name="core" />
    <orderEntry type="module" module-name="intellij-stubs" />
    <orderEntry type="module" module-name="kernelSolution2" />
    <orderEntry type="module" module-name="project" />
    <orderEntry type="module" module-name="openapi" />
    <orderEntry type="module" module-name="persistence" />
    <orderEntry type="module" module-name="project3" />
  </component>
</module>
```

### F103 AddRequiredImportsDialog.java

**Path:** `workbench/mps-platform/jetbrains.mps.ide.platform/source_gen/jetbrains/mps/ide/datatransfer/AddRequiredImportsDialog.java`

[Browser file](C:/Codex/mps-j2cl/mps-web/workbench/mps-platform/jetbrains.mps.ide.platform/source_gen/jetbrains/mps/ide/datatransfer/AddRequiredImportsDialog.java) · [Compared upstream file](C:/Codex/mps-j2cl/mps/workbench/mps-platform/jetbrains.mps.ide.platform/source_gen/jetbrains/mps/ide/datatransfer/AddRequiredImportsDialog.java)

**Classification:** Editor rendering input and dialogs. Text delta: +55 / −261 lines; 270 → 64 total lines.

**What changes:** Replaces a blocking DialogWrapper checkbox UI with a project-provided ImportPolicy and immutable copied selections, subset/duplicate validation, one-shot show and a real CopyPasteUtil update command.

**Behavior and limits:** show is still synchronous and must not block. There is no interactive asynchronous import dialog here; absent policy fails and null selection declines imports.

**To make the replacement unnecessary:** Upstream an asynchronous import-decision service and update the originating source model/generator, or keep a synchronous host policy as an explicit API. A DOM dialog cannot safely be dropped into the existing blocking call chain.

Representative excerpts from the compared files; the prose above covers additional changes.

Pinned upstream at line 45

```java
@GeneratedClass(nodeId = "6058851216819286114", model = "r:84719e1a-99f6-4297-90ba-8ad2a947fa4a(jetbrains.mps.ide.datatransfer)")
public class AddRequiredImportsDialog extends DialogWrapper {
  private final SModelReference[] myRequiredImports;
  private final SLanguage[] myRequiredLanguages;
  private final Map<SModelReference, Icon> myModelIcons = new HashMap<SModelReference, Icon>();
  private CheckBoxList<SModelReference> myModelsList;
  private CheckBoxList<SLanguage> myLanguagesList;
  private SModelReference[] mySelectedImports;
  private SLanguage[] mySelectedLanguages;

  public AddRequiredImportsDialog(@NotNull final Project project, @NotNull SModelReference[] requiredImports, @NotNull SLanguage[] requiredLanguages) {
    super(ProjectHelper.toIdeaProject(project), true);
```

Browser replacement at line 13

```java
public final class AddRequiredImportsDialog {
  public interface ImportPolicy {
    /** Return null to decline imports. A policy must decide without blocking the browser event loop. */
    Selection select(Project project, SModelReference[] models, SLanguage[] languages);
  }
  public static final class Selection {
    private final SModelReference[] models;
    private final SLanguage[] languages;
    public Selection(SModelReference[] models, SLanguage[] languages) {
      this.models = Arrays.copyOf(models, models.length);
      this.languages = Arrays.copyOf(languages, languages.length);
    }
```

### F104 BaseIconManager.java

**Path:** `workbench/mps-platform/jetbrains.mps.ide.platform/source_gen/jetbrains/mps/ide/icons/BaseIconManager.java`

[Browser file](C:/Codex/mps-j2cl/mps-web/workbench/mps-platform/jetbrains.mps.ide.platform/source_gen/jetbrains/mps/ide/icons/BaseIconManager.java) · [Compared upstream file](C:/Codex/mps-j2cl/mps/workbench/mps-platform/jetbrains.mps.ide.platform/source_gen/jetbrains/mps/ide/icons/BaseIconManager.java)

**Classification:** Icon resources and encoding. Text delta: +36 / −38 lines; 303 → 301 total lines.

**What changes:** Retains icon selection/composition/cache logic but replaces lock/row icons, classloader reload checks and lazy CachedImageIcon unwrapping with generation-based resource invalidation, explicit module loads and a local SideBySideIcon implementation.

**Behavior and limits:** Real icons and dimensions remain. Cache invalidation follows resource registration changes rather than JVM loader identity.

**To make the replacement unnecessary:** Provide a shared resource-origin/icon cache service and missing icon composition primitives; upstream only the MPS selection policy. Update the original generated model rather than hand-editing source_gen output indefinitely.

Representative excerpts from the compared files; the prose above covers additional changes.

Pinned upstream at line 234

```java
    if (ir.isLegacy()) {
      if (isAlreadyReloaded(ir)) {
        MapSequence.fromMap(myResToIcon).removeKey(ir);
      }
      if (MapSequence.fromMap(myResToIcon).containsKey(ir)) {
        return MapSequence.fromMap(myResToIcon).get(ir);
      }

      Class<?> provider = ir.getProvider();
      icon = IconLoadingUtil.loadIcon(ir.getResourceId(myNewUI), provider);
      if (provider != null && icon instanceof IconLoader.CachedImageIcon) {
        // see MPS-30995. There's no way to ensure the provider will not be disposed already when the icon will be required the first time. That's why we ensure here that for a non-default provider we load the icon exactly at the moment it's requested
```

Browser replacement at line 229

```java
    }
    long generation = IconLoadingUtil.getGeneration();
    if (generation != myIconGeneration) {
      invalidate();
      myIconGeneration = generation;
    }
    Icon icon = null;
    if (ir.isLegacy()) {
      if (MapSequence.fromMap(myResToIcon).containsKey(ir)) {
        return MapSequence.fromMap(myResToIcon).get(ir);
      }

```

### F105 IconLoadingUtil.java

**Path:** `workbench/mps-platform/jetbrains.mps.ide.platform/source_gen/jetbrains/mps/ide/icons/IconLoadingUtil.java`

[Browser file](C:/Codex/mps-j2cl/mps-web/workbench/mps-platform/jetbrains.mps.ide.platform/source_gen/jetbrains/mps/ide/icons/IconLoadingUtil.java) · [Compared upstream file](C:/Codex/mps-j2cl/mps/workbench/mps-platform/jetbrains.mps.ide.platform/source_gen/jetbrains/mps/ide/icons/IconLoadingUtil.java)

**Classification:** Icon resources and encoding. Text delta: +84 / −18 lines; 29 → 95 total lines.

**What changes:** Adds explicit module-root and provider-package registrations, conflict checks, unregister cleanup and a generation counter; routes loading to registered BrowserIcons. Fixes isNewUIActive to false for the bundled classic assets.

**Behavior and limits:** No Class package/classloader discovery occurs. Relative icons require metadata; classic UI selection is a browser-profile policy.

**To make the replacement unnecessary:** Generate icon/provider catalogs and expose a common upstream resource resolver plus UI-style policy. A generic JRE Class implementation cannot supply missing asset bytes by itself.

Representative excerpts from the compared files; the prose above covers additional changes.

Pinned upstream at line 18

```java
      return IconLoader.findIcon(resource, loader.getClassLoader());
    }

    String packName = loader.getPackage().getName();
    String resourcePath = (packName.isEmpty() ? "/" + resource : "/" + packName.replace('.', '/') + "/" + resource);
    return IconLoader.findIcon(resourcePath, loader.getClassLoader());
  }

  public static boolean isNewUIActive() {
    return ExperimentalUI.isNewUI();
  }
}
```

Browser replacement at line 28

```java
  /** packageName is declared by the build manifest; it is never inferred from Class metadata. */
  public static void registerProvider(Class<?> provider, SModuleReference module, String packageName) {
    Objects.requireNonNull(provider); Objects.requireNonNull(packageName);
    if (!MODULE_ROOTS.containsKey(module)) throw new IllegalStateException("Register the module icon root first");
    if (packageName.startsWith(".") || packageName.endsWith(".") || packageName.contains("..")
        || packageName.indexOf('/') >= 0 || packageName.indexOf('\\') >= 0) {
      throw new IllegalArgumentException("Expected a declared Java package name");
    }
    ProviderOrigin origin = new ProviderOrigin(module, packageName.replace('.', '/'));
    ProviderOrigin previous = PROVIDERS.get(provider);
    if (previous != null && (!previous.module.equals(module) || !previous.packagePath.equals(origin.packagePath))) {
      throw new IllegalStateException("Icon provider already belongs to another resource origin");
```

### F106 mps-platform.iml

**Path:** `workbench/mps-platform/mps-platform.iml`

[Browser file](C:/Codex/mps-j2cl/mps-web/workbench/mps-platform/mps-platform.iml) · [Compared upstream file](C:/Codex/mps-j2cl/mps/workbench/mps-platform/mps-platform.iml)

**Classification:** IntelliJ project metadata. Text delta: +23 / −28 lines; 39 → 34 total lines.

**What changes:** Customized IntelliJ descriptor with a same-path upstream counterpart. Indexes the overlay source root(s) `source` with inherited compiler output. Declares 22 module dependencies and 1 project libraries. The complete XML or descriptor diff below records every dependency name. The upstream source/test root(s) `jetbrains.mps.ide.platform/source_gen`, `tests` are absent here. Added module references: `browser`, `core`, `intellij-stubs`, `jetbrains.mps.ide`, `kernel2`, `kernelSolution2`, `logging2`, `mps-platform2`, `mps-workbench`, `openapi`, `openapi2`, `platform2`, `platform4`, `project-check`, `project3`, `testbench`, `vfs`, `vfs2`, `webswing`, `xmlPersistence`. Removed module references: `ant-mps-common`, `baseLanguage-search`, `closures-runtime`, `collections-runtime`, `editing-runtime`, `editor-api`, `environment`, `findUsages-runtime`, `generator-engine`, `java-stub`, `kernel-resources`, `make-runtime`, `mps-icons`, `platform`, `resources_en`, `tuples-runtime`, `vfs-tracking`.

**Behavior and limits:** The root .idea/modules.xml references this file, so it affects IDE indexing. It is not an MPS_OVERRIDES entry and is not used to select Bazel Java sources. Local module names such as kernel2 or webswing are IDE identifiers, not proof of Bazel dependency ownership.

**To make the replacement unnecessary:** Generate the IDE project from the authoritative selected-source/build graph, or keep equivalent descriptors outside the overlay. Update the root IDE module references before relocating metadata. Removing this file alone would break that IDE reference and would not reduce the 100 Java replacements.

Complete descriptor diff

```diff
--- mps/workbench/mps-platform/mps-platform.iml
+++ mps-web/workbench/mps-platform/mps-platform.iml
@@ -1,39 +1,34 @@
 <?xml version="1.0" encoding="UTF-8"?>
 <module type="JAVA_MODULE" version="4">
-  <component name="NewModuleRootManager">
-    <output url="file://$MODULE_DIR$/classes" />
-    <output-test url="file://$MODULE_DIR$/testclasses" />
+  <component name="NewModuleRootManager" inherit-compiler-output="true">
     <exclude-output />
     <content url="file://$MODULE_DIR$">
       <sourceFolder url="file://$MODULE_DIR$/source" isTestSource="false" />
-      <sourceFolder url="file://$MODULE_DIR$/jetbrains.mps.ide.platform/source_gen" isTestSource="false" generated="true" />
-      <sourceFolder url="file://$MODULE_DIR$/tests" isTestSource="true" />
     </content>
     <orderEntry type="inheritedJdk" />
     <orderEntry type="sourceFolder" forTests="false" />
-    <orderEntry type="library" name="Idea-platform" level="project" />
-    <orderEntry type="library" name="google.gson" level="project" />
-    <orderEntry type="module" module-name="kernel" exported="" />
-    <orderEntry type="module" module-name="collections-runtime" />
-    <orderEntry type="module" module-name="editor-api" />
-    <orderEntry type="module" module-name="make-runtime" />
-    <orderEntry type="module" module-name="closures-runtime" />
-    <orderEntry type="module" module-name="mps-icons" />
-    <orderEntry type="module" module-name="kernel-resources" />
-    <orderEntry type="module" module-name="java-stub" />
-    <orderEntry type="module" module-name="findUsages-runtime" />
-    <orderEntry type="module" module-name="environment" />
-    <orderEntry type="module" module-name="platform" exported="" />
-    <orderEntry type="module" module-name="baseLanguage-search" />
+    <orderEntry type="library" name="lib" level="project" />
+    <orderEntry type="module" module-name="mps-platform2" />
+    <orderEntry type="module" module-name="browser" />
+    <orderEntry type="module" module-name="logging2" />
+    <orderEntry type="module" module-name="webswing" />
+    <orderEntry type="module" module-name="mps-workbench" />
+    <orderEntry type="module" module-name="kernel" />
+    <orderEntry type="module" module-name="openapi2" />
+    <orderEntry type="module" module-name="core" />
+    <orderEntry type="module" module-name="intellij-stubs" />
+    <orderEntry type="module" module-name="kernelSolution2" />
+    <orderEntry type="module" module-name="kernel2" />
+    <orderEntry type="module" module-name="testbench" />
+    <orderEntry type="module" module-name="openapi" />
+    <orderEntry type="module" module-name="platform4" />
+    <orderEntry type="module" module-name="vfs" />
     <orderEntry type="module" module-name="persistence" />
-    <orderEntry type="module" module-name="tuples-runtime" />
-    <orderEntry type="module" module-name="generator-engine" />
-    <orderEntry type="module" module-name="resources_en" />
-    <orderEntry type="module" module-name="ant-mps-common" />
-    <orderEntry type="module" module-name="editing-runtime" />
-    <orderEntry type="library" scope="TEST" name="JUnit4" level="project" />
-    <orderEntry type="library" name="gnu.trove" level="project" />
-    <orderEntry type="module" module-name="vfs-tracking" />
-    <orderEntry type="library" name="objectweb.asm" level="project" />
+    <orderEntry type="module" module-name="xmlPersistence" />
+    <orderEntry type="module" module-name="project-check" />
+    <orderEntry type="module" module-name="jetbrains.mps.ide" />
+    <orderEntry type="module" module-name="project3" />
+    <orderEntry type="module" module-name="platform2" />
+    <orderEntry type="module" module-name="vfs2" />
   </component>
 </module>
```

### F107 MPSCoreComponents.java

**Path:** `workbench/mps-platform/source/jetbrains/mps/ide/MPSCoreComponents.java`

[Browser file](C:/Codex/mps-j2cl/mps-web/workbench/mps-platform/source/jetbrains/mps/ide/MPSCoreComponents.java) · [Compared upstream file](C:/Codex/mps-j2cl/mps/workbench/mps-platform/source/jetbrains/mps/ide/MPSCoreComponents.java)

**Classification:** Project services node handles and undo. Text delta: +22 / −121 lines; 149 → 50 total lines.

**What changes:** Replaces desktop component/plugin startup with an injected Platform, explicit service lookups, a disposal guard and platform disposal.

**Behavior and limits:** This class becomes the host's bridge to the selected MPS platform; full desktop startup/plugin discovery is absent.

**To make the replacement unnecessary:** Introduce an upstream bootstrap interface or a separate browser service binding. Keep the application host responsible for creating and closing the Platform exactly once.

Representative excerpts from the compared files; the prose above covers additional changes.

Pinned upstream at line 64

```java

  public MPSCoreComponents() {
    //
    // FIXME check ApplicationInitializedListener and <applicationInitializedListener> extpoint if they can serve MPSCoreComponents initialization task
    //
    @NotNull ManagingFS fs = ManagingFS.getInstance();
    // Make sure WMA has a chance to MA.setInstance() *before* MPSModuleRepository and its GlobalModelAccess need one
    ModelAccess access = WorkbenchModelAccess.getInstance();
    assert access != null;
    myPlatform = PlatformFactory.initPlatform(PlatformOptionsBuilder.ALL);
    final ExtensionPointName<ComponentPluginFactory> cpfExtPoint = ExtensionPointName.create("jetbrains.mps.componentPluginFactory");
    for (ComponentPluginFactory cpf : cpfExtPoint.getExtensionList()) {
```

Browser replacement at line 25

```java

  public MPSCoreComponents(Platform platform) {
    this.platform = Objects.requireNonNull(platform, "Register the browser Platform before MPSCoreComponents");
  }

  public Platform getPlatform() {
    if (disposed) throw new IllegalStateException("MPS core components are disposed");
    return platform;
  }

  public PersistenceFacade getPersistenceFacade() { return getPlatform().findComponent(PersistenceRegistry.class); }
  public LibraryInitializer getLibraryInitializer() { return getPlatform().findComponent(LibraryInitializer.class); }
```

### F108 ThreadUtils.java

**Path:** `workbench/mps-platform/source/jetbrains/mps/ide/ThreadUtils.java`

[Browser file](C:/Codex/mps-j2cl/mps-web/workbench/mps-platform/source/jetbrains/mps/ide/ThreadUtils.java) · [Compared upstream file](C:/Codex/mps-j2cl/mps/workbench/mps-platform/source/jetbrains/mps/ide/ThreadUtils.java)

**Classification:** Scheduling model access and invalidation. Text delta: +4 / −2 lines; 138 → 140 total lines.

**What changes:** Replaces ProcessIOExecutorService background execution with Application.executeOnPooledThread and an explicit missing-application check.

**Behavior and limits:** EDT dispatch and exception-reporting methods remain. The actual browser Application decides scheduling; this is not a native process-IO thread pool.

**To make the replacement unnecessary:** Use an upstream executor abstraction or a shared platform compatibility implementation for the original pool API. Verify whether each caller needs a real later task rather than inline execution.

Complete textual diff

```diff
--- mps/workbench/mps-platform/source/jetbrains/mps/ide/ThreadUtils.java
+++ mps-web/workbench/mps-platform/source/jetbrains/mps/ide/ThreadUtils.java
@@ -15,7 +15,6 @@
  */
 package jetbrains.mps.ide;
 
-import com.intellij.execution.process.ProcessIOExecutorService;
 import com.intellij.openapi.application.Application;
 import com.intellij.openapi.application.ApplicationManager;
 import com.intellij.openapi.application.ModalityState;
@@ -33,7 +32,10 @@
 public class ThreadUtils {
 
   public static void submitToBGT(@NotNull Runnable code) {
-      ProcessIOExecutorService.INSTANCE.submit(code);
+    Application application = ApplicationManager.getApplication();
+    if (application == null) throw new IllegalStateException("Initialize the browser application before submitting background work");
+    // BrowserApplication schedules deferred work on its owned executor; no JVM process-I/O threads.
+    application.executeOnPooledThread(code);
   }
 
   @Nullable
```

### F109 OnReloadingUndoCleaner.java

**Path:** `workbench/mps-platform/source/jetbrains/mps/ide/undo/OnReloadingUndoCleaner.java`

[Browser file](C:/Codex/mps-j2cl/mps-web/workbench/mps-platform/source/jetbrains/mps/ide/undo/OnReloadingUndoCleaner.java) · [Compared upstream file](C:/Codex/mps-j2cl/mps/workbench/mps-platform/source/jetbrains/mps/ide/undo/OnReloadingUndoCleaner.java)

**Classification:** Project services node handles and undo. Text delta: +36 / −127 lines; 154 → 63 total lines.

**What changes:** Replaces document/virtual-file-specific undo cleanup with repository listeners that synchronously clear the entire owning project's undo history when writable models are removed/replaced. Adds explicit detach/disposal.

**Behavior and limits:** This is deliberately conservative: one model replacement invalidates unrelated model history in the same project. It avoids replaying old model objects.

**To make the replacement unnecessary:** Expose model-based undo invalidation upstream. A finer browser history would need per-model ownership/dependency tracking before narrowing invalidation; replacing text-document APIs alone is insufficient.

Representative excerpts from the compared files; the prose above covers additional changes.

Pinned upstream at line 134

```java

  private void clearUndoStack(SModel model) {
    WeakList<VirtualFile> registeredFiles = myUndoForModel.remove(model.getReference());
    if (registeredFiles == null || registeredFiles.isEmpty()) {
      return;
    }

    ApplicationManager.getApplication().invokeLater(() -> {
      if (isDisposed() || myProject.isDisposed()) {
        return;
      }
      UndoManager undoManager = UndoManager.getInstance(myProject.getProject());
```

Browser replacement at line 51

```java
      // A model replacement/removal invalidates the whole project's history.
      UndoManager.getInstance(((MPSProject) project).getProject()).clearHistory();
    }
  }

  @Override public void dispose() {
    if (myListenerRegistrar == null) return;
    clearUndoStack();
    RepoListenerRegistrar registrar = myListenerRegistrar;
    myListenerRegistrar = null;
    registrar.detach();
  }
```

### F110 UndoActionsCollector.java

**Path:** `workbench/mps-platform/source/jetbrains/mps/ide/undo/UndoActionsCollector.java`

[Browser file](C:/Codex/mps-j2cl/mps-web/workbench/mps-platform/source/jetbrains/mps/ide/undo/UndoActionsCollector.java) · [Compared upstream file](C:/Codex/mps-j2cl/mps/workbench/mps-platform/source/jetbrains/mps/ide/undo/UndoActionsCollector.java)

**Classification:** Project services node handles and undo. Text delta: +26 / −146 lines; 170 → 50 total lines.

**What changes:** Collects real MPS UndoItems and submits SNodeIdeaUndoableAction to the project UndoManager, dropping desktop DocumentReference, virtual-file and cache bookkeeping. Clears the action list on flush even on failure.

**Behavior and limits:** Actual model undo/redo remains, but it uses project-wide history and empty document references. No text-document persistence is supplied.

**To make the replacement unnecessary:** Split MPS model undo from desktop document bookkeeping upstream and provide a host undo-history interface. Verify all node/model operations and failure cleanup.

Representative excerpts from the compared files; the prose above covers additional changes.

Pinned upstream at line 134

```java

  void flushAndDispose() {
    assert !myDisposed;
    myDisposed = true;
    List<UndoItem> allItems = new ArrayList<>();
    // restoring all created virtual files on redo before redoing any other model modifications
    // in order to put file into VFS before accessing it from the redo actions
    for (MPSNodeVirtualFile createdFile : myCreatedFiles) {
      allItems.add(new RestoreVirtualFileInstance(createdFile, false));
    }
    allItems.addAll(myActions);
    // restoring all deleted virtual files on undo before undoing any other model modifications
```

Browser replacement at line 39

```java
      if (!(project instanceof MPSProject)) {
        throw new IllegalStateException("No browser MPSProject owns the command repository: " + project);
      }
      UndoManager manager = UndoManager.getInstance(((MPSProject) project).getProject());
      // The browser has a project-wide model history. Undo/redo executes these same
      // model operations; only IDEA's text Document and virtual-file caches are absent.
      manager.undoableActionPerformed(new SNodeIdeaUndoableAction(myActions, myUndoContext.getRepository(), true, Collections.emptyList()));
    } finally {
      myActions.clear();
    }
  }
}
```

### F111 WorkbenchUndoHandler.java

**Path:** `workbench/mps-platform/source/jetbrains/mps/ide/undo/WorkbenchUndoHandler.java`

[Browser file](C:/Codex/mps-j2cl/mps-web/workbench/mps-platform/source/jetbrains/mps/ide/undo/WorkbenchUndoHandler.java) · [Compared upstream file](C:/Codex/mps-j2cl/mps/workbench/mps-platform/source/jetbrains/mps/ide/undo/WorkbenchUndoHandler.java)

**Classification:** Project services node handles and undo. Text delta: +48 / −40 lines; 61 → 69 total lines.

**What changes:** Adds nested-command depth and repository checks, accumulates one outer undo action, owns reload cleaners and explicitly disposes them; rejects start after disposal.

**Behavior and limits:** Nested commands for different repositories are rejected, and the browser project owns cleanup. UndoItems remain the actual model operations.

**To make the replacement unnecessary:** Upstream a model-command undo collector with explicit lifetime and nesting semantics. Keep desktop document integration in a separate adapter.

Representative excerpts from the compared files; the prose above covers additional changes.

Pinned upstream at line 50

```java
  // FIXME why it's not a command listener, so that gets notifications about command start and command end? Won't need
  // neither isInsideUndoableCommand and ModelAccess.isInsideCommand, not this flushCommand.
  public void flushCommand() {
    myActionsCollector.flushAndDispose();
    myActionsCollector = null;
  }

  public void startCommand(UndoContext context) {
    assert myActionsCollector == null;
    myActionsCollector = new UndoActionsCollector(context);
  }
}
```

Browser replacement at line 33

```java

  public void startCommand(UndoContext context) {
    if (myDisposed) throw new IllegalStateException("Undo handler is disposed");
    Objects.requireNonNull(context);
    SRepository repository = Objects.requireNonNull(context.getRepository());
    if (myCommandDepth > 0) {
      if (repository != myCommandRepository) throw new IllegalStateException("Nested command changed repositories");
      myCommandDepth++;
      return;
    }
    myReloadCleaners.computeIfAbsent(repository, OnReloadingUndoCleaner::new);
    myCommandRepository = repository;
```

### F112 MPSNodeVirtualFile.java

**Path:** `workbench/mps-platform/source/jetbrains/mps/nodefs/MPSNodeVirtualFile.java`

[Browser file](C:/Codex/mps-j2cl/mps-web/workbench/mps-platform/source/jetbrains/mps/nodefs/MPSNodeVirtualFile.java) · [Compared upstream file](C:/Codex/mps-j2cl/mps/workbench/mps-platform/source/jetbrains/mps/nodefs/MPSNodeVirtualFile.java)

**Classification:** Project services node handles and undo. Text delta: +58 / −320 lines; 341 → 79 total lines.

**What changes:** Replaces the desktop node VirtualFile implementation with a repository/reference handle resolved under model read access, ephemeral paths, readiness callbacks and invalidation.

**Behavior and limits:** The handle is read-only, has empty byte content, no persisted editor history and no desktop file metadata integration. Real node data lives in the MPS repository, not the virtual file stream.

**To make the replacement unnecessary:** Introduce a repository-scoped NodeHandle/EditorIdentity service upstream; adapt it to VirtualFile only in the desktop host. Keep path/history policy explicit.

Representative excerpts from the compared files; the prose above covers additional changes.

Pinned upstream at line 50

```java
@SuppressWarnings("UnstableApiUsage")
public final class MPSNodeVirtualFile extends VirtualFile implements ProjectAwareVirtualFile, VirtualFileWithoutContent,
                                                                     EditorHistoryManager.OptionallyIncluded {
  private static final byte[] CONTENTS = new byte[0];
  private static final Logger LOG = Logger.getLogger(MPSNodeVirtualFile.class);
  public static final String NODE_PREFIX = "node://";

  @NotNull
  private SNodeReference myNode;
  @NotNull
  private RepositoryVirtualFiles myRepoFiles;
  private String myPath;
```

Browser replacement at line 32

```java

  public SNode getNode() {
    return active ? new ModelAccessHelper(repository).runReadAction(() -> reference.resolve(repository)) : null;
  }
  public SNodeReference getSNodePointer() { return reference; }
  @Override public String getPath() { return path; }
  @Override public VirtualFileSystem getFileSystem() { return fileSystem; }
  @Override public String getName() {
    return new ModelAccessHelper(repository).runReadAction(() -> {
      SNode node = active ? reference.resolve(repository) : null;
      return node == null ? reference.toString() : node.getPresentation();
    });
```

### F113 NodeVirtualFileSystem.java

**Path:** `workbench/mps-platform/source/jetbrains/mps/nodefs/NodeVirtualFileSystem.java`

[Browser file](C:/Codex/mps-j2cl/mps-web/workbench/mps-platform/source/jetbrains/mps/nodefs/NodeVirtualFileSystem.java) · [Compared upstream file](C:/Codex/mps-j2cl/mps/workbench/mps-platform/source/jetbrains/mps/nodefs/NodeVirtualFileSystem.java)

**Classification:** Project services node handles and undo. Text delta: +49 / −641 lines; 668 → 76 total lines.

**What changes:** Replaces global/desktop VFS listeners, repository file adapters and change notifications with identity-scoped repository maps, node-reference handles, path lookup, refresh and releaseRepository/disposal.

**Behavior and limits:** Only node handles are supplied; full desktop filesystem and model-file functionality is outside this subset. Paths identify deployed handles rather than durable storage.

**To make the replacement unnecessary:** Separate node identity from desktop VirtualFileSystem upstream. A browser host can own a small handle registry; implementing a full IDE VFS is not required for projectional editing.

Representative excerpts from the compared files; the prose above covers additional changes.

Pinned upstream at line 66

```java

public final class NodeVirtualFileSystem extends VirtualFileSystem implements Disposable {
  /**
   * Custom notification topic for events from this FS. We use it instead of VirtualFileManager.VFS_CHANGES to deal with
   * {@link com.intellij.openapi.command.impl.FileUndoProvider} invalidating undo actions for any beforeFileDeleted event from an FS which is not
   * backed up by LocalHistory (see {@code FileUndoProvider#beforeFileDeletion()} and {@code FileUndoProvider#shouldProcess()}.
   * <p>
   * The drawback of custom dispatch is that IDEA in general would not know about changes in this FS, however, this was the case anyway when
   * the class has been DeprecatedVirtualFileSystem subclass (which did send out VirtualFileListener events for listeners that we explicitly added to the FS ,
   * but did not publish anything to VFS_CHANGES topic)
   * The benefit is that our solution is independent from IDEA.
   * <p>
```

Browser replacement at line 53

```java

  public void releaseRepository(SRepository repository) {
    Map<SNodeReference, MPSNodeVirtualFile> files = repositories.remove(repository);
    if (files == null) return;
    for (MPSNodeVirtualFile file : files.values()) {
      paths.remove(file.getPath());
      file.invalidate();
    }
  }

  @Override public String getProtocol() { return "mps"; }
  @Override public VirtualFile findFileByPath(String path) { checkAlive(); return paths.get(path); }
```

### F114 MPSProject.java

**Path:** `workbench/mps-platform/source/jetbrains/mps/project/MPSProject.java`

[Browser file](C:/Codex/mps-j2cl/mps-web/workbench/mps-platform/source/jetbrains/mps/project/MPSProject.java) · [Compared upstream file](C:/Codex/mps-j2cl/mps/workbench/mps-platform/source/jetbrains/mps/project/MPSProject.java)

**Classification:** Project services node handles and undo. Text delta: +66 / −204 lines; 231 → 93 total lines.

**What changes:** Builds a real ProjectRepository with installed BrowserModelAccess and injected platform components, explicitly opens/closes project manager state and disposes model access/undo/node handles. Removes VFS watchers, filesystem conflict/VCS bridges and desktop opening; adds an explicit filesystem setter.

**Behavior and limits:** open(path) and nonempty desktop reconciliation throw. getFileSystem requires a provider. save delegates to the host project and does not prove browser persistence. The filesystem return type broadens from IdeaFileSystem to IFileSystem.

**To make the replacement unnecessary:** Upstream an IDE-independent project host interface covering repository, services, storage and lifecycle, with desktop/browser implementations. Implement persistence as a separate provider before promising reopen/save behavior.

Representative excerpts from the compared files; the prose above covers additional changes.

Pinned upstream at line 64

```java
  // model lock object and executeCommand* implementations, we won't need this WMA parameter
  public MPSProject(@NotNull com.intellij.openapi.project.Project project) {
    super(project.getName(), MPSCoreComponents.getInstance().getPlatform(), false);
    myProject = project;
    myProjectFileSystem = IdeaFileSystem.getInstance();
    myLifecycleEvents = new LifecycleEventDispatch(this);
    project.getService(ProjectRootListenerComponent.class).boostProjectRead(myProjectFileSystem);
    Platform platform = MPSCoreComponents.getInstance().getPlatform();
    final MPSModuleRepository extRepo = platform.findComponent(MPSModuleRepository.class);
    final SRepositoryRegistry registry = platform.findComponent(SRepositoryRegistry.class);
    final ModelAccess projectMA = WorkbenchModelAccess.getInstance().createForProject(MPSProject.this);
    final ProjectRepository repo = new ProjectRepository(this, extRepo, registry, projectMA);
```

Browser replacement at line 25

```java
    this.project = project;
    modelAccess = new jetbrains.mps.smodel.ProjectModelAccess2(this, jetbrains.mps.smodel.BrowserModelAccess.getInstalled());
    ProjectRepository repository = new ProjectRepository(this,
        myPlatform.findComponent(jetbrains.mps.smodel.MPSModuleRepository.class),
        myPlatform.findComponent(jetbrains.mps.extapi.module.SRepositoryRegistry.class), modelAccess);
    repository.init();
    initRepository(repository);
  }

  @Override public void projectOpened() {
    if (isDisposed()) throw new IllegalStateException("Project is disposed");
    if (opened) return;
```

### F115 ProjectModelAccess2.java

**Path:** `workbench/mps-platform/source/jetbrains/mps/smodel/ProjectModelAccess2.java`

[Browser file](C:/Codex/mps-j2cl/mps-web/workbench/mps-platform/source/jetbrains/mps/smodel/ProjectModelAccess2.java) · [Compared upstream file](C:/Codex/mps-j2cl/mps/workbench/mps-platform/source/jetbrains/mps/smodel/ProjectModelAccess2.java)

**Classification:** Scheduling model access and invalidation. Text delta: +7 / −8 lines; 196 → 195 total lines.

**What changes:** Makes ProjectModelAccess2 and its constructor public, replaces WorkbenchModelAccess with BrowserModelAccess, removes cancelReads calls, adapts command calls to the host project and adds undo-handler disposal.

**Behavior and limits:** Model-command context and ordering remain. This binds the project to a specific browser access implementation; merely changing concurrency libraries does not remove that coupling.

**To make the replacement unnecessary:** Introduce a common model-access/command service upstream and inject JVM/browser implementations through the same constructor. Preserve nested commands, read/write scopes and undo disposal.

Complete textual diff

```diff
--- mps/workbench/mps-platform/source/jetbrains/mps/smodel/ProjectModelAccess2.java
+++ mps-web/workbench/mps-platform/source/jetbrains/mps/smodel/ProjectModelAccess2.java
@@ -29,14 +29,14 @@
  * @author Artem Tikhomirov
  * @since 2018.3
  */
-final class ProjectModelAccess2 extends ModelAccessBase implements ModelCommandContext.Provider {
+public final class ProjectModelAccess2 extends ModelAccessBase implements ModelCommandContext.Provider {
   private final MPSProject myProject;
-  private final WorkbenchModelAccess mySharedImpl;
+  private final BrowserModelAccess mySharedImpl;
   private final CommandContextProvider myCommandContextProvider = new CommandContextProvider();
   private final ActionDispatcher<CommandListener> myCommandActionDispatcher;
   private final WorkbenchUndoHandler myUndoHandler;
 
-  /*package*/ ProjectModelAccess2(MPSProject mpsProject, WorkbenchModelAccess sharedImpl) {
+  public ProjectModelAccess2(MPSProject mpsProject, BrowserModelAccess sharedImpl) {
     super(sharedImpl, false);
     myProject = mpsProject;
     mySharedImpl = sharedImpl;
@@ -56,10 +56,11 @@
     }, CommandListener::commandStarted, CommandListener::commandFinished);
   }
 
+  public void dispose() { myUndoHandler.dispose(); }
+
   @Override
   public void executeCommand(Runnable r) {
 
-    mySharedImpl.cancelReads();
 
     if (canRead() && !canWrite()) {
       // This check is to avoid deadlocks like in https://youtrack.jetbrains.com/issue/MPS-31083
@@ -98,14 +99,13 @@
         throw new IllegalModelAccessException("Do not start a new command while start/finish notification for another command is in process");
       }
     }
-    mySharedImpl.executeCommand_(cmd, myProject, undoDescription);
+    mySharedImpl.executeCommand_(cmd, myProject.getProject(), undoDescription);
   }
 
   @Override
   public void executeCommandInEDT(Runnable r) {
-    mySharedImpl.cancelReads(); // see mySharedImpl.runWriteInEDT for reasons
 
-    mySharedImpl.runCommandInEDT_(wrapTopCommandRunnable(r, myProject), myProject, r instanceof UndoRunnable ? (UndoRunnable) r : null);
+    mySharedImpl.runCommandInEDT_(wrapTopCommandRunnable(r, myProject), myProject.getProject(), r instanceof UndoRunnable ? (UndoRunnable) r : null);
   }
 
   @Override
@@ -114,7 +114,6 @@
       // XXX why not, except for the newly introduced wrapTopCommandRunnable() limitation of nested commands?
       throw new IllegalStateException("undo transparent action cannot be invoked in a command");
     }
-    mySharedImpl.cancelReads();
     mySharedImpl.runUndoTransparentCommand(wrapTopCommandRunnable(r, myProject));
   }
 
```

### F116 BaseAction.java

**Path:** `workbench/mps-platform/source/jetbrains/mps/workbench/action/BaseAction.java`

[Browser file](C:/Codex/mps-j2cl/mps-web/workbench/mps-platform/source/jetbrains/mps/workbench/action/BaseAction.java) · [Compared upstream file](C:/Codex/mps-j2cl/mps/workbench/mps-platform/source/jetbrains/mps/workbench/action/BaseAction.java)

**Classification:** Project services node handles and undo. Text delta: +2 / −4 lines; 369 → 367 total lines.

**What changes:** Removes lookup of MakeServiceComponent and returns false from the active-make-session check.

**Behavior and limits:** Action dispatch remains; the browser profile has no JVM make service. If compilation/deployment is added, actions will need an explicit busy policy.

**To make the replacement unnecessary:** Inject a host activity predicate upstream, as the highlighter already allows, or separate make-sensitive action logic into a desktop provider. Returning false is a profile assumption, not general make-service support.

Complete textual diff

```diff
--- mps/workbench/mps-platform/source/jetbrains/mps/workbench/action/BaseAction.java
+++ mps-web/workbench/mps-platform/source/jetbrains/mps/workbench/action/BaseAction.java
@@ -34,7 +34,6 @@
 import jetbrains.mps.ide.actions.MPSCommonDataKeys;
 import jetbrains.mps.ide.project.ProjectHelper;
 import jetbrains.mps.logging.Logger;
-import jetbrains.mps.make.MakeServiceComponent;
 import jetbrains.mps.workbench.ActionPlace;
 import org.jetbrains.annotations.NotNull;
 import org.jetbrains.annotations.Nullable;
@@ -332,9 +331,8 @@
   protected abstract void doExecute(AnActionEvent e, Map<String, Object> params);
 
   protected final boolean isMakeSessionActive() {
-    final Platform mpsPlaf = MPSCoreComponents.getInstance().getPlatform();
-    final MakeServiceComponent makeService = mpsPlaf.findComponent(MakeServiceComponent.class);
-    return makeService != null && makeService.isSessionActive();
+    // The selected browser platform contains no JVM make service.
+    return false;
   }
 
   // this method is protected to help complex actions that may grab model write/command later
```

