package com.intellij.openapi.util;
/** Browser profile, independent of the build machine's OS. */
public final class SystemInfo {
  public static final boolean isMac = false, isWindows = false, isLinux = false, isUnix = false, isFileSystemCaseSensitive = true;
  public static final String OS_NAME = "browser", OS_VERSION = "", JAVA_VERSION = "J2CL";
  private SystemInfo() {}
}
