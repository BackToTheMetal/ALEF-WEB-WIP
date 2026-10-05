/*
 * Copyright 2003-2016 JetBrains s.r.o.
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

package jetbrains.mps.persistence;

import org.jdom.Attribute;
import org.jdom.Element;
import org.jetbrains.mps.openapi.persistence.Memento;

/**
 * evgeny, 11/19/12
 */
public class MementoUtil {

  public static void readMemento(Memento memento, Element element) {
    for (Attribute attr : element.getAttributes()) {
      String name = attr.getName();
      memento.put(name, attr.getValue());
    }
    for (Element elem : element.getChildren()) {
      Memento child = memento.createChild(elem.getName());
      readMemento(child, elem);
    }
  }

  public static void writeMemento(Memento memento, Element element) {
    for (String key : memento.getKeys()) {
      element.setAttribute(key, memento.get(key));
    }
    for (Memento childMemento : memento.getChildren()) {
      Element child = new Element(childMemento.getType());
      writeMemento(childMemento, child);
      element.addContent(child);
    }
  }
}
