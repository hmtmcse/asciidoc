package com.hmtmcse.texttoweb.data

import com.hmtmcse.te.data.TextToWebEngineConfig

class ProcessRequest extends TextToWebEngineConfig {
    public ProcessTask task = ProcessTask.REPORT
    public Map<String, TopicMergeReport> mergeData = [:]

    ProcessRequest setTask(ProcessTask task) {
        this.task = task
        return this
    }

    ProcessRequest addMergeData(TopicMergeReport topicMergeReport) {
        mergeData.put(topicMergeReport.topicKey, topicMergeReport)
        return this
    }
}
