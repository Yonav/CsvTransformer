package model.csv.operations;

import model.csv.Table;
import model.csv.Row;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

class ColumnReorderTest {

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
        // arrange & act
        List<String> reorder = List.of("second", "third");
        new ColumnReorder(reorder).apply(table);

        // assert
        assertThat(table.getHeader().getCells()).hasSize(1);
        assertThat(table.getHeader().getCells().get(0)).isEqualTo("second");
        assertThat(table.getContent()).allMatch(row -> row.getCells().size() == 1);
        assertThat(table.getContent()).allMatch(row -> row.getCells().get(0).equals("second_1"));
    }
}