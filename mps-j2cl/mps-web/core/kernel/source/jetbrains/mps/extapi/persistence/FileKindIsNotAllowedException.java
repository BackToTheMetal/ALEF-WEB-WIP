/*
 * Copyright 2003-2016 JetBrains s.r.o.
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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.mps.annotations.Immutable;


/**
 * Each {@link FileBasedModelRoot} has a notion of the so-called "allowed file kinds".
 * This exception is thrown when model root is addressed externally with file kind (as parameter) which
 * is not allowed by this particular model root.
 *
 * @since 3.5
 * @author apyshkin
 */
@Immutable
public final class FileKindIsNotAllowedException extends IllegalArgumentException {
  private final SourceRootKind myDisallowedKind;
  private final SourceRoot myRoot;

  private final String myKindName;
  private final String myRootPath;

  public FileKindIsNotAllowedException(@NotNull SourceRootKind disallowedKind, SourceRoot root) {
    myDisallowedKind = disallowedKind;
    myRoot = root;
    myKindName = null;
    myRootPath = null;
  }

  public FileKindIsNotAllowedException(@NotNull String kindName) {
    myKindName = kindName;
    myRoot = null;
    myDisallowedKind = null;
    myRootPath = null;
  }

  public FileKindIsNotAllowedException(@NotNull String kindName, @NotNull String rootPath) {
    myDisallowedKind = null;
    myRoot = null;
    myKindName = kindName;
    myRootPath = rootPath;
  }

  @Override
  @NotNull
  public String getMessage() {
    if (myRoot != null) {
      return "Trying to register the source root '" + myRoot + "' with the prohibited FileKind: '" + myDisallowedKind + "'";
    } else {
      return "The FileKind '" + myKindName + "' is not allowed";
    }
  }
}
