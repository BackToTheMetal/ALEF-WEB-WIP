"""Regenerate the narrowly scoped Java compatibility replacements."""
from pathlib import Path
import hashlib
import json
import re

ROOT = Path(__file__).resolve().parents[1]
MPS = ROOT.parent / 'mps-j2cl'
records = []

def emit(source, content):
    package = re.search(r'^package ([\w.]+);', content, re.M).group(1).replace('.', '/')
    relative = package + '/' + source.name
    target = ROOT / 'j2cl-src' / relative
    target.parent.mkdir(parents=True, exist_ok=True)
    target.write_text(content)
    return relative

def replace_checked(content, before, after, count=1):
    assert content.count(before) == count, (before, content.count(before))
    return content.replace(before, after)

def mps(relative, replacements):
    source = MPS / 'mps-web' / relative
    if not source.exists():
        source = MPS / 'mps' / relative
    content = source.read_text()
    for before, after in replacements:
        content = replace_checked(content, before, after)
    replacement = emit(source, content)
    records.append({'original': relative, 'replacement': replacement, 'base': source.relative_to(MPS).as_posix(), 'base_sha256': hashlib.sha256(source.read_bytes()).hexdigest()})

prefix = 'editor/editor-runtime/source/jetbrains/mps/'
for path, replacements in {
    'nodeEditor/EditorComponent.java': [('private volatile EditorComponentActions myEditorComponentActions;', 'public volatile EditorComponentActions myEditorComponentActions;')],
    'nodeEditor/EditorComponentActions.java': [('class EditorComponentActions {', 'public class EditorComponentActions {'), ('private final HashMap<CellActionType, CellAction> myActionMap;', 'public final HashMap<CellActionType, CellAction> myActionMap;')],
    'nodeEditor/updater/UpdaterImpl.java': [('private Map<SNode, WeakReference<EditorCell>> myBigCellsMap', 'public Map<SNode, WeakReference<EditorCell>> myBigCellsMap')],
    'nodeEditor/cells/EditorCell_Label.java': [('private String getUpdatedText(String replacingText)', 'public String getUpdatedText(String replacingText)')],
    'nodeEditor/sidetransform/EditorCell_STHint.java': [('private final EditorCell myAnchorCell;', 'public final EditorCell myAnchorCell;')],
    'nodeEditor/cellActions/CellAction_InsertIntoCollection.java': [('private AbstractCellListHandler myListHandler;', 'public AbstractCellListHandler myListHandler;')],
    'lang/editor/menus/transformation/SubstituteMenuItemAsActionItem.java': [('private final SubstituteMenuItem mySubstituteItem;', 'public final SubstituteMenuItem mySubstituteItem;')],
    'nodeEditor/NodeEditorActions.java': [('public static class MoveDown extends NavigationAction {\n    private final CursorPositionTracker myPositionTracker;', 'public static class MoveDown extends NavigationAction {\n    public final CursorPositionTracker myPositionTracker;')],
}.items():
    mps(prefix + path, replacements)
(ROOT / 'mps-access.json').write_text(json.dumps(records, indent=2) + '\n')

def extension(module, package, name):
    return ROOT / 'upstream-src' / module / package.replace('.', '/') / (name + '.java')

p = extension('de.slisson.mps.hacks.editor', 'de.slisson.mps.hacks.editor', 'SubstituteUtil')
s = p.read_text()
a = s.index('      try {\n        Class<?> SubstituteMenuItemAsActionItem')
b = s.index('\n    }\n    return SNodeOperations', a)
s = s[:a] + '''      if (actionItem instanceof jetbrains.mps.lang.editor.menus.transformation.SubstituteMenuItemAsActionItem) {
        SubstituteMenuItem substituteItem = (SubstituteMenuItem) ReflectionUtil.readField(
            jetbrains.mps.lang.editor.menus.transformation.SubstituteMenuItemAsActionItem.class,
            actionItem, "mySubstituteItem");
        return substituteItem.getOutputConcept();
      }''' + s[b:]
emit(p, s)

p = extension('de.slisson.mps.hacks.editor', 'de.slisson.mps.hacks.editor', 'EditorComponentHacks')
s = p.read_text().replace('import com.intellij.ui.tabs.JBTabs;\n', '')
s = replace_checked(s, 'collect(tabPane.getTabComponentAt(i));', 'collect(tabPane.getComponentAt(i));')
a = s.index('        } else if (component instanceof JBTabs)')
b = s.index('        } else if (component instanceof Container)', a)
s = s[:a] + s[b:]
emit(p, s)

p = extension('com.mbeddr.mpsutil.grammarcells.runtime', 'com.mbeddr.mpsutil.grammarcells.runtime', 'InputCursor')
s = p.read_text()
a = s.index('  @Override\n  protected InputCursor clone()')
b = s.index('\n  public Iterator<InputCursor[]>', a)
s = s[:a] + '''  protected InputCursor clone() {
    InputCursor copy = new InputCursor(myInput);
    copy.myPosition = myPosition;
    return copy;
  }
''' + s[b:]
emit(p, s)
p = extension('com.mbeddr.mpsutil.grammarcells.runtime', 'com.mbeddr.mpsutil.grammarcells.runtime', 'GrammarCellsUtil')
s = replace_checked(p.read_text(), 'getDescriptor(it.getSourceModule())', 'DescriptorCache.getInstance().getDescriptorByReference(it.getSourceModuleReference())')
s = s.replace('import jetbrains.mps.lang.structure.behavior.AbstractConceptDeclaration__BehaviorDescriptor;\n', '')
a = s.index('    final SNode linkDecl = genuineLink.getDeclarationNode();')
b = s.index('\n    return genuineLink.getTargetConcept();', a)
s = s[:a] + '''    // The compiled structure descriptors retain link specialization without language-design models.
    jetbrains.mps.smodel.language.StructureRegistry registry =
        jetbrains.mps.smodel.language.ConceptRegistry.getInstance().getStructureRegistry();
    jetbrains.mps.smodel.runtime.ConceptDescriptor descriptor =
        registry.getConceptDescriptor(jetbrains.mps.smodel.adapter.ids.MetaIdHelper.getConcept(owner));
    jetbrains.mps.smodel.adapter.ids.SContainmentLinkId linkId =
        jetbrains.mps.smodel.adapter.ids.MetaIdHelper.getAggregation(genuineLink);
    if (descriptor != null && descriptor.getLinkDescriptor(linkId) != null) {
      jetbrains.mps.smodel.adapter.ids.SConceptId target = descriptor.getMostSpecificLinkTarget(registry, linkId);
      if (target != null) return MetaAdapterFactory.getAbstractConcept(registry.getConceptDescriptor(target));
    }''' + s[b:]
emit(p, s)
# Generated menus can retain a compiled language identity without a desktop module.
# Keep the old getModule hook for existing generated consumers.
p = extension('com.mbeddr.mpsutil.grammarcells.runtime', 'com.mbeddr.mpsutil.grammarcells.runtime.menu', 'GrammarCellsTransformationMenuPart')
s = replace_checked(p.read_text(), 'import org.jetbrains.mps.openapi.module.SModule;',
    'import org.jetbrains.mps.openapi.module.SModule;\nimport org.jetbrains.mps.openapi.module.SModuleReference;\nimport com.mbeddr.mpsutil.grammarcells.runtime.DescriptorCache;')
s = replace_checked(s, '  public abstract SModule getModule(SRepository repository);', '''  public abstract SModule getModule(SRepository repository);

  /** Override in compiled consumers, where the language's source module is absent. */
  public SModuleReference getModuleReference(SRepository repository) {
    SModule module = getModule(repository);
    return module == null ? null : module.getModuleReference();
  }''')
s = replace_checked(s,
    'GrammarCellsUtil.getDescriptor(getModule(context.getEditorContext().getRepository()))',
    'DescriptorCache.getInstance().getDescriptorByReference(getModuleReference(context.getEditorContext().getRepository()))')
s = s.replace('import com.mbeddr.mpsutil.grammarcells.runtime.GrammarCellsUtil;\n', '')
emit(p, s)
p = extension('com.mbeddr.mpsutil.grammarcells.runtime', 'com.mbeddr.mpsutil.grammarcells.runtime', 'VisibleLanguagesCache')
s = replace_checked(p.read_text(), 'it.getSourceModule().getModuleReference()', 'it.getSourceModuleReference()')
emit(p, s)
p = extension('com.mbeddr.mpsutil.grammarcells.runtime', 'com.mbeddr.mpsutil.grammarcells.runtime', 'EditorHierachyCache')
s = replace_checked(p.read_text(), 'return getActiveEditor(concept);', 'return getActiveEditor(concept, null);')
s = replace_checked(s, '  public void invalidate() {', '''  private final Set<Class<? extends ConceptEditor>> myConditionalEditors = new java.util.HashSet<>();

  /** Register generated conditional wrappers explicitly; class names may be stripped. */
  public void registerConditionalEditor(Class<? extends ConceptEditor> editor) {
    myConditionalEditors.add(java.util.Objects.requireNonNull(editor));
    invalidate();
  }

  public void unregisterConditionalEditor(Class<? extends ConceptEditor> editor) {
    myConditionalEditors.remove(editor);
    invalidate();
  }

  public void clearConditionalEditors() {
    myConditionalEditors.clear();
    invalidate();
  }

  public void invalidate() {''')
s = replace_checked(s, '''        String name = editor.getClass().getName();
        return name.contains("_conditionalEditor_");''', '''        return myConditionalEditors.contains(editor.getClass());''')
emit(p, s)
p = extension('com.mbeddr.mpsutil.grammarcells.runtimelang', 'com.mbeddr.mpsutil.grammarcells.runtimelang.editor', 'ArbitraryTextAnnotation_EditorBuilder_a')
s = p.read_text().replace('import jetbrains.mps.lang.core.behavior.PropertyAttribute__BehaviorDescriptor;\n', '')
s = replace_checked(s, '''      Iterable<SNode> propertyAttributes = SNodeOperations.ofConcept(new IAttributeDescriptor.AllAttributes().list(myNode), CONCEPTS.PropertyAttribute$Gb);
      Iterable<SNode> currentPropertyAttributes = Sequence.fromIterable(propertyAttributes).where((it) -> Objects.equals(PropertyAttribute__BehaviorDescriptor.getProperty_id1avfQ4BBzOo.invoke(it), property));''', '''      // Use the kernel's behavior-dispatch API instead of linking language-design Java.
      Iterable<SNode> currentPropertyAttributes = jetbrains.mps.lang.smodel.generator.smodelAdapter.AttributeOperations.getPropertyAttributes(myNode, property);''')
emit(p, s)
p = extension('com.mbeddr.mpsutil.grammarcells.runtime', 'com.mbeddr.mpsutil.grammarcells.runtime', 'Parser')
s = replace_checked(p.read_text(), '      nodeToParse = findNodeForReparse(rootNode);', '''      nodeToParse = findNodeForReparse(rootNode);
      // Deleting the last annotation from a model root leaves no subtree to reparse.
      if (nodeToParse == null) return rootNode;''')
emit(p, s)
print('Generated eight MPS access overlays and eight extension replacements')
