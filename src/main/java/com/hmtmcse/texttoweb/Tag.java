package com.hmtmcse.texttoweb;

import java.util.LinkedHashMap;
import java.util.Map;

public class Tag {
    public String name;
    public String content;
    public Map<String, String> attrs;

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
}
