package com.hmtmcse.te.data

class TextToWebEngineConfig {

    public String urlExtension = ""
    public String defaultContent = "<h1>Coming Soon....</h1>";
    public String defaultTitle = "..:: Bismillah ::.."
    public String breadcrumbName = "Back to"
    public String errorTitle = "..:: Inalillah ::.."
    public String defaultPage = null
    public String page404 = "404.ftl"
    public String page500 = "500.ftl"
    public String layoutFileExtension = "ftl"
    public String docFileExtension = "adoc"
    public String exportFileExtension = null
    public HtmlExportType htmlExportType = HtmlExportType.ONLINE
    public Boolean isFromWebsite = true
    public Boolean isDevelopmentMode = false

    public String getExportFileExtensionByNullCheck() {
        return exportFileExtension ? ("." + exportFileExtension) : ""
    }

}
