package de.itemis.mps.editor.math.runtime;

import java.awt.Color;
import jetbrains.mps.editor.runtime.style.InheritableStyleAttribute;
import jetbrains.mps.nodeEditor.EditorAspectDescriptorBase;
import jetbrains.mps.openapi.editor.descriptor.EditorAspectDescriptor;
import jetbrains.mps.openapi.editor.descriptor.StyleAttributeProvider;
import jetbrains.mps.openapi.editor.style.StyleAttribute;
import jetbrains.mps.smodel.adapter.ids.MetaIdFactory;
import jetbrains.mps.smodel.adapter.ids.SLanguageId;
import jetbrains.mps.smodel.language.LanguageRuntime;
import jetbrains.mps.smodel.runtime.ILanguageAspect;

/** Runtime-only style aspect. Register before creating math cells if the full language is absent. */
public final class MathRuntimeLanguage extends LanguageRuntime {
  @Override public String getNamespace() { return "de.itemis.mps.editor.math"; }
  @Override public SLanguageId getId() { return MetaIdFactory.langId(0x766348f76a674b85L, 0x9323384840132299L); }
  @Override public int getVersion() { return 0; }
  @Override protected <T extends ILanguageAspect> T createAspect(Class<T> type) {
    if (type == EditorAspectDescriptor.class) return type.cast(new Styles());
    return null;
  }
  private static final class Styles extends EditorAspectDescriptorBase implements StyleAttributeProvider {
    @Override public StyleAttribute<?> getStyleAttribute(String name) {
      switch (name) {
        case "math-symbol-color": return new InheritableStyleAttribute<Color>(name);
        case "side-tranformation-helper-cells": return new InheritableStyleAttribute<Boolean>(name, true);
        default: return null;
      }
    }
  }
}
