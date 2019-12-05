package com.hmtmcse.asciidoc

import com.hmtmcse.jtfutil.text.ReadWriteTextFile
import com.hmtmcse.jtfutil.text.TextFileData
import org.asciidoctor.Asciidoctor
import org.asciidoctor.Attributes
import org.asciidoctor.Options

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
        Attributes attributes = new Attributes()
        attributes.setSourceHighlighter("coderay")
        Options options = new Options()
        options.setAttributes(attributes)

        return getAdoc().convert(text, options)
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
