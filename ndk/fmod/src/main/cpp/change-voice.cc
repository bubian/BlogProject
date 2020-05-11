#include "com_pds_fmod_MainActivity.h"
#include <stdlib.h>
#include "android/log.h"
#include "common.h"
#include "fmod.hpp"
#include <unistd.h>

#define TAG "change-voice"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, TAG, __VA_ARGS__)

using  namespace FMOD;
JNIEXPORT void JNICALL Java_com_pds_fmod_MainActivity_fix(JNIEnv *env, jobject job, jstring path, jint mode){

  LOGI("start fmod");
  const char *cPath = env->GetStringUTFChars(path,NULL);
  System  *system;
  Sound   *sound1,*sound2,*sound3;
  Channel *channel = 0;
  FMOD_RESULT   result;
  bool playing = true;
  uint32_t      version;
  void          *exthadriverdata;
  DSP           *dsp;

  System_Create(&system);

  Common_Init(&exthadriverdata);

  try {
    system->getVersion(&version);
    system->init(32,FMOD_INIT_NORMAL,exthadriverdata);
    system->createSound(cPath,FMOD_DEFAULT,0,&sound1);
    sound1->setMode(FMOD_LOOP_OFF);

    system->createDSPByType(FMOD_DSP_TYPE_PITCHSHIFT, &dsp);
    dsp->setParameterFloat(FMOD_DSP_PITCHSHIFT_PITCH,2.5);
    system->playSound(sound1,0, false,&channel);
    channel->addDSP(0, dsp);
    //检查是否结束播放
  }catch(...) {
    LOGI("%s","发送异常");
    goto end;
  }

  system->update();
  while(playing){
    channel->isPlaying(&playing);
    usleep(1000);
  }


  end:
  LOGI("%s","end");
  env->ReleaseStringUTFChars(path,cPath);
  sound1->release();
  system->close();
  system->release();

}

