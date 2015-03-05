package eu.codlab.markdown.raw;

import java.util.ArrayList;
import java.util.List;

import eu.codlab.markdown.raw.array.Row;

/**
 * Created by kevinleperf on 04/03/15.
 */
public class ArrayItem extends RawItem<List<Row>> {
    public static char HEADER_CONTENT = '-';

    public static enum State {
        HEADER,
        BODY,
        CLOSE
    }

    private int _number_column;
    protected List<Integer> _header_column_descriptor = new ArrayList<>();

    private State _current_state;
    private Row _header;

    private Row _current_row;

    public void setHeader(String header) {
        int i = 0;
        while (i < header.length()) {
            if (header.charAt(i) == HEADER_CONTENT) {
                int j = i;

                //first, get all the '-'
                while (j < header.length() && header.charAt(j) == HEADER_CONTENT) {
                    j++;
                }
                //then all te ' ' we can find until the next '-' > next column
                while (j < header.length() && header.charAt(j) != HEADER_CONTENT) {
                    j++;
                }
                _header_column_descriptor.add(j - i);
                //start next round
                i = j;
            } else {
                i++;
            }
        }
        _number_column = _header_column_descriptor.size();

        _current_row = new Row(_header_column_descriptor);
    }

    public void flushRow() {
        if (_current_row.getRow().size() == 0) {
            _current_state = State.CLOSE;
        }

        if (_current_row != null) {
            _content.add(_current_row);
            _current_row = new Row(_header_column_descriptor);
        }
    }

    public void setIsHeaderForThisRow() {
        _header = _current_row;
        _current_row = new Row(_header_column_descriptor);
        _current_state = State.BODY;
    }


    public ArrayItem() {
        super();
        _current_row = new Row(null);
        _current_state = State.HEADER;
        _content = new ArrayList<>();
    }

    @Override
    public void appendString(String string) {
        _current_row.append(string);
    }

    public Row getHeader() {
        return _header;
    }

    public boolean isInHeader() {
        return State.HEADER.equals(_current_state);
    }

    public boolean isInBody() {
        return State.HEADER.equals(_current_state);
    }

    public boolean isFinish() {
        return State.CLOSE.equals(_current_state);
    }

    public int getNumberColumns() {
        return _number_column;
    }

    public int getNumberRows() {
        return _content.size() + (_header != null ? 1 : 0);
    }

}
