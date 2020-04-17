package com.hmtmcse.te.data

import com.hmtmcse.te.taglib.HtmlTagHelper
import com.hmtmcse.texttoweb.Block
import com.hmtmcse.texttoweb.Topic

class TextToWebPageData {

    public String title = ""
    public String content  = ""
    public String layout = null
    public Map<String, TopicNavItem> nav = [:]
    public Map<String, TopicNavItem> relatedNav = [:]
    public Map<String, Block> blocks = [:]
    public List<Topic> topics = []
    public HtmlTagHelper tagHelper
    public List<BreadcrumbData> breadcrumb = []
    public TextToWebEngineData textToWebEngineData


    public String getLeftNavHtml(Map<String, TopicNavItem> nav) {
        String html = "", nestedNav = ""
        if (nav) {
            nav.each { String key, TopicNavItem navItem ->
                nestedNav = ""
                if (navItem.childs) {
                    nestedNav = "<ul>";
                    nestedNav += getLeftNavHtml(navItem.childs);
                    nestedNav += "</ul>";
                }
                html += "<li><a class='" + navItem.active + "' href='" + tagHelper.twUrlWithExtension(navItem.url) + "'>" + navItem.name + "</a>";
                html += nestedNav
                html += "</li>"
            }
        }
        return html;
    }

}
