| Type in `gnu.trove` | Main MPS files | Browser files | Example use |
|---|---:|---:|---|
| **`THashMap`** | 26 | 22 | Node lookup, model caches, type-system state and workbench maps |
| **`THashSet`** | 37 | 28 | Type-system sets, structure registry, references and editor tracking |
| **`TIntArrayList`** | 2 | 1 | Column widths in the editor vertical layout; also a collections-language wrapper |
| **`TIntObjectHashMap`** | 4 | 2 | Editor cell actions and keymaps; upstream persistence and workbench indexes |
| **`TLongIntHashMap`** | 3 | 3 | Concept and enumeration-literal indexes |
| **`TLongObjectHashMap`** | 3 | 3 | Numeric node-ID lookup and structure adapter caches |
| **`TObjectHashingStrategy`** | 2 | 1 | Object-identity equality/hash strategy; also upstream JPS integration |
| **`TObjectIntHashMap`** | 9 | 5 | Concept-switch indexes, type-system rules and workbench lookup |
| `TByteArrayList` | 1 | 0 | Collections-language byte-list wrapper |
| `TDoubleArrayList` | 1 | 0 | Collections-language double-list wrapper |
| `TFloatArrayList` | 1 | 0 | Collections-language float-list wrapper |
| `THash` | 1 | 0 | Superclass of the generator implementation NodeMap |
| `TIntHashSet` | 1 | 0 | Workbench PropertyValueIndex |
| `TLongArrayList` | 1 | 0 | Collections-language long-list wrapper |
| `TShortArrayList` | 1 | 0 | Collections-language short-list wrapper; absent from the current J2CL provider |
