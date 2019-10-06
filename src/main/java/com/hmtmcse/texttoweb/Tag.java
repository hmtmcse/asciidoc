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
}
