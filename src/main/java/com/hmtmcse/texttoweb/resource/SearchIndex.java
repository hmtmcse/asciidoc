package com.hmtmcse.texttoweb.resource;

import java.util.LinkedHashMap;

public class SearchIndex {
    public LinkedHashMap<String, SearchIndexData> deletedIndex = new LinkedHashMap<>();
    public LinkedHashMap<String, SearchIndexData> currentIndex = new LinkedHashMap<>();
}
