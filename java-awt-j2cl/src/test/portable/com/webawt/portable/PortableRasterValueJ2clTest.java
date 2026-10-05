package com.webawt.portable;

import org.junit.Test;

public final class PortableRasterValueJ2clTest {
    @Test
    public void copiesAndReportsKernelData() {
        PortableRasterValueScenarios.copiesAndReportsKernelData();
    }

    @Test
    public void appliesByteLookupTables() {
        PortableRasterValueScenarios.appliesByteLookupTables();
    }

    @Test
    public void appliesShortLookupTables() {
        PortableRasterValueScenarios.appliesShortLookupTables();
    }

    @Test
    public void rejectsInvalidLookupDefinitionsAndIndexes() {
        PortableRasterValueScenarios.rejectsInvalidLookupDefinitionsAndIndexes();
    }

    @Test
    public void preservesImagingExceptionMessages() {
        PortableRasterValueScenarios.preservesImagingExceptionMessages();
    }

    @Test
    public void filtersRasterSamplesWithLookupAndRescaleOperations() {
        PortableRasterValueScenarios.filtersRasterSamplesWithLookupAndRescaleOperations();
    }

    @Test
    public void filtersBufferedImageColorsWithoutChangingAlpha() {
        PortableRasterValueScenarios.filtersBufferedImageColorsWithoutChangingAlpha();
    }

    @Test
    public void combinesRasterBandsWithLinearMatrices() {
        PortableRasterValueScenarios.combinesRasterBandsWithLinearMatrices();
    }

    @Test
    public void convolvesRasterInteriorsAndAppliesEdgePolicies() {
        PortableRasterValueScenarios.convolvesRasterInteriorsAndAppliesEdgePolicies();
    }

    @Test
    public void transformsRasterBoundsAndPixels() {
        PortableRasterValueScenarios.transformsRasterBoundsAndPixels();
    }

    @Test
    public void selectsAndDelegatesMultiResolutionImages() {
        PortableRasterValueScenarios.selectsAndDelegatesMultiResolutionImages();
    }

    @Test
    public void cropsAndReplicatesProducerPixels() {
        PortableRasterValueScenarios.cropsAndReplicatesProducerPixels();
    }

    @Test
    public void averagesScaledProducerPixels() {
        PortableRasterValueScenarios.averagesScaledProducerPixels();
    }

    @Test
    public void filtersProducerFramesWithBufferedImageOperations() {
        PortableRasterValueScenarios.filtersProducerFramesWithBufferedImageOperations();
    }

    @Test
    public void grabsImagePixelsAndReportsCompletion() {
        PortableRasterValueScenarios.grabsImagePixelsAndReportsCompletion();
    }

    @Test
    public void managesRenderableParametersAndContexts() {
        PortableRasterValueScenarios.managesRenderableParametersAndContexts();
    }

    @Test
    public void evaluatesRenderableGraphsAndProducesPixels() {
        PortableRasterValueScenarios.evaluatesRenderableGraphsAndProducesPixels();
    }

    @Test
    public void tilesTexturePaintAcrossDeviceCoordinates() {
        PortableRasterValueScenarios.tilesTexturePaintAcrossDeviceCoordinates();
    }

    @Test
    public void tracksCompletedImagesByIdentifier() {
        PortableRasterValueScenarios.tracksCompletedImagesByIdentifier();
    }

    @Test
    public void expandsIndexedRastersToDiscreteImages() {
        PortableRasterValueScenarios.expandsIndexedRastersToDiscreteImages();
    }

    @Test
    public void drawsImagesAcrossGraphicsOverloads() {
        PortableRasterValueScenarios.drawsImagesAcrossGraphicsOverloads();
    }
}
