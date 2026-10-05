package mpsj2cl.vfs;

import java.util.Arrays;
import java.util.stream.Collectors;
import jetbrains.mps.util.FileUtil;
import jetbrains.mps.util.MacroHelper;
import jetbrains.mps.util.PathSpec;
import jetbrains.mps.util.PathSpecBundle;
import jetbrains.mps.vfs.IFile;
import jetbrains.mps.vfs.QualifiedPath;
import jetbrains.mps.vfs.VFSManager;
import jetbrains.mps.vfs.path.Path;
import jetbrains.mps.vfs.path.PathFormat;
import jetbrains.mps.vfs.path.PathFormats;
import jetbrains.mps.vfs.util.PathFormatChecker;
import jetbrains.mps.vfs.util.PathUtil;

/** The same public API operations are evaluated on untouched JVM MPS and the browser overlay. */
public final class VfsLexicalTest {
  private static final char[] MACRO_ALPHABET = {'$', '{', '}', 'a'};

  public static String run() {
    StringBuilder result = new StringBuilder();
    String[] macros = {
        "", "plain/path", "$", "{", "}", "$$", "$a", "${}", "${a}", "${a}/b", "a/${b}",
        "${a}${b}", "${a}x}", "${a}}", "${a}x}tail", "${a$b}", "${a{b}", "${a${b}}",
        "$${b}", "${}${}", "${a\nb}", "${a\r\nb}", "${\u0000}", "${😀}", "before/${a}/after/${b}",
        "${a", "${a}$x}", "${a}{b}", "${a}\n}", "${a} ${b} tail"
    };
    for (String text : macros) {
      PathSpec spec = new PathSpec(text);
      row(result, "macro:" + text, () -> spec.hasMacro() + "|" + spec.startsWithMacro() + "|"
          + spec.allMacro().collect(Collectors.joining("|")));
      row(result, "scan:" + text, () -> scan(text));
      row(result, "shrink:" + text, () -> spec.shrink(new MacroHelper.MacroNoHelper()));
    }
    StringBuilder exhaustive = new StringBuilder();
    for (int length = 0; length <= 6; length++) {
      int count = 1 << (2 * length);
      for (int code = 0; code < count; code++) {
        char[] chars = new char[length];
        for (int position = 0; position < length; position++) {
          chars[position] = MACRO_ALPHABET[(code >>> (position * 2)) & 3];
        }
        String text = new String(chars);
        var matcher = MacroHelper.MACRO_PATTERN.matcher(text);
        while (matcher.find()) {
          exhaustive.append((char) ('A' + matcher.start())).append((char) ('A' + matcher.end()));
          if (!matcher.group().equals(matcher.group(0)) || !matcher.group().equals(matcher.group(1))) {
            throw new AssertionError("Macro capture mismatch: " + text);
          }
        }
        exhaustive.append('.');
      }
    }
    row(result, "5461 exhaustive macro inputs", exhaustive::toString);

    String[] unix = {
        "", ".", "..", "/", "/a", "/a/", "/a//b", "a/b", "a/./b", "a/b/../c", "/a/../b",
        "/a/../../b", "/a///./b//../c/", "//host/share", "//host/share/a/b",
        "///host/share/a", "//host//share/a", "/a!/b", "/a.jar!/b", "/a.JAR!/b/",
        "/a.zip!/b.jar!/c", "/a.jar!/", "/a.zip!/x!/y", "/x.jar!/a/../b", "/x!x/a",
        "/white space/😀/x", "/name.with.dots/..hidden/file"
    };
    String[] win = {
        "", ".", "..", "C:\\", "C:\\a", "C:\\a\\", "C:\\a\\\\b", "a\\b", "a\\.\\b",
        "C:\\a\\..\\b", "\\\\host\\share", "\\\\host\\share\\a\\b", "\\\\host\\\\share\\a",
        "\\rooted", "C:\\a.jar!/b", "C:\\a.zip!/b.jar!/c", "C:\\white space\\😀\\x"
    };
    paths(result, PathFormats.UNIX, unix);
    paths(result, PathFormats.WIN, win);
    for (String text : new String[]{"", ".", "./", "/", "/a//b/./", "//host/share//a/",
        "\\\\host\\share\\\\a\\", "C:\\a\\\\b\\.\\", "/x.jar!/", "/x.jar!/a/", "/x!/a/",
        "/a/../b", "///a////b", "/.///./a/././b/.", "a.b", ".hidden", "x.", "x"}) {
      row(result, "normalize:" + text, () -> FileUtil.normalize(text));
      row(result, "strip:" + text, () -> FileUtil.stripLastSlashes(text));
      row(result, "extension:" + text, () -> FileUtil.getNameWithoutExtension(text) + "|" + FileUtil.getExtension(text));
      row(result, "archive:" + text, () -> PathUtil.extractArchivePath(text) + "|" + PathUtil.hasArchiveFileName(text)
          + "|" + PathUtil.indexOfArchiveSeparator(text, 0));
    }
    for (String[] pair : new String[][]{
        {"/a/b", "/a/c", "/"}, {"/a/b/", "/a/c/", "/"}, {"/", "/a/", "/"},
        {"a.b.c", "a.b.d", "."}, {"a|b|c", "a|b|d|", "|"}, {"a\\b\\c", "a\\b\\d\\", "\\"},
        {"a::b::c", "a::b::d::", "::"}, {"same", "same", ""}, {"abcd", "abxy", ""},
        {"x", "y", "/"}, {"", "", "/"}}) {
      row(result, "relative:" + Arrays.toString(pair), () -> FileUtil.getRelativePath(pair[0], pair[1], pair[2]));
    }
    for (String path : new String[]{"/a/b/../c", "/a/..", "/../a", "/a/b/../../c", "C:/a/../b"}) {
      row(result, "parents:" + path, () -> FileUtil.resolveParentDirs(path));
    }
    PathSpec unresolved = new PathSpec("/unresolved");
    row(result, "unresolved-state", unresolved::resolved);
    row(result, "unresolved-file", unresolved::resolvedFile);
    row(result, "unresolved-path", unresolved::resolvedPath);
    row(result, "failed-resolution", () -> unresolved.resolve(x -> null).isPresent() + "|" + unresolved.resolved());

    VFSManager manager = new VFSManager();
    manager.init();
    try {
      PathSpec resolving = new PathSpec("${home}/module.msd");
      resolving.resolve(text -> manager.getFileSystem(VFSManager.FILE_FS).getFile(text.replace("${home}", "/workspace")));
      row(result, "resolved-state", () -> resolving.resolved() + "|" + resolving.resolvedPath());
      MacroHelper helper = new MacroHelper() {
        public String expandPath(String path) { return path; }
        public String shrinkPath(String path, String hint) { return path + "|hint=" + hint; }
      };
      row(result, "resolved-shrink", () -> resolving.shrink(helper));
      IFile file = resolving.resolvedFile();
      row(result, "file-spec", () -> new PathSpec(file).value() + "|" + new PathSpec(file).resolved());
      PathSpecBundle bundle = new PathSpecBundle(Arrays.asList(unresolved, resolving));
      row(result, "bundle", () -> bundle.isEmpty() + "|" + bundle.paths().map(PathSpec::value).collect(Collectors.joining(";")));
      for (String name : new String[]{"plain.txt", "white space", "a#b%c?d", "a[b]", "é😀", "a;b:c@d&x=y+z,$!()'"}) {
        row(result, "local-url:" + name, () -> url(manager.getFileSystem(VFSManager.FILE_FS).getFile("/mps-j2cl-oracle/" + name)));
        row(result, "jar-url:" + name, () -> url(manager.getFileSystem(VFSManager.JAR_FS).getFile("/mps-j2cl-oracle/archive.jar!/" + name)));
      }
      QualifiedPath qualified = new QualifiedPath("file", "/workspace/a.mps");
      row(result, "qualified", () -> qualified.serialize(null) + "|" + QualifiedPath.deserialize(qualified.serialize(null), null));
    } finally {
      manager.dispose();
    }
    return result.toString();
  }

  private static String scan(String text) {
    StringBuilder result = new StringBuilder();
    var matcher = MacroHelper.MACRO_PATTERN.matcher(text);
    while (matcher.find()) {
      result.append(matcher.start()).append(':').append(matcher.end()).append(':').append(matcher.group(1)).append('|');
    }
    if (matcher.find()) {
      throw new AssertionError("Exhausted matcher must remain exhausted");
    }
    try {
      matcher.start();
      throw new AssertionError("Failed match must invalidate its bounds");
    } catch (IllegalStateException expected) {
      // Both the JVM Pattern and browser scanner expose no match after exhaustion.
    }
    return result.toString();
  }

  private static String url(IFile file) {
    try {
      return file.getUrl().toExternalForm();
    } catch (java.net.MalformedURLException ex) {
      throw new IllegalStateException(ex);
    }
  }

  private static void paths(StringBuilder result, PathFormat format, String[] inputs) {
    for (String input : inputs) {
      String prefix = format.getSeparator() + ":" + input;
      row(result, "parts:" + prefix, () -> format.extractRootPart(input) + "|" + format.extractNonRootParts(input));
      row(result, "path:" + prefix, () -> {
        Path path = format.fromString(input);
        return path.toText() + "|" + path.isRelative() + "|" + path.isArchive() + "|"
            + path.getRootPart() + "|" + path.getAllParts() + "|" + path.getFileName();
      });
      row(result, "normal-path:" + prefix, () -> format.fromString(input).normalize().toText());
      row(result, "parent-path:" + prefix, () -> {
        Path parent = format.fromString(input).getParent();
        return parent == null ? "null" : parent.toText();
      });
      row(result, "copy-path:" + prefix, () -> {
        Path path = format.fromString(input);
        return path.equals(path.copy()) + "|" + (path.hashCode() == path.copy().hashCode());
      });
    }
  }

  private interface Operation {
    Object run();
  }

  private static void row(StringBuilder result, String label, Operation operation) {
    String value;
    try {
      value = String.valueOf(operation.run());
    } catch (jetbrains.mps.vfs.path.PathParseException ex) {
      value = "PathParseException:" + ex;
    } catch (PathFormatChecker.PathFormatException ex) {
      value = "PathFormatException:" + ex.getProblemPath();
    } catch (FileUtil.PathResolutionException ex) {
      value = "PathResolutionException:" + ex.getMessage();
    } catch (IllegalStateException ex) {
      value = "IllegalStateException:" + ex.getMessage();
    } catch (IndexOutOfBoundsException ex) {
      value = "IndexOutOfBoundsException";
    }
    result.append(escape(label)).append('\t').append(escape(value)).append('\n');
  }

  private static String escape(String value) {
    return value.replace("\\", "\\\\").replace("\n", "\\n").replace("\r", "\\r")
        .replace("\t", "\\t").replace("\u0000", "\\0");
  }

  public static void main(String[] args) {
    System.out.print(run());
  }
}
