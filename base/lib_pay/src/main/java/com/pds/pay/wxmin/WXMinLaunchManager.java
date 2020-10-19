package com.pds.pay.wxmin;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.pds.pay.wx.WXConfig;
import com.pds.pay.wx.WXPayState;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelbiz.WXLaunchMiniProgram;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Observer;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * @author: pengdaosong
 * @CreateTime: 2020/10/15 3:38 PM
 * @Email: pengdaosong@medlinker.com
 * @Description:
 */
public class WXMinLaunchManager {

    private static final String TAG = "WXMinPayManager";
    private volatile static WXMinLaunchManager sWXMinLaunchManager;
    private final AtomicBoolean mProcessing = new AtomicBoolean(false);
    private IWXAPI mWxApi;
    private Observer mObserver;
    private Application mApplication;
    private long mLastTimestamp;
    private IWXAPIEventHandler mWeiXinHandler = new IWXAPIEventHandler() {
        // 微信发送请求到第三方应用时，会回调到该方法
        @Override
        public void onReq(BaseReq baseReq) {
            int type = baseReq.getType();
            Log.d(TAG, "onReq = " + type);
            switch (type) {
                // 从微信启动应用
                case ConstantsAPI.COMMAND_LAUNCH_BY_WX:
                    break;
                default:
            }
        }

        // 第三方应用发送到微信的请求处理后的响应结果，会回调到该方法
        @Override
        public void onResp(BaseResp resp) {
            int type = resp.getType();
            Log.d(TAG, "onResp = " + type);
            if (resp.getType() == ConstantsAPI.COMMAND_LAUNCH_WX_MINIPROGRAM) {
                WXLaunchMiniProgram.Resp launchMiniProResp = (WXLaunchMiniProgram.Resp) resp;
                // 对应小程序组件 <button open-type="launchApp"> 中的 app-parameter 属性
                String extraData = launchMiniProResp.extMsg;
                doCallback(mObserver, resp.errCode, resp.errStr, extraData);
                mObserver = null;
            }
            mProcessing.set(false);
        }
    };

    private WXMinLaunchManager() {
    }

    public static WXMinLaunchManager instance(Activity activity) {
        if (null == sWXMinLaunchManager) {
            synchronized (WXMinLaunchManager.class) {
                if (null == sWXMinLaunchManager) {
                    sWXMinLaunchManager = new WXMinLaunchManager();
                }
            }
        }
        sWXMinLaunchManager.initWXApi(activity.getApplication());
        return sWXMinLaunchManager;
    }

    /**
     * 初始化微信小程序
     */
    public void initWXApi(Application application) {
        if (mProcessing.get() || null == application) {
            return;
        }
        mApplication = application;
        mWxApi = WXAPIFactory.createWXAPI(mApplication, WXConfig.APP_ID);
    }

    /**
     * 注册小程序结果回调
     */
    public boolean onHandleIntent(Intent intent) {
        return mWxApi.handleIntent(intent, mWeiXinHandler);
    }

    private boolean isCap(){
        boolean cap =  (System.currentTimeMillis() - mLastTimestamp) > 1000;
        if (cap){
            mLastTimestamp = System.currentTimeMillis();
        }
        return cap;
    }

    public boolean isProcessing() {
        return mProcessing.get();
    }

    public void launch(String payInfo, Observer observer) {
        if (isCap()){
            mProcessing.set(false);
        }
        if (isProcessing()) {
            doCallback(observer, WXPayState.WX_MIN_LAUNCHING,
                    WXPayState.getTipMsg(WXPayState.WX_MIN_LAUNCHING));
            return;
        }

        if (null == mWxApi && null != mApplication) {
            initWXApi(mApplication);
        }
        if (null == mWxApi) {
            doCallback(observer, WXPayState.ERROR_NOT_API,
                    WXPayState.getTipMsg(WXPayState.ERROR_NOT_API));
            return;
        }
        if (!mWxApi.isWXAppInstalled()) {
            doCallback(observer, WXPayState.ERROR_NOT_INSTALL_WX,
                    WXPayState.getTipMsg(WXPayState.ERROR_NOT_INSTALL_WX));
            return;
        }

        mProcessing.set(true);
        this.mObserver = observer;
        launchWXMin(payInfo);
    }

    /**
     * 发起微信支付
     *
     * @param payInfo json参数
     */
    private void launchWXMin(String payInfo) {
        try {
            JSONObject json = new JSONObject(payInfo);
            WXLaunchMiniProgram.Req req = new WXLaunchMiniProgram.Req();
            // 填小程序原始id
            req.userName = json.getString("userName");
            // 拉起小程序页面的可带参路径，不填默认拉起小程序首页，对于小游戏，可以只传入 query 部分，来实现传参效果，如：传入 "?foo=bar"。
            req.path = json.getString("path");
            req.miniprogramType = json.getInt("miniProgramType");
            req.extData = json.getString("extra");
            mWxApi.sendReq(req);
        } catch (JSONException e) {
            doCallback(mObserver, WXPayState.ERROR_JSON,
                    WXPayState.getTipMsg(WXPayState.ERROR_JSON));
            destroy();
            e.printStackTrace();
            doTips("异常：" + e.getMessage());
        }
    }

    private void doCallback(Observer observer, int code, String result) {
        doCallback(observer, code, result, null);
    }

    private void doCallback(Observer observer, int code, String result, String extraData) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("code", code);
            jsonObject.put("result", result);
            if (!TextUtils.isEmpty(extraData)) {
                jsonObject.put("extraData", extraData);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (null != observer) {
            observer.update(null, jsonObject.toString());
        }
    }


    /**
     * 支付提示
     */
    private void doTips(String msg) {
        try {
            Toast.makeText(mApplication, msg, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void destroy() {
        mProcessing.set(false);
        mObserver = null;
        mApplication = null;
        sWXMinLaunchManager = null;
    }
}
