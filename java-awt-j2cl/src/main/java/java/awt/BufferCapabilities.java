package java.awt;

public class BufferCapabilities implements Cloneable {
    private final ImageCapabilities frontCapabilities;
    private final ImageCapabilities backCapabilities;
    private final FlipContents flipContents;

    public BufferCapabilities(ImageCapabilities frontCapabilities, ImageCapabilities backCapabilities, FlipContents flipContents) {
        if (frontCapabilities == null || backCapabilities == null) {
            throw new IllegalArgumentException("buffer capabilities must not be null");
        }
        this.frontCapabilities = frontCapabilities;
        this.backCapabilities = backCapabilities;
        this.flipContents = flipContents;
    }

    public ImageCapabilities getFrontBufferCapabilities() {
        return frontCapabilities;
    }

    public ImageCapabilities getBackBufferCapabilities() {
        return backCapabilities;
    }

    public boolean isPageFlipping() {
        return flipContents != null;
    }

    public FlipContents getFlipContents() {
        return flipContents;
    }

    public boolean isFullScreenRequired() {
        return false;
    }

    public boolean isMultiBufferAvailable() {
        return false;
    }

    public Object clone() {
        return new BufferCapabilities(frontCapabilities, backCapabilities, flipContents);
    }

    public static final class FlipContents extends AttributeValue {
        private static final String[] NAMES = {"undefined", "background", "prior", "copied"};
        public static final FlipContents UNDEFINED = new FlipContents(0);
        public static final FlipContents BACKGROUND = new FlipContents(1);
        public static final FlipContents PRIOR = new FlipContents(2);
        public static final FlipContents COPIED = new FlipContents(3);

        private FlipContents(int value) {
            super(value, NAMES);
        }

        @Override
        public String toString() {
            return super.toString();
        }

        @Override
        public int hashCode() {
            return super.hashCode();
        }
    }
}
