package com.intellij.openapi.extensions;
import java.util.HashMap;
import java.util.Map;
public final class PluginId {
  private static final Map<String, PluginId> IDS = new HashMap<>();
  private final String id;
  private PluginId(String id) { this.id = id; }
  public static PluginId getId(String id) { return IDS.computeIfAbsent(id, PluginId::new); }
  public String getIdString() { return id; }
  @Override public String toString() { return id; }
}
