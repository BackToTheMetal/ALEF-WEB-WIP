#!/usr/bin/env python3
"""Consumer-only transaction checkpoints around browser commands and undo groups."""
from pathlib import Path
root=Path(__file__).resolve().parents[1]
source=root.parent/'mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/command'
target=root/'j2cl-src/intellij/com/intellij/openapi/command'
text=(source/'CommandProcessor.java').read_text()
needle='  private int transparentDepth;'
assert needle in text
text=text.replace(needle,needle+'''
  public interface BrowserTransactionGuard { void begin(); void finish(boolean success); }
  private BrowserTransactionGuard browserGuard;
  private int browserDepth;
  private boolean browserFailed;
  public void setBrowserTransactionGuard(BrowserTransactionGuard guard) { browserGuard = guard; }
  public void beginBrowserTransaction() {
    if (browserDepth++ == 0) { browserFailed = false; if (browserGuard != null) browserGuard.begin(); }
  }
  public void endBrowserTransaction(boolean success) {
    browserFailed |= !success;
    if (--browserDepth == 0 && browserGuard != null) browserGuard.finish(!browserFailed);
  }
''')
needle='    try {\n      notifyListeners(listener -> listener.commandStarted(event));\n      action.run();\n    } finally {'
assert needle in text
text=text.replace(needle,'''    beginBrowserTransaction(); boolean succeeded = false;
    try {
      notifyListeners(listener -> listener.commandStarted(event));
      action.run(); succeeded = true;
    } finally {''')
needle='finally { command = null; this.name = null; this.group = null; this.project = null; token = null; }'
assert needle in text
text=text.replace(needle,'finally { command = null; this.name = null; this.group = null; this.project = null; token = null; endBrowserTransaction(succeeded); }')
needle='''    boolean outer = transparentDepth++ == 0;
    try { if (outer) notifyListeners(CommandListener::undoTransparentActionStarted); action.run(); }'''
assert needle in text
text=text.replace(needle,'''    boolean outer = transparentDepth++ == 0;
    beginBrowserTransaction(); boolean succeeded = false;
    try { if (outer) notifyListeners(CommandListener::undoTransparentActionStarted); action.run(); succeeded = true; }''')
needle='finally { transparentDepth--; if (outer) notifyListeners(CommandListener::undoTransparentActionFinished); }'
assert needle in text
text=text.replace(needle,'finally { try { transparentDepth--; if (outer) notifyListeners(CommandListener::undoTransparentActionFinished); } finally { endBrowserTransaction(succeeded); } }')
target.mkdir(parents=True,exist_ok=True);(target/'CommandProcessor.java').write_text(text)
undo=target/'undo/UndoManager.java'
# Preserve the consumer's existing undo-transparent quick-fix behavior.
text=undo.read_text() if undo.exists() else (source/'undo/UndoManager.java').read_text()
if 'CommandProcessor.getInstance().beginBrowserTransaction(); boolean succeeded' in text:
    raise SystemExit(0)
needle='    undoing = !forward; redoing = forward;\n    try {'
assert needle in text
text=text.replace(needle,'''    undoing = !forward; redoing = forward;
    CommandProcessor.getInstance().beginBrowserTransaction(); boolean succeeded = false;
    try {''')
needle='      if (historyVersion == version) { from.remove(from.size() - 1); to.add(frame); }'
assert needle in text
text=text.replace(needle,needle+'\n      succeeded = true;')
text=text.replace('finally { undoing = false; redoing = false; mergeAllowed = false; }','finally { undoing = false; redoing = false; mergeAllowed = false; CommandProcessor.getInstance().endBrowserTransaction(succeeded); }')
(target/'undo').mkdir(exist_ok=True);(target/'undo/UndoManager.java').write_text(text)
