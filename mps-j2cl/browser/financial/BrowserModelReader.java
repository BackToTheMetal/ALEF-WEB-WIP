package jetbrains.mps.smodel.persistence.def.v9;

import java.io.ByteArrayInputStream;
import jetbrains.mps.persistence.MetaModelInfoProvider;
import jetbrains.mps.smodel.ModelLoadResult;
import jetbrains.mps.smodel.SModel;
import jetbrains.mps.smodel.SModelHeader;
import jetbrains.mps.smodel.loading.ModelLoadingState;
import org.jdom.mps.MpsXml;
import org.xml.sax.InputSource;

/** In-memory entry point into the unchanged generated MPS XML9 reader. */
public final class BrowserModelReader {
  public static ModelLoadResult<SModel> read(SModel data, byte[] content) {
    SModelHeader header = new SModelHeader();
    header.setModelReference(data.getReference());
    ModelReader9Handler reader = new ModelReader9Handler(header, data,
        new IdInfoReadHelper(new MetaModelInfoProvider.RegularMetaModelInfo(), false, false));
    try {
      MpsXml.parse(new InputSource(new ByteArrayInputStream(content)), reader);
      jetbrains.mps.smodel.loading.ModelLoadResult result = reader.getResult();
      if (result == null || result.getState() != ModelLoadingState.FULLY_LOADED) {
        throw new IllegalStateException("The embedded XML9 model did not load completely");
      }
      return new ModelLoadResult<>(result.getModel(), result.getState());
    } catch (Exception failure) {
      throw new IllegalStateException("Cannot load the bundled primitive-type accessory model", failure);
    }
  }
}
