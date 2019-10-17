package com.hmtmcse.texttoweb;


import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Descriptor {
    public Layout layout;
    public String defaultTitle;
    public List<Topic> topics = null;
    public Settings settings;
    public Map<String, Block> blocks = null;
    public Map<String, String> staticMap;

    public Descriptor addTopic(Topic topic) {
        if (topics == null) {
            topics = new ArrayList<>();
        }
        topics.add(topic);
        return this;
    }

    public Descriptor addBlock(String name, Block block) {
        if (blocks == null) {
            blocks = new LinkedHashMap<>();
        }
        blocks.put(name, block);
        return this;
    }

    public Descriptor findReplace(String key, String value) {
        if (staticMap == null) {
            staticMap = new LinkedHashMap<>();
        }
        staticMap.put(key, value);
        return this;
    }

    public Descriptor addTopic(String name, String url, String summer) {
        Topic topic = new Topic(name, url).setSummery(summer);
        addTopic(topic);
        return this;
    }

    public Topic topic(String name, String url, String summer) {
        return new Topic(name, url).setSummery(summer);
    }

    public Descriptor addTopicDummySummery(String name, String url) {
        Topic topic = new Topic(name, url).setSummery(
                "This section will describe about " + name + ". For More details please click on it."
        );
        addTopic(topic);
        return this;
    }

}
