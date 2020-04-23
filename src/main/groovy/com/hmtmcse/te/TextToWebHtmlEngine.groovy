package com.hmtmcse.te

import com.hmtmcse.asciidoc.AdocConverter
import com.hmtmcse.common.AsciiDocException
import com.hmtmcse.fileutil.data.FDInfo
import com.hmtmcse.fileutil.fd.FDUtil
import com.hmtmcse.fileutil.fd.FileDirectory
import com.hmtmcse.fileutil.text.TextFile
import com.hmtmcse.fm.TwFileUtil
import com.hmtmcse.te.data.*
import com.hmtmcse.te.taglib.HtmlTagHelper
import com.hmtmcse.texttoweb.Config
import com.hmtmcse.texttoweb.Descriptor
import com.hmtmcse.texttoweb.TextToWebConst
import com.hmtmcse.texttoweb.Topic
import com.hmtmcse.texttoweb.common.ConfigLoader
import com.hmtmcse.texttoweb.processor.SeoProcessor
import com.hmtmcse.texttoweb.processor.TextToWebProcessor
import com.hmtmcse.tmutil.TStringUtil
import com.hmtmcse.tmutil.TomTom

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

    public String getContentByURL(String url, TextToWebEngineConfig config = new TextToWebEngineConfig()) {
        String content = "Error From Text to Web Engine"
        try {
            content = process(url, config)
        } catch (Exception e) {
            e.printStackTrace()
            content = "<html><head><title>..:: 500 ::..</title></head><body>"
            content += "<h3>Internal Server Error </h1>"
            content += "<pre>"
            content += e.getMessage()
            content += "</pre>"
            content += "</body></html>"
        }
        println(url)
        return content
    }

    private String concatPath(String path) {
        return FDUtil.concatPath(config.source, path)
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
            Integer lengthTo
            TextToWebProcessor textToWebProcessor = new TextToWebProcessor()
            if (urlFragments) {
                for (String fragment : urlFragments) {
                    descriptorFile = FDUtil.concatPath(concatPath(relativePath), descriptorName)
                    if (fileDirectory.isExist(descriptorFile)) {
                        textToWebEngineData.descriptor = textToWebProcessor.loadYmlFromFile(descriptorFile)
                        textToWebEngineData.descriptorAbsolutePath = descriptorFile
                        return textToWebEngineData
                    }
                    temp = File.separator + fragment
                    lengthTo = relativePath.length() - temp.length()
                    if (lengthTo > 0) {
                        relativePath = relativePath.substring(0, lengthTo)
                    }
                }
            }
            if (url != null && url.equals("")) {
                textToWebEngineData.descriptorAbsolutePath = concatPath(descriptorName)
                textToWebEngineData.descriptor = textToWebProcessor.loadYmlFromFile(textToWebEngineData.descriptorAbsolutePath)
            }
            return textToWebEngineData
        } catch (Exception e) {
            e.printStackTrace()
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

    BreadcrumbData getBreadcrumb(String url, TextToWebEngineConfig config) {
        BreadcrumbData breadcrumbData = null
        TextToWebEngineData toWebEngineData = processTextToWebEngineData(url, config)
        if (toWebEngineData) {
            breadcrumbData = new BreadcrumbData(getBreadcrumb(toWebEngineData, config), url)
        }
        return breadcrumbData
    }

    List<BreadcrumbData> getBreadcrumbList(String url, TextToWebEngineConfig config) {
        List<String> list = TwFileUtil.splitUrl(url)
        List<BreadcrumbData> breadcrumb = []
        String concatUrl = "/"
        BreadcrumbData breadcrumbData = null
        Config appConfig = ConfigLoader.getConfig()
        breadcrumb.add(new BreadcrumbData(appConfig.homePageName, concatUrl))
        list.each { String urlFragment ->
            concatUrl = TomTom.concatWithSeparator(concatUrl, urlFragment, "/")
            breadcrumbData = getBreadcrumb(concatUrl, config)
            if (breadcrumbData) {
                breadcrumb.add(breadcrumbData)
            }
        }
        return breadcrumb
    }

    TextToWebEngineData processTextToWebEngineData(String url, TextToWebEngineConfig config) throws AsciiDocException {
        String trimUrl = TwFileUtil.trimSlash(url)
        String urlToPath = TwFileUtil.urlToPath(trimUrl)
        String path = concatPath(urlToPath)
        InternalResponse internalResponse = getDescriptorName(path)

        TextToWebEngineData textToWebEngineData = getDescriptorData(trimUrl, urlToPath, internalResponse.descriptorName)
        textToWebEngineData.urlKey = urlToUrlKey(textToWebEngineData.url)
        textToWebEngineData.absolutePath = path

        if (textToWebEngineData.descriptor) {
            Descriptor navigationDescriptor = textToWebEngineData.descriptor
            textToWebEngineData.topicNav = getNavigation(navigationDescriptor.topics, textToWebEngineData.urlKey, config.urlExtension)
            if (navigationDescriptor.relatedTopics) {
                textToWebEngineData.relatedTopicNav = getNavigation(navigationDescriptor.relatedTopics, textToWebEngineData.urlKey, config.urlExtension)
            }
        }
        return textToWebEngineData
    }

    String process(String url, TextToWebEngineConfig config) throws AsciiDocException {
        TextToWebPageData pageData = new TextToWebPageData()
        try {
            if (!url) {
                throw new AsciiDocException("Empty URL")
            }
            TextToWebEngineData textToWebEngineData = processTextToWebEngineData(url, config)
            if (textToWebEngineData.descriptor) {
                setupLayout(textToWebEngineData, config)
                pageData = getPageData(textToWebEngineData, config)
            }
            pageData.breadcrumb = getBreadcrumbList(url, config)
            pageData.textToWebEngineData = textToWebEngineData
        } catch (Exception e) {
            pageData.content = e.getMessage()
            pageData.title = config.errorTitle
            pageData.layout = config.page500
            e.printStackTrace()
        }
        return renderPage(pageData, config)
    }

    public String getBreadcrumb(TextToWebEngineData textToWebEngineData, TextToWebEngineConfig config) {
        String title = config.breadcrumbName
        if (textToWebEngineData.topicNav && textToWebEngineData.topicNav.meta && textToWebEngineData.topicNav.meta.get(textToWebEngineData.urlKey)?.breadcrumbName) {
            return textToWebEngineData.topicNav.meta.get(textToWebEngineData.urlKey).breadcrumbName
        }
        if (textToWebEngineData.descriptor) {
            title = textToWebEngineData.descriptor.name ?: textToWebEngineData.descriptor.defaultTitle
        }
        return title
    }

    public String getPageTitle(TextToWebEngineData textToWebEngineData, TextToWebEngineConfig config) {
        String title = config.defaultTitle
        if (textToWebEngineData.topicNav.meta && textToWebEngineData.topicNav.meta.get(textToWebEngineData.urlKey)?.title) {
            title = textToWebEngineData.topicNav.meta.get(textToWebEngineData.urlKey).title
        } else if (textToWebEngineData.descriptor.seo?.title) {
            title = textToWebEngineData.descriptor.seo.title
        } else if (textToWebEngineData.descriptor.defaultTitle) {
            title = textToWebEngineData.descriptor.defaultTitle
        }
        return title
    }

    public TextToWebPageData getPageData(TextToWebEngineData textToWebEngineData, TextToWebEngineConfig config) throws AsciiDocException {
        TextToWebPageData textToWebPageData = new TextToWebPageData()
        textToWebPageData.title = getPageTitle(textToWebEngineData, config)

        if (textToWebEngineData.topicNav?.nav) {
            textToWebPageData.nav = textToWebEngineData.topicNav.nav
        }

        if (textToWebEngineData.relatedTopicNav?.nav) {
            textToWebPageData.relatedNav = textToWebEngineData.relatedTopicNav.nav
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

    private String parseDocFileToHtml(String path, TextToWebEngineConfig config) throws AsciiDocException {
        try {
            AdocConverter adocConverter = new AdocConverter()
            return adocConverter.getHtmlFromFile(path)
        } catch (Exception ignore) {
            throw new AsciiDocException(ignore.getMessage())
        }
    }

    private String readSubDirectoryContent(String content, TextToWebEngineData textToWebEngineData, TextToWebEngineConfig config) {
        try {
            String path = concatPath(textToWebEngineData.url)
            if (fileDirectory.isDirectory(path)) {
                FDInfo info = fileDirectory.getDetailsInfo(path, false)
                path = FDUtil.concatPath(path, info.name) + (config.docFileExtension ? ".${config.docFileExtension}" : "")
                if (fileDirectory.isExist(path)) {
                    return parseDocFileToHtml(path, config)
                }
            }
        } catch (Exception ignore) {
        }
        return content
    }

    public String getPageContent(TextToWebEngineData textToWebEngineData, TextToWebEngineConfig config) throws AsciiDocException {
        String content = config.defaultContent
        if (textToWebEngineData.urlKey && textToWebEngineData.topicNav.meta.get(textToWebEngineData.urlKey)) {
            TopicNavItem meta = textToWebEngineData.topicNav.meta.get(textToWebEngineData.urlKey)
            String path
            if (meta.filePath) {
                path = concatPath(TwFileUtil.urlToPath(meta.filePath))
            } else {
                path = "${textToWebEngineData.absolutePath}.${config.docFileExtension}".toString()
            }
            if (path && fileDirectory.isExist(path)) {
                content = parseDocFileToHtml(path, config)
            }
        } else {
            content = readSubDirectoryContent(content, textToWebEngineData, config)
        }
        return content
    }


    public TextToWebEngineData setupLayout(TextToWebEngineData textToWebEngineData, TextToWebEngineConfig config) {
        if (textToWebEngineData.descriptor && textToWebEngineData.descriptor.layout.type) {
            textToWebEngineData.layout = "${textToWebEngineData.descriptor.layout.type}.${config.layoutFileExtension}".toString()
        } else {
            textToWebEngineData.layout = config.page404
        }
        return textToWebEngineData
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

                topicNavItem.seo = topic.seo

                if (topic.breadcrumbName) {
                    topicNavItem.breadcrumbName = topic.breadcrumbName
                } else {
                    topicNavItem.breadcrumbName = topic.name
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

    public String renderPage(TextToWebPageData pageData, TextToWebEngineConfig twConfig) throws AsciiDocException {
        try {
            Config config = ConfigLoader.getConfig()
            if (!pageData.layout) {
                pageData.layout = twConfig.page404
            }
            String layoutPath = FDUtil.concatPath(config.template, pageData.layout)
            if (!fileDirectory.isExist(layoutPath)) {
                throw new AsciiDocException("File not found.\nName: ${pageData.layout}, \nPath: ${layoutPath}")
            }
            HtmlTagHelper htmlTagHelper = new HtmlTagHelper(twConfig, pageData)
            pageData.tagHelper = htmlTagHelper
            FreemarkerTemplate freemarkerTemplate = new FreemarkerTemplate()

            String seoEditor = ""
            if (twConfig.isDevelopmentMode){
                SeoProcessor seoProcessor = new SeoProcessor()
                seoEditor = seoProcessor.getSeoEditor(pageData.textToWebEngineData.url)
            }
            return freemarkerTemplate.processTextWithTemplateDir(config.template, pageData.layout, [page: pageData, tagHelper: htmlTagHelper, seoEditor: seoEditor])
        } catch (Exception e) {
            e.printStackTrace()
            throw new AsciiDocException(e.getMessage())
        }
    }

}
