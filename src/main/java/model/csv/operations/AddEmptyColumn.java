package model.csv.operations;

import lombok.Data;
import lombok.NonNull;
import model.csv.Row;
import model.csv.Table;

@Data
public class AddEmptyColumn implements Operation {

  @NonNull String columnName;

  @Override
  public void apply(Table table) {
    table.getHeader().addCell(columnName);
    for (Row row : table.getContent()) {
      row.addCell("");
    }
  }
}
