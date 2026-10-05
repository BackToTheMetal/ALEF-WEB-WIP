package com.webawt.portable;

import org.junit.Test;

public final class PortableImageJ2clTest {
    @Test
    public void readsUnsignedAndOffsetBuffers() {
        PortableImageScenarios.readsUnsignedAndOffsetBuffers();
    }

    @Test
    public void isolatesBanksAndOffsets() {
        PortableImageScenarios.isolatesBanksAndOffsets();
    }

    @Test
    public void preservesFloatingPointSamples() {
        PortableImageScenarios.preservesFloatingPointSamples();
    }

    @Test
    public void readsAndWritesInterleavedPixels() {
        PortableImageScenarios.readsAndWritesInterleavedPixels();
    }

    @Test
    public void readsAndWritesBandedPixels() {
        PortableImageScenarios.readsAndWritesBandedPixels();
    }

    @Test
    public void packsSingleBitPixelsMostSignificantBitFirst() {
        PortableImageScenarios.packsSingleBitPixelsMostSignificantBitFirst();
    }

    @Test
    public void readsAndWritesMaskedPixels() {
        PortableImageScenarios.readsAndWritesMaskedPixels();
    }

    @Test
    public void sharesDataWithTranslatedRasterChildren() {
        PortableImageScenarios.sharesDataWithTranslatedRasterChildren();
    }

    @Test
    public void clipsRasterCopiesToDestinationBounds() {
        PortableImageScenarios.clipsRasterCopiesToDestinationBounds();
    }

    @Test
    public void convertsPackedAndIndexedColors() {
        PortableImageScenarios.convertsPackedAndIndexedColors();
    }

    @Test
    public void exposesRenderedImageContracts() {
        PortableImageScenarios.exposesRenderedImageContracts();
    }
}
