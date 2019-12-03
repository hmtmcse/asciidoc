package com.hmtmcse.texttoweb;

public class Settings {

    public Seo defaultSeo;
    public Boolean fbComments = false;
    public Boolean linkedInShare = false;
    public Boolean fbShare = false;

    public Seo getDefaultSeo() {
        return defaultSeo;
    }

    public void setDefaultSeo(Seo defaultSeo) {
        this.defaultSeo = defaultSeo;
    }

    public Boolean getFbComments() {
        return fbComments;
    }

    public void setFbComments(Boolean fbComments) {
        this.fbComments = fbComments;
    }

    public Boolean getLinkedInShare() {
        return linkedInShare;
    }

    public void setLinkedInShare(Boolean linkedInShare) {
        this.linkedInShare = linkedInShare;
    }

    public Boolean getFbShare() {
        return fbShare;
    }

    public void setFbShare(Boolean fbShare) {
        this.fbShare = fbShare;
    }
}
