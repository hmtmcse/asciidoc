package com.hmtmcse.te.data

import com.hmtmcse.texttoweb.Descriptor
import com.hmtmcse.texttoweb.Seo

class TextToWebEngineData {

    public Descriptor descriptor
    public String breadcrumbName = "Back to"
    public String layout;
    public String urlKey;
    public String url;
    public String relativePath;
    public String absolutePath;
    public String descriptorAbsolutePath;
    public TopicNav topicNav;
    public TopicNav relatedTopicNav;

    Seo getSeoData(Seo seo = null) {
        if (topicNav && topicNav.meta) {
            Seo seoFromLoop = processMeta(topicNav.meta)
            if (seoFromLoop) {
                return seoFromLoop
            }
        }
        return descriptor?.seo ?: seo
    }

    private Seo processMeta(Map<String, TopicNavItem> meta) {
        Seo seo = null
        meta.find { key, item ->
            if (urlKey.equals(key)) {
                seo = item.seo
                return true
            }
            if (item.childs) {
                seo = processMeta(item.childs)
            }
        }
        return seo
    }

}
