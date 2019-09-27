package com.hmtmcse.asciidoc

import com.hmtmcse.jtfutil.parser.YmlReader
import com.hmtmcse.texttoweb.Block
import com.hmtmcse.texttoweb.Descriptor
import com.hmtmcse.texttoweb.Layout
import com.hmtmcse.texttoweb.Seo
import com.hmtmcse.texttoweb.Settings
import com.hmtmcse.texttoweb.Tag
import com.hmtmcse.texttoweb.Tags
import com.hmtmcse.texttoweb.TextToWebConst
import com.hmtmcse.texttoweb.Topic
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

        Block thumbBlock = new Block("Thumb Block", "#")
        thumbBlock.addChild(
                new Topic("Video Tutorial", "#")
                        .setIcon("fas fa-desktop fa-2x")
                        .setSummery("Please Follow this link for see all video tutorial from YouTube. Here your will get lots of tutorial.")
        )
        thumbBlock.addChild(
                new Topic("বাংলা টিউটোরিয়াল", "#")
                        .setIcon("fas fa-file fa-2x")
                        .setSummery("সাইটের সব বাংলা টিউটোরিয়াল  দেখতে এই লিংক এ ক্লিক করুন, YouTube থেকে দেখতে পাবেন।")
        )
        descriptor.addBlock("thumbBlock", thumbBlock)

        Block listBlock = new Block("List Block", "#")
        Block popularTutorials = new Block("Popular Tutorials", "#")
        popularTutorials.addChild(new Topic("Java Basic", "#"))
        popularTutorials.addChild(new Topic("Java Advanced", "#"))
        popularTutorials.addChild(new Topic("Java Certifications", "#"))
        listBlock.addBlock("popularTutorials", popularTutorials)

        Block upcomingTutorials = new Block("Upcoming Tutorials", "#")
        upcomingTutorials.addChild(new Topic("Python Basic", "#"))
        upcomingTutorials.addChild(new Topic("React JS", "#"))
        upcomingTutorials.addChild(new Topic("PHP Laravel", "#"))
        listBlock.addBlock("upcomingTutorials", upcomingTutorials)

        Block recentUploadedTutorial = new Block("Recent Uploaded Tutorial", "#")
        recentUploadedTutorial.addChild(new Topic("Java Basic", "#"))
        recentUploadedTutorial.addChild(new Topic("Java Advanced", "#"))
        recentUploadedTutorial.addChild(new Topic("Java Certifications", "#"))
        listBlock.addBlock("recentUploadedTutorial", recentUploadedTutorial)

        descriptor.addBlock("listBlock", listBlock)

        Settings settings = new Settings()
        Seo defaultSeo = new Seo("..:: HMTMCSE ::..")
        Tags head = new Tags(TextToWebConst.HEAD)
        head.addTag(new Tag().canonical("#"))
        defaultSeo.addTags(head)
        settings.defaultSeo = defaultSeo
        descriptor.settings = settings

        expect:
        YmlReader ymlReader = new YmlReader()
        String yml = ymlReader.klassToString(descriptor)
        print(yml)
        yml != null
    }

}
