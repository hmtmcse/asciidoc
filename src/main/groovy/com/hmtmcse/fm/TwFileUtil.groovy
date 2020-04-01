package com.hmtmcse.fm

import com.hmtmcse.tmutil.TStringUtil

class TwFileUtil {

    public static String slash = "/"

    public static String urlToPath(String url) {
        return TStringUtil.findReplace(url, slash, File.separator)
    }

    public static String trimSlash(String url) {
        return TStringUtil.trimStartEndChar(url, slash)
    }

    public static String trimAndUrlToPath(String url) {
        return urlToPath(trimSlash(url))
    }
}
