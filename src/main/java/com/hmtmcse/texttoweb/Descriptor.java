package com.hmtmcse.texttoweb;


import java.util.List;
import java.util.Map;

public class Descriptor {
    public Layout layout;
    public String defaultTitle;
    public List<Topic> topics = null;
    public Settings settings;
    public Map<String, Block> block = null;
    public Map<String, String> findReplace;
}
