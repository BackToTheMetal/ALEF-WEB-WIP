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
package jetbrains.mps.persistence;

import org.jetbrains.mps.openapi.persistence.Memento;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * evgeny, 11/16/12
 */
public class MementoImpl implements Memento {
  private String type;
  private Map<String, String> values;
  private Map<String, List<Memento>> children;
  private String text;

  public MementoImpl() {
  }

  private MementoImpl(String type) {
    this.type = type;
  }

  @Override
  public String getType() {
    return type;
  }

  @Override
  public String get(String key) {
    return values == null ? null : values.get(key);
  }

  @Override
  public void put(String key, String value) {
    if (value == null) {
      if (values != null) {
        values.remove(key);
      }
      return;
    }
    if (key == null) {
      throw new IllegalArgumentException("Null key in memento doesn't make sense");
    }
    if (values == null) {
      values = new LinkedHashMap<>();
    }
    values.put(key, value);
  }

  @Override
  public Iterable<String> getKeys() {
    return values == null ? Collections.emptyList() : Collections.unmodifiableCollection(values.keySet());
  }

  @Override
  public String getText() {
    return text;
  }

  @Override
  public void setText(String text) {
    this.text = text;
  }

  @Override
  public Iterable<Memento> getChildren(String type) {
    if (children == null) return Collections.emptySet();
    List<Memento> mementos = children.get(type);
    if (mementos == null) return Collections.emptySet();
    return Collections.unmodifiableList(new ArrayList<>(mementos));
  }

  @Override
  public Memento getChild(String type) {
    if (children == null) return null;
    List<Memento> mementos = children.get(type);
    if (mementos == null || mementos.isEmpty()) return null;
    return mementos.iterator().next();
  }

  @Override
  public Iterable<Memento> getChildren() {
    if (children == null) return Collections.emptyList();
    List<Memento> mementos = new ArrayList<>();
    for (List<Memento> mementoList : children.values()) {
      mementos.addAll(mementoList);
    }
    return Collections.unmodifiableList(mementos);
  }

  @Override
  public Memento createChild(String type) {
    if (children == null) {
      children = new LinkedHashMap<>();
    }
    if (!children.containsKey(type)) {
      children.put(type, new ArrayList<>());
    }
    Memento m = new MementoImpl(type);
    children.get(type).add(m);
    return m;
  }

  @Override
  public void clearChildren(String type) {
    children.remove(type);
  }

  @Override
  public Memento copy() {
    MementoImpl m = new MementoImpl(type);
    if (values != null) {
      for (Map.Entry<String, String> entry : values.entrySet()) {
        m.put(entry.getKey(), entry.getValue());
      }
    }
    if (children != null) {
      m.children = new LinkedHashMap<>();
      for (Map.Entry<String, List<Memento>> entry : children.entrySet()) {
        List<Memento> copy = new ArrayList<>();
        for (Memento memento : entry.getValue()) {
          copy.add(memento.copy());
        }
        m.children.put(entry.getKey(), copy);
      }
    }
    if (text != null) {
      m.setText(text);
    }
    return m;
  }

  @Override
  public boolean equals(Object obj) {
    if(!(obj instanceof MementoImpl))
      return false;

    MementoImpl memento = (MementoImpl) obj;

    boolean equals = true;
    equals &= type == null ? memento.type == null : type.equals(memento.type);
    equals &= values == null ? memento.values == null : values.equals(memento.values);
    equals &= children == null ? memento.children == null : children.equals(memento.children);
    equals &= text == null ? memento.text == null : text.equals(memento.text);

    return equals;
  }

  @Override
  public int hashCode() {
    int hash = type != null ? type.hashCode() : 1;
    hash = hash * 17 + (values != null ? values.hashCode() : 1);
    hash = hash * 23 + (children != null ? children.hashCode() : 1);
    return hash * 31 + (text != null ? text.hashCode() : 1);
  }
}
