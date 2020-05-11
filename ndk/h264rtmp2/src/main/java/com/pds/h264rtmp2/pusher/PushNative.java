package com.pds.h264rtmp2.pusher;

/**
 * Created by david on 2017/10/13.
 */

public class PushNative {
    static {
        System.loadLibrary("native-lib");
    }

    //    设置视频参数
    public native void setVideoOptions(int width, int height, int bitrate, int fps);

    //    设置视频参数
    public native void setAudioOptions(int sampleRate, int channel);

    //    推视频流
    public native void frieAudio(byte[] data,int len);


    //    推视频流
    public native void frieVedeo(byte[] data);

    public native void startPush(String url);
}
