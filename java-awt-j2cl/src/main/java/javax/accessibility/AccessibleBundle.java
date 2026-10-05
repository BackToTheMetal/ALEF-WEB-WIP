package javax.accessibility;

import java.util.Locale;

public abstract class AccessibleBundle {
    private static final String DEFAULT_RESOURCE_BUNDLE_NAME = "com.sun.accessibility.internal.resources.accessibility";
    protected String key;

    public AccessibleBundle() {
    }

    protected String toDisplayString(String resourceBundleName, Locale locale) {
        if (resourceBundleName == null || locale == null) {
            throw new NullPointerException();
        }
        return key;
    }

    public String toDisplayString(Locale locale) {
        return toDisplayString(DEFAULT_RESOURCE_BUNDLE_NAME, locale);
    }

    public String toDisplayString() {
        return toDisplayString(Locale.getDefault());
    }

    @Override
    public String toString() {
        return toDisplayString();
    }
}
