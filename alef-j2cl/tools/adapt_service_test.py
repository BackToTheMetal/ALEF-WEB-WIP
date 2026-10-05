#!/usr/bin/env python3
"""Browser boundaries for the service/test language profile.

Keep generated language semantics; desktop generation and disk export are not
browser services. Never report their operations as successfully completed.
"""
import re
from adapt_helpers import edit
from adapt_translators import body, save, end_at
from inventory import code_only


def main():
    # TemporaryModels uses the global repository. Its ModelAccess cannot answer
    # isCommandAction(); the editor's project owns the running test command.
    def debugger_lifecycle(s):
        s = s.replace('ModelAccess modelAccess = tmpModel.getRepository().getModelAccess();',
                      'ModelAccess modelAccess = MPSUtil.mpsProject().getModelAccess();')
        s = body(s, 'public void setDebugging(SNode rootAction, boolean value)', '''    if (isDebugging(rootAction) == value) return;
    // isDebugged is a virtual property backed by this trace. Do not write the
    // source node to refresh it: that dirties the workspace and fails without
    // an editing lease. Removing the trace invalidates its dependent editors.
    SNode previous = debugRootAction;
    debugRootAction = null;
    this.rootAction = null;
    debugNodes = null;
    focusManager.clear();
    actions.clear();
    contexts.clear();
    objects.clear();
    errors.clear();
    disposeOfTemporalRootNode(previous);
    RuntimeController.INSTANCE.reset();''')
        return body(s, 'private SNode makeDebugRootAction(RtRootActionRun action, SNode debugCtx)', '''    // Also clear caches after an explicit stop, when debugRootAction is null.
    disposeOfTemporalRootNode(debugRootAction);
    focusManager.clear();
    objects.clear();
    errors.clear();
    contexts.clear();
    actions.clear();
    debugNodes = new DebugNodes(action, objects);
    debugRootAction = debugNodes.makeDebugRootAction(debugCtx);
    addTemporalRootNode(debugRootAction);
    return debugRootAction;''')
    edit('solutions/interpreter.runtime/source_gen/interpreter/runtime/Debugger.java', debugger_lifecycle)
    def runtime_lifecycle(s):
        s = s.replace('ModelAccess modelAccess = tmpModel.getRepository().getModelAccess();',
                      'ModelAccess modelAccess = MPSUtil.mpsProject().getModelAccess();')
        s = s.replace('    currentRtRootAction = RtRootActionRun.of(rootAction, tracer);',
                      '    reset();\n    currentRtRootAction = RtRootActionRun.of(rootAction, tracer);')
        return body(s, 'public void reset()', '''    currentRtRootAction = null;
    if (tmpModel != null) {
      SModel previous = tmpModel;
      tmpModel = null;
      TMP_MODELS.dispose(previous);
    }''')
    edit('solutions/interpreter.runtime/source_gen/interpreter/runtime/RuntimeController.java', runtime_lifecycle)
    edit('solutions/interpreter.runtime/source_gen/interpreter/runtime/DebugFocusManager.java', lambda s:
         body(s, 'public void clear()', '''    initialized = false;
    foci.clear();
    debugActions.clear();'''))
    # Generated debug controls refer to desktop module PNGs. Keep the same
    # generated actions and layout, using font glyphs available in the browser.
    def debug_icons(s):
        for name, glyph in [('expand-right', '\u25b8'), ('expand', '\u25be'),
                            ('Eye-16', '\u25c9'), ('Pen-16', '\u270e')]:
            old = ('EditorCell_Image editorCell = EditorCell_Image.createImageCell(getEditorContext(), myNode, '
                   'SNodeOperations.getConcept(myNode).getLanguage().getSourceModule(), "${module}/icons/' + name + '.png");')
            if old not in s:
                raise ValueError('Generated debug image changed: ' + name)
            s = s.replace(old, 'EditorCell_Constant editorCell = new EditorCell_Constant(getEditorContext(), myNode, "' + glyph + '");\n'
                              '    editorCell.getStyle().set(StyleAttributes.EDITABLE, false);')
        return s.replace('    editorCell.setDescent(4);\n', '')
    edit('languages/interpreter.debug/source_gen/interpreter/debug/editor/DebugAction_ShowInDebugOverview_EditorBuilder_a.java', debug_icons)
    # Error messages are content, not editor-definition nodes. The latter need
    # the desktop editor language and otherwise render as CellModel_Constant.
    edit('languages/interpreter.debug/source_gen/interpreter/debug/editor/DebugThrowable_EditorBuilder_a.java', lambda s:
         body(s, 'private EditorCell createCustom_0()', '''    jetbrains.mps.nodeEditor.cells.EditorCell_Collection editorCell =
        new jetbrains.mps.nodeEditor.cells.EditorCell_Collection(getEditorContext(), myNode,
            new jetbrains.mps.nodeEditor.cellLayout.CellLayout_Vertical());
    editorCell.setCellId("Custom_fajra7_a0");
    for (String line : SPropertyOperations.getString(myNode, PROPS.message$IgDZ).split("\\n")) {
      jetbrains.mps.nodeEditor.cells.EditorCell_Constant cell =
          new jetbrains.mps.nodeEditor.cells.EditorCell_Constant(getEditorContext(), myNode, line);
      cell.getStyle().set(jetbrains.mps.editor.runtime.style.StyleAttributes.EDITABLE, false);
      editorCell.addEditorCell(cell);
    }
    Style style = new StyleImpl();
    new DebugBasisStyleClass(this).apply(style, editorCell);
    editorCell.getStyle().putAll(style);
    return editorCell;'''))
    test = 'languages/validatie/source_gen/testspraak/'
    # A failed batch result should open debugging on the first click, rather
    # than just clearing the red result and requiring another click to run it.
    edit(test + 'editor/TestGevalActions.java', lambda s:
         s.replace('if (userState.knownResult() || SPropertyOperations.getBoolean(node, PROPS.isDebugged$bJeg)) {',
                   'if (Boolean.TRUE.equals(userState.result()) || SPropertyOperations.getBoolean(node, PROPS.isDebugged$bJeg)) {'))
    def batch_debug(s):
        s = s.replace('// we don\'t show details anyway', '// The first failure is traced after the batch finishes.')
        s = s.replace('HashMap<MemoKey, Object> memo = new HashMap<MemoKey, Object>();',
                      'SNode firstFailure = null;\n            HashMap<MemoKey, Object> memo = new HashMap<MemoKey, Object>();', 1)
        old = '''                  userState.setResult(false);
                }
              }
            }
          }
          EditorUtil.verversEditor(editorContext);'''
        new = '''                  userState.setResult(false);
                }
                if (firstFailure == null && Boolean.FALSE.equals(userState.result())) {
                  firstFailure = test;
                }
              }
            }
            if (firstFailure != null) {
              TestsRunConfiguration.runDebugger(editorContext, firstFailure, UserState.of(firstFailure), false);
            }
          }
          EditorUtil.verversEditor(editorContext);'''
        if old not in s:
            raise ValueError('Generated batch test execution changed')
        return s.replace(old, new)
    edit(test + 'editor/Actions_RunTests.java', batch_debug)
    service = 'languages/servicespraak/source_gen/servicespraak/'
    save(service + 'behavior/XmlSchemaRegister.java', '''package servicespraak.behavior;
import org.jetbrains.mps.openapi.model.SNode;
import org.jetbrains.mps.openapi.module.SRepository;
import jetbrains.mps.openapi.editor.EditorContext;
import jetbrains.mps.baseLanguage.closures.runtime._FunctionTypes;
/** XSD generation requires the desktop MPS generator. */
public final class XmlSchemaRegister {
  public XmlSchemaRegister(EditorContext context) {}
  public XmlSchemaRegister(SRepository repository) {}
  private UnsupportedOperationException unavailable() {
    return new UnsupportedOperationException("XSD generation and registration require desktop MPS");
  }
  public void getSchemaAndThen(SNode service, _FunctionTypes._void_P1_E0<? super SNode> next) { throw unavailable(); }
  public boolean checkSchema(SNode service, SNode expected) { throw unavailable(); }
  public void registerSchema(SNode xsd, SNode service) { throw unavailable(); }
  public void signaleerWijzigingXsd(SNode oldSchema, SNode newSchema) { throw unavailable(); }
  public void openXsdInEditor(SNode xsd) { throw unavailable(); }
}
''')
    def xsd(s):
        s = re.sub(r'^import (?:jetbrains.mps.text.TextGeneratorEngine|javax.xml\.[\w.]+|org.xml.sax.[\w.]+|java.io.StringReader);\n', '', s, flags=re.M)
        start = s.index('    String xsdText =')
        end = s.index('\n  public SAbstractConcept', start)
        return s[:start] + '''    typeCheckingContext.reportWarning(schema,
        "XSD validation requires desktop MPS", null, null, null, new NodeMessageTarget());
  }
''' + s[end:]
    edit(service + 'typesystem/check_Xsd_NonTypesystemRule.java', xsd)
    def service_behavior(s):
        s = s.replace('import jetbrains.mps.generator.template.TemplateQueryContext;\n', '')
        s = s.replace('TemplateQueryContext', 'Object')
        return body(s, 'static boolean restService_id6sIogtXOswF(',
                    '    throw new UnsupportedOperationException("Generator input mapping requires desktop MPS");')
    edit(service + 'behavior/Service__BehaviorDescriptor.java', service_behavior)
    def without_generator_methods(s):
        code = code_only(s)
        spans = []
        for match in re.finditer(r'  (?:public|private) static [^\n{]+\([^\n]*TemplateQueryContext[^\n]*\)\s*\{', code):
            spans.append((match.start(), end_at(code, match.end() - 1) + 1))
        for start, end in reversed(spans):
            s = s[:start] + s[end:]
        return s.replace('import jetbrains.mps.generator.template.TemplateQueryContext;\n', '').replace('import java.text.Normalizer;\n', '').replace('Normalizer.normalize(name, Normalizer.Form.NFD).replaceAll("[\\\\p{M}]", "")', 'alefj2cl.BrowserUnicode.stripMarks(name)')
    for relative in ('languages/objecten/source_gen/gegevensspraak/utils/JavaUtil.java',
                     'solutions/generatorUtils/source_gen/generatorUtils/IdUtil.java',
                     'solutions/generatorUtils/source_gen/generatorUtils/PathUtil.java'):
        edit(relative, without_generator_methods)
    edit('solutions/generatorUtils/source_gen/generatorUtils/XmlUtil.java', lambda s:
         s.replace('import org.apache.commons.lang3.StringEscapeUtils;\n', '')
          .replace('StringEscapeUtils.escapeXml10', 'alefj2cl.BrowserHelpers.escapeXml10'))
    # JetBrains JDOM is the browser's existing, package-relocated JDOM provider.
    for relative in ('solutions/xmlUtils/source_gen/xmlUtils/XMLReader.java',
                     'languages/validatie/source_gen/testspraak/translator/ServiceTestImporter.java'):
        edit(relative, lambda s: s.replace('org.jdom2.', 'org.jdom.'))


if __name__ == '__main__':
    main()
