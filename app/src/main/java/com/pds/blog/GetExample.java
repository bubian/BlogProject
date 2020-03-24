package com.pds.blog;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.EventListener;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * @author: pengdaosong
 * CreateTime:  2019/5/9 10:33 AM
 * Email：pengdaosong@medlinker.com
 * Description:
 */

public class GetExample {

    String getRequst(String url) throws IOException {
        OkHttpClient client = new OkHttpClient.Builder()
                .eventListener(new EventListener() {
                    @Override
                    public void callStart(Call call) {
                        // 开始执行任务前调用
                    }
                })
                .build();

        Request request = new Request.Builder()
                .url(url)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }

    public static Response postRequst(String url, String json) throws IOException {
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(JSON,json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        return response;
    }
}
