package com.webawt.portable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.awt.AWTException;
import java.awt.BufferCapabilities;
import java.awt.Canvas;
import java.awt.DefaultKeyboardFocusManager;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.ImageCapabilities;
import java.awt.KeyboardFocusManager;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.Panel;
import java.awt.PopupMenu;
import java.awt.event.FocusEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

public final class WindowEventJ2clTest {
    @Test
    public void exposesWindowEventStateAndDefaultAdapterMethods() {
        Frame source = new Frame("source");
        Frame opposite = new Frame("opposite");
        WindowEvent event = new WindowEvent(source, WindowEvent.WINDOW_STATE_CHANGED, opposite, Frame.NORMAL, Frame.MAXIMIZED_BOTH);
        assertSame(source, event.getWindow());
        assertSame(opposite, event.getOppositeWindow());
        assertEquals(Frame.NORMAL, event.getOldState());
        assertEquals(Frame.MAXIMIZED_BOTH, event.getNewState());
        assertTrue(event.paramString().startsWith("WINDOW_STATE_CHANGED"));
        WindowAdapter adapter = new WindowAdapter() { };
        adapter.windowOpened(event);
        adapter.windowClosing(event);
        adapter.windowClosed(event);
        adapter.windowIconified(event);
        adapter.windowDeiconified(event);
        adapter.windowActivated(event);
        adapter.windowDeactivated(event);
        adapter.windowStateChanged(event);
        adapter.windowGainedFocus(event);
        adapter.windowLostFocus(event);
        source.dispose();
        opposite.dispose();
    }

    @Test
    public void appliesDefaultLocaleOrientationForNamedResourceBundles() {
        Frame frame = new Frame("localized");
        frame.applyResourceBundle("com.example.Messages");
        assertSame(java.awt.ComponentOrientation.getOrientation(java.util.Locale.getDefault()), frame.getComponentOrientation());
        frame.dispose();
    }

    @Test
    public void dispatchesWindowLifecycleStateAndFocusEvents() {
        Frame frame = new Frame("events");
        int[] events = new int[6];
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent event) {
                events[0]++;
            }
            @Override
            public void windowClosed(WindowEvent event) {
                events[1]++;
            }
            @Override
            public void windowIconified(WindowEvent event) {
                events[2]++;
            }
            @Override
            public void windowDeiconified(WindowEvent event) {
                events[3]++;
            }
        });
        frame.addWindowStateListener(event -> events[4]++);
        frame.addWindowFocusListener(new WindowAdapter() {
            @Override
            public void windowGainedFocus(WindowEvent event) {
                events[5]++;
            }
        });
        frame.setVisible(false);
        frame.setVisible(true);
        assertEquals(1, events[0]);
        frame.setState(Frame.ICONIFIED);
        frame.setState(Frame.NORMAL);
        assertEquals(1, events[2]);
        assertEquals(1, events[3]);
        assertEquals(2, events[4]);
        KeyboardFocusManager previous = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        DefaultKeyboardFocusManager manager = new DefaultKeyboardFocusManager();
        KeyboardFocusManager.setCurrentKeyboardFocusManager(manager);
        Panel child = new Panel();
        frame.add(child);
        manager.dispatchEvent(new FocusEvent(child, FocusEvent.FOCUS_GAINED));
        assertTrue(frame.isFocused());
        assertTrue(frame.isActive());
        assertSame(child, frame.getFocusOwner());
        assertSame(child, frame.getMostRecentFocusOwner());
        assertEquals(1, events[5]);
        assertEquals(1, frame.getWindowListeners().length);
        assertEquals(1, frame.getWindowFocusListeners().length);
        assertEquals(1, frame.getWindowStateListeners().length);
        frame.dispose();
        assertEquals(1, events[1]);
        KeyboardFocusManager.setCurrentKeyboardFocusManager(previous);
    }

    @Test
    public void preservesGraphicsConfigurationIconsAndModalityState() {
        GraphicsConfiguration configuration = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
        Frame frame = new Frame("configured", configuration);
        Canvas canvas = new Canvas(configuration);
        Dialog dialog = new Dialog(frame, "dialog", true, configuration);
        Dialog modalityOnly = new Dialog(frame, Dialog.ModalityType.DOCUMENT_MODAL);
        assertSame(configuration, frame.getGraphicsConfiguration());
        assertSame(configuration, canvas.getGraphicsConfiguration());
        assertSame(configuration, dialog.getGraphicsConfiguration());
        assertSame(configuration, modalityOnly.getGraphicsConfiguration());
        assertSame(Dialog.ModalityType.DOCUMENT_MODAL, modalityOnly.getModalityType());
        Image icon = new BufferedImage(2, 2, BufferedImage.TYPE_INT_ARGB);
        int[] changes = new int[1];
        frame.addPropertyChangeListener("iconImage", event -> changes[0]++);
        List<Image> icons = new ArrayList<>();
        icons.add(icon);
        frame.setIconImages(icons);
        icons.clear();
        assertSame(icon, frame.getIconImage());
        List<Image> returnedIcons = frame.getIconImages();
        returnedIcons.clear();
        assertEquals(1, frame.getIconImages().size());
        assertEquals(1, changes[0]);
        frame.setIconImage(null);
        assertSame(null, frame.getIconImage());
        assertEquals(2, changes[0]);
        frame.setModalExclusionType(Dialog.ModalExclusionType.APPLICATION_EXCLUDE);
        assertSame(Dialog.ModalExclusionType.APPLICATION_EXCLUDE, frame.getModalExclusionType());
        frame.setModalExclusionType(Dialog.ModalExclusionType.TOOLKIT_EXCLUDE);
        assertSame(Dialog.ModalExclusionType.NO_EXCLUDE, frame.getModalExclusionType());
        dialog.dispose();
        modalityOnly.dispose();
        frame.dispose();
    }

    @Test
    public void managesBrowserBufferStrategies() throws AWTException {
        Canvas canvas = new Canvas();
        boolean rejectedUndisplayable = false;
        try {
            canvas.createBufferStrategy(2);
        } catch (IllegalStateException expected) {
            rejectedUndisplayable = true;
        }
        assertTrue(rejectedUndisplayable);
        canvas.setSize(8, 6);
        canvas.addNotify();
        canvas.createBufferStrategy(2);
        BufferStrategy canvasStrategy = canvas.getBufferStrategy();
        assertTrue(canvasStrategy != null);
        assertTrue(!canvasStrategy.getCapabilities().isPageFlipping());
        Graphics canvasGraphics = canvasStrategy.getDrawGraphics();
        assertTrue(canvasGraphics != null);
        canvasGraphics.fillRect(0, 0, 2, 2);
        canvasGraphics.dispose();
        canvasStrategy.show();
        assertTrue(!canvasStrategy.contentsLost());
        assertTrue(!canvasStrategy.contentsRestored());
        BufferCapabilities capabilities = new BufferCapabilities(new ImageCapabilities(false), new ImageCapabilities(false), null);
        canvas.createBufferStrategy(1, capabilities);
        BufferStrategy replacement = canvas.getBufferStrategy();
        assertSame(capabilities, replacement.getCapabilities());
        replacement.dispose();
        assertSame(null, canvas.getBufferStrategy());
        canvas.removeNotify();
        Frame frame = new Frame("buffered");
        frame.setSize(5, 4);
        frame.setVisible(true);
        frame.createBufferStrategy(2);
        BufferStrategy windowStrategy = frame.getBufferStrategy();
        Graphics windowGraphics = windowStrategy.getDrawGraphics();
        assertTrue(windowGraphics != null);
        windowGraphics.dispose();
        assertTrue(windowStrategy.contentsRestored());
        assertTrue(!windowStrategy.contentsRestored());
        windowStrategy.dispose();
        assertSame(null, frame.getBufferStrategy());
        frame.dispose();
        boolean rejectedCount = false;
        try {
            canvas.createBufferStrategy(0);
        } catch (IllegalArgumentException expected) {
            rejectedCount = true;
        }
        assertTrue(rejectedCount);
        Canvas accelerated = new Canvas();
        accelerated.addNotify();
        boolean rejectedAcceleration = false;
        try {
            accelerated.createBufferStrategy(2, new BufferCapabilities(new ImageCapabilities(true), new ImageCapabilities(false), null));
        } catch (AWTException expected) {
            rejectedAcceleration = true;
        }
        assertTrue(rejectedAcceleration);
        accelerated.removeNotify();
    }

    @Test
    public void attachesMenuBarsAndPopupMenusToDisplayableComponents() {
        Frame frame = new Frame("menus");
        MenuBar menuBar = new MenuBar();
        Menu file = menuBar.add(new Menu("File"));
        file.add(new MenuItem("Open"));
        frame.setMenuBar(menuBar);
        assertSame(menuBar, frame.getMenuBar());
        assertSame(frame, menuBar.getParent());
        frame.addNotify();
        frame.remove(menuBar);
        assertSame(null, frame.getMenuBar());
        assertSame(null, menuBar.getParent());
        Panel panel = new Panel();
        PopupMenu popupMenu = new PopupMenu("Popup");
        popupMenu.add(new MenuItem("Action"));
        panel.add(popupMenu);
        panel.addNotify();
        assertSame(panel, popupMenu.getParent());
        popupMenu.show(panel, 3, 4);
        panel.remove(popupMenu);
        assertSame(null, popupMenu.getParent());
        panel.removeNotify();
        frame.dispose();
    }
}
