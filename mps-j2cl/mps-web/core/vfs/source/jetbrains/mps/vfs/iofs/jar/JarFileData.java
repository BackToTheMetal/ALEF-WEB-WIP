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
package jetbrains.mps.vfs.iofs.jar;

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
