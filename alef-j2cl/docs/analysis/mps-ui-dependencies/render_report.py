"""Produce the editable diagrams, class tables and source-evidence report."""
import json
from pathlib import Path

HERE = Path(__file__).resolve().parent
D = json.loads((HERE / 'inventory.json').read_text(encoding='utf-8'))


def totals(prefix, scope):
    return sum(c['counts'][scope] > 0 for c in D['classes'] if c['name'].startswith(prefix + '.'))


def package_totals(prefix, scope):
    return sum(p['counts'][scope] > 0 for p in D['packages'] if p['name'].startswith(prefix))


def graph(prefix):
    packages = [p for p in D['packages'] if p['name'] == prefix or p['name'].startswith(prefix + '.')]
    ids = {p['name']: 'p' + str(i) for i, p in enumerate(packages)}
    lines = ['flowchart LR',
             '  classDef browser fill:#d9eee3,stroke:#548768,color:#142e20;',
             '  classDef desktop fill:#fff0cd,stroke:#aa8735,color:#352909;',
             '  classDef auxiliary fill:#e8e1f4,stroke:#8c78ae,color:#30223f;',
             '  classDef absent fill:#f1f3f5,stroke:#a8adb5,color:#434851;']
    for p in packages:
        u, b, a = (p['counts'][x] for x in ('upstream', 'browser', 'auxiliary'))
        state = 'browser' if b else 'desktop' if u else 'auxiliary' if a else 'absent'
        count = f'{u} MPS types · {b} browser' if u or b else f'{a} auxiliary type · 0 MPS' if a else 'No direct reference found'
        lines.append(f'  {ids[p["name"]]}["{p["name"]}<br/>{count}"]:::{state}')
    for p in packages[1:]:
        parent = p['name'].rsplit('.', 1)[0]
        lines.append(f'  {ids[parent]} --- {ids[p["name"]]}')
    return '\n'.join(lines)


def class_table(prefix):
    lines = ['| Package | Classes / interfaces referenced by MPS |', '|---|---|']
    for p in D['packages']:
        if not p['name'].startswith(prefix):
            continue
        members = [c for c in D['classes'] if c['package'] == p['name'] and c['counts']['upstream']]
        if not members:
            continue
        labels = []
        for c in members:
            short = c['name'][len(p['name']) + 1:]
            label = f'`{short}`'
            if c['counts']['browser']:
                label = '**' + label + '**'
            labels.append(label)
        lines.append(f'| `{p["name"]}` ({len(members)}) | ' + ', '.join(labels) + ' |')
    return '\n'.join(lines)


def source_link(ref):
    path = ref['path']
    return f'[{Path(path).name}:{ref["line"]}](<{path}:{ref["line"]}>)'


intro = f'''# MPS requirements for Swing and AWT

Analysis date: 2026-09-29. Upstream MPS commit: `{D['commit']}`.

This is a direct source-reference inventory against the Java 21 public/protected type catalog. It answers which Swing and AWT types appear in MPS source code, and which remain in the selected MPS sources used by this ALEF browser workspace. It is not a runtime trace or a complete transitive dependency closure.

| Scope | Swing types | Swing packages | AWT types | AWT packages |
|---|---:|---:|---:|---:|
| Main upstream MPS sources | {totals('javax.swing', 'upstream')} | {package_totals('javax.swing', 'upstream')} / 18 | {totals('java.awt', 'upstream')} | {package_totals('java.awt', 'upstream')} / 13 |
| Selected MPS sources in ALEF browser profile | {totals('javax.swing', 'browser')} | {package_totals('javax.swing', 'browser')} / 18 | {totals('java.awt', 'browser')} | {package_totals('java.awt', 'browser')} / 13 |

Counts include interfaces and explicitly named nested types. Unused imports are excluded from these counts and listed separately below. The browser row is a subset of the upstream row at the type level.

**Diagram colors:** green = directly referenced in the selected browser sources; amber = directly referenced in other main MPS sources; purple = only found in auxiliary sources; gray = no direct reference found. Green packages can also contain types used only outside the browser profile. Lines show package-name hierarchy, not calls or inheritance. Java subpackages are separate packages.

**Class tables:** bold type names also occur in the selected browser sources. Plain names occur in the main upstream scan but have no code reference in that selected browser source set.
'''

sections = []
for prefix, title in [('javax.swing', 'Swing'), ('java.awt', 'AWT')]:
    diagram = graph(prefix)
    (HERE / f'{title.lower()}-packages.mmd').write_text(diagram + '\n', encoding='utf-8')
    table = class_table(prefix)
    sections.append(f'## {title}: all public packages\n\n```mermaid\n{diagram}\n```\n\n{table}\n')

analysis = '''## What this means for the port

MPS uses Swing as a host for the projectional editor and its surrounding controls: components, panels, scroll panes, labels/icons, lists and popups, action/input handling, borders, and selected UI delegates. The direct browser footprint still contains text/HTML support and `TreeNode`; it is broader than a single custom-painted component.

AWT is a substantial part of the editor contract: component geometry and layouts, `Graphics`/`Graphics2D`, color, font metrics, image rendering, keyboard/mouse/focus events, clipboard transfer types, and input-method support. Replacing Swing alone would not replace these editor dependencies.

`javax.swing.table` and `java.awt.dnd` have direct references in main MPS sources but none in the selected MPS browser source set. `javax.swing.undo` has no direct reference in the scanned sources; that does not mean MPS lacks undo. Its editor exposes its own command/undo integration.

Packages such as `javax.swing.plaf.metal`, `.nimbus`, `.synth`, `java.awt.print`, and `java.awt.color` cannot be declared removable from a complete distribution using this analysis. An IntelliJ class, Swing implementation, superclass, reflection, service provider, or selected look and feel may require them indirectly. In particular, gray packages mean **no direct source reference found**, not **never used**.

The selected browser source list includes classes that the optimizer may discard and methods whose providers have browser adaptations. A reference to `Desktop`, `Dialog`, `Window`, or `Toolkit` therefore does not prove that the browser implements the complete corresponding desktop API.

## Source selection and method

- The upstream scan inventories all 29,684 Git-tracked `.java`, `.kt`, and `.kts` paths. A Git content prefilter finds 1,432 candidates containing `javax.swing` or `java.awt`.
- The main scope contains 22,143 paths. Another 7,541 paths are separated as auxiliary by the reproducible path rules in `analyze.py`: top-level tests/testbench/samples, test/testdata/sandbox path components, and components ending in `.tests` or `-tests`. This is a source-tree classification, not a release packaging assertion. Generator output outside those paths is included.
- The browser scope contains 4,646 selected MPS files: 2,052 from the 36-project browser manifest, 2,287 additional non-sample financial/language-support files, and 307 supplemental ALEF MPS files. Duplicated logical paths are counted once.
- Browser providers follow the current prepared rule precedence: ALEF replacement, then MPS-extensions replacement, then MPS browser replacement, then original upstream. The complete resolved file list and SHA-256 hashes are in `inventory.json`.
- The browser comparison intentionally measures selected MPS sources. It does not include ALEF language code, MPS-extensions language code, browser host helpers, IntelliJ adapters, or the Swing/AWT library implementations themselves. Their additional and transitive requirements need a separate closure analysis before reducing providers.
- Comments and string/character literals are removed while preserving line numbers. Explicit imports are matched against code identifiers; fully qualified references, package wildcards, static imports and explicitly qualified nested types are handled. Wildcard matches are checked against the Java API catalog and explicit competing imports. All 57 wildcard evidence rows come from six source files and were inspected for the relevant type references.
- This is lexical analysis, not a complete compiler symbol-resolution pass. It can miss implicit inherited nested types, reflective names and generated code that is absent from the checkout. Symbol shadowing and unusual syntax are remaining limitations. Source evidence is retained so each result can be reviewed.
- A fresh API catalog was generated using the local JDK 25 compiler with `--release 21`; it reports all 31 public AWT/Swing packages. The source catalog includes public/protected nested types, and each such referenced type counts separately. JDK-internal packages such as `java.awt.peer` and `sun.awt` are outside the public-package diagram.
- No MPS source, model, build configuration or library provider was changed. No application build or browser capability test was needed or claimed for this inventory.

Package boundaries were checked against the official [Java 21 java.desktop API](https://docs.oracle.com/en/java/javase/21/docs/api/java.desktop/module-summary.html) and [java.datatransfer API](https://docs.oracle.com/en/java/javase/21/docs/api/java.datatransfer/module-summary.html). `java.awt.datatransfer` belongs to `java.datatransfer`; it is included here because its package is in the AWT namespace.

## Auxiliary-only types

These additional direct references occur only in paths classified as tests, testbench, samples or sandboxes. They are not in the main totals.

| Type | Example evidence |
|---|---|
'''
for c in D['classes']:
    if c['counts']['auxiliary'] and not c['counts']['upstream'] and not c['counts']['browser']:
        ref = next(r for r in c['evidence'] if r['scope'] == 'auxiliary' and r['method'] != 'import-only')
        analysis += f'| `{c["name"]}` | {source_link(ref)} |\n'
analysis += '\n## Types with imports but no code use in that scope\n\nThese are excluded from the usage counts. An unused Java import can still impose a compilation dependency until removed.\n\n| Type | Scope | Example evidence |\n|---|---|---|\n'
for c in D['classes']:
    for scope in ('upstream', 'browser'):
        if c['import_counts'][scope] and not c['counts'][scope]:
            ref = next(r for r in c['evidence'] if r['scope'] == scope)
            analysis += f'| `{c["name"]}` | {scope} | {source_link(ref)} |\n'

analysis += '\n## Per-type source evidence\n\nFile counts measure files containing a code reference, not method calls or execution frequency. One example is shown per type; the JSON retains every matching file. Browser evidence is preferred when available.\n\n| Type | Main MPS files | Browser files | Auxiliary files | Example source |\n|---|---:|---:|---:|---|\n'
for c in D['classes']:
    if not c['counts']['upstream'] and not c['counts']['browser']:
        continue
    refs = [r for r in c['evidence'] if r['method'] != 'import-only']
    ref = next((r for r in refs if r['scope'] == 'browser'), next(r for r in refs if r['scope'] == 'upstream'))
    analysis += f'| `{c["name"]}` | {c["counts"]["upstream"]} | {c["counts"]["browser"]} | {c["counts"]["auxiliary"]} | {source_link(ref)} |\n'

analysis += '''
## Reproduction and files

Run `refresh.ps1` with paths to Python and a JDK 21-or-newer `java.exe`; the supplied catalog command uses Java source-file launch and `--release 21`. The script only refreshes files in this report directory.

- `REPORT.md`: this editable report, both diagrams, package class tables and per-type evidence.
- `swing-packages.mmd`, `awt-packages.mmd`: editable Mermaid diagrams.
- `classes.csv`: all 239 detected types, including auxiliary-only and import-only entries, with separate scope counts.
- `inventory.json`: all evidence and the exact selected browser sources with file hashes.
- `api-results/jdk-21.tsv`: freshly generated reference API catalog.
- `analyze.py`, `render_report.py`, `ApiInventory.java`, `refresh.ps1`: reproducible extraction and report generation.
'''
(HERE / 'REPORT.md').write_text(intro + '\n' + '\n'.join(sections) + '\n' + analysis, encoding='utf-8')
(HERE / 'chat-tables.md').write_text('\n\n'.join(class_table(p) for p in ('javax.swing', 'java.awt')) + '\n', encoding='utf-8')
print('Created REPORT.md and two package diagrams')
