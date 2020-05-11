#include <jni.h>
#include <android/log.h>
#include <assert.h>
#include <stdio.h>
#include <string.h>
#include <malloc.h>
#include <pthread.h>

#define TAG "Video_Split_merge"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, TAG, __VA_ARGS__)
#define NELEM(x) ((int) (sizeof(x) / sizeof((x)[0])))

#define NATIVE_METHOD(className, functionName, signature) \
{ #functionName, signature, (void*)(className ## _ ## functionName) }

jobject g_obj = NULL;
JavaVM* g_jvm = NULL;

//获取文件大小
long  get_file_size(const char* path) {
  FILE *fp = fopen(path, "rb"); //打开一个文件， 文件必须存在，只运行读
  fseek(fp, 0, SEEK_END);
  long ret = ftell(fp);
  fclose(fp);
  return ret;
}

JNIEXPORT void native_video_split(JNIEnv* env,jclass clazz,jstring path,jstring pattern_path,jint file_num){
  LOGI("start split video! ");
  const char *path_s = (*env)->GetStringUTFChars(env,path,NULL);
  const char *pattern_path_s = (*env)->GetStringUTFChars(env,pattern_path,NULL);
  //申请二维字符数据, 存放子文件名
  char **patches = (char **)malloc(sizeof(char *) * file_num);

  int i = 0;
  for (; i < file_num ; ++i) {
    //每个文件名申请地址
    LOGI("char = %d char * = %d", sizeof(char), sizeof(char *));
    patches[i] = (char*) malloc(sizeof(char) * 100);
    // 需要分割的文件 Vibrato.mp4
    // 每个子文件名称 Vibrato_n.mp4
    sprintf(patches[i], pattern_path_s, i);// 格式化文件名
    LOGI("patch path : %s",patches[i]);
  }

  int fileSize = get_file_size(path_s);
  FILE *fp = fopen(path_s,"rb");

  if (fileSize % file_num == 0){
    int part = fileSize / file_num;
    for (int i =0; i< file_num; i++) {
      FILE *fpw = fopen(patches[i], "wb");//文件已经存在 就删除，只运行写
      for (int j =0; j < part; j++) {
        fputc(fgetc(fp), fpw);
      }
      fclose(fpw);
    }
  } else{
    int part = fileSize / (file_num - 1);
    for (int i =0 ; i< file_num - 1; i++) {
      FILE *fpw = fopen(patches[i], "wb");//文件已经存在 就删除，只运行写
      for (int j =0; j < part; j++) {
        fputc(fgetc(fp), fpw);
      }
      fclose(fpw);
    }
    FILE *fpw = fopen(patches[file_num - 1], "wb");

    for (int i = 0; i < fileSize % (file_num - 1); i++) {
      fputc(fgetc(fp),fpw);
    }
    fclose(fpw);
  }
  fclose(fp);
  for (int i =0; i< file_num; i++) {
    free(patches[i]);
  }
  free(patches);
  (*env)->ReleaseStringUTFChars(env, path, path_s);
  (*env)->ReleaseStringUTFChars(env, pattern_path, pattern_path_s);
}

JNIEXPORT void JNICALL native_video_merge
    (JNIEnv *env, jclass clazz, jstring merge_path, jstring pattern_Path, jint file_num)
{
  LOGI("JNI native merge begin");
  const char *path_Str = (*env) -> GetStringUTFChars(env, merge_path, NULL);
  const char *pattern_Path_str = (*env) ->GetStringUTFChars(env, pattern_Path, NULL);

  //申请二维字符数据, 存放子文件名
  char **patches = (char **)malloc(sizeof(char *) * file_num);

  int i =0;
  for (; i < file_num; i++) {
    //每个文件名申请地址
//        LOGI("char = %d char * = %d", sizeof(char), sizeof(char *));
    patches[i] = (char*) malloc(sizeof(char) * 100);
    // 需要分割的文件 Vibrato.mp4
    // 每个子文件名称 Vibrato_n.mp4
    sprintf(patches[i], pattern_Path_str, i);// 格式化文件名
    LOGI("patch path : %s",patches[i]);
  }

  FILE *fpw = fopen(path_Str, "wb");

  for (int i =0; i < file_num; i++) {
    int filesize = get_file_size(patches[i]);
    FILE *fpr = fopen(patches[i], "rb");
    for (int j =0; j < filesize; j++) {
      fputc(fgetc(fpr), fpw);
    }
    fclose(fpr);
  }
  fclose(fpw);

  for (int i =0; i< file_num; i++) {
    free(patches[i]); //每一个malloc 对应一个free
  }
  free(patches);
  (*env)->ReleaseStringUTFChars(env, merge_path, path_Str);
  (*env)->ReleaseStringUTFChars(env, pattern_Path, pattern_Path_str);
}
void* thread_fun(void * arg) {

  JNIEnv  *env;
  jclass cls;
  jmethodID mid,mid1;

  if ( (*g_jvm) -> AttachCurrentThread(g_jvm, &env, NULL) != JNI_OK ) {
    LOGI("%s AttachCurrentThread error failed ",__FUNCTION__);
    return NULL;
  }

  cls = (*env) -> GetObjectClass(env ,g_obj);
  if (cls == NULL) {
    LOGI("findClass error....");
    goto  error;
  }
  LOGI("call back begin");
  mid = (*env) -> GetStaticMethodID(env, cls, "formJni", "(I)V");
  if (mid == NULL) {
    LOGI("GetStaticMethodID error....");
    goto  error;
  }

  (*env) -> CallStaticVoidMethod(env, cls, mid, (int)arg);

  mid1 = (*env) -> GetMethodID(env, cls, "form_JNI_Again", "(I)V");
  if (mid1 == NULL) {
    LOGI("GetMethodID error....");
    goto  error;
  }
  (*env) ->CallVoidMethod(env, g_obj, mid1 ,(int)arg);

  error:
  if ((*g_jvm) -> DetachCurrentThread(g_jvm) != JNI_OK) {
    LOGI("%s DetachCurrentThread error failed ",__FUNCTION__);
  }
  pthread_exit(0);
}

JNIEXPORT void JNICALL native_newThead
    (JNIEnv *env, jclass clazz)
{
  LOGI("newThread begin");
  int i;
  pthread_t pt[5];

  for (i = 0; i < 5; i++) {
    pthread_create(&pt[i], NULL, &thread_fun, (void*)i);

  }

}

JNIEXPORT void JNICALL native_setJniEnv
    (JNIEnv *env, jobject obj)
{
  LOGI("native_setJniEnv");
  //保存JVM
  (*env) -> GetJavaVM(env, &g_jvm);
  //保持actvity对象
  g_obj = (*env) -> NewGlobalRef(env, obj);

}

JNIEXPORT void native_accessMethod(JNIEnv* env,jobject jobj){
  LOGI("JNI begin native_accessMethod ");
  jclass jclz = (*env)->FindClass(env,"kt/edu/pds/kt/mrp/ndk/CVideoSplitAndMerge");
  if(jclz==JNI_FALSE){
    LOGI("find class error");
    return;
  }
  jmethodID  mid = (*env)->GetMethodID(env,jclz,"getRandomInt","(I)I");
  if(mid==JNI_FALSE){
    LOGI("find method1 error");
    return;
  }
  jint random = (*env)->CallIntMethod(env,jobj,mid,200);
//  char * ra;
//  printf(ra,"%d",random);
  LOGI("random");
}

JNIEXPORT void native_accessStaticMethod(JNIEnv* env,jobject jobj){
  LOGI("JNI begin native_accessStaticMethod ");
//  jclass  clazz = (*env)->GetObjectClass(env,jobj);
  jclass clazz = (*env)->FindClass(env,"kt/edu/pds/kt/mrp/ndk/CVideoSplitAndMerge");

  jmethodID jmid = (*env)->GetStaticMethodID(env, clazz, "getRandeomUUId", "()Ljava/lang/String;");
  jstring uuid = (*env)->CallStaticObjectMethod(env, jobj, jmid);
  char * uuid_c = (*env)->GetStringUTFChars(env, uuid, NULL);
  printf("访问完成\n");

}
JNIEXPORT jobject native_accessConstructor(JNIEnv* env,jclass clazz){
  LOGI("JNI begin native_accessConstructor ");

  jclass jclz = (*env)->FindClass(env, "java/util/Date");
  jmethodID jmid = (*env)->GetMethodID(env, jclz, "<init>", "()V");
  jobject date_obj = (*env)->NewObject(env, jclz, jmid);
  jmethodID time_mid = (*env)->GetMethodID(env, jclz, "getTime", "()J");
  jlong time = (*env)->CallLongMethod(env, date_obj, time_mid);

  printf("time: %lld \n", time);
  return date_obj;
}
JNIEXPORT jobject native_chineseChars(JNIEnv* env,jclass clazz,jstring jstr){
  LOGI("JNI begin native_chineseChars ");

  char *c_str = (*env)->GetStringChars(env,jstr,NULL);
  jclass str_cls = (*env)->FindClass(env, "java/lang/String");
  jmethodID jmid = (*env)->GetMethodID(env, str_cls, "<init>", "([BLjava/lang/String;)V");
  jbyteArray bytes = (*env)->NewByteArray(env, strlen(c_str));
  (*env)->SetByteArrayRegion(env, bytes, 0, strlen(c_str), c_str);
  jstring charsetName = (*env)->NewStringUTF(env, "GB2312");

  return(*env)->NewObject(env, str_cls, jmid, bytes, charsetName);
}


static const JNINativeMethod gMethods[] =
  {
    {"videoSplit","(Ljava/lang/String;Ljava/lang/String;I)V",(void*)native_video_split},
    {"videoMerge","(Ljava/lang/String;Ljava/lang/String;I)V",(void*)native_video_merge},
    {"newJniThread","()V",(void*)native_newThead},
    {"setJniEnv","()V",(void*)native_setJniEnv},
    {"accessMethod","()V",(void*)native_accessMethod},
    {"accessStaticMethod","()V",(void*)native_accessStaticMethod},
    {"accessConstructor","()Ljava/util/Date;",(void*)native_accessConstructor},
    {"chineseChars","(Ljava/lang/String;)Ljava/lang/String;",(void*)native_chineseChars}
  };

static int registerNatives(JNIEnv* engv){

  LOGI("registerNatives begin");
  jclass clazz;

  clazz = (*engv)->FindClass(engv,"kt/edu/pds/kt/mrp/ndk/CVideoSplitAndMerge");
  if (clazz == NULL){
    LOGI("clazz is null");
    return JNI_FALSE;
  }

  if ((*engv)->RegisterNatives(engv,clazz,gMethods,NELEM(gMethods)) < 0){
    LOGI("RegisterNatives error");
    return JNI_FALSE;
  }
  return JNI_TRUE;

}


JNIEXPORT jint JNI_OnLoad(JavaVM* vm, void* reserved)
{
  LOGI("开始动态注册JNI");

  JNIEnv* env = NULL;
  jint result = -1;

  if ((*vm)->GetEnv(vm,(void**) &env , JNI_VERSION_1_4) != JNI_OK){

    LOGI("ERROR: GetEnv failed\n");
    return -1;
  }

  assert(env != NULL);

  registerNatives(env);

  return JNI_VERSION_1_4;

}