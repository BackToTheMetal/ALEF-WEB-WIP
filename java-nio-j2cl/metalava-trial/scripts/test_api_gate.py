#!/usr/bin/env python3
"""Negative control against the actual compiled trial: a missing type must fail."""
import json
import tempfile
import zipfile
from pathlib import Path

from check_api import ROOT, check, header_key


def main():
    assert header_key("public class C implements A<X,Y>, B") == header_key("public class C implements B,A<X,Y>")
    assert header_key("public class C implements A<X,Y>") != header_key("public class C implements A<Y,X>")
    with tempfile.TemporaryDirectory(prefix="metalava-api-control-") as temporary:
        folder = Path(temporary)
        broken = folder / "missing-type.jar"
        with zipfile.ZipFile(ROOT / "build/raw-stubs.jar") as source, zipfile.ZipFile(broken, "w") as target:
            assert "java/nio/ByteOrder.class" in source.namelist()
            for entry in source.infolist():
                if entry.filename != "java/nio/ByteOrder.class":
                    target.writestr(entry, source.read(entry.filename))
        report = folder / "missing-type-control.json"
        try:
            check(broken, report)
        except SystemExit as error:
            assert error.code == 1
        else:
            raise AssertionError("The gate accepted a missing NIO type")
        result = json.loads(report.read_text())
        assert any(item == {"type": "java.nio.ByteOrder", "missing": "class"} for item in result["missing"])
    print("PASS: missing class rejected; only interface order is normalized, not generic argument order")


if __name__ == "__main__":
    main()
