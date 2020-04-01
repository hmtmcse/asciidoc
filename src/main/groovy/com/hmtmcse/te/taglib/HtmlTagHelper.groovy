package com.hmtmcse.te.taglib

import com.hmtmcse.te.data.HtmlExportType
import com.hmtmcse.te.data.TextToWebEngineConfig

class HtmlTagHelper {

    private TextToWebEngineConfig config

    public HtmlTagHelper(TextToWebEngineConfig twConfig) {
        config = twConfig
    }

    private String processLink(String url) {
        if (url && url.equals("#")){
            return url
        }else if (config.htmlExportType == HtmlExportType.ONLINE) {
            if (url && !url.startsWith("/")) {
                return "/${url}"
            }
        } else if (config.htmlExportType == HtmlExportType.OFFLINE) {
            if (url && url.startsWith("/")) {
                return url.substring(1)
            }
        }
        return url
    }

    public String addCssTag(String cssLink) {
        return "<link rel='stylesheet' href='${processLink(cssLink)}'>"
    }

    public String addJsTag(String jsLink) {
        return "<script src='${processLink(jsLink)}' type='text/javascript' ></script>"
    }

    public String twUrl(String url) {
        return processLink(url)
    }

}
