package com.pds.ndk;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;

/**
 * Created by Administrator on 2017/11/6.
 */

public class AndroidFixActivity extends AppCompatActivity {

  {
    System.loadLibrary("android-fix-lib");
  }
  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    DexManager.getInstance().setContext(this);
  }

  public void onFix(View view){
    new Caclutor().caculator(0);
  }

  public void onExc(View view){
    DexManager.getInstance().loadFile(new File(Environment.getExternalStorageDirectory(),"out1.dex"));
  }

  @Override
  public void onAttachFragment(Fragment fragment) {
    super.onAttachFragment(fragment);
  }
}
