#include <jni.h>
#include <unistd.h>
#include <cstdio>
#include <cstdlib>
#include "Log.h"

using namespace std;

static JavaVM* vm = NULL;
//子进程变成僵尸进程会调用这个方法
void sig_handler(int sino) {
    LOG_PRINT_E("SIGILL 收到!");
    JNIEnv* env;
    if(vm->AttachCurrentThread(&env, NULL) != JNI_OK)
        return;
}

bool root1(JNIEnv *env, jclass type){
    env->GetJavaVM(&vm);
    struct  sigaction sa;
    memset(&sa, 0, sizeof(struct sigaction));
    sa.sa_flags=SA_SIGINFO;
    sa.sa_handler = sig_handler;
    sigaction(SIGILL, &sa, NULL);
    /**
     * 第一个参数：从PATH环境变量中找到该名字的file文件
     * 第二个参数：要执行的命令
     * return：如果执行成功则函数不会返回, 执行失败则直接返回-1, 失败原因存于errno 中
     */
    LOG_PRINT_E("exec su result start");
    int result = execlp("su","su",(char*)NULL);
    if (env->ExceptionCheck()) {
        env->ExceptionDescribe();
        env->ExceptionClear();
    }
    LOG_PRINT_E("exec su result = %d",result);
    if (result == -1){
    }
}

bool root2(JNIEnv *env, jclass type){
    env->GetJavaVM(&vm);
    struct  sigaction sa;
    memset(&sa, 0, sizeof(struct sigaction));
    sa.sa_flags=SA_SIGINFO;
    sa.sa_handler = sig_handler;
    sigaction(SIGILL, &sa, NULL);
    int result = system("su");
    LOG_PRINT_E("exec su result = %d",result);
}

// 检查Android手机是否ROOT
bool CheckRoot(JNIEnv *env, jclass type){
    root2(env,type);
}



