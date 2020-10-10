package com.pds.tools.core.dokit.httpcarry.entity;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

/**
 * @author: pengdaosong
 * @CreateTime: 2020/10/4 2:44 PM
 * @Email: pengdaosong@medlinker.com
 * @Description:
 */
public class ApiEntity implements Parcelable {
    // 请求数据
    public long time;
    public String protocol;
    public String path;
    public String method;
    public String requestStartMessage;
    public String queries;
    public String headers;
    public String requestBody;
    // 响应数据
    public String responseStartBody;
    public String responseCode;
    public String responseMsg;
    public String responseUrl;
    public String responseBody;
    public String responseHeaders;

    public ApiEntity() {
    }

    protected ApiEntity(Parcel in) {
        time = in.readLong();
        protocol = in.readString();
        path = in.readString();
        method = in.readString();
        requestStartMessage = in.readString();
        queries = in.readString();
        headers = in.readString();
        requestBody = in.readString();
        responseStartBody = in.readString();
        responseCode = in.readString();
        responseMsg = in.readString();
        responseUrl = in.readString();
        responseBody = in.readString();
        responseHeaders = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(time);
        dest.writeString(protocol);
        dest.writeString(path);
        dest.writeString(method);
        dest.writeString(requestStartMessage);
        dest.writeString(queries);
        dest.writeString(headers);
        dest.writeString(requestBody);
        dest.writeString(responseStartBody);
        dest.writeString(responseCode);
        dest.writeString(responseMsg);
        dest.writeString(responseUrl);
        dest.writeString(responseBody);
        dest.writeString(responseHeaders);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ApiEntity> CREATOR = new Creator<ApiEntity>() {
        @Override
        public ApiEntity createFromParcel(Parcel in) {
            return new ApiEntity(in);
        }

        @Override
        public ApiEntity[] newArray(int size) {
            return new ApiEntity[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        ApiEntity entity = (ApiEntity) o;
        return TextUtils.equals(entity.path, path);
    }

}
