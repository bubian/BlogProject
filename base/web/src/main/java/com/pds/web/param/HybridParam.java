package com.pds.web.param;

import android.os.Parcel;
import android.os.Parcelable;

public class HybridParam implements Parcelable {
    public int id;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
    }

    public HybridParam() {
    }

    protected HybridParam(Parcel in) {
        this.id = in.readInt();
    }

    public static final Creator<HybridParam> CREATOR = new Creator<HybridParam>() {
        @Override
        public HybridParam createFromParcel(Parcel source) {
            return new HybridParam(source);
        }

        @Override
        public HybridParam[] newArray(int size) {
            return new HybridParam[size];
        }
    };
}
