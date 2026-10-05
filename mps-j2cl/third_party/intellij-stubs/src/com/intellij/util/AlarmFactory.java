package com.intellij.util;
import com.intellij.openapi.Disposable;
public final class AlarmFactory {
  private static final AlarmFactory INSTANCE = new AlarmFactory();
  public static AlarmFactory getInstance() { return INSTANCE; }
  public Alarm create(Alarm.ThreadToUse thread) { return new Alarm(thread); }
  public Alarm create(Alarm.ThreadToUse thread, Disposable parent) { return new Alarm(thread, parent); }
}
