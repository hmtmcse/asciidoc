package com.hmtmcse.texttoweb.data

import com.hmtmcse.fileutil.data.FDInfo
import com.hmtmcse.fileutil.data.FileDirectoryListing
import com.hmtmcse.texttoweb.resource.StaticResourceIndex

class StaticResourceParams {

    public List<FileDirectoryListing> list
    public Boolean isCopySourceToOutCopy = false
    public String source
    public String out
    public FDInfo sourceFileInfo
    public StaticResourceIndex oldStaticResourceIndex

    StaticResourceParams() {}

    StaticResourceParams(List<FileDirectoryListing> list) {
        this.list = list
    }

    StaticResourceParams setIsCopySourceToOutCopy(Boolean isCopySourceToOutCopy) {
        this.isCopySourceToOutCopy = isCopySourceToOutCopy
        return this
    }

    StaticResourceParams setList(List<FileDirectoryListing> list) {
        this.list = list
        return this
    }

    StaticResourceParams setSource(String source) {
        this.source = source
        return this
    }

    StaticResourceParams setOut(String out) {
        this.out = out
        return this
    }

    StaticResourceParams setSourceFileInfo(FDInfo sourceFileInfo) {
        this.sourceFileInfo = sourceFileInfo
        return this
    }

    StaticResourceParams setOldStaticResourceIndex(StaticResourceIndex oldStaticResourceIndex) {
        this.oldStaticResourceIndex = oldStaticResourceIndex
        return this
    }

    StaticResourceParams copy(List<FileDirectoryListing> list = []) {
        StaticResourceParams staticResourceParams = new StaticResourceParams(list)
        staticResourceParams.source = this.source
        staticResourceParams.sourceFileInfo = this.sourceFileInfo
        staticResourceParams.out = this.out
        staticResourceParams.isCopySourceToOutCopy = this.isCopySourceToOutCopy
        staticResourceParams.oldStaticResourceIndex = this.oldStaticResourceIndex
        return staticResourceParams
    }

}
