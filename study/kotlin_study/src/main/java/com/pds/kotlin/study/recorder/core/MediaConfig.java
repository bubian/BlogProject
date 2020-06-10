package com.pds.kotlin.study.recorder.core;

import android.media.MediaRecorder;

import com.pds.kotlin.BaseApplication;

/**
 * @author: pengdaosong
 * CreateTime:  2020-06-06 11:36
 * Email：pengdaosong@medlinker.com
 * Description:
 */
public class MediaConfig {
    // 大部分音频源（包括 DEFAULT）都会对音频信号进行处理。要录制原始音频，请选择 UNPROCESSED。有些设备不支持未经处理的输入。
    // 请先调用 AudioManager.getProperty(AudioManager.PROPERTY_SUPPORT_AUDIO_SOURCE_UNPROCESSED) 以验证其是否可用。
    // 如果不可用，请尝试使用 VOICE_RECOGNITION，VOICE_RECOGNITION 不会采用 AGC 或噪音抑制。
    // 即使在不支持该属性的情况下，您也可以将 UNPROCESSED 用作音频源，但不能保证在这种情况下信号是否未经处理。
    public static final int AUDIO_SOURCE = MediaRecorder.AudioSource.MIC;
    public static final int OUTPUT_FORMAT = MediaRecorder.OutputFormat.MPEG_4;

    // 音频文件缓存目录
    public static final String AUDIO_FILE_DIR = BaseApplication.app().getCacheDir().getAbsolutePath();

}
