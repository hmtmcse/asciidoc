package com.hmtmcse.data

import com.hmtmcse.texttoweb.Seo

class SeoEditData extends Seo {

    public String url
    public String navName

    String getUrl() {
        return url
    }

    String getNavName() {
        return navName
    }

    SeoEditData setNavName(String navName) {
        this.navName = navName
        return this
    }

    SeoEditData setUrl(String url) {
        this.url = url
        return this
    }

    public SeoEditData copy(Seo seo) {
        this.title = seo.title
        this.scripts = seo.scripts
        this.tags = seo.tags
        return this
    }

    public Seo getSeo() {
        Seo seo = new Seo()
        seo.title = this.title
        seo.scripts = this.scripts
        seo.tags = this.tags
        return seo
    }

}
