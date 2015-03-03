package eu.codlab.markdown;

import android.util.Log;

import eu.codlab.markdown.entities.ImageEntity;
import eu.codlab.markdown.entities.MarkDownEntity;
import eu.codlab.markdown.entities.TextEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import in.uncod.android.bypass.Bypass;

/**
 * Created by kevinleperf on 08/01/15.
 */
class SplitManager {
    //TODO FIX THIS LOOK BEHIND STRUCTURE
    private final static String DELIMITER_IMAGE_WITH_DELIMITER = "(?=!\\[.+\\]\\(.+\\))|(?<=!\\[[a-zA-Z0-9]{0,30}\\]\\([a-zA-Z0-9]{0,50}\\)){50}+";
    private final static String MATCH_IMAGE = "!\\[(.+)\\]\\((.+)\\)";
    private final static Pattern IMAGE_PATTERN = Pattern.compile(MATCH_IMAGE);

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

            String [] splits = splitRaw();

            MarkDownEntity tmp = null;
            for(String split : splits) {
                tmp = getSubTextImage(split);
                if(tmp != null) {
                    result.add(tmp);
                }else {
                    result.add(getText(split));
                }
            }
            return result;
        }
        return null;
    }

    private String [] concatenate(List<String> str){
        String []res = new String[str.size()];
        for(int i=0;i<res.length;i++) {
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
            for(String split : splitted) {
                if(matchImage(split)){
                    result.add(tmp);
                    result.add(split);
                    tmp="";
                }else{
                    tmp+=split+"\n";
                }
            }
            result.add(tmp);

            return concatenate(result);
        }
        //return new String[]{_text_to_transform};
        return null;
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
        if (matcher.matches() && matcher.groupCount() > 1) {
            Log.d("SplitManager", "matcher.matches(): " + matcher.matches() + " matcher.groupCount():" +
                    matcher.groupCount()+" "+matcher.group(0)+" "+matcher.group(1));
            String alt = matcher.group(1);
            String src = matcher.group(2);
            return new ImageEntity(src, alt);
        }
        return null;
    }

    /**
     * Return the markdown entity representing by this text
     * @param text_to_entity
     * @return
     */
    private MarkDownEntity getText(String text_to_entity) {
        Bypass bypass = new Bypass();
        CharSequence string = bypass.markdownToSpannable(text_to_entity);
        //text.setText(string);
        //text.setMovementMethod(LinkMovementMethod.getInstance());


        return new TextEntity(string);
    }
}
