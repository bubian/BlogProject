package com.pds.web.param;

import android.os.Parcel;
import android.text.TextUtils;

import com.pds.web.util.LogUtil;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public class HybridParamShowKeyboard extends HybridParamCallback {

    /**
     * hasImg : 1
     * bucket : transform
     * isPublic : 1
     * waterText : 水印文字
     * count : 1
     * sizeType : ["original","compressed"]
     * sourceType : ["album","camera"]
     * type : Done
     * btnTxt : 发送
     * value : 默认填充
     * tips : 描述信息
     * textMin : 20
     * textMax : 500
     */

    private int hasImg;
    private String bucket;
    private int isPublic;
    private String waterText;
    private int count;
    private String type;
    private String btnTxt;
    private String value;
    private String tips;
    private int textMin;
    private int textMax;
    private List<String> sizeType;
    private List<String> sourceType;
    // 是否展示转发到时空的选择框， 1：显示，0 不显示， 默认显示。
    private int syncTimespace = 1;

    public int getHasImg() {
        return hasImg;
    }

    public void setHasImg(int hasImg) {
        this.hasImg = hasImg;
    }

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBtnTxt() {
        return btnTxt;
    }

    public void setBtnTxt(String btnTxt) {
        this.btnTxt = btnTxt;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getTips() {
        return tips;
    }

    public void setTips(String tips) {
        this.tips = tips;
    }

    public int getTextMin() {
        return textMin;
    }

    public void setTextMin(int textMin) {
        this.textMin = textMin;
    }

    public int getTextMax() {
        return textMax;
    }

    public void setTextMax(int textMax) {
        this.textMax = textMax;
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

    public int getSyncTimespace() {
        return syncTimespace;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(this.hasImg);
        dest.writeString(this.bucket);
        dest.writeInt(this.isPublic);
        dest.writeString(this.waterText);
        dest.writeInt(this.count);
        dest.writeString(this.type);
        dest.writeString(this.btnTxt);
        dest.writeString(this.value);
        dest.writeString(this.tips);
        dest.writeInt(this.textMin);
        dest.writeInt(this.textMax);
        dest.writeStringList(this.sizeType);
        dest.writeStringList(this.sourceType);
        dest.writeInt(this.syncTimespace);
    }

    public HybridParamShowKeyboard() {
    }

    protected HybridParamShowKeyboard(Parcel in) {
        super(in);
        this.hasImg = in.readInt();
        this.bucket = in.readString();
        this.isPublic = in.readInt();
        this.waterText = in.readString();
        this.count = in.readInt();
        this.type = in.readString();
        this.btnTxt = in.readString();
        this.value = in.readString();
        this.tips = in.readString();
        this.textMin = in.readInt();
        this.textMax = in.readInt();
        this.sizeType = in.createStringArrayList();
        this.sourceType = in.createStringArrayList();
        this.syncTimespace = in.readInt();
    }

    public static final Creator<HybridParamShowKeyboard> CREATOR = new Creator<HybridParamShowKeyboard>() {
        @Override
        public HybridParamShowKeyboard createFromParcel(Parcel source) {
            return new HybridParamShowKeyboard(source);
        }

        @Override
        public HybridParamShowKeyboard[] newArray(int size) {
            return new HybridParamShowKeyboard[size];
        }
    };

    /**
     * 组装数据，回调h5
     *
     * @param param
     * @param text
     * @param ids
     * @param urls
     * @param isPublishTS 是否发布到时空
     */
    public static void postCallback(HybridParamShowKeyboardCallback param, String text, List<Integer> ids, List<String> urls, boolean isPublishTS) {
        try {
            JSONObject data = new JSONObject();
            if (!TextUtils.isEmpty(text)) {
                data.put("text", text);
            }
            if (ids != null) {
                data.put("ids", new JSONArray(ids));
            }
            if (urls != null) {
                data.put("urls", new JSONArray(urls));
            }
            data.put("isToTimeSpace", isPublishTS);
            param.data = data.toString();
            param.callbackData = data;
            EventBus.getDefault().post(param);
            LogUtil.i("HybridParamUpload", String.format(" imgs upload success post data = %s, callbackData = %s", data.toString(), data));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void postCallback(HybridParamUploadCallback param, String text, List<Integer> ids, List<String> urls) {
        try {
            JSONObject data = new JSONObject();
            if (!TextUtils.isEmpty(text)) {
                data.put("text", text);
            }
            if (ids != null) {
                data.put("ids", new JSONArray(ids));
            }
            if (urls != null) {
                data.put("urls", new JSONArray(urls));
            }
            param.data = data.toString();
            param.callbackData = data;
            EventBus.getDefault().post(param);
            LogUtil.i("HybridParamUpload", String.format(" imgs upload success post data = %s, callbackData = %s", data.toString(), data));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Eventbus post 需要类型唯一。接收的地方不能接收父类。
     */
    public static class HybridParamShowKeyboardCallback extends HybridParamCallback {

    }

    public static class HybridParamUploadCallback extends HybridParamCallback {

    }

}

