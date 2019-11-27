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



    // Add Remove
    Descriptor margeDescriptorTopics(Descriptor newDescriptor, Descriptor oldDescriptor){

        Map<String, Topic> oldTopicMap = [:]
        oldDescriptor.topics.each { Topic topic ->
            oldTopicMap.put(topic.url, topic)
        }

        Map<String, Topic> newTopicMap = [:]
        newDescriptor.topics.each { Topic newTopic ->
            if (oldTopicMap.get(newTopic.url)){
                oldTopicMap.remove(newTopic.url)
            }else{
                newTopicMap.put(newTopic.url, newTopic)
            }
        }

        oldDescriptor.topics.each { Topic topic ->
            if (oldTopicMap.get(topic.url)){
                oldTopicMap.get(topic.url).name = oldTopicMap.get(topic.url).name + " - Deleted"
            }
        }


        newTopicMap.each { String key, Topic newTopic ->
            newTopic.name = newTopic.name + " - Added"
            oldDescriptor.topics.add(newTopic)
        }


        return oldDescriptor
    }


    Descriptor margeDescriptorTopics(String descriptorFile, Descriptor newDescriptor) {
        if (!JavaNio.isExist(descriptorFile)) {
            return newDescriptor
        }

        Descriptor currentDescriptor = null
        try {
            currentDescriptor = loadYmlFromFile(descriptorFile)
            if (!currentDescriptor) {
                return newDescriptor
            }
        } catch (Exception e) {
            return newDescriptor
        }
        return margeDescriptorTopics(newDescriptor, currentDescriptor)
    }


    void topicsRootProcess(FileInfo rootDir) {
        if (!rootDir) {
            return
        }
        Map<String, Topic> topicMap = [:]
        Descriptor descriptor = DescriptorSample.getTopicsDescriptor(makeHumReadableWithoutExt(rootDir.name))
        Topic topic
        String url, humReadableName, topicsRootPath = rootDir.absolutePath

        // JAVA ....
        rootDir.subDirectories.each { FileInfo topicsDir ->
            if (topicsDir && topicsDir.name && (topicsDir.name.equals(this.ymlDescriptorFileName()) || topicsDir.name.equals(this.jsonDescriptorFileName()) || topicsDir.name.equals(this.ymltOutlineFileName()))){
                return
            }
            url = getURL(topicsRootPath) + "/" + topicsDir.name
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

        String descriptorYml = JavaNio.concatPathString(topicsRootPath, ymlDescriptorFileName())
        descriptor = margeDescriptorTopics(descriptorYml, descriptor)

        println(exportToYmlFile(topicsRootPath, descriptor))
    }

    // Grails Details and outline
    void topicRootProcess(FileInfo topicsDir) {
        OutlineAndDescriptor outlineAndDescriptor = new OutlineAndDescriptor(makeHumReadableWithoutExt(topicsDir.name))
        outlineAndDescriptor = outlineAndDescriptorPrepare(topicsDir.subDirectories, outlineAndDescriptor)

        String topicsRootPath = topicsDir.absolutePath
        String outlineDescriptorYml = JavaNio.concatPathString(topicsRootPath, ymltOutlineFileName())
        if (outlineAndDescriptor.outlineDescriptor){
            outlineAndDescriptor.outlineDescriptor = margeDescriptorTopics(outlineDescriptorYml, outlineAndDescriptor.outlineDescriptor)
        }
        exportToOutlineYmlFile(topicsRootPath, outlineAndDescriptor.outlineDescriptor)

        String descriptorYml = JavaNio.concatPathString(topicsRootPath, ymlDescriptorFileName())
        if (outlineAndDescriptor.detailsDescriptor){
            outlineAndDescriptor.detailsDescriptor = margeDescriptorTopics(descriptorYml, outlineAndDescriptor.detailsDescriptor)
        }
        exportToYmlFile(topicsRootPath, outlineAndDescriptor.detailsDescriptor)
    }

    OutlineAndDescriptor outlineAndDescriptorPrepare(List<FileInfo> list, OutlineAndDescriptor outlineAndDescriptor, Map index = [outline: 0, details: null]){
        String url, humReadableName
        list.each { FileInfo topicDir ->
            url = getURL(topicDir.absolutePath)
            humReadableName = makeHumReadableWithoutExt(topicDir.name)
            if (topicDir.isDirectory && topicDir.subDirectories) {
                outlineAndDescriptor.addTopicParent(humReadableName)
                outlineAndDescriptorPrepare(topicDir.subDirectories, outlineAndDescriptor, [outline: outlineAndDescriptor.outlineLastIndex(), details: outlineAndDescriptor.detailsLastIndex()])
            }else{
                outlineAndDescriptor.addToOutlineByIndex(index.outline, new Topic(humReadableName, url))
                outlineAndDescriptor.addToDetailsByIndex(index.details, new Topic(humReadableName, url))
            }
        }
        return outlineAndDescriptor
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
