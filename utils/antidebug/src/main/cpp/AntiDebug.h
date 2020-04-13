#ifndef _ANTI_DEBUG_H
#define _ANTI_DEBUG_H
#include <jni.h>

// 隐藏了关键的函数符号，避免被静态分析
#define MACRO_HIDE_SYMBOL __attribute__ ((visibility ("hidden")))

class AntiDebug{
public:
    static void antiDebug(JavaVM* jvm);
    static bool isDebugMode();
private:
    AntiDebug();
    void antiDebugInner();
    static void* antiDebugCallback(void *arg);
    char* getPackageName(JNIEnv* env);
    void getGlobalRef();
    bool readStatus();
    bool isBeDebug();
    bool IsHookByXPosed();
    bool analyzeStackTrace();
private:
    jclass mDebugGlobalRef;
    jclass mXPosedGlobalRef;
    jclass mExceptionGlobalRef;
    jclass mStackElementRef;
    static int mAppFlags;
    static AntiDebug* s_instance;
};
#endif