package mpsj2cl.intellij;

import com.intellij.compat.BrowserApplication;
import com.intellij.compat.BrowserComponentManager;
import com.intellij.compat.BrowserMessageBus;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationListener;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.UserDataHolderBase;
import com.intellij.util.messages.MessageBusConnection;
import com.intellij.util.messages.Topic;

public final class IntelliJContractsTest {
  private static int assertions;
  public interface Changed { void changed(String value); }
  private static final class Service implements Disposable {
    int disposals;
    @Override public void dispose() { disposals++; }
  }
  public static String run() {
    assertions = 0;
    keysAndRanges(); disposal(); services(); messages(); application(); undo(); defensiveCopies(); actions(); fragments(); dataSnapshots(); popupsAndContents(); html(); clipboard(); keymapsAndActionPlaces();
    return "IntelliJ contracts: " + assertions + " assertions passed";
  }
  /** Browser and JVM callback contract: no blocking wait is performed on the event thread. */
  public static void runAsync(Runnable success, java.util.function.Consumer<Throwable> failure) {
    com.intellij.compat.BrowserEdtExecutor executor = new com.intellij.compat.BrowserEdtExecutor();
    boolean[] callerReturned = {false}; int[] calls = {0};
    java.util.concurrent.ScheduledFuture<?> canceled = executor.schedule(() -> calls[0] += 1000, 10, java.util.concurrent.TimeUnit.MILLISECONDS);
    canceled.cancel(false);
    executor.execute(() -> {
      try { check(callerReturned[0]); check(javax.swing.SwingUtilities.isEventDispatchThread()); calls[0]++; }
      catch (Throwable error) { executor.shutdownNow(); failure.accept(error); }
    });
    RuntimeException sentinel = new RuntimeException("periodic failure");
    java.util.concurrent.ScheduledFuture<?> periodic = executor.scheduleAtFixedRate(() -> { throw sentinel; }, 0, 10, java.util.concurrent.TimeUnit.MILLISECONDS);
    executor.schedule(() -> {
      try {
        equal(1, calls[0]); check(canceled.isCancelled()); check(periodic.isDone()); check(!periodic.isCancelled());
        boolean correctFailure = false;
        try { periodic.get(); } catch (java.util.concurrent.ExecutionException error) { correctFailure = error.getCause() == sentinel; }
        check(correctFailure); executor.shutdownNow(); check(executor.isTerminated()); success.run();
      } catch (Throwable error) { executor.shutdownNow(); failure.accept(error); }
    }, 40, java.util.concurrent.TimeUnit.MILLISECONDS);
    callerReturned[0] = true;
  }
  public static void main(String[] arguments) { System.out.println(run()); }
  private static void keysAndRanges() {
    Key<String> first = Key.create("same");
    Key<String> second = Key.create("same");
    UserDataHolderBase holder = new UserDataHolderBase();
    first.set(holder, "first"); second.set(holder, "second");
    equal("first", first.get(holder)); equal("second", second.get(holder));
    first.set(holder, null); equal("fallback", first.get(holder, "fallback"));
    TextRange range = new TextRange(2, 5);
    check(range.contains(5)); check(!range.containsOffset(5));
    equal("cde", range.substring("abcdef"));
    equal(new TextRange(4, 5), range.intersection(new TextRange(4, 8)));
    equal(null, range.intersection(new TextRange(6, 8)));
  }
  private static void disposal() {
    StringBuilder order = new StringBuilder();
    Disposable parent = () -> order.append("P");
    Disposable first = () -> order.append("1");
    Disposable second = () -> order.append("2");
    Disposer.register(parent, first); Disposer.register(parent, second);
    Disposer.dispose(parent); equal("21P", order.toString());
    Disposer.dispose(parent); equal("21P", order.toString());
    check(!Disposer.tryRegister(parent, () -> {}));
    Disposable a = Disposer.newDisposable(), b = Disposer.newDisposable();
    Disposer.register(a, b);
    expectFailure(() -> Disposer.register(b, a));
    Disposer.dispose(a);
    Disposable x = Disposer.newDisposable(), y = Disposer.newDisposable();
    Disposable child = () -> order.append("C");
    Disposer.register(x, child); Disposer.register(y, child);
    Disposer.dispose(x); equal("21P", order.toString());
    Disposer.dispose(y); equal("21PC", order.toString());
    Disposable failing = () -> order.append("F");
    Disposer.register(failing, () -> { throw new IllegalStateException("expected"); });
    expectFailure(() -> Disposer.dispose(failing));
    equal("21PCF", order.toString());
    Disposer.assertIsEmpty();
  }
  private static void services() {
    BrowserComponentManager manager = new BrowserComponentManager();
    Service service = new Service(); manager.registerService(Service.class, service);
    check(manager.getService(Service.class) == service);
    equal(null, manager.getComponent(String.class));
    expectFailure(() -> manager.getService(String.class));
    expectFailure(() -> manager.registerService(Service.class, new Service()));
    check(manager.getService(Service.class) == service);
    manager.registerService(Disposable.class, () -> {
      check(!manager.isDisposed());
      check(manager.getService(Service.class) == service);
      expectFailure(() -> manager.registerService(String.class, "too late"));
    });
    Disposer.dispose(manager); equal(1, service.disposals);
    expectFailure(() -> manager.getService(Service.class));
  }
  private static void messages() {
    BrowserMessageBus root = new BrowserMessageBus();
    BrowserMessageBus child = new BrowserMessageBus(root);
    Topic<Changed> topic = Topic.create("change", Changed.class);
    root.registerPublisher(topic, dispatch -> value -> dispatch.accept(listener -> listener.changed(value)));
    StringBuilder events = new StringBuilder();
    Disposable owner = Disposer.newDisposable();
    root.connect(owner).subscribe(topic, value -> events.append("R").append(value));
    MessageBusConnection connection = child.connect();
    connection.subscribe(topic, value -> events.append("C").append(value));
    root.syncPublisher(topic).changed("1"); equal("R1C1", events.toString());
    connection.disconnect();
    root.syncPublisher(topic).changed("2"); equal("R1C1R2", events.toString());
    Disposer.dispose(owner);
    root.syncPublisher(topic).changed("3"); equal("R1C1R2", events.toString());
    Topic<Changed> unbound = Topic.create("unbound", Changed.class);
    expectFailure(() -> root.syncPublisher(unbound));
    root.dispose(); check(child.isDisposed());
    expectFailure(root::connect);
  }
  private static void application() {
    BrowserApplication app = new BrowserApplication(true, true);
    StringBuilder events = new StringBuilder();
    Disposable owner = Disposer.newDisposable();
    app.addApplicationListener(new ApplicationListener() {
      @Override public void beforeWriteActionStart(Object action) { events.append("b"); }
      @Override public void writeActionStarted(Object action) { check(app.isWriteAccessAllowed()); events.append("s"); }
      @Override public void writeActionFinished(Object action) { check(app.isWriteAccessAllowed()); events.append("f"); }
      @Override public void afterWriteActionFinished(Object action) { events.append("a"); }
    }, owner);
    app.runReadAction(() -> {
      check(app.isReadAccessAllowed()); check(!app.isWriteAccessAllowed());
      app.runWriteAction(() -> { check(app.isReadAccessAllowed()); check(app.isWriteAccessAllowed()); });
      check(app.isReadAccessAllowed()); check(!app.isWriteAccessAllowed());
    });
    equal("bsfa", events.toString()); check(!app.isReadAccessAllowed());
    expectFailure(() -> app.runWriteAction(() -> { throw new IllegalStateException("expected"); }));
    check(!app.isWriteAccessAllowed()); equal("bsfabsfa", events.toString());
    Disposer.dispose(owner);
    app.runWriteAction(() -> {}); equal("bsfabsfa", events.toString());
    Disposer.dispose(app); Disposer.assertIsEmpty();
  }
  private static void undo() {
    BrowserApplication app = new BrowserApplication(true, true);
    com.intellij.compat.BrowserProject project = new com.intellij.compat.BrowserProject("test", "/test", app);
    com.intellij.openapi.command.undo.UndoManager history = com.intellij.openapi.command.undo.UndoManager.getInstance(project);
    com.intellij.openapi.command.CommandProcessor commands = com.intellij.openapi.command.CommandProcessor.getInstance();
    int[] model = {0};
    java.util.function.IntConsumer change = value -> {
      int before = model[0]; model[0] = value;
      history.undoableActionPerformed(new com.intellij.openapi.command.undo.BasicUndoableAction() {
        @Override public void undo() {
          check(history.isUndoInProgress()); model[0] = before;
          history.undoableActionPerformed(this); // Playback must not recursively create history.
        }
        @Override public void redo() { check(history.isRedoInProgress()); model[0] = value; }
      });
    };
    commands.executeCommand(project, () -> {
      change.accept(1);
      commands.executeCommand(project, () -> change.accept(2), "nested", null);
    }, "outer", null);
    equal(2, model[0]); check(history.isUndoAvailable(null));
    history.undo(); equal(0, model[0]); check(!history.isUndoAvailable(null)); check(history.isRedoAvailable(null));
    history.redo(); equal(2, model[0]);
    commands.executeCommand(project, () -> change.accept(3), "typing", "typing");
    commands.executeCommand(project, () -> change.accept(4), "typing", "typing");
    history.undo(); equal(2, model[0]);
    commands.executeCommand(project, () -> change.accept(5), "new branch", "typing");
    check(!history.isRedoAvailable(null)); history.undo(); equal(2, model[0]);
    history.clearHistory(); check(!history.isUndoAvailable(null)); check(!history.isRedoAvailable(null));
    history.undoableActionPerformed(new com.intellij.openapi.command.undo.BasicUndoableAction() {
      @Override public void undo() throws com.intellij.openapi.command.undo.UnexpectedUndoException {
        throw new com.intellij.openapi.command.undo.UnexpectedUndoException("stale");
      }
      @Override public void redo() {}
    });
    expectFailure(history::undo); check(!history.isUndoAvailable(null)); check(!history.isRedoAvailable(null));
    AssertionError playbackError = new AssertionError("partially applied undo");
    history.undoableActionPerformed(new com.intellij.openapi.command.undo.BasicUndoableAction() {
      @Override public void undo() { throw playbackError; }
      @Override public void redo() {}
    });
    boolean sameError = false;
    try { history.undo(); } catch (AssertionError error) { sameError = error == playbackError; }
    check(sameError); check(!history.isUndoAvailable(null)); check(!history.isRedoAvailable(null));
    history.undoableActionPerformed(new com.intellij.openapi.command.undo.BasicUndoableAction() {
      @Override public void undo() { history.clearHistory(); }
      @Override public void redo() { throw new AssertionError("invalidated history must not reappear"); }
    });
    history.undo(); check(!history.isUndoAvailable(null)); check(!history.isRedoAvailable(null));
    Disposer.dispose(project); check(!history.isUndoAvailable(null)); Disposer.dispose(app);
    Disposer.assertIsEmpty();
  }
  private static void defensiveCopies() {
    com.intellij.openapi.actionSystem.Shortcut shortcut = new com.intellij.openapi.actionSystem.KeyboardShortcut(javax.swing.KeyStroke.getKeyStroke("ESCAPE"), null);
    com.intellij.openapi.actionSystem.Shortcut[] shortcutInput = {shortcut};
    com.intellij.openapi.actionSystem.CustomShortcutSet shortcuts = new com.intellij.openapi.actionSystem.CustomShortcutSet(shortcutInput);
    shortcutInput[0] = null;
    check(shortcuts.getShortcuts()[0] == shortcut);
    shortcuts.getShortcuts()[0] = null;
    check(shortcuts.getShortcuts()[0] == shortcut);
    com.intellij.openapi.command.undo.DocumentReference reference = new com.intellij.openapi.command.undo.DocumentReference() {
      public com.intellij.openapi.editor.Document getDocument() { return null; }
      public com.intellij.openapi.vfs.VirtualFile getFile() { return null; }
    };
    com.intellij.openapi.command.undo.DocumentReference[] documentInput = {reference};
    com.intellij.openapi.command.undo.BasicUndoableAction action = new com.intellij.openapi.command.undo.BasicUndoableAction(documentInput) {
      public void undo() {}
      public void redo() {}
    };
    documentInput[0] = null;
    check(action.getAffectedDocuments()[0] == reference);
    action.getAffectedDocuments()[0] = null;
    check(action.getAffectedDocuments()[0] == reference);
  }
  private static void actions() {
    com.intellij.openapi.actionSystem.DataKey<String> key = com.intellij.openapi.actionSystem.DataKey.create("test.data");
    check(key == com.intellij.openapi.actionSystem.DataKey.<String>create("test.data"));
    com.intellij.openapi.actionSystem.DataContext parent = com.intellij.openapi.actionSystem.impl.SimpleDataContext.builder().add(key, "parent").build();
    equal("parent", key.getData(com.intellij.openapi.actionSystem.impl.SimpleDataContext.builder(parent).build()));
    equal(null, key.getData(com.intellij.openapi.actionSystem.impl.SimpleDataContext.builder(parent).add(key, null).build()));
    com.intellij.openapi.actionSystem.Presentation original = new com.intellij.openapi.actionSystem.Presentation("first");
    original.setDescription("description"); original.putClientProperty("key", "value");
    com.intellij.openapi.actionSystem.Presentation copy = original.clone(); copy.setText("second");
    equal("first", original.getText()); equal("description", copy.getDescription()); equal("value", copy.getClientProperty("key"));
    int[] changes = {0}; copy.addPropertyChangeListener(event -> changes[0]++);
    copy.setEnabledAndVisible(false); equal(2, changes[0]); check(!copy.isEnabledAndVisible());
    java.util.Map<com.intellij.openapi.actionSystem.DataKey<?>, Object> snapshot = new java.util.HashMap<>();
    com.intellij.openapi.actionSystem.DataSink sink = new com.intellij.openapi.actionSystem.DataSink() {
      public <T> void set(com.intellij.openapi.actionSystem.DataKey<T> dataKey, T value) { snapshot.put(dataKey, value); }
    };
    com.intellij.openapi.actionSystem.DataSink.uiDataSnapshot(sink, dataId -> null);
    check(!snapshot.containsKey(key));
    sink.setNull(key); check(snapshot.containsKey(key));
  }
  private static void fragments() {
    com.intellij.ui.SimpleColoredComponent component = new com.intellij.ui.SimpleColoredComponent();
    com.intellij.ui.SimpleTextAttributes base = new com.intellij.ui.SimpleTextAttributes(0, java.awt.Color.BLACK);
    com.intellij.ui.SimpleTextAttributes match = new com.intellij.ui.SimpleTextAttributes(1, java.awt.Color.RED);
    com.intellij.ui.speedSearch.SpeedSearchUtil.appendColoredFragments(component, "HelloWorld", java.util.Arrays.asList(new TextRange(0, 1), new TextRange(5, 6)), base, match);
    equal("HelloWorld", component.getCharSequence(false)); equal(4, component.getFragmentCount());
    equal("H", component.getFragmentText(0)); equal("ello", component.getFragmentText(1));
    check(component.getFragmentAttributes(2) == match); check(component.getFragmentAttributes(3) == base);
    component.clear(); equal(0, component.getFragmentCount());
    expectFailure(() -> com.intellij.ui.speedSearch.SpeedSearchUtil.appendColoredFragments(component, "abc", java.util.Arrays.asList(new TextRange(1, 3), new TextRange(0, 1)), base, match));
  }
  private static void dataSnapshots() {
    com.intellij.openapi.actionSystem.DataKey<String> first = com.intellij.openapi.actionSystem.DataKey.create("lazy.first");
    com.intellij.openapi.actionSystem.DataKey<String> second = com.intellij.openapi.actionSystem.DataKey.create("lazy.second");
    int[] calls = {0};
    com.intellij.openapi.actionSystem.DataContext context = com.intellij.openapi.actionSystem.CustomizedDataContext.withSnapshot(com.intellij.openapi.actionSystem.DataContext.EMPTY_CONTEXT, sink -> {
      sink.lazy(first, () -> { calls[0]++; return "value"; });
      sink.lazyValue(second, values -> values.get(first) + ".dependent");
    });
    equal(0, calls[0]); equal("value.dependent", second.getData(context)); equal(1, calls[0]);
    equal("value", first.getData(context)); equal(1, calls[0]);
    com.intellij.openapi.actionSystem.DataContext cycle = com.intellij.openapi.actionSystem.CustomizedDataContext.withSnapshot(context, sink -> {
      sink.lazyValue(first, values -> values.get(second)); sink.lazyValue(second, values -> values.get(first));
    });
    expectFailure(() -> first.getData(cycle));
    com.intellij.openapi.actionSystem.DataContext masked = com.intellij.openapi.actionSystem.CustomizedDataContext.withSnapshot(context, sink -> sink.setNull(first));
    equal(null, first.getData(masked)); equal("value.dependent", second.getData(masked));
  }
  private static void popupsAndContents() {
    com.intellij.ui.popup.AbstractPopup popup = new com.intellij.ui.popup.AbstractPopup(new javax.swing.JPanel(), null);
    int[] closed = {0}; Disposer.register(popup, () -> closed[0]++);
    popup.setCancelCallback(() -> false); popup.cancel(); check(!popup.isDisposed()); equal(0, closed[0]);
    popup.closeOk(null); check(popup.isDisposed()); equal(1, closed[0]); popup.cancel(); equal(1, closed[0]);
    class PopupWithCloseEvent extends com.intellij.ui.popup.AbstractPopup {
      int cancellations;
      PopupWithCloseEvent() { super(new javax.swing.JPanel(), null); }
      @Override protected void afterCanceled() { cancellations++; }
      void nativeClose() {
        javax.swing.event.PopupMenuEvent event = new javax.swing.event.PopupMenuEvent(popup);
        for (javax.swing.event.PopupMenuListener listener : popup.getPopupMenuListeners()) listener.popupMenuWillBecomeInvisible(event);
      }
    }
    PopupWithCloseEvent outside = new PopupWithCloseEvent();
    int[] callbacks = {0}; outside.setCancelCallback(() -> { callbacks[0]++; return true; });
    outside.nativeClose(); outside.nativeClose(); outside.cancel();
    check(outside.isDisposed()); equal(1, outside.cancellations); equal(1, callbacks[0]);
    PopupWithCloseEvent selected = new PopupWithCloseEvent();
    selected.closeOk(null); selected.nativeClose(); equal(0, selected.cancellations);
    PopupWithCloseEvent explicit = new PopupWithCloseEvent();
    explicit.setCancelCallback(() -> false); explicit.cancel(); equal(0, explicit.cancellations);
    explicit.setCancelCallback(() -> true); explicit.cancel(); explicit.nativeClose(); equal(1, explicit.cancellations);
    BrowserApplication app = new BrowserApplication(true, true);
    com.intellij.compat.BrowserProject project = new com.intellij.compat.BrowserProject("tool", "/tool", app);
    com.intellij.openapi.wm.ToolWindow tool = com.intellij.openapi.wm.ToolWindowManager.getInstance(project).getToolWindow("Documentation");
    check(!tool.isVisible());
    com.intellij.ui.content.Content content = com.intellij.ui.content.ContentFactory.getInstance().createContent(new javax.swing.JPanel(), "first", false);
    tool.getContentManager().addContent(content); check(tool.getContentManager().getSelectedContent() == content);
    javax.swing.JPanel replacement = new javax.swing.JPanel(); content.setComponent(replacement);
    check(replacement.getParent() == tool.getContentManager().getComponent());
    tool.show(); check(tool.isVisible()); tool.setAvailable(false); check(!tool.isVisible()); expectFailure(tool::show);
    Disposer.register(content, () -> closed[0]++); Disposer.dispose(project); equal(2, closed[0]); Disposer.dispose(app); Disposer.assertIsEmpty();
  }
  private static void html() {
    com.intellij.openapi.util.text.HtmlChunk.Element base = com.intellij.openapi.util.text.HtmlChunk.div().setClass("content");
    equal("<div class=\"content\"></div>", base.toString());
    equal("<div class=\"content\">&lt;tag&gt;&amp;&#39;&quot;</div>", base.addText("<tag>&'\"").toString());
    equal("a\\.b\\[c\\]\\+", com.intellij.openapi.util.text.StringUtil.escapeToRegexp("a.b[c]+"));
    equal(10, com.intellij.openapi.options.FontSize.XX_SMALL.smaller().getSize());
    equal(24, com.intellij.openapi.options.FontSize.XX_LARGE.larger().getSize());
  }
  private static void clipboard() {
    com.intellij.ide.CopyPasteManagerEx manager = com.intellij.ide.CopyPasteManagerEx.getInstanceEx();
    java.awt.datatransfer.Clipboard clipboard = new java.awt.datatransfer.Clipboard("test"); manager.setBrowserClipboard(clipboard); manager.setBrowserHistoryLimit(2);
    java.awt.datatransfer.Transferable first = new java.awt.datatransfer.StringSelection("same");
    java.awt.datatransfer.Transferable second = new java.awt.datatransfer.StringSelection("same");
    java.awt.datatransfer.Transferable third = new java.awt.datatransfer.StringSelection("new");
    try {
      manager.setContents(first); manager.setContents(second);
      check(manager.getAllContents()[0] == second); check(manager.getAllContents()[1] == first);
      manager.setContents(first); equal(2, manager.getAllContents().length); check(manager.getAllContents()[0] == first);
      clipboard.setContents(third, null); check(manager.getContents() == third); equal(2, manager.getAllContents().length);
      manager.removeContent(third); check(manager.getContents() == first); equal(1, manager.getAllContents().length);
      equal("same", manager.getContents(java.awt.datatransfer.DataFlavor.stringFlavor));
      manager.removeContent(first); equal(null, manager.getContents()); equal(0, manager.getAllContents().length);
    } finally { manager.setBrowserClipboard(null); manager.setBrowserHistoryLimit(50); }
  }
  @SuppressWarnings("unchecked")
  private static void keymapsAndActionPlaces() {
    com.intellij.openapi.actionSystem.Shortcut[] shortcuts = com.intellij.openapi.keymap.KeymapUtil.getActiveKeymapShortcuts(com.intellij.openapi.actionSystem.IdeActions.ACTION_EDITOR_ESCAPE).getShortcuts();
    equal(1, shortcuts.length); equal(javax.swing.KeyStroke.getKeyStroke("ESCAPE"), ((com.intellij.openapi.actionSystem.KeyboardShortcut) shortcuts[0]).getFirstKeyStroke());
    com.intellij.openapi.actionSystem.KeyboardShortcut parsed = com.intellij.openapi.actionSystem.KeyboardShortcut.fromString("ctrl K, ctrl C");
    equal(javax.swing.KeyStroke.getKeyStroke("ctrl K"), parsed.getFirstKeyStroke()); equal(javax.swing.KeyStroke.getKeyStroke("ctrl C"), parsed.getSecondKeyStroke());
    expectFailure(() -> com.intellij.openapi.actionSystem.KeyboardShortcut.fromString("not a key"));
    String[] calledAt = {null};
    com.intellij.openapi.actionSystem.AnAction action = new com.intellij.openapi.actionSystem.AnAction("Run") {
      public void actionPerformed(com.intellij.openapi.actionSystem.AnActionEvent event) { calledAt[0] = event.getPlace(); }
    };
    com.intellij.openapi.ui.popup.ListPopup popup = com.intellij.openapi.ui.popup.JBPopupFactory.getInstance().createActionGroupPopup("Intentions", new com.intellij.openapi.actionSystem.DefaultActionGroup(action), com.intellij.openapi.actionSystem.DataContext.EMPTY_CONTEXT, com.intellij.openapi.ui.popup.JBPopupFactory.ActionSelectionAid.SPEEDSEARCH, false, "editor.intentions");
    com.intellij.openapi.ui.popup.ListPopupStep<com.intellij.openapi.actionSystem.AnAction> step = (com.intellij.openapi.ui.popup.ListPopupStep<com.intellij.openapi.actionSystem.AnAction>) popup.getListStep();
    equal(1, step.getValues().size()); step.onChosen(action, true); step.getFinalRunnable().run(); equal("editor.intentions", calledAt[0]); popup.closeOk(null);
    int[] cancelled = {0};
    com.intellij.openapi.ui.popup.ListPopup cancelPopup = com.intellij.openapi.ui.popup.JBPopupFactory.getInstance().createListPopup(new com.intellij.openapi.ui.popup.util.BaseListPopupStep<String>("Test", new String[]{"value"}) {
      @Override public void canceled() { cancelled[0]++; }
    });
    cancelPopup.cancel(); cancelPopup.cancel(); equal(1, cancelled[0]); Disposer.assertIsEmpty();
  }
  private static void expectFailure(Runnable action) {
    boolean failed = false;
    try { action.run(); } catch (RuntimeException expected) { failed = true; }
    check(failed);
  }
  private static void check(boolean value) {
    assertions++;
    if (!value) throw new AssertionError("Assertion " + assertions);
  }
  private static void equal(Object expected, Object actual) {
    assertions++;
    if (!java.util.Objects.equals(expected, actual)) throw new AssertionError(expected + " != " + actual);
  }
}
