#!/usr/bin/env python3
"""Create reviewed, sparse browser adaptations from pinned generated Java."""
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]

def adapt(path, replacements):
    source = (ROOT / 'mps' / path).read_text(encoding='utf-8')
    for old, new in replacements:
        if source.count(old) != 1:
            raise ValueError(f'Expected exactly one adaptation anchor in {path}: {old}')
        source = source.replace(old, new)
    target = ROOT / 'financial-web' / path
    target.parent.mkdir(parents=True, exist_ok=True)
    target.write_text(source, encoding='utf-8', newline='\n')

adapt('languages/util/execution/util/source_gen/jetbrains/mps/execution/util/behavior/IMainClass__BehaviorDescriptor.java', [
    ('import jetbrains.mps.textgen.trace.TraceInfo;\n', ''),
    ('return Sequence.fromIterable(((Iterable<String>) new TraceInfo().getUnitNames(__thisNode__))).first();',
     '// This in-memory editor has no generated executable units or generation trace.\n    return null;'),
])

adapt('languages/baseLanguage/baseLanguage/source_gen/jetbrains/mps/baseLanguage/typesystem/check_DynamicReference_NonTypesystemRule.java', [
    ('import jetbrains.mps.smodel.DefaultSModelDescriptor;', 'import jetbrains.mps.smodel.EditableModelDescriptor;'),
    ('ourModel instanceof DefaultSModelDescriptor', 'ourModel instanceof EditableModelDescriptor'),
])

adapt('languages/baseLanguage/baseLanguage.util/source_gen/jetbrains/mps/baseLanguage/util/ReflectionUtil.java', [
    ('import java.lang.reflect.Field;\n', ''),
    ('''    Class aClass = findClass(module, classNode);
    Field field;
    try {
      field = aClass.getField(constantName);
      return field.get(null);
    } catch (NoSuchFieldException e) {
      throw new RuntimeException(e);
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }''', '''    throw new UnsupportedOperationException("JVM stub constant reflection is unavailable in the browser: " + constantName);'''),
])

adapt('languages/baseLanguage/baseLanguage/source_gen/jetbrains/mps/baseLanguage/typesystem/ClassifierMethodsHierarchyChecker.java', [
    ('import java.text.MessageFormat;\n', ''),
    ("The anonymous class must implement abstract method ''{0}'' in ''{1}''", "The anonymous class must implement abstract method '%s' in '%s'"),
    ("The classifier ''{0}'' must be declared abstract or implement abstract method ''{1}'' in ''{2}''", "The classifier '%s' must be declared abstract or implement abstract method '%s' in '%s'"),
    ('MessageFormat.format(msg, abstractMethod', 'String.format(msg, abstractMethod'),
    ('MessageFormat.format(msg, SPropertyOperations', 'String.format(msg, SPropertyOperations'),
])

adapt('workbench/mps-refactoring/j.m.refactoring.runtime/source_gen/jetbrains/mps/refactoring/runtime/access/RefactoringAccess.java', [
    ('r.getOverridenRefactoringClass().getClassLoader().toString()', '"ahead-of-time browser class registry"'),
    ('refClass.getClassLoader().toString()', '"ahead-of-time browser class registry"'),
])

adapt('core/persistence/source/jetbrains/mps/smodel/SModelHeader.java', [
    ('import jetbrains.mps.smodel.persistence.def.ModelPersistence;\n', ''),
    ('!ModelPersistence.REF.equals(key)', '!"ref".equals(key)'),
])
adapt('core/persistence/source/jetbrains/mps/persistence/MetaModelInfoProvider.java', [
    ('Logger.getLogger(DefaultModelPersistence.class)', 'Logger.getLogger(MetaModelInfoProvider.class)'),
])
