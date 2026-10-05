/*
 * Copyright 2003-2025 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jetbrains.mps.vfs.util;

import jetbrains.mps.logging.Logger;
import jetbrains.mps.vfs.IFileSystem;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Locale;

//this is an internal class with assertions usable for checking formats of file paths in File/Jar/Jrt FSes
public final class PathFormatChecker {
  private static final Logger LOG = Logger.getLogger(PathFormatChecker.class);

  private final String myPath;

  public PathFormatChecker(@NotNull String path) {
    myPath = path;
  }

  public PathFormatChecker osIndependentPath() {
    // the backslash is valid on linux, for example
    // we have faced such a file https://github.com/systemd/systemd/blob/main/units/system-systemd%5Cx2dcryptsetup.slice
    // obviously it does not do much, still we are not able to work with project file a\b.class on linux,
    // but at least now, when just traversing some tree with such file names, we do not crash
    if (myPath.contains("\\")) {
      LOG.warning("Possibly not os-independent path: " + myPath, new PathFormatException("", myPath));
    }
    return this;
  }

  public PathFormatChecker osDependentPath() {
    String badSeparator = "/".equals(File.separator) ? "\\" : "/";

    if (myPath.contains(badSeparator)) {
      LOG.warning("Possibly not os-dependent path: " + myPath, new PathFormatException("", myPath));
    }
    return this;
  }

  public PathFormatChecker absolute() {
    if (!isAbsolute(myPath)) {
      throw new PathFormatException("Path should be absolute: " + myPath + ", os " + System.getProperty("os.name"), myPath);
    }
    return this;
  }

  // com.intellij.openapi.util.io.OSAgnosticPathUtil.isAbsolute
  private static boolean isAbsolute(@NotNull String path) {
    return path.startsWith("/") || isAbsoluteDosPath(path) || isUncRootPath(path);
  }

  private static boolean isAbsoluteDosPath(@NotNull String path) {
    if (path.length() > 2 && path.charAt(1) == ':' && isSeparator(path.charAt(2))) {
      char ch = path.charAt(0);
      return 'A' <= ch && ch <= 'Z' || 'a' <= ch && ch <= 'z';
    }
    return false;
  }

  private static boolean isUncRootPath(@NotNull String path) {
    if (path.length() > 1 && isSeparator(path.charAt(0)) && path.charAt(1) == path.charAt(0)) {
      final boolean isWindows = System.getProperty("os.name").toLowerCase(Locale.ENGLISH).startsWith("windows");
      // PathUtilRt.isWindowsUNCRoot
      if (!isWindows) {
        return false;
      }
      // !PathUtilRt.hasFileSeparatorsOrNavigatableDots
      if (path.length() > 2 && path.charAt(2) == '.') {
        // "//.", "//./", "//.." or "//../"
        return path.length() == 3 || isSeparator(path.charAt(3)) || (path.charAt(3) == '.' && (path.length() == 4 || isSeparator(path.charAt(4))));
      }
      return true;
    }
    return false;
  }

  private static boolean isSeparator(char c) {
    return c == '/' || c == '\\';
  }




  public PathFormatChecker noDots() {
    for (String part : myPath.split(IFileSystem.SEPARATOR)) {
      if (".".equals(part) || "..".equals(part)) {
        throw new PathFormatException("Path should not contain \".\" and \"..\": " + myPath, myPath);
      }
    }
    return this;
  }

  public PathFormatChecker nonEmpty() {
    if (myPath.trim().isEmpty()) {
      throw new PathFormatException("Empty suffix not allowed: " + myPath, myPath);
    }
    return this;
  }

  public PathFormatChecker noSeparators() {
    if (myPath.contains(IFileSystem.SEPARATOR)) {
      throw new PathFormatException("Separators are not allowed: " + myPath, myPath);
    }
    return this;
  }

  //Checks that no slash is after file name. E.g. /a/b/ is error, while c:/, /a.jar!/ and / are not
  public PathFormatChecker noOddEndSlash() {
    if (!(PathUtil.isRoot(myPath) ||
        !myPath.endsWith(IFileSystem.SEPARATOR) ||
        myPath.endsWith("!" + IFileSystem.SEPARATOR)))  {
      throw new PathFormatException("Only archive paths can end with " + IFileSystem.SEPARATOR + ": " + myPath, myPath);
    }
    return this;
  }

  /**
   * fixme control flow exception for now
   */
  public static final class PathFormatException extends RuntimeException {
    @NotNull
    private final String myProblemPath;

    public PathFormatException(@NotNull String msg, @NotNull String problemPath) {
      super(msg);
      myProblemPath = problemPath;
    }

    @NotNull
    public String getProblemPath() {
      return myProblemPath;
    }
  }
}
