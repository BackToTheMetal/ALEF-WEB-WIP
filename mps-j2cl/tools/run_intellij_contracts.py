#!/usr/bin/env python3
"""Compile authored IntelliJ compatibility sources and execute JVM behavioral contracts.

Uses temporary output below .work, never IntelliJ jars, and never updates golden data.
The callback-based scheduler test runs on the JVM event thread; browser parity is
covered separately by tools/test_browser.mjs intellij_contracts.
"""
from __future__ import annotations
import argparse
import os
from pathlib import Path
import shutil
import subprocess
import tempfile

ROOT = Path(__file__).resolve().parent.parent
RUNNER = r'''public final class IntelliJJvmRunner {
  public static void main(String[] arguments) throws Exception {
    System.out.println(mpsj2cl.intellij.IntelliJContractsTest.run());
    java.util.concurrent.CountDownLatch done = new java.util.concurrent.CountDownLatch(1);
    Throwable[] failure = {null};
    javax.swing.SwingUtilities.invokeLater(() -> mpsj2cl.intellij.IntelliJContractsTest.runAsync(done::countDown, error -> { failure[0] = error; done.countDown(); }));
    if (!done.await(10, java.util.concurrent.TimeUnit.SECONDS)) throw new AssertionError("Asynchronous scheduler contracts timed out");
    if (failure[0] != null) throw new AssertionError(failure[0]);
    System.out.println("IntelliJ asynchronous scheduler contracts passed");
  }
}
'''


def executable(name, override):
    if override:
        return str(Path(override).resolve()) if Path(override).exists() else override
    located = shutil.which(name)
    if located:
        return located
    suffix = ".exe" if os.name == "nt" else ""
    java_home = os.environ.get("JAVA_HOME")
    if java_home and (Path(java_home) / "bin" / (name + suffix)).is_file():
        return str(Path(java_home) / "bin" / (name + suffix))
    if os.name == "nt":
        candidates = sorted(Path("C:/Program Files/Eclipse Adoptium").glob("jdk-*/bin/" + name + suffix))
        if candidates:
            return str(candidates[-1])
    raise SystemExit("Cannot locate " + name + "; specify --" + name)


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--java", help="java executable")
    parser.add_argument("--javac", help="javac executable (JDK 17 or newer)")
    arguments = parser.parse_args()
    java, javac = executable("java", arguments.java), executable("javac", arguments.javac)
    work = ROOT / ".work"
    work.mkdir(exist_ok=True)
    if not work.resolve().is_relative_to(ROOT.resolve()):
        raise RuntimeError("Contract output directory must remain inside the workspace")
    with tempfile.TemporaryDirectory(prefix="intellij-contracts-", dir=work) as temporary:
        output = Path(temporary).resolve()
        # Verify the concrete cleanup target before TemporaryDirectory recursively removes it.
        if not output.is_relative_to(work.resolve()) or output == work.resolve():
            raise RuntimeError("Unsafe temporary contract directory: " + str(output))
        runner = output / "IntelliJJvmRunner.java"
        runner.write_text(RUNNER, encoding="utf-8")
        sources = sorted((ROOT / "third_party/intellij-stubs/src").rglob("*.java"))
        sources += [ROOT / "tests/intellij_stubs/IntelliJContractsTest.java", runner]
        argfile = output / "javac.args"
        argfile.write_text("\n".join(['-encoding', 'UTF-8', '-d', '"' + output.as_posix() + '"'] + ['"' + source.as_posix() + '"' for source in sources]) + "\n", encoding="utf-8")
        subprocess.run([javac, "@" + str(argfile)], cwd=ROOT, check=True, timeout=120)
        subprocess.run([java, "-Djava.awt.headless=true", "-cp", str(output), "IntelliJJvmRunner"], cwd=ROOT, check=True, timeout=30)


if __name__ == "__main__":
    main()
