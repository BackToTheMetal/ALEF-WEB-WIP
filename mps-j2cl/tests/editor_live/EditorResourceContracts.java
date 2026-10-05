package mpsj2cl.editorlive;

import com.intellij.compat.BrowserIcons;
import javax.swing.Icon;
import jetbrains.mps.ide.icons.BaseIconManager;
import jetbrains.mps.ide.icons.IconLoadingUtil;
import jetbrains.mps.ide.search.SearchConditions;
import jetbrains.mps.icons.MPSIcons;
import jetbrains.mps.smodel.runtime.IconResource;
import mpsj2cl.editor.BrowserEditorHost;
import org.jetbrains.mps.openapi.module.SModuleReference;

/** Real bundled asset lookup and MPS search entrypoints, including STRIPPED metadata. */
final class EditorResourceContracts {
  @SuppressWarnings("removal")
  static void run(BrowserEditorHost host) {
    Icon language = BrowserIcons.findRegistered("/nodes/language.png");
    require(language != null, "host installs the original language icon asset");
    require(MPSIcons.Nodes.Language.getIconWidth() == language.getIconWidth()
        && MPSIcons.Nodes.Language.getIconHeight() == language.getIconHeight(), "MPS icon uses actual resource dimensions");
    SModuleReference module = ExpressionLanguage.LITERAL.getLanguage().getSourceModuleReference();
    String alias = "/editor-fixture/nodes/language.png";
    BrowserIcons.register(alias, language);
    IconLoadingUtil.registerModuleOrigin(module, "/editor-fixture");
    IconLoadingUtil.registerProvider(EditorResourceContracts.class, module, "nodes");
    try {
      Icon loaded = IconLoadingUtil.loadIcon("language.png", EditorResourceContracts.class);
      require(loaded != null && loaded.getIconWidth() == language.getIconWidth(), "declared provider package resolves relative assets");
      require(IconLoadingUtil.loadIcon("missing.png", EditorResourceContracts.class) == null, "missing resources remain absent");
      BaseIconManager manager = new BaseIconManager(host.getPlatform());
      IconResource legacy = new IconResource("language.png", EditorResourceContracts.class);
      IconResource modern = new IconResource("language.png", null, EditorResourceContracts.class);
      require(manager.getIconForResource(legacy) != null, "legacy resources use registered provider identity");
      require(manager.getIconForResource(modern) != null, "modern resources resolve through the real deployed module");
      IconLoadingUtil.unregisterModuleOrigin(module);
      require(IconLoadingUtil.loadIcon("/nodes/language.png", EditorResourceContracts.class) == null, "unloaded provider cannot access a global asset");
      require(manager.getIconForResource(legacy) == null, "resource origin change invalidates the cached legacy icon");
      try {
        new IconResource("language.png", EditorResourceContracts.class);
        throw new AssertionError("unloaded relative resource inferred a Class package");
      } catch (IllegalStateException expected) {}
      IconLoadingUtil.registerModuleOrigin(module, "/editor-fixture");
      IconLoadingUtil.registerProvider(EditorResourceContracts.class, module, "nodes");
      require(manager.getIconForResource(legacy) != null, "reloaded provider resolves the original icon again");
    } finally {
      IconLoadingUtil.unregisterModuleOrigin(module);
      BrowserIcons.unregister(alias, language);
    }
    // Upstream's boolean is named ignoreCase but its UI passes isCaseSensitive.
    require(SearchConditions.containsString("a.b", false).matcher("A.B").find(), "case-insensitive quoted MPS search");
    require(!SearchConditions.containsString("a.b", true).matcher("A.B").find(), "case-sensitive MPS search");
    require(!SearchConditions.containsString("a.b", false).matcher("axb").find(), "literal search quotes regex metacharacters");
    require(SearchConditions.containsWholeWord("word", false).matcher("WORDé").find(), "whole-word MPS search uses Java21 ASCII boundaries");
    require(!SearchConditions.containsWholeWord("word", false).matcher("sword").find(), "whole-word search rejects interior match");
    require(SearchConditions.containsRegexp("[", false) == null, "invalid supported regex is reported as no search pattern");
  }

  private static void require(boolean condition, String detail) { if (!condition) throw new AssertionError(detail); }
}
