package eu.codlab.markdown.entities;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import eu.codlab.markdown.raw.ArrayItem;
import eu.codlab.markdown.raw.array.Row;
import eu.codlab.markdown.ui.AutoMeasureAdapter;

/**
 * Created by kevinleperf on 04/03/15.
 */
public class ArrayEntity extends MarkDownEntity {
    private ArrayItem _array_item;

    private ArrayEntity() {

    }

    public ArrayEntity(ArrayItem array_item) {
        _array_item = array_item;
    }

    public boolean hasHeader() {
        return _array_item.getHeader() != null && _array_item.getHeader().getRow().size() > 0;
    }

    public Row getHeader() {
        return _array_item.getHeader();
    }

    public List<Row> getRows() {
        return _array_item.getContent();
    }

    public int getNumberColumns() {
        return _array_item.getNumberColumns();
    }

    public int getNumberRows() {
        return _array_item.getNumberRows();
    }

    private boolean addRowToContentForAdapter(List<String> content, Row row) {
        if (row == null) {
            return false;
        }

        int i = 0;
        List<String> columns = row.toColumns();
        //add every column in this row
        while (i < columns.size()) {
            Log.d("ArrayEntity", "adding entity row/column : " + columns.get(i));
            content.add(columns.get(i));
            i++;
        }
        //add empty string when not enough (could be in case we had only first few columns
        //populated in the MD file
        while (i < getNumberColumns()) {
            content.add("");
            i++;
        }
        return true;
    }

    public AutoMeasureAdapter createAdapter(final Context context, final int color, int header_color, int body_color,
                                            int cell_padding) {
        final List<String> content = new ArrayList<>();
        List<Row> rows = getRows();
        boolean has_header = hasHeader();
        if (has_header) {
            Row row = getHeader();
            has_header = addRowToContentForAdapter(content, row);
        }

        for (Row row : rows) {
            addRowToContentForAdapter(content, row);
        }

        return new AutoMeasureAdapter(context, color, content, getNumberColumns(),
                has_header, header_color, body_color, cell_padding);
    }
}
