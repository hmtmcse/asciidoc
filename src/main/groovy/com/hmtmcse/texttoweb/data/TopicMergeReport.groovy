package com.hmtmcse.texttoweb.data

class TopicMergeReport {

    public Boolean isEditable = true
    public Boolean isMerge = true
    public String name
    public String topicKey
    public String relativePath

    TopicMergeReport() {}

    TopicMergeReport(String name, String topicKey) {
        this.name = name
        this.topicKey = topicKey
    }

    TopicMergeReport setIsEditable(Boolean isEditable) {
        this.isEditable = isEditable
        return this
    }

    TopicMergeReport setIsMerge(Boolean isMerge) {
        this.isMerge = isMerge
        return this
    }

    TopicMergeReport setName(String name) {
        this.name = name
        return this
    }

    TopicMergeReport setTopicKey(String topicKey) {
        this.topicKey = topicKey
        return this
    }

    TopicMergeReport setRelativePath(String relativePath) {
        this.relativePath = relativePath
        return this
    }
}
