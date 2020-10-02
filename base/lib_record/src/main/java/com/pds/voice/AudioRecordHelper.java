package com.pds.voice;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.MotionEvent;

import java.io.File;


/**
 * Created by wudeng on 2017/9/6.
 */

public class AudioRecordHelper implements AudioRecordManager.OnAudioStateListener {

    private static final int STATE_NORMAL = 100001;
    private static final int STATE_RECORDING = 100002;
    private static final int STATE_WANT_CANCEL = 100003;
    private static final int CANCEL_HEIGHT = 50;

    private static final int MSG_AUDIO_PREPARED = 100004;
    private static final int MSG_VOICE_CHANGE = 100005;
    private static final int MSG_DIALOG_DISMISS = 100006;
    private static final int MSG_VOICE_FINISHED = 100007;
    private static final int MSG_VOICE_ERROR = 100008;
    private static final int MSG_AUDIO_ERROR = 100009;


    // 当前状态，默认为正常
    private int mCurrentState = STATE_NORMAL;
    private boolean isReady = false;
    private volatile boolean isRecording = false;
    private AudioRecordManager mAudioRecordManager;
    private AudioManager mAudioManager;
    private String mAudioSaveDir;
    private long mRecordTime;
    private OnRecordingListener mRecordingListener;
    private String mAudioFilePath;
    private boolean hasInit = false;
    private volatile boolean isActionUp = false;


    /**
     * 设置录音回调
     *
     * @param listener 回调监听
     */
    public void setRecordingListener(OnRecordingListener listener) {
        this.mRecordingListener = listener;
    }

    /**
     * 按钮初始化
     *
     * @param audioSaveDir 录音文件保存路径
     */
    public void init(String audioSaveDir, Context context) {
        try {
            mAudioSaveDir = audioSaveDir;
            // 获取音频管理，以申请音频焦点
            mAudioManager = (AudioManager) context.getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
            // 初始化录音管理器
            mAudioRecordManager = AudioRecordManager.getInstance(mAudioSaveDir);

            mAudioRecordManager.setAudioStateListener(this);
            hasInit = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 子线程 runnable，每隔0.1秒获取音量大小，并记录录音时间
    private Runnable mGetVoiceLevelRunnable = new Runnable() {
        @Override
        public void run() {
            while (isRecording) {
                try {
                    Thread.sleep(100);
                    mRecordTime += 100;
                    if (mRecordTime >= 59 * 1000) {
                        isRecording = false;
                        mHandler.sendEmptyMessage(MSG_VOICE_FINISHED);
                    } else {
                        if (mRecordTime >= 200 && mRecordingListener != null && getRecordAudioLength() < 20) {
                            mRecordingListener.recordError("");
                            isRecording = false;
                            mHandler.sendEmptyMessage(MSG_VOICE_ERROR);
                        } else {
                            if (isActionUp) {
                                isRecording = false;
                                if (mRecordTime < 1000) {
                                    mAudioRecordManager.releaseAudio();
                                } else {
                                    mHandler.sendEmptyMessage(MSG_VOICE_FINISHED);
                                }
                            } else {
                                mHandler.sendEmptyMessage(MSG_VOICE_CHANGE);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            try {
                switch (msg.what) {
                    case MSG_AUDIO_PREPARED:
                        // 录音管理器 prepare 成功，开始录音并显示dialog
                        // 启动线程记录时间并获取音量变化
                        isRecording = true;
                        // 启动线程，每隔0.1秒获取音量大小
                        new Thread(mGetVoiceLevelRunnable).start();
                        break;
                    case MSG_VOICE_CHANGE:
                        break;
                    case MSG_DIALOG_DISMISS:
                        mHandler.removeCallbacksAndMessages(null);
                        break;
                    case MSG_VOICE_FINISHED:
                        mAudioRecordManager.releaseAudio();
                        // 将录音文件路径和录音时长回调
                        if (mRecordingListener != null) {
                            mRecordingListener.recordFinish(mAudioFilePath, mRecordTime);
                        }
                        reset();
                    case MSG_VOICE_ERROR:
                        mAudioRecordManager.releaseAudio();
                        reset();
                        break;
                    case MSG_AUDIO_ERROR:
                        if (mRecordingListener != null) {
                            String message = (String) msg.obj;
                            if (!TextUtils.isEmpty(message)) {
                                mRecordingListener.recordError(message);
                            }
                        }
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    // 录音准备出错时回调
    @Override
    public void prepareError(String message) {
        try {
            Message handlerMsg = mHandler.obtainMessage(
                    MSG_AUDIO_ERROR);
            handlerMsg.obj = message;
            mHandler.sendMessage(handlerMsg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 录音准备完成后回调
    @Override
    public void prepareFinish(String audioFilePath) {
        try {
            mAudioFilePath = audioFilePath;
            mHandler.sendEmptyMessage(MSG_AUDIO_PREPARED);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendDiaogMiss() {
        mHandler.sendEmptyMessage(MSG_DIALOG_DISMISS);
    }

    public boolean onTouchEvent(MotionEvent event) {
        try {
            if (!hasInit) {
                return true;
            }
            int x = (int) event.getX();
            int y = (int) event.getY();
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (isRecording) {

                    }
                    break;
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    // 未触发 longClick,直接重置
                    if (!isReady) {
                        isActionUp = true;
                        reset();
//                        return super.onTouchEvent(event);
                    }
                    // 触发了longClick，开始初始化录音，但是为初始化完成,或者录音时间太短
                    if (!isRecording || mRecordTime <= 1000) {
                        sendDiaogMiss();
                        mAudioRecordManager.cancelAudio();
                        isReady = false;
                        isRecording = false;
                        mRecordTime = 0;
                        // 释放焦点
                        if (mAudioManager != null) {
                            mAudioManager.abandonAudioFocus(null);
                        }
                        isActionUp = true;
                        break;
                    } else if (mCurrentState == STATE_RECORDING) {
                        mAudioRecordManager.releaseAudio();
                        // 将录音文件路径和录音时长回调
                        if (mRecordingListener != null) {
                            mRecordingListener.recordFinish(mAudioFilePath, mRecordTime);
                        }
                    } else if (mCurrentState == STATE_WANT_CANCEL) {
                        mAudioRecordManager.cancelAudio();
                    }
                    isActionUp = true;
                    reset();
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
//        return super.onTouchEvent(event);
        return false;
    }

    public long getRecordAudioLength() {
        if (TextUtils.isEmpty(mAudioFilePath)) {
            return 0;
        }
        try {
            File file = new File(mAudioFilePath);
            long length = file.length();
            return length;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }


    /**
     * 释放资源，释放音频焦点
     */
    private void reset() {
        try {
            isReady = false;
            isRecording = false;
            mRecordTime = 0;
            // 释放焦点
            if (mAudioManager != null) {
                mAudioManager.abandonAudioFocus(null);
            }
            mHandler.removeCallbacksAndMessages(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取当前录音文件保存路径
     *
     * @return 当前录音文件保存路径
     */
    public String getAudioSaveDir() {
        return mAudioSaveDir;
    }


    public interface OnRecordingListener {

        void recordStart();

        /**
         * 录音正常结束
         *
         * @param audioFilePath 录音文件绝对路径
         * @param recordTime    录音时长,ms
         */
        void recordFinish(String audioFilePath, long recordTime);

        /**
         * 录音发生错误
         *
         * @param message 错误提示
         */
        void recordError(String message);
    }

    public void destoryRelease() {
        try {
            reset();
            if (mAudioRecordManager != null) {
                mAudioRecordManager.setAudioStateListener(null);
                mAudioRecordManager = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
