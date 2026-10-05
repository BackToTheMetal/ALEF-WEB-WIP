/*
 * Copyright 2003-2022 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jetbrains.mps.util;

import jetbrains.mps.logging.Logger;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.mps.openapi.util.Consumer;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;

public class IconCreationUtil {
  protected static Logger LOG = Logger.getLogger(IconCreationUtil.class);

  private static final int DEFAULT_ICON_SIZE = 16;

  @Nullable
  public static byte[] drawIcon(Consumer<DrawContext> drawer) {
    BufferedImage image = new BufferedImage(DEFAULT_ICON_SIZE, DEFAULT_ICON_SIZE, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = image.createGraphics();
    RenderingHints rh = new RenderingHints(new HashMap<>());
    rh.put(RenderingHints.KEY_ALPHA_INTERPOLATION,RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
    rh.put(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
    rh.put(RenderingHints.KEY_COLOR_RENDERING,RenderingHints.VALUE_COLOR_RENDER_QUALITY);
    rh.put(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BICUBIC);
    rh.put(RenderingHints.KEY_FRACTIONALMETRICS,RenderingHints.VALUE_FRACTIONALMETRICS_ON);
    rh.put(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY);
    rh.put(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    try {
      g.setRenderingHints(rh);
      drawer.consume(new DrawContext(g, DEFAULT_ICON_SIZE, DEFAULT_ICON_SIZE));
    } finally {
      g.dispose();
    }
    return encodePng(image);
  }

  /** Synchronous, lossless RGBA PNG from the existing buffered raster; no ImageIO or DOM encoding. */
  private static byte[] encodePng(BufferedImage image) {
    int width = image.getWidth();
    int height = image.getHeight();
    byte[] scanlines = new byte[height * (1 + 4 * width)];
    int position = 0;
    for (int y = 0; y < height; y++) {
      scanlines[position++] = 0; // PNG filter type None.
      for (int x = 0; x < width; x++) {
        int argb = image.getRGB(x, y);
        scanlines[position++] = (byte) (argb >>> 16);
        scanlines[position++] = (byte) (argb >>> 8);
        scanlines[position++] = (byte) argb;
        scanlines[position++] = (byte) (argb >>> 24);
      }
    }

    ByteArrayOutputStream png = new ByteArrayOutputStream();
    byte[] signature = {(byte) 137, 80, 78, 71, 13, 10, 26, 10};
    png.write(signature, 0, signature.length);
    ByteArrayOutputStream header = new ByteArrayOutputStream();
    writeInt(header, width);
    writeInt(header, height);
    header.write(8); // Eight bits per channel.
    header.write(6); // Truecolor with alpha.
    header.write(0); // DEFLATE compression method.
    header.write(0); // PNG filter method.
    header.write(0); // No interlacing.
    writeChunk(png, "IHDR", header.toByteArray());
    writeChunk(png, "IDAT", storedZlib(scanlines));
    writeChunk(png, "IEND", new byte[0]);
    return png.toByteArray();
  }

  /** Valid zlib/DEFLATE stream with stored blocks; small icons need no compression engine. */
  private static byte[] storedZlib(byte[] bytes) {
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    output.write(0x78); // DEFLATE, 32 KiB window.
    output.write(0x01); // No dictionary, fastest strategy, valid modulo-31 header check.
    for (int offset = 0; offset < bytes.length;) {
      int length = Math.min(65535, bytes.length - offset);
      output.write(offset + length == bytes.length ? 1 : 0); // BFINAL and stored BTYPE, then byte padding.
      output.write(length);
      output.write(length >>> 8);
      output.write(~length);
      output.write((~length) >>> 8);
      output.write(bytes, offset, length);
      offset += length;
    }
    int a = 1;
    int b = 0;
    for (byte value : bytes) {
      a = (a + (value & 0xff)) % 65521;
      b = (b + a) % 65521;
    }
    writeInt(output, (b << 16) | a);
    return output.toByteArray();
  }

  private static void writeChunk(ByteArrayOutputStream output, String type, byte[] bytes) {
    writeInt(output, bytes.length);
    int crc = 0xffffffff;
    for (int index = 0; index < type.length(); index++) {
      int value = type.charAt(index);
      output.write(value);
      crc = updateCrc(crc, value);
    }
    output.write(bytes, 0, bytes.length);
    for (byte value : bytes) crc = updateCrc(crc, value & 0xff);
    writeInt(output, ~crc);
  }

  private static int updateCrc(int crc, int value) {
    crc ^= value;
    for (int bit = 0; bit < 8; bit++) {
      crc = (crc >>> 1) ^ ((crc & 1) == 0 ? 0 : 0xedb88320);
    }
    return crc;
  }

  private static void writeInt(ByteArrayOutputStream output, int value) {
    output.write(value >>> 24);
    output.write(value >>> 16);
    output.write(value >>> 8);
    output.write(value);
  }

  public static class DrawContext {
    public Graphics2D g;
    public int width;
    public int height;

    public DrawContext(Graphics2D g, int width, int height) {
      this.g = g;
      this.width = width;
      this.height = height;
    }
  }
}
