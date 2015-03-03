package eu.codlab.markdown;

import android.content.Context;
import android.graphics.Color;
import android.text.method.LinkMovementMethod;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import eu.codlab.markdown.entities.ColorEntity;
import eu.codlab.markdown.entities.ImageEntity;
import eu.codlab.markdown.entities.MarkDownEntity;
import eu.codlab.markdown.entities.TextEntity;

import java.util.List;

/**
 * Created by kevinleperf on 08/01/15.
 */
public class MarkdownView extends LinearLayout {
    private int _last_color;

    private Markdown _markdown_item;
    private List<MarkDownEntity> _entities;
    private ViewGroup _layout;

    private void init() {
        _markdown_item = new Markdown(getContext());
        View main = ((LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.markdown_view, this, false);
        _layout = (ViewGroup) main.findViewById(R.id.markdown_area);
        addView(main);
        _last_color = Color.BLACK;
    }

    public MarkdownView(Context context) {
        super(context);
        init();
    }

    public MarkdownView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MarkdownView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void clear() {
        //this.removeAllViews();
    }

    public void setStringContent(String text) {
        clear();
        List<MarkDownEntity> entities = _markdown_item.processText(text);
        setContent(entities);
    }

    public void setAssetContent(String asset) {
        clear();
        List<MarkDownEntity> entities = _markdown_item.processAssetFile(asset);
        setContent(entities);
    }

    private void setContent(List<MarkDownEntity> entities) {
        _entities = entities;

        if (_entities != null) {
            for (MarkDownEntity entity : _entities) {
                if (entity instanceof ImageEntity) {
                    addImageEntityInLayout((ImageEntity) entity);
                } else if (entity instanceof TextEntity) {
                    addTextEntityInLayout((TextEntity) entity);
                } else if (entity instanceof ColorEntity) {
                    addColorEntity((ColorEntity) entity);
                }
            }
        }
    }

    private void addColorEntity(ColorEntity entity) {
        Log.d("MarkdownView","set color "+entity.getColorInteger());
        _last_color = entity.getColorInteger();
    }

    private void addTextEntityInLayout(TextEntity entity) {
        TextView view = new TextView(getContext());
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        view.setTextColor(_last_color);//getContext().getResources().getColor(R.color.black));
        view.setText(entity.getString());

        Log.d("MarkdownView", "setText " + entity.getString().toString());
        view.setClickable(true);
        view.setLinksClickable(true);
        view.setMovementMethod(LinkMovementMethod.getInstance());
        _layout.addView(view);
    }

    private void addImageEntityInLayout(ImageEntity entity) {
        Log.d("MarkdownView", "setImage " + entity.getSrc());
        final ImageView view = new ImageView(getContext());
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        try {

            _layout.addView(view);
            if (entity.isHttp()) {
                Picasso.with(getContext()).load(entity.getSrc()).into(view);
            } else {
                int resID = getResources().getIdentifier(entity.getSrc(), "drawable",
                        getContext().getPackageName());
                view.setImageResource(resID);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
