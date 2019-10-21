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
        outlineDescriptor = DescriptorSample.getOutlineDescriptor(name)
        outlineDescriptor.addTopic(new Topic("Bismillah", "#"))
        detailsDescriptor = DescriptorSample.getDetailsDescriptor(name)
    }

    public addTopicParent(String name) {
        outlineDescriptor.addTopic(new Topic(name, "#"))
        detailsDescriptor.addTopic(new Topic(name, "#"))
    }

    public OutlineAndDescriptor addToOutlineByIndex(Integer index, Topic topic){
        outlineDescriptor.topics.get(index).addChild(topic)
        outlineTopicMap.put(topic.url, topic)
        return this
    }


    public OutlineAndDescriptor addToDetailsByIndex(Integer index, Topic topic){
        if (index){
            detailsDescriptor.topics.get(index).addChild(topic)
        }else{
            detailsDescriptor.addTopic(topic)
        }
        detailsTopicMap.put(topic.url, topic)
        return this
    }

    public Integer outlineLastIndex(){
        if (outlineDescriptor.topics.size()){
            return outlineDescriptor.topics.size() - 1
        }else{
            return 0
        }
    }

    public Integer detailsLastIndex(){
        if (detailsDescriptor.topics.size()){
            return detailsDescriptor.topics.size() - 1
        }else{
            return 0
        }
    }

}
