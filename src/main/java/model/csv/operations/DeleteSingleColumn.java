package model.csv.operations;

import lombok.Data;
import lombok.NonNull;
import model.csv.Row;
import model.csv.Table;

@Data
public class DeleteSingleColumn implements Operation {

  @NonNull String columnName;

  @Override
  public void apply(Table table) {
    Row copiedRow = table.getHeader().deepCopy();
    copiedRow.remove(columnName);
    ColumnReorder columnReorder = new ColumnReorder(copiedRow.getCells());
    columnReorder.apply(table);
  }
}
