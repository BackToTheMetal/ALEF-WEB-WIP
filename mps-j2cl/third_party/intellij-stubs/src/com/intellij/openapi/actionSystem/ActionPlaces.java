package com.intellij.openapi.actionSystem;
public class ActionPlaces {
  public static final String UNKNOWN = "unknown";
  public static final String EDITOR_POPUP = "EditorPopup";
  public static final String EDITOR_TOOLBAR = "EditorToolbar";
  public static final String MAIN_MENU = "MainMenu";
  public static final String MAIN_TOOLBAR = "MainToolbar";
  public static final String POPUP = "popup";
  public static final String TOOLBAR = "toolbar";
  public static final String INTENTION_MENU = "IntentionMenu";
  public static boolean isPopupPlace(String place) { return place != null && place.toLowerCase().contains("popup"); }
  protected ActionPlaces() {}
}
