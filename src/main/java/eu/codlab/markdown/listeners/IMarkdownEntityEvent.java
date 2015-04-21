package eu.codlab.markdown.listeners;

import android.view.View;

import eu.codlab.markdown.enums.EventType;

/**
 * Created by loicduparc on 21/04/15.
 */
public interface IMarkdownEntityEvent {

    void onEntityEvent(EventType type, View view, String link);
}
