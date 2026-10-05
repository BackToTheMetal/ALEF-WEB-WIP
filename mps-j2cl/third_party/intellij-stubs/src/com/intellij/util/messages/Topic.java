package com.intellij.util.messages;

public class Topic<L> {
  public enum BroadcastDirection { TO_CHILDREN, NONE, TO_PARENT, TO_DIRECT_CHILDREN }
  private final String name;
  private final Class<L> listenerClass;
  private final BroadcastDirection direction;
  private final boolean immediate;
  public Topic(String name, Class<L> listenerClass) { this(name, listenerClass, BroadcastDirection.TO_CHILDREN); }
  public Topic(Class<L> listenerClass) { this(listenerClass.getName(), listenerClass); }
  public Topic(Class<L> listenerClass, BroadcastDirection direction) { this(listenerClass.getName(), listenerClass, direction); }
  public Topic(Class<L> listenerClass, BroadcastDirection direction, boolean immediate) {
    this.name = listenerClass.getName(); this.listenerClass = listenerClass; this.direction = direction; this.immediate = immediate;
  }
  public Topic(String name, Class<L> listenerClass, BroadcastDirection direction) {
    this.name = name; this.listenerClass = listenerClass; this.direction = direction; this.immediate = false;
  }
  public static <L> Topic<L> create(String name, Class<L> listenerClass) { return new Topic<>(name, listenerClass); }
  public static <L> Topic<L> create(String name, Class<L> listenerClass, BroadcastDirection direction) { return new Topic<>(name, listenerClass, direction); }
  public String getDisplayName() { return name; }
  public Class<L> getListenerClass() { return listenerClass; }
  public BroadcastDirection getBroadcastDirection() { return direction; }
  public boolean isImmediateDelivery() { return immediate; }
  @Override public String toString() { return name; }
  public @interface AppLevel {}
  public @interface ProjectLevel {}
}
