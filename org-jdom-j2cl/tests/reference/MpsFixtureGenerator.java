import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import jetbrains.mps.persistence.MetaModelInfoProvider;
import jetbrains.mps.smodel.DefaultSModel;
import jetbrains.mps.smodel.SModelId;
import jetbrains.mps.smodel.SModelReference;
import jetbrains.mps.smodel.SNode;
import jetbrains.mps.smodel.SNodeId;
import jetbrains.mps.smodel.adapter.ids.*;
import jetbrains.mps.smodel.adapter.structure.MetaAdapterFactory;
import jetbrains.mps.smodel.persistence.def.v9.ModelWriter9;
import jetbrains.mps.smodel.runtime.ConceptKind;
import jetbrains.mps.smodel.runtime.StaticScope;
import org.jdom.Document;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

/** Fixture export through the installed MPS writer, without editing serialized MPS models. */
public final class MpsFixtureGenerator {
  public static void main(String[] args)throws Exception {
    jetbrains.mps.core.platform.Platform platform=jetbrains.mps.core.platform.PlatformFactory.initPlatform(jetbrains.mps.core.platform.PlatformOptionsBuilder.PERSISTENCE);
    try {
    Path output=Path.of(args[0]);Files.createDirectories(output);
    DefaultSModel model=new DefaultSModel(new SModelReference(null,SModelId.regular("11111111-2222-3333-4444-555555555555"),"example.browser.fixture"));
    model.getSModelHeader().setPersistenceVersion(9);
    model.getSModelHeader().setOptionalProperty("test","metadata <>&\"\n\t🙂");
    SLanguageId language=new SLanguageId(1,2);SConceptId concept=new SConceptId(language,3);SPropertyId property=new SPropertyId(concept,4);
    MetaModelInfoProvider.StuffedMetaModelInfo info=new MetaModelInfoProvider.StuffedMetaModelInfo(new MetaModelInfoProvider.BaseMetaModelInfo());
    info.setLanguageName(language,"example");info.setConceptName(concept,"example.Root");info.setPropertyName(property,"value");
    info.setKind(concept,ConceptKind.NORMAL);info.setScope(concept,StaticScope.GLOBAL);
    SNode node=new SNode(MetaAdapterFactory.getConcept(concept,"example.Root"),new SNodeId.Regular(1234));
    node.setProperty(MetaAdapterFactory.getProperty(property,"value"),"spaces  <>&\"\n\r\t🙂");
    model.addRootNode(node);
    ModelWriter9 writer=new ModelWriter9(info,false);
    XMLOutputter xml=new XMLOutputter(Format.getPrettyFormat().setLineSeparator("\n"));
    Files.writeString(output.resolve("single.xml"),xml.outputString(writer.saveModel(model)),StandardCharsets.UTF_8);
    int index=0;
    for(Map.Entry<String,Document> stream:writer.saveModelAsMultiStream(model).entrySet()) {
      Files.writeString(output.resolve(stream.getKey().equals(".model")?"stream-1.xml":"stream-0.xml"),xml.outputString(stream.getValue()),StandardCharsets.UTF_8);
      index++;
      System.out.println(stream.getKey());
    }
    System.out.println("Exported model with one property-bearing root and "+index+" streams");
    } finally { platform.dispose(); }
  }
}
