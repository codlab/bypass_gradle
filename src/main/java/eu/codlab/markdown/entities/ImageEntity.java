package com.wopata.markdown.entities;

/**
 * Created by kevinleperf on 08/01/15.
 */
public class ImageEntity extends MarkDownEntity{
    private String _alt;
    private String _src;

    private ImageEntity(){

    }

    public ImageEntity(String src, String alt){
        _src= src;
        _alt = alt;
    }

    public String getSrc(){
        return _src != null ? _src : "";
    }

    public String getAlt(){
        return _alt != null ? _alt : "";
    }

    public boolean isHttp(){
        return _src != null && _src.indexOf("http") == 0;
    }
}
