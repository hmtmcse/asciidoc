package com.hmtmcse.te.data

import com.hmtmcse.texttoweb.Seo

class TopicNavItem {

    public String name
    public String title
    public String url
    public String active = ""
    public String breadcrumbName
    public Seo seo
    public String filePath = null
    public Map<String, TopicNavItem> childs = [:]


}
