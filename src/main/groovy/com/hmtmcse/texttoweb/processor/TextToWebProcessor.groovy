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
import com.hmtmcse.te.data.TextToWebEngineConfig
import com.hmtmcse.te.data.TextToWebEngineData
import com.hmtmcse.texttoweb.Config
import com.hmtmcse.texttoweb.Descriptor
import com.hmtmcse.texttoweb.Topic
import com.hmtmcse.texttoweb.common.ConfigLoader
import com.hmtmcse.texttoweb.data.*
import com.hmtmcse.texttoweb.model.CommandProcessor
import com.hmtmcse.texttoweb.sample.DescriptorSample
import com.hmtmcse.tmutil.TomTom

import java.util.concurrent.CopyOnWriteArrayList

class TextToWebProcessor implements CommandProcessor {

    private FileDirectory fileDirectory
    private Config config
    ProcessRequest processRequest
    private Map<String, TopicMergeReport> reports = [:]
    public List<TopicMergeReport> docExportReports = []
    private Boolean isDescriptorUpdated = false
    private Boolean isUpdateAllHtml = false
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


    public String getRelativePath(String absolutePath) {
        if (absolutePath) {
            return absolutePath.replace(config.source, "")
        }
        return absolutePath
    }

    private void addReport(TopicMergeReport report) {
        report.descriptorName = getLoadedDescriptorName()
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

    private void removeTopic(Topic topic) {
        String relativePath = topic.url
        if (!topic.childs) {
            relativePath += processRequest.exportFileExtension ?: ""
        }
        String source = FDUtil.concatPath(config.out, relativePath)
        if (fileDirectory.isExist(source)) {
            resourceProcessor.copyToTrash(source, topic.url)
            fileDirectory.removeAllIfExist(source)
        }
    }



    private TopicMergeData margeTopicDescriptor(TopicMergeData topicMergeData) {

        TopicMergeData temp
        TopicListToMapData tempTopicListMap
        List<Topic> tempChild
        topicMergeData.previousTopic = new CopyOnWriteArrayList<>(topicMergeData.previousTopic)
        Integer adjustIndex = 0
        topicMergeData.previousTopic.eachWithIndex { Topic topic, Integer index ->
            tempTopicListMap = topicMergeData.currentTopicMap.get(topic.url)
            tempChild = topic.childs
            index = index - adjustIndex
            if (tempChild && tempTopicListMap) { // Content has been updated
                temp = new TopicMergeData(tempTopicListMap.child, tempChild)
                temp = margeTopicDescriptor(temp)
                topicMergeData.currentTopicMap.remove(topic.url)
                topicMergeData.previousTopic.get(index).childs = temp.previousTopic
            } else if (tempChild) { // Previous Content Deleted
                if (processRequest.task.equals(ProcessTask.MERGE)) {
                    TopicMergeReport topicMergeReport = getTopicReport(topicMergeData.previousTopic.get(index))
                    if (topicMergeReport && topicMergeReport.isMerge) {
                        isDescriptorUpdated = true
                        removeTopic(topicMergeData.previousTopic.get(index))
                        topicMergeData.previousTopic.remove(index)
                        adjustIndex++
                        return
                    }
                }

                temp = new TopicMergeData(tempChild)
                temp = margeTopicDescriptor(temp)
                topicMergeData.previousTopic.get(index).name += " - Deleted With Child"
                topicMergeData.previousTopic.get(index).childs = temp.previousTopic
                addTopicReport(topicMergeData.previousTopic.get(index))
                isDescriptorUpdated = true
            } else if (tempTopicListMap) { // Content exist both side
                if (!topicMergeData.currentTopicMap.get(topic.url)?.child){
                    topicMergeData.currentTopicMap.remove(topic.url)
                }
            } else {

                if (processRequest.task.equals(ProcessTask.MERGE)) {
                    TopicMergeReport topicMergeReport = getTopicReport(topicMergeData.previousTopic.get(index))
                    if (topicMergeReport && topicMergeReport.isMerge) {
                        isDescriptorUpdated = true
                        removeTopic(topicMergeData.previousTopic.get(index))
                        topicMergeData.previousTopic.remove(index)
                        adjustIndex++
                        return
                    }
                }
                isDescriptorUpdated = true
                topicMergeData.previousTopic.get(index).name += " - Deleted"
                addTopicReport(topicMergeData.previousTopic.get(index))
            }
        }

        topicMergeData.currentTopicMap.each { String key, TopicListToMapData newTopic ->
            addTopicReport(newTopic.topic)
            if (newTopic.child) {
                temp = new TopicMergeData(newTopic.child)
                temp = margeTopicDescriptor(temp)
                newTopic.topic.childs = temp.previousTopic
            }

            if (processRequest.task.equals(ProcessTask.MERGE)) {
                TopicMergeReport topicMergeReport = getTopicReport(newTopic.topic)
                if (topicMergeReport && !topicMergeReport.isMerge) {
                    return
                } else if (topicMergeReport && topicMergeReport.name) {
                    newTopic.topic.name = topicMergeReport.name
                }
            }
            topicMergeData.previousTopic.add(newTopic.topic)
            isDescriptorUpdated = true
        }

        return topicMergeData
    }

    private List<Topic> margeTopicDescriptor(Map<String, TopicListToMapData> currentTopicMap, List<Topic> previousTopic) {
        return margeTopicDescriptor(new TopicMergeData(currentTopicMap, previousTopic)).previousTopic
    }


    public void addDocExportReport(String status, String path, String message = "", String url = "") {
        TopicMergeReport topicMergeReport = new TopicMergeReport()
        topicMergeReport.setStatus(status)
        topicMergeReport.setPath(path)
        topicMergeReport.setMessage(message)
        topicMergeReport.setUrl(url)
        docExportReports.add(topicMergeReport)
    }

    public Boolean isAlreadyExported(String url) {
        Boolean isExist = false
        docExportReports.collect { item ->
            if (item.url.equals(url)) {
                isExist = true
            }
        }
        return isExist
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

    private Map<String, TopicListToMapData> topicListToMap(List<Topic> topics) {
        Map<String, TopicListToMapData> topicMap = [:]
        TopicListToMapData temp
        if (!topics) {
            return topicMap
        }
        topics.each { Topic topic ->
            if (topic.childs) {
                temp = new TopicListToMapData(topic)
                temp.child = topicListToMap(topic.childs)
                topicMap.put(topic.url, temp)
            } else {
                topicMap.put(topic.url, new TopicListToMapData(topic))
            }
        }
        return topicMap
    }


    private Descriptor preprocessAndMargeDescriptorTopics(Descriptor currentDescriptor, Descriptor previousDescriptor) {
        isDescriptorUpdated = false
        if (!previousDescriptor.topics) {
            previousDescriptor.topics = new ArrayList<>()
        }
        previousDescriptor.topics = margeTopicDescriptor(topicListToMap(currentDescriptor.topics), previousDescriptor.topics)
        previousDescriptor.updateStatus(isDescriptorUpdated)
        return previousDescriptor
    }

    private List<Topic> newDescriptorTopicProcessAndReport(List<Topic> topics) {
        topics = new CopyOnWriteArrayList<>(topics)
        if (processRequest.task.equals(ProcessTask.MERGE)) {
            TopicMergeReport topicMergeReport
            topics.each { Topic topic ->
                topicMergeReport = getTopicReport(topic)
                if (topicMergeReport && !topicMergeReport.isMerge) {
                    topics.remove(topic)
                } else {
                    if (topicMergeReport && topicMergeReport.name) {
                        topic.name = topicMergeReport.name
                    }
                    if (topic.childs) {
                        topic.childs = newDescriptorTopicProcessAndReport(topic.childs)
                    }
                }
            }
        } else {
            topics.each { Topic topic ->
                addTopicReport(topic)
                if (topic.childs) {
                    topic.childs = newDescriptorTopicProcessAndReport(topic.childs)
                }
            }
        }
        return topics
    }

    private Descriptor newDescriptorProcessAndReport(Descriptor newDescriptor) {
        if (newDescriptor.topics) {
            newDescriptor.topics = newDescriptorTopicProcessAndReport(newDescriptor.topics)
        }
        return newDescriptor
    }

    private Descriptor processAndMargeDescriptorTopics(String descriptorFile, Descriptor newDescriptor) {
        if (!fileDirectory.isExist(descriptorFile)) {
            return newDescriptorProcessAndReport(newDescriptor)
        }

        Descriptor currentDescriptor = null
        try {
            currentDescriptor = loadYmlFromFile(descriptorFile)
            if (!currentDescriptor) {
                return newDescriptorProcessAndReport(newDescriptor)
            }
        } catch (Exception e) {
            return newDescriptorProcessAndReport(newDescriptor)
        }
        return preprocessAndMargeDescriptorTopics(newDescriptor, currentDescriptor)
    }

    Boolean isChildIndex(FDInfo topicDir) {
        if (!topicDir.isDirectory) {
            String topicName = removeAdocExtension(topicDir.name)
            String parent = fileDirectory.getParentPath(topicDir.absolutePath)
            if (topicName && parent && parent.endsWith(topicName)) {
                return true
            }
        }
        return false
    }

    OutlineAndDescriptor prepareOutlineAndDescriptor(List<FileDirectoryListing> subDirectories, OutlineAndDescriptor outlineAndDescriptor, Map index = [outline: 0, details: null]) {
        String url, humReadableName
        Boolean isIncludeTopic
        subDirectories.each { FileDirectoryListing fileDirectoryListing ->
            FDInfo topicDir = fileDirectoryListing.fileDirectoryInfo
            if (isSkipFile(topicDir)) {
                return
            }
            url = getURL(topicDir.absolutePath)
            isIncludeTopic = !isChildIndex(topicDir)
            humReadableName = makeHumReadableWithoutExt(topicDir.name)
            if (topicDir.isDirectory && fileDirectoryListing.subDirectories) {
                OutlineAndDescriptor _outlineAndDescriptor = new OutlineAndDescriptor(humReadableName, url, "##" + url)
                _outlineAndDescriptor = prepareOutlineAndDescriptor(fileDirectoryListing.subDirectories, _outlineAndDescriptor, [outline: 0, details: null])

                Topic detailsDescriptorTopic = new Topic(humReadableName, url).setTracker("##" + url)
                _outlineAndDescriptor.detailsDescriptor.topics.each {
                    detailsDescriptorTopic.addChild(it)
                }
                outlineAndDescriptor.addToDetailsByIndex(index.details, detailsDescriptorTopic)


                _outlineAndDescriptor.outlineDescriptor.topics.each {
                    outlineAndDescriptor.addToOutlineByIndex(index.outline, it)
                }

            } else if (isIncludeTopic) {
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
        OutlineAndDescriptor outlineAndDescriptor = new OutlineAndDescriptor(makeHumReadableWithoutExt(topicsDir.name), url, "##" + url)
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
        return getSourceList()
    }

    private Map subContentIndex(relativePath) {
        Map response = [
                relativePath: relativePath,
                originalRelativePath: relativePath,
                isSubContent: false,
        ]
        try {
            String path = FDUtil.concatPath(config.source, relativePath)
            if (fileDirectory.isDirectory(path)) {
                FDInfo info = fileDirectory.getDetailsInfo(path, false)
                path = FDUtil.concatPath(path, info.name) + (processRequest.docFileExtension ? ".${processRequest.docFileExtension}" : "")
                if (fileDirectory.isExist(path)) {
                    response.relativePath = TomTom.concatWithSeparator(relativePath, info.name, File.separator)
                    response.isSubContent = true
                }
            }
        } catch (Exception ignore) {
        }
        return response
    }

    private UrlEligibleForExport urlEligibleForExport(String url) {
        UrlEligibleForExport urlEligibleForExport = new UrlEligibleForExport()
        String relativePath = TwFileUtil.trimAndUrlToPath(url)
        Map subContentIndex = subContentIndex(relativePath)
        relativePath = subContentIndex.relativePath
        String sourceRelativePath = "${relativePath}.${processRequest.docFileExtension}".toString()
        String sourceDoc = FDUtil.concatPath(config.source, sourceRelativePath)

        if (!fileDirectory.isExist(sourceDoc)) {
            return urlEligibleForExport
        }

        FDInfo fdInfo = fileDirectory.getDetailsInfo(sourceDoc, true)
        if (fdInfo.isDirectory || subContentIndex.isSubContent) {
            urlEligibleForExport.name = getBismillahFileName()
        }

        if (isUpdateAllHtml) {
            return urlEligibleForExport.setIsEligible(true)
        }

        String outputDoc = FDUtil.concatPath(config.out, "${relativePath}${processRequest.getExportFileExtensionByNullCheck()}".toString())
        if (resourceProcessor.isModifiedDocFile(sourceRelativePath, fdInfo)) {
            return urlEligibleForExport.setIsEligible(true)
        } else if (subContentIndex.isSubContent && FDUtil.concatPath(config.out, subContentIndex.originalRelativePath, urlEligibleForExport.name)) {
            return urlEligibleForExport
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
        String status = "Doc Exported"
        try {
            if (isAlreadyExported(url)) {
                return true
            }
            SearchProcessor searchIndexProcessor = new SearchProcessor()
            String outputDoc = urlToOutputDocFile(url, name)
            if (fileDirectory.isExist(outputDoc)) {
                status = "Doc Updated"
            }
            if (!fileDirectory.removeIfExist(outputDoc)) {
                println("${errorFrom} Unable to remove existing output file: ${outputDoc}")
                return
            }
            String html = textToWebHtmlEngine.getContentByURL(url, processRequest)
            if (html) {
                File outputDocFile = new File(outputDoc)
                fileDirectory.createDirectoriesIfNotExist(outputDocFile.getParentFile().absolutePath)
                html = searchIndexProcessor.process(url, html)
                addDocExportReport(status, outputDoc, "Exported")
                return textFile.stringToFile(outputDoc, html)
            } else {
                println("${errorFrom} HTML Not found.")
            }
        } catch (Exception e) {
            println("${errorFrom} ${e.getMessage()}")
        }
    }

    private exportTopicToHtml(Topic topic){
        UrlEligibleForExport urlEligibleForExport = urlEligibleForExport(topic.url)
        if (topic.url && !topic.url.equals("#") && urlEligibleForExport.isEligible) {
            if (!exportUrlToHtml(topic.url, urlEligibleForExport.name)) {
                println("Unable to export file for url: ${topic.url}")
            }
        }
    }

    private void processTopicToHtml(List<Topic> topics) {
        if (topics) {
            topics.each { Topic topic ->
                if (topic.childs) {
                    exportTopicToHtml(topic)
                    processTopicToHtml(topic.childs)
                } else {
                    exportTopicToHtml(topic)
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
        isUpdateAllHtml = false
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

            if (!resourceProcessor.isModifiedDocFile(descriptorPath) && fileDirectory.isExist(urlToOutputDocFile(url, outputName))) {
                return
            }
            exportUrlToHtml(url, outputName)
            isUpdateAllHtml = true
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
        String path = FDUtil.concatPath(config.out, url + extension)
        if (!fileDirectory.isExist(path)) {
            exportUrlToHtml(url, extension)
        }
    }

    public List<TopicMergeReport> exportToHtml() throws AsciiDocException {
        List<FileDirectoryListing> topics = getTopicList()
        processRequest.isFromWebsite = false
        resourceProcessor.loadDocumentIndex()
        iterateDescriptor(topics)
        exportStaticPage()
        resourceProcessor.exportStaticContent()
        docExportReports.addAll(resourceProcessor.reports)
        return docExportReports
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
        if (reports) {
            reports = reports.sort { a, b -> a.value.url <=> b.value.url }
        }
        return reports
    }

}
