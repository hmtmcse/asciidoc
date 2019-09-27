package com.hmtmcse.texttoweb;

import java.util.ArrayList;
import java.util.List;

public class Tags {
    public String placedIn = TextToWebConst.HEAD;
    public List<Tag> tag;

    public Tags addTag(String name, String content) {
        if (tag == null) {
            tag = new ArrayList<>();
        }
        tag.add(new Tag(name, content));
        return this;
    }
}
