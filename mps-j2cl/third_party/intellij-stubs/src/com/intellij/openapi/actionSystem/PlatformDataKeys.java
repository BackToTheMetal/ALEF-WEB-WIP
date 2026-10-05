package com.intellij.openapi.actionSystem;
import com.intellij.ide.CopyProvider;
import com.intellij.ide.CutProvider;
import com.intellij.ide.PasteProvider;
public class PlatformDataKeys extends CommonDataKeys {
  public static final DataKey<com.intellij.openapi.fileEditor.FileEditor> FILE_EDITOR = DataKey.create("fileEditor");
  public static final DataKey<CopyProvider> COPY_PROVIDER = DataKey.create("copyProvider");
  public static final DataKey<CutProvider> CUT_PROVIDER = DataKey.create("cutProvider");
  public static final DataKey<PasteProvider> PASTE_PROVIDER = DataKey.create("pasteProvider");
  public static final DataKey<Object> SELECTED_ITEM = DataKey.create("selectedItem");
  public static final DataKey<java.awt.Component> CONTEXT_COMPONENT = DataKey.create("contextComponent");
}
