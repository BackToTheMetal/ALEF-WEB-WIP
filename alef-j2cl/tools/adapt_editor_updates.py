#!/usr/bin/env python3
"""Complete browser editor updates; preserve the shared MPS/extension sources."""
import hashlib
import json
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
MPS = ROOT.parent / 'mps-j2cl/mps'
EXT = ROOT.parent / 'mps-extensions-j2cl'
registry = ROOT / 'mps-access.json'
entries = json.loads(registry.read_text())


def write(relative, source, transform):
    text = transform(source.read_text())
    target = ROOT / 'j2cl-src/mps' / relative
    target.parent.mkdir(parents=True, exist_ok=True)
    target.write_text(text)
    entries[:] = [row for row in entries if row['original'] != relative] + [{
        'original': relative, 'replacement': target.relative_to(ROOT).as_posix(),
        'base': '../' + source.relative_to(ROOT.parent).as_posix(),
        'base_sha256': hashlib.sha256(source.read_bytes()).hexdigest(),
    }]


def updater(text):
    marker = '  private boolean myProcessSelection;'
    assert text.count(marker) == 1
    text = text.replace(marker, marker + '''
  private boolean myBrowserRefreshPending;
  private boolean myBrowserRebuildRequired;

  // Cell rebuilding and painting are separate operations in MPS. A direct
  // update (e.g. transient test results) has no model-event relayout afterwards.
  // Finish every update, including inspectors, once the current action is over.
  void requestBrowserRefresh(boolean rebuild) {
    if (myDisposed) return;
    myBrowserRebuildRequired |= rebuild;
    if (myBrowserRefreshPending) return;
    myBrowserRefreshPending = true;
    getEditorContext().getRepository().getModelAccess().runReadInEDT(() -> {
      try {
        if (myDisposed || myEditorComponent.isDisposed()) return;
        boolean rebuildRequired = myBrowserRebuildRequired;
        myBrowserRebuildRequired = false;
        if (rebuildRequired) update();
        myEditorComponent.relayout();
        myEditorComponent.validateExternalComponent();
      } finally {
        myBrowserRefreshPending = false;
      }
    });
  }
''')
    marker = '  private void fireCellSynchronized(EditorCell cell) {'
    assert text.count(marker) == 1
    text = text.replace(marker, marker + '\n    requestBrowserRefresh(false);')
    marker = '    myInProgress = false;\n  }'
    assert text.count(marker) == 1
    return text.replace(marker, '    myInProgress = false;\n    requestBrowserRefresh(false);\n  }')


def listener(text):
    marker = '  private final UpdaterImpl myUpdater;'
    assert text.count(marker) == 1
    return text.replace(marker, marker + '''

  // ModelsEventsCollector deliberately ignores writes outside commands. These
  // are legitimate for temporary/runtime models and read-only comparison data.
  // Observe the same dependency models and refresh after the write completes.
  private final org.jetbrains.mps.openapi.model.SNodeChangeListener myWriteListener =
      new org.jetbrains.mps.openapi.model.SNodeChangeListener() {
        private void changed() {
          if (!myUpdater.getEditorContext().getRepository().getModelAccess().isCommandAction()) {
            myUpdater.requestBrowserRefresh(true);
          }
        }
        @Override public void propertyChanged(org.jetbrains.mps.openapi.event.SPropertyChangeEvent e) { changed(); }
        @Override public void referenceChanged(org.jetbrains.mps.openapi.event.SReferenceChangeEvent e) { changed(); }
        @Override public void nodeAdded(org.jetbrains.mps.openapi.event.SNodeAddEvent e) { changed(); }
        @Override public void nodeRemoved(org.jetbrains.mps.openapi.event.SNodeRemoveEvent e) { changed(); }
      };

  @Override public void startListeningToModel(org.jetbrains.mps.openapi.model.SModel model) {
    super.startListeningToModel(model);
    model.addChangeListener(myWriteListener);
  }

  @Override public void stopListeningToModel(org.jetbrains.mps.openapi.model.SModel model) {
    model.removeChangeListener(myWriteListener);
    super.stopListeningToModel(model);
  }
''')


path = 'editor/editor-runtime/source/jetbrains/mps/nodeEditor/updater/UpdaterImpl.java'
base = next(row for row in json.loads((EXT / 'mps-access.json').read_text()) if row['original'] == path)
write(path, EXT / 'j2cl-src' / base['replacement'], updater)
path = 'editor/editor-runtime/source/jetbrains/mps/nodeEditor/updater/UpdaterModelListener.java'
write(path, MPS / path, listener)
registry.write_text(json.dumps(entries, indent=2) + '\n')
