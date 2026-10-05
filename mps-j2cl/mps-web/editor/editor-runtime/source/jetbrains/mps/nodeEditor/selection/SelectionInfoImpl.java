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
package jetbrains.mps.nodeEditor.selection;

import jetbrains.mps.logging.Logger;
import jetbrains.mps.module.ReloadableModule;
import jetbrains.mps.nodeEditor.cells.DefaultCellInfo;
import jetbrains.mps.openapi.editor.EditorComponent;
import jetbrains.mps.openapi.editor.cells.CellInfo;
import jetbrains.mps.openapi.editor.selection.Selection;
import jetbrains.mps.openapi.editor.selection.SelectionInfo;
import jetbrains.mps.openapi.editor.selection.SelectionStoreException;
import jetbrains.mps.smodel.ModelAccessHelper;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.mps.openapi.module.SModule;
import org.jetbrains.mps.openapi.module.SModuleReference;
import org.jetbrains.mps.openapi.module.SRepository;
import org.jetbrains.mps.openapi.persistence.PersistenceFacade;

import jetbrains.mps.classloading.ModuleClassLoader;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class SelectionInfoImpl implements SelectionInfo {
  private static final Logger LOG = Logger.getLogger(SelectionInfoImpl.class);

  private static final String CLASS_NAME_ATTRIBUTE = "className";
  private static final String MODULE_ID_ATTRIBUTE = "moduleID";
  private static final String CELL_INFO_ELEMENT_NAME = "cellInfo";
  private static final String PROPERTY_ELEMENT_NAME = "property";
  private static final String PROPERTY_NAME_ATTRIBUTE = "name";
  private static final String PROPERTY_VALUE_ATTRIBUTE = "value";


  private String mySelectionClassName;
  private String myModuleID;
  private Map<String, String> myProperties = new HashMap<>();
  private DefaultCellInfo myCellInfo = null;

  public SelectionInfoImpl(Element element) {
    mySelectionClassName = element.getAttributeValue(CLASS_NAME_ATTRIBUTE);
    myModuleID = element.getAttributeValue(MODULE_ID_ATTRIBUTE);
    for (Object childElement : element.getChildren(PROPERTY_ELEMENT_NAME)) {
      Element entry = (Element) childElement;
      String name = entry.getAttributeValue(PROPERTY_NAME_ATTRIBUTE);
      assert name != null;
      String value = entry.getAttributeValue(PROPERTY_VALUE_ATTRIBUTE);
      assert value != null;
      myProperties.put(name, value);
    }
    Element cellInfoElement = element.getChild(CELL_INFO_ELEMENT_NAME);
    if (cellInfoElement != null) {
      myCellInfo = DefaultCellInfo.loadFrom(cellInfoElement);
    }
  }

  public SelectionInfoImpl(@NotNull String selectionClassName, @Nullable SModuleReference moduleID) {
    this(selectionClassName);
    myModuleID = moduleID == null ? null : PersistenceFacade.getInstance().asString(moduleID);
  }

  public SelectionInfoImpl(@NotNull String selectionClassName) {
    mySelectionClassName = selectionClassName;
  }

  public Map<String, String> getPropertiesMap() {
    return myProperties;
  }

  public void setCellInfo(CellInfo cellInfo) throws SelectionStoreException {
    if (!(cellInfo instanceof DefaultCellInfo)) {
      throw new SelectionStoreException("CellInfo is different from DefaultCellInfo: " + cellInfo);
    }
    myCellInfo = (DefaultCellInfo) cellInfo;
  }

  @Override
  public Selection createSelection(EditorComponent editorComponent) {
    try {
      Class<?> selectionClass;
      if (myModuleID != null) {
        SRepository repo = editorComponent.getEditorContext().getRepository();
        // XXX I have no idea whether there's model read access when #createSelection is invoked, rather take one.
        ReloadableModule reloadableModule = new ModelAccessHelper(repo).runReadAction(() -> {
          SModule module;
          try {
            SModuleReference mr = PersistenceFacade.getInstance().createModuleReference(myModuleID);
            module = mr.resolve(repo);
          } catch (IllegalArgumentException ex) {
            // fallback, perhaps, it's an old selection, where just module name has been stored
            module = null;
          }
          if (module == null) {
            LOG.error("Specified selection class module was not found by ID: " + myModuleID);
            return null;
          }
          if (!(module instanceof ReloadableModule)) {
            LOG.error(String.format("Module %s of specified selection class (%s) can not load classes", myModuleID, mySelectionClassName));
            return null;
          }
          return (ReloadableModule) module;
        });
        if (reloadableModule == null) {
          return null;
        }
        // I know it's odd to access module outside ot model read (although the module is likely deployed and shall not
        // get disposed unexpectedly). Just don't want to refactor the a lot (exception handling for both if/else cases).
        // FIXME this usage of ReloadableModule has to be replaced with a mechanism like extension point or
        //       EditorAspectDescriptor contribution.
        selectionClass = reloadableModule.getClass(mySelectionClassName);
      } else {
        selectionClass = ClassLoader.getSystemClassLoader().loadClass(mySelectionClassName);
      }
      if (!Selection.class.isAssignableFrom(selectionClass)) {
        LOG.error("Serialized selection class: " + mySelectionClassName + " is not a subclass of " + Selection.class.getName());
        return null;
      }
      try {
        return ModuleClassLoader.newInstance(selectionClass, Selection.class,
            new Class<?>[] {EditorComponent.class, Map.class, CellInfo.class}, editorComponent, myProperties, myCellInfo);
      } catch (Error constructorFailure) {
        // JVM reflection wrapped constructor Errors in InvocationTargetException.
        LOG.error(constructorFailure);
        return null;
      }
    } catch (SelectionRestoreException e) {
      // Stale model/cell selections are expected and must not become error messages.
      return null;
    } catch (Exception e) {
      LOG.error(e);
      return null;
    }
  }

  public void persistToXML(Element element) {
    element.setAttribute(CLASS_NAME_ATTRIBUTE, mySelectionClassName);
    if (myModuleID != null) {
      element.setAttribute(MODULE_ID_ATTRIBUTE, myModuleID);
    }
    for (Entry<String, String> propertyEntry : myProperties.entrySet()) {
      Element propertyElement = new Element(PROPERTY_ELEMENT_NAME);
      propertyElement.setAttribute(PROPERTY_NAME_ATTRIBUTE, propertyEntry.getKey());
      propertyElement.setAttribute(PROPERTY_VALUE_ATTRIBUTE, propertyEntry.getValue());
      element.addContent(propertyElement);
    }
    if (myCellInfo != null) {
      Element cellInfoElement = new Element(CELL_INFO_ELEMENT_NAME);
      myCellInfo.saveTo(cellInfoElement);
      element.addContent(cellInfoElement);
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    SelectionInfoImpl that = (SelectionInfoImpl) o;

    if (!mySelectionClassName.equals(that.mySelectionClassName)) {
      return false;
    }
    if (myCellInfo != null ? !myCellInfo.equals(that.myCellInfo) : that.myCellInfo != null) {
      return false;
    }
    return myProperties.equals(that.myProperties);
  }

  @Override
  public int hashCode() {
    int result = mySelectionClassName.hashCode();
    result = 31 * result + myProperties.hashCode();
    result = 31 * result + (myCellInfo != null ? myCellInfo.hashCode() : 0);
    return result;
  }

  public static class Util {
    public static int getIntProperty(Map<String, String> properties, String propertyName) throws SelectionStoreException {
      String propertyValue = properties.get(propertyName);
      if (propertyValue == null) {
        throw new SelectionStoreException("Cannot load int property - property value was not specified for propertyName = " + propertyName);
      }
      try {
        return Integer.parseInt(propertyValue);
      } catch (NumberFormatException e) {
        throw new SelectionStoreException("Unable to parse integer position value: " + propertyValue);
      }
    }

    public static boolean getBooleanProperty(Map<String, String> properties, String propertyName) throws SelectionStoreException {
      String propertyValue = properties.get(propertyName);
      if (propertyValue == null) {
        throw new SelectionStoreException("Cannot load boolean property - property value was not specified for propertyName = " + propertyName);
      }
      return Boolean.parseBoolean(propertyValue);
    }

    public static Enum getEnumProperty(Map<String, String> properties, String propertyName, Class<? extends Enum> enumClass, Enum defaultPropertyValue) throws
        SelectionStoreException {
      String propertyValue = properties.get(propertyName);
      if (propertyValue == null) {
        return defaultPropertyValue;
//        throw new SelectionStoreException("Cannot load enum property - property value was not specified for propertyName = " + propertyName);
      }
      try {
        // Enum.valueOf(Class, String) needs JVM enum reflection. The only enum
        // used by MPS selection persistence has a generated static parser.
        if (enumClass == Selection.SelectionDirection.class) {
          return Selection.SelectionDirection.valueOf(propertyValue);
        }
        return Enum.valueOf(enumClass, propertyValue);
      } catch (IllegalArgumentException ex) {
        throw new SelectionStoreException("Invalid enum literal name specified: " + propertyValue + " for enum: " + enumClass.getCanonicalName());
      }
    }
  }
}
