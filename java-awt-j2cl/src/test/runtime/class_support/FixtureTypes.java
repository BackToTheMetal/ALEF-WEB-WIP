package org.webawt.classsupport.fixtures;
/** Kept in a separate Bazel target to exercise metadata across library boundaries. */
public final class FixtureTypes {
  private FixtureTypes() {}
  public interface Root {}
  public interface Left extends Root {}
  public interface Right extends Root {}
  public interface Diamond extends Left, Right {}
  public interface Action { void run(); }
  public static class Base implements Left {}
  public static class Middle extends Base implements Right {}
  public static final class Leaf extends Middle implements Diamond, Runnable {
    @Override public void run() {}
  }
  public static final class Other {}
  public enum Choice {
    PLAIN,
    SPECIAL { @Override public boolean special() { return true; } };
    public boolean special() { return false; }
  }
  public static int initializations;
  public static class Dormant implements Root {
    static { initializations++; }
    public Dormant() { throw new AssertionError("Type checks must not invoke a constructor"); }
  }
}
