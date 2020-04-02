package com.hmtmcse.asciidoc

import com.hmtmcse.te.taglib.HtmlTagHelper
import com.hmtmcse.texttoweb.data.ProcessRequest
import com.hmtmcse.texttoweb.processor.TextToWebProcessor
import spock.lang.Specification

class HtmlTagHelperTest extends Specification {

    def "concat url test"() {
        expect: "concat url test"
        println(HtmlTagHelper.concatUrl("/mia/vai/ase"))
        println(HtmlTagHelper.concatUrl("mia/vai/ase"))
        println(HtmlTagHelper.concatUrl("/"))
        println(HtmlTagHelper.concatUrl(null))
    }
}
