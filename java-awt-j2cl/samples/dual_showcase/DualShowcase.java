package org.webawt.samples.dualshowcase;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Canvas;
import java.awt.CardLayout;
import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.CheckboxMenuItem;
import java.awt.Choice;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Label;
import java.awt.LinearGradientPaint;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.MenuShortcut;
import java.awt.MultipleGradientPaint;
import java.awt.Panel;
import java.awt.PopupMenu;
import java.awt.RenderingHints;
import java.awt.ScrollPane;
import java.awt.Scrollbar;
import java.awt.SystemColor;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragSource;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.awt.image.RescaleOp;
import java.net.URI;
import java.util.Locale;
import java.util.function.Consumer;

public final class DualShowcase {
    private static final String[] PAGE_NAMES = {"Controls & events", "Layouts", "Graphics2D", "Images & color", "Text & focus", "Menus & scrolling", "DnD & clipboard", "Windows & Desktop", "Runtime & limits", "Keyboard & input", "Rendering lab", "Lifecycle & accessibility"};
    private final String runtimeName;
    private final ClipboardAccess clipboardAccess;
    private final CardLayout cardLayout = new CardLayout();
    private final Panel cards = new Panel(cardLayout);
    private Frame frame;
    private Choice pageChoice;
    private Label status;
    private TextArea eventLog;
    private GalleryCanvas galleryCanvas;
    private ImageCanvas imageCanvas;
    private PopupMenu popupMenu;
    private Button popupButton;
    private CheckboxMenuItem darkThemeItem;
    private Label dragTarget;
    private String selectedPage = PAGE_NAMES[0];
    private int eventCount;
    private KeyboardInputPage keyboardPage;
    private RenderingLabPage renderingPage;
    private FontLoading fontLoading;
    private LifecyclePage lifecyclePage;

    public DualShowcase(String runtimeName) {
        this(runtimeName, new ClipboardAccess());
    }

    public DualShowcase(String runtimeName, ClipboardAccess clipboardAccess) {
        if (runtimeName == null) {
            throw new NullPointerException("runtimeName");
        }
        if (clipboardAccess == null) {
            throw new NullPointerException("clipboardAccess");
        }
        this.runtimeName = runtimeName;
        this.clipboardAccess = clipboardAccess;
    }

    public void setFontLoading(FontLoading loading) {
        fontLoading = loading;
    }

    public void show() {
        if (frame != null) {
            frame.setVisible(true);
            return;
        }
        frame = new Frame("WebAWT dual-runtime capability gallery");
        frame.setName("dualShowcaseFrame");
        frame.setLayout(new BorderLayout(8, 8));
        frame.setMenuBar(createMenuBar());
        frame.add(createHeader(), BorderLayout.NORTH);
        addPages();
        frame.add(cards, BorderLayout.CENTER);
        status = new Label("Ready on " + runtimeName);
        status.setName("showcaseStatus");
        frame.add(status, BorderLayout.SOUTH);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                dispose();
            }
        });
        frame.setSize(1120, 780);
        frame.validate();
        frame.setVisible(true);
        EventQueue.invokeLater(this::paintDemos);
    }

    public void dispose() {
        if (frame != null) {
            keyboardPage.dispose();
            renderingPage.dispose();
            lifecyclePage.dispose();
            Frame current = frame;
            frame = null;
            current.dispose();
        }
    }

    public Frame getFrame() {
        return frame;
    }

    public int getPageCount() {
        return PAGE_NAMES.length;
    }

    public String getSelectedPage() {
        return selectedPage;
    }

    public String getStatusText() {
        return status == null ? null : status.getText();
    }

    public String getKeyboardTrace() {
        return keyboardPage == null ? "" : keyboardPage.traceText();
    }

    public String getKeyboardSelection() {
        return keyboardPage == null ? "" : keyboardPage.selectionState();
    }

    public void selectPage(String pageName) {
        if (!isPage(pageName)) {
            throw new IllegalArgumentException("Unknown page: " + pageName);
        }
        selectedPage = pageName;
        if (pageChoice != null) {
            pageChoice.select(pageName);
        }
        cardLayout.show(cards, pageName);
        setStatus("Showing " + pageName);
        if ("Graphics2D".equals(pageName) || "Images & color".equals(pageName) || "Rendering lab".equals(pageName) || "Keyboard & input".equals(pageName)) {
            EventQueue.invokeLater(this::paintDemos);
        }
    }

    public void paintDemos() {
        repaintNow(galleryCanvas);
        repaintNow(imageCanvas);
        if (keyboardPage != null) {
            keyboardPage.paintNow();
        }
        if (renderingPage != null) {
            renderingPage.paintNow();
        }
    }

    public boolean runSelfCheck() {
        try {
            Area area = new Area(new Rectangle2D.Double(0, 0, 100, 100));
            area.intersect(new Area(new Ellipse2D.Double(10, 10, 80, 80)));
            AffineTransform transform = AffineTransform.getRotateInstance(Math.PI / 4.0, 50.0, 50.0);
            Point2D point = transform.transform(new Point2D.Double(50.0, 20.0), null);
            BufferedImage source = createSourceImage();
            BufferedImage gray = new ColorConvertOp(java.awt.color.ColorSpace.getInstance(java.awt.color.ColorSpace.CS_GRAY), null).filter(source, null);
            boolean passed = area.contains(50.0, 50.0) && point.getX() > 70.0 && gray.getWidth() == source.getWidth() && gray.getColorModel().getColorSpace().getType() == java.awt.color.ColorSpace.TYPE_GRAY;
            setStatus(passed ? "Self-check passed: geometry, transforms, images and color" : "Self-check failed");
            return passed;
        } catch (RuntimeException exception) {
            setStatus("Self-check failed: " + exception.getClass().getSimpleName());
            return false;
        }
    }

    public void showPopupMenu() {
        if (popupMenu != null && popupButton != null) {
            popupMenu.show(popupButton, 12, 24);
        }
    }

    private Panel createHeader() {
        Panel header = new Panel(new BorderLayout(12, 0));
        Label title = new Label("WebAWT capability gallery - " + runtimeName);
        title.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
        pageChoice = new Choice();
        pageChoice.setName("pageChoice");
        for (String pageName : PAGE_NAMES) {
            pageChoice.add(pageName);
        }
        pageChoice.addItemListener(event -> selectPage(pageChoice.getSelectedItem()));
        header.add(title, BorderLayout.CENTER);
        header.add(pageChoice, BorderLayout.EAST);
        return header;
    }

    private void addPages() {
        cards.add(createControlsPage(), PAGE_NAMES[0]);
        cards.add(createLayoutsPage(), PAGE_NAMES[1]);
        cards.add(createGraphicsPage(), PAGE_NAMES[2]);
        cards.add(createImagesPage(), PAGE_NAMES[3]);
        cards.add(createTextPage(), PAGE_NAMES[4]);
        cards.add(createMenusPage(), PAGE_NAMES[5]);
        cards.add(createDndPage(), PAGE_NAMES[6]);
        cards.add(createDesktopPage(), PAGE_NAMES[7]);
        cards.add(createRuntimePage(), PAGE_NAMES[8]);
        keyboardPage = new KeyboardInputPage();
        renderingPage = new RenderingLabPage(fontLoading);
        lifecyclePage = new LifecyclePage(frame);
        cards.add(keyboardPage, PAGE_NAMES[9]);
        cards.add(renderingPage, PAGE_NAMES[10]);
        cards.add(lifecyclePage, PAGE_NAMES[11]);
    }

    private Panel createControlsPage() {
        Panel page = page("Standard controls and portable event delivery");
        Panel form = new Panel(new GridLayout(0, 2, 8, 8));
        TextField name = new TextField("Ada", 18);
        name.setName("nameField");
        name.addActionListener(event -> record("TextField action: " + name.getText()));
        name.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent event) {
                record("TextField focus gained");
            }
        });
        name.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent event) {
                setStatus("Key pressed: " + event.getKeyCode());
            }
        });
        form.add(new Label("TextField"));
        form.add(name);
        Choice accent = new Choice();
        accent.setName("accentChoice");
        accent.add("Ocean");
        accent.add("Forest");
        accent.add("Sunset");
        accent.addItemListener(event -> record("Choice: " + accent.getSelectedItem()));
        form.add(new Label("Choice"));
        form.add(accent);
        Checkbox enabled = new Checkbox("Enabled", true);
        enabled.setName("enabledCheckbox");
        enabled.addItemListener(event -> record("Checkbox: " + enabled.getState()));
        form.add(new Label("Checkbox"));
        form.add(enabled);
        CheckboxGroup mode = new CheckboxGroup();
        Panel radios = new Panel(new FlowLayout(FlowLayout.LEFT));
        Checkbox compact = new Checkbox("Compact", true, mode);
        Checkbox comfortable = new Checkbox("Comfortable", false, mode);
        compact.addItemListener(event -> record("Radio: Compact"));
        comfortable.addItemListener(event -> record("Radio: Comfortable"));
        radios.add(compact);
        radios.add(comfortable);
        form.add(new Label("CheckboxGroup"));
        form.add(radios);
        java.awt.List features = new java.awt.List(4, true);
        features.setName("featureList");
        features.add("DOM controls");
        features.add("Canvas graphics");
        features.add("Portable events");
        features.add("J2CL linking");
        features.select(0);
        features.addItemListener(event -> record("List selections: " + features.getSelectedIndexes().length));
        form.add(new Label("Multi-select List"));
        form.add(features);
        Scrollbar value = new Scrollbar(Scrollbar.HORIZONTAL, 35, 10, 0, 110);
        value.setName("controlScrollbar");
        Label valueLabel = new Label("35");
        value.addAdjustmentListener(event -> {
            valueLabel.setText(Integer.toString(value.getValue()));
            record("Scrollbar: " + value.getValue());
        });
        Panel slider = new Panel(new BorderLayout());
        slider.add(value, BorderLayout.CENTER);
        slider.add(valueLabel, BorderLayout.EAST);
        form.add(new Label("Scrollbar"));
        form.add(slider);
        Button apply = new Button("Apply controls");
        apply.setName("applyControls");
        apply.addActionListener(event -> record("Button action for " + name.getText()));
        apply.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent event) {
                setStatus("Pointer entered the Apply button");
            }
        });
        form.add(new Label("Button"));
        form.add(apply);
        eventLog = new TextArea("Event log\n", 12, 34, TextArea.SCROLLBARS_VERTICAL_ONLY);
        eventLog.setName("eventLog");
        eventLog.setEditable(false);
        page.add(form, BorderLayout.CENTER);
        page.add(eventLog, BorderLayout.EAST);
        return page;
    }

    private Panel createLayoutsPage() {
        Panel page = page("BorderLayout, FlowLayout, GridLayout and GridBagLayout; this page switcher itself uses CardLayout");
        Panel examples = new Panel(new GridLayout(2, 2, 10, 10));
        Panel border = layoutPanel(new BorderLayout(4, 4));
        border.add(sampleLabel("North", new Color(220, 235, 255)), BorderLayout.NORTH);
        border.add(sampleLabel("West", new Color(235, 245, 235)), BorderLayout.WEST);
        border.add(sampleLabel("Center", new Color(255, 245, 220)), BorderLayout.CENTER);
        border.add(sampleLabel("East", new Color(245, 230, 245)), BorderLayout.EAST);
        border.add(sampleLabel("South", new Color(235, 235, 235)), BorderLayout.SOUTH);
        Panel flow = layoutPanel(new FlowLayout(FlowLayout.CENTER, 6, 6));
        for (int index = 1; index <= 7; index++) {
            flow.add(new Button("Item " + index));
        }
        Panel grid = layoutPanel(new GridLayout(3, 3, 4, 4));
        for (int index = 1; index <= 9; index++) {
            grid.add(sampleLabel(Integer.toString(index), index % 2 == 0 ? new Color(225, 240, 255) : new Color(245, 245, 245)));
        }
        Panel bag = layoutPanel(new GridBagLayout());
        addGridBag(bag, new Label("Flexible"), 0, 0, 1, 0.0);
        addGridBag(bag, new TextField("grows horizontally"), 1, 0, 2, 1.0);
        addGridBag(bag, new Button("Left"), 0, 1, 1, 0.0);
        addGridBag(bag, new Button("Wide action"), 1, 1, 2, 1.0);
        examples.add(border);
        examples.add(flow);
        examples.add(grid);
        examples.add(bag);
        page.add(examples, BorderLayout.CENTER);
        return page;
    }

    private Panel createGraphicsPage() {
        Panel page = page("Canvas and Graphics2D: shapes, paths, gradients, alpha, strokes, text and transforms");
        galleryCanvas = new GalleryCanvas();
        galleryCanvas.setName("graphicsCanvas");
        galleryCanvas.setPreferredSize(new Dimension(940, 560));
        Panel actions = new Panel(new FlowLayout(FlowLayout.LEFT));
        Button repaint = new Button("Repaint canvas");
        repaint.setName("repaintGraphics");
        repaint.addActionListener(event -> repaintNow(galleryCanvas));
        Button rotate = new Button("Rotate motif");
        rotate.addActionListener(event -> {
            galleryCanvas.rotateMotif();
            repaintNow(galleryCanvas);
        });
        actions.add(repaint);
        actions.add(rotate);
        page.add(galleryCanvas, BorderLayout.CENTER);
        page.add(actions, BorderLayout.SOUTH);
        return page;
    }

    private Panel createImagesPage() {
        Panel page = page("BufferedImage, Raster, RescaleOp, ConvolveOp, AffineTransformOp, ColorConvertOp and ICC standard spaces");
        imageCanvas = new ImageCanvas();
        imageCanvas.setName("imageCanvas");
        imageCanvas.setPreferredSize(new Dimension(940, 520));
        Button rebuild = new Button("Rebuild image pipeline");
        rebuild.setName("rebuildImages");
        rebuild.addActionListener(event -> repaintNow(imageCanvas));
        page.add(imageCanvas, BorderLayout.CENTER);
        page.add(rebuild, BorderLayout.SOUTH);
        return page;
    }

    private Panel createTextPage() {
        Panel page = page("Native text controls, fonts, selection, caret, orientation and focus traversal");
        Panel editor = new Panel(new BorderLayout(8, 8));
        TextArea text = new TextArea("The same java.awt text component runs on the JDK and in a browser.\nSelect text, change its font, or reverse component orientation.", 10, 58, TextArea.SCROLLBARS_BOTH);
        text.setName("textEditor");
        text.addTextListener(event -> setStatus("Text event: " + text.getText().length() + " characters"));
        Panel tools = new Panel(new FlowLayout(FlowLayout.LEFT));
        Choice family = new Choice();
        family.setName("fontFamily");
        family.add(Font.SANS_SERIF);
        family.add(Font.SERIF);
        family.add(Font.MONOSPACED);
        Choice size = new Choice();
        size.add("12");
        size.add("16");
        size.add("22");
        size.select("16");
        Checkbox bold = new Checkbox("Bold");
        Button applyFont = new Button("Apply font");
        applyFont.addActionListener(event -> {
            int style = bold.getState() ? Font.BOLD : Font.PLAIN;
            text.setFont(new Font(family.getSelectedItem(), style, Integer.parseInt(size.getSelectedItem())));
            setStatus("Applied " + family.getSelectedItem() + " " + size.getSelectedItem());
        });
        Button selectAll = new Button("Select all");
        selectAll.addActionListener(event -> {
            text.selectAll();
            setStatus("Selected " + text.getSelectionEnd() + " characters");
        });
        Button leftToRight = new Button("LTR");
        leftToRight.addActionListener(event -> text.applyComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT));
        Button rightToLeft = new Button("RTL");
        rightToLeft.addActionListener(event -> text.applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT));
        TextField focusTarget = new TextField("Tab or click to focus", 20);
        focusTarget.setName("focusTarget");
        focusTarget.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent event) {
                setStatus("Focus owner: focusTarget");
            }
        });
        tools.add(new Label("Family"));
        tools.add(family);
        tools.add(new Label("Size"));
        tools.add(size);
        tools.add(bold);
        tools.add(applyFont);
        tools.add(selectAll);
        tools.add(leftToRight);
        tools.add(rightToLeft);
        tools.add(focusTarget);
        editor.add(tools, BorderLayout.NORTH);
        editor.add(text, BorderLayout.CENTER);
        editor.add(new Label("Difference to inspect: standard TextField/TextArea painting is native-peer painting; custom Canvas painting is portable."), BorderLayout.SOUTH);
        page.add(editor, BorderLayout.CENTER);
        return page;
    }

    private Panel createMenusPage() {
        Panel page = page("The frame menu bar, popup menus, ScrollPane and adjustable state use native semantic peers where possible");
        ScrollPane scrollPane = new ScrollPane(ScrollPane.SCROLLBARS_ALWAYS);
        scrollPane.setName("showcaseScrollPane");
        Panel content = new Panel(null);
        content.setPreferredSize(new Dimension(1200, 800));
        for (int index = 0; index < 18; index++) {
            Label tile = sampleLabel("Scrollable tile " + (index + 1), index % 2 == 0 ? new Color(220, 238, 255) : new Color(235, 245, 225));
            tile.setBounds(30 + index % 6 * 185, 30 + index / 6 * 220, 160, 170);
            content.add(tile);
        }
        scrollPane.add(content);
        Panel actions = new Panel(new FlowLayout(FlowLayout.LEFT));
        popupButton = new Button("Show popup menu");
        popupButton.setName("popupButton");
        popupMenu = new PopupMenu("Gallery actions");
        MenuItem popupAction = new MenuItem("Record popup action");
        popupAction.addActionListener(event -> record("Popup menu action"));
        CheckboxMenuItem popupCheck = new CheckboxMenuItem("Persistent option");
        popupCheck.addItemListener(event -> record("Popup option: " + popupCheck.getState()));
        Menu nested = new Menu("Nested actions");
        MenuItem nestedAction = new MenuItem("Nested item");
        nestedAction.addActionListener(event -> record("Nested menu action"));
        nested.add(nestedAction);
        popupMenu.add(popupAction);
        popupMenu.add(popupCheck);
        popupMenu.addSeparator();
        popupMenu.add(nested);
        page.add(popupMenu);
        popupButton.addActionListener(event -> showPopupMenu());
        Button move = new Button("Scroll to 640, 420");
        move.addActionListener(event -> {
            scrollPane.setScrollPosition(640, 420);
            setStatus("Scroll position: " + scrollPane.getScrollPosition());
        });
        actions.add(popupButton);
        actions.add(move);
        page.add(scrollPane, BorderLayout.CENTER);
        page.add(actions, BorderLayout.SOUTH);
        return page;
    }

    private Panel createDndPage() {
        Panel page = page("Drag-and-drop string transfer and system clipboard text");
        Panel stage = new Panel(null);
        stage.setPreferredSize(new Dimension(900, 420));
        Label source = sampleLabel("Drag this WebAWT payload", new Color(220, 238, 255));
        source.setName("dragSource");
        source.setBounds(90, 90, 260, 90);
        dragTarget = sampleLabel("Drop payload here", new Color(235, 245, 225));
        dragTarget.setName("dragTarget");
        dragTarget.setBounds(520, 90, 260, 90);
        stage.add(source);
        stage.add(dragTarget);
        installDragAndDrop(source);
        Panel clipboardActions = new Panel(new FlowLayout(FlowLayout.LEFT));
        TextField clipboardText = new TextField("Shared system clipboard text", 28);
        clipboardText.setName("clipboardText");
        Button write = new Button("Write system clipboard");
        Button read = new Button("Read system clipboard");
        Label clipboardStatus = new Label("Ready to copy or paste text.");
        clipboardStatus.setName("clipboardStatus");
        write.addActionListener(event -> transferClipboard(false, clipboardText, write, read, clipboardStatus));
        read.addActionListener(event -> transferClipboard(true, clipboardText, write, read, clipboardStatus));
        clipboardActions.add(clipboardText);
        clipboardActions.add(write);
        clipboardActions.add(read);
        Panel clipboardPanel = new Panel(new BorderLayout(0, 4));
        clipboardPanel.add(clipboardActions, BorderLayout.NORTH);
        clipboardPanel.add(new Label(clipboardAccess.description()), BorderLayout.CENTER);
        clipboardPanel.add(clipboardStatus, BorderLayout.SOUTH);
        page.add(stage, BorderLayout.CENTER);
        page.add(clipboardPanel, BorderLayout.SOUTH);
        return page;
    }

    private Panel createDesktopPage() {
        Panel page = page("Window ownership, dialogs, Toolkit services and Desktop actions");
        Panel information = new Panel(new GridLayout(0, 2, 8, 8));
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screen = toolkit.getScreenSize();
        information.add(new Label("Runtime"));
        information.add(new Label(runtimeName));
        information.add(new Label("Toolkit screen"));
        information.add(new Label(screen.width + " x " + screen.height + " @ " + toolkit.getScreenResolution() + " dpi"));
        information.add(new Label("Graphics environment"));
        information.add(new Label(GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getIDstring()));
        information.add(new Label("Available font families"));
        information.add(new Label(Integer.toString(GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames().length)));
        information.add(new Label("System colors"));
        Label colors = new Label("window / control / text");
        colors.setBackground(SystemColor.window);
        colors.setForeground(SystemColor.windowText);
        information.add(colors);
        Panel actions = new Panel(new FlowLayout(FlowLayout.LEFT));
        Button dialog = new Button("Open Dialog");
        dialog.setName("openDialog");
        dialog.addActionListener(event -> openDialog());
        Button child = new Button("Open child Frame");
        child.addActionListener(event -> openChildFrame());
        Button beep = new Button("Toolkit.beep()");
        beep.addActionListener(event -> {
            toolkit.beep();
            setStatus("Beep requested");
        });
        Button cursor = new Button("Toggle cursor");
        cursor.addActionListener(event -> frame.setCursor(frame.getCursor().getType() == Cursor.WAIT_CURSOR ? Cursor.getDefaultCursor() : Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR)));
        Button browse = new Button("Browse WebAWT repository");
        browse.setName("browseDesktop");
        browse.setEnabled(desktopSupports(Desktop.Action.BROWSE));
        browse.addActionListener(event -> browse("https://github.com/google/j2cl"));
        Button mail = new Button("Compose mail");
        mail.setEnabled(desktopSupports(Desktop.Action.MAIL));
        mail.addActionListener(event -> mail("mailto:?subject=WebAWT%20showcase"));
        actions.add(dialog);
        actions.add(child);
        actions.add(beep);
        actions.add(cursor);
        actions.add(browse);
        actions.add(mail);
        page.add(information, BorderLayout.CENTER);
        page.add(actions, BorderLayout.SOUTH);
        return page;
    }

    private Panel createRuntimePage() {
        Panel page = page("Generated API completeness and deliberately visible platform differences");
        Panel facts = new Panel(new GridLayout(0, 1, 4, 4));
        facts.add(new Label("Runtime: " + runtimeName));
        facts.add(new Label("API declaration coverage is audited separately from behavior; see compatibility/REPORT.md"));
        facts.add(new Label("Main source dependencies: pinned J2CL and Elemental2 only"));
        facts.add(new Label("Excluded: file dialogs and printing"));
        facts.add(new Label("Graphics2D: custom Composite and Canvas XOR are unavailable; arbitrary paints support shapes, not native text"));
        facts.add(new Label("Unavailable in browsers: unrestricted files, system tray, Robot, native printing and operating-system session events"));
        facts.add(new Label("Limited: custom device ICC transforms, synchronous system clipboard, nested/blocking event loops and native text custom painting"));
        facts.add(new Label("Expected visual differences: platform font metrics, native controls, focus rings, menu styling and color management"));
        Button selfCheck = new Button("Run deterministic self-check");
        selfCheck.setName("runSelfCheck");
        selfCheck.addActionListener(event -> runSelfCheck());
        page.add(facts, BorderLayout.CENTER);
        page.add(selfCheck, BorderLayout.SOUTH);
        return page;
    }

    private MenuBar createMenuBar() {
        MenuBar menuBar = new MenuBar();
        Menu gallery = new Menu("Gallery");
        for (String pageName : PAGE_NAMES) {
            MenuItem item = new MenuItem(pageName);
            item.addActionListener(event -> selectPage(pageName));
            gallery.add(item);
        }
        Menu actions = new Menu("Actions");
        MenuItem inputShortcut = new MenuItem("Record input shortcut", new MenuShortcut(KeyEvent.VK_M, true));
        inputShortcut.addActionListener(event -> {
            keyboardPage.menuAction();
            setStatus("Input menu shortcut activated");
        });
        actions.add(inputShortcut);
        MenuItem selfCheck = new MenuItem("Run self-check");
        selfCheck.addActionListener(event -> runSelfCheck());
        MenuItem dialog = new MenuItem("Open dialog");
        dialog.addActionListener(event -> openDialog());
        darkThemeItem = new CheckboxMenuItem("Dark canvas backgrounds");
        darkThemeItem.addItemListener(event -> {
            Color background = darkThemeItem.getState() ? new Color(35, 39, 47) : Color.WHITE;
            if (galleryCanvas != null) {
                galleryCanvas.setBackground(background);
            }
            if (imageCanvas != null) {
                imageCanvas.setBackground(background);
            }
            paintDemos();
        });
        actions.add(selfCheck);
        actions.add(dialog);
        actions.add(darkThemeItem);
        Menu help = new Menu("Help");
        MenuItem limits = new MenuItem("Runtime & limits");
        limits.addActionListener(event -> selectPage("Runtime & limits"));
        help.add(limits);
        menuBar.add(gallery);
        menuBar.add(actions);
        menuBar.add(help);
        menuBar.setHelpMenu(help);
        return menuBar;
    }

    private void installDragAndDrop(Label source) {
        try {
            DragSource dragSource = DragSource.getDefaultDragSource();
            dragSource.createDefaultDragGestureRecognizer(source, DnDConstants.ACTION_COPY, event -> event.startDrag(DragSource.DefaultCopyDrop, new StringSelection("WebAWT dual-runtime payload")));
            new DropTarget(dragTarget, DnDConstants.ACTION_COPY, new DropTargetAdapter() {
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
                        if (!event.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                            event.rejectDrop();
                            return;
                        }
                        event.acceptDrop(DnDConstants.ACTION_COPY);
                        String value = (String) event.getTransferable().getTransferData(DataFlavor.stringFlavor);
                        dragTarget.setText("Dropped: " + value);
                        event.dropComplete(true);
                        record("DnD completed");
                    } catch (Exception exception) {
                        event.rejectDrop();
                        event.dropComplete(false);
                        setStatus("DnD failed: " + exception.getClass().getSimpleName());
                    }
                }
            }, true);
        } catch (RuntimeException exception) {
            dragTarget.setText("DnD unavailable: " + exception.getClass().getSimpleName());
        }
    }

    private void transferClipboard(boolean reading, TextField text, Button write, Button read, Label result) {
        Frame owner = frame;
        setClipboardBusy(true, text, write, read);
        result.setText(reading ? "Reading system clipboard..." : "Writing system clipboard...");
        Consumer<String> complete = message -> {
            if (frame == owner && frame != null) {
                setClipboardBusy(false, text, write, read);
                result.setText(message);
                setStatus(message);
            }
        };
        Consumer<String> failure = reason -> complete.accept("Clipboard " + (reading ? "read" : "write") + " failed: " + reason);
        if (reading) {
            clipboardAccess.readText(value -> {
                if (frame == owner && frame != null) {
                    text.setText(value);
                    complete.accept("Read system clipboard");
                }
            }, failure);
        } else {
            clipboardAccess.writeText(text.getText(), () -> complete.accept("Wrote system clipboard"), failure);
        }
    }

    private static void setClipboardBusy(boolean busy, TextField text, Button write, Button read) {
        text.setEditable(!busy);
        write.setEnabled(!busy);
        read.setEnabled(!busy);
    }

    private void openDialog() {
        Dialog dialog = new Dialog(frame, "AWT Dialog", false);
        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                dialog.dispose();
            }
        });
        dialog.setLayout(new BorderLayout(8, 8));
        dialog.add(new Label("This is the same owned java.awt.Dialog on both runtimes."), BorderLayout.CENTER);
        Button close = new Button("Close");
        close.addActionListener(event -> dialog.dispose());
        dialog.add(close, BorderLayout.SOUTH);
        dialog.setSize(460, 170);
        dialog.setLocation(frame.getX() + 120, frame.getY() + 120);
        dialog.validate();
        dialog.setVisible(true);
        setStatus("Opened owned Dialog");
    }

    private void openChildFrame() {
        Frame child = new Frame("Child AWT Frame");
        child.setLayout(new BorderLayout());
        child.add(new Label("Independent top-level Frame created by " + runtimeName), BorderLayout.CENTER);
        Button close = new Button("Dispose child");
        close.addActionListener(event -> child.dispose());
        child.add(close, BorderLayout.SOUTH);
        child.setSize(440, 180);
        child.validate();
        child.setVisible(true);
        setStatus("Opened child Frame");
    }

    private boolean desktopSupports(Desktop.Action action) {
        try {
            return Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(action);
        } catch (RuntimeException exception) {
            return false;
        }
    }

    private void browse(String location) {
        try {
            Desktop.getDesktop().browse(URI.create(location));
            setStatus("Browse requested");
        } catch (Exception exception) {
            setStatus("Browse failed: " + exception.getClass().getSimpleName());
        }
    }

    private void mail(String location) {
        try {
            Desktop.getDesktop().mail(URI.create(location));
            setStatus("Mail requested");
        } catch (Exception exception) {
            setStatus("Mail failed: " + exception.getClass().getSimpleName());
        }
    }

    private void record(String message) {
        eventCount++;
        if (eventLog != null) {
            eventLog.append(eventCount + ". " + message + "\n");
        }
        setStatus(message);
    }

    private void setStatus(String message) {
        if (status != null) {
            status.setText(message);
        }
    }

    private static Panel page(String description) {
        Panel panel = new Panel(new BorderLayout(8, 8));
        Label heading = new Label(description);
        heading.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        panel.add(heading, BorderLayout.NORTH);
        return panel;
    }

    private static Panel layoutPanel(java.awt.LayoutManager layout) {
        return new Panel(layout);
    }

    private static Label sampleLabel(String text, Color background) {
        Label label = new Label(text, Label.CENTER);
        label.setBackground(background);
        return label;
    }

    private static void addGridBag(Panel panel, java.awt.Component component, int x, int y, int width, double weight) {
        GridBagConstraints constraints = new GridBagConstraints(x, y, width, 1, weight, 0.0, GridBagConstraints.CENTER, weight > 0.0 ? GridBagConstraints.HORIZONTAL : GridBagConstraints.NONE, new Insets(4, 4, 4, 4), 2, 2);
        panel.add(component, constraints);
    }

    private static boolean isPage(String pageName) {
        for (String candidate : PAGE_NAMES) {
            if (candidate.equals(pageName)) {
                return true;
            }
        }
        return false;
    }

    private static void repaintNow(Canvas canvas) {
        if (canvas == null) {
            return;
        }
        Graphics graphics = canvas.getGraphics();
        if (graphics != null) {
            try {
                canvas.update(graphics);
            } finally {
                graphics.dispose();
            }
        }
    }

    private static BufferedImage createSourceImage() {
        BufferedImage image = new BufferedImage(96, 96, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int red = x * 255 / (image.getWidth() - 1);
                int green = y * 255 / (image.getHeight() - 1);
                int blue = 255 - (red + green) / 2;
                int alpha = 160 + (x + y) * 95 / (image.getWidth() + image.getHeight() - 2);
                image.setRGB(x, y, alpha << 24 | red << 16 | green << 8 | blue);
            }
        }
        return image;
    }

    private static final class GalleryCanvas extends Canvas {
        private static final long serialVersionUID = 1L;
        private double rotation;

        private GalleryCanvas() {
            setBackground(Color.WHITE);
        }

        private void rotateMotif() {
            rotation += Math.PI / 12.0;
        }

        @Override
        public void paint(Graphics graphics) {
            Graphics2D painter = (Graphics2D) graphics;
            int width = Math.max(1, getWidth());
            int height = Math.max(1, getHeight());
            painter.setColor(getBackground());
            painter.fillRect(0, 0, width, height);
            painter.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            painter.setPaint(new LinearGradientPaint(20.0f, 20.0f, Math.max(240.0f, width - 20.0f), 20.0f, new float[] {0.0f, 0.45f, 1.0f}, new Color[] {new Color(38, 132, 255), new Color(125, 78, 220), new Color(255, 110, 80)}, MultipleGradientPaint.CycleMethod.NO_CYCLE));
            painter.fillRoundRect(24, 24, Math.max(260, width - 48), 105, 28, 28);
            painter.setColor(Color.WHITE);
            painter.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 30));
            painter.drawString("Graphics2D on " + (width > 600 ? "Canvas" : "AWT"), 48, 82);
            painter.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 15));
            painter.drawString("gradient paint - alpha composite - paths - transforms - strokes", 49, 108);
            painter.setStroke(new BasicStroke(5.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            painter.setColor(new Color(20, 105, 190));
            painter.draw(new Rectangle2D.Double(45, 170, 175, 105));
            painter.setPaint(new GradientPaint(260.0f, 165.0f, new Color(255, 205, 60), 430.0f, 285.0f, new Color(255, 90, 110), true));
            painter.fill(new Ellipse2D.Double(260, 165, 175, 115));
            painter.setColor(new Color(35, 45, 65));
            painter.drawArc(475, 165, 170, 115, 20, 305);
            GeneralPath star = new GeneralPath();
            star.moveTo(0.0f, -58.0f);
            for (int index = 1; index < 10; index++) {
                double angle = -Math.PI / 2.0 + index * Math.PI / 5.0;
                double radius = index % 2 == 0 ? 58.0 : 25.0;
                star.lineTo((float) (Math.cos(angle) * radius), (float) (Math.sin(angle) * radius));
            }
            star.closePath();
            AffineTransform originalTransform = painter.getTransform();
            painter.translate(760, 220);
            painter.rotate(rotation);
            painter.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.78f));
            painter.setColor(new Color(30, 190, 150));
            painter.fill(star);
            painter.setColor(new Color(20, 80, 70));
            painter.draw(star);
            painter.setComposite(AlphaComposite.SrcOver);
            painter.setTransform(originalTransform);
            painter.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
            painter.setColor(darkText(getBackground()));
            painter.drawString("Rectangle2D", 78, 305);
            painter.drawString("Ellipse2D", 305, 305);
            painter.drawString("Arc", 540, 305);
            painter.drawString("GeneralPath", 710, 305);
            painter.setColor(new Color(35, 120, 210));
            for (int index = 0; index < 12; index++) {
                painter.drawLine(45, 365 + index * 10, 45 + index * 62, 470);
            }
        }
    }

    private static final class ImageCanvas extends Canvas {
        private static final long serialVersionUID = 1L;

        private ImageCanvas() {
            setBackground(Color.WHITE);
        }

        @Override
        public void paint(Graphics graphics) {
            Graphics2D painter = (Graphics2D) graphics;
            painter.setColor(getBackground());
            painter.fillRect(0, 0, Math.max(1, getWidth()), Math.max(1, getHeight()));
            BufferedImage source = createSourceImage();
            BufferedImage brighter = new RescaleOp(1.12f, 18.0f, null).filter(source, null);
            float ninth = 1.0f / 9.0f;
            BufferedImage blurred = new ConvolveOp(new Kernel(3, 3, new float[] {ninth, ninth, ninth, ninth, ninth, ninth, ninth, ninth, ninth}), ConvolveOp.EDGE_NO_OP, null).filter(source, null);
            BufferedImage scaled = new AffineTransformOp(AffineTransform.getScaleInstance(1.25, 1.25), AffineTransformOp.TYPE_BILINEAR).filter(source, null);
            BufferedImage gray = new ColorConvertOp(java.awt.color.ColorSpace.getInstance(java.awt.color.ColorSpace.CS_GRAY), null).filter(source, null);
            Color textColor = darkText(getBackground());
            drawImage(painter, source, 40, 70, "BufferedImage", textColor);
            drawImage(painter, brighter, 220, 70, "RescaleOp", textColor);
            drawImage(painter, blurred, 400, 70, "ConvolveOp", textColor);
            drawImage(painter, scaled, 580, 70, "AffineTransformOp", textColor);
            drawImage(painter, gray, 790, 70, "ColorConvertOp", textColor);
            int center = source.getRaster().getSample(48, 48, 0);
            painter.setColor(darkText(getBackground()));
            painter.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
            painter.drawString("Raster sample[48,48,0] = " + center, 40, 260);
            painter.drawString("Source color space: " + source.getColorModel().getColorSpace().getName(0) + "/" + source.getColorModel().getColorSpace().getType(), 40, 288);
            painter.drawString("Converted space: " + gray.getColorModel().getColorSpace().getName(0) + "/" + gray.getColorModel().getColorSpace().getType(), 40, 316);
            painter.drawString("All images above were generated and filtered in shared java.awt source.", 40, 360);
        }

        private static void drawImage(Graphics2D painter, BufferedImage image, int x, int y, String label, Color textColor) {
            painter.drawImage(image, x, y, null);
            painter.setColor(textColor);
            painter.drawString(label, x, y + image.getHeight() + 24);
        }
    }

    private static Color darkText(Color background) {
        return background.getRed() + background.getGreen() + background.getBlue() < 330 ? Color.WHITE : new Color(30, 35, 45);
    }
}
