package com.pds.pay.ali;

import android.app.Activity;
import android.text.TextUtils;

import com.alipay.sdk.app.H5PayCallback;
import com.alipay.sdk.app.PayTask;
import com.alipay.sdk.util.H5PayResultModel;
import com.pds.pay.ali.task.AsyncTaskCompat;
import com.pds.pay.ali.task.ITaskManager;
import com.pds.pay.ali.task.PayAsyncTask;
import com.pds.pay.ali.task.TaskManagerImpl;
import com.pds.pay.wx.WXPayManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.Observer;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author pengdaosong
 */
public class AliPayManager {

    private static final String TAG = "AliPayManager";
    private static AliPayManager sPayManager;
    private Observer mObserver;
    private WeakReference<Activity> mWeakActivity;
    private final AtomicBoolean mProcessing = new AtomicBoolean(false);
    private final ITaskManager mTaskManager = new TaskManagerImpl();

    private AliPayManager(Activity activity) {
    }

    public static AliPayManager instance(Activity activity) {
        if (null == sPayManager) {
            synchronized (AliPayManager.class) {
                if (null == sPayManager) {
                    sPayManager = new AliPayManager(activity);
                }
            }
        }
        return sPayManager;
    }

    private Activity getActivity() {
        return mWeakActivity != null ? mWeakActivity.get() : null;
    }

    public void pay(Activity activity, String payInfo, Observer observer) {
        if (mProcessing.get()) {
            doCallback(observer, AliPayState.ERROR_PAYING,
                    AliPayState.getTipMsg(AliPayState.ERROR_PAYING));
            return;
        }
        mProcessing.set(true);
        this.mWeakActivity = new WeakReference<>(activity);
        this.mObserver = observer;
        AsyncTaskCompat.executeParallel(new InternalPayTask(mTaskManager), payInfo);
    }

    /**
     * 网页支付
     */
    public void payInterceptorWithUrl(String url, Observer observer) {
        if (!(url.startsWith("http") || url.startsWith("https"))) {
            doCallback(observer, buildH5Result(AliPayState.ERROR_H5_URL,
                    AliPayState.getTipMsg(AliPayState.ERROR_H5_URL)));
            doComplete();
            return;
        }
        final Activity activity = getActivity();
        if (activity == null || activity.isFinishing()) {
            doCallback(observer, buildH5Result(AliPayState.ERROR_ACTIVITY_NULL,
                    AliPayState.getTipMsg(AliPayState.ERROR_ACTIVITY_NULL)));
            doComplete();
            return;
        }

        if (mProcessing.get()) {
            doCallback(observer, buildH5Result(AliPayState.ERROR_PAYING,
                    AliPayState.getTipMsg(AliPayState.ERROR_PAYING)));
            return;
        }

        /**
         * 推荐采用的新的二合一接口(payInterceptorWithUrl),只需调用一次
         */
        final PayTask task = new PayTask(activity);
        mProcessing.set(true);
        mObserver = observer;
        boolean isIntercepted = task.payInterceptorWithUrl(url, true, new H5PayCallback() {
            @Override
            public void onPayResult(final H5PayResultModel result) {
                doCallback(mObserver, buildH5Result(AliPayState.SUCCESS_PAY, result.getResultCode(),
                        result.getReturnUrl(), true));
                doComplete();
            }
        });
        doCallback(mObserver, buildH5Result(AliPayState.H5_INTERCEPTED,
                AliPayState.getTipMsg(AliPayState.H5_INTERCEPTED), isIntercepted));
    }

    private class InternalPayTask extends PayAsyncTask<String, Void, Map<String, String>> {

        public InternalPayTask(ITaskManager manager) {
            super(manager);
        }

        @Override
        protected Map<String, String> doInBackground(String... params) {
            Activity activity = getActivity();
            if (activity == null || activity.isFinishing()) {
                return null;
            }
            PayTask pay = new PayTask(activity);
            String version = pay.getVersion();
            Map<String, String> map = pay.payV2(params[0], true);
            map = null == map ? new HashMap<String, String>(2) : map;
            map.put("version", version);
            return map;
        }

        @Override
        protected void onPostExecute(Map<String, String> result) {
            super.onPostExecute(result);
            if (null == result) {
                doCallback(mObserver, AliPayState.ERROR_ACTIVITY_NULL,
                        AliPayState.getTipMsg(AliPayState.ERROR_ACTIVITY_NULL));
                doComplete();
                return;
            }
            PayResult payResult = new PayResult(result);
            // 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
            String resultStatus = payResult.getResultStatus();
            if (TextUtils
                    .equals(resultStatus, AliPayState.getStringState(AliPayState.SUCCESS_PAY))) {
                doCallback(mObserver, buildResult(payResult, AliPayState.SUCCESS_PAY));
            } else {
                if (TextUtils.equals(resultStatus,
                        AliPayState.getStringState(AliPayState.ERROR_INDETERMINATE))) {
                    doCallback(mObserver, buildResult(payResult, AliPayState.ERROR_INDETERMINATE));
                } else {
                    // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                    doCallback(mObserver, buildResult(payResult, AliPayState.ERROR_PAY));
                }
            }
            doComplete();
        }
    }

    private void doComplete() {
        mProcessing.set(false);
        destroy();
    }

    private String buildH5Result(int code, String result, boolean isIntercepted) {
        JSONObject object = new JSONObject();
        try {
            object.put(PayConstants.CODE, code);
            object.put(PayResult.RESULT, result);
            object.put(PayConstants.IS_INTERCEPTED, isIntercepted);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object.toString();
    }

    private String buildH5Result(int code, String result) {
        JSONObject object = new JSONObject();
        try {
            object.put(PayConstants.CODE, code);
            object.put(PayResult.RESULT, result);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object.toString();
    }

    private String buildH5Result(int code, String resultCode, String returnUrl,
            boolean isIntercepted) {
        JSONObject object = new JSONObject();
        try {
            object.put(PayConstants.CODE, code);
            object.put(PayConstants.RETURN_CODE, resultCode);
            object.put(PayConstants.RETURN_URL, returnUrl);
            object.put(PayConstants.IS_INTERCEPTED, isIntercepted);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object.toString();
    }

    private String buildResult(PayResult payResult, int code) {
        JSONObject object = new JSONObject();
        try {
            object.put(PayConstants.CODE, code);
            object.put(PayResult.RESULT_STATUS, payResult.getResultStatus());
            object.put(PayResult.RESULT, payResult.getResult());
            object.put(PayResult.MEMO, payResult.getMemo());
            object.put(PayResult.VERSION, payResult.getVersion());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object.toString();
    }

    private void doCallback(Observer observer, int code, String result) {
        JSONObject object = new JSONObject();
        try {
            object.put(PayConstants.CODE, code);
            object.put(PayResult.RESULT_STATUS, result);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        doCallback(observer, object.toString());
    }

    private void doCallback(Observer observer, String result) {
        if (null != observer) {
            observer.update(null, result);
        }
    }

    public void destroy() {
        mProcessing.set(false);
        mObserver = null;
        sPayManager = null;
        mTaskManager.reset();
    }
}
