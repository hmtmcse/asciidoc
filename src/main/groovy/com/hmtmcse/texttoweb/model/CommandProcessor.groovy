package com.hmtmcse.texttoweb.model

import com.hmtmcse.shellutil.console.menu.OptionValues

trait CommandProcessor {

    abstract void process(OptionValues optionValues)

    Boolean exportToFile(){

    }

    Boolean exportToJsonFile(){

    }

    Boolean exportToYmlFile(){

    }

    Boolean exportToHtmlFile(){

    }
}