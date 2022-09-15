package model.csv.operations;

import lombok.Data;
import lombok.NonNull;
import model.csv.Table;

import java.util.Map;

@Data
public class RenameHeader implements Operation {

  @NonNull Map<String, String> translator;

  @Override
  public void apply(Table table) {
    table.getHeader().remapRow(cell -> translator.getOrDefault(cell, cell));
  }
}
