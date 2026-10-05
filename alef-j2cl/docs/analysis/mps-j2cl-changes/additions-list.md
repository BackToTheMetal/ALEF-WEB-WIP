# Complete browser and compatibility additions

These files provide the browser host or external platform APIs. They are separate from replacements of JetBrains MPS source. Scope labels distinguish shared editor support from the financial demonstration and ALEF-specific deployment copies.

## Browser support classes

| File | Scope | Purpose |
| --- | --- | --- |
| [browser/financial/BrowserModelReader.java](<C:/Codex/mps-j2cl/browser/financial/BrowserModelReader.java>) | financial demo deployment; ALEF has separate deployment copies where listed | Loads XML9 model data into the browser MPS repository. |
| [browser/financial/FinancialClassLoader.java](<C:/Codex/mps-j2cl/browser/financial/FinancialClassLoader.java>) | financial demo deployment; ALEF has separate deployment copies where listed | Constructs a per-language ModuleClassLoader for registering compiled classes and typed factories. |
| [browser/financial/FinancialDemo.java](<C:/Codex/mps-j2cl/browser/financial/FinancialDemo.java>) | financial demo deployment; ALEF has separate deployment copies where listed | Financial calculator browser demonstration and its editor host. |
| [browser/financial/FinancialLanguages.java](<C:/Codex/mps-j2cl/browser/financial/FinancialLanguages.java>) | financial demo deployment; ALEF has separate deployment copies where listed | Generated language and aspect deployment registration for the financial sample. |
| [browser/financial/PrimitiveModelResource.java](<C:/Codex/mps-j2cl/browser/financial/PrimitiveModelResource.java>) | financial demo deployment; ALEF has separate deployment copies where listed | Embeds the base-language primitive model as a deployable browser resource. |
| [browser/financial/RuntimeTypesLanguage.java](<C:/Codex/mps-j2cl/browser/financial/RuntimeTypesLanguage.java>) | financial demo deployment; ALEF has separate deployment copies where listed | Minimal runtime language supplying type-system meta-concepts. |
| [browser/src/jetbrains/mps/classloading/BrowserEditorClassLoader.java](<C:/Codex/mps-j2cl/browser/src/jetbrains/mps/classloading/BrowserEditorClassLoader.java>) | ALEF shared editor dependency | Registers compiled editor classes, factories, rule owners, and module resource metadata. |
| [browser/src/jetbrains/mps/core/platform/BrowserPlatform.java](<C:/Codex/mps-j2cl/browser/src/jetbrains/mps/core/platform/BrowserPlatform.java>) | ALEF shared editor dependency | Bootstraps selected MPS platform, checking, and type-system components without IntelliJ desktop startup. |
| [browser/src/jetbrains/mps/smodel/BrowserModelAccess.java](<C:/Codex/mps-j2cl/browser/src/jetbrains/mps/smodel/BrowserModelAccess.java>) | ALEF shared editor dependency | Owns model access and command context on the browser event loop; connects disposal and undo. |
| [browser/src/mpsj2cl/editor/BrowserEditorActionBindings.java](<C:/Codex/mps-j2cl/browser/src/mpsj2cl/editor/BrowserEditorActionBindings.java>) | ALEF shared editor dependency | Generated shortcut bindings from MPS action declarations to original workbench actions. |
| [browser/src/mpsj2cl/editor/BrowserEditorHost.java](<C:/Codex/mps-j2cl/browser/src/mpsj2cl/editor/BrowserEditorHost.java>) | ALEF shared editor dependency | Installs browser services, project, repository, editor, inspectors, actions, and their lifecycle. |
| [browser/src/mpsj2cl/editor/BrowserEditorShortcuts.java](<C:/Codex/mps-j2cl/browser/src/mpsj2cl/editor/BrowserEditorShortcuts.java>) | ALEF shared editor dependency | Focus-aware shortcut dispatch preserving action applicability, access, read-only state, and undo policy. |
| [browser/src/mpsj2cl/editor/BrowserMpsAssetData.java](<C:/Codex/mps-j2cl/browser/src/mpsj2cl/editor/BrowserMpsAssetData.java>) | ALEF shared editor dependency | Generated payloads for original MPS assets used by browser editor icons. |
| [browser/src/mpsj2cl/editor/BrowserMpsAssets.java](<C:/Codex/mps-j2cl/browser/src/mpsj2cl/editor/BrowserMpsAssets.java>) | ALEF shared editor dependency | Installs and releases the browser MPS asset registry. |
| [browser/src/mpsj2cl/editor/BrowserResourceIcon.java](<C:/Codex/mps-j2cl/browser/src/mpsj2cl/editor/BrowserResourceIcon.java>) | ALEF shared editor dependency | Paints registered MPS resources as browser-supported icons. |
| [browser/src/mpsj2cl/editor/BrowserSelectionFactories.java](<C:/Codex/mps-j2cl/browser/src/mpsj2cl/editor/BrowserSelectionFactories.java>) | ALEF shared editor dependency | Registers typed constructors for selections and filters without reflective construction. |
| [browser/src/mpsj2cl/editor/BrowserStyleRegistry.java](<C:/Codex/mps-j2cl/browser/src/mpsj2cl/editor/BrowserStyleRegistry.java>) | ALEF shared editor dependency | Registers compiled style attributes without JVM aspect discovery. |
| [j2cl-src/mps/browser/financial/FinancialClassLoader.java](<C:/Codex/alef-j2cl/j2cl-src/mps/browser/financial/FinancialClassLoader.java>) | ALEF language deployment | Unchanged copy of the shared helper. |
| [j2cl-src/mps/browser/financial/FinancialLanguages.java](<C:/Codex/alef-j2cl/j2cl-src/mps/browser/financial/FinancialLanguages.java>) | ALEF language deployment | Consumer version of FinancialLanguages.java; 2 added and 27 removed lines relative to shared helper. |
| [j2cl-src/mps/browser/financial/RuntimeTypesLanguage.java](<C:/Codex/alef-j2cl/j2cl-src/mps/browser/financial/RuntimeTypesLanguage.java>) | ALEF language deployment | Unchanged copy of the shared helper. |


## IntelliJ compatibility classes

The shared dependency contains 276 Java source files implementing the selected IntelliJ-facing API surface. ALEF overrides 4 and adds 1, giving 277 selected files. These are authored compatibility implementations, interfaces, enums and annotations; the directory name intellij-stubs does not imply that every implementation is empty. It is not the complete IntelliJ platform.

Shared implementations cover application/project services, disposal, synchronous message bus, action data, command/undo history, clipboard, scheduling, popups, fonts and text utilities. The source list below is exhaustive; the source links show each class and its implemented members. Historical test claims in the dependency manifest were not rerun for this inventory.

### com.intellij.codeInsight

| Type and effective source | Provider |
| --- | --- |
| [com.intellij.codeInsight.CodeInsightSettings](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/codeInsight/CodeInsightSettings.java>) | Shared compatibility |
| [com.intellij.codeInsight.documentation.DocFontSizePopup](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/codeInsight/documentation/DocFontSizePopup.java>) | Shared compatibility |
| [com.intellij.codeInsight.documentation.DocumentationFontSize](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/codeInsight/documentation/DocumentationFontSize.java>) | Shared compatibility |
| [com.intellij.codeInsight.hint.HintManagerImpl](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/codeInsight/hint/HintManagerImpl.java>) | Shared compatibility |
| [com.intellij.codeInsight.hint.HintUtil](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/codeInsight/hint/HintUtil.java>) | Shared compatibility |
| [com.intellij.codeInsight.hint.LineTooltipRenderer](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/codeInsight/hint/LineTooltipRenderer.java>) | Shared compatibility |
| [com.intellij.codeInsight.hint.TooltipController](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/codeInsight/hint/TooltipController.java>) | Shared compatibility |
| [com.intellij.codeInsight.hint.TooltipGroup](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/codeInsight/hint/TooltipGroup.java>) | Shared compatibility |
| [com.intellij.codeInsight.hint.TooltipRenderer](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/codeInsight/hint/TooltipRenderer.java>) | Shared compatibility |


### com.intellij.compat

| Type and effective source | Provider |
| --- | --- |
| [com.intellij.compat.BrowserApplication](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/compat/BrowserApplication.java>) | Shared compatibility |
| [com.intellij.compat.BrowserComponentManager](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/compat/BrowserComponentManager.java>) | Shared compatibility |
| [com.intellij.compat.BrowserEdtExecutor](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/compat/BrowserEdtExecutor.java>) | Shared compatibility |
| [com.intellij.compat.BrowserIcons](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/compat/BrowserIcons.java>) | Shared compatibility |
| [com.intellij.compat.BrowserMessageBus](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/compat/BrowserMessageBus.java>) | Shared compatibility |
| [com.intellij.compat.BrowserProject](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/compat/BrowserProject.java>) | Shared compatibility |


### com.intellij.diagnostic

| Type and effective source | Provider |
| --- | --- |
| [com.intellij.diagnostic.StartUpPerformanceService](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/diagnostic/StartUpPerformanceService.java>) | Shared compatibility |


### com.intellij.execution

| Type and effective source | Provider |
| --- | --- |
| [com.intellij.execution.process.ProcessIOExecutorService](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/execution/process/ProcessIOExecutorService.java>) | Shared compatibility |


### com.intellij.featureStatistics

| Type and effective source | Provider |
| --- | --- |
| [com.intellij.featureStatistics.FeatureUsageTracker](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/featureStatistics/FeatureUsageTracker.java>) | Shared compatibility |


### com.intellij.icons

| Type and effective source | Provider |
| --- | --- |
| [com.intellij.icons.AllIcons](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/icons/AllIcons.java>) | Shared compatibility |


### com.intellij.ide

| Type and effective source | Provider |
| --- | --- |
| [com.intellij.ide.BrowserUtil](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/ide/BrowserUtil.java>) | Shared compatibility |
| [com.intellij.ide.CopyPasteManagerEx](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/ide/CopyPasteManagerEx.java>) | Shared compatibility |
| [com.intellij.ide.CopyProvider](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/ide/CopyProvider.java>) | Shared compatibility |
| [com.intellij.ide.CutProvider](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/ide/CutProvider.java>) | Shared compatibility |
| [com.intellij.ide.DataManager](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/ide/DataManager.java>) | Shared compatibility |
| [com.intellij.ide.HelpTooltipManager](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/ide/HelpTooltipManager.java>) | Shared compatibility |
| [com.intellij.ide.IdeBundle](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/ide/IdeBundle.java>) | Shared compatibility |
| [com.intellij.ide.IdeEventQueue](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/ide/IdeEventQueue.java>) | Shared compatibility |
| [com.intellij.ide.IdeTooltip](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/ide/IdeTooltip.java>) | Shared compatibility |
| [com.intellij.ide.IdeTooltipManager](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/ide/IdeTooltipManager.java>) | Shared compatibility |
| [com.intellij.ide.PasteProvider](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/ide/PasteProvider.java>) | Shared compatibility |
| [com.intellij.ide.PowerSaveMode](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/ide/PowerSaveMode.java>) | Shared compatibility |
| [com.intellij.ide.actions.RecentProjectsGroup](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/ide/actions/RecentProjectsGroup.java>) | Shared compatibility |
| [com.intellij.ide.lightEdit.LightEditCompatible](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/ide/lightEdit/LightEditCompatible.java>) | Shared compatibility |
| [com.intellij.ide.plugins.newui.EmptyCaret](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/ide/plugins/newui/EmptyCaret.java>) | Shared compatibility |
| [com.intellij.ide.ui.AntialiasingType](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/ide/ui/AntialiasingType.java>) | Shared compatibility |
| [com.intellij.ide.ui.UISettings](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/ide/ui/UISettings.java>) | Shared compatibility |
| [com.intellij.ide.ui.UISettingsListener](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/ide/ui/UISettingsListener.java>) | Shared compatibility |
| [com.intellij.ide.ui.customization.CustomActionsSchema](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/ide/ui/customization/CustomActionsSchema.java>) | Shared compatibility |


### com.intellij.lang

| Type and effective source | Provider |
| --- | --- |
| [com.intellij.lang.documentation.DocumentationMarkup](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/lang/documentation/DocumentationMarkup.java>) | Shared compatibility |


### com.intellij.notification

| Type and effective source | Provider |
| --- | --- |
| [com.intellij.notification.Notification](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/notification/Notification.java>) | Shared compatibility |
| [com.intellij.notification.NotificationType](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/notification/NotificationType.java>) | Shared compatibility |
| [com.intellij.notification.Notifications](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/notification/Notifications.java>) | Shared compatibility |


### com.intellij.openapi

| Type and effective source | Provider |
| --- | --- |
| [com.intellij.openapi.Disposable](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/Disposable.java>) | Shared compatibility |
| [com.intellij.openapi.actionSystem.ActionGroup](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/actionSystem/ActionGroup.java>) | Shared compatibility |
| [com.intellij.openapi.actionSystem.ActionInGroup](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/actionSystem/ActionInGroup.java>) | Shared compatibility |
| [com.intellij.openapi.actionSystem.ActionManager](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/actionSystem/ActionManager.java>) | Shared compatibility |
| [com.intellij.openapi.actionSystem.ActionPlaces](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/actionSystem/ActionPlaces.java>) | Shared compatibility |
| [com.intellij.openapi.actionSystem.ActionPopupMenu](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/actionSystem/ActionPopupMenu.java>) | Shared compatibility |
| [com.intellij.openapi.actionSystem.ActionToolbar](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/actionSystem/ActionToolbar.java>) | Shared compatibility |
| [com.intellij.openapi.actionSystem.ActionUiKind](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/actionSystem/ActionUiKind.java>) | Shared compatibility |
| [com.intellij.openapi.actionSystem.ActionUpdateThread](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/actionSystem/ActionUpdateThread.java>) | Shared compatibility |
| [com.intellij.openapi.actionSystem.AnAction](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/actionSystem/AnAction.java>) | Shared compatibility |
| [com.intellij.openapi.actionSystem.AnActionEvent](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/actionSystem/AnActionEvent.java>) | Shared compatibility |
| [com.intellij.openapi.actionSystem.Anchor](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/actionSystem/Anchor.java>) | Shared compatibility |
| [com.intellij.openapi.actionSystem.CommonDataKeys](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/actionSystem/CommonDataKeys.java>) | Shared compatibility |
| [com.intellij.openapi.actionSystem.Constraints](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/actionSystem/Constraints.java>) | Shared compatibility |
| [com.intellij.openapi.actionSystem.CustomShortcutSet](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/actionSystem/CustomShortcutSet.java>) | Shared compatibility |
| [com.intellij.openapi.actionSystem.CustomizedDataContext](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/actionSystem/CustomizedDataContext.java>) | Shared compatibility |
| [com.intellij.openapi.actionSystem.DataContext](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/actionSystem/DataContext.java>) | Shared compatibility |
| [com.intellij.openapi.actionSystem.DataKey](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/actionSystem/DataKey.java>) | Shared compatibility |
| [com.intellij.openapi.actionSystem.DataMap](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/actionSystem/DataMap.java>) | Shared compatibility |
| [com.intellij.openapi.actionSystem.DataProvider](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/actionSystem/DataProvider.java>) | Shared compatibility |
| [com.intellij.openapi.actionSystem.DataSink](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/actionSystem/DataSink.java>) | Shared compatibility |
| [com.intellij.openapi.actionSystem.DataSnapshotProvider](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/actionSystem/DataSnapshotProvider.java>) | Shared compatibility |
| [com.intellij.openapi.actionSystem.DefaultActionGroup](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/actionSystem/DefaultActionGroup.java>) | Shared compatibility |
| [com.intellij.openapi.actionSystem.IdeActions](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/actionSystem/IdeActions.java>) | Shared compatibility |
| [com.intellij.openapi.actionSystem.KeyboardShortcut](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/actionSystem/KeyboardShortcut.java>) | Shared compatibility |
| [com.intellij.openapi.actionSystem.PlatformDataKeys](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/actionSystem/PlatformDataKeys.java>) | Shared compatibility |
| [com.intellij.openapi.actionSystem.Presentation](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/actionSystem/Presentation.java>) | Shared compatibility |
| [com.intellij.openapi.actionSystem.Separator](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/actionSystem/Separator.java>) | Shared compatibility |
| [com.intellij.openapi.actionSystem.Shortcut](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/actionSystem/Shortcut.java>) | Shared compatibility |
| [com.intellij.openapi.actionSystem.ShortcutSet](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/actionSystem/ShortcutSet.java>) | Shared compatibility |
| [com.intellij.openapi.actionSystem.ToggleAction](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/actionSystem/ToggleAction.java>) | Shared compatibility |
| [com.intellij.openapi.actionSystem.UiDataProvider](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/actionSystem/UiDataProvider.java>) | Shared compatibility |
| [com.intellij.openapi.actionSystem.ex.ActionManagerEx](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/actionSystem/ex/ActionManagerEx.java>) | Shared compatibility |
| [com.intellij.openapi.actionSystem.ex.ActionUtil](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/actionSystem/ex/ActionUtil.java>) | Shared compatibility |
| [com.intellij.openapi.actionSystem.impl.ActionButton](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/actionSystem/impl/ActionButton.java>) | Shared compatibility |
| [com.intellij.openapi.actionSystem.impl.SimpleDataContext](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/actionSystem/impl/SimpleDataContext.java>) | Shared compatibility |
| [com.intellij.openapi.application.Application](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/application/Application.java>) | Shared compatibility |
| [com.intellij.openapi.application.ApplicationListener](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/application/ApplicationListener.java>) | Shared compatibility |
| [com.intellij.openapi.application.ApplicationManager](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/application/ApplicationManager.java>) | Shared compatibility |
| [com.intellij.openapi.application.ModalityState](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/application/ModalityState.java>) | Shared compatibility |
| [com.intellij.openapi.command.CommandEvent](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/command/CommandEvent.java>) | Shared compatibility |
| [com.intellij.openapi.command.CommandListener](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/command/CommandListener.java>) | Shared compatibility |
| [com.intellij.openapi.command.CommandProcessor](<C:/Codex/alef-j2cl/j2cl-src/intellij/com/intellij/openapi/command/CommandProcessor.java>) | ALEF override |
| [com.intellij.openapi.command.UndoConfirmationPolicy](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/command/UndoConfirmationPolicy.java>) | Shared compatibility |
| [com.intellij.openapi.command.undo.BasicUndoableAction](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/command/undo/BasicUndoableAction.java>) | Shared compatibility |
| [com.intellij.openapi.command.undo.DocumentReference](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/command/undo/DocumentReference.java>) | Shared compatibility |
| [com.intellij.openapi.command.undo.UndoManager](<C:/Codex/alef-j2cl/j2cl-src/intellij/com/intellij/openapi/command/undo/UndoManager.java>) | ALEF override |
| [com.intellij.openapi.command.undo.UndoableAction](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/command/undo/UndoableAction.java>) | Shared compatibility |
| [com.intellij.openapi.command.undo.UnexpectedUndoException](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/command/undo/UnexpectedUndoException.java>) | Shared compatibility |
| [com.intellij.openapi.components.ApplicationComponent](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/components/ApplicationComponent.java>) | Shared compatibility |
| [com.intellij.openapi.components.BaseComponent](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/components/BaseComponent.java>) | Shared compatibility |
| [com.intellij.openapi.components.ComponentManager](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/components/ComponentManager.java>) | Shared compatibility |
| [com.intellij.openapi.components.PersistentStateComponent](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/components/PersistentStateComponent.java>) | Shared compatibility |
| [com.intellij.openapi.components.ProjectComponent](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/components/ProjectComponent.java>) | Shared compatibility |
| [com.intellij.openapi.components.RoamingType](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/components/RoamingType.java>) | Shared compatibility |
| [com.intellij.openapi.components.State](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/components/State.java>) | Shared compatibility |
| [com.intellij.openapi.components.Storage](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/components/Storage.java>) | Shared compatibility |
| [com.intellij.openapi.components.StoragePathMacros](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/components/StoragePathMacros.java>) | Shared compatibility |
| [com.intellij.openapi.diagnostic.Logger](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/diagnostic/Logger.java>) | Shared compatibility |
| [com.intellij.openapi.editor.Document](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/editor/Document.java>) | Shared compatibility |
| [com.intellij.openapi.editor.Editor](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/editor/Editor.java>) | Shared compatibility |
| [com.intellij.openapi.editor.EditorCoreUtil](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/editor/EditorCoreUtil.java>) | Shared compatibility |
| [com.intellij.openapi.editor.LogicalPosition](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/editor/LogicalPosition.java>) | Shared compatibility |
| [com.intellij.openapi.editor.ScrollType](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/editor/ScrollType.java>) | Shared compatibility |
| [com.intellij.openapi.editor.ScrollingModel](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/editor/ScrollingModel.java>) | Shared compatibility |
| [com.intellij.openapi.editor.colors.CodeInsightColors](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/editor/colors/CodeInsightColors.java>) | Shared compatibility |
| [com.intellij.openapi.editor.colors.ColorKey](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/editor/colors/ColorKey.java>) | Shared compatibility |
| [com.intellij.openapi.editor.colors.EditorColors](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/editor/colors/EditorColors.java>) | Shared compatibility |
| [com.intellij.openapi.editor.colors.EditorColorsListener](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/editor/colors/EditorColorsListener.java>) | Shared compatibility |
| [com.intellij.openapi.editor.colors.EditorColorsManager](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/editor/colors/EditorColorsManager.java>) | Shared compatibility |
| [com.intellij.openapi.editor.colors.EditorColorsScheme](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/editor/colors/EditorColorsScheme.java>) | Shared compatibility |
| [com.intellij.openapi.editor.colors.EditorColorsUtil](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/editor/colors/EditorColorsUtil.java>) | Shared compatibility |
| [com.intellij.openapi.editor.colors.FontPreferences](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/editor/colors/FontPreferences.java>) | Shared compatibility |
| [com.intellij.openapi.editor.colors.ModifiableFontPreferences](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/editor/colors/ModifiableFontPreferences.java>) | Shared compatibility |
| [com.intellij.openapi.editor.colors.TextAttributesKey](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/editor/colors/TextAttributesKey.java>) | Shared compatibility |
| [com.intellij.openapi.editor.event.VisibleAreaEvent](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/editor/event/VisibleAreaEvent.java>) | Shared compatibility |
| [com.intellij.openapi.editor.event.VisibleAreaListener](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/editor/event/VisibleAreaListener.java>) | Shared compatibility |
| [com.intellij.openapi.editor.ex.EditorSettingsExternalizable](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/editor/ex/EditorSettingsExternalizable.java>) | Shared compatibility |
| [com.intellij.openapi.editor.ex.util.EditorUtil](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/editor/ex/util/EditorUtil.java>) | Shared compatibility |
| [com.intellij.openapi.editor.impl.EditorCssFontResolver](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/editor/impl/EditorCssFontResolver.java>) | Shared compatibility |
| [com.intellij.openapi.editor.impl.EditorHeaderComponent](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/editor/impl/EditorHeaderComponent.java>) | Shared compatibility |
| [com.intellij.openapi.editor.impl.EditorImpl](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/editor/impl/EditorImpl.java>) | Shared compatibility |
| [com.intellij.openapi.editor.impl.FontInfo](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/editor/impl/FontInfo.java>) | Shared compatibility |
| [com.intellij.openapi.editor.impl.view.FontLayoutService](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/editor/impl/view/FontLayoutService.java>) | Shared compatibility |
| [com.intellij.openapi.editor.markup.EffectType](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/editor/markup/EffectType.java>) | Shared compatibility |
| [com.intellij.openapi.editor.markup.TextAttributes](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/editor/markup/TextAttributes.java>) | Shared compatibility |
| [com.intellij.openapi.extensions.AreaInstance](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/extensions/AreaInstance.java>) | Shared compatibility |
| [com.intellij.openapi.extensions.ExtensionPointName](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/extensions/ExtensionPointName.java>) | Shared compatibility |
| [com.intellij.openapi.extensions.PluginId](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/extensions/PluginId.java>) | Shared compatibility |
| [com.intellij.openapi.fileEditor.FileEditor](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/fileEditor/FileEditor.java>) | Shared compatibility |
| [com.intellij.openapi.fileEditor.FileEditorManager](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/fileEditor/FileEditorManager.java>) | Shared compatibility |
| [com.intellij.openapi.keymap.Keymap](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/keymap/Keymap.java>) | Shared compatibility |
| [com.intellij.openapi.keymap.KeymapManager](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/keymap/KeymapManager.java>) | Shared compatibility |
| [com.intellij.openapi.keymap.KeymapUtil](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/keymap/KeymapUtil.java>) | Shared compatibility |
| [com.intellij.openapi.options.FontSize](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/options/FontSize.java>) | Shared compatibility |
| [com.intellij.openapi.progress.EmptyProgressIndicator](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/progress/EmptyProgressIndicator.java>) | Shared compatibility |
| [com.intellij.openapi.progress.ProcessCanceledException](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/progress/ProcessCanceledException.java>) | Shared compatibility |
| [com.intellij.openapi.progress.ProgressIndicator](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/progress/ProgressIndicator.java>) | Shared compatibility |
| [com.intellij.openapi.progress.ProgressManager](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/progress/ProgressManager.java>) | Shared compatibility |
| [com.intellij.openapi.progress.util.ProgressIndicatorBase](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/progress/util/ProgressIndicatorBase.java>) | Shared compatibility |
| [com.intellij.openapi.project.DumbAware](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/project/DumbAware.java>) | Shared compatibility |
| [com.intellij.openapi.project.DumbAwareAction](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/project/DumbAwareAction.java>) | Shared compatibility |
| [com.intellij.openapi.project.DumbService](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/project/DumbService.java>) | Shared compatibility |
| [com.intellij.openapi.project.IndexNotReadyException](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/project/IndexNotReadyException.java>) | Shared compatibility |
| [com.intellij.openapi.project.Project](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/project/Project.java>) | Shared compatibility |
| [com.intellij.openapi.ui.JBPopupMenu](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/ui/JBPopupMenu.java>) | Shared compatibility |
| [com.intellij.openapi.ui.MessageType](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/ui/MessageType.java>) | Shared compatibility |
| [com.intellij.openapi.ui.Messages](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/ui/Messages.java>) | Shared compatibility |
| [com.intellij.openapi.ui.popup.Balloon](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/ui/popup/Balloon.java>) | Shared compatibility |
| [com.intellij.openapi.ui.popup.BalloonBuilder](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/ui/popup/BalloonBuilder.java>) | Shared compatibility |
| [com.intellij.openapi.ui.popup.ComponentPopupBuilder](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/ui/popup/ComponentPopupBuilder.java>) | Shared compatibility |
| [com.intellij.openapi.ui.popup.JBPopup](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/ui/popup/JBPopup.java>) | Shared compatibility |
| [com.intellij.openapi.ui.popup.JBPopupFactory](<C:/Codex/alef-j2cl/j2cl-src/intellij/com/intellij/openapi/ui/popup/JBPopupFactory.java>) | ALEF override |
| [com.intellij.openapi.ui.popup.ListPopup](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/ui/popup/ListPopup.java>) | Shared compatibility |
| [com.intellij.openapi.ui.popup.ListPopupStep](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/ui/popup/ListPopupStep.java>) | Shared compatibility |
| [com.intellij.openapi.ui.popup.ListSeparator](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/ui/popup/ListSeparator.java>) | Shared compatibility |
| [com.intellij.openapi.ui.popup.MnemonicNavigationFilter](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/ui/popup/MnemonicNavigationFilter.java>) | Shared compatibility |
| [com.intellij.openapi.ui.popup.PopupStep](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/ui/popup/PopupStep.java>) | Shared compatibility |
| [com.intellij.openapi.ui.popup.SpeedSearchFilter](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/ui/popup/SpeedSearchFilter.java>) | Shared compatibility |
| [com.intellij.openapi.ui.popup.util.BaseListPopupStep](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/ui/popup/util/BaseListPopupStep.java>) | Shared compatibility |
| [com.intellij.openapi.util.CheckedDisposable](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/util/CheckedDisposable.java>) | Shared compatibility |
| [com.intellij.openapi.util.Computable](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/util/Computable.java>) | Shared compatibility |
| [com.intellij.openapi.util.Condition](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/util/Condition.java>) | Shared compatibility |
| [com.intellij.openapi.util.Disposer](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/util/Disposer.java>) | Shared compatibility |
| [com.intellij.openapi.util.IconLoader](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/util/IconLoader.java>) | Shared compatibility |
| [com.intellij.openapi.util.InvalidDataException](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/util/InvalidDataException.java>) | Shared compatibility |
| [com.intellij.openapi.util.Key](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/util/Key.java>) | Shared compatibility |
| [com.intellij.openapi.util.NlsActions](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/util/NlsActions.java>) | Shared compatibility |
| [com.intellij.openapi.util.NlsSafe](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/util/NlsSafe.java>) | Shared compatibility |
| [com.intellij.openapi.util.Pair](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/util/Pair.java>) | Shared compatibility |
| [com.intellij.openapi.util.Ref](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/util/Ref.java>) | Shared compatibility |
| [com.intellij.openapi.util.ScalableIcon](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/util/ScalableIcon.java>) | Shared compatibility |
| [com.intellij.openapi.util.SystemInfo](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/util/SystemInfo.java>) | Shared compatibility |
| [com.intellij.openapi.util.TextRange](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/util/TextRange.java>) | Shared compatibility |
| [com.intellij.openapi.util.ThrowableComputable](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/util/ThrowableComputable.java>) | Shared compatibility |
| [com.intellij.openapi.util.UserDataHolder](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/util/UserDataHolder.java>) | Shared compatibility |
| [com.intellij.openapi.util.UserDataHolderBase](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/util/UserDataHolderBase.java>) | Shared compatibility |
| [com.intellij.openapi.util.registry.Registry](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/util/registry/Registry.java>) | Shared compatibility |
| [com.intellij.openapi.util.text.HtmlBuilder](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/util/text/HtmlBuilder.java>) | Shared compatibility |
| [com.intellij.openapi.util.text.HtmlChunk](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/util/text/HtmlChunk.java>) | Shared compatibility |
| [com.intellij.openapi.util.text.StringUtil](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/util/text/StringUtil.java>) | Shared compatibility |
| [com.intellij.openapi.util.text.Strings](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/util/text/Strings.java>) | Shared compatibility |
| [com.intellij.openapi.vfs.VirtualFile](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/vfs/VirtualFile.java>) | Shared compatibility |
| [com.intellij.openapi.vfs.VirtualFileManager](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/vfs/VirtualFileManager.java>) | Shared compatibility |
| [com.intellij.openapi.vfs.VirtualFileSystem](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/vfs/VirtualFileSystem.java>) | Shared compatibility |
| [com.intellij.openapi.wm.IdeFocusManager](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/wm/IdeFocusManager.java>) | Shared compatibility |
| [com.intellij.openapi.wm.IdeFrame](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/wm/IdeFrame.java>) | Shared compatibility |
| [com.intellij.openapi.wm.IdeGlassPane](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/wm/IdeGlassPane.java>) | Shared compatibility |
| [com.intellij.openapi.wm.StatusBar](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/wm/StatusBar.java>) | Shared compatibility |
| [com.intellij.openapi.wm.ToolWindow](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/wm/ToolWindow.java>) | Shared compatibility |
| [com.intellij.openapi.wm.ToolWindowManager](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/wm/ToolWindowManager.java>) | Shared compatibility |
| [com.intellij.openapi.wm.WindowManager](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/wm/WindowManager.java>) | Shared compatibility |
| [com.intellij.openapi.wm.ex.StatusBarEx](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/wm/ex/StatusBarEx.java>) | Shared compatibility |
| [com.intellij.openapi.wm.ex.ToolWindowEx](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/wm/ex/ToolWindowEx.java>) | Shared compatibility |
| [com.intellij.openapi.wm.impl.IdeRootPane](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/wm/impl/IdeRootPane.java>) | Shared compatibility |


### com.intellij.psi

| Type and effective source | Provider |
| --- | --- |
| [com.intellij.psi.codeStyle.MinusculeMatcher](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/psi/codeStyle/MinusculeMatcher.java>) | Shared compatibility |
| [com.intellij.psi.codeStyle.NameUtil](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/psi/codeStyle/NameUtil.java>) | Shared compatibility |


### com.intellij.reference

| Type and effective source | Provider |
| --- | --- |
| [com.intellij.reference.SoftReference](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/reference/SoftReference.java>) | Shared compatibility |


### com.intellij.ui

| Type and effective source | Provider |
| --- | --- |
| [com.intellij.ui.CollectionListModel](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/ui/CollectionListModel.java>) | Shared compatibility |
| [com.intellij.ui.ColorUtil](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/ui/ColorUtil.java>) | Shared compatibility |
| [com.intellij.ui.DarculaColors](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/ui/DarculaColors.java>) | Shared compatibility |
| [com.intellij.ui.DirtyUI](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/ui/DirtyUI.java>) | Shared compatibility |
| [com.intellij.ui.Gray](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/ui/Gray.java>) | Shared compatibility |
| [com.intellij.ui.HintHint](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/ui/HintHint.java>) | Shared compatibility |
| [com.intellij.ui.HyperlinkLabel](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/ui/HyperlinkLabel.java>) | Shared compatibility |
| [com.intellij.ui.IconManager](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/ui/IconManager.java>) | Shared compatibility |
| [com.intellij.ui.IdeBorderFactory](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/ui/IdeBorderFactory.java>) | Shared compatibility |
| [com.intellij.ui.JBColor](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/ui/JBColor.java>) | Shared compatibility |
| [com.intellij.ui.LayeredIcon](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/ui/LayeredIcon.java>) | Shared compatibility |
| [com.intellij.ui.LightColors](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/ui/LightColors.java>) | Shared compatibility |
| [com.intellij.ui.LightweightHint](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/ui/LightweightHint.java>) | Shared compatibility |
| [com.intellij.ui.NewUI](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/ui/NewUI.java>) | Shared compatibility |
| [com.intellij.ui.ScrollPaneFactory](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/ui/ScrollPaneFactory.java>) | Shared compatibility |
| [com.intellij.ui.SideBorder](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/ui/SideBorder.java>) | Shared compatibility |
| [com.intellij.ui.SimpleColoredComponent](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/ui/SimpleColoredComponent.java>) | Shared compatibility |
| [com.intellij.ui.SimpleTextAttributes](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/ui/SimpleTextAttributes.java>) | Shared compatibility |
| [com.intellij.ui.UserActivityProviderComponent](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/ui/UserActivityProviderComponent.java>) | Shared compatibility |
| [com.intellij.ui.awt.RelativePoint](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/ui/awt/RelativePoint.java>) | Shared compatibility |
| [com.intellij.ui.components.Interpolable](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/ui/components/Interpolable.java>) | Shared compatibility |
| [com.intellij.ui.components.JBLayeredPane](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/ui/components/JBLayeredPane.java>) | Shared compatibility |
| [com.intellij.ui.components.JBList](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/ui/components/JBList.java>) | Shared compatibility |
| [com.intellij.ui.components.JBScrollBar](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/ui/components/JBScrollBar.java>) | Shared compatibility |
| [com.intellij.ui.components.JBScrollPane](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/ui/components/JBScrollPane.java>) | Shared compatibility |
| [com.intellij.ui.components.panels.NonOpaquePanel](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/ui/components/panels/NonOpaquePanel.java>) | Shared compatibility |
| [com.intellij.ui.content.Content](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/ui/content/Content.java>) | Shared compatibility |
| [com.intellij.ui.content.ContentFactory](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/ui/content/ContentFactory.java>) | Shared compatibility |
| [com.intellij.ui.content.ContentManager](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/ui/content/ContentManager.java>) | Shared compatibility |
| [com.intellij.ui.paint.LinePainter2D](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/ui/paint/LinePainter2D.java>) | Shared compatibility |
| [com.intellij.ui.popup.AbstractPopup](<C:/Codex/alef-j2cl/j2cl-src/intellij/com/intellij/ui/popup/AbstractPopup.java>) | ALEF override |
| [com.intellij.ui.popup.BrowserActionPopupStyle](<C:/Codex/alef-j2cl/j2cl-src/intellij/com/intellij/ui/popup/BrowserActionPopupStyle.java>) | ALEF addition |
| [com.intellij.ui.popup.ClosableByLeftArrow](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/ui/popup/ClosableByLeftArrow.java>) | Shared compatibility |
| [com.intellij.ui.scale.JBUIScale](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/ui/scale/JBUIScale.java>) | Shared compatibility |
| [com.intellij.ui.speedSearch.SpeedSearchUtil](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/ui/speedSearch/SpeedSearchUtil.java>) | Shared compatibility |


### com.intellij.unscramble

| Type and effective source | Provider |
| --- | --- |
| [com.intellij.unscramble.AnalyzeStacktraceUtil](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/unscramble/AnalyzeStacktraceUtil.java>) | Shared compatibility |


### com.intellij.util

| Type and effective source | Provider |
| --- | --- |
| [com.intellij.util.Alarm](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/util/Alarm.java>) | Shared compatibility |
| [com.intellij.util.AlarmFactory](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/util/AlarmFactory.java>) | Shared compatibility |
| [com.intellij.util.ArrayUtil](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/util/ArrayUtil.java>) | Shared compatibility |
| [com.intellij.util.ConcurrencyUtil](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/util/ConcurrencyUtil.java>) | Shared compatibility |
| [com.intellij.util.IconUtil](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/util/IconUtil.java>) | Shared compatibility |
| [com.intellij.util.IncorrectOperationException](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/util/IncorrectOperationException.java>) | Shared compatibility |
| [com.intellij.util.LocalTimeCounter](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/util/LocalTimeCounter.java>) | Shared compatibility |
| [com.intellij.util.ThrowableRunnable](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/util/ThrowableRunnable.java>) | Shared compatibility |
| [com.intellij.util.concurrency.EdtExecutorService](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/util/concurrency/EdtExecutorService.java>) | Shared compatibility |
| [com.intellij.util.concurrency.annotations.RequiresEdt](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/util/concurrency/annotations/RequiresEdt.java>) | Shared compatibility |
| [com.intellij.util.containers.ContainerUtil](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/util/containers/ContainerUtil.java>) | Shared compatibility |
| [com.intellij.util.containers.FList](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/util/containers/FList.java>) | Shared compatibility |
| [com.intellij.util.containers.SortedList](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/util/containers/SortedList.java>) | Shared compatibility |
| [com.intellij.util.io.URLUtil](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/util/io/URLUtil.java>) | Shared compatibility |
| [com.intellij.util.messages.MessageBus](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/util/messages/MessageBus.java>) | Shared compatibility |
| [com.intellij.util.messages.MessageBusConnection](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/util/messages/MessageBusConnection.java>) | Shared compatibility |
| [com.intellij.util.messages.SimpleMessageBusConnection](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/util/messages/SimpleMessageBusConnection.java>) | Shared compatibility |
| [com.intellij.util.messages.Topic](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/util/messages/Topic.java>) | Shared compatibility |
| [com.intellij.util.text.Matcher](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/util/text/Matcher.java>) | Shared compatibility |
| [com.intellij.util.ui.AbstractLayoutManager](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/util/ui/AbstractLayoutManager.java>) | Shared compatibility |
| [com.intellij.util.ui.ButtonlessScrollBarUI](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/util/ui/ButtonlessScrollBarUI.java>) | Shared compatibility |
| [com.intellij.util.ui.JBDimension](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/util/ui/JBDimension.java>) | Shared compatibility |
| [com.intellij.util.ui.JBUI](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/util/ui/JBUI.java>) | Shared compatibility |
| [com.intellij.util.ui.MouseEventAdapter](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/util/ui/MouseEventAdapter.java>) | Shared compatibility |
| [com.intellij.util.ui.StartupUiUtil](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/util/ui/StartupUiUtil.java>) | Shared compatibility |
| [com.intellij.util.ui.UIUtil](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/util/ui/UIUtil.java>) | Shared compatibility |
| [com.intellij.util.ui.accessibility.ScreenReader](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/util/ui/accessibility/ScreenReader.java>) | Shared compatibility |
| [com.intellij.util.ui.update.MergingUpdateQueue](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/util/ui/update/MergingUpdateQueue.java>) | Shared compatibility |
| [com.intellij.util.ui.update.Update](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/util/ui/update/Update.java>) | Shared compatibility |
| [com.intellij.util.xmlb.XmlSerializerUtil](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/util/xmlb/XmlSerializerUtil.java>) | Shared compatibility |
| [com.intellij.util.xmlb.annotations.AbstractCollection](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/util/xmlb/annotations/AbstractCollection.java>) | Shared compatibility |
| [com.intellij.util.xmlb.annotations.Attribute](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/util/xmlb/annotations/Attribute.java>) | Shared compatibility |
| [com.intellij.util.xmlb.annotations.CollectionBean](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/util/xmlb/annotations/CollectionBean.java>) | Shared compatibility |
| [com.intellij.util.xmlb.annotations.MapAnnotation](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/util/xmlb/annotations/MapAnnotation.java>) | Shared compatibility |
| [com.intellij.util.xmlb.annotations.OptionTag](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/util/xmlb/annotations/OptionTag.java>) | Shared compatibility |
| [com.intellij.util.xmlb.annotations.Property](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/util/xmlb/annotations/Property.java>) | Shared compatibility |
| [com.intellij.util.xmlb.annotations.Tag](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/util/xmlb/annotations/Tag.java>) | Shared compatibility |
| [com.intellij.util.xmlb.annotations.Text](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/util/xmlb/annotations/Text.java>) | Shared compatibility |
| [com.intellij.util.xmlb.annotations.Transient](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/util/xmlb/annotations/Transient.java>) | Shared compatibility |
| [com.intellij.util.xmlb.annotations.XCollection](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/util/xmlb/annotations/XCollection.java>) | Shared compatibility |
| [com.intellij.util.xmlb.annotations.XMap](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/util/xmlb/annotations/XMap.java>) | Shared compatibility |

