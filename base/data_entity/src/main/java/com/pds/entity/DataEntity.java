package com.pds.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * @author <a href="mailto:zhangkuiwen@medlinker.net">Kuiwen.Zhang</a>
 * @version 1.0
 * @description 功能描述
 * @time 2015/10/21 10:05
 **/
public class DataEntity implements Parcelable {
    @SerializedName("_id")
    protected int _id;
    private boolean mSelected;
    private String acsToken;
    private String acsSign;
    private String acsMessage;
    private int acsDayResidue;
    @SerializedName("acsType")
    private int acsType;
    @SerializedName("loginTime")
    private long loginTime;
    @SerializedName("platform")
    private String plateform;

    @Override
    public int describeContents() {
        return 0;
    }

    public String getAcsToken() {
        return acsToken;
    }

    public void setAcsToken(String acsToken) {
        this.acsToken = acsToken;
    }

    public int getAcsType() {
        return acsType;
    }

    public void setAcsType(int acsType) {
        this.acsType = acsType;
    }

    public String getAcsSign() {
        return acsSign;
    }

    public void setAcsSign(String acsSign) {
        this.acsSign = acsSign;
    }

    public String getAcsMessage() {
        return acsMessage;
    }

    public void setAcsMessage(String acsMessage) {
        this.acsMessage = acsMessage;
    }

    public int getAcsDayResidue() {
        return acsDayResidue;
    }

    public void setAcsDayResidue(int acsDayResidue) {
        this.acsDayResidue = acsDayResidue;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this._id);
        dest.writeInt(this.acsType);
        dest.writeInt(this.acsDayResidue);
        dest.writeByte(this.mSelected ? (byte) 1 : (byte) 0);
        dest.writeLong(this.loginTime);
        dest.writeString(this.plateform);
        dest.writeString(this.acsToken);
        dest.writeString(this.acsSign);
        dest.writeString(this.acsMessage);
    }

    protected DataEntity(Parcel in) {
        this._id = in.readInt();
        this.acsType = in.readInt();
        this.acsDayResidue = in.readInt();
        this.mSelected = in.readByte() != 0;
        this.loginTime = in.readLong();
        this.plateform = in.readString();
        this.acsToken = in.readString();
        this.acsSign = in.readString();
        this.acsMessage = in.readString();
    }

    public static final Creator<DataEntity> CREATOR = new Creator<DataEntity>() {
        @Override
        public DataEntity createFromParcel(Parcel source) {
            return new DataEntity(source);
        }

        @Override
        public DataEntity[] newArray(int size) {
            return new DataEntity[size];
        }
    };
}
