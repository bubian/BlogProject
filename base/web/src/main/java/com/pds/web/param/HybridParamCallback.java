package com.pds.web.param;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;


public class HybridParamCallback extends HybridParam implements Parcelable {
    public String tagname;
    public String callback;
    public String data;
    public JSONObject callbackData;
    public int errno = 0;//成功就是0，默认成功，如果不成功就是非0数字
    public String msg = "";//成功就是空，不成功就是失败原因

    public HybridParamCallback() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.tagname);
        dest.writeString(this.callback);
        dest.writeString(this.data);
        dest.writeInt(this.id);
        dest.writeInt(this.errno);
        dest.writeString(this.msg);
    }

    protected HybridParamCallback(Parcel in) {
        this.tagname = in.readString();
        this.callback = in.readString();
        this.data = in.readString();
        this.id = in.readInt();
        this.errno = in.readInt();
        this.msg = in.readString();
    }

    public static final Creator<HybridParamCallback> CREATOR = new Creator<HybridParamCallback>() {
        @Override
        public HybridParamCallback createFromParcel(Parcel source) {
            return new HybridParamCallback(source);
        }

        @Override
        public HybridParamCallback[] newArray(int size) {
            return new HybridParamCallback[size];
        }
    };
}
