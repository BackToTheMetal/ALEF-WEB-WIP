#!/usr/bin/env python3
"""Explicit browser adaptations, always written to external source copies."""
import json
from pathlib import Path
import re
from adapt_translators import body, end_at, save
from inventory import code_only

ROOT = Path(__file__).resolve().parents[1]


def edit(relative, operation):
    original = (ROOT / 'upstream-src' / relative).read_text()
    save(relative, operation(original))


def remove_method(text, signature):
    start = text.index(signature)
    end = end_at(code_only(text), text.index('{', start))
    return text[:start] + text[end + 1:]


def main():
    edit('languages/bronspraak/source_gen/bronspraak/editor/ShowOrHideMetatags.java', lambda s:
        body(s.replace('import jetbrains.mps.vcs.diff.ui.common.DiffEditor;\n', ''),
             'public boolean showMetatags()', '    return EditorProperties.get(node, KEY);'))
    edit('languages/objecten/source_gen/gegevensspraak/behavior/GegevensspraakLinguistics.java', lambda s:
        body(s.replace('import java.lang.reflect.InvocationTargetException;\n', ''),
             'public static LinguisticsAspectDescriptor getAspect()', '    return new gegevensspraak.linguistics.LinguaAspectDescriptor();'))
    edit('languages/regelspraak/source_gen/regelspraak/behavior/RegelspraakLinguistics.java', lambda s:
        body(s, 'public static LinguisticsAspectDescriptor getAspect()', '    return new regelspraak.linguistics.LinguaAspectDescriptor();'))
    edit('languages/regelspraak/source_gen/regelspraak/typesystem/check_ConsistentieRegel_NonTypesystemRule.java',
         lambda s: s.replace('jetbrains.mps.smodel.DefaultSModelDescriptor', 'jetbrains.mps.extapi.model.GeneratableSModel').replace('DefaultSModelDescriptor', 'GeneratableSModel'))
    def mpsutil(s):
        s = s.replace('  public static Collection<SLanguage> importedLanguages(SLanguage slanguage) {',
                      '  public static Collection<SLanguage> importedLanguages(SLanguage slanguage) {\n'
                      '    if (slanguage.getSourceModule() == null) return alefj2cl.BrowserLanguageImports.importedLanguages(slanguage);')
        for signature in ('public static String getCurrentGitBranchName(SModel',
                          'public static String getCurrentGitBranchName(com.intellij',
                          'public static String getCurrentGitBranchWorkAroundMPS20243(',
                          'public static void setAlefHomeMacro()', 'public static void setProjectMacro('):
            s = remove_method(s, signature)
        s = s.replace('import jetbrains.mps.workbench.MPSDataKeys;', 'import jetbrains.mps.ide.actions.MPSCommonDataKeys;')
        s = s.replace('MPSDataKeys.MPS_PROJECT', 'MPSCommonDataKeys.MPS_PROJECT')
        s = s.replace('import java.util.Scanner;\n', '').replace('import com.intellij.openapi.application.PathMacros;\n', '')
        s = s.replace('      for (Class<?> i : c.getInterfaces()) {\n        traceClass(pre + "    ", i);\n      }\n', '')
        s = s.replace('String.format("<html><body>%s</body></html>", messagesBuilder.toString()), title, option', 'title + "\\n" + String.join("\\n", messages)')
        return s.replace('import org.apache.commons.lang3.StringEscapeUtils;\n', '').replace('StringEscapeUtils.escapeHtml4', 'alefj2cl.BrowserHelpers.escapeHtml')
    edit('solutions/MpsUtils/source_gen/mpsUtils/MPSUtil.java', mpsutil)
    edit('languages/bronspraak/source_gen/bronspraak/behavior/JuriConnectWetsReferentie__BehaviorDescriptor.java',
         lambda s: s.replace('import org.apache.commons.lang3.StringEscapeUtils;\n', '').replace('StringEscapeUtils.escapeHtml4', 'alefj2cl.BrowserHelpers.escapeHtml'))
    def timeslider(s):
        s = s.replace('import javax.swing.event.AncestorListener;\n', '').replace('import javax.swing.event.AncestorEvent;\n', '')
        start = s.index('    slider.addAncestorListener(')
        end = s.index('    slider.setMajorTickSpacing', start)
        s = s[:start] + '''    slider.addHierarchyListener(event -> {
      boolean showing = slider.isShowing();
      if (active != showing) { active = showing; updateDependents(); }
    });
    slider.addChangeListener(event -> updateDependents());
''' + s[end:]
        s = s.replace('    slider.setMajorTickSpacing(1);\n    slider.setPaintTicks(true);\n    slider.setPaintLabels(true);', '''    javax.swing.JLabel year = new javax.swing.JLabel(Integer.toString(now));
    slider.addChangeListener(event -> year.setText(Integer.toString(slider.getValue())));
    this.add(year);''')
        return s
    edit('languages/regelspraak/source_gen/regelspraak/plugin/TimeSlider.java', timeslider)
    def messages(s):
        for cls in ('MessagesViewTool', 'MessageListOptions', 'MessageList'):
            s = s.replace('import jetbrains.mps.ide.messages.' + cls + ';\n', '')
        return body(s, 'private static void internalMessage(', '''    toConsole(message, messageKind);
    alefj2cl.BrowserMessages.publish(messageTabTitle, new Message(messageKind, message));''')
    edit('solutions/MpsUtils/source_gen/mpsUtils/AlefMsg.java', messages)
    edit('languages/objecten/source_gen/gegevensspraak/behavior/Eenheid.java', lambda s:
         s.replace('f.numerator().sqrtAndRemainder()', 'alefj2cl.BrowserHelpers.sqrtAndRemainder(f.numerator())').replace('f.denominator().sqrtAndRemainder()', 'alefj2cl.BrowserHelpers.sqrtAndRemainder(f.denominator())'))
    manifest = json.loads((ROOT / 'out/source-inventory.json').read_text())
    originals = {replacement: original for original, replacement in manifest['overrides'].items()}
    for selected in manifest['sources']:
        path = originals.get(selected, selected)
        if not path.startswith(('upstream-src/', 'alef/')):
            continue
        s = (ROOT / path).read_text()
        if 'StringBuilder b' in s and 'b.isEmpty()' in s:
            relative = path.removeprefix('upstream-src/').removeprefix('alef/')
            existing = ROOT / 'j2cl-src/alef' / relative
            s = existing.read_text() if existing.exists() else s
            save(relative, s.replace('b.isEmpty()', '(b.length() == 0)'))
        if path.endswith('/JuriConnect.java'):
            save(path.removeprefix('upstream-src/'), s.replace('aURL.getPath()', 'alefj2cl.BrowserHelpers.urlPath(aURL.toString())'))
    edit('solutions/strings/source_gen/strings/StringUtil.java', lambda s:
         s.replace('System.getProperty(name, System.getenv(name))', 'alefj2cl.BrowserEnvironment.get(name)'))
    edit('solutions/editorUtils/source_gen/editorUtils/StyleConstants.java', lambda s:
         s.replace('EditorColorsManager.getInstance().getGlobalScheme().getName().toLowerCase().contains("dark")',
                   '(EditorColorsManager.getInstance().getGlobalScheme().getDefaultBackground().getRed() + EditorColorsManager.getInstance().getGlobalScheme().getDefaultBackground().getGreen() + EditorColorsManager.getInstance().getGlobalScheme().getDefaultBackground().getBlue() < 384)')
          .replace('System.getProperty("os.name")', 'System.getProperty("os.name", "browser")')
          .replace('Linux();', 'Linux(), Browser();')
          .replace('      return null;', '      return OS.Browser;')
          .replace('      case Linux:', '      case Browser:\n        return EditorColorsManager.getInstance().getGlobalScheme().getEditorFontName();\n      case Linux:'))
    # Direct renderer constructors also retain the generated inheritance fallback.
    registry = '''package alefj2cl;
import org.jetbrains.mps.openapi.model.SNode;
import linguistics.runtime.Renderer;
public final class RendererRegistry {
  public static Renderer create(String name, SNode node) {
    switch (name) {
'''
    generated = json.loads((ROOT / 'out/generated-sources.json').read_text())['sources']
    enums = []
    for row in generated:
        path = ROOT / row['path']
        if not path.name.endswith('_Renderer.java'):
            continue
        text = path.read_text()
        package = re.search(r'^package ([\w.]+);', text, re.M)[1]
        if package.rsplit('.', 1)[0] not in manifest['languages']:
            continue
        if re.search(r'public abstract class', text):
            continue
        fqn = package + '.' + path.stem
        if 'extends EnumRenderer' in text:
            enums.append(fqn)
            continue
        registry += '      case "' + fqn + '": return new ' + fqn + '(node);\n'
    registry += '      default: return null;\n    }\n  }\n'
    registry += '  public static linguistics.runtime.EnumRenderer createEnum(String name) {\n    switch (name) {\n'
    for fqn in enums:
        registry += '      case "' + fqn + '": return new ' + fqn + '();\n'
    registry += '      default: return null;\n    }\n  }\n}\n'
    (ROOT / 'browser-src/alefj2cl/RendererRegistry.java').write_text(registry)
    def renderer(s):
        s = s.replace('import java.beans.Expression;\n', '')
        s = body(s, 'public static Renderer forNode(', '''    Renderer renderer = getRendererForConcept(SNodeOperations.getConcept(node), node);
    if (renderer == null && SNodeOperations.isInstanceOf(node, CONCEPTS.BaseCommentAttribute$nv)) return EMPTY_RENDERER;
    return renderer;''')
        start = s.index('  private static Class<Renderer> getRendererClassForConcept(')
        end = end_at(code_only(s), s.index('{', start))
        s = s[:start] + '''  private static Renderer getRendererForConcept(SAbstractConcept concept, SNode node) {
    String name = concept.getLanguage().getQualifiedName() + ".linguistics." + getRendererClassNameForConcept(concept.getName());
    Renderer renderer = alefj2cl.RendererRegistry.create(name, node);
    if (renderer != null) return renderer;
    for (SAbstractConcept parent : SConceptOperations.getDirectSuperConcepts(concept, false)) {
      renderer = getRendererForConcept(parent, node);
      if (renderer != null) return renderer;
    }
    return null;
  }''' + s[end + 1:]
        return s
    edit('plugins/linguistics/solutions/linguistics.runtime/source_gen/linguistics/runtime/Renderer.java', renderer)
    edit('plugins/linguistics/solutions/linguistics.runtime/source_gen/linguistics/runtime/EnumRenderer.java', lambda s:
         body(s.replace('import java.beans.Expression;\n', ''), 'public static EnumRenderer forEnum(', '''    String name = INamedConcept__BehaviorDescriptor.getFqName_idhEwIO9y.invoke(enumDecl).replace(".structure.", ".linguistics.") + "_Renderer";
    EnumRenderer renderer = alefj2cl.RendererRegistry.createEnum(name);
    return renderer == null ? new DefaultEnumRenderer() : renderer;'''))
    relative = 'plugins/mps-tooltips/languages/tooltips.runtime/source_gen/jetbrains/mps/lang/editor/tooltips/runtime/TooltipTimingProperties.java'
    source = (ROOT.parent / 'mps-j2cl/mps' / relative).read_text()
    source = source.replace('Registry.intValue("ide.tooltip.initialDelay")', 'Integer.parseInt(System.getProperty("ide.tooltip.initialDelay", "500"))')
    source = source.replace('Registry.intValue("ide.tooltip.reshowDelay")', 'Integer.parseInt(System.getProperty("ide.tooltip.reshowDelay", "500"))')
    target = ROOT / 'j2cl-src/mps' / relative
    target.parent.mkdir(parents=True, exist_ok=True)
    target.write_text(source)


if __name__ == '__main__':
    main()
