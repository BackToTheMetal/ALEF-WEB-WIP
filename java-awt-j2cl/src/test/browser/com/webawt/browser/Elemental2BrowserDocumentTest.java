package com.webawt.browser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import elemental2.dom.DomGlobal;
import elemental2.dom.Element;
import elemental2.dom.Event;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLInputElement;
import elemental2.dom.HTMLCanvasElement;
import elemental2.dom.HTMLSelectElement;
import java.awt.Button;
import java.awt.Canvas;
import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.Choice;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.KeyboardFocusManager;
import java.awt.Label;
import java.awt.List;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.Panel;
import java.awt.PopupMenu;
import java.awt.ScrollPane;
import java.awt.SystemColor;
import java.awt.TextField;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.InputMethodEvent;
import java.awt.event.InputMethodListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import org.junit.Test;
import org.webawt.internal.browser.BrowserDocument;
import org.webawt.internal.browser.Elemental2BrowserDocument;
import org.webawt.internal.browser.Elemental2DesktopPeer;
import org.webawt.internal.browser.Elemental2DomPeerFactory;
import org.webawt.internal.browser.Elemental2MenuPeerFactory;
import org.webawt.internal.browser.Elemental2DisplayMetricsProvider;
import org.webawt.internal.browser.Elemental2SystemColorProvider;
import org.webawt.internal.peer.DomPeer;
import org.webawt.internal.peer.CanvasPeer;
import org.webawt.internal.peer.PeerDesktopEvent;
import org.webawt.internal.peer.WebAwtRuntime;

public final class Elemental2BrowserDocumentTest {
    @Test
    public void copiesOnlyAccessibleCanvasSourcePixels() {
        org.webawt.internal.browser.Elemental2CanvasPeer peer = new org.webawt.internal.browser.Elemental2CanvasPeer();
        Element root = DomGlobal.document.createElement("div");
        root.setAttribute("id", "webawt-copy-source-root");
        DomGlobal.document.body.appendChild(root);
        try {
            peer.attachToElement("webawt-copy-source-root");
            peer.setBounds(0, 0, 16, 16);
            peer.setFillStyle("#ff0000");
            peer.fillRect(0, 0, 16, 16);
            peer.setFillStyle("#0000ff");
            peer.fillRect(0, 0, 8, 8);
            peer.setCompositeOperation("copy");
            peer.copyArea(-4, 0, 8, 8, 8, 8);
            peer.copyArea(-100, 0, 8, 8, 100, 8);
            HTMLCanvasElement canvas = (HTMLCanvasElement) DomGlobal.document.getElementById(peer.getElementId());
            elemental2.dom.CanvasRenderingContext2D context = (elemental2.dom.CanvasRenderingContext2D) (Object) canvas.getContext("2d");
            for (int x = 0; x < 16; x++) {
                elemental2.dom.ImageData pixel = context.getImageData(x, 12, 1, 1);
                boolean copied = x >= 8 && x < 12;
                assertEquals(copied ? 0 : 255, pixel.data.getAt(0).intValue());
                assertEquals(copied ? 255 : 0, pixel.data.getAt(2).intValue());
                assertEquals(255, pixel.data.getAt(3).intValue());
            }
        } finally {
            peer.detach();
            DomGlobal.document.body.removeChild(root);
        }
    }

    @Test
    public void browserControlEventsReachMaskedSubclassHooksBeforeListeners() {
        Element root = createControlRoot();
        Panel panel = new Panel(null);
        int[] hooks = new int[8];
        int[] observed = new int[1];
        java.awt.event.AWTEventListener observer = event -> observed[0]++;
        long mask = java.awt.AWTEvent.ACTION_EVENT_MASK | java.awt.AWTEvent.ITEM_EVENT_MASK | java.awt.AWTEvent.TEXT_EVENT_MASK | java.awt.AWTEvent.ADJUSTMENT_EVENT_MASK;
        java.awt.Toolkit.getDefaultToolkit().addAWTEventListener(observer, mask);
        try {
            Button button = new Button("Run") {
                {
                    enableEvents(java.awt.AWTEvent.ACTION_EVENT_MASK);
                }
                @Override
                protected void processActionEvent(java.awt.event.ActionEvent event) {
                    hooks[0]++;
                    assertTrue(observed[0] > 0);
                    assertEquals("Run", event.getActionCommand());
                    super.processActionEvent(event);
                }
            };
            Checkbox checkbox = new Checkbox("Check") {
                {
                    enableEvents(java.awt.AWTEvent.ITEM_EVENT_MASK);
                }
                @Override
                protected void processItemEvent(java.awt.event.ItemEvent event) {
                    hooks[1]++;
                    assertTrue(observed[0] > 0);
                    assertEquals("Check", event.getItem());
                    super.processItemEvent(event);
                }
            };
            Choice choice = new Choice() {
                {
                    enableEvents(java.awt.AWTEvent.ITEM_EVENT_MASK);
                }
                @Override
                protected void processItemEvent(java.awt.event.ItemEvent event) {
                    hooks[2]++;
                    assertTrue(observed[0] > 0);
                    assertEquals("Second", event.getItem());
                    super.processItemEvent(event);
                }
            };
            List list = new List() {
                {
                    enableEvents(java.awt.AWTEvent.ITEM_EVENT_MASK | java.awt.AWTEvent.ACTION_EVENT_MASK);
                }
                @Override
                protected void processItemEvent(java.awt.event.ItemEvent event) {
                    hooks[3]++;
                    assertTrue(observed[0] > 0);
                    super.processItemEvent(event);
                }
                @Override
                protected void processActionEvent(java.awt.event.ActionEvent event) {
                    hooks[4]++;
                    assertTrue(observed[0] > 0);
                    assertEquals("Second", event.getActionCommand());
                    super.processActionEvent(event);
                }
            };
            TextField field = new TextField() {
                {
                    enableEvents(java.awt.AWTEvent.TEXT_EVENT_MASK);
                }
                @Override
                protected void processTextEvent(java.awt.event.TextEvent event) {
                    hooks[5]++;
                    assertTrue(observed[0] > 0);
                    super.processTextEvent(event);
                }
            };
            java.awt.TextArea area = new java.awt.TextArea() {
                {
                    enableEvents(java.awt.AWTEvent.TEXT_EVENT_MASK);
                }
                @Override
                protected void processTextEvent(java.awt.event.TextEvent event) {
                    hooks[6]++;
                    assertTrue(observed[0] > 0);
                    super.processTextEvent(event);
                }
            };
            java.awt.Scrollbar scrollbar = new java.awt.Scrollbar() {
                {
                    enableEvents(java.awt.AWTEvent.ADJUSTMENT_EVENT_MASK);
                }
                @Override
                protected void processAdjustmentEvent(java.awt.event.AdjustmentEvent event) {
                    hooks[7]++;
                    assertTrue(observed[0] > 0);
                    assertEquals(42, event.getValue());
                    super.processAdjustmentEvent(event);
                }
            };
            choice.add("First");
            choice.add("Second");
            list.add("First");
            list.add("Second");
            button.addActionListener(event -> assertEquals(1, hooks[0]));
            panel.add(button);
            panel.add(checkbox);
            panel.add(choice);
            panel.add(list);
            panel.add(field);
            panel.add(area);
            panel.add(scrollbar);
            panel.addNotify();
            ((HTMLElement) root.querySelector("button")).click();
            ((HTMLInputElement) root.querySelector("input[type=checkbox]")).click();
            HTMLSelectElement choiceElement = (HTMLSelectElement) root.querySelectorAll("select").item(0);
            choiceElement.selectedIndex = 1;
            choiceElement.dispatchEvent(new Event("change"));
            HTMLSelectElement listElement = (HTMLSelectElement) root.querySelectorAll("select").item(1);
            listElement.selectedIndex = 1;
            listElement.dispatchEvent(new Event("change"));
            listElement.dispatchEvent(new Event("click"));
            HTMLInputElement fieldElement = (HTMLInputElement) root.querySelector("input[type=text]");
            fieldElement.value = "updated";
            fieldElement.dispatchEvent(new Event("input"));
            elemental2.dom.HTMLTextAreaElement areaElement = (elemental2.dom.HTMLTextAreaElement) root.querySelector("textarea");
            areaElement.value = "multiline";
            areaElement.dispatchEvent(new Event("input"));
            HTMLInputElement range = (HTMLInputElement) root.querySelector("input[type=range]");
            range.value = "42";
            range.dispatchEvent(new Event("input"));
            for (int count : hooks) {
                assertEquals(1, count);
            }
            assertEquals(8, observed[0]);
            assertEquals("updated", field.getText());
            assertEquals("multiline", area.getText());
            assertTrue(checkbox.getState());
            assertEquals(42, scrollbar.getValue());
        } finally {
            java.awt.Toolkit.getDefaultToolkit().removeAWTEventListener(observer);
            panel.removeNotify();
            DomGlobal.document.body.removeChild(root);
        }
    }

    @Test
    public void mapsVisibilityAndCloseRequestsToDesktopEvents() {
        int[] counts = new int[3];
        Elemental2DesktopPeer peer = Elemental2DesktopPeer.getInstance();
        peer.attach();
        peer.attach();
        peer.setEventHandler(type -> {
            if (type == PeerDesktopEvent.APP_FOREGROUND || type == PeerDesktopEvent.APP_BACKGROUND) {
                counts[0]++;
            } else if (type == PeerDesktopEvent.APP_HIDDEN || type == PeerDesktopEvent.APP_UNHIDDEN) {
                counts[1]++;
            } else if (type == PeerDesktopEvent.QUIT_REQUEST) {
                counts[2]++;
                return true;
            }
            return false;
        });
        try {
            DomGlobal.document.dispatchEvent(new Event("visibilitychange"));
            DomGlobal.window.dispatchEvent(new Event("beforeunload"));
            assertEquals(1, counts[0]);
            assertEquals(1, counts[1]);
            assertEquals(1, counts[2]);
            peer.detach();
            DomGlobal.document.dispatchEvent(new Event("visibilitychange"));
            DomGlobal.window.dispatchEvent(new Event("beforeunload"));
            assertEquals(1, counts[0]);
            assertEquals(1, counts[2]);
        } finally {
            peer.detach();
        }
    }

    @Test
    public void mapsGraphicsEnvironmentToBrowserViewportAndScale() {
        WebAwtRuntime.installDisplayMetricsProvider(new Elemental2DisplayMetricsProvider());
        GraphicsConfiguration configuration = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
        assertEquals(DomGlobal.window.innerWidth, configuration.getBounds().width);
        assertEquals(DomGlobal.window.innerHeight, configuration.getBounds().height);
        assertEquals(Math.max(1.0, DomGlobal.window.devicePixelRatio), configuration.getDefaultTransform().getScaleX(), 0.0001);
    }

    @Test
    public void resolvesSystemColorsThroughComputedCss() {
        Elemental2SystemColorProvider provider = new Elemental2SystemColorProvider();
        WebAwtRuntime.installSystemColorProvider(provider);
        int expected = provider.getSystemColor(SystemColor.WINDOW, 0xffffff);
        assertEquals(expected, SystemColor.window.getRGB() & 0xffffff);
        assertEquals(255, SystemColor.window.getAlpha());
    }

    @Test
    public void writesTextToAnExistingElement() {
        Element element = DomGlobal.document.createElement("div");
        element.setAttribute("id", "webawt-test-target");
        DomGlobal.document.body.appendChild(element);
        try {
            BrowserDocument browserDocument = new Elemental2BrowserDocument();
            browserDocument.setTextContent("webawt-test-target", "ready");
            assertEquals("ready", element.textContent);
        } finally {
            DomGlobal.document.body.removeChild(element);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsAnUnknownElement() {
        BrowserDocument browserDocument = new Elemental2BrowserDocument();
        browserDocument.setTextContent("webawt-missing-target", "unused");
    }

    @Test
    public void mountsAndUpdatesSemanticDomPeers() {
        Element root = DomGlobal.document.createElement("div");
        root.setAttribute("id", "webawt-peer-root");
        DomGlobal.document.body.appendChild(root);
        try {
            WebAwtRuntime.installPeerFactory(new Elemental2DomPeerFactory());
            DomPeer peer = WebAwtRuntime.createPeer("button");
            peer.setText("Save");
            peer.setBounds(10, 20, 30, 40);
            peer.setEnabled(false);
            peer.attachToElement("webawt-peer-root");
            Element element = DomGlobal.document.getElementById(peer.getElementId());
            assertTrue(peer.isAttached());
            assertEquals("Save", element.textContent);
            assertEquals("true", element.getAttribute("aria-disabled"));
            assertTrue(element.getAttribute("style").contains("left: 10px"));
            peer.detach();
            assertFalse(peer.isAttached());
        } finally {
            DomGlobal.document.body.removeChild(root);
        }
    }

    @Test
    public void screenLocationIncludesHostOffsetAndAncestorScrolling() {
        HTMLElement host = (HTMLElement) DomGlobal.document.createElement("div");
        host.id = "webawt-screen-location-root";
        host.style.cssText = "position:fixed;left:73px;top:119px;width:180px;height:90px;overflow:scroll";
        DomGlobal.document.body.appendChild(host);
        Panel panel = new Panel(null);
        try {
            WebAwtRuntime.installPeerFactory(new Elemental2DomPeerFactory());
            WebAwtRuntime.setRootElementId(host.id);
            panel.setBounds(11, 17, 600, 400);
            Button button = new Button("Anchor");
            button.setBounds(23, 29, 80, 24);
            panel.add(button);
            panel.addNotify();
            assertEquals(new java.awt.Point(107, 165), button.getLocationOnScreen());
            host.scrollLeft = 19;
            host.scrollTop = 31;
            assertEquals(new java.awt.Point(88, 134), button.getLocationOnScreen());
            host.style.left = "91px";
            host.style.top = "137px";
            assertEquals(new java.awt.Point(106, 152), button.getLocationOnScreen());
            assertEquals(new java.awt.Point(23, 29), button.getLocation());
        } finally {
            panel.removeNotify();
            DomGlobal.document.body.removeChild(host);
        }
    }

    @Test
    public void mountsComponentTreeAndDispatchesButtonAction() {
        Element root = DomGlobal.document.createElement("div");
        root.setAttribute("id", "webawt-component-root");
        DomGlobal.document.body.appendChild(root);
        Panel panel = new Panel(null);
        try {
            WebAwtRuntime.installPeerFactory(new Elemental2DomPeerFactory());
            WebAwtRuntime.setRootElementId("webawt-component-root");
            panel.setBounds(0, 0, 200, 80);
            Label label = new Label("Ready");
            label.setBounds(4, 5, 80, 20);
            Button button = new Button("Save");
            button.setBounds(90, 5, 80, 24);
            int[] actions = new int[1];
            button.addActionListener(event -> actions[0]++);
            panel.add(label);
            panel.add(button);
            panel.addNotify();
            Element buttonElement = root.querySelector("button");
            assertEquals("ReadySave", root.textContent);
            assertEquals("Save", buttonElement.textContent);
            ((HTMLElement) buttonElement).click();
            assertEquals(1, actions[0]);
        } finally {
            panel.removeNotify();
            DomGlobal.document.body.removeChild(root);
        }
    }

    @Test
    public void synchronizesTextFieldInputWithoutSubmittingOnChange() {
        Element root = DomGlobal.document.createElement("div");
        root.setAttribute("id", "webawt-text-root");
        DomGlobal.document.body.appendChild(root);
        Panel panel = new Panel(null);
        try {
            WebAwtRuntime.installPeerFactory(new Elemental2DomPeerFactory());
            WebAwtRuntime.setRootElementId("webawt-text-root");
            TextField field = new TextField("initial", 12);
            int[] changes = new int[1];
            int[] actions = new int[1];
            field.addTextListener(event -> changes[0]++);
            field.addActionListener(event -> actions[0]++);
            field.setEchoChar('*');
            panel.add(field);
            panel.addNotify();
            HTMLInputElement input = (HTMLInputElement) root.querySelector("input");
            assertEquals("initial", input.value);
            assertEquals("password", input.type);
            input.value = "updated";
            input.dispatchEvent(new Event("input"));
            input.dispatchEvent(new Event("change"));
            assertEquals("updated", field.getText());
            assertEquals(1, changes[0]);
            assertEquals(0, actions[0]);
            field.setEditable(false);
            assertTrue(input.readOnly);
        } finally {
            panel.removeNotify();
            DomGlobal.document.body.removeChild(root);
        }
    }

    @Test
    public void translatesBrowserCompositionEventsToAwtInputMethodEvents() {
        Element root = DomGlobal.document.createElement("div");
        root.setAttribute("id", "webawt-composition-root");
        DomGlobal.document.body.appendChild(root);
        Panel panel = new Panel(null);
        try {
            WebAwtRuntime.installPeerFactory(new Elemental2DomPeerFactory());
            WebAwtRuntime.setRootElementId("webawt-composition-root");
            TextField field = new TextField();
            int[] events = new int[2];
            field.addInputMethodListener(new InputMethodListener() {
                @Override
                public void inputMethodTextChanged(InputMethodEvent event) {
                    events[0]++;
                    events[1] = event.getCommittedCharacterCount();
                }
                @Override
                public void caretPositionChanged(InputMethodEvent event) {
                }
            });
            panel.add(field);
            panel.addNotify();
            HTMLInputElement input = (HTMLInputElement) root.querySelector("input");
            input.dispatchEvent(new Event("compositionupdate"));
            assertEquals(1, events[0]);
            input.dispatchEvent(new Event("compositionend"));
            assertEquals(2, events[0]);
            assertEquals(0, events[1]);
        } finally {
            panel.removeNotify();
            DomGlobal.document.body.removeChild(root);
        }
    }


    @Test
    public void executesCanvasCommandsOnARealCanvasContext() {
        Element root = DomGlobal.document.createElement("div");
        root.setAttribute("id", "webawt-canvas-root");
        DomGlobal.document.body.appendChild(root);
        CanvasPeer peer = null;
        try {
            WebAwtRuntime.installPeerFactory(new Elemental2DomPeerFactory());
            peer = (CanvasPeer) WebAwtRuntime.createPeer("canvas");
            peer.setBounds(0, 0, 64, 32);
            peer.attachToElement("webawt-canvas-root");
            peer.setFillStyle("#ff0000");
            peer.fillRect(0, 0, 10, 10);
            peer.beginPath();
            peer.moveTo(0, 0);
            peer.lineTo(20, 20);
            peer.stroke();
            HTMLCanvasElement canvas = (HTMLCanvasElement) root.querySelector("canvas");
            assertEquals(64, canvas.width);
            assertEquals(32, canvas.height);
        } finally {
            if (peer != null) {
                peer.detach();
            }
            DomGlobal.document.body.removeChild(root);
        }
    }

    @Test
    public void paintsThroughAwtCanvasGraphics() {
        Element root = DomGlobal.document.createElement("div");
        root.setAttribute("id", "webawt-awt-canvas-root");
        DomGlobal.document.body.appendChild(root);
        Canvas canvas = new Canvas();
        try {
            WebAwtRuntime.installPeerFactory(new Elemental2DomPeerFactory());
            WebAwtRuntime.setRootElementId("webawt-awt-canvas-root");
            canvas.setBounds(0, 0, 80, 40);
            canvas.addNotify();
            Graphics2D graphics = (Graphics2D) canvas.getGraphics();
            graphics.setColor(Color.BLUE);
            graphics.fillRect(2, 3, 20, 10);
            graphics.drawOval(5, 5, 15, 12);
            graphics.dispose();
            HTMLCanvasElement element = (HTMLCanvasElement) root.querySelector("canvas");
            assertEquals(80, element.width);
            assertEquals(40, element.height);
        } finally {
            canvas.removeNotify();
            DomGlobal.document.body.removeChild(root);
        }
    }

    @Test
    public void mapsDialogToAnInPageDialogElement() {
        Element root = DomGlobal.document.createElement("div");
        root.setAttribute("id", "webawt-dialog-root");
        DomGlobal.document.body.appendChild(root);
        Dialog dialog = new Dialog((Window) null, "Confirm");
        try {
            WebAwtRuntime.installPeerFactory(new Elemental2DomPeerFactory());
            WebAwtRuntime.setRootElementId("webawt-dialog-root");
            Label message = new Label("Continue?");
            message.setPreferredSize(new java.awt.Dimension(100, 20));
            dialog.add(message);
            dialog.pack();
            dialog.setVisible(true);
            Element element = root.querySelector("dialog");
            assertTrue(element.textContent.contains("Continue?"));
            assertEquals("Confirm", element.getAttribute("aria-label"));
            assertEquals("Confirm", element.querySelector("[data-webawt-dialog-title] span").textContent);
            assertEquals(108, dialog.getWidth());
            assertEquals(54, dialog.getHeight());
            assertEquals(4, message.getX());
            assertEquals(30, message.getY());
            dialog.setName("independent-name");
            dialog.setTitle("Updated");
            assertEquals("Updated", element.getAttribute("aria-label"));
            assertEquals("independent-name", dialog.getName());
            int[] closing = {0};
            dialog.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent event) {
                    closing[0]++;
                }
            });
            HTMLElement closeButton = (HTMLElement) element.querySelector("button");
            closeButton.click();
            assertEquals(1, closing[0]);
            assertTrue(element.hasAttribute("open"));
            assertTrue(dialog.isShowing());
            dialog.setVisible(false);
            assertFalse(element.hasAttribute("open"));
            assertFalse(dialog.isShowing());
            dialog.dispose();
            closeButton.click();
            assertEquals(1, closing[0]);
        } finally {
            dialog.dispose();
            DomGlobal.document.body.removeChild(root);
        }
    }

    @Test
    public void leavesUndecoratedDialogsWithoutTitleOrInsets() {
        Element root = DomGlobal.document.createElement("div");
        root.setAttribute("id", "webawt-undecorated-dialog-root");
        DomGlobal.document.body.appendChild(root);
        Dialog dialog = new Dialog((Window) null, "Undecorated");
        try {
            WebAwtRuntime.installPeerFactory(new Elemental2DomPeerFactory());
            WebAwtRuntime.setRootElementId("webawt-undecorated-dialog-root");
            dialog.setUndecorated(true);
            Label message = new Label("Content only");
            message.setPreferredSize(new java.awt.Dimension(100, 20));
            dialog.add(message);
            dialog.pack();
            dialog.setVisible(true);
            Element element = root.querySelector("dialog");
            assertEquals("Content only", element.textContent);
            assertEquals(100, dialog.getWidth());
            assertEquals(20, dialog.getHeight());
            assertEquals(0, message.getX());
            assertEquals(0, message.getY());
            assertTrue(element.querySelector("button") == null);
        } finally {
            dialog.dispose();
            DomGlobal.document.body.removeChild(root);
        }
    }

    @Test
    public void mapsCheckboxStateAndItemEvents() {
        Element root = DomGlobal.document.createElement("div");
        root.setAttribute("id", "webawt-checkbox-root");
        DomGlobal.document.body.appendChild(root);
        Panel panel = new Panel(null);
        try {
            WebAwtRuntime.installPeerFactory(new Elemental2DomPeerFactory());
            WebAwtRuntime.setRootElementId("webawt-checkbox-root");
            Checkbox checkbox = new Checkbox("Updates", false);
            int[] events = new int[1];
            checkbox.addItemListener(event -> events[0]++);
            panel.add(checkbox);
            panel.addNotify();
            HTMLInputElement input = (HTMLInputElement) root.querySelector("input[type=checkbox]");
            assertEquals("Updates", input.getAttribute("aria-label"));
            input.click();
            assertTrue(checkbox.getState());
            assertEquals(1, events[0]);
        } finally {
            panel.removeNotify();
            DomGlobal.document.body.removeChild(root);
        }
    }

    @Test
    public void synchronizesRadioCaptionsGroupsFocusAndDisposal() {
        Element root = createControlRoot();
        Panel panel = new Panel(null);
        panel.setBounds(0, 0, 400, 220);
        try {
            CheckboxGroup group = new CheckboxGroup();
            Checkbox first = new Checkbox("First", true, group);
            Checkbox second = new Checkbox("Second", false, group);
            first.setBounds(10, 10, 300, 80);
            second.setBounds(10, 100, 300, 80);
            panel.add(first);
            panel.add(second);
            int[] changes = new int[1];
            int[] clicks = new int[1];
            second.addItemListener(event -> changes[0]++);
            second.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent event) {
                    clicks[0]++;
                }
            });
            panel.addNotify();
            HTMLInputElement firstInput = (HTMLInputElement) root.querySelector("input[aria-label=First]");
            HTMLInputElement secondInput = (HTMLInputElement) root.querySelector("input[aria-label=Second]");
            assertEquals("radio", firstInput.type);
            assertEquals(firstInput.name, secondInput.name);
            assertTrue(firstInput.checked);
            ((HTMLElement) secondInput.parentNode.lastChild).click();
            assertTrue(second.getState());
            assertFalse(first.getState());
            assertFalse(firstInput.checked);
            assertEquals(1, changes[0]);
            assertEquals(1, clicks[0]);
            secondInput.click();
            assertTrue(second.getState());
            assertEquals(1, changes[0]);
            second.requestFocus();
            assertTrue(DomGlobal.document.activeElement == secondInput);
            assertTrue(KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner() == second);
            second.setLabel("Renamed");
            assertEquals("Renamed", secondInput.parentNode.textContent);
            second.setCheckboxGroup(null);
            assertEquals("checkbox", secondInput.type);
            assertEquals("", secondInput.name);
            assertTrue(secondInput.checked);
            second.setEnabled(false);
            secondInput.click();
            assertTrue(second.getState());
            assertTrue(secondInput.disabled);
            second.setEnabled(true);
            second.setState(false);
            assertFalse(secondInput.checked);
            assertEquals(1, changes[0]);
            panel.removeNotify();
            secondInput.click();
            assertEquals(1, changes[0]);
            panel.addNotify();
            assertEquals(2, root.querySelectorAll("label").length);
        } finally {
            panel.removeNotify();
            DomGlobal.document.body.removeChild(root);
        }
    }

    @Test
    public void alignsLabelTextAndUpdatesInheritedFontMetrics() {
        Element root = createControlRoot();
        Panel panel = new Panel(null);
        panel.setBounds(0, 0, 400, 220);
        panel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        Label label = new Label("Aligned", Label.RIGHT);
        label.setBounds(10, 10, 300, 80);
        panel.add(label);
        try {
            int previousWidth = label.getPreferredSize().width;
            panel.addNotify();
            HTMLElement outer = (HTMLElement) root.querySelector("span");
            HTMLElement caption = (HTMLElement) outer.firstChild;
            assertEquals(outer.getBoundingClientRect().right - 2, caption.getBoundingClientRect().right, 1.0);
            assertEquals(outer.getBoundingClientRect().top + 40, caption.getBoundingClientRect().top + caption.getBoundingClientRect().height / 2, 1.0);
            label.setAlignment(Label.CENTER);
            assertEquals(outer.getBoundingClientRect().left + 150, caption.getBoundingClientRect().left + caption.getBoundingClientRect().width / 2, 1.0);
            label.setAlignment(Label.LEFT);
            assertEquals(outer.getBoundingClientRect().left + 2, caption.getBoundingClientRect().left, 1.0);
            panel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 24));
            assertTrue(label.getPreferredSize().width > previousWidth);
            assertEquals("24px", outer.style.getPropertyValue("font-size"));
            assertEquals("sans-serif", outer.style.getPropertyValue("font-family"));
            label.setVisible(false);
            label.setVisible(true);
            assertEquals(300.0, outer.getBoundingClientRect().width, 1.0);
            assertEquals(outer.getBoundingClientRect().left + 2, caption.getBoundingClientRect().left, 1.0);
            label.setText(null);
            assertEquals("", caption.textContent);
        } finally {
            panel.removeNotify();
            DomGlobal.document.body.removeChild(root);
        }
    }

    @Test
    public void reservesMenuSpaceAcrossFontResizeReplacementAndRemount() {
        Element root = createControlRoot();
        Frame frame = new Frame();
        MenuBar bar = new MenuBar();
        bar.add(new Menu("Gallery"));
        bar.add(new Menu("Actions"));
        Menu help = new Menu("Help");
        bar.setHelpMenu(help);
        Label content = new Label("Content");
        frame.add(content);
        frame.setMenuBar(bar);
        frame.setSize(500, 300);
        try {
            frame.addNotify();
            frame.validate();
            HTMLElement menu = (HTMLElement) root.querySelector("nav");
            assertTrue(frame.getInsets().top > 0);
            assertEquals(frame.getInsets().top, content.getY());
            assertEquals(500.0, menu.getBoundingClientRect().width, 1.0);
            int previousHeight = frame.getInsets().top;
            bar.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 24));
            assertTrue(frame.getInsets().top > previousHeight);
            assertEquals(frame.getInsets().top, content.getY());
            frame.setSize(160, 300);
            assertEquals(160.0, menu.getBoundingClientRect().width, 1.0);
            assertEquals(frame.getInsets().top, content.getY());
            frame.setMenuBar(null);
            assertEquals(0, root.querySelectorAll("nav").length);
            assertEquals(0, content.getY());
            MenuBar replacement = new MenuBar();
            replacement.add(new Menu("New"));
            frame.setMenuBar(replacement);
            assertEquals(1, root.querySelectorAll("nav").length);
            assertEquals(frame.getInsets().top, content.getY());
            frame.setVisible(false);
            replacement.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 18));
            frame.setVisible(true);
            assertEquals(((HTMLElement) root.querySelector("nav")).getBoundingClientRect().height, content.getY(), 1.0);
            frame.removeNotify();
            frame.addNotify();
            frame.validate();
            assertEquals(1, root.querySelectorAll("nav").length);
            assertEquals(frame.getInsets().top, content.getY());
        } finally {
            frame.dispose();
            DomGlobal.document.body.removeChild(root);
        }
    }

    @Test
    public void anchorsPopupToAScrolledDescendantAndFitsViewportEdges() {
        Element root = createControlRoot();
        Frame frame = new Frame("Popup origin");
        frame.setLayout(null);
        frame.setBounds(20, 30, 600, 400);
        try {
            ScrollPane scroll = new ScrollPane();
            scroll.setBounds(25, 35, 250, 200);
            Panel content = new Panel(null);
            content.setPreferredSize(new java.awt.Dimension(800, 600));
            Button origin = new Button("Popup origin");
            origin.setBounds(140, 120, 100, 30);
            content.add(origin);
            scroll.add(content);
            frame.add(scroll);
            PopupMenu popup = new PopupMenu();
            popup.add(new MenuItem("Action"));
            Menu nested = new Menu("Nested");
            nested.add(new MenuItem("Nested action"));
            popup.add(nested);
            frame.add(popup);
            frame.setVisible(true);
            scroll.doLayout();
            scroll.setScrollPosition(100, 80);
            popup.show(origin, 12, 24);
            HTMLElement button = (HTMLElement) root.querySelector("button:not([role])");
            HTMLElement menu = (HTMLElement) root.querySelector("[role=menu]");
            assertEquals(button.getBoundingClientRect().left + 12, menu.getBoundingClientRect().left, 1.0);
            assertEquals(button.getBoundingClientRect().top + 24, menu.getBoundingClientRect().top, 1.0);
            popup.show(origin, DomGlobal.window.innerWidth, DomGlobal.window.innerHeight);
            assertTrue(menu.getBoundingClientRect().left >= 0);
            assertTrue(menu.getBoundingClientRect().top >= 0);
            assertTrue(menu.getBoundingClientRect().right <= DomGlobal.window.innerWidth + 1);
            assertTrue(menu.getBoundingClientRect().bottom <= DomGlobal.window.innerHeight + 1);
            HTMLElement submenu = (HTMLElement) menu.querySelector("[role=menu]");
            HTMLElement nestedButton = (HTMLElement) submenu.parentNode.firstChild;
            nestedButton.click();
            assertEquals("block", submenu.style.display);
            assertTrue(submenu.getBoundingClientRect().left >= 0);
            assertEquals(nestedButton.getBoundingClientRect().left, submenu.getBoundingClientRect().right, 1.0);
            assertTrue(submenu.getBoundingClientRect().bottom <= DomGlobal.window.innerHeight + 1);
            origin.setVisible(false);
            boolean rejected = false;
            try {
                popup.show(origin, 0, 0);
            } catch (java.awt.IllegalComponentStateException expected) {
                rejected = true;
            }
            assertTrue(rejected);
        } finally {
            frame.dispose();
            assertEquals(null, root.querySelector("[role=menu]"));
            DomGlobal.document.body.removeChild(root);
        }
    }

    private static Element createControlRoot() {
        Element root = DomGlobal.document.createElement("div");
        root.id = "webawt-control-layout-root";
        DomGlobal.document.body.appendChild(root);
        WebAwtRuntime.installPeerFactory(new Elemental2DomPeerFactory());
        WebAwtRuntime.installMenuPeerFactory(new Elemental2MenuPeerFactory());
        WebAwtRuntime.setRootElementId(root.id);
        return root;
    }

    @Test
    public void mapsChoiceItemsSelectionAndItemEvents() {
        Element root = DomGlobal.document.createElement("div");
        root.setAttribute("id", "webawt-choice-root");
        DomGlobal.document.body.appendChild(root);
        Panel panel = new Panel(null);
        try {
            WebAwtRuntime.installPeerFactory(new Elemental2DomPeerFactory());
            WebAwtRuntime.setRootElementId("webawt-choice-root");
            Choice choice = new Choice();
            choice.add("Red");
            choice.add("Green");
            int[] events = new int[1];
            choice.addItemListener(event -> events[0]++);
            panel.add(choice);
            panel.addNotify();
            HTMLSelectElement select = (HTMLSelectElement) root.querySelector("select");
            assertEquals(2, select.options.length);
            assertEquals(0, select.selectedIndex);
            select.selectedIndex = 1;
            select.dispatchEvent(new Event("change"));
            assertEquals("Green", choice.getSelectedItem());
            assertEquals(1, events[0]);
        } finally {
            panel.removeNotify();
            DomGlobal.document.body.removeChild(root);
        }
    }

    @Test
    public void mapsMultipleListSelection() {
        Element root = DomGlobal.document.createElement("div");
        root.setAttribute("id", "webawt-list-root");
        DomGlobal.document.body.appendChild(root);
        Panel panel = new Panel(null);
        try {
            WebAwtRuntime.installPeerFactory(new Elemental2DomPeerFactory());
            WebAwtRuntime.setRootElementId("webawt-list-root");
            List list = new List(3, true);
            list.add("One");
            list.add("Two");
            list.add("Three");
            int[] events = new int[1];
            list.addItemListener(event -> events[0]++);
            panel.add(list);
            panel.addNotify();
            HTMLSelectElement select = (HTMLSelectElement) root.querySelector("select");
            assertTrue(select.multiple);
            select.options.item(0).selected = true;
            select.options.item(2).selected = true;
            select.dispatchEvent(new Event("change"));
            assertEquals(2, list.getSelectedIndexes().length);
            assertEquals("Three", list.getSelectedItems()[1]);
            assertEquals(2, events[0]);
        } finally {
            panel.removeNotify();
            DomGlobal.document.body.removeChild(root);
        }
    }

    @Test
    public void translatesFocusAndPointerEvents() {
        Element root = DomGlobal.document.createElement("div");
        root.setAttribute("id", "webawt-input-root");
        DomGlobal.document.body.appendChild(root);
        Panel panel = new Panel(null);
        try {
            WebAwtRuntime.installPeerFactory(new Elemental2DomPeerFactory());
            WebAwtRuntime.setRootElementId("webawt-input-root");
            Button button = new Button("Input");
            int[] counts = new int[2];
            button.addFocusListener(new java.awt.event.FocusAdapter() {
                @Override
                public void focusGained(java.awt.event.FocusEvent event) {
                    counts[0]++;
                }
            });
            button.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent event) {
                    counts[1]++;
                }
            });
            panel.add(button);
            panel.addNotify();
            HTMLElement element = (HTMLElement) root.querySelector("button");
            element.dispatchEvent(new Event("focus"));
            assertEquals("focus", 1, counts[0]);
            assertEquals(button, KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner());
            element.click();
            assertEquals("mouse", 1, counts[1]);
            element.dispatchEvent(new Event("blur"));
            assertEquals(null, KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner());
        } finally {
            panel.removeNotify();
            DomGlobal.document.body.removeChild(root);
        }
    }
}
