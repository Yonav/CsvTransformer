package model.csv.operations;

import model.csv.Table;
import model.csv.Row;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class ValidateTableTest {

  Table table;

  @BeforeEach
  public void initialize() {
    table = new Table();

    Row header = new Row();
    header.setSeparator(',');
    header.addCell("first");
    header.addCell("second");
    table.addHeader(header);

    Row row_1 = new Row();
    row_1.setSeparator(',');
    row_1.addCell("first_1");
    row_1.addCell("second_1");
    table.addContent(row_1);
  }

  @Test
  public void test() {
    // arrange & act & assert
    assertThatNoException().isThrownBy(() -> new ValidateTable().apply(table));
  }

  @Test
  public void test_2() {
    // arrange & act & assert
    table.getHeader().addCell("test");
    assertThatExceptionOfType(RuntimeException.class).isThrownBy(() -> new ValidateTable().apply(table));
  }

  @Test
  public void test_3() {
    // arrange & act & assert
    table.getHeader().setSeparator(';');
    assertThatExceptionOfType(RuntimeException.class).isThrownBy(() -> new ValidateTable().apply(table));
  }

  @Test
  public void test_4() {
    // arrange & act & assert
    table.getContent().get(0).setSeparator(';');
    assertThatExceptionOfType(RuntimeException.class).isThrownBy(() -> new ValidateTable().apply(table));
  }

  @Test
  public void test_5() {
    // arrange & act & assert
    table.getContent().get(0).addCell("test");
    assertThatExceptionOfType(RuntimeException.class).isThrownBy(() -> new ValidateTable().apply(table));
  }
}
