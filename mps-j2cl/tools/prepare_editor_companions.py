#!/usr/bin/env python3
"""Reproduce focused browser adaptations in selected editor companion classes."""
import argparse
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]


def replace_once(text, old, new):
    if text.count(old) != 1:
        raise SystemExit('Upstream editor companion changed: ' + old[:90])
    return text.replace(old, new, 1)


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--check', action='store_true')
    args = parser.parse_args()
    results = {}
    path = 'editor/editor-runtime/source/jetbrains/mps/nodeEditor/HintPopupController.java'
    text = (ROOT / 'mps' / path).read_text()
    text = replace_once(text, '  public void uninstallListeners(Component owner) {',
                        '  public void uninstallListeners(Component owner) {\n    cancelAllRequests();')
    text = replace_once(text, '''    public void mouseClicked(MouseEvent e) {
      MPSDocumentationManager.getInstance().cancelAll();''', '''    public void mouseClicked(MouseEvent e) {
      cancelAllRequests();
      MPSDocumentationManager.getInstance().cancelAll();''')
    text = replace_once(text, '''    public void keyTyped(KeyEvent e) {
      MPSDocumentationManager.getInstance().cancelAll();''', '''    public void keyTyped(KeyEvent e) {
      cancelAllRequests();
      MPSDocumentationManager.getInstance().cancelAll();''')
    text = replace_once(text, '''    public void keyPressed(KeyEvent e) {
      if (e.isActionKey()) {
        MPSDocumentationManager.getInstance().cancelAll();
      }
    }''', '''    public void keyPressed(KeyEvent e) {
      // Cancel the pending hover too: keyboard editing can invalidate its diagnostic
      // before the browser's delayed callback creates the tooltip component.
      cancelAllRequests();
      MPSDocumentationManager.getInstance().cancelAll();
    }''')
    results[path] = text

    path = 'editor/editor-runtime/source/jetbrains/mps/nodeEditor/cellMenu/CompletionHelper.java'
    text = (ROOT / 'mps' / path).read_text()
    text = replace_once(text, 'parseProperty("mps.completion.delay.millis", 500)', 'parsePopupDelay(500)')
    text = replace_once(text, 'parseProperty(@NotNull String name, int defValue)', 'parsePopupDelay(int defValue)')
    text = replace_once(text, 'System.getProperty(name)', 'System.getProperty("mps.completion.delay.millis")')
    results[path] = text

    path = 'editor/editor-runtime/source/jetbrains/mps/nodeEditor/updater/UpdaterRepositoryContentAdapter.java'
    text = (ROOT / 'mps' / path).read_text()
    text = replace_once(text, 'Thread.currentThread().getStackTrace()', 'new Throwable("Model removed").getStackTrace()')
    results[path] = text

    path = 'editor/editing-runtime/source_gen/jetbrains/mps/datatransfer/DataTransferManager.java'
    text = (ROOT / 'mps' / path).read_text()
    text = replace_once(text, 'private static DataTransferManager INSTANCE = new DataTransferManager(LanguageRegistry.getInstance());',
                        'private static DataTransferManager INSTANCE;')
    text = replace_once(text, '  public static DataTransferManager getInstance() {\n    return INSTANCE;\n  }', '''  public static DataTransferManager getInstance() {
    LanguageRegistry registry = LanguageRegistry.getInstance();
    if (registry == null) throw new IllegalStateException("No language registry installed");
    if (INSTANCE != null && INSTANCE.myLanguageRegistry != registry) releaseInstance();
    if (INSTANCE == null) INSTANCE = new DataTransferManager(registry);
    return INSTANCE;
  }

  /** Release only the transfer manager belonging to the caller's platform. */
  public static void releaseInstance(LanguageRegistry owner) {
    if (INSTANCE != null && INSTANCE.myLanguageRegistry == owner) releaseInstance();
  }

  /** Browser host teardown releases the listener and aspect caches before the platform. */
  public static void releaseInstance() {
    if (INSTANCE == null) return;
    INSTANCE.myLanguageRegistry.removeRegistryListener(INSTANCE);
    INSTANCE.myCopyPreProcessors.clear();
    INSTANCE.myPastePostProcessors.clear();
    INSTANCE.myPasteWrappers.clear();
    INSTANCE = null;
  }''')
    results[path] = text

    path = 'editor/editor-runtime/source/jetbrains/mps/nodeEditor/IntentionsSupport.java'
    text = (ROOT / 'mps' / path).read_text()
    text = replace_once(text,
        'for (Shortcut shortcut : ideaActionRegistration.getShortcutSet().getShortcuts())',
        'for (Shortcut shortcut : ideaActionRegistration == null ? new Shortcut[0] : ideaActionRegistration.getShortcutSet().getShortcuts())')
    text = text.replace('import com.intellij.openapi.application.ApplicationManager;\n', 'import com.intellij.util.Alarm;\n')
    text = text.replace('IntentionsThread', 'IntentionsRequest')
    text = text.replace('myShowIntentionsRequest', 'myPendingIntentions')
    text = text.replace('stopIntentionThread', 'cancelIntentionRequest')
    text = text.replace('intentionThreadCompleted', 'intentionRequestCompleted')
    text = text.replace('IntentionsRequest thread', 'IntentionsRequest request')
    text = text.replace('if (thread != null)', 'if (request != null)')
    text = text.replace('thread.requestStop()', 'request.requestStop()')
    text = text.replace('compareAndSet(thread, null)', 'compareAndSet(request, null)')
    text = replace_once(text,
        '  private final AtomicReference<IntentionsRequest> myPendingIntentions = new AtomicReference<>();',
        '''  private final AtomicReference<IntentionsRequest> myPendingIntentions = new AtomicReference<>();
  private final Alarm myIntentionsAlarm = new Alarm(Alarm.ThreadToUse.SWING_THREAD);
  private boolean myDisposed;''')
    text = replace_once(text,
        '        // If editor lost focus, thread can be stopped - it will be any way restarted on focus gain',
        '        // Focus loss cancels the delay; focus gain starts a fresh request.')
    text = replace_once(text,
        '      public void editorWillBeDisposed(@NotNull EditorComponent component) {\n        cancelIntentionRequest();',
        '''      public void editorWillBeDisposed(@NotNull EditorComponent component) {
        myDisposed = true;
        cancelIntentionRequest();
        myIntentionsAlarm.dispose();''')
    start = text.index('  private void updateIntentionsStatus() {')
    end = text.index('\n  private boolean isInconsistentEditor()', start)
    text = text[:start] + '''  private void updateIntentionsStatus() {
    cancelIntentionRequest();
    if (myDisposed || myEditor.isDisposed()) return;
    hideLightBulb();
    if (!myEditor.getEditorConfiguration().showLightBulb || !myEditor.isFocusOwner()) return;

    // Capture model identity under a read action, then release it before the delay.
    IntentionsRequest request = getModelAccess().computeReadAction(() ->
        isInconsistentEditor() || intentionsDisabled() ? null
            : new IntentionsRequest(myEditor.getSelectedNode(), myEditor.getEditedNode()));
    if (request != null) {
      myPendingIntentions.set(request);
      myIntentionsAlarm.addRequest(request, INTENTION_SHOW_DELAY);
    }
  }
''' + text[end:]
    start = text.index('  private class IntentionsRequest extends Thread {')
    text = text[:start] + '''  private final class IntentionsRequest implements Runnable {
    private final SNode mySelectedNode;
    private final SNode myEditedNode;
    private boolean myStopRequested;

    IntentionsRequest(SNode selectedNode, SNode editedNode) {
      mySelectedNode = selectedNode;
      myEditedNode = editedNode;
    }

    void requestStop() {
      myStopRequested = true;
      myIntentionsAlarm.cancelRequest(this);
    }

    private boolean isCurrent() {
      return !myStopRequested && !myDisposed && !myEditor.isDisposed()
          && myPendingIntentions.get() == this && myEditor.isFocusOwner();
    }

    // Model identities are checked only while holding the repository read action.
    private boolean matchesSelection() {
      return !isInconsistentEditor() && !intentionsDisabled()
          && myEditor.getSelectedNode() == mySelectedNode && myEditor.getEditedNode() == myEditedNode;
    }

    @Override
    public void run() {
      try {
        if (!isCurrent()) return;
        getModelAccess().runReadAction(() -> {
          if (!isCurrent() || !matchesSelection() || myEditor.getTypecheckingSession() == null) return;
          // Alarm runs on the browser Swing event queue. Keep the real lookup,
          // typechecking session and bulb geometry in one scoped read action.
          Kind intentionKind = TypecheckingFacade.getFromContext().computeWithSession(
              myEditor.getTypecheckingSession(), session -> IntentionsManager.getInstance()
                  .getHighestAvailableBaseIntentionType(mySelectedNode, myEditor.getEditorContext()));
          if (intentionKind == null || !isCurrent() || !matchesSelection()) return;
          if (myEditor.getSelectedCell() == null) {
            hideLightBulb();
          } else {
            adjustLightBulbLocation();
            showLightBulbComponent(intentionKind == Kind.NORMAL ? Icons.INTENTION : new IntentionIconProvider(intentionKind).getIcon());
          }
        });
      } finally {
        intentionRequestCompleted(this);
      }
    }
  }
}
'''
    results[path] = text

    path = 'workbench/mps-platform/source/jetbrains/mps/workbench/action/BaseAction.java'
    text = (ROOT / 'mps' / path).read_text()
    text = text.replace('import jetbrains.mps.make.MakeServiceComponent;\n', '')
    text = replace_once(text,
        '    final Platform mpsPlaf = MPSCoreComponents.getInstance().getPlatform();\n'
        '    final MakeServiceComponent makeService = mpsPlaf.findComponent(MakeServiceComponent.class);\n'
        '    return makeService != null && makeService.isSessionActive();',
        '    // The selected browser platform contains no JVM make service.\n'
        '    return false;')
    results[path] = text

    path = 'core/findUsages-runtime/source_gen/jetbrains/mps/ide/findusages/model/scopes/FindUsagesScope.java'
    text = (ROOT / 'mps' / path).read_text()
    text = replace_once(text,
        'return (FindUsagesScope) Class.forName(scopeClass).getConstructor(Element.class, Project.class).newInstance(scopeXml, project);',
        'return jetbrains.mps.classloading.ModuleClassLoader.newInstance(\n'
        '          ClassLoader.getSystemClassLoader().loadClass(scopeClass), FindUsagesScope.class,\n'
        '          new Class<?>[] {Element.class, Project.class}, scopeXml, project);')
    results[path] = text

    path = 'editor/editor-runtime/source/jetbrains/mps/nodeEditor/cells/contextAssistant/ContextAssistantSettings.java'
    text = (ROOT / 'mps' / path).read_text()
    text = text.replace('import com.intellij.util.xmlb.XmlSerializerUtil;\n', '')
    text = replace_once(text, 'XmlSerializerUtil.copyBean(state, this);', 'HELP_SHOWN = state.HELP_SHOWN;')
    results[path] = text

    path = 'editor/editor-runtime/source/jetbrains/mps/nodeEditor/documentation/MPSDocumentationToolWindowManager.java'
    text = (ROOT / 'mps' / path).read_text()
    text = replace_once(text, 'private MPSDocumentationToolWindowManager(Project project)', 'public MPSDocumentationToolWindowManager(Project project)')
    results[path] = text

    path = 'workbench/mps-platform/source/jetbrains/mps/smodel/ProjectModelAccess2.java'
    text = (ROOT / 'mps' / path).read_text()
    text = replace_once(text, 'final class ProjectModelAccess2', 'public final class ProjectModelAccess2')
    text = text.replace('WorkbenchModelAccess', 'BrowserModelAccess')
    text = replace_once(text, '/*package*/ ProjectModelAccess2(', 'public ProjectModelAccess2(')
    text = text.replace('    mySharedImpl.cancelReads();\n', '')
    text = text.replace('    mySharedImpl.cancelReads(); // see mySharedImpl.runWriteInEDT for reasons\n', '')
    text = text.replace('executeCommand_(cmd, myProject, undoDescription)', 'executeCommand_(cmd, myProject.getProject(), undoDescription)')
    text = text.replace('wrapTopCommandRunnable(r, myProject), myProject, r instanceof UndoRunnable',
                        'wrapTopCommandRunnable(r, myProject), myProject.getProject(), r instanceof UndoRunnable')
    at = text.index('  @Override\n  public void executeCommand(')
    text = text[:at] + '  public void dispose() { myUndoHandler.dispose(); }\n\n' + text[at:]
    results[path] = text

    path = 'editor/editor-runtime/source/jetbrains/mps/nodeEditor/NodeInformationDialog.java'
    text = (ROOT / 'mps' / path).read_text()
    start = text.index('        final HTMLDocument doc =')
    end = text.index('\n      }\n    };', start)
    text = text[:start] + ('        // The browser JEditorPane already stores normalized plain text.\n'
                          '        String selected = super.getSelectedText();\n'
                          '        return (selected == null ? getText() : selected).trim();') + text[end:]
    for imported in ['javax.swing.text.BadLocationException', 'javax.swing.text.html.HTMLDocument',
                     'javax.swing.text.html.HTMLEditorKit', 'java.io.IOException', 'java.io.StringWriter']:
        text = text.replace('import ' + imported + ';\n', '')
    results[path] = text
    for path, text in results.items():
        output = ROOT / 'mps-web' / path
        if args.check:
            if not output.exists() or output.read_bytes() != text.encode():
                raise SystemExit('Stale editor companion overlay: ' + path)
        else:
            output.parent.mkdir(parents=True, exist_ok=True)
            output.write_text(text, encoding='utf-8', newline='\n')
    print(f'Editor companion overlays: {len(results)} verified')


if __name__ == '__main__':
    main()
