package com.hmtmcse.texttoweb.processor

import com.hmtmcse.jtfutil.io.FileInfo
import com.hmtmcse.jtfutil.io.FileUtil
import com.hmtmcse.jtfutil.io.JavaNio
import com.hmtmcse.shellutil.console.menu.OptionValues
import com.hmtmcse.texttoweb.Config
import com.hmtmcse.texttoweb.TextToWebConst
import com.hmtmcse.texttoweb.common.ConfigLoader
import com.hmtmcse.texttoweb.data.PathData
import com.hmtmcse.texttoweb.data.ProjectData
import com.hmtmcse.texttoweb.model.CommandProcessor
import com.hmtmcse.texttoweb.sample.DescriptorSample

class GenerateProcessor implements CommandProcessor {

    private Config config


    @Override
    void process(OptionValues optionValues) {
        String command = optionValues.valueAsString(TextToWebConst.DESCRIPTOR)
        config = ConfigLoader.getConfig(optionValues)
        switch (command) {
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





    private String getRelativePath(String absolutePath){
        if (absolutePath){
           return absolutePath.replace(config.source, "")
        }
        return absolutePath
    }



    private void prepareProjectData(List<FileInfo> list) {
        PathData pathData = null
        if (list) {
            list.each { FileInfo rootDir ->
                if (rootDir.isDirectory && rootDir.subDirectories) {
                    topicsRootProcess(rootDir.subDirectories)
                }
            }
            if (!pathData) {
                pathData = new PathData(config.source, getRelativePath(config.source))
            }
            projectRootProcess(pathData)
        }
    }


    void manipulateDescriptor(){
        List<FileInfo> list = FileUtil.listAll(config.source)
        prepareProjectData(list)
    }


    void projectRootProcess(PathData pathData){
        String path = JavaNio.concatPath(pathData.absolutePath, ymlDescriptorFileName())
        if (!JavaNio.isExist(path)){
            exportToYmlFile(pathData.absolutePath, DescriptorSample.landingDescriptor)
        }
    }

    void topicsRootProcess(List<FileInfo> list){
        list.each { FileInfo topicsDir ->
            println("Topics: ${makeHumReadable(topicsDir.name)} ${topicsDir.name}")
            if (topicsDir.isDirectory && topicsDir.subDirectories) {
                topicRootProcess(topicsDir.subDirectories)
            }
        }
    }

    void topicRootProcess(List<FileInfo> list){
        list.each { FileInfo topicDir ->
            println("  Topic: ${makeHumReadableWithoutExt(topicDir.name)} ${topicDir.name}")
            if (topicDir.isDirectory && topicDir.subDirectories) {
//                topicRootProcess(topicsDir.subDirectories)
            }

        }
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
        manipulateDescriptor()

//        exportToJsonNdYmlFile(config.source, DescriptorSample.landingDescriptor)
    }

    void topics() {}

    void outline() {}

    void details() {}

}
