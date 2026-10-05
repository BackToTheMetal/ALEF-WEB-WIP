/*
 * Copyright 2003-2022 JetBrains s.r.o.
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
package jetbrains.mps.extapi.persistence;

import jetbrains.mps.logging.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.mps.annotations.Immutable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;

/**
 * Maps {@link SourceRootKind} to {@link SourceRoot}.
 * Preserves the order of the <code>SourcePath</code>s.
 */
final class SourcePaths {
  private final Map<SourceRootKind, List<SourceRoot>> myFileKind2SourcePaths = new LinkedHashMap<>();
  @NotNull private final Predicate<SourceRootKind> isFileKindAllowed;

  public SourcePaths() {
    this((kind) -> true); // always allows
  }

  public SourcePaths(@NotNull Predicate<SourceRootKind> isFileKindAllowed0) {
    isFileKindAllowed = isFileKindAllowed0;
  }

  /**
   * @return an empty list if the kind is not registered.
   */
  @NotNull
  @Immutable
  public synchronized List<SourceRoot> getByKind(@NotNull SourceRootKind kind) {
    List<SourceRoot> list = myFileKind2SourcePaths.get(kind);
    return list != null ? unmodifiableList(list) : emptyList();
  }

  /**
   * Adds source roots to the container
   *
   * @throws FileKindIsNotAllowedException if provided file kind is not allowed
   * @throws SourceRootAlreadyExistsException if such source path already exists
   */
  public synchronized void addSourceRoot(@NotNull SourceRootKind kind, @NotNull SourceRoot root) {
    if (!isKindAllowed(kind)) {
      throw new FileKindIsNotAllowedException(kind, root);
    }
    SourceRootKind existingRootKind = getKind(root);
    if (existingRootKind != null) {
      Logger.getLogger(SourcePaths.class).warning(SourceRootAlreadyExistsException.getMsg(root, kind, root, existingRootKind));
      return;
    }
    myFileKind2SourcePaths.putIfAbsent(kind, new ArrayList<>());
    myFileKind2SourcePaths.get(kind).add(root);
  }

  private boolean isKindAllowed(@NotNull SourceRootKind kind) {
    return isFileKindAllowed.test(kind);
  }

  @Nullable
  private SourceRootKind getKind(@NotNull SourceRoot root) {
    for (SourceRootKind kind : myFileKind2SourcePaths.keySet()) {
      List<SourceRoot> sourceRoots = myFileKind2SourcePaths.get(kind);
      if (sourceRoots.contains(root)) {
        return kind;
      }
    }
    return null;
  }

  @Nullable
  public SourceRootKind removeSourceRoot(SourceRoot root) {
    for (SourceRootKind kind : myFileKind2SourcePaths.keySet()) {
      List<SourceRoot> sourceRoots = myFileKind2SourcePaths.get(kind);
      if (sourceRoots.contains(root)) {
        sourceRoots.remove(root);
        return kind;
      }
    }
    return null;
  }

  public void clearAll() {
    myFileKind2SourcePaths.clear();
  }

  @Override
  public int hashCode() {
    return Objects.hash(myFileKind2SourcePaths);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof SourcePaths) {
      return Objects.equals(myFileKind2SourcePaths, ((SourcePaths) obj).myFileKind2SourcePaths);
    }
    return false;
  }

  @Override
  public String toString() {
    return "SourcePaths [" + myFileKind2SourcePaths.values().size() + " source root paths]";
  }

  public static final class SourceRootAlreadyExistsException extends IllegalStateException {
    private final SourceRoot myRoot;
    private final SourceRootKind myPathKind;
    private final SourceRoot myExistingRoot;
    private final SourceRootKind myExistingRootKind;

    public SourceRootAlreadyExistsException(SourceRoot root, SourceRootKind pathKind,
                                            SourceRoot existingRoot, SourceRootKind existingPathKind) {
      myRoot = root;
      myPathKind = pathKind;
      myExistingRoot = existingRoot;
      myExistingRootKind = existingPathKind;
    }

    @Override
    @NotNull
    public String getMessage() {
      return getMsg(myRoot, myPathKind, myExistingRoot, myExistingRootKind);
    }

    @NotNull
    public static String getMsg(SourceRoot root, SourceRootKind pathKind, SourceRoot existingRoot, SourceRootKind existingRootKind) {
      return "Trying to register the source root: [" + root + ", kind " + pathKind + "] while [" + existingRoot + ", kind " + existingRootKind + "] is already registered";
    }
  }
}
