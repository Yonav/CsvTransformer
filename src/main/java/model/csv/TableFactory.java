package model.csv;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class TableFactory {

  /**
   * Creates a {@link Table csvTable} from the content of a file that is specified via the
   * inputFilePath. The content inside the file must be separated via the specified separator.
   *
   * @param file file to read.
   * @param separator character that separates the fields
   * @return the table
   * @throws IOException
   */
  public static Table buildFromFile(File file, char separator) throws IOException {

    FileInputStream inputStream = new FileInputStream(file);
    InputStreamReader inputReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
    Scanner scanner = new Scanner(inputReader);

    Table table = new Table();

    boolean headerScanned = false;
    while (scanner.hasNextLine()) {
      String line = scanner.nextLine();
      Row row = createRow(line, separator);

      if (!headerScanned) {
        table.addHeader(row);
        headerScanned = true;
      } else {
        table.addContent(row);
      }
    }

    return table;
  }

  /**
   * Creates a row from a String containing a row. The fields inside the String are separated by the
   * specified separator.
   *
   * <p>Removes the first character of the row, if it is a "filler" character <code>UFEFF</code>.
   * Accounts for the usage of quotation marks, meaning that a separator is ignored between two
   * marks.
   *
   * @param rawRow String containing the row as String
   * @param separator character that separates the fields
   * @return the row
   */
  public static Row createRow(String rawRow, char separator) {
    Row resultRow = new Row();
    resultRow.setSeparator(separator);

    // clean Row
    if (rawRow.startsWith("\uFEFF")) {
      rawRow = rawRow.substring(1);
    }

    String[] splitRow = rawRow.split(String.valueOf(separator));

    boolean evenNumberOfQuotationMarks = true;
    StringBuilder restFromLastIteration = new StringBuilder();
    for (String rawCell : splitRow) {
      //
      if (rawCell.contains("\"")) {
        for (int i = 0; i < rawCell.length(); i++) {
          if (rawCell.charAt(i) == '"') {
            evenNumberOfQuotationMarks = !evenNumberOfQuotationMarks;
          }
        }
      }

      if (restFromLastIteration.length() == 0) {
        // there is no rest to add
        resultRow.addCell(rawCell);
      } else {
        // there is rest to add
        restFromLastIteration.append(rawCell);
        if (evenNumberOfQuotationMarks) {
          // since even number of quotation marks, we can add the content to a new cell
          resultRow.addCell(restFromLastIteration.toString());
          restFromLastIteration = new StringBuilder();
        }
      }
    }

    // collect the rest at the end if there is any
    // only happens, if there is an odd number of quotation marks at the end
    if (restFromLastIteration.length() != 0) {
      resultRow.addCell(restFromLastIteration.toString());
    }

    return resultRow;
  }

  public static void writeToFile(Table table, File file) {
    String tableAsString = table.toString();

    try {
      FileWriter out = new FileWriter(file);
      BufferedWriter writer = new BufferedWriter(out);
      writer.write(tableAsString);

      writer.close();
    } catch (Exception e) {

      e.printStackTrace();
    }
  }
}
