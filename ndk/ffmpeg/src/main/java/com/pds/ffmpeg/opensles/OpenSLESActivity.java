package com.pds.ffmpeg.opensles;

import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

import md.edu.pds.kt.ffmpeg.R;

public class OpenSLESActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_opensles);
    }

    public void palyer(View view) {
        FfmpegPlayer player = new FfmpegPlayer();
        player.sound();
    }

}
