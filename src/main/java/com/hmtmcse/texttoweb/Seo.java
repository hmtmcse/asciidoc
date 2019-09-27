package com.hmtmcse.texttoweb;

import java.util.ArrayList;
import java.util.List;

public class Seo {
    public String title;
    public List<String> scripts;
    public List<Tags> tags;

    public Seo() {}

    public Seo(String title) {
        this.title = title;
    }

    public Seo addScript(String script) {
        if (scripts == null) {
            scripts = new ArrayList<>();
        }
        scripts.add(script);
        return this;
    }

    public Seo addTags(Tags tag) {
        if (tags == null) {
            tags = new ArrayList<>();
        }
        tags.add(tag);
        return this;
    }

}
