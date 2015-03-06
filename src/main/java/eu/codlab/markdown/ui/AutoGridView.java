package eu.codlab.markdown.ui;

import android.content.Context;
import android.content.res.Configuration;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ListAdapter;

import com.twotoasters.jazzylistview.JazzyGridView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kevinleperf on 04/03/15.
 */
public class AutoGridView extends JazzyGridView {
    private boolean hasCalculated = false;
    private List<Integer> _rows = new ArrayList<>();
    private int mPaddingBottom = 0;
    private int mPaddingTop = 0;

    private int numColumnsID;
    private int previousFirstVisible;
    private int numColumns = 1;

    public AutoGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    public AutoGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public AutoGridView(Context context) {
        super(context);
    }


    private void setHeightAtRow(int index, int height) {
        while (_rows.size() <= index) {
            _rows.add(0);
        }

        int h = _rows.get(index);
        h = height;
        _rows.set(index, h);
    }

    private int getOverAllHeight() {
        int height = mPaddingTop;
        if (_rows != null && _rows.size() > 0) {
            height += (_rows.size() - 1) * height;
            for (Integer integer : _rows) {
                height += integer.intValue();
            }
        }
        height += mPaddingBottom;
        return height;
    }

    private void init(AttributeSet attrs) {
        // Read numColumns out of the AttributeSet
        int count = attrs.getAttributeCount();
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                String name = attrs.getAttributeName(i);
                if (name != null && name.equals("numColumns")) {
                    // Update columns
                    this.numColumnsID = attrs.getAttributeResourceValue(i, 1);
                    updateColumns();
                    break;
                }
            }
        }
    }

    private void updateColumns() {
        this.numColumns = getContext().getResources().getInteger(numColumnsID);
    }

    @Override
    public void setNumColumns(int numColumns) {
        this.numColumns = numColumns;
        super.setNumColumns(numColumns);

        setSelection(previousFirstVisible);
    }

    @Override
    protected void onLayout(boolean changed, int leftPos, int topPos, int rightPos, int bottomPos) {
        super.onLayout(changed, leftPos, topPos, rightPos, bottomPos);
        setHeights();
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        updateColumns();
        setNumColumns(this.numColumns);
    }

    @Override
    protected void onScrollChanged(int newHorizontal, int newVertical, int oldHorizontal, int oldVertical) {
        int firstVisible = getFirstVisiblePosition();
        if (previousFirstVisible != firstVisible) {
            previousFirstVisible = firstVisible;
            setHeights();
        }

        super.onScrollChanged(newHorizontal, newVertical, oldHorizontal, oldVertical);
    }

    private boolean setHeights() {
        boolean calculated = hasCalculated;
        ListAdapter adapter = getAdapter();

        if (adapter != null) {
            int row = 0;
            for (int i = 0; i < getChildCount(); i += numColumns) {
                // Determine the maximum height for this row
                int maxHeight = 0;
                for (int j = i; j < i + numColumns; j++) {
                    View view = getChildAt(j);
                    if (view != null && view.getHeight() > maxHeight) {
                        maxHeight = view.getHeight();
                    }
                }

                if (maxHeight > 0) {
                    setHeightAtRow(row, maxHeight);
                    for (int j = i; j < i + numColumns; j++) {
                        View view = getChildAt(j);
                        if (view != null && view.getHeight() != maxHeight) {
                            view.setMinimumHeight(maxHeight);
                        }
                    }
                }
                row++;

                hasCalculated = true;
            }
        }

        if (!calculated) {
            return hasCalculated;
        }
        return false;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int heightSpec;
        setHeights();
        heightSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, heightSpec);

        if (hasCalculated) {
            Log.d("Markdown", "has calculated " + getOverAllHeight());
            setMeasuredDimension(getMeasuredWidth(), getOverAllHeight());
        }
    }

    public void setPadding(int left, int top, int right, int bottom) {
        mPaddingTop = top;
        mPaddingBottom = bottom;
        super.setPadding(left, top, right, bottom);
    }
}
