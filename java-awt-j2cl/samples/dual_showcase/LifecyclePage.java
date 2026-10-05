package org.webawt.samples.dualshowcase;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Checkbox;
import java.awt.Component;
import java.awt.Container;
import java.awt.ContainerOrderFocusTraversalPolicy;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ContainerAdapter;
import java.awt.event.ContainerEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.accessibility.AccessibleAction;
import javax.accessibility.AccessibleContext;

final class LifecyclePage extends Panel {
    private final Frame owner;
    private final Panel dynamic = new Panel(new FlowLayout(FlowLayout.LEFT, 8, 8));
    private final TextArea log = new TextArea("", 12, 70, TextArea.SCROLLBARS_BOTH);
    private final Label accessibility = new Label("Inspect the sample action's accessible name, role, states and actions.");
    private final Button sample = new Button("Lifecycle action");
    private Dialog dialog;
    private int next;
    private boolean narrow;

    LifecyclePage(Frame owner) {
        super(new BorderLayout(8, 8));
        this.owner = owner;
        Panel header = new Panel(new GridLayout(0, 1));
        header.add(new Label("Dynamic layout, a nested focus cycle, accessibility and owned-window lifecycle."));
        Panel actions = new Panel(new FlowLayout(FlowLayout.LEFT));
        Button add = new Button("Add component");
        add.addActionListener(event -> addComponent());
        Button remove = new Button("Remove component");
        remove.addActionListener(event -> {
            if (dynamic.getComponentCount() > 1) {
                dynamic.remove(dynamic.getComponentCount() - 1);
                validate();
            }
        });
        Button resize = new Button("Toggle container width");
        resize.addActionListener(event -> {
            narrow = !narrow;
            dynamic.setPreferredSize(new Dimension(narrow ? 320 : 780, 170));
            validate();
        });
        Checkbox reverse = new Checkbox("Reverse focus order");
        reverse.addItemListener(event -> dynamic.setFocusTraversalPolicy(new ContainerOrderFocusTraversalPolicy() {
            @Override
            public Component getComponentAfter(Container root, Component component) {
                return reverse.getState() ? super.getComponentBefore(root, component) : super.getComponentAfter(root, component);
            }
            @Override
            public Component getComponentBefore(Container root, Component component) {
                return reverse.getState() ? super.getComponentAfter(root, component) : super.getComponentBefore(root, component);
            }
            @Override
            protected boolean accept(Component component) {
                return component instanceof Button && super.accept(component);
            }
        }));
        Button window = new Button("Open lifecycle dialog");
        window.addActionListener(event -> openDialog());
        actions.add(add);
        actions.add(remove);
        actions.add(resize);
        actions.add(reverse);
        actions.add(window);
        header.add(actions);
        header.add(accessibilityTools());
        header.add(accessibility);
        add(header, BorderLayout.NORTH);
        Panel center = new Panel(new BorderLayout());
        Panel stage = new Panel(new FlowLayout(FlowLayout.LEFT));
        dynamic.setName("dynamicContainer");
        dynamic.setPreferredSize(new Dimension(780, 170));
        dynamic.setFocusCycleRoot(true);
        dynamic.setFocusable(false);
        installLifecycleListeners();
        sample.setName("lifecycleAction");
        sample.getAccessibleContext().setAccessibleName("Lifecycle action");
        sample.addActionListener(event -> append("ACTION lifecycle sample"));
        dynamic.add(sample);
        addComponent();
        addComponent();
        stage.add(dynamic);
        center.add(stage, BorderLayout.NORTH);
        log.setName("lifecycleLog");
        log.setEditable(false);
        center.add(log, BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);
        add(new Label("Tab stays within the nested focus cycle; click outside to leave. Browser dialogs use in-page window semantics."), BorderLayout.SOUTH);
    }

    void dispose() {
        if (dialog != null) {
            dialog.dispose();
            dialog = null;
        }
    }

    private Panel accessibilityTools() {
        Panel tools = new Panel(new FlowLayout(FlowLayout.LEFT));
        Button inspect = new Button("Inspect accessibility");
        inspect.addActionListener(event -> inspectAccessibility());
        Button invoke = new Button("Invoke accessible action");
        invoke.addActionListener(event -> {
            AccessibleAction action = sample.getAccessibleContext().getAccessibleAction();
            append("Accessible action invoked=" + (action != null && action.doAccessibleAction(0)));
        });
        Checkbox enabled = new Checkbox("Sample enabled", true);
        enabled.addItemListener(event -> {
            sample.setEnabled(enabled.getState());
            inspectAccessibility();
        });
        tools.add(inspect);
        tools.add(invoke);
        tools.add(enabled);
        return tools;
    }

    private void inspectAccessibility() {
        AccessibleContext context = sample.getAccessibleContext();
        AccessibleAction action = context.getAccessibleAction();
        accessibility.setText("Name=" + context.getAccessibleName() + " | role=" + context.getAccessibleRole() + " | states=" + context.getAccessibleStateSet() + " | actions=" + (action == null ? 0 : action.getAccessibleActionCount()));
    }

    private void addComponent() {
        Button button = new Button("Dynamic " + ++next);
        button.setName("dynamic" + next);
        button.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent event) {
                append("FOCUS " + button.getName());
            }
        });
        dynamic.add(button);
        dynamic.invalidate();
        validate();
    }

    private void installLifecycleListeners() {
        dynamic.addContainerListener(new ContainerAdapter() {
            @Override
            public void componentAdded(ContainerEvent event) {
                append("ADDED " + event.getChild().getName());
            }
            @Override
            public void componentRemoved(ContainerEvent event) {
                append("REMOVED " + event.getChild().getName());
            }
        });
        dynamic.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent event) {
                append("RESIZED " + dynamic.getWidth() + " x " + dynamic.getHeight());
            }
        });
        dynamic.addHierarchyListener(event -> append("HIERARCHY flags=" + event.getChangeFlags()));
    }

    private void openDialog() {
        if (dialog != null && dialog.isDisplayable()) {
            dialog.toFront();
            return;
        }
        dialog = new Dialog(owner, "Lifecycle dialog", false);
        dialog.setLayout(new BorderLayout());
        dialog.add(new Label("Owned modeless dialog: inspect open, focus and close events."), BorderLayout.CENTER);
        Button close = new Button("Close lifecycle dialog");
        close.addActionListener(event -> dialog.dispose());
        dialog.add(close, BorderLayout.SOUTH);
        WindowAdapter listener = new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent event) { append("WINDOW opened"); }
            @Override
            public void windowClosed(WindowEvent event) { append("WINDOW closed"); }
            @Override
            public void windowGainedFocus(WindowEvent event) { append("WINDOW focus gained"); }
            @Override
            public void windowLostFocus(WindowEvent event) { append("WINDOW focus lost"); }
            @Override
            public void windowClosing(WindowEvent event) { event.getWindow().dispose(); }
        };
        dialog.addWindowListener(listener);
        dialog.addWindowFocusListener(listener);
        dialog.setSize(520, 170);
        dialog.setLocation(owner.getX() + 120, owner.getY() + 180);
        dialog.validate();
        dialog.setVisible(true);
    }

    private void append(String line) {
        String text = log.getText();
        if (text.length() > 10000) {
            text = text.substring(text.indexOf('\n', text.length() - 8000) + 1);
        }
        log.setText(text + line + '\n');
        log.setCaretPosition(log.getText().length());
    }
}
