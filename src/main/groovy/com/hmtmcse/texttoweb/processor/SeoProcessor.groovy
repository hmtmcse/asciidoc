package com.hmtmcse.texttoweb.processor

import com.hmtmcse.common.AsciiDocException
import com.hmtmcse.data.SeoEditData
import com.hmtmcse.te.TextToWebHtmlEngine
import com.hmtmcse.te.data.TextToWebEngineConfig
import com.hmtmcse.te.data.TextToWebEngineData
import com.hmtmcse.texttoweb.Descriptor
import com.hmtmcse.texttoweb.Seo
import com.hmtmcse.texttoweb.Tag
import com.hmtmcse.texttoweb.Topic
import com.hmtmcse.texttoweb.model.CommandProcessor

class SeoProcessor implements CommandProcessor {


    String getNameFromTextToWebEngineData(TextToWebEngineData textToWebEngineData) {
        if (textToWebEngineData.topicNav.meta && textToWebEngineData.topicNav.meta.get(textToWebEngineData.urlKey)?.name) {
            return textToWebEngineData.topicNav.meta.get(textToWebEngineData.urlKey).name
        }
        return ""
    }

    private Descriptor mergeSeoSettings(SeoEditData seoEditData, Descriptor descriptor) {
        String url = seoEditData.url
        if (url && url.startsWith("/")) {
            url = url.substring(1)
        }
        Integer index = 0
        if (descriptor.topics && url) {
            for (Topic topic : descriptor.topics) {
                if (topic.url.equals(url)) {
                    descriptor.topics.get(index).seo = seoEditData.getSeo()
                    descriptor.topics.get(index).name = seoEditData.navName
                    return descriptor
                }
                index++
            }
        }
        descriptor.seo = seoEditData.getSeo()
        return descriptor
    }

    String updateSeoSetting(SeoEditData seoEditData) {
        TextToWebEngineConfig config = new TextToWebEngineConfig()
        TextToWebHtmlEngine textToWebHtmlEngine = new TextToWebHtmlEngine()
        TextToWebEngineData textToWebEngineData = textToWebHtmlEngine.processTextToWebEngineData(seoEditData.url, config)
        println(textToWebEngineData.absolutePath)
        println(textToWebEngineData.descriptorAbsolutePath)
        Descriptor descriptor = mergeSeoSettings(seoEditData, textToWebEngineData.descriptor)
        exportToYmlFileFromAbsolutePath(textToWebEngineData.descriptorAbsolutePath, descriptor)
        return jsonStatus("Success");
    }

    SeoEditData getSeoSetting(String url) throws AsciiDocException {
        if (!url) {
            throw new AsciiDocException("Empty URL")
        }
        SeoEditData seoEditData = new SeoEditData().setUrl(url)
        TextToWebHtmlEngine textToWebHtmlEngine = new TextToWebHtmlEngine()
        TextToWebEngineConfig config = new TextToWebEngineConfig()
        TextToWebEngineData textToWebEngineData = textToWebHtmlEngine.processTextToWebEngineData(url, config)
        Seo seo = textToWebEngineData.getSeoData(new Seo())
        if (!seo.title) {
            seo.title = textToWebHtmlEngine.getPageTitle(textToWebEngineData, config)
        }
        seoEditData.navName = getNameFromTextToWebEngineData(textToWebEngineData)
        if (!seo.tags) {
            seo.addTags(new Tag().description(seoEditData.navName))
        }
        seoEditData.copy(seo)
        return seoEditData
    }


    String getSeoEditor(String url = ""){
        String modal = '<script src="/internal-asset/js/seo-edit.js"></script>'
        modal += '<div class="modal fade" data-url="/' + url + '" id="seo-editor-modal">'
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
