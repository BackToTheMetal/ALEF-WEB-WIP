#!/usr/bin/env python3
"""Generate small kernel-required System and Collections additions from exact J2CL sources."""
import argparse
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]


def replace_once(source, before, after):
    if source.count(before) != 1:
        raise SystemExit("Unexpected J2CL anchor: " + before)
    return source.replace(before, after, 1)


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--check", action="store_true")
    args = parser.parse_args()
    for name in ("java/lang/System.java", "java/util/Collections.java"):
        source = (ROOT / ".work/j2cl-20260402/jre/java" / name).read_text(encoding="utf-8")
        if name.endswith("System.java"):
            source = replace_once(source, "  private static final int MILLIS_TO_NANOS = 1_000_000;",
                                  '  private static final String LINE_SEPARATOR = getProperty("line.separator", "\\n");\n\n'
                                  '  /** Browser text defaults to LF; a build-time line.separator define may override it. */\n'
                                  '  public static String lineSeparator() {\n    return LINE_SEPARATOR;\n  }\n\n'
                                  '  private static final int MILLIS_TO_NANOS = 1_000_000;')
        else:
            anchor = '  @SuppressWarnings(value = {"unchecked", "cast"})\n  public static <T> Iterator<T> emptyIterator() {'
            source = replace_once(source, anchor,
                                  "  public static <T> Enumeration<T> emptyEnumeration() {\n"
                                  "    return new Enumeration<T>() {\n"
                                  "      @Override public boolean hasMoreElements() { return false; }\n"
                                  "      @Override public T nextElement() { throw new NoSuchElementException(); }\n"
                                  "      @Override public Iterator<T> asIterator() { return Collections.emptyIterator(); }\n"
                                  "    };\n  }\n\n" + anchor)
        output = ROOT / "third_party/j2cl/api-support/jre/java" / name
        if args.check:
            if output.read_text(encoding="utf-8") != source: raise SystemExit("Stale kernel JRE source: " + str(output))
        else:
            output.parent.mkdir(parents=True, exist_ok=True)
            output.write_text(source, encoding="utf-8", newline="\n")
    print("Kernel JRE sources: System.lineSeparator and Collections.emptyEnumeration")


if __name__ == "__main__":
    main()
