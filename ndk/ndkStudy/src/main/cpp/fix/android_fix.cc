#include <jni.h>
#include "dalvik.h"
#include "art_7_0.h"
#include <string>
typedef Object *(*FindObject)(void *thread, jobject jobject1);
typedef  void* (*FindThread)();
FindObject  findObject;
FindThread  findThread;


JNIEXPORT void JNICALL Java_kt_edu_pds_kt_mrp_ndk_DexManager_androidFix
(JNIEnv * env, jobject obj,jint sdk_version,jobject error_method,jobject right_method){

  Method *error = (Method *) env->FromReflectedMethod(error_method);
  Method *right =(Method *) env->FromReflectedMethod(right_method);

  //下一步  把right 对应Object   第一个成员变量ClassObject   status

  //    ClassObject
  void *dvm_hand = dlopen("libdvm.so",RTLD_NOW);

//    sdk  10    以前是这样   10会发生变化

  findObject = (FindObject)dlsym(dvm_hand, sdk_version > 10 ?
                                "_Z20dvmDecodeIndirectRefP6ThreadP8_jobject" :
                                "dvmDecodeIndirectRef");

  findThread = (FindThread)dlsym(dvm_hand,sdk_version > 10 ? "_Z13dvmThreadSelfv" : "dvmThreadSelf");

  // method   所声明的Class

  jclass  methodClazz = env->FindClass("java/lang/reflect/Method");
  jmethodID rightMethodId = env->GetMethodID(methodClazz,"getDeclaringClass","()Ljava/lang/Class;");

  jobject ndkObj = env->CallObjectMethod(right_method,rightMethodId);

  ClassObject *firstFiled = (ClassObject *) findObject(findThread(), ndkObj);

  firstFiled->status=CLASS_INITIALIZED;

  error->accessFlags |= ACC_PUBLIC;
  error->methodIndex=right->methodIndex;
  error->jniArgInfo=right->jniArgInfo;
  error->registersSize=right->registersSize;
  error->outsSize=right->outsSize;
//    方法参数 原型
  error->prototype=right->prototype;
//
  error->insns=right->insns;
  error->nativeFunc=right->nativeFunc;
}


extern "C"
JNIEXPORT void JNICALL
Java_kt_edu_pds_kt_mrp_ndk_DexManager_androidArt(JNIEnv *env,
                                                         jobject instance,
                                                         jint sdk,
                                                         jobject wrongMethod,
                                                         jobject rightMethod) {

//    art虚拟机替换  art  ArtMethod  ---》Java方法
  art::mirror::ArtMethod *wrong = (art::mirror::ArtMethod *) env->FromReflectedMethod(wrongMethod);
  art::mirror::ArtMethod *right = (art::mirror::ArtMethod *) env->FromReflectedMethod(rightMethod);
  jclass jcls = env->FindClass("com/dongnao/fixdavlik/NativeStructsModel");

  size_t firMid = (size_t) env->GetStaticMethodID(jcls, "f1", "()V");
  size_t secMid = (size_t) env->GetStaticMethodID(jcls, "f2", "()V");

  int size=secMid-firMid;
  memcpy(wrong,right,size);
//
//    wrong->declaring_class_=right->declaring_class_;
//
//    wrong->dex_code_item_offset_=right->dex_code_item_offset_;
//    wrong->method_index_=right->method_index_;
//    wrong->dex_method_index_=right->dex_method_index_;
//
//
////入口
//    wrong->ptr_sized_fields_.entry_point_from_jni_=right->ptr_sized_fields_.entry_point_from_jni_;
//    //    机器码模式
//    wrong->ptr_sized_fields_.entry_point_from_quick_compiled_code_=right->ptr_sized_fields_.entry_point_from_quick_compiled_code_;
//
////  不一样
//    wrong->ptr_sized_fields_.entry_point_from_jni_=right->ptr_sized_fields_.entry_point_from_jni_;
//    wrong->ptr_sized_fields_.dex_cache_resolved_methods_=right->ptr_sized_fields_.dex_cache_resolved_methods_;
//    wrong->ptr_sized_fields_.dex_cache_resolved_types_=right->ptr_sized_fields_.dex_cache_resolved_types_;
//    wrong->hotness_count_=right->hotness_count_;

}