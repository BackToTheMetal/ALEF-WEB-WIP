"""Render Trove package, class and source-dependency analysis."""
import json
from pathlib import Path

HERE = Path(__file__).resolve().parent
D = json.loads((HERE / 'inventory.json').read_text(encoding='utf-8'))
assert int((HERE / 'validation/exit-code.txt').read_text(encoding='utf-8-sig')) == 0, 'Compile the subset before claiming validation'
USAGE = {
    'THashMap': 'Node lookup, model caches, type-system state and workbench maps',
    'THashSet': 'Type-system sets, structure registry, references and editor tracking',
    'TIntArrayList': 'Column widths in the editor vertical layout; also a collections-language wrapper',
    'TIntObjectHashMap': 'Editor cell actions and keymaps; upstream persistence and workbench indexes',
    'TLongIntHashMap': 'Concept and enumeration-literal indexes',
    'TLongObjectHashMap': 'Numeric node-ID lookup and structure adapter caches',
    'TObjectHashingStrategy': 'Object-identity equality/hash strategy; also upstream JPS integration',
    'TObjectIntHashMap': 'Concept-switch indexes, type-system rules and workbench lookup',
    'THash': 'Superclass of the generator implementation NodeMap',
    'TIntHashSet': 'Workbench PropertyValueIndex',
    'TByteArrayList': 'Collections-language byte-list wrapper',
    'TDoubleArrayList': 'Collections-language double-list wrapper',
    'TFloatArrayList': 'Collections-language float-list wrapper',
    'TLongArrayList': 'Collections-language long-list wrapper',
    'TShortArrayList': 'Collections-language short-list wrapper; absent from the current J2CL provider',
}

DIAGRAM = '''flowchart LR
  classDef browser fill:#d9eee3,stroke:#548768,color:#142e20;
  classDef desktop fill:#fff0cd,stroke:#aa8735,color:#352909;
  classDef auxiliary fill:#e8e1f4,stroke:#8c78ae,color:#30223f;
  classDef absent fill:#f1f3f5,stroke:#a8adb5,color:#434851;

  P["gnu.trove<br/>15 main MPS types · 8 browser"]:::browser
  D["gnu.trove.decorator<br/>0 main · 0 browser<br/>15 auxiliary types"]:::auxiliary
  O["Object collections<br/>THashMap · THashSet"]:::browser
  M["Primitive maps<br/>TIntObjectHashMap · TLongObjectHashMap<br/>TLongIntHashMap · TObjectIntHashMap"]:::browser
  L["Editor list<br/>TIntArrayList"]:::browser
  S["Hash strategy<br/>TObjectHashingStrategy"]:::browser
  G["Generator hash base<br/>THash"]:::desktop
  I["Workbench integer set<br/>TIntHashSet"]:::desktop
  A["Other language-runtime lists<br/>TByteArrayList · TDoubleArrayList<br/>TFloatArrayList · TLongArrayList<br/>TShortArrayList"]:::desktop
  U["Remaining exported types<br/>No direct main-MPS reference<br/>Some are internal dependencies"]:::absent
  P --- D
  P --- O
  P --- M
  P --- L
  P --- S
  P --- G
  P --- I
  P --- A
  P --- U
'''


def link(ref):
    return f'[{Path(ref["path"]).name}:{ref["line"]}](<{ref["path"]}:{ref["line"]}>)'


def representative(c):
    refs = [r for r in c['evidence'] if r['method'] != 'import-only']
    return next((r for r in refs if r['scope'] == 'browser'), refs[0])


main_types = [c for c in D['classes'] if c['counts']['upstream']]
browser_types = [c for c in D['classes'] if c['counts']['browser']]
table = ['| Type in `gnu.trove` | Main MPS files | Browser files | Example use |', '|---|---:|---:|---|']
for c in sorted(main_types, key=lambda c: (not bool(c['counts']['browser']), c['name'])):
    short = c['name'].rsplit('.', 1)[1]
    name = f'**`{short}`**' if c['counts']['browser'] else f'`{short}`'
    table.append(f'| {name} | {c["counts"]["upstream"]} | {c["counts"]["browser"]} | {USAGE[short]} |')

body = f'''# MPS requirements for GNU Trove

Analysis date: 2026-09-29. MPS commit: `{D['commit']}`.

Main MPS sources directly reference **{len(main_types)} Trove types**. The selected MPS sources used by this ALEF browser workspace reference **{len(browser_types)} types**, all in `gnu.trove`. The `gnu.trove.decorator` package appears only in the auxiliary test sources in this scan.

The inspected Trove variants have only **two public Java packages**: `gnu.trove` and `gnu.trove.decorator`. The type families expanded in the diagram are descriptive groupings, not additional packages. This is the flat package layout used by these variants, not Trove 3's reorganized layout.

**Colors:** green = directly referenced in selected browser sources; amber = direct references elsewhere in main MPS sources; purple = auxiliary references only; gray = no direct reference from main MPS. Bold table entries are directly referenced in the browser source set. Counts are numbers of source files, not executions.

## Packages and directly referenced types

```mermaid
{DIAGRAM}```

{chr(10).join(table)}

## Version and provider boundaries

| Input | Actual artifact or declaration | Scope |
|---|---|---|
| Current browser library | `org.jetbrains.intellij.deps:trove4j:1.0.20200330`, adapted by `C:/Codex/gnu-trove-j2cl` | Selected by ALEF and MPS Bazel modules |
| MPS platform declaration | `.idea/libraries/gnu_trove.xml` points to `lib/trove.jar` | The original platform JAR is absent from this checkout; its binary identity is not asserted |
| MPS collections-language runtime | Bundled `trove-2.1.0.jar` | Distinct from the browser library baseline |

The JAR catalogs were read directly from the local binary artifacts, using class access flags for public top-level types:

| Package | JetBrains baseline public types | Trove 2.1.0 public types | Main MPS direct types | Browser direct types | Auxiliary direct types |
|---|---:|---:|---:|---:|---:|
'''
for p in D['packages']:
    body += f'| `{p["name"]}` | {p["available"]["jetbrains"]} | {p["available"]["language_2_1"]} | {p["counts"]["upstream"]} | {p["counts"]["browser"]} | {p["counts"]["auxiliary"]} |\n'
body += '''
All eight directly referenced browser types have source providers in the current J2CL library. This checks provider presence, not complete API or behavioral compatibility.

`TShortArrayList` is a concrete boundary: the MPS collections-language wrapper references it, and it exists in the bundled Trove 2.1.0 JAR, but it is absent from the JetBrains baseline and current J2CL provider. The auxiliary tests also name `TShortHashSet`, `TShortShortHashMap`, `TShortHashSetDecorator`, and `TShortShortHashMapDecorator`, which are absent from that provider. Expanding the browser profile to the full collections language therefore needs separate compatibility work.

The similarly named `TByteArrayListDecorator` through `TShortArrayListDecorator` in MPS belong to `jetbrains.mps.baseLanguage.collections.trove.runtime`, **not** `gnu.trove.decorator`. They wrap the primitive list types listed in the main table.

## Direct use versus library support

Keeping only the eight directly referenced source classes would not compile. Following named type dependencies through complete current Trove source files reaches **42 of the provider's 207 Java source files**: the eight direct types plus 34 support types. Every file in this candidate set is in `gnu.trove`; none is in `gnu.trove.decorator`.

The candidate set was compiled in isolation with JDK 25 `javac --release 21`, an empty source path, an empty class path, and annotation processing disabled. It passed, with unchecked-operation warnings. Compiler output and the exact source list are in `validation/`.

This establishes a compilable library subset for these direct roots, **not** the minimum runtime footprint or a validated replacement for the existing browser build. The subset retains complete source units, including methods not called by these consumers. IntelliJ adapters, browser host helpers, ALEF/MPS-extensions code, inferred callback types, or future generated code may introduce additional requirements. No existing library sources or build targets were reduced.

| Supporting role | Additional types in the 42-file set |
|---|---|
| Hash bases | `THash`, `TLongHash`, `TObjectHash`, `TPrimitiveHash` |
| Iterators | `THashIterator`, `TIntObjectIterator`, `TIterator`, `TLongIntIterator`, `TLongObjectIterator`, `TObjectHashIterator`, `TObjectIntIterator`, `TPrimitiveIterator` |
| Callbacks and conversion | `TIntFunction`, `TIntObjectProcedure`, `TIntProcedure`, `TLongIntProcedure`, `TLongObjectProcedure`, `TLongProcedure`, `TObjectFunction`, `TObjectIntProcedure`, `TObjectObjectProcedure`, `TObjectProcedure`, `ToObjectArrayProcedure` |
| Hashing and equality | `CanonicalEquality`, `Equality`, `IdentityEquality`, `TIntHashingStrategy`, `TLongHashingStrategy`, `TObjectCanonicalHashingStrategy`, `TObjectIdentityHashingStrategy` |
| Internal utilities | `ArraySupport`, `FloatSupport`, `HashFunctions`, `PrimeFinder` |

`THash` illustrates the distinction: MPS names it directly only in its desktop generator, but the browser collections still require it internally.

## Auxiliary references

Auxiliary sources directly name 31 types: 16 in `gnu.trove`, and 15 in `gnu.trove.decorator`. Two of the 16 root-package types (`TIntHashSet` and `TLongObjectHashMap`) also occur in main MPS sources. These counts are not additive to the main total.

| Package | Types referenced in auxiliary sources |
|---|---|
'''
for package in ('gnu.trove', 'gnu.trove.decorator'):
    names = [f'`{c["name"].removeprefix(package + ".")}`' for c in D['classes'] if c['package'] == package and c['counts']['auxiliary']]
    body += f'| `{package}` | {", ".join(names)} |\n'

body += '''
## Scope and limitations

- The source scopes match the earlier [Swing/AWT analysis](../mps-ui-dependencies/REPORT.md): 29,684 tracked Java/Kotlin paths, divided into 22,143 main and 7,541 auxiliary paths. There are 84 files containing the `gnu.trove` prefix. Main includes checked-in generated language/runtime code outside the auxiliary path rules.
- The browser scope contains 4,646 selected MPS files, resolved from current source manifests and provider precedence: ALEF replacement, MPS-extensions replacement, MPS replacement, upstream. The JSON records exact paths and SHA-256 hashes. It excludes the implementations of ALEF languages, MPS-extensions languages, IntelliJ adapters, browser host helpers and external libraries.
- Comments and string literals are removed for direct-reference counting. Explicit imports must have a matching code identifier; qualified references and package wildcards are supported. The actual Trove scan has no unresolved named references against the combined artifact catalog. This is lexical analysis, not a compiler-attributed scan of all MPS sources; implicit inherited types, inferred lambda target types, shadowing, reflection and ungenerated source remain limitations.
- `THashSet` is imported but not used in `DoubleTermRules.java`; this import is excluded in both upstream and browser counts.
- The collections-language generator constructs names using `"gnu.trove.T" + ... + "ArrayList"`. These string-producing expressions are recorded separately and excluded from the direct type counts. The count is not a bound on every type the language could generate. Serialized MPS model files were not read or changed.
- The original baseline artifacts have overlapping fully qualified names; their presence does not imply that they can be merged or exchanged on one classpath. No complete compatibility test was run for Trove 2.1.0 or the MPS platform artifact.
- The 42-file set was compile-checked as a standalone Trove subset only. No MPS application build, J2CL compilation or browser behavior test was performed for a reduced provider.

## Source evidence for all detected types

Each row shows one example; `inventory.json` retains all matching files and the source dependency paths. Counts below exclude unused imports. Provider availability means a matching source filename exists in the current J2CL provider, not that the original type's complete contract has been verified.

| Type | Main files | Browser files | Auxiliary files | Port source present | Example source |
|---|---:|---:|---:|---|---|
'''
for c in D['classes']:
    body += f'| `{c["name"]}` | {c["counts"]["upstream"]} | {c["counts"]["browser"]} | {c["counts"]["auxiliary"]} | {"Yes" if c["providers"]["port_source"] else "No"} | {link(representative(c))} |\n'
body += '\n## Artifact fingerprints\n\n'
for key, item in D['artifacts'].items():
    body += f'- `{key}`: `{item["sha256"]}` — [{Path(item["path"]).name}](<{item["path"]}>)\n'
body += '''
## Reproduction

`refresh.ps1 -Python <python.exe> -Javac <JDK-25-javac.exe>` refreshes Git candidate lists, reconstructs the catalogs and selected sources, compiles the candidate Trove subset, and renders this report. It writes only into this analysis directory. The scanner reuses the lexical and scope helpers from the sibling Swing/AWT report directory.

Files: `REPORT.md`, `trove-packages.mmd`, `classes.csv`, `inventory.json`, `analyze_trove.py`, `render_report.py`, `refresh.ps1`, and `validation/`.
'''
(HERE / 'REPORT.md').write_text(body, encoding='utf-8')
(HERE / 'trove-packages.mmd').write_text(DIAGRAM, encoding='utf-8')
(HERE / 'chat-table.md').write_text('\n'.join(table) + '\n', encoding='utf-8')
print('Created Trove report and package diagram')
