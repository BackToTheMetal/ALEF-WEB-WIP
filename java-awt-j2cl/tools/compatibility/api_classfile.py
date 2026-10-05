"""Read visibility and enclosing-class metadata without loading replacement JRE classes."""

import struct


class Reader:
    def __init__(self, data):
        self.data = data
        self.offset = 0

    def take(self, count):
        result = self.data[self.offset:self.offset + count]
        if len(result) != count:
            raise ValueError("Truncated class file")
        self.offset += count
        return result

    def u1(self):
        return self.take(1)[0]

    def u2(self):
        return struct.unpack(">H", self.take(2))[0]

    def u4(self):
        return struct.unpack(">I", self.take(4))[0]


def class_metadata(data):
    reader = Reader(data)
    if reader.u4() != 0xCAFEBABE:
        raise ValueError("Not a Java class file")
    reader.take(4)
    pool = [None] * reader.u2()
    index = 1
    while index < len(pool):
        tag = reader.u1()
        if tag == 1:
            pool[index] = reader.take(reader.u2()).decode("utf-8", errors="replace")
        elif tag == 7:
            pool[index] = reader.u2()
        elif tag in (3, 4, 9, 10, 11, 12, 17, 18):
            reader.take(4)
        elif tag in (5, 6):
            reader.take(8)
            index += 1
        elif tag in (8, 16, 19, 20):
            reader.take(2)
        elif tag == 15:
            reader.take(3)
        else:
            raise ValueError(f"Unsupported constant-pool tag: {tag}")
        index += 1
    access = reader.u2()
    this_class = reader.u2()
    name = pool[pool[this_class]].replace("/", ".")
    reader.take(2)
    reader.take(2 * reader.u2())
    for _ in range(2):
        for _ in range(reader.u2()):
            reader.take(6)
            for _ in range(reader.u2()):
                reader.take(2)
                reader.take(reader.u4())
    outer = None
    local = False
    for _ in range(reader.u2()):
        attribute = pool[reader.u2()]
        payload = Reader(reader.take(reader.u4()))
        if attribute == "EnclosingMethod":
            local = True
        if attribute == "InnerClasses":
            for _ in range(payload.u2()):
                inner_index, outer_index, simple_name, flags = (payload.u2() for _ in range(4))
                if inner_index == this_class:
                    access = flags
                    outer = pool[pool[outer_index]].replace("/", ".") if outer_index else None
                    local = local or simple_name == 0
    return {"name": name, "visibility": "public" if access & 1 else "protected" if access & 4 else "private" if access & 2 else "package", "enclosingType": outer, "static": bool(access & 8), "local": local}


def accessible_classes(classes):
    metadata = {name: class_metadata(data) for name, data in classes.items()}
    reachable = {}

    def accessible(name, visiting):
        if name in reachable:
            return reachable[name]
        if name in visiting or name not in metadata:
            return False
        entry = metadata[name]
        visible = not entry["local"] and entry["visibility"] in ("public", "protected")
        if entry["enclosingType"]:
            visible = visible and accessible(entry["enclosingType"], visiting | {name})
        reachable[name] = visible
        return visible

    return {name: entry for name, entry in metadata.items() if accessible(name, set())}
