package com.intellij.util.ui.update;
public abstract class Update implements Runnable {
  private final Object identity;
  private boolean rejected, processed;
  public Update(Object identity) { this.identity = identity; }
  public Object getIdentity() { return identity; }
  public boolean canEat(Update other) { return false; }
  public boolean isExpired() { return false; }
  public boolean isDisposed() { return false; }
  public void setRejected() { rejected = true; }
  public boolean isRejected() { return rejected; }
  public void setProcessed() { processed = true; }
  public boolean wasProcessed() { return processed; }
}
