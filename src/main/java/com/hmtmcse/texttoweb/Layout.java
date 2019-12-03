package com.hmtmcse.texttoweb;

public class Layout {
    public String type = TextToWebConst.DETAILS;
    public Seo seo;

    public Layout() {}
    public Layout(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Seo getSeo() {
        return seo;
    }

    public void setSeo(Seo seo) {
        this.seo = seo;
    }
}
