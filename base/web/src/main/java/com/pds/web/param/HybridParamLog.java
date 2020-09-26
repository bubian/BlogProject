package com.pds.web.param;

import android.os.Parcel;


public class HybridParamLog extends HybridParamCallback {

    private String med_project;
    private String med_channel;

    public HybridParamLog() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getMed_project() {
        return med_project;
    }

    public String getMed_channel() {
        return med_channel;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.med_project);
        dest.writeString(this.med_channel);
    }

    protected HybridParamLog(Parcel in) {
        super(in);
        this.med_project = in.readString();
        this.med_channel = in.readString();
    }

    public static final Creator<HybridParamLog> CREATOR = new Creator<HybridParamLog>() {
        @Override
        public HybridParamLog createFromParcel(Parcel source) {
            return new HybridParamLog(source);
        }

        @Override
        public HybridParamLog[] newArray(int size) {
            return new HybridParamLog[size];
        }
    };
}
