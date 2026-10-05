package com.intellij.openapi.actionSystem.impl;
import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.util.Key;
import java.awt.Dimension;
import javax.swing.JButton;
public class ActionButton extends JButton {
  public static final Key<Boolean> HIDE_DROPDOWN_ICON = Key.create("hideDropdownIcon");
  private boolean noIconsInPopup;
  public ActionButton(AnAction action, Presentation presentation, String place, Dimension minimumSize) {
    super(presentation.getIcon()); setPreferredSize(minimumSize); setToolTipText(presentation.getDescription());
    addActionListener(ignored -> {
      AnActionEvent event = AnActionEvent.createFromDataContext(place, presentation.clone(), DataManager.getInstance().getDataContext(this));
      action.beforeActionPerformedUpdate(event);
      if (!event.getPresentation().isEnabledAndVisible()) return;
      if (action instanceof ActionGroup) {
        ActionPopupMenu menu = ActionManager.getInstance().createActionPopupMenu(place, (ActionGroup) action); menu.setTargetComponent(this);
        javax.swing.JPopupMenu component = menu.getComponent();
        if (noIconsInPopup) for (java.awt.Component child : component.getComponents()) if (child instanceof javax.swing.JMenuItem) ((javax.swing.JMenuItem) child).setIcon(null);
        component.show(this, 0, getHeight());
      } else action.actionPerformed(event);
    });
  }
  public void setNoIconsInPopup(boolean value) { noIconsInPopup = value; }
}
