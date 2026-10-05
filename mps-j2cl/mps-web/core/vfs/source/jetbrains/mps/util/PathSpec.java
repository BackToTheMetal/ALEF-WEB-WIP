/*
 * Copyright 2000-2023 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */
package jetbrains.mps.util;

import jetbrains.mps.vfs.IFile;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.Stream.Builder;

/**
 * Help to specify a file system location, with optional use of macro values in paths
 *
 * @author Artem Tikhomirov
 * @since 2023.1
 */
public class PathSpec {
  private final String myValue;
  private IFile myResolvedFile;

  public PathSpec(@NotNull String value) {
    myValue = value;
  }

  public PathSpec(@NotNull IFile file) {
    myValue = file.getPath();
    myResolvedFile = file;
  }

  public String value() {
    return myValue;
  }

  /**
   * tells if there's any macro in the path specification
   */
  public boolean hasMacro() {
    return MacroHelper.MACRO_PATTERN.matcher(myValue).find();
  }

  /**
   * Legacy code used to process macros in the very start of the path string only; this method
   * helps to mimic this behavior. Generally, MPS shall process macros inside any place of the path
   * specification. It's just not very common for automatically expanded/shrunken paths to get macro
   * in a different place. However, if we start using this {@code PathSpec} elsewhere, e.g. where a
   * user can enter a value, {@link #hasMacro()} is more appropriate.
   */
  public boolean startsWithMacro() {
    final MacroHelper.MacroMatcher mm = MacroHelper.MACRO_PATTERN.matcher(myValue);
    return mm.find() && mm.start() == 0;
  }

  public Stream<String> allMacro() {
    final Builder<String> builder = Stream.builder();
    final MacroHelper.MacroMatcher mm = MacroHelper.MACRO_PATTERN.matcher(myValue);
    while (mm.find()) {
      final String macroName = myValue.substring(mm.start(), mm.end());
      if (macroName.isBlank()) {
        continue;
      }
      builder.add(macroName);
    }
    return builder.build();
  }

  public Optional<IFile> resolve(@NotNull Function<String, IFile> expandMacro) {
    myResolvedFile = expandMacro.apply(myValue);
    return Optional.ofNullable(myResolvedFile);
  }

  public boolean resolved() {
    return myResolvedFile != null;
  }

  @NotNull
  public IFile resolvedFile() {
    checkAccessResolved();
    return myResolvedFile;
  }

  public String resolvedPath() {
    checkAccessResolved();
    return myResolvedFile.getPath();
  }

  /**
   * PROVISIONAL API
   * I expect to further shape API around path expansion/shrinking a bit outside of present {@link MacroHelper} scope, so this
   * is transitional API to move on with just few code changes around; don't expect it to stay long
   */
  public String shrink(@NotNull MacroHelper macroHelper) {
    if (myResolvedFile != null) {
      return macroHelper.shrinkPath(myResolvedFile, myValue);
    } else {
      // startsWithMacro(), not hasMacro(), as a tribute to legacy logic (macros in the start of a path only).
      // Hope to rework this logic once get closer to a MacroHelper replacement.
      return startsWithMacro() ? myValue : macroHelper.shrinkPath(myValue, null);
    }
  }

  private void checkAccessResolved() {
    if (!resolved()) {
      throw new IllegalStateException("Shall make sure the path spec is resolved() first");
    }
  }
}
