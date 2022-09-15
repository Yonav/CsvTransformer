package configurations;

import model.csv.operations.ColumnTransformationBiFunc;
import model.csv.operations.ColumnTransformationFunc;
import model.csv.operations.Operation;
import model.csv.operations.RenameHeader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** This class holds the operations to transform csv from the ING format to my MME Format. */
public class IngConfiguration extends BasicOperations {

  private boolean manualInput;

  public IngConfiguration(boolean manualInput) {
    super(';');
    this.manualInput = manualInput;
  }

  public List<Operation> createOperations() {
    ArrayList<Operation> operations = new ArrayList<>();

    // pre cleaning
    operations.add(deleteContentFromColumn("Saldo"));
    operations.add(combineColumns("Verwendungszweck", "Auftraggeber/Empfanger"));
    operations.add(removeDotAndChangeCommaToDot("Auftraggeber/Empfanger"));
    operations.add(removeDotAndChangeCommaToDot("Betrag"));

    operations.add(renameHeader());
    operations.add(reorderHeaderCompatibleWithMME());
    operations.add(deleteContentFromColumn("Kategorie"));
    operations.add(deleteContentFromColumn("Unterkategorie"));
    operations.add(deriveRecipientBasedOnDescription());
    operations.add(deriveCategoryBasedOnDescription());
    operations.add(parseSubcategoryEncodedInCategoryToSubcategoryColumn());
    operations.add(cleanseCategoryColumnFromSubcategoryContent());

    if (manualInput) {
      // System.out.println("--------------------------------------------");
      // System.out.println("Change "Empfänger" based on "Beschreibung"");
      // System.out.println("--------------------------------------------");
      operations.add(manualInputOfEmptyContentBasedOnCurrentContent("Beschreibung", "Empfänger"));
      // System.out.println("--------------------------------------------");
      // System.out.println("Change "Beschreibung" based on a mapping");
      // System.out.println("Enter -> All before _ ; 1 no changes");
      // System.out.println("--------------------------------------------");
      operations.add(
          takeSuggestedContentOrInputManually("Beschreibung", descriptionMappedToDescription()));
    }
    return operations;
  }

  public Operation renameHeader() {
    Map<String, String> translator = new HashMap<>();

    translator.put("Buchung", "Datum");
    translator.put("Betrag", "Wert");
    translator.put("Auftraggeber/Empfanger", "Beschreibung");
    translator.put("Valuta", "Kategorie");
    translator.put("Buchungstext", "Unterkategorie");
    translator.put("Saldo", "Empfänger");

    return new RenameHeader(translator);
  }

  public Operation combineColumns(String helperColumn, String transformerColumn) {
    return new ColumnTransformationBiFunc(
        helperColumn, transformerColumn, (helper, transformer) -> transformer + "_" + helper);
  }

  public Operation removeDotAndChangeCommaToDot(String columnName) {
    return new ColumnTransformationFunc(
        columnName, transformer -> transformer.replace(".", "").replace(",", "."));
  }
}
