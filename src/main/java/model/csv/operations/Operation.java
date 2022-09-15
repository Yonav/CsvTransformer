package model.csv.operations;

import model.csv.Table;

public interface Operation {

  void apply(Table table);
}
