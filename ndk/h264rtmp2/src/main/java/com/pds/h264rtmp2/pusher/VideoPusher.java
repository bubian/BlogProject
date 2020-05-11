package com.pds.h264rtmp2.pusher;

import android.app.Activity;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;


import com.pds.h264rtmp2.params.VideoParam;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by david on 2017/10/11.
 */

public class VideoPusher extends  Pusher implements SurfaceHolder.Callback, Camera.PreviewCallback {

    private Camera camera;
    private VideoParam videoParam;
    private SurfaceHolder surfaceHolder;
    private byte[] buffers;
    private PushNative pushNative;
    private Activity mActivity;
    private int screen;

    private byte[] raw;
    private final static int SCREEN_PORTRAIT = 0;
    private final static int SCREEN_LANDSCAPE_LEFT = 90;
    private final static int SCREEN_LANDSCAPE_RIGHT = 270;
    private String url;
    public void setPushNative(PushNative pushNative) {
        this.pushNative = pushNative;
    }

    public VideoPusher(String url, Activity activity, VideoParam videoParam, SurfaceHolder surfaceHolder) {
        this.videoParam = videoParam;
        this.surfaceHolder = surfaceHolder;
        this.mActivity = activity;
        surfaceHolder.addCallback(this);
        this.url = url;
    }

    @Override
    public void startPush() {
        startPreview();
        isPushing = true;

    }

    @Override
    public void stopPush() {

    }

    @Override
    public void relase() {

    }
    public  void switchCamera(){

        if(videoParam.getCameraId() == Camera.CameraInfo.CAMERA_FACING_BACK){
            videoParam.setCameraId(Camera.CameraInfo.CAMERA_FACING_FRONT);
        }else{
            videoParam.setCameraId(Camera.CameraInfo.CAMERA_FACING_BACK);
        }
        //重新预览
        stopPreview();
        startPreview();
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
    }

    private void stopPreview(){
        if (camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }
    /**
     * 开始预览
     */
    private void startPreview() {
        try {
            pushNative.setVideoOptions(videoParam.getWidth(),
                    videoParam.getHeight(), videoParam.getBitrate(),
                    videoParam.getFps());
            //SurfaceView初始化完成，开始相机预览
            camera = Camera.open(videoParam.getCameraId());
            Camera.Parameters parameters = camera.getParameters();
            //设置相机参数  没用
            parameters.setPreviewFormat(ImageFormat.NV21); //YUV 预览图像的像素格式
            setPreviewSize(parameters);
            setPreviewOrientation(parameters);
            camera.setParameters(parameters);
            //parameters.setPreviewFpsRange(videoParam.getFps()-1, videoParam.getFps());
            camera.setPreviewDisplay(surfaceHolder);
            //获取预览图像数据
            int bitPerPixel= ImageFormat.getBitsPerPixel(ImageFormat.NV21);
//            videoParam.getWidth() * videoParam.getHeight()*bitPerPixel/8  数据大
            buffers = new byte[videoParam.getWidth() * videoParam.getHeight()*bitPerPixel/8];
            raw=new byte[videoParam.getWidth() * videoParam.getHeight()*bitPerPixel/8];
//       相机26%
            camera.addCallbackBuffer(buffers);
            camera.setPreviewCallbackWithBuffer(this);
            camera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        stopPreview();
        startPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

    }

    /**
     * 相机预览到的画面进行回调
     * @param bytes
     * @param camera
     *
     * bytes  buffer
     * 回调
     * NV21   ---->流媒体
     * 斗鱼
     *
     * onPreviewFrame
     * NV21
     *
     * yuv
     * rtmp nv21
     * yuv
     */
    @Override
    public void onPreviewFrame(byte[] bytes, Camera camera) {
        if (isPushing) {
            switch (screen) {
                case SCREEN_PORTRAIT:
                    portraitData2Raw(buffers);
                    break;
//                头  是在左边  home  右边
                case SCREEN_LANDSCAPE_LEFT:
                    raw=buffers;
                    break;
                case SCREEN_LANDSCAPE_RIGHT:
                    landscapeData2Raw(buffers);
                    break;

            }
        }
        if (camera != null) {
//            不这写的话 只会调用一次
            camera.addCallbackBuffer(buffers);
        }
        pushNative.frieVedeo(raw);
//        nv21  yuv   h264
    }
    private void setPreviewSize(Camera.Parameters parameters) {
        List<Integer> supportedPreviewFormats = parameters.getSupportedPreviewFormats();
        for (Integer integer : supportedPreviewFormats) {
            System.out.println("支持:" + integer);
        }
        List<Camera.Size> supportedPreviewSizes = parameters.getSupportedPreviewSizes();
        Camera.Size size = supportedPreviewSizes.get(0);
        Log.d(TAG, "支持 " + size.width + "x" + size.height);
        int m = Math.abs(size.height * size.width - videoParam.getHeight() * videoParam.getWidth());
        supportedPreviewSizes.remove(0);
        Iterator<Camera.Size> iterator = supportedPreviewSizes.iterator();
        while (iterator.hasNext()) {
            Camera.Size next = iterator.next();
            Log.d(TAG, "支持 " + next.width + "x" + next.height);
            int n = Math.abs(next.height * next.width - videoParam.getHeight() *videoParam.getWidth());
            if (n < m) {
                m = n;
                size = next;
            }
        }
        videoParam.setHeight(size.height);
        videoParam.setWidth(size.width);
        parameters.setPreviewSize(videoParam.getWidth(),videoParam.getHeight());
        Log.d(TAG, "预览分辨率 width:" + size.width + " height:" + size.height);
    }
    private void setPreviewOrientation(Camera.Parameters parameters) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(videoParam.getCameraId(), info);
        int rotation = mActivity.getWindowManager().getDefaultDisplay().getRotation();
        screen = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                screen = SCREEN_PORTRAIT;
                pushNative.setVideoOptions(videoParam.getHeight(), videoParam.getWidth(), videoParam.getBitrate(), videoParam.getFps());
                break;
            case Surface.ROTATION_90: // 横屏 左边是头部(home键在右边)
                screen = SCREEN_LANDSCAPE_LEFT;
                pushNative.setVideoOptions(videoParam.getWidth(), videoParam.getHeight(), videoParam.getBitrate(), videoParam.getFps());
                break;
            case Surface.ROTATION_180:
                screen = 180;
                break;
            case Surface.ROTATION_270:// 横屏 头部在右边
                screen = SCREEN_LANDSCAPE_RIGHT;
                pushNative.setVideoOptions(videoParam.getWidth(), videoParam.getHeight(), videoParam.getBitrate(), videoParam.getFps());
                break;
        }
        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + screen) % 360;
            result = (360 - result) % 360; // compensate the mirror
        } else { // back-facing
            result = (info.orientation - screen + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }

    private void landscapeData2Raw(byte[] data) {
        int width =videoParam.getWidth();
        int height = videoParam.getHeight();
        int y_len = width * height;
        int k = 0;
        // y数据倒叙插入raw中
        for (int i = y_len - 1; i > -1; i--) {
            raw[k] = data[i];
            k++;
        }
        // System.arraycopy(data, y_len, raw, y_len, uv_len);
        // v1 u1 v2 u2
        // v3 u3 v4 u4
        // 需要转换为:
        // v4 u4 v3 u3
        // v2 u2 v1 u1
        int maxpos = data.length - 1;
        int uv_len = y_len >> 2; // 4:1:1
        for (int i = 0; i < uv_len; i++) {
            int pos = i << 1;
            raw[y_len + i * 2] = data[maxpos - pos - 1];
            raw[y_len + i * 2 + 1] = data[maxpos - pos];
        }
    }

    private void portraitData2Raw(byte[] data) {
        // if (mContext.getResources().getConfiguration().orientation !=
        // Configuration.ORIENTATION_PORTRAIT) {
        // raw = data;
        // return;
        // }
        int width = videoParam.getWidth(), height = videoParam.getHeight();
        int y_len = width * height;
        int uvHeight = height >> 1; // uv数据高为y数据高的一半
        int k = 0;
        if ( videoParam.getCameraId() == Camera.CameraInfo.CAMERA_FACING_BACK) {
            for (int j = 0; j < width; j++) {
                for (int i = height - 1; i >= 0; i--) {
                    raw[k++] = data[width * i + j];
                }
            }
            for (int j = 0; j < width; j += 2) {
                for (int i = uvHeight - 1; i >= 0; i--) {
                    raw[k++] = data[y_len + width * i + j];
                    raw[k++] = data[y_len + width * i + j + 1];
                }
            }
        } else {
            for (int i = 0; i < width; i++) {
                int nPos = width - 1;
                for (int j = 0; j < height; j++) {
                    raw[k] = data[nPos - i];
                    k++;
                    nPos += width;
                }
            }
            for (int i = 0; i < width; i += 2) {
                int nPos = y_len + width - 1;
                for (int j = 0; j < uvHeight; j++) {
                    raw[k] = data[nPos - i - 1];
                    raw[k + 1] = data[nPos - i];
                    k += 2;
                    nPos += width;
                }
            }
        }
    }

}
