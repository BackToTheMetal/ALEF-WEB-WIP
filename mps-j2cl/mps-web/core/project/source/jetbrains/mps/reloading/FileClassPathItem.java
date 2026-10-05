/*
 * Copyright 2003-2024 JetBrains s.r.o.
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

import gnu.trove.THashMap;
import gnu.trove.THashSet;
import jetbrains.mps.project.MPSExtentions;
import jetbrains.mps.util.NameUtil;
import jetbrains.mps.util.ReadUtil;

import java.io.File;
import jetbrains.mps.util.FileUtil;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Kostik
 */
class FileClassPathItem extends RealClassPathItem {
  private final File myClassPath;
  private final Map<String, Set<String>> myAvailableClassesCache = new THashMap<>();

  FileClassPathItem(File classPath) {
    myClassPath = classPath;
  }

  @Override
  public String getPath() {
    return myClassPath.getPath();
  }

  @Override
  public boolean hasClass(String name) {
    String namespace = NameUtil.namespaceFromLongName(name);
    String shortname = NameUtil.shortNameFromLongName(name);

    if (!myAvailableClassesCache.containsKey(namespace)) {
      buildCacheFor(namespace);
    }

    Set<String> classes = myAvailableClassesCache.get(namespace);
    return classes != null && classes.contains(shortname);
  }

  @Override
  public synchronized ClassBytes getClassBytes(String name) {
    String namespace = NameUtil.namespaceFromLongName(name);
    String shortName = NameUtil.shortNameFromLongName(name);

    if (!myAvailableClassesCache.containsKey(namespace)) {
      buildCacheFor(namespace);
    }

    Set<String> classes = myAvailableClassesCache.get(namespace);
    if (classes == null || !classes.contains(shortName)) {
      return null;
    }

    File path = new File(getModelDir(namespace), shortName + MPSExtentions.DOT_CLASSFILE);
    try {
      byte[] bytes;
      try (InputStream inp = new FileInputStream(path)) {
        bytes = ReadUtil.read(inp);
      }

      return bytes == null ? null : new DefaultClassBytes(bytes, new URL(FileUtil.toFileUri(path, false)));
    } catch (IOException e) {
      return null;
    }
  }

  @Override
  public URL getResource(String name) {
    try {
      File resourceFile = new File(myClassPath, name.replace('/', File.separatorChar));
      return resourceFile.exists()? new URL(FileUtil.toFileUri(resourceFile, resourceFile.isDirectory())) : null;
    } catch (MalformedURLException e) {
      return null;
    }
  }

  private synchronized void buildCacheFor(String namespace) {
    // namespace != null
    Set<String> classes = null;
    File dir = getModelDir(namespace);

    String[] files = dir.list();
    if (files != null) {
      for (String name : files) {
        if (name.endsWith(MPSExtentions.DOT_CLASSFILE)) { //isDirectory is quite expensive operation
          if (classes == null) {
            classes = new THashSet<>(files.length);
          }
          String classname = name.substring(0, name.length() - MPSExtentions.DOT_CLASSFILE.length());
          classes.add(classname.intern()); // not sure if there's any reason to strive for unique classname here, not that many duplicating classes out there
        }
      }
    }

    myAvailableClassesCache.put(namespace.intern(), classes);
  }

  @Override
  public List<RealClassPathItem> flatten() {
    return Collections.singletonList(this);
  }

  private File getModelDir(String namespace) {
    // namespace != null
    return new File(myClassPath, NameUtil.pathFromNamespace(namespace));
  }

  public String toString() {
    return "file-cp: " + myClassPath;
  }
}
