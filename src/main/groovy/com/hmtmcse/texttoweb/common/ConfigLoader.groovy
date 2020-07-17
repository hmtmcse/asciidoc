package com.hmtmcse.texttoweb.common

import com.hmtmcse.common.AsciiDocConstant
import com.hmtmcse.fileutil.fd.FileDirectory
import com.hmtmcse.parser4java.YamlProcessor
import com.hmtmcse.shellutil.console.menu.OptionValues
import com.hmtmcse.texttoweb.Config
import com.hmtmcse.texttoweb.TextToWebConst

class ConfigLoader {

    public static String CONFIG_FILE = "config.yml"

    public static Config getConfig(OptionValues optionValues = null) {
        String path = CONFIG_FILE
        String pathUnderModuleDir = "all-plugins/asciidoc/" + CONFIG_FILE
        String pathUnderEngineDir = "libraries/asciidoc/" + CONFIG_FILE
        Config config = new Config()
        try {
            if (FileDirectory.instance().isExist(path)) {
                path = FileDirectory.instance().getFile(path).getAbsolutePath()
            } else if (FileDirectory.instance().isExist(pathUnderEngineDir)) {
                path = FileDirectory.instance().getFile(pathUnderEngineDir).getAbsolutePath()
            } else if (FileDirectory.instance().isExist(pathUnderModuleDir)) {
                path = FileDirectory.instance().getFile(pathUnderModuleDir).getAbsolutePath()
            } else {
                path = null
            }
            if (path) {
                YamlProcessor yamlProcessor = new YamlProcessor()
                config = yamlProcessor.ymlAsNestedKlass(path, Config.class)
            }
        } catch (Exception e) {
        }

        if (optionValues) {
            String source = optionValues.valueAsString(TextToWebConst.SOURCE)
            String out = optionValues.valueAsString(TextToWebConst.OUT)
            if (source) {
                config.source = source
            }
            if (out) {
                config.out = out
            }
        }
        if (config) {
            config.ignore.add(".git")
            config.ignore.add(".gitignore")
            config.ignore.add("static-files")
            config.ignore.add("data-file")
            config.ignore.add(".back")
            config.ignore.add(".idea")
            config.ignore.add(".gitignore")
            config.ignore.add(".vscode")
            config.ignore.add(AsciiDocConstant.text2webData)
        }
        return config
    }

}
