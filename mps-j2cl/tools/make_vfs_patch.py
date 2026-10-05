#!/usr/bin/env python3
"""Produce the reviewed VFS browser replacements; never modifies the MPS checkout."""
import argparse
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
RESULTS = {}


def source(path):
    return (ROOT / "mps" / path).read_text(encoding="utf-8")


def replace(text, before, after):
    if text.count(before) != 1:
        raise RuntimeError("Upstream fragment changed: " + before[:100])
    return text.replace(before, after)


def output(path, text):
    RESULTS[path] = text


def whole_class(path, body):
    original = source(path)
    output(path, original[:original.index("package ")] + body)


def generate():
    path = "core/vfs/source/jetbrains/mps/util/MacroHelper.java"
    text = source(path).replace("import java.util.regex.Pattern;\n", "")
    start = text.index("  // XXX is it intentional")
    end = text.index("\n  String expandPath", start)
    text = text[:start] + r'''  /**
   * Browser profile's scanner for this one fixed macro grammar. It preserves the
   * upstream greedy match, including empty names and extra closing braces.
   * This is intentionally not a general java.util.regex.Pattern implementation.
   */
  MacroPattern MACRO_PATTERN = new MacroPattern();

  final class MacroPattern {
    public MacroMatcher matcher(CharSequence input) {
      return new MacroMatcher(input.toString());
    }
  }

  final class MacroMatcher {
    private final String input;
    private int next;
    private int start = -1;
    private int end = -1;

    private MacroMatcher(String input) {
      this.input = input;
    }

    public boolean find() {
      start = end = -1;
      for (int from = next; from + 1 < input.length(); from++) {
        if (input.charAt(from) != '$' || input.charAt(from + 1) != '{') {
          continue;
        }
        int lastClosingBrace = -1;
        for (int i = from + 2; i < input.length(); i++) {
          char c = input.charAt(i);
          if (c == '$' || c == '{') {
            break;
          }
          if (c == '}') {
            lastClosingBrace = i;
          }
        }
        if (lastClosingBrace >= 0) {
          start = from;
          end = next = lastClosingBrace + 1;
          return true;
        }
      }
      next = input.length();
      return false;
    }

    public int start() {
      checkMatch();
      return start;
    }

    public int end() {
      checkMatch();
      return end;
    }

    public String group() {
      return input.substring(start(), end());
    }

    public String group(int number) {
      checkMatch();
      if (number != 0 && number != 1) {
        throw new IndexOutOfBoundsException("No group " + number);
      }
      return group();
    }

    private void checkMatch() {
      if (start < 0) {
        throw new IllegalStateException("No match found");
      }
    }
  }
''' + text[end:]
    output(path, text)

    path = "core/vfs/source/jetbrains/mps/util/PathSpec.java"
    text = source(path).replace("import java.util.regex.Matcher;\n", "")
    text = text.replace("final Matcher mm =", "final MacroHelper.MacroMatcher mm =")
    output(path, text)

    path = "core/project/source/jetbrains/mps/project/ProjectModuleLoader.java"
    text = source(path).replace("import java.util.regex.Matcher;\n", "")
    text = replace(text, "Matcher matcher = MacroHelper.MACRO_PATTERN", "MacroHelper.MacroMatcher matcher = MacroHelper.MACRO_PATTERN")
    text = replace(text, 'String.format("Modules are loaded: %d new; %d removed", loadedModules, myDroppedDescriptorFiles.size())',
                   '"Modules are loaded: " + loadedModules + " new; " + myDroppedDescriptorFiles.size() + " removed"')
    text = replace(text, 'String.format("Can\'t load module from %s. File doesn\'t exist.", descriptorFile.getPath())',
                   '"Can\'t load module from " + descriptorFile.getPath() + ". File doesn\'t exist."')
    text = replace(text, 'String.format("Module %s (%s) not found in the project repository", e.moduleReference.getModuleName(), e.descriptorFile)',
                   '"Module " + e.moduleReference.getModuleName() + " (" + e.descriptorFile + ") not found in the project repository"')
    text = replace(text, 'String.format("Can\'t load module from %s. Unknown file type.", descriptorFile.getPath())',
                   '"Can\'t load module from " + descriptorFile.getPath() + ". Unknown file type."')
    output(path, text)

    path = "core/vfs/source/jetbrains/mps/vfs/path/PathFormats.java"
    text = source(path).replace("import java.util.regex.Pattern;\n", "")
    text = replace(text, '''    for (String s : path.split(Pattern.quote(separator))) {
      if (!s.isEmpty()) {
        result.add(s);
      }
    }''', '''    int from = 0;
    int next;
    while ((next = path.indexOf(separator, from)) >= 0) {
      if (next > from) {
        result.add(path.substring(from, next));
      }
      from = next + separator.length();
    }
    if (from < path.length()) {
      result.add(path.substring(from));
    }''')
    output(path, text)

    path = "core/vfs/source/jetbrains/mps/util/FileUtil.java"
    text = source(path)
    for imp in ["java.util.regex.Pattern", "java.util.zip.ZipEntry", "java.util.zip.ZipOutputStream"]:
        text = text.replace("import " + imp + ";\n", "")
    start = text.index('  @SuppressWarnings({"UnusedDeclaration"})')
    end = text.index("  public static void copyDir", start)
    text = text[:start] + '''  public static void zip(File dir, File to) {
    throw new UnsupportedOperationException("ZIP creation requires an archive provider in the browser profile");
  }

  public static void zip(Map<String, File> entries, File to) {
    throw new UnsupportedOperationException("ZIP creation requires an archive provider in the browser profile");
  }

''' + text[end:]
    text = text.replace("basePath.split(Pattern.quote(pathSeparator))", "splitLiteral(basePath, pathSeparator)")
    text = text.replace("targetPath.split(Pattern.quote(pathSeparator))", "splitLiteral(targetPath, pathSeparator)")
    start = text.index("  private abstract static class Packer")
    text = text[:start] + r'''  /**
   * Lexical file URI used because the sibling File/URI profiles do not implement
   * toURI/toURL. Does not access storage or assume a browser can fetch this URL.
   * The ASCII form encodes the path's exact Unicode sequence; unlike JDK URI it
   * does not first normalize decomposed Unicode to NFC.
   */
  public static String toFileUri(File file, boolean ascii) {
    File absolute = file.getAbsoluteFile();
    String path = absolute.getPath().replace(File.separatorChar, '/');
    if (!path.startsWith("/")) {
      path = "/" + path;
    }
    if (absolute.isDirectory() && !path.endsWith("/")) {
      path += "/";
    }
    if (path.startsWith("//")) {
      path = "//" + path;
    }
    StringBuilder result = new StringBuilder("file:");
    for (int i = 0; i < path.length();) {
      char c = path.charAt(i);
      if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9')
          || "/-_.!~*'();:@&=+$,".indexOf(c) >= 0
          || (!ascii && c >= 128 && !(c <= 159) && !Character.isSpaceChar(c))) {
        result.append(c);
        i++;
      } else {
        int end = i + 1;
        if (Character.isHighSurrogate(c) && end < path.length() && Character.isLowSurrogate(path.charAt(end))) {
          end++;
        }
        byte[] bytes = path.substring(i, end).getBytes(DEFAULT_CHARSET);
        for (byte value : bytes) {
          int unsigned = value & 255;
          result.append('%').append("0123456789ABCDEF".charAt(unsigned >>> 4))
              .append("0123456789ABCDEF".charAt(unsigned & 15));
        }
        i = end;
      }
    }
    return result.toString();
  }

  // String.split(Pattern.quote(separator)) semantics, including empty input,
  // zero-width splitting for an empty separator, and discarded trailing empties.
  private static String[] splitLiteral(String value, String separator) {
    if (value.isEmpty()) {
      return new String[]{""};
    }
    if (separator.isEmpty()) {
      String[] result = new String[value.length()];
      for (int i = 0; i < result.length; i++) {
        result[i] = value.substring(i, i + 1);
      }
      return result;
    }
    java.util.ArrayList<String> parts = new java.util.ArrayList<>();
    int from = 0;
    int next;
    while ((next = value.indexOf(separator, from)) >= 0) {
      parts.add(value.substring(from, next));
      from = next + separator.length();
    }
    parts.add(value.substring(from));
    while (!parts.isEmpty() && parts.get(parts.size() - 1).isEmpty()) {
      parts.remove(parts.size() - 1);
    }
    return parts.toArray(new String[0]);
  }
}
'''
    output(path, text)

    path = "core/vfs/source/jetbrains/mps/vfs/QualifiedPath.java"
    text = replace(source(path), "fs.chars().allMatch(Character::isLetter)",
                   "isFilesystemName(fs)")
    text = replace(text, "  @NotNull\n  public String getFsId()", '''  private static boolean isFilesystemName(String fs) {
    for (int i = 0; i < fs.length(); i++) {
      if (!Character.isLetter(fs.charAt(i))) return false;
    }
    return true;
  }

  @NotNull
  public String getFsId()''')
    output(path, text)

    path = "core/vfs/source/jetbrains/mps/vfs/iofs/file/LocalFile.java"
    text = replace(source(path), "return myFile.toURI().toURL();",
                   "return new URL(FileUtil.toFileUri(myFile, false));")
    output(path, text)

    path = "core/vfs/source/jetbrains/mps/vfs/iofs/jar/JarEntryFile.java"
    text = source(path).replace("import java.net.URI;\n", "")
    text = replace(text, "import jetbrains.mps.util.IFileUtil;",
                   "import jetbrains.mps.util.IFileUtil;\nimport jetbrains.mps.util.FileUtil;")
    text = replace(text, 'String encoded = new File(myJarFileData.getFile().getAbsoluteFile() + Path.ARCHIVE_SEPARATOR + myEntryPath).toURI().toASCIIString();',
                   'String encoded = FileUtil.toFileUri(new File(myJarFileData.getFile().getAbsoluteFile() + Path.ARCHIVE_SEPARATOR + myEntryPath), true);')
    text = replace(text, 'return URI.create("jar:" + encoded).toURL();', 'return new URL("jar:" + encoded);')
    text = replace(text, "return myJarFileData.getFile().lastModified();",
                   'throw new UnsupportedOperationException("Archive timestamps require a filesystem provider in the browser profile");')
    output(path, text)

    path = "core/vfs/source/jetbrains/mps/util/IFileUtil.java"
    text = source(path).replace("import java.text.SimpleDateFormat;\n", "")
    text = replace(text, 'String.format("Failed to copy %s to %s", oldFile, newFile)',
                   '"Failed to copy " + oldFile + " to " + newFile')
    text = replace(text, 'String.format("received io error when trying to access the file %s", file)',
                   '"received io error when trying to access the file " + file')
    text = replace(text, 'String prefix = "mps-" + new SimpleDateFormat("yyyy-MM-dd-").format(new Date());', '''// Date's local calendar fields preserve the upstream default-time-zone date.
    // The browser profile uses numeric Gregorian names independent of host locale.
    Date today = new Date();
    String year = Integer.toString(today.getYear() + 1900);
    while (year.length() < 4) {
      year = "0" + year;
    }
    int month = today.getMonth() + 1;
    int day = today.getDate();
    String prefix = "mps-" + year + "-" + (month < 10 ? "0" : "") + month + "-" + (day < 10 ? "0" : "") + day + "-";''')
    output(path, text)

    path = "core/vfs/source/jetbrains/mps/vfs/util/PathFormatChecker.java"
    text = replace(source(path), '''String.format("Path should be absolute: %s, os %s",
                                                  myPath,
                                                  System.getProperty("os.name"))''',
                   '''"Path should be absolute: " + myPath + ", os " + System.getProperty("os.name")''')
    output(path, text)

    path = "core/vfs/deprecated/jetbrains/mps/vfs/Files.java"
    text = replace(source(path), '''String.format("The path '%s' ends with '.jar' or '.zip' but the contents are not recognized as a zip archive", absolutePath)''',
                   '''"The path '" + absolutePath + "' ends with '.jar' or '.zip' but the contents are not recognized as a zip archive"''')
    output(path, text)

    whole_class("core/vfs/source/jetbrains/mps/vfs/iofs/jar/JarFileData.java", '''package jetbrains.mps.vfs.iofs.jar;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

/**
 * Lexical archive identity for the browser profile. No ZIP/JAR reader is present
 * in the composed sibling JRE, so content requests fail explicitly.
 */
class JarFileData {
  private final File myFile;

  JarFileData(File file, JarFileDataCache cache) {
    myFile = file;
  }

  File getFile() {
    return myFile;
  }

  Set<String> getFiles(String dir) {
    throw unavailable();
  }

  Set<String> getSubdirectories(String dir) {
    throw unavailable();
  }

  boolean exists(String path) {
    throw unavailable();
  }

  boolean isDirectory(String path) {
    throw unavailable();
  }

  String getParentDirectory(String dir) {
    int lastSlash = dir.lastIndexOf('/');
    return lastSlash < 0 ? "" : dir.substring(0, lastSlash);
  }

  InputStream openStream(String path) throws IOException {
    throw unavailable();
  }

  long getLength(String path) {
    throw unavailable();
  }

  private UnsupportedOperationException unavailable() {
    return new UnsupportedOperationException("Archive content requires a ZIP/JAR provider in the browser profile: " + myFile);
  }
}
''')
    whole_class("core/vfs/source/jetbrains/mps/vfs/iofs/jar/JarFileDataCache.java", '''package jetbrains.mps.vfs.iofs.jar;

import java.io.File;

/**
 * Lexical archive handles own no streams or native resources in this profile.
 * They are not retained in a strong global cache or tied to unavailable Java
 * weak-reference notifications. JarEntryFile equality also compares file paths.
 */
final class JarFileDataCache {
  public JarFileData getDataFor(File file) {
    return new JarFileData(file, this);
  }
}
''')
    whole_class("core/vfs/source/jetbrains/mps/vfs/iofs/jar/ZipFileContainer.java", '''package jetbrains.mps.vfs.iofs.jar;

/**
 * Upstream implementation type retained in the source inventory. The browser
 * archive boundary opens no ZIP files and therefore has no resources to close.
 */
class ZipFileContainer {
}
''')


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--check", action="store_true")
    args = parser.parse_args()
    generate()
    for relative, text in RESULTS.items():
        target = ROOT / "mps-web" / relative
        if args.check:
            if not target.is_file() or target.read_text(encoding="utf-8") != text:
                raise SystemExit("VFS replacement differs: " + relative)
        else:
            target.parent.mkdir(parents=True, exist_ok=True)
            target.write_text(text, encoding="utf-8", newline="\n")
    print(("Checked" if args.check else "Generated") + " " + str(len(RESULTS)) + " VFS/caller replacements")


if __name__ == "__main__":
    main()
