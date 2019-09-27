package com.hmtmcse.texttoweb;

import java.util.LinkedHashMap;
import java.util.Map;

public class Tag {
    public String name;
    public String content;
    public Map<String, String> attrs = new LinkedHashMap<>();

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
        this.attrs.put("name", "description");
        this.attrs.put("content", content);
        return this;
    }

    public Tag canonical(String href){
        this.name = "meta";
        this.attrs.put("href", href);
        this.attrs.put("rel", "canonical");
        return this;
    }
}
