package com.hmtmcse.asciidoc

import com.hmtmcse.parser4java.YamlProcessor
import com.hmtmcse.te.data.HtmlExportType
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
        ProcessRequest processRequest = new ProcessRequest().setTask(ProcessTask.MERGE)

        TopicMergeReport topicMergeReport = new TopicMergeReport()
//        topicMergeReport.isMerge = true
//        topicMergeReport.name = "Extended"
//        topicMergeReport.topicKey = "##/java/grails/domain/extension"
//        processRequest.addMergeData(topicMergeReport)

        topicMergeReport = new TopicMergeReport()
        topicMergeReport.isMerge = true
        topicMergeReport.name = "Book"
        topicMergeReport.topicKey = "##/php/laravel/book"
        processRequest.addMergeData(topicMergeReport)

        expect: "Test Descriptor Generate"
        TextToWebProcessor textToWebProcessor = new TextToWebProcessor(processRequest)
        println(YamlProcessor.instance().klassToString(textToWebProcessor.manipulateDescriptorOutline()))
    }

    def "Test Export to html"() {
        given:
        ProcessRequest processRequest = new ProcessRequest()
//        processRequest.htmlExportType = HtmlExportType.OFFLINE
//        processRequest.exportFileExtension = "html"

        expect: "Test Export to html"
        TextToWebProcessor textToWebProcessor = new TextToWebProcessor(processRequest)
//        println(YamlProcessor.instance().klassToString(textToWebProcessor.manipulateDescriptorOutline()))
        textToWebProcessor.exportToHtml()
    }

}
