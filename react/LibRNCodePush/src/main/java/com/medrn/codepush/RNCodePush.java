package com.medrn.codepush;

import android.app.Application;
import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ViewManager;
import com.medrn.codepush.entity.MetaInfoEntity;
import com.medrn.codepush.entity.PendingUpdateEntity;
import com.medrn.codepush.util.FileUtils;
import com.medrn.codepush.util.RNEncryptUtils;
import com.medrn.codepush.util.RNReloadUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Observer;

public class RNCodePush implements ReactPackage {

    private static RNCodePush sInstance;
    private static Application sApp;
    private static boolean sIsDebugMode;
    private static String sServerUrl;

    private static boolean sIsRunningBinaryVersion = true;
    private static boolean sIsLoadedJSBundle = false;

    private RNCodePushUpdateManager mUpdateManager;
    private RNSettingsManager mSettingsManager;

    public static void init(Application context, boolean isDebugMode, String serverUrl, final Observer observer) {
        sInstance = new RNCodePush(context, isDebugMode, serverUrl);
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                sInstance.checkDeleteOldVersionCodePushFiles();
                RNEncryptUtils.initEncryptFiles(sApp);
                sInstance.initializeUpdateAfterRestart();
                sInstance.checkRollback();
                return null;
            }

            @Override
            protected void onPostExecute(Void o) {
                observer.update(null, null);
            }
        };
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public static RNCodePush getInstance() {
        if (sInstance == null) {
            sInstance = new RNCodePush(sApp, sIsDebugMode, sServerUrl);
        }
        return sInstance;
    }

    public RNCodePush(Application context, boolean isDebugMode, String serverUrl) {
        sApp = context;
        sIsDebugMode = isDebugMode;
        sServerUrl = serverUrl;
        sIsRunningBinaryVersion = true;
        sIsLoadedJSBundle = false;
        mSettingsManager = new RNSettingsManager(context);
    }

    public String getServerUrl() {
        return sServerUrl;
    }

    public RNCodePushUpdateManager getUpdateManager() {
        if (mUpdateManager == null) {
            String rootFolderName = RNCodePushConstants.CODE_PUSH_ROOT_FOLDER + File.separator + FileUtils.getAppVersionName(sApp);
            mUpdateManager = new RNCodePushUpdateManager(sApp.getFilesDir().getAbsolutePath(),
                    FileUtils.appendPathComponent(sApp.getFilesDir().getAbsolutePath(), rootFolderName), getServerUrl());
        }
        return mUpdateManager;
    }

    public RNSettingsManager getSettingsManager() {
        if (mSettingsManager == null) {
            mSettingsManager = new RNSettingsManager(sApp);
        }
        return mSettingsManager;
    }

    public void checkDeleteOldVersionCodePushFiles() {
        File rootFile = new File(FileUtils.appendPathComponent(sApp.getFilesDir().getAbsolutePath(),
                RNCodePushConstants.CODE_PUSH_ROOT_FOLDER));
        if (!rootFile.exists()) {
            return;
        }
        File[] files = rootFile.listFiles();
        if (files == null) {
            return;
        }
        String currentAppVersion = FileUtils.getAppVersionName(sApp);
        for (File file : files) {
            if (file != null) {
                if (!TextUtils.equals(file.getName(), currentAppVersion)) {
                    FileUtils.deleteDirectoryAtPath(file.getPath());
                }
            }
        }
    }

    public static String getJSBundleFile() {
        sIsLoadedJSBundle = true;
        String jsBundleFile;
        if (sInstance != null) {
            jsBundleFile = sInstance.getJSBundleFileInternal();
        } else {
            jsBundleFile = getDefaultJsBundleFile();
        }
        return jsBundleFile;
    }

    private static String getDefaultJsBundleFile() {
//        RNEncryptUtils.initEncryptFiles(sApp);
        String decryptJsBundleFilePath = FileUtils.appendPathComponent(getInstance().getUpdateManager().getBaseBundleFilePath(),
                RNCodePushConstants.DEFAULT_JS_BUNDLE_FILE_NAME);
        if (BuildConfig.DEBUG && !FileUtils.fileAtPathExists(decryptJsBundleFilePath)) {
            return RNCodePushConstants.ASSETS_BUNDLE_PREFIX + RNCodePushConstants.DEFAULT_JS_BUNDLE_FILE_NAME;
        }
        return decryptJsBundleFilePath;
    }

    private String getJSBundleFileInternal() {
        String binaryJsBundleUrl = getDefaultJsBundleFile();

        String packageFilePath = getUpdateManager().getCurrentPackageBundlePath();
        if (packageFilePath == null) {
            // There has not been any downloaded updates.
            sIsRunningBinaryVersion = true;
            return binaryJsBundleUrl;
        }

        MetaInfoEntity metaInfo = getUpdateManager().getCurrentPackageMetaInfo();
        if (isLastBundlePackage(metaInfo)) {
            sIsRunningBinaryVersion = false;
            if (getSettingsManager().isUnloadUpdate(metaInfo)) { // 有未加载的更新，才需要做回滚逻辑
                getSettingsManager().saveFailedUpdate(metaInfo.toJSONObject());
                getSettingsManager().removeUnloadUpdate();
            }
            return packageFilePath;
        } else {
            if (!sIsDebugMode) {
                clearUpdates();
            }
            sIsRunningBinaryVersion = true;
            return binaryJsBundleUrl;
        }
    }

    private boolean isRunningBinaryVersion() {
        return sIsRunningBinaryVersion;
    }

    public MetaInfoEntity getCurrentLoadPackageMetaInfo() {
        if (isRunningBinaryVersion()) {
            return getUpdateManager().getAssetsMetaInfo();
        }
        return getUpdateManager().getCurrentPackageMetaInfo();
    }

    private boolean isLastBundlePackage(MetaInfoEntity metaInfo) {
        if (metaInfo == null || !metaInfo.isExist()) {
            return false;
        }
        MetaInfoEntity assetsMetaInfo = getAssetsMetaInfo();
        return !TextUtils.equals(metaInfo.getHash(), assetsMetaInfo.getHash())
                && metaInfo.getBuildTime() > assetsMetaInfo.getBuildTime();
    }

    private MetaInfoEntity getAssetsMetaInfo() {
        try {
            String metaJson = FileUtils.getJsonStringFromFile(new File(FileUtils.appendPathComponent(
                    getUpdateManager().getBaseBundleFilePath(), RNCodePushConstants.STATUS_FILE_NAME)));
            JSONObject jsonObject = new JSONObject(metaJson);
            return new MetaInfoEntity(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
            throw new RuntimeException("Error in assets://android.meta", e);
        }
    }

    public void clearUpdates() {
        getUpdateManager().clearUpdates();
        getUpdateManager().removePendingUpdate();
        getSettingsManager().removeFailedUpdates();
        getSettingsManager().removePendingUpdate();
    }

    public void removeCurrentFailedUpdates() {
        MetaInfoEntity infoEntity = getCurrentLoadPackageMetaInfo();
        getSettingsManager().removeFailedUpdates(infoEntity.getHash());
    }

    public static boolean isLoadedJSBundle() {
        return sIsLoadedJSBundle;
    }

    private void initializeUpdateAfterRestart() {
        try {
            PendingUpdateEntity pendingUpdateEntity = getSettingsManager().getPendingUpdate();
            if (pendingUpdateEntity.isExist() && pendingUpdateEntity.isLoading()) {
                MetaInfoEntity metaInfoEntity = getUpdateManager().getCurrentPackageMetaInfo();
                boolean isNeedUpdate = false;
                if (metaInfoEntity.isExist()) {
                    if (getUpdateManager().isUpdate(pendingUpdateEntity, metaInfoEntity)) {
                        isNeedUpdate = true;
                    }
                } else {
                    MetaInfoEntity assetsMetaInfo = getAssetsMetaInfo();
                    if (getUpdateManager().isUpdate(pendingUpdateEntity, assetsMetaInfo)
                            && getUpdateManager().isSameBaseVersion(pendingUpdateEntity, assetsMetaInfo)) {
                        isNeedUpdate = true;
                    }
                }

                if (isNeedUpdate) {
                    getUpdateManager().checkChangeCodePushPath(sApp);
                }
                getSettingsManager().removePendingUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 检查回滚
     */
    private void checkRollback() {
        MetaInfoEntity metaInfoEntity = getUpdateManager().getCurrentPackageMetaInfo();
        if (getSettingsManager().isFailedHash(metaInfoEntity.getHash())) {
            getUpdateManager().rollbackPackage();
        }
    }

    public void checkUpdate(Context context, boolean auto) {
        checkUpdate(context, auto, false, null);
    }

    /**
     * 从远程下载meta文件，用于检查更新
     *
     * @param auto                true:自动后台更新， false:显示弹窗，用户手动确认更新
     * @param isFunctionCheck     是否功能检测（在加载界面不存在时，需要检查更新，并给用户提示）
     * @param checkUpdateCallBack 是否有更新回调
     */
    public void checkUpdate(Context context, boolean auto, boolean isFunctionCheck, CallBack<Boolean> checkUpdateCallBack) {
        if (!isDebug()) {
            getUpdateManager().checkUpdate(context, auto, checkUpdateCallBack, isFunctionCheck);
        }
    }

    public void checkReload() {
        if (!isDebug()) {
            PendingUpdateEntity pendingUpdateEntity = getSettingsManager().getPendingUpdate();
            if (pendingUpdateEntity.isExist() && pendingUpdateEntity.isLoading()) {
                sInstance = new RNCodePush(sApp, sIsDebugMode, sServerUrl);
                initializeUpdateAfterRestart();
                checkRollback();
                RNReloadUtil.getInstance().reloadBundle(sApp);
            }
        }
    }

    private boolean isDebug() {
        return BuildConfig.DEBUG;
    }

    @Override
    public List<NativeModule> createNativeModules(ReactApplicationContext reactContext) {
        List<NativeModule> modules = new ArrayList<>();
        modules.add(new MedHotUpdateModule(reactContext));
        return modules;
    }

    @Override
    public List<ViewManager> createViewManagers(ReactApplicationContext reactContext) {
        return Arrays.asList();
    }
}
