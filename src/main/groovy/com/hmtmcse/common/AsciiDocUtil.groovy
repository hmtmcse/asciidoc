package com.hmtmcse.common

import com.hmtmcse.fileutil.data.FDInfo
import com.hmtmcse.texttoweb.Config
import com.hmtmcse.texttoweb.common.ConfigLoader

class AsciiDocUtil {

    public static Boolean isSkipFile(FDInfo topicsDir) {
        Config config = ConfigLoader.getConfig()
        if (!topicsDir || !topicsDir.name) {
            return true
        }

        if (config.ignore) {
            for (String ignore : config.ignore) {
                if (ignore.equals(topicsDir.name) || topicsDir.name.endsWith(ignore)) {
                    return true
                }
            }
        }
        return false
    }
}
