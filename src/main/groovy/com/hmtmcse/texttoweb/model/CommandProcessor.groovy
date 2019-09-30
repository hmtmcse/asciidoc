package com.hmtmcse.texttoweb.model


import com.hmtmcse.jtfutil.io.JavaNio
import com.hmtmcse.jtfutil.parser.JsonReadWrite
import com.hmtmcse.jtfutil.parser.YmlReader
import com.hmtmcse.jtfutil.text.ReadWriteTextFile
import com.hmtmcse.shellutil.console.menu.OptionValues
import com.hmtmcse.texttoweb.Descriptor
import com.hmtmcse.texttoweb.TextToWebConst

trait CommandProcessor {

    abstract void process(OptionValues optionValues)

    private Boolean exportToFile(String content, String name, String location) {
        String path = JavaNio.concatPathString(location, name)
        if (backupFile(path)) {
            ReadWriteTextFile readWriteTextFile = new ReadWriteTextFile()
            try {
                content = readWriteTextFile.findAndReplaceInText(content, readWriteTextFile.copyFromMap(["!!com.hmtmcse.texttoweb.Descriptor\\n": ""]))
                return readWriteTextFile.writeStringToFile(location, name, content)
            } catch (Exception e) {
                return false
            }
        }
        return false
    }

    Boolean backupFile(String newFile, String oldFile = null) {
        if (JavaNio.isExist(newFile)) {
            backupFile(newFile + ".${TextToWebConst.BACK}", newFile)
        } else {
            if (oldFile) {
                return JavaNio.move(oldFile, newFile)
            }
        }
        return true
    }

    Boolean exportToJsonFile(String path, Descriptor descriptor) {
        JsonReadWrite jsonReadWrite = new JsonReadWrite()
        try {
            String json = jsonReadWrite.objectAsJsonStringPretty(descriptor)
            return exportToFile(json, "${TextToWebConst.DESCRIPTOR}.${TextToWebConst.JSON}", path)
        } catch (Exception e) {
            return false
        }
    }

    Boolean exportToYmlFile(String path, Descriptor descriptor) {
        YmlReader ymlReader = new YmlReader()
        try {
            String content = ymlReader.klassToStringSkipNull(descriptor)
            return exportToFile(content, "${TextToWebConst.DESCRIPTOR}.${TextToWebConst.YML}", path)
        } catch (Exception e) {
            return false
        }
    }

    Boolean exportToJsonNdYmlFile(String path, Descriptor descriptor) {
        if (exportToJsonFile(path, descriptor) && exportToYmlFile(path, descriptor)) {
            return true
        }
        return false
    }

    Boolean exportToHtmlFile() {

    }
}