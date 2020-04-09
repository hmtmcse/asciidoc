package com.hmtmcse.texttoweb.sample

import com.hmtmcse.texttoweb.Block
import com.hmtmcse.texttoweb.Descriptor
import com.hmtmcse.texttoweb.Layout
import com.hmtmcse.texttoweb.Seo
import com.hmtmcse.texttoweb.Settings
import com.hmtmcse.texttoweb.Tag
import com.hmtmcse.texttoweb.Tags
import com.hmtmcse.texttoweb.TextToWebConst
import com.hmtmcse.texttoweb.Topic

class DescriptorSample {

    public static Descriptor getLandingDescriptor(){

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

        Block squireBlock = new Block("Squire Block", "#")
        squireBlock.addChild(
                new Topic("Video Tutorial", "#")
                        .setIcon("fas fa-desktop fa-2x")
                        .setSummery("Please Follow this link for see all video tutorial from YouTube. Here your will get lots of tutorial.")
        )
        squireBlock.addChild(
                new Topic("বাংলা টিউটোরিয়াল", "#")
                        .setIcon("fas fa-file fa-2x")
                        .setSummery("সাইটের সব বাংলা টিউটোরিয়াল  দেখতে এই লিংক এ ক্লিক করুন, YouTube থেকে দেখতে পাবেন।")
        )
        descriptor.addBlock(TextToWebConst.SQUIRE_BLOCK, squireBlock)

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

        return descriptor
    }

    public static Descriptor getTopicsDescriptor(String title){
        Descriptor descriptor = new Descriptor()
        descriptor.layout = new Layout(TextToWebConst.TOPICS)
        descriptor.defaultTitle = title
        return descriptor
    }

    public static Descriptor getOutlineDescriptor(String title){
        Descriptor descriptor = new Descriptor()
        descriptor.layout = new Layout(TextToWebConst.OUTLINE)
        descriptor.defaultTitle = title
        return descriptor
    }

    public static Descriptor getDetailsDescriptor(String title){
        Descriptor descriptor = new Descriptor()
        descriptor.layout = new Layout(TextToWebConst.DETAILS)
        descriptor.defaultTitle = title
        return descriptor
    }

}
