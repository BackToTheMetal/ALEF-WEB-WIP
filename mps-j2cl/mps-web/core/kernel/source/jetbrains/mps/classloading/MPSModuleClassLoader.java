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
package jetbrains.mps.classloading;

import jetbrains.mps.classloading.ModuleClassLoader.ModuleClassLoaderIsDisposedException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.mps.openapi.module.SModuleReference;

/** Common lifecycle for module loaders backed by precompiled browser classes. */
public abstract class MPSModuleClassLoader extends ClassLoader {
  private boolean myDisposed;
  private Throwable myDisposeTrace;

  public MPSModuleClassLoader(ClassLoader parent) {
    super(parent);
  }

  protected MPSModuleClassLoader(String name, ClassLoader parent) {
    super(name, parent);
  }

  @Override
  public String toString() {
    String name = getName() == null ? super.toString()
        : "MPS Module CL " + getName() + "@" + Integer.toHexString(hashCode());
    return isDisposed() ? name + " [DISPOSED]" : name;
  }

  @NotNull
  public abstract Class<?> loadOwnClass(String name) throws ClassNotFoundException;

  public abstract SModuleReference getModule();

  public boolean isDisposed() {
    return myDisposed;
  }

  public void dispose() {
    if (!myDisposed) {
      myDisposeTrace = new Throwable("Original dispose trace for " + getModule().getModuleName());
      myDisposed = true;
      clearRegistrations();
    }
  }

  protected final void checkNotDisposed() throws ModuleClassLoaderIsDisposedException {
    if (isDisposed()) {
      throw new ModuleClassLoaderIsDisposedException(getModule(), myDisposeTrace);
    }
  }

  @Override
  protected final void checkOpen() {
    checkNotDisposed();
  }
}
