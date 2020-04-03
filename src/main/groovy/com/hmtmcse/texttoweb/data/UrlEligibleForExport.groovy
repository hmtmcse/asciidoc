package com.hmtmcse.texttoweb.data

class UrlEligibleForExport {
    public Boolean isEligible = false
    public String name = ""

    UrlEligibleForExport() {}

    UrlEligibleForExport(Boolean isEligible, String name = "") {
        this.isEligible = isEligible
        this.name = name
    }

    UrlEligibleForExport setIsEligible(Boolean isEligible) {
        this.isEligible = isEligible
        return this
    }

    UrlEligibleForExport setName(String name) {
        this.name = name
        return this
    }
}
