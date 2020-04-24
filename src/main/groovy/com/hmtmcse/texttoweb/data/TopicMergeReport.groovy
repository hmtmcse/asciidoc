package com.hmtmcse.texttoweb.data

class TopicMergeReport {

    public Boolean isEditable = true
    public Boolean isMerge = true
    public String name
    public String url
    public String topicKey
    public String relativePath
    public String trashPath
    public String path
    public String status
    public String message
    public String descriptorName

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

    TopicMergeReport setTrashPath(String trashPath) {
        this.trashPath = trashPath
        return this
    }

    TopicMergeReport setPath(String path) {
        this.path = path
        return this
    }

    TopicMergeReport setStatus(String status) {
        this.status = status
        return this
    }

    TopicMergeReport setMessage(String message) {
        this.message = message
        return this
    }

    TopicMergeReport setUrl(String url) {
        this.url = url
        return this
    }
}
