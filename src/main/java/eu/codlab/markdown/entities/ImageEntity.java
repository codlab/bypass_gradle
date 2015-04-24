package eu.codlab.markdown.entities;

import android.util.Log;

/**
 * Created by kevinleperf on 08/01/15.
 */
public class ImageEntity extends MarkDownEntity{
    private String _alt;
    private String _src;
    private String _clickableUrl;
    private boolean _clickable;

    private ImageEntity(){
    }

    public ImageEntity(String src, String alt, String clickableUrl, boolean clickable){
        _src = src.toLowerCase();
        _alt = alt;
        _clickableUrl = clickableUrl;
        _clickable = clickable;
        if (!isHttp()) {
            _src = _src.replaceAll("\\.\\w+", "").replace(" ", "_").replace("-", "_");
        }
    }

    public String getSrc(){
        return _src != null ? _src : "";
    }

    public String getAlt(){
        return _alt != null ? _alt : "";
    }

    public boolean isClickable(){
        return _clickable;
    }

    public boolean isHttp(){
        return _src != null && _src.startsWith("http");
    }
}
