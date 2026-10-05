/* Copyright 2003-2015 JetBrains s.r.o. Licensed under the Apache License, Version 2.0. */
package jetbrains.mps.nodeEditor.memory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

/** JVM object-layout instrumentation is unavailable in a JavaScript runtime. */
public class MemoryAnalyzer {
  public boolean isValid() { return false; }
  public long getSize() { throw unavailable(); }
  public void appendObject(Object object) { throw unavailable(); }
  public void appendCollection(Collection collection) { throw unavailable(); }
  public void appendLinkedList(LinkedList list) { throw unavailable(); }
  public void appendArrayList(ArrayList list) { throw unavailable(); }
  public void appendFirstNonPrimitiveField(Object object) { throw unavailable(); }
  public void appendField(Object object, String fieldName) { throw unavailable(); }
  private UnsupportedOperationException unavailable() {
    return new UnsupportedOperationException("JVM object-size instrumentation is not available in the browser");
  }
}
