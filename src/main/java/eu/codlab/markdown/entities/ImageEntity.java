package eu.codlab.markdown.entities;

import android.util.Log;

/**
 * Created by kevinleperf on 08/01/15.
 */
public class ImageEntity extends MarkDownEntity{
    private String _alt;
    private String _src;

    private ImageEntity(){
    }

    public ImageEntity(String src, String alt){
        _src = src.toLowerCase();
        _alt = alt;

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

    public boolean isHttp(){
        return _src != null && _src.startsWith("http");
    }
}
