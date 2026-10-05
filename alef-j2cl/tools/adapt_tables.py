#!/usr/bin/env python3
"""Adapt MPS cell metrics and replace table reflection with typed operations."""
import ast
import hashlib
import json
from pathlib import Path
import re

ROOT = Path(__file__).resolve().parents[1]
MPS = ROOT.parent / 'mps-j2cl'
EXT = ROOT.parent / 'mps-extensions-j2cl'


def replace(text, before, after):
    if text.count(before) != 1:
        raise RuntimeError('Expected one occurrence: ' + before)
    return text.replace(before, after)


def write_new(path, text):
    if path.exists():
        return
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(text)


def main():
    tree = ast.parse((MPS / 'mps-overrides.bzl').read_text())
    mps_overrides = next(ast.literal_eval(n.value) for n in tree.body if isinstance(n, ast.Assign) and n.targets[0].id == 'MPS_OVERRIDES')
    extension_overrides = {row['original']: row for row in json.loads((EXT / 'mps-access.json').read_text())}
    changes = {
        'editor/editorlang-runtime/source/jetbrains/mps/editor/runtime/style/StyleImpl.java': ('private Style getParentStyle()', 'public Style getParentStyle()'),
        'editor/editor-runtime/source/jetbrains/mps/nodeEditor/cells/EditorCell_Basic.java': ('  void setParent(EditorCell_Collection parent)', '  public void setParent(EditorCell_Collection parent)'),
        'editor/editor-runtime/source/jetbrains/mps/nodeEditor/cells/EditorCell_Collection.java': ('protected CellLayout myCellLayout;', 'public CellLayout myCellLayout;'),
        'editor/editor-runtime/source/jetbrains/mps/nodeEditor/cells/EditorCell_Label.java': ('  TextLine getTextLine()', '  public TextLine getTextLine()'),
        'editor/editor-runtime/source/jetbrains/mps/nodeEditor/cells/TextLine.java': ('private void updateStyle(Set<StyleAttribute> attributes)', 'public void updateStyle(Set<StyleAttribute> attributes)'),
        'editor/editor-runtime/source/jetbrains/mps/nodeEditor/cellLayout/CellLayout_Vertical.java': ('  boolean isGridLayout()', '  public boolean isGridLayout()'),
        'editor/editor-runtime/source/jetbrains/mps/nodeEditor/cells/EditorFontMetricsImpl.java': ('''    char[] chars = text.toCharArray();
    float result = 0f;
    for (int i = beginIndex; i < endIndex; i++) {
      result += FontLayoutService.getInstance().charWidth2D(getFontMetrics(), chars[i]);
    }

    return Math.round(result);''', '''    // Canvas paints complete text runs. Summing integer charWidth values loses
    // fractional advances on every character and lets long labels overlap.
    // Measure the same run (including kerning) and round only its final width up.
    return (int) Math.ceil(getFont().getStringBounds(text, beginIndex, endIndex, DEFAULT_FRC).getWidth());'''),
    }
    completion_renderers = [
        'editor/editor-runtime/source/jetbrains/mps/nodeEditor/cellMenu/NodeItemCellRenderer.java',
        'editor/editor-runtime/source/jetbrains/mps/nodeEditor/cellMenu/NodeSubstituteChooserUi.java',
    ]
    manifest = []
    for original in [*changes, *completion_renderers]:
        if original in extension_overrides:
            source = EXT / 'j2cl-src' / extension_overrides[original]['replacement']
        elif original in mps_overrides:
            source = MPS / 'mps-web' / mps_overrides[original]
        else:
            source = MPS / 'mps' / original
        target = ROOT / 'j2cl-src/mps' / original
        text = source.read_text()
        if original in changes:
            text = replace(text, *changes[original])
        if original.endswith('/cells/TextLine.java'):
            text = replace(text, 'private final Color myTextSelectedTextColor', 'private Color myTextSelectedTextColor')
            text = replace(text, 'private final Color myTextSelectedBackgroundColor', 'private Color myTextSelectedBackgroundColor')
            anchor = '  private Color myTextSelectedBackgroundColor = EditorSettings.getInstance().getSelectionBackgroundColor();\n'
            text = replace(text, anchor, anchor + (ROOT / 'tools/templates/text-line-highlights.java.inc').read_text())
            text = replace(text, '    if (myShowCaret) {', '    paintTextHighlights(g, shiftX, shiftY);\n    if (myShowCaret) {')
        if original.endswith('/EditorFontMetricsImpl.java'):
            text = text.replace('import com.intellij.openapi.editor.impl.view.FontLayoutService;\n', '')
            text = replace(text, 'new FontRenderContext(null, false, false)', 'new FontRenderContext(null, true, true)')
        write_new(target, text)
        manifest.append({'original': original, 'replacement': target.relative_to(ROOT).as_posix(),
                         'base': '../' + source.relative_to(ROOT.parent).as_posix(),
                         'base_sha256': hashlib.sha256(source.read_bytes()).hexdigest()})
    (ROOT / 'mps-access.json').write_text(json.dumps(manifest, indent=2) + '\n')
    for module, filename in [
        ('de.slisson.mps.tables.runtime', 'style/TableStyleUtils.java'),
        ('de.slisson.mps.tables.runtime', 'cells/CellLayout_Table.java'),
        ('de.itemis.mps.editor.celllayout.runtime', 'CellLayoutUtil.java'),
        ('de.itemis.mps.editor.celllayout.runtime', 'LayoutInterceptor.java'),
        ('de.itemis.mps.editor.celllayout.runtime', 'LayoutableCollectionAdapter.java'),
    ]:
        relative = module + '/' + module.replace('.', '/') + '/' + filename
        source = (EXT / 'upstream-src' / relative).read_text()
        if filename == 'style/TableStyleUtils.java':
            source = source.replace('import java.lang.reflect.Method;\n', '').replace('import java.lang.reflect.InvocationTargetException;\n', '')
            source = re.sub(r'  public static Style getParentStyle\(Style style\) \{.*?\n  \}',
                '  public static Style getParentStyle(Style style) {\n    return ((StyleImpl) style).getParentStyle();\n  }', source, count=1, flags=re.S)
        elif filename == 'cells/CellLayout_Table.java':
            source = source.replace('import java.lang.reflect.Field;\n', '')
            source = re.sub(r'  public static void setParent\(EditorCell child, EditorCell_Collection parent\) \{.*?\n  \}',
                '  public static void setParent(EditorCell child, EditorCell_Collection parent) {\n    ((EditorCell_Basic) child).setParent((jetbrains.mps.nodeEditor.cells.EditorCell_Collection) parent);\n  }', source, count=1, flags=re.S)
        elif filename == 'CellLayoutUtil.java':
            source = replace(source, 'ReflectionUtil.callVoidMethod(TextLine.class, ((TextLine) ReflectionUtil.callMethod(EditorCell_Label.class, cell, "getTextLine", new Class[]{}, new Object[]{})), "updateStyle", new Class[]{Set.class}, new Object[]{null});',
                'cell.getTextLine().updateStyle(null);')
            source = replace(source, 'ReflectionUtil.writeField(jetbrains.mps.nodeEditor.cells.EditorCell_Collection.class, ((jetbrains.mps.nodeEditor.cells.EditorCell_Collection) cell), "myCellLayout", new TopDownCellLayoutAdapter(layout));',
                '((jetbrains.mps.nodeEditor.cells.EditorCell_Collection) cell).myCellLayout = new TopDownCellLayoutAdapter(layout);')
        elif filename == 'LayoutInterceptor.java':
            source = replace(source, 'ReflectionUtil.writeField(jetbrains.mps.nodeEditor.cells.EditorCell_Collection.class, collectionCell, "myCellLayout", wrapped);',
                'collectionCell.myCellLayout = wrapped;')
            source = replace(source, 'ReflectionUtil.writeField(jetbrains.mps.nodeEditor.cells.EditorCell_Collection.class, collectionCell, "myCellLayout", ((CellLayout) CellLayoutExtensions.unwrapAll(layout)));',
                'collectionCell.myCellLayout = ((CellLayout) CellLayoutExtensions.unwrapAll(layout));')
            source = replace(source, '((Boolean) ReflectionUtil.callMethod(CellLayout_Vertical.class, checkedDotOperand, "isGridLayout", new Class[]{}, new Object[]{}))',
                'checkedDotOperand.isGridLayout()')
        else:
            source = replace(source, '((Boolean) ReflectionUtil.callMethod(CellLayout_Vertical.class, ((CellLayout_Vertical) cellLayout), "isGridLayout", new Class[]{}, new Object[]{}))',
                '((CellLayout_Vertical) cellLayout).isGridLayout()')
        if 'ReflectionUtil.' not in source:
            source = source.replace('import de.slisson.mps.reflection.runtime.ReflectionUtil;\n', '')
        write_new(ROOT / 'j2cl-src/extensions' / relative, source)
    additions = {
        'de.itemis.mps.editor.celllayout.runtime': [
            'de/itemis/mps/editor/celllayout/boxmodel/AbstractLayoutBox.java',
            'de/itemis/mps/editor/celllayout/boxmodel/CalcOnWriteFrame.java',
            'de/itemis/mps/editor/celllayout/boxmodel/DefaultBoxModel.java',
            'de/itemis/mps/editor/celllayout/runtime/MethodPolymorphismInfo.java',
        ],
        'de.slisson.mps.tables.runtime': [
            'de/slisson/mps/tables/runtime/gridmodel/GridElement.java',
            'de/slisson/mps/tables/runtime/gridmodel/HeaderReference.java',
            'de/slisson/mps/tables/runtime/simplegrid/SimpleGrid.java',
            'de/slisson/mps/tables/runtime/cells/EditorCell_GridCell.java',
        ],
    }
    for module, files in additions.items():
        for filename in files:
            relative = module + '/' + filename
            source = (EXT / 'upstream-src' / relative).read_text()
            if 'super.clone()' in source:
                source = replace(source, 'super.clone()', 'alefj2cl.extensions.ObjectCopy.copy(this)')
                if filename.endswith(('HeaderReference.java', 'SimpleGrid.java')):
                    source = source.replace('  @Override\n  public ' + Path(filename).stem, '  public ' + Path(filename).stem)
            if filename.endswith('EditorCell_GridCell.java'):
                source = source.replace('import com.intellij.ui.components.JBViewport;', 'import javax.swing.JViewport;').replace('JBViewport', 'JViewport')
                source = source.replace('import com.intellij.util.ObjectUtils;\n', '').replace('ObjectUtils.notNull(', 'java.util.Objects.requireNonNullElse(')
            if filename.endswith('CalcOnWriteFrame.java'):
                source = replace(source, 'clone.myResultBox.clone();', 'clone.myResultBox = myResultBox.clone();')
            if filename.endswith('MethodPolymorphismInfo.java'):
                source = re.sub(r'  protected boolean calcOverridesLayoutMethod\(Class cellClass\) \{.*?\n  \}', '''  protected boolean calcOverridesLayoutMethod(Class cellClass) {
    for (Class current = cellClass; current != null; current = current.getSuperclass()) {
      if (current == EditorCell_Collection.class || current == EditorCell_Basic.class) return false;
      if (alefj2cl.extensions.LayoutMethods.declaresRelayout(current)) return true;
    }
    return false;
  }''', source, count=1, flags=re.S)
            write_new(ROOT / 'j2cl-src/extensions' / relative, source)
    print(f'Prepared {len(manifest)} MPS overrides and thirteen table runtime adaptations')


if __name__ == '__main__':
    main()
