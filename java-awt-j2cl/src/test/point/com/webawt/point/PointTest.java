package com.webawt.point;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

import java.awt.Point;
import org.junit.Test;

public final class PointTest {
    @Test
    public void translatesCoordinates() {
        Point point = new Point(5, 7);
        point.translate(3, -2);
        assertEquals(8, point.x);
        assertEquals(5, point.y);
    }

    @Test
    public void returnsDefensiveLocationCopy() {
        Point point = new Point(2, 4);
        Point location = point.getLocation();
        assertEquals(point, location);
        assertNotSame(point, location);
    }

    @Test
    public void calculatesDistanceThroughPoint2D() {
        Point point = new Point(3, 4);
        assertEquals(5.0, point.distance(0, 0), 0.0);
    }

    @Test
    public void clonesWithoutSharingCoordinates() {
        Point point = new Point(9, 6);
        Point clone = (Point) point.clone();
        assertEquals(point, clone);
        assertNotSame(point, clone);
    }
}
