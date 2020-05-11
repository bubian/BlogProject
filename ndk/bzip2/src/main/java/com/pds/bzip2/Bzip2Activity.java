package com.pds.bzip2;

import android.Manifest.permission;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.File;
import java.util.List;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;


/**
 * Created by Administrator on 2017/10/15.
 */

public class Bzip2Activity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

  private static final String TAG = "Bzip2Activity";
  private static final int READ_EXTERNAL_STORAGE = 0;
  private static final String PATH = Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separatorChar;
  private MediaPlayer mediaPlayer;

  private static native int patch(String oldPath, String newPath, String patchPath);

  public static final String SD_CARD = Environment.getExternalStorageDirectory() + File.separator;

  //新版本apk的目录
  public static final String NEW_APK_PATH = SD_CARD+"apk_new.apk";

  static {
    System.loadLibrary("native-lib");
  }

  private TextView mTextView;
  private Button mButton1;
  private Button mButton2;
  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    mTextView = findViewById(R.id.text_font);
    mButton1 = findViewById(R.id.btn_1);
    mButton2 = findViewById(R.id.btn_2);
    mButton2.setVisibility(View.GONE);
    mTextView.setVisibility(View.GONE);
    mButton1.setText("增量更新");

  }

  public void onSplitVideo(View view){
    Log.d(TAG, "diff begin----");

        if (VERSION.SDK_INT >= VERSION_CODES.M){
          if (checkSelfPermission(permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(Bzip2Activity.this,new String[]{permission.READ_EXTERNAL_STORAGE},READ_EXTERNAL_STORAGE);
          }
        }else {
          patchApk();
        }
    Log.d(TAG, "diff end----");
  }

  private void patchApk(){
    String oldfile = ApkUtils.getSourceApkPath(this, getPackageName());
    String newFile = NEW_APK_PATH;
    String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separatorChar + "apk.patch";
    patch(oldfile,newFile,path);
    ApkUtils.installApk(this, NEW_APK_PATH);
  }


  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
      @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults,this);
  }

  @Override
  public void onPermissionsGranted(int requestCode, List<String> perms) {
    if (requestCode == READ_EXTERNAL_STORAGE) {
      patchApk();
    }
  }

  @Override
  public void onPermissionsDenied(int requestCode, List<String> perms) {
    if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
      new AppSettingsDialog.Builder(this).build().show();
    }
  }
}
