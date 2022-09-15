package configurations;

import main.FileUtilities;
import model.csv.operations.ColumnTransformationBiFunc;
import model.csv.operations.ColumnTransformationFunc;
import model.csv.operations.Operation;
import model.csv.operations.RenameHeader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

/** This class holds the operations to transform csv from the notion format to my MME Format. */
public class NotionConfiguration extends BasicOperations {

  public enum Compatiblity {
    N_2019,
    N_2020,
    N_2021
  };

  private Compatiblity compatiblity;

  private boolean manualInput = false;

  public NotionConfiguration(Compatiblity compatiblity) {
    super(',');
    this.compatiblity = compatiblity;
  }

  public List<Operation> createOperations() {
    ArrayList<Operation> operations = new ArrayList<>();

    operations.add(renameHeader(compatiblity));
    operations.add(reorderHeaderCompatibleWithMME());
    operations.add(removeNotionUrl());
    operations.add(changeValueToNegativeWhenIncomeCategorie());
    operations.add(mapNotionToMMECategoriesWithSubCategories());
    operations.add(deleteContentFromColumn("Empfänger"));
    operations.add(deriveRecipientBasedOnDescription());
    operations.add(deriveCategoryBasedOnDescription());
    operations.add(parseSubcategoryEncodedInCategoryToSubcategoryColumn());
    operations.add(cleanseCategoryColumnFromSubcategoryContent());

    if (manualInput) {
      operations.add(manualInputOfEmptyContentBasedOnCurrentContent("Beschreibung", "Empfänger"));
    }
    return operations;
  }

  public Operation renameHeader(Compatiblity compatiblity) {
    Map<String, String> translator = new HashMap<>();

    translator.put("€ corrected", "Wert");
    translator.put("Date", "Datum");
    translator.put("Kat_Ober", "Unterkategorie");
    translator.put("Property", "Empfänger");

    if (compatiblity == Compatiblity.N_2019) {
      translator.put("Ausgaben", "Kategorie");
      translator.put("Comment", "Beschreibung");
      translator.put("Kategorie", "DeleteMe");
    } else {
      translator.put("Ausgaben", "Beschreibung");
      translator.put("Comment", "DeleteMe");
      translator.put("Kategorie", "Kategorie");
    }

    return new RenameHeader(translator);
  }

  public Operation removeNotionUrl() {
    Function<String, String> remappingFunc =
        (transformer) -> {
          if (transformer.isEmpty()) {
            return "";
          } else if (!transformer.contains("https://")) {
            return transformer;
          } else {
            String[] firstSplit = transformer.split("/");
            String[] secondSplit = firstSplit[firstSplit.length - 1].split("-");
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < secondSplit.length - 1; i++) {
              sb.append(secondSplit[i]);
            }

            return sb.toString();
          }
        };
    return new ColumnTransformationFunc("Kategorie", remappingFunc);
  }

  public Operation changeValueToNegativeWhenIncomeCategorie() {
    BiFunction<String, String, String> remapFunc =
        (helper, transformer) -> {
          if (helper.equals("Job")
              || helper.equals("Einkommen")
              || helper.equals("SonstigesEinkommen")
              || helper.equals("Mama")
              || helper.equals("Papa")
              || helper.equals("Stipendium")) {
            if (!transformer.startsWith("-")) {
              transformer = "-" + transformer;
            }
          }
          return transformer;
        };

    return new ColumnTransformationBiFunc("Kategorie", "Wert", remapFunc);
  }

  public Operation mapNotionToMMECategoriesWithSubCategories() {
    return translateIfPresentInMap("Kategorie", notionToMMECategoryTranslator());
  }

  public static Map<String, String> notionToMMECategoryTranslator() {
    // Categories starting with _Mod_ require a manual correction

    return FileUtilities.readCustomMaps("notionToMMWCategoryTranslator");
  }
}
