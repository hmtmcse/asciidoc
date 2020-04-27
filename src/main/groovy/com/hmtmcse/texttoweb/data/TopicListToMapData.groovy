package com.hmtmcse.texttoweb.data

import com.hmtmcse.texttoweb.Topic

class TopicListToMapData {

    public Topic topic
    public Map<String, TopicListToMapData> child = [:]

    TopicListToMapData() {}

    TopicListToMapData(Topic topic) {
        this.topic = topic
    }
}
