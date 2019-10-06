package com.hmtmcse.texttoweb;

import java.util.ArrayList;
import java.util.List;

public class Topic {
    public String name;
    public String url;
    public String target;
    public String thumb;
    public String image;
    public String id;
    public String icon;
    public String videoLink;
    public String videoPlayer;
    public String description;
    public String summery;
    public String filePath;
    public String parentId;
    public Seo seo;
    public List<Topic> childs;


    public Topic() {}

    public Topic(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public Topic addChild(Topic topic) {
        if (childs == null) {
            childs = new ArrayList<>();
        }
        childs.add(topic);
        return this;
    }

    public Topic addChild(String name, String url ) {
        return addChild(new Topic(name, url));
    }

    public Topic setThumb(String thumb) {
        this.thumb = thumb;
        return this;
    }

    public Topic setId(String id) {
        this.id = id;
        return this;
    }

    public Topic setVideoLink(String videoLink) {
        this.videoLink = videoLink;
        return this;
    }

    public Topic setSummery(String summery) {
        this.summery = summery;
        return this;
    }

    public Topic setIcon(String icon) {
        this.icon = icon;
        return this;
    }

    public Topic setSeo(Seo seo) {
        this.seo = seo;
        return this;
    }
}
