package com.webawt.portable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;

import java.awt.color.ColorSpace;
import java.awt.color.ICC_Profile;
import java.awt.color.ICC_ProfileRGB;
import java.awt.color.ProfileDataException;
import org.junit.Test;

public final class PortableIccJ2clTest {
    @Test
    public void preservesBuiltInAndParsedProfileMetadata() {
        assertEquals("true:1:5:0:3:128", PortableIccScenarios.rgbProfileMetadata());
        assertEquals("true:1:5:0:3", PortableIccScenarios.parsedProfileMetadata());
    }

    @Test
    public void roundTripsSrgbThroughTheIccColorSpace() {
        float[] roundTrip = PortableIccScenarios.rgbRoundTrip();
        assertEquals(0.2f, roundTrip[0], 0.002f);
        assertEquals(0.4f, roundTrip[1], 0.002f);
        assertEquals(0.8f, roundTrip[2], 0.002f);
    }

    @Test
    public void exposesStandardRgbProfileCharacteristics() {
        float[] values = PortableIccScenarios.whitePointAndMatrix();
        assertEquals(0.95015f, values[0], 0.003f);
        assertEquals(1.0f, values[1], 0.003f);
        assertEquals(1.08826f, values[2], 0.003f);
        assertTrue(values[3] > 0.4f);
        assertTrue(values[4] > 0.7f);
        assertTrue(values[5] > 0.7f);
    }

    @Test
    public void distinguishesGammaAndTableTransferCurves() {
        ICC_ProfileRGB linear = (ICC_ProfileRGB) ICC_Profile.getInstance(ColorSpace.CS_LINEAR_RGB);
        assertEquals(1.0f, linear.getGamma(ICC_ProfileRGB.REDCOMPONENT), 0.0001f);
        ICC_ProfileRGB srgb = (ICC_ProfileRGB) ICC_Profile.getInstance(ColorSpace.CS_sRGB);
        assertTrue(srgb.getTRC(ICC_ProfileRGB.REDCOMPONENT).length > 1);
        try {
            srgb.getGamma(ICC_ProfileRGB.REDCOMPONENT);
            throw new AssertionError("sRGB should expose a table transfer curve");
        } catch (ProfileDataException expected) {
            assertTrue(expected.getMessage().length() > 0);
        }
    }

    @Test
    public void persistsMutatedTagsInSerializedProfileData() {
        ICC_Profile profile = ICC_Profile.getInstance(ColorSpace.CS_sRGB);
        byte[] copyright = new byte[] {0x74, 0x65, 0x78, 0x74, 0, 0, 0, 0, 0x78, 0, 0, 0};
        profile.setData(ICC_Profile.icSigCopyrightTag, copyright);
        ICC_Profile reparsed = ICC_Profile.getInstance(profile.getData());
        assertArrayEquals(copyright, reparsed.getData(ICC_Profile.icSigCopyrightTag));
    }
}
