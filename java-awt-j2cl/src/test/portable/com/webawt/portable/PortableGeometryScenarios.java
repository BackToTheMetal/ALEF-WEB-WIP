package com.webawt.portable;

import java.awt.Rectangle;
import java.awt.Polygon;
import java.awt.BasicStroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.FlatteningPathIterator;
import java.awt.geom.GeneralPath;
import java.awt.geom.IllegalPathStateException;
import java.awt.geom.Line2D;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.QuadCurve2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

public final class PortableGeometryScenarios {
    private static final double TOLERANCE = 0.000000001;

    private PortableGeometryScenarios() {
    }

    public static void main(String[] arguments) throws NoninvertibleTransformException {
        runAll();
    }

    public static void runAll() throws NoninvertibleTransformException {
        checksRectangleBoundariesAndNegativeExtents();
        combinesRectangles();
        roundsRectangleFramesLikeOpenJdk();
        preservesRectangleGeometryAcrossIntegerOverflow();
        growsAndAddsRectangleGeometry();
        checksRectangleOutcodesAndLines();
        iteratesRectanglePaths();
        calculatesLineRelationshipsAndDistances();
        composesAndInvertsAffineTransforms();
        transformsOverlappingArrays();
        classifiesQuadrantRotation();
        classifiesCommonAffineTransforms();
        formatsAffineTransformsLikeOpenJdk();
        formatsFloatingGeometryLikeOpenJdk();
        transformsShapePathsAndPoints();
        buildsAndTransformsGeneralPaths();
        appendsAndClassifiesPathRegions();
        flattensCurvedSegments();
        calculatesQuadraticCurves();
        calculatesCubicCurves();
        manipulatesPolygons();
        createsBasicStrokeOutlines();
        evaluatesPrimitiveCurvedShapes();
        combinesAreas();
        rejectsInvalidPathOperations();
        rejectsSingularTransforms();
    }

    public static void checksRectangleBoundariesAndNegativeExtents() {
        Rectangle rectangle = new Rectangle(0, 0, 10, 10);
        assertTrue(rectangle.contains(0, 0));
        assertFalse(rectangle.contains(10, 5));
        assertFalse(rectangle.contains(10, 5, 0, 0));
        assertTrue(rectangle.contains(2, 3, 4, 5));
        assertFalse(new Rectangle(0, 0, -1, 4).contains(0, 0));
        assertTrue(rectangle.intersects(new Rectangle(9, 9, 2, 2)));
        assertFalse(rectangle.intersects(new Rectangle(10, 0, 2, 2)));
    }

    public static void combinesRectangles() {
        Rectangle rectangle = new Rectangle(0, 0, 10, 10);
        assertEquals(new Rectangle(8, 0, 2, 3), rectangle.intersection(new Rectangle(8, -2, 5, 5)));
        assertEquals(new Rectangle(-2, -3, 12, 13), rectangle.union(new Rectangle(-2, -3, 3, 4)));
        Rectangle2D.Double floating = new Rectangle2D.Double(1.5, 2.5, 4.0, 5.0);
        assertEquals(new Rectangle2D.Double(1.5, 2.5, 4.0, 5.0), floating.getBounds2D());
        assertTrue(floating.contains(1.5, 2.5));
        assertFalse(floating.contains(5.5, 2.5));
    }

    public static void roundsRectangleFramesLikeOpenJdk() {
        Rectangle positive = new Rectangle();
        positive.setRect(1.2, 2.8, 3.1, 4.1);
        assertEquals(new Rectangle(1, 2, 4, 5), positive);
        Rectangle negative = new Rectangle();
        negative.setRect(5.8, 6.2, -2.1, -3.4);
        assertEquals(new Rectangle(5, 6, -3, -4), negative);
    }

    public static void preservesRectangleGeometryAcrossIntegerOverflow() {
        Rectangle positive = new Rectangle(Integer.MAX_VALUE - 2, 0, 10, 5);
        positive.translate(10, 0);
        assertEquals(new Rectangle(Integer.MAX_VALUE, 0, 18, 5), positive);
        Rectangle negative = new Rectangle(Integer.MIN_VALUE + 2, 0, 10, 5);
        negative.translate(-10, 0);
        assertEquals(new Rectangle(Integer.MIN_VALUE, 0, 2, 5), negative);
    }

    public static void growsAndAddsRectangleGeometry() {
        Rectangle rectangle = new Rectangle(4, 5, 6, 7);
        rectangle.grow(2, 3);
        assertEquals(new Rectangle(2, 2, 10, 13), rectangle);
        rectangle.add(20, -2);
        assertEquals(new Rectangle(2, -2, 18, 17), rectangle);
        Rectangle nearMaximum = new Rectangle(Integer.MAX_VALUE - 2, 10, 4, 5);
        nearMaximum.grow(10, 2);
        assertEquals(new Rectangle(Integer.MAX_VALUE - 12, 8, 24, 9), nearMaximum);
    }

    public static void checksRectangleOutcodesAndLines() {
        Rectangle2D rectangle = new Rectangle2D.Double(0.0, 0.0, 10.0, 10.0);
        assertEquals(Rectangle2D.OUT_LEFT | Rectangle2D.OUT_TOP, rectangle.outcode(-1.0, -1.0));
        assertEquals(0, rectangle.outcode(10.0, 10.0));
        assertTrue(rectangle.intersectsLine(-1.0, 5.0, 11.0, 5.0));
        assertFalse(rectangle.intersectsLine(-1.0, -1.0, -2.0, -2.0));
    }

    public static void iteratesRectanglePaths() {
        PathIterator iterator = new Rectangle2D.Double(1.0, 2.0, 3.0, 4.0).getPathIterator(null);
        int[] expectedSegments = {PathIterator.SEG_MOVETO, PathIterator.SEG_LINETO, PathIterator.SEG_LINETO, PathIterator.SEG_LINETO, PathIterator.SEG_LINETO, PathIterator.SEG_CLOSE};
        double[][] expectedCoordinates = {{1.0, 2.0}, {4.0, 2.0}, {4.0, 6.0}, {1.0, 6.0}, {1.0, 2.0}};
        double[] coordinates = new double[6];
        int index = 0;
        while (!iterator.isDone()) {
            assertEquals(expectedSegments[index], iterator.currentSegment(coordinates));
            if (index < expectedCoordinates.length) {
                assertClose(expectedCoordinates[index][0], coordinates[0]);
                assertClose(expectedCoordinates[index][1], coordinates[1]);
            }
            iterator.next();
            index++;
        }
        assertEquals(expectedSegments.length, index);
    }

    public static void calculatesLineRelationshipsAndDistances() {
        Line2D line = new Line2D.Double(0.0, 0.0, 10.0, 0.0);
        assertEquals(-1, line.relativeCCW(0.0, 1.0));
        assertEquals(1, line.relativeCCW(20.0, 0.0));
        assertFalse(Line2D.linesIntersect(0.0, 0.0, 1.0, 0.0, 2.0, 0.0, 3.0, 0.0));
        assertTrue(Line2D.linesIntersect(0.0, 0.0, 10.0, 0.0, 5.0, -2.0, 5.0, 2.0));
        assertClose(3.0, line.ptSegDist(5.0, 3.0));
        assertClose(5.0, line.ptSegDist(15.0, 0.0));
    }

    public static void composesAndInvertsAffineTransforms() throws NoninvertibleTransformException {
        AffineTransform transform = new AffineTransform();
        transform.translate(2.0, 3.0);
        transform.scale(4.0, 5.0);
        Point2D transformed = transform.transform(new Point2D.Double(2.0, 3.0), null);
        assertClose(10.0, transformed.getX());
        assertClose(18.0, transformed.getY());
        Point2D restored = transform.inverseTransform(transformed, null);
        assertClose(2.0, restored.getX());
        assertClose(3.0, restored.getY());
        double[] matrix = new double[6];
        transform.getMatrix(matrix);
        assertArrayEquals(new double[] {4.0, 0.0, 0.0, 5.0, 2.0, 3.0}, matrix);
    }

    public static void transformsOverlappingArrays() {
        AffineTransform transform = AffineTransform.getTranslateInstance(10.0, 20.0);
        double[] coordinates = {1.0, 2.0, 3.0, 4.0, 0.0, 0.0};
        transform.transform(coordinates, 0, coordinates, 2, 2);
        assertArrayEquals(new double[] {1.0, 2.0, 11.0, 22.0, 13.0, 24.0}, coordinates);
    }

    public static void classifiesQuadrantRotation() {
        AffineTransform transform = AffineTransform.getRotateInstance(Math.PI / 2.0);
        assertEquals(AffineTransform.TYPE_QUADRANT_ROTATION, transform.getType());
        Point2D point = transform.transform(new Point2D.Double(2.0, 3.0), null);
        assertClose(-3.0, point.getX());
        assertClose(2.0, point.getY());
    }

    public static void classifiesCommonAffineTransforms() {
        assertEquals(AffineTransform.TYPE_IDENTITY, new AffineTransform().getType());
        assertEquals(AffineTransform.TYPE_TRANSLATION, AffineTransform.getTranslateInstance(2.0, 3.0).getType());
        assertEquals(AffineTransform.TYPE_UNIFORM_SCALE, AffineTransform.getScaleInstance(2.0, 2.0).getType());
        assertEquals(AffineTransform.TYPE_GENERAL_SCALE, AffineTransform.getScaleInstance(2.0, 3.0).getType());
        assertEquals(AffineTransform.TYPE_FLIP | AffineTransform.TYPE_UNIFORM_SCALE, AffineTransform.getScaleInstance(-2.0, 2.0).getType());
        assertEquals(AffineTransform.TYPE_GENERAL_TRANSFORM, new AffineTransform(1.0, 1.0, 1.0, 1.0, 0.0, 0.0).getType());
        assertEquals(AffineTransform.TYPE_QUADRANT_ROTATION | AffineTransform.TYPE_UNIFORM_SCALE, new AffineTransform(0.0, 2.0, -2.0, 0.0, 0.0, 0.0).getType());
        assertEquals(AffineTransform.TYPE_QUADRANT_ROTATION | AffineTransform.TYPE_GENERAL_SCALE, new AffineTransform(0.0, 2.0, -3.0, 0.0, 0.0, 0.0).getType());
        assertEquals(AffineTransform.TYPE_GENERAL_ROTATION, AffineTransform.getRotateInstance(Math.PI / 4.0).getType());
    }

    public static void formatsAffineTransformsLikeOpenJdk() {
        AffineTransform transform = AffineTransform.getRotateInstance(Math.PI / 6.0);
        assertEquals("AffineTransform[[0.866025403784439, -0.5, 0.0], [0.5, 0.866025403784439, 0.0]]", transform.toString());
    }

    public static void formatsFloatingGeometryLikeOpenJdk() {
        assertEquals("Point2D.Double[1.0, 2.0]", new Point2D.Double(1.0, 2.0).toString());
        assertEquals("Point2D.Float[1.0, 2.0]", new Point2D.Float(1.0f, 2.0f).toString());
        assertEquals("java.awt.geom.Rectangle2D$Double[x=1.0,y=2.0,w=3.0,h=4.0]", new Rectangle2D.Double(1.0, 2.0, 3.0, 4.0).toString());
        assertEquals("java.awt.geom.Rectangle2D$Float[x=1.0,y=2.0,w=3.0,h=4.0]", new Rectangle2D.Float(1.0f, 2.0f, 3.0f, 4.0f).toString());
    }

    public static void transformsShapePathsAndPoints() {
        AffineTransform transform = AffineTransform.getTranslateInstance(5.0, 7.0);
        java.awt.Shape transformed = transform.createTransformedShape(new Rectangle2D.Double(0.0, 0.0, 2.0, 3.0));
        assertEquals(new Rectangle(5, 7, 2, 3), transformed.getBounds());
        assertTrue(transformed.contains(6.0, 8.0));
        assertFalse(transformed.contains(4.0, 8.0));
        double[] coordinates = new double[6];
        PathIterator iterator = transformed.getPathIterator(null);
        assertEquals(PathIterator.SEG_MOVETO, iterator.currentSegment(coordinates));
        assertClose(5.0, coordinates[0]);
        assertClose(7.0, coordinates[1]);
    }

    public static void buildsAndTransformsGeneralPaths() {
        GeneralPath path = new GeneralPath(Path2D.WIND_EVEN_ODD, 1);
        path.moveTo(1.0f, 2.0f);
        path.lineTo(3.0f, 4.0f);
        path.quadTo(5.0f, 6.0f, 7.0f, 8.0f);
        path.curveTo(9.0f, 10.0f, 11.0f, 12.0f, 13.0f, 14.0f);
        path.closePath();
        assertEquals(Path2D.WIND_EVEN_ODD, path.getWindingRule());
        assertTrue(path.getCurrentPoint() instanceof Point2D.Float);
        assertEquals(new Point2D.Float(1.0f, 2.0f), path.getCurrentPoint());
        assertEquals(new Rectangle2D.Float(1.0f, 2.0f, 12.0f, 12.0f), path.getBounds2D());
        int[] expected = {PathIterator.SEG_MOVETO, PathIterator.SEG_LINETO, PathIterator.SEG_QUADTO, PathIterator.SEG_CUBICTO, PathIterator.SEG_CLOSE};
        assertSegmentTypes(expected, path.getPathIterator(null));
        Path2D.Float copy = (Path2D.Float) path.clone();
        assertTrue(copy instanceof GeneralPath);
        copy.transform(AffineTransform.getTranslateInstance(2.0, 3.0));
        assertEquals(new Point2D.Double(3.0, 5.0), copy.getCurrentPoint());
        assertEquals(new Point2D.Float(1.0f, 2.0f), path.getCurrentPoint());
    }

    public static void appendsAndClassifiesPathRegions() {
        Path2D.Double path = new Path2D.Double();
        path.moveTo(0.0, 0.0);
        path.lineTo(10.0, 0.0);
        Path2D.Double continuation = new Path2D.Double();
        continuation.moveTo(10.0, 0.0);
        continuation.lineTo(10.0, 10.0);
        continuation.lineTo(0.0, 10.0);
        continuation.closePath();
        path.append(continuation, true);
        assertSegmentTypes(new int[] {PathIterator.SEG_MOVETO, PathIterator.SEG_LINETO, PathIterator.SEG_LINETO, PathIterator.SEG_LINETO, PathIterator.SEG_CLOSE}, path.getPathIterator(null));
        assertTrue(path.contains(5.0, 5.0));
        assertFalse(path.contains(15.0, 5.0));
        assertTrue(path.intersects(9.0, 9.0, 3.0, 3.0));
        assertTrue(path.contains(2.0, 2.0, 2.0, 2.0));
        assertFalse(path.contains(9.0, 9.0, 3.0, 3.0));
    }

    public static void flattensCurvedSegments() {
        Path2D.Double path = new Path2D.Double();
        path.moveTo(0.0, 0.0);
        path.quadTo(5.0, 10.0, 10.0, 0.0);
        path.curveTo(15.0, -10.0, 20.0, 10.0, 25.0, 0.0);
        FlatteningPathIterator iterator = new FlatteningPathIterator(path.getPathIterator(null), 0.5, 8);
        assertClose(0.5, iterator.getFlatness());
        assertEquals(8, iterator.getRecursionLimit());
        int count = 0;
        double[] values = new double[6];
        while (!iterator.isDone()) {
            int type = iterator.currentSegment(values);
            assertTrue(type == PathIterator.SEG_MOVETO || type == PathIterator.SEG_LINETO || type == PathIterator.SEG_CLOSE);
            iterator.next();
            count++;
        }
        assertTrue(count > 4);
        assertSegmentTypes(new int[] {PathIterator.SEG_MOVETO, PathIterator.SEG_LINETO, PathIterator.SEG_LINETO}, new FlatteningPathIterator(path.getPathIterator(null), 0.0, 0));
    }

    public static void calculatesQuadraticCurves() {
        QuadCurve2D.Double curve = new QuadCurve2D.Double(0.0, 0.0, 5.0, 10.0, 10.0, 0.0);
        assertClose(10.0, curve.getFlatness());
        QuadCurve2D.Double left = new QuadCurve2D.Double();
        QuadCurve2D.Double right = new QuadCurve2D.Double();
        curve.subdivide(left, right);
        assertPoint(0.0, 0.0, left.getP1());
        assertPoint(5.0, 5.0, left.getP2());
        assertPoint(5.0, 5.0, right.getP1());
        assertPoint(10.0, 0.0, right.getP2());
        double[] roots = new double[3];
        int count = QuadCurve2D.solveQuadratic(new double[] {6.0, -5.0, 1.0}, roots);
        assertEquals(2, count);
        sort(roots, count);
        assertClose(2.0, roots[0]);
        assertClose(3.0, roots[1]);
        assertTrue(curve.contains(5.0, 2.0));
        assertFalse(curve.contains(5.0, 6.0));
        assertSegmentTypes(new int[] {PathIterator.SEG_MOVETO, PathIterator.SEG_QUADTO}, curve.getPathIterator(null));
        assertTrue(curve.clone() instanceof QuadCurve2D.Double);
    }

    public static void calculatesCubicCurves() {
        CubicCurve2D.Float curve = new CubicCurve2D.Float(0.0f, 0.0f, 0.0f, 9.0f, 9.0f, 9.0f, 9.0f, 0.0f);
        assertClose(9.0, curve.getFlatness());
        CubicCurve2D.Double left = new CubicCurve2D.Double();
        CubicCurve2D.Double right = new CubicCurve2D.Double();
        new CubicCurve2D.Double(0.0, 0.0, 0.0, 8.0, 8.0, 8.0, 8.0, 0.0).subdivide(left, right);
        assertPoint(4.0, 6.0, left.getP2());
        assertPoint(4.0, 6.0, right.getP1());
        double[] roots = new double[4];
        int count = CubicCurve2D.solveCubic(new double[] {-6.0, 11.0, -6.0, 1.0}, roots);
        assertEquals(3, count);
        sort(roots, count);
        assertClose(1.0, roots[0]);
        assertClose(2.0, roots[1]);
        assertClose(3.0, roots[2]);
        assertTrue(curve.contains(4.5, 2.0));
        assertFalse(curve.contains(4.5, 8.0));
        assertSegmentTypes(new int[] {PathIterator.SEG_MOVETO, PathIterator.SEG_CUBICTO}, curve.getPathIterator(null));
        assertTrue(curve.clone() instanceof CubicCurve2D.Float);
    }

    public static void manipulatesPolygons() {
        Polygon polygon = new Polygon(new int[] {0, 10, 10, 0}, new int[] {0, 0, 10, 10}, 4);
        assertEquals(new Rectangle(0, 0, 10, 10), polygon.getBounds());
        assertTrue(polygon.contains(5, 5));
        assertFalse(polygon.contains(10, 5));
        assertTrue(polygon.intersects(9.0, 9.0, 3.0, 3.0));
        assertTrue(polygon.contains(2.0, 2.0, 2.0, 2.0));
        polygon.translate(-2, 3);
        assertEquals(new Rectangle(-2, 3, 10, 10), polygon.getBoundingBox());
        polygon.addPoint(20, 5);
        assertEquals(new Rectangle(-2, 3, 22, 10), polygon.getBounds());
        assertSegmentTypes(new int[] {PathIterator.SEG_MOVETO, PathIterator.SEG_LINETO, PathIterator.SEG_LINETO, PathIterator.SEG_LINETO, PathIterator.SEG_LINETO, PathIterator.SEG_CLOSE}, polygon.getPathIterator(null));
        polygon.reset();
        assertEquals(new Rectangle(), polygon.getBounds());
        assertTrue(polygon.getPathIterator(null).isDone());
    }

    public static void createsBasicStrokeOutlines() {
        BasicStroke stroke = new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, new float[] {3.0f, 2.0f}, 1.0f);
        assertClose(2.0, stroke.getLineWidth());
        assertEquals(BasicStroke.CAP_BUTT, stroke.getEndCap());
        assertEquals(BasicStroke.JOIN_MITER, stroke.getLineJoin());
        assertArrayEquals(new double[] {3.0, 2.0}, toDouble(stroke.getDashArray()));
        float[] dash = stroke.getDashArray();
        dash[0] = 99.0f;
        assertClose(3.0, stroke.getDashArray()[0]);
        assertEquals(stroke, new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, new float[] {3.0f, 2.0f}, 1.0f));
        BasicStroke solid = new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
        Rectangle2D bounds = solid.createStrokedShape(new Line2D.Double(0.0, 0.0, 10.0, 0.0)).getBounds2D();
        assertClose(0.0, bounds.getX());
        assertClose(-1.0, bounds.getY());
        assertClose(10.0, bounds.getWidth());
        assertClose(2.0, bounds.getHeight());
        for (int direction : new int[] {-1, 1}) {
            Path2D corner = new Path2D.Double();
            corner.moveTo(0, 0);
            corner.lineTo(10, 0);
            corner.lineTo(10, direction * 20);
            java.awt.Shape outline = solid.createStrokedShape(corner);
            for (double x : new double[] {10.25, 10.75}) {
                for (double y : new double[] {0.25, 0.75}) {
                    assertTrue(outline.contains(x, -direction * y));
                }
            }
            assertFalse(outline.contains(11.25, 0));
        }
    }

    public static void evaluatesPrimitiveCurvedShapes() {
        Ellipse2D.Double ellipse = new Ellipse2D.Double(0.0, 0.0, 10.0, 8.0);
        assertTrue(ellipse.contains(5.0, 4.0));
        assertFalse(ellipse.contains(0.0, 0.0));
        assertTrue(ellipse.intersects(8.0, 3.0, 4.0, 2.0));
        assertEquals(new Rectangle2D.Double(0.0, 0.0, 10.0, 8.0), ellipse.getBounds2D());
        assertSegmentTypes(new int[] {PathIterator.SEG_MOVETO, PathIterator.SEG_CUBICTO, PathIterator.SEG_CUBICTO, PathIterator.SEG_CUBICTO, PathIterator.SEG_CUBICTO, PathIterator.SEG_CLOSE}, ellipse.getPathIterator(null));
        RoundRectangle2D.Double rounded = new RoundRectangle2D.Double(0.0, 0.0, 20.0, 10.0, 6.0, 4.0);
        assertTrue(rounded.contains(10.0, 5.0));
        assertFalse(rounded.contains(0.0, 0.0));
        assertTrue(rounded.intersects(18.0, 4.0, 4.0, 2.0));
        assertSegmentTypes(new int[] {PathIterator.SEG_MOVETO, PathIterator.SEG_LINETO, PathIterator.SEG_CUBICTO, PathIterator.SEG_LINETO, PathIterator.SEG_CUBICTO, PathIterator.SEG_LINETO, PathIterator.SEG_CUBICTO, PathIterator.SEG_LINETO, PathIterator.SEG_CUBICTO, PathIterator.SEG_CLOSE}, rounded.getPathIterator(null));
        Arc2D.Double arc = new Arc2D.Double(0.0, 0.0, 10.0, 10.0, 0.0, 180.0, Arc2D.PIE);
        assertPoint(10.0, 5.0, arc.getStartPoint());
        assertPoint(0.0, 5.0, arc.getEndPoint());
        assertTrue(arc.containsAngle(90.0));
        assertFalse(arc.containsAngle(270.0));
        assertTrue(arc.contains(5.0, 2.0));
        assertFalse(arc.contains(5.0, 8.0));
        assertSegmentTypes(new int[] {PathIterator.SEG_MOVETO, PathIterator.SEG_CUBICTO, PathIterator.SEG_CUBICTO, PathIterator.SEG_LINETO, PathIterator.SEG_CLOSE}, arc.getPathIterator(null));
    }

    public static void combinesAreas() {
        Area first = new Area(new Rectangle2D.Double(0.0, 0.0, 10.0, 10.0));
        Area second = new Area(new Rectangle2D.Double(5.0, 0.0, 10.0, 10.0));
        Area intersection = (Area) first.clone();
        intersection.intersect(second);
        assertEquals(new Rectangle2D.Double(5.0, 0.0, 5.0, 10.0), intersection.getBounds2D());
        assertTrue(intersection.isRectangular());
        assertTrue(intersection.contains(7.0, 5.0));
        assertFalse(intersection.contains(2.0, 5.0));
        Area subtraction = (Area) first.clone();
        subtraction.subtract(second);
        assertEquals(new Rectangle2D.Double(0.0, 0.0, 5.0, 10.0), subtraction.getBounds2D());
        Area exclusive = (Area) first.clone();
        exclusive.exclusiveOr(second);
        assertTrue(exclusive.contains(2.0, 5.0));
        assertFalse(exclusive.contains(7.0, 5.0));
        assertTrue(exclusive.contains(12.0, 5.0));
        Area union = (Area) first.clone();
        union.add(second);
        assertEquals(new Rectangle2D.Double(0.0, 0.0, 15.0, 10.0), union.getBounds2D());
        assertTrue(union.equals(new Area(new Rectangle2D.Double(0.0, 0.0, 15.0, 10.0))));
        union.transform(AffineTransform.getTranslateInstance(2.0, 3.0));
        assertEquals(new Rectangle2D.Double(2.0, 3.0, 15.0, 10.0), union.getBounds2D());
    }

    public static void rejectsInvalidPathOperations() {
        try {
            new Path2D.Double().lineTo(1.0, 2.0);
            throw new AssertionError("Expected IllegalPathStateException");
        } catch (IllegalPathStateException expected) {
            assertTrue(expected.getMessage().contains("moveto"));
        }
        try {
            new Path2D.Double(7);
            throw new AssertionError("Expected IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            assertTrue(expected.getMessage().contains("winding rule"));
        }
        try {
            new FlatteningPathIterator(new Path2D.Double().getPathIterator(null), -1.0);
            throw new AssertionError("Expected IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            assertTrue(expected.getMessage().contains("flatness"));
        }
    }

    public static void rejectsSingularTransforms() {
        try {
            AffineTransform.getScaleInstance(0.0, 1.0).createInverse();
            throw new AssertionError("Expected NoninvertibleTransformException");
        } catch (NoninvertibleTransformException expected) {
            assertTrue(expected.getMessage().startsWith("Determinant is"));
        }
    }

    private static void assertArrayEquals(double[] expected, double[] actual) {
        assertEquals(expected.length, actual.length);
        for (int index = 0; index < expected.length; index++) {
            assertClose(expected[index], actual[index]);
        }
    }

    private static void assertSegmentTypes(int[] expected, PathIterator iterator) {
        double[] coordinates = new double[6];
        int index = 0;
        while (!iterator.isDone()) {
            assertTrue(index < expected.length);
            assertEquals(expected[index], iterator.currentSegment(coordinates));
            iterator.next();
            index++;
        }
        assertEquals(expected.length, index);
    }

    private static void assertPoint(double expectedX, double expectedY, Point2D actual) {
        assertClose(expectedX, actual.getX());
        assertClose(expectedY, actual.getY());
    }

    private static void sort(double[] values, int length) {
        for (int outer = 0; outer < length - 1; outer++) {
            for (int inner = outer + 1; inner < length; inner++) {
                if (values[inner] < values[outer]) {
                    double value = values[outer];
                    values[outer] = values[inner];
                    values[inner] = value;
                }
            }
        }
    }

    private static double[] toDouble(float[] values) {
        double[] result = new double[values.length];
        for (int index = 0; index < values.length; index++) {
            result[index] = values[index];
        }
        return result;
    }

    private static void assertClose(double expected, double actual) {
        if (Math.abs(expected - actual) > TOLERANCE) {
            throw new AssertionError("Expected " + expected + " but was " + actual);
        }
    }

    private static void assertEquals(int expected, int actual) {
        if (expected != actual) {
            throw new AssertionError("Expected " + expected + " but was " + actual);
        }
    }

    private static void assertEquals(Object expected, Object actual) {
        if (!expected.equals(actual)) {
            throw new AssertionError("Expected " + expected + " but was " + actual);
        }
    }

    private static void assertTrue(boolean condition) {
        if (!condition) {
            throw new AssertionError("Expected true");
        }
    }

    private static void assertFalse(boolean condition) {
        if (condition) {
            throw new AssertionError("Expected false");
        }
    }
}
