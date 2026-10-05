/*
 * Copyright 2003-2023 JetBrains s.r.o.
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
package jetbrains.mps.util;

import jetbrains.mps.vfs.IFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.mps.openapi.module.SModule;


public interface MacroHelper {
  /**
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

  String expandPath(@Nullable String path);

  /**
   * @deprecated use {@link #shrinkPath(IFile, String)} or {@link #shrinkPath(String, String)} instead
   */
  @Deprecated(since = "2022.3", forRemoval = true)
  default String shrinkPath(@Nullable String absolutePath) {
    return shrinkPath(absolutePath, null);
  }

  /**
   * Please override this method instead of {@link #shrinkPath(String)}
   * Optional hint with original path helps to pick proper macro value (to preserve original one in case there are few pointing to the same location)
   * @since 2022.3
   */
  default String shrinkPath(@Nullable String absolutePath, @Nullable String hintOriginalPath) {
    // not abstract method to facilitate transition of clients in case they got own MacroHelper implementation.
    // can remove the body once the method is around for a while.
    return shrinkPath(absolutePath);
  }

  /**
   * Capture explicit scenarios where we got IFile for path shrinking, to get rid of string paths eventually.
   * @since 2022.3
   */
  default String shrinkPath(@Nullable IFile absolutePath, @Nullable String hintOriginalPath) {
    return shrinkPath(absolutePath == null ? null : absolutePath.getPath(), hintOriginalPath);
  }

  /**
   * leaves the path unchanged
   */
  final class MacroNoHelper implements MacroHelper {
    @Override
    public String expandPath(@Nullable String path) {
      return path;
    }

    @Override
    public String shrinkPath(@Nullable String absolutePath, @Nullable String hintOriginalPath) {
      // XXX perhaps, `hint != null ? hint : absolutePath` would be more close to the idea of the class?
      return absolutePath;
    }
  }

  /**
   * PROVISIONAL API
   *
   * Source capable of producing MacroHelper instances.
   * XXX need to decide whether this source has to be generic or have specific methods for typical usage scenarios (like module, etc). In latter case,
   *     would be nice to have a dedicated method for project. OTOH, it's unlikely we can specify all possible usage scenarions, and former approach, then,
   *     is more appealing (specific Source, e.g. the one aware of module files and their locations) would get instantiated where appropriate.
   * <p/>
   * Implementation note. Implementors of this interface that have no idea how to support specific methods are free to return  more general instance, usually
   * {@linkplain #global() global one}.
   * @since 2018.1
   */
  interface Source {
    @NotNull
    MacroHelper global();
    @NotNull
    MacroHelper module(SModule m);
    @NotNull
    MacroHelper moduleFile(IFile f);
    @NotNull
    MacroHelper projectFile(IFile f);
  }
}
