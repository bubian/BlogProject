package com.pds.download.entity;

public class Version {

    private long versionId;

    private int type;//平台类型

    private String releaseCode;//最新版本

    private boolean mandatory;//是否强制升级

    private int isNew;//是否是最新版本（1是；2否）

    private String url;//下载地址

    private String tipsTime;

    private String tipsData;

    public long getVersionId() {
        return versionId;
    }

    public int getType() {
        return type;
    }

    public String getReleaseCode() {
        return releaseCode;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public int getIsNew() {
        return isNew;
    }

    public String getUrl() {
        return url;
    }

    public String getTipsTime() {
        return tipsTime;
    }

    public String getTipsData() {
        return tipsData;
    }

    public static class Builder {

        private final Version version;

        public Builder() {
            version = new Version();
        }

        public Builder setVersionId(long versionId) {
            version.versionId = versionId;
            return this;
        }

        public Builder setType(int type) {
            version.type = type;
            return this;
        }

        public Builder setReleaseCode(String releaseCode) {
            version.releaseCode = releaseCode;
            return this;
        }

        public Builder setMandatory(boolean mandatory) {
            version.mandatory = mandatory;
            return this;
        }

        public Builder setIsNew(int isNew) {
            version.isNew = isNew;
            return this;
        }

        public Builder setUrl(String url) {
            version.url = url;
            return this;
        }

        public Builder setTipsTime(String tipsTime) {
            version.tipsTime = tipsTime;
            return this;
        }

        public Builder setTipsData(String tipsData) {
            version.tipsData = tipsData;
            return this;
        }

        public Version build() {
            return version;
        }
    }
}
