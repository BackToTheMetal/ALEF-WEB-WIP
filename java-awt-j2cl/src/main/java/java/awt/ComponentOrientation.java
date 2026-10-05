package java.awt;

import java.io.Serializable;
import java.util.Locale;

public final class ComponentOrientation implements Serializable {
    private static final long serialVersionUID = -4113291392143563828L;
    public static final ComponentOrientation LEFT_TO_RIGHT = new ComponentOrientation(true, true);
    public static final ComponentOrientation RIGHT_TO_LEFT = new ComponentOrientation(true, false);
    public static final ComponentOrientation UNKNOWN = new ComponentOrientation(false, true);
    private final boolean horizontal;
    private final boolean leftToRight;

    private ComponentOrientation(boolean horizontal, boolean leftToRight) {
        this.horizontal = horizontal;
        this.leftToRight = leftToRight;
    }

    public boolean isHorizontal() {
        return horizontal;
    }

    public boolean isLeftToRight() {
        return leftToRight;
    }

    public static ComponentOrientation getOrientation(Locale locale) {
        String localeName = locale.toString();
        int separator = localeName.indexOf('_');
        String language = separator < 0 ? localeName : localeName.substring(0, separator);
        return isRightToLeftLanguage(language) ? RIGHT_TO_LEFT : LEFT_TO_RIGHT;
    }

    private static boolean isRightToLeftLanguage(String language) {
        return "ar".equals(language) || "fa".equals(language) || "he".equals(language) || "iw".equals(language) || "ur".equals(language);
    }
}
