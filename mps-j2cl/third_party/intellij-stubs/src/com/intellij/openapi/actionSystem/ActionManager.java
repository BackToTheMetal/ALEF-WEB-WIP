package com.intellij.openapi.actionSystem;
import com.intellij.ide.DataManager;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
public class ActionManager {
  private static final ActionManager INSTANCE = new ActionManager();
  private final Map<String, AnAction> actions = new LinkedHashMap<>();
  public static ActionManager getInstance() { return INSTANCE; }
  public AnAction getAction(String id) { return actions.get(id); }
  public AnAction getActionOrStub(String id) { return getAction(id); }
  public String getId(AnAction action) { for (Map.Entry<String, AnAction> entry : actions.entrySet()) if (entry.getValue() == action) return entry.getKey(); return null; }
  public void registerAction(String id, AnAction action) {
    if (actions.containsKey(id)) throw new IllegalArgumentException("Action already registered: " + id);
    actions.put(id, action);
  }
  public void unregisterAction(String id) { actions.remove(id); }
  public ActionToolbar createActionToolbar(String place, ActionGroup group, boolean horizontal) {
    return new ActionToolbar() {
      private final javax.swing.JToolBar toolbar = new javax.swing.JToolBar(horizontal ? javax.swing.JToolBar.HORIZONTAL : javax.swing.JToolBar.VERTICAL);
      private JComponent target;
      @Override public JComponent getComponent() { updateActionsImmediately(); return toolbar; }
      @Override public void setTargetComponent(JComponent component) { target = component; }
      @Override public void setLayoutPolicy(int policy) { if (policy != NOWRAP_LAYOUT_POLICY && policy != WRAP_LAYOUT_POLICY && policy != AUTO_LAYOUT_POLICY) throw new IllegalArgumentException("Unknown toolbar layout"); }
      @Override public void updateActionsImmediately() {
        toolbar.removeAll(); toolbar.setFloatable(false);
        DataContext context = DataManager.getInstance().getDataContext(target);
        for (AnAction action : group.getChildren(AnActionEvent.createFromDataContext(place, group.getTemplatePresentation().clone(), context))) {
          if (action instanceof Separator) { toolbar.addSeparator(); continue; }
          AnActionEvent event = AnActionEvent.createFromDataContext(place, action.getTemplatePresentation().clone(), context);
          action.update(event);
          Presentation presentation = event.getPresentation();
          if (!presentation.isVisible()) continue;
          javax.swing.JButton button = new javax.swing.JButton(presentation.getIcon());
          if (presentation.getIcon() == null) button.setText(presentation.getText());
          button.setToolTipText(presentation.getDescription() == null ? presentation.getText() : presentation.getDescription());
          button.setEnabled(presentation.isEnabled());
          button.addActionListener(ignored -> { action.beforeActionPerformedUpdate(event); if (presentation.isEnabledAndVisible()) action.actionPerformed(event); });
          toolbar.add(button);
        }
        toolbar.revalidate(); toolbar.repaint();
      }
    };
  }
  public String[] getActionIds(String prefix) { return actions.keySet().stream().filter(key -> key.startsWith(prefix)).toArray(String[]::new); }
  public ActionPopupMenu createActionPopupMenu(String place, ActionGroup group) {
    return new ActionPopupMenu() {
      private final JPopupMenu menu = new JPopupMenu();
      private JComponent target;
      @Override public void setTargetComponent(JComponent component) { target = component; }
      @Override public JPopupMenu getComponent() {
        menu.removeAll();
        DataContext context = DataManager.getInstance().getDataContext(target == null ? menu.getInvoker() : target);
        append(menu, group, place, context);
        return menu;
      }
    };
  }
  private static void append(JComponent menu, ActionGroup group, String place, DataContext context) {
    AnActionEvent groupEvent = AnActionEvent.createFromDataContext(place, group.getTemplatePresentation().clone(), context);
    for (AnAction action : group.getChildren(groupEvent)) {
      if (action instanceof Separator) { if (menu instanceof JPopupMenu) ((JPopupMenu) menu).addSeparator(); else ((JMenu) menu).addSeparator(); continue; }
      Presentation presentation = action.getTemplatePresentation().clone();
      AnActionEvent event = AnActionEvent.createFromDataContext(place, presentation, context);
      action.update(event);
      if (!presentation.isVisible()) continue;
      if (action instanceof ActionGroup) {
        ActionGroup child = (ActionGroup) action;
        if (!child.isPopup()) append(menu, child, place, context);
        else { JMenu sub = new JMenu(presentation.getText()); sub.setIcon(presentation.getIcon()); sub.setEnabled(presentation.isEnabled()); append(sub, child, place, context); menu.add(sub); }
      } else {
        JMenuItem item = new JMenuItem(presentation.getText(), presentation.getIcon()); item.setEnabled(presentation.isEnabled());
        item.addActionListener(ignored -> {
          action.beforeActionPerformedUpdate(event);
          if (presentation.isEnabledAndVisible()) action.actionPerformed(event);
        });
        menu.add(item);
      }
    }
  }
}
