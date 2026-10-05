package mpsj2cl.icons;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.util.zip.CRC32;
import javax.imageio.ImageIO;

/** JVM-only independent ImageIO decoder and standard CRC32 oracle for the portable encoder. */
public final class IconCreationJvmTest {
  public static void main(String[] arguments) throws Exception {
    verify(IconCreationTest.fixture(), false);
    verify(IconCreationTest.emptyFixture(), true);
    System.out.println(IconCreationTest.run());
    System.out.println("JVM ImageIO decoded all 512 fixture pixels exactly, including transparent/partial-alpha values; all PNG chunk CRCs passed");
    if (arguments.length == 2) {
      java.nio.file.Files.write(java.nio.file.Path.of(arguments[0]), IconCreationTest.fixture());
      java.nio.file.Files.write(java.nio.file.Path.of(arguments[1]), IconCreationTest.emptyFixture());
    }
  }

  private static void verify(byte[] png, boolean empty) throws Exception {
    BufferedImage image = ImageIO.read(new ByteArrayInputStream(png));
    if (image == null || image.getWidth() != 16 || image.getHeight() != 16) throw new AssertionError("PNG dimensions");
    for (int y = 0; y < 16; y++) {
      for (int x = 0; x < 16; x++) {
        int expected = empty ? 0 : IconCreationTest.fixtureArgb(x, y);
        if (image.getRGB(x, y) != expected) throw new AssertionError("RGBA mismatch at " + x + "," + y);
      }
    }
    try (DataInputStream input = new DataInputStream(new ByteArrayInputStream(png))) {
      if (input.readLong() != 0x89504e470d0a1a0aL) throw new AssertionError("PNG signature");
      while (input.available() > 0) {
        int length = input.readInt();
        byte[] typeAndData = new byte[4 + length];
        input.readFully(typeAndData);
        CRC32 crc = new CRC32();
        crc.update(typeAndData);
        if (input.readInt() != (int) crc.getValue()) throw new AssertionError("PNG chunk CRC");
      }
    }
  }
}
