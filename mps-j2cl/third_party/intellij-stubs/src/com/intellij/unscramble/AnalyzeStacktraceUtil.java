package com.intellij.unscramble;
import com.intellij.openapi.project.Project;
import javax.swing.Icon;
public final class AnalyzeStacktraceUtil {
  public interface ConsoleSink { void show(Project project, String title, String text); }
  private static ConsoleSink sink = (project, title, text) -> System.err.println(title + "\n" + text);
  private AnalyzeStacktraceUtil() {}
  public static void setBrowserConsoleSink(ConsoleSink value) { sink = java.util.Objects.requireNonNull(value); }
  public static void addConsole(Project project, Icon icon, String title, String text) { sink.show(project, title, text); }
}
