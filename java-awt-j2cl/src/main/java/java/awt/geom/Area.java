package java.awt.geom;

import java.awt.Rectangle;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * A constructive geometry shape. Boolean operations preserve polygon edges and approximate curves by adaptive subdivision with an absolute flatness of {@value #BOOLEAN_FLATNESS} and at most {@value #BOOLEAN_RECURSION_LIMIT} subdivision levels.
 */
public class Area implements Shape, Cloneable {
    private static final double BOOLEAN_FLATNESS = 0.001;
    private static final int BOOLEAN_RECURSION_LIMIT = 16;
    private static final double COORDINATE_EPSILON = 0.0000000001;
    private Path2D.Double path;
    private boolean singular;

    public Area() {
        path = new Path2D.Double();
        singular = true;
    }

    public Area(Shape shape) {
        path = new Path2D.Double(shape);
        singular = countSubpaths(path) <= 1;
    }

    public void add(Area area) {
        if (area.isEmpty()) {
            return;
        }
        if (isEmpty()) {
            assign(area);
            return;
        }
        combine(area, Operation.UNION);
    }

    public void subtract(Area area) {
        if (isEmpty() || area.isEmpty()) {
            return;
        }
        combine(area, Operation.SUBTRACTION);
    }

    public void intersect(Area area) {
        if (isEmpty() || area.isEmpty()) {
            reset();
            return;
        }
        combine(area, Operation.INTERSECTION);
    }

    public void exclusiveOr(Area area) {
        if (area.isEmpty()) {
            return;
        }
        if (isEmpty()) {
            assign(area);
            return;
        }
        combine(area, Operation.EXCLUSIVE_OR);
    }

    public void reset() {
        path.reset();
        singular = true;
    }

    public boolean isEmpty() {
        return !hasInterior(path);
    }

    public boolean isPolygonal() {
        PathIterator iterator = path.getPathIterator(null);
        double[] coordinates = new double[6];
        while (!iterator.isDone()) {
            int type = iterator.currentSegment(coordinates);
            if (type == PathIterator.SEG_QUADTO || type == PathIterator.SEG_CUBICTO) {
                return false;
            }
            iterator.next();
        }
        return true;
    }

    public boolean isRectangular() {
        if (isEmpty()) {
            return true;
        }
        Rectangle2D bounds = getBounds2D();
        List<Edge> edges = readEdges(path);
        List<java.lang.Double> coordinates = collectXCoordinates(edges, Collections.<Edge>emptyList());
        PathSupport.FlattenedEdges hitEdges = new PathSupport.FlattenedEdges(path.getPathIterator(null));
        if (coordinates.size() < 2) {
            return false;
        }
        for (int index = 0; index < coordinates.size() - 1; index++) {
            double left = coordinates.get(index);
            double right = coordinates.get(index + 1);
            if (right <= left) {
                continue;
            }
            double middle = left + (right - left) / 2.0;
            List<Crossing> crossings = crossingsAt(edges, middle);
            int intervalCount = 0;
            for (int crossingIndex = 0; crossingIndex < crossings.size() - 1; crossingIndex++) {
                double low = crossings.get(crossingIndex).y;
                double high = crossings.get(crossingIndex + 1).y;
                if (high > low && hitEdges.contains(middle, low + (high - low) / 2.0)) {
                    intervalCount++;
                    if (!near(low, bounds.getMinY()) || !near(high, bounds.getMaxY())) {
                        return false;
                    }
                }
            }
            if (intervalCount != 1) {
                return false;
            }
        }
        return true;
    }

    public boolean isSingular() {
        return isEmpty() || singular;
    }

    @Override
    public Rectangle2D getBounds2D() {
        return path.getBounds2D();
    }

    @Override
    public Rectangle getBounds() {
        return path.getBounds();
    }

    public Object clone() {
        Area copy = new Area();
        copy.path = (Path2D.Double) path.clone();
        copy.singular = singular;
        return copy;
    }

    public boolean equals(Area area) {
        if (area == this) {
            return true;
        }
        if (area == null) {
            return false;
        }
        Area difference = (Area) clone();
        difference.exclusiveOr(area);
        return difference.isEmpty();
    }

    public void transform(AffineTransform transform) {
        if (transform == null) {
            throw new NullPointerException("transform must not be null");
        }
        path.transform(transform);
    }

    public Area createTransformedArea(AffineTransform transform) {
        Area transformed = (Area) clone();
        transformed.transform(transform);
        return transformed;
    }

    @Override
    public boolean contains(double x, double y) {
        return path.contains(x, y);
    }

    @Override
    public boolean contains(Point2D point) {
        return contains(point.getX(), point.getY());
    }

    @Override
    public boolean contains(double x, double y, double width, double height) {
        if (width <= 0.0 || height <= 0.0) {
            return false;
        }
        Area remainder = new Area(new Rectangle2D.Double(x, y, width, height));
        remainder.subtract(this);
        return remainder.isEmpty();
    }

    @Override
    public boolean contains(Rectangle2D rectangle) {
        return contains(rectangle.getX(), rectangle.getY(), rectangle.getWidth(), rectangle.getHeight());
    }

    @Override
    public boolean intersects(double x, double y, double width, double height) {
        return path.intersects(x, y, width, height);
    }

    @Override
    public boolean intersects(Rectangle2D rectangle) {
        return intersects(rectangle.getX(), rectangle.getY(), rectangle.getWidth(), rectangle.getHeight());
    }

    @Override
    public PathIterator getPathIterator(AffineTransform transform) {
        return path.getPathIterator(transform);
    }

    @Override
    public PathIterator getPathIterator(AffineTransform transform, double flatness) {
        return path.getPathIterator(transform, flatness);
    }

    private void combine(Area area, Operation operation) {
        List<Edge> firstEdges = readEdges(path);
        List<Edge> secondEdges = readEdges(area.path);
        PathSupport.FlattenedEdges firstHitEdges = new PathSupport.FlattenedEdges(path.getPathIterator(null));
        PathSupport.FlattenedEdges secondHitEdges = new PathSupport.FlattenedEdges(area.path.getPathIterator(null));
        List<java.lang.Double> xCoordinates = collectXCoordinates(firstEdges, secondEdges);
        Path2D.Double result = new Path2D.Double(Path2D.WIND_NON_ZERO);
        List<Cell> cells = new ArrayList<Cell>();
        boolean splitInterval = false;
        for (int index = 0; index < xCoordinates.size() - 1; index++) {
            double left = xCoordinates.get(index);
            double right = xCoordinates.get(index + 1);
            if (right <= left) {
                continue;
            }
            double middle = left + (right - left) / 2.0;
            List<Crossing> crossings = crossingsAt(firstEdges, secondEdges, middle);
            int intervalsInSlab = 0;
            for (int crossingIndex = 0; crossingIndex < crossings.size() - 1; crossingIndex++) {
                Crossing lower = crossings.get(crossingIndex);
                Crossing upper = crossings.get(crossingIndex + 1);
                if (upper.y <= lower.y) {
                    continue;
                }
                double sampleY = lower.y + (upper.y - lower.y) / 2.0;
                boolean inFirst = firstHitEdges.contains(middle, sampleY);
                boolean inSecond = secondHitEdges.contains(middle, sampleY);
                if (operation.includes(inFirst, inSecond)) {
                    addCell(result, cells, left, right, lower.edge, upper.edge);
                    intervalsInSlab++;
                }
            }
            splitInterval |= intervalsInSlab > 1;
        }
        path = result;
        singular = !splitInterval && countComponents(cells) <= 1;
    }

    private void assign(Area area) {
        path = (Path2D.Double) area.path.clone();
        singular = area.singular;
    }

    private static void addCell(Path2D.Double result, List<Cell> cells, double left, double right, Edge lower, Edge upper) {
        double lowerLeft = lower.yAt(left);
        double lowerRight = lower.yAt(right);
        double upperLeft = upper.yAt(left);
        double upperRight = upper.yAt(right);
        result.moveTo(left, lowerLeft);
        result.lineTo(right, lowerRight);
        result.lineTo(right, upperRight);
        result.lineTo(left, upperLeft);
        result.closePath();
        cells.add(new Cell(left, right, lowerLeft, lowerRight, upperLeft, upperRight));
    }

    private static List<Edge> readEdges(Path2D.Double source) {
        List<Edge> edges = new ArrayList<Edge>();
        FlatteningPathIterator iterator = new FlatteningPathIterator(source.getPathIterator(null), BOOLEAN_FLATNESS, BOOLEAN_RECURSION_LIMIT);
        double[] coordinates = new double[2];
        double moveX = 0.0;
        double moveY = 0.0;
        double currentX = 0.0;
        double currentY = 0.0;
        boolean hasSubpath = false;
        while (!iterator.isDone()) {
            int type = iterator.currentSegment(coordinates);
            if (type == PathIterator.SEG_MOVETO) {
                if (hasSubpath) {
                    addEdge(edges, currentX, currentY, moveX, moveY);
                }
                moveX = coordinates[0];
                moveY = coordinates[1];
                currentX = moveX;
                currentY = moveY;
                hasSubpath = true;
            } else if (type == PathIterator.SEG_LINETO) {
                addEdge(edges, currentX, currentY, coordinates[0], coordinates[1]);
                currentX = coordinates[0];
                currentY = coordinates[1];
            } else if (hasSubpath) {
                addEdge(edges, currentX, currentY, moveX, moveY);
                currentX = moveX;
                currentY = moveY;
            }
            iterator.next();
        }
        if (hasSubpath) {
            addEdge(edges, currentX, currentY, moveX, moveY);
        }
        return edges;
    }

    private static void addEdge(List<Edge> edges, double x1, double y1, double x2, double y2) {
        if (x1 != x2 || y1 != y2) {
            edges.add(new Edge(x1, y1, x2, y2));
        }
    }

    private static List<java.lang.Double> collectXCoordinates(List<Edge> first, List<Edge> second) {
        List<Edge> all = new ArrayList<Edge>(first.size() + second.size());
        all.addAll(first);
        all.addAll(second);
        List<java.lang.Double> coordinates = new ArrayList<java.lang.Double>();
        for (Edge edge : all) {
            addFinite(coordinates, edge.x1);
            addFinite(coordinates, edge.x2);
        }
        for (int firstIndex = 0; firstIndex < all.size(); firstIndex++) {
            for (int secondIndex = firstIndex + 1; secondIndex < all.size(); secondIndex++) {
                addIntersectionX(coordinates, all.get(firstIndex), all.get(secondIndex));
            }
        }
        Collections.sort(coordinates);
        List<java.lang.Double> unique = new ArrayList<java.lang.Double>();
        for (java.lang.Double coordinate : coordinates) {
            if (unique.isEmpty() || !near(unique.get(unique.size() - 1), coordinate)) {
                unique.add(coordinate);
            }
        }
        return unique;
    }

    private static void addIntersectionX(List<java.lang.Double> coordinates, Edge first, Edge second) {
        double firstDx = first.x2 - first.x1;
        double firstDy = first.y2 - first.y1;
        double secondDx = second.x2 - second.x1;
        double secondDy = second.y2 - second.y1;
        double denominator = firstDx * secondDy - firstDy * secondDx;
        if (denominator == 0.0) {
            return;
        }
        double offsetX = second.x1 - first.x1;
        double offsetY = second.y1 - first.y1;
        double firstPosition = (offsetX * secondDy - offsetY * secondDx) / denominator;
        double secondPosition = (offsetX * firstDy - offsetY * firstDx) / denominator;
        if (firstPosition > 0.0 && firstPosition < 1.0 && secondPosition > 0.0 && secondPosition < 1.0) {
            addFinite(coordinates, first.x1 + firstPosition * firstDx);
        }
    }

    private static void addFinite(List<java.lang.Double> coordinates, double value) {
        if (!java.lang.Double.isNaN(value) && !java.lang.Double.isInfinite(value)) {
            coordinates.add(value);
        }
    }

    private static List<Crossing> crossingsAt(List<Edge> edges, double x) {
        return crossingsAt(edges, Collections.<Edge>emptyList(), x);
    }

    private static List<Crossing> crossingsAt(List<Edge> first, List<Edge> second, double x) {
        List<Crossing> crossings = new ArrayList<Crossing>();
        addCrossings(crossings, first, x);
        addCrossings(crossings, second, x);
        Collections.sort(crossings, new Comparator<Crossing>() {
            @Override
            public int compare(Crossing firstCrossing, Crossing secondCrossing) {
                return java.lang.Double.compare(firstCrossing.y, secondCrossing.y);
            }
        });
        List<Crossing> unique = new ArrayList<Crossing>();
        for (Crossing crossing : crossings) {
            if (unique.isEmpty() || !near(unique.get(unique.size() - 1).y, crossing.y)) {
                unique.add(crossing);
            }
        }
        return unique;
    }

    private static void addCrossings(List<Crossing> crossings, List<Edge> edges, double x) {
        for (Edge edge : edges) {
            double minimum = Math.min(edge.x1, edge.x2);
            double maximum = Math.max(edge.x1, edge.x2);
            if (x > minimum && x < maximum) {
                crossings.add(new Crossing(edge, edge.yAt(x)));
            }
        }
    }

    private static int countComponents(List<Cell> cells) {
        if (cells.isEmpty()) {
            return 0;
        }
        int[] parents = new int[cells.size()];
        for (int index = 0; index < parents.length; index++) {
            parents[index] = index;
        }
        for (int first = 0; first < cells.size(); first++) {
            for (int second = first + 1; second < cells.size(); second++) {
                if (cells.get(first).touches(cells.get(second))) {
                    join(parents, first, second);
                }
            }
        }
        int components = 0;
        for (int index = 0; index < parents.length; index++) {
            if (root(parents, index) == index) {
                components++;
            }
        }
        return components;
    }

    private static void join(int[] parents, int first, int second) {
        int firstRoot = root(parents, first);
        int secondRoot = root(parents, second);
        if (firstRoot != secondRoot) {
            parents[secondRoot] = firstRoot;
        }
    }

    private static int root(int[] parents, int index) {
        int current = index;
        while (parents[current] != current) {
            parents[current] = parents[parents[current]];
            current = parents[current];
        }
        return current;
    }

    private static boolean hasInterior(Path2D.Double source) {
        List<Edge> edges = readEdges(source);
        List<java.lang.Double> xCoordinates = collectXCoordinates(edges, Collections.<Edge>emptyList());
        PathSupport.FlattenedEdges hitEdges = new PathSupport.FlattenedEdges(source.getPathIterator(null));
        for (int index = 0; index < xCoordinates.size() - 1; index++) {
            double left = xCoordinates.get(index);
            double right = xCoordinates.get(index + 1);
            if (right <= left) {
                continue;
            }
            double middle = left + (right - left) / 2.0;
            List<Crossing> crossings = crossingsAt(edges, middle);
            for (int crossingIndex = 0; crossingIndex < crossings.size() - 1; crossingIndex++) {
                double low = crossings.get(crossingIndex).y;
                double high = crossings.get(crossingIndex + 1).y;
                if (high > low && hitEdges.contains(middle, low + (high - low) / 2.0)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static int countSubpaths(Path2D.Double source) {
        PathIterator iterator = source.getPathIterator(null);
        double[] coordinates = new double[6];
        int count = 0;
        while (!iterator.isDone()) {
            if (iterator.currentSegment(coordinates) == PathIterator.SEG_MOVETO) {
                count++;
            }
            iterator.next();
        }
        return count;
    }

    private static boolean near(double first, double second) {
        double scale = Math.max(1.0, Math.max(Math.abs(first), Math.abs(second)));
        return Math.abs(first - second) <= COORDINATE_EPSILON * scale;
    }

    private enum Operation {
        UNION {
            @Override
            boolean includes(boolean first, boolean second) {
                return first || second;
            }
        },
        SUBTRACTION {
            @Override
            boolean includes(boolean first, boolean second) {
                return first && !second;
            }
        },
        INTERSECTION {
            @Override
            boolean includes(boolean first, boolean second) {
                return first && second;
            }
        },
        EXCLUSIVE_OR {
            @Override
            boolean includes(boolean first, boolean second) {
                return first != second;
            }
        };

        abstract boolean includes(boolean first, boolean second);
    }

    private static final class Edge {
        private final double x1;
        private final double y1;
        private final double x2;
        private final double y2;

        private Edge(double x1, double y1, double x2, double y2) {
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
        }

        private double yAt(double x) {
            return y1 + (x - x1) * (y2 - y1) / (x2 - x1);
        }
    }

    private static final class Crossing {
        private final Edge edge;
        private final double y;

        private Crossing(Edge edge, double y) {
            this.edge = edge;
            this.y = y;
        }
    }

    private static final class Cell {
        private final double left;
        private final double right;
        private final double lowerLeft;
        private final double lowerRight;
        private final double upperLeft;
        private final double upperRight;

        private Cell(double left, double right, double lowerLeft, double lowerRight, double upperLeft, double upperRight) {
            this.left = left;
            this.right = right;
            this.lowerLeft = lowerLeft;
            this.lowerRight = lowerRight;
            this.upperLeft = upperLeft;
            this.upperRight = upperRight;
        }

        private boolean touches(Cell other) {
            if (near(right, other.left)) {
                return overlaps(lowerRight, upperRight, other.lowerLeft, other.upperLeft);
            }
            if (near(other.right, left)) {
                return overlaps(other.lowerRight, other.upperRight, lowerLeft, upperLeft);
            }
            return false;
        }

        private static boolean overlaps(double firstLow, double firstHigh, double secondLow, double secondHigh) {
            return Math.max(firstLow, secondLow) <= Math.min(firstHigh, secondHigh) + COORDINATE_EPSILON;
        }
    }
}
