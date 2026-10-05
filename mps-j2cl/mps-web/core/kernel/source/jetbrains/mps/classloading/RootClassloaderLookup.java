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
package jetbrains.mps.classloading;

import jetbrains.mps.extapi.module.SRepositoryExt;
import jetbrains.mps.library.SLibrary;
import jetbrains.mps.module.ReloadableModule;
import jetbrains.mps.smodel.MPSModuleOwner;
import org.jetbrains.mps.openapi.module.SModule;
import org.jetbrains.mps.openapi.module.SRepository;

import java.util.Set;
import java.util.function.Supplier;

/**
 * Looks up so-called parent (or root) class loader. Simply returns the Idea plugin classloader in the case the module is
 * bundled into an idea plugin. Will return an application classloader in the case there is no idea plugin.
 *
 * Class loader returned from {@link ReloadableModule#getClassLoader()} for a given module always depends on this root class loader.
 */
public final class RootClassloaderLookup implements Supplier<ClassLoader> {
  private final SModule myModule;

  public RootClassloaderLookup(SModule module) {
    myModule = module;
  }

  @Override
  public ClassLoader get() {
    SRepository repository = myModule.getRepository();
    if (false == repository instanceof SRepositoryExt) {
      // no idea how/why to look up owners of a module attached to unknown repository.
      return ClassLoader.getSystemClassLoader();
    }
    repository.getModelAccess().checkReadAccess();
    // XXX may want to refactor this to ModuleRepositoryFacade() to hide getOwners internals of an SRepository
    Set<MPSModuleOwner> moduleOwners = ((SRepositoryExt) repository).getOwners(myModule);
    for (MPSModuleOwner owner : moduleOwners) {
      if (owner instanceof SLibrary) {
        ClassLoader classLoader = ((SLibrary) owner).getPluginClassLoader();
        if (classLoader != null) {
          return classLoader;
        }
      }
    }
    return ClassLoader.getSystemClassLoader();

  }
}
