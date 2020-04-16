#include <jni.h>
#include <unistd.h>
#include <cstdio>
#include "../Log.h"

static void getExceptionSummary(JNIEnv* env, jthrowable exception, char* buf, size_t bufLen)
{
    int success = 0;

    /* get the name of the exception's class */
    jclass exceptionClazz = env->GetObjectClass(exception); // can't fail
    jclass classClazz = env->GetObjectClass(exceptionClazz); // java.lang.Class, can't fail
    jmethodID classGetNameMethod = env->GetMethodID(classClazz, "getName", "()Ljava/lang/String;");
    jstring classNameStr = static_cast<jstring>(env->CallObjectMethod(exceptionClazz, classGetNameMethod));
    if (classNameStr != NULL) {
        /* get printable string */
        const char* classNameChars = env->GetStringUTFChars( classNameStr, NULL);
        if (classNameChars != NULL) {
            /* if the exception has a message string, get that */
            jmethodID throwableGetMessageMethod = env->GetMethodID(
                    exceptionClazz, "getMessage", "()Ljava/lang/String;");
            jstring messageStr = static_cast<jstring>(env->CallObjectMethod(exception, throwableGetMessageMethod));

            if (messageStr != NULL) {
                const char* messageChars = env->GetStringUTFChars( messageStr, NULL);
                if (messageChars != NULL) {
                    snprintf(buf, bufLen, "%s: %s", classNameChars, messageChars);
                    env->ReleaseStringUTFChars( messageStr, messageChars);
                } else {
                    env->ExceptionClear(); // clear OOM
                    snprintf(buf, bufLen, "%s: <error getting message>", classNameChars);
                }
                env->DeleteLocalRef( messageStr);
            } else {
                strncpy(buf, classNameChars, bufLen);
                buf[bufLen - 1] = '\0';
            }

            env->ReleaseStringUTFChars( classNameStr, classNameChars);
            success = 1;
        }
        env->DeleteLocalRef( classNameStr);
    }
    env->DeleteLocalRef( classClazz);
    env->DeleteLocalRef( exceptionClazz);

    if (! success) {
        env->ExceptionClear();
        snprintf(buf, bufLen, "%s", "<error getting class name>");
    }
}


int jniThrowException(JNIEnv* env)
{
    jclass exceptionClass;
    int result = 0;

    if (env->ExceptionCheck()) {
        /* TODO: consider creating the new exception with this as "cause" */
        char buf[256];

        jthrowable exception = env->ExceptionOccurred();
        env->ExceptionClear();
        if (exception != NULL) {
            getExceptionSummary(env,exception, buf, sizeof(buf));
            __android_log_print(ANDROID_LOG_INFO, "", "Discarding pending exception (%s) to throw\n",buf);
            env->DeleteLocalRef(exception);
            result = -1;
        }
    }
    return result;
}

