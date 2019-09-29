package com.hmtmcse.texttoweb.common

import com.hmtmcse.common.CommonConst
import com.hmtmcse.common.util.TMUtil
import com.hmtmcse.jtfutil.io.FDHelper
import com.hmtmcse.jtfutil.parser.YmlReader
import com.hmtmcse.texttoweb.Config

class ConfigLoader {

    public static String CONFIG_FILE = "config.yml"

    public static Config getConfig() {
        String path = CONFIG_FILE
        String pathUnderModuleDir = TMUtil.rootPath("asciidoc") + "/" + CONFIG_FILE
        Config config = new Config()
        try {
            if (FDHelper.instance().isExist(path)) {
                path = FDHelper.instance().getFile(path).getAbsolutePath()
            } else  if (FDHelper.instance().isExist(pathUnderModuleDir)) {
                path = FDHelper.instance().getFile(pathUnderModuleDir).getAbsolutePath()
            }else{
                path = null
            }
            if (path) {
                YmlReader ymlReader = new YmlReader()
                config = ymlReader.ymlAsKlass(path, Config.class)
            }
        } catch (Exception e) {}
        return config
    }

}
