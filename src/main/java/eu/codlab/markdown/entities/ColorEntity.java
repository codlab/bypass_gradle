package eu.codlab.markdown.entities;

import android.graphics.Color;

import java.util.HashMap;

/**
 * Created by kevinleperf on 08/01/15.
 */
public class ColorEntity extends MarkDownEntity {
    private final static int DEFAULT_COLOR = 0xFFFFFFFF;

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

    private ColorEntity() {

    }

    public ColorEntity(String xml) {
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


    public ColorEntity(String r, String g, String b) {
        this(Integer.parseInt(r),
                Integer.parseInt(g),
                Integer.parseInt(b));
    }


    public ColorEntity(int r, int g, int b) {
        _r = r % 256;
        _g = g % 256;
        _b = b % 256;
        _color = DEFAULT_COLOR;
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
}
