#include <jni.h>
#include <string>
#include "AntiDebug.h"
#include "SafeCheck.h"
#include "tools/DeviceInfo.h"
#include "Log.h"

jobject g_callbackRef;
jmethodID  g_MethodCallback;

extern "C" JNIEXPORT void JNICALL MACRO_SECTION
Java_com_pds_antisafe_AntiSafe_setAntiDebugCallback(JNIEnv* env,jclass type, jobject jCallback) {
    jclass jclazz = env->GetObjectClass(jCallback);
    g_callbackRef = env->NewGlobalRef(jCallback);
    g_MethodCallback = env->GetMethodID(jclazz, "beInjectedDebug", "()V");
}

extern "C" JNIEXPORT void JNICALL MACRO_SECTION
Java_com_pds_antisafe_AntiSafe_safeCheck(JNIEnv* env,jclass type) {

    char *value = NULL;
    LOG_PRINT_E("ro.serialno = ");
    __system_property_get("ro.serialno",value);

    LOG_PRINT_E("ro.serialno = ", value);
//    CheckRoot(env,type);
}

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void* reserved){
    AntiDebug::antiDebug(vm);
    return JNI_VERSION_1_4;
}