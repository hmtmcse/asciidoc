package com.hmtmcse.asciidoc

import com.hmtmcse.texttoweb.console.TextToWebMenu
import com.hmtmcse.texttoweb.data.ProcessRequest
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
        println(textToWebProcessor.manipulateDescriptorOutline())
    }

}
