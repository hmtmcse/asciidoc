package com.hmtmcse.asciidoc

import com.hmtmcse.jtfutil.parser.JsonReadWrite
import com.hmtmcse.jtfutil.parser.YmlReader
import com.hmtmcse.texttoweb.Block
import com.hmtmcse.texttoweb.Descriptor
import com.hmtmcse.texttoweb.Layout
import com.hmtmcse.texttoweb.Outline
import com.hmtmcse.texttoweb.Seo
import com.hmtmcse.texttoweb.Settings
import com.hmtmcse.texttoweb.Tag
import com.hmtmcse.texttoweb.Tags
import com.hmtmcse.texttoweb.TextToWebConst
import com.hmtmcse.texttoweb.Topic
import spock.lang.Specification

class DescriptorTest extends Specification {

    def "Check Landing Page YML Descriptor"() {

        given:
        Descriptor descriptor = new Descriptor()
        descriptor.layout = new Layout(TextToWebConst.LANDING)
        descriptor.defaultTitle = "..:: HMTMCSE ::.."


        descriptor.staticMap = [
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
        descriptor.addBlock(TextToWebConst.SQUIRE_BLOCK, thumbBlock)

        Block listBlock = new Block("List Block", "#")

        Topic popularTutorials = new Topic("Popular Tutorials", "#")
        popularTutorials.addChild(new Topic("Java Basic", "#"))
        popularTutorials.addChild(new Topic("Java Advanced", "#"))
        popularTutorials.addChild(new Topic("Java Certifications", "#"))
        listBlock.addChild(popularTutorials)

        Topic upcomingTutorials = new Topic("Upcoming Tutorials", "#")
        upcomingTutorials.addChild(new Topic("Python Basic", "#"))
        upcomingTutorials.addChild(new Topic("React JS", "#"))
        upcomingTutorials.addChild(new Topic("PHP Laravel", "#"))
        listBlock.addChild(upcomingTutorials)

        Topic recentUploadedTutorial = new Topic("Recent Uploaded Tutorial", "#")
        recentUploadedTutorial.addChild(new Topic("Java Basic", "#"))
        recentUploadedTutorial.addChild(new Topic("Java Advanced", "#"))
        recentUploadedTutorial.addChild(new Topic("Java Certifications", "#"))
        listBlock.addChild(recentUploadedTutorial)

        descriptor.addBlock(TextToWebConst.LIST_BLOCK, listBlock)

        Settings settings = new Settings()
        Seo defaultSeo = new Seo("..:: HMTMCSE ::..")
        Tags head = new Tags(TextToWebConst.HEAD)
        head.addTag(new Tag().canonical("#"))
        defaultSeo.addTags(head)
        settings.defaultSeo = defaultSeo
        descriptor.settings = settings

        expect:
        YmlReader ymlReader = new YmlReader()
        String yml = ymlReader.klassToStringSkipNull(descriptor)
        print("\n\n")
        print(yml)

        JsonReadWrite jsonReadWrite = new JsonReadWrite()
        String json = jsonReadWrite.objectAsJsonStringPretty(descriptor)
        print("\n\n")
        print(json)
        yml != null
    }


    def "Check Details Page YML Descriptor"() {

        given:
        Descriptor descriptor = new Descriptor()
        descriptor.layout = new Layout(TextToWebConst.LANDING)
        descriptor.defaultTitle = "..:: HMTMCSE ::.."

        descriptor.staticMap = [
                "FACEBOOK_PAGE"      : "https://www.facebook.com/hmtmcsecom",
                "GITHUB_PAGE"        : "https://github.com/hmtmcse-com",
                "YOUTUBE_PAGE"       : "https://www.youtube.com/hmtmcse",
                "PRIVACY"            : "/static-page/privacy",
                "TERMS_AND_CONDITION": "/static-page/terms-and-condition",
                "ABOUT"              : "/static-page/about",
        ]




        Settings settings = new Settings()
        Seo defaultSeo = new Seo("..:: HMTMCSE ::..")
        Tags head = new Tags(TextToWebConst.HEAD)
        head.addTag(new Tag().canonical("#"))
        defaultSeo.addTags(head)
        settings.defaultSeo = defaultSeo
        descriptor.settings = settings

        descriptor.addTopic(new Topic("Bismillah", "/java/bismillah").setSeo(new Seo("Bismillah")))
        descriptor.addTopic(
                new Topic("Environment Setup", "/java/environment-Setup")
                        .addChild("JDK Setup", "/java/environment-Setup/jdk-setup")
                        .addChild("IDE Setup", "/java/environment-Setup/ide-setup")
        )
        descriptor.addTopic(new Topic("Input Output", "/java/input-output"))

        expect:
        YmlReader ymlReader = new YmlReader()
        String yml = ymlReader.klassToStringSkipNull(descriptor)
        print(yml)

        JsonReadWrite jsonReadWrite = new JsonReadWrite();
        String json = jsonReadWrite.objectAsJsonStringPretty(descriptor)
        print(json)
        yml != null
    }

    def "Check Outline Page YML Descriptor"() {

        given:
        Outline outline = new Outline()
        outline.layout = new Layout(TextToWebConst.OUTLINE)
        outline.defaultTitle = "..:: Outline ::.."

        Topic popularTutorials = new Topic("Java Basic", "#")
        popularTutorials.addChild(new Topic("Overview", "#"))
        popularTutorials.addChild(new Topic("Main Method", "#"))
        popularTutorials.addChild(new Topic("Input Out", "#"))
        outline.addTopic(popularTutorials)

        Topic upcomingTutorials = new Topic("Java Class", "#")
        upcomingTutorials.addChild(new Topic("Properties", "#"))
        upcomingTutorials.addChild(new Topic("Method", "#"))
        upcomingTutorials.addChild(new Topic("Method Overloading", "#"))
        outline.addTopic(upcomingTutorials)

        Topic recentUploadedTutorial = new Topic("Java Advance", "#")
        recentUploadedTutorial.addChild(new Topic("Generics", "#"))
        recentUploadedTutorial.addChild(new Topic("Collections", "#"))
        recentUploadedTutorial.addChild(new Topic("Thread", "#"))
        outline.addTopic(recentUploadedTutorial)

        Settings settings = new Settings()
        Seo defaultSeo = new Seo("..:: Outline ::..")
        Tags head = new Tags(TextToWebConst.HEAD)
        head.addTag(new Tag().canonical("#"))
        defaultSeo.addTags(head)
        settings.defaultSeo = defaultSeo
        outline.settings = settings

        outline.addRelatedTopic(new Topic("Spring Boot", "#"))
        outline.addRelatedTopic(new Topic("Groovy", "#"))
        outline.addRelatedTopic(new Topic("Grails 4", "#"))
        outline.addRelatedTopic(new Topic("Java FX", "#"))

        expect:
        YmlReader ymlReader = new YmlReader()
        String yml = ymlReader.klassToStringSkipNull(outline)
        print("\n\n")
        print(yml)

        JsonReadWrite jsonReadWrite = new JsonReadWrite()
        String json = jsonReadWrite.objectAsJsonStringPretty(outline)
        print(json)
        yml != null
    }

}
