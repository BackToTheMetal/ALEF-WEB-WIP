package mpsj2cl.editor;

import com.intellij.compat.BrowserApplication;
import com.intellij.compat.BrowserProject;
import com.intellij.openapi.Disposable;
import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.UiDataProvider;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.undo.UndoManager;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.vfs.VirtualFileManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import jetbrains.mps.core.platform.BrowserPlatform;
import jetbrains.mps.core.platform.Platform;
import jetbrains.mps.classloading.BrowserEditorClassLoader;
import jetbrains.mps.classloading.ModuleClassLoader;
import jetbrains.mps.ide.MPSCoreComponents;
import jetbrains.mps.ide.datatransfer.AddRequiredImportsDialog;
import jetbrains.mps.ide.icons.GlobalIconManager;
import jetbrains.mps.ide.actions.MPSCommonDataKeys;
import jetbrains.mps.datatransfer.DataTransferManager;
import jetbrains.mps.findUsages.FindUsagesManager;
import jetbrains.mps.intentions.IntentionsManager;
import jetbrains.mps.nodeEditor.EditorSettings;
import jetbrains.mps.nodeEditor.Highlighter;
import jetbrains.mps.nodeEditor.NodeEditorComponent;
import jetbrains.mps.nodeEditor.SearchHistoryComponent;
import jetbrains.mps.ide.search.SearchHistoryStorage;
import jetbrains.mps.nodeEditor.hintsSettings.ConceptEditorHintSettingsComponent;
import jetbrains.mps.nodeEditor.cells.contextAssistant.ContextAssistantSettings;
import jetbrains.mps.nodeEditor.documentation.MPSDocumentationManager;
import jetbrains.mps.nodeEditor.documentation.MPSDocumentationToolWindowManager;
import jetbrains.mps.nodefs.NodeVirtualFileSystem;
import jetbrains.mps.project.MPSProject;
import jetbrains.mps.smodel.BrowserModelAccess;
import jetbrains.mps.smodel.language.LanguageRegistry;
import jetbrains.mps.typesystem.checking.TypesEditorChecker;
import jetbrains.mps.typesystem.checking.NonTypesystemEditorChecker;
import org.jetbrains.mps.openapi.model.SNode;

/** Owns a browser application, the real MPS platform, and one in-memory editor project. */
public final class BrowserEditorHost implements Disposable {
  private final Disposable applicationScope = Disposer.newDisposable("Browser editor application");
  private final List<NodeEditorComponent> editors = new ArrayList<>();
  private BrowserApplication application;
  private BrowserProject ideaProject;
  private BrowserModelAccess modelAccess;
  private BrowserPlatform platform;
  private LanguageRegistry languageRegistry;
  private BrowserMpsAssets assets;
  private BrowserStyleRegistry styles;
  private FindUsagesManager findUsages;
  private ModuleClassLoader selectionLoader;
  private Highlighter highlighter;
  private NodeVirtualFileSystem nodeFiles;
  private MPSProject project;
  private boolean disposed;

  public BrowserEditorHost(String name) {
    Objects.requireNonNull(name);
    try {
      application = new BrowserApplication();
      application.assertIsDispatchThread();
      // Acquire the exclusive model-access host before replacing any application globals.
      modelAccess = new BrowserModelAccess();
      ApplicationManager.setApplication(application, applicationScope);
      platform = new BrowserPlatform();
      languageRegistry = platform.findComponent(LanguageRegistry.class);
      FindUsagesManager searches = new FindUsagesManager();
      searches.init();
      findUsages = searches;
      application.registerService(FindUsagesManager.class, searches);
      application.registerService(org.jetbrains.mps.openapi.module.FindUsagesFacade.class, searches);
      application.registerService(Platform.class, platform);
      application.registerService(MPSCoreComponents.class, new MPSCoreComponents(platform));
      application.registerService(GlobalIconManager.class, new GlobalIconManager());
      assets = new BrowserMpsAssets();
      styles = new BrowserStyleRegistry();
      styles.init();
      selectionLoader = BrowserEditorClassLoader.create();
      BrowserSelectionFactories.registerCoreSelections(selectionLoader);
      application.registerService(EditorSettings.class, new EditorSettings());
      application.registerService(ContextAssistantSettings.class, new ContextAssistantSettings());
      application.registerService(IntentionsManager.class, new IntentionsManager());
      application.registerService(MPSDocumentationManager.class, new MPSDocumentationManager());
      nodeFiles = new NodeVirtualFileSystem();
      application.registerService(NodeVirtualFileSystem.class, nodeFiles);
      VirtualFileManager.getInstance().registerBrowserFileSystem(nodeFiles);
      ideaProject = new BrowserProject(name, "/browser-project", application);
      project = new MPSProject(ideaProject) {};
      ideaProject.registerService(MPSProject.class, project);
      ideaProject.registerService(ConceptEditorHintSettingsComponent.class, new ConceptEditorHintSettingsComponent(ideaProject));
      ideaProject.registerService(SearchHistoryStorage.class, new SearchHistoryComponent());
      // Browser paste preserves the fragment's required model/language imports automatically.
      ideaProject.registerService(AddRequiredImportsDialog.ImportPolicy.class,
          (owner, models, languages) -> new AddRequiredImportsDialog.Selection(models, languages));
      ideaProject.registerService(MPSDocumentationToolWindowManager.class, new MPSDocumentationToolWindowManager(ideaProject));
      project.projectOpened();
      highlighter = new Highlighter(ideaProject);
      ideaProject.registerService(Highlighter.class, highlighter);
      highlighter.addChecker(new TypesEditorChecker(project.getRepository(), java.util.Collections.emptyList()));
      highlighter.addChecker(new NonTypesystemEditorChecker(project.getRepository(), java.util.Collections.emptyList()));
    } catch (RuntimeException | Error failure) {
      try { dispose(); } catch (RuntimeException | Error cleanup) { failure.addSuppressed(cleanup); }
      throw failure;
    }
  }

  public MPSProject getProject() { checkLive(); return project; }
  public BrowserPlatform getPlatform() { checkLive(); return platform; }
  public BrowserApplication getApplication() { checkLive(); return application; }
  public BrowserProject getIdeaProject() { checkLive(); return ideaProject; }
  /** Number of live editor components owned by this host. */
  public int getEditorCount() { checkLive(); return editors.size(); }

  /** Requires the browser event thread and a model read action. */
  public NodeEditorComponent createEditor(SNode node) {
    checkLive();
    application.assertIsDispatchThread();
    project.getModelAccess().checkReadAccess();
    if (node == null || node.getModel() == null || node.getReference().resolve(project.getRepository()) != node) {
      throw new IllegalArgumentException("Editor node must belong to this project's repository");
    }
    NodeEditorComponent editor = new NodeEditorComponent(project.getRepository());
    // Generic Swing containers have no DOM tab stop until focus is explicitly requested.
    editor.setFocusable(true);
    editors.add(editor);
    DataManager.registerDataProvider(editor.getExternalComponent(), (UiDataProvider) sink -> {
      sink.set(CommonDataKeys.PROJECT, ideaProject);
      sink.set(MPSCommonDataKeys.MPS_PROJECT, project);
    });
    editor.editNode(node);
    highlighter.addAdditionalEditorComponent(editor);
    Runnable releaseHistoryBindings = installEditorShortcuts(editor);
    editor.addDisposeListener(component -> {
      editors.remove(component);
      releaseHistoryBindings.run();
      highlighter.removeAdditionalEditorComponent(component);
      DataManager.removeDataProvider(editor.getExternalComponent());
    });
    return editor;
  }

  /** Install the same focus-scoped MPS shortcuts on a host-owned inspector or editor.
   * The returned cleanup restores its previous maps and must run at disposal. */
  public Runnable installEditorShortcuts(jetbrains.mps.nodeEditor.EditorComponent editor) {
    checkLive();
    if (editor.getEditorContext().getRepository() != project.getRepository()) {
      throw new IllegalArgumentException("Editor must belong to this host repository");
    }
    InputMap previousInput = editor.getInputMap(JComponent.WHEN_FOCUSED);
    ActionMap previousActions = editor.getActionMap();
    InputMap input = new InputMap();
    ActionMap actions = new ActionMap();
    input.setParent(previousInput);
    actions.setParent(previousActions);
    Object undoKey = new Object();
    Object redoKey = new Object();
    for (int modifier : new int[] {InputEvent.CTRL_DOWN_MASK, InputEvent.META_DOWN_MASK}) {
      input.put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, modifier), undoKey);
      if (modifier == InputEvent.META_DOWN_MASK) input.put(KeyStroke.getKeyStroke(KeyEvent.VK_Y, modifier), redoKey);
      input.put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, modifier | InputEvent.SHIFT_DOWN_MASK), redoKey);
    }
    actions.put(undoKey, historyAction(editor, false));
    actions.put(redoKey, historyAction(editor, true));
    BrowserEditorShortcuts.install(editor, project, input, actions);
    editor.setInputMap(JComponent.WHEN_FOCUSED, input);
    editor.setActionMap(actions);
    return () -> {
      if (editor.getInputMap(JComponent.WHEN_FOCUSED) == input) editor.setInputMap(JComponent.WHEN_FOCUSED, previousInput);
      if (editor.getActionMap() == actions) editor.setActionMap(previousActions);
    };
  }

  private AbstractAction historyAction(jetbrains.mps.nodeEditor.EditorComponent editor, boolean redo) {
    return new AbstractAction(redo ? "Redo" : "Undo") {
      @Override public boolean isEnabled() {
        return !disposed && !editor.isDisposed() && editor.isFocusOwner();
      }
      @Override public void actionPerformed(ActionEvent event) {
        if (!isEnabled()) return;
        if (editor.getEditorContext().getRepository().getModelAccess().computeReadAction(editor::isReadOnly)) return;
        UndoManager history = UndoManager.getInstance(ideaProject);
        // Consume the editor shortcut even when history is empty; the DOM input proxy
        // must not replay its separate native text history over the MPS model.
        if (redo) {
          if (history.isRedoAvailable(null)) history.redo(null);
        } else if (history.isUndoAvailable(null)) {
          history.undo(null);
        }
      }
    };
  }

  @Override public void dispose() {
    if (disposed) return;
    Throwable failure = null;
    if (highlighter != null) failure = clean(failure, highlighter::stopUpdater);
    // Keep the application and platform available while editors and project services release listeners.
    List<NodeEditorComponent> disposingEditors = new ArrayList<>(editors);
    editors.clear();
    for (int i = disposingEditors.size() - 1; i >= 0; i--) {
      NodeEditorComponent editor = disposingEditors.get(i);
      if (!editor.isDisposed()) failure = clean(failure, editor::dispose);
    }
    if (selectionLoader != null) failure = clean(failure, selectionLoader::dispose);
    if (styles != null) failure = clean(failure, styles::dispose);
    if (languageRegistry != null) failure = clean(failure, () -> DataTransferManager.releaseInstance(languageRegistry));
    if (ideaProject != null && !ideaProject.isDisposed()) failure = clean(failure, () -> Disposer.dispose(ideaProject));
    if (nodeFiles != null) failure = clean(failure, () -> VirtualFileManager.getInstance().unregisterBrowserFileSystem(nodeFiles));
    if (findUsages != null) failure = clean(failure, findUsages::dispose);
    if (application != null && !application.isDisposed()) failure = clean(failure, () -> Disposer.dispose(application));
    if (assets != null) failure = clean(failure, assets::dispose);
    if (platform != null) failure = clean(failure, platform::dispose);
    if (modelAccess != null) failure = clean(failure, modelAccess::dispose);
    failure = clean(failure, () -> Disposer.dispose(applicationScope));
    disposed = true;
    if (failure instanceof Error) throw (Error) failure;
    if (failure != null) throw (RuntimeException) failure;
  }

  private static Throwable clean(Throwable first, Runnable cleanup) {
    try { cleanup.run(); } catch (RuntimeException | Error failure) {
      if (first == null) return failure;
      if (first != failure) first.addSuppressed(failure);
    }
    return first;
  }

  private void checkLive() { if (disposed) throw new IllegalStateException("Browser editor host is disposed"); }
}
