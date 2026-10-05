package com.intellij.openapi.actionSystem;
import com.intellij.openapi.util.Key;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import javax.swing.Icon;
public final class Presentation implements Cloneable {
  public static final String PROP_TEXT = "text", PROP_DESCRIPTION = "description", PROP_ICON = "icon", PROP_VISIBLE = "visible", PROP_ENABLED = "enabled";
  private String text, description;
  private Icon icon, disabledIcon, hoveredIcon, selectedIcon;
  private boolean enabled = true, visible = true;
  private final Map<Object, Object> properties = new HashMap<>();
  private final PropertyChangeSupport changes = new PropertyChangeSupport(this);
  public Presentation() {}
  public Presentation(String text) { this.text = text; }
  public Presentation(Supplier<String> text) { this(text.get()); }
  public String getText() { return text; }
  public String getText(boolean withSuffix) { return text; }
  public boolean hasText() { return text != null && !text.isEmpty(); }
  public String getTextWithMnemonic() { return text; }
  public void setText(String value) { String old = text; text = value; changes.firePropertyChange(PROP_TEXT, old, value); }
  public void setText(String value, boolean mayContainMnemonic) { setText(value); }
  public void setText(Supplier<String> value) { setText(value.get()); }
  public void setText(Supplier<String> value, boolean mayContainMnemonic) { setText(value.get()); }
  public String getDescription() { return description; }
  public void setDescription(String value) { String old = description; description = value; changes.firePropertyChange(PROP_DESCRIPTION, old, value); }
  public void setDescription(Supplier<String> value) { setDescription(value.get()); }
  public Icon getIcon() { return icon; }
  public void setIcon(Icon value) { Icon old = icon; icon = value; changes.firePropertyChange(PROP_ICON, old, value); }
  public void setIconSupplier(Supplier<? extends Icon> value) { setIcon(value.get()); }
  public Icon getDisabledIcon() { return disabledIcon; }
  public void setDisabledIcon(Icon value) { disabledIcon = value; }
  public Icon getHoveredIcon() { return hoveredIcon; }
  public void setHoveredIcon(Icon value) { hoveredIcon = value; }
  public Icon getSelectedIcon() { return selectedIcon; }
  public void setSelectedIcon(Icon value) { selectedIcon = value; }
  public boolean isEnabled() { return enabled; }
  public void setEnabled(boolean value) { boolean old = enabled; enabled = value; changes.firePropertyChange(PROP_ENABLED, old, value); }
  public boolean isVisible() { return visible; }
  public void setVisible(boolean value) { boolean old = visible; visible = value; changes.firePropertyChange(PROP_VISIBLE, old, value); }
  public boolean isEnabledAndVisible() { return enabled && visible; }
  public void setEnabledAndVisible(boolean value) { setEnabled(value); setVisible(value); }
  public void setPopupGroup(boolean value) { putClientProperty("popupGroup", value); }
  public boolean isPopupGroup() { return Boolean.TRUE.equals(getClientProperty("popupGroup")); }
  public void setPerformGroup(boolean value) { putClientProperty("performGroup", value); }
  public boolean isPerformGroup() { return Boolean.TRUE.equals(getClientProperty("performGroup")); }
  public void addPropertyChangeListener(PropertyChangeListener listener) { changes.addPropertyChangeListener(listener); }
  public void removePropertyChangeListener(PropertyChangeListener listener) { changes.removePropertyChangeListener(listener); }
  @SuppressWarnings("unchecked") public <T> T getClientProperty(Key<T> key) { return (T) properties.get(key); }
  public <T> void putClientProperty(Key<T> key, T value) { if (value == null) properties.remove(key); else properties.put(key, value); }
  public Object getClientProperty(String key) { return properties.get(key); }
  public void putClientProperty(String key, Object value) { if (value == null) properties.remove(key); else properties.put(key, value); }
  public void copyFrom(Presentation source) {
    setText(source.text); setDescription(source.description); setIcon(source.icon); setEnabled(source.enabled); setVisible(source.visible);
    disabledIcon = source.disabledIcon; hoveredIcon = source.hoveredIcon; selectedIcon = source.selectedIcon;
    properties.clear(); properties.putAll(source.properties);
  }
  public Presentation clone() { Presentation copy = new Presentation(); copy.copyFrom(this); return copy; }
}
