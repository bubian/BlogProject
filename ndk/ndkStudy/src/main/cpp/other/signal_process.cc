#include <jni.h>
#include <string>
#include <signal.h>
#include <android/log.h>
#include <sys/wait.h>
#include <pthread.h>
#include <sys/select.h>
#include <unistd.h>
#include <sys/socket.h>
#include <sys/types.h>
#include <stdlib.h>
#include <sys/un.h>
#include <errno.h>
#include <linux/signal.h>

#define TAG  "signal_process"
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR,TAG,__VA_ARGS__)
const char *PATH = "/data/data/md.edu.pds.kt.dp/my.sock";

void create_child();
void child_start_monitor();

int m_child;
int m_parent = -1;

int user_id;

void child_listen_msg() {
  fd_set rfds;
  while (1) {
    //清空端口号
    FD_ZERO(&rfds);
    FD_SET(m_child,&rfds);
//        设置超时时间
    struct timeval timeout={3,0};
    int r=select(m_child + 1, &rfds, NULL, NULL, &timeout);
    LOGE("读取消息前  %d  ",r);
    if (r > 0) {
      char pkg[256] = {0};
//            确保读到的内容是制定的端口号
      if (FD_ISSET(m_child, &rfds)) {
//                阻塞式函数  客户端写到内容  apk进程  没有进行任何写入    连接
        int result = read(m_child, pkg, sizeof(pkg));
//                读到内容的唯一方式 是客户端断开
        LOGE("重启父进程  %d ",result);
        LOGE("读到信息  %d    userid  %d ",result,user_id);

        execlp("am", "am", "startservice", "--user",user_id,
               "com.dongnao.socketprocess/com.dongnao.socketprocess.ProcessService", (char*)NULL);
        break;
      }
    }
  }
}

int child_create_channel() {
//    创建socket  listenfd 对象
  int listenfd=socket(AF_LOCAL, SOCK_STREAM, 0);
//    取消之前进程文件连接
  unlink(PATH);
  struct sockaddr_un  addr;
//    清空内存
  memset(&addr, 0, sizeof(sockaddr_un));
  addr.sun_family = AF_LOCAL;

  strcpy(addr.sun_path, PATH);
  int connfd=0;
  LOGE("绑定端口号");
  if(bind(listenfd, (const sockaddr *) &addr, sizeof(addr))<0) {
    LOGE("绑定错误");
    return 0;
  }
  listen(listenfd, 5);
  while (1) {
    LOGE("子进程循环等待连接  %d ",m_child);
//        不断接受客户端请求的数据
//        等待 客户端连接  accept阻塞式函数
    if ((connfd = accept(listenfd, NULL, NULL)) < 0) {
      if (errno == EINTR) {
        continue;
      } else{
        LOGE("读取错误");
        return 0;
      }
    }
//        apk 进程连接上了
    m_child = connfd;
    LOGE("apk 父进程连接上了  %d ",m_child);
    break;
  }
  LOGE("返回成功");
  return 1;
}
void child_do_work() {
//    守护进程
//   1 建立socket服务
//    2读取消息
  if(child_create_channel()) {
    child_listen_msg();
  }

}

//子进程变成僵尸进程会调用这个方法
void sig_handler(int sino) {
//
  int status;
//    阻塞式函数
  LOGE("等待死亡信号");
  wait(&status);

  LOGE("创建进程");
  create_child();

}
void create_child() {
  pid_t pid = fork();
//
  if (pid < 0) {

  } else if (pid > 0) {
//父进程
  } else if (pid == 0){
    LOGE("子进程开启 ");
//开启线程轮询
    child_start_monitor();
    child_do_work();
  }

}
extern "C"
JNIEXPORT void JNICALL
Java_md_edu_pds_kt_dp_signal_Wathcer_createWatcher(JNIEnv *env, jobject instance,jint userId) {
//    父进程
    user_id = userId;
//为了防止子进程被弄成僵尸进程   不要    1
    struct  sigaction sa;
    sa.sa_flags=0;

    sa.sa_handler = sig_handler;
    sigaction(SIGCHLD, &sa, NULL);
    create_child();
}
extern "C"
JNIEXPORT void JNICALL
Java_md_edu_pds_kt_dp_signal_Wathcer_connectToMonitor(JNIEnv *env, jobject instance) {
//    子进程1    父进程2
  int sockfd;
  struct sockaddr_un  addr;
  while (1) {
    LOGE("客户端  父进程开始连接");
    sockfd=socket(AF_LOCAL, SOCK_STREAM, 0);
    if (sockfd < 0) {
      return;
    }
    memset(&addr, 0, sizeof(sockaddr_un));
    addr.sun_family = AF_LOCAL;
    strcpy(addr.sun_path, PATH);
    if (connect(sockfd, (const sockaddr *) &addr, sizeof(addr)) < 0) {
      LOGE("连接失败  休眠");
//            连接失败
      close(sockfd);
      sleep(1);
//            再来继续下一次尝试
      continue;
    }
//        连接成功
    m_parent = sockfd;
    LOGE("连接成功  父进程跳出循环");
    break;
  }
}


//相当于java  run方法
void *thread_rt(void *data){
  pid_t pid;
  while ((pid = getppid()) != 1) {
    sleep(2);
    LOGE("循环 %d ",pid);
  }
//    父进程等于1  apk被干掉了
  LOGE("重启父进程");
  execlp("am", "am", "startservice", "--user", user_id,
         "md.edu.pds.kt.dp.service/md.edu.pds.kt.dp.service.LocalService", (char*)NULL);
}

void child_start_monitor() {
  pthread_t tid;
  pthread_create(&tid, NULL, thread_rt, NULL);
}
//SIGABRT	由调用abort函数产生，进程非正常退出
//SIGALRM	用alarm函数设置的timer超时或setitimer函数设置的interval timer超时
//SIGBUS	某种特定的硬件异常，通常由内存访问引起
//SIGCANCEL	由Solaris Thread Library内部使用，通常不会使用
//SIGCHLD	进程Terminate或Stop的时候，SIGCHLD会发送给它的父进程。缺省情况下该Signal会被忽略
//SIGCONT	当被stop的进程恢复运行的时候，自动发送
//SIGEMT	和实现相关的硬件异常
//SIGFPE	数学相关的异常，如被0除，浮点溢出，等等
//SIGFREEZE	Solaris专用，Hiberate或者Suspended时候发送
//SIGHUP	发送给具有Terminal的Controlling Process，当terminal 被disconnect时候发送
//SIGILL	非法指令异常
//SIGINFO	BSD signal。由Status Key产生，通常是CTRL+T。发送给所有Foreground Group的进程
//SIGINT	由Interrupt Key产生，通常是CTRL+C或者DELETE。发送给所有ForeGround Group的进程
//SIGIO	异步IO事件
//SIGIOT	实现相关的硬件异常，一般对应SIGABRT
//SIGKILL	无法处理和忽略。中止某个进程
//SIGLWP	由Solaris Thread Libray内部使用
//SIGPIPE	在reader中止之后写Pipe的时候发送
//SIGPOLL	当某个事件发送给Pollable Device的时候发送
//SIGPROF	Setitimer指定的Profiling Interval Timer所产生
//SIGPWR	和系统相关。和UPS相关。
//SIGQUIT	输入Quit Key的时候（CTRL+\）发送给所有Foreground Group的进程
//SIGSEGV	非法内存访问
//SIGSTKFLT	Linux专用，数学协处理器的栈异常
//SIGSTOP	中止进程。无法处理和忽略。
//SIGSYS	非法系统调用
//SIGTERM	请求中止进程，kill命令缺省发送
//SIGTHAW	Solaris专用，从Suspend恢复时候发送
//SIGTRAP	实现相关的硬件异常。一般是调试异常
//SIGTSTP	Suspend Key，一般是Ctrl+Z。发送给所有Foreground Group的进程
//SIGTTIN	当Background Group的进程尝试读取Terminal的时候发送
//SIGTTOU	当Background Group的进程尝试写Terminal的时候发送
//SIGURG	当out-of-band data接收的时候可能发送
//SIGUSR1	用户自定义signal 1
//SIGUSR2	用户自定义signal 2
//SIGVTALRM	setitimer函数设置的Virtual Interval Timer超时的时候
//SIGWAITING	Solaris Thread Library内部实现专用
//SIGWINCH	当Terminal的窗口大小改变的时候，发送给Foreground Group的所有进程
//SIGXCPU	当CPU时间限制超时的时候
//SIGXFSZ	进程超过文件大小限制
//SIGXRES	Solaris专用，进程超过资源限制的时候发
