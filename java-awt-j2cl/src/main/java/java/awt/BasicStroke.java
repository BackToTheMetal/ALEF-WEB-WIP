package java.awt;

import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.util.ArrayList;
import java.util.List;

public class BasicStroke implements Stroke {
    public static final int JOIN_MITER = 0;
    public static final int JOIN_ROUND = 1;
    public static final int JOIN_BEVEL = 2;
    public static final int CAP_BUTT = 0;
    public static final int CAP_ROUND = 1;
    public static final int CAP_SQUARE = 2;
    private static final float DEFAULT_WIDTH = 1.0f;
    private static final float DEFAULT_MITER_LIMIT = 10.0f;
    private static final int ROUND_SEGMENTS = 32;
    private static final double GEOMETRY_EPSILON = 0.000000001;
    private static final double MINIMUM_FLATNESS = 0.0001;
    private final float width;
    private final int cap;
    private final int join;
    private final float miterLimit;
    private final float[] dash;
    private final float dashPhase;

    public BasicStroke(float width, int cap, int join, float miterLimit, float[] dash, float dashPhase) {
        validate(width, cap, join, miterLimit, dash, dashPhase);
        this.width = width;
        this.cap = cap;
        this.join = join;
        this.miterLimit = miterLimit;
        this.dash = copy(dash);
        this.dashPhase = dashPhase;
    }

    public BasicStroke(float width, int cap, int join, float miterLimit) {
        this(width, cap, join, miterLimit, null, 0.0f);
    }

    public BasicStroke(float width, int cap, int join) {
        this(width, cap, join, DEFAULT_MITER_LIMIT, null, 0.0f);
    }

    public BasicStroke(float width) {
        this(width, CAP_SQUARE, JOIN_MITER, DEFAULT_MITER_LIMIT, null, 0.0f);
    }

    public BasicStroke() {
        this(DEFAULT_WIDTH, CAP_SQUARE, JOIN_MITER, DEFAULT_MITER_LIMIT, null, 0.0f);
    }

    @Override
    public Shape createStrokedShape(Shape shape) {
        GeneralPath result = new GeneralPath(PathIterator.WIND_NON_ZERO);
        PathIterator iterator = shape.getPathIterator(null, flatness());
        List<Point> points = new ArrayList<>();
        double[] coordinates = new double[6];
        while (!iterator.isDone()) {
            int type = iterator.currentSegment(coordinates);
            if (type == PathIterator.SEG_MOVETO) {
                strokeSubpath(result, points, false);
                points.clear();
                addPoint(points, coordinates[0], coordinates[1]);
            } else if (type == PathIterator.SEG_LINETO) {
                addPoint(points, coordinates[0], coordinates[1]);
            } else if (type == PathIterator.SEG_CLOSE) {
                strokeSubpath(result, points, true);
                points.clear();
            }
            iterator.next();
        }
        strokeSubpath(result, points, false);
        return result;
    }

    public float getLineWidth() {
        return width;
    }

    public int getEndCap() {
        return cap;
    }

    public int getLineJoin() {
        return join;
    }

    public float getMiterLimit() {
        return miterLimit;
    }

    public float[] getDashArray() {
        return copy(dash);
    }

    public float getDashPhase() {
        return dashPhase;
    }

    @Override
    public int hashCode() {
        int hash = Float.floatToIntBits(width);
        hash = 31 * hash + join;
        hash = 31 * hash + cap;
        hash = 31 * hash + Float.floatToIntBits(miterLimit);
        if (dash != null) {
            hash = 31 * hash + Float.floatToIntBits(dashPhase);
            for (float value : dash) {
                hash = 31 * hash + Float.floatToIntBits(value);
            }
        }
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof BasicStroke)) {
            return false;
        }
        BasicStroke stroke = (BasicStroke) object;
        return width == stroke.width && cap == stroke.cap && join == stroke.join && miterLimit == stroke.miterLimit && dashPhase == stroke.dashPhase && equalDash(dash, stroke.dash);
    }

    private double flatness() {
        return Math.max(MINIMUM_FLATNESS, width / 64.0);
    }

    private void strokeSubpath(GeneralPath result, List<Point> points, boolean closed) {
        if (closed && points.size() > 1 && same(points.get(0), points.get(points.size() - 1))) {
            points.remove(points.size() - 1);
        }
        if (points.size() < 2 || width == 0.0f) {
            return;
        }
        if (dash == null) {
            appendRun(result, points, closed);
            return;
        }
        List<List<Point>> runs = dash(points, closed);
        for (List<Point> run : runs) {
            boolean runClosed = run.size() > 2 && same(run.get(0), run.get(run.size() - 1));
            if (runClosed) {
                run.remove(run.size() - 1);
            }
            appendRun(result, run, runClosed);
        }
    }

    private List<List<Point>> dash(List<Point> points, boolean closed) {
        List<List<Point>> runs = new ArrayList<>();
        DashState state = createDashState();
        List<Point> run = null;
        int segmentCount = closed ? points.size() : points.size() - 1;
        for (int index = 0; index < segmentCount; index++) {
            Point start = points.get(index);
            Point end = points.get((index + 1) % points.size());
            double length = distance(start, end);
            if (length == 0.0) {
                continue;
            }
            double consumed = 0.0;
            while (consumed < length) {
                double part = Math.min(length - consumed, state.remaining);
                Point partStart = interpolate(start, end, consumed / length);
                Point partEnd = interpolate(start, end, (consumed + part) / length);
                if (state.painted) {
                    if (run == null) {
                        run = new ArrayList<>();
                        addPoint(run, partStart.x, partStart.y);
                    }
                    addPoint(run, partEnd.x, partEnd.y);
                }
                consumed += part;
                state.remaining -= part;
                if (state.remaining <= GEOMETRY_EPSILON) {
                    boolean wasPainted = state.painted;
                    state.advance(dash);
                    if (wasPainted && !state.painted && run != null) {
                        runs.add(run);
                        run = null;
                    }
                }
            }
        }
        if (run != null) {
            runs.add(run);
        }
        mergeClosedRuns(runs, points, closed);
        return runs;
    }

    private DashState createDashState() {
        double cycle = 0.0;
        for (float value : dash) {
            cycle += value;
        }
        if ((dash.length & 1) != 0) {
            cycle *= 2.0;
        }
        double phase = dashPhase % cycle;
        DashState state = new DashState(0, true, dash[0]);
        while (phase >= state.remaining && state.remaining > 0.0) {
            phase -= state.remaining;
            state.advance(dash);
        }
        state.remaining -= phase;
        if (state.remaining <= GEOMETRY_EPSILON) {
            state.advance(dash);
        }
        return state;
    }

    private static void mergeClosedRuns(List<List<Point>> runs, List<Point> source, boolean closed) {
        if (!closed || runs.size() < 2) {
            return;
        }
        Point start = source.get(0);
        List<Point> first = runs.get(0);
        List<Point> last = runs.get(runs.size() - 1);
        if (!same(first.get(0), start) || !same(last.get(last.size() - 1), start)) {
            return;
        }
        List<Point> merged = new ArrayList<>(last);
        for (int index = 1; index < first.size(); index++) {
            merged.add(first.get(index));
        }
        runs.set(0, merged);
        runs.remove(runs.size() - 1);
    }

    private void appendRun(GeneralPath result, List<Point> points, boolean closed) {
        int segmentCount = closed ? points.size() : points.size() - 1;
        for (int index = 0; index < segmentCount; index++) {
            Point start = points.get(index);
            Point end = points.get((index + 1) % points.size());
            boolean extendStart = !closed && index == 0 && cap == CAP_SQUARE;
            boolean extendEnd = !closed && index == segmentCount - 1 && cap == CAP_SQUARE;
            appendSegment(result, start, end, extendStart, extendEnd);
        }
        int firstJoin = closed ? 0 : 1;
        int lastJoin = closed ? points.size() : points.size() - 1;
        for (int index = firstJoin; index < lastJoin; index++) {
            Point previous = points.get((index + points.size() - 1) % points.size());
            Point current = points.get(index % points.size());
            Point next = points.get((index + 1) % points.size());
            appendJoin(result, previous, current, next);
        }
        if (!closed && cap == CAP_ROUND) {
            appendCircle(result, points.get(0));
            appendCircle(result, points.get(points.size() - 1));
        }
    }

    private void appendSegment(GeneralPath result, Point originalStart, Point originalEnd, boolean extendStart, boolean extendEnd) {
        double length = distance(originalStart, originalEnd);
        if (length == 0.0) {
            return;
        }
        double directionX = (originalEnd.x - originalStart.x) / length;
        double directionY = (originalEnd.y - originalStart.y) / length;
        double halfWidth = width / 2.0;
        Point start = new Point(originalStart.x - (extendStart ? directionX * halfWidth : 0.0), originalStart.y - (extendStart ? directionY * halfWidth : 0.0));
        Point end = new Point(originalEnd.x + (extendEnd ? directionX * halfWidth : 0.0), originalEnd.y + (extendEnd ? directionY * halfWidth : 0.0));
        double normalX = -directionY * halfWidth;
        double normalY = directionX * halfWidth;
        appendPolygon(result, new Point[] {
            new Point(start.x + normalX, start.y + normalY),
            new Point(start.x - normalX, start.y - normalY),
            new Point(end.x - normalX, end.y - normalY),
            new Point(end.x + normalX, end.y + normalY)
        });
    }

    private void appendJoin(GeneralPath result, Point previous, Point current, Point next) {
        Direction incoming = direction(previous, current);
        Direction outgoing = direction(current, next);
        if (incoming == null || outgoing == null) {
            return;
        }
        double cross = incoming.x * outgoing.y - incoming.y * outgoing.x;
        if (Math.abs(cross) <= GEOMETRY_EPSILON) {
            return;
        }
        if (join == JOIN_ROUND) {
            appendCircle(result, current);
            return;
        }
        double side = cross > 0.0 ? -1.0 : 1.0;
        double halfWidth = width / 2.0;
        Point outerIncoming = new Point(current.x - incoming.y * halfWidth * side, current.y + incoming.x * halfWidth * side);
        Point outerOutgoing = new Point(current.x - outgoing.y * halfWidth * side, current.y + outgoing.x * halfWidth * side);
        if (join == JOIN_MITER) {
            Point intersection = lineIntersection(outerIncoming, incoming, outerOutgoing, outgoing);
            if (intersection != null && distance(current, intersection) <= miterLimit * halfWidth) {
                appendPolygon(result, new Point[] {outerIncoming, intersection, outerOutgoing, current});
                return;
            }
        }
        appendPolygon(result, new Point[] {outerIncoming, current, outerOutgoing});
    }

    private void appendCircle(GeneralPath result, Point center) {
        Point[] polygon = new Point[ROUND_SEGMENTS];
        double radius = width / 2.0;
        for (int index = 0; index < ROUND_SEGMENTS; index++) {
            double angle = 2.0 * Math.PI * index / ROUND_SEGMENTS;
            polygon[index] = new Point(center.x + Math.cos(angle) * radius, center.y + Math.sin(angle) * radius);
        }
        appendPolygon(result, polygon);
    }

    private static void appendPolygon(GeneralPath result, Point[] points) {
        if (points.length < 3) {
            return;
        }
        boolean reverse = signedArea(points) < 0.0;
        int first = reverse ? points.length - 1 : 0;
        result.moveTo(points[first].x, points[first].y);
        for (int offset = 1; offset < points.length; offset++) {
            int index = reverse ? points.length - 1 - offset : offset;
            result.lineTo(points[index].x, points[index].y);
        }
        result.closePath();
    }

    private static double signedArea(Point[] points) {
        double area = 0.0;
        for (int index = 0; index < points.length; index++) {
            Point current = points[index];
            Point next = points[(index + 1) % points.length];
            area += current.x * next.y - next.x * current.y;
        }
        return area;
    }

    private static Point lineIntersection(Point first, Direction firstDirection, Point second, Direction secondDirection) {
        double denominator = firstDirection.x * secondDirection.y - firstDirection.y * secondDirection.x;
        if (Math.abs(denominator) <= GEOMETRY_EPSILON) {
            return null;
        }
        double deltaX = second.x - first.x;
        double deltaY = second.y - first.y;
        double factor = (deltaX * secondDirection.y - deltaY * secondDirection.x) / denominator;
        return new Point(first.x + factor * firstDirection.x, first.y + factor * firstDirection.y);
    }

    private static Direction direction(Point start, Point end) {
        double length = distance(start, end);
        if (length == 0.0) {
            return null;
        }
        return new Direction((end.x - start.x) / length, (end.y - start.y) / length);
    }

    private static Point interpolate(Point start, Point end, double fraction) {
        return new Point(start.x + (end.x - start.x) * fraction, start.y + (end.y - start.y) * fraction);
    }

    private static double distance(Point first, Point second) {
        return Math.hypot(second.x - first.x, second.y - first.y);
    }

    private static void addPoint(List<Point> points, double x, double y) {
        Point point = new Point(x, y);
        if (points.isEmpty() || !same(points.get(points.size() - 1), point)) {
            points.add(point);
        }
    }

    private static boolean same(Point first, Point second) {
        return first.x == second.x && first.y == second.y;
    }

    private static void validate(float width, int cap, int join, float miterLimit, float[] dash, float dashPhase) {
        if (width < 0.0f) {
            throw new IllegalArgumentException("negative width");
        }
        if (cap < CAP_BUTT || cap > CAP_SQUARE) {
            throw new IllegalArgumentException("illegal end cap value");
        }
        if (join < JOIN_MITER || join > JOIN_BEVEL) {
            throw new IllegalArgumentException("illegal line join value");
        }
        if (join == JOIN_MITER && miterLimit < 1.0f) {
            throw new IllegalArgumentException("miter limit less than one");
        }
        validateDash(dash, dashPhase);
    }

    private static void validateDash(float[] dash, float dashPhase) {
        if (dash == null) {
            return;
        }
        if (dashPhase < 0.0f) {
            throw new IllegalArgumentException("negative dash phase");
        }
        boolean positive = false;
        for (float value : dash) {
            if (value < 0.0f) {
                throw new IllegalArgumentException("negative dash length");
            }
            positive |= value > 0.0f;
        }
        if (!positive) {
            throw new IllegalArgumentException("dash lengths all zero");
        }
    }

    private static float[] copy(float[] values) {
        if (values == null) {
            return null;
        }
        float[] result = new float[values.length];
        System.arraycopy(values, 0, result, 0, values.length);
        return result;
    }

    private static boolean equalDash(float[] first, float[] second) {
        if (first == null || second == null) {
            return first == second;
        }
        if (first.length != second.length) {
            return false;
        }
        for (int index = 0; index < first.length; index++) {
            if (first[index] != second[index]) {
                return false;
            }
        }
        return true;
    }

    private static final class Point {
        private final double x;
        private final double y;

        private Point(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }

    private static final class Direction {
        private final double x;
        private final double y;

        private Direction(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }

    private static final class DashState {
        private int index;
        private boolean painted;
        private double remaining;

        private DashState(int index, boolean painted, double remaining) {
            this.index = index;
            this.painted = painted;
            this.remaining = remaining;
        }

        private void advance(float[] dash) {
            do {
                index = (index + 1) % dash.length;
                painted = !painted;
                remaining = dash[index];
            } while (remaining == 0.0);
        }
    }
}
