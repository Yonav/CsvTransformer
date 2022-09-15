package model.csv.operations;

import lombok.Data;
import lombok.NonNull;
import model.csv.Row;
import model.csv.Table;

import java.util.ArrayList;
import java.util.List;

@Data
public class ColumnReorder implements Operation {

  @NonNull List<String> columnOrder;

  @Override
  public void apply(Table table) {
    List<Integer> indicesOfHeads = new ArrayList<>();
    Row header = table.getHeader();
    for (String head : columnOrder) {
      int index = table.getHeader().indexOf(head);
      if (index != -1) {
        indicesOfHeads.add(index);
      }
    }

    header.reorderRow(indicesOfHeads);
    for (Row row : table.getContent()) {
      row.reorderRow(indicesOfHeads);
    }
  }
}
