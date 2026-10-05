/*
 * Copyright 2003-2020 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jetbrains.mps.nodeEditor;

import jetbrains.mps.editor.runtime.commands.EditorComputable;
import jetbrains.mps.nodeEditor.cells.APICellAdapter;
import jetbrains.mps.nodeEditor.keyboard.TextChangeEvent;
import jetbrains.mps.nodeEditor.keymaps.KeymapHandler;
import jetbrains.mps.openapi.editor.cells.CellActionType;
import jetbrains.mps.openapi.editor.cells.EditorCell;
import jetbrains.mps.openapi.editor.cells.KeyMapAction;
import jetbrains.mps.util.Pair;

import java.awt.event.KeyEvent;
import java.util.Collection;

public class EditorComponentKeyboardHandler implements KeyboardHandler {

  private final KeymapHandler<KeyEvent> myKeymapHandler;

  EditorComponentKeyboardHandler(KeymapHandler<KeyEvent> keymapHandler) {
    myKeymapHandler = keymapHandler;
  }

  @Override
  public boolean processKeyPressed(final EditorContext editorContext, final KeyEvent keyEvent) {
    if (processKeyMaps(editorContext, keyEvent)) {
      return true;
    }

    jetbrains.mps.openapi.editor.cells.CellActionType actionType = editorContext.getNodeEditorComponent().getActionType(keyEvent, editorContext);
    EditorCell selectedCell = editorContext.getSelectedCell();

    // process action
    if (selectedCell != null && actionType != null && editorContext.getEditorComponent().getActionHandler().executeAction(selectedCell, actionType)) {
      return true;
    }

    // The browser fallback receives Ctrl+Up for structural selection.

    return false;
  }

  @Override
  public boolean processKeyTyped(EditorContext editorContext, final KeyEvent keyEvent) {
    if (processKeyMaps(editorContext, keyEvent)) {
      return true;
    }

    // normal keys + TAB ESC
    jetbrains.mps.nodeEditor.cells.EditorCell selectedCell = (jetbrains.mps.nodeEditor.cells.EditorCell) editorContext.getSelectedCell();
    if (selectedCell == null) {
      return false;
    }

    if (selectedCell.processKeyTyped(keyEvent, false)) {
      keyEvent.consume();
      return true;
    }

    CellActionType actionType = editorContext.getEditorComponent().isAutomaticSubstitutionEnabled() ? editorContext.getNodeEditorComponent().getActionType(keyEvent, editorContext) : null;
    if (actionType != null) {
      // we only trigger autovalidation of an error cell when doing RIGHT_TRANSFORM (typing space at the end of an error cell)
      // trying to validate for LEFT_TRANSFORM will yield no results since the completion popup will not have an exact match available
      boolean autoValidate = actionType == CellActionType.RIGHT_TRANSFORM;
      boolean errorState = selectedCell.isErrorState();
      if (errorState && autoValidate) {
        EditorComputable<Boolean> validateCommand = new EditorComputable<Boolean>(editorContext) {
          @Override
          protected Boolean doCompute() {
            return APICellAdapter.validate(selectedCell, true, false);
          }
        };
        editorContext.getRepository().getModelAccess().executeCommand(validateCommand);
        if (validateCommand.getResult()) {
          return true;
        }
      } else {
        if (editorContext.getEditorComponent().getActionHandler().executeAction(selectedCell, actionType)) {
          return true;
        }
      }
    }

    if (selectedCell.processKeyTyped(keyEvent, true)) {
      keyEvent.consume();
      return true;
    }

    return false;
  }

  @Override
  public boolean processKeyReleased(EditorContext editorContext, KeyEvent keyEvent) {
    return false;
  }

  private boolean processKeyMaps(EditorContext editorContext, KeyEvent keyEvent) {
    EditorCell selectedCell = editorContext.getSelectedCell();
    if (selectedCell == null) {
      return false;
    }

    if (keyEvent.getID() != KeyEvent.KEY_PRESSED && keyEvent.getID() != KeyEvent.KEY_TYPED) {
      // Looks like unnecessary check
      return false;
    }

    int keyCode = keyEvent.getKeyCode();
    if (keyCode == KeyEvent.VK_CONTROL || keyCode == KeyEvent.VK_ALT || keyCode == KeyEvent.VK_SHIFT ||
        keyCode == KeyEvent.VK_PAGE_UP || keyCode == KeyEvent.VK_PAGE_DOWN) {
      return false;
    }

    Collection<Pair<KeyMapAction, EditorCell>> actionCellPairs = myKeymapHandler.getActions(selectedCell, keyEvent, editorContext);
    if (actionCellPairs.isEmpty()) {
      return false;
    }
    Pair<KeyMapAction, EditorCell> first = actionCellPairs.iterator().next();
    if (actionCellPairs.size() == 1 && !first.o1.isMenuAlwaysShown()) {
      myKeymapHandler.executeAction(first.o1, first.o2, editorContext);
    } else {
      // show menu
      myKeymapHandler.showActionsMenu(actionCellPairs, editorContext, selectedCell);
    }
    return true;
  }

  @Override
  public boolean processTextChanged(EditorContext editorContext, TextChangeEvent textChangeEvent) {
    EditorCell selectedCell = editorContext.getSelectedCell();
    return selectedCell != null && ((jetbrains.mps.nodeEditor.cells.EditorCell) selectedCell).processTextChanged(textChangeEvent);
  }
}
