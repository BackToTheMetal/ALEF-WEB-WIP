package regelspraak.editor;

/* Browser copy: generated from the original editor models by tools/editor_metadata.py. */
import org.jetbrains.mps.openapi.language.SAbstractConcept;

public class PuntConcept {
  public static boolean is(SAbstractConcept concept) {
    switch (concept.getQualifiedName()) {
      case "regelspraak.structure.Aggregatie":
      case "regelspraak.structure.ElfproefCheck":
      case "regelspraak.structure.IsGevuld":
      case "regelspraak.structure.IsLeeg":
      case "regelspraak.structure.IsNumeriekMetLengte":
      case "regelspraak.structure.Uniciteit":
      case "regelspraak.structure.Vergelijking":
        return true;
      default:
        return false;
    }
  }
}
