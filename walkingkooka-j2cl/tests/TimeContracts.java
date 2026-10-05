package walkingkooka.integration.tests;

import java.time.*;
import java.time.chrono.*;
import java.time.format.*;
import java.time.temporal.*;
import java.time.zone.*;
import java.util.Locale;

/** Same program runs against the JDK and the actual browser java.time overlay. */
public final class TimeContracts {
    private final StringBuilder result = new StringBuilder();
    private int count;

    private void record(String name, Object value) {
        result.append(name).append('=').append(value).append('\n');
        count++;
    }

    private void invalid(String name, Runnable action) {
        try {
            action.run();
        } catch (DateTimeException expected) {
            record(name, "DateTimeException");
            return;
        }
        throw new AssertionError(name + " should throw DateTimeException");
    }

    public static String run() {
        TimeContracts t = new TimeContracts();
        t.check();
        return "PASS " + t.count + " contracts\n" + t.result;
    }

    public static void main(String[] args) {
        System.out.print(run());
    }

    private void check() {
        record("leap", LocalDate.of(2024, 2, 29).plusYears(1));
        record("monthEnd", LocalDate.of(2024, 1, 31).plusMonths(1));
        record("centuries", Year.isLeap(1900) + "/" + Year.isLeap(2000));
        record("negativeYear", LocalDate.of(-44, 3, 15));
        record("minDate", LocalDate.MIN.toEpochDay());
        record("maxDate", LocalDate.MAX.toEpochDay());
        record("yearMonth", YearMonth.of(2024, 2).atEndOfMonth());
        record("monthDay", MonthDay.of(2, 29).atYear(2023));
        record("midnight", LocalTime.of(23, 59, 59, 999999999).plusNanos(1));
        record("nanoTime", LocalTime.ofNanoOfDay(12345678901234L));
        record("dateTimeCarry", LocalDateTime.of(2024, 12, 31, 23, 59).plusMinutes(2));
        record("epoch", Instant.EPOCH);
        record("negativeNanos", Instant.ofEpochSecond(0, -1));
        record("millis", Instant.ofEpochMilli(-1));
        record("precision", Instant.parse("2024-02-29T12:34:56.123456789Z"));
        record("truncated", Instant.parse("2024-02-29T12:34:56.123456789Z").truncatedTo(ChronoUnit.SECONDS));
        record("duration", Duration.parse("P2DT3H4M5.123456789S"));
        record("negativeDuration", Duration.parse("-PT0.5S"));
        record("durationMath", Duration.ofSeconds(3).dividedBy(2));
        record("between", Duration.between(Instant.ofEpochSecond(-1), Instant.ofEpochSecond(1, 1)));
        record("period", Period.parse("P1Y2M3W4D"));
        record("periodNegative", Period.parse("-P1Y2M"));
        record("periodNormalized", Period.of(1, 15, 3).normalized());
        record("periodBetween", Period.between(LocalDate.of(2020, 2, 29), LocalDate.of(2024, 3, 1)));
        record("offset", OffsetDateTime.parse("2024-02-29T12:34:56+05:45").toInstant());
        record("offsetTime", OffsetTime.parse("12:34:56+02:00").withOffsetSameInstant(ZoneOffset.UTC));
        record("fixedClock", LocalDateTime.now(Clock.fixed(Instant.parse("2024-02-29T23:30:00Z"), ZoneOffset.ofHours(2))));
        record("offsetClock", Clock.offset(Clock.fixed(Instant.EPOCH, ZoneOffset.UTC), Duration.ofMinutes(5)).instant());
        record("isoWeek", LocalDate.of(2021, 1, 1).get(IsoFields.WEEK_BASED_YEAR) + "/" + LocalDate.of(2021, 1, 1).get(IsoFields.WEEK_OF_WEEK_BASED_YEAR));
        record("weekFields", LocalDate.of(2021, 1, 1).get(WeekFields.ISO.weekOfWeekBasedYear()));
        record("adjuster", LocalDate.of(2024, 2, 1).with(TemporalAdjusters.lastInMonth(DayOfWeek.MONDAY)));
        record("nextMonday", LocalDate.of(2024, 2, 29).with(TemporalAdjusters.next(DayOfWeek.MONDAY)));
        record("isoFormat", DateTimeFormatter.ISO_DATE_TIME.format(LocalDateTime.of(2024, 2, 29, 12, 34, 56)));
        record("customFormat", LocalDate.of(2024, 2, 29).format(DateTimeFormatter.ofPattern("dd/MM/uuuu")));
        record("strictParse", LocalDate.parse("29/02/2024", DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT)));
        record("englishText", LocalDate.of(2024, 2, 29).format(DateTimeFormatter.ofPattern("EEEE d MMMM uuuu", Locale.forLanguageTag("en-US"))));
        record("dutchText", LocalDate.of(2024, 2, 29).format(DateTimeFormatter.ofPattern("EEEE d MMMM uuuu", Locale.forLanguageTag("nl-NL"))));
        record("dutchParse", LocalDate.parse("29 februari 2024", DateTimeFormatter.ofPattern("d MMMM uuuu", Locale.forLanguageTag("nl-NL"))));
        record("builder", new DateTimeFormatterBuilder().appendValue(ChronoField.YEAR, 4).appendLiteral('/').appendValue(ChronoField.DAY_OF_YEAR, 3).toFormatter().format(LocalDate.of(2024, 2, 29)));
        try {
            TemporalAccessor parsed = (TemporalAccessor) DateTimeFormatter.ISO_LOCAL_DATE.toFormat().parseObject("2024-02-29");
            record("legacyFormat", parsed.get(ChronoField.DAY_OF_MONTH));
        } catch (java.text.ParseException e) {
            throw new AssertionError(e);
        }
        try {
            DateTimeFormatter.ISO_LOCAL_DATE.toFormat().parseObject("invalid");
            throw new AssertionError("Expected ParseException");
        } catch (java.text.ParseException e) {
            record("legacyParseError", e.getErrorOffset());
        }
        record("thai", ThaiBuddhistDate.from(LocalDate.of(2024, 2, 29)).get(ChronoField.YEAR));
        record("minguo", MinguoDate.from(LocalDate.of(2024, 2, 29)).get(ChronoField.YEAR));
        ZoneId amsterdam = ZoneId.of("Europe/Amsterdam");
        record("winterOffset", amsterdam.getRules().getOffset(Instant.parse("2024-01-01T12:00:00Z")));
        record("summerOffset", amsterdam.getRules().getOffset(Instant.parse("2024-07-01T12:00:00Z")));
        record("dstGap", LocalDateTime.of(2024, 3, 31, 2, 30).atZone(amsterdam));
        ZonedDateTime overlap = LocalDateTime.of(2024, 10, 27, 2, 30).atZone(amsterdam);
        record("dstOverlapEarlier", overlap);
        record("dstOverlapLater", overlap.withLaterOffsetAtOverlap());
        record("gapOffsets", amsterdam.getRules().getValidOffsets(LocalDateTime.of(2024, 3, 31, 2, 30)).size());
        record("overlapOffsets", amsterdam.getRules().getValidOffsets(LocalDateTime.of(2024, 10, 27, 2, 30)).size());
        ZonedDateTime before = LocalDateTime.of(2024, 3, 30, 12, 0).atZone(amsterdam);
        record("calendarDayVs24h", before.plusDays(1).getHour() + "/" + before.plusHours(24).getHour());
        record("nextTransition", amsterdam.getRules().nextTransition(Instant.parse("2024-01-01T00:00:00Z")).getInstant());
        for (String zone : new String[] {"Europe/London", "America/New_York", "Australia/Sydney", "Asia/Tokyo", "UTC", "GMT"}) {
            record("zone/" + zone, Instant.parse("2024-07-01T00:00:00Z").atZone(ZoneId.of(zone)));
        }
        invalid("invalidLeap", () -> LocalDate.of(2023, 2, 29));
        invalid("invalidMonth", () -> LocalDate.of(2024, 13, 1));
        invalid("invalidTime", () -> LocalTime.of(24, 0));
        invalid("invalidParse", () -> LocalDate.parse("2023-02-29"));
        invalid("invalidStrict", () -> LocalDate.parse("31/04/2024", DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT)));
        invalid("invalidDuration", () -> Duration.parse("PT"));
        invalid("invalidPeriod", () -> Period.parse("P"));
        invalid("invalidOffset", () -> ZoneOffset.ofHours(19));
        invalid("unknownZone", () -> ZoneId.of("Missing/Zone"));
        for (int i = 0; i < 240; i++) {
            long epochDay = (i * 7919L) % 200000 - 100000;
            LocalDate d = LocalDate.ofEpochDay(epochDay);
            record("dateSweep/" + i, d + "/" + d.getDayOfWeek() + "/" + d.plusMonths(i % 25 - 12).plusDays(i % 63 - 31) + "/" + d.toEpochDay());
        }
    }
}
