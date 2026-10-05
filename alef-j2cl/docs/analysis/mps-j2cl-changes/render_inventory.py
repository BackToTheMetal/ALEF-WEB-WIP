"""Render the saved inventory as readable Markdown without rereading dependencies."""
from pathlib import Path
import collections
import json

ROOT = Path(__file__).resolve().parent
data = json.loads((ROOT / 'inventory.json').read_text())
stats = data['stats']


def link(path, label=None):
    return '[' + (label or Path(path).name) + '](<' + path + '>)'


def safe(value):
    return str(value).replace('|', '\\|').replace('\n', ' ')


def table(headers, rows):
    return '\n'.join(['| ' + ' | '.join(headers) + ' |', '| ' + ' | '.join('---' for _ in headers) + ' |'] +
                     ['| ' + ' | '.join(safe(v) for v in row) + ' |' for row in rows]) + '\n'


def save(name, blocks):
    (ROOT / name).write_text('\n\n'.join(blocks) + '\n', encoding='utf-8')


blocks = ['# Complete MPS replacement list',
          'Snapshot: 1 October 2026. Each row is one replacement at one layer. The full original path disambiguates classes such as Language.java. “Selected” means ALEF selects that exact file; lower layers remain part of the derivation even when a later copy takes precedence. Added/removed line counts are relative to the immediate base, not a measure of semantic complexity.',
          f"There are {stats['replacement_records']} layer records covering {stats['unique_modified_mps_paths']} distinct original MPS files. Exact incremental differences are in {link('C:/Codex/alef-j2cl/docs/analysis/mps-j2cl-changes/replacement-differences.diff')}; hashes and complete provider paths are in {link('C:/Codex/alef-j2cl/docs/analysis/mps-j2cl-changes/mps-replacements.csv')}."]
for layer, count in stats['replacement_records_by_layer'].items():
    blocks += ['## ' + layer, f'{count} replacement files.']
    rows = []
    for item in data['replacements']:
        if item['layer'] != layer:
            continue
        rows.append([link(item['replacement'], item['original']), item['reason'],
                     'Yes' if item['selected_in_alef'] else 'Later layer',
                     '+' + str(item['lines_added_to_base']) + ' / -' + str(item['lines_removed_from_base'])])
    blocks.append(table(['Original MPS path and replacement link', 'Change and purpose', 'Selected', 'Lines'], rows))
save('replacement-list.md', blocks)

blocks = ['# Complete browser and compatibility additions',
          'These files provide the browser host or external platform APIs. They are separate from replacements of JetBrains MPS source. Scope labels distinguish shared editor support from the financial demonstration and ALEF-specific deployment copies.',
          '## Browser support classes',
          table(['File', 'Scope', 'Purpose'], [[link(r['path'], r['path'].split('/mps-j2cl/')[-1] if '/mps-j2cl/' in r['path'] else r['path'].split('/alef-j2cl/')[-1]), r['scope'], r['reason']] for r in data['browser_additions']]),
          '## IntelliJ compatibility classes',
          f"The shared dependency contains {stats['shared_intellij_java_sources']} Java source files implementing the selected IntelliJ-facing API surface. ALEF overrides {stats['alef_intellij_overrides']} and adds {stats['alef_intellij_additions']}, giving {stats['effective_intellij_java_sources']} selected files. These are authored compatibility implementations, interfaces, enums and annotations; the directory name intellij-stubs does not imply that every implementation is empty. It is not the complete IntelliJ platform.",
          'Shared implementations cover application/project services, disposal, synchronous message bus, action data, command/undo history, clipboard, scheduling, popups, fonts and text utilities. The source list below is exhaustive; the source links show each class and its implemented members. Historical test claims in the dependency manifest were not rerun for this inventory.']
groups = collections.defaultdict(list)
for r in data['intellij_sources']:
    group = '.'.join(r['type'].split('.')[:3])
    groups[group].append(r)
for group, items in sorted(groups.items()):
    blocks += ['### ' + group,
               table(['Type and effective source', 'Provider'], [[link(r['effective_source'], r['type']), 'ALEF addition' if r['alef_addition'] else 'ALEF override' if r['alef_override'] else 'Shared compatibility'] for r in items])]
save('additions-list.md', blocks)

blocks = ['# Build and adjacent dependency inventory',
          'This appendix accounts for supporting work outside MPS Java replacements: source selection, J2CL compiler and JRE patches, AWT/Swing views, tests, generators, and manifests. These counts must not be added to the MPS replacement total.',
          '## Shared browser source projects',
          table(['MPS project', 'Selected Java files', 'Selection mode', 'Reason'], [[k, v['selected'], v['mode'], v['reason']] for k,v in sorted(data['projects'].items())]),
          '## Patch targets',
          'Every target file in the shared dependency’s patch files is listed. A target may appear in multiple ordered patches; these are patch records, not distinct Java classes. The composed JRE draws from sibling IO, NIO, concurrency and networking ports; its full source ownership is preserved in inventory.json.',
          table(['Patch', 'Target', 'Operation'], [[link(r['patch'], r['patch'].split('/mps-j2cl/')[-1]), r['target'], r['change']] for r in data['patch_targets']]),
          '## Adjacent WebAWT and WebSwing changes',
          'These are the exact dependency-view mappings owned by mps-j2cl. An addition means the original path is absent in the current sibling source tree; an override means it exists. They are not MPS replacements. ALEF also has its own AWT/Swing integration work outside the scope of this MPS dependency inventory.',
          table(['Dependency', 'Original path', 'Mapped file', 'Kind'], [[r['dependency'], r['original'], link(r['replacement']), r['kind']] for r in data['adjacent_dependency_overlays']]),
          '## Current tracked changes inside the upstream checkout',
          'The pinned MPS commit is ' + stats['mps_commit'] + '. Git reports the following tracked local changes. This list is separate from the external replacement mechanism. The shared source audit currently fails while parsing local module metadata; it reports unresolved IDEAPlugin dependencies intellij-stubs, webawt and webswing.',
          table(['Git status', 'Path'], [[r[:2], link('C:/Codex/mps-j2cl/mps/' + r[3:], r[3:])] for r in stats['upstream_tracked_changes']]),
          '## Complete shared package support file list',
          'Includes browser assets, compatibility sources, patch sources, tooling, tests, documentation, manifests and root build files. Excludes the upstream tree, replacements already listed separately, local output directories, prepared dependency views, IDE state, Python caches, node_modules, Bazel output symlinks and MODULE.bazel.lock. Presence here describes the current package, not an assertion about when or by whom it was added.',
          table(['File', 'Role'], [[link(r['path'], r['path'].split('/mps-j2cl/')[-1]), r['role']] for r in data['support_files']])]
save('build-and-support-list.md', blocks)
print('Rendered replacement-list.md, additions-list.md and build-and-support-list.md')

base = 'C:/Codex/alef-j2cl/docs/analysis/mps-j2cl-changes/'
counts = stats['replacement_records_by_layer']
blocks = ['# MPS J2CL replacements and additions',
          'This report inventories the current mps-j2cl browser dependency and the additional MPS adaptations that ALEF places over it. It covers source replacement, new browser/platform support, source selection, and build patches. It does not inventory the separate Java backend or changes to ALEF-generated business-language code. Snapshot: 1 October 2026.',
          f"The shared dependency replaces **{counts['shared MPS browser runtime']} MPS runtime files** and **{counts['shared generated language support']} generated-language/persistence support files**. Including MPS extensions and ALEF’s own MPS adaptations, the current deployment has **{stats['unique_modified_mps_paths']} distinct modified MPS source paths**, represented by {stats['replacement_records']} layered replacement records. Most selected MPS Java files are used directly.",
          '## Complete lists',
          '\n'.join('- ' + link(base + name, label) for name, label in [
              ('replacement-list.md', 'Every MPS replacement and its purpose'),
              ('additions-list.md', 'Every browser helper and IntelliJ compatibility class'),
              ('build-and-support-list.md', 'Every patch target, AWT/Swing view mapping, support file, and local upstream metadata change'),
              ('mps-replacements.csv', 'Filterable replacement inventory with hashes, bases, selected providers, and line counts'),
              ('selected-mps-sources.csv', 'Every selected MPS source and its effective provider'),
              ('replacement-differences.diff', 'Exact incremental differences for MPS replacements, ALEF helper copies, and IntelliJ overrides'),
              ('inventory.json', 'Combined machine-readable inventory')]),
          '## How the replacement layers work',
          'ALEF’s MODULE.bazel selects .work/mps-j2cl. tools/prepare.py composes that view from the MPS extensions view, which in turn points to the shared mps-j2cl package. For the 36 browser projects, the source rule picks exactly one file for each original MPS path, in this priority order: ALEF override, extension override, shared mps-web override, original MPS. The financial language-support list and ALEF’s supplemental MPS list are separate source selections.',
          '```mermaid\nflowchart TD\n  O["Pinned MPS sources"] --> S["Shared browser runtime: 100 replacements"]\n  S --> E["MPS extensions: 8 access adaptations"]\n  E --> A["ALEF: 16 runtime/editor adaptations"]\n  O --> F["Shared language support: 10 replacements"]\n  O --> L["ALEF supplemental languages: 12 replacements"]\n  A --> B["ALEF browser compilation"]\n  F --> B\n  L --> B\n  C["Browser helpers and IntelliJ compatibility"] --> B\n  J["J2CL JRE/compiler patches and AWT/Swing ports"] --> B\n```',
          table(['Replacement layer', 'Files at this layer', 'Exact files selected by ALEF'], [[k, v, stats['effective_replacements_by_layer'].get(k, 0)] for k,v in counts.items()]),
          'EditorComponent appears in three layers; EditorCell_Label and UpdaterImpl appear in two. Earlier layers contribute to the later copies and therefore must not be counted as separate original MPS classes. The inventory identifies the selected provider for every row.',
          '## What had to change in MPS',
          table(['Area', 'Replacement mechanism and purpose', 'Representative files'], [
              ['Reflection and class loading', 'Register already compiled classes, constructor factories, query callbacks, rule owners, selection aliases, resources and style attributes. The selected J2CL runtime has no JVM bytecode loader or general runtime Java reflection.', 'ModuleClassLoader, MPSModuleClassLoader, LanguageRegistry, ExtensionRegistry, ModuleRuntime, QueryMethods, BaseQuickFixProvider, SelectionInfoImpl, AWTKeymapHandler'],
              ['Execution and lifecycle', 'Use cancellable browser scheduling and explicit disposal. Preserve model-access and command ordering on one event loop. Release listeners, typechecking sessions and weak registries when hosts close.', 'Highlighter, HighlighterUpdateSession, IntentionsSupport, ThreadUtils, DefaultModelAccess, ProjectModelAccess2, TypecheckingFacade and controllers, DataTransferManager'],
              ['Editor rendering and input', 'Route real MPS cells, gutter, completion input, keyboard events and IME through browser-supported Swing/AWT and canvas. Bind selected original MPS actions to shortcuts.', 'EditorComponent, LeftEditorHighlighter, NodeSubstitutePatternEditor, PlatformEditorEmulation, EditorComponentKeyboardHandler'],
              ['IDE services and undo', 'Inject platform/project services, retain real in-memory repositories and MPS undo items, use repository-scoped node handles, and replace a blocking paste-import dialog with a host policy.', 'MPSProject, MPSCoreComponents, WorkbenchUndoHandler, UndoActionsCollector, OnReloadingUndoCleaner, MPSNodeVirtualFile, NodeVirtualFileSystem, AddRequiredImportsDialog'],
              ['Resources and persistence boundaries', 'Register model/icon bytes and resource origins explicitly. Use lexical paths/URLs and portable XML. Reject unsupported JAR content access, ZIP extraction/creation, JDK discovery and desktop file loading.', 'BaseIconManager, IconLoadingUtil, IconResource, MPSIcons, ParseFacility, FileUtil, JarFileData, ZipFileContainer, SDKDiscovery, UnzipUtil, JDOMUtil'],
              ['JRE and compiler compatibility', 'Replace unavailable formatting, resource bundles, scanners, regex forms, collection APIs, JVM timestamp/thread inspection and weak-reference mechanisms while retaining the intended selected behavior.', 'NameUtil, PatternUtil, ModuleReference, MacroHelper, PathSpec, SModelName, WeakSet, ArrayWrapper, StringUtil, StringsIO, ClassLoaderManager'],
              ['Generated language deployment', 'Restrict generated Language descriptors to selected aspects; retain portable persistence and checking interfaces; explicitly reject JVM static-field reflection and omit unavailable generation-trace lookup.', 'Three shared Language.java profiles, eleven ALEF Language.java profiles, SModelHeader, MetaModelInfoProvider, ReflectionUtil, IMainClass__BehaviorDescriptor'],
              ['Extension access', 'Expose existing members/classes across eight files so grammar-cell helpers can use typed Java calls instead of reflective access.', 'EditorComponentActions, UpdaterImpl, EditorCell_Label, EditorCell_STHint, CellAction_InsertIntoCollection, SubstituteMenuItemAsActionItem, NodeEditorActions, EditorComponent'],
              ['ALEF editor behavior', 'Expose table layout/style access, measure complete text runs, style completion, add text highlights, enforce editing policy at mutation/IME/quick-fix boundaries, and refresh temporary models after writes.', 'StyleImpl, EditorCell_Basic, EditorCell_Collection, TextLine, CellLayout_Vertical, EditorFontMetricsImpl, SNode, SModel, InputMethodListenerImpl, UpdaterModelListener'],
          ]),
          'The file-by-file reasons for the shared 100 replacements come from MPS_OVERRIDE_REASONS. Extension and ALEF reasons were checked against their generators, current copies and incremental differences. “Necessary” here means used to make the current selected deployment work; it does not prove that each individual source override is the only possible design.',
          '## What was added',
          f"The shared dependency supplies **{stats['shared_browser_java_helpers']} browser Java helpers**: eleven for the reusable editor/platform integration and six for the financial sample deployment. ALEF supplies three deployment copies of those sample helpers. It also supplies **{stats['shared_intellij_java_sources']} compatibility Java files** in third_party/intellij-stubs/src; ALEF replaces four of these and adds BrowserActionPopupStyle, resulting in **{stats['effective_intellij_java_sources']} selected compatibility files**.",
          'The four ALEF IntelliJ overrides are CommandProcessor (transaction begin/finish hooks), UndoManager (undo-transparent quick fixes and guarded undo/redo transactions), AbstractPopup (resize the actual popup when its content changes), and JBPopupFactory (action popup styling and measured labels). BrowserActionPopupStyle provides the shared styling for those action popups.',
          table(['Added integration', 'Purpose'], [
              ['BrowserPlatform and BrowserModelAccess', 'Create the selected MPS platform and supply model/command access on the browser event loop.'],
              ['BrowserEditorHost', 'Install application/project services, repository, editors, inspectors, actions and cleanup.'],
              ['BrowserEditorClassLoader, BrowserSelectionFactories and BrowserStyleRegistry', 'Supply explicit class/factory/style registrations that replace desktop discovery and reflection.'],
              ['BrowserEditorActionBindings and BrowserEditorShortcuts', 'Connect keyboard input to original MPS workbench action implementations, including their access and undo policies.'],
              ['BrowserMpsAssets, BrowserMpsAssetData and BrowserResourceIcon', 'Make original MPS image assets available through a browser registry.'],
              ['Financial deployment helpers', 'Demonstrate generated language registration, XML9 model loading and embedded model resources; ALEF has its own deployment copies.'],
              ['IntelliJ compatibility surface', 'Supply selected services, disposal, message bus, action data, clipboard, scheduling, commands, undo, popups and utility contracts without IntelliJ binaries.'],
              ['J2CL compiler and JRE changes', 'Compose IO/NIO/concurrency/networking providers; support Class relations, reflective array allocation, registered ClassLoader resources/factories and missing JRE APIs.'],
              ['Root build and audit infrastructure', 'Select sources, register overrides, prepare dependency views, generate language/assets/shortcuts, check ownership and run JVM/browser contracts.'],
          ]),
          'The Class type-support patch changes seven J2CL runtime/compiler files. It supports isInstance, isAssignableFrom, cast, asSubclass and reflective Array.newInstance through retained type relations. It does not provide arbitrary JVM reflection. The composed JRE manifest contains ' + str(stats['jre_composition_sources']) + ' source providers; the complete patch target list is in the build appendix. Shared root-owned AWT/Swing view mappings are also listed there. ALEF-specific Java time and other sibling-port work are outside this MPS dependency count.',
          '## Original code that is selected without replacement',
          table(['Source group', 'Selected Java source files'], [
              ['Shared browser profile across 36 projects', stats['shared_browser_sources']],
              ['Shared generated language support selected by ALEF', stats['alef_shared_language_sources']],
              ['Original workbench action implementations', stats['original_workbench_action_sources']],
              ['ALEF supplemental MPS support', stats['alef_supplemental_mps_sources']],
              ['Distinct selected original MPS paths across these groups', stats['selected_unique_mps_paths']],
              ['Paths used directly without a replacement', stats['selected_unmodified_mps_paths']],
          ]),
          'The 23 workbench action files are upstream MPS implementations added to the compilation selection; they were not rewritten. Similarly, selecting generated structure, behavior, constraints, editor, intentions or type-system files is not an addition to MPS source. The financial deployment manifest includes sample code that ALEF explicitly filters out. These are compile-input counts, not counts of classes retained after Closure optimization.',
          '## Which changes are inherent and which are choices',
          'Replacing JVM bytecode loading, arbitrary reflection, native threads, desktop process/window integration and host filesystem discovery is inherent to this browser architecture. Typed registries, cooperative tasks and explicit resource providers are the chosen implementations of those boundaries.',
          'Formatting, parsing, library-method and access-visibility adaptations are compatibility choices. Some could move into a more complete JRE or upstream API over time; their presence does not establish that MPS must always be patched this way. The public-member changes for grammar cells/tables replace reflection but still couple consumers to MPS internals.',
          'Completion styling, search/comparison highlights, application edit leases, transaction hooks and transient-model refreshes support ALEF behavior. They are not generic requirements for transpiling MPS. Generated aspect removal is a deliberate feature profile: code excluded from that profile is not claimed to work.',
          'Explicitly unavailable paths remain unavailable: desktop plugin/service discovery, runtime JVM class definition, general reflective field/method discovery, arbitrary archive extraction, JVM instrumentation, blocking desktop dialogs, native process/window control and parallel execution through the compatibility executor. Some documentation HTML is rendered as normalized text. Custom two-stroke action shortcuts still require host dispatch.',
          '## Verification and items needing attention',
          'The inventory resolves the existing WSL dependency view, reads current manifests, compares replacement files with their immediate bases and original MPS files, checks stored base hashes, and verifies the effective IntelliJ compatibility providers. The CSV/JSON outputs record the current hashes. No application code was changed, no dependency preparation was run, and no browser/JVM build or behavioral suite was rerun.',
          f"The upstream MPS Java baseline is commit {stats['mps_commit']}. Git currently reports **{len(stats['upstream_tracked_changes'])} tracked local changes** in that checkout; they are listed in the build appendix. The source-audit command failed with `IDEAPlugin: unresolved module dependencies: ['intellij-stubs', 'webawt', 'webswing']`. The inspected .iml differences replace original module dependencies with local compatibility modules. Therefore the checkout cannot currently be described as wholly pristine, and the shared audit cannot be reported as passing.",
          ('Additional inventory findings:\n\n' + '\n'.join('- ' + e for e in stats['errors'])) if stats['errors'] else 'The independent inventory checks reported no missing or unregistered replacement files, recorded base-hash mismatches, conflicting source providers, or prepared IntelliJ-provider mismatches.',
          'The local module metadata should be reconciled separately with the intended upstream/source-audit boundary. This analysis leaves it untouched. Source mapping verification does not replace a successful compile or browser behavior test.',
          '## Sources and regeneration',
          '\n'.join('- ' + link(path) for path in [
              'C:/Codex/alef-j2cl/MODULE.bazel', 'C:/Codex/alef-j2cl/tools/prepare.py',
              'C:/Codex/mps-j2cl/mps-rules.bzl', 'C:/Codex/mps-j2cl/mps-overrides.bzl',
              'C:/Codex/mps-j2cl/mps-browser-projects.bzl', 'C:/Codex/mps-j2cl/financial-sources.bzl',
              'C:/Codex/mps-j2cl/editor-action-sources.bzl', 'C:/Codex/mps-extensions-j2cl/mps-access.json',
              'C:/Codex/alef-j2cl/mps-access.json', 'C:/Codex/alef-j2cl/mps-sources.bzl',
              'C:/Codex/mps-j2cl/compatibility/intellij-api.json', 'C:/Codex/mps-j2cl/dependency-views.json']),
          'The analysis scripts write only this documentation directory. Regenerate from Windows PowerShell with:',
          '```powershell\nwsl -d Ubuntu -- python3 /mnt/c/Codex/alef-j2cl/docs/analysis/mps-j2cl-changes/collect_inventory.py\nwsl -d Ubuntu -- python3 /mnt/c/Codex/alef-j2cl/docs/analysis/mps-j2cl-changes/render_inventory.py\n```']
save('README.md', blocks)
print('Rendered README.md')
