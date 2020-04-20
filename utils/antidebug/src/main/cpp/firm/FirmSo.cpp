#include <jni.h>
#include <stdio.h>
#include <android/log.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <sys/types.h>
#include <elf.h>
#include <sys/mman.h>

jstring getString(JNIEnv*) __attribute__((section (".mytext")));

jstring getString(JNIEnv* env){
    return (*env)->NewStringUTF(env, "Native method return!");
};

void init_getString() __attribute__((constructor));
unsigned long getLibAddr();

void init_getString(){
    char name[15];
    unsigned int nblock;
    unsigned int nsize;
    unsigned long base;
    unsigned long text_addr;
    unsigned int i;
    Elf32_Ehdr *ehdr;
    Elf32_Shdr *shdr;

    // 获取so的起始地址
    base = getLibAddr();

    // 获取指定section的偏移值和size
    ehdr = (Elf32_Ehdr *)base;
    text_addr = ehdr->e_shoff + base;

    nblock = ehdr->e_entry >> 16;
    nsize = ehdr->e_entry & 0xffff;

    __android_log_print(ANDROID_LOG_INFO, "JNITag", "nblock =  0x%x,nsize:%d", nblock,nsize);
    __android_log_print(ANDROID_LOG_INFO, "JNITag", "base =  0x%x", text_addr);
    printf("nblock = %d\n", nblock);

    // 修改内存的操作权限,第一个参数：需要修改内存的起始地址
    //
    //必须需要页面对齐，也就是必须是页面PAGE_SIZE(0x1000=4096)的整数倍
    //
    //第二个参数：需要修改的大小
    //
    //占用的页数*PAGE_SIZE
    //
    //第三个参数：权限值
    //
    //最后读取内存中的section内容，然后进行解密，在将内存权限修改回去。
    if(mprotect((void *) (text_addr / PAGE_SIZE * PAGE_SIZE), 4096 * nsize, PROT_READ | PROT_EXEC | PROT_WRITE) != 0){
        puts("mem privilege change failed");
        __android_log_print(ANDROID_LOG_INFO, "JNITag", "mem privilege change failed");
    }

    // 解密
    for(i=0;i< nblock; i++){
        char *addr = (char*)(text_addr + i);
        *addr = ~(*addr);
    }

    if(mprotect((void *) (text_addr / PAGE_SIZE * PAGE_SIZE), 4096 * nsize, PROT_READ | PROT_EXEC) != 0){
        puts("mem privilege change failed");
    }
    puts("Decrypt success");
}

/**
 * 获取so加载到内存中的起始地址
 * 这里的代码其实就是读取设备的proc/<uid>/maps中的内容，因为这个maps中是程序运行的内存映像：
 * @return
 */
unsigned long getLibAddr(){
    unsigned long ret = 0;
    char name[] = "libdemo.so";
    char buf[4096], *temp;
    int pid;
    FILE *fp;
    pid = getpid();
    sprintf(buf, "/proc/%d/maps", pid);
    fp = fopen(buf, "r");
    if(fp == NULL)
    {
        puts("open failed");
        goto _error;
    }
    while(fgets(buf, sizeof(buf), fp)){
        if(strstr(buf, name)){
            temp = strtok(buf, "-");
            ret = strtoul(temp, NULL, 16);
            break;
        }
    }
    _error:
    fclose(fp);
    return ret;
}

JNIEXPORT jstring
Java_com_pds_antisafe_AntiSafe_getString(JNIEnv* env, jclass thiz)
{
#if defined(__arm__)
    #if defined(__ARM_ARCH_7A__)
    #if defined(__ARM_NEON__)
      #define ABI "armeabi-v7a/NEON"
    #else
      #define ABI "armeabi-v7a"
    #endif
  #else
   #define ABI "armeabi"
  #endif
#elif defined(__i386__)
#define ABI "x86"
#elif defined(__mips__)
#define ABI "mips"
#else
#define ABI "unknown"
#endif

    return getString(env);
}

