package org.trovej2cl;
import jsinterop.annotations.JsMethod;
public final class BrowserEntry {
  @JsMethod
  public static String run(int seed) { return ContractSuite.run(seed); }
  @JsMethod
  public static String regressions() { return RegressionSuite.run(); }
}
