/*
 * Copyright 2000-2022 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */
package jetbrains.mps.nodeEditor.cellMenu;

import com.intellij.codeInsight.CodeInsightSettings;
import com.intellij.util.ConcurrencyUtil;
import jetbrains.mps.RuntimeFlags;
import jetbrains.mps.logging.Logger;
import jetbrains.mps.nodeEditor.EditorComponent;
import jetbrains.mps.nodeEditor.IntelligentInputUtil;
import jetbrains.mps.nodeEditor.SubstituteActionComparator;
import jetbrains.mps.openapi.editor.cells.EditorCell;
import jetbrains.mps.openapi.editor.cells.SubstituteAction;
import jetbrains.mps.openapi.editor.cells.SubstituteInfo;
import jetbrains.mps.typechecking.TypecheckingFacade;
import jetbrains.mps.typechecking.TypecheckingSession;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class CompletionHelper {

  private static final Logger LOG = Logger.getLogger(CompletionHelper.class);

  // can be shared among all helpers
  private static final ScheduledExecutorService scheduler = ConcurrencyUtil.newSingleScheduledThreadExecutor("CompletionHelper");
  private static final int DEFAULT_POPUP_DELAY_MILLIS = parsePopupDelay(500);

  private final EditorComponent myEditorComponent;
  private CompletionCalculationTask currentCompletionCalculationTask;
  private ScheduledFuture<?> myTask;

  public CompletionHelper(EditorComponent editorComponent) {
    myEditorComponent = editorComponent;
  }

  /**
   * Automatic completion popup.
   */
  public void show(@NotNull jetbrains.mps.nodeEditor.cells.EditorCell cell) {
    if (myEditorComponent.getNodeSubstituteChooser().isVisible()) {
      return;
    }

    SubstituteInfo substituteInfo = cell.getSubstituteInfo();
    if (substituteInfo == null) {
      return;
    }
    NodeSubstitutePatternEditor patternEditor = cell.createSubstitutePatternEditor();
    scheduleActivation(cell, substituteInfo, patternEditor);
  }

  private void scheduleActivation(jetbrains.mps.nodeEditor.cells.EditorCell cell, SubstituteInfo substituteInfo, NodeSubstitutePatternEditor patternEditor) {
    if (currentCompletionCalculationTask != null) {
      myTask.cancel(false);
      currentCompletionCalculationTask.cancel();
    }
    NodeSubstituteChooserHandler substituteChooserHandler = new NodeSubstituteChooserHandler(cell, myEditorComponent, substituteInfo, patternEditor, false);
    currentCompletionCalculationTask = new CompletionCalculationTask(substituteChooserHandler);
    myTask = scheduler.schedule(currentCompletionCalculationTask, DEFAULT_POPUP_DELAY_MILLIS, TimeUnit.MILLISECONDS);
  }

  public void hide() {
    NodeSubstituteChooser nodeSubstituteChooser = myEditorComponent.getNodeSubstituteChooser();
    if (nodeSubstituteChooser != null) {
      nodeSubstituteChooser.setVisible(false);
    }
  }

  /**
   * User activated completion popup.
   */
  public void showNow(@NotNull EditorCell cell, @NotNull SubstituteInfo substituteInfo, boolean resetPattern, boolean isSmart) {
    if (myEditorComponent.getNodeSubstituteChooser().isVisible()) {
      return;
    }
    NodeSubstitutePatternEditor patternEditor = ((jetbrains.mps.nodeEditor.cells.EditorCell) cell).createSubstitutePatternEditor();
    if (resetPattern) {
      patternEditor.toggleReplaceMode();
    }
    NodeSubstituteChooserHandler substituteChooserHandler = new NodeSubstituteChooserHandler(cell, myEditorComponent, substituteInfo, patternEditor, isSmart);
    if (!substituteChooserHandler.tryToSubstituteImmediately()) {
      substituteChooserHandler.showNodeSubstituteChooser(myEditorComponent.getNodeSubstituteChooser());
    }
  }

  private static class CompletionCalculationTask implements Runnable {

    private final NodeSubstituteChooserHandler mySubstituteChooserHandler;
    private CompletionCustomizationManager myCompletionCustomizationManager;
    private boolean canceled;

    CompletionCalculationTask(NodeSubstituteChooserHandler substituteChooserHandler) {
      mySubstituteChooserHandler = substituteChooserHandler;
    }

    @Override
    public void run() {
      myCompletionCustomizationManager = new CompletionCustomizationManager(mySubstituteChooserHandler.getEditorCell());
      List<SubstituteAction> matchingActions = computeMatchingActions();
      if (matchingActions.isEmpty()) {
        return;
      }
      mySubstituteChooserHandler.getEditorComponent().getEditorContext().getRepository().getModelAccess().runReadInEDT(() -> showCompletion(matchingActions));
    }

    private void showCompletion(List<SubstituteAction> matchingActions) {
      if (canceled) {
        return;
      }
      if (mySubstituteChooserHandler.getEditorCell() == mySubstituteChooserHandler.getEditorComponent().getSelectedCell()
          && !mySubstituteChooserHandler.getEditorComponent().getNodeSubstituteChooser().isVisible()) {
        mySubstituteChooserHandler.showNodeSubstituteChooser(matchingActions, myCompletionCustomizationManager);
      }
    }

    private List<SubstituteAction> computeMatchingActions() {
      String pattern = mySubstituteChooserHandler.getPatternEditor().getPattern();

      final List<SubstituteAction> matchingActions = getMatchingActions(pattern);

      mySubstituteChooserHandler.getEditorComponent().getEditorContext().getRepository().getModelAccess().runReadAction(() -> {
        boolean needToTrim;
        String trimPattern = IntelligentInputUtil.trimLeft(pattern);
        if (pattern.equals(trimPattern)) {
          needToTrim = false;
        } else {
          needToTrim = true;
          for (SubstituteAction action : matchingActions) {
            if (action.canSubstitute(pattern)) {
              needToTrim = false;
              break;
            }
          }
        }
        if (needToTrim) {
          matchingActions.clear();
          matchingActions.addAll(getMatchingActions(trimPattern));
        }
        String sortPattern = needToTrim ? trimPattern : pattern;

        matchingActions.removeIf(action -> !myCompletionCustomizationManager.getVisibility(action, pattern));
        myCompletionCustomizationManager.sort(matchingActions, pattern);
        if (!pattern.isEmpty()) {
          try {
            matchingActions.sort(new SubstituteActionComparator(sortPattern) {
              private final Map<SubstituteAction, Integer> myRatesMap = new HashMap<>();
              private final Map<SubstituteAction, String> myVisibleMatchingTextsMap = new HashMap<>();
              private final Map<SubstituteAction, Boolean> myCanSubstituteStrictlyMap = new HashMap<>();
              private final Map<SubstituteAction, Boolean> myStartsWithMap = new HashMap<>();
              private final Map<SubstituteAction, Boolean> myStartsWithLowerCaseMap = new HashMap<>();


              @Override
              protected String getVisibleMatchingText(SubstituteAction action) {
                return myVisibleMatchingTextsMap.computeIfAbsent(action, super::getVisibleMatchingText);
              }

              @Override
              protected boolean canSubstituteStrictly(SubstituteAction action) {
                return myCanSubstituteStrictlyMap.computeIfAbsent(action, super::canSubstituteStrictly);
              }

              @Override
              protected int getRate(SubstituteAction action) {
                return myRatesMap.computeIfAbsent(action, super::getRate);
              }

              @Override
              protected boolean startsWith(SubstituteAction action) {
                return myStartsWithMap.computeIfAbsent(action, super::startsWith);
              }

              @Override
              protected boolean startsWithLowerCase(SubstituteAction action) {
                return myStartsWithLowerCaseMap.computeIfAbsent(action, super::startsWithLowerCase);
              }
            });
          } catch (Exception e) {
            LOG.error(e, e);
          }
        }
      });

      return matchingActions;
    }

    private List<SubstituteAction> getMatchingActions(String pattern) {
      TypecheckingSession typecheckingSession = mySubstituteChooserHandler.getEditorComponent().getTypecheckingSession();
      if (typecheckingSession == null) {
        return Collections.emptyList();
      } else {
        return TypecheckingFacade.getFromContext().computeWithSession(typecheckingSession,
                                                                      (session) -> mySubstituteChooserHandler.getSubstituteInfo().getMatchingActions(pattern, false));
      }
    }

    public void cancel() {
      canceled = true;
    }
  }

  public static boolean isAutoPopup() {
    return CodeInsightSettings.getInstance().AUTO_POPUP_COMPLETION_LOOKUP && !RuntimeFlags.isTestMode();
  }

  private static int parsePopupDelay(int defValue) {
    try {
      return Integer.parseInt(System.getProperty("mps.completion.delay.millis"));
    } catch (Exception ignored) {
    }
    return defValue;
  }
}
