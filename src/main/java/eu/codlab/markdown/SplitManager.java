package eu.codlab.markdown;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import eu.codlab.markdown.entities.ArrayEntity;
import eu.codlab.markdown.entities.ColorEntity;
import eu.codlab.markdown.entities.ImageEntity;
import eu.codlab.markdown.entities.MarkDownEntity;
import eu.codlab.markdown.entities.TextEntity;
import eu.codlab.markdown.raw.ArrayItem;
import eu.codlab.markdown.raw.RawItem;
import eu.codlab.markdown.raw.StringItem;
import eu.codlab.markdown.raw.array.Row;
import in.uncod.android.bypass.Bypass;

/**
 * Created by kevinleperf on 08/01/15.
 */
class SplitManager {
    private List<ColorEntity> _stack_of_color;
    //TODO FIX THIS LOOK BEHIND STRUCTURE
    private final static String DELIMITER_IMAGE_WITH_DELIMITER = "(?=!\\[.+\\]\\(.+\\))|(?<=!\\[[a-zA-Z0-9]{0,30}\\]\\([a-zA-Z0-9]{0,50}\\)){50}+";
    private final static String MATCH_IMAGE = "!\\[(.+)\\]\\((.+)\\)";
    private final static Pattern IMAGE_PATTERN = Pattern.compile(MATCH_IMAGE);


    private final static String MATCH_XML_WITH_BG_COLOR = "rgb\\(([^,)]+),([^,)]+)\\)";
    private final static Pattern COLOR_XML_WITH_BG_PATTERN = Pattern.compile(MATCH_XML_WITH_BG_COLOR);

    private final static String MATCH_XML_COLOR = "rgb\\(([^,)]+)\\)";
    private final static Pattern COLOR_XML_PATTERN = Pattern.compile(MATCH_XML_COLOR);

    private final static String MATCH_COLOR = "rgb\\(([0-9]+),([0-9]+),([0-9]+)\\)";
    private final static Pattern COLOR_PATTERN = Pattern.compile(MATCH_COLOR);

    private final static String MATCH_COLOR_WITH_BG = "rgb\\(([0-9]+),([0-9]+),([0-9]+),([0-9]+),([0-9]+),([0-9]+)\\)";
    private final static Pattern COLOR_WITH_BG_PATTERN = Pattern.compile(MATCH_COLOR_WITH_BG);

    private final static String MATCH_END_COLOR = "!rgb";
    private final static Pattern COLOR_END_PATTERN = Pattern.compile(MATCH_END_COLOR);

    private final static String MATCH_ARRAY_LINE = "[-]+([ ]+[-]+)";
    private final static Pattern ARRAY_LINE_PATTERN = Pattern.compile(MATCH_ARRAY_LINE);

    private final static String MATCH_EMPTY_LINE = "[\n\t]*";
    private final static Pattern EMPTY_LINE_PATTERN = Pattern.compile(MATCH_EMPTY_LINE);

    private String _text_to_transform;

    public SplitManager() {
        _stack_of_color = new ArrayList<>();
    }

    /**
     * @param text_to_transform
     */
    public void setup(String text_to_transform) {
        _text_to_transform = text_to_transform;
    }


    public List<MarkDownEntity> compute() {
        if (_text_to_transform != null) {
            List<MarkDownEntity> result = new ArrayList<>();

            RawItem[] splits = splitRaw();

            MarkDownEntity tmp;
            for (RawItem split : splits) {
                if (split instanceof StringItem) {
                    StringItem current = (StringItem) split;

                    tmp = getSubTextImage(current.getContent());
                    if (tmp != null) {
                        result.add(tmp);
                    } else {
                        result.add(getText(current.getContent()));
                    }
                } else if (split instanceof ArrayItem) {
                    ArrayItem array = (ArrayItem) split;
                    Row header = array.getHeader();
                    result.add(getArray(array));
                }
            }
            return result;
        }
        return null;
    }

    private ColorEntity unstackColor() {
        if (_stack_of_color != null && _stack_of_color.size() > 0) {
            _stack_of_color.remove(0);
            if (_stack_of_color.size() > 0) {
                return _stack_of_color.get(0);
            }
        }
        return ColorEntity.createDefaultColor();
    }

    private ColorEntity stackColor(ColorEntity color) {
        if (_stack_of_color != null) {
            _stack_of_color.add(0, color);
        }
        return color;
    }

    private RawItem[] concatenate(List<RawItem> str) {
        RawItem[] res = new RawItem[str.size()];
        for (int i = 0; i < res.length; i++) {
            res[i] = str.get(i);
        }
        return res;
    }

    private RawItem[] splitRaw() {
        if (_text_to_transform != null) {
            String splitted[] = _text_to_transform.split("\\r?\\n");

            String tmp = "";
            ArrayItem array_tmp = null;

            List<RawItem> result = new ArrayList<>();
            for (String split : splitted) {
                if (array_tmp != null && array_tmp.isFinish()) {
                    result.add(array_tmp);
                    array_tmp = null;
                }

                if (matchArrayLine(split)) {
                    if (tmp != null && tmp.length() > 0) {
                        result.add(new StringItem(tmp));
                        tmp = "\n";
                    }
                    if (array_tmp == null) {
                        array_tmp = new ArrayItem();
                        array_tmp.setHeader(split);
                    } else if (array_tmp.isInHeader()) {
                        array_tmp.setIsHeaderForThisRow();
                    } else {//if(array_tmp.isInBody()){
                        array_tmp.flushRow();
                        //if we are already in the body, it is the end
                        result.add(array_tmp);
                        tmp += "\n";
                        array_tmp = null;
                    }
                } else if (matchEmptyString(split) && array_tmp != null) {
                    //in case we are in an array WITH an empty string
                    //it is a new row
                    array_tmp.flushRow();
                } else if (matchEndColor(split)) {
                    StringItem item_tmp = new StringItem(tmp);
                    StringItem item_split = new StringItem(split);
                    result.add(item_tmp);
                    result.add(item_split);
                    tmp = " \n";
                } else if (matchColor(split)) {
                    StringItem item_tmp = new StringItem(tmp + "\n\n");
                    StringItem item_split = new StringItem(split);
                    result.add(item_tmp);
                    result.add(item_split);
                    tmp = "";
                } else if (matchImage(split)) {
                    result.add(new StringItem(tmp));
                    result.add(new StringItem(split));
                    tmp = "\n";
                } else if (array_tmp != null) {
                    array_tmp.appendString(split);
                } else {
                    tmp += split + "\n";
                }
            }

            if (array_tmp != null && array_tmp.isFinish()) {
                result.add(array_tmp);
            }

            if (tmp != null && tmp.length() > 0) {
                result.add(new StringItem(tmp));
            }

            return concatenate(result);
        }
        return null;
    }

    private boolean matchEndColor(String text_to_test) {
        Matcher end_matcher = COLOR_END_PATTERN.matcher(text_to_test);
        return end_matcher.matches();
    }


    private boolean matchColor(String text_to_test) {
        Matcher matcher = COLOR_PATTERN.matcher(text_to_test);
        Matcher xml_matcher = COLOR_XML_PATTERN.matcher(text_to_test);
        Matcher bg_matcher = COLOR_WITH_BG_PATTERN.matcher(text_to_test);
        Matcher bg_xml_matcher = COLOR_XML_WITH_BG_PATTERN.matcher(text_to_test);
        return matcher.matches() || xml_matcher.matches()
                || bg_matcher.matches() || bg_xml_matcher.matches();
    }

    private boolean matchImage(String text_to_test) {
        Matcher matcher = IMAGE_PATTERN.matcher(text_to_test);
        return matcher.matches();
    }

    private boolean matchEmptyString(String text_to_test) {
        Matcher matcher = EMPTY_LINE_PATTERN.matcher(text_to_test);
        return matcher.matches();
    }

    private boolean matchArrayLine(String text_to_test) {
        Matcher matcher = ARRAY_LINE_PATTERN.matcher(text_to_test);
        return matcher.matches();
    }

    /**
     * Fetch the image from this text
     *
     * @param text_to_test
     * @return a new markdownentity if the pattern is an image, null otherwise
     */
    private MarkDownEntity getSubTextImage(String text_to_test) {

        //text_to_test = "![Clearfield Plus](dashboard_bg.png)";
        Matcher matcher = IMAGE_PATTERN.matcher(text_to_test);

        Matcher color_matcher = COLOR_PATTERN.matcher(text_to_test);
        Matcher color_bg_matcher = COLOR_WITH_BG_PATTERN.matcher(text_to_test);
        Matcher color_xml_bg_matcher = COLOR_XML_WITH_BG_PATTERN.matcher(text_to_test);
        Matcher color_xml_matcher = COLOR_XML_PATTERN.matcher(text_to_test);
        Matcher color_end_matcher = COLOR_END_PATTERN.matcher(text_to_test);

        if (matcher.matches() && matcher.groupCount() > 1) {
            String alt = matcher.group(1);
            String src = matcher.group(2);
            return new ImageEntity(src, alt);
        } else if (color_end_matcher.matches()) {
            return unstackColor();
        } else {
            //return the color stacked
            ColorEntity color = null;
            if (color_matcher.matches() && color_matcher.groupCount() > 1) {
                String r = color_matcher.group(1);
                String g = color_matcher.group(2);
                String b = color_matcher.group(3);
                color = new ColorEntity(r, g, b);
            } else if (color_bg_matcher.matches() && color_bg_matcher.groupCount() >= 1) {
                String r = color_bg_matcher.group(1);
                String g = color_bg_matcher.group(2);
                String b = color_bg_matcher.group(3);
                String br = color_bg_matcher.group(4);
                String bg = color_bg_matcher.group(5);
                String bb = color_bg_matcher.group(6);
                color = new ColorEntity(r, g, b, br, bg, bb);
            } else if (color_xml_bg_matcher.matches() && color_xml_bg_matcher.groupCount() >= 1) {
                color = new ColorEntity(color_xml_bg_matcher.group(1),
                        color_xml_bg_matcher.group(2));
            } else if (color_xml_matcher.matches() && color_xml_matcher.groupCount() >= 1) {
                color = new ColorEntity(color_xml_matcher.group(1));
            }

            if (color != null) {
                return stackColor(color);
            }
        }
        return null;
    }

    /**
     * Return the markdown entity representing by this text
     *
     * @param text_to_entity
     * @return
     */
    private MarkDownEntity getText(String text_to_entity) {
        Bypass bypass = new Bypass();
        CharSequence string = bypass.markdownToSpannable(text_to_entity);

        if (string.length() > 2 && string.charAt(string.length() - 1) == '\n') {
            return new TextEntity(string.subSequence(0, string.length() - 2));
        }
        return new TextEntity(string);
    }

    private ArrayEntity getArray(ArrayItem array) {
        return new ArrayEntity(array);
    }
}
