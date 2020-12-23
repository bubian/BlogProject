#include <jni.h>
#include <string>
#include <asm/fcntl.h>
#include <unistd.h>
#include <sys/types.h>
#include <sstream>
#include <fcntl.h>
#include <android/log.h>
#include <elf.h>
#include <sys/mman.h>

#define PAGE_SIZE 4096

jstring getString(JNIEnv*) __attribute__((section (".mytext")));
//声明为构造函数，在init_array节执行
void decryte_section() __attribute__((constructor));
unsigned long getLibAddr();

void decryte_section() {
  unsigned long base;
  Elf32_Ehdr *ehdr;
  Elf32_Shdr *shdr;
  unsigned long my_text_addr;
  unsigned int nblock;
  unsigned int nsize;
  unsigned int i;


  base = getLibAddr();
  ehdr = (Elf32_Ehdr *)base;
  //自定义节的位置
  my_text_addr = base + ehdr->e_shoff;
  nblock = ehdr->e_entry >> 16;
  nsize = (nblock / PAGE_SIZE) + (nblock%PAGE_SIZE == 0 ? 0 : 1);
  __android_log_print(ANDROID_LOG_INFO, "JNITag", "size of encrypted section is %d", nblock);
  if (mprotect((void *)(my_text_addr / PAGE_SIZE * PAGE_SIZE), nsize*PAGE_SIZE, PROT_READ | PROT_EXEC | PROT_WRITE) == -1){
    __android_log_print(ANDROID_LOG_ERROR, "JNITag", "Memory privilege change failed before encrypt");
  }
  //解密
  for(i=0; i<nblock; i++){
    char * addr = (char *)(my_text_addr + i);
    * addr = ~(*addr);
  }
  //改回该节的权限
  if (mprotect((void *)(my_text_addr / PAGE_SIZE * PAGE_SIZE), nsize*PAGE_SIZE, PROT_READ | PROT_EXEC) == -1){
    __android_log_print(ANDROID_LOG_ERROR, "JNITag", "Memory privilege change failed after encrypt");
  }
  __android_log_print(ANDROID_LOG_INFO, "JNITag", "Decrypt completed!");
}

/** 获取当前进程内部指定共享库文件的内存映射地址*/
unsigned long getLibAddr(){

  int pid;
  char buffer[4096];
  FILE *fd;
  char *tmp;

  unsigned long ret = 0;
  char so_name[] = "libnative-lib.so";

  pid = getpid();
  sprintf(buffer, "/proc/%d/maps", pid);
  if((fd = fopen(buffer, "r")) == NULL){
    __android_log_print(ANDROID_LOG_DEBUG, "JNITag", "open /proc/%d/maps failed!", pid);
    goto _error;
  }
  __android_log_print(ANDROID_LOG_INFO, "JNITag", "cat /proc/%d/maps succussful!", pid);
  while(fgets(buffer, sizeof(buffer), fd)){
    if(strstr(buffer, so_name)){
      tmp = strtok(buffer, "-");
      ret = strtoul(tmp, 0, 16);
      __android_log_print(ANDROID_LOG_INFO, "JNITag", "Find file %s", so_name);
      __android_log_print(ANDROID_LOG_INFO, "JNITag", "The memory address of %s is 0x%X", so_name, ret);
      break;
    }
  }

  _error:
  fclose(fd);
  return ret;
}

//后面是java层调用的Native函数
extern "C"
JNIEXPORT jstring JNICALL
Java_com_testjni_MainActivity_isTraceMe(JNIEnv *env, jobject instance){
  return getString(env);
}

//需要加密的核心代码主要存放在这个自定义节中
jstring getString(JNIEnv* env){
  return env->NewStringUTF("Text from JNI");
}