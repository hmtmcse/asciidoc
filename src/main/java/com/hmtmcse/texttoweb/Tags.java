package com.hmtmcse.texttoweb;

import java.util.ArrayList;
import java.util.List;

public class Tags {
    public String placedIn;
    public List<Tag> tag;


    public Tags(String placedIn) {
        this.placedIn = placedIn;
    }

    public Tags addTag(String name, String content) {
        if (tag == null) {
            tag = new ArrayList<>();
        }
        tag.add(new Tag(name, content));
        return this;
    }

    public Tags addTag(Tag _tag) {
        if (tag == null) {
            tag = new ArrayList<>();
        }
        tag.add(_tag);
        return this;
    }

}
