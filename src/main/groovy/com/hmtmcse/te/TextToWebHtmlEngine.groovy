package com.hmtmcse.te

import com.hmtmcse.common.AsciiDocException
import com.hmtmcse.fileutil.data.TextFileData
import com.hmtmcse.fileutil.fd.FDUtil
import com.hmtmcse.fileutil.fd.FileDirectory
import com.hmtmcse.fileutil.text.TextFile
import com.hmtmcse.te.data.InternalResponse
import com.hmtmcse.texttoweb.Config
import com.hmtmcse.texttoweb.TextToWebConst
import com.hmtmcse.texttoweb.common.ConfigLoader
import com.hmtmcse.tmutil.TStringUtil
import groovy.text.SimpleTemplateEngine

class TextToWebHtmlEngine {

    private FileDirectory fileDirectory
    private TextFile textFile
    private Config config


    public TextToWebHtmlEngine() {
        fileDirectory = new FileDirectory()
        textFile = new TextFile()
        config = ConfigLoader.getConfig()
    }

    public String getTextToWebData(String url) {

    }

    private String concatPath(String path) {
        return FDUtil.concatPath(config.source, path)
    }


    private InternalResponse getDescriptorName(String path) {
        InternalResponse internalResponse = new InternalResponse()
        internalResponse.descriptorName = TextToWebConst.OUTLINE + "." + TextToWebConst.YML
        String pathWithFile = FDUtil.concatPath(path, internalResponse.descriptorName)
        if (fileDirectory.isDirectory(path) && fileDirectory.isExist(pathWithFile)) {
            internalResponse.isOutline = true
        } else {
            internalResponse.descriptorName = TextToWebConst.DESCRIPTOR + "." + TextToWebConst.YML
        }
        return internalResponse
    }

    String process(String url) throws AsciiDocException {
        try {
            if (!url) {
                throw new AsciiDocException("Empty URL")
            }
            String trimUrl = TStringUtil.trimStartEndChar(url, "/")
            String path = concatPath(trimUrl)
            InternalResponse internalResponse = getDescriptorName(path)
            println(path)

            return trimUrl
        } catch (Exception e) {
            throw new AsciiDocException(e.getMessage())
        }
    }

    public String parse() {

        Config config = ConfigLoader.getConfig()

        TextFileData textFileData = textFile.fileToString(FDUtil.concatPath(config.template, "landing.html"))


        def text = "<html><head><title> Bismillah </title></head>"
        text += 'Dear "$firstname $lastname",\nSo nice to meet you in <% print city %>.\nSee you in ${month},\n${signed}'
        text += "</html>"

        def binding = ["firstname": "Sam", "lastname": "Pullara", "city": "San Francisco", "month": "December", "signed": "Groovy-Dev"]

        def engine = new SimpleTemplateEngine()
        def template = engine.createTemplate(text).make(binding)
        return textFileData.text
    }

}
