package model.csv.operations;

import model.csv.Table;
import model.csv.Row;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ColumnTransformationBiFuncTest {

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
        new ColumnTransformationBiFunc("first", "second", (t, u) -> t + "_" + u).apply(table);

        // assert
        assertThat(table.getHeader().getCells()).hasSize(2);
        assertThat(table.getHeader().getCells().get(0)).isEqualTo("first");
        assertThat(table.getHeader().getCells().get(1)).isEqualTo("second");
        assertThat(table.getContent()).allMatch(row -> row.getCells().size() == 2);
        assertThat(table.getContent()).allMatch(row -> row.getCells().get(0).equals("first_1"));
        assertThat(table.getContent()).allMatch(row -> row.getCells().get(1).equals("first_1_second_1"));
    }
}