#include <jni.h>
#include <string>
#include <android/log.h>
#include <x264.h>
#include <malloc.h>

#define LOGI(FORMAT,...) __android_log_print(ANDROID_LOG_INFO,"jason",FORMAT,##__VA_ARGS__)
#define LOGE(FORMAT,...) __android_log_print(ANDROID_LOG_ERROR,"jason",FORMAT,##__VA_ARGS__)

extern "C"
{
#include "x264.h"
#include "faac.h"
#include "librtmp/rtmp.h"
}

#include <queue>
x264_picture_t *pic;
x264_picture_t *pic_out;
int y_len ;
int u_len  ;
int v_len ;
x264_t *video_encoder;
char *path;
//待推流的队列
std::queue<RTMPPacket *> queue;
pthread_mutex_t mutex;
pthread_cond_t cond;
int publishing=0;
unsigned long inputSamples;
unsigned long maxOutputBytes;
faacEncHandle audioHandle;
/**
 * 生产者  1
 * 消费者 2
 * @param pPacket
 */
void put(RTMPPacket *packet) {
    pthread_mutex_lock(&mutex);
    if (publishing) {
        queue.push(packet);
    }
    pthread_cond_signal(&cond);

    pthread_mutex_unlock(&mutex);
}


void add_aac_sequence_header() {
    if (!audioHandle) {
        return;
    }
    unsigned char *buf;
    unsigned long len;/*buf长度,一般是2*/
    faacEncGetDecoderSpecificInfo(audioHandle, &buf, &len);
    RTMPPacket *packet = (RTMPPacket *) malloc(sizeof(RTMPPacket));
    RTMPPacket_Alloc(packet, len + 2);
    RTMPPacket_Reset(packet);
    unsigned char *body = (unsigned char *) packet->m_body;
    /*AF 00 + AAC RAW data*/
    body[0] = 0xAF;
    body[1] = 0x00;
    memcpy(&body[2], buf, len); /*spec_buf是AAC sequence header数据*/
    packet->m_packetType = RTMP_PACKET_TYPE_AUDIO;
    packet->m_nBodySize = len + 2;
    packet->m_nChannel = 0x04;
    packet->m_hasAbsTimestamp = 0;
    packet->m_nTimeStamp = 0;
    packet->m_hasAbsTimestamp = 0;
    packet->m_headerType = RTMP_PACKET_SIZE_LARGE;
    put(packet);
    free(buf);
}
long start_time;

void add_aac_body(unsigned char *bitbuf, int length) {
//    封装声音的rtmp包
    int body_size = length + 2;
    RTMPPacket *packet = (RTMPPacket *) malloc(sizeof(RTMPPacket));
    RTMPPacket_Alloc(packet, length+ 2);
    char *body = packet->m_body;
    body[0] = 0xAF;
    body[1] = 0x01;
    memcpy(&body[2], bitbuf, length);
    packet->m_packetType = RTMP_PACKET_TYPE_AUDIO;

    packet->m_nBodySize = body_size;
    packet->m_hasAbsTimestamp = 0;
    packet->m_nChannel = 0x04;
    packet->m_headerType = RTMP_PACKET_SIZE_MEDIUM;
//	packet->m_nTimeStamp = -1;
    packet->m_nTimeStamp = RTMP_GetTime() - start_time;
    put(packet);
}


void add_264_body(unsigned char *buf, int len) {
    /*去掉帧界定符 *00 00 00 01*/
    if (buf[2] == 0x00) { //
        buf += 4;
        len -= 4;
    } else if (buf[2] == 0x01) { //00 00 01
        buf += 3;
        len -= 3;
    }
    int body_size = len + 9;
    RTMPPacket *packet = (RTMPPacket *) malloc(sizeof(RTMPPacket));
    RTMPPacket_Alloc(packet, len + 9);
    char *body = packet->m_body;
    int type = buf[0] & 0x1f;
    /*key frame*/
    body[0] = 0x27;
    if (type == NAL_SLICE_IDR) {
        body[0] = 0x17;
    }
    body[1] = 0x01; /*nal unit*/
    body[2] = 0x00;
    body[3] = 0x00;
    body[4] = 0x00;

    body[5] = (len >> 24) & 0xff;
    body[6] = (len >> 16) & 0xff;
    body[7] = (len >> 8) & 0xff;
    body[8] = (len) & 0xff;

    /*copy data*/
    memcpy(&body[9], buf, len);

    packet->m_hasAbsTimestamp = 0;
    packet->m_nBodySize = body_size;
    packet->m_packetType = RTMP_PACKET_TYPE_VIDEO;
    packet->m_nChannel = 0x04;
    packet->m_headerType = RTMP_PACKET_SIZE_LARGE;
//	packet->m_nTimeStamp = -1;
    packet->m_nTimeStamp = RTMP_GetTime() - start_time;
    put(packet);
}


RTMPPacket * get(){
    RTMPPacket *packet;
    pthread_mutex_lock(&mutex);
    if (queue.empty()) {
        pthread_cond_wait(&cond, &mutex);
    }
    packet = queue.front();
    queue.pop();
    pthread_mutex_unlock(&mutex);
    return packet;
}
//消费者
void *push_thread(void *arg){
    publishing = 1;
    RTMP *rtmp=RTMP_Alloc();
//    初始化
    RTMP_Init(rtmp);
    rtmp->Link.timeout = 5;
    RTMP_SetupURL(rtmp, path);
    RTMP_EnableWrite(rtmp);
    if (!RTMP_Connect(rtmp, NULL)) {
        LOGE("链接失败");
        goto end;
    }
//    链接流
    RTMP_ConnectStream(rtmp, 0);
    add_aac_sequence_header();

//    无限循环  知道停止推流
    while (publishing) {
        RTMPPacket *packet = get();
//        推流
        packet->m_nInfoField2 = rtmp->m_stream_id;
//         1  代表rtmp上传队列
        int i = RTMP_SendPacket(rtmp, packet, 1);
        LOGE("发送信息");
        RTMPPacket_Free(packet);
        free(packet);
    }
    publishing = 0;
    free(path);
//    推流结束
end:
//    关闭
    RTMP_Close(rtmp);
    RTMP_Free(rtmp);
    pthread_exit(NULL);
}
/**
 * 传送头信息
 * @param pps
 * @param sps
 * @param len
 * @param sps_len
 */

void add_264_sequence_header(unsigned char *pps, unsigned char *sps,
                             int pps_len, int sps_len) {
    int body_size = 13 + sps_len + 3 + pps_len;
    RTMPPacket *packet = (RTMPPacket *) malloc(sizeof(RTMPPacket));
    RTMPPacket_Alloc(packet, body_size);
    RTMPPacket_Reset(packet);
    char *body = packet->m_body;
    int i = 0;
    body[i++] = 0x17;
    body[i++] = 0x00;
    //composition time 0x000000
    body[i++] = 0x00;
    body[i++] = 0x00;
    body[i++] = 0x00;

    /*AVCDecoderConfigurationRecord*/
    body[i++] = 0x01;
    body[i++] = sps[1];
    body[i++] = sps[2];
    body[i++] = sps[3];
    body[i++] = 0xFF;

    /*sps*/
    body[i++] = 0xE1;
    body[i++] = (sps_len >> 8) & 0xff;
    body[i++] = sps_len & 0xff;
    memcpy(&body[i], sps, sps_len);
    i += sps_len;

    /*pps*/
    body[i++] = 0x01;
    body[i++] = (pps_len >> 8) & 0xff;
    body[i++] = (pps_len) & 0xff;
    memcpy(&body[i], pps, pps_len);
    i += pps_len;

    packet->m_packetType = RTMP_PACKET_TYPE_VIDEO;
    packet->m_nBodySize = body_size;
    packet->m_nChannel = 0x04;
    packet->m_nTimeStamp = 0;
    packet->m_hasAbsTimestamp = 0;
    packet->m_headerType = RTMP_PACKET_SIZE_MEDIUM;
    put(packet);
}


extern "C"
{
//音频初始化
JNIEXPORT void JNICALL
Java_com_dongnao_h264rtmp_pusher_PushNative_setAudioOptions(JNIEnv *env, jobject instance,
                                                            jint sampleRate, jint channel) {
    unsigned long nChannels = channel;
    int nSampleRate = sampleRate;
     audioHandle=faacEncOpen(nSampleRate, channel, &inputSamples, &maxOutputBytes);
    if (!audioHandle) {
//        失败
    }
    faacEncConfigurationPtr configurationPtr =faacEncGetCurrentConfiguration(audioHandle);
    configurationPtr->mpegVersion = MPEG4;
    configurationPtr->allowMidside = 1;
    configurationPtr->aacObjectType = LOW;
    configurationPtr->outputFormat = 0;
//    消除爆破声
    configurationPtr->useTns = 1;
    configurationPtr->useLfe = 0;
    configurationPtr->shortctl = SHORTCTL_NORMAL;
    configurationPtr->inputFormat = FAAC_INPUT_16BIT;
    configurationPtr->quantqual = 100;
    configurationPtr->bandWidth = 0;

//使上面配置生效
    if (!faacEncSetConfiguration(audioHandle, configurationPtr)) {
        LOGE("失败");

    }
    LOGE("声音设置成功");
    // TODO
}

JNIEXPORT void JNICALL
Java_com_dongnao_h264rtmp_pusher_PushNative_frieAudio(JNIEnv *env, jobject instance,
                                                      jbyteArray data_,jint len) {
    jbyte *data = env->GetByteArrayElements(data_, NULL);
//    pcm的数据转码 mpeg4
    unsigned char *bitbuf = (unsigned char *) malloc(sizeof(unsigned char) * maxOutputBytes);
    int byteLength=faacEncEncode(audioHandle, (int32_t *) data, inputSamples, bitbuf, maxOutputBytes);
    LOGE("获取声音%d",byteLength);
    if (byteLength > 0) {
        add_aac_body(bitbuf, byteLength);
    }

    env->ReleaseByteArrayElements(data_, data, 0);
}

JNIEXPORT void JNICALL
Java_com_dongnao_h264rtmp_pusher_PushNative_startPush(JNIEnv *env, jobject instance, jstring url_) {
    const char *url = env->GetStringUTFChars(url_, 0);
    path = (char *) malloc(strlen(url) + 1);
    memset(path,0,strlen(url) + 1);
    memcpy(path,url,strlen(url));
    pthread_cond_init(&cond, NULL);
    pthread_mutex_init(&mutex, NULL);
    pthread_t push_thread_id;
    start_time = RTMP_GetTime();
    pthread_create(&push_thread_id, NULL, push_thread, NULL);
    env->ReleaseStringUTFChars(url_, url);
}



JNIEXPORT void JNICALL
Java_com_dongnao_h264rtmp_pusher_PushNative_setVideoOptions(JNIEnv *env, jobject instance,
                                                            jint width, jint height, jint bitrate,
                                                            jint fps) {
//    x264  的画面参数
    x264_param_t param;
    LOGE("设置参数");
    y_len = width * height;
     u_len = y_len / 4;
    v_len = u_len;
//zerolatency无缓存
    x264_param_default_preset(&param, "ultrafast", "zerolatency");
    param.i_level_idc = 51;
//    推流的格式
    param.i_csp = X264_CSP_I420;
    param.i_width = width;
    param.i_height = height;
    param.i_threads = 1;
    /**
     * timebase    分子 25-32 分母 1s
     * 1s播放多少帧画面
     *
     */
    param.i_fps_num = fps;
    param.i_fps_den = 1;
    param.i_timebase_num = param.i_fps_num;
    param.i_timebase_den = param.i_fps_den;
//   码率相关设置 关键帧间隔时间的帧率
    param.i_keyint_max = fps * 2;
    /**
     * CQP(恒定质量)，CRF(恒定码率)，ABR(平均码率)
     */
    param.rc.i_rc_method = X264_RC_ABR;
//    设置码率
    param.rc.i_bitrate = bitrate / 1000;
    param.rc.i_vbv_max_bitrate = bitrate / 1000 * 1.2;
    param.rc.i_vbv_buffer_size = bitrate / 1000;


//    设置输入  0 pts 来做音视频同步
    param.b_vfr_input = 0;
//    SPS  PPS  蛮重要
    param.b_repeat_headers = 1;
//    设置画面质量
    x264_param_apply_profile(&param, "baseline");

//设置完成  打开解码器
    video_encoder=x264_encoder_open(&param);
    if (!video_encoder) {
        LOGE("打开失败");
        return;
    }

//元数据
    pic = (x264_picture_t *) malloc(sizeof(x264_picture_t));
    pic_out = (x264_picture_t *) malloc(sizeof(x264_picture_t));
    x264_picture_alloc(pic, X264_CSP_I420, width, height);


}

JNIEXPORT void JNICALL
Java_com_dongnao_h264rtmp_pusher_PushNative_frieVedeo(JNIEnv *env, jobject instance,
                                                      jbyteArray data_) {
//    含有nv21的数据  容器
    jbyte *data = env->GetByteArrayElements(data_, NULL);
/**
 * 把nv21的u  转到pic->img.plane[1] width*height y   u  width*height/4
 *
 * v  width*height/4
 */

    //把nv21的v  转到pic->img.plane[2]
    jbyte *u = (jbyte *) pic->img.plane[1];
    jbyte *v = (jbyte *) pic->img.plane[2];
    memcpy(pic->img.plane[0], data, y_len);
    for (int i = 0; i < u_len; ++i) {
        *(u + i)=*(data+y_len+i*2+1);
        *(v + i)=*(data+y_len+i*2);
    }
//    是一个NALU数组
    x264_nal_t *nal = NULL;
    int n_nal = -1;
//    下一步开始做推流 pic 牛 yuv420 服务器 h264
//    yuv   ----》大 编码
    if (x264_encoder_encode(video_encoder, &nal, &n_nal, pic, pic_out) < 0) {
        LOGE("编码失败");
        return;
    }
    unsigned char sps[100];
    unsigned char pps[100];
    int sps_len;
    int pps_len;
    for (int i = 0; i < n_nal; ++i) {
//  关键帧信息   I  帧  1f x264
        if (nal[i].i_type == NAL_SPS) {
            sps_len=nal[i].i_payload-4;
            memcpy(sps, nal[i].p_payload + 4, sps_len);
        } else if (nal[i].i_type == NAL_PPS) {
            pps_len=nal[i].i_payload-4;
            memcpy(pps, nal[i].p_payload + 4, pps_len);

            add_264_sequence_header(pps, sps, pps_len, sps_len);
//            发送整个头信息
        } else{
//            关键帧与非关键帧
            add_264_body(nal[i].p_payload, nal[i].i_payload);
        }
    }
    env->ReleaseByteArrayElements(data_, data, 0);
}

}



