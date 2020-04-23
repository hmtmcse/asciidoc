package com.hmtmcse.texttoweb.processor

import com.hmtmcse.common.AsciiDocConstant
import com.hmtmcse.common.AsciiDocUtil
import com.hmtmcse.fileutil.data.FDInfo
import com.hmtmcse.fileutil.data.FileDirectoryListing
import com.hmtmcse.fileutil.fd.FDUtil
import com.hmtmcse.fileutil.fd.FileDirectory
import com.hmtmcse.fileutil.text.TextFile
import com.hmtmcse.parser4java.JsonProcessor
import com.hmtmcse.texttoweb.Config
import com.hmtmcse.texttoweb.data.StaticResourceParams
import com.hmtmcse.texttoweb.data.TopicMergeReport
import com.hmtmcse.texttoweb.resource.StaticResourceIndex
import com.hmtmcse.texttoweb.resource.StaticResourceIndexData
import com.hmtmcse.tmutil.TStringUtil

class ResourceProcessor {

    private Config config
    private TextFile textFile
    private FileDirectory fileDirectory
    private JsonProcessor jsonProcessor
    public StaticResourceIndex newStaticResourceIndex
    public StaticResourceIndex oldStaticResourceIndex
    public StaticResourceIndex docFileLogIndex
    public List<TopicMergeReport> reports = []
    private static final String copied = "copied"
    private static final String deleted = "deleted"

    ResourceProcessor(Config config) {
        this.config = config
        textFile = new TextFile()
        fileDirectory = new FileDirectory()
        jsonProcessor = new JsonProcessor()
    }

    private String logDir() {
        return FDUtil.concatPath(config.source, AsciiDocConstant.text2webData, AsciiDocConstant.vcs)
    }

    private String trashDir() {
        return FDUtil.concatPath(config.out, AsciiDocConstant.text2webData, AsciiDocConstant.trash)
    }

    private StaticResourceIndex loadLogFileIndex(String fileName) {
        String path = FDUtil.concatPath(logDir(), fileName)
        if (fileDirectory.isExist(path)) {
            try {
                String json = textFile.fileToString(path)?.text
                if (json) {
                    return jsonProcessor.objectFromText(json, StaticResourceIndex.class)
                }
            } catch (Exception e) {
                println("Error from loadFileIndex: ${e.getMessage()}")
            }
        }
        return new StaticResourceIndex()
    }

    private void exportFileIndex(StaticResourceIndex staticResourceIndex, String fileName) {
        String path = logDir()
        fileDirectory.createDirectoriesIfNotExist(path)
        path = FDUtil.concatPath(path, fileName)
        fileDirectory.removeIfExist(path)
        try {
            String json = jsonProcessor.objectToJson(staticResourceIndex)
            if (json) {
                textFile.stringToFile(path, json)
            }
        } catch (Exception e) {
            println("Error from exportFileIndex: ${e.getMessage()}")
        }
    }

    private String getRelativePath(String path, String source = config.source) {
        path = path.replace(source, "")
        if (path.length() > 1 && path.startsWith(File.separator)) {
            path = path.substring(1)
        }

        if (path.length() > 1 && path.endsWith(File.separator)) {
            path = path.substring(0, path.length() - 1)
        }
        return path
    }

    private String pathToKey(String path) {
        String key = TStringUtil.findReplace(path, File.separator, "_")
        key = TStringUtil.findReplace(key, ".", "_")
        return TStringUtil.findReplace(key, "-", "_")
    }

    public void addReport(String status, String path, String message = "", String trash = "") {
        TopicMergeReport topicMergeReport = new TopicMergeReport()
        topicMergeReport.setStatus(status)
        topicMergeReport.setPath(path)
        topicMergeReport.setMessage(message)
        topicMergeReport.setTrashPath(trash)
        reports.add(topicMergeReport)
    }

    public void copySourceToOut(StaticResourceParams params, FDInfo fdInfo, String relativePath) {
        String source = FDUtil.concatPath(params.source, relativePath)
        String out = FDUtil.concatPath(params.out, relativePath)
        if (fdInfo.isDirectory) {
            fileDirectory.createDirectoriesIfNotExist(out)
        } else if (fdInfo.isRegularFile && fileDirectory.isExist(source)) {
            fileDirectory.removeIfExist(out)
            fileDirectory.createParentDir(out)
            fileDirectory.copy(source, out)
            println("Coping to ${out}")
            addReport(copied, out, "Copied")
        }
    }

    public void copyToTrash(String source, String relativePath){
        String trash, parent
        trash = FDUtil.concatPath(trashDir(), relativePath)
        fileDirectory.createParentDir(trash)
        fileDirectory.removeAllIfExist(trash)
        fileDirectory.copyAll(source, trash)
        println("Deleting ${trash}")
    }

    private void removeOutResources(StaticResourceParams params, StaticResourceIndex removeStaticResourceIndex) {
        config
        if (removeStaticResourceIndex && removeStaticResourceIndex.fileLogs) {
            String out
            String trash
            fileDirectory.createDirectoriesIfNotExist(trashDir())
            removeStaticResourceIndex.fileLogs.each { String key, StaticResourceIndexData data ->
                out = FDUtil.concatPath(params.out, data.relativePath)
                trash = FDUtil.concatPath(trashDir(), data.relativePath)
                copyToTrash(out, data.relativePath)
                fileDirectory.removeAllIfExist(out)
                removeStaticResourceIndex.fileLogs.remove(key)
                addReport(deleted, out, "Deleted", trash)
            }
        }
    }

    private void createAndCopyStaticResourceIndex(StaticResourceParams params, List notSkip = []) {
        FDInfo fdInfo;
        String key, relativePath
        StaticResourceIndexData staticResourceIndexData
        if (params.list) {
            params.list.each { FileDirectoryListing fileDirectoryListing ->
                fdInfo = fileDirectoryListing.fileDirectoryInfo
                if (AsciiDocUtil.isSkipFile(fdInfo, null, notSkip)) {
                    return
                }
                if (fdInfo.isDirectory && fileDirectoryListing.subDirectories) {
                    createAndCopyStaticResourceIndex(params.copy(fileDirectoryListing.subDirectories), notSkip)
                }
                relativePath = getRelativePath(fdInfo.absolutePath, params.source)
                key = pathToKey(relativePath)
                if (oldStaticResourceIndex && oldStaticResourceIndex.fileLogs && oldStaticResourceIndex.fileLogs.get(key)) {
                    staticResourceIndexData = oldStaticResourceIndex.fileLogs.get(key)
                    if ((fdInfo.updatedAt.toMillis() != staticResourceIndexData.lastUpdated) || (params.out && !fileDirectory.isExist(FDUtil.concatPath(params.out, relativePath)))) {
                        if (params.isCopySourceToOutCopy) {
                            copySourceToOut(params, fdInfo, relativePath)
                        }
                        staticResourceIndexData.lastUpdated = fdInfo.updatedAt.toMillis()
                    }
                    newStaticResourceIndex.fileLogs.put(key, staticResourceIndexData)
                    oldStaticResourceIndex.fileLogs.remove(key)
                } else {
                    staticResourceIndexData = new StaticResourceIndexData()
                    staticResourceIndexData.relativePath = relativePath
                    staticResourceIndexData.lastUpdated = fdInfo.updatedAt.toMillis()
                    newStaticResourceIndex.fileLogs.put(key, staticResourceIndexData)
                    if (params.isCopySourceToOutCopy) {
                        copySourceToOut(params, fdInfo, relativePath)
                    }
                }
            }
        }
    }

    private StaticResourceIndex processCreateAndCopyStaticResourceIndex(StaticResourceParams staticResourceParams, List notSkip = []) {
        if (fileDirectory.isExist(staticResourceParams.source)) {
            try {
                newStaticResourceIndex = new StaticResourceIndex()
                this.oldStaticResourceIndex = staticResourceParams.oldStaticResourceIndex
                staticResourceParams.setList(fileDirectory.listDirRecursively(staticResourceParams.source))
                createAndCopyStaticResourceIndex(staticResourceParams, notSkip)
            } catch (Exception e) {
                println("Error from createStaticResourceIndex: ${e.getMessage()}")
                return oldStaticResourceIndex
            }
        }
        return newStaticResourceIndex
    }

    public void updateDocumentIndex() {
        try {
            println("Updating Document Index")
            StaticResourceParams staticResourceParams = new StaticResourceParams()
            staticResourceParams.oldStaticResourceIndex = (docFileLogIndex ?: new StaticResourceIndex())
            staticResourceParams.source = config.source
            this.newStaticResourceIndex = processCreateAndCopyStaticResourceIndex(staticResourceParams)
            exportFileIndex(this.newStaticResourceIndex, AsciiDocConstant.docFileLog)
            removeOutResources(staticResourceParams, oldStaticResourceIndex)
        } catch (Exception e) {
            println("Error from updateDocumentIndex: ${e.getMessage()}")
        }
    }

    public void loadDocumentIndex() {
        try {
            docFileLogIndex = loadLogFileIndex(AsciiDocConstant.docFileLog)
        } catch (Exception e) {
            println("Error from loadDocumentIndex: ${e.getMessage()}")
        }
    }

    public Boolean isModifiedDocFile(String path) {
        String relativePath = getRelativePath(path)
        if (relativePath && relativePath.startsWith("/")){
            relativePath = relativePath.substring(1)
        }
        if (relativePath) {
            FDInfo fdInfo = fileDirectory.getDetailsInfo(path, false)
            return isModifiedDocFile(relativePath, fdInfo)
        }
        return true
    }

    public Boolean isModifiedDocFile(String relativePath, FDInfo fdInfo) {
        if (docFileLogIndex == null) {
            loadDocumentIndex()
        }
        String key = pathToKey(relativePath)
        if (docFileLogIndex && docFileLogIndex.fileLogs.get(key) && docFileLogIndex.fileLogs.get(key).lastUpdated == fdInfo.updatedAt.toMillis()) {
            return false
        }
        return true
    }




    public void updateDocumentResourceIndex() {
        try {
            println("Updating Document Resource Index")
            StaticResourceParams staticResourceParams = new StaticResourceParams()
            staticResourceParams.oldStaticResourceIndex = (loadLogFileIndex(AsciiDocConstant.docResourcesFileLog) ?: new StaticResourceIndex())
            staticResourceParams.source = FDUtil.concatPath(config.source, AsciiDocConstant.staticFiles)
            staticResourceParams.out = FDUtil.concatPath(config.out, AsciiDocConstant.staticFiles)
            staticResourceParams.isCopySourceToOutCopy = true
            fileDirectory.createDirectoriesIfNotExist(staticResourceParams.out)
            this.newStaticResourceIndex = processCreateAndCopyStaticResourceIndex(staticResourceParams, [AsciiDocConstant.staticFiles])
            exportFileIndex(this.newStaticResourceIndex, AsciiDocConstant.docResourcesFileLog)
            removeOutResources(staticResourceParams, oldStaticResourceIndex)
        } catch (Exception e) {
            println("Error from updateDocumentResourceIndex: ${e.getMessage()}")
        }
    }

    public void updateTemplateAssetIndex() {
        try {
            println("Updating Template Asset Index")
            StaticResourceParams staticResourceParams = new StaticResourceParams()
            staticResourceParams.oldStaticResourceIndex = (loadLogFileIndex(AsciiDocConstant.templateFileLog) ?: new StaticResourceIndex())
            staticResourceParams.source = FDUtil.concatPath(config.template, AsciiDocConstant.asset)
            staticResourceParams.out = FDUtil.concatPath(config.out, AsciiDocConstant.asset)
            staticResourceParams.isCopySourceToOutCopy = true
            fileDirectory.createDirectoriesIfNotExist(staticResourceParams.out)
            this.newStaticResourceIndex = processCreateAndCopyStaticResourceIndex(staticResourceParams)
            exportFileIndex(this.newStaticResourceIndex, AsciiDocConstant.templateFileLog)
            removeOutResources(staticResourceParams, oldStaticResourceIndex)
        } catch (Exception e) {
            println("Error from updateTemplateAssetIndex: ${e.getMessage()}")
        }
    }


    private String get404Page() {
        if (config.urlStartWith && config.urlStartWith.startsWith("/")) {
            return "${config.urlStartWith}/${AsciiDocConstant.page404}"
        }
        return "/${config.urlStartWith}/${AsciiDocConstant.page404}"
    }

    public void exportHtaccess() {
        String content = "ErrorDocument 404 ${get404Page()}"
        content += "\n" + "<IfModule mod_deflate.c>"
        content += "\n" + "AddOutputFilterByType DEFLATE application/javascript"
        content += "\n" + "AddOutputFilterByType DEFLATE application/x-javascript"
        content += "\n" + "AddOutputFilterByType DEFLATE text/css"
        content += "\n" + "AddOutputFilterByType DEFLATE text/html"
        content += "\n" + "AddOutputFilterByType DEFLATE text/javascript"
        content += "\n" + "AddOutputFilterByType DEFLATE text/plain"
        content += "\n" + "</IfModule>"
        try {
            String path = FDUtil.concatPath(config.out, ".htaccess")
            fileDirectory.removeIfExist(path)
            textFile.stringToFile(path, content)
        } catch (Exception e) {
            println("Error from Export .htaccess: ${e.getMessage()}")
        }
    }

    public void exportGithubConfig() {
        String content = "---"
        content += "\n" + "permalink: ${get404Page()}"
        content += "\n" + "---"
        try {
            String path = FDUtil.concatPath(config.out, "404.md")
            fileDirectory.removeIfExist(path)
            textFile.stringToFile(path, content)
        } catch (Exception e) {
            println("Error from Export github 404: ${e.getMessage()}")
        }
    }

    public List<TopicMergeReport> exportStaticContent() {
        updateDocumentIndex()
        exportHtaccess()
        exportGithubConfig()
        updateDocumentResourceIndex()
        updateTemplateAssetIndex()
        return reports
    }

}
