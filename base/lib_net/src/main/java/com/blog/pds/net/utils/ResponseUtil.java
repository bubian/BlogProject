package com.blog.pds.net.utils;

import android.net.ParseException;
import android.text.TextUtils;

import androidx.annotation.StringRes;
import com.blog.pds.net.exception.ApiException;
import com.google.gson.JsonParseException;
import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONException;

import java.net.ConnectException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.SSLHandshakeException;

import retrofit2.HttpException;

public class ResponseUtil {


    /**
     *
     */
    public static void errorHandler(Throwable e) {
        if (e instanceof ApiException) {
            ApiException apiException = (ApiException) e;
            String errorMsg = apiException.getMsg();
            if (!TextUtils.isEmpty(errorMsg)) {
//                ToastUtil.showMessage(errorMsg);
            }
            return;
        }
        if (e instanceof HttpException) {
            HttpException httpException = (HttpException) e;
//            ToastUtil.showMessage(getString(R.string.throwable_http_exception) + "(" + httpException.code() + ")");
        } else if (e instanceof ConnectException) {
//            ToastUtil.showMessage(getString(R.string.throwable_connect_exception));
        } else if (e instanceof JsonParseException
                || e instanceof JSONException
                || e instanceof ParseException) {
//            ToastUtil.showMessage(getString(R.string.throwable_parse_exception));
//            ToastUtil.showMessageLong(e.getMessage());
        } else if (e instanceof ConnectTimeoutException
                || e instanceof SocketTimeoutException
                || e instanceof TimeoutException) {
//            ToastUtil.showMessage(getString(R.string.throwable_timeout_exception));
        } else if (e instanceof SSLHandshakeException) {
//            ToastUtil.showMessage(getString(R.string.throwable_ssl_handshake_exception));
        } else if (e instanceof UnknownHostException
                || e instanceof SocketException) {
//            ToastUtil.showMessage(getString(R.string.throwable_unknown_host_exception));
        } else {
            e.printStackTrace();
//            ToastUtil.showMessage(e.getMessage());
        }
    }

    private static String getString(@StringRes int resId) {
//        return BaseApplication.getApplication().getString(resId);
        return "";
    }

    /**
     * 领导要求客户端把server返回的英文错误转成中文。。。。。。
     *
     * @param msg
     * @return
     */
    public static String transErrorMsg(String msg) {
        String pattern = "(?:sql|system|error|server)";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(msg);
//        if (m.find()) {
//            return getString(R.string.server_error);
//        }
        return msg;
    }
}
