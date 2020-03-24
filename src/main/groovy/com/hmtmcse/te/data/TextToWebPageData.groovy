package com.hmtmcse.te.data

import com.hmtmcse.texttoweb.Block
import com.hmtmcse.texttoweb.Topic

class TextToWebPageData {

    public String title = ""
    public String content  = ""
    public String layout = "404"
    public Map<String, TopicNavItem> nav = [:]
    public Map<String, Block> blocks = [:]
    public List<Topic> topics = []
}
