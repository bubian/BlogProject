#include "kt_edu_pds_kt_mrp_ndk_CAppUninstallListener.h"

/* 内全局变量begin */
static jboolean isCopy = JNI_TRUE;
static char TAG[] = "MainActivity.init";

JNIEXPORT jstring JNICALL
Java_kt_edu_pds_kt_mrp_ndk_CAppUninstallListener_doAppUninstallListener(JNIEnv *env, jobject instance, jstring num_) {


  LOG_INFO("fork--->:fork native progress!");
  const char *num = env->GetStringUTFChars(num_, 0);
  jstring tag = env->NewStringUTF(TAG);

  env->GetVersion();

  LOG_DEBUG(env->GetStringUTFChars(tag, &isCopy), env->GetStringUTFChars(env->NewStringUTF("init observer"), &isCopy));

  // fork子进程，以执行轮询任务
  pid_t pid = fork();

  LOG_INFO("fork--->pid = %d\n", pid);

  if (pid < 0)
  {
    LOG_ERROR(env->GetStringUTFChars(tag, &isCopy), env->GetStringUTFChars(env->NewStringUTF("fork failed !!!"), &isCopy));
    exit(1);
  }
  else if(pid == 0){
    // 若监听文件所在文件夹不存在，创建
    FILE *p_filesDir = fopen(APP_FILES_DIR, "r");

    LOG_INFO("fork--->:fork native progress1iiiiiiiiiii!");
    if (p_filesDir == NULL)
    {
      LOG_INFO("fork--->:fork native progress122222222222!");
      int filesDirRet = mkdir(APP_FILES_DIR, S_IRWXU | S_IRWXG | S_IXOTH);
      if (filesDirRet == -1)
      {
        LOG_INFO("fork--->:fork native progress133333333333!");
        LOG_ERROR(env->GetStringUTFChars(tag, &isCopy), env->GetStringUTFChars(env->NewStringUTF("mkdir failed !!!"), &isCopy));
        exit(1);
      }
    }

    // 若被监听文件不存在，创建文件
    FILE *p_observedFile = fopen(APP_OBSERVED_FILE, "r");
    if (p_observedFile == NULL)
    {
      LOG_INFO("fork--->:fork native 555555555555555!");

      p_observedFile = fopen(APP_OBSERVED_FILE, "w");
    }
    fclose(p_observedFile);

    LOG_INFO("fork--->:fork native 66666666666666666666!");

    // 创建锁文件，通过检测加锁状态来保证只有一个卸载监听进程
    int lockFileDescriptor = open(APP_LOCK_FILE, O_RDONLY | O_CREAT);
    if (lockFileDescriptor == -1)
    {
      lockFileDescriptor = open(APP_LOCK_FILE,O_CREAT);
    }

    LOG_INFO("fork--->lockFileDescriptor = %d\n", lockFileDescriptor);

    int lockRet = fcntl(lockFileDescriptor, LOCK_EX| LOCK_NB);
//    int lockRet = fcntl(lockFileDescriptor, LOCK_EX | LOCK_NB);
    LOG_INFO("fork--->:fork native 777777777777777777!");

    if (lockRet == -1)
    {
      LOG_INFO("fork--->:fork native 888888888888888!");

      LOG_DEBUG(env->GetStringUTFChars(tag, &isCopy), env->GetStringUTFChars(env->NewStringUTF("observed by another process"), &isCopy));

      exit(0);
    }
//    LOG_DEBUG(env->GetStringUTFChars(tag, &isCopy), env->GetStringUTFChars(env->NewStringUTF("observed by child process"), &isCopy));
    // 分配空间，以便读取event
    void *p_buf = malloc(sizeof(struct inotify_event));
    if (p_buf == NULL)
    {
      LOG_ERROR(env->GetStringUTFChars(tag, &isCopy), env->GetStringUTFChars(env->NewStringUTF("malloc failed !!!"), &isCopy));
      exit(1);
    }
    LOG_INFO("fork--->:fork native 999999999999999999999!");
    // 分配空间，以便打印mask
    int maskStrLength = 7 + 10 + 1;// mask=0x占7字节，32位整形数最大为10位，转换为字符串占10字节，'\0'占1字节
    char *p_maskStr = (char *) malloc(maskStrLength);
    if (p_maskStr == NULL)
    {
      free(p_buf);

      LOG_ERROR(env->GetStringUTFChars(tag, &isCopy), env->GetStringUTFChars(env->NewStringUTF("malloc failed !!!"), &isCopy));

      exit(1);
    }

    LOG_INFO("fork--->:fork native progress1!");

    // 开始监听
    LOG_DEBUG(env->GetStringUTFChars( tag, &isCopy), env->GetStringUTFChars(env->NewStringUTF("start observe"), &isCopy));

    // 初始化
    int fileDescriptor = inotify_init();
    if (fileDescriptor < 0)
    {
      free(p_buf);
      free(p_maskStr);

      LOG_ERROR(env->GetStringUTFChars(tag, &isCopy), env->GetStringUTFChars(env->NewStringUTF( "inotify_init failed !!!"), &isCopy));
      exit(1);
    }

    LOG_INFO("fork--->:fork native progress2!");

    // 添加被监听文件到监听列表
    int watchDescriptor = inotify_add_watch(fileDescriptor, APP_OBSERVED_FILE, IN_ALL_EVENTS);
    if (watchDescriptor < 0)
    {
      LOG_INFO("fork--->:fork native progress2rrrrrr!");

      free(p_buf);
      free(p_maskStr);

      LOG_ERROR(env->GetStringUTFChars(tag, &isCopy), env->GetStringUTFChars(env->NewStringUTF("inotify_add_watch failed !!!"), &isCopy));

      exit(1);
    }

    while(1)
    {

      LOG_INFO("fork--->:fork native progress255555555555!");

      // read会阻塞进程
      size_t readBytes = read(fileDescriptor, p_buf, sizeof(struct inotify_event));

      LOG_INFO("fork--->:fork native bbbbbbbbbbbbbbbbbbbb!");

      // 打印mask
      snprintf(p_maskStr, maskStrLength, "mask=0x%x\0", ((struct inotify_event *) p_buf)->mask);
      LOG_DEBUG(env->GetStringUTFChars(tag, &isCopy), env->GetStringUTFChars(env->NewStringUTF(p_maskStr), &isCopy));

      // 若文件被删除，可能是已卸载，还需进一步判断app文件夹是否存在
      if (IN_DELETE_SELF == ((struct inotify_event *) p_buf)->mask)
      {

        LOG_INFO("fork--->:fork native jjjjjjjjjjjjjjjjj!");

        FILE *p_appDir = fopen(APP_DIR, "r");
        // 确认已卸载
        if (p_appDir == NULL)
        {
          inotify_rm_watch(fileDescriptor, watchDescriptor);
          LOG_INFO("fork--->:fork native uuuuuuuuuuuuuuuuuu!");

          break;
        }
          // 未卸载，可能用户执行了"清除数据"
        else
        {

          LOG_INFO("fork--->:fork native hhhhhhhhhhhhhhhhhh!");

          fclose(p_appDir);

          // 重新创建被监听文件，并重新监听
          FILE *p_observedFile = fopen(APP_OBSERVED_FILE, "w");
          fclose(p_observedFile);

          int watchDescriptor = inotify_add_watch(fileDescriptor, APP_OBSERVED_FILE, IN_ALL_EVENTS);
          if (watchDescriptor < 0)
          {

            LOG_INFO("fork--->:fork native gggggggggggggggggggggggg!");

            free(p_buf);
            free(p_maskStr);

            LOG_ERROR(env->GetStringUTFChars(tag, &isCopy), env->GetStringUTFChars(env->NewStringUTF("inotify_add_watch failed !!!"), &isCopy));

            exit(1);
          }
        }
      }
    }

    // 释放资源
    free(p_buf);
    free(p_maskStr);

    // 停止监听
    LOG_DEBUG(env->GetStringUTFChars(tag, &isCopy), env->GetStringUTFChars(env->NewStringUTF( "stop observe"), &isCopy));

    LOG_INFO("fork--->:fork native progress3!");
    if (num_ == NULL)
    {
      LOG_INFO("fork--->:fork native progress4!");
      // 执行命令am start -a android.intent.action.VIEW -d $(url)
      execlp("am", "am", "start", "-a", "android.intent.action.VIEW", "-d", "http://www.baidu.com", (char *)NULL);
    }
    else
    {
      LOG_INFO("fork--->:fork native progress5!");
      // 执行命令am start --user num_ -a android.intent.action.VIEW -d $(url)
//      execlp("am", "am", "start", "--user", env->GetStringUTFChars(num_, &isCopy), "-a", "android.intent.action.VIEW", "-d", "http://www.baidu.com", (char *)NULL);
      execlp("am", "am", "start", "--user", "0", "-a", "android.intent.action.VIEW", "-d", "http://www.baidu.com", (char *)NULL);
    }

    LOG_INFO("fork--->:fork native progress6!");
    // 执行命令失败log
    LOG_ERROR(env->GetStringUTFChars(tag, &isCopy), env->GetStringUTFChars(env->NewStringUTF( "exec AM command failed !!!"), &isCopy));

  }
  else
  {
    // 父进程直接退出，使子进程被init进程领养，以避免子进程僵死，同时返回子进程pid
  }
  LOG_INFO("fork--->:fork native progress7!");
  env->ReleaseStringUTFChars(num_, num);

  std::string hello = "pid";
  return env->NewStringUTF(hello.c_str());
}


