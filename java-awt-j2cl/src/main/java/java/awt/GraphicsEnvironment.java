package java.awt;

import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.webawt.internal.peer.WebAwtRuntime;

public abstract class GraphicsEnvironment {
    private static final GraphicsEnvironment LOCAL_ENVIRONMENT = new BrowserGraphicsEnvironment();

    protected GraphicsEnvironment() {
    }

    public static GraphicsEnvironment getLocalGraphicsEnvironment() {
        return LOCAL_ENVIRONMENT;
    }

    public static boolean isHeadless() {
        return false;
    }

    public boolean isHeadlessInstance() {
        return false;
    }

    public abstract GraphicsDevice[] getScreenDevices() throws HeadlessException;

    public abstract GraphicsDevice getDefaultScreenDevice() throws HeadlessException;

    public abstract Graphics2D createGraphics(BufferedImage image);

    public abstract Font[] getAllFonts();

    public abstract String[] getAvailableFontFamilyNames();

    public abstract String[] getAvailableFontFamilyNames(Locale locale);

    public boolean registerFont(Font font) {
        return false;
    }

    public void preferLocaleFonts() {
    }

    public void preferProportionalFonts() {
    }

    public Point getCenterPoint() throws HeadlessException {
        Rectangle bounds = getMaximumWindowBounds();
        return new Point(bounds.x + bounds.width / 2, bounds.y + bounds.height / 2);
    }

    public Rectangle getMaximumWindowBounds() throws HeadlessException {
        return getDefaultScreenDevice().getDefaultConfiguration().getBounds();
    }
}

final class BrowserGraphicsEnvironment extends GraphicsEnvironment {
    private static final String[] LOGICAL_FAMILIES = {Font.DIALOG, Font.DIALOG_INPUT, Font.SANS_SERIF, Font.SERIF, Font.MONOSPACED};
    private final GraphicsDevice defaultDevice = new BrowserGraphicsDevice();
    private final List<Font> registeredFonts = new ArrayList<>();
    private boolean localeFontsPreferred;
    private boolean proportionalFontsPreferred;

    @Override
    public GraphicsDevice[] getScreenDevices() {
        return new GraphicsDevice[] {defaultDevice};
    }

    @Override
    public GraphicsDevice getDefaultScreenDevice() {
        return defaultDevice;
    }

    @Override
    public Graphics2D createGraphics(BufferedImage image) {
        if (image == null) {
            throw new NullPointerException("image");
        }
        return image.createGraphics();
    }

    @Override
    public Font[] getAllFonts() {
        String[] familyNames = getAvailableFontFamilyNames();
        Font[] fonts = new Font[familyNames.length];
        for (int index = 0; index < familyNames.length; index++) {
            fonts[index] = new Font(familyNames[index], Font.PLAIN, 1);
        }
        return fonts;
    }

    @Override
    public String[] getAvailableFontFamilyNames() {
        List<String> familyNames = new ArrayList<>();
        appendLogicalFamilies(familyNames);
        for (Font font : registeredFonts) {
            if (!familyNames.contains(font.getFamily())) {
                familyNames.add(font.getFamily());
            }
        }
        return familyNames.toArray(new String[0]);
    }

    @Override
    public String[] getAvailableFontFamilyNames(Locale locale) {
        if (locale == null) {
            throw new NullPointerException("locale");
        }
        return getAvailableFontFamilyNames();
    }

    @Override
    public boolean registerFont(Font font) {
        if (font == null) {
            throw new NullPointerException("font");
        }
        if (isLogicalFamily(font.getFamily()) || registeredFonts.contains(font)) {
            return false;
        }
        registeredFonts.add(font);
        return true;
    }

    @Override
    public void preferLocaleFonts() {
        localeFontsPreferred = true;
    }

    @Override
    public void preferProportionalFonts() {
        proportionalFontsPreferred = true;
    }

    private void appendLogicalFamilies(List<String> familyNames) {
        if (localeFontsPreferred || proportionalFontsPreferred) {
            familyNames.add(Font.SANS_SERIF);
        }
        for (String family : LOGICAL_FAMILIES) {
            if (!familyNames.contains(family)) {
                familyNames.add(family);
            }
        }
    }

    private static boolean isLogicalFamily(String family) {
        for (String logicalFamily : LOGICAL_FAMILIES) {
            if (logicalFamily.equalsIgnoreCase(family)) {
                return true;
            }
        }
        return false;
    }
}

final class BrowserGraphicsDevice extends GraphicsDevice {
    private final GraphicsConfiguration configuration = new BrowserGraphicsConfiguration(this);

    @Override
    public int getType() {
        return TYPE_RASTER_SCREEN;
    }

    @Override
    public String getIDstring() {
        return "webawt-screen-0";
    }

    @Override
    public GraphicsConfiguration[] getConfigurations() {
        return new GraphicsConfiguration[] {configuration};
    }

    @Override
    public GraphicsConfiguration getDefaultConfiguration() {
        return configuration;
    }
}

final class BrowserGraphicsConfiguration extends GraphicsConfiguration {
    private final GraphicsDevice device;

    BrowserGraphicsConfiguration(GraphicsDevice device) {
        this.device = device;
    }

    @Override
    public GraphicsDevice getDevice() {
        return device;
    }

    @Override
    public ColorModel getColorModel() {
        return ColorModel.getRGBdefault();
    }

    @Override
    public ColorModel getColorModel(int transparency) {
        return colorModel(transparency);
    }

    @Override
    public AffineTransform getDefaultTransform() {
        double scale = WebAwtRuntime.getDeviceScaleFactor();
        return AffineTransform.getScaleInstance(scale, scale);
    }

    @Override
    public AffineTransform getNormalizingTransform() {
        return new AffineTransform();
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(0, 0, WebAwtRuntime.getViewportWidth(), WebAwtRuntime.getViewportHeight());
    }
}
