package com.hmtmcse.texttoweb.processor

import com.hmtmcse.common.AsciiDocException
import com.hmtmcse.te.TextToWebHtmlEngine
import com.hmtmcse.te.data.TextToWebEngineConfig
import com.hmtmcse.te.data.TextToWebEngineData
import com.hmtmcse.texttoweb.Seo
import com.hmtmcse.texttoweb.Tag
import com.hmtmcse.texttoweb.model.CommandProcessor

class SeoProcessor implements CommandProcessor {


    Seo getSeoFromTextToWebEngineData(TextToWebEngineData textToWebEngineData) {
        if (textToWebEngineData.topicNav.meta && textToWebEngineData.topicNav.meta.get(textToWebEngineData.urlKey)?.seo) {
            return textToWebEngineData.topicNav.meta.get(textToWebEngineData.urlKey).seo
        }
        return new Seo()
    }

    Seo getSeoSetting(String url) throws AsciiDocException {
        if (!url) {
            throw new AsciiDocException("Empty URL")
        }
        TextToWebHtmlEngine textToWebHtmlEngine = new TextToWebHtmlEngine()
        TextToWebEngineConfig config = new TextToWebEngineConfig()
        TextToWebEngineData textToWebEngineData = textToWebHtmlEngine.processTextToWebEngineData(url, config)
        Seo seo = getSeoFromTextToWebEngineData(textToWebEngineData)
        if (!seo.title) {
            seo.title = textToWebHtmlEngine.getPageTitle(textToWebEngineData, config)
        }

        if (!seo.tags) {
            seo.addTags(new Tag().description(""))
        }
        return seo
    }


    String getSeoEditor(String url = ""){
        String modal = '<script src="/internal-asset/js/seo-edit.js"></script>'
        modal += '<div class="modal fade" data-url="' + url + '" id="seo-editor-modal">'
        modal += '<div class="modal-dialog modal-lg">'
        modal += '<div class="modal-content">'
        modal += '<div class="modal-header"><h5 class="modal-title" id="seo-editor-modal-label">Edit SEO</h5></div>'
        modal += '<div class="modal-body" id="seo-editor"></div>'
        modal += '<div class="modal-footer">'
        modal += '<button type="button" class="btn btn-secondary" data-dismiss="modal">Close</button>'
        modal += '<button type="button" class="btn btn-primary" id="update-form" >Update</button>'
        modal += '</div></div></div></div>'
        modal += '<button type="button" class="btn fixed-bottom" id="click-edit-seo">Edit SEO</button>'
        return modal
    }


}
