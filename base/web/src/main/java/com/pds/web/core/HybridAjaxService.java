package com.pds.web.core;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.blog.pds.net.BuildConfig;
import com.google.gson.Gson;
import com.pds.web.CommonPath;
import com.pds.web.action.HybridAction;
import com.pds.web.param.HybridVersionEntity;
import com.pds.web.util.FileUtil;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

public class HybridAjaxService {

    private static Context mContext;

    private static HashMap<String, IApiService> mMap = new HashMap<>(1);

    public static IApiService getService(Uri uri) {
        String baseUrl = uri.getScheme() + "://" + uri.getHost();
        IApiService iApiService = mMap.get(baseUrl);
        if (null == iApiService) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            iApiService = retrofit.create(IApiService.class);
            mMap.put(baseUrl, iApiService);
        }
        return iApiService;
    }

    public interface IApiService {

        @GET("{path}")
        Call<String> get(@Path("path") String path, @QueryMap HashMap<String, String> params);

        @FormUrlEncoded
        @POST("{path}")
        Call<String> post(@Path(value = "path", encoded = true) String path, @FieldMap HashMap<String, String> params);

        @Streaming
        @GET
        Call<ResponseBody> download(@Url String url);

        /**
         * 获取更新版本号
         *
         * @return
         */
        @GET("/app/version/latestList?app=medlinker&sys_p=a")
        Call<HybridVersionEntity> requestVersion(@Query("cli_v") String version);
    }

    private static class CompareVersion {
        HybridVersionEntity localVersion;
        HybridVersionEntity remoteVersion;

        public CompareVersion(HybridVersionEntity localVersion, HybridVersionEntity remoteVersion) {
            this.localVersion = localVersion;
            this.remoteVersion = remoteVersion;
        }
    }

    public static void checkVersion(final Context context) {
        mContext = context.getApplicationContext();
        //1.服务器下载版本信息
        Uri uri = Uri.parse(CommonPath.getCheckversionUrl());
        IApiService service = HybridAjaxService.getService(uri);
        Call<HybridVersionEntity> call = service.requestVersion(BuildConfig.VERSION_NAME);
        call.enqueue(new Callback<HybridVersionEntity>() {
            @Override
            public void onResponse(Call<HybridVersionEntity> call, final Response<HybridVersionEntity> response) {
                AsyncTask asyncTask = new AsyncTask<Void, Void, CompareVersion>() {
                    @Override
                    protected CompareVersion doInBackground(Void... params) {
                        //2.对比本地保存是版本信息和服务器的版本信息是否一致
                        HybridVersionEntity localVersion;
                        HybridVersionEntity remoteVersion;

                        File version = new File(FileUtil.getRootDir(context), HybridConfig.FILE_HYBRID_DATA_VERSION);
                        if (!version.exists() || version.isDirectory() || TextUtils.isEmpty(FileUtil.readFile(version))) {
                            localVersion = null;
                            //3.本地保存版本信息
                            File target = FileUtil.rebuildFile(FileUtil.getRootDir(context), HybridConfig.FILE_HYBRID_DATA_VERSION);
                            FileUtil.writeFile(target, HybridAction.mGson.toJson(response.body()));
                            String versionStr = FileUtil.readFile(version);
                            remoteVersion = new Gson().fromJson(versionStr, HybridVersionEntity.class);
                        } else {
                            try {
                                localVersion = new Gson().fromJson(FileUtil.readFile(version), HybridVersionEntity.class);
                            } catch (Exception e) {
                                localVersion = null;
                            }
                            File target = FileUtil.rebuildFile(FileUtil.getRootDir(context), HybridConfig.FILE_HYBRID_DATA_VERSION);
                            FileUtil.writeFile(target, HybridAction.mGson.toJson(response.body()));
                            String remoteVersionStr = FileUtil.readFile(target);
                            remoteVersion = new Gson().fromJson(remoteVersionStr, HybridVersionEntity.class);
                        }
                        return new CompareVersion(localVersion, remoteVersion);
                    }

                    @Override
                    protected void onPostExecute(CompareVersion compareVersion) {
                        compareVersion(compareVersion.localVersion, compareVersion.remoteVersion);
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }

            @Override
            public void onFailure(Call<HybridVersionEntity> call, Throwable t) {

            }
        });
    }

    /**
     * 下载web业务包
     */
    private static void compareVersion(HybridVersionEntity localVersion, final HybridVersionEntity remoteVersion) {
        if (null == remoteVersion || remoteVersion.getErrcode() != 0) return;
        List<HybridVersionEntity.DataBean> data = remoteVersion.getData();
        if (null == data || data.isEmpty()) return;
        int size = data.size();
        if (null == localVersion || null == localVersion.getData()) {
            for (int i = 0; i < size; i++) {
                HybridVersionEntity.DataBean dataBean = data.get(i);
                if (dataBean.getVersion().equalsIgnoreCase("forbidden")) {//如果version为forbidden表示禁用增量包。
                    continue;
                }
                zipToSdcard(dataBean.getSrc(), dataBean.getChannel() + ".zip", dataBean.getChannel());
            }
            return;
        }
        List<HybridVersionEntity.DataBean> localVersionData = localVersion.getData();
        int localSize = localVersionData.size();
        for (int i = 0; i < size; i++) {
            HybridVersionEntity.DataBean dataBean = data.get(i);
            if (dataBean.getVersion().equalsIgnoreCase("forbidden")) {
                continue;
            }
            boolean localHas = true;
            for (int j = 0; j < localSize; j++) {
                HybridVersionEntity.DataBean localDateBean = localVersionData.get(j);
                if (dataBean.getChannel().equals(localDateBean.getChannel())) {
                    if (dataBean.getVersion().equalsIgnoreCase("forbidden")) {

                        Observable.just(dataBean.getChannel()).observeOn(Schedulers.io())
                                .doOnNext(new Consumer<String>() {
                                    @Override
                                    public void accept(String dataChannel) throws Exception {
                                        // delete local datafile
                                        File storageDirectory = new File(FileUtil.getRootDir(mContext), HybridConfig.FILE_HYBRID_DATA_PATH);
                                        File unZip = new File(storageDirectory, dataChannel);
                                        if (unZip.exists()) {
                                            FileUtil.clearFolder(unZip);
                                        }
                                    }
                                })
                                .subscribe();

                    } else {
                        localHas = dataBean.getVersion().equals(localDateBean.getVersion());
                    }
                }
            }
            if (!localHas)
                zipToSdcard(dataBean.getSrc(), dataBean.getChannel() + ".zip", dataBean.getChannel());
        }
    }

    private static void zipToSdcard(String url, final String zipFileName, final String zipFolderName) {
        final Uri uri = Uri.parse(url);
        IApiService service = HybridAjaxService.getService(uri);
        Call<ResponseBody> call = service.download(url);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, final Response<ResponseBody> response) {
                if (response == null) {
                    return;
                }
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        unZipFile(response, zipFileName, zipFolderName);
                        return null;
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
            }
        });
    }

    private static void unZipFile(Response<ResponseBody> response, String zipFileName, String zipFolderName) {
        File storageDirectory = new File(FileUtil.getRootDir(mContext), HybridConfig.FILE_HYBRID_DATA_PATH);
        if (!storageDirectory.exists()) {
            storageDirectory.mkdirs();
        }
        File zip = FileUtil.rebuildFile(storageDirectory, zipFileName);
        FileUtil.writeFile(zip, response.body());

        File unZip = new File(storageDirectory, zipFolderName);
        if (unZip.exists()) {
            FileUtil.clearFolder(unZip);
        } else {
            unZip.mkdirs();
        }
        FileUtil.unZip(zip.getAbsolutePath(), unZip.getAbsolutePath());
    }
}
