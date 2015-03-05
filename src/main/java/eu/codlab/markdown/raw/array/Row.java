package eu.codlab.markdown.raw.array;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kevinleperf on 04/03/15.
 */
public class Row {
    private List<String> _calculated_row = null;
    private List<Integer> _column_descriptor;

    private List<String> _lines;

    public Row(List<Integer> column_descriptor) {
        _column_descriptor = column_descriptor;
        _lines = new ArrayList<>();
    }

    public void append(String line) {
        _lines.add(line);
    }

    public List<String> getRow() {
        return _lines;
    }

    public String toString() {
        List<String> columns = toColumns();
        String result = "lines : " + _lines.size()
                + (_lines.size() > 0 ? " first: " + _lines.get(0) : " no rows") + "\n calculated :\n";
        int index = 0;
        for (String column : columns) {
            result += "column " + index + " : " + column + "\n";
            index++;
        }
        return result;
    }


    private void appendAtIndex(int index, String string) {
        if (_calculated_row != null && string != null) {
            while (_calculated_row.size() <= index) {
                _calculated_row.add(new String());
            }

            String str = _calculated_row.get(index);
            if (str == null) str = new String();
            str = str.trim();
            string = string.trim();
            if (str.length() != 0 && string.length() > 0) {
                str += " ";
            }
            str += string;
            _calculated_row.set(index, str);
        }
    }

    public List<String> toColumns() {
        if (_calculated_row == null) {
            _calculated_row = new ArrayList<>();

            for (String line : _lines) {
                int index = 0;
                for (Integer size : _column_descriptor) {
                    if (line.length() > 0) {
                        int min = Math.min(size, line.length());
                        String current = line.substring(0, min).trim();
                        line = line.substring(min);
                        appendAtIndex(index, current);
                    }
                    index++;
                }
            }
        }
        return _calculated_row;
    }
}
