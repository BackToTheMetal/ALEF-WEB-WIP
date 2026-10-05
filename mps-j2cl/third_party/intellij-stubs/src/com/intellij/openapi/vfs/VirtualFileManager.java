package com.intellij.openapi.vfs;
import java.util.LinkedHashMap;
import java.util.Map;
/** Protocol routing only. A host explicitly registers each available file system. */
public final class VirtualFileManager {
  private static final VirtualFileManager INSTANCE = new VirtualFileManager();
  private final Map<String, VirtualFileSystem> systems = new LinkedHashMap<>();
  public static VirtualFileManager getInstance() { return INSTANCE; }
  public void registerBrowserFileSystem(VirtualFileSystem system) {
    VirtualFileSystem previous = systems.putIfAbsent(system.getProtocol(), system);
    if (previous != null && previous != system) throw new IllegalStateException("Protocol already registered: " + system.getProtocol());
  }
  public void unregisterBrowserFileSystem(VirtualFileSystem system) { if (systems.get(system.getProtocol()) == system) systems.remove(system.getProtocol()); }
  public VirtualFileSystem getFileSystem(String protocol) { return systems.get(protocol); }
  public VirtualFile findFileByUrl(String url) {
    VirtualFileSystem system = getFileSystem(extractProtocol(url));
    return system == null ? null : system.findFileByPath(extractPath(url));
  }
  public VirtualFile refreshAndFindFileByUrl(String url) {
    VirtualFileSystem system = getFileSystem(extractProtocol(url));
    return system == null ? null : system.refreshAndFindFileByPath(extractPath(url));
  }
  public static String constructUrl(String protocol, String path) { return protocol + "://" + path; }
  public static String extractProtocol(String url) { int index = url.indexOf("://"); return index < 0 ? null : url.substring(0, index); }
  public static String extractPath(String url) { int index = url.indexOf("://"); return index < 0 ? url : url.substring(index + 3); }
}
