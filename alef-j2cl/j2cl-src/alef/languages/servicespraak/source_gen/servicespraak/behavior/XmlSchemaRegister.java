package servicespraak.behavior;
import org.jetbrains.mps.openapi.model.SNode;
import org.jetbrains.mps.openapi.module.SRepository;
import jetbrains.mps.openapi.editor.EditorContext;
import jetbrains.mps.baseLanguage.closures.runtime._FunctionTypes;
/** XSD generation requires the desktop MPS generator. */
public final class XmlSchemaRegister {
  public XmlSchemaRegister(EditorContext context) {}
  public XmlSchemaRegister(SRepository repository) {}
  private UnsupportedOperationException unavailable() {
    return new UnsupportedOperationException("XSD generation and registration require desktop MPS");
  }
  public void getSchemaAndThen(SNode service, _FunctionTypes._void_P1_E0<? super SNode> next) { throw unavailable(); }
  public boolean checkSchema(SNode service, SNode expected) { throw unavailable(); }
  public void registerSchema(SNode xsd, SNode service) { throw unavailable(); }
  public void signaleerWijzigingXsd(SNode oldSchema, SNode newSchema) { throw unavailable(); }
  public void openXsdInEditor(SNode xsd) { throw unavailable(); }
}
