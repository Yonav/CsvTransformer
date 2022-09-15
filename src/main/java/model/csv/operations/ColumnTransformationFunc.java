package model.csv.operations;

import lombok.Data;
import lombok.NonNull;
import model.csv.Row;
import model.csv.Table;

import java.util.function.Function;

@Data
public class ColumnTransformationFunc implements Operation {

  @NonNull private String transformationColumn;
  @NonNull private Function<String, String> mappingFunction;

  @Override
  public void apply(Table table) {
    int indexTransformer = table.getHeader().indexOf(transformationColumn);

    if (indexTransformer == -1) {
      // warn or ex
      return;
    }

    for (Row row : table.getContent()) {
      row.remapSingleCell(indexTransformer, mappingFunction);
    }
  }
}
