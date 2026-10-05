/* Copyright 2003-2020 JetBrains s.r.o. Licensed under the Apache License, Version 2.0. */
package jetbrains.mps.ide.undo;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;
import jetbrains.mps.ide.ThreadUtils;
import jetbrains.mps.smodel.ModelRenameUndoableAction;
import jetbrains.mps.smodel.SNodeUndoableAction;
import jetbrains.mps.smodel.UndoHandler;
import jetbrains.mps.smodel.undo.UndoContext;
import org.jetbrains.mps.openapi.module.SRepository;

/** Collects actual MPS model changes into one undo action per outer browser command. */
public class WorkbenchUndoHandler implements UndoHandler {
  private UndoActionsCollector myActionsCollector;
  private final Map<SRepository, OnReloadingUndoCleaner> myReloadCleaners = new IdentityHashMap<>();
  private SRepository myCommandRepository;
  private int myCommandDepth;
  private boolean myDisposed;

  @Override public void addUndoableAction(SNodeUndoableAction action) {
    if (needRegisterAction()) myActionsCollector.addAction(action);
  }

  @Override public void addUndoableAction(ModelRenameUndoableAction action) {
    if (needRegisterAction()) myActionsCollector.addAction(action);
  }

  private boolean needRegisterAction() {
    return !myDisposed && myActionsCollector != null && ThreadUtils.isInEDT();
  }

  public void startCommand(UndoContext context) {
    if (myDisposed) throw new IllegalStateException("Undo handler is disposed");
    Objects.requireNonNull(context);
    SRepository repository = Objects.requireNonNull(context.getRepository());
    if (myCommandDepth > 0) {
      if (repository != myCommandRepository) throw new IllegalStateException("Nested command changed repositories");
      myCommandDepth++;
      return;
    }
    myReloadCleaners.computeIfAbsent(repository, OnReloadingUndoCleaner::new);
    myCommandRepository = repository;
    myActionsCollector = new UndoActionsCollector(context);
    myCommandDepth = 1;
  }

  public void flushCommand() {
    if (myDisposed) return;
    if (myCommandDepth == 0) throw new IllegalStateException("No command to flush");
    if (--myCommandDepth > 0) return;
    UndoActionsCollector collector = myActionsCollector;
    myActionsCollector = null;
    myCommandRepository = null;
    collector.flushAndDispose();
  }

  /** The project disposes the handler before closing its repository. */
  public void dispose() {
    if (myDisposed) return;
    myDisposed = true;
    myActionsCollector = null;
    myCommandRepository = null;
    myCommandDepth = 0;
    for (OnReloadingUndoCleaner cleaner : myReloadCleaners.values()) cleaner.dispose();
    myReloadCleaners.clear();
  }
}
