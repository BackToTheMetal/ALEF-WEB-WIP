package alefj2cl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Locale;

/** Checks the overlapping MPS IO/formatting and WalkingKooka time providers. */
public final class JreContracts {
  public static String run() throws Exception {
    java.time.temporal.TemporalAccessor temporal = LocalDate.of(2024, 2, 29);
    check(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE.format(temporal).equals("2024-02-29"), "temporal interfaces");
    check(LocalDate.of(2024, 2, 29).plusYears(1).toString().equals("2025-02-28"), "leap day");
    ZoneId amsterdam = ZoneId.of("Europe/Amsterdam");
    long hours = Duration.between(LocalDate.of(2024, 3, 31).atStartOfDay(amsterdam),
        LocalDate.of(2024, 4, 1).atStartOfDay(amsterdam)).toHours();
    check(hours == 23, "Amsterdam DST day");
    check(String.format(Locale.ROOT, "%04d:%s", 7, "ALEF").equals("0007:ALEF"), "MPS formatting");
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    DataOutputStream output = new DataOutputStream(bytes);
    output.writeUTF("Regelspraak \u20ac");
    output.writeInt(2025);
    DataInputStream input = new DataInputStream(new ByteArrayInputStream(bytes.toByteArray()));
    check(input.readUTF().equals("Regelspraak \u20ac") && input.readInt() == 2025, "MPS binary IO");
    return "PASS: composed JRE";
  }

  private static void check(boolean condition, String name) {
    if (!condition) throw new AssertionError(name);
  }
}
