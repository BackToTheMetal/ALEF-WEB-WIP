package com.intellij.openapi.ui.popup;
import com.intellij.ui.popup.AbstractPopup;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
public class JBPopupFactory {
  public enum ActionSelectionAid { SPEEDSEARCH, MNEMONICS, NUMBERING, ALPHA_NUMBERING }
  private static final JBPopupFactory INSTANCE = new JBPopupFactory();
  public static JBPopupFactory getInstance() { return INSTANCE; }
  public ComponentPopupBuilder createComponentPopupBuilder(javax.swing.JComponent content, javax.swing.JComponent focus) { return new ComponentPopupBuilder(content, focus); }
  public BalloonBuilder createHtmlTextBalloonBuilder(String text, com.intellij.openapi.ui.MessageType type, javax.swing.event.HyperlinkListener links) { return new BalloonBuilder(text, type, links); }
  public ListPopup createListPopup(ListPopupStep<?> step) { return new BrowserListPopup(step); }
  public ListPopup createActionGroupPopup(String title, com.intellij.openapi.actionSystem.ActionGroup group, com.intellij.openapi.actionSystem.DataContext context, ActionSelectionAid aid, boolean showDisabled) {
    return createActionGroupPopup(title, group, context, aid, showDisabled, com.intellij.openapi.actionSystem.ActionPlaces.POPUP);
  }
  public ListPopup createActionGroupPopup(String title, com.intellij.openapi.actionSystem.ActionGroup group, com.intellij.openapi.actionSystem.DataContext context, ActionSelectionAid aid, boolean showDisabled, String place) { return createListPopup(actionStep(title, group, context, showDisabled, place)); }
  private ListPopupStep<com.intellij.openapi.actionSystem.AnAction> actionStep(String title, com.intellij.openapi.actionSystem.ActionGroup group, com.intellij.openapi.actionSystem.DataContext context, boolean showDisabled, String place) {
    java.util.List<com.intellij.openapi.actionSystem.AnAction> values = new java.util.ArrayList<>();
    java.util.Map<com.intellij.openapi.actionSystem.AnAction, com.intellij.openapi.actionSystem.Presentation> presentations = new java.util.IdentityHashMap<>();
    com.intellij.openapi.actionSystem.AnActionEvent groupEvent = com.intellij.openapi.actionSystem.AnActionEvent.createFromDataContext(place, group.getTemplatePresentation().clone(), context);
    for (com.intellij.openapi.actionSystem.AnAction action : group.getChildren(groupEvent)) {
      if (action instanceof com.intellij.openapi.actionSystem.Separator) continue;
      com.intellij.openapi.actionSystem.Presentation presentation = action.getTemplatePresentation().clone();
      action.update(com.intellij.openapi.actionSystem.AnActionEvent.createFromDataContext(place, presentation, context));
      if (presentation.isVisible() && (showDisabled || presentation.isEnabled())) { values.add(action); presentations.put(action, presentation); }
    }
    return new com.intellij.openapi.ui.popup.util.BaseListPopupStep<com.intellij.openapi.actionSystem.AnAction>(title, values) {
      public String getTextFor(com.intellij.openapi.actionSystem.AnAction action) { return presentations.get(action).getText(); }
      public javax.swing.Icon getIconFor(com.intellij.openapi.actionSystem.AnAction action) { return presentations.get(action).getIcon(); }
      public boolean isSelectable(com.intellij.openapi.actionSystem.AnAction action) { return presentations.get(action).isEnabled(); }
      public boolean hasSubstep(com.intellij.openapi.actionSystem.AnAction action) { return action instanceof com.intellij.openapi.actionSystem.ActionGroup; }
      public PopupStep<?> onChosen(com.intellij.openapi.actionSystem.AnAction action, boolean finalChoice) {
        if (action instanceof com.intellij.openapi.actionSystem.ActionGroup) return actionStep(getTextFor(action), (com.intellij.openapi.actionSystem.ActionGroup) action, context, showDisabled, place);
        return doFinalStep(() -> { com.intellij.openapi.actionSystem.AnActionEvent event = com.intellij.openapi.actionSystem.AnActionEvent.createFromDataContext(place, presentations.get(action), context); action.beforeActionPerformedUpdate(event); if (event.getPresentation().isEnabledAndVisible()) action.actionPerformed(event); });
      }
    };
  }
  private static final class BrowserListPopup extends AbstractPopup implements ListPopup {
    private final ListPopupStep<?> step;
    BrowserListPopup(ListPopupStep<?> step) { super(new JPanel(), null); this.step = step; popup.removeAll(); addStep(popup, step); }
    @Override public ListPopupStep<?> getListStep() { return step; }
    private <T> void addStep(javax.swing.JComponent menu, ListPopupStep<T> current) {
      for (T value : current.getValues()) {
        ListSeparator separator = current.getSeparatorAbove(value);
        if (separator != null) { if (menu instanceof JPopupMenu) ((JPopupMenu) menu).addSeparator(); else ((JMenu) menu).addSeparator(); }
        if (current.hasSubstep(value)) {
          JMenu submenu = new JMenu(current.getTextFor(value));
          submenu.setIcon(current.getIconFor(value)); submenu.setEnabled(current.isSelectable(value));
          submenu.addMenuListener(new javax.swing.event.MenuListener() {
            @Override public void menuSelected(javax.swing.event.MenuEvent event) {
              submenu.removeAll();
              PopupStep<?> next = current.onChosen(value, false);
              if (next instanceof ListPopupStep) addStep(submenu, (ListPopupStep<?>) next);
            }
            @Override public void menuDeselected(javax.swing.event.MenuEvent event) {}
            @Override public void menuCanceled(javax.swing.event.MenuEvent event) {}
          });
          menu.add(submenu); continue;
        }
        JMenuItem item = new JMenuItem(current.getTextFor(value), current.getIconFor(value));
        item.setEnabled(current.isSelectable(value));
        item.addActionListener(event -> {
          PopupStep<?> next = current.onChosen(value, true);
          if (next instanceof ListPopupStep) {
            popup.removeAll(); addStep(popup, (ListPopupStep<?>) next); popup.pack();
          } else {
            Runnable finalAction = current.getFinalRunnable(); closeOk(null);
            if (finalAction != null) finalAction.run();
          }
        });
        menu.add(item);
      }
    }
    @Override protected void afterCanceled() { step.canceled(); }
  }
}
