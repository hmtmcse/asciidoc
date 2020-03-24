package com.hmtmcse.te

import com.hmtmcse.asciidoc.AdocConverter
import com.hmtmcse.common.AsciiDocException
import com.hmtmcse.fileutil.data.TextFileData
import com.hmtmcse.fileutil.fd.FDUtil
import com.hmtmcse.fileutil.fd.FileDirectory
import com.hmtmcse.fileutil.text.TextFile
import com.hmtmcse.te.data.*
import com.hmtmcse.texttoweb.Config
import com.hmtmcse.texttoweb.Descriptor
import com.hmtmcse.texttoweb.TextToWebConst
import com.hmtmcse.texttoweb.Topic
import com.hmtmcse.texttoweb.common.ConfigLoader
import com.hmtmcse.texttoweb.processor.GenerateProcessor
import com.hmtmcse.tmutil.TStringUtil
import groovy.text.SimpleTemplateEngine

class TextToWebHtmlEngine {

    private FileDirectory fileDirectory
    private TextFile textFile
    private Config config
    private String slash = "/"
    private String defaultTitle = "..:: Bismillah ::.."


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

    private String urlToUrlKey(String url) {
        return TStringUtil.findReplace(url, slash, "_")
    }

    private String trimAndUrlToUrlKey(String url) {
        String trimUrl = TStringUtil.trimStartEndChar(url, slash)
        return urlToUrlKey(trimUrl)
    }

    public TextToWebEngineData getDescriptorData(String url, String relativePath, String descriptorName) throws AsciiDocException {
        try {
            List<String> urlFragments = []
            if (url && !url.equals("")) {
                urlFragments = TStringUtil.splitAsList(url, slash)
            }
            urlFragments = TStringUtil.reverseList(urlFragments)
            TextToWebEngineData textToWebEngineData = new TextToWebEngineData()
            textToWebEngineData.url = url
            textToWebEngineData.relativePath = relativePath
            String descriptorFile, temp
            GenerateProcessor generateProcessor = new GenerateProcessor()
            if (urlFragments) {
                for (String fragment : urlFragments) {
                    descriptorFile = FDUtil.concatPath(concatPath(relativePath), descriptorName)
                    if (fileDirectory.isExist(descriptorFile)) {
                        textToWebEngineData.descriptor = generateProcessor.loadYmlFromFile(descriptorFile)
                        return textToWebEngineData
                    }
                    temp = File.separator + fragment
                    relativePath = relativePath.substring(0, (relativePath.length() - temp.length()))
                }
            }
            textToWebEngineData.descriptor = generateProcessor.loadYmlFromFile(concatPath(descriptorName))
            return textToWebEngineData
        } catch (Exception e) {
            throw new AsciiDocException(e.getMessage())
        }
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

    String process(String url, TextToWebEngineConfig config = new TextToWebEngineConfig()) throws AsciiDocException {
        TextToWebPageData pageData = new TextToWebPageData()
        try {
            if (!url) {
                throw new AsciiDocException("Empty URL")
            }
            String trimUrl = TStringUtil.trimStartEndChar(url, slash)
            String urlToPath = urlToPath(trimUrl)
            String path = concatPath(urlToPath)
            InternalResponse internalResponse = getDescriptorName(path)

            TextToWebEngineData textToWebEngineData = getDescriptorData(trimUrl, urlToPath, internalResponse.descriptorName)
            textToWebEngineData.urlKey = urlToUrlKey(textToWebEngineData.url)
            textToWebEngineData.absolutePath = path
            Descriptor navigationDescriptor = textToWebEngineData.descriptor
            textToWebEngineData.topicNav = getNavigation(navigationDescriptor.topics, textToWebEngineData.urlKey, config.urlExtension)
            setupLayout(textToWebEngineData, config)
        } catch (Exception e) {
            pageData.content = e.getMessage()
            pageData.title = config.errorTitle
            pageData.layout = config.page500
        }
        return renderPage(pageData)
    }

    public String getPageTitle(TextToWebEngineData textToWebEngineData, TextToWebEngineConfig config) {
        String title = config.defaultTitle
        if (textToWebEngineData.topicNav.meta[textToWebEngineData.urlKey].title) {
            title = textToWebEngineData.topicNav.meta[textToWebEngineData.urlKey].title
        } else if (textToWebEngineData.descriptor.defaultTitle) {
            title = textToWebEngineData.descriptor.defaultTitle
        }
        return title
    }

    public TextToWebPageData getPageData(TextToWebEngineData textToWebEngineData, TextToWebEngineConfig config) throws AsciiDocException {
        TextToWebPageData textToWebPageData = new TextToWebPageData()
        textToWebPageData.title = getPageTitle(textToWebEngineData, config)
        if (textToWebEngineData.topicNav.nav) {
            textToWebPageData.nav = textToWebEngineData.topicNav.nav
        }

        if (textToWebEngineData.descriptor.blocks) {
            textToWebPageData.blocks = textToWebEngineData.descriptor.blocks
        }

        if (textToWebEngineData.descriptor.topics) {
            textToWebPageData.topics = textToWebEngineData.descriptor.topics
        }

        if (textToWebEngineData.layout) {
            textToWebPageData.layout = textToWebEngineData.layout
        }

        textToWebPageData.content = getPageContent(textToWebEngineData, config)
        return textToWebPageData
    }

    public String getPageContent(TextToWebEngineData textToWebEngineData, TextToWebEngineConfig config) throws AsciiDocException {
        String content = config.defaultContent
        if (textToWebEngineData.urlKey && textToWebEngineData.topicNav.meta.get(textToWebEngineData.urlKey)) {
            TopicNavItem meta = textToWebEngineData.topicNav.meta.get(textToWebEngineData.urlKey)
            String path
            if (meta.filePath) {
                path = concatPath(urlToPath(meta.filePath))
            } else {
                path = FDUtil.concatPath(textToWebEngineData.absolutePath, ".adoc")
            }
            if (path && fileDirectory.isExist(path)) {
                try {
                    AdocConverter adocConverter = new AdocConverter()
                    content = adocConverter.getHtmlFromFile(path)
                } catch (Exception ignore) {
                    throw new AsciiDocException(ignore.getMessage())
                }
            }
        }
        return content
    }

    public void setupLayout(TextToWebEngineData textToWebEngineData, TextToWebEngineConfig config) {
        if (textToWebEngineData.descriptor && textToWebEngineData.descriptor.layout.type) {
            textToWebEngineData.layout = "${textToWebEngineData.descriptor.layout.type}.html"
        } else {
            textToWebEngineData.layout = config.page404
        }
    }

    public TopicNav getNavigation(List<Topic> topics, String currentUrlKey, String extension = "") {
        TopicNav topicNav = new TopicNav()
        if (topics) {
            Integer itemIndex = 1
            Integer navIndex = 1
            TopicNavItem topicNavItem
            String navKey
            for (Topic topic : topics) {
                topicNavItem = new TopicNavItem()
                navKey = "";
                if (!topic) {
                    continue
                }

                if (topic.seo && topic.seo.title) {
                    topicNavItem.title = topic.seo.title
                } else if (topic.name) {
                    topicNavItem.title = topic.name
                } else {
                    topicNavItem.title = defaultTitle
                }

                if (topic.url && topic.url != "#") {
                    topicNavItem.url = topic.url + extension
                    navKey = trimAndUrlToUrlKey(topic.url)
                } else {
                    topicNavItem.url = "#"
                    navKey = "#-" + navIndex
                    navIndex++
                }

                if (topic.name) {
                    topicNavItem.name = topic.name
                } else {
                    topicNavItem.name = "Nav Item " + itemIndex
                    itemIndex++
                }

                if (topic.seo) {
                    topicNavItem.seo = topic.seo
                }

                if (topic.filePath) {
                    topicNavItem.filePath = topic.filePath
                }

                if (navKey == currentUrlKey) {
                    topicNavItem.active = "active"
                }

                topicNav.nav[navKey] = topicNavItem
                topicNav.meta[navKey] = topicNavItem

                if (topic.childs) {
                    TopicNav topicNavTemp = getNavigation(topic.childs, currentUrlKey, extension)
                    topicNav.nav[navKey].childs = topicNavTemp.nav
                    topicNavTemp.meta.each { key, value ->
                        topicNav.meta[key] = value
                    }
                }
            }
        }
        return topicNav
    }

    public String renderPage(TextToWebPageData pageData) throws AsciiDocException {
        try {
            Config config = ConfigLoader.getConfig()
            TextFileData textFileData = textFile.fileToString(FDUtil.concatPath(config.template, pageData.layout))
            def engine = new SimpleTemplateEngine()
            def template = engine.createTemplate(textFileData.text).make([page: pageData])
            return template.toString()
        } catch (Exception e) {
            throw new AsciiDocException(e.getMessage())
        }
    }

}
