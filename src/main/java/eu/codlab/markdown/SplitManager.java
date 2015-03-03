package eu.codlab.markdown;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import eu.codlab.markdown.entities.ColorEntity;
import eu.codlab.markdown.entities.ImageEntity;
import eu.codlab.markdown.entities.MarkDownEntity;
import eu.codlab.markdown.entities.TextEntity;
import in.uncod.android.bypass.Bypass;

/**
 * Created by kevinleperf on 08/01/15.
 */
class SplitManager {
    //TODO FIX THIS LOOK BEHIND STRUCTURE
    private final static String DELIMITER_IMAGE_WITH_DELIMITER = "(?=!\\[.+\\]\\(.+\\))|(?<=!\\[[a-zA-Z0-9]{0,30}\\]\\([a-zA-Z0-9]{0,50}\\)){50}+";
    private final static String MATCH_IMAGE = "!\\[(.+)\\]\\((.+)\\)";
    private final static Pattern IMAGE_PATTERN = Pattern.compile(MATCH_IMAGE);


    private final static String MATCH_XML_COLOR = "rgb\\(([^,)]+)\\)";
    private final static Pattern COLOR_XML_PATTERN = Pattern.compile(MATCH_XML_COLOR);

    private final static String MATCH_COLOR = "rgb\\(([0-9]+),([0-9]+),([0-9]+)\\)";
    private final static Pattern COLOR_PATTERN = Pattern.compile(MATCH_COLOR);

    private final static String MATCH_END_COLOR = "!rgb";
    private final static Pattern COLOR_END_PATTERN = Pattern.compile(MATCH_END_COLOR);

    private String _text_to_transform;

    public SplitManager() {
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

            String[] splits = splitRaw();

            MarkDownEntity tmp;
            for (String split : splits) {
                tmp = getSubTextImage(split);
                if (tmp != null) {
                    result.add(tmp);
                } else {
                    result.add(getText(split));
                }
            }
            return result;
        }
        return null;
    }

    private String[] concatenate(List<String> str) {
        String[] res = new String[str.size()];
        for (int i = 0; i < res.length; i++) {
            res[i] = str.get(i);
        }
        return res;
    }

    //WHEN THE LOOK BEHING IS FIXED
    //HERE IT IS SIMPLY return _text_to_transform.split(THE STRUCTURE);
    private String[] splitRaw() {
        if (_text_to_transform != null) {
            String splitted[] = _text_to_transform.split("\n");

            String tmp = "";
            List<String> result = new ArrayList<>();
            for (String split : splitted) {
                if (matchColor(split)) {
                    Log.d("MarkDownView", "color detected " + split);
                    result.add(tmp);
                    result.add(split);
                    tmp = "";
                } else if (matchImage(split)) {
                    result.add(tmp);
                    result.add(split);
                    tmp = "";
                } else {
                    tmp += split + "\n";
                }
            }
            result.add(tmp);

            return concatenate(result);
        }
        return null;
    }

    private boolean matchColor(String text_to_test) {
        Matcher matcher = COLOR_PATTERN.matcher(text_to_test);
        Matcher xml_matcher = COLOR_XML_PATTERN.matcher(text_to_test);
        Matcher end_matcher = COLOR_END_PATTERN.matcher(text_to_test);
        return matcher.matches() || end_matcher.matches() || xml_matcher.matches();
    }

    private boolean matchImage(String text_to_test) {
        Matcher matcher = IMAGE_PATTERN.matcher(text_to_test);
        return matcher.matches();
    }

    /**
     * Fetch the image from this text
     *
     * @param text_to_test
     * @return a new markdownentity if the pattern is an image, null otherwise
     */
    private MarkDownEntity getSubTextImage(String text_to_test) {
        Matcher matcher =
                IMAGE_PATTERN.matcher(text_to_test);

        Matcher color_matcher = COLOR_PATTERN.matcher(text_to_test);
        Matcher color_xml_matcher = COLOR_XML_PATTERN.matcher(text_to_test);
        Matcher color_end_matcher = COLOR_END_PATTERN.matcher(text_to_test);

        if (matcher.matches() && matcher.groupCount() > 1) {
            String alt = matcher.group(1);
            String src = matcher.group(2);
            return new ImageEntity(src, alt);
        } else if (color_matcher.matches() && color_matcher.groupCount() > 1) {
            String r = color_matcher.group(1);
            String g = color_matcher.group(2);
            String b = color_matcher.group(3);
            return new ColorEntity(r, g, b);
        } else if (color_xml_matcher.matches() && color_xml_matcher.groupCount() >= 1) {
            return new ColorEntity(color_xml_matcher.group(1));
        } else if (color_end_matcher.matches()) {
            return new ColorEntity(0, 0, 0);
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
        return new TextEntity(string);
    }
}
