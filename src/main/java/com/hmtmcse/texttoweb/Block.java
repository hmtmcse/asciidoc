package com.hmtmcse.texttoweb;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Block {
    public String title;
    public String name;
    public String url;
    public String id;
    public String thumb;
    public String image;
    public List<Topic> childs;
    public Map<String, Block> blocks = null;

    public Block(String title, String url) {
        this.title = title;
        this.url = url;
    }

    public Block addChild(Topic topic) {
        if (childs == null) {
            childs = new ArrayList<>();
        }
        childs.add(topic);
        return this;
    }

    public Block addBlock(String name, Block block) {
        if (blocks == null) {
            blocks = new LinkedHashMap<>();
        }
        blocks.put(name, block);
        return this;
    }
}
