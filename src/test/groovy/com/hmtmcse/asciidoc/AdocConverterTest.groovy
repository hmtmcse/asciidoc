package com.hmtmcse.asciidoc

import com.hmtmcse.common.util.TMUtil
import spock.lang.Shared
import spock.lang.Specification

class AdocConverterTest extends Specification {

    @Shared
    String testContentPath

    def setup() {
        testContentPath = TMUtil.testContentPath("asciidoc")
    }

    def "convert adoc file to html"() {
        given: "Setup Content Path"
        String path = testContentPath + "/bismillah.adoc"

        expect: "Will Get Adoc to HTML"
        String html = AdocConverter.instance().getHtmlFromFile(path)
        println(html)
        html != null && html.contains("Unit Test H2")
    }

}
