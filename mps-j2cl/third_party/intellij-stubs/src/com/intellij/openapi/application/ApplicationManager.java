package com.intellij.openapi.application;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.util.Disposer;
public class ApplicationManager {
  protected static Application ourApplication;
  public static Application getApplication() { return ourApplication; }
  public static void setApplication(Application application) { ourApplication = application; }
  public static void setApplication(Application application, Disposable parent) {
    Application previous = ourApplication;
    ourApplication = application;
    Disposer.register(parent, () -> { if (ourApplication == application) ourApplication = previous; });
  }
  public static void registerCleaner(Runnable cleaner) {
    if (ourApplication == null) throw new IllegalStateException("No browser application installed");
    Disposer.register(ourApplication, cleaner::run);
  }
}
