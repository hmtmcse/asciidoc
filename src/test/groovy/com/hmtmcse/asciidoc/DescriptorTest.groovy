package com.hmtmcse.asciidoc

import com.hmtmcse.jtfutil.parser.YmlReader
import com.hmtmcse.texttoweb.Descriptor
import com.hmtmcse.texttoweb.Layout
import com.hmtmcse.texttoweb.TextToWebConst
import spock.lang.Specification

class DescriptorTest extends Specification {

    def "Create Landing Page YML Descriptor"() {
        given:
        Descriptor descriptor = new Descriptor()
        descriptor.layout = new Layout(TextToWebConst.LANDING)
        descriptor.defaultTitle = "..:: HMTMCSE ::.."


        descriptor.findReplace = [
                "FACEBOOK_PAGE"      : "https://www.facebook.com/hmtmcsecom",
                "GITHUB_PAGE"        : "https://github.com/hmtmcse-com",
                "YOUTUBE_PAGE"       : "https://www.youtube.com/hmtmcse",
                "PRIVACY"            : "/static-page/privacy",
                "TERMS_AND_CONDITION": "/static-page/terms-and-condition",
                "ABOUT"              : "/static-page/about",
        ]

        expect:
        YmlReader ymlReader = new YmlReader()
        String yml = ymlReader.klassToString(descriptor)
        print(yml)
        yml != null

    }

}
