/*
 * Copyright 2015 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package java.nio.charset;

import javaemul.internal.EmulatedCharset;

/** Constant definitions for the standard Charsets. */
public final class StandardCharsets {
  public static final Charset ISO_8859_1 = EmulatedCharset.ISO_8859_1;
  public static final Charset UTF_8 = EmulatedCharset.UTF_8;

  private StandardCharsets() {
    // Hides the constructor.
  }

  // Additional source compatibility declarations; operations are unsupported.
  public static final Charset US_ASCII = new MetadataCharset("US-ASCII");
  public static final Charset UTF_16 = new MetadataCharset("UTF-16");
  public static final Charset UTF_16BE = new MetadataCharset("UTF-16BE");
  public static final Charset UTF_16LE = new MetadataCharset("UTF-16LE");
  private static final class MetadataCharset extends EmulatedCharset {
    MetadataCharset(String name) { super(name); }
    public boolean contains(Charset other) { throw javaemul.internal.nio.UnsupportedNio.operation("java.nio.charset.Charset.contains(Charset)"); }
    public CharsetDecoder newDecoder() { throw javaemul.internal.nio.UnsupportedNio.operation("java.nio.charset.Charset.newDecoder()"); }
    public CharsetEncoder newEncoder() { throw javaemul.internal.nio.UnsupportedNio.operation("java.nio.charset.Charset.newEncoder()"); }
    public byte[] getBytes(String text, boolean invalid) { throw javaemul.internal.nio.UnsupportedNio.operation("java.nio.charset.StandardCharsets encoding " + name()); }
    public byte[] getBytes(char[] text, int offset, int count, boolean invalid) { throw javaemul.internal.nio.UnsupportedNio.operation("java.nio.charset.StandardCharsets encoding " + name()); }
    public char[] decodeString(byte[] bytes, int offset, int length, boolean invalid) { throw javaemul.internal.nio.UnsupportedNio.operation("java.nio.charset.StandardCharsets decoding " + name()); }
  }

}
