/*
 * Copyright 2003-2026 JetBrains s.r.o.
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
package jetbrains.mps.make;

import org.jetbrains.mps.annotations.Immutable;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Path;

@Immutable
public final class FileWithPosition {
  private final Path myFile;
  private final long myLine;
  private final long myColumn;
  private final long myOffset;

  public FileWithPosition(File file, long offset) {
    this(file == null ? null : file.toPath(), offset, -1, -1);
  }

  // use {@code -1} for any unknown value
  // offset, line and column are 0-based
  public FileWithPosition(File file, long offset, long line, long column) {
    this(file == null ? null : file.toPath(), offset, line, column);
  }

  public FileWithPosition(Path file, long offset, long line, long column) {
    myFile = file;
    myOffset = offset;
    myLine = line;
    myColumn = column;
  }


  /**
   * @deprecated use {@link #getPath()}
   */
  @Deprecated(since = "2026.1", forRemoval = true)
  public File getFile() {
    return myFile == null || !myFile.getFileSystem().equals(FileSystems.getDefault()) ? null : myFile.toFile();
  }

  public Path getPath() {
    return myFile;
  }

  public int getOffset() {
    return (int) myOffset;
  }

  public long getOffsetLong() {
    return myOffset;
  }

  public int getLine() {
    return (int) myLine;
  }

  public int getColumn() {
    return (int) myColumn;
  }

  @Override
  public String toString() {
    if (myLine != -1) {
      return String.format("%s[%d:%d]", myFile, myLine, myColumn);
    } else {
      return String.format("%s@%d", myFile, myOffset);
    }
  }
}
