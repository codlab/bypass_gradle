package eu.codlab.markdown.raw;

/**
 * Created by kevinleperf on 04/03/15.
 */
public abstract class RawItem<T extends Object> {
    protected T _content;

    public abstract void appendString(String string);

    public T getContent() {
        return _content;
    }
}
