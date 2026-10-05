package walkingkooka.integration;

/** Linkable capability marker for consumers of the patched J2CL JRE. */
public final class JavaTimeSupport {
    private JavaTimeSupport() {}

    public static String version() {
        // Keep the marker tied to a real java.time API.
        return "0.1.0/" + java.time.LocalDate.of(2000, 1, 1);
    }
}
