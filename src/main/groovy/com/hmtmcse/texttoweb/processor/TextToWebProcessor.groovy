package com.hmtmcse.texttoweb.processor

import com.hmtmcse.common.AsciiDocException
import com.hmtmcse.fileutil.data.FDInfo
import com.hmtmcse.fileutil.data.FDListingFilter
import com.hmtmcse.fileutil.data.FileDirectoryListing
import com.hmtmcse.fileutil.fd.FDUtil
import com.hmtmcse.fileutil.fd.FileDirectory
import com.hmtmcse.jtfutil.io.JavaNio
import com.hmtmcse.shellutil.console.menu.OptionValues
import com.hmtmcse.texttoweb.Config
import com.hmtmcse.texttoweb.Descriptor
import com.hmtmcse.texttoweb.Topic
import com.hmtmcse.texttoweb.common.ConfigLoader
import com.hmtmcse.texttoweb.data.PathData
import com.hmtmcse.texttoweb.data.ProcessRequest
import com.hmtmcse.texttoweb.data.ProcessTask
import com.hmtmcse.texttoweb.data.TopicMergeReport
import com.hmtmcse.texttoweb.model.CommandProcessor
import com.hmtmcse.texttoweb.sample.DescriptorSample

class TextToWebProcessor implements CommandProcessor {

    private FileDirectory fileDirectory
    private Config config
    ProcessRequest processRequest
    private Map<String, TopicMergeReport> reports = [:]

    public TextToWebProcessor(ProcessRequest processRequest) {
        fileDirectory = new FileDirectory()
        config = ConfigLoader.getConfig()
        this.processRequest = processRequest
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

    private List<FileDirectoryListing> getSourceList() throws AsciiDocException {
        if (!fileDirectory.isExist(config.source)) {
            throw new AsciiDocException("Source Not found! Please specify source at config.yml")
        }
        FDListingFilter filter = new FDListingFilter().details()
        return fileDirectory.listDirRecursively(config.source, filter)
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

    private Boolean isSkipFile(FDInfo topicsDir) {
        if (topicsDir && topicsDir.name &&
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
            path = config.urlStartWith + path
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
                        previousTopic.remove(index)
                        return
                    }
                }
                if (topic.childs) {
                    previousTopic.get(index).name += " - Deleted With Child"
                } else {
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
                TopicMergeReport topicMergeReport = getTopicReport(newTopic)
                if (topicMergeReport && topicMergeReport.isMerge && topicMergeReport.name) {
                    name = topicMergeReport.name
                }
            }

            if (newTopic instanceof Topic) {
                newTopic.name = newTopic.name + " - Added"
                newTopic.name = name ?: newTopic.name
                previousTopic.add(newTopic)
                addTopicReport(newTopic)
            } else if (newTopic instanceof Map) {
                temp = newTopic.topic
                temp.name += " - Added with Child"
                temp.name = name ?: temp.name
                previousTopic.add(temp)
                addTopicReport(temp)
            }
        }
        return previousTopic
    }


    void test() {
        exportToYmlFile(null, null)
    }

    Map<String, TopicMergeReport> manipulateDescriptorOutline() throws AsciiDocException {
        List<FileDirectoryListing> topics = getSourceList()
        if (!topics) {
            throw new AsciiDocException("Topics Not available")
        }
        topics.each { FileDirectoryListing fileDirectoryListing ->
            if (fileDirectoryListing.fileDirectoryInfo.isDirectory && fileDirectoryListing.subDirectories) {
                processTopic(fileDirectoryListing)
            }
        }
        bismillahDescriptorProcess()
        return reports
    }

    void processTopic(FileDirectoryListing fileDirectoryListing) throws AsciiDocException {
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
                topicRootProcess(topicsDir)
            }
        }

        String descriptorYml = JavaNio.concatPathString(topicsRootPath, ymlDescriptorFileName())
        descriptor = margeDescriptorTopics(descriptorYml, descriptor)
        println(exportToYmlFile(topicsRootPath, descriptor))
    }

    void processSubTopic() {}

    void processTopicDetails() {}


}
