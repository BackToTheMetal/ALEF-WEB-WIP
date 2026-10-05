package com.webawt.portable;

import java.awt.Image;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import org.junit.Test;

public final class BufferedImageJ2clTest {
    @Test
    public void fillsOpenCurvesHolesAndInvalidatesPreparedClips() {
        BufferedImage image = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        java.awt.geom.Path2D path = new java.awt.geom.Path2D.Double(java.awt.geom.Path2D.WIND_EVEN_ODD);
        path.append(new java.awt.Rectangle(0, 0, 50, 50), false);
        path.append(new java.awt.Rectangle(10, 10, 20, 20), false);
        g.setColor(Color.BLUE);
        g.fill(path);
        assertEquals(Color.BLUE.getRGB(), image.getRGB(5, 5));
        assertEquals(0, image.getRGB(20, 20));
        path.reset();
        path.moveTo(0, 0); path.curveTo(60, 0, 60, 60, 0, 60);
        g.setClip(new java.awt.Rectangle(0, 0, 10, 60));
        g.setColor(Color.GREEN); g.fill(path);
        assertEquals(Color.GREEN.getRGB(), image.getRGB(5, 30));
        g.setClip(new java.awt.Rectangle(20, 0, 30, 60));
        g.fill(path);
        assertEquals(Color.GREEN.getRGB(), image.getRGB(30, 30));
        g.setClip(null);
        g.setColor(Color.YELLOW); g.fillRect(60, 60, 4, 4);
        assertEquals(Color.YELLOW.getRGB(), image.getRGB(62, 62));
        g.dispose();
    }

    @Test
    public void storesArgbPixelsAcrossRepresentativeLayouts() {
        int[] types = {BufferedImage.TYPE_INT_ARGB, BufferedImage.TYPE_4BYTE_ABGR, BufferedImage.TYPE_USHORT_565_RGB, BufferedImage.TYPE_BYTE_GRAY, BufferedImage.TYPE_BYTE_INDEXED};
        for (int type : types) {
            BufferedImage image = new BufferedImage(3, 2, type);
            image.setRGB(1, 1, 0x804080c0);
            int actual = image.getRGB(1, 1);
            if (type == BufferedImage.TYPE_INT_ARGB || type == BufferedImage.TYPE_4BYTE_ABGR) {
                assertEquals(0x804080c0, actual);
            } else {
                assertEquals(255, actual >>> 24);
            }
        }
    }

    @Test
    public void sharesSubimageStorage() {
        BufferedImage image = new BufferedImage(4, 4, BufferedImage.TYPE_INT_ARGB);
        BufferedImage child = image.getSubimage(1, 1, 2, 2);
        child.setRGB(0, 0, 0xff123456);
        assertEquals(0xff123456, image.getRGB(1, 1));
    }

    @Test
    public void scalesBufferedImagesSynchronously() {
        BufferedImage image = new BufferedImage(2, 1, BufferedImage.TYPE_INT_ARGB);
        image.setRGB(0, 0, 0xffff0000);
        image.setRGB(1, 0, 0xff0000ff);
        Image scaled = image.getScaledInstance(4, 2, Image.SCALE_FAST);
        assertEquals(4, scaled.getWidth(null));
        assertEquals(2, scaled.getHeight(null));
        assertEquals(0xffff0000, ((BufferedImage) scaled).getRGB(0, 0));
        assertEquals(0xff0000ff, ((BufferedImage) scaled).getRGB(3, 1));
    }

    @Test
    public void paintsIntoRasterWithTransformClipAndComposite() {
        BufferedImage image = new BufferedImage(8, 8, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        graphics.translate(2, 1);
        graphics.clipRect(0, 0, 3, 3);
        graphics.setColor(Color.RED);
        graphics.fillRect(0, 0, 5, 5);
        graphics.setComposite(AlphaComposite.Clear);
        graphics.fillRect(1, 1, 1, 1);
        graphics.dispose();
        assertEquals(0xffff0000, image.getRGB(2, 1));
        assertEquals(0, image.getRGB(3, 2));
        assertEquals(0, image.getRGB(6, 1));
    }

    private static void assertEquals(int expected, int actual) {
        if (expected != actual) {
            throw new AssertionError("Expected " + expected + " but got " + actual);
        }
    }
}
