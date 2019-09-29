package com.hmtmcse.texttoweb.model

import com.hmtmcse.shellutil.console.menu.OptionValues

interface CommandProcessor {
    void process(OptionValues optionValues)
}