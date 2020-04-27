package com.hmtmcse.texttoweb.data

import com.hmtmcse.texttoweb.Topic

class TopicMergeData {

    public Map<String, TopicListToMapData> currentTopicMap = [:]
    public List<Topic> previousTopic = []
    public String nav = ""

    TopicMergeData() {}

    TopicMergeData(Map<String, TopicListToMapData> currentTopicMap, List<Topic> previousTopic) {
        this.currentTopicMap = currentTopicMap
        this.previousTopic = previousTopic
    }

    TopicMergeData(List<Topic> previousTopic) {
        this.previousTopic = previousTopic
    }

    TopicMergeData(Map<String, TopicListToMapData> currentTopicMap) {
        this.currentTopicMap = currentTopicMap
    }

    TopicMergeData addNav(String name) {
        nav += "> " + name + " "
        return this
    }

    TopicMergeData initNav(String name) {
        nav = "> " + name + " "
        return this
    }
}
