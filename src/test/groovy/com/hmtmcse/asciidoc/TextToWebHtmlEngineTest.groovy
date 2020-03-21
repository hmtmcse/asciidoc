package com.hmtmcse.asciidoc

import com.hmtmcse.te.TextToWebHtmlEngine
import spock.lang.Specification

class TextToWebHtmlEngineTest extends Specification {


    def "URL parsing Test"() {
        expect: "URL parsing Test"
        TextToWebHtmlEngine textToWeb = new TextToWebHtmlEngine()
        println(textToWeb.process("/"))
        println(textToWeb.process("/app-and-lib"))
        println(textToWeb.process("/app-and-lib/java"))
    }

}
