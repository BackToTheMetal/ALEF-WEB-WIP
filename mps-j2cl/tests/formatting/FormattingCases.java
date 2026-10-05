package mpsj2cl.formatting;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/** Inputs for goldens generated with the real JVM Formatter, shared with browser checks. */
public final class FormattingCases {
  public static final class Case {
    public final String format;
    public final Object[] arguments;
    private Case(String format, Object... arguments) {
      this.format = format;
      this.arguments = arguments;
    }
  }

  private FormattingCases() {}

  @SuppressWarnings("deprecation")
  public static List<Case> all() {
    List<Case> cases = new ArrayList<>();
    cases.add(new Case("plain Unicode: café 雪 😀"));
    cases.add(new Case("[%s] [%S]", "hello", "Straße"));
    cases.add(new Case("[%s]", new Object() { public String toString() { return "custom"; } }));
    cases.add(new Case("[%s] [%S] [%b] [%B] [%c] [%d] [%x] [%f] [%tT]",
        null, null, null, null, null, null, null, null, null));
    cases.add(new Case("[%s] [%b] [%d] [%99$s]", (Object[]) null));
    cases.add(new Case("[%8s] [%-8s] [%.3s] [%8.3S] [%8.2s]", "text", "text", "abcdef", "straße", null));
    cases.add(new Case("[%b] [%b] [%B] [%b] [%.3B]", true, false, true, new Object(), null));
    cases.add(new Case("[%c] [%C] [%c] [%c] [%c]", 'é', 'é', 0x1f600, (byte) 65, (short) 90));
    cases.add(new Case("[%4c] [%-4c] [%c]", 0, 'x', Character.MAX_VALUE));
    Object[] integral = {Byte.MIN_VALUE, Byte.MAX_VALUE, Short.MIN_VALUE, Short.MAX_VALUE,
        Integer.MIN_VALUE, Integer.MAX_VALUE, Long.MIN_VALUE, Long.MAX_VALUE, 0, 1, -1,
        new BigInteger("123456789012345678901234567890"), new BigInteger("-123456789012345678901234567890")};
    for (Object value : integral) {
      cases.add(new Case("%d | %025d | %x | %04x | %025X | %-30d", value, value, value, value, value, value));
    }
    Object[] floating = {0.0, -0.0, 1.0, -1.0, 1.005, -1.005, 2.675, -2.675,
        0.0004, -0.0004, 0.0005, -0.0005, 9.9995, -9.9995, 123456789.125,
        Double.MAX_VALUE, Double.MIN_VALUE, Double.MIN_NORMAL, Double.NaN,
        Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY, Float.MIN_VALUE,
        Float.MAX_VALUE, Float.valueOf(1.005f), Float.valueOf(-0.0f),
        new BigDecimal("9.9995"), new BigDecimal("-0.0004"), new BigDecimal("1.005"),
        new BigDecimal("123456789012345678901234567890.125")};
    for (Object value : floating) {
      cases.add(new Case("%f | %.0f | %.2f | %.3f | %010.3f | %-10.3f", value, value, value, value, value, value));
    }
    cases.add(new Case("%.324f", Double.MIN_VALUE));
    cases.add(new Case("%.324f", Double.MIN_NORMAL));
    cases.add(new Case("%2$s/%s/%<s/%1$s/%s/%2$04x", "a", 15));
    cases.add(new Case("%% [%5%] [%-5%] %nend"));
    cases.add(new Case("[%06d] [%06x] [%06f] [%.2f]", null, null, null, null));
    Date localDate = new Date(123, 0, 2, 3, 4, 5);
    cases.add(new Case("%tT | %TT | %12tT | %-12tT", localDate, localDate.getTime(), localDate, localDate.getTime()));
    cases.add(new Case("%s", "unused arguments accepted", 1, 2));
    return cases;
  }
}
