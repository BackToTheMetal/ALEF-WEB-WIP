package com.intellij.codeInsight.hint;
import com.intellij.openapi.editor.Editor;
import com.intellij.ui.HintHint;
import com.intellij.ui.LightweightHint;
import java.awt.Point;
public interface TooltipRenderer { LightweightHint show(Editor editor, Point point, boolean alignToRight, TooltipGroup group, HintHint hint); }
