/*
 * Copyright 2000-2024 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */
package jetbrains.mps.reloading;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import jetbrains.mps.util.FileUtil;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.List;

/*package*/final class LocalResourceClassPathItem extends RealClassPathItem {

  private final File myLocation;

  LocalResourceClassPathItem(@NotNull File location) {
    myLocation = location;
  }

  @Override
  @Nullable
  public ClassBytes getClassBytes(String name) {
    return null;
  }

  @Override
  public boolean hasClass(String name) {
    return false;
  }

  @Override
  @Nullable
  public URL getResource(String name) {
    name = name.trim();
    // JFTR, IDEA's ImageDataByPathLoader trims starting '/' in an icon's path, therefore generally expect non-absolute location here.
    // However, may be worth checking charAt(0) == '/' and strip it off, in case it's some other code trying to access
    // module resource as a "module-absolute" path
    // XXX in fact, could get "../../path", which we shall not process to prevent module going outside of myLocation, but do I care?
    while (!name.isEmpty() && name.charAt(0) == '/') {
      name = name.substring(1); // DoS me with long path starting with zillions of '/'
    }
    if (name.isEmpty()) {
      return null;
    }
    try {
      File resourceFile = new File(myLocation, name.replace('/', File.separatorChar));
      return resourceFile.exists()? new URL(FileUtil.toFileUri(resourceFile, resourceFile.isDirectory())) : null;
    } catch (MalformedURLException e) {
      return null;
    }
  }

  @Override
  public List<RealClassPathItem> flatten() {
    return Collections.singletonList(this);
  }

  @Override
  public String getPath() {
    return "[resource]" + myLocation.getPath(); // not to eclipse FileClassPathItem for the same location, just in case.
  }
}
