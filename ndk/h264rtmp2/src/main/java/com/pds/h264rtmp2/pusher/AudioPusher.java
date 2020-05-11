package com.pds.h264rtmp2.pusher;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import com.pds.h264rtmp2.params.AudioParam;

/**
 * Created by david on 2017/10/11.
 */

public class AudioPusher extends  Pusher {
    private AudioParam audioParam;
    private AudioRecord audioRecord;
    private int minBufferSize;
    private PushNative pushNative;
    public AudioPusher(PushNative pushNative,AudioParam audioParam) {
        this.pushNative = pushNative;
        this.audioParam = audioParam;
        pushNative.setAudioOptions(audioParam.getSampleRateInHz(),audioParam.getChannel());
        int channelConfig = audioParam.getChannel() == 1 ?
                AudioFormat.CHANNEL_IN_MONO : AudioFormat.CHANNEL_IN_STEREO;
        //最小缓冲区大小
        minBufferSize = AudioRecord.getMinBufferSize(audioParam.getSampleRateInHz(), channelConfig, AudioFormat.ENCODING_PCM_16BIT);
        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                audioParam.getSampleRateInHz(),
                channelConfig,
                AudioFormat.ENCODING_PCM_16BIT, minBufferSize);

    }

    @Override
    public void startPush() {
        isPushing = true;
        //启动一个录音子线程
        new Thread(new AudioRecordTask()).start();
    }

    @Override
    public void stopPush() {

    }

    class AudioRecordTask implements Runnable {

        @Override
        public void run() {
            //开始录音
            audioRecord.startRecording();

            while(isPushing){
                //通过AudioRecord不断读取音频数据
                byte[] buffer = new byte[1024 * 2];
                int len = audioRecord.read(buffer, 0, buffer.length);
                if(len > 0){
                    //传给Native代码，进行音频编码
//buffer            音频  buffer  编码简

                    pushNative.frieAudio(buffer, len);


                }
            }
        }

    }
    @Override
    public void relase() {

    }
}
