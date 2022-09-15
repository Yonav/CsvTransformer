package model.csv.operations;

import model.csv.Table;
import model.csv.Row;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class AddEmptyColumnTest {

    Table table;

    @BeforeEach
    public void initialize() {
        table = new Table();

        Row header = new Row();
        header.setSeparator(',');
        header.addCell("first");
        table.addHeader(header);

        Row row_1 = new Row();
        row_1.setSeparator(',');
        row_1.addCell("first_1");
        table.addContent(row_1);
    }

    @Test
    public void test() {
        // arrange & act
        String columnName = "second";
        new AddEmptyColumn(columnName).apply(table);

        // assert
        assertThat(table.getHeader().getCells()).hasSize(2);
        assertThat(table.getHeader().getCells().get(1)).isEqualTo(columnName);
        assertThat(table.getContent()).allMatch(row -> row.getCells().size() == 2);
        assertThat(table.getContent()).allMatch(row -> row.getCells().get(1).isEmpty());
    }
}