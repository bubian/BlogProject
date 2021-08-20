package com.pds.fmod;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;

/**
 * Created by Administrator on 2017/10/22.
 */

public class MainActivity extends AppCompatActivity {

  private static final String TAG = "MainActivity";
  private ImageView mImageViewN;
  private ImageView mImageViewL;
  private ImageView mImageViewD;
  private ImageView mImageViewJ;
  private ImageView mImageViewG;
  private ImageView mImageViewK;

  private native void fix(String path , int mode);

  {
    System.loadLibrary("change-voice");
    System.loadLibrary("fmodL");
    System.loadLibrary("fmod");
  }
  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    mImageViewN = findViewById(R.id.btn_normal);
    mImageViewL = findViewById(R.id.btn_luoli);
    mImageViewD = findViewById(R.id.btn_dashu);
    mImageViewJ = findViewById(R.id.btn_jingsong);
    mImageViewG = findViewById(R.id.btn_gaoguai);
    mImageViewK = findViewById(R.id.btn_kongling);
  }

  @Override
  protected void onResume() {
    super.onResume();

  }

  public void mFix(View view){
    switch (view.getId()){
      case R.id.btn_normal:
        new Thread(() -> {
          File file = new File(Environment.getExternalStorageDirectory() + File.separator +"hongdou.mp3");
          Log.d(TAG,"path = "+ file.getAbsolutePath());
          fix(file.getAbsolutePath(),0);
        }).start();
        break;
    }

  }

}
