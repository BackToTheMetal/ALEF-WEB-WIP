/* Copyright 2003-2026 JetBrains s.r.o. Licensed under the Apache License, Version 2.0. */
package jetbrains.mps.ide.datatransfer;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import jetbrains.mps.project.Project;
import org.jetbrains.mps.openapi.language.SLanguage;
import org.jetbrains.mps.openapi.model.SModel;
import org.jetbrains.mps.openapi.model.SModelReference;

/** Browser import selection through a host policy; no blocking desktop dialog. */
public final class AddRequiredImportsDialog {
  public interface ImportPolicy {
    /** Return null to decline imports. A policy must decide without blocking the browser event loop. */
    Selection select(Project project, SModelReference[] models, SLanguage[] languages);
  }
  public static final class Selection {
    private final SModelReference[] models;
    private final SLanguage[] languages;
    public Selection(SModelReference[] models, SLanguage[] languages) {
      this.models = Arrays.copyOf(models, models.length);
      this.languages = Arrays.copyOf(languages, languages.length);
    }
  }
  private final Project project;
  private final SModelReference[] models;
  private final SLanguage[] languages;
  private Selection selected;
  private boolean shown;

  public AddRequiredImportsDialog(Project project, SModelReference[] models, SLanguage[] languages) {
    this.project = Objects.requireNonNull(project);
    this.models = Arrays.copyOf(models, models.length);
    this.languages = Arrays.copyOf(languages, languages.length);
  }
  public void show() {
    if (shown) throw new IllegalStateException("Import selection has already completed");
    ImportPolicy policy = project.getComponent(ImportPolicy.class);
    if (policy == null) throw new UnsupportedOperationException("Register a browser import policy before pasting dependencies");
    Selection choice = policy.select(project, Arrays.copyOf(models, models.length), Arrays.copyOf(languages, languages.length));
    if (choice != null) {
      validate(models, choice.models);
      validate(languages, choice.languages);
      selected = new Selection(choice.models, choice.languages);
    }
    shown = true;
  }
  public boolean isOK() { return shown && selected != null; }
  public SModelReference[] getSelectedImports() { return selected == null ? new SModelReference[0] : Arrays.copyOf(selected.models, selected.models.length); }
  public SLanguage[] getSelectedLanguages() { return selected == null ? new SLanguage[0] : Arrays.copyOf(selected.languages, selected.languages.length); }
  public Runnable asUpdateCommand(SModel model) {
    if (!isOK()) throw new IllegalStateException("Imports were not selected");
    SModelReference[] chosenModels = Arrays.copyOf(selected.models, selected.models.length);
    SLanguage[] chosenLanguages = Arrays.copyOf(selected.languages, selected.languages.length);
    return () -> CopyPasteUtil.applyRequiredImports(model, Arrays.asList(chosenLanguages), Arrays.asList(chosenModels));
  }
  private static <T> void validate(T[] available, T[] chosen) {
    HashSet<T> remaining = new HashSet<>(Arrays.asList(available));
    for (T item : chosen) {
      if (item == null || !remaining.remove(item)) throw new IllegalArgumentException("Import policy selected an unknown or duplicate dependency");
    }
  }
}
