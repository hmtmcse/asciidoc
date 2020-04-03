package com.hmtmcse.te.taglib

import com.hmtmcse.common.AsciiDocConstant
import com.hmtmcse.te.data.HtmlExportType
import com.hmtmcse.te.data.TextToWebEngineConfig
import com.hmtmcse.texttoweb.Config
import com.hmtmcse.texttoweb.common.ConfigLoader

class HtmlTagHelper {

    private TextToWebEngineConfig config
    private Config appConfig

    public HtmlTagHelper(TextToWebEngineConfig twConfig) {
        config = twConfig
        appConfig = ConfigLoader.getConfig()
    }

    private String concatUrl(String url) {
        String _url = appConfig.urlStartWith
        if (!_url) {
            _url = "/"
        } else if (_url && !_url.startsWith("/")) {
            _url = "/" + _url
        }
        if (!_url.endsWith("/")) {
            _url += "/"
        }
        if (url && url.startsWith("/")) {
            url = url.substring(1)
        }
        return _url + (url ?: "")
    }

    private String processLink(String url, Boolean isExtension = false) {
        if (url && url.equals("#")) {
            return url
        } else if (!config.isFromWebsite) {
            url = concatUrl(url)
        }
        if (config.htmlExportType == HtmlExportType.ONLINE) {
            if (url && !url.startsWith("/")) {
                return "/${url}"
            }
        } else if (config.htmlExportType == HtmlExportType.OFFLINE) {
            if (url && url.startsWith("/")) {
                return url.substring(1)
            }
        }
        if (url && url.endsWith("/") && url.length() > 1) {
            url = url.substring(0, url.length() - 1)
        }

        if (isExtension && config.exportFileExtension && !url.endsWith("/")) {
            url += "${config.getExportFileExtensionByNullCheck()}".toString()
        }

        return url
    }

    private String processOnlySlash(String url) {
        if (url && url.equals("/") && config.exportFileExtension) {
            return url + AsciiDocConstant.bismillahFile
        }
        return url
    }

    public String addCssTag(String cssLink) {
        return "<link rel='stylesheet' href='${processLink(cssLink, false)}'>"
    }

    public String addJsTag(String jsLink) {
        return "<script src='${processLink(jsLink, false)}' type='text/javascript' ></script>"
    }

    public String twUrl(String url, Boolean isExtension = false) {
        return processLink(url, isExtension)
    }


    public String twUrlWithExtension(String url) {
        return processLink(processOnlySlash(url), true)
    }

}
