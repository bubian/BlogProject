package com.pds.kotlin.study.recorder;

import android.os.Parcel;
import android.os.Parcelable;

public class RecordEntity implements Parcelable {
    private String mName;
    private String mFilePath;
    private int mId;
    private int mLength;
    private long mTime;

    public RecordEntity() { }

    public RecordEntity(Parcel in) {
        mName = in.readString();
        mFilePath = in.readString();
        mId = in.readInt();
        mLength = in.readInt();
        mTime = in.readLong();
    }

    public String getFilePath() {
        return mFilePath;
    }

    public void setFilePath(String filePath) {
        mFilePath = filePath;
    }

    public int getLength() {
        return mLength;
    }

    public void setLength(int length) {
        mLength = length;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public long getTime() {
        return mTime;
    }

    public void setTime(long time) {
        mTime = time;
    }

    public static final Creator<RecordEntity> CREATOR = new Creator<RecordEntity>() {
        public RecordEntity createFromParcel(Parcel in) {
            return new RecordEntity(in);
        }

        public RecordEntity[] newArray(int size) {
            return new RecordEntity[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mId);
        dest.writeInt(mLength);
        dest.writeLong(mTime);
        dest.writeString(mFilePath);
        dest.writeString(mName);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}