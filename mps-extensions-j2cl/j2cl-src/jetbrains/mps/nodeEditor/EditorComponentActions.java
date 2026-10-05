/*
 * Copyright 2003-2024 JetBrains s.r.o.
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

import jetbrains.mps.editor.runtime.impl.cellActions.CellAction_CommentOrUncommentCurrentSelectedNode;
import jetbrains.mps.editor.runtime.impl.cellActions.CellAction_InsertPlaceholder;
import jetbrains.mps.nodeEditor.actions.CursorPositionTracker;
import jetbrains.mps.nodeEditor.cellActions.CellAction_CopyNode;
import jetbrains.mps.nodeEditor.cellActions.CellAction_CutNode;
import jetbrains.mps.nodeEditor.cellActions.CellAction_PasteNode;
import jetbrains.mps.nodeEditor.cellActions.CellAction_PasteNodeRelative;
import jetbrains.mps.nodeEditor.cellActions.CellAction_SideTransform;
import jetbrains.mps.nodeEditor.cellActions.SideTransformSubstituteInfo.Side;
import jetbrains.mps.nodeEditor.folding.CallAction_ToggleCellFolding;
import jetbrains.mps.nodeEditor.folding.CellAction_FoldCell;
import jetbrains.mps.nodeEditor.folding.CellAction_UnfoldCell;
import jetbrains.mps.nodeEditor.folding.CollapseAllCellAction;
import jetbrains.mps.nodeEditor.folding.CollapseRecursivelyCellAction;
import jetbrains.mps.openapi.editor.EditorComponent;
import jetbrains.mps.openapi.editor.cells.CellAction;
import jetbrains.mps.openapi.editor.cells.CellActionType;
import jetbrains.mps.openapi.editor.selection.Selection.SelectionDirection;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.function.Supplier;

public class EditorComponentActions {
  public final HashMap<CellActionType, CellAction> myActionMap;

  @NotNull
  private final EditorComponent myEditorComponent;

  EditorComponentActions(@NotNull EditorComponent editorComponent) {
    myEditorComponent = editorComponent;
    myActionMap = new HashMap<>();

    myActionMap.put(CellActionType.LEFT, new NodeEditorActions.MoveLeft());
    myActionMap.put(CellActionType.RIGHT, new NodeEditorActions.MoveRight());
    CursorPositionTracker cursorPositionTracker = new CursorPositionTracker(myEditorComponent.getEditorContext());
    myActionMap.put(CellActionType.UP, new NodeEditorActions.MoveUp(cursorPositionTracker));
    myActionMap.put(CellActionType.DOWN, new NodeEditorActions.MoveDown(cursorPositionTracker));
    myActionMap.put(CellActionType.NEXT, new NodeEditorActions.MoveNext());
    myActionMap.put(CellActionType.PREV, new NodeEditorActions.MovePrev());
    myActionMap.put(CellActionType.LOCAL_HOME, new NodeEditorActions.MoveLocal(true));
    myActionMap.put(CellActionType.LOCAL_END, new NodeEditorActions.MoveLocal(false));

    myActionMap.put(CellActionType.ROOT_HOME, new NodeEditorActions.MoveToRoot(true));
    myActionMap.put(CellActionType.ROOT_END, new NodeEditorActions.MoveToRoot(false));
    myActionMap.put(CellActionType.HOME, new NodeEditorActions.MoveHome());
    myActionMap.put(CellActionType.END, new NodeEditorActions.MoveEnd());
    myActionMap.put(CellActionType.PAGE_DOWN, new NodeEditorActions.MovePageUp());
    myActionMap.put(CellActionType.PAGE_UP, new NodeEditorActions.MovePageDown());

    myActionMap.put(CellActionType.SELECT_ALL, new NodeEditorActions.SelectAll());
    myActionMap.put(CellActionType.SELECT_UP, new NodeEditorActions.SelectUp());
    myActionMap.put(CellActionType.SELECT_DOWN, new NodeEditorActions.SelectDown());
    myActionMap.put(CellActionType.SELECT_RIGHT, new NodeEditorActions.SideSelect(SelectionDirection.RIGHT));
    myActionMap.put(CellActionType.SELECT_LEFT, new NodeEditorActions.SideSelect(SelectionDirection.LEFT));
    myActionMap.put(CellActionType.SELECT_NEXT, new NodeEditorActions.EnlargeSelection(true));
    myActionMap.put(CellActionType.SELECT_PREVIOUS, new NodeEditorActions.EnlargeSelection(false));

    myActionMap.put(CellActionType.COPY, new CellAction_CopyNode());
    myActionMap.put(CellActionType.CUT, new CellAction_CutNode());
    myActionMap.put(CellActionType.PASTE, new CellAction_PasteNode());
    myActionMap.put(CellActionType.PASTE_BEFORE, new CellAction_PasteNodeRelative(true));
    myActionMap.put(CellActionType.PASTE_AFTER, new CellAction_PasteNodeRelative(false));

    myActionMap.put(CellActionType.FOLD, new CellAction_FoldCell());
    myActionMap.put(CellActionType.UNFOLD, new CellAction_UnfoldCell());
    myActionMap.put(CellActionType.FOLD_ALL, new CollapseAllCellAction(true));
    myActionMap.put(CellActionType.UNFOLD_ALL, new CollapseAllCellAction(false));
    myActionMap.put(CellActionType.FOLD_RECURSIVELY, new CollapseRecursivelyCellAction(true));
    myActionMap.put(CellActionType.UNFOLD_RECURSIVELY, new CollapseRecursivelyCellAction(false));
    myActionMap.put(CellActionType.TOGGLE_FOLDING, new CallAction_ToggleCellFolding());

    myActionMap.put(CellActionType.RIGHT_TRANSFORM, new CellAction_SideTransform(Side.RIGHT));
    myActionMap.put(CellActionType.LEFT_TRANSFORM, new CellAction_SideTransform(Side.LEFT));

    myActionMap.put(CellActionType.COMPLETE, new NodeEditorActions.Complete(false));
    myActionMap.put(CellActionType.COMPLETE_SMART, new NodeEditorActions.Complete(true));

    myActionMap.put(CellActionType.SHOW_MESSAGE, new NodeEditorActions.ShowMessage());

    myActionMap.put(CellActionType.COMMENT, new CellAction_CommentOrUncommentCurrentSelectedNode());
    myActionMap.put(CellActionType.INSERT_PLACEHOLDER, new CellAction_InsertPlaceholder(true));
    myActionMap.put(CellActionType.INSERT_PLACEHOLDER_BEFORE, new CellAction_InsertPlaceholder(false));

    myActionMap.put(CellActionType.FIND, new NodeEditorActions.FindTextInEditor());
    myActionMap.put(CellActionType.FIND_NEXT, new NodeEditorActions.FindNextTextInEditor());
    myActionMap.put(CellActionType.FIND_PREV, new NodeEditorActions.FindPrevTextInEditor());
    myActionMap.put(CellActionType.FIND_STOP, new NodeEditorActions.CancelFindTextInEditor());
  }


  CellAction getComponentAction(final CellActionType type) {
    return myEditorComponent.getEditorContext().getRepository().getModelAccess().computeReadAction(new ActionGetter(type));
  }

  private class ActionGetter implements Supplier<CellAction> {
    private final CellActionType myType;

    private ActionGetter(CellActionType type) {
      myType = type;
    }

    @Override
    public CellAction get() {
      CellAction action = myActionMap.get(myType);
      if (action != null && action.canExecute(myEditorComponent.getEditorContext())) {
        return action;
      }
      return null;
    }
  }
}
