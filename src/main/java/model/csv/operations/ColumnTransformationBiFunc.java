package model.csv.operations;

import lombok.Data;
import lombok.NonNull;
import model.csv.Row;
import model.csv.Table;

import java.util.function.BiFunction;

@Data
public class ColumnTransformationBiFunc implements Operation {

  @NonNull private String helperColumn;
  @NonNull private String transformationColumn;
  @NonNull private BiFunction<String, String, String> mappingFunction;

  @Override
  public void apply(Table table) {
    int indexHelper = table.getHeader().indexOf(helperColumn);
    int indexTransformer = table.getHeader().indexOf(transformationColumn);

    if (indexHelper == -1 || indexTransformer == -1) {
      // warn or ex
      return;
    }

    for (Row row : table.getContent()) {
      row.remapSingleCell(indexHelper, indexTransformer, mappingFunction);
    }
  }
}
