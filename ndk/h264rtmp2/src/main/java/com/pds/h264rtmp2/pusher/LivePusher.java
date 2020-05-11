package com.pds.h264rtmp2.pusher;

import android.app.Activity;
import android.hardware.Camera;
import android.view.SurfaceHolder;

import com.pds.h264rtmp2.params.AudioParam;
import com.pds.h264rtmp2.params.VideoParam;

/**
 * Created by david on 2017/10/11.
 */

public class LivePusher {
    private SurfaceHolder surfaceHolder;
    private AudioPusher audioPusher;
    private VideoPusher videoPusher;
    private PushNative pushNative;
    public LivePusher(Activity activity, SurfaceHolder surfaceHolder, String url) {
        this.surfaceHolder = surfaceHolder;
        init(activity,url);
    }

    private void init(Activity activity, String url) {
        pushNative = new PushNative();
        audioPusher = new AudioPusher(pushNative,new AudioParam(44100,1));
        videoPusher = new VideoPusher(url,activity,new VideoParam(480,320, Camera.CameraInfo.CAMERA_FACING_BACK),surfaceHolder);
        videoPusher.setPushNative(pushNative);
        pushNative.startPush(url);
    }

    public void startPush() {
        videoPusher.startPush();
        audioPusher.startPush();
    }

    public void stopPush() {

    }

    public void switchCamera() {
        videoPusher.switchCamera();
    }
}
