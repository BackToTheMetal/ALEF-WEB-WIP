package com.intellij.openapi.util;

public class Ref<T> {
  private T value;
  public Ref() {}
  public Ref(T value) { this.value = value; }
  public static <T> Ref<T> create() { return new Ref<>(); }
  public static <T> Ref<T> create(T value) { return new Ref<>(value); }
  public T get() { return value; }
  public void set(T value) { this.value = value; }
  public boolean isNull() { return value == null; }
}
