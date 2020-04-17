package com.hmtmcse.texttoweb;

import java.util.ArrayList;
import java.util.List;

public class Seo {

    public String title;
    public List<String> scripts;
    public List<Tag> tags;

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

    public Seo setTitle(String title) {
        this.title = title;
        return this;
    }

    public Seo addTags(Tag tag) {
        if (tags == null) {
            tags = new ArrayList<>();
        }
        tags.add(tag);
        return this;
    }

    public String getTitle() {
        return title;
    }

    public List<String> getScripts() {
        return scripts;
    }

    public void setScripts(List<String> scripts) {
        this.scripts = scripts;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }


}
