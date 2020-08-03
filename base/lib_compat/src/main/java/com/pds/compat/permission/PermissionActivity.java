package com.pds.compat.permission;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.pds.compat.R;
import com.tbruyelle.rxpermissions3.RxPermissions;

import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;
import pub.devrel.easypermissions.PermissionRequest;

/**
 * @author: pengdaosong
 * CreateTime:  2020-08-03 13:45
 * Email：pengdaosong@medlinker.com
 * Description: 多种权限申请方式
 *
 * 参考：https://developer.android.google.cn/guide/topics/security/permissions?hl=zh-cn
 *
 * 使用 adb 工具从命令行管理权限：
 *    查看所以权限：adb shell pm list permissions
 *    按组列出权限和状态：adb shell pm list permissions -d -g
 *    授予或撤消一项或多项权限：adb shell pm [grant|revoke] <permission-name> ...
 */
@RequiresApi(api = Build.VERSION_CODES.M)
public class PermissionActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * 使用Android系统Api进行权限申请
     * 参考：https://developer.android.google.cn/training/permissions/requesting#java
     */

    private static final  int CAMERA_REQUEST_CODE = 1;
    private void doPermissionCompatAndroidApi(){
        // 检查用户是否已向您的应用授予特定权限,方法会返回 PERMISSION_GRANTED 或 PERMISSION_DENIED
        int re = ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA);
        if (re == PackageManager.PERMISSION_GRANTED){ // 已授予权限

        }else if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)){ // 是否应显示具有请求权限的UI界面

        }else {// 没有授予权限，进行权限请求
            requestPermissions(new String[] { Manifest.permission.CAMERA },CAMERA_REQUEST_CODE);
            // 调用 launch() 之后，系统会显示系统权限对话框。当用户做出选择后，系统会异步调用您在上一步中定义的 ActivityResultCallback 实现
            // requestPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    /**
     * 由Android系统管理权限
     * 如需请求一项权限，请使用 RequestPermission。
     * 如需同时请求多项权限，请使用 RequestMultiplePermissions。
     */
    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // Permission is granted.
                } else {
                    doPermissionCompatAndroidApi();
                }
    });

    /**
     * 自行管理权限请求
     * 当用户响应系统权限对话框后，系统调用该方法
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case CAMERA_REQUEST_CODE:{
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){ // 权限被已授予

                }else {

                }
                return;
            }
        }

        // 使用EasyPermissions库方式，将结果转发到EasyPermissions
        // EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    /**
     * 使用RxPermissions进行权限申请
     */

    private void doPermissionCompatRxPermissions(){
        final RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions
                .request(Manifest.permission.CAMERA)
                .subscribe(granted -> {
                    if (granted) { // Always true pre-M
                        // I can control the camera now
                    } else {
                        // Oups permission denied
                    }
                });

        // 如果同时具有多个权限，则将结果合并
        rxPermissions
                .request(Manifest.permission.CAMERA,
                        Manifest.permission.READ_PHONE_STATE)
                .subscribe(granted -> {
                    if (granted) {
                        // All requested permissions are granted
                    } else {
                        // At least one permission is denied
                    }
                });

        rxPermissions
                .requestEach(Manifest.permission.CAMERA,
                        Manifest.permission.READ_PHONE_STATE)
                .subscribe(permission -> { // will emit 2 Permission objects
                    if (permission.granted) {
                        // `permission.name` is granted !
                    } else if (permission.shouldShowRequestPermissionRationale) {
                        // Denied permission without ask never again
                    } else {
                        // Denied permission with ask never again
                        // Need to go to the settings
                    }
                });

        rxPermissions
                .requestEachCombined(Manifest.permission.CAMERA,
                        Manifest.permission.READ_PHONE_STATE)
                .subscribe(permission -> { // will emit 1 Permission object
                    if (permission.granted) {
                        // All permissions are granted !
                    } else if (permission.shouldShowRequestPermissionRationale){
                        // At least one denied permission without ask never again
                    } else {
                        // At least one denied permission with ask never again
                        // Need to go to the settings
                    }
                });

    }

    // EasyPermissions 使用注解的方式
    @AfterPermissionGranted(CAMERA_REQUEST_CODE)
    private void methodRequiresCamrea(){
        String[] perms = {Manifest.permission.CAMERA};
        if (EasyPermissions.hasPermissions(this, perms)) {
            // Already have permission, do the thing
            // ...
        } else {
            // Do not have permissions, request them now
            // EasyPermissions.requestPermissions(this, "请求相机权限", CAMERA_REQUEST_CODE, perms);
            //
            EasyPermissions.requestPermissions(
                    new PermissionRequest.Builder(this, CAMERA_REQUEST_CODE, perms)
                            .setRationale("请求相机权限")
                            .setPositiveButtonText("确定")
                            .setNegativeButtonText("取消")
                            .setTheme(R.style.Theme_AppCompat)
                            .build());
        }
    }

    // EasyPermissions 使用接口回调方式

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }
}
