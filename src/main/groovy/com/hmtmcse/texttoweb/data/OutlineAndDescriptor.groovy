package com.hmtmcse.texttoweb.data

import com.hmtmcse.texttoweb.Descriptor
import com.hmtmcse.texttoweb.Topic
import com.hmtmcse.texttoweb.sample.DescriptorSample

class OutlineAndDescriptor {

    public Map<String, Topic> outlineTopicMap = [:]
    public Map<String, Topic> detailsTopicMap = [:]
    public Descriptor outlineDescriptor
    public Descriptor detailsDescriptor

    public OutlineAndDescriptor(String name) {
        outlineDescriptor = DescriptorSample.getTopicsDescriptor(name)
        outlineDescriptor.addTopic(new Topic("Bismillah", "#"))
        detailsDescriptor = DescriptorSample.getTopicsDescriptor(name)
    }


    public OutlineAndDescriptor addToOutlineBismillah(Topic topic){
        outlineDescriptor.topics.first().addChild(topic)
        outlineTopicMap.put(topic.url, topic)
        return this;
    }
}
