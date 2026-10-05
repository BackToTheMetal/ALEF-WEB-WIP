#!/usr/bin/env python3
"""Generate reviewed OpenAPI replacements from pinned sources and JVM identifier data."""
import argparse
import json
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
PREFIX = "core/openapi/source/org/jetbrains/mps/openapi/"


def replace_once(text, before, after):
    if text.count(before) != 1:
        raise SystemExit("Pinned MPS source fragment changed: " + before[:90])
    return text.replace(before, after)


def java_string(value):
    return json.dumps(value, ensure_ascii=True)


def render():
    result = {}
    for relative, before, after in [
        ("persistence/DataSourceNotSupportedProblem.java",
         'String.format("The data source \'%s\' is not supported", mySource)',
         '"The data source \'" + mySource + "\' is not supported"'),
        ("persistence/ModelFactory.java",
         'String.format("Could not handle mandatory save option %s", mandatoryOption)',
         '"Could not handle mandatory save option " + mandatoryOption'),
    ]:
        source = (ROOT / "mps" / (PREFIX + relative)).read_text()
        result[PREFIX + relative] = replace_once(source, before, after)

    identifier = json.loads((ROOT / "compatibility/java-identifiers.json").read_text())
    properties = {}
    resource = ROOT / "mps/core/resources_en/source/jetbrains/mps/project/validation/CoreBundle.properties"
    for line in resource.read_text().splitlines():
        if line.startswith("smodel.name.check."):
            key, value = line.split("=", 1)
            properties[key] = value
    name = (ROOT / "mps" / (PREFIX + "model/SModelName.java")).read_text()
    name = name.replace("import javax.lang.model.SourceVersion;\n", "").replace("import java.util.ResourceBundle;\n", "")
    message_parts = properties["smodel.name.check.exception.text"].split("%s")
    if len(message_parts) != 3:
        raise SystemExit("Unexpected model name diagnostic format")
    message = java_string(message_parts[0]) + " + fullName + " + java_string(message_parts[1]) + " + check.getProblemDescription() + " + java_string(message_parts[2])
    name = replace_once(name,
        '      ResourceBundle bundle = ResourceBundle.getBundle("jetbrains.mps.project.validation.CoreBundle");\n'
        '      throw new IllegalArgumentException(String.format(bundle.getString("smodel.name.check.exception.text"), fullName, check.getProblemDescription()));',
        '      throw new IllegalArgumentException(' + message + ');')
    cases = "\n".join("        case " + java_string(key) + ": return " + java_string(value) + ";"
                      for key, value in sorted(properties.items()) if key != "smodel.name.check.exception.text")
    name = replace_once(name,
        '      ResourceBundle bundle = ResourceBundle.getBundle("jetbrains.mps.project.validation.CoreBundle");\n'
        '      return bundle.getString(myProblemId);',
        '      switch (myProblemId) {\n' + cases + '\n        default: throw new IllegalStateException(myProblemId);\n      }')
    name = replace_once(name, "SourceVersion.isName(longName)", "isJavaName(longName.toString())")
    helpers = '''
  // Complete codepoint ranges exported from the pinned JVM; no host compiler API is needed.
  private static boolean inRanges(int codepoint, int[] ranges) {
    int low = 0, high = ranges.length / 2 - 1;
    while (low <= high) {
      int middle = (low + high) >>> 1;
      if (codepoint < ranges[middle * 2]) high = middle - 1;
      else if (codepoint > ranges[middle * 2 + 1]) low = middle + 1;
      else return true;
    }
    return false;
  }

  private static boolean isJavaName(String value) {
    if (value.isEmpty()) return false;
    int start = 0;
    for (int end = 0; end <= value.length(); end++) {
      if (end != value.length() && value.charAt(end) != '.') continue;
      if (end == start) return false;
      String identifier = value.substring(start, end);
      for (String keyword : JAVA_KEYWORDS) {
        if (keyword.equals(identifier)) return false;
      }
      int codepoint = value.codePointAt(start);
      if (!inRanges(codepoint, JAVA_START)) return false;
      for (int index = start + Character.charCount(codepoint); index < end;) {
        codepoint = value.codePointAt(index);
        if (!inRanges(codepoint, JAVA_PART)) return false;
        index += Character.charCount(codepoint);
      }
      start = end + 1;
    }
    return true;
  }
'''
    for field, key in [("JAVA_START", "start"), ("JAVA_PART", "part")]:
        values = identifier[key]
        rows = ["      " + ", ".join(str(n) for n in values[i:i+16]) + "," for i in range(0, len(values), 16)]
        helpers += "\n  private static final int[] " + field + " = {\n" + "\n".join(rows) + "\n  };\n"
    helpers += "\n  private static final String[] JAVA_KEYWORDS = {\n      " + ", ".join(java_string(k) for k in identifier["keywords"]) + "\n  };\n"
    name = name[:name.rfind("}")] + helpers + "}\n"
    result[PREFIX + "model/SModelName.java"] = name
    return result


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--check", action="store_true")
    args = parser.parse_args()
    for relative, text in render().items():
        target = ROOT / "mps-web" / relative
        if args.check:
            if not target.exists() or target.read_text() != text:
                raise SystemExit("Stale OpenAPI replacement: " + relative)
        else:
            target.parent.mkdir(parents=True, exist_ok=True)
            target.write_text(text, newline="\n")
    print("OpenAPI replacements " + ("verified" if args.check else "generated"))


if __name__ == "__main__":
    main()
