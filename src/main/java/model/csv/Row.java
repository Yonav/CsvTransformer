package model.csv;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

@Data
public class Row {

  private List<String> cells = new ArrayList<>();
  private char separator;

  public void addCell(String cell) {
    cells.add(cell);
  }

  public void remapRow(Function<String, String> transformator) {
    for (int i = 0; i < cells.size(); i++) {
      remapSingleCell(i, transformator);
    }
  }

  public void remapSingleCell(int transformationIndex, Function<String, String> transformator) {
    String newCell = transformator.apply(cells.get(transformationIndex));
    cells.set(transformationIndex, newCell);
  }

  public void remapSingleCell(
      int helperIndex,
      int transformationIndex,
      BiFunction<String, String, String> mappingFunction) {
    String newContent =
        mappingFunction.apply(cells.get(helperIndex), cells.get(transformationIndex));
    cells.set(transformationIndex, newContent);
  }

  public void reorderRow(List<Integer> indicesOfHeads) {
    List<String> reorderedCells = new ArrayList<>();

    for (Integer indicesOfHead : indicesOfHeads) {
      reorderedCells.add(cells.get(indicesOfHead));
    }

    cells = reorderedCells;
  }

  public int indexOf(String content) {
    return cells.indexOf(content);
  }

  public Row deepCopy() {
    return null;
  }

  public boolean remove(String content) {
    return cells.remove(content);
  }

  public int size() {
    return cells.size();
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    for (String cell : cells) {
      sb.append(cell);
      sb.append(separator);
    }
    sb.setCharAt(sb.length() - 1, '\n');
    return sb.toString();
  }
}
