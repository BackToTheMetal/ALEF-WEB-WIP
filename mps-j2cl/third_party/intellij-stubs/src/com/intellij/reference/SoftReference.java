package com.intellij.reference;
public class SoftReference<T> extends java.lang.ref.SoftReference<T> {
  public SoftReference(T value) { super(value); }
  public static <T> T dereference(java.lang.ref.Reference<T> reference) { return reference == null ? null : reference.get(); }
}
