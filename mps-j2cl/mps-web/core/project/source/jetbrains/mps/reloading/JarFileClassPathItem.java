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
package jetbrains.mps.reloading;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import org.jetbrains.annotations.NotNull;

/** Lexical archive identity; archive bytes require an explicit browser backend. */
public class JarFileClassPathItem extends RealClassPathItem {
  private final File myFile;
  private final String myPath;

  JarFileClassPathItem(@NotNull File jarFile, @NotNull String path) {
    myFile = jarFile;
    myPath = path;
  }

  JarFileClassPathItem(@NotNull File jarFile, @NotNull String entry, @NotNull String path) throws FileNotFoundException {
    throw unavailable();
  }

  @Override public String getPath() { return myPath; }
  public File getFile() { return myFile; }
  @Override public boolean hasClass(String name) { throw unavailable(); }
  @Override public synchronized ClassBytes getClassBytes(String name) { throw unavailable(); }
  @Override public URL getResource(String name) { throw unavailable(); }
  @Override public List<RealClassPathItem> flatten() { return Collections.singletonList(this); }
  @Override public String toString() { return "jar-cp: " + myFile; }
  private static UnsupportedOperationException unavailable() {
    return new UnsupportedOperationException("Reading JAR archives requires a browser archive backend; register compiled module classes and resources instead");
  }
}
