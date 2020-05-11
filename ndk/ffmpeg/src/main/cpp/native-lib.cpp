#include <jni.h>
#include <string>
#include <android/log.h>
extern "C" {
//编码
#include "libavcodec/avcodec.h"
//封装格式处理
#include "libavformat/avformat.h"
//像素处理
#include "libswscale/swscale.h"
}

#define LOGI(FORMAT,...) __android_log_print(ANDROID_LOG_INFO,"jason",FORMAT,##__VA_ARGS__);
#define LOGE(FORMAT,...) __android_log_print(ANDROID_LOG_ERROR,"jason",FORMAT,##__VA_ARGS__);
extern "C"
JNIEXPORT void JNICALL
Java_com_pds_ffmpeg_MainActivity_open(
        JNIEnv *env,
        jobject obj,jstring input_path,jstring output_path) {
    const char *input_cstr = env->GetStringUTFChars(input_path,false);
    const char *output_cstr = env->GetStringUTFChars(output_path,false);
    av_register_all();

    AVFormatContext *pFormatCtx = avformat_alloc_context();
    //第四个参数是 可以传一个 字典   是一个入参出参对象
    if (avformat_open_input(&pFormatCtx, input_cstr, NULL, NULL) != 0) {
        LOGE("%s","打开输入视频文件失败");
    }
    //3.获取视频信息
    if(avformat_find_stream_info(pFormatCtx,NULL) < 0){
        LOGE("%s","获取视频信息失败");
        return;
    }


    int vidio_stream_idx=-1;
    int i=0;
    for (int i = 0; i < pFormatCtx->nb_streams; ++i) {
        if (pFormatCtx->streams[i]->codec->codec_type == AVMEDIA_TYPE_VIDEO) {
            LOGE("  找到视频id %d", pFormatCtx->streams[i]->codec->codec_type);
            vidio_stream_idx=i;
            break;
        }
    }

//    获取视频编解码器
    AVCodecContext *pCodecCtx=pFormatCtx->streams[vidio_stream_idx]->codec;
    LOGE("获取视频编码器上下文 %p  ",pCodecCtx);
//    加密的用不了
    AVCodec *pCodex = avcodec_find_decoder(pCodecCtx->codec_id);
    LOGE("获取视频编码 %p",pCodex);
//版本升级了
    if (avcodec_open2(pCodecCtx, pCodex, NULL)<0) {


    }
    AVPacket *packet = (AVPacket *)av_malloc(sizeof(AVPacket));
//    av_init_packet(packet);
//    像素数据
    AVFrame *pFrame;
    pFrame = av_frame_alloc();
    AVFrame *frame;
    AVFrame *yuvFrame=av_frame_alloc();
    //只有指定了AVFrame的像素格式、画面大小才能真正分配内存
    //缓冲区分配内存
    uint8_t    *out_buffer= (uint8_t *) av_malloc(avpicture_get_size(AV_PIX_FMT_YUV420P, pCodecCtx->width, pCodecCtx->height));
    LOGE("宽  %d,  高  %d  ",pCodecCtx->width,pCodecCtx->height);
//设置yuvFrame的缓冲区，像素格式
    int re= avpicture_fill((AVPicture *) yuvFrame, out_buffer, AV_PIX_FMT_YUV420P, pCodecCtx->width, pCodecCtx->height);
    LOGE("申请内存%d   ",re);
    int length=0;
    int got_frame;
//    输出文件
    FILE *fp_yuv = fopen(output_cstr, "wb");

    int frameCount=0;

    SwsContext *swsContext = sws_getContext(pCodecCtx->width,pCodecCtx->height,pCodecCtx->pix_fmt,
                                            pCodecCtx->width,pCodecCtx->height,AV_PIX_FMT_YUV420P,SWS_BILINEAR,NULL,NULL,NULL
    );
    LOGE("上下文  swsContext  %p  ,%d",swsContext,av_read_frame(pFormatCtx, packet));

    while (av_read_frame(pFormatCtx, packet)>=0) {
//        AvFrame

        length = avcodec_decode_video2(pCodecCtx, frame, &got_frame, packet);
        LOGE(" 获得长度   %d ",length);
//非零   正在解码
        if (got_frame) {
            LOGI("解码%d帧",frameCount++);
            //转为指定的YUV420P
            sws_scale(swsContext, (const uint8_t *const *) frame->data, frame->linesize, 0, frame->height, yuvFrame->data,
                      yuvFrame->linesize);

            int y_size = pCodecCtx->width * pCodecCtx->height;
            fwrite(yuvFrame->data[0], 1, y_size, fp_yuv);
            fwrite(yuvFrame->data[1], 1, y_size/4, fp_yuv);
            fwrite(yuvFrame->data[2], 1, y_size/4, fp_yuv);
        }
        av_free_packet(packet);
    }
    fclose(fp_yuv);
    av_frame_free(&frame);
    avcodec_close(pCodecCtx);
    avformat_free_context(pFormatCtx);

    //像素数据（解码数据）
    //    AVFrame *yuv_frame = av_frame_alloc();
    //    AVFrame *rgb_frame = av_frame_alloc();
    //    avformat_open_input();
}
