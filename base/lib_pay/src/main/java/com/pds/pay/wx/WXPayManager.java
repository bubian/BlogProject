package com.pds.pay.wx;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.pds.pay.SignUtil;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.Observer;
import java.util.concurrent.atomic.AtomicBoolean;

public class WXPayManager {

    private static final String TAG = "WXPayManager";
    private IWXAPI mWxApi;
    private Observer mObserver;
    private final AtomicBoolean mProcessing = new AtomicBoolean(false);
    private Context mApplication;
    private WeakReference<Activity> mWeakActivity;
    private volatile static WXPayManager sPayManager;
    private long mLastTimestamp;
    private WXPayManager() {
    }

    public static WXPayManager instance(Activity activity) {
        if (null == sPayManager) {
            synchronized (WXPayManager.class) {
                if (null == sPayManager) {
                    sPayManager = new WXPayManager();
                }
            }
        }
        sPayManager.initWXPayApi(activity);
        return sPayManager;
    }

    private Activity getActivity() {
        return mWeakActivity != null ? mWeakActivity.get() : null;
    }

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
            if (type == ConstantsAPI.COMMAND_PAY_BY_WX) {
                doCallback(mObserver, resp.errCode, resp.errStr);
                mObserver = null;
            }
            mProcessing.set(false);
        }
    };

    /**
     * 初始化微信支付
     */
    public void initWXPayApi(Activity activity) {
        if (mProcessing.get() || null == activity) {
            return;
        }
        mWeakActivity = new WeakReference<>(activity);
        mApplication = activity.getApplicationContext();
        mWxApi = WXAPIFactory.createWXAPI(activity.getApplicationContext(), WXConfig.APP_ID);
        mWxApi.registerApp(WXConfig.APP_ID);
    }

    /**
     * 注册微信支付结果回调
     */
    public boolean onHandleIntent(Intent intent) {
        return mWxApi.handleIntent(intent, mWeiXinHandler);
    }

    public boolean isProcessing() {
        return mProcessing.get();
    }

    private boolean isCap(){
        boolean cap =  (System.currentTimeMillis() - mLastTimestamp) > 1000;
        if (cap){
            mLastTimestamp = System.currentTimeMillis();
        }
        return cap;
    }

    public void sendPay(String payInfo, Observer observer) {
        if (isCap()){
            mProcessing.set(false);
        }
        if (isProcessing()) {
            doCallback(observer, WXPayState.ERROR_PAYING,
                    WXPayState.getTipMsg(WXPayState.ERROR_PAYING));
            return;
        }

        if (null == mWxApi && null != getActivity()) {
            initWXPayApi(getActivity());
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
        sendWXPay(payInfo);
    }

    private void doCallback(Observer observer, int code, String result) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("code", code);
            jsonObject.put("result", result);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (null != observer) {
            observer.update(null, jsonObject.toString());
        }
    }

    /**
     * 发起微信支付
     *
     * @param payInfo json参数
     */
    private void sendWXPay(String payInfo) {
        try {
            JSONObject json = new JSONObject(payInfo);
            PayReq req = new PayReq();
            req.appId = json.getString("appId");
            // 商户号
            req.partnerId = json.getString("mchId");
            req.prepayId = json.getString("prepayId");
            req.packageValue = json.getString("package");
            req.nonceStr = json.getString("nonceStr");
            req.timeStamp = json.getString("timestamp");
            req.sign = json.getString("sign");
            if (!doSignCheck(json)) {
                doCallback(mObserver, WXPayState.ERROR_SIGN,
                        WXPayState.getTipMsg(WXPayState.ERROR_SIGN));
                destroy();
                return;
            }
            mWxApi.sendReq(req);
        } catch (JSONException e) {
            doCallback(mObserver, WXPayState.ERROR_JSON,
                    WXPayState.getTipMsg(WXPayState.ERROR_JSON));
            destroy();
            e.printStackTrace();
            doTips("异常：" + e.getMessage());
        }
    }

    /**
     * 这里校验应用签名
     */
    private boolean doSignCheck(JSONObject json) {
        boolean isSignCheck = false;
        if (null != json) {
            isSignCheck = json.optBoolean("isSignCheck", false);
        }
        if (isSignCheck) {
            String serviceSign = json.optString("sign");
            String sign = SignUtil.md5(SignUtil.getSignature(getActivity()));
            Log.d(TAG, "doSignCheck:sign = " + sign + " serviceSign = " + serviceSign);
            return TextUtils.equals(sign, serviceSign);
        }
        return true;
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
        sPayManager = null;
    }
}
