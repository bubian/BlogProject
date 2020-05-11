package com.pds.h264rtmp2;

import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.pds.h264rtmp2.pusher.LivePusher;


public class MainActivity extends AppCompatActivity {
//    private static String URL = "rtmp://59.110.240.67/myapp/mystream";
    private LivePusher livePusher;
    private static String URL = "rtmp://send3.douyu.com/live/3251491rhfeXZHfM?wsSecret=8ffc67e1e629c49f1b23a1ed5c2f8dd5&wsTime=59f9d8cd&wsSeek=off";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.surface);
        //相机图像的预览
        livePusher = new LivePusher(this,surfaceView.getHolder(),URL);
    }

    public void start(View view) {
        Button btn = (Button)view;
        if(btn.getText().equals("开始直播")){
            livePusher.startPush();
            btn.setText("停止直播");
        }else{
            livePusher.stopPush();
            btn.setText("开始直播");
        }
    }
    public void switchVideo(View view) {
        livePusher.switchCamera();
    }

}
