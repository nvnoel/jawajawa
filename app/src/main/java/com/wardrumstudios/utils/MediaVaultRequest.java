package com.wardrumstudios.utils;

public class MediaVaultRequest {
    private String ipAddress;
    private String mediaURL;
    private String pageURL;
    private String referrer;
    private long startTime = -1;
    private long endTime = -1;

    public MediaVaultRequest(String mediaURL) {
        this.mediaURL = mediaURL;
    }

    public String getURLParamers() {
        StringBuilder urlParams = new StringBuilder();
        urlParams.append("&p=100");
        if (this.referrer != null) {
            urlParams.append("&ru=1");
        }
        if (this.pageURL != null && this.pageURL.length() > 0) {
            urlParams.append("&pu=1");
        }
        if (this.ipAddress != null && this.ipAddress.length() > 0) {
            urlParams.append("&ip=" + this.ipAddress);
        }
        if (this.startTime != -1) {
            urlParams.append("&s=" + this.startTime);
        }
        if (this.endTime != -1) {
            urlParams.append("&e=" + this.endTime);
        }
        return urlParams.length() > 0 ? urlParams.substring(1) : urlParams.toString();
    }

    public String getHashParameters() {
        StringBuilder hash = new StringBuilder();
        if (this.referrer != null) {
            hash.append(this.referrer);
        }
        if (this.pageURL != null) {
            hash.append(this.pageURL);
        }
        return hash.toString();
    }

    public long getStartTime() {
        return this.startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return this.endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public String getIPAddress() {
        return this.ipAddress;
    }

    public void setIPAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getReferrer() {
        return this.referrer;
    }

    public void setReferrer(String referrer) {
        this.referrer = referrer;
    }

    public String getPageURL() {
        return this.pageURL;
    }

    public void setPageURL(String pageURL) {
        this.pageURL = pageURL;
    }

    public String getMediaURL() {
        return this.mediaURL;
    }

    public void setMediaURL(String mediaURL) {
        this.mediaURL = mediaURL;
    }
}