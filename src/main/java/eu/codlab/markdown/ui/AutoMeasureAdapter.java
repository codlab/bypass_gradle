package eu.codlab.markdown.ui;

import android.content.Context;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by kevinleperf on 04/03/15.
 */
public class AutoMeasureAdapter extends BaseAdapter {
    private Context _context;
    private List<String> _content;
    private int _text_color;

    private int _number_column;
    private boolean _use_header;
    private int _body_color;
    private int _header_color;
    private int _cell_padding;

    private AutoMeasureAdapter() {

    }

    public AutoMeasureAdapter(Context context, int text_color, List<String> content,
                              int number_column,
                              boolean use_header,
                              int header_color, int body_color,
                              int cell_padding) {
        _context = context;
        _text_color = text_color;
        _content = content;

        _number_column = number_column;
        _use_header = use_header;
        _header_color = header_color;
        _body_color = body_color;
        _cell_padding = cell_padding;
    }

    @Override
    public int getCount() {
        return _content.size();
    }

    private boolean isHeader(int position) {
        return _use_header && position < _number_column;
    }

    @Override
    public String getItem(int position) {
        return _content.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {

            TextView textview = new TextView(_context);
            textview.setLayoutParams(new AutoGridView.LayoutParams(AutoGridView.LayoutParams.MATCH_PARENT,
                    AutoGridView.LayoutParams.WRAP_CONTENT));
            if (!_use_header) {
                convertView.setBackgroundColor(_body_color);
            }
            textview.setPadding(_cell_padding, _cell_padding, _cell_padding, _cell_padding);
            textview.setClickable(true);
            textview.setLinksClickable(true);
            textview.setMovementMethod(LinkMovementMethod.getInstance());
            textview.setTextColor(_text_color);

            convertView = textview;
        }

        if (_use_header) {
            convertView.setBackgroundColor(isHeader(position) ? _header_color : _body_color);
        }
        ((TextView) convertView).setText(getItem(position));

        return convertView;
    }
}
