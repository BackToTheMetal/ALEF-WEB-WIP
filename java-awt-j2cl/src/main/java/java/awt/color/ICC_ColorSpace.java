package java.awt.color;

public class ICC_ColorSpace extends ColorSpace {
    private static final float MAX_XYZ = 1.9999695f;
    private final ICC_Profile profile;
    private final ColorSpace delegate;

    public ICC_ColorSpace(ICC_Profile profile) {
        super(requireProfile(profile).getColorSpaceType(), profile.getNumComponents());
        this.profile = profile;
        delegate = selectDelegate(profile);
    }

    public ICC_Profile getProfile() {
        return profile;
    }

    @Override
    public float[] toRGB(float[] colorValue) {
        return delegate.toRGB(colorValue);
    }

    @Override
    public float[] fromRGB(float[] rgbValue) {
        return delegate.fromRGB(rgbValue);
    }

    @Override
    public float[] toCIEXYZ(float[] colorValue) {
        return delegate.toCIEXYZ(colorValue);
    }

    @Override
    public float[] fromCIEXYZ(float[] colorValue) {
        return delegate.fromCIEXYZ(colorValue);
    }

    @Override
    public float getMinValue(int component) {
        super.getMinValue(component);
        return 0.0f;
    }

    @Override
    public float getMaxValue(int component) {
        super.getMaxValue(component);
        return getType() == TYPE_XYZ ? MAX_XYZ : 1.0f;
    }

    private static ICC_Profile requireProfile(ICC_Profile profile) {
        if (profile == null) {
            throw new NullPointerException("profile");
        }
        return profile;
    }

    private static ColorSpace selectDelegate(ICC_Profile profile) {
        int standard = profile.getStandardColorSpace();
        if (standard != 0) {
            return ColorSpace.getInstance(standard);
        }
        if (profile.getColorSpaceType() == TYPE_GRAY) {
            return ColorSpace.getInstance(CS_GRAY);
        }
        if (profile.getColorSpaceType() == TYPE_XYZ) {
            return ColorSpace.getInstance(CS_CIEXYZ);
        }
        if (profile.getColorSpaceType() == TYPE_3CLR) {
            return ColorSpace.getInstance(CS_PYCC);
        }
        if (profile.getColorSpaceType() == TYPE_RGB) {
            return ColorSpace.getInstance(CS_sRGB);
        }
        throw new CMMException("No dependency-free conversion is available for ICC color type " + profile.getColorSpaceType());
    }
}
