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

import static javaemul.internal.InternalPreconditions.checkArgument;

import java.util.Collections;
import java.util.SortedMap;
import java.util.TreeMap;
import javaemul.internal.EmulatedCharset;

/** A minimal emulation of {@link Charset}. */
public abstract class Charset implements Comparable<Charset> {

  private static final class AvailableCharsets {
    private static final SortedMap<String, Charset> CHARSETS;

    static {
      SortedMap<String, Charset> map = new TreeMap<>();
      map.put(EmulatedCharset.ISO_8859_1.name(), EmulatedCharset.ISO_8859_1);
      map.put(EmulatedCharset.ISO_LATIN_1.name(), EmulatedCharset.ISO_LATIN_1);
      map.put(EmulatedCharset.UTF_8.name(), EmulatedCharset.UTF_8);
      CHARSETS = Collections.unmodifiableSortedMap(map);
    }
  }

  public static SortedMap<String, Charset> availableCharsets() {
    return AvailableCharsets.CHARSETS;
  }

  public static Charset defaultCharset() {
    return EmulatedCharset.UTF_8;
  }

  public static Charset forName(String charsetName) {
    checkArgument(charsetName != null, "Null charset name");

    charsetName = charsetName.toUpperCase();
    if (EmulatedCharset.ISO_8859_1.name().equals(charsetName)) {
      return EmulatedCharset.ISO_8859_1;
    } else if (EmulatedCharset.ISO_LATIN_1.name().equals(charsetName)) {
      return EmulatedCharset.ISO_LATIN_1;
    } else if (EmulatedCharset.UTF_8.name().equals(charsetName)) {
      return EmulatedCharset.UTF_8;
    }

    throw new UnsupportedCharsetException(charsetName);
  }

  private final String name;
  private final java.util.Set<String> aliases;

  protected Charset(String name, String[] aliasesIgnored) {
    this.name = name;
    this.aliases = aliasesIgnored == null ? java.util.Collections.emptySet() : java.util.Collections.unmodifiableSet(new java.util.HashSet<>(java.util.Arrays.asList(aliasesIgnored)));
  }

  public final String name() {
    return name;
  }

  @Override
  public final int compareTo(Charset that) {
    return this.name.compareToIgnoreCase(that.name);
  }

  @Override
  public final int hashCode() {
    return name.hashCode();
  }

  @Override
  public final boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof Charset)) {
      return false;
    }
    Charset that = (Charset) o;
    return this.name.equals(that.name);
  }

  @Override
  public final String toString() {
    return name;
  }

  // Additional source compatibility declarations; operations are unsupported.
  public final java.util.Set<java.lang.String> aliases() { return aliases; }
  public boolean canEncode() { throw javaemul.internal.nio.UnsupportedNio.operation("java.nio.charset.Charset.canEncode()"); }
  public abstract boolean contains(java.nio.charset.Charset p0);
  public final java.nio.CharBuffer decode(java.nio.ByteBuffer p0) { throw javaemul.internal.nio.UnsupportedNio.operation("java.nio.charset.Charset.decode(java.nio.ByteBuffer)"); }
  public java.lang.String displayName() { return name(); }
  public java.lang.String displayName(java.util.Locale p0) { return name(); }
  public final java.nio.ByteBuffer encode(java.lang.String p0) { throw javaemul.internal.nio.UnsupportedNio.operation("java.nio.charset.Charset.encode(java.lang.String)"); }
  public final java.nio.ByteBuffer encode(java.nio.CharBuffer p0) { throw javaemul.internal.nio.UnsupportedNio.operation("java.nio.charset.Charset.encode(java.nio.CharBuffer)"); }
  public static java.nio.charset.Charset forName(java.lang.String p0, java.nio.charset.Charset p1) { try { return forName(p0); } catch (UnsupportedCharsetException e) { return p1; } }
  public final boolean isRegistered() { return !name().startsWith("X-") && !name().startsWith("x-"); }
  public static boolean isSupported(java.lang.String p0) { try { forName(p0); return true; } catch (UnsupportedCharsetException e) { return false; } }
  public abstract java.nio.charset.CharsetDecoder newDecoder();
  public abstract java.nio.charset.CharsetEncoder newEncoder();
}
