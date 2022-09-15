package model.csv.operations;

import model.csv.Row;
import model.csv.Table;

public class ValidateTable implements Operation {
  @Override
  public void apply(Table table) {
    int headerSize = table.getHeader().size();
    char headerSeparator = table.getHeader().getSeparator();

    for (Row row : table.getContent()) {
      if (row.size() != headerSize || row.getSeparator() != headerSeparator) {
        throw new RuntimeException("Non valid csv table");
      }
    }
  }
}
