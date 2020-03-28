package com.hmtmcse.asciidoc

import com.hmtmcse.parser4java.YamlProcessor
import com.hmtmcse.texttoweb.data.ProcessRequest
import com.hmtmcse.texttoweb.data.ProcessTask
import com.hmtmcse.texttoweb.data.TopicMergeReport
import com.hmtmcse.texttoweb.processor.TextToWebProcessor
import spock.lang.Specification

class TextToWebProcessorTest extends Specification {

    def "Test Main Method"() {
        expect: "Test Main Method"
        TextToWebProcessor textToWebProcessor = new TextToWebProcessor(new ProcessRequest())
        textToWebProcessor.test()
        println("Test")
    }

    def "Test Descriptor Report"() {
        expect: "Test Descriptor Report"
        TextToWebProcessor textToWebProcessor = new TextToWebProcessor(new ProcessRequest())
        println(YamlProcessor.instance().klassToString(textToWebProcessor.manipulateDescriptorOutline()))
    }

    def "Test Descriptor Generate"() {
        given:
        TopicMergeReport topicMergeReport = new TopicMergeReport()
        topicMergeReport.isMerge = true
        topicMergeReport.name = "Other"
        topicMergeReport.topicKey = "/php/laravel/others"

        ProcessRequest processRequest = new ProcessRequest().setTask(ProcessTask.MERGE)
        processRequest.addMergeData(topicMergeReport)

        expect: "Test Descriptor Generate"
        TextToWebProcessor textToWebProcessor = new TextToWebProcessor(processRequest)
        println(YamlProcessor.instance().klassToString(textToWebProcessor.manipulateDescriptorOutline()))
    }

}
