package com.mbeddr.mpsutil.grammarcells.runtime;

import com.mbeddr.mpsutil.grammarcells.runtimelang.editor.ArbitraryTextDeleteAction;
import jetbrains.mps.lang.smodel.generator.smodelAdapter.SNodeOperations;
import org.jetbrains.mps.openapi.model.SModel;
import org.jetbrains.mps.openapi.model.SNode;

/** Application-wide browser lifecycle replacing the desktop application plugin. */
public final class GrammarCellsRuntime {
  private GrammarCellsRuntime() {}

  /** Call once per application host, after registering the compiled runtime language. */
  public static void init() {
    ArbitraryTextDeleteAction.setHandler(annotation -> {
      SModel model = annotation.getModel();
      SNode parent = annotation.getParent();
      SNodeOperations.deleteNode(annotation);
      Parser parser = new Parser(model);
      parser.processAfterTextDelete(parser.findRootExpression(parent));
    });
  }

  /** Call before disposing the host; compiled grammar registrations are released. */
  public static void dispose() {
    ArbitraryTextDeleteAction.setHandler(null);
    DescriptorCache.getInstance().dispose();
    EditorHierachyCache.getInstance().clearConditionalEditors();
    VisibleLanguagesCache.getInstance().dispose();
  }
}
