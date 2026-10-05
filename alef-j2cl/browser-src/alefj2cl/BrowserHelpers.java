package alefj2cl;

import java.math.BigInteger;

public final class BrowserHelpers {
  private BrowserHelpers() {}
  /** XML 1.0 escaping, including invalid code point removal and C1 entities. */
  public static String escapeXml10(String text) {
    if (text == null) return null;
    StringBuilder out = new StringBuilder();
    for (int i = 0; i < text.length();) {
      int cp = text.codePointAt(i);
      i += Character.charCount(cp);
      if (cp != 9 && cp != 10 && cp != 13 && (cp < 32 || cp >= 0xd800 && cp <= 0xdfff || cp == 0xfffe || cp == 0xffff)) continue;
      switch (cp) {
        case '&': out.append("&amp;"); break;
        case '<': out.append("&lt;"); break;
        case '>': out.append("&gt;"); break;
        case '"': out.append("&quot;"); break;
        case '\'': out.append("&apos;"); break;
        default:
          if (cp >= 0x7f && cp <= 0x84 || cp >= 0x86 && cp <= 0x9f) out.append("&#").append(cp).append(';');
          else out.appendCodePoint(cp);
      }
    }
    return out.toString();
  }
  /** Escape HTML text while keeping Unicode characters as Unicode. */
  public static String escapeHtml(String text) {
    return text == null ? null : text.replace("&", "&amp;").replace("<", "&lt;")
        .replace(">", "&gt;").replace("\"", "&quot;");
  }
  /** URL.getPath equivalent for an already validated URL; retain percent escapes. */
  public static String urlPath(String url) {
    int start = url.indexOf(':') + 1;
    int end = url.length();
    int query = url.indexOf('?', start), fragment = url.indexOf('#', start);
    if (query >= 0) end = Math.min(end, query);
    if (fragment >= 0) end = Math.min(end, fragment);
    if (url.startsWith("//", start)) {
      start = url.indexOf('/', start + 2);
      if (start < 0 || start >= end) return "";
    }
    return url.substring(start, end);
  }
  /** Exact integer square root and nonnegative remainder. */
  public static BigInteger[] sqrtAndRemainder(BigInteger value) {
    if (value.signum() < 0) throw new ArithmeticException("Negative square root");
    if (value.signum() == 0) return new BigInteger[]{BigInteger.ZERO, BigInteger.ZERO};
    BigInteger root = BigInteger.ONE.shiftLeft((value.bitLength() + 1) / 2);
    for (;;) {
      BigInteger next = root.add(value.divide(root)).shiftRight(1);
      if (next.compareTo(root) >= 0) return new BigInteger[]{root, value.subtract(root.multiply(root))};
      root = next;
    }
  }
}
