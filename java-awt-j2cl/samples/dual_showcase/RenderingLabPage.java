package org.webawt.samples.dualshowcase;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Canvas;
import java.awt.Choice;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Label;
import java.awt.Panel;
import java.awt.RadialGradientPaint;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.TexturePaint;
import java.awt.Toolkit;
import java.awt.font.GlyphVector;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BaseMultiResolutionImage;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.text.AttributedCharacterIterator;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

final class RenderingLabPage extends Panel {
    private final LabCanvas canvas = new LabCanvas();
    private final Label status = new Label("Choose a rendering example; click shapes to inspect hit testing.");
    private final Choice mode = new Choice();
    private final String png = "89504E470D0A1A0A0000000D49484452000000010000000108060000001F15C4890000000D49444154789C63F8CFC0F01F00050001FF89993D1D0000000049454E44AE426082";
    private Image decoded;
    private BufferedImage textureImage;
    private Area geometryArea;
    private boolean disposed;
    private int generation;
    private int fontGeneration;
    private int wrapWidth = 400;
    private final Choice stateScenario = new Choice();
    private int stateOffset = 20;
    private boolean transparentClear;

    RenderingLabPage(FontLoading fontLoading) {
        super(new BorderLayout(8, 8));
        mode.setName("renderingMode");
        mode.add("Paint & geometry");
        mode.add("Text layout");
        mode.add("Images & buffering");
        mode.add("Graphics state");
        mode.addItemListener(event -> paintNow());
        Panel controls = new Panel(new FlowLayout(FlowLayout.LEFT));
        controls.add(mode);
        Button wrap = new Button("Toggle wrap width");
        wrap.addActionListener(event -> {
            wrapWidth = wrapWidth == 400 ? 230 : 400;
            mode.select("Text layout");
            paintNow();
            status.setText("Wrap width: " + wrapWidth);
        });
        Button load = new Button("Decode PNG");
        load.addActionListener(event -> loadImage(false));
        Button fail = new Button("Decode invalid image");
        fail.addActionListener(event -> loadImage(true));
        Button buffer = new Button("Draw buffered frame");
        buffer.addActionListener(event -> drawBufferedFrame());
        controls.add(wrap);
        controls.add(load);
        controls.add(fail);
        controls.add(buffer);
        Button fontLoad = new Button("Load reference font");
        Button fontFail = new Button("Fail font load");
        fontLoad.setEnabled(fontLoading != null);
        fontFail.setEnabled(fontLoading != null);
        fontLoad.addActionListener(event -> loadFont(fontLoading, false));
        fontFail.addActionListener(event -> loadFont(fontLoading, true));
        controls.add(fontLoad);
        controls.add(fontFail);
        add(controls, BorderLayout.NORTH);
        canvas.setName("renderingCanvas");
        canvas.setPreferredSize(new Dimension(980, 520));
        canvas.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent event) {
                Area area = sampleArea();
                status.setText("Area.contains(" + event.getX() + ", " + event.getY() + ") = " + area.contains(event.getPoint()));
            }
        });
        add(canvas, BorderLayout.CENTER);
        Panel footer = new Panel(new BorderLayout());
        status.setName("renderingStatus");
        footer.add(status, BorderLayout.NORTH);
        Panel stateControls = new Panel(new FlowLayout(FlowLayout.LEFT));
        stateScenario.setName("graphicsStateScenario");
        for (String name : GraphicsStateScenarios.NAMES) {
            stateScenario.add(name);
        }
        stateScenario.addItemListener(event -> {
            mode.select("Graphics state");
            paintNow();
        });
        stateControls.add(new Label("Graphics state:"));
        stateControls.add(stateScenario);
        Button offset = new Button("Toggle translation 20 / 40");
        offset.addActionListener(event -> {
            stateOffset = stateOffset == 20 ? 40 : 20;
            mode.select("Graphics state");
            paintNow();
        });
        stateControls.add(offset);
        Button background = new Button("Toggle cyan / transparent clear");
        background.addActionListener(event -> {
            transparentClear = !transparentClear;
            stateScenario.select(2);
            mode.select("Graphics state");
            paintNow();
        });
        stateControls.add(background);
        footer.add(stateControls, BorderLayout.CENTER);
        footer.add(new Label("Compare text shaping and glyph outlines with the JDK. WebAWT buffer presentation uses its browser Canvas profile."), BorderLayout.SOUTH);
        add(footer, BorderLayout.SOUTH);
    }

    private void loadFont(FontLoading loading, boolean fail) {
        int requested = ++fontGeneration;
        status.setText("Reference font loading...");
        loading.load(fail ? "fonts/missing.ttf" : "fonts/DejaVuSans.ttf", () -> {
            if (!disposed && requested == fontGeneration) {
                stateScenario.select(20);
                mode.select("Graphics state");
                paintNow();
                status.setText("Reference font ready; measurements refreshed");
            }
        }, () -> {
            if (!disposed && requested == fontGeneration) {
                status.setText("Reference font failed; retry is available");
            }
        });
    }

    void dispose() {
        disposed = true;
        generation++;
        fontGeneration++;
        if (decoded != null) {
            decoded.flush();
        }
        if (canvas.getBufferStrategy() != null) {
            canvas.getBufferStrategy().dispose();
        }
    }

    void paintNow() {
        if (!disposed && canvas.isShowing()) {
            Graphics graphics = canvas.getGraphics();
            if (graphics != null) {
                try {
                    canvas.paint(graphics);
                } finally {
                    graphics.dispose();
                }
            }
        }
    }

    private void loadImage(boolean invalid) {
        mode.select("Images & buffering");
        int request = ++generation;
        if (decoded != null) {
            decoded.flush();
        }
        decoded = Toolkit.getDefaultToolkit().createImage(invalid ? new byte[] {1, 2, 3} : pngBytes());
        status.setText("Image pending: waiting for decoder / ImageObserver");
        ImageObserver observer = (image, flags, x, y, width, height) -> {
            EventQueue.invokeLater(() -> finishImage(request, flags, width, height));
            return !disposed && request == generation && (flags & (ImageObserver.ALLBITS | ImageObserver.ERROR | ImageObserver.ABORT)) == 0;
        };
        if (Toolkit.getDefaultToolkit().prepareImage(decoded, -1, -1, observer)) {
            finishImage(request, ImageObserver.ALLBITS, decoded.getWidth(null), decoded.getHeight(null));
        }
        paintNow();
    }

    private void finishImage(int request, int flags, int width, int height) {
        if (disposed || request != generation) {
            return;
        }
        if ((flags & (ImageObserver.ERROR | ImageObserver.ABORT)) != 0) {
            status.setText("Image failed: decoder rejected the payload");
        } else if ((flags & ImageObserver.ALLBITS) != 0) {
            status.setText("Image ready: " + width + " x " + height);
        } else {
            status.setText("Image progress: flags=" + flags);
        }
        paintNow();
    }

    private byte[] pngBytes() {
        byte[] bytes = new byte[png.length() / 2];
        for (int index = 0; index < bytes.length; index++) {
            bytes[index] = (byte) Integer.parseInt(png.substring(index * 2, index * 2 + 2), 16);
        }
        return bytes;
    }

    private void drawBufferedFrame() {
        mode.select("Images & buffering");
        if (canvas.getBufferStrategy() == null) {
            canvas.createBufferStrategy(2);
        }
        BufferStrategy strategy = canvas.getBufferStrategy();
        Graphics graphics = strategy.getDrawGraphics();
        try {
            canvas.paint(graphics);
        } finally {
            graphics.dispose();
        }
        strategy.show();
        status.setText("BufferStrategy presented; pageFlipping=" + strategy.getCapabilities().isPageFlipping() + ", lost=" + strategy.contentsLost());
    }

    private Area sampleArea() {
        if (geometryArea == null) {
            geometryArea = new Area(new Rectangle2D.Double(35, 210, 210, 135));
            geometryArea.subtract(new Area(new Ellipse2D.Double(95, 235, 90, 90)));
        }
        return geometryArea;
    }

    private static BufferedImage tile(int size, Color color) {
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        try {
            graphics.setColor(color);
            graphics.fillRect(0, 0, size, size);
            graphics.setColor(Color.WHITE);
            graphics.fillRect(0, 0, size / 2, size / 2);
        } finally {
            graphics.dispose();
        }
        return image;
    }

    private final class LabCanvas extends Canvas {
        @Override
        public void paint(Graphics graphics) {
            Graphics2D painter = (Graphics2D) graphics.create();
            try {
                painter.setColor(Color.WHITE);
                painter.fillRect(0, 0, getWidth(), getHeight());
                painter.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 16));
                if (mode.getSelectedIndex() == 0) {
                    paintGeometry(painter);
                } else if (mode.getSelectedIndex() == 1) {
                    paintText(painter);
                } else if (mode.getSelectedIndex() == 3) {
                    paintState(painter);
                } else {
                    paintImages(painter);
                }
            } finally {
                painter.dispose();
            }
        }

        private void paintState(Graphics2D painter) {
            int scenario = stateScenario.getSelectedIndex();
            painter.setColor(Color.DARK_GRAY);
            painter.drawString("Direct Canvas", 30, 45);
            painter.drawString("BufferedImage", 450, 45);
            Graphics2D direct = (Graphics2D) painter.create(30, 70, GraphicsStateScenarios.WIDTH, GraphicsStateScenarios.HEIGHT);
            String report;
            try {
                report = GraphicsStateScenarios.paint(direct, scenario, stateOffset, transparentClear);
            } finally {
                direct.dispose();
            }
            BufferedImage image = GraphicsStateScenarios.image(scenario, stateOffset, transparentClear);
            painter.drawImage(image, 450, 70, null);
            image.flush();
            painter.setColor(Color.DARK_GRAY);
            painter.drawString(report, 30, 285);
            if (scenario == 20) {
                painter.drawString("Rows: iiii / WWWW / WebAWT 123 / Cafe\u0301. Green = 24px; blue = 12px.", 30, 315);
                painter.drawString("Bars compare font measurements. Select Plain text overloads to inspect painted glyphs.", 30, 345);
            } else if (scenario >= 30) {
                painter.drawString("Each primitive and query has its own pixel/state checks on both rendering backends.", 30, 315);
                painter.drawString("Canvas XOR remains explicitly unavailable. Rendering hints are retained; rasterization may differ.", 30, 345);
            } else if (scenario >= 22) {
                painter.drawString("Plain text uses browser glyphs on both backends. BufferedImage stores logical-resolution pixels.", 30, 315);
                painter.drawString("Load the reference font for comparison. Styled runs work; mixed-direction carets and true outlines remain limited.", 30, 345);
            } else {
                painter.drawString("Red = untouched; blue / green / yellow = source pixels; cyan = background; white = filtered.", 30, 315);
                painter.drawString("Transparent BufferedImage pixels reveal this white page; direct Canvas pixels retain alpha zero.", 30, 345);
            }
            status.setText("Graphics state checks passed: " + GraphicsStateScenarios.NAMES[scenario] + "; translation=" + stateOffset);
        }

        private void paintGeometry(Graphics2D painter) {
            painter.setPaint(new RadialGradientPaint(135, 100, 75, new float[] {0, 1}, new Color[] {Color.YELLOW, Color.BLUE}));
            painter.fillOval(60, 25, 150, 150);
            if (textureImage == null) {
                textureImage = new BufferedImage(42, 30, BufferedImage.TYPE_INT_ARGB);
                Graphics2D texture = textureImage.createGraphics();
                try {
                    texture.setPaint(new TexturePaint(tile(4, new Color(25, 150, 120)), new Rectangle(0, 0, 4, 4)));
                    texture.fillRect(0, 0, 42, 30);
                } finally {
                    texture.dispose();
                }
            }
            painter.drawImage(textureImage, 300, 25, 210, 150, null);
            Graphics2D clipped = (Graphics2D) painter.create();
            try {
                clipped.clip(new Ellipse2D.Double(590, 25, 190, 150));
                clipped.setColor(new Color(235, 90, 80));
                clipped.fillRect(560, 10, 250, 190);
            } finally {
                clipped.dispose();
            }
            painter.setColor(new Color(60, 125, 195));
            painter.fill(sampleArea());
            painter.setColor(Color.RED);
            painter.fillRect(320, 235, 120, 100);
            painter.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
            painter.setColor(Color.BLUE);
            painter.fillRect(380, 265, 120, 100);
            painter.setComposite(AlphaComposite.SrcOver);
            painter.setColor(Color.DARK_GRAY);
            painter.drawString("Radial gradient", 65, 195);
            painter.drawString("TexturePaint via BufferedImage", 290, 195);
            painter.drawString("Ellipse clip", 615, 195);
            painter.drawString("Area subtraction: click the hole", 35, 390);
            painter.drawString("SRC_OVER at 50%", 320, 390);
            painter.drawString("Graphics2D.hit = " + painter.hit(new Rectangle(35, 210, 10, 10), sampleArea(), false), 590, 280);
        }

        private void paintText(Graphics2D painter) {
            Font font = new Font(Font.SERIF, Font.PLAIN, 26);
            TextLayout layout = new TextLayout("Selection and caret", font, painter.getFontRenderContext());
            Graphics2D line = (Graphics2D) painter.create();
            try {
                line.translate(35, 65);
                line.setColor(new Color(185, 220, 255));
                line.fill(layout.getLogicalHighlightShape(0, 9));
                line.setColor(Color.BLACK);
                layout.draw(line, 0, 0);
                line.setColor(Color.RED);
                for (Shape caret : layout.getCaretShapes(9)) {
                    if (caret != null) {
                        line.draw(caret);
                    }
                }
            } finally {
                line.dispose();
            }
            painter.setColor(Color.DARK_GRAY);
            GlyphVector glyphs = font.createGlyphVector(painter.getFontRenderContext(), "Glyph vector");
            painter.drawGlyphVector(glyphs, 35, 130);
            painter.drawString("Cafe \u00e9 | \u6f22\u5b57 | \u0645\u0631\u062d\u0628\u0627 | \ud83d\ude42", 35, 180);
            String paragraph = "LineBreakMeasurer wraps this attributed paragraph to the selected width. Compare word boundaries, font metrics and glyph appearance between both runtimes.";
            LineBreakMeasurer measurer = new LineBreakMeasurer(new GalleryText(paragraph, font.deriveFont(18f)), painter.getFontRenderContext());
            float y = 225;
            while (measurer.getPosition() < paragraph.length()) {
                TextLayout next = measurer.nextLayout(wrapWidth);
                if (next == null) {
                    break;
                }
                y += next.getAscent();
                next.draw(painter, 35, y);
                y += next.getDescent() + next.getLeading();
            }
            painter.setColor(Color.BLUE);
            painter.drawRect(30, 215, wrapWidth + 10, Math.max(1, (int) y - 210));
        }

        private void paintImages(Graphics2D painter) {
            BaseMultiResolutionImage variants = new BaseMultiResolutionImage(tile(32, Color.BLUE), tile(96, new Color(30, 170, 80)));
            Image small = variants.getResolutionVariant(24, 24);
            Image large = variants.getResolutionVariant(80, 80);
            painter.drawImage(small, 40, 65, 96, 96, null);
            painter.drawImage(large, 230, 65, 96, 96, null);
            painter.setColor(Color.DARK_GRAY);
            painter.drawString("24px request: " + small.getWidth(null) + "px variant", 35, 195);
            painter.drawString("80px request: " + large.getWidth(null) + "px variant", 230, 225);
            if (decoded != null) {
                painter.drawImage(decoded, 500, 65, 96, 96, null);
            }
            painter.drawString("Decoded image (scaled)", 480, 195);
            painter.drawString("Use Decode PNG, Decode invalid image and Draw buffered frame above.", 35, 300);
        }
    }

    private static final class GalleryText implements AttributedCharacterIterator {
        private final String text;
        private final Map<Attribute, Object> attributes;
        private int index;

        private GalleryText(String text, Font font) {
            this.text = text;
            attributes = Collections.singletonMap(TextAttribute.FONT, font);
        }

        @Override
        public char first() { return setIndex(0); }
        @Override
        public char last() { return setIndex(Math.max(0, text.length() - 1)); }
        @Override
        public char current() { return index == text.length() ? DONE : text.charAt(index); }
        @Override
        public char next() { return setIndex(Math.min(text.length(), index + 1)); }
        @Override
        public char previous() { return index == 0 ? DONE : setIndex(index - 1); }
        @Override
        public char setIndex(int position) {
            if (position < 0 || position > text.length()) {
                throw new IllegalArgumentException("Invalid text position");
            }
            index = position;
            return current();
        }
        @Override
        public int getBeginIndex() { return 0; }
        @Override
        public int getEndIndex() { return text.length(); }
        @Override
        public int getIndex() { return index; }
        @Override
        public int getRunStart() { return 0; }
        @Override
        public int getRunStart(Attribute attribute) { return 0; }
        @Override
        public int getRunStart(Set<? extends Attribute> attributes) { return 0; }
        @Override
        public int getRunLimit() { return text.length(); }
        @Override
        public int getRunLimit(Attribute attribute) { return text.length(); }
        @Override
        public int getRunLimit(Set<? extends Attribute> attributes) { return text.length(); }
        @Override
        public Map<Attribute, Object> getAttributes() { return attributes; }
        @Override
        public Object getAttribute(Attribute attribute) { return attributes.get(attribute); }
        @Override
        public Set<Attribute> getAllAttributeKeys() { return attributes.keySet(); }
        @Override
        public Object clone() {
            GalleryText copy = new GalleryText(text, (Font) attributes.get(TextAttribute.FONT));
            copy.index = index;
            return copy;
        }
    }
}
