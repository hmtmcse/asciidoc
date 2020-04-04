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

    ResourceProcessor(Config config) {
        this.config = config
        textFile = new TextFile()
        fileDirectory = new FileDirectory()
        jsonProcessor = new JsonProcessor()
    }

    private String logDir() {
        return FDUtil.concatPath(config.source, AsciiDocConstant.text2webLog)
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

    private String getRelativePath(String path) {
        path = path.replace(config.source, "")
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

    public void copySourceToOut(FDInfo fdInfo, String relativePath) {
        String source = FDUtil.concatPath(config.source, relativePath)
        String out = FDUtil.concatPath(config.out, relativePath)
        if (fdInfo.isDirectory) {
            fileDirectory.createDirectoriesIfNotExist(out)
        } else if (fdInfo.isRegularFile && fileDirectory.isExist(source)) {
            fileDirectory.removeIfExist(out)
            fileDirectory.copy(source, out)
        }
    }

    private void removeOutResources(StaticResourceIndex removeStaticResourceIndex) {
        if (removeStaticResourceIndex && removeStaticResourceIndex.fileLogs) {
            String out
            removeStaticResourceIndex.fileLogs.each { String key, StaticResourceIndexData data ->
                out = FDUtil.concatPath(config.out, data.relativePath)
                fileDirectory.removeAllIfExist(out)
            }
        }
    }

    private void createAndCopyStaticResourceIndex(StaticResourceParams params) {
        FDInfo fdInfo;
        String key, relativePath
        StaticResourceIndexData staticResourceIndexData
        if (params.list) {
            params.list.each { FileDirectoryListing fileDirectoryListing ->
                fdInfo = fileDirectoryListing.fileDirectoryInfo
                if (AsciiDocUtil.isSkipFile(fdInfo)) {
                    return
                }
                if (fdInfo.isDirectory && fileDirectoryListing.subDirectories) {
                    createAndCopyStaticResourceIndex(new StaticResourceParams(fileDirectoryListing.subDirectories))
                }
                relativePath = getRelativePath(fdInfo.absolutePath)
                key = pathToKey(relativePath)
                if (oldStaticResourceIndex && oldStaticResourceIndex.fileLogs && oldStaticResourceIndex.fileLogs.get(key)) {
                    staticResourceIndexData = oldStaticResourceIndex.fileLogs.get(key)
                    if (fdInfo.updatedAt.toMillis() != staticResourceIndexData.lastUpdated) {
                        if (params.isCopySourceToOutCopy) {
                            copySourceToOut(fdInfo, relativePath)
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
                        copySourceToOut(fdInfo, relativePath)
                    }
                }
            }
        }
    }

    private StaticResourceIndex createAndCopyStaticResourceIndex(String path, StaticResourceIndex oldStaticResourceIndex) {
        if (fileDirectory.isExist(path)) {
            try {
                newStaticResourceIndex = new StaticResourceIndex()
                this.oldStaticResourceIndex = oldStaticResourceIndex
                createAndCopyStaticResourceIndex(new StaticResourceParams(fileDirectory.listDirRecursively(path)))
            } catch (Exception e) {
                println("Error from createStaticResourceIndex: ${e.getMessage()}")
                return oldStaticResourceIndex
            }
        }
        return newStaticResourceIndex
    }

    public void loadDocumentIndex() {
        try {
            docFileLogIndex = loadLogFileIndex(AsciiDocConstant.docFileLog)
        } catch (Exception e) {
            println("Error from loadDocumentIndex: ${e.getMessage()}")
        }
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

    public void updateDocumentIndex() {
        this.newStaticResourceIndex = new StaticResourceIndex()
        this.oldStaticResourceIndex = (docFileLogIndex ?: new StaticResourceIndex())
        try {
            this.newStaticResourceIndex = createAndCopyStaticResourceIndex(config.source, this.oldStaticResourceIndex)
            exportFileIndex(this.newStaticResourceIndex, AsciiDocConstant.docFileLog)
        } catch (Exception e) {
            println("Error from updateDocumentIndex: ${e.getMessage()}")
        }
    }

    public void loadDocumentResourceIndex() {

    }

    public void updateDocumentResourceIndex() {

    }

    public void exportDocumentResourceIndex() {

    }

    public void loadTemplateAssetIndex() {

    }

    public void updateTemplateAssetIndex() {

    }

    public void exportTemplateAssetIndex() {

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

    public void exportStaticContent() {
        updateDocumentIndex()
        exportHtaccess()
        exportGithubConfig()
    }

}
