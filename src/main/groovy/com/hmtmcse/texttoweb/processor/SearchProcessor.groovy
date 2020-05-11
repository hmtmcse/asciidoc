package com.hmtmcse.texttoweb.processor

import org.jsoup.Jsoup
import org.jsoup.nodes.Document

class SearchProcessor {

    private void processIndex(Document document) {

    }

    private String minify(Document document, String html) {
        if (document) {
            document.outputSettings().indentAmount(0).prettyPrint(false)
            return html = document.toString()
//            return html.toString().replaceAll(">\\s+", ">").replaceAll("\\s+<", "<")
        }
        return html
    }

    public String process(String url, String html) {
        try {
            Document document = Jsoup.parse(html)
            processIndex(document)
            return minify(document, html)
        } catch (Exception e) {
            println("Error from Search index: ${e.getMessage()}")
        }
        return html
    }

}
