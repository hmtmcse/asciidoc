package com.hmtmcse.common

import com.hmtmcse.fileutil.data.FDInfo
import com.hmtmcse.fileutil.fd.FDUtil
import com.hmtmcse.texttoweb.Config
import com.hmtmcse.texttoweb.common.ConfigLoader

class AsciiDocUtil {

    public static Boolean isSkipFile(FDInfo topicsDir, String source = null) {
        Config config = ConfigLoader.getConfig()
        if (!topicsDir || !topicsDir.name) {
            return true
        }
        if (!source) {
            source = config.source
        }

        if (config.ignore) {
            for (String ignore : config.ignore) {
                if (
                (source && topicsDir.absolutePath && topicsDir.absolutePath.startsWith(FDUtil.concatPath(source, ignore, topicsDir.name))) ||
                        ignore.equals(topicsDir.name) ||
                        topicsDir.name.endsWith(ignore)
                ) {
                    return true
                }
            }
        }
        return false
    }
}
