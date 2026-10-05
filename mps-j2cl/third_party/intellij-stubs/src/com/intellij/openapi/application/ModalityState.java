package com.intellij.openapi.application;
import java.awt.Component;
/** Browser editor operations run in one event loop; no native modal queue is installed. */
public class ModalityState {
  public static final ModalityState NON_MODAL = new ModalityState();
  public static ModalityState nonModal() { return NON_MODAL; }
  public static ModalityState current() { return NON_MODAL; }
  public static ModalityState any() { return NON_MODAL; }
  public static ModalityState stateForComponent(Component component) { return NON_MODAL; }
  public static ModalityState defaultModalityState() { return NON_MODAL; }
  public boolean accepts(ModalityState other) { return other == NON_MODAL; }
  public final boolean dominates(ModalityState other) { return !accepts(other); }
  @Override public String toString() { return "browser non-modal"; }
}
