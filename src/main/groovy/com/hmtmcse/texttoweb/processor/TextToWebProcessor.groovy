package com.hmtmcse.texttoweb.processor

import com.hmtmcse.common.AsciiDocConstant
import com.hmtmcse.common.AsciiDocException
import com.hmtmcse.common.AsciiDocUtil
import com.hmtmcse.fileutil.data.FDInfo
import com.hmtmcse.fileutil.data.FDListingFilter
import com.hmtmcse.fileutil.data.FileDirectoryListing
import com.hmtmcse.fileutil.fd.FDUtil
import com.hmtmcse.fileutil.fd.FileDirectory
import com.hmtmcse.fileutil.text.TextFile
import com.hmtmcse.fm.TwFileUtil
import com.hmtmcse.shellutil.console.menu.OptionValues
import com.hmtmcse.te.TextToWebHtmlEngine
import com.hmtmcse.texttoweb.Config
import com.hmtmcse.texttoweb.Descriptor
import com.hmtmcse.texttoweb.Topic
import com.hmtmcse.texttoweb.common.ConfigLoader
import com.hmtmcse.texttoweb.data.*
import com.hmtmcse.texttoweb.model.CommandProcessor
import com.hmtmcse.texttoweb.sample.DescriptorSample

import java.util.concurrent.CopyOnWriteArrayList

class TextToWebProcessor implements CommandProcessor {

    private FileDirectory fileDirectory
    private Config config
    ProcessRequest processRequest
    private Map<String, TopicMergeReport> reports = [:]
    private Boolean isDescriptorUpdated = false
    private TextToWebHtmlEngine textToWebHtmlEngine
    private TextFile textFile
    private Map<String, Boolean> trackDescriptorPage = [:]
    private ResourceProcessor resourceProcessor


    public TextToWebProcessor(ProcessRequest processRequest) {
        fileDirectory = new FileDirectory()
        config = ConfigLoader.getConfig()
        this.processRequest = processRequest
        textToWebHtmlEngine = new TextToWebHtmlEngine()
        textFile = new TextFile()
        trackDescriptorPage = [:]
        resourceProcessor = new ResourceProcessor(config)
        init(processRequest)
    }


    @Override
    void process(OptionValues optionValues) {}

    public String getRelativePath(String absolutePath) {
        if (absolutePath) {
            return absolutePath.replace(config.source, "")
        }
        return absolutePath
    }

    private void addReport(TopicMergeReport report) {
        reports.put(report.topicKey, report)
    }

    private String getSourcePath() throws AsciiDocException {
        if (!fileDirectory.isExist(config.source)) {
            throw new AsciiDocException("Source Not found! Please specify source at config.yml")
        }
        return config.source
    }

    private List<FileDirectoryListing> getSourceList() throws AsciiDocException {
        FDListingFilter filter = new FDListingFilter().details()
        return fileDirectory.listDirRecursively(getSourcePath(), filter)
    }

    private TopicMergeReport allowedByUser(String topicKey) {
        if (topicKey && processRequest && processRequest.mergeData && processRequest.mergeData.get(topicKey)) {
            return processRequest.mergeData.get(topicKey)
        }
        return null
    }

    private Boolean isAllowedByUser(String topicKey) {
        TopicMergeReport topicMergeReport = allowedByUser(topicKey)
        if (topicMergeReport) {
            return topicMergeReport.isMerge
        }
        return true
    }

    private void bismillahDescriptorProcess() {
        PathData pathData = new PathData(config.source, getRelativePath(config.source))
        String path = FDUtil.concatPath(pathData.absolutePath, ymlDescriptorFileName())
        String bismillahTopicKey = "this-is-root-topic"
        if (processRequest.task.equals(ProcessTask.REPORT)) {
            if (!fileDirectory.isExist(path)) {
                addReport(new TopicMergeReport("Main Descriptor Not Exist", bismillahTopicKey).setIsEditable(false).setRelativePath("/"))
            }
            return
        }

        if (!fileDirectory.isExist(path) && isAllowedByUser(bismillahTopicKey)) {
            exportToYmlFile(pathData.absolutePath, DescriptorSample.landingDescriptor)
        }
    }

    private Boolean isSkipFile(FDInfo topicsDir, Boolean isNotDescriptor = false) {
        if (AsciiDocUtil.isSkipFile(topicsDir)) {
            return true
        }

        if (!isNotDescriptor && topicsDir && topicsDir.name &&
                (topicsDir.name.equals(this.ymlDescriptorFileName()) ||
                        topicsDir.name.equals(this.jsonDescriptorFileName()) ||
                        topicsDir.name.equals(this.jsonOutlineFileName()) ||
                        topicsDir.name.equals(this.ymltOutlineFileName()))) {
            return true
        }
        return false
    }

    private String getURL(String absolutePath) {
        String path = getRelativePath(absolutePath)
        if (path) {
            path = removeAdocExtension(path)
            path = pathToURL(path)
        }
        return path
    }

    private String getTopicKey(Topic topic) {
        if (topic.tracker) {
            return topic.tracker
        } else if (topic.url && !topic.url.equals("#")) {
            return topic.url
        }
        return topic.name
    }

    private void addTopicReport(Topic topic) {
        if (!processRequest.task.equals(ProcessTask.REPORT)) {
            return
        }
        TopicMergeReport topicMergeReport = new TopicMergeReport()
        topicMergeReport.topicKey = getTopicKey(topic)
        topicMergeReport.name = topic.name
        topicMergeReport.relativePath = topic.url == "#" ? "" : topic.url
        addReport(topicMergeReport)
    }

    private TopicMergeReport getTopicReport(Topic topic) {
        String key = getTopicKey(topic)
        return allowedByUser(key)
    }

    private List<Topic> margeTopicDescriptor(Map currentTopicMap, List<Topic> previousTopic) {
        previousTopic = new CopyOnWriteArrayList<>(previousTopic)
        previousTopic.eachWithIndex { Topic topic, Integer index ->
            if (topic.childs && currentTopicMap.get(topic.tracker)) {
                Map topicMap = currentTopicMap.get(topic.tracker).topicMap
                topic.childs = margeTopicDescriptor(topicMap, topic.childs)
                currentTopicMap.remove(topic.tracker)
            } else if (currentTopicMap.get(topic.url)) {
                currentTopicMap.remove(topic.url)
            } else {
                if (processRequest.task.equals(ProcessTask.MERGE)) {
                    TopicMergeReport topicMergeReport = getTopicReport(previousTopic.get(index))
                    if (topicMergeReport && topicMergeReport.isMerge) {
                        isDescriptorUpdated = true
                        previousTopic.remove(index)
                        return
                    }
                }
                if (topic.childs) {
                    isDescriptorUpdated = true
                    previousTopic.get(index).name += " - Deleted With Child"
                } else {
                    isDescriptorUpdated = true
                    previousTopic.get(index).name += " - Deleted"
                }
                addTopicReport(previousTopic.get(index))
            }
        }

        Topic temp
        String name
        currentTopicMap.each { key, newTopic ->
            name = null
            if (processRequest.task.equals(ProcessTask.MERGE)) {
                TopicMergeReport topicMergeReport
                if (newTopic instanceof Topic) {
                    topicMergeReport = getTopicReport(newTopic)
                } else if (newTopic instanceof Map) {
                    topicMergeReport = getTopicReport(newTopic.topic)
                }
                if (topicMergeReport && !topicMergeReport.isMerge) {
                    return
                } else if (topicMergeReport && topicMergeReport.name) {
                    name = topicMergeReport.name
                }
            }

            if (newTopic instanceof Topic) {
                newTopic.name = newTopic.name + " - Added"
                newTopic.name = name ?: newTopic.name
                previousTopic.add(newTopic)
                addTopicReport(newTopic)
                isDescriptorUpdated = true
            } else if (newTopic instanceof Map) {
                temp = newTopic.topic
                temp.name += " - Added with Child"
                temp.name = name ?: temp.name
                previousTopic.add(temp)
                addTopicReport(temp)
                isDescriptorUpdated = true
            }
        }
        return previousTopic
    }

    private Map topicDescriptorListToMap(List<Topic> topics) {
        Map topicMap = [:]
        if (!topics) {
            return topicMap
        }
        topics.each { Topic topic ->
            if (topic.childs) {
                topicMap.put(topic.tracker, [topic: topic, topicMap: topicDescriptorListToMap(topic.childs)])
            } else {
                topicMap.put(topic.url, topic)
            }
        }
        return topicMap
    }

    private Descriptor preprocessAndMargeDescriptorTopics(Descriptor currentDescriptor, Descriptor previousDescriptor) {
        Map currentTopicMap = topicDescriptorListToMap(currentDescriptor.topics)
        isDescriptorUpdated = false
        if (!previousDescriptor.topics) {
            previousDescriptor.topics = new ArrayList<>()
        }
        previousDescriptor.topics = margeTopicDescriptor(currentTopicMap, previousDescriptor.topics)
        previousDescriptor.updateStatus(isDescriptorUpdated)
        return previousDescriptor
    }

    private Descriptor processAndMargeDescriptorTopics(String descriptorFile, Descriptor newDescriptor) {
        if (!fileDirectory.isExist(descriptorFile)) {
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
        return preprocessAndMargeDescriptorTopics(newDescriptor, currentDescriptor)
    }

    OutlineAndDescriptor prepareOutlineAndDescriptor(List<FileDirectoryListing> subDirectories, OutlineAndDescriptor outlineAndDescriptor, Map index = [outline: 0, details: null]) {
        String url, humReadableName
        subDirectories.each { FileDirectoryListing fileDirectoryListing ->
            FDInfo topicDir = fileDirectoryListing.fileDirectoryInfo
            if (isSkipFile(topicDir)) {
                return
            }
            url = getURL(topicDir.absolutePath)
            humReadableName = makeHumReadableWithoutExt(topicDir.name)
            if (topicDir.isDirectory && fileDirectoryListing.subDirectories) {
                OutlineAndDescriptor _outlineAndDescriptor = new OutlineAndDescriptor(humReadableName, "#", "##" + url)
                _outlineAndDescriptor = prepareOutlineAndDescriptor(fileDirectoryListing.subDirectories, _outlineAndDescriptor, [outline: 0, details: null])

                Topic detailsDescriptorTopic = new Topic(humReadableName, "#").setTracker("##" + url)
                _outlineAndDescriptor.detailsDescriptor.topics.each {
                    detailsDescriptorTopic.addChild(it)
                }
                outlineAndDescriptor.addToDetailsByIndex(index.details, detailsDescriptorTopic)


                _outlineAndDescriptor.outlineDescriptor.topics.each {
                    outlineAndDescriptor.addToOutlineByIndex(index.outline, it)
                }

            } else {
                outlineAndDescriptor.addToOutlineByIndex(index.outline, new Topic(humReadableName, url))
                outlineAndDescriptor.addToDetailsByIndex(index.details, new Topic(humReadableName, url))
            }
        }
        return outlineAndDescriptor
    }

    private void processTopic(FileDirectoryListing fileDirectoryListing) throws AsciiDocException {
        if (!fileDirectoryListing) {
            return
        }

        FDInfo fileDirectoryInfo = fileDirectoryListing.fileDirectoryInfo
        Map<String, Topic> topicMap = [:]
        Descriptor descriptor = DescriptorSample.getTopicsDescriptor(makeHumReadableWithoutExt(fileDirectoryInfo.name))
        Topic topic
        String url, humReadableName, topicsRootPath = fileDirectoryInfo.absolutePath

        // JAVA ....
        fileDirectoryListing.subDirectories.each { FileDirectoryListing fileDirectoryListingInternal ->
            FDInfo topicsDir = fileDirectoryListingInternal.fileDirectoryInfo
            if (isSkipFile(topicsDir)) {
                return
            }
            url = getURL(topicsRootPath) + "/" + topicsDir.name
            humReadableName = makeHumReadableWithoutExt(topicsDir.name)

            topic = descriptor.topic(makeHumReadableWithoutExt(topicsDir.name), url, "For more details about ${humReadableName} click here. It will bring you to details of this Topic.")
            topicMap.put(url, topic)
            descriptor.addTopic(topic)

            // Grails
            if (topicsDir.isDirectory && fileDirectoryListingInternal.subDirectories) {
                processSubTopicAndDetails(fileDirectoryListingInternal)
            }
        }

        String descriptorYml = FDUtil.concatPath(topicsRootPath, ymlDescriptorFileName())
        descriptor = processAndMargeDescriptorTopics(descriptorYml, descriptor)
        println("Export Process Topic: " + exportToYmlFile(topicsRootPath, descriptor))
    }

    private void processSubTopicAndDetails(FileDirectoryListing fileDirectoryListingInternal) {
        FDInfo topicsDir = fileDirectoryListingInternal.fileDirectoryInfo
        String url = getURL(topicsDir.absolutePath)
        OutlineAndDescriptor outlineAndDescriptor = new OutlineAndDescriptor(makeHumReadableWithoutExt(topicsDir.name), "#", "##" + url)
        outlineAndDescriptor = prepareOutlineAndDescriptor(fileDirectoryListingInternal.subDirectories, outlineAndDescriptor)

        String topicsRootPath = topicsDir.absolutePath
        String outlineDescriptorYml = FDUtil.concatPath(topicsRootPath, ymltOutlineFileName())
        if (outlineAndDescriptor.outlineDescriptor) {
            outlineAndDescriptor.outlineDescriptor = processAndMargeDescriptorTopics(outlineDescriptorYml, outlineAndDescriptor.outlineDescriptor)
        }
        exportToOutlineYmlFile(topicsRootPath, outlineAndDescriptor.outlineDescriptor)

        String descriptorYml = FDUtil.concatPath(topicsRootPath, ymlDescriptorFileName())
        if (outlineAndDescriptor.detailsDescriptor) {
            outlineAndDescriptor.detailsDescriptor = processAndMargeDescriptorTopics(descriptorYml, outlineAndDescriptor.detailsDescriptor)
        }
        exportToYmlFile(topicsRootPath, outlineAndDescriptor.detailsDescriptor)
    }

    private List<FileDirectoryListing> getTopicList() throws AsciiDocException {
        List<FileDirectoryListing> topics = getSourceList()
        if (!topics) {
            throw new AsciiDocException("Topics Not available")
        }
        return topics
    }


    private UrlEligibleForExport urlEligibleForExport(String url) {
        UrlEligibleForExport urlEligibleForExport = new UrlEligibleForExport()
        String relativePath = TwFileUtil.trimAndUrlToPath(url)
        String sourceRelativePath = "${relativePath}.${processRequest.docFileExtension}".toString()
        String sourceDoc = FDUtil.concatPath(config.source, sourceRelativePath)

        if (!fileDirectory.isExist(sourceDoc)) {
            return urlEligibleForExport
        }

        FDInfo fdInfo = fileDirectory.getDetailsInfo(sourceDoc, true)
        if (fdInfo.isDirectory) {
            urlEligibleForExport.name = getBismillahFileName()
        }

        String outputDoc = FDUtil.concatPath(config.out, "${relativePath}.${processRequest.getExportFileExtensionByNullCheck()}".toString())
        if (resourceProcessor.isModifiedDocFile(sourceRelativePath, fdInfo)) {
            return urlEligibleForExport.setIsEligible(true)
        } else if (!fileDirectory.isExist(outputDoc)) {
            return urlEligibleForExport.setIsEligible(true)
        }
        return urlEligibleForExport
    }

    private String urlToOutputDocFile(String url, String name = "", String out = config.out) {
        String relativePath = TwFileUtil.trimAndUrlToPath(url)
        return FDUtil.concatPath(out, "${relativePath}${name}${processRequest.getExportFileExtensionByNullCheck()}".toString())
    }

    private Boolean exportUrlToHtml(String url, String name = "") {
        String errorFrom = "Export Url to Html Error:"
        try {
            SearchProcessor searchIndexProcessor = new SearchProcessor()
            String outputDoc = urlToOutputDocFile(url, name)
            if (!fileDirectory.removeIfExist(outputDoc)) {
                println("${errorFrom} Unable to remove existing output file: ${outputDoc}")
                return
            }
            String html = textToWebHtmlEngine.getContentByURL(url, processRequest)
            if (html) {
                File outputDocFile = new File(outputDoc)
                fileDirectory.createDirectoriesIfNotExist(outputDocFile.getParentFile().absolutePath)
                html = searchIndexProcessor.process(url, html)
                return textFile.stringToFile(outputDoc, html)
            } else {
                println("${errorFrom} HTML Not found.")
            }
        } catch (Exception e) {
            println("${errorFrom} ${e.getMessage()}")
        }
    }


    private void processTopicToHtml(List<Topic> topics) {
        if (topics) {
            topics.each { Topic topic ->
                if (topic.childs) {
                    processTopicToHtml(topic.childs)
                } else {
                    UrlEligibleForExport urlEligibleForExport = urlEligibleForExport(topic.url)
                    if (topic.url && !topic.url.equals("#") && urlEligibleForExport.isEligible) {
                        if (!exportUrlToHtml(topic.url, urlEligibleForExport.name)) {
                            println("Unable to export file for url: ${topic.url}")
                        }
                    }
                }
            }
        }
    }

    private void processDescriptorToHtml(String descriptorPath) {
        try {
            if (fileDirectory.isExist(descriptorPath)) {
                Descriptor descriptor = loadYmlFromFile(descriptorPath)
                if (!descriptor) {
                    println("Empty Descriptor")
                    return
                }
                if (descriptor.topics) {
                    processTopicToHtml(descriptor.topics)
                }
                if (descriptor.relatedTopics) {
                    processTopicToHtml(descriptor.relatedTopics)
                }
            } else {
                println("Descriptor not exist.")
            }
        } catch (Exception e) {
            println("Process Descriptor To Html Error: " + e.getMessage())
        }
    }

    private String getBismillahFileName() {
        return "/${AsciiDocConstant.bismillahFile}.html".toString()
    }

    private void exportDescriptorPage(String descriptorPath) {
        String path
        if (descriptorPath && descriptorPath.endsWith(ymlDescriptorFileName())) {
            path = descriptorPath.replace(ymlDescriptorFileName(), "")
        } else if (descriptorPath && descriptorPath.endsWith(ymltOutlineFileName())) {
            path = descriptorPath.replace(ymltOutlineFileName(), "")
        }
        if (path) {
            String url = getURL(path)
            if (!url || url.equals("") || trackDescriptorPage.get(url)) {
                return
            }
            String name = ""
            String nameWithExtension = getBismillahFileName()
            trackDescriptorPage.put(url, true)
            if (url && url.equals("/")) {
                name = AsciiDocConstant.bismillahFile
            } else if (url && !url.startsWith("/")) {
                url = "/" + url
            }

            String outputName = name
            if (!processRequest.exportFileExtension) {
                outputName = nameWithExtension
            }

            if (!resourceProcessor.isModifiedDocFile(path) && fileDirectory.isExist(urlToOutputDocFile(url, outputName))) {
                return
            }
            exportUrlToHtml(url, outputName)
        }
    }

    private void iterateDescriptor(List<FileDirectoryListing> sources) throws AsciiDocException {
        String fileName
        sources.each { FileDirectoryListing fileDirectoryListing ->
            if (isSkipFile(fileDirectoryListing.fileDirectoryInfo, true)) {
                return
            } else if (fileDirectoryListing.fileDirectoryInfo.isDirectory && fileDirectoryListing.subDirectories) {
                iterateDescriptor(fileDirectoryListing.subDirectories)
            } else {
                fileName = fileDirectoryListing.fileDirectoryInfo.name
                if (fileName && (fileName.equals(ymlDescriptorFileName()) || fileName.equals(ymltOutlineFileName()))) {
                    exportDescriptorPage(fileDirectoryListing.fileDirectoryInfo.absolutePath)
                    processDescriptorToHtml(fileDirectoryListing.fileDirectoryInfo.absolutePath)
                }
            }
        }
    }


    private void exportStaticPage() {
        String url = "/page-404"
        String extension = ""
        if (!processRequest.exportFileExtension) {
            extension = ".html"
        }
        exportUrlToHtml(url, extension)
    }

    public void exportToHtml() throws AsciiDocException {
        List<FileDirectoryListing> topics = getTopicList()
        processRequest.isFromWebsite = false
        resourceProcessor.loadDocumentIndex()
        iterateDescriptor(topics)
        exportStaticPage()
        resourceProcessor.exportStaticContent()
    }

    public void test() {
        exportToYmlFile(null, null)
    }


    public Map<String, TopicMergeReport> manipulateDescriptorOutline() throws AsciiDocException {
        List<FileDirectoryListing> topics = getTopicList()
        topics.each { FileDirectoryListing fileDirectoryListing ->
            if (fileDirectoryListing.fileDirectoryInfo.isDirectory && fileDirectoryListing.subDirectories) {
                if (isSkipFile(fileDirectoryListing.fileDirectoryInfo)) {
                    return
                }
                processTopic(fileDirectoryListing)
            }
        }
        bismillahDescriptorProcess()
        return reports
    }

}
