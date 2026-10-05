/*
 * Copyright 2000-2025 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */
package jetbrains.mps.util;

import org.jetbrains.mps.annotations.Internal;
import org.jetbrains.mps.annotations.Singleton;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Responsible for different predefined paths in the distribution layout
 *
 * IMPORTANT: this class is not for MPS startup, rather to figure out relevant values when there's MPS instance running.
 */
@Singleton
public final class PathManager {
  private static final String FILE_PROTO = "file";
  private static final String JAR_PROTO = "jar";

  private static final String LAUNCHER_CLASS = "jetbrains/mps/Launcher.class";

  private static String ourHomePath;

  private PathManager() {
  }

  /**
   * The thing is that we have two main #getHomePath implementations: here and in IDEA's PathManager#getHomePath.
   * These almost always should return the same value, however the method here answers to the question where the MPS classes are located,
   * while the IDEA's method answers where the IDEA classes are located.
   * Also this paths are configurable from the outside by the properties.
   * In MPS IDE we obviously have these two pointing to the same location, however
   * in MPS IDEA plugin the one below point to the root of the mps-core plugin, while the IDEA's method returns
   * the location of the IDEA distribution.
   * @see #getPlatformLibPath()
   *
   * @return the MPS home path
   */
  public static String getHomePath() {
    if (ourHomePath == null) {
      String configured = System.getProperty("mps.home.path", null);
      if (configured == null || configured.isEmpty()) {
        throw new IllegalStateException("Configure the browser application's logical MPS installation root with PathManager.configureHomePath");
      }
      ourHomePath = configured;
    }
    return ourHomePath;
  }

  public static void configureHomePath(String path) {
    if (path == null || path.isEmpty()) throw new IllegalArgumentException("Empty MPS home path");
    if (ourHomePath != null && !ourHomePath.equals(path)) throw new IllegalStateException("MPS home path already configured");
    ourHomePath = path;
  }

  /**
   * Defines whether we are starting from sources not from distribution
   */
  @Internal
  public static boolean isFromSources() {
    final URL launcherURL = ClassLoader.getSystemResource(LAUNCHER_CLASS);
    return launcherURL != null && launcherURL.getProtocol().equals(FILE_PROTO);
  }

  /**
   * Returns the classpath entry corresponding to {@code jetbrains.mps.Launcher} class used to bootstrap MPS.
   * Only makes sense if {@link PathManager#isFromSources()} returns true.
   */
  @Internal
  public static String getLauncherClassPathEntry() {
    // Browser class resources have no JVM launcher classpath directory.
    return null;
  }

  public static String getLibExtPath() {
    return getLibPath() + File.separator + "ext";
  }

  /**
   * @return <MPS home>/lib location, where IDEA platform jars reside. Is the same as {@link #getLibPath()}
   */
  public static String getPlatformLibPath() {
    return getLibPath();
  }

  public static Collection<String> getBootstrapPaths() {
    Collection<String> paths = new ArrayList<>(4);
    if (new File(getCorePath()).exists()) {
      paths.add(getCorePath());
    }
    if (new File(getEditorPath()).exists()) {
      paths.add(getEditorPath());
    }
    return paths;
  }

  /**
   * @return <MPS home>/lib location, where mps own jars reside. Now is the same as {@link #getPlatformLibPath()}
   */
  public static String getLibPath() {
    // Given getIdeaPath() + getHomePath(), I assume we face few scenarios with location for MPS libraries:
    // I) "Big" MPS aka MPS as IDE
    //    there's one <MPS Installation>/lib folder to host both IDEA and MPS libraries
    // II) MPS as IDEA plugin -- NO LONGER ACTUAL
    //    there's <IDEA installation>/lib for IDEA jars
    //    <mps-core plugin>/lib with MPS jars
    // III) MPS started from sources
    //    there's <checkout dir>/lib with IDEA jars
    //    there's lib/ with MPS jars (IDEA project artifacts)
    //    getLibPath() == getPlatformLibPath().
    return getHomePath() + File.separator + "lib";
  }

  public static String getLanguagesPath() {
    return getHomePath() + File.separator + "languages";
  }

  public static String getWorkbenchPath() {
    return getHomePath() + File.separator + "workbench";
  }

  public static String getCorePath() {
    return getHomePath() + File.separator + "core";
  }

  public static String getEditorPath() {
    return getHomePath() + File.separator + "editor";
  }

  public static String getPreInstalledPluginsPath() {
    return getHomePath() + File.separator + "plugins";
  }

  public static String getUserDir() {
    return System.getProperty("user.dir");
  }
}
