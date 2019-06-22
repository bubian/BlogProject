//
// Created by pengdaosong on 2019/6/19.
//
#include <jni.h>
#include <string.h>
#include <stdio.h>
#include <android/log.h>
#include <assert.h>

#define TAG "socket:"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, TAG, __VA_ARGS__)

//extern "C" JNIEXPORT int JNICALL
//Java_blog_pds_com_socket_core_client_CSocket_connect( JNIEnv *env, jobject instance,jstring ip, int port,int time){
//
//    LOGI("start connect!!!!!!");
//    return 1;
//
//}

extern "C" JNIEXPORT int JNICALL
connect( JNIEnv *env, jobject instance,jstring ip, int port,int time){
    LOGI("start connect!!!!!!");
    return 1;
}


static const JNINativeMethod gMethods[] =
        {
                {"connect","(Ljava/lang/String;I;I;I)I",(void*)connect},
        };

#define NELEM(x) ((int) (sizeof(x) / sizeof((x)[0])))
static int registerNatives(JNIEnv **engv){

    LOGI("registerNatives begin");
    jclass clazz;

    clazz = (*engv)->FindClass("blog.pds.com.socket.core.client/CSocket");
    if (clazz == NULL){
        LOGI("clazz is null");
        return JNI_FALSE;
    }

    if ((*engv)->RegisterNatives(clazz,gMethods,NELEM(gMethods)) < 0){
        LOGI("RegisterNatives error");
        return JNI_FALSE;
    }
    return JNI_TRUE;
}


JNIEXPORT jint JNI_OnLoad(JavaVM** vm, void* reserved)
{
    LOGI("JNI_OnLoad");
    JNIEnv **env = NULL;

    if ((*vm)->GetEnv((void**) env , JNI_VERSION_1_4) != JNI_OK){
        LOGI("ERROR: GetEnv failed\n");
        return -1;
    }

    assert(env != NULL);
    LOGI("开始动态注册JNI");
    registerNatives(env);
    return JNI_VERSION_1_4;

}