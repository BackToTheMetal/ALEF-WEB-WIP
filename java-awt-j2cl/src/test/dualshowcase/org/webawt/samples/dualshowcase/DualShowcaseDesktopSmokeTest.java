package org.webawt.samples.dualshowcase;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.awt.Component;
import java.awt.Container;
import java.awt.Label;
import java.awt.Rectangle;
import java.awt.EventQueue;
import java.awt.Robot;
import java.awt.TextField;
import java.awt.event.KeyEvent;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Test;

public final class DualShowcaseDesktopSmokeTest {
    @org.junit.BeforeClass
    public static void loadReferenceFont() throws Exception {
        DesktopFonts.install();
    }

    @Test
    public void verifiesGraphicsStatePixelsAndQueries() {
        GraphicsStateAssertions.verify();
    }

    @Test
    public void verifiesDirectJdkCanvasGraphicsState() throws Exception {
        java.awt.Frame frame = new java.awt.Frame("Graphics state reference");
        java.awt.Canvas canvas = new java.awt.Canvas();
        EventQueue.invokeAndWait(() -> {
            canvas.setPreferredSize(new java.awt.Dimension(GraphicsStateScenarios.WIDTH, GraphicsStateScenarios.HEIGHT));
            frame.add(canvas);
            frame.pack();
            frame.setVisible(true);
        });
        try {
            Robot robot = new Robot();
            robot.waitForIdle();
            for (int scenario = 0; scenario < GraphicsStateScenarios.NAMES.length; scenario++) {
                if (GraphicsStateScenarios.requiresAlphaSurface(scenario)) {
                    continue;
                }
                for (int offset : new int[] {20, 40}) {
                    int selected = scenario;
                    EventQueue.invokeAndWait(() -> {
                        java.awt.Graphics2D graphics = (java.awt.Graphics2D) canvas.getGraphics();
                        try {
                            GraphicsStateScenarios.paint(graphics, selected, offset, false);
                        } finally {
                            graphics.dispose();
                        }
                        Toolkit.getDefaultToolkit().sync();
                    });
                    robot.waitForIdle();
                    java.awt.Point origin = canvas.getLocationOnScreen();
                    StringBuilder differences = new StringBuilder();
                    for (int[] pixel : GraphicsStateScenarios.pixels(scenario, offset, false)) {
                        int actual = robot.getPixelColor(origin.x + pixel[0], origin.y + pixel[1]).getRGB();
                        if (!GraphicsStateScenarios.matchesPixel(pixel[2], actual, pixel.length > 4 ? pixel[4] : pixel.length > 3 ? pixel[3] : 0)) {
                            differences.append(" at ").append(pixel[0]).append(',').append(pixel[1]).append(": expected=").append(Integer.toHexString(pixel[2])).append(", actual=").append(Integer.toHexString(actual));
                        }
                    }
                    assertTrue(GraphicsStateScenarios.NAMES[scenario] + differences, differences.length() == 0);
                    if (scenario == 36) { PerformanceScenarios.verifyHairline(robot.createScreenCapture(new Rectangle(origin.x, origin.y, GraphicsStateScenarios.WIDTH, GraphicsStateScenarios.HEIGHT)), offset); }
                    if (scenario >= 26 && scenario < 30) {
                        AttributedScenarios.verifyInk(robot.createScreenCapture(new Rectangle(origin.x, origin.y, GraphicsStateScenarios.WIDTH, GraphicsStateScenarios.HEIGHT)), scenario - 26, offset);
                    } else if (scenario >= 22 && scenario < 25) {
                        TextScenarios.verifyInk(robot.createScreenCapture(new Rectangle(origin.x, origin.y, GraphicsStateScenarios.WIDTH, GraphicsStateScenarios.HEIGHT)), scenario - 22, offset);
                    }
                }
            }
        } finally {
            EventQueue.invokeAndWait(frame::dispose);
        }
    }

    @Test
    public void comparesRealJdkKeyboardSequenceAndSelectionInTheSharedInputPage() throws Exception {
        DualShowcase showcase = new DualShowcase("JDK keyboard reference");
        AtomicReference<TextField> input = new AtomicReference<>();
        EventQueue.invokeAndWait(() -> {
            showcase.show();
            showcase.selectPage("Keyboard & input");
            input.set((TextField) named(showcase.getFrame(), "keyInput"));
            input.get().setText("abcdef");
            input.get().setCaretPosition(6);
            input.get().requestFocus();
        });
        try {
            Robot robot = new Robot();
            robot.setAutoDelay(40);
            robot.waitForIdle();
            assertTrue(input.get().isFocusOwner());
            robot.keyPress(KeyEvent.VK_A);
            robot.keyRelease(KeyEvent.VK_A);
            robot.waitForIdle();
            EventQueue.invokeAndWait(() -> {
                String trace = showcase.getKeyboardTrace();
                assertTrue(trace, trace.contains("PRESSED target=keyInput code=65"));
                assertTrue(trace, trace.contains("TYPED target=keyInput code=0 key=Undefined char=U+61"));
                assertTrue(trace, trace.contains("RELEASED target=keyInput code=65"));
                input.get().select(1, 4);
                assertEquals("1:4:4", showcase.getKeyboardSelection());
            });
            robot.keyPress(KeyEvent.VK_ENTER);
            robot.keyRelease(KeyEvent.VK_ENTER);
            robot.keyPress(KeyEvent.VK_ENTER);
            robot.keyRelease(KeyEvent.VK_ENTER);
            robot.waitForIdle();
            EventQueue.invokeAndWait(() -> assertTrue(showcase.getKeyboardTrace(), showcase.getKeyboardTrace().contains("ACTION Enter count=2")));
        } finally {
            EventQueue.invokeAndWait(showcase::dispose);
        }
    }

    private static Component named(Container parent, String name) {
        for (Component child : parent.getComponents()) {
            if (name.equals(child.getName())) {
                return child;
            }
            if (child instanceof Container) {
                Component match = named((Container) child, name);
                if (match != null) {
                    return match;
                }
            }
        }
        return null;
    }

    @Test
    public void usesTheJdkSystemClipboardThroughTheSharedRuntimeBoundary() throws Exception {
        ClipboardAccess clipboard = new ClipboardAccess();
        AtomicReference<String> result = new AtomicReference<>();
        clipboard.writeText("Desktop clipboard text", () -> result.set("written"), reason -> fail(reason));
        assertEquals("written", result.get());
        assertEquals("Desktop clipboard text", Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor));
        StringSelection external = new StringSelection("Text from another desktop application");
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(external, external);
        clipboard.readText(result::set, reason -> fail(reason));
        assertEquals("Text from another desktop application", result.get());
    }

    @Test
    public void opensNavigatesPaintsAndDisposesTheJdkAwtGallery() {
        DualShowcase showcase = new DualShowcase("JDK test runtime");
        showcase.show();
        try {
            assertNotNull(showcase.getFrame());
            assertTrue(showcase.getFrame().isDisplayable());
            assertEquals(12, showcase.getPageCount());
            showcase.selectPage("Menus & scrolling");
            assertEquals(18, assertTileGeometry(showcase.getFrame()));
            showcase.selectPage("Graphics2D");
            showcase.paintDemos();
            showcase.selectPage("Images & color");
            showcase.paintDemos();
            showcase.selectPage("Rendering lab");
            showcase.paintDemos();
            showcase.selectPage("Lifecycle & accessibility");
            assertTrue(showcase.runSelfCheck());
        } finally {
            showcase.dispose();
        }
    }

    private static int assertTileGeometry(Container container) {
        int count = 0;
        for (Component component : container.getComponents()) {
            if (component instanceof Label && ((Label) component).getText().startsWith("Scrollable tile ")) {
                Label tile = (Label) component;
                int index = Integer.parseInt(tile.getText().substring("Scrollable tile ".length())) - 1;
                assertEquals(new Rectangle(30 + index % 6 * 185, 30 + index / 6 * 220, 160, 170), tile.getBounds());
                assertEquals(Label.CENTER, tile.getAlignment());
                count++;
            } else if (component instanceof Container) {
                count += assertTileGeometry((Container) component);
            }
        }
        return count;
    }
}
