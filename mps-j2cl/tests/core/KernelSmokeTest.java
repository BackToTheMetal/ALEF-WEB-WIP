package mpsj2cl.core;

import java.util.Arrays;
import java.util.List;
import jetbrains.mps.baseLanguage.tuples.runtime.MultiTuple;
import jetbrains.mps.internal.collections.runtime.ListSequence;
import jetbrains.mps.internal.collections.runtime.Sequence;
import jetbrains.mps.smodel.SModel;
import jetbrains.mps.smodel.SModelId;
import jetbrains.mps.smodel.SModelReference;
import jetbrains.mps.smodel.SNode;
import jetbrains.mps.smodel.SNodeId;
import jetbrains.mps.smodel.TrivialModelDescriptor;
import jetbrains.mps.smodel.adapter.BootstrapAdapterFactory;
import org.jetbrains.mps.openapi.event.SNodeAddEvent;
import org.jetbrains.mps.openapi.event.SNodeRemoveEvent;
import org.jetbrains.mps.openapi.event.SPropertyChangeEvent;
import org.jetbrains.mps.openapi.event.SReferenceChangeEvent;
import org.jetbrains.mps.openapi.language.SConcept;
import org.jetbrains.mps.openapi.language.SContainmentLink;
import org.jetbrains.mps.openapi.language.SLanguage;
import org.jetbrains.mps.openapi.language.SProperty;
import org.jetbrains.mps.openapi.language.SReferenceLink;
import org.jetbrains.mps.openapi.model.SModelName;
import org.jetbrains.mps.openapi.model.SNodeChangeListener;

/** Exercises the real kernel graph and OpenAPI model view without an IDE or storage. */
public final class KernelSmokeTest {
  private KernelSmokeTest() {
  }

  public static String run() {
    // Bootstrap adapters are real MPS meta-objects for operation before language
    // descriptors are installed; this smoke test deliberately has no language registry.
    long languageHigh = 0x4d5053L;
    long languageLow = 0x4a32434cL;
    SConcept concept = BootstrapAdapterFactory.getConcept(languageHigh, languageLow, 1001L,
        "mpsj2cl.smoke.structure.Node");
    SProperty title = BootstrapAdapterFactory.getProperty(languageHigh, languageLow, 1001L, 1002L, "title");
    SContainmentLink children = BootstrapAdapterFactory.getContainmentLink(languageHigh, languageLow, 1001L, 1003L, "children");
    SReferenceLink target = BootstrapAdapterFactory.getReferenceLink(languageHigh, languageLow, 1001L, 1004L, "target");
    org.jetbrains.mps.openapi.model.SNode root = new SNode(concept, new SNodeId.Regular(1L));
    org.jetbrains.mps.openapi.model.SNode first = new SNode(concept, new SNodeId.Regular(2L));
    org.jetbrains.mps.openapi.model.SNode middle = new SNode(concept, new SNodeId.Regular(3L));
    org.jetbrains.mps.openapi.model.SNode last = new SNode(concept, new SNodeId.Regular(4L));
    require(root.getConcept() == concept && root.isInstanceOfConcept(concept), "concept identity");
    require("mpsj2cl.smoke.structure.Node".equals(concept.getQualifiedName()), "concept qualified name");
    root.setProperty(title, "root");
    first.setProperty(title, "first");
    last.setProperty(title, "last");
    root.addChild(children, first);
    root.addChild(children, last);
    root.insertChildBefore(children, middle, last);
    require(root.getFirstChild() == first && root.getLastChild() == last, "first/last child");
    require(first.getNextSibling() == middle && middle.getNextSibling() == last, "forward siblings");
    require(last.getPrevSibling() == middle && middle.getPrevSibling() == first, "backward siblings");
    require(middle.getParent() == root && middle.getContainingRoot() == root, "parent/root navigation");
    require(middle.getContainmentLink().equals(children), "containment role identity");
    root.setReferenceTarget(target, last);
    require(root.getReferenceTarget(target) == last, "direct reference between detached nodes");

    List<String> names = Sequence.fromIterable(root.getChildren(children))
        .where(node -> node.hasProperty(title)).select(node -> node.getProperty(title)).toList();
    require(names.equals(Arrays.asList("first", "last")), "MPS sequence filtering and mapping");
    require(ListSequence.fromArray(1, 2, 3).reversedList().toList().equals(Arrays.asList(3, 2, 1)),
        "MPS reversed sequence");
    MultiTuple._2<org.jetbrains.mps.openapi.model.SNode, Integer> tuple = new MultiTuple._2<>(root, 3);
    MultiTuple._2<org.jetbrains.mps.openapi.model.SNode, Integer> copied = new MultiTuple._2<>(null, null);
    copied.assign(tuple);
    require(copied._0() == root && copied._1() == 3 && tuple.equals(copied), "tuple copy and component access");
    require(tuple.hashCode() == copied.hashCode(), "tuple equality/hash contract");
    copied._1(4);
    require(!tuple.equals(copied) && tuple._1() == 3, "tuple copies mutate independently");

    org.jetbrains.mps.openapi.model.SModelId id = SModelId.regular("00000000-0000-4000-8000-000000000001");
    SModelReference reference = new SModelReference(null, id, new SModelName("mpsj2cl.smoke.model"));
    SModel data = new SModel(reference);
    org.jetbrains.mps.openapi.model.SModel model = new ObservableModelDescriptor(data);
    StringBuilder events = new StringBuilder();
    SNodeChangeListener listener = new SNodeChangeListener() {
      @Override public void nodeAdded(SNodeAddEvent event) {
        require(event.getModel() == model, "add event model identity");
        events.append(event.isRoot() ? 'A' : 'a');
      }
      @Override public void nodeRemoved(SNodeRemoveEvent event) {
        require(event.getModel() == model, "remove event model identity");
        events.append(event.isRoot() ? 'D' : 'd');
      }
      @Override public void propertyChanged(SPropertyChangeEvent event) {
        require(event.getNode() == root && event.getProperty().equals(title), "property event identity");
        events.append('P');
      }
      @Override public void referenceChanged(SReferenceChangeEvent event) {
        require(event.getModel() == model, "reference event model identity");
        events.append('R');
      }
    };
    model.addChangeListener(listener);
    // TrivialModelDescriptor supplies the OpenAPI view; its documented root writes
    // belong to the underlying real SModel data rather than the descriptor facade.
    data.addRootNode(root);
    require(model.isLoaded() && model.getRepository() == null, "loaded in-memory model without repository");
    require(model.getReference().equals(reference) && model.getModelId().equals(id), "model reference identity");
    require(root.getModel() == model && middle.getModel() == model, "subtree model ownership");
    require(model.getNode(new SNodeId.Regular(3L)) == middle, "node lookup by equal id");
    require(model.getRootNodes().iterator().next() == root && data.isRoot(root), "root indexing");
    require(root.getReferenceTarget(target) == last, "direct reference survives model attachment");
    require(last.getReference().getModelReference().equals(reference), "node reference identifies model");
    require(last.getReference().getNodeId().equals(last.getNodeId()), "node reference identifies node");
    root.setProperty(title, "updated \u03bb");
    root.setProperty(title, "updated \u03bb");
    require("updated \u03bb".equals(root.getProperty(title)), "property replacement and Unicode value");
    root.setProperty(title, null);
    require(!root.hasProperty(title) && !root.getProperties().iterator().hasNext(), "property removal");
    root.setProperty(title, "final");
    root.setReference(target, first.getReference());
    require(root.getReferenceTarget(target) == first, "local model reference resolves through node index");
    root.dropReference(target);
    require(root.getReference(target) == null && root.getReferenceTarget(target) == null, "reference removal");

    org.jetbrains.mps.openapi.model.SNode added = new SNode(concept, new SNodeId.Regular(5L));
    root.addChild(children, added);
    require(model.getNode(added.getNodeId()) == added && added.getModel() == model, "attached child enters model index");
    root.removeChild(middle);
    require(model.getNode(middle.getNodeId()) == null && middle.getParent() == null && middle.getModel() == null,
        "removed child leaves parent and model index");
    SLanguage language = concept.getLanguage();
    require(data.addLanguage(language, 3), "language import added");
    require(data.getLanguageImportVersion(language) == 3, "language import version");
    data.setLanguageImportVersion(language, 5);
    require(data.getLanguageImportVersion(language) == 5 && data.deleteLanguage(language), "language version update/removal");
    data.removeRootNode(root);
    require(!model.getRootNodes().iterator().hasNext(), "root removed");
    require(model.getNode(first.getNodeId()) == null && first.getModel() == null, "removed subtree leaves model index");
    require(first.getParent() == root, "detaching a root preserves its subtree");
    require("APPPRRadD".equals(events.toString()), "model change notifications: " + events);
    model.removeChangeListener(listener);
    data.addRootNode(root);
    require(model.getNode(first.getNodeId()) == first && first.getModel() == model, "detached subtree reattaches");
    data.removeRootNode(root);
    require("APPPRRadD".equals(events.toString()), "removed listener receives no later notifications");
    return "PASS";
  }

  /** Opt into the real event dispatcher; the trivial descriptor suppresses listeners by design. */
  private static final class ObservableModelDescriptor extends TrivialModelDescriptor {
    ObservableModelDescriptor(SModel data) {
      super(data);
    }

    @Override public void addChangeListener(SNodeChangeListener listener) {
      getNodeEventDispatch().addChangeListener(listener);
    }

    @Override public void removeChangeListener(SNodeChangeListener listener) {
      getNodeEventDispatch().removeChangeListener(listener);
    }
  }

  public static void main(String[] args) {
    System.out.print(run());
  }

  private static void require(boolean condition, String message) {
    if (!condition) {
      throw new AssertionError(message);
    }
  }
}
