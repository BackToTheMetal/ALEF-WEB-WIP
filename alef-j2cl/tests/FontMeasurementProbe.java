package alefj2cl;

import org.webawt.internal.browser.Elemental2FontMeasurementProvider;

/** Exposes the actual provider to browser-side cache and native-metric checks. */
public final class FontMeasurementProbe {
  private static Elemental2FontMeasurementProvider provider = new Elemental2FontMeasurementProvider();
  public static double[] measure(String font, String text) { return provider.measure(font, text); }
}
