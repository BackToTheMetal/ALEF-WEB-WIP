package extensionsj2cl;

import java.util.ArrayList;
import java.util.Collection;
import jetbrains.mps.smodel.adapter.ids.SConceptId;
import jetbrains.mps.smodel.adapter.ids.SLanguageId;
import jetbrains.mps.smodel.language.LanguageRuntime;
import jetbrains.mps.smodel.runtime.BaseStructureAspectDescriptor;
import jetbrains.mps.smodel.runtime.ConceptDescriptor;
import jetbrains.mps.smodel.runtime.ILanguageAspect;
import jetbrains.mps.smodel.runtime.StructureAspectDescriptor;
import jetbrains.mps.smodel.runtime.impl.ConceptDescriptorBuilder2;
import mpsj2cl.editorlive.ExpressionLanguage;

/** Extends the handwritten test core metadata with the actual attribute identities. */
final class AttributeCoreLanguage extends LanguageRuntime {
  private final LanguageRuntime base = ExpressionLanguage.coreLanguage();
  private static final long HIGH = 0xceab519525ea4f22L, LOW = 0x9b92103b95ca8c0cL;
  @Override public String getNamespace() { return base.getNamespace(); }
  @Override public SLanguageId getId() { return base.getId(); }
  @Override public int getVersion() { return 0; }
  @Override protected <T extends ILanguageAspect> T createAspect(Class<T> type) {
    if (type != StructureAspectDescriptor.class) return base.getAspect(type);
    Collection<ConceptDescriptor> descriptors = new ArrayList<>(base.getAspect(StructureAspectDescriptor.class).getDescriptors());
    descriptors.removeIf(d -> d.getId().getIdValue() == 0x10802efe25aL);
    descriptors.add(new ConceptDescriptorBuilder2(getNamespace(), "BaseConcept", HIGH, LOW, 0x10802efe25aL).class_(false, false)
        .aggregate("smodelAttribute", 0x47bf8397520e5942L).target(HIGH, LOW, 0x47bf8397520e5939L)
        .optional(true).ordered(true).multiple(true).done().create());
    descriptors.add(new ConceptDescriptorBuilder2(getNamespace(), "Attribute", HIGH, LOW, 0x47bf8397520e5939L).class_(true, false).create());
    descriptors.add(new ConceptDescriptorBuilder2(getNamespace(), "NodeAttribute", HIGH, LOW, 0x2eb1ad060897da54L).class_(true, false)
        .super_(HIGH, LOW, 0x47bf8397520e5939L).create());
    // The generated annotation editor checks for property attributes, even when absent.
    descriptors.add(new ConceptDescriptorBuilder2(getNamespace(), "PropertyAttribute", HIGH, LOW, 0x2eb1ad060897da56L).class_(true, false)
        .super_(HIGH, LOW, 0x47bf8397520e5939L).create());
    return type.cast(new BaseStructureAspectDescriptor() {
      @Override public ConceptDescriptor getDescriptor(SConceptId id) {
        for (ConceptDescriptor descriptor : descriptors) if (descriptor.getId().equals(id)) return descriptor;
        return null;
      }
      @Override public Collection<ConceptDescriptor> getDescriptors() { return descriptors; }
    });
  }
}
