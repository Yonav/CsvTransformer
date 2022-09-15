package model.csv;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Table {

  private Row header;
  private List<Row> content = new ArrayList<>();

  public void addHeader(Row row) {
    header = row;
  }

  public void addContent(Row row) {
    content.add(row);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();

    sb.append(header.toString());
    for (Row row : content) {
      sb.append(row.toString());
    }
    return sb.toString();
  }
}
