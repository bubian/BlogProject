package com.medrn.codepush;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.medrn.codepush.entity.MetaInfoEntity;

public class MedHotUpdateModule extends ReactContextBaseJavaModule {

    private RNSettingsManager mSettingsManager;

    public MedHotUpdateModule(ReactApplicationContext reactContext) {
        super(reactContext);
        mSettingsManager = new RNSettingsManager(reactContext);
    }

    @Override
    public String getName() {
        return "MedHotUpdateModule";
    }

    /**
     * 拉取当前更新的元数据
     *
     * @param param
     * @param promise
     */
    @ReactMethod
    public void getCurrentHotUpdatedMetaInfo(ReadableMap param, Promise promise) {
        WritableMap map = Arguments.createMap();
        MetaInfoEntity infoEntity = RNCodePush.getInstance().getCurrentLoadPackageMetaInfo();
        map.putString("baseVersion", infoEntity.getBaseVersion());
        map.putString("version", infoEntity.getVersion());
        map.putString("downloadFile", infoEntity.getDownloadFile());
        map.putString("hash", infoEntity.getHash());
        map.putDouble("buildTime", infoEntity.getBuildTime());
        map.putDouble("updateTime", infoEntity.getUpdateTime());
        promise.resolve(map);
    }

    /**
     * 通知native更新成功
     */
    @ReactMethod
    public void updateSuccess(ReadableMap param, Promise promise) {
        RNCodePush.getInstance().removeCurrentFailedUpdates();
    }
}
