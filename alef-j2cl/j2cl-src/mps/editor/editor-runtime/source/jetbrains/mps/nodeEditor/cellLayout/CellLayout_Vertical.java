/*
 * Copyright 2003-2011 JetBrains s.r.o.
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
package jetbrains.mps.nodeEditor.cellLayout;

import gnu.trove.TIntArrayList;
import jetbrains.mps.editor.runtime.HtmlTextBuilderImpl;
import jetbrains.mps.editor.runtime.TextBuilderImpl;
import jetbrains.mps.editor.runtime.style.CellAlign;
import jetbrains.mps.editor.runtime.style.DefaultBaseLine;
import jetbrains.mps.editor.runtime.style.StyleAttributes;
import jetbrains.mps.openapi.editor.HtmlTextBuilder;
import jetbrains.mps.openapi.editor.TextBuilder;
import jetbrains.mps.openapi.editor.cells.EditorCell;
import jetbrains.mps.openapi.editor.cells.EditorCell_Collection;

import java.util.Deque;
import java.util.LinkedList;

/**
 * User: Sergey Dmitriev
 * Date: Jan 19, 2005
 */
public class CellLayout_Vertical extends AbstractCellLayout {
  private boolean myGridLayout = false;

  public void setGridLayout(boolean gridLayout) {
    myGridLayout = gridLayout;
  }

  public boolean isGridLayout() {
    return myGridLayout;
  }

  @Override
  public boolean canBeFolded() {
    return true;
  }

  @Override
  public void doLayout(EditorCell_Collection editorCells) {
    Iterable<EditorCell> cells = editorCells.getContentCells();
    EditorCell closingBrace = editorCells.getClosingBrace();
    EditorCell openingBrace = editorCells.getOpeningBrace();
    boolean usesBraces = editorCells.usesBraces();
    if (usesBraces) {
      closingBrace.relayout();
      openingBrace.moveTo(editorCells.getX(), editorCells.getY());
      openingBrace.relayout();
    }

    final int x = usesBraces ? editorCells.getX() + openingBrace.getWidth() : editorCells.getX();
    final int y = editorCells.getY();
    int braceIndent = 0;
    int width = 0;
    int height = 0;
    for (EditorCell editorCell : cells) {
      editorCell.moveTo(x, y + height + editorCell.getTopInset());
      editorCell.relayout();
      int cellHeight = editorCell.getHeight();
      int indent = getBracesIndent(editorCell);
      braceIndent = Math.max(indent, braceIndent);
      height += cellHeight /*+ editorCell.getTopInset() + editorCell.getBottomInset()*/;
    }
    for (EditorCell editorCell : cells) {
      int lastCellWidth = editorCell.getWidth();
      int indent = getBracesIndent(editorCell);
      int delta = braceIndent - indent;
      width = Math.max(width, lastCellWidth + delta);
    }

    for (EditorCell editorCell : cells) {
      int cellX = editorCell.getX();
      int cellY = editorCell.getY();
      int newCellX = cellX;
      CellAlign cellAlign = editorCell.getStyle().get(StyleAttributes.HORIZONTAL_ALIGN);
      if (cellAlign == CellAlign.CENTER && !myGridLayout) {
        newCellX = cellX + (width - editorCell.getWidth()) / 2;
      } else if (cellAlign == CellAlign.RIGHT && !myGridLayout) {
        newCellX = cellX + width - editorCell.getWidth();
      }
      if (newCellX != cellX) {
        editorCell.moveTo(newCellX, cellY);
        editorCell.relayout();
      }
    }

    editorCells.setArtificialBracesIndent(braceIndent);
    for (EditorCell editorCell : cells) {
      int cellX = editorCell.getX();
      int cellY = editorCell.getY();
      int indent = getBracesIndent(editorCell);
      int newCellX = cellX - indent + braceIndent;
      if (newCellX != cellX) {
        editorCell.moveTo(newCellX, cellY);
        editorCell.relayout();
      }
    }

    if (myGridLayout) {
      TIntArrayList columnWidths = new TIntArrayList();
      for (EditorCell editorCell : cells) {
        if (editorCell instanceof EditorCell_Collection && ((EditorCell_Collection) editorCell).getCellLayout() instanceof CellLayout_Horizontal) {
          EditorCell_Collection collectionCell = (EditorCell_Collection) editorCell;
          int columnNumber = 0;
          for (EditorCell columnCell : collectionCell) {
            if (columnNumber < columnWidths.size()) {
              columnWidths.set(columnNumber, Math.max(columnWidths.get(columnNumber), columnCell.getWidth()));
            } else {
              columnWidths.add(columnCell.getWidth());
            }
            columnNumber++;
          }
        }
      }

      for (EditorCell editorCell : cells) {
        if (editorCell instanceof EditorCell_Collection && ((EditorCell_Collection) editorCell).getCellLayout() instanceof CellLayout_Horizontal) {
          EditorCell_Collection collectionCell = (EditorCell_Collection) editorCell;
          int lineWidth = 0;
          int columnNumber = 0;
          for (EditorCell columnCell : collectionCell) {
            int columnWidth = columnWidths.get(columnNumber);
            CellAlign cellAlign = columnCell.getStyle().get(StyleAttributes.HORIZONTAL_ALIGN);
            if (cellAlign == CellAlign.RIGHT) {
              setX(columnCell, x + lineWidth + columnWidth - columnCell.getWidth());
            } else if (cellAlign == CellAlign.CENTER) {
              int halfGap = (columnWidth - columnCell.getWidth()) / 2;
              setX(columnCell, x + lineWidth + halfGap);
            } else {
              setX(columnCell, x + lineWidth);
              columnCell.setWidth(columnWidth);
            }
            lineWidth += columnWidth;
            columnNumber++;
          }
          editorCell.setWidth(lineWidth);
          width = Math.max(width, lineWidth);
        }
      }
    }

    if (usesBraces) {
      closingBrace.setY(y + height - closingBrace.getHeight());
      if (myGridLayout) {
        closingBrace.setX(x + width);
        width += closingBrace.getWidth();
      } else {
        EditorCell lastCell = editorCells.lastContentCell();
        while ((lastCell instanceof EditorCell_Collection) && !((EditorCell_Collection) lastCell).isCollapsed()) {
          lastCell = ((EditorCell_Collection) lastCell).lastCell();
        }
        if (lastCell == null) {
          lastCell = editorCells.lastContentCell() != null ? editorCells.lastContentCell() : openingBrace;
        }
        closingBrace.setX(lastCell.getX() + lastCell.getWidth()/*x + lastCellWidth*/);
        width = Math.max(width, (closingBrace.getX() - x) + closingBrace.getWidth());
      }
      width += openingBrace.getWidth();
    }
    editorCells.setWidth(width);
    editorCells.setHeight(height);
  }

  private void setX(EditorCell cell, int newX) {
    int deltaX = newX - cell.getX();
    Deque<EditorCell> cellsToMove = new LinkedList<>();
    cellsToMove.add(cell);
    while (!cellsToMove.isEmpty()) {
      EditorCell nextCell = cellsToMove.removeFirst();
      nextCell.setX(nextCell.getX() + deltaX);
      if (nextCell instanceof EditorCell_Collection) {
        for (EditorCell childCell : ((EditorCell_Collection) nextCell)) {
          cellsToMove.addLast(childCell);
        }
      }
    }
  }

  private int getBracesIndent(EditorCell cell) {
    return cell instanceof EditorCell_Collection ? ((EditorCell_Collection) cell).getBracesIndent() : 0;
  }

  @Override
  public int getRightInternalInset(EditorCell_Collection editorCell_collection) {
    return editorCell_collection.isEmpty() ? 0 : editorCell_collection.firstCell().getRightInset();
  }

  @Override
  public TextBuilder doLayoutText(Iterable<EditorCell> editorCells) {
    TextBuilder result = new TextBuilderImpl();
    for (EditorCell editorCell : editorCells) {
      result.appendToTheBottom(editorCell.renderText());
    }
    return result;
  }

  @Override
  public HtmlTextBuilder doLayoutHtml(Iterable<EditorCell> editorCells) {
    HtmlTextBuilder result = new HtmlTextBuilderImpl();
    for (EditorCell editorCell : editorCells) {
      result.appendToTheBottom(editorCell.renderHtml());
    }
    return result;
  }

  @Override
  public int getAscent(EditorCell_Collection editorCells) {
    for (EditorCell cell : editorCells) {
      if (cell.getStyle().get(StyleAttributes.BASE_LINE_CELL)) {
        return cell.getY() - editorCells.getY() + cell.getAscent();
      }
    }

    DefaultBaseLine bL = editorCells.getStyle().get(StyleAttributes.DEFAULT_BASE_LINE);

    int result = 0;
    for (EditorCell cell : editorCells) {
      result = cell.getAscent();
      if (result > 0) {
        break;
      }
    }

    switch (bL) {
      case FIRST: // default behavior
        return result;
      case CENTER:
        return Math.max(result, editorCells.getHeight() / 2);
      case LAST:
        if (!editorCells.isEmpty()) {
          EditorCell lastCell = editorCells.lastCell();
          return lastCell.getY() - editorCells.getY() + lastCell.getAscent();
        }
    }

    return 0;
  }

  public String toString() {
    return "Vertical";
  }

  @Override
  public void requestRelayout(EditorCell_Collection editorCells) {
    super.requestRelayout(editorCells);
    if (myGridLayout) {
      for (EditorCell childCell : editorCells) {
        if (childCell instanceof EditorCell_Collection) {
          for (EditorCell innerCell : (EditorCell_Collection) childCell) {
            innerCell.requestRelayout();
          }
        } else {
          childCell.requestRelayout();
        }
      }
    }
  }
}
