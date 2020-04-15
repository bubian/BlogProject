#include <jni.h>
#include <string>
#include "AntiDebug.h"

jobject g_callbackRef;
jmethodID  g_MethodCallback;

extern "C" JNIEXPORT void JNICALL MACRO_SECTION
Java_com_pds_antidebug_AntiDebug_setAntiDebugCallback(
        JNIEnv* env,
        jclass type, jobject jCallback) {
    jclass jclazz = env->GetObjectClass(jCallback);
    g_callbackRef = env->NewGlobalRef(jCallback);
    g_MethodCallback = env->GetMethodID(jclazz, "beInjectedDebug", "()V");
}

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void* reserved){
    AntiDebug::antiDebug(vm);
    return JNI_VERSION_1_4;
}

