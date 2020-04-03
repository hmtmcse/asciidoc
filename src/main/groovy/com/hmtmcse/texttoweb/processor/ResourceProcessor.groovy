package com.hmtmcse.texttoweb.processor

import com.hmtmcse.common.AsciiDocConstant
import com.hmtmcse.fileutil.fd.FDUtil
import com.hmtmcse.fileutil.fd.FileDirectory
import com.hmtmcse.fileutil.text.TextFile
import com.hmtmcse.parser4java.JsonProcessor
import com.hmtmcse.texttoweb.Config
import com.hmtmcse.texttoweb.resource.StaticResourceIndex

class ResourceProcessor {

    private Config config
    private TextFile textFile
    private FileDirectory fileDirectory
    private JsonProcessor jsonProcessor
    private StaticResourceIndex templateFileLogIndex
    private StaticResourceIndex docFileLogIndex
    private StaticResourceIndex docResourcesFileLogIndex

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
