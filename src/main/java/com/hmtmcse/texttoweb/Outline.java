package com.hmtmcse.texttoweb;

import java.util.ArrayList;
import java.util.List;

public class Outline extends Descriptor {

    public List<Topic> relatedTopics = null;

    public Outline addRelatedTopic(Topic topic) {
        if (relatedTopics == null) {
            relatedTopics = new ArrayList<>();
        }
        relatedTopics.add(topic);
        return this;
    }
}
