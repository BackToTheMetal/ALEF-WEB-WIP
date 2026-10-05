#!/usr/bin/env python3
"""Reproduce browser action dispatch, including mandatory cleanup after finish failures."""
import argparse
import hashlib
import json
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
PATH = 'core/smodel/source/jetbrains/mps/smodel/ActionDispatcher.java'


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--check', action='store_true')
    arguments = parser.parse_args()
    original = (ROOT / 'mps' / PATH).read_text(encoding='utf-8')
    text = original

    def replace(before, after):
        nonlocal text
        if text.count(before) != 1:
            raise SystemExit('ActionDispatcher preimage changed: ' + before)
        text = text.replace(before, after, 1)

    replace('import org.jetbrains.annotations.NotNull;\nimport org.jetbrains.annotations.Nullable;\n\n', '')
    replace('import java.util.ArrayList;\n', 'import java.util.ArrayList;\nimport java.util.Collections;\n')
    replace('          onExit(toNotify.reversed());', '''          final ArrayList<T> finishing = new ArrayList<>(toNotify);
          Collections.reverse(finishing);
          onExit(finishing);''')
    start = text.index('  /*package*/ static boolean isControlFlowIDEA(')
    end = text.index('  private void logUnexpectedRuntimeException(', start)
    text = text[:start] + '''  /*package*/ static boolean isControlFlowIDEA(RuntimeException ex) {
    // Browser builds have no IntelliJ marker-interface reflection. Recognize the
    // linked platform cancellation types by name, including their subclasses.
    // Other exceptions still propagate unchanged and retain ordinary error logging.
    for (Class<?> cls = ex.getClass(); cls != null; cls = cls.getSuperclass()) {
      String name = cls.getName();
      if ("com.intellij.openapi.actionSystem.impl.AwaitSharedData".equals(name)
          || "com.intellij.openapi.progress.ProcessCanceledException".equals(name)) {
        return true;
      }
    }
    return false;
  }

''' + text[end:]
    replace('return String.format("%s. Thread %s, state %s, %d active clients.", msg, threadName, actualState, myActiveThreads.get());',
            'return msg + ". Thread " + threadName + ", state " + actualState + ", " + myActiveThreads.get() + " active clients.";')
    replace('formatMessageDetails(String.format("Expected %s, actual is %s", expected, actual))',
            'formatMessageDetails("Expected " + expected + ", actual is " + actual)')
    replace('    InterruptedException interruptedException = null;', '    InterruptedException interruptedException = null;\n    Throwable failure = null;')
    replace('      interruptedException = ex;\n    } finally {', '''      interruptedException = ex;
    } catch (RuntimeException | Error ex) {
      failure = ex;
    } finally {''')
    replace('          interruptedException = ex;\n        }', '''          interruptedException = ex;
        } catch (RuntimeException | Error ex) {
          failure = addFailure(failure, ex);
        }''')
    replace('      throw new RuntimeException(interruptedException);\n    }', '''      failure = addFailure(failure, new RuntimeException(interruptedException));
    }
    rethrow(failure);''')
    replace('          toNotify.forEach(myOnActionFinish);', '''          Throwable failure = null;
          for (T listener : toNotify) {
            try { myOnActionFinish.accept(listener); }
            catch (RuntimeException | Error ex) { failure = addFailure(failure, ex); }
          }''')
    replace('            myOnActionFinish.accept(myPrePostListener);\n          }', '''            try { myOnActionFinish.accept(myPrePostListener); }
            catch (RuntimeException | Error ex) { failure = addFailure(failure, ex); }
          }
          rethrow(failure);''')
    replace('      LOG.info(formatMessageDetails("Expected " + expected + ", actual is " + actual), new Throwable());\n    }\n  }', '''      LOG.info(formatMessageDetails("Expected " + expected + ", actual is " + actual), new Throwable());
    }
  }

  private static Throwable addFailure(Throwable first, Throwable next) {
    if (first == null) return next;
    if (first != next) first.addSuppressed(next);
    return first;
  }

  private static void rethrow(Throwable failure) {
    if (failure instanceof Error) throw (Error) failure;
    if (failure != null) throw (RuntimeException) failure;
  }''')
    manifest = {
        'path': PATH,
        'upstream_sha256': hashlib.sha256(original.encode()).hexdigest(),
        'replacement_sha256': hashlib.sha256(text.encode()).hexdigest(),
        'contract': 'Every finish listener and mandatory cleanup runs; first failure identity retained with later failures suppressed',
    }
    for output, value in ((ROOT / 'mps-web' / PATH, text),
                          (ROOT / 'compatibility/action-dispatcher.json', json.dumps(manifest, indent=2) + '\n')):
        if arguments.check:
            if not output.exists() or output.read_text(encoding='utf-8') != value:
                raise SystemExit('Stale action dispatcher artifact: ' + str(output))
        else:
            output.parent.mkdir(parents=True, exist_ok=True)
            if not output.exists() or output.read_text(encoding='utf-8') != value:
                output.write_text(value, encoding='utf-8', newline='\n')
    print('ActionDispatcher: ordering, cancellation recognition, failure identity and mandatory cleanup verified')


if __name__ == '__main__':
    main()
