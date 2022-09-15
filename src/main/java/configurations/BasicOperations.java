package configurations;

import main.FileUtilities;
import model.csv.Table;
import model.csv.TableFactory;
import model.csv.operations.ColumnReorder;
import model.csv.operations.ColumnTransformationBiFunc;
import model.csv.operations.ColumnTransformationFunc;
import model.csv.operations.Operation;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

/** This class contains the basic operations a Configuration should have. */
public abstract class BasicOperations {

  char separator;

  abstract List<Operation> createOperations();

  public BasicOperations(char separator) {
    this.separator = separator;
  }

  public void readTransformWrite(String fileInputName, String fileOutputName) throws IOException {
    File inputFile = FileUtilities.createOrReadFileFromResources(fileInputName);
    Table table = TableFactory.buildFromFile(inputFile, separator);
    List<Operation> operations = createOperations();
    for (Operation operation : operations) {
      operation.apply(table);
      System.out.println("Operation Finished: " + operation.getClass().getSimpleName());
    }
    File outputFile = FileUtilities.createOrReadFile(fileOutputName);
    TableFactory.writeToFile(table, outputFile);
  }

  Operation cleanseCategoryColumnFromSubcategoryContent() {
    return new ColumnTransformationFunc("Kategorie", (cat) -> cat.split(":")[0]);
  }

  Operation parseSubcategoryEncodedInCategoryToSubcategoryColumn() {
    BiFunction<String, String, String> remapFunc =
        (cat, subCat) -> {
          if (cat.contains(":")) {
            return cat.split(":")[1];
          }
          return "";
        };

    return new ColumnTransformationBiFunc("Kategorie", "Unterkategorie", remapFunc);
  }

  Operation deleteContentFromColumn(String columnName) {
    return new ColumnTransformationFunc(columnName, (transform) -> "");
  }

  /**
   * This function prompts the user to write new input for the transformed column if the content of
   * the cell is empty. The helper column displays its content to give additional information to the
   * user.
   */
  Operation manualInputOfEmptyContentBasedOnCurrentContent(
      String helperColumn, String transformedColumn) {
    BiFunction<String, String, String> remapFunc =
        (helper, transformer) -> {
          if (transformer.isEmpty()) {
            System.out.println("Helper-Column data: \"" + helper + "\"");

            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("New Input: ");
            try {
              return br.readLine();
            } catch (IOException e) {
              e.printStackTrace();
            }
          }
          return transformer;
        };
    return new ColumnTransformationBiFunc(helperColumn, transformedColumn, remapFunc);
  }

  /**
   * This function tries to generate the data of the transformed column via the specified map and
   * gives the user the option to pick the generated data or input something manually.
   *
   * <p>Input possibilities:
   *
   * <p>No input (just press enter) -> Take suggested from map
   *
   * <p>"1" as input -> concat (possible) generated and prior data
   *
   * <p>Other input -> Takes new input as new data.
   */
  Operation takeSuggestedContentOrInputManually(String transformedColumn, Map<String, String> map) {
    Function<String, String> remapFunc =
        (transformer) -> {
          // concat automatic Description
          String description = transformer;
          for (Map.Entry<String, String> entry : map.entrySet()) {
            if (transformer.toLowerCase().contains(entry.getKey().toLowerCase())) {
              description = entry.getValue() + "%_%" + description;
              break;
            }
          }

          // Get manual input
          System.out.println("Descr: \"" + description + "\"");

          BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
          System.out.print("Neue Eingabe: ");
          try {
            String newInput = br.readLine();
            if (newInput.equals("")) {
              description = description.split("%_%")[0];
            } else if (newInput.equals("1")) {
              return description;
            } else {
              description = newInput;
            }
          } catch (IOException e) {
            e.printStackTrace();
          }

          return description;
        };
    return new ColumnTransformationFunc(transformedColumn, remapFunc);
  }

  static Operation translateIfPresentInMap(String columnName, Map<String, String> translator) {
    return new ColumnTransformationFunc(
        columnName, (transform) -> translator.getOrDefault(transform, transform));
  }

  public static Operation reorderHeaderCompatibleWithMME() {
    ArrayList<String> order = new ArrayList<>();

    order.add("Beschreibung");
    order.add("Kategorie");
    order.add("Unterkategorie");
    order.add("Datum");
    order.add("Wert");
    order.add("Empfänger");

    return new ColumnReorder(order);
  }

  public static Operation deriveColumnBasedOnDescription(
      String transformerColumn, Map<String, String> deriveMap) {
    BiFunction<String, String, String> remapFunc =
        (helper, transformer) -> {
          for (Map.Entry<String, String> entry : deriveMap.entrySet()) {
            if (helper.toLowerCase().contains(entry.getKey().toLowerCase())) {
              return entry.getValue();
            }
          }
          return transformer;
        };

    return new ColumnTransformationBiFunc("Beschreibung", transformerColumn, remapFunc);
  }

  public Operation deriveCategoryBasedOnDescription() {
    return deriveColumnBasedOnDescription("Kategorie", descriptionMappedToCategory());
  }

  public Operation deriveRecipientBasedOnDescription() {
    return deriveColumnBasedOnDescription("Empfänger", descriptionMappedToRecipient());
  }

  public static Map<String, String> descriptionMappedToCategory() {
    return FileUtilities.readCustomMaps("descriptionMappedToCategory");
  }

  public static Map<String, String> descriptionMappedToRecipient() {
    return FileUtilities.readCustomMaps("descriptionMappedToRecipient");
  }

  public static Map<String, String> descriptionMappedToDescription() {
    return FileUtilities.readCustomMaps("descriptionMappedToDescription");
  }
}
