package mpsj2cl.icons;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.util.Base64;
import jetbrains.mps.util.IconCreationUtil;

public final class IconCreationTest {
  private IconCreationTest() {}

  public static int fixtureArgb(int x, int y) {
    int alpha = ((x + y) % 4) * 85;
    return alpha == 0 ? 0 : (alpha << 24) | ((x * 17) << 16) | ((y * 17) << 8) | ((x ^ y) * 17);
  }

  public static byte[] fixture() {
    return IconCreationUtil.drawIcon(context -> {
      require(context.width == 16 && context.height == 16, "upstream icon dimensions");
      context.g.setComposite(AlphaComposite.Src);
      for (int y = 0; y < context.height; y++) {
        for (int x = 0; x < context.width; x++) {
          context.g.setColor(new Color(fixtureArgb(x, y), true));
          context.g.fillRect(x, y, 1, 1);
        }
      }
    });
  }

  public static byte[] emptyFixture() {
    return IconCreationUtil.drawIcon(context -> {});
  }

  public static String fixtureBase64() {
    return Base64.getEncoder().encodeToString(fixture());
  }

  public static String emptyFixtureBase64() {
    return Base64.getEncoder().encodeToString(emptyFixture());
  }

  public static String run() {
    byte[] png = fixture();
    require(png.length > 32 && (png[0] & 255) == 137 && png[1] == 'P' && png[2] == 'N' && png[3] == 'G', "real PNG signature");
    IllegalArgumentException failure = new IllegalArgumentException("drawer failed");
    try {
      IconCreationUtil.drawIcon(context -> { throw failure; });
      throw new AssertionError("Expected the drawing failure");
    } catch (IllegalArgumentException expected) {
      require(expected == failure, "drawer exception identity");
    }
    return "IconCreationUtil: synchronous 16x16 PNG output and drawing error propagation passed; fixture pixels require independent decoder validation";
  }

  private static void require(boolean condition, String message) {
    if (!condition) throw new AssertionError(message);
  }
}
