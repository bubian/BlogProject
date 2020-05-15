package com.pds.blog.web.common;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author: pengdaosong
 * CreateTime:  2020-04-29 15:38
 * Email：pengdaosong@medlinker.com
 * Description:
 */
public class HbParam implements Parcelable {
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int id;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
    }

    public HbParam() { }

    protected HbParam(Parcel in) {
        this.id = in.readInt();
    }

    public static final Parcelable.Creator<HbParam> CREATOR = new Parcelable.Creator<HbParam>() {
        @Override
        public HbParam createFromParcel(Parcel source) {
            return new HbParam(source);
        }

        @Override
        public HbParam[] newArray(int size) {
            return new HbParam[size];
        }
    };
}
