#!/usr/bin/env python3
"""Reproduce the narrowly adapted keymap, selection and editor painting overlays."""
from prepare_editor_shortcuts import adapt_editor
import argparse
import re
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
BASE = 'editor/editor-runtime/source/jetbrains/mps/nodeEditor/'


def replace_once(text, old, new):
    if text.count(old) != 1:
        raise SystemExit('Upstream editor source changed: ' + old[:100])
    return text.replace(old, new, 1)


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--check', action='store_true')
    args = parser.parse_args()
    key_event = ROOT.parent / 'java-awt-j2cl/src/main/java/java/awt/event/KeyEvent.java'
    constants = re.findall(r'public static final int (VK_\w+)\s*=', key_event.read_text(encoding='utf-8'))
    if len(constants) < 180 or len(constants) != len(set(constants)):
        raise SystemExit('Unexpected sibling KeyEvent constant inventory')
    sources = {}
    relative = BASE + 'keymaps/AWTKeymapHandler.java'
    text = (ROOT / 'mps' / relative).read_text(encoding='utf-8')
    text = text.replace('import java.lang.reflect.Field;\n', '')
    text = text.replace('TIntObjectHashMap<String>', 'TIntObjectHashMap<List<String>>')
    start = text.index('  static {\n')
    end = text.index('  } // static init', start) + len('  } // static init')
    statements = ['  static {', '    // Explicit sibling AWT constants; retain aliases sharing the same numeric code.']
    for name in constants:
        if name not in ['VK_CONTROL', 'VK_ALT', 'VK_SHIFT']:
            statements.append(f'    registerKey(KeyEvent.{name}, "{name}");')
    statements += ['  }', '', '  private static void registerKey(int code, String name) {',
                   '    List<String> names = ourJavaKeyCodesMap.get(code);',
                   '    if (names == null) {', '      names = new ArrayList<>();',
                   '      ourJavaKeyCodesMap.put(code, names);', '    }',
                   '    names.add(name);', '  }']
    text = text[:start] + '\n'.join(statements) + text[end:]
    text = replace_once(text, '      result.add(value);', '      result.addAll(value);')
    text = replace_once(text, 'String keyCodeName = ourJavaKeyCodesMap.get(keyCode);',
                        'List<String> keyCodeNames = ourJavaKeyCodesMap.get(keyCode);')
    text = replace_once(text, 'if (keyCodeName != null) {\n        keyCodes.add(keyCodeName);',
                        'if (keyCodeNames != null) {\n        keyCodes.addAll(keyCodeNames);')
    sources[relative] = text

    relative = BASE + 'selection/EditorCellSelection.java'
    text = (ROOT / 'mps' / relative).read_text(encoding='utf-8')
    text = replace_once(text, 'package jetbrains.mps.nodeEditor.selection;\n',
                        'package jetbrains.mps.nodeEditor.selection;\n\nimport jetbrains.mps.classloading.ModuleClassLoader;\n')
    text = replace_once(text, 'new SelectionInfoImpl(this.getClass().getName())',
                        'new SelectionInfoImpl(ModuleClassLoader.getRegisteredClassName(getClass()))')
    sources[relative] = text

    relative = BASE + 'selection/SelectionInfoImpl.java'
    text = (ROOT / 'mps' / relative).read_text(encoding='utf-8')
    text = text.replace('import java.lang.reflect.Constructor;\n', 'import jetbrains.mps.classloading.ModuleClassLoader;\n')
    text = text.replace('import java.lang.reflect.InvocationTargetException;\n', '')
    text = replace_once(text, 'getClass().getClassLoader().loadClass(mySelectionClassName)',
                        'ClassLoader.getSystemClassLoader().loadClass(mySelectionClassName)')
    old = '''      Constructor<Selection> constructor = ((Class<Selection>) selectionClass).getConstructor(EditorComponent.class, Map.class, CellInfo.class);
      return constructor.newInstance(editorComponent, myProperties, myCellInfo);'''
    new = '''      try {
        return ModuleClassLoader.newInstance(selectionClass, Selection.class,
            new Class<?>[] {EditorComponent.class, Map.class, CellInfo.class}, editorComponent, myProperties, myCellInfo);
      } catch (Error constructorFailure) {
        // JVM reflection wrapped constructor Errors in InvocationTargetException.
        LOG.error(constructorFailure);
        return null;
      }'''
    text = replace_once(text, old, new)
    start = text.index('    } catch (ClassNotFoundException |')
    end = text.index('\n  public void persistToXML', start)
    text = text[:start] + '''    } catch (SelectionRestoreException e) {
      // Stale model/cell selections are expected and must not become error messages.
      return null;
    } catch (Exception e) {
      LOG.error(e);
      return null;
    }
  }
''' + text[end:]
    text = replace_once(text, '        return Enum.valueOf(enumClass, propertyValue);', '''        // Enum.valueOf(Class, String) needs JVM enum reflection. The only enum
        // used by MPS selection persistence has a generated static parser.
        if (enumClass == Selection.SelectionDirection.class) {
          return Selection.SelectionDirection.valueOf(propertyValue);
        }
        return Enum.valueOf(enumClass, propertyValue);''')
    sources[relative] = text

    relative = BASE + 'selection/NodeRangeSelection.java'
    text = (ROOT / 'mps' / relative).read_text(encoding='utf-8')
    text = text.replace('import java.lang.reflect.Constructor;\n', 'import jetbrains.mps.classloading.ModuleClassLoader;\n')
    text = text.replace('import java.lang.reflect.InvocationTargetException;\n', '')
    text = text.replace('Invoked via reflection.', 'Invoked through a registered compiled constructor factory.')
    text = replace_once(text, 'new SelectionInfoImpl(this.getClass().getName())',
                        'new SelectionInfoImpl(ModuleClassLoader.getRegisteredClassName(getClass()))')
    text = replace_once(text, 'myFilter.getClass().getName()',
                        'ModuleClassLoader.getRegisteredClassName(myFilter.getClass())')
    text = replace_once(text, '(Class<? extends RangeSelectionFilter>) Class.forName(filterClassName)',
                        'ClassLoader.getSystemClassLoader().loadClass(filterClassName).asSubclass(RangeSelectionFilter.class)')
    start = text.index('      Constructor<? extends RangeSelectionFilter> constructor')
    end = text.index('\n\n    result.loadFilter(properties);', start)
    text = text[:start] + '''      try {
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
    }''' + text[end:]
    sources[relative] = text

    relative = BASE + 'EditorComponent.java'
    text = (ROOT / 'mps' / relative).read_text(encoding='utf-8')
    text = replace_once(text, '  private static final Logger LOG = Logger.getLogger(EditorComponent.class);', '''  private static final Logger LOG = Logger.getLogger(EditorComponent.class);

  // A separate canvas peer preserves native embedded controls and stays outside
  // the child list rebuilt by setRootCell(). Its paint callback uses the original
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
      getInputMethodRequests(); // Installs MPS's real InputMethodListenerImpl once.
      updatePeerCompositionEventHandler(this::processBrowserComposition);
      myBrowserInputMethodSupport.mount();
    }
  }

  @Override
  public void removeNotify() {
    myBrowserInputMethodSupport.unmount();
    updatePeerCompositionEventHandler(null);
    myBrowserPaintSurface.unmount();
    super.removeNotify();
  }

  @Override
  public void setBounds(int x, int y, int width, int height) {
    super.setBounds(x, y, width, height);
    if (myBrowserPaintSurface != null) myBrowserPaintSurface.resize();
    if (myBrowserInputMethodSupport != null) myBrowserInputMethodSupport.position();
  }

  @Override
  public Graphics getGraphics() {
    return myBrowserPaintSurface == null ? null : myBrowserPaintSurface.getGraphics();
  }

  private void processBrowserComposition(String type, String data, long when) {
    if (isDisposed() || !hasUI() || "compositionstart".equals(type)) return;
    String text = data == null ? "" : data;
    boolean committed = "compositionend".equals(type);
    // Opening/cancelling an empty IME session must not reset an MPS selection.
    if (text.isEmpty() && !myBrowserCompositionHasText) return;
    myBrowserCompositionHasText = !committed && !text.isEmpty();
    java.awt.font.TextHitInfo caret = java.awt.font.TextHitInfo.afterOffset(text.length());
    dispatchEvent(new java.awt.event.InputMethodEvent(this,
        java.awt.event.InputMethodEvent.INPUT_METHOD_TEXT_CHANGED, when,
        new org.webawt.internal.text.PlainAttributedCharacterIterator(text),
        committed ? text.length() : 0, caret, caret));
  }''')
    text = replace_once(text, '    myDisposed = true;\n', '    myDisposed = true;\n    myBrowserInputMethodSupport.dispose();\n    myBrowserPaintSurface.dispose();\n')
    text = replace_once(text, '    assert myModelDisposedStackTrace == null : getModelDisposedMessage();', """    // Keep diagnostic construction lazy even when browser assertion lowering
    // evaluates message arguments eagerly. A live model has no disposal trace.
    if (myModelDisposedStackTrace != null) {
      throw new AssertionError(getModelDisposedMessage());
    }""")
    text = replace_once(text, '    assert model.getRepository() != null : getNodeDisposedMessage(model);', """    if (model.getRepository() == null) {
      throw new AssertionError(getNodeDisposedMessage(model));
    }""")
    sources[relative] = adapt_editor(text)

    painting_members = '''private final javax.swing.BrowserPaintSurface myBrowserPaintSurface =
    new javax.swing.BrowserPaintSurface(this, this::paint);

@Override
public void addNotify() {
  super.addNotify();
  myBrowserPaintSurface.mount();
}

@Override
public void removeNotify() {
  myBrowserPaintSurface.unmount();
  super.removeNotify();
}

@Override
public void setBounds(int x, int y, int width, int height) {
  super.setBounds(x, y, width, height);
  if (myBrowserPaintSurface != null) myBrowserPaintSurface.resize();
}

@Override
public Graphics getGraphics() {
  return myBrowserPaintSurface == null ? null : myBrowserPaintSurface.getGraphics();
}
'''
    relative = BASE + 'leftHighlighter/LeftEditorHighlighter.java'
    text = (ROOT / 'mps' / relative).read_text(encoding='utf-8')
    declaration = 'public final class LeftEditorHighlighter extends JComponent implements UiDataProvider {\n'
    text = replace_once(text, declaration, declaration + '\n'.join('  ' + line if line else '' for line in painting_members.split('\n')) + '\n')
    text = replace_once(text, '  public void dispose() {\n', '  public void dispose() {\n    myBrowserPaintSurface.dispose();\n')
    sources[relative] = text

    relative = BASE + 'cellMenu/NodeSubstitutePatternEditor.java'
    text = (ROOT / 'mps' / relative).read_text(encoding='utf-8')
    declaration = '    private class EditorPanel extends JPanel {\n'
    text = replace_once(text, declaration, declaration + '\n'.join('      ' + line if line else '' for line in painting_members.split('\n')) + '\n')
    sources[relative] = text

    for relative, text in sources.items():
        output = ROOT / 'mps-web' / relative
        if args.check:
            if not output.is_file() or output.read_bytes() != text.encode('utf-8'):
                raise SystemExit('Stale editor overlay: ' + str(output))
        else:
            output.parent.mkdir(parents=True, exist_ok=True)
            output.write_text(text, encoding='utf-8', newline='\n')
    print(f'Editor runtime overlays: {len(sources)}; {len(constants)} AWT key names inventoried')


if __name__ == '__main__':
    main()
