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
package jetbrains.mps.vfs;

import jetbrains.mps.logging.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public final class Files {
  private static final Logger LOG = Logger.getLogger(Files.class);

  private Files() {
  }

  public static boolean isJarOrZipFile(@NotNull File file) throws IOException {
    boolean result = isJarOrZipFile0(file);
    String absolutePath = file.getAbsolutePath();
    if (!result && (absolutePath.endsWith(".zip") || absolutePath.endsWith(".jar"))) {
      LOG.warning("The path '" + absolutePath + "' ends with '.jar' or '.zip' but the contents are not recognized as a zip archive");
      printDebugOnSuspiciousArchive(file);
    }
    return result;
  }

  private static void printDebugOnSuspiciousArchive(@NotNull File file) throws IOException {
    if (!file.exists()) {
      LOG.warning(" the file does not exist");
      return;
    }
    if (file.isDirectory()) {
      LOG.warning(" the file is a directory");
      return;
    }
    if (file.length() < 4) { // less than 4 bytes
      LOG.warning(" the file length is less than 4 bytes");
      return;
    }
    try (var dis = new DataInputStream(new FileInputStream(file))) {
      int fileSignature = dis.readInt();
      LOG.warning(" the file signature is " + fileSignature);
    }
  }

  private static boolean isJarOrZipFile0(@NotNull File file) throws IOException {
    if (!file.exists()) {
      return false;
    }
    if (file.isDirectory()) {
      return false;
    }
    if (file.length() < 4) { // less than 4 bytes
      return false;
    }
    try (var dis = new DataInputStream(new FileInputStream(file))) {
      int fileSignature = dis.readInt();
      return fileSignature == 0x504B0304 || fileSignature == 0x504B0506 || fileSignature == 0x504B0708;
    }
  }
}
