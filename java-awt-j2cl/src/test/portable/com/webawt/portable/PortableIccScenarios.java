package com.webawt.portable;

import java.awt.color.ColorSpace;
import java.awt.color.ICC_ColorSpace;
import java.awt.color.ICC_Profile;
import java.awt.color.ICC_ProfileRGB;

public final class PortableIccScenarios {
    private PortableIccScenarios() {
    }

    public static String rgbProfileMetadata() {
        ICC_Profile profile = ICC_Profile.getInstance(ColorSpace.CS_sRGB);
        return (profile instanceof ICC_ProfileRGB) + ":" + profile.getProfileClass() + ":" + profile.getColorSpaceType() + ":" + profile.getPCSType() + ":" + profile.getNumComponents() + ":" + profile.getData(ICC_Profile.icSigHead).length;
    }

    public static String parsedProfileMetadata() {
        ICC_Profile original = ICC_Profile.getInstance(ColorSpace.CS_sRGB);
        ICC_Profile parsed = ICC_Profile.getInstance(original.getData());
        return (parsed instanceof ICC_ProfileRGB) + ":" + parsed.getProfileClass() + ":" + parsed.getColorSpaceType() + ":" + parsed.getPCSType() + ":" + parsed.getNumComponents();
    }

    public static float[] rgbRoundTrip() {
        ICC_ColorSpace colorSpace = new ICC_ColorSpace(ICC_Profile.getInstance(ColorSpace.CS_sRGB));
        return colorSpace.toRGB(colorSpace.fromRGB(new float[] {0.2f, 0.4f, 0.8f}));
    }

    public static float[] whitePointAndMatrix() {
        ICC_ProfileRGB profile = (ICC_ProfileRGB) ICC_Profile.getInstance(ColorSpace.CS_sRGB);
        float[] whitePoint = profile.getMediaWhitePoint();
        float[][] matrix = profile.getMatrix();
        return new float[] {whitePoint[0], whitePoint[1], whitePoint[2], matrix[0][0], matrix[1][1], matrix[2][2]};
    }
}
