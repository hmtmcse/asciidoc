package com.hmtmcse.texttoweb.processor

import com.hmtmcse.jtfutil.io.FileInfo
import com.hmtmcse.jtfutil.io.FileUtil
import com.hmtmcse.jtfutil.io.JavaNio
import com.hmtmcse.shellutil.console.menu.OptionValues
import com.hmtmcse.texttoweb.Config
import com.hmtmcse.texttoweb.Descriptor
import com.hmtmcse.texttoweb.TextToWebConst
import com.hmtmcse.texttoweb.Topic
import com.hmtmcse.texttoweb.common.ConfigLoader
import com.hmtmcse.texttoweb.data.OutlineAndDescriptor
import com.hmtmcse.texttoweb.data.PathData
import com.hmtmcse.texttoweb.model.CommandProcessor
import com.hmtmcse.texttoweb.sample.DescriptorSample

class GenerateProcessor implements CommandProcessor {

    private Config config


    @Override
    void process(OptionValues optionValues) {
        String command = optionValues.valueAsString(TextToWebConst.DESCRIPTOR)
        config = ConfigLoader.getConfig(optionValues)
        switch (command) {
            case TextToWebConst.LANDING:
                landing()
                break
            case TextToWebConst.TOPICS:
                topics()
                break
            case TextToWebConst.OUTLINE:
                outline()
                break
            case TextToWebConst.DETAILS:
                details()
                break
            default:
                println("-------------")
        }
    }





    public String getRelativePath(String absolutePath){
        if (absolutePath){
            return absolutePath.replace(config.source, "")
        }
        return absolutePath
    }

    public String getURL(String absolutePath) {
        String path = getRelativePath(absolutePath)
        if (path) {
            path = removeAdocExtension(path)
            path = pathToURL(path)
            path = config.urlStartWith + path
        }
        return path
    }


    private void prepareProjectData(List<FileInfo> list) {
        PathData pathData = null
        if (list) {
            list.each { FileInfo rootDir ->
                if (rootDir.isDirectory && rootDir.subDirectories) {
                    topicsRootProcess(rootDir)
                }
            }
            if (!pathData) {
                pathData = new PathData(config.source, getRelativePath(config.source))
            }
            projectRootProcess(pathData)
        }
    }


    void manipulateDescriptor(){
        List<FileInfo> list = FileUtil.listAll(config.source)
        prepareProjectData(list)
    }


    void projectRootProcess(PathData pathData){
        String path = JavaNio.concatPath(pathData.absolutePath, ymlDescriptorFileName())
        if (!JavaNio.isExist(path)){
            exportToYmlFile(pathData.absolutePath, DescriptorSample.landingDescriptor)
        }
    }


    Descriptor margeDescriptor(Map<String, Topic> topicMap, Descriptor descriptor){

        return descriptor
    }

    void topicsRootProcess(FileInfo rootDir) {
        if (!rootDir) {
            return
        }
        Map<String, Topic> topicMap = [:]
        Descriptor descriptor = DescriptorSample.getTopicsDescriptor(makeHumReadableWithoutExt(rootDir.name))
        Topic topic
        String url, humReadableName

        // JAVA ....
        rootDir.subDirectories.each { FileInfo topicsDir ->
            url = getURL(topicsDir.absolutePath)
            humReadableName = makeHumReadableWithoutExt(topicsDir.name)
//            println("\nTopics: ${humReadableName} ${topicsDir.name}")
//            println("URL: ${url}")

            topic = descriptor.topic(makeHumReadableWithoutExt(topicsDir.name), url, "For more details about ${humReadableName} click here. It will bring you to details of this Topic.")
            topicMap.put(url, topic)
            descriptor.addTopic(topic)

            // Grails
            if (topicsDir.isDirectory && topicsDir.subDirectories) {
                topicRootProcess(topicsDir)
            }
        }
        println(exportToYmlText(descriptor))
    }

    // Grails Details and outline
    void topicRootProcess(FileInfo topicsDir) {

        Map<String, Topic> outlineTopicMap = [:]
        Map<String, Topic> detailsTopicMap = [:]
        Descriptor outlineDescriptor = DescriptorSample.getTopicsDescriptor(makeHumReadableWithoutExt(topicsDir.name))
        Descriptor detailsDescriptor = DescriptorSample.getTopicsDescriptor(makeHumReadableWithoutExt(topicsDir.name))
        Topic outlineTopic, detailsTopic
        String outlineURL, outlineHumReadableName, detailsURL, detailsHumReadableName, url, humReadableName

        OutlineAndDescriptor outlineAndDescriptor = new OutlineAndDescriptor(makeHumReadableWithoutExt(topicsDir.name))

        topicsDir.subDirectories.each { FileInfo topicDir ->

            url = getURL(topicsDir.absolutePath)
            humReadableName = makeHumReadableWithoutExt(topicsDir.name)

            println("  Topic: ${humReadableName} ${topicDir.name}")
            println("  URL: ${url}\n")
            if (topicDir.isDirectory && topicDir.subDirectories) {
//                topicRootProcess(topicsDir.subDirectories)
            }

        }
    }

    OutlineAndDescriptor outlineAndDescriptor(List<FileInfo> list, OutlineAndDescriptor outlineAndDescriptor){

    }

    void searchIndex(List<FileInfo> list){

    }

    void siteMap(List<FileInfo> list){

    }

    void exportProject(List<FileInfo> list){

    }

    void landing() {
        manipulateDescriptor()

//        exportToJsonNdYmlFile(config.source, DescriptorSample.landingDescriptor)
    }

    void topics() {}

    void outline() {}

    void details() {}

}
