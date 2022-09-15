package model.csv.operations;

import lombok.Data;
import lombok.NonNull;
import model.csv.Row;
import model.csv.Table;

import java.util.List;

@Data
public class DeleteMultipleColumn implements Operation {

  @NonNull List<String> columns;

  @Override
  public void apply(Table table) {
    Row copiedRow = table.getHeader().deepCopy();
    for (String columnName : columns) {
      copiedRow.remove(columnName);
    }
    ColumnReorder columnReorder = new ColumnReorder(copiedRow.getCells());
    columnReorder.apply(table);
  }
}
