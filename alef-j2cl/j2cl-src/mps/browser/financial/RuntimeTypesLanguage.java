package mpsj2cl.financial;

import jetbrains.mps.smodel.language.LanguageRuntime;
import jetbrains.mps.smodel.adapter.ids.SLanguageId;
import jetbrains.mps.smodel.runtime.ILanguageAspect;
import jetbrains.mps.smodel.runtime.StructureAspectDescriptor;

/** Generated typesystem metadata needed by the equation engine, not a typesystem-language editor. */
final class RuntimeTypesLanguage extends LanguageRuntime {
  @Override public String getNamespace() { return "jetbrains.mps.lang.typesystem"; }
  @Override public SLanguageId getId() { return SLanguageId.deserialize("7a5dda62-9140-4668-ab76-d5ed1746f2b2"); }
  @Override public int getVersion() { return 0; }
  @Override protected <T extends ILanguageAspect> T createAspect(Class<T> type) {
    if (type == StructureAspectDescriptor.class) return type.cast(new jetbrains.mps.lang.typesystem.structure.StructureAspectDescriptor());
    return null;
  }
}
