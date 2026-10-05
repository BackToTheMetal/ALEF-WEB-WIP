package java.net;

import java.io.Serializable;

public final class URL implements Serializable {
    private final String value;
    private final String protocol;

    public URL(String value) throws MalformedURLException {
        if (value == null) {
            throw new NullPointerException("value");
        }
        int colon = value.indexOf(':');
        if (colon <= 0 || !validProtocol(value.substring(0, colon))) {
            throw new MalformedURLException("Invalid URL: " + value);
        }
        this.value = value;
        protocol = value.substring(0, colon).toLowerCase();
    }

    public String getProtocol() {
        return protocol;
    }

    public String toExternalForm() {
        return value;
    }

    @Override
    public String toString() {
        return toExternalForm();
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof URL && value.equals(((URL) object).value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    private static boolean validProtocol(String value) {
        if (!Character.isLetter(value.charAt(0))) {
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
