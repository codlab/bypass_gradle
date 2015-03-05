package eu.codlab.markdown.entities;

import android.graphics.Color;

import java.util.HashMap;

/**
 * Created by kevinleperf on 08/01/15.
 */
public class ColorEntity extends MarkDownEntity {
    private final static int DEFAULT_COLOR = 0xFFFFFFFF;
    private final static int DEFAULT_COLOR_WHITE = Color.TRANSPARENT;

    private final static HashMap<String, Integer> _color_map = new HashMap<>();

    {
        _color_map.put("black", Color.BLACK);
        _color_map.put("dkgray", Color.DKGRAY);
        _color_map.put("gray", Color.GRAY);
        _color_map.put("ltgray", Color.LTGRAY);
        _color_map.put("white", Color.WHITE);
        _color_map.put("red", Color.RED);
        _color_map.put("green", Color.GREEN);
        _color_map.put("blue", Color.BLUE);
        _color_map.put("yellow", Color.YELLOW);
        _color_map.put("cyan", Color.CYAN);
        _color_map.put("magenta", Color.MAGENTA);
        _color_map.put("transparent", Color.TRANSPARENT);
    }

    private int _color;
    private int _r;
    private int _g;
    private int _b;

    private int _background_color;
    private int _background_r;
    private int _background_g;
    private int _background_b;

    private ColorEntity() {
        _background_color = DEFAULT_COLOR_WHITE;
        _background_b = _background_g = _background_r = -1;
    }

    public ColorEntity(String xml) {
        this();
        setXMLTextColor(xml);
    }

    public ColorEntity(String xml, String background_xml) {
        this();
        setXMLTextColor(xml);
        setXMLBackgroundColor(background_xml);
    }


    public ColorEntity(String r, String g, String b, String br, String bg, String bb) {
        this(Integer.parseInt(r),
                Integer.parseInt(g),
                Integer.parseInt(b),
                Integer.parseInt(br),
                Integer.parseInt(bg),
                Integer.parseInt(bb));

    }

    public ColorEntity(String r, String g, String b) {
        this(Integer.parseInt(r),
                Integer.parseInt(g),
                Integer.parseInt(b));
    }


    public ColorEntity(int r, int g, int b, int br, int bg, int bb) {
        this();
        _r = r % 256;
        _g = g % 256;
        _b = b % 256;
        _color = DEFAULT_COLOR;

        _background_r = br % 256;
        _background_g = bg % 256;
        _background_b = bb % 256;
        _background_color = DEFAULT_COLOR_WHITE;
    }

    public ColorEntity(int r, int g, int b) {
        this();
        _r = r % 256;
        _g = g % 256;
        _b = b % 256;
        _color = DEFAULT_COLOR;
    }

    private void setXMLBackgroundColor(String xml) {
        try {
            xml = xml.toLowerCase();
            Integer color = _color_map.get(xml);
            if (color != null) {
                _background_color = color.intValue();
            } else {
                if (xml.indexOf("#") < 0) {
                    _background_color = Color.parseColor("#" + xml);
                } else {
                    _background_color = Color.parseColor(xml);
                }
            }
        } catch (Exception e) {
            _background_color = Color.BLACK;
        }
        _background_r = -1;
        _background_g = -1;
        _background_b = -1;
    }

    private void setXMLTextColor(String xml) {
        try {
            xml = xml.toLowerCase();
            Integer color = _color_map.get(xml);
            if (color != null) {
                _color = color.intValue();
            } else {
                if (xml.indexOf("#") < 0) {
                    _color = Color.parseColor("#" + xml);
                } else {
                    _color = Color.parseColor(xml);
                }
            }
        } catch (Exception e) {
            _color = Color.BLACK;
        }
        _r = -1;
        _g = -1;
        _b = -1;
    }

    public int getR() {
        return _r;
    }

    public int getG() {
        return _g;
    }

    public int getB() {
        return _b;
    }


    public int getBackgroundR() {
        return _background_r;
    }

    public int getBackgroundG() {
        return _background_g;
    }

    public int getBackgroundB() {
        return _background_b;
    }


    public int getColorInteger() {
        if (_color != 0xFFFFFFFF) {
            return _color;
        }

        return Color.rgb(getR(), getG(), getB());
    }

    public boolean isDefaultColor() {
        return getR() == -1 && getG() == -1 && getB() == -1 && _color == DEFAULT_COLOR;
    }

    public static ColorEntity createDefaultColor() {
        return new ColorEntity(-1, -1, -1);
    }

    public boolean hasBackgroundColor() {
        return (getBackgroundR() != -1 && getBackgroundG() != -1 && getBackgroundB() != -1) || _background_color != DEFAULT_COLOR_WHITE;
    }

    public int getBackgroundColorInteger() {
        if (_background_color != DEFAULT_COLOR_WHITE) {
            return _background_color;
        }

        return Color.rgb(getBackgroundR(), getBackgroundG(), getBackgroundB());
    }
}
