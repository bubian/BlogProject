package com.pds.web.param;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class HybridParamUpload extends HybridParamCallback {

    @SerializedName("bucket")
    private String bucket;
    @SerializedName("isPublic")
    private int isPublic;
    @SerializedName("waterText")
    private String waterText;
    @SerializedName("count")
    private int count;
    @SerializedName("sizeType")
    private List<String> sizeType;
    @SerializedName("sourceType")
    private List<String> sourceType;

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public int getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(int isPublic) {
        this.isPublic = isPublic;
    }

    public String getWaterText() {
        return waterText;
    }

    public void setWaterText(String waterText) {
        this.waterText = waterText;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<String> getSizeType() {
        return sizeType;
    }

    public void setSizeType(List<String> sizeType) {
        this.sizeType = sizeType;
    }

    public List<String> getSourceType() {
        return sourceType;
    }

    public void setSourceType(List<String> sourceType) {
        this.sourceType = sourceType;
    }

    @Override
    public String toString() {
        return "HybridParamUpload{" +
                "bucket='" + bucket + '\'' +
                ", isPublic=" + isPublic +
                ", waterText='" + waterText + '\'' +
                ", count=" + count +
                ", sizeType=" + sizeType +
                ", sourceType=" + sourceType +
                '}';
    }
}
