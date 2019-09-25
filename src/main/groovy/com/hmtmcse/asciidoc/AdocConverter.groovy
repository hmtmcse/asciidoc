package com.hmtmcse.asciidoc

import com.hmtmcse.jtfutil.text.ReadWriteTextFile
import com.hmtmcse.jtfutil.text.TextFileData
import org.asciidoctor.Asciidoctor

class AdocConverter {

    public aDocOptions = new HashMap<String, Object>()
    public ReadWriteTextFile readWriteTextFile = null

    public AdocConverter(){
        this.readWriteTextFile = new ReadWriteTextFile()
    }

    public Asciidoctor getAdoc(){
        return Asciidoctor.Factory.create()
    }

    public String getHtmlFromText(String text){
        return getAdoc().convert(text, aDocOptions)
    }


    public String getHtmlFromFile(String location){
        try{
            TextFileData textFileData = readWriteTextFile.readFileToString(location)
            return getHtmlFromText(textFileData.text)
        }catch(Exception e){
            return ""
        }
    }

    public static AdocConverter instance(){
        return new AdocConverter()
    }

}
