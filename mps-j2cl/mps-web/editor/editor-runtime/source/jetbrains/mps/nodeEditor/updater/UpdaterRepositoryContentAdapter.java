/*
 * Copyright 2003-2019 JetBrains s.r.o.
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
package jetbrains.mps.nodeEditor.updater;

import jetbrains.mps.nodeEditor.EditorComponent;
import org.jetbrains.mps.openapi.model.SModel;
import org.jetbrains.mps.openapi.model.SModelReference;
import org.jetbrains.mps.openapi.module.SModule;
import org.jetbrains.mps.openapi.module.SRepositoryContentAdapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

/**
 * User: shatalin
 * Date: 11/09/14
 */
class UpdaterRepositoryContentAdapter extends SRepositoryContentAdapter {
  private final EditorComponent myEditorComponent;
  private SModelReference myModelReference;
  private Collection<SModel> myUsedModels = Collections.emptySet();


  UpdaterRepositoryContentAdapter(EditorComponent editorComponent) {
    myEditorComponent = editorComponent;
  }

  /**
   * We track modelRemoved event for debug purposes - to record corresponding stacktrace within associated EditorComponent.
   * May be removed in future.
   */
  @Override
  public void beforeModelRemoved(SModule module, SModel model) {
    if (myModelReference == null) {
      return;
    }
    if (myModelReference.equals(model.getReference())) {
      myEditorComponent.setModelDisposedTrace(new Throwable("Model removed").getStackTrace());
    }
    stopListening(model);
  }

  @Override
  public void modelReplaced(SModel model) {
    if (myUsedModels.contains(model)) {
      myEditorComponent.getEditorContext().getRepository().getModelAccess().runReadInEDT(() -> {
        if (myEditorComponent.isDisposed()) {
          return;
        }
        myEditorComponent.rebuildAfterReloadModel();
        myEditorComponent.rebuildEditorContent();
      });
    }
  }

  @Override
  public void modelUnloaded(SModel model) {
    modelReplaced(model);
  }

  @Override
  protected void stopListening(SModel model) {
    // Listeners we try to remove here were added in #setUsedModels. We don't need to track models as they come,
    // as we are interested in particular models only. However, here we utilize the fact SRepositoryContentAdapter
    // sends out stopListening(SModel) for any mode being removed from a repository (there's no dedicated event,
    // as it used to be with SModelRepositoryListener, to tell model is removed as part of module removal.
    // In fact, GlobalRepositoriesListener used the same trick, stopListening(SModel) to send out SModelRepositoryListener#modelRemoved)
    if (myUsedModels.remove(model)) {
      model.removeModelListener(this);
    }
  }

  void setMainModel(SModel model) {
    myModelReference = model.getReference();
  }

  void setUsedModels(Set<SModel> models) {
    for (SModel old : myUsedModels) {
      old.removeModelListener(this);
    }
    myUsedModels = new ArrayList<>(models);
    for (SModel m : models) {
      m.addModelListener(this);
    }
  }

}
