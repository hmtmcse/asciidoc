package com.hmtmcse.texttoweb;

import java.util.LinkedHashMap;
import java.util.Map;

public class Tag {

    public String name;
    public String content;
    public Map<String, String> attrs;

    public Tag() {}

    public Tag(String name, String content) {
        this.name = name;
        this.content = content;
    }

    public Tag addAttrs(String name, String value) {
        if (attrs == null) {
            attrs = new LinkedHashMap<>();
        }
        attrs.put(name, value);
        return this;
    }


    public Tag description(String content){
        this.name = "meta";
        addAttrs("name", "description");
        addAttrs("content", content);
        return this;
    }

    public Tag canonical(String href){
        this.name = "meta";
        addAttrs("rel", "canonical");
        addAttrs("href", href);
        return this;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Map<String, String> getAttrs() {
        return attrs;
    }

    public void setAttrs(Map<String, String> attrs) {
        this.attrs = attrs;
    }
}
