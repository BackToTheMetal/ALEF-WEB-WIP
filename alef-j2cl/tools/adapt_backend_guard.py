#!/usr/bin/env python3
"""Consumer-owned node mutation guards. Never modifies the MPS checkout."""
import hashlib,json
from pathlib import Path
ROOT=Path(__file__).resolve().parents[1]
MPS=ROOT.parent/'mps-j2cl/mps'
registry=ROOT/'mps-access.json'
entries=json.loads(registry.read_text())
def write(relative,transform,source=None):
    source=source or MPS/relative
    target=ROOT/'j2cl-src/mps'/relative
    result=transform(source.read_text())
    target.parent.mkdir(parents=True,exist_ok=True)
    target.write_text(result)
    row={'original':relative,'replacement':target.relative_to(ROOT).as_posix(),
         'base':'../'+source.relative_to(ROOT.parent).as_posix(),'base_sha256':hashlib.sha256(source.read_bytes()).hexdigest()}
    entries[:]=[old for old in entries if old['original']!=relative]+[row]
def node(text):
    old='  private void assertCanChange() {\n    myOwner.assertLegalChange();'
    assert old in text
    text=text.replace(old,'''  /** Application policy hook at the actual node mutation boundary, including undo. */
  public interface BrowserChangeGuard {
    boolean canChangeNode(org.jetbrains.mps.openapi.model.SNode node);
    void beforeNodeChange(org.jetbrains.mps.openapi.model.SNode node);
    void beforeRootChange(org.jetbrains.mps.openapi.model.SModel model, org.jetbrains.mps.openapi.model.SNode root, boolean adding);
  }
  private static BrowserChangeGuard browserChangeGuard;
  public static void setBrowserChangeGuard(BrowserChangeGuard guard) { browserChangeGuard = guard; }
  public static boolean isBrowserEditable(org.jetbrains.mps.openapi.model.SNode node) {
    return browserChangeGuard == null || browserChangeGuard.canChangeNode(node);
  }
  static void checkBrowserRootChange(org.jetbrains.mps.openapi.model.SModel model, org.jetbrains.mps.openapi.model.SNode root, boolean adding) {
    if (browserChangeGuard != null) browserChangeGuard.beforeRootChange(model, root, adding);
  }
  private void assertCanChange() {
    myOwner.assertLegalChange();
    if (browserChangeGuard != null) browserChangeGuard.beforeNodeChange(this);''')
    # Editor focus/flush can reassign the current property value without changing
    # the model. Preserve MPS command checks, but enforce leases only on a change.
    old='''  public void setProperty(@NotNull final SProperty property, String propertyValue) {
    assertCanChange();

    int index = getPropertyIndex(property);
    final String oldValue = index == -1 ? null : (String) myProperties[index + 1];
    if (Objects.equals(oldValue, propertyValue)) return;'''
    assert old in text
    return text.replace(old,old.replace('    assertCanChange();','    myOwner.assertLegalChange();')+'\n    if (browserChangeGuard != null) browserChangeGuard.beforeNodeChange(this);')
def model(text):
    old='  public void addRootNode(@NotNull final org.jetbrains.mps.openapi.model.SNode node) {'
    assert old in text
    text=text.replace(old,old+'\n    SNode.checkBrowserRootChange(myModelDescriptor, node, true);')
    old='  public void removeRootNode(final org.jetbrains.mps.openapi.model.SNode node) {'
    assert old in text
    return text.replace(old,old+'\n    SNode.checkBrowserRootChange(myModelDescriptor, node, false);')
write('core/kernel/source/jetbrains/mps/smodel/SNode.java',node)
write('core/kernel/source/jetbrains/mps/smodel/SModel.java',model)
def quickfix(text):
    old='  private void execute(EditorContext editorContext) {'
    assert old in text
    return text.replace(old,old+'\n    // Deferred automatic fixes must recheck the current editing lease.\n    if (!jetbrains.mps.smodel.SNode.isBrowserEditable(editorContext.getEditorComponent().getEditedNode())) return;')
write('editor/editor-runtime/source/jetbrains/mps/nodeEditor/checking/QuickFixRuntimeEditorWrapper.java',quickfix)

def editor(text):
    old='''  public boolean isReadOnly() {
    return myReadOnly;
  }'''
    assert text.count(old)==1
    return text.replace(old,'''  public boolean isReadOnly() {
    // Query the live root/model lease before native input reaches a cell. Keep
    // mouse selection/navigation available; the model guard still checks writes.
    return myReadOnly || getModelAccess().computeReadAction(
        () -> !jetbrains.mps.smodel.SNode.isBrowserEditable(getEditedNode()));
  }''')
editor_path='editor/editor-runtime/source/jetbrains/mps/nodeEditor/EditorComponent.java'
extensions=ROOT.parent/'mps-extensions-j2cl'
editor_base=next(row for row in json.loads((extensions/'mps-access.json').read_text()) if row['original']==editor_path)
write(editor_path,editor,extensions/'j2cl-src'/editor_base['replacement'])

def input_method(text):
    old='  public void inputMethodTextChanged(InputMethodEvent event) {'
    assert text.count(old)==1
    return text.replace(old,old+'''
    // Browser text insertion/IME dispatches here without a key-typed event.
    if (myEditorComponent.getEditorContext().getRepository().getModelAccess().computeReadAction(() ->
        jetbrains.mps.editor.runtime.cells.ReadOnlyUtil.isSelectionReadOnlyInEditor(myEditorComponent))) {
      myUncommittedText = "";
      event.consume();
      return;
    }''')
write('editor/editor-runtime/source/jetbrains/mps/nodeEditor/ui/InputMethodListenerImpl.java',input_method)
registry.write_text(json.dumps(entries,indent=2)+'\n')
