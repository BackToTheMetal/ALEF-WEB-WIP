package extensionsj2cl;

import java.util.Collection;
import java.util.Collections;
import jetbrains.mps.smodel.adapter.ids.MetaIdFactory;
import jetbrains.mps.smodel.adapter.ids.SConceptId;
import jetbrains.mps.smodel.adapter.ids.SLanguageId;
import jetbrains.mps.smodel.adapter.structure.MetaAdapterFactory;
import jetbrains.mps.smodel.language.LanguageRuntime;
import jetbrains.mps.smodel.runtime.BaseStructureAspectDescriptor;
import jetbrains.mps.smodel.runtime.ConceptDescriptor;
import jetbrains.mps.smodel.runtime.ILanguageAspect;
import jetbrains.mps.smodel.runtime.StructureAspectDescriptor;
import jetbrains.mps.smodel.runtime.impl.ConceptDescriptorBuilder2;
import org.jetbrains.mps.openapi.language.SConcept;

/** A compiled-only specialization of the fixture's group containment. */
final class SpecializedLanguage extends LanguageRuntime {
  static final SConcept CONCEPT = MetaAdapterFactory.getConcept(188, 3, 1, "extensionsj2cl.specialized.structure.LiteralGroup");
  @Override public String getNamespace() { return "extensionsj2cl.specialized"; }
  @Override public SLanguageId getId() { return MetaIdFactory.langId(188, 3); }
  @Override public int getVersion() { return 0; }
  @Override protected <T extends ILanguageAspect> T createAspect(Class<T> type) {
    if (type == jetbrains.mps.openapi.editor.descriptor.EditorAspectDescriptor.class) {
      return type.cast(new jetbrains.mps.nodeEditor.EditorAspectDescriptorBase() {
        @Override public Collection<jetbrains.mps.openapi.editor.descriptor.ConceptEditor> getDeclaredEditors(org.jetbrains.mps.openapi.language.SAbstractConcept concept) {
          return concept.equals(CONCEPT) ? Collections.singletonList(new WrapperEditor()) : Collections.emptyList();
        }
      });
    }
    if (type != StructureAspectDescriptor.class) return null;
    ConceptDescriptor descriptor = new ConceptDescriptorBuilder2(getNamespace(), "LiteralGroup", 188, 3, 1)
        .class_(false, false).super_(94, 112, 5)
        .aggregate("literalMembers", 2).target(94, 112, 1).specialize(94, 112, 5, 6).multiple(true).optional(true).done().create();
    return type.cast(new BaseStructureAspectDescriptor() {
      @Override public ConceptDescriptor getDescriptor(SConceptId id) { return descriptor.getId().equals(id) ? descriptor : null; }
      @Override public Collection<ConceptDescriptor> getDescriptors() { return Collections.singletonList(descriptor); }
    });
  }

  static final class WrapperEditor implements jetbrains.mps.openapi.editor.descriptor.ConceptEditor {
    @Override public Collection<String> getContextHints() { return Collections.emptyList(); }
    @Override public jetbrains.mps.openapi.editor.cells.EditorCell createInspectedCell(jetbrains.mps.openapi.editor.EditorContext context, org.jetbrains.mps.openapi.model.SNode node) {
      return createEditorCell(context, node);
    }
    @Override public jetbrains.mps.openapi.editor.cells.EditorCell createEditorCell(jetbrains.mps.openapi.editor.EditorContext context, org.jetbrains.mps.openapi.model.SNode node) {
      return new jetbrains.mps.nodeEditor.cells.EditorCell_Constant(context, node, "wrapper");
    }
  }
}
