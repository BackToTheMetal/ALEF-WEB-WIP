package java.awt;

import java.io.Serializable;
import org.webawt.internal.peer.WebAwtRuntime;

public final class SystemColor extends Color implements Serializable {
    public static final int DESKTOP = 0;
    public static final int ACTIVE_CAPTION = 1;
    public static final int ACTIVE_CAPTION_TEXT = 2;
    public static final int ACTIVE_CAPTION_BORDER = 3;
    public static final int INACTIVE_CAPTION = 4;
    public static final int INACTIVE_CAPTION_TEXT = 5;
    public static final int INACTIVE_CAPTION_BORDER = 6;
    public static final int WINDOW = 7;
    public static final int WINDOW_BORDER = 8;
    public static final int WINDOW_TEXT = 9;
    public static final int MENU = 10;
    public static final int MENU_TEXT = 11;
    public static final int TEXT = 12;
    public static final int TEXT_TEXT = 13;
    public static final int TEXT_HIGHLIGHT = 14;
    public static final int TEXT_HIGHLIGHT_TEXT = 15;
    public static final int TEXT_INACTIVE_TEXT = 16;
    public static final int CONTROL = 17;
    public static final int CONTROL_TEXT = 18;
    public static final int CONTROL_HIGHLIGHT = 19;
    public static final int CONTROL_LT_HIGHLIGHT = 20;
    public static final int CONTROL_SHADOW = 21;
    public static final int CONTROL_DK_SHADOW = 22;
    public static final int SCROLLBAR = 23;
    public static final int INFO = 24;
    public static final int INFO_TEXT = 25;
    public static final int NUM_COLORS = 26;
    public static final SystemColor desktop = color(DESKTOP, 0x005c5c);
    public static final SystemColor activeCaption = color(ACTIVE_CAPTION, 0x000080);
    public static final SystemColor activeCaptionText = color(ACTIVE_CAPTION_TEXT, 0xffffff);
    public static final SystemColor activeCaptionBorder = color(ACTIVE_CAPTION_BORDER, 0xc0c0c0);
    public static final SystemColor inactiveCaption = color(INACTIVE_CAPTION, 0x808080);
    public static final SystemColor inactiveCaptionText = color(INACTIVE_CAPTION_TEXT, 0xc0c0c0);
    public static final SystemColor inactiveCaptionBorder = color(INACTIVE_CAPTION_BORDER, 0xc0c0c0);
    public static final SystemColor window = color(WINDOW, 0xffffff);
    public static final SystemColor windowBorder = color(WINDOW_BORDER, 0x000000);
    public static final SystemColor windowText = color(WINDOW_TEXT, 0x000000);
    public static final SystemColor menu = color(MENU, 0xc0c0c0);
    public static final SystemColor menuText = color(MENU_TEXT, 0x000000);
    public static final SystemColor text = color(TEXT, 0xc0c0c0);
    public static final SystemColor textText = color(TEXT_TEXT, 0x000000);
    public static final SystemColor textHighlight = color(TEXT_HIGHLIGHT, 0x000080);
    public static final SystemColor textHighlightText = color(TEXT_HIGHLIGHT_TEXT, 0xffffff);
    public static final SystemColor textInactiveText = color(TEXT_INACTIVE_TEXT, 0x808080);
    public static final SystemColor control = color(CONTROL, 0xc0c0c0);
    public static final SystemColor controlText = color(CONTROL_TEXT, 0x000000);
    public static final SystemColor controlHighlight = color(CONTROL_HIGHLIGHT, 0xffffff);
    public static final SystemColor controlLtHighlight = color(CONTROL_LT_HIGHLIGHT, 0xe0e0e0);
    public static final SystemColor controlShadow = color(CONTROL_SHADOW, 0x808080);
    public static final SystemColor controlDkShadow = color(CONTROL_DK_SHADOW, 0x000000);
    public static final SystemColor scrollbar = color(SCROLLBAR, 0xe0e0e0);
    public static final SystemColor info = color(INFO, 0xffffe1);
    public static final SystemColor infoText = color(INFO_TEXT, 0x000000);
    private static final long serialVersionUID = 4503142729533789064L;
    private final int index;

    private SystemColor(int index, int rgb) {
        super(WebAwtRuntime.getSystemColor(index, rgb));
        this.index = index;
    }

    @Override
    public String toString() {
        return "java.awt.SystemColor[i=" + index + "]";
    }

    private static SystemColor color(int index, int rgb) {
        return new SystemColor(index, rgb);
    }
}
