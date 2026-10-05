package com.intellij.util.ui.update;
import com.intellij.openapi.Disposable;
import com.intellij.util.Alarm;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.JComponent;
public class MergingUpdateQueue implements Disposable {
  private final Map<Object, Update> updates = new LinkedHashMap<>();
  private final Alarm alarm;
  private final int delay;
  private boolean active, restart, disposed;
  private final Runnable flush = this::flush;
  public MergingUpdateQueue(String name, int delay, boolean active, JComponent component, Disposable parent) { this(name, delay, active, component, parent, null, true); }
  public MergingUpdateQueue(String name, int delay, boolean active, JComponent component, Disposable parent, JComponent modality, boolean swing) {
    this.delay = delay; this.active = active;
    alarm = new Alarm(swing ? Alarm.ThreadToUse.SWING_THREAD : Alarm.ThreadToUse.POOLED_THREAD);
    if (parent != null) com.intellij.openapi.util.Disposer.register(parent, this);
  }
  public void queue(Update update) {
    if (disposed) { update.setRejected(); return; }
    for (Update old : new ArrayList<>(updates.values())) {
      if (old.canEat(update)) { update.setRejected(); return; }
      if (update.canEat(old)) { updates.remove(old.getIdentity()); old.setRejected(); }
    }
    Update old = updates.put(update.getIdentity(), update);
    if (old != null && old != update) old.setRejected();
    if (active && (restart || alarm.isEmpty())) { alarm.cancelRequest(flush); alarm.addRequest(flush, delay); }
  }
  public void setRestartTimerOnAdd(boolean value) { restart = value; }
  public void activate() { active = true; if (!updates.isEmpty() && alarm.isEmpty()) alarm.addRequest(flush, delay); }
  public void deactivate() { active = false; alarm.cancelAllRequests(); }
  public void flush() {
    if (disposed) return;
    alarm.cancelRequest(flush);
    ArrayList<Update> snapshot = new ArrayList<>(updates.values()); updates.clear();
    for (Update update : snapshot) {
      if (update.isExpired() || update.isDisposed()) update.setRejected();
      else { update.run(); update.setProcessed(); }
    }
  }
  public void cancelAllUpdates() { alarm.cancelAllRequests(); for (Update update : updates.values()) update.setRejected(); updates.clear(); }
  @Override public void dispose() { disposed = true; cancelAllUpdates(); alarm.dispose(); }
}
