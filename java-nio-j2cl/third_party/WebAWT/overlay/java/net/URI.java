package java.net;

import java.io.Serializable;

public final class URI implements Comparable<URI>, Serializable {
    private final String value;
    private final String scheme;

    public URI(String value) throws URISyntaxException {
        validate(value);
        this.value = value;
        int colon = value.indexOf(':');
        scheme = colon < 0 ? null : value.substring(0, colon);
    }

    public static URI create(String value) {
        try {
            return new URI(value);
        } catch (URISyntaxException exception) {
            throw new IllegalArgumentException(exception);
        }
    }

    public String getScheme() {
        return scheme;
    }

    public boolean isAbsolute() {
        return scheme != null;
    }

    public boolean isOpaque() {
        return scheme != null && !value.substring(scheme.length() + 1).startsWith("/");
    }

    public String toASCIIString() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof URI && value.equals(((URI) object).value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public int compareTo(URI other) {
        return value.compareTo(other.value);
    }

    private static void validate(String value) throws URISyntaxException {
        if (value == null) {
            throw new NullPointerException("value");
        }
        for (int index = 0; index < value.length(); index++) {
            char character = value.charAt(index);
            if (character <= ' ' || character == '<' || character == '>' || character == '\\') {
                throw new URISyntaxException(value, "Illegal character", index);
            }
        }
        int colon = value.indexOf(':');
        if (colon == 0 || colon > 0 && !validScheme(value.substring(0, colon))) {
            throw new URISyntaxException(value, "Illegal scheme", 0);
        }
    }

    private static boolean validScheme(String value) {
        if (value.length() == 0 || !Character.isLetter(value.charAt(0))) {
            return false;
        }
        for (int index = 1; index < value.length(); index++) {
            char character = value.charAt(index);
            if (!Character.isLetterOrDigit(character) && character != '+' && character != '-' && character != '.') {
                return false;
            }
        }
        return true;
    }
}
