package eu.codlab.markdown;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.net.Uri;
import android.text.method.LinkMovementMethod;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import eu.codlab.markdown.entities.ArrayEntity;
import eu.codlab.markdown.entities.ColorEntity;
import eu.codlab.markdown.entities.ImageEntity;
import eu.codlab.markdown.entities.MarkDownEntity;
import eu.codlab.markdown.entities.TextEntity;
import eu.codlab.markdown.enums.EventType;
import eu.codlab.markdown.listeners.IMarkdownEntityEvent;
import eu.codlab.markdown.ui.AutoGridView;
import eu.codlab.markdown.ui.AutoMeasureAdapter;

/**
 * Created by kevinleperf on 08/01/15.
 */
public class MarkdownView extends LinearLayout {
    private int _md_array_spacing;
    private int _md_array_header_color;
    private int _md_array_body_color;
    private int _md_text_color;
    private int _md_cell_padding;
    private int _md_padding_left;
    private int _md_padding_right;

    private int _last_color;
    private int _last_background_color;

    private Markdown _markdown_item;
    private List<MarkDownEntity> _entities;
    private final List<IMarkdownEntityEvent> _listeners = new ArrayList<>();
    private ViewGroup _layout;

    private void init(AttributeSet attributes) {
        _markdown_item = new Markdown(getContext());
        View main = ((LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.markdown_view, this, false);
        _layout = (ViewGroup) main.findViewById(R.id.markdown_area);
        addView(main);

        if (attributes != null) {
            TypedArray theAttrs = getContext().obtainStyledAttributes(attributes, R.styleable.MarkdownView);
            _md_array_body_color = theAttrs.getColor(R.styleable.MarkdownView_md_array_body_color, Color.WHITE);
            _md_array_header_color = theAttrs.getColor(R.styleable.MarkdownView_md_array_header_color, Color.LTGRAY);
            _md_text_color = theAttrs.getColor(R.styleable.MarkdownView_md_text_color, Color.BLACK);
            _md_array_spacing = (int) theAttrs.getDimension(R.styleable.MarkdownView_md_array_spacing, 1);
            _md_cell_padding = (int) theAttrs.getDimension(R.styleable.MarkdownView_md_cell_padding, 5);
            _md_padding_left = (int) theAttrs.getDimension(R.styleable.MarkdownView_md_padding_left, 0);
            _md_padding_right = (int) theAttrs.getDimension(R.styleable.MarkdownView_md_padding_right, 0);

            _last_color = _md_text_color;
            _last_background_color = Color.TRANSPARENT;
            theAttrs.recycle();
        } else {
            _md_array_body_color = Color.WHITE;
            _md_array_header_color = Color.LTGRAY;
            _md_text_color = Color.BLACK;
            _md_array_spacing = 1;
            _md_cell_padding = 5;
            _md_padding_left = _md_padding_right = 0;

            _last_color = Color.BLACK;
            _last_background_color = Color.TRANSPARENT;
        }
    }

    public MarkdownView(Context context) {
        super(context);
        init(null);
    }

    public MarkdownView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public MarkdownView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void clear() {
        //this.removeAllViews();
    }

    public void setStringContent(String text) {
        clear();
        List<MarkDownEntity> entities = _markdown_item.processText(text);
        setContent(entities);
    }

    public void setAssetContent(String asset) throws IOException {
        clear();
        List<MarkDownEntity> entities = _markdown_item.processAssetFile(asset);
        setContent(entities);
    }

    public void register(final IMarkdownEntityEvent listener) {
        synchronized (_listeners) {
            if (listener != null && !_listeners.contains(listener)) {
                _listeners.add(listener);
            }
        }
    }

    public void unregister(IMarkdownEntityEvent listener) {
        synchronized (_listeners) {
            if (listener != null && _listeners.contains(listener)) {
                _listeners.remove(listener);
            }
        }
    }

    private void setContent(List<MarkDownEntity> entities) {
        _entities = entities;

        if (_entities != null) {
            boolean last_required_new_line = false;
            for (MarkDownEntity entity : _entities) {
                if(last_required_new_line){// && !entity.canManageNewLine()){
                    //TODO append \n on next manageable
                    addTextEntityInLayout(new TextEntity(" "));
                    last_required_new_line = false;
                }

                if (entity instanceof ImageEntity) {
                    addImageEntityInLayout((ImageEntity) entity);
                    last_required_new_line = true;
                } else if (entity instanceof TextEntity) {
                    addTextEntityInLayout((TextEntity) entity);
                    last_required_new_line = false;
                } else if (entity instanceof ColorEntity) {
                    addColorEntity((ColorEntity) entity);
                    if(((ColorEntity) entity).isDefaultColor()){
                        last_required_new_line = true;
                    }
                } else if (entity instanceof ArrayEntity) {
                    addArrayEntityInLayout((ArrayEntity) entity);
                    last_required_new_line = true;
                }
            }
        }
    }

    private void addColorEntity(ColorEntity entity) {
        if (!entity.isDefaultColor()) {
            _last_color = entity.getColorInteger();
            _last_background_color = entity.getBackgroundColorInteger();
        } else {
            _last_color = _md_text_color;
            _last_background_color = Color.TRANSPARENT;
        }
    }

    private void addTextEntityInLayout(TextEntity entity) {
        TextView view = new TextView(getContext());
        if(_last_background_color != Color.TRANSPARENT) {
            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
        }else{
            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
        }
        view.setTextColor(_last_color);//getContext().getResources().getColor(R.color.black));
        view.setBackgroundColor(_last_background_color);
        view.setPadding(_md_padding_left, 0, _md_padding_right, 0);
        view.setText(entity.getString());

        view.setClickable(true);
        view.setLinksClickable(true);
        view.setMovementMethod(LinkMovementMethod.getInstance());
        _layout.addView(view);
    }

    private void addArrayEntityInLayout(ArrayEntity entity) {

        AutoGridView gridview = new AutoGridView(getContext());
        gridview.setNumColumns(entity.getNumberColumns());
        gridview.setLayoutParams(new AutoGridView.LayoutParams(LayoutParams.FILL_PARENT, Integer.MAX_VALUE >> 1));
        gridview.setBackgroundColor(Color.BLACK);
        gridview.setColumnWidth(GridView.AUTO_FIT);
        gridview.setVerticalSpacing(_md_array_spacing);
        gridview.setHorizontalSpacing(_md_array_spacing);
        gridview.setPadding(_md_array_spacing, _md_array_spacing, _md_array_spacing, _md_array_spacing);
        gridview.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);

        AutoMeasureAdapter adapter = entity.createAdapter(getContext(), _last_color,
                _md_array_header_color, _md_array_body_color, _md_cell_padding);
        gridview.setAdapter(adapter);
        _layout.addView(gridview);
    }

    private void addImageEntityInLayout(final ImageEntity entity) {
        final ImageView view = new ImageView(getContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER_HORIZONTAL;
        view.setLayoutParams(params);
        view.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        try {

            _layout.addView(view);
            if (entity.isHttp()) {
                Picasso.with(getContext()).load(entity.getSrc()).into(view);
            } else {
                int resID = getResources().getIdentifier(entity.getSrc(), "drawable", getContext().getPackageName());
                view.setImageResource(resID);
            }

            if (entity.isClickable()) {
                view.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        synchronized (_listeners) {
                            for (IMarkdownEntityEvent listener : _listeners) {
                                listener.onEntityEvent(EventType.IMAGE, view, entity);
                            }
                        }
                    }
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
