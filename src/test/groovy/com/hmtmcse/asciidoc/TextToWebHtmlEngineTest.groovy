package com.hmtmcse.asciidoc

import com.hmtmcse.te.TextToWebHtmlEngine
import spock.lang.Specification

class TextToWebHtmlEngineTest extends Specification {


    def "URL parsing Test"() {
        expect: "URL parsing Test"
        TextToWebHtmlEngine textToWeb = new TextToWebHtmlEngine()
        println(textToWeb.getContentByURL("/"))
        println(textToWeb.getContentByURL("/app-and-lib"))
        println(textToWeb.getContentByURL("/app-and-lib/java"))
    }

}
