package com.intellij.openapi.progress.util;
import com.intellij.openapi.progress.ProcessCanceledException;
import com.intellij.openapi.progress.ProgressIndicator;
import java.util.ArrayList;
import java.util.List;
public class ProgressIndicatorBase implements ProgressIndicator {
  private boolean running, canceled, indeterminate = true;
  private String text = "", text2 = "";
  private double fraction;
  private final List<State> states = new ArrayList<>();
  public ProgressIndicatorBase() {}
  public ProgressIndicatorBase(boolean reusable) {}
  @Override public void start() { running = true; canceled = false; }
  @Override public void stop() { running = false; }
  @Override public boolean isRunning() { return running; }
  @Override public void cancel() { canceled = true; }
  @Override public boolean isCanceled() { return canceled; }
  @Override public void checkCanceled() { if (canceled) throw new ProcessCanceledException(); }
  @Override public void setText(String value) { text = value; }
  @Override public String getText() { return text; }
  @Override public void setText2(String value) { text2 = value; }
  @Override public String getText2() { return text2; }
  @Override public double getFraction() { return fraction; }
  @Override public void setFraction(double value) { fraction = value; }
  @Override public boolean isIndeterminate() { return indeterminate; }
  @Override public void setIndeterminate(boolean value) { indeterminate = value; }
  @Override public void pushState() { states.add(new State(text, text2, fraction, indeterminate)); }
  @Override public void popState() {
    if (states.isEmpty()) throw new IllegalStateException("No progress state to restore");
    State state = states.remove(states.size() - 1);
    text = state.text; text2 = state.text2; fraction = state.fraction; indeterminate = state.indeterminate;
  }
  private static final class State {
    final String text, text2; final double fraction; final boolean indeterminate;
    State(String a, String b, double c, boolean d) { text = a; text2 = b; fraction = c; indeterminate = d; }
  }
}
