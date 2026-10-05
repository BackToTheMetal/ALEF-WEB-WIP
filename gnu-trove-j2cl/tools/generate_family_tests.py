#!/usr/bin/env python3
"""Expand typed scenarios; independent array models specify map behavior."""
from pathlib import Path
import sys

ROOT = Path(__file__).resolve().parents[1]
TYPES = [('Byte', 'byte'), ('Int', 'int'), ('Long', 'long'), ('Float', 'float'), ('Double', 'double')]


def main():
    methods, calls = [], []
    for kn, kt in TYPES:
        for vn, vt in TYPES:
            name = kn + vn
            calls.append(f'        test{name}();')
            methods.append(f'''
    static void test{name}() {{
        T{kn}{vn}HashMap map = new T{kn}{vn}HashMap(1);
        {vt}[] values = new {vt}[64];
        boolean[] present = new boolean[64];
        int size = 0;
        for (int step = 0; step < 1200; step++) {{
            int slot = ContractSuite.next() & 63;
            {kt} key = ({kt})(slot - 32);
            {vt} value = ({vt})((ContractSuite.next() & 127) - 64);
            if ((step & 3) != 0) {{
                ContractSuite.check(map.put(key, value) == (present[slot] ? values[slot] : 0), "{name}:put");
                if (!present[slot]) size++;
                present[slot] = true;
                values[slot] = value;
            }} else {{
                ContractSuite.check(map.remove(key) == (present[slot] ? values[slot] : 0), "{name}:remove");
                if (present[slot]) size--;
                present[slot] = false;
            }}
            ContractSuite.check(map.size() == size, "{name}:size");
            ContractSuite.check(map.containsKey(key) == present[slot], "{name}:contains");
            ContractSuite.check(map.get(key) == (present[slot] ? values[slot] : 0), "{name}:get");
            if (step % 100 == 0) {{ map.compact(); map.ensureCapacity(20); }}
        }}
        T{kn}{vn}HashMap copy = (T{kn}{vn}HashMap)map.clone();
        copy.put(({kt})100, ({vt})17);
        ContractSuite.check(!map.containsKey(({kt})100), "{name}:clone");
        map.transformValues(v -> ({vt})(v + 1));
        map.compact();
        map.retainEntries((k, v) -> k >= -20);
        T{kn}{vn}Iterator it = map.iterator();
        int seen = 0;
        while (it.hasNext()) {{
            it.advance();
            int slot = (int)it.key() + 32;
            ContractSuite.check(it.key() >= -20, "{name}:retain");
            ContractSuite.check(it.value() == ({vt})(values[slot] + 1), "{name}:transform");
            it.setValue(({vt})2);
            seen++;
            it.remove();
        }}
        ContractSuite.check(map.isEmpty(), "{name}:iterator remove");
        ContractSuite.record("{name}:visited=" + seen);
        map.put(({kt})1, ({vt})4);
        map.increment(({kt})1);
        map.adjustValue(({kt})1, ({vt})2);
        ContractSuite.check(map.get(({kt})1) == 7, "{name}:adjust");
        ContractSuite.check(!map.adjustValue(({kt})2, ({vt})1), "{name}:adjust missing");
        java.util.Map<{boxed(kt)}, {boxed(vt)}> decorated = new T{kn}{vn}HashMapDecorator(map);
        decorated.put(({kt})2, ({vt})9);
        ContractSuite.check(map.get(({kt})2) == 9, "{name}:decorator put");
        decorated.entrySet().iterator().next().setValue(({vt})6);
        ContractSuite.check(map.containsValue(({vt})6), "{name}:decorator entry");
        decorated.clear();
        ContractSuite.check(map.isEmpty(), "{name}:decorator clear");
    }}
''')
        calls.append(f'        test{kn}Collections();')
        methods.append(f'''
    static void test{kn}Collections() {{
        T{kn}HashSet set = new T{kn}HashSet(new {kt}[] {{1, 2, 2, 3}});
        ContractSuite.check(set.size() == 3 && !set.add(({kt})2), "{kn}:set duplicates");
        set.removeAll(new {kt}[] {{2, 4}});
        set.addAll(new {kt}[] {{4, 5}});
        set.retainAll(new {kt}[] {{1, 4}});
        ContractSuite.check(set.containsAll(new {kt}[] {{1, 4}}) && set.size() == 2, "{kn}:set bulk");
        T{kn}HashSet clone = (T{kn}HashSet)set.clone();
        clone.clear();
        ContractSuite.check(set.size() == 2, "{kn}:set clone");
        java.util.Set<{boxed(kt)}> decorated = new T{kn}HashSetDecorator(set);
        decorated.remove(({kt})1);
        ContractSuite.check(!set.contains(({kt})1), "{kn}:set decorator");
        T{kn}Iterator iterator = set.iterator();
        iterator.next(); iterator.remove();
        ContractSuite.check(set.isEmpty(), "{kn}:set iterator");

        T{kn}ArrayList list = new T{kn}ArrayList(new {kt}[] {{1, 2, 3}});
        list.insert(1, new {kt}[] {{4, 5}});
        ContractSuite.check(list.size() == 5 && list.get(2) == 5, "{kn}:list insert");
        list.remove(1, 2); list.add(({kt})4); list.reverse();
        ContractSuite.check(list.get(0) == 4 && list.get(3) == 1, "{kn}:list reverse");
        list.sort();
        ContractSuite.check(list.binarySearch(({kt})3) == 2, "{kn}:list search");
        list.fill(1, 3, ({kt})7);
        ContractSuite.check(list.get(1) == 7 && list.get(0) == 1, "{kn}:list fill");
        list.transformValues(v -> ({kt})(v + 1));
        ContractSuite.check(list.grep(v -> v > 5).size() == 2, "{kn}:list grep");
        ContractSuite.check(list.inverseGrep(v -> v > 5).size() == 2, "{kn}:list inverseGrep");
        T{kn}ArrayList copy = (T{kn}ArrayList)list.clone();
        copy.set(0, ({kt})42);
        ContractSuite.check(list.get(0) == 2, "{kn}:list clone");
        {kt}[] target = new {kt}[8]; list.toNativeArray(target, 0, list.size());
        ContractSuite.check(target[0] == 2 && target[4] == 0, "{kn}:list array");
        list.resetQuick(); list.add(({kt})9); list.trimToSize();
        ContractSuite.check(list.size() == 1 && list.get(0) == 9, "{kn}:list reset");

        TObject{kn}HashMap<String> objects = new TObject{kn}HashMap<>();
        objects.put("zero", ({kt})0); objects.put("one", ({kt})1);
        ContractSuite.check(objects.containsKey("zero") && !objects.containsKey("missing"), "{kn}:object sentinel");
        objects.adjustValue("one", ({kt})2);
        ContractSuite.check(objects.get("one") == 3, "{kn}:object adjustment");
        objects.transformValues(v -> ({kt})(v + 2));
        objects.retainEntries((k, v) -> k.equals("one"));
        ContractSuite.check(objects.size() == 1 && objects.get("one") == 5, "{kn}:object retain");
        new TObject{kn}HashMapDecorator<String>(objects).clear();
        ContractSuite.check(objects.isEmpty(), "{kn}:object decorator");

        T{kn}ObjectHashMap<String> primitive = new T{kn}ObjectHashMap<>();
        primitive.put(({kt})0, null); primitive.put(({kt})1, "one");
        ContractSuite.check(primitive.containsKey(({kt})0) && primitive.get(({kt})0) == null, "{kn}:primitive null");
        T{kn}ObjectHashMap<String> pc = (T{kn}ObjectHashMap<String>)primitive.clone();
        pc.put(({kt})1, "copy");
        ContractSuite.check(primitive.get(({kt})1).equals("one"), "{kn}:primitive clone");
        new T{kn}ObjectHashMapDecorator<String>(primitive).remove(({kt})1);
        ContractSuite.check(!primitive.containsKey(({kt})1), "{kn}:primitive decorator");
    }}
''')
    path = ROOT / 'tests/contract/org/trovej2cl/FamilyScenarios.java'
    path.parent.mkdir(parents=True, exist_ok=True)
    content = '''// Generated by tools/generate_family_tests.py.
package org.trovej2cl;
import gnu.trove.*;
import gnu.trove.decorator.*;
final class FamilyScenarios {
    static void run() {
''' + '\n'.join(calls) + '\n    }\n' + '\n'.join(methods) + '\n}\n'
    if '--check' in sys.argv:
        if path.read_text() != content: raise SystemExit('Generated family tests are out of date')
    else: path.write_text(content)


def boxed(t):
    return {'byte': 'Byte', 'int': 'Integer', 'long': 'Long', 'float': 'Float', 'double': 'Double'}[t]


if __name__ == '__main__':
    main()
