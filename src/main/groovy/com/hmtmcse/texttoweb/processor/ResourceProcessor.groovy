package com.hmtmcse.texttoweb.processor

import com.hmtmcse.common.AsciiDocConstant
import com.hmtmcse.fileutil.fd.FDUtil
import com.hmtmcse.fileutil.fd.FileDirectory
import com.hmtmcse.fileutil.text.TextFile
import com.hmtmcse.texttoweb.Config

class ResourceProcessor {

    private Config config
    private TextFile textFile
    private FileDirectory fileDirectory

    ResourceProcessor(Config config) {
        this.config = config
        textFile = new TextFile()
        fileDirectory = new FileDirectory()
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
