import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import jetbrains.mps.persistence.MetaModelInfoProvider;
import jetbrains.mps.smodel.SModelHeader;
import jetbrains.mps.smodel.SModelId;
import jetbrains.mps.smodel.SModelReference;
import jetbrains.mps.smodel.loading.ModelLoadingState;
import jetbrains.mps.smodel.persistence.def.v9.ModelPersistence9;
import org.jdom.internal.xml.PortableXmlReader;
import org.xml.sax.InputSource;

/** Reloads browser-saved XML with the actual installed MPS XML9 model handler. JVM test only. */
public final class MpsFixtureReload {
  public static void main(String[] args)throws Exception {
    var platform=jetbrains.mps.core.platform.PlatformFactory.initPlatform(jetbrains.mps.core.platform.PlatformOptionsBuilder.PERSISTENCE);
    int checked=0;
    try(var files=Files.walk(Path.of(args[0]))) {
      for(Path file:files.filter(p->p.toString().endsWith(".xml")).toList()) {
        SModelHeader header=new SModelHeader();
        header.setModelReference(new SModelReference(null,SModelId.regular("11111111-2222-3333-4444-555555555555"),"example.browser.fixture"));
        header.setMetaInfoProvider(new MetaModelInfoProvider.StuffedMetaModelInfo(new MetaModelInfoProvider.BaseMetaModelInfo()));
        var handler=new ModelPersistence9().getModelReaderHandler(ModelLoadingState.FULLY_LOADED,header);
        PortableXmlReader reader=new PortableXmlReader();reader.setContentHandler(handler);
        try(Reader input=Files.newBufferedReader(file,StandardCharsets.UTF_8)){reader.parse(new InputSource(input));}
        var model=handler.getResult().getModel();
        if(!model.getReference().toString().equals("r:11111111-2222-3333-4444-555555555555(example.browser.fixture)"))throw new AssertionError("Model identity: "+file);
        int roots=0;
        for(var node:model.getRootNodes()) {
          roots++;
          if(!node.getNodeId().toString().equals("1234"))throw new AssertionError("Node identity: "+file);
          if(!node.getConcept().getQualifiedName().equals("example.Root"))throw new AssertionError("Concept identity: "+file);
          boolean found=false;
          for(var property:node.getProperties())if(property.getName().equals("value")) {
            found=true;
            if(!"browser edit <>&\"\n\r\t🙂".equals(node.getProperty(property)))throw new AssertionError("Property data: "+file);
          }
          if(!found)throw new AssertionError("Missing property: "+file);
        }
        boolean isHeader=file.getFileName().toString().equals("stream-1.xml");
        if(roots!=(isHeader?0:1))throw new AssertionError("Root count: "+file);
        if(!file.getFileName().toString().equals("stream-0.xml") && !"metadata <>&\"\n\t🙂".equals(header.getOptionalProperty("test")))throw new AssertionError("Metadata: "+file);
        checked++;
      }
    } finally {platform.dispose();}
    if(checked!=18)throw new AssertionError("Expected 18 browser-produced files, got "+checked);
    System.out.println("Actual MPS XML9 reader reloaded "+checked+" browser-produced single/header/root documents; identity, properties and metadata preserved.");
  }
}
