package com.hmtmcse.texttoweb.model

import com.hmtmcse.asciidoc.AdocConverter
import com.hmtmcse.fileutil.data.FDInfo
import com.hmtmcse.fileutil.fd.FDUtil
import com.hmtmcse.fileutil.fd.FileDirectory
import com.hmtmcse.fileutil.text.TextFile
import com.hmtmcse.jtfutil.parser.JsonReadWrite
import com.hmtmcse.parser4java.JsonProcessor
import com.hmtmcse.parser4java.YamlProcessor
import com.hmtmcse.shellutil.console.menu.OptionValues
import com.hmtmcse.texttoweb.Descriptor
import com.hmtmcse.texttoweb.TextToWebConst
import com.hmtmcse.texttoweb.data.ProcessRequest
import com.hmtmcse.texttoweb.data.ProcessTask

trait CommandProcessor {

    public ProcessRequest processRequest
    public String loadedDescriptorName = ""


    private Boolean exportToFile(String content, String name, String location) {
        String path = FDUtil.concatPath(location, name)
        if (backupFile(path)) {
            TextFile textFile = new TextFile()
            try {
                content = textFile.findOnlyReplaceInText(content, ["!!com.hmtmcse.texttoweb.Descriptor\\n": ""])
                return textFile.stringToFile(location, name, content)
            } catch (Exception e) {
                return false
            }
        }
        return false
    }

    public String jsonStatus(String message = "", String status = "success") {
        JsonProcessor jsonProcessor = new JsonProcessor()
        return jsonProcessor.klassToString([status: status, message: message])
    }

    public void init(ProcessRequest processRequest) {
        this.processRequest = processRequest
    }

    public String jsonDescriptorFileName() {
        return "${TextToWebConst.DESCRIPTOR}.${TextToWebConst.JSON}"
    }


    public String ymltOutlineFileName() {
        return "${TextToWebConst.OUTLINE}.${TextToWebConst.YML}"
    }

    public String jsonOutlineFileName() {
        return "${TextToWebConst.OUTLINE}.${TextToWebConst.JSON}"
    }

    public String ymlDescriptorFileName() {
        return "${TextToWebConst.DESCRIPTOR}.${TextToWebConst.YML}"
    }

    Boolean backupFile(String saveTo, String backupTo = null, Integer index = 0) {
        backupTo = backupTo ?: saveTo
        if (FileDirectory.instance().isExist(backupTo)) {
            backupFile(saveTo, saveTo + "-${index}.${TextToWebConst.BACK}", index + 1)
        } else if (FileDirectory.instance().isExist(saveTo)) {
            return FileDirectory.instance().move(saveTo, backupTo)
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
        YamlProcessor yamlProcessor = new YamlProcessor()
        try {
            return yamlProcessor.klassToString(descriptor)
        } catch (Exception e) {
            return null
        }
    }

    Boolean exportToOutlineYmlFile(String path, Descriptor descriptor) {
        return exportToYmlFile(path, descriptor, ymltOutlineFileName())
    }

    Boolean exportToYmlFileFromAbsolutePath(String path, Descriptor descriptor) {
        FileDirectory fileDirectory = new FileDirectory()
        FDInfo fdInfo = fileDirectory.getDetailsInfo(path, true)
        String descriptorPath = fileDirectory.getParentPath(path)
        String content = exportToYmlText(descriptor)
        return exportToFile(content, fdInfo.name, descriptorPath)
    }

    Boolean exportToYmlFile(String path, Descriptor descriptor, String fileName = ymlDescriptorFileName()) {
        try {
            if (processRequest.task.equals(ProcessTask.REPORT)) {
                return false
            }
            String descriptorFile = FDUtil.concatPath(path, ymlDescriptorFileName())
            if (FileDirectory.instance().isExist(descriptorFile) && !descriptor.dataUpdatedStatus()) {
                return true
            }
            String content = exportToYmlText(descriptor)
            return exportToFile(content, fileName, path)
        } catch (Exception e) {
            return false
        }
    }


    Descriptor loadJsonFromFile(String path) {
        JsonReadWrite jsonReadWrite = new JsonReadWrite()
        try {
            String pathWithName = FDUtil.concatPath(path, jsonDescriptorFileName())
            return jsonReadWrite.readJsonFileAsKlass(pathWithName, Descriptor.class)
        } catch (Exception e) {
            return null
        }
    }

    private void setDescriptorType(String path) {
        if (path && path.endsWith(ymlDescriptorFileName())) {
            loadedDescriptorName = TextToWebConst.DESCRIPTOR
        } else if (path && path.endsWith(ymltOutlineFileName())) {
            loadedDescriptorName = TextToWebConst.OUTLINE
        } else {
            loadedDescriptorName = ""
        }
    }

    String getLoadedDescriptorName() {
        return loadedDescriptorName
    }

    Descriptor loadYmlFromFile(String path) {
        YamlProcessor yamlProcessor = new YamlProcessor()
        try {
            setDescriptorType(path)
            return yamlProcessor.ymlAsNestedKlass(path, Descriptor.class)
        } catch (Exception e) {
            println("Load Yml From File " + e.getMessage())
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
        if (FileDirectory.instance().isExist(sourceAdoc)) {
            htmlContent = adocConverter.getHtmlFromFile(sourceAdoc)
        }
        return exportToFile(htmlContent, name, outPath)
    }

    public removeAdocExtension(String text) {
        return text.replace(".${TextToWebConst.ADOC}", "")
    }

    public String makeHumReadableWithoutExt(String text) {
        return makeHumReadable(removeAdocExtension(text))
    }


    public String makeHumReadable(String text) {
        String underscoreToSpace = text.replaceAll("(_+)([A-Za-z0-9_])", {
            Object[] it -> " " + it[2]?.trim()
        })

        String camelCaseToSpace = underscoreToSpace.replaceAll("(\\s*[A-Z])", {
            Object[] it -> " " + it[0]?.trim()
        })

        String hyphenToSpace = camelCaseToSpace.replaceAll("\\s*[\\-_]*\\s*", {
            Object[] it -> it[0].equals("") ? "" : " "
        })

        if (hyphenToSpace) {
            return hyphenToSpace.trim().toLowerCase().capitalize()
        }
        return text
    }

    public String pathToURL(String path) {
        if (path) {
            path = path.replace(File.separator, "/")
            if (path.length() > 1 && path.startsWith("/")) {
                path = path.substring(1)
            }

            if (path.length() > 1 && path.endsWith("/")) {
                path = path.substring(0, path.length() - 1)
            }
        }
        return path
    }

}