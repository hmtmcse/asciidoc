package com.hmtmcse.texttoweb.processor

import com.hmtmcse.common.AsciiDocException
import com.hmtmcse.fileutil.data.FDListingFilter
import com.hmtmcse.fileutil.data.FileDirectoryListing
import com.hmtmcse.fileutil.fd.FDUtil
import com.hmtmcse.fileutil.fd.FileDirectory
import com.hmtmcse.shellutil.console.menu.OptionValues
import com.hmtmcse.texttoweb.Config
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

    private void addReport(TopicMergeReport report){
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
        if (processRequest && processRequest.mergeData && processRequest.mergeData.get(topicKey)) {
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
                addReport(new TopicMergeReport(bismillahTopicKey, "Main Descriptor Not Exist").setIsEditable(false).setRelativePath("/"))
            }
            return
        }

        if (!fileDirectory.isExist(path) && isAllowedByUser(bismillahTopicKey)) {
            exportToYmlFile(pathData.absolutePath, DescriptorSample.landingDescriptor)
        }
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

    }

    void processSubTopic() {}

    void processTopicDetails() {}


}
