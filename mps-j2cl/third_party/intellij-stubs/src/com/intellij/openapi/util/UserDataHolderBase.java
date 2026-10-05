package com.intellij.openapi.util;

import java.util.IdentityHashMap;
import java.util.Map;

public class UserDataHolderBase implements UserDataHolder {
  private final Map<Key<?>, Object> data = new IdentityHashMap<>();
  @SuppressWarnings("unchecked")
  @Override public <T> T getUserData(Key<T> key) { return (T) data.get(key); }
  @Override public <T> void putUserData(Key<T> key, T value) {
    if (value == null) data.remove(key); else data.put(key, value);
  }
  public void clearUserData() { data.clear(); }
}
