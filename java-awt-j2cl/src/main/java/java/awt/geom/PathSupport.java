package java.awt.geom;

final class PathSupport {
    private static final double HIT_TEST_FLATNESS = 0.0000001;
    private static final int HIT_TEST_LIMIT = 16;

    private PathSupport() {
    }

    static boolean contains(PathIterator source, double x, double y) {
        if (Double.isNaN(x) || Double.isNaN(y)) {
            return false;
        }
        return new FlattenedEdges(source).contains(x, y);
    }

    static boolean contains(PathIterator source, double x, double y, double width, double height) {
        if (width <= 0.0 || height <= 0.0) {
            return false;
        }
        FlattenedEdges edges = new FlattenedEdges(source);
        if (!edges.contains(x, y) || !edges.contains(x + width, y) || !edges.contains(x, y + height) || !edges.contains(x + width, y + height)) {
            return false;
        }
        return !edges.hasPointInside(x, y, width, height) && !edges.intersectsBoundary(x, y, width, height);
    }

    static boolean intersects(PathIterator source, double x, double y, double width, double height) {
        if (width <= 0.0 || height <= 0.0) {
            return false;
        }
        FlattenedEdges edges = new FlattenedEdges(source);
        return edges.contains(x, y) || edges.contains(x + width, y) || edges.contains(x, y + height) || edges.contains(x + width, y + height) || edges.hasPointInside(x, y, width, height) || edges.intersectsBoundary(x, y, width, height);
    }

    private static int crossing(double x1, double y1, double x2, double y2, double x, double y) {
        if (y1 <= y) {
            if (y2 > y && isLeft(x1, y1, x2, y2, x, y) > 0.0) {
                return 1;
            }
        } else if (y2 <= y && isLeft(x1, y1, x2, y2, x, y) < 0.0) {
            return -1;
        }
        return 0;
    }

    private static double isLeft(double x1, double y1, double x2, double y2, double x, double y) {
        return (x2 - x1) * (y - y1) - (x - x1) * (y2 - y1);
    }

    static final class FlattenedEdges {
        private double[] edges = new double[64];
        private int edgeValueCount;
        private final int windingRule;

        FlattenedEdges(PathIterator source) {
            windingRule = source.getWindingRule();
            read(source);
        }

        private void read(PathIterator source) {
            FlatteningPathIterator iterator = new FlatteningPathIterator(source, HIT_TEST_FLATNESS, HIT_TEST_LIMIT);
            double[] values = new double[2];
            double moveX = 0.0;
            double moveY = 0.0;
            double currentX = 0.0;
            double currentY = 0.0;
            boolean hasSubpath = false;
            while (!iterator.isDone()) {
                int type = iterator.currentSegment(values);
                if (type == PathIterator.SEG_MOVETO) {
                    if (hasSubpath) {
                        add(currentX, currentY, moveX, moveY);
                    }
                    moveX = values[0];
                    moveY = values[1];
                    currentX = moveX;
                    currentY = moveY;
                    hasSubpath = true;
                } else if (type == PathIterator.SEG_LINETO) {
                    add(currentX, currentY, values[0], values[1]);
                    currentX = values[0];
                    currentY = values[1];
                } else if (hasSubpath) {
                    add(currentX, currentY, moveX, moveY);
                    currentX = moveX;
                    currentY = moveY;
                }
                iterator.next();
            }
            if (hasSubpath) {
                add(currentX, currentY, moveX, moveY);
            }
        }

        boolean contains(double x, double y) {
            int crossings = 0;
            for (int index = 0; index < edgeValueCount; index += 4) {
                crossings += crossing(edges[index], edges[index + 1], edges[index + 2], edges[index + 3], x, y);
            }
            return windingRule == PathIterator.WIND_NON_ZERO ? crossings != 0 : (crossings & 1) != 0;
        }

        private boolean hasPointInside(double x, double y, double width, double height) {
            for (int index = 0; index < edgeValueCount; index += 4) {
                double pointX = edges[index];
                double pointY = edges[index + 1];
                if (pointX > x && pointX < x + width && pointY > y && pointY < y + height) {
                    return true;
                }
            }
            return false;
        }

        private boolean intersectsBoundary(double x, double y, double width, double height) {
            for (int index = 0; index < edgeValueCount; index += 4) {
                double x1 = edges[index];
                double y1 = edges[index + 1];
                double x2 = edges[index + 2];
                double y2 = edges[index + 3];
                if (Line2D.linesIntersect(x1, y1, x2, y2, x, y, x + width, y) || Line2D.linesIntersect(x1, y1, x2, y2, x + width, y, x + width, y + height) || Line2D.linesIntersect(x1, y1, x2, y2, x + width, y + height, x, y + height) || Line2D.linesIntersect(x1, y1, x2, y2, x, y + height, x, y)) {
                    return true;
                }
            }
            return false;
        }

        private void add(double x1, double y1, double x2, double y2) {
            if (x1 == x2 && y1 == y2) {
                return;
            }
            if (edgeValueCount + 4 > edges.length) {
                double[] expanded = new double[edges.length * 2];
                System.arraycopy(edges, 0, expanded, 0, edgeValueCount);
                edges = expanded;
            }
            edges[edgeValueCount++] = x1;
            edges[edgeValueCount++] = y1;
            edges[edgeValueCount++] = x2;
            edges[edgeValueCount++] = y2;
        }
    }
}
