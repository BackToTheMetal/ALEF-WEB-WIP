package com.webawt.portable;

import static org.junit.Assert.assertEquals;

import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import org.junit.Test;

public final class PortableGeometryJ2clTest {
    @Test
    public void checksRectangleBoundariesAndNegativeExtents() {
        PortableGeometryScenarios.checksRectangleBoundariesAndNegativeExtents();
    }

    @Test
    public void combinesRectangles() {
        PortableGeometryScenarios.combinesRectangles();
    }

    @Test
    public void roundsRectangleFramesLikeOpenJdk() {
        PortableGeometryScenarios.roundsRectangleFramesLikeOpenJdk();
    }

    @Test
    public void preservesRectangleGeometryAcrossIntegerOverflow() {
        PortableGeometryScenarios.preservesRectangleGeometryAcrossIntegerOverflow();
    }

    @Test
    public void growsAndAddsRectangleGeometry() {
        PortableGeometryScenarios.growsAndAddsRectangleGeometry();
    }

    @Test
    public void checksRectangleOutcodesAndLines() {
        PortableGeometryScenarios.checksRectangleOutcodesAndLines();
    }

    @Test
    public void iteratesRectanglePaths() {
        PortableGeometryScenarios.iteratesRectanglePaths();
    }

    @Test
    public void calculatesLineRelationshipsAndDistances() {
        PortableGeometryScenarios.calculatesLineRelationshipsAndDistances();
    }

    @Test
    public void composesAndInvertsAffineTransforms() throws NoninvertibleTransformException {
        PortableGeometryScenarios.composesAndInvertsAffineTransforms();
    }

    @Test
    public void transformsOverlappingArrays() {
        PortableGeometryScenarios.transformsOverlappingArrays();
    }

    @Test
    public void classifiesQuadrantRotation() {
        PortableGeometryScenarios.classifiesQuadrantRotation();
    }

    @Test
    public void classifiesCommonAffineTransforms() {
        PortableGeometryScenarios.classifiesCommonAffineTransforms();
    }

    @Test
    public void formatsAffineTransformsLikeOpenJdk() {
        assertEquals("AffineTransform[[0.866025403784439, -0.5, 0.0], [0.5, 0.866025403784439, 0.0]]", AffineTransform.getRotateInstance(Math.PI / 6.0).toString());
    }

    @Test
    public void formatsFloatingGeometryLikeOpenJdk() {
        PortableGeometryScenarios.formatsFloatingGeometryLikeOpenJdk();
    }

    @Test
    public void transformsShapePathsAndPoints() {
        PortableGeometryScenarios.transformsShapePathsAndPoints();
    }

    @Test
    public void buildsAndTransformsGeneralPaths() {
        PortableGeometryScenarios.buildsAndTransformsGeneralPaths();
    }

    @Test
    public void appendsAndClassifiesPathRegions() {
        PortableGeometryScenarios.appendsAndClassifiesPathRegions();
    }

    @Test
    public void flattensCurvedSegments() {
        PortableGeometryScenarios.flattensCurvedSegments();
    }

    @Test
    public void calculatesQuadraticCurves() {
        PortableGeometryScenarios.calculatesQuadraticCurves();
    }

    @Test
    public void calculatesCubicCurves() {
        PortableGeometryScenarios.calculatesCubicCurves();
    }

    @Test
    public void manipulatesPolygons() {
        PortableGeometryScenarios.manipulatesPolygons();
    }

    @Test
    public void createsBasicStrokeOutlines() {
        PortableGeometryScenarios.createsBasicStrokeOutlines();
    }

    @Test
    public void evaluatesPrimitiveCurvedShapes() {
        PortableGeometryScenarios.evaluatesPrimitiveCurvedShapes();
    }

    @Test
    public void combinesAreas() {
        PortableGeometryScenarios.combinesAreas();
    }

    @Test
    public void rejectsInvalidPathOperations() {
        PortableGeometryScenarios.rejectsInvalidPathOperations();
    }

    @Test
    public void rejectsSingularTransforms() {
        PortableGeometryScenarios.rejectsSingularTransforms();
    }
}
