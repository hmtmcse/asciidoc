package com.hmtmcse.texttoweb.processor

import com.hmtmcse.common.AsciiDocConstant
import com.hmtmcse.fileutil.data.FDInfo
import com.hmtmcse.fileutil.data.FileDirectoryListing
import com.hmtmcse.fileutil.fd.FDUtil
import com.hmtmcse.fileutil.fd.FileDirectory
import com.hmtmcse.fileutil.text.TextFile
import com.hmtmcse.parser4java.JsonProcessor
import com.hmtmcse.texttoweb.Config
import com.hmtmcse.texttoweb.resource.StaticResourceIndex
import com.hmtmcse.texttoweb.resource.StaticResourceIndexData
import com.hmtmcse.tmutil.TStringUtil

class ResourceProcessor {

    private Config config
    private TextFile textFile
    private FileDirectory fileDirectory
    private JsonProcessor jsonProcessor
    private StaticResourceIndex templateFileLogIndex
    private StaticResourceIndex docFileLogIndex
    private StaticResourceIndex docResourcesFileLogIndex
    private StaticResourceIndex tempOldIndex

    ResourceProcessor(Config config) {
        this.config = config
        textFile = new TextFile()
        fileDirectory = new FileDirectory()
    }

    private String logDir() {
        return FDUtil.concatPath(config.source, AsciiDocConstant.text2webLog)
    }

    private StaticResourceIndex loadFileIndex(String fileName) {
        String path = FDUtil.concatPath(logDir(), fileName)
        if (fileDirectory.isDirectory(path)) {
            try {
                String json = textFile.fileToString(path)
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
        if (path.length() > 1 && path.startsWith("/")) {
            path = path.substring(1)
        }

        if (path.length() > 1 && path.endsWith("/")) {
            path = path.substring(0, path.length() - 1)
        }
        return path
    }

    private String pathToKey(String path) {
        String key = TStringUtil.findReplace(path, File.separator, "_")
        key = TStringUtil.findReplace(key, ".", "_")
        return TStringUtil.findReplace(key, "-", "_")
    }

    private StaticResourceIndex createStaticResourceIndex(StaticResourceIndex staticResourceIndex, StaticResourceIndex oldStaticResourceIndex, List<FileDirectoryListing> list) {
        FDInfo fdInfo;
        String key, relativePath
        StaticResourceIndexData staticResourceIndexData
        if (list) {
            list.each { FileDirectoryListing fileDirectoryListing ->
                fdInfo = fileDirectoryListing.fileDirectoryInfo
                if (fdInfo.isDirectory && fileDirectoryListing.subDirectories) {
                    createStaticResourceIndex(staticResourceIndex, oldStaticResourceIndex, fileDirectoryListing.subDirectories)
                }
                relativePath = getRelativePath(fdInfo.absolutePath)
                key = pathToKey(relativePath)
                if (oldStaticResourceIndex && oldStaticResourceIndex.fileLogs && oldStaticResourceIndex.fileLogs.get(key)) {
                    staticResourceIndex.fileLogs.put(key, oldStaticResourceIndex.fileLogs.get(key))
                    oldStaticResourceIndex.fileLogs.remove(key)
                } else {
                    staticResourceIndexData = new StaticResourceIndexData()
                    staticResourceIndexData.relativePath = relativePath
                    staticResourceIndexData.lastUpdated = fdInfo.updatedAt.toMillis()
                    staticResourceIndex.fileLogs.put(key, staticResourceIndexData)
                }
            }
        }
        return oldStaticResourceIndex
    }

    private StaticResourceIndex createStaticResourceIndex(String path, StaticResourceIndex oldStaticResourceIndex) {
        StaticResourceIndex staticResourceIndex = new StaticResourceIndex()
        if (fileDirectory.isExist(path)) {
            try {
                return createStaticResourceIndex(staticResourceIndex, oldStaticResourceIndex, fileDirectory.listDirRecursively())
            } catch (Exception e) {
                println("Error from createStaticResourceIndex: ${e.getMessage()}")
            }
        }
        return staticResourceIndex
    }

    public void loadDocumentIndex() {

    }

    public void updateDocumentIndex() {

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
        exportHtaccess()
        exportGithubConfig()
    }

}
