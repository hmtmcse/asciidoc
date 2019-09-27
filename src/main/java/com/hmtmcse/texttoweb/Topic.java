package com.hmtmcse.texttoweb;

import java.util.List;

public class Topic {
    public String name;
    public String url;
    public String thumb;
    public String image;
    public String id;
    public String icon;
    public String videoLink;
    public String videoPlayer = TextToWebConst.YOUTUBE;
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

}
