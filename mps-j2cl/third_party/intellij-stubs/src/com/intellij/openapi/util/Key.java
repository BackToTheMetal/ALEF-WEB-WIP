package com.intellij.openapi.util;

import java.util.Objects;

/** Keys deliberately compare by identity, including keys with the same display name. */
public class Key<T> {
  private final String name;
  public Key(String name) { this.name = Objects.requireNonNull(name); }
  public static <T> Key<T> create(String name) { return new Key<>(name); }
  public T get(UserDataHolder holder) { return holder == null ? null : holder.getUserData(this); }
  public T get(UserDataHolder holder, T defaultValue) {
    T value = get(holder);
    return value == null ? defaultValue : value;
  }
  public T getRequired(UserDataHolder holder) { return Objects.requireNonNull(get(holder), name); }
  public boolean isIn(UserDataHolder holder) { return get(holder) != null; }
  public void set(UserDataHolder holder, T value) { holder.putUserData(this, value); }
  @Override public String toString() { return name; }
}
