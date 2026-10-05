package com.intellij.featureStatistics;

/** IDE usage statistics are not collected by the browser host. */
public final class FeatureUsageTracker {
  private static final FeatureUsageTracker INSTANCE = new FeatureUsageTracker();
  private FeatureUsageTracker() {}
  public static FeatureUsageTracker getInstance() { return INSTANCE; }
  public void triggerFeatureUsed(String featureId) { /* No browser telemetry collector. */ }
}
