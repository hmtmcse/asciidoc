package com.hmtmcse.texttoweb.processor

import com.hmtmcse.shellutil.console.menu.OptionValues
import com.hmtmcse.texttoweb.Config
import com.hmtmcse.texttoweb.TextToWebConst
import com.hmtmcse.texttoweb.common.ConfigLoader
import com.hmtmcse.texttoweb.model.CommandProcessor

class GenerateProcessor implements CommandProcessor {

    private Config config

    public GenerateProcessor(){
        config = ConfigLoader.getConfig()
    }

    @Override
    void process(OptionValues optionValues) {
        String command = optionValues.valueAsString(TextToWebConst.DESCRIPTOR)
        switch (command){
            case TextToWebConst.LANDING:
                landing()
                break
            case TextToWebConst.TOPICS:
                topics()
                break
            case TextToWebConst.OUTLINE:
                outline()
                break
            case TextToWebConst.DETAILS:
                details()
                break
            default:
                println("-------------")
        }
    }

    void landing(){}

    void topics(){}

    void outline(){}

    void details(){}

}
