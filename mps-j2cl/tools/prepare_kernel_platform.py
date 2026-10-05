#!/usr/bin/env python3
"""Generate explicit browser boundaries for host installation/SDK/archive operations."""
import argparse
import hashlib
import json
from pathlib import Path
import re

ROOT = Path(__file__).resolve().parents[1]
BASE = 'core/kernel/source/jetbrains/mps/'

def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--check', action='store_true')
    args = parser.parse_args()
    originals = {name: (ROOT / 'mps' / (BASE + name)).read_text(encoding='utf-8')
                 for name in ['reloading/SDKDiscovery.java', 'util/UnzipUtil.java', 'util/PathManager.java']}
    sdk = originals['reloading/SDKDiscovery.java'].split('package ', 1)[0] + '''package jetbrains.mps.reloading;

import java.io.File;
import java.util.List;
import jetbrains.mps.vfs.QualifiedPath;
import org.jetbrains.annotations.NotNull;

/** The browser runtime has no installed JVM or host JDK archives to discover. */
public class SDKDiscovery {
  public static List<QualifiedPath> discover() { throw unavailable(); }
  public static boolean isModularRuntime(@NotNull File homePath) { throw unavailable(); }
  public static List<File> getJdkClassesRoots(@NotNull File home, boolean isJre) { throw unavailable(); }
  private static UnsupportedOperationException unavailable() {
    return new UnsupportedOperationException("Host JDK discovery is unavailable in the browser; J2CL links its JRE at build time");
  }
}
'''
    unzip = originals['util/UnzipUtil.java'].split('package ', 1)[0] + '''package jetbrains.mps.util;

import java.io.File;
import java.io.IOException;

/** Host file extraction requires an archive/filesystem backend. */
public class UnzipUtil {
  public static void unzip(File zipFile, File outputDir) throws IOException {
    throw new UnsupportedOperationException("Extracting a ZIP into host files is unavailable in the browser");
  }
}
'''
    path_manager, count = re.subn(r'  public static String getHomePath\(\) \{[\s\S]*?\n  }', '''  public static String getHomePath() {
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
  }''', originals['util/PathManager.java'], count=1)
    if count != 1:
        raise SystemExit('PathManager.getHomePath preimage changed')
    path_manager, count = re.subn(r'  public static String getLauncherClassPathEntry\(\) \{[\s\S]*?\n  }', '''  public static String getLauncherClassPathEntry() {
    // Browser class resources have no JVM launcher classpath directory.
    return null;
  }''', path_manager, count=1)
    if count != 1:
        raise SystemExit('PathManager.getLauncherClassPathEntry preimage changed')
    replacements = {'reloading/SDKDiscovery.java': sdk, 'util/UnzipUtil.java': unzip, 'util/PathManager.java': path_manager}
    manifest = []
    for name, replacement in replacements.items():
        path = ROOT / 'mps-web' / (BASE + name)
        manifest.append({'path': BASE + name, 'upstream_sha256': hashlib.sha256(originals[name].encode()).hexdigest(),
                         'replacement_sha256': hashlib.sha256(replacement.encode()).hexdigest()})
        if args.check:
            if not path.exists() or path.read_text(encoding='utf-8') != replacement:
                raise SystemExit('Stale platform replacement: ' + str(path))
        else:
            path.parent.mkdir(parents=True, exist_ok=True)
            path.write_text(replacement, encoding='utf-8', newline='\n')
    manifest_path = ROOT / 'compatibility/kernel-platform.json'
    expected = json.dumps(manifest, indent=2) + '\n'
    if args.check:
        if manifest_path.read_text(encoding='utf-8') != expected:
            raise SystemExit('Stale kernel-platform provenance')
    else:
        manifest_path.write_text(expected, encoding='utf-8', newline='\n')
    print('Kernel platform boundaries: 3 reproducible replacements')

if __name__ == '__main__':
    main()
