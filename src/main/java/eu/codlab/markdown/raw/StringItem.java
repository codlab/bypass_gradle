package eu.codlab.markdown.raw;

/**
 * Created by kevinleperf on 04/03/15.
 */
public class StringItem extends RawItem<String> {

    public StringItem(String content) {
        super();
        appendString(content);
    }

    @Override
    public void appendString(String string) {
        _content = string;
    }
}
