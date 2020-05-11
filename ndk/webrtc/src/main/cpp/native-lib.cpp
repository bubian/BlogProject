#include <jni.h>
#include <string>

extern "C"
jstring
Java_md_edu_pds_kt_webrtc_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}
