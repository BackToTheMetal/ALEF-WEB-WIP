"""Read source manifests and prepared symlinks; write this analysis directory only.

Run with WSL Python so the existing Linux dependency symlinks resolve.
This does not prepare dependencies, compile code, or run application tests.
"""
import ast
import collections
import csv
import difflib
import hashlib
import json
import os
from pathlib import Path
import re
import subprocess

OUT = Path(__file__).resolve().parent
ALEF = OUT.parents[2]
MPS = ALEF.parent / 'mps-j2cl'
EXT = ALEF.parent / 'mps-extensions-j2cl'
VIEW = ALEF / '.work/mps-j2cl'


def constants(path):
    result = {}
    for node in ast.parse(path.read_text(encoding='utf-8-sig')).body:
        if isinstance(node, ast.Assign) and isinstance(node.targets[0], ast.Name):
            try:
                result[node.targets[0].id] = ast.literal_eval(node.value)
            except (ValueError, TypeError):
                pass
    return result


def win(path):
    value = str(path)
    return 'C:/' + value[len('/mnt/c/'):] if value.startswith('/mnt/c/') else value


def sha(path):
    return hashlib.sha256(path.read_bytes()).hexdigest()


def lines(path):
    return path.read_text(encoding='utf-8-sig').splitlines(keepends=True)


def delta(base, target):
    before, after = lines(base), lines(target)
    change = list(difflib.unified_diff(before, after, fromfile=win(base), tofile=win(target), n=3))
    return (sum(s.startswith('+') and not s.startswith('+++') for s in change),
            sum(s.startswith('-') and not s.startswith('---') for s in change), ''.join(change))


def files(root):
    return sorted(Path(folder) / name for folder, dirs, names in os.walk(root, followlinks=True)
                  for name in names)


def csv_write(name, rows):
    with (OUT / name).open('w', newline='', encoding='utf-8-sig') as stream:
        writer = csv.DictWriter(stream, fieldnames=list(rows[0]))
        writer.writeheader()
        writer.writerows(rows)


shared = constants(MPS / 'mps-overrides.bzl')
projects = constants(MPS / 'mps-browser-projects.bzl')['MPS_BROWSER_PROJECTS']
financial = constants(MPS / 'financial-sources.bzl')['FINANCIAL_SOURCES']
actions = constants(MPS / 'editor-action-sources.bzl')['EDITOR_ACTION_SOURCES']
supplemental = constants(ALEF / 'mps-sources.bzl')['MPS_SOURCES']
extensions = json.loads((EXT / 'mps-access.json').read_text())
consumer = json.loads((ALEF / 'mps-access.json').read_text())
prepared = constants(VIEW / 'mps-rules.bzl')
profiles = constants(ALEF / 'tools/adapt_mps_languages.py')['PROFILES']
errors = []
assert set(shared['MPS_OVERRIDES']) == set(shared['MPS_OVERRIDE_REASONS'])
assert set(prepared['ALEF_OVERRIDES']) == {r['original'] for r in consumer}
assert set(prepared['EXTENSION_OVERRIDES']) == {r['original'] for r in extensions}

selection = []
providers = {}
roots = {prefix: (VIEW / prefix).resolve() for prefix in ('mps', 'mps-web', 'financial-web', 'extensions-src', 'alef-src/j2cl-src')}
print('Read manifests; resolving selected providers', flush=True)
for project, info in projects.items():
    for original in info['srcs']:
        if original in prepared['ALEF_OVERRIDES']:
            path = roots['alef-src/j2cl-src'] / prepared['ALEF_OVERRIDES'][original].removeprefix('j2cl-src/')
        elif original in prepared['EXTENSION_OVERRIDES']:
            path = roots['extensions-src'] / prepared['EXTENSION_OVERRIDES'][original]
        elif original in shared['MPS_OVERRIDES']:
            path = roots['mps-web'] / shared['MPS_OVERRIDES'][original]
        else:
            path = roots['mps'] / original
        providers[original] = path
        selection.append(dict(group='browser project: ' + project, original=original, provider=win(path)))
        if not path.is_file():
            errors.append('Missing selected source: ' + win(path))
for group, values, root in [('generated language support', [s for s in financial if '/samples/' not in s], VIEW),
                            ('workbench actions', actions, VIEW), ('ALEF supplemental MPS', supplemental, ALEF)]:
    for value in values:
        prefix = next(p for p in ['financial-web/', 'mps/', '.work/mps-src/', 'j2cl-src/mps/'] if value.startswith(p))
        original = value[len(prefix):]
        path = ((roots[prefix.rstrip('/')] / original) if root == VIEW else
                roots['mps'] / original if prefix == '.work/mps-src/' else root / value)
        if original in providers and providers[original] != path:
            errors.append('Conflicting provider: ' + original)
        providers[original] = path
        selection.append(dict(group=group, original=original, provider=win(path)))
        if not path.is_file():
            errors.append('Missing selected source: ' + win(path))

extension_reasons = {
    'EditorComponent': 'Expose myEditorComponentActions for the typed grammar-cell bridge instead of reflective field access.',
    'EditorComponentActions': 'Expose the action container and myActionMap for typed grammar-cell action installation.',
    'UpdaterImpl': 'Expose myBigCellsMap for the typed editor cache bridge.',
    'EditorCell_Label': 'Expose getUpdatedText so grammar cells can reuse MPS text replacement without reflection.',
    'EditorCell_STHint': 'Expose myAnchorCell for typed side-transform hint access.',
    'CellAction_InsertIntoCollection': 'Expose myListHandler for typed grammar-cell insertion access.',
    'SubstituteMenuItemAsActionItem': 'Expose mySubstituteItem for typed access to the wrapped completion item.',
    'NodeEditorActions': 'Expose MoveDown.myPositionTracker for typed editor navigation access.',
}
alef_reasons = {
    'StyleImpl': 'Expose getParentStyle for table style inheritance without reflection.',
    'EditorCell_Basic': 'Expose setParent for table and custom cell-layout composition without reflection.',
    'EditorCell_Collection': 'Expose myCellLayout for typed table layout wrapping and unwrapping.',
    'EditorCell_Label': 'Expose getTextLine on top of the extension text-access adaptation for typed style updates.',
    'TextLine': 'Expose updateStyle; add browser text highlight ranges and selectable text colors for search and comparison presentation.',
    'CellLayout_Vertical': 'Expose isGridLayout for typed table layout inspection.',
    'EditorFontMetricsImpl': 'Measure whole text runs with fractional metrics and kerning; avoid cumulative integer character-width errors.',
    'NodeItemCellRenderer': 'Add completion row padding, smaller label fonts, and smaller regular provenance text with distinct foreground colors.',
    'NodeSubstituteChooserUi': 'Include horizontal scrollbar height when calculating completion popup dimensions so result rows remain visible.',
    'SNode': 'Add an application edit-policy hook at actual mutations; check changing property values while retaining MPS legal-change checks.',
    'SModel': 'Apply the same browser edit guard when adding or removing model roots.',
    'QuickFixRuntimeEditorWrapper': 'Recheck the current edit lease before executing a deferred automatic quick fix.',
    'EditorComponent': 'Consult the live edit lease when reporting read-only state, on top of shared browser and extension adaptations.',
    'InputMethodListenerImpl': 'Enforce read-only state for direct browser text insertion and IME composition.',
    'UpdaterImpl': 'Coalesce a post-update relayout and validation on the browser event loop, including transient results and inspector updates.',
    'UpdaterModelListener': 'Observe model writes outside commands and request an editor refresh for temporary and comparison models.',
    'TooltipTimingProperties': 'Use literal J2CL build-property lookups for tooltip timing while retaining value parsing and defaults.',
}
financial_reasons = {
    'MetaModelInfoProvider': 'Use this available class as logger owner instead of pulling in desktop DefaultModelPersistence.',
    'SModelHeader': 'Use the persisted ref attribute literal without depending on the desktop ModelPersistence implementation.',
    'ClassifierMethodsHierarchyChecker': 'Format the existing fixed diagnostics with String.format instead of MessageFormat.',
    'check_DynamicReference_NonTypesystemRule': 'Check EditableModelDescriptor instead of the desktop DefaultSModelDescriptor class.',
    'ReflectionUtil': 'Reject reflective static-field lookup explicitly because the browser has no JVM field reflection.',
    'IMainClass__BehaviorDescriptor': 'Return no generated Java main-class unit names because browser execution has no JVM generation trace lookup.',
    'RefactoringAccess': 'Use a fixed ahead-of-time registry description instead of inspecting JVM classloaders in diagnostics.',
    'Language': 'Remove unsupported generated aspect imports and dispatch branches to restrict deployment to the selected browser language profile.',
}
records = []
diffs = []


def record(layer, original, replacement, reason, base=None, expected_hash=None):
    upstream = MPS / 'mps' / original
    base = base or upstream
    if not upstream.is_file() or not replacement.is_file() or not base.is_file():
        raise RuntimeError('Missing replacement or baseline: ' + original)
    added, removed, diff = delta(base, replacement)
    ua, ur, _ = delta(upstream, replacement)
    current = providers.get(original)
    row = dict(layer=layer, original=original, replacement=win(replacement),
               base=win(base), reason=reason, selected_in_alef=current == replacement.resolve(),
               effective_provider=win(current) if current else 'not selected',
               lines_added_to_base=added, lines_removed_from_base=removed,
               lines_added_to_upstream=ua, lines_removed_from_upstream=ur,
               base_sha256=sha(base), replacement_sha256=sha(replacement), upstream_sha256=sha(upstream),
               recorded_base_hash_matches=(sha(base) == expected_hash) if expected_hash else 'not recorded')
    if expected_hash and sha(base) != expected_hash:
        errors.append('Stale recorded base hash: ' + layer + ': ' + original)
    if not added and not removed:
        errors.append('Replacement identical to base: ' + layer + ': ' + original)
    records.append(row)
    diffs.append('\n# ' + layer + ': ' + original + '\n' + diff)


for original, replacement in sorted(shared['MPS_OVERRIDES'].items()):
    record('shared MPS browser runtime', original, MPS / 'mps-web' / replacement, shared['MPS_OVERRIDE_REASONS'][original])
print('Compared shared MPS runtime replacements', flush=True)
for value in sorted(financial):
    if value.startswith('financial-web/'):
        original = value[len('financial-web/'):]
        record('shared generated language support', original, MPS / value, financial_reasons[Path(value).stem])
for row in extensions:
    record('MPS extensions access', row['original'], EXT / 'j2cl-src' / row['replacement'],
           extension_reasons[Path(row['original']).stem], MPS / row['base'], row['base_sha256'])
for row in consumer:
    record('ALEF runtime and editor', row['original'], ALEF / row['replacement'],
           alef_reasons[Path(row['original']).stem], (ALEF / row['base']).resolve(), row['base_sha256'])
for value in supplemental:
    if value.startswith('j2cl-src/mps/'):
        original = value[len('j2cl-src/mps/'):]
        reason = ('Keep only these generated language aspects: ' + ', '.join(sorted(profiles[original])) + '.'
                  if original in profiles else alef_reasons[Path(value).stem])
        record('ALEF supplemental MPS language support', original, ALEF / value, reason)
print('Compared all MPS replacement layers', flush=True)

# Every local MPS copy must be accounted for, including browser helper copies.
registered = {Path(r['replacement'].replace('C:/', '/mnt/c/')) for r in records}
browser_copies = sorted((ALEF / 'j2cl-src/mps/browser').rglob('*.java'))
unregistered = sorted(set((ALEF / 'j2cl-src/mps').rglob('*.java')) - registered - set(browser_copies))
errors += ['Unregistered ALEF MPS copy: ' + win(p) for p in unregistered]
assert {MPS / 'mps-web' / x for x in shared['MPS_OVERRIDES'].values()} == set((MPS / 'mps-web').rglob('*.java'))

browser_reasons = {
    'BrowserEditorClassLoader': 'Registers compiled editor classes, factories, rule owners, and module resource metadata.',
    'BrowserPlatform': 'Bootstraps selected MPS platform, checking, and type-system components without IntelliJ desktop startup.',
    'BrowserModelAccess': 'Owns model access and command context on the browser event loop; connects disposal and undo.',
    'BrowserEditorActionBindings': 'Generated shortcut bindings from MPS action declarations to original workbench actions.',
    'BrowserEditorShortcuts': 'Focus-aware shortcut dispatch preserving action applicability, access, read-only state, and undo policy.',
    'BrowserEditorHost': 'Installs browser services, project, repository, editor, inspectors, actions, and their lifecycle.',
    'BrowserMpsAssetData': 'Generated payloads for original MPS assets used by browser editor icons.',
    'BrowserMpsAssets': 'Installs and releases the browser MPS asset registry.',
    'BrowserResourceIcon': 'Paints registered MPS resources as browser-supported icons.',
    'BrowserSelectionFactories': 'Registers typed constructors for selections and filters without reflective construction.',
    'BrowserStyleRegistry': 'Registers compiled style attributes without JVM aspect discovery.',
    'BrowserModelReader': 'Loads XML9 model data into the browser MPS repository.',
    'FinancialClassLoader': 'Constructs a per-language ModuleClassLoader for registering compiled classes and typed factories.',
    'FinancialDemo': 'Financial calculator browser demonstration and its editor host.',
    'FinancialLanguages': 'Generated language and aspect deployment registration for the financial sample.',
    'PrimitiveModelResource': 'Embeds the base-language primitive model as a deployable browser resource.',
    'RuntimeTypesLanguage': 'Minimal runtime language supplying type-system meta-concepts.',
}
additions = []
for path in sorted((MPS / 'browser').rglob('*.java')):
    additions.append(dict(kind='shared browser helper', path=win(path),
                          scope='ALEF shared editor dependency' if '/browser/src/' in path.as_posix() else 'financial demo deployment; ALEF has separate deployment copies where listed',
                          reason=browser_reasons[path.stem], sha256=sha(path)))
for path in browser_copies:
    base = MPS / 'browser/financial' / path.name
    a, d, diff = delta(base, path)
    additions.append(dict(kind='ALEF adaptation of shared browser helper', path=win(path), scope='ALEF language deployment',
                          reason=('Unchanged copy of the shared helper.' if a == d == 0 else
                                  'Consumer version of ' + path.name + '; ' + str(a) + ' added and ' + str(d) + ' removed lines relative to shared helper.'), sha256=sha(path)))
    diffs.append('\n# ALEF browser helper\n' + diff)

stubroot = MPS / 'third_party/intellij-stubs/src'
stubpaths = sorted(stubroot.rglob('*.java'))
stubmanifest = json.loads((MPS / 'compatibility/intellij-api.json').read_text())
assert {MPS / r['source'] for r in stubmanifest['sources']} == set(stubpaths)
consumerstubs = {p.relative_to(ALEF / 'j2cl-src/intellij').as_posix(): p for p in (ALEF / 'j2cl-src/intellij').rglob('*.java')}
stubs = []
for relative in sorted({p.relative_to(stubroot).as_posix() for p in stubpaths} | set(consumerstubs)):
    base = stubroot / relative
    effective = consumerstubs.get(relative, base)
    actual = VIEW / 'third_party/intellij-stubs/src' / relative
    if not actual.is_file() or sha(actual) != sha(effective):
        errors.append('Prepared IntelliJ provider mismatch: ' + relative)
    text = effective.read_text()
    declaration = re.search(r'\b(?:class|interface|enum|@interface)\s+(\w+)', text)
    stubs.append(dict(type=relative[:-5].replace('/', '.'),
                      shared_source=win(base) if base.is_file() else '', effective_source=win(effective),
                      alef_override=relative in consumerstubs and base.is_file(),
                      alef_addition=not base.is_file(), sha256=sha(effective)))
    if relative in consumerstubs and base.is_file():
        diffs.append('\n# ALEF IntelliJ compatibility\n' + delta(base, effective)[2])
actual_stubs = [p for p in files(VIEW / 'third_party/intellij-stubs/src') if p.suffix == '.java']
assert len(actual_stubs) == len(stubs)
print('Verified effective IntelliJ providers', flush=True)

patches = []
for path in sorted((MPS / 'third_party').rglob('*.patch')):
    before = None
    for line in path.read_text().splitlines():
        if line.startswith('--- '):
            before = line[4:].split('\t')[0]
        elif line.startswith('+++ '):
            after = line[4:].split('\t')[0]
            patches.append(dict(patch=win(path), target=after[2:] if after.startswith('b/') else after,
                                change='add' if before == '/dev/null' else 'delete' if after == '/dev/null' else 'modify'))

views = json.loads((MPS / 'dependency-views.json').read_text())
adjacent = []
for view in views['views']:
    for original, replacement in view['overrides'].items():
        base = MPS / view['source'] / original
        replacement = MPS / replacement
        adjacent.append(dict(dependency=view['name'], original=original, replacement=win(replacement),
                             kind='override' if base.is_file() else 'addition', sha256=sha(replacement)))

# Complete authored/package support tree, excluding upstream, local outputs and IDE state.
support = []
excluded_dirs = {'mps', 'mps-web', 'financial-web', '.work', '.idea', '.git', 'out', '__pycache__', 'node_modules'}
for folder, dirs, names in os.walk(MPS):
    dirs[:] = sorted(d for d in dirs if d not in excluded_dirs and not d.startswith('bazel-'))
    for name in sorted(names):
        path = Path(folder) / name
        if path.is_symlink() or name.endswith('.pyc') or name == 'MODULE.bazel.lock':
            continue
        relative = path.relative_to(MPS).as_posix()
        if relative.startswith(('browser/', 'third_party/intellij-stubs/src/')):
            role = 'runtime or demo sources/assets (detailed in dedicated inventory)'
        elif relative.startswith('tests/'):
            role = 'test fixture or browser test entry'
        elif relative.startswith('tools/'):
            role = 'inventory, adaptation, generation, build, or validation tooling'
        elif relative.startswith('third_party/'):
            role = 'external compatibility source, patch, build metadata, or provenance'
        elif relative.startswith(('docs/', 'compatibility/')) or name.endswith('.md'):
            role = 'documentation or compatibility manifest'
        else:
            role = 'root build, selection, or project metadata'
        support.append(dict(path=win(path), role=role, sha256=sha(path)))

windows_git = Path('/mnt/c/Users/niekh/.cache/codex-runtimes/codex-primary-runtime/dependencies/native/git/cmd/git.exe')
git = ([str(windows_git), '--no-optional-locks', '-c', 'safe.directory=' + win(MPS / 'mps'), '-C', win(MPS / 'mps')]
       if windows_git.is_file() else
       ['git', '--no-optional-locks', '-c', 'safe.directory=' + str(MPS / 'mps'), '-C', str(MPS / 'mps')])
head = subprocess.check_output(git + ['rev-parse', 'HEAD'], text=True).strip()
status = subprocess.check_output(git + ['status', '--porcelain', '--untracked-files=no'], text=True).splitlines()
composition = json.loads((MPS / 'third_party/j2cl/composition.json').read_text())
stats = dict(snapshot_date='2026-10-01', mps_commit=head,
             shared_browser_projects=len(projects), shared_browser_sources=sum(len(v['srcs']) for v in projects.values()),
             shared_financial_sources=len(financial), alef_shared_language_sources=sum('/samples/' not in p for p in financial),
             original_workbench_action_sources=len(actions), alef_supplemental_mps_sources=len(supplemental),
             selected_source_records=len(selection), selected_unique_mps_paths=len(providers),
             replacement_records=len(records), unique_modified_mps_paths=len({r['original'] for r in records}),
             replacement_records_by_layer=dict(collections.Counter(r['layer'] for r in records)),
             effective_replacements_by_layer=dict(collections.Counter(r['layer'] for r in records if r['selected_in_alef'])),
             selected_unmodified_mps_paths=len(set(providers) - {r['original'] for r in records}),
             shared_browser_java_helpers=sum(r['kind'] == 'shared browser helper' for r in additions),
             shared_intellij_java_sources=len(stubpaths), effective_intellij_java_sources=len(stubs),
             alef_intellij_overrides=sum(r['alef_override'] for r in stubs), alef_intellij_additions=sum(r['alef_addition'] for r in stubs),
             jre_composition_sources=composition['source_count'], patch_target_records=len(patches),
             adjacent_dependency_records=len(adjacent), support_files=len(support),
             upstream_tracked_changes=status, errors=errors)
data = dict(stats=stats, replacements=records, browser_additions=additions, intellij_sources=stubs,
            selected_sources=selection, patch_targets=patches, adjacent_dependency_overlays=adjacent,
            support_files=support, jre_composition=composition,
            projects={k: dict(selected=len(v['srcs']), available=v['upstream_java_sources'], mode=v['selection'], reason=v['selection_reason']) for k,v in projects.items()})
(OUT / 'inventory.json').write_text(json.dumps(data, indent=2) + '\n')
for name, rows in [('mps-replacements.csv', records), ('browser-additions.csv', additions), ('intellij-sources.csv', stubs),
                   ('selected-mps-sources.csv', selection), ('patch-targets.csv', patches),
                   ('adjacent-dependency-overlays.csv', adjacent), ('support-files.csv', support)]:
    csv_write(name, rows)
(OUT / 'replacement-differences.diff').write_text(''.join(diffs))
print(json.dumps(stats, indent=2))
