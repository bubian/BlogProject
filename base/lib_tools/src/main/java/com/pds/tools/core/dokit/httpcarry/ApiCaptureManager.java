package com.pds.tools.core.dokit.httpcarry;

import com.pds.tools.common.callback.CallBack;
import com.pds.tools.core.dokit.httpcarry.entity.ApiEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: pengdaosong
 * @CreateTime: 2020/10/4 3:12 PM
 * @Email: pengdaosong@medlinker.com
 * @Description:
 */
public class ApiCaptureManager {

    private static final ApiCaptureManager mManager = new ApiCaptureManager();
    private CallBack<List<ApiEntity>> mCallBack;
    private static final int MAX_RECORD_NUM = 50;

    private ArrayList<ApiEntity> mCache = new ArrayList<>(25);

    public static ApiCaptureManager instance() {
        return mManager;
    }

    public boolean size() {
        return mCache.size() > MAX_RECORD_NUM;
    }

    public void addData(ApiEntity data) {
        int size = mCache.size();
        if (size > MAX_RECORD_NUM) {
            mCache.remove(0);
        }
        mCache.add(data);
        notice();
    }

    public ArrayList<ApiEntity> getCache() {
        return mCache;
    }

    public void notice() {
        if (null != mCallBack) {
            mCallBack.value(mCache);
        }
    }

    public void register(CallBack<List<ApiEntity>> callBack) {
        mCallBack = callBack;
    }

    public void clear() {
        mCache.clear();
        notice();
    }

    public ApiEntity getData(String path) {

        for (ApiEntity e : mCache) {
            if (e.equals(path)) {
                return e;
            }
        }
        return new ApiEntity();
    }
}
