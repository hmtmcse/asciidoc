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
        if (topicNav && topicNav.meta && topicNav.meta.get(urlKey)?.seo) {
            return topicNav.meta.get(urlKey).seo
        }
        return descriptor?.seo ?: seo
    }

}
