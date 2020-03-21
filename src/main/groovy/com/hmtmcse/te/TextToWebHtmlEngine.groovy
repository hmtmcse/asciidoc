package com.hmtmcse.te

import com.hmtmcse.common.AsciiDocException
import com.hmtmcse.fileutil.data.TextFileData
import com.hmtmcse.fileutil.fd.FDUtil
import com.hmtmcse.fileutil.fd.FileDirectory
import com.hmtmcse.fileutil.text.TextFile
import com.hmtmcse.te.data.InternalResponse
import com.hmtmcse.te.data.TextToWebEngineData
import com.hmtmcse.texttoweb.Config
import com.hmtmcse.texttoweb.TextToWebConst
import com.hmtmcse.texttoweb.common.ConfigLoader
import com.hmtmcse.texttoweb.processor.GenerateProcessor
import com.hmtmcse.tmutil.TStringUtil
import groovy.text.SimpleTemplateEngine

class TextToWebHtmlEngine {

    private FileDirectory fileDirectory
    private TextFile textFile
    private Config config
    private String slash = "/"


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

    private String urlToPath(String url) {
        return TStringUtil.findReplace(url, slash, File.separator)
    }

    public TextToWebEngineData getDescriptorData(String url, String relativePath, String descriptorName) {
        List<String> urlFragments = TStringUtil.splitAsList(url, slash)
        urlFragments = TStringUtil.reverseList(urlFragments)

        TextToWebEngineData textToWebEngineData = new TextToWebEngineData()
        textToWebEngineData.url = url
        textToWebEngineData.relativePath = relativePath

        String descriptorFile, temp
        GenerateProcessor generateProcessor = new GenerateProcessor()
        if (urlFragments) {
            for (String fragment : urlFragments) {
                descriptorFile = FDUtil.concatPath(concatPath(relativePath), descriptorName)
                if (fileDirectory.isDirectory(descriptorFile)) {
                    textToWebEngineData.descriptor = generateProcessor.loadYmlFromFile(descriptorFile)
                    return textToWebEngineData
                }
                temp = File.separator + fragment
                relativePath = relativePath.substring(0, (relativePath.length() - temp.length()))
            }
        }
        textToWebEngineData.descriptor = generateProcessor.loadYmlFromFile(concatPath(descriptorName))
        return textToWebEngineData
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
            String urlToPath = urlToPath(trimUrl)
            String path = concatPath(urlToPath)
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
