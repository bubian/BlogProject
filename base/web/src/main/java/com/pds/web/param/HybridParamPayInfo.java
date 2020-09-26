package com.pds.web.param;

import android.os.Parcel;
import android.os.Parcelable;

public class HybridParamPayInfo extends HybridParamCallback {
    public String payInfo;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.payInfo);
    }

    protected HybridParamPayInfo(Parcel in) {
        super(in);
        this.payInfo = in.readString();
    }

    public static final Parcelable.Creator<HybridParamPayInfo> CREATOR = new Parcelable.Creator<HybridParamPayInfo>() {
        @Override
        public HybridParamPayInfo createFromParcel(Parcel source) {
            return new HybridParamPayInfo(source);
        }

        @Override
        public HybridParamPayInfo[] newArray(int size) {
            return new HybridParamPayInfo[size];
        }
    };
}
