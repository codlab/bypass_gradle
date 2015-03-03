package eu.codlab.markdown.entities;

/**
 * Created by kevinleperf on 08/01/15.
 */
public class TextEntity extends MarkDownEntity{
    private CharSequence _string;

    private TextEntity() {

    }

    public TextEntity(CharSequence string) {
        _string = string;
    }

    public CharSequence getString() {
        return _string;
    }
}
