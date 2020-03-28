package com.hmtmcse.texttoweb.data

import com.hmtmcse.te.data.TextToWebEngineConfig

class ProcessRequest extends TextToWebEngineConfig {
    public ProcessTask task = ProcessTask.REPORT
    public Map<String, TopicMergeReport> mergeData
}
