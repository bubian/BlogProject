package com.pds.kotlin.study.recorder.core;

import android.content.Context;
import android.media.MediaRecorder;

import com.pds.kotlin.study.ModuleKotlin;

import java.io.File;
import java.io.IOException;

/**
 * @author: pengdaosong
 * CreateTime:  2020-06-06 11:39
 * Email：pengdaosong@medlinker.com
 * Description:
 */
public class AudioRecordManager {

    private static AudioRecordManager instance;
    private MediaRecorder mRecorder = null;
    private long mStartingTimeMillis = 0;
    private long mElapsedMillis = 0;

    private RecordDBHelper mDatabase;
    private RecordNotificationManager notificationManager;

    private Context mContext;

    private String mFileName;
    private String mFilePath;

    private AudioRecordManager() {
        mContext = ModuleKotlin.instance().appContext();
        notificationManager = RecordNotificationManager.instance(mContext);
        mDatabase = new RecordDBHelper(mContext);
        File f = mDatabase.getRecordFilePath();
        mFileName = f.getName();
        mFilePath = f.getAbsolutePath();
    }

    public static AudioRecordManager instance() {
        if (instance == null) {
            synchronized (AudioRecordManager.class) {
                if (instance == null) {
                    instance = new AudioRecordManager();
                }
            }
        }
        return instance;
    }

    public void startRecording() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaConfig.AUDIO_SOURCE);
        mRecorder.setOutputFormat(MediaConfig.OUTPUT_FORMAT);
        mRecorder.setOutputFile(mFilePath);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mRecorder.setAudioChannels(1);
        setPrefHighQuality();
        try {
            mRecorder.prepare();
            mRecorder.start();
            mStartingTimeMillis = System.currentTimeMillis();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopRecording() {
        try {
            mRecorder.stop();
        } catch (RuntimeException e) {
        } finally {
            mRecorder.release();
            mRecorder = null;
        }

        mElapsedMillis = (System.currentTimeMillis() - mStartingTimeMillis);
        notificationManager.cancelIncrementTimerTask();
        try {
            mDatabase.addRecording(mFileName, mFilePath, mElapsedMillis);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setPrefHighQuality() {
        mRecorder.setAudioSamplingRate(44100);
        mRecorder.setAudioEncodingBitRate(192000);
    }
}
