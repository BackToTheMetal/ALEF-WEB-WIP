package org.webawt.samples.showcase;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Canvas;
import java.awt.CheckboxMenuItem;
import java.awt.Choice;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.KeyboardFocusManager;
import java.awt.Label;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.Panel;
import java.awt.PopupMenu;
import java.awt.ScrollPane;
import java.awt.Scrollbar;
import java.awt.Toolkit;
import java.awt.TextField;
import java.awt.event.InputMethodEvent;
import java.awt.event.InputMethodListener;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragSource;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import jsinterop.annotations.JsMethod;
import org.webawt.WebAwt;

public final class Showcase {
    private static final String TEST_IMAGE_SOURCE = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR4nGP4z8DwHwAFAAH/iZk9HQAAAABJRU5ErkJggg==";
    private static Panel root;
    private static Canvas canvas;
    private static Choice colors;
    private static CheckboxMenuItem popupCheckbox;
    private static Frame menuFrame;
    private static PopupMenu popupMenu;
    private static ScrollPane scrollPane;
    private static Scrollbar scrollbar;
    private static int menuActionCount;
    private static int scrollbarEventCount;
    private static Image testImage;
    private static int compositionEventCount;
    private static int committedCompositionCharacterCount;
    private static int dropCount;

    private Showcase() {
    }

    @JsMethod
    public static void mount(String elementId) {
        WebAwt.install(elementId);
        menuActionCount = 0;
        scrollbarEventCount = 0;
        compositionEventCount = 0;
        committedCompositionCharacterCount = 0;
        dropCount = 0;
        root = new Panel(new BorderLayout());
        Label status = new Label("Ready");
        colors = new Choice();
        colors.setName("colors");
        colors.add("Blue");
        colors.add("Red");
        Button update = new Button("Update");
        update.setName("update");
        update.addActionListener(event -> status.setText(colors.getSelectedItem()));
        Panel controls = new Panel();
        controls.add(colors);
        controls.add(update);
        Label dragSourceLabel = new Label("Drag payload");
        Label dropTargetLabel = new Label("Drop here");
        DragSource dragSource = DragSource.getDefaultDragSource();
        dragSource.createDefaultDragGestureRecognizer(dragSourceLabel, DnDConstants.ACTION_COPY, event -> event.startDrag(DragSource.DefaultCopyDrop, new StringSelection("WebAWT drag payload")));
        new DropTarget(dropTargetLabel, DnDConstants.ACTION_COPY, new DropTargetAdapter() {
            @Override
            public void dragEnter(DropTargetDragEvent event) {
                event.acceptDrag(DnDConstants.ACTION_COPY);
            }
            @Override
            public void dragOver(DropTargetDragEvent event) {
                event.acceptDrag(DnDConstants.ACTION_COPY);
            }
            @Override
            public void drop(DropTargetDropEvent event) {
                try {
                    event.acceptDrop(DnDConstants.ACTION_COPY);
                    String droppedText = (String) event.getTransferable().getTransferData(DataFlavor.stringFlavor);
                    dropTargetLabel.setText("Dropped: " + droppedText);
                    dropCount++;
                    event.dropComplete(true);
                } catch (Exception exception) {
                    event.rejectDrop();
                    event.dropComplete(false);
                }
            }
        });
        TextField composition = new TextField();
        composition.setName("composition");
        composition.addInputMethodListener(new InputMethodListener() {
            @Override
            public void inputMethodTextChanged(InputMethodEvent event) {
                compositionEventCount++;
                committedCompositionCharacterCount = event.getCommittedCharacterCount();
            }
            @Override
            public void caretPositionChanged(InputMethodEvent event) {
            }
        });
        canvas = new Canvas();
        canvas.setPreferredSize(new java.awt.Dimension(240, 120));
        root.add(status, BorderLayout.NORTH);
        root.add(controls, BorderLayout.SOUTH);
        root.add(canvas, BorderLayout.CENTER);
        popupMenu = new PopupMenu("Actions");
        MenuItem popupAction = popupMenu.add(new MenuItem("Popup action"));
        popupAction.addActionListener(event -> menuActionCount++);
        popupCheckbox = new CheckboxMenuItem("Popup option");
        popupMenu.add(popupCheckbox);
        popupMenu.addSeparator();
        root.add(popupMenu);
        root.setBounds(0, 0, 320, 220);
        root.addNotify();
        root.doLayout();
        menuFrame = new Frame("Menus");
        MenuBar menuBar = new MenuBar();
        Menu file = menuBar.add(new Menu("File"));
        MenuItem menuAction = file.add(new MenuItem("Menu action"));
        menuAction.addActionListener(event -> menuActionCount++);
        menuFrame.setMenuBar(menuBar);
        menuFrame.setLayout(null);
        scrollPane = new ScrollPane();
        scrollPane.setBounds(8, 8, 220, 72);
        Panel scrollContent = new Panel(null);
        scrollContent.setPreferredSize(new Dimension(420, 220));
        Label distantContent = new Label("Distant content");
        distantContent.setBounds(300, 160, 110, 24);
        scrollContent.add(distantContent);
        scrollPane.add(scrollContent);
        menuFrame.add(scrollPane);
        scrollbar = new Scrollbar(Scrollbar.HORIZONTAL, 0, 10, 0, 100);
        scrollbar.setBounds(8, 92, 220, 20);
        scrollbar.addAdjustmentListener(event -> scrollbarEventCount++);
        menuFrame.add(scrollbar);
        composition.setBounds(8, 118, 220, 24);
        menuFrame.add(composition);
        dragSourceLabel.setBounds(8, 148, 110, 24);
        dropTargetLabel.setBounds(130, 148, 160, 24);
        menuFrame.add(dragSourceLabel);
        menuFrame.add(dropTargetLabel);
        menuFrame.setBounds(340, 0, 300, 184);
        menuFrame.addNotify();
        scrollPane.doLayout();
        Graphics2D graphics = (Graphics2D) canvas.getGraphics();
        graphics.setColor(Color.BLUE);
        graphics.fillRoundRect(20, 20, 160, 60, 16, 16);
        graphics.dispose();
    }

    @JsMethod
    public static void unmount() {
        if (root != null) {
            root.removeNotify();
            root = null;
            colors = null;
            popupMenu = null;
            popupCheckbox = null;
            testImage = null;
            canvas = null;
        }
        if (menuFrame != null) {
            menuFrame.removeNotify();
            menuFrame = null;
            scrollPane = null;
            scrollbar = null;
        }
    }

    @JsMethod
    public static String focusOwnerName() {
        java.awt.Component focusOwner = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
        return focusOwner == null ? null : focusOwner.getName();
    }

    @JsMethod
    public static int graphicsEnvironmentWidth() {
        return graphicsConfiguration().getBounds().width;
    }

    @JsMethod
    public static int graphicsEnvironmentHeight() {
        return graphicsConfiguration().getBounds().height;
    }

    @JsMethod
    public static double graphicsEnvironmentScale() {
        return graphicsConfiguration().getDefaultTransform().getScaleX();
    }

    @JsMethod
    public static int toolkitScreenWidth() {
        return Toolkit.getDefaultToolkit().getScreenSize().width;
    }

    @JsMethod
    public static int colorChoiceMouseX() {
        java.awt.Point position = colors == null ? null : colors.getMousePosition();
        return position == null ? -1 : position.x;
    }

    @JsMethod
    public static int colorChoiceMouseY() {
        java.awt.Point position = colors == null ? null : colors.getMousePosition();
        return position == null ? -1 : position.y;
    }

    @JsMethod
    public static void showPopupMenu() {
        if (popupMenu != null && root != null) {
            popupMenu.show(root, 20, 40);
        }
    }

    @JsMethod
    public static int menuActionCount() {
        return menuActionCount;
    }

    @JsMethod
    public static boolean isPopupOptionSelected() {
        return popupCheckbox != null && popupCheckbox.getState();
    }

    @JsMethod
    public static int scrollPaneX() {
        return scrollPane == null ? -1 : scrollPane.getScrollPosition().x;
    }

    @JsMethod
    public static int scrollPaneY() {
        return scrollPane == null ? -1 : scrollPane.getScrollPosition().y;
    }

    @JsMethod
    public static int scrollbarValue() {
        return scrollbar == null ? -1 : scrollbar.getValue();
    }

    @JsMethod
    public static int scrollbarEventCount() {
        return scrollbarEventCount;
    }

    @JsMethod
    public static int compositionEventCount() {
        return compositionEventCount;
    }

    @JsMethod
    public static int committedCompositionCharacterCount() {
        return committedCompositionCharacterCount;
    }

    @JsMethod
    public static int dropCount() {
        return dropCount;
    }

    @JsMethod
    public static void startImageDecode() {
        testImage = Toolkit.getDefaultToolkit().createImage(TEST_IMAGE_SOURCE);
    }

    @JsMethod
    public static int imageDecodeStatus() {
        return testImage == null ? 0 : Toolkit.getDefaultToolkit().checkImage(testImage, -1, -1, null);
    }

    @JsMethod
    public static int imageDecodeWidth() {
        return testImage == null ? -1 : testImage.getWidth(null);
    }

    @JsMethod
    public static int imageDecodeHeight() {
        return testImage == null ? -1 : testImage.getHeight(null);
    }

    @JsMethod
    public static boolean drawDecodedImage() {
        if (canvas == null || testImage == null) {
            return false;
        }
        Graphics2D graphics = (Graphics2D) canvas.getGraphics();
        boolean complete = graphics.drawImage(testImage, 200, 20, 220, 40, 0, 0, 1, 1, null);
        graphics.dispose();
        return complete;
    }

    private static GraphicsConfiguration graphicsConfiguration() {
        return GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
    }
}
