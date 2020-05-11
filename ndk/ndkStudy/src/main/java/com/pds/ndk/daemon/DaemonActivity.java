package com.pds.ndk.daemon;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.pds.ndk.R;
import com.pds.ndk.daemon.service.JobHandleService;
import com.pds.ndk.daemon.service.LocalService;
import com.pds.ndk.daemon.service.RemoteService;

public class DaemonActivity extends AppCompatActivity {


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    startService(new Intent(this, LocalService.class));
    startService(new Intent(this, RemoteService.class));
    startService(new Intent(this, JobHandleService.class));
  }
}
