package com.hmtmcse.texttoweb.model

import com.hmtmcse.asciidoc.AdocConverter
import com.hmtmcse.jtfutil.io.JavaNio
import com.hmtmcse.jtfutil.parser.JsonReadWrite
import com.hmtmcse.jtfutil.parser.YmlReader
import com.hmtmcse.jtfutil.text.ReadWriteTextFile
import com.hmtmcse.parser4java.YamlProcessor
import com.hmtmcse.shellutil.console.menu.OptionValues
import com.hmtmcse.texttoweb.Descriptor
import com.hmtmcse.texttoweb.TextToWebConst
import com.hmtmcse.texttoweb.data.ProcessRequest
import com.hmtmcse.texttoweb.data.ProcessTask

trait CommandProcessor {

    public ProcessRequest processRequest
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

    public String jsonDescriptorFileName(){
        return "${TextToWebConst.DESCRIPTOR}.${TextToWebConst.JSON}"
    }

    public String ymltOutlineFileName(){
        return "${TextToWebConst.OUTLINE}.${TextToWebConst.YML}"
    }

    public String jsonOutlineFileName(){
        return "${TextToWebConst.OUTLINE}.${TextToWebConst.JSON}"
    }

    public String ymlDescriptorFileName(){
        return "${TextToWebConst.DESCRIPTOR}.${TextToWebConst.YML}"
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


    Boolean exportToJsonFile(String path, Descriptor descriptor, String name = jsonDescriptorFileName()) {
        JsonReadWrite jsonReadWrite = new JsonReadWrite()
        try {
            String json = jsonReadWrite.objectAsJsonStringPretty(descriptor)
            return exportToFile(json, name, path)
        } catch (Exception e) {
            return false
        }
    }


    String exportToYmlText(Descriptor descriptor) {
        YmlReader ymlReader = new YmlReader()
        try {
            return ymlReader.klassToStringSkipNull(descriptor)
        } catch (Exception e) {
            return null
        }
    }

    Boolean exportToOutlineYmlFile(String path, Descriptor descriptor) {
        try {
            String content = exportToYmlText(descriptor)
            return exportToFile(content, ymltOutlineFileName(), path)
        } catch (Exception e) {
            return false
        }
    }

    Boolean exportToYmlFile(String path, Descriptor descriptor) {
        try {
            if (processRequest.task.equals(ProcessTask.REPORT)) {
                return false
            }
            String content = exportToYmlText(descriptor)
            return exportToFile(content, ymlDescriptorFileName(), path)
        } catch (Exception e) {
            return false
        }
    }


    Descriptor loadJsonFromFile(String path) {
        JsonReadWrite jsonReadWrite = new JsonReadWrite()
        try {
            String pathWithName = JavaNio.concatPathString(path, jsonDescriptorFileName())
            return jsonReadWrite.readJsonFileAsKlass(pathWithName, Descriptor.class)
        } catch (Exception e) {
            return null
        }
    }

    Descriptor loadYmlFromFile(String path) {
        YamlProcessor yamlProcessor = new YamlProcessor()
        try {
            return yamlProcessor.ymlAsNestedKlass(path, Descriptor.class)
        } catch (Exception e) {
            println(e.getMessage())
            return null
        }
    }

    Boolean exportToJsonNdYmlFile(String path, Descriptor descriptor) {
        if (exportToJsonFile(path, descriptor) && exportToYmlFile(path, descriptor)) {
            return true
        }
        return false
    }

    Boolean exportAdocToHtmlFile(String sourceAdoc, String outPath, String name) {
        AdocConverter adocConverter = new AdocConverter()
        String htmlContent = ""
        if (JavaNio.isExist(sourceAdoc)){
            htmlContent = adocConverter.getHtmlFromFile(sourceAdoc)
        }
        return exportToFile(htmlContent, name, outPath)
    }

    public removeAdocExtension(String text){
        return text.replace(".${TextToWebConst.ADOC}", "")
    }

    public String makeHumReadableWithoutExt(String text) {
        return makeHumReadable(removeAdocExtension(text))
    }


    public String makeHumReadable(String text) {
        String underscoreToSpace = text.replaceAll("(_+)([A-Za-z0-9_])", {
            Object[] it -> " " + it[2]?.trim()
        })

        String camelCaseToSpace =  underscoreToSpace.replaceAll("(\\s*[A-Z])", {
            Object[] it -> " " + it[0]?.trim()
        })

        String hyphenToSpace = camelCaseToSpace.replaceAll("\\s*[\\-_]*\\s*", {
            Object[] it -> it[0].equals("")?"":" "
        })

        if (hyphenToSpace){
            return hyphenToSpace.trim().toLowerCase().capitalize()
        }
        return text
    }


    public String pathToURL(String path){
        if (path){
            path = path.replace(File.separator, "/")
            if (path.startsWith("/")){
                path = path.substring(1)
            }

            if (path.endsWith("/")){
                path = path.substring(0, path.length() - 1)
            }
        }
        return path
    }
}