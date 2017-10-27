package com.apps.koru.star8_video_app.objects.dbobjects;

/**
 * Created by danielluzgarten on 27/10/2017.
 */

public class AdFormatAndPo {
    private String po;
    private String format;

    private AdFormatAndPo(){}

    public AdFormatAndPo(String po, String format) {
        this.po = po;
        this.format = format;
    }

    public String getPo() {
        return po;
    }

    public String getFormat() {
        return format;
    }
}
