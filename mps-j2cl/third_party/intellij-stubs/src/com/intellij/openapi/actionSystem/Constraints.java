package com.intellij.openapi.actionSystem;
public final class Constraints {
  public static final Constraints FIRST = new Constraints(Anchor.FIRST, null), LAST = new Constraints(Anchor.LAST, null);
  private final Anchor anchor; private final String relativeToActionId;
  public Constraints(Anchor anchor, String relativeToActionId) { this.anchor = anchor; this.relativeToActionId = relativeToActionId; }
  public Anchor getAnchor() { return anchor; }
  public String getRelativeToActionId() { return relativeToActionId; }
}
