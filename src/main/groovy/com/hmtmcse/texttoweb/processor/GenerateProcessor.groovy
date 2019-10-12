package com.hmtmcse.texttoweb.processor

import com.hmtmcse.jtfutil.io.FileInfo
import com.hmtmcse.jtfutil.io.FileUtil
import com.hmtmcse.shellutil.console.menu.OptionValues
import com.hmtmcse.texttoweb.Config
import com.hmtmcse.texttoweb.TextToWebConst
import com.hmtmcse.texttoweb.common.ConfigLoader
import com.hmtmcse.texttoweb.data.ProjectData
import com.hmtmcse.texttoweb.model.CommandProcessor

class GenerateProcessor implements CommandProcessor {

    private Config config


    @Override
    void process(OptionValues optionValues) {
        String command = optionValues.valueAsString(TextToWebConst.DESCRIPTOR)
        config = ConfigLoader.getConfig(optionValues)
        switch (command) {
            case TextToWebConst.LANDING:
                FileUtil.listAll("W:\\all-in-one\\all-plugins\\asciidoc\\example\\root").each { FileInfo fileInfo ->
                    println(fileInfo.name)
                }
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


    public static String makeHumReadable(String text) {
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


    private ProjectData prepareProjectData(List<FileInfo> list) {
        ProjectData projectData = null
        if (list) {
            String descriptor = "${TextToWebConst.DESCRIPTOR}.${TextToWebConst.YML}"
            projectData = new ProjectData()
            list.each { FileInfo rootDir ->
                if (rootDir.isDirectory && rootDir.subDirectories) {
                    rootDir.subDirectories.each { FileInfo topicsDir ->
                        if (topicsDir.subDirectories) {
                            topicsDir.subDirectories.each { FileInfo topicDir ->

                            }
                        }
                    }
                } else if (rootDir.isFile && rootDir.name && rootDir.name.equals(descriptor)) {
                    projectData.descriptor = ""
                }
            }
        }
        return projectData
    }

    void manipulateDescriptor(){
        List<FileInfo> list = FileUtil.listAll(config.source)
    }

    void rootDescriptor(List<FileInfo> list){

    }

    void topicsDescriptor(List<FileInfo> list){

    }

    void outlineDescriptor(List<FileInfo> list){

    }

    void searchIndex(List<FileInfo> list){

    }

    void siteMap(List<FileInfo> list){

    }

    void exportProject(List<FileInfo> list){

    }

    void landing() {


//        exportToJsonNdYmlFile(config.source, DescriptorSample.landingDescriptor)
    }

    void topics() {}

    void outline() {}

    void details() {}

}
