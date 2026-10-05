#!/usr/bin/env python3
"""Generate narrow MPS browser adaptations for clocks, diagnostics and streams."""
import argparse
import hashlib
import json
from pathlib import Path
import re

ROOT = Path(__file__).resolve().parents[1]
KERNEL = 'core/kernel/source/jetbrains/mps/'

def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--check', action='store_true')
    args = parser.parse_args()
    changes = {}
    def edit(path, pairs):
        original = (ROOT / 'mps' / path).read_text(encoding='utf-8')
        value = original
        for before, after in pairs:
            if before not in value:
                raise SystemExit('MPS source fragment changed: ' + path + ': ' + before)
            value = value.replace(before, after)
        changes[path] = (original, value)
        return value

    edit(KERNEL + 'messages/Message.java', [
        ('import java.time.Instant;\n', ''),
        ('private final Instant myCreationTime = Instant.now();',
         'private final long myCreationTime = System.currentTimeMillis();'),
        ('myCreationTime.toEpochMilli()', 'myCreationTime'),
    ])
    edit(KERNEL + 'classloading/MPSClassLoadersRegistry.java', [
        ('import java.time.Duration;\n', ''), ('import java.time.Instant;\n', ''),
        ('Instant ', 'Long '), ('Instant.now()', 'System.currentTimeMillis()'),
        ('Duration.between(myPlanningDisposalTime, now).toMinutes()',
         '((now - myPlanningDisposalTime) / 60000L)'),
    ])
    edit(KERNEL + 'generator/cache/ParseFacility.java', [
        ('myUrl.openStream()', 'ClassLoader.openRegisteredResource(myUrl)'),
    ])
    edit(KERNEL + 'classloading/ClassLoaderManager.java', [
        ('"Possible leaking class loaders : currently there are %d sessions alive. "\n'
         '            + "Please avoid running too many reloads in the single write action".formatted(session.actualNumberOfSessions())',
         '("Possible leaking class loaders : currently there are %d sessions alive. "\n'
         '            + "Please avoid running too many reloads in the single write action").formatted(session.actualNumberOfSessions())'),
    ])
    for name in ['FileClassPathItem', 'LocalResourceClassPathItem']:
        edit('core/project/source/jetbrains/mps/reloading/' + name + '.java', [
            ('import java.io.File;\n', 'import java.io.File;\nimport jetbrains.mps.util.FileUtil;\n'),
            ('resourceFile.toURI().toURL()', 'new URL(FileUtil.toFileUri(resourceFile, resourceFile.isDirectory()))'),
        ] + ([('path.toURI().toURL()', 'new URL(FileUtil.toFileUri(path, false))')] if name == 'FileClassPathItem' else []))
    edit(KERNEL + 'extapi/persistence/SourcePaths.java', [
        ('import java.text.MessageFormat;\n', ''),
        ('MessageFormat.format("Trying to register the source root: [{0}, kind {1}] while [{2}, kind {3}] " +\n'
         '                                  "is already registered", root, pathKind, existingRoot, existingRootKind)',
         '"Trying to register the source root: [" + root + ", kind " + pathKind + "] while [" + existingRoot + ", kind " + existingRootKind + "] is already registered"'),
    ])
    edit(KERNEL + 'extapi/persistence/FileKindIsNotAllowedException.java', [
        ('import java.text.MessageFormat;\n', ''),
        ('MessageFormat.format("Trying to register the source root \'\'{0}\'\' with the prohibited FileKind: \'\'{1}\'\'", myRoot, myDisallowedKind)',
         '"Trying to register the source root \'" + myRoot + "\' with the prohibited FileKind: \'" + myDisallowedKind + "\'"'),
        ('MessageFormat.format("The FileKind \'\'{0}\'\' is not allowed", myKindName)',
         '"The FileKind \'" + myKindName + "\' is not allowed"'),
    ])
    concept_path = 'core/project/source/jetbrains/mps/project/validation/ConceptMissingError.java'
    concept = edit(concept_path, [
        ('import java.text.MessageFormat;\n', ''), ('import java.util.ResourceBundle;\n', ''),
        ('MessageFormat.format(msgFromBundle(), concept.getName(), concept.getLanguage().getQualifiedName())',
         '"Concept \'" + concept.getName() + "\' was not found in the language \'" + concept.getLanguage().getQualifiedName() + "\'"'),
    ])
    bundle = (ROOT / 'mps/core/resources_en/source/jetbrains/mps/project/validation/CoreBundle.properties').read_text()
    if not bundle.startswith("concept.not.found=Concept ''{0}'' was not found in the language ''{1}''\n"):
        raise SystemExit('Pinned concept-not-found message changed')
    concept, count = re.subn(r'  private static String msgFromBundle\(\) \{[\s\S]*?\n  }\n', '', concept, count=1)
    if count != 1: raise SystemExit('ConceptMissingError source changed')
    changes[concept_path] = (changes[concept_path][0], concept)

    rules_path = 'core/aspects/constraints/constraints-runtime/source/jetbrains/mps/core/aspects/constraints/rules/kinds/PredefinedRuleKinds.java'
    rules = edit(rules_path, [('import java.text.MessageFormat;\n', '')])
    # Capture both arguments before rendering, preserving call and toString order.
    pairs = [
        ('return MessageFormat.format("Node \'\'{0}\'\' cannot be child of node \'\'{1}\'\'", context1.getChildNode(), context1.getParentNode());',
         'Object child = context1.getChildNode();\n      Object parent = context1.getParentNode();\n      return "Node \'" + child + "\' cannot be child of node \'" + parent + "\'";'),
        ('return MessageFormat.format("Node \'\'{1}\'\' cannot be parent of node \'\'{0}\'\'", context1.getChildNode(), context1.getParentNode());',
         'Object child = context1.getChildNode();\n      Object parent = context1.getParentNode();\n      return "Node \'" + parent + "\' cannot be parent of node \'" + child + "\'";'),
        ('return MessageFormat.format("The concept \'\'{0}\'\' cannot be root", ((CanBeRootContext) context).getConcept());',
         'return "The concept \'" + ((CanBeRootContext) context).getConcept() + "\' cannot be root";'),
        ('return MessageFormat.format("Node \'\'{1}\'\' cannot be ancestor of node \'\'{0}\'\'",\n'
         '                                  context1.getDescendantNode(),\n                                  context1.getParentNode());',
         'Object descendant = context1.getDescendantNode();\n      Object parent = context1.getParentNode();\n      return "Node \'" + parent + "\' cannot be ancestor of node \'" + descendant + "\'";'),
    ]
    for before, after in pairs:
        if rules.count(before) != 1: raise SystemExit('PredefinedRuleKinds source changed')
        rules = rules.replace(before, after)
    changes[rules_path] = (changes[rules_path][0], rules)

    strings_path = 'core/kernel/kernelSolution/source_gen/jetbrains/mps/util/StringsIO.java'
    strings = edit(strings_path, [('import java.util.Scanner;\n', '')])
    start = strings.index('    Scanner sc = new Scanner(input);')
    end = strings.index('    return lines;', start)
    strings = strings[:start] + '''    if (input == null) throw new NullPointerException("input");
    Utf8Input reader = new Utf8Input(input);
    StringBuilder line = new StringBuilder();
    boolean afterCR = false;
    try {
      int value;
      while ((value = reader.read()) != -1) {
        if (afterCR && value == '\\n') { afterCR = false; continue; }
        afterCR = value == '\\r';
        if (value == '\\r' || value == '\\n' || value == 0x85 || value == 0x2028 || value == 0x2029) {
          lines.add(line.toString());
          line.setLength(0);
        } else {
          if (value <= 0xffff) {
            line.append((char) value);
          } else {
            line.append((char) (0xd800 + ((value - 0x10000) >>> 10)));
            line.append((char) (0xdc00 + ((value - 0x10000) & 0x3ff)));
          }
        }
      }
    } catch (IOException ignored) {
      // Scanner retains decoded text but does not flush an incomplete sequence on an IO failure.
    } finally {
      try { input.close(); } catch (IOException ignored) { }
    }
    if (line.length() != 0) lines.add(line.toString());
''' + strings[end:]
    helper = '''  /** UTF-8 line input with the replacement grouping used by Java 21's default Scanner. */
  private static final class Utf8Input {
    private final InputStream input;
    private final byte[] buffer = new byte[8192];
    private int offset;
    private int limit;
    private int pending = -1;
    private boolean ended;

    Utf8Input(InputStream input) { this.input = input; }

    private int readByte() throws IOException {
      if (pending >= 0) {
        int value = pending;
        pending = -1;
        return value;
      }
      if (ended) return -1;
      if (offset == limit) {
        int count = input.read(buffer, 0, buffer.length);
        if (count < 0) { ended = true; return -1; }
        // A nonconforming stream may return zero; do not spin indefinitely.
        if (count == 0) {
          int value = input.read();
          ended = value == -1;
          return value;
        }
        offset = 0;
        limit = count;
      }
      return buffer[offset++] & 0xff;
    }

    int read() throws IOException {
      int first = readByte();
      if (first < 0x80) return first;
      int count;
      int value;
      if (first >= 0xc2 && first <= 0xdf) {
        count = 2;
        value = first & 0x1f;
      } else if (first >= 0xe0 && first <= 0xef) {
        count = 3;
        value = first & 0x0f;
      } else if (first >= 0xf0 && first <= 0xf4) {
        count = 4;
        value = first & 7;
      } else {
        return 0xfffd;
      }
      for (int index = 1; index < count; index++) {
        int next = readByte();
        // EOF flushes one replacement for the entire incomplete sequence.
        // IOException propagates instead, leaving that sequence unflushed.
        if (next == -1) return 0xfffd;
        if ((next & 0xc0) != 0x80 ||
            (index == 1 && ((first == 0xe0 && next < 0xa0) ||
                           (first == 0xf0 && next < 0x90) ||
                           (first == 0xf4 && next >= 0x90)))) {
          // A bad continuation starts the next character; do not consume it twice.
          pending = next;
          return 0xfffd;
        }
        value = (value << 6) | (next & 0x3f);
      }
      // Java consumes a complete UTF-8 encoded surrogate as one malformed sequence.
      return value >= 0xd800 && value <= 0xdfff ? 0xfffd : value;
    }
  }
'''
    strings = strings.replace('  public static void writeLines(File file, Iterable<String> lines) throws IOException {',
                              helper + '  public static void writeLines(File file, Iterable<String> lines) throws IOException {', 1)
    changes[strings_path] = (changes[strings_path][0], strings)

    jar_path = 'core/project/source/jetbrains/mps/reloading/JarFileClassPathItem.java'
    original = (ROOT / 'mps' / jar_path).read_text(encoding='utf-8')
    jar = original.split('package ', 1)[0] + '''package jetbrains.mps.reloading;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import org.jetbrains.annotations.NotNull;

/** Lexical archive identity; archive bytes require an explicit browser backend. */
public class JarFileClassPathItem extends RealClassPathItem {
  private final File myFile;
  private final String myPath;

  JarFileClassPathItem(@NotNull File jarFile, @NotNull String path) {
    myFile = jarFile;
    myPath = path;
  }

  JarFileClassPathItem(@NotNull File jarFile, @NotNull String entry, @NotNull String path) throws FileNotFoundException {
    throw unavailable();
  }

  @Override public String getPath() { return myPath; }
  public File getFile() { return myFile; }
  @Override public boolean hasClass(String name) { throw unavailable(); }
  @Override public synchronized ClassBytes getClassBytes(String name) { throw unavailable(); }
  @Override public URL getResource(String name) { throw unavailable(); }
  @Override public List<RealClassPathItem> flatten() { return Collections.singletonList(this); }
  @Override public String toString() { return "jar-cp: " + myFile; }
  private static UnsupportedOperationException unavailable() {
    return new UnsupportedOperationException("Reading JAR archives requires a browser archive backend; register compiled module classes and resources instead");
  }
}
'''
    changes[jar_path] = (original, jar)

    manifest = []
    for path, (original, replacement) in sorted(changes.items()):
        output = ROOT / 'mps-web' / path
        manifest.append({'path': path, 'upstream_sha256': hashlib.sha256(original.encode()).hexdigest(),
                         'replacement_sha256': hashlib.sha256(replacement.encode()).hexdigest()})
        if args.check:
            if not output.exists() or output.read_text(encoding='utf-8') != replacement:
                raise SystemExit('Stale replacement: ' + path)
        else:
            output.parent.mkdir(parents=True, exist_ok=True)
            output.write_text(replacement, encoding='utf-8', newline='\n')
    output = ROOT / 'compatibility/kernel-utilities.json'
    expected = json.dumps(manifest, indent=2) + '\n'
    if args.check:
        if output.read_text() != expected: raise SystemExit('Stale kernel utility provenance')
    else:
        output.write_text(expected, encoding='utf-8', newline='\n')
    print('Kernel utility adaptations: ' + str(len(changes)))

if __name__ == '__main__':
    main()
