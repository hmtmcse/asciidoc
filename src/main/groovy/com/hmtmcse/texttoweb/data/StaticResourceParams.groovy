package com.hmtmcse.texttoweb.data

import com.hmtmcse.fileutil.data.FileDirectoryListing

class StaticResourceParams {
    public List<FileDirectoryListing> list
    public Boolean isCopySourceToOutCopy = false

    StaticResourceParams() {}

    StaticResourceParams(List<FileDirectoryListing> list) {
        this.list = list
    }
}
