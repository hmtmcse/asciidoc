package com.hmtmcse.texttoweb;

import java.util.ArrayList;
import java.util.List;

public class VideoTopic {


    public String name;
    public String videoLink;
    public String videoThumb;
    public String videoPlayer;
    public String description;
    public String summery;
    public String id;
    public Seo seo;
    public List<VideoTopic> childs;

    public VideoTopic() {}

    public VideoTopic addChild(VideoTopic topic) {
        if (childs == null) {
            childs = new ArrayList<>();
        }
        childs.add(topic);
        return this;
    }

}
