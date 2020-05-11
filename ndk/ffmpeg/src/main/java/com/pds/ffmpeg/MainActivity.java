package com.pds.ffmpeg;

import android.os.Bundle;
import android.os.Environment;
import android.view.SurfaceView;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;

import md.edu.pds.kt.ffmpeg.R;

public class MainActivity extends AppCompatActivity {

    static{
        System.loadLibrary("avcodec-56");
        System.loadLibrary("avdevice-56");
        System.loadLibrary("avfilter-5");
        System.loadLibrary("avformat-56");
        System.loadLibrary("avutil-54");
        System.loadLibrary("postproc-53");
        System.loadLibrary("swresample-1");
        System.loadLibrary("swscale-3");
        System.loadLibrary("native-lib");
    }
    FfmpegPlayer ffmpegPlayer;
    SurfaceView surfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_ffmpeg);
        surfaceView = findViewById(R.id.surface);
        ffmpegPlayer = new FfmpegPlayer();
        ffmpegPlayer.setSurfaceView(surfaceView);
    }

    public void player(View view) {
        File file = new File(Environment.getExternalStorageDirectory(), "Warcraft3_End.avi");
        ffmpegPlayer.playJava("rtmp://59.110.240.67/myapp/mystream");
//        ffmpegPlayer.playJava(file.getAbsolutePath());
    }
    public void stop(View view) {
        ffmpegPlayer.release();
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native void open(String inputStr, String outStr);

    public void load(View view) {
        String input = new File(Environment.getExternalStorageDirectory(),"input.mp4").getAbsolutePath();
        String output = new File(Environment.getExternalStorageDirectory(),"output_1280x720_yuv420p.yuv").getAbsolutePath();
        open(input,output);
    }
}
